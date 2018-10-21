package unsw.graphics.world;


import java.awt.*;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.*;


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
    private List<Pond> ponds;
    private Vector3 sunlight;
    private Point3DBuffer vertexBuffer;
    private IntBuffer indicesBuffer;
    private int verticesName;
    private int indicesName;
    private TriangleMesh terrain;
    private int rotateX;
    private int rotateY;
    private int rotateZ;
    private Texture texture;
    private Point3D lightLocation;

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
        ponds = new ArrayList<Pond>();
        this.sunlight = sunlight;
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }
    public List<Pond> ponds() {
        return ponds;
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
        if(x < 0 || x+1 >= width || z < 0 || z+1 >= depth) {
            return 0;
        }
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
    public void addTree(float x, float z) throws IOException {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road.
     *
     * @param
     * @param
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine, altitude(spine.get(0).getX(),spine.get(0).getY()));
        roads.add(road);
    }
    public void addPond(float x, float z, float radius) {
        Pond pond = new Pond(radius,new Point3D(x,altitude(x,z),z));
        ponds.add(pond);
    }

    public List<Point3D> getVertices() {
        List<Point3D> vertices = new ArrayList<Point3D>();
        for(int i = 0; i < width;i++) {
            for(int j = 0; j < depth; j++) {
                vertices.add(new Point3D(i,altitudes[i][j],j));
            }
        }
        return vertices;
    }

    public List<Integer> getIndices() {
        int a = 0;
        int b = 1;
        int c = width;
        int d = width+1;
        int change = 0;
        int [] indices = new int[(width-1)*(depth-1)*6];
        List<Integer> intList = new ArrayList<Integer>();
        int i = 0;
        int even = 0;
        while(d < depth*width && c < depth*width) {
            if(change == 0) {
                indices[i] = b;
                indices[i+1] = c;
                indices[i+2] = a;
                indices[i+3] = d;
                indices[i+4] = c;
                indices[i+5] = b;
            }else{
                indices[i] = d;
                indices[i+1] = b;
                indices[i+2] = a;
                indices[i+3] = c;
                indices[i+4] = d;
                indices[i+5] = a;
            }
            i = i + 6;
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
        for(int m : indices){
            intList.add(m);
        }
        return intList;

//		int m = Array.getLength(indices);
//		int counter = 0;
//		for(int j = 0; j < m; j +=3) {
//			System.out.print(indices[j] + " ");
//			System.out.print(indices[j+1] + " ");
//			System.out.println(indices[j+2]);
//			counter++;
//		}
//		System.out.println(counter);
//    	return indices;
    }

    public List<Point2D> getTexture() {
        List<Point2D> texCoords = new ArrayList<>();

        for (int row = 0; row < width; row++){
            for(int col = 0; col < depth; col++){
                texCoords.add(new Point2D((float)row,(float)col));

            }
        }
        return texCoords;
    }



    public void init(GL3 gl) {
        terrain = new TriangleMesh(getVertices(),getIndices(), true, getTexture() );
        terrain.init(gl);

//        int[] names = new int[2];
//        gl.glGenBuffers(2, names, 0);

        //verticesName = names[0];
        //indicesName = names[1];

        texture = new Texture(gl, "res/textures/grass.bmp", "bmp", false);
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
        shader.use(gl);

        for (Tree tree : trees) {
            tree.init(gl);
        }
        for (Road road: roads) {
            road.init(gl);
        }

        for (Pond pond: ponds) {
            pond.init(gl);
        }
    }

    public void terrainReshape(GL3 gl, int width, int height) {
        //Shader.setProjMatrix(gl, Matrix4.perspective(50, 1, 1, 10));
    }

    public void terrainDisplay(GL3 gl,CoordFrame3D frame, Point3D location, boolean night) {
        frame = frame.translate(0,0,0);
        //        .scale(0.1f, 0.1f, 0.1f);
        //rotateX += 1; // left right
        //rotateY += 1; // forwards
        //rotateZ += 1; // up down


        drawTerrain(gl, frame, location, night);
    }

    public void destroy(GL3 gl) {
        //gl.glDeleteBuffers(2, new int[] { indicesName, verticesName }, 0);
        terrain.destroy(gl);
    }

    public void drawTerrain(GL3 gl, CoordFrame3D frame, Point3D location, boolean night) {
        //	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,  GL3.GL_LINE);

        Shader.setInt (gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        Shader.setPenColor(gl, Color.WHITE);

        // Test light
        if (night) {
            Shader.setPoint3D(gl, "lightPos", new Point3D(location.getX(), location.getY(), location.getZ()));
            Shader.setColor(gl, "lightIntensity", Color.WHITE);
            Shader.setColor(gl, "ambientIntensity", new Color(0.9f, 0.9f, 0.9f));
            Shader.setInt(gl, "night", 1);
            Shader.setFloat(gl, "cutoff", 10);
            Shader.setFloat(gl, "attenuation", 64);
            Shader.setColor(gl, "ambientCoeff", Color.DARK_GRAY);
            Shader.setColor(gl, "diffuseCoeff", new Color(0.9f, 0.9f, 0.9f));
            Shader.setColor(gl, "specularCoeff", new Color(0.05f, 0.05f, 0.05f));
            Shader.setFloat(gl, "phongExp", 4f);
            Shader.setModelMatrix(gl, frame.getMatrix());
            System.out.println("night");
            terrain.draw(gl,frame);
            for (Tree tree : trees) {
                tree.display(gl, new Vector3(location.getX(), location.getY(), location.getZ()));
            }
            for (Road road : roads) {
                road.display(gl, new Vector3(location.getX(), location.getY(), location.getZ()), true);
            }
            for (Pond pond: ponds) {
                pond.display(gl, new Vector3(location.getX(), location.getY(), location.getZ()), true);
            }
        } else {
            Shader.setPoint3D(gl, "lightDir", new Point3D(getSunlight().asPoint3D().getX(), getSunlight().asPoint3D().getY(),getSunlight().asPoint3D().getZ()));
            Shader.setColor(gl, "lightIntensity", Color.WHITE);
            Shader.setColor(gl, "ambientIntensity", new Color(0.75f, 0.75f, 0.75f));
            Shader.setInt(gl, "night", 0);
            Shader.setColor(gl, "ambientCoeff", Color.WHITE);
            Shader.setColor(gl, "diffuseCoeff", new Color(0.8f, 0.8f, 0.8f));
            Shader.setColor(gl, "specularCoeff", new Color(0.2f, 0.2f, 0.2f));
            Shader.setFloat(gl, "phongExp", 4f);
            Shader.setModelMatrix(gl, frame.getMatrix());
            terrain.draw(gl,frame);
            for (Tree tree : trees) {
                tree.display(gl, getSunlight());
            }
            for (Road road : roads) {
                road.display(gl, getSunlight(), false);
            }
            for (Pond pond: ponds) {
                pond.display(gl, getSunlight(), false);
            }
        }
    }
}
