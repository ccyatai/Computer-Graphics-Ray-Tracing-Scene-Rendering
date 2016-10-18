                                            Computer Graphics Project Description
                                                 Ray Tracing Scene Rendering 
                                             Programming application: Java, OpenGL
In this project, I implemented several algorithms for rendering photorealistic images by ray tracing.
In PhongShader rendering, I implemented phong reflection model formula which is to sum up all the reflection radiance coming from all the light sources to the intersect point.

In DirectOnly rendering, I implemented the stochastic rendering algorithm with only direct illumination considered. That is, the surface can be illuminated when only the light rays can reach it directly, and light reections are ignored completely. However, the resulting image was capable of capturing interesting effects such as soft shadows. The total radiance was divided by 2 parts: emitted radiance and direct illumination radiance. The emitted radiance is emitted from the intersect point itself. For the direct radiance, I traced reversely a new ray starting from the intersect point with random direction, and then found the new intersect point and calculated the emitted radiance from this new point if it was an emitting point. Then I calculated the reflection radiance coming from this new intersect point and added it up to the total radiance.

In BruteForcePath Tracer rendering, I implements the rendering algorithm that considered ray reflections on the object surface. Theoretically, a ray can bounce on object surfaces many times, resulting in a impractical simulation process. The recursive ray tracing would be terminated after 5 recursions. This of course was not
physically accurate, but it is sufficient to produce realistic images like the ones in my showcase rendering scenes. This algorithm is the extension of DirectOnly method.

Welcome to continue to see my showcase rendering scenes on https://youtu.be/XKKarRQSCn0!
