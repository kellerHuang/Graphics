package unsw.graphics.world;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import unsw.graphics.*;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Road
 *
 * @author malcolmr
 */
public class Road {

    private List<Point2D> points;
    private float width;
    private Texture texture;
    private List<TriangleMesh> meshes = new ArrayList<>();
    private float altitude;
    private int segments = 32;
    private List<Point3D> topRoad;
    private List<Point3D> bottomRoad;
    private Point3D extra;


    /**
     * Create a new road with the specified spine
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine, float altitude) {
        this.width = width;
        this.points = spine;
        this.altitude = altitude;
        topRoad = new ArrayList<>();
        bottomRoad = new ArrayList<>();
    }

    public void display(GL3 gl, Vector3 lightDir){
        Shader.setInt (gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        Shader.setPenColor(gl, Color.WHITE);

        // Test light
        Shader.setPoint3D(gl, "lightPos", new Point3D(lightDir.getX(), lightDir.getY(), lightDir.getZ()));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.3f, 0.3f, 0.3f));

        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
        Shader.setColor(gl, "specularCoeff", new Color(0.75f, 0.75f, 0.75f));
        Shader.setFloat(gl, "phongExp", 16f);

        for(TriangleMesh mesh: meshes){
            mesh.draw(gl);
        }
    }

    public void init(GL3 gl){
        texture = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
        Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
        shader.use(gl);
        for (int i = 0; i < size(); i++) {
            makeExtrusion(i);
        }

        meshes.add(makeMesh(bottomRoad,topRoad));
        for(TriangleMesh i: meshes){
            i.init(gl);
        }

    }


    private void makeExtrusion(int offset) {
        //The initial shape we're extruding
        List<Point3D> roadControl = new ArrayList<Point3D>();
        List<Point3D> roadPoints = new ArrayList<Point3D>();
        for(Point2D p: points) {
            roadControl.add(new Point3D(p.getX(),altitude,p.getY()));
        }


        float segmentSize = 1.0f/segments;
        if(extra != null){
            roadPoints.add(extra);
        }
        for(int i = 0; i<segments; i++){
            roadPoints.add(new Point3D(point(i*segmentSize + offset).getX(),altitude+0.05f,point(i*segmentSize+offset).getY()));

        }

        if(offset < size()-1){
            extra = roadPoints.get(roadPoints.size()-1);
            roadPoints.add(new Point3D(point(1+ offset).getX(),altitude+0.05f,point(1+offset).getY()));
//            extra = new Point3D(point(1+ offset).getX(),altitude+0.05f,point(1+offset).getY());
        }
        //roadPoints.add(new Point3D(endPoint(0+offset).getX(),altitude+0.01f,endPoint(0+offset).getY()));

        // extrude the top points

        for(int i = 0; i < roadPoints.size(); i++){
            // start point case
            Matrix4 rot90 = Matrix4.rotationY(90);
            Matrix4 rot270 = Matrix4.rotationY(270);
            Point3D a;
            Point3D b;
            Vector3 c;
            Vector4 d;
            if (i == roadPoints.size()-1){
                a = roadPoints.get(i);
                b = roadPoints.get(i-1);
                c = b.minus(a).normalize().scale(width/2);
                d = new Vector4(c.getX(),c.getY(),c.getZ(),0);
                topRoad.add(roadPoints.get(i).translate(rot270.multiply(d).trim()));
                bottomRoad.add(roadPoints.get(i).translate(rot90.multiply(d).trim()));
            }else {
                a = roadPoints.get(i);
                b = roadPoints.get(i + 1);
                c = a.minus(b).normalize().scale(width/2);
                d = new Vector4(c.getX(), c.getY(), c.getZ(), 0);
                topRoad.add(roadPoints.get(i).translate(rot90.multiply(d).trim()));
                bottomRoad.add(roadPoints.get(i).translate(rot270.multiply(d).trim()));
            }

        }
    }

    private TriangleMesh makeMesh(List<Point3D> a,List<Point3D> b){
        List<Point3D> full = new ArrayList<>();
        for(Point3D i: a){
            full.add(i);
        }
        for(Point3D i: b){
            full.add(i);
        }
        List<Integer> indices = new ArrayList<>();
        for(int i = 0 ; i < b.size()-1;i++){
            int w = i;
            int x = i+1;
            int y = b.size()+i;
            int z = b.size()+i+1;

            indices.add(w);
            indices.add(y);
            indices.add(x);
            indices.add(x);
            indices.add(y);
            indices.add(z);

        }
        TriangleMesh road = new TriangleMesh(full,indices,true);
        return road;
    }

    /**
     * The width of the road.
     *
     * @return
     */
    public double width() {
        return width;
    }

    /**
     * Get the number of segments in the curve
     *
     * @return
     */
    public int size() {
        return points.size() / 3;
    }

    /**
     * Get the specified control point.
     *
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return points.get(i);
    }

    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     *
     * @param t
     * @return
     */
    public Point2D point(float t) {
        int i = (int)Math.floor(t);
        t = t - i;

        i *= 3;

        Point2D p0 = points.get(i++);
        Point2D p1 = points.get(i++);
        Point2D p2 = points.get(i++);
        Point2D p3 = points.get(i++);


        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();

        return new Point2D(x, y);
    }

    /**
     * Calculate the Bezier coefficients
     *
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {

        switch(i) {

            case 0:
                return (1-t) * (1-t) * (1-t);

            case 1:
                return 3 * (1-t) * (1-t) * t;

            case 2:
                return 3 * (1-t) * t * t;

            case 3:
                return t * t * t;
        }

        // this should never happen
        throw new IllegalArgumentException("" + i);
    }


}
