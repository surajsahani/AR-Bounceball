# AR-Bounceball
ARCore SDK Android Bouncing ball App

https://user-images.githubusercontent.com/22853459/150648813-ca66f624-ebec-45ef-8320-22eb86bad4af.mp4


# Features
Rotate X (left arrow)<br />
Rotate Y (right arrow)<br />
cube3D object using ARCore <br />
sphere3D object ARCore <br />
persistence of object after app close <br />

# Project Architecture
AR Scene form <br />
Android Navigation Component <br />
Firebae Auth <br />
Kotlin Extension <br />


Sphere 3D object

```    private fun makeTextureSphere(hitResult: HitResult, res: Int) {
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
```
