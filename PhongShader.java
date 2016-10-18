package ray.renderer;

import ray.light.PointLight;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.surface.Surface;

public class PhongShader implements Renderer {
    
    private double phongCoeff = 2.5;
    
    public PhongShader() { }
    
    public void setAlpha(double a) {
        phongCoeff = a;
    }
    
    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
            int sampleIndex, Color outColor) {
    	
    	Vector3 viewDir = new Vector3(-ray.direction.x,-ray.direction.y,-ray.direction.z);
    	viewDir.normalize();
    	Vector3 lightDir = new Vector3();
    	Vector3 reflectDir = new Vector3();
    	Vector3 normal = new Vector3();
    	Vector3 color = new Vector3();
    	IntersectionRecord record = new IntersectionRecord();
    	Color outBRDFValue = new Color();
    	
    	if (scene.getFirstIntersection(record, ray)) { 
			for (PointLight light : scene.getPointLights()){
	    		lightDir.set(light.location.x-record.frame.o.x,light.location.y-record.frame.o.y,light.location.z-record.frame.o.z);
	    		lightDir.normalize();
	    		normal.set(record.frame.w);
	    		double lambertian = Math.max(lightDir.x*normal.x+lightDir.y*normal.y+lightDir.z*normal.z, 0.0);
		        double specular = 0.0;
		        double specAngle = 0.0;
		         
		        if(lambertian > 0.0) {
		         
//    		        "    vec3 viewDir = normalize(-vertPos);" +
//    		         
//    		            // this is blinn phong
//    		        "    vec3 halfDir = normalize(lightDir + viewDir);" +
//    		        "    float specAngle = max(dot(halfDir, normal), 0.0);" +
//    		        "    specular = pow(specAngle, 16.0);" +
		         
   	            	// this is phong (for comparison)
		        	double costheta = lightDir.x*normal.x+lightDir.y*normal.y+lightDir.z*normal.z;
		        	
		        	reflectDir.set(costheta*normal.x,costheta*normal.y,costheta*normal.z);
		        	reflectDir.sub(lightDir);
		        	reflectDir.add(reflectDir);
		        	reflectDir.add(lightDir);

		            specAngle = Math.max(reflectDir.x*viewDir.x+reflectDir.y*viewDir.y+reflectDir.z*viewDir.z, 0.0);
		            // note that the exponent is different here
		            specular = Math.pow(specAngle, 4);
                }
		        record.surface.getMaterial().getBRDF(record).evaluate(record.frame, lightDir, viewDir, outBRDFValue);
		        
		        color.x = color.x + outBRDFValue.r * Math.PI * lambertian * light.diffuse.r + specular * light.specular.r;
                color.y = color.y + outBRDFValue.g * Math.PI * lambertian * light.diffuse.g + specular * light.specular.g;
                color.z = color.z + outBRDFValue.b * Math.PI * lambertian * light.diffuse.b + specular * light.specular.b;
			}
	    	outColor.set(color.x, color.y, color.z);
	    	return;
    	}
   			
    	scene.getBackground().evaluate(ray.direction, outColor);

    	
        // W4160 TODO (A)
        // Here you need to implement the basic phong reflection model to calculate
        // the color value (radiance) along the given ray. The output color value 
        // is stored in outColor. 
        // 
        // For such a simple rendering algorithm, you might not need Monte Carlo integration
        // In this case, you can ignore the input variable, sampler and sampleIndex.
    }
}
