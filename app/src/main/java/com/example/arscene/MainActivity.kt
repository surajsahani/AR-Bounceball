package com.example.arscene;


import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity(), ControlFragment.OnQuaternionChangedListener {

    private lateinit var mSharedPreferences: SharedPreferences;

    // Firebase instance variables
    private var mMessagesDatabaseReference: DatabaseReference? = null
    private var mChildEventListener: ChildEventListener? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    private var mFirebaseAuth: FirebaseAuth? = null

    private var mUsername: String? = null



    private lateinit var arFragment: ArFragment

    private var anchorNode: AnchorNode? = null
    private lateinit var cubeNode: Node

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAuth()
        setupSceneformFragment()

        //setupSceneformFragment()
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    private fun checkAuth() {
        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance()
        //val database = Firebase.database.reference
        //mMessagesDatabaseReference = database.child("messages")

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                //onSignedInInitialize(user.displayName)
            } else {
                // User is signed out
                onSignedOutCleanup()
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                    RC_SIGN_IN
                )
            }

        }
    }

    private fun onSignedInInitialize(username: String) {
        mUsername = username
        //attachDatabaseReadListener()
    }

    private fun onSignedOutCleanup() {
        mUsername = ANONYMOUS

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mAuthStateListener?.let {
            mFirebaseAuth?.addAuthStateListener(it)
        }
    }

    override fun onPause() {
        super.onPause()
        mAuthStateListener?.let {
            mFirebaseAuth?.removeAuthStateListener(it)
        }

    }

    companion object {
        private const val TAG = "MainActivity"
        const val ANONYMOUS = "anonymous"
        const val DEFAULT_MSG_LENGTH_LIMIT = 1000
        const val RC_SIGN_IN = 1
    }
    private fun setupSceneformFragment() {

        arFragment = supportFragmentManager
            .findFragmentById(R.id.sceneform_fragment)
                as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            makeCube(hitResult, Color.RED)
        }

    }

    /**
     * Constructs cube of radius 1f and at position 0.0f, 0.15f, 0.0f on the plane
     * Here Vector3 takes up the size - 0.2f, 0.2f, 0.2f
     * @param hitResult - If the hit result is a plane
     * @param res - Color
     */
    private fun makeCube(hitResult: HitResult, color: Int) {
        MaterialFactory.makeOpaqueWithColor(this,
            com.google.ar.sceneform.rendering.Color(color))
            .thenAccept { material ->
                addNodeToScene(arFragment, hitResult.createAnchor(),
                    ShapeFactory.makeCube(
                        Vector3(0.2f, 0.2f, 0.2f),
                        Vector3(0.0f, 0.15f, 0.0f),
                        material)
                )

            }
    }

    /**
     * Adds node to the scene and the object.
     * @param fragment - sceneform fragment
     * @param anchor - created anchor at the tapped position
     * @param modelObject - rendered object
     */
    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, modelObject: ModelRenderable) {

        anchorNode = AnchorNode(anchor).apply {
            setParent(fragment.arSceneView.scene)
        }

        anchorNode?.addChild(createCubeNode(modelObject))

    }

    override fun onLeft(value: Float) {
        cubeNode.apply {
            Log.d("left", value.toString())
            localRotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), value)
        }
    }

    override fun onRight(value: Float) {
        cubeNode.apply {
            Log.d("right", value.toString())
            localRotation = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), -value)
        }
    }

    fun createCubeNode(modelObject: ModelRenderable): Node {
        cubeNode =  Node().apply {
            renderable = modelObject
            localPosition = Vector3(0.0f, 0.15f, 0.0f)
        }

        return cubeNode
    }
    private fun makeTextureSphere(hitResult: HitResult, res: Int) {
        Texture.builder().setSource(BitmapFactory.decodeResource(resources, res))
            .build()
            .thenAccept {
                MaterialFactory.makeOpaqueWithTexture(this, it)
                    .thenAccept { material ->
                        addNodeToScene(arFragment, hitResult.createAnchor(),
                            ShapeFactory.makeSphere(
                                0.1f,
                                Vector3(0.0f, 0.15f, 0.0f),
                                material
                            ))

                    }
            }
    }
}




