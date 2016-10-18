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

public abstract class PathTracer extends DirectOnlyRenderer {

    protected int depthLimit = 5;
    protected int backgroundIllumination = 1;

    public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }
    public void setBackgroundIllumination(int backgroundIllumination) { this.backgroundIllumination = backgroundIllumination; }

    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    
        rayRadianceRecursive(scene, ray, sampler, sampleIndex, 0, outColor);
    }

    protected abstract void rayRadianceRecursive(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, int level, Color outColor);

    public void gatherIllumination(Scene scene, Vector3 outDir, 
            IntersectionRecord iRec, SampleGenerator sampler, 
            int sampleIndex, int level, Color outColor) {
		
    	Point2 directSeed = new Point2();
		Color output = new Color();
    	/* sample random seed on unit square */
		sampler.sample(level, sampleIndex, directSeed);

        Vector3 incDir = new Vector3();
        Vector3 refDir = new Vector3();
        Vector3 normal = new Vector3();
        normal.set(iRec.frame.w);
		normal.normalize();
		
        // Generate a random incident direction       
        Geometry.squareToHemisphere(directSeed, incDir);
        iRec.frame.frameToCanonical(incDir);
        
//      double costheta = incDir.x*normal.x+incDir.y*normal.y+incDir.z*normal.z;
    	
//    	refDir.set(costheta*normal.x,costheta*normal.y,costheta*normal.z);
//    	refDir.sub(incDir);
//    	refDir.add(refDir);
//    	refDir.add(incDir);

    	Ray sample = new Ray();
    	Color brdf = new Color();

    	sample.set(iRec.frame.o, incDir);
		sample.makeOffsetRay();

		rayRadianceRecursive(scene, sample, sampler, sampleIndex, level, output);

		Material m = iRec.surface.getMaterial();
		m.getBRDF(iRec).evaluate(iRec.frame, incDir, refDir, brdf);

		/* compute direct illumination */
		output.scale(brdf);
		output.scale(Math.PI);
		outColor.set(output);
    	
    	// W4160 TODO (B)
    	//
        // This method computes a Monte Carlo estimate of reflected radiance due to direct and/or indirect 
        // illumination.  It generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
    	// You need: 
    	//   1. Generate a random incident direction according to proj solid angle
    	//      pdf is constant 1/pi
    	//   2. Recursively find incident radiance from that direction
    	//   3. Estimate the reflected radiance: brdf * radiance / pdf = pi * brdf * radiance
    }
}
