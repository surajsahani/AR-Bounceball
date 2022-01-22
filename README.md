# AR-Bounceball
ARCore SDK Android Bouncing ball App

https://user-images.githubusercontent.com/22853459/150648813-ca66f624-ebec-45ef-8320-22eb86bad4af.mp4


# Features
Firebase Login/Registration<br />
Place an 3D object cube,sphere<br />
Rotate cube X (left arrow)<br />
Rotate cube Y (right arrow)<br />
place sphere3D object ARCore <br />
persistence of 3D object after screen move away<br />
bounce (on progress) <br />
Rotate Z (on progress)<br />
# Project Architecture
AR Scene form <br />
Android Navigation Component <br />
Firebae Auth <br />
Kotlin Extension <br />


Sphere 3D object

```    
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
```
