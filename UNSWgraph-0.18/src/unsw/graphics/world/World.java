package unsw.graphics.world;

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
public class World extends Application3D implements KeyListener{

    private Terrain terrain;
    private Camera camera;
    private Avatar avatar;
    private Point3D myPos;
    private float myAngle;
    private Boolean thirdPerson = false;
    private Boolean night = false;

    public World(Terrain terrain) {
        super("Assignment 2", 800, 600);
        this.terrain = terrain;
        try {
            camera = new Camera(terrain);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myAngle = 0;
        myPos = new Point3D(0,0,0);
        avatar = new Avatar(camera);
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


        if (thirdPerson){
            avatar.setView(gl);
            camera.display(gl, night);
            //camera.setView(gl);
        }else{
            camera.setView(gl);
        }
        if (night) {
            terrain.terrainDisplay(gl, CoordFrame3D.identity(), camera.myPos, true);
        } else {
            terrain.terrainDisplay(gl, CoordFrame3D.identity(), camera.myPos, false);
        }

        //terrain.terrainDisplay(gl,CoordFrame3D.identity());
    }

    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        terrain.destroy(gl);

    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);
        gl.glEnable(GL3.GL_POLYGON_OFFSET_FILL);
        camera.init(gl);
        getWindow().addKeyListener(camera);
        getWindow().addKeyListener(this);
        terrain.init(gl);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 0.01f, 100));
        terrain.terrainReshape(gl, width, height);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_V:
                if(thirdPerson){
                    thirdPerson = false;
                }else{
                    thirdPerson = true;
                }
                System.out.println(thirdPerson);
                break;
            case KeyEvent.VK_B:
                night ^= true;
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

}
