package ray.material;

import ray.brdf.BRDF;
import ray.brdf.Lambertian;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;

public class texture implements Material{
	
	Color radiance = new Color();
//	BRDF brdf = new texture(new Color(0, 0, 0));

	public texture() {
		// TODO Auto-generated constructor stub
		
		
	}

	@Override
	public BRDF getBRDF(IntersectionRecord iRec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void emittedRadiance(LuminaireSamplingRecord lRec, Color outRadiance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEmitter() {
		// TODO Auto-generated method stub
		return false;
	}

}
