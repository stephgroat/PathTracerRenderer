package ray.renderer;

import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
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
    	// W4160 TODO (B)
    	//
        // Find the visible surface along the ray, then add emitted and reflected radiance
        // to get the resulting color.
    	//
    	// If the ray depth is less than the limit (depthLimit), you need
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) reflected radiance from other lights and objects. You need recursively compute the radiance
    	//    hint: You need to call gatherIllumination(...) method.

        IntersectionRecord iRec = new IntersectionRecord();

        if (level > depthLimit) {
            outColor.set(0);
            return;
        }
        if (scene.getFirstIntersection(iRec, ray)) {

            Color emittance = new Color();
            emittedRadiance(iRec, ray.direction, emittance);
            Color reflected = new Color();
            Vector3 dir = ray.direction;
            dir.scale(-1);
            gatherIllumination(scene, dir, iRec, sampler, sampleIndex, level+1, reflected);

            outColor.set(reflected);
            outColor.add(emittance);
        }
        else {
            outColor.set(0);
        }
    }

}
