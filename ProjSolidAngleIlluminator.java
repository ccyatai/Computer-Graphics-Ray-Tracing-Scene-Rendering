package ray.renderer;

import ray.brdf.BRDF;
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
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 * 
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 * 
 * @author srm, Changxi Zheng (at Columbia)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {
    
    
    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir, 
            IntersectionRecord iRec, Point2 seed, Color outColor) {
    	IntersectionRecord lightIRec = new IntersectionRecord();
		LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();

    	Ray sample = new Ray();
    	Color brdf = new Color();
    	Color irradiance = new Color();
    	

		/*
		 * cast a ray at the sample direction and see if it intersects an
		 * emitter
		 */
		sample.set(iRec.frame.o, incDir);
		sample.makeOffsetRay();
		if (scene.getFirstIntersection(lightIRec, sample)
				&& lightIRec.surface.getMaterial().isEmitter()) {
			/*
			 * if our surface is directly illuminated, calculate the rendering
			 * equation terms
			 */

			/* get BRDF */
			Material m = iRec.surface.getMaterial();
			m.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);

			/* get incident illumination */
			lightIRec.surface.getMaterial().emittedRadiance(lRec, irradiance);

			/* compute direct illumination */
			outColor.set(1.0);
			outColor.scale(brdf);
			outColor.scale(irradiance);
			outColor.scale(Math.PI);

		} else {

			/* otherwise, there is no illumination from this sample direction */
			outColor.set(0);
		}
    	
        // W4160 TODO (A)
    	// This method computes a Monte Carlo estimate of reflected radiance due to direct illumination.  It
        // generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
        //
        // The same code could be interpreted as an integration wrt. solid angle, as follows:
        //
        //    f = brdf * radiance * cos_theta
        //    p = cos_theta / pi
        //    g = f / p = brdf * radiance * pi
    	// 
    	// As a hint, here are a few steps when I code this function
    	// 1. Generate a random incident direction according to proj solid angle
        //    pdf is constant 1/pi
    	// 2. Find incident radiance from that direction
    	// 3. Estimate reflected radiance using brdf * radiance / pdf = pi * brdf * radiance
    }
    
}
