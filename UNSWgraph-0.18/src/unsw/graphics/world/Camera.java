package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;



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

    public Camera() {
        myPos = new Point3D(0, 0,1);
        myAngle = 0;
        myScale = 1f;
    }
    
    public void draw(GL3 gl, CoordFrame3D frame) {
        CoordFrame3D cameraFrame = frame.translate(myPos)
                .rotateZ(myAngle)
                .scale(myScale, myScale,myScale);
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
        	System.out.println("left");
        	myAngle -=10;
        	break;
        case KeyEvent.VK_RIGHT:
           	System.out.println("right");
        	myAngle += 10; 
        	break;
        case KeyEvent.VK_DOWN:
        	System.out.println("down");
        	x = (float)Math.sin(Math.toRadians(myAngle));
        	y = (float)Math.cos(Math.toRadians(myAngle));
        	myPos = myPos.translate(-x,0,y);
        	System.out.println(myAngle);
        	System.out.println(myPos.getX());
        	System.out.println(myPos.getY());
        	System.out.println(myPos.getZ());
        	break;
        case KeyEvent.VK_UP:
        	System.out.println("up");
        	x = (float)Math.sin(Math.toRadians(myAngle));
        	y = (float)Math.cos(Math.toRadians(myAngle));
        	myPos = myPos.translate(x,0,-y);
        	System.out.println(myAngle);
        	System.out.println(myPos.getX());
        	System.out.println(myPos.getY());
        	System.out.println(myPos.getZ());
        	break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {}

}