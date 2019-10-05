package ray.renderer;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public abstract class PathTracer extends DirectIlluminationRenderer {

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
    	//
    	// Here you need to use Geometry.squareToPSAHemisphere that you implemented earlier in this function

        Point2 seed = new Point2();
        sampler.sample(sampler.getNumSamples(), sampleIndex, seed);

        Vector3 incDir = new Vector3();

        Geometry.squareToPSAHemisphere(seed, incDir);
        iRec.frame.frameToCanonical(incDir);

        Vector3 normal = new Vector3(iRec.frame.w);
        normal.normalize();

        outDir.set(normal);
        outDir.scale(2* incDir.dot(normal));
        outDir.sub(incDir);

        Ray reflRay = new Ray(iRec.frame.o, outDir);
        reflRay.makeOffsetRay();
        Color brdf = new Color();
        Color radiance = new Color();

        rayRadianceRecursive(scene, reflRay, sampler, sampleIndex, level, radiance);

        Material m = iRec.surface.getMaterial();
        m.getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, brdf);

        outColor.set(backgroundIllumination);
        outColor.scale(Math.PI);
        outColor.scale(brdf);
        outColor.scale(radiance);
    }
}
