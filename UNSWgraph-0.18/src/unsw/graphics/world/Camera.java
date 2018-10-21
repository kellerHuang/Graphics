package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.*;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

import java.awt.*;
import java.io.IOException;


/**
 * The camera for the person demo
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Camera implements KeyListener {

	private Point3D myPos;
	private float myAngle;
	private float myScale;
	private Terrain terrain;
	private Texture texture;
	private TriangleMesh model;
	public Camera(Terrain terrain) throws IOException {
		myPos = new Point3D(0, 1,20);
		myAngle = 0;
		myScale = 1f;
		model = new TriangleMesh("res/models/bunny.ply", true, true);
		this.terrain = terrain;
	}

	public void draw(GL3 gl, CoordFrame3D frame) {
		CoordFrame3D cameraFrame = frame.translate(myPos)
				.rotateZ(myAngle)
				.scale(myScale, myScale,myScale);
	}
	public void init(GL3 gl) {
		model.init(gl);
		texture = new Texture(gl, "res/textures/rock.bmp", "bmp", false);
		Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
		shader.use(gl);
	}
	public void display(GL3 gl) {
		Shader.setInt (gl, "tex", 0);
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
		Shader.setPenColor(gl, Color.GREEN);

		// Test light
		Matrix4 rotate = Matrix4.rotationY(myAngle);
		Vector3 rotated = rotate.multiply(terrain.getSunlight().extend()).trim();
		Shader.setPoint3D(gl, "lightDir", new Point3D(rotated.getX(),rotated.getY(),rotated.getZ()));
		Shader.setColor(gl, "lightIntensity", Color.WHITE);
		Shader.setColor(gl, "ambientIntensity", new Color(0.3f, 0.3f, 0.3f));

		Shader.setColor(gl, "ambientCoeff", Color.WHITE);
		Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
		Shader.setColor(gl, "specularCoeff", new Color(0.75f, 0.75f, 0.75f));
		Shader.setFloat(gl, "phongExp", 16f);

		CoordFrame3D frame = CoordFrame3D.identity().translate(myPos).translate(0,-1,0).rotateY(-myAngle+180).scale(2,2,2);
		model.draw (gl, frame);
	}
	public Point3D getMyPos(){
		return myPos;
	}
	public int getMyAngle(){
		return (int)myAngle;
	}
	/**
	 * Set the view transform
	 *
	 * Note: this is the inverse of the model transform above
	 *
	 * @param gl
	 */
	public void setView(GL3 gl) {
		CoordFrame3D viewFrame = CoordFrame3D.identity()
				.scale(1/myScale, 1/myScale, 1/myScale)
				.rotateY(myAngle)
				.translate(-myPos.getX(), -myPos.getY(), -myPos.getZ());
		Shader.setViewMatrix(gl, viewFrame.getMatrix());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		float x;
		float y;
		switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				myAngle -=30;
				break;
			case KeyEvent.VK_RIGHT:
				myAngle += 30;
				break;
			case KeyEvent.VK_DOWN:
				x = (float)Math.sin(Math.toRadians(myAngle))/5;
				y = (float)Math.cos(Math.toRadians(myAngle))/5;
				myPos = myPos.translate(-x,0,y);
				myPos = myPos.translate(0, terrain.altitude(myPos.getX(), myPos.getZ())-myPos.getY()+1, 0);
				break;
			case KeyEvent.VK_UP:
				x = (float)Math.sin(Math.toRadians(myAngle))/5;
				y = (float)Math.cos(Math.toRadians(myAngle))/5;
				myPos = myPos.translate(x,0,-y);
				myPos = myPos.translate(0, terrain.altitude(myPos.getX(), myPos.getZ())-myPos.getY()+1, 0);
				break;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {}

}