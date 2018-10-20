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
 * sin cos of angle of rotation one behind
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Avatar {

    private Point3D myPos;
    private float myAngle;
    private float myScale;
    private Camera modelLoc;

    public Avatar(Camera cam) {
        myPos = cam.getMyPos();
        myAngle = cam.getMyAngle();
        myScale = 1f;
        modelLoc = cam;
    }

    public void draw(GL3 gl, CoordFrame3D frame) {
        CoordFrame3D cameraFrame = frame.translate(myPos)
                .rotateZ(myAngle)
                .scale(myScale, myScale, myScale);
    }

//    public void setView(GL3 gl) {
//        myAngle = modelLoc.getMyAngle();
//        myPos = modelLoc.getMyPos();
//        float x = (float)Math.sin(Math.toRadians(myAngle));
//        float z = (float)Math.cos(Math.toRadians(myAngle));
//        CoordFrame3D viewFrame = CoordFrame3D.identity()
//                .scale(1/myScale, 1/myScale, 1/myScale)
//                .rotateY(myAngle)
//                .translate(-myPos.getX()-x, -myPos.getY(), -myPos.getZ()-z);
//        Shader.setViewMatrix(gl, viewFrame.getMatrix());
//    }

}