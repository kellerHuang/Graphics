package unsw.graphics.world;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import unsw.graphics.*;
import unsw.graphics.geometry.*;

/**
 * COMMENT: Comment Road
 *
 * @author malcolmr
 */
public class Pond {

    private Texture texture;
    private Texture texture2;
    private Texture texture3;
    private Point3D centre;
    private float radius;
    private TriangleMesh mesh;

    /**
     * Create a new road with the specified spine
     *
     */
    public Pond(float radius, Point3D centre) {
        this.centre = centre;
        this.radius = radius;
        //centre = centre.translate(0,0.05f,0);
        Point3D topLeft = centre.translate(-radius,0,radius);
        Point3D topRight = centre.translate(radius,0,radius);
        Point3D bottomLeft = centre.translate(-radius,0,-radius);
        Point3D bottomRight = centre.translate(radius,0,-radius);

        List<Point3D> vertices = new ArrayList<>();
        vertices.add(topLeft);
        vertices.add(topRight);
        vertices.add(bottomLeft);
        vertices.add(bottomRight);

        List<Integer> indices = new ArrayList<>();
        indices.add(1);
        indices.add(2);
        indices.add(0);
        indices.add(3);
        indices.add(2);
        indices.add(1);

        List<Point2D> texCoords = new ArrayList<>();
        texCoords.add(new Point2D(0,1));
        texCoords.add(new Point2D(1,1));
        texCoords.add(new Point2D(0,0));
        texCoords.add(new Point2D(1,0));

        mesh  = new TriangleMesh(vertices,indices,true,texCoords);
    }

    public void display(GL3 gl){
//        if(timer == 60){
//            timer = 0;
//        }
//        if(timer > 30){
//            loadGrass(gl);
//        }else{
            //loadRock(gl);
//        }
        float timer = (System.currentTimeMillis()/100 % 15);
        System.out.println("x");
        if(timer <5) {
            Shader.setInt(gl, "tex", 0);
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
            Shader.setPenColor(gl, Color.WHITE);
        }else if (timer >=5 && timer < 10){
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture2.getId());
            Shader.setPenColor(gl, Color.WHITE);
        } else{
            gl.glActiveTexture(GL.GL_TEXTURE0);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture3.getId());
            Shader.setPenColor(gl, Color.WHITE);
        }
        //gl.glPolygonOffset(3.0f, -3.0f);
        // Test light
        Shader.setPoint3D(gl, "lightDir", new Point3D(0, 0, 5));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.9f, 0.9f, 0.9f));

        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.75f, 0.75f, 0.75f));
        Shader.setFloat(gl, "phongExp", 16f);
        mesh.draw(gl);
    }

    public void init(GL3 gl){
        texture = new Texture(gl, "res/textures/water1.png", "png", false);
        texture2 = new Texture(gl, "res/textures/water3.png", "png", false);
        texture3 = new Texture(gl, "res/textures/water2.png", "png", false);
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
        shader.use(gl);
        mesh.init(gl);
    }

}
