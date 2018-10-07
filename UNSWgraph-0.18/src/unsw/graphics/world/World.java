package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D{

    private Terrain terrain;
    private Camera camera;
    private Point3D myPos;
    private float myAngle;
    
    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        camera = new Camera(terrain);
        myAngle = 0;
        myPos = new Point3D(0,0,0);
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws IOException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		camera.setView(gl);
		terrain.drawTerrain(gl,CoordFrame3D.identity());
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		terrain.destroy(gl);
		
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);
        getWindow().addKeyListener(camera);
//	    Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
//	    shader.use(gl);
//	    // Test light
//	    Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
//	    Shader.setColor(gl, "lightIntensity", Color.WHITE);
//	    Shader.setColor(gl, "ambientIntensity", new Color(0.3f, 0.3f, 0.3f));
//	
//	    Shader.setColor(gl, "ambientCoeff", Color.WHITE);
//	    Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
//	    Shader.setColor(gl, "specularCoeff", new Color(0.75f, 0.75f, 0.75f));
//	    Shader.setFloat(gl, "phongExp", 16f);
		terrain.init(gl);
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 0.01f, 100));
	}
	

}
