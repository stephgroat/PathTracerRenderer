package ray.renderer;

import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.*;


/**
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 * 
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 * 
 * @author Changxi Zheng (at Columbia)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {


    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir, 
            IntersectionRecord iRec, Point2 seed, Color outColor) {
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

       Geometry.squareToPSAHemisphere(seed, incDir);
       iRec.frame.frameToCanonical(incDir);

       Vector3 normal = new Vector3(iRec.frame.w);

       normal.normalize();
       incDir.normalize();

       outDir.set(normal);
       outDir.scale(2 * incDir.dot(normal));
       outDir.sub(incDir);
       outDir.normalize();

       Ray shadowRay = new Ray(iRec.frame.o, incDir);
       Color brdf = new Color();
       Color radiance = new Color();

       shadowRay.makeOffsetRay();

       IntersectionRecord incIllum = new IntersectionRecord();


       if (scene.getFirstIntersection(incIllum, shadowRay) && incIllum.surface.getMaterial().isEmitter()) {

            Material m = iRec.surface.getMaterial();
            m.getBRDF(incIllum).evaluate(incIllum.frame, incDir, outDir, brdf);

            scene.incidentRadiance(shadowRay.origin, shadowRay.direction, radiance);

            outColor.set(1);
            outColor.scale(Math.PI);
            outColor.scale(brdf);
            outColor.scale(radiance);
       }

    }
    
}
