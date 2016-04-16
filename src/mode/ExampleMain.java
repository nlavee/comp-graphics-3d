package src.mode;

/*

  Example program for CS325

  Author: Michael Eckmann

  Updated to work with JOGL 2.3.2 (from October 2015 build)

  This class contains the main method and draws a window and creates an object of 
  DrawAndHandleInput which in a GLEventListener as well as a KeyListener and MouseListener.

  
*/
import java.awt.Frame;
import java.awt.event.*;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

public class ExampleMain {


	private static DrawAndHandleInput dahi;
	public static Frame testFrame;
	public static void main(String[] args) 
  	{
	    /* Create the Frame */
        	testFrame = new Frame("TestFrame");
 
        
	        /* set the coordinates on the screen of the
	       upper left corner of the window 

	       So the window will start off at 10,10 
	       (near the upper left corner of the whole screen)
	       */
        	testFrame.setLocation(10, 10);

	    /* set the window to be 400x500 pixels 
               higher b/c of borders
            */
	        testFrame.setSize( 500, 500 );


		// This allows us to define some attributes
		// about the capabilities of GL for this program
		// such as color depth, and whether double buffering is
		// used.
		//GLCapabilities glCapabilities = new GLCapabilities();

        GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        
		glCapabilities.setRedBits(8);
		glCapabilities.setGreenBits(8);
		glCapabilities.setBlueBits(8);
		glCapabilities.setAlphaBits(8);

		/*
		 * this will turn on double buffering
		 * ignore for now
		 * glCapabilities.setDoubleBuffered(true);
		 */
		glCapabilities.setDoubleBuffered(true);
		// create the GLCanvas that is to be added to our Frame
		GLCanvas canvas = new GLCanvas(glCapabilities);
		testFrame.add( canvas );

		// create the Animator and attach the GLCanvas to it
		Animator a = new Animator(canvas);
		
		// create an instance of the Class that listens to all events
		// (GLEvents, Keyboard, and Mouse events)
		// add this object as all these listeners to the canvas 
		dahi = new DrawAndHandleInput(canvas);
		canvas.addGLEventListener(dahi);
		canvas.addKeyListener(dahi);
		canvas.addMouseListener(dahi);

		// this will swap the buffers (when double buffering)
		// ignore for now
		// canvas.swapBuffers();
		
		// if user closes the window by clicking on the X in 
		// upper right corner
		testFrame.addWindowListener( new WindowListener() {
		    public void windowClosing(WindowEvent e) {
		      System.exit(0);
		    }
		    public void windowClosed(WindowEvent e) {
			      
		    }
		    public void windowDeiconified(WindowEvent e) {
			      
		    }
		    public void windowIconified(WindowEvent e) {
			      
		    }
		    public void windowOpened(WindowEvent e) {
			      
			    }
		    public void windowDeactivated(WindowEvent e) {
			      
		    }
		    public void windowActivated(WindowEvent e) {
			      
		    }
		  });
/*		
		.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		      System.exit(0);
		    }
		  });
	*/	
		testFrame.setVisible(true);
		a.start(); // start the Animator, which periodically calls display() on the GLCanvas

	}
}
