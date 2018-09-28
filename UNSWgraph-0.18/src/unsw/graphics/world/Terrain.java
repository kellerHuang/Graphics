package unsw.graphics.world;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain{

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
        float altitude = 0;
        double a = getGridAltitude((int) x,(int) z);
        double b = getGridAltitude((int) x,(int) z + 1);
        double c = getGridAltitude((int) x + 1,(int) z);
        double d = getGridAltitude((int) x + 1,(int) z + 1);
        
        float diffx = x - (int) x;
        float diffz = z - (int) z;
        double e = diffz/1*b + (1-diffz/1)*a;
        double f = diffz/1*d + (1-diffz/1)*c;
        
        double g = diffx/1*f + (1-diffx/1)*e;
        
        altitude = (float) g;
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);        
    }
    
    public List<Point3D> getVertices() {
    	List<Point3D> vertices = new ArrayList<Point3D>();
    	for(int i = 0; i < width;i++) {
    		for(int j = 0; j < depth; j++) {
    			vertices.add(new Point3D(i,j,altitudes[i][j]));
    		}
    	}
    	return vertices;
    }
    
    public ArrayList<ArrayList<Integer>> getIndices() {
    	int a = 0;
    	int b = 1;
    	int c = width;
    	int d = width+1;
    	int change = 0;
    	ArrayList<ArrayList<Integer>> indices = new ArrayList<ArrayList<Integer>>();
    	while(d < depth*width && c < depth*width) {
    		ArrayList<Integer> x = new ArrayList<Integer>();
    		ArrayList<Integer> y = new ArrayList<Integer>();
    		x.add(a);
    		x.add(b);
    		x.add(c);
    		y.add(b);
    		y.add(c);
    		y.add(d);
    		indices.add(x);
    		indices.add(y);
    		
    		//compute new two points
    		//check if it is last square in grid
    		if((a+1)%width == 0 || (b+1)%width == 0) {
    			a = a + 2;
    			b = b + 2;
    			c = c + 2;
    			d = d + 2;
    			continue;
    		}
    		if(change == 0) {
    			a = a + 2;
    			c = c + 2;
    			change = 1;
    		}else {
    			b = b + 2;
    			d = d + 2;
    			change = 0;
    		}
    	}
    	return indices;
    }
}
