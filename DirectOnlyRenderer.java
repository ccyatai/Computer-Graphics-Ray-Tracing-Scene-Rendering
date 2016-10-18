package ray.renderer;

import ray.light.PointLight;
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

/**
 * A renderer that computes radiance due to emitted and directly reflected light only.
 * 
 * @author cxz (at Columbia)
 */
public class DirectOnlyRenderer implements Renderer {
    
    /**
     * This is the object that is responsible for computing direct illumination.
     */
    DirectIlluminator direct = null;
        
    /**
     * The default is to compute using uninformed sampling wrt. projected solid angle over the hemisphere.
     */
    public DirectOnlyRenderer() {
        this.direct = new ProjSolidAngleIlluminator();
    }
    
    
    /**
     * This allows the rendering algorithm to be selected from the input file by substituting an instance
     * of a different class of DirectIlluminator.
     * @param direct  the object that will be used to compute direct illumination
     */
    public void setDirectIlluminator(DirectIlluminator direct) {
        this.direct = direct;
    }

    
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    	IntersectionRecord iRec = new IntersectionRecord();
    	Vector3 viewDir = new Vector3(-ray.direction.x,-ray.direction.y,-ray.direction.z);
    	viewDir.normalize();
    	
    	Color outEmitted = new Color();
		Color outDirect = new Color();
		Color output = new Color();
		Point2 directSeed = new Point2();
//		direct = new ProjSolidAngleIlluminator();
//		Vector3 lightDir = new Vector3();
//    	Vector3 reflectDir = new Vector3();
//    	Vector3 normal = new Vector3();
        
    	if (scene.getFirstIntersection(iRec, ray)) {
//    		for (PointLight light : scene.getPointLights()){
//			/* if the ray intersects an object in the scene: */
//    			lightDir.set(light.location.x-iRec.frame.o.x,light.location.y-iRec.frame.o.y,light.location.z-iRec.frame.o.z);
//	    		lightDir.normalize();
//	    		normal.set(iRec.frame.w);
//	    		double costheta = lightDir.x*normal.x+lightDir.y*normal.y+lightDir.z*normal.z;
//	        	
//	        	reflectDir.set(costheta*normal.x,costheta*normal.y,costheta*normal.z);
//	        	reflectDir.sub(lightDir);
//	        	reflectDir.add(reflectDir);
//	        	reflectDir.add(lightDir);

			/* --- compute emitted radiance --- */
    		emittedRadiance(iRec, ray.direction, outEmitted);

			/* --- compute direct illumination --- */

			/* sample random seed on unit square */
			sampler.sample(1, sampleIndex, directSeed);
			
//			System.out.println("seed = " + directSeed.x + " " + directSeed.y);
			
            // Generate a random incident direction
            Vector3 incDir = new Vector3();
            Vector3 outDir = new Vector3();
            Vector3 normal = new Vector3();
            normal.set(iRec.frame.w);
    		normal.normalize();
            
            Geometry.squareToHemisphere(directSeed, incDir);
            iRec.frame.frameToCanonical(incDir);
            
            double costheta = incDir.x*normal.x+incDir.y*normal.y+incDir.z*normal.z;
        	
        	outDir.set(costheta*normal.x,costheta*normal.y,costheta*normal.z);
        	outDir.sub(incDir);
        	outDir.add(outDir);
        	outDir.add(incDir);

			direct.directIllumination(scene, incDir, viewDir, iRec, directSeed, outDirect);

			/* --- set outColor to sum of computed radiances --- */
			output.add(outEmitted);
			output.add(outDirect);
    		outColor.set(output);
		} else {
			/* otherwise, just compute background color */
			scene.getBackground().evaluate(ray.direction, outColor);
		}
    	   	
//    	direct.directIllumination(scene, incDir, outDir, iRec, seed, outColor);;
    	
    	// W4160 TODO (A)
    	// In this function, you need to implement your direct illumination rendering algorithm
    	//
    	// you need:
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) direct reflected radiance from other lights. This is by implementing the function
    	//    ProjSolidAngleIlluminator.directIlluminaiton(...), and call direct.directIllumination(...) in this
    	//    function here.
    }

    
    /**
     * Compute the radiance emitted by a surface.
     * @param iRec      Information about the surface point being shaded
     * @param dir          The exitant direction (surface coordinates)
     * @param outColor  The emitted radiance is written to this color
     */
    protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir, Color outColor) {
    	
    	/* get the material of the intersected surface */

		if (iRec.surface.getMaterial().isEmitter()) {
			/* get the emitted radiance if the material is an emitter */
			LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
			lRec.set(iRec);
			lRec.emitDir.set(dir);
			lRec.emitDir.scale(-1);
			iRec.surface.getMaterial().emittedRadiance(lRec, outColor);
		} else {
			/* emitted radiance is zero if the material is not an emitter */
			outColor.set(0.0);
		}
    	
    	// W4160 TODO (A)
        // If material is emitting, query it for emission in the relevant direction.
        // If not, the emission is zero.
    	// This function should be called in the rayRadiance(...) method above
    }
}
