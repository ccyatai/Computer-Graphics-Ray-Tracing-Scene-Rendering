package ray.renderer;

import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class BruteForcePathTracer extends PathTracer {
    /**
     * @param scene
     * @param ray
     * @param sampler
     * @param sampleIndex
     * @param outColor
     */
    protected void rayRadianceRecursive(Scene scene, Ray ray, 
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) {

    	if (level >= depthLimit)
    			return;
    	
    	// find if the ray intersect with any surface
        IntersectionRecord iRec = new IntersectionRecord();
        Color outEmitted = new Color();
		Color outDirect = new Color();
		Color output = new Color();
       
        if (scene.getFirstIntersection(iRec, ray)) {
        	
        	emittedRadiance(iRec, ray.direction, outEmitted);
        	    		
			Vector3 outDir = new Vector3();
//			outDir.set(-ray.direction.x, -ray.direction.y, -ray.direction.z);
    		
    		/* --- compute direct illumination --- */
			gatherIllumination(scene, outDir, iRec, sampler, sampleIndex, level+1, outDirect);
			
			output.add(outEmitted);
			output.add(outDirect);
			outColor.set(output);
			return;
        }
			/* otherwise, just compute background color */
			if (level == 0)
				scene.getBackground().evaluate(ray.direction, outColor);
    	
    	// W4160 TODO (B)
    	//
        // Find the visible surface along the ray, then add emitted and reflected radiance
        // to get the resulting color.
    	//
    	// If the ray depth is less than the limit (depthLimit), you need
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) reflected radiance from other lights and objects. You need recursively compute the radiance
    	//    hint: You need to call gatherIllumination(...) method.
    }

}
