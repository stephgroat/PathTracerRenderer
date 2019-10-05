package ray.surface;

import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
    
    /** Material for this sphere. */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The center of the sphere. */
    protected final Point3 center = new Point3();
    
    /** The radius of the sphere. */
    protected double radius = 1.0;
    
    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {        
    }
    
    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter The center of the new sphere.
     * @param newRadius The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {        
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }
    
    public void updateArea() {
    	area = 4 * Math.PI * radius*radius;
    	oneOverArea = 1. / area;
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    /**
     * Returns the center of the sphere in the input Point3
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {        
        outPoint.set(center);        
    }
    
    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {        
        this.center.set(center);        
    }
    
    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }
    
    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }
        
    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    public boolean intersect(IntersectionRecord outRecord, Ray ray) {
        // W4160 TODO (A)
    	// In this function, you need to test if the given ray intersect with a sphere.
    	// You should look at the intersect method in other classes such as ray.surface.Triangle.java
    	// to see what fields of IntersectionRecord should be set correctly.
    	// The following fields should be set in this function
    	//     IntersectionRecord.t
    	//     IntersectionRecord.frame
    	//     IntersectionRecord.surface
    	//
    	// Note: Although a ray is conceptually a infinite line, in practice, it often has a length,
    	//       and certain rendering algorithm relies on the length. Therefore, here a ray is a 
    	//       segment rather than a infinite line. You need to test if the segment is intersect
    	//       with the sphere. Look at ray.misc.Ray.java to see the information provided by a ray.



        // code based off of: https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection
        double a,b,c;
        double t0, t1;
        double discriminant;

        Point3 O = ray.origin;
        Point3 C = center;

        Vector3 L;
        L = new Vector3(O.x - C.x, O.y - C.y, O.z - C.z);

        a = ray.direction.dot(ray.direction);
        b = 2 * ray.direction.dot(L);
        c = L.dot(L) - radius * radius;

        discriminant = b*b - 4*a*c;

        if (discriminant < 0)
            return false;
        else if (discriminant == 0) {
            t0 = -0.5 * b / a;
            t1 = t0;
        }
        else {
            double q;
            if (b>0)
                q = -0.5 * (b + Math.sqrt(discriminant));
            else
                q = -0.5 * (b - Math.sqrt(discriminant));
            t0 = q / a;
            t1 = c / q;
        }

        if (t0 > t1) {
            double temp;
            temp = t0;
            t0 = t1;
            t1 = temp;
        }

        if (t0 < 0) {
            t0 = t1;
            if (t0 < 0)
                return false;
        }


        Vector3 D = new Vector3(ray.direction);
        D.scale(t0);
        Vector3 point = new Vector3(O.x, O.y, O.z);
        point.add(D);
        Vector3 center = new Vector3(C.x, C.y, C.z);
        Vector3 normal = new Vector3(point);
        normal.sub(center);
        normal.normalize();

        outRecord.t = t0;
        outRecord.surface = this;
        outRecord.frame.o.set(point);
        outRecord.frame.w.set(normal);
        outRecord.frame.initFromW();

        return true;
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);        
    }
    
}
