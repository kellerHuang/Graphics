package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;


/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree extends Application3D {

    private Point3D position;
    private TriangleMesh model;
    private Texture texture;

    
    public Tree(float x, float y, float z) throws IOException {
        super("Tree", 600,600);
        model = new TriangleMesh("C:\\Users\\Evan\\Desktop\\Graphics2\\UNSWgraph-0.18\\src\\res\\models\\tree.ply", true, true);
        position = new Point3D(x, y, z);
    }

    @Override
    public void init(GL3 gl) {
        super.init(gl);
        model.init(gl);
        texture = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
        shader.use(gl);
    }

    @Override
    public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(45, 1, 1, 100));
    }

    @Override
    public void display(GL3 gl) {
        super.display(gl);
        Shader.setInt (gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        Shader.setPenColor(gl, Color.BLACK);

        // Test light
        Shader.setPoint3D(gl, "lightPos", new Point3D(0, 0, 5));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.3f, 0.3f, 0.3f));

        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.75f, 0.75f, 0.75f));
        Shader.setFloat(gl, "phongExp", 16f);

        CoordFrame3D frame = CoordFrame3D.identity();

        CoordFrame3D treeFrame = frame
                .translate (0, 0.3f, 0)
                .scale (0.2f, 0.2f, 0.2f);

        model.draw (gl, treeFrame);
    }

    @Override
    public void destroy(GL3 gl) {
        super.destroy(gl);
        model.destroy(gl);
        texture.destroy(gl);
    }



    public Point3D getPosition() {
        return position;
    }
    

}
