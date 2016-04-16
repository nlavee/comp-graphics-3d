package src.mode;

/*

  Example program for CS325

  Author: Michael Eckmann

  Updated to work with JOGL 2.3.2 (from October 2015 build)

  This class DrawAndHandleInput which in a GLEventListener as well as a KeyListener and MouseListener
  displays a grid of "big" pixels for student use to add code to draw Bresenham Lines, Circles, and
  do antialiasing.

 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;

import src.models.Matrix;
import src.models.Polygon;
import src.models.Vector;
import src.models.Vertex;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
//import com.jogamp.nativewindow.util.*;

public class DrawAndHandleInput implements GLEventListener, KeyListener, MouseListener
{

	/* this object will give us access to the gl functions */
	private GL2            gl;
	/* this object will give us access to the glu functions */
	private GLU            glu;

	/* define the world coordinate limits */
	public static  final int MIN_X =-1;
	public static  final int MIN_Y =-1;
	public static  final int MAX_X =1;
	public static  final int MAX_Y =1;

	/* Initialize the mode enum to the start enum 
	 * and set that we have changed the mode in order to 
	 * force calculation to take place
	 * */
	private static Mode mode = Mode.START;
	private static boolean modeChanged = true;

	private GLCanvas canvas;

	/*
	 * Data structure to save the transformation data so that 
	 * we do not have to continuously perform computation
	 */
	private Vertex[] vTransformedList;
	private ArrayList<Polygon> pFrontFaceList;

	Matrix rotationDoneSoFar = null;

	public DrawAndHandleInput(GLCanvas c)
	{
		this.canvas = c;
	}
	// ====================================================================================
	//
	// Start of the methods in GLEventListener
	//

	/**
	   =============================================================
	   This method is called by the drawable to do initialization. 
  	   =============================================================

  	   @param drawable The GLCanvas that will be drawn to

	 */
	public void init(GLAutoDrawable drawable)
	{
		this.gl = drawable.getGL().getGL2();
		this.glu = new GLU(); // from demo for new version

		/* Set the clear color to black */
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		/* sets up the projection matrix from world to window coordinates */
		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();
		/* show the whole world within the window */
		glu.gluOrtho2D(MIN_X, MAX_X, MIN_Y, MAX_Y);

		/* sets up the modelview matrix */
		/* ignore this for now
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, 0.0f);
		 */

		// wraps the GL to provide error checking and so
		// it will throw a GLException at the point of failure
		drawable.setGL( new DebugGL2(drawable.getGL().getGL2() ));

	} // end init

	/**
	   =============================================================
	   This method is called when the screen needs to be drawn.
	   =============================================================

	   @param drawable The GLCanvas that will be drawn to

	 */
	public void display(GLAutoDrawable drawable)
	{
		//float r1, g1, b1; /* red, green and blue values */

		/* clear the color buffer */
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		/* sets up the current color for drawing 
	       for polygons, it's the "fill color" */
		//gl.glColor3f(1,1,1);


		/*
		 * if we have seen the mode changed, we calculate the whole
		 * nPer matrix and operations done on the vertices
		 */
		if(modeChanged) 
		{
			detectMode();
			Matrix nPer = normalizingToCvv();
			//			System.out.println("nPer Matrix: \n" + nPer);
			vTransformedList = new Vertex[ReadObjectAndViewingFiles.v.length];
			pFrontFaceList = new ArrayList<Polygon>();

			for( Polygon p : ReadObjectAndViewingFiles.polygon)
			{

				// Get a list of 3 test vertices
				ArrayList<Vertex> testVertices = new ArrayList<Vertex>();

				// Take three vertices to do back face culling 
				for(int vIdx : p.vertexIndices)
				{
					Vertex v = ReadObjectAndViewingFiles.v[vIdx];
					Vertex vTransformed = nPer.multiply(v); // apply transformation
					if(rotationDoneSoFar != null) vTransformed = rotationDoneSoFar.multiply(vTransformed);

					// take three, also save the transformed vertex
					if(testVertices.size() < 4)
					{
						testVertices.add(vTransformed);
					}
					else break;
				}

				// check back face, if not, then we carry out with the full calculation
				boolean isBackFace = checkBackFace(testVertices);
				if(!isBackFace)
				{
					pFrontFaceList.add(p);
					gl.glBegin(GL2.GL_POLYGON);
					gl.glColor3f((float)p.getRed(),(float)p.getGreen(),(float)p.getBlue());
					for(int vIdx : p.vertexIndices)
					{
						Vertex v = ReadObjectAndViewingFiles.v[vIdx];
						Vertex vTransformed = nPer.multiply(v); // apply transformation
						
						// if we have done rotation, we can apply transformation 
						// and then do the rotation by the same amount
						if(rotationDoneSoFar != null) vTransformed = rotationDoneSoFar.multiply(vTransformed);

						/* 
						 * Save the transformed vertex in 3D so we can use it without
						 * the need to recompute the values.
						 */					
						vTransformedList[vIdx] = vTransformed;

						Vertex v2D = tranformVertexTo2D( vTransformed ); // transformed to 2D
						gl.glVertex2d( v2D.getX1(), v2D.getX2() );
					}
					gl.glEnd();
				}
			}
		}
		else
		{
			//System.out.println("redrawing.." + pFrontFaceList.size() + " polygon");

			/*
			 * Getting the polygon from the original list and the list 
			 * of vertices from what we have stored when we first did the calculation 
			 * to save time at the expense of space 
			 */
			for( Polygon p : pFrontFaceList)
			{
				gl.glBegin(GL2.GL_POLYGON);
				gl.glColor3f((float)p.getRed(),(float)p.getGreen(),(float)p.getBlue());
				for(int vIdx : p.vertexIndices)
				{
					// Call the saved vertices
					Vertex vTransformed = vTransformedList[vIdx];
					Vertex v2D = tranformVertexTo2D( vTransformed );
					gl.glVertex2d( v2D.getX1(), v2D.getX2() );
				}
				gl.glEnd();
			}
		}

		/* force any buffered calls to actually be executed */
		gl.glFlush();

	} // end display

	/**
	 * Method that is called when we have changed the mode of operation
	 * For the new mode of operation, we would have specific method associated
	 */
	private void detectMode() {
		modeChanged = false;
		switch(mode)
		{
		case QUIT:
			System.out.println("NOTIFICATION: You have chosen to exit the program");
			System.exit(0);
			break;
		case ZOOM_IN:
			System.out.println("NOTIFICATION: You have entered zoom in mode");
			doZoomIn();
			break;
		case ZOOM_OUT:
			System.out.println("NOTIFICATION: You have entered zoom out mode");
			doZoomOut();
			break;
		case VRP_POSITIVE_U:
			System.out.println("NOTIFICATION: You have entered mode to change VRP in Positive u");
			doVrpPositiveU();
			break;
		case VRP_POSITIVE_V:
			System.out.println("NOTIFICATION: You have entered mode to change VRP in Positive v");
			doVrpPositiveV();
			break;
		case VRP_POSITIVE_N:
			System.out.println("NOTIFICATION: You have entered mode to change VRP in Positive n");
			doVrpPositiveN();
			break;
		case VRP_NEGATIVE_U:
			System.out.println("NOTIFICATION: You have entered mode to change VRP in Negative u");
			doVrpNegativeU();
			break;
		case VRP_NEGATIVE_V:
			System.out.println("NOTIFICATION: You have entered mode to change VRP in Negative v");
			doVrpNegativeV();
			break;
		case VRP_NEGATIVE_N:
			System.out.println("NOTIFICATION: You have entered mode to change VRP in Negative n");
			doVrpNegativeN();
			break;
		case ROTATION_CLOCKWISE_X:
			System.out.println("NOTIFICATION: You have entered mode to rotate clockwise around x-axis");
			doRotation();
			break;
		case ROTATION_CLOCKWISE_Y:
			System.out.println("NOTIFICATION: You have entered mode to rotate clockwise around y-axis");
			doRotation();
			break;
		case ROTATION_CLOCKWISE_Z:
			System.out.println("NOTIFICATION: You have entered mode to rotate clockwise around z-axis");
			doRotation();
			break;
		case ROTATION_COUNTER_CLOCKWISE_X: 
			System.out.println("NOTIFICATION: You have entered mode to rotate counterclockwise around x-axis");
			doRotation();
			break;
		case ROTATION_COUNTER_CLOCKWISE_Y:
			System.out.println("NOTIFICATION: You have entered mode to rotate counterclockwise around y-axis");
			doRotation();
			break;
		case ROTATION_COUNTER_CLOCKWISE_Z:
			System.out.println("NOTIFICATION: You have entered mode to rotate counterclockwise around z-axis");
			doRotation();
			break;
		case DEFAULT:
			System.out.println("NOTIFICATION: You have requested to revert all changes.");
			revertAllChanges();
			break;
		case START:
			break;
		}
	}

	/**
	 * Since we have been changing values, this reinitialize
	 * the values we need for calculating nPer
	 */
	private void revertAllChanges() {

		//reset all values
		rotationDoneSoFar = null;
		ReadObjectAndViewingFiles.vrp = new Vertex(
				ReadObjectAndViewingFiles.vrpTemp.getX1(), 
				ReadObjectAndViewingFiles.vrpTemp.getX2(), 
				ReadObjectAndViewingFiles.vrpTemp.getX3(), 
				ReadObjectAndViewingFiles.vrpTemp.getX4());
		ReadObjectAndViewingFiles.prp = new Vertex(
				ReadObjectAndViewingFiles.prpTemp.getX1(), 
				ReadObjectAndViewingFiles.prpTemp.getX2(), 
				ReadObjectAndViewingFiles.prpTemp.getX3(), 
				ReadObjectAndViewingFiles.prpTemp.getX4());
		ReadObjectAndViewingFiles.vpn = new Vector(
				ReadObjectAndViewingFiles.vpnTemp.getX1(), 
				ReadObjectAndViewingFiles.vpnTemp.getX2(), 
				ReadObjectAndViewingFiles.vpnTemp.getX3());
		ReadObjectAndViewingFiles.vup = new Vector(
				ReadObjectAndViewingFiles.vupTemp.getX1(), 
				ReadObjectAndViewingFiles.vupTemp.getX2(), 
				ReadObjectAndViewingFiles.vupTemp.getX3());
		ReadObjectAndViewingFiles.prp = new Vertex(
				ReadObjectAndViewingFiles.prpTemp.getX1(),
				ReadObjectAndViewingFiles.prpTemp.getX2(),
				ReadObjectAndViewingFiles.prpTemp.getX3(), 
				ReadObjectAndViewingFiles.prpTemp.getX4());
		ReadObjectAndViewingFiles.umin = new Double(ReadObjectAndViewingFiles.uminTemp);
		ReadObjectAndViewingFiles.umax = new Double(ReadObjectAndViewingFiles.umaxTemp); 
		ReadObjectAndViewingFiles.vmin = new Double(ReadObjectAndViewingFiles.vminTemp);
		ReadObjectAndViewingFiles.vmax = new Double(ReadObjectAndViewingFiles.vmaxTemp);
		ReadObjectAndViewingFiles.frontClip = new Double(ReadObjectAndViewingFiles.frontClipTemp);
		ReadObjectAndViewingFiles.backClip = new Double(ReadObjectAndViewingFiles.backClipTemp);

	}

	/**
	 * Method to project 3D points into 2D
	 * @param vTransformed
	 * @return
	 */
	private Vertex tranformVertexTo2D(Vertex vTransformed) {

		/*
		x = x1*t
		y = y1*t
		z = z1*t

		since the view plane is on z = -1, we solve the last equation above for t by:  -1 = z1*t to be: t = -1/z1
		So, the projected x,y is: x = -1*x1/z1 and y = -1*y1/z1
		 */
		double x = vTransformed.getX1() / ( -1 * vTransformed.getX3() ) ;
		double y = vTransformed.getX2() / ( -1 * vTransformed.getX3() );
		Vertex v2D = new Vertex(x, y, 0);

		return v2D;
	}

	/**
	 * Method to check whether a polygon is backfacing. This method is useful for determining whether we 
	 * should draw a specific polygon.
	 * @param Arraylist of 3 vertices to test back facing
	 * @return
	 */
	private boolean checkBackFace(ArrayList<Vertex> testVertices) {
		/**
		 * Calculating normal vector by doing cross product on two vectors on the
		 * plane of the polygon
		 */
		Vector V1 = testVertices.get(2).subtractVertices(testVertices.get(1));
		Vector V2 = testVertices.get(0).subtractVertices(testVertices.get(1));
		Vector N = V1.crossProduct(V2);
		//		System.out.println("V1: \n " + V1);
		//		System.out.println("V2: \n " + V2);
		//		System.out.println("N: \n " + N);

		/**
		 * Getting values for different coefficient for equation of 
		 * the plane
		 */
		double D = -1 * ( 
				N.getX1() * testVertices.get(2).getX1() +  
				N.getX2() * testVertices.get(2).getX2() + 
				N.getX3() * testVertices.get(2).getX3()
				);


		/**
		 * Perform the check, if negative value, we're having a backface, 
		 * if not, we are not having a backface
		 */
		//		System.out.println(D);
		if(D < 0) return true;
		else return false;
	}

	/**
	 * This method calculate the matrix needed to do the rotation.
	 * The matrix is then saved to the global matrix rotationDoneSoFar so that
	 * every time need to do rotation, we can just take rotationDoneSoFar multiply with
	 * the transformed vertex
	 */
	private void doRotation() {
		double degree = 10; // currently rotating by 10 degree
		
		if(mode.getKey() == 'a') // Rotate Clockwise Around x-axis
		{
			double[][] rotateMatrixData = {
					{1, 0, 0, 0},
					{0, Math.cos(Math.toRadians(-1*degree)), -1 * Math.sin(Math.toRadians(-1*degree)), 0},
					{0, Math.sin(Math.toRadians(-1*degree)), Math.cos(Math.toRadians(-1*degree)), 0},
					{0, 0, 0 ,1}
			};
			Matrix rotateMatrix = new Matrix(rotateMatrixData);
			
			if(rotationDoneSoFar == null) rotationDoneSoFar = rotateMatrix;
			else rotationDoneSoFar = rotateMatrix.multiply(rotationDoneSoFar);
		}
		else if(mode.getKey() == 's') //Rotate Clockwise Around y-axis
		{
			double[][] rotateMatrixData = {
					{Math.cos(Math.toRadians(-1*degree)), 0, Math.sin(Math.toRadians(-1*degree)), 0},
					{0, 1, 0, 0},
					{-1 * Math.sin(Math.toRadians(-1 * degree)), 0, Math.cos(Math.toRadians(-1 * degree)), 0},
					{0, 0, 0 ,1}
			};
			Matrix rotateMatrix = new Matrix(rotateMatrixData);
			
			if(rotationDoneSoFar == null) rotationDoneSoFar = rotateMatrix;
			else rotationDoneSoFar = rotateMatrix.multiply(rotationDoneSoFar);
		}
		else if(mode.getKey() == 'd') //Rotate Clockwise Around z-axis
		{
			double[][] rotateMatrixData = {
					{Math.cos(Math.toRadians(-1*degree)), -1 * Math.sin(Math.toRadians(-1 * degree)), 0, 0},
					{Math.sin(Math.toRadians(-1*degree)), Math.cos(Math.toRadians(-1*degree)), 0, 0},
					{0, 0, 1, 0},
					{0, 0, 0 ,1}
			};
			Matrix rotateMatrix = new Matrix(rotateMatrixData);
			
			if(rotationDoneSoFar == null) rotationDoneSoFar = rotateMatrix;
			else rotationDoneSoFar = rotateMatrix.multiply(rotationDoneSoFar);
		}
		else if(mode.getKey() == 'z') // Rotate Counterclockwise Around x-axis
		{
			double[][] rotateMatrixData = {
					{1, 0, 0, 0},
					{0, Math.cos(Math.toRadians(degree)), -1 * Math.sin(Math.toRadians(degree)), 0},
					{0, Math.sin(Math.toRadians(degree)), Math.cos(Math.toRadians(degree)), 0},
					{0, 0, 0 ,1}
			};
			Matrix rotateMatrix = new Matrix(rotateMatrixData);
			
			if(rotationDoneSoFar == null) rotationDoneSoFar = rotateMatrix;
			else rotationDoneSoFar = rotateMatrix.multiply(rotationDoneSoFar);
		}
		else if(mode.getKey() == 'x') // Rotate Counterclockwise Around y-axis
		{
			double[][] rotateMatrixData = {
					{Math.cos(Math.toRadians(degree)), 0, Math.sin(Math.toRadians(degree)), 0},
					{0, 1, 0, 0},
					{-1 * Math.sin(Math.toRadians(degree)), 0, Math.cos(Math.toRadians(degree)), 0},
					{0, 0, 0 ,1}
			};
			Matrix rotateMatrix = new Matrix(rotateMatrixData);
			
			if(rotationDoneSoFar == null) rotationDoneSoFar = rotateMatrix;
			else rotationDoneSoFar = rotateMatrix.multiply(rotationDoneSoFar);
		}
		else if(mode.getKey() == 'c') //Rotate Counterclockwise Around z-axis
		{
			double[][] rotateMatrixData = {
					{Math.cos(Math.toRadians(degree)), -1 * Math.sin(Math.toRadians(degree)), 0, 0},
					{Math.sin(Math.toRadians(degree)), Math.cos(Math.toRadians(degree)), 0, 0},
					{0, 0, 1, 0},
					{0, 0, 0 ,1}
			};
			Matrix rotateMatrix = new Matrix(rotateMatrixData);
			
			if(rotationDoneSoFar == null) rotationDoneSoFar = rotateMatrix;
			else rotationDoneSoFar = rotateMatrix.multiply(rotationDoneSoFar);
		}
	}
	
	/**
	 * Method to decrease N of VRP by 1.0
	 */
	private void doVrpNegativeN() {
		ReadObjectAndViewingFiles.vrp.setX3(ReadObjectAndViewingFiles.vrp.getX3() - 1.0);
	}

	/**
	 * Method to decrease V of VRP by 1.0
	 */
	private void doVrpNegativeV() {
		ReadObjectAndViewingFiles.vrp.setX2(ReadObjectAndViewingFiles.vrp.getX2() - 1.0);
	}

	/**
	 * Method to decrease U of VRP by 1.0
	 */
	private void doVrpNegativeU() {
		ReadObjectAndViewingFiles.vrp.setX1(ReadObjectAndViewingFiles.vrp.getX1() - 1.0);
	}

	/**
	 * Method to increase N of VRP by 1.0
	 */
	private void doVrpPositiveN() {
		ReadObjectAndViewingFiles.vrp.setX3(ReadObjectAndViewingFiles.vrp.getX3() + 1.0);
	}

	/**
	 * Method to increase V of VRP by 1.0
	 */
	private void doVrpPositiveV() {
		ReadObjectAndViewingFiles.vrp.setX2(ReadObjectAndViewingFiles.vrp.getX2() + 1.0);
	}

	/**
	 * Method to increase U of VRP by 1.0
	 */
	private void doVrpPositiveU() {
		ReadObjectAndViewingFiles.vrp.setX1(ReadObjectAndViewingFiles.vrp.getX1() + 1.0);
	}
	
	/**
	 * Apply zooming through manipulating the parameter umin, umax, vmin, vmax
	 */
	private void doZoomOut() {
		ReadObjectAndViewingFiles.umax += 1;
		ReadObjectAndViewingFiles.vmax += 1;
		ReadObjectAndViewingFiles.umin -= 1;
		ReadObjectAndViewingFiles.vmin -= 1;
	}
	
	/**
	 * Apply zooming through manipulating the parameter umin, umax, vmin, vmax
	 */
	private void doZoomIn() {
		ReadObjectAndViewingFiles.umax -= 1;
		ReadObjectAndViewingFiles.vmax -= 1;
		ReadObjectAndViewingFiles.umin += 1;
		ReadObjectAndViewingFiles.vmin += 1;
	}

	/**
	 * Method to calculate the Transformation Matrix needed to do perspective projection
	 * @return the Transformation Matrix
	 */
	private static Matrix normalizingToCvv() {

		/*
		 * Calculating the different matrix components
		 */
		Matrix translateVRP = getMatrixTranslateVertexToOrigin(ReadObjectAndViewingFiles.vrp);
		Matrix rotateVRC = getMatrixRotateVRC();
		Matrix translatePRP = getMatrixTranslateVertexToOrigin(ReadObjectAndViewingFiles.prp);
		Matrix shearPer = getShearMatrix();
		Matrix finalScale = getScaleMatrix();

		//		System.out.println("translate VRP: \n" + translateVRP);
		//		System.out.println();
		//		System.out.println("rotate VRC: \n" + rotateVRC);
		//		System.out.println();
		//		System.out.println("translate PRP: \n" + translatePRP);
		//		System.out.println();
		//		System.out.println("shear: \n" + shearPer);
		//		System.out.println();
		//		System.out.println("final Scale: \n " + finalScale);

		/*
		 * Doing the actual multiplication in order
		 */
		Matrix temp = finalScale.multiply(shearPer);
		temp = temp.multiply(translatePRP);
		temp = temp.multiply(rotateVRC);
		temp = temp.multiply(translateVRP);
		//		System.out.println("nPer: " + temp);

		return temp;
	}

	/**
	 * Calculating the Scaling component for transformation matrix
	 * @return
	 */
	private static Matrix getScaleMatrix() {

		/*
		 * Scale xy 
		 */
		double sx1 = 2 * ReadObjectAndViewingFiles.prp.getX3() / 
				(ReadObjectAndViewingFiles.umax - ReadObjectAndViewingFiles.umin);
		double sy1 = 2 * ReadObjectAndViewingFiles.prp.getX3() / 
				(ReadObjectAndViewingFiles.vmax - ReadObjectAndViewingFiles.vmin);

		double[][] dataScaleXY = {
				{sx1, 0, 0, 0},
				{0, sy1, 0, 0},
				{0, 0, 1, 0},
				{0, 0, 0, 1}
		};
		Matrix scaleXY = new Matrix(dataScaleXY);

		/*
		 * Scale z
		 */
		double sx2 = -1 / 
				(-1 * ReadObjectAndViewingFiles.prp.getX3() + ReadObjectAndViewingFiles.backClip);
		double sy2 = -1 / 
				(-1 * ReadObjectAndViewingFiles.prp.getX3() + ReadObjectAndViewingFiles.backClip);
		double sz2 = -1 / 
				(-1 * ReadObjectAndViewingFiles.prp.getX3() + ReadObjectAndViewingFiles.backClip);

		double[][] dataScaleZ = {
				{sx2, 0, 0, 0},
				{0, sy2, 0, 0},
				{0, 0, sz2, 0},
				{0,0,0,1}
		};
		Matrix scaleZ = new Matrix(dataScaleZ);


		//		System.out.println("scaleXY : \n" + scaleXY);
		//		System.out.println("scaleZ: \n" + scaleZ);
		return scaleZ.multiply(scaleXY);
	}

	/**
	 * Calculating the Shearing component for transformation matrix
	 * @return
	 */
	private static Matrix getShearMatrix() {
		double shzx = (ReadObjectAndViewingFiles.prp.getX1() - 
				((ReadObjectAndViewingFiles.umin + ReadObjectAndViewingFiles.umax) / 2)) / 
				(-1 * ReadObjectAndViewingFiles.prp.getX3());

		double shzy = (ReadObjectAndViewingFiles.prp.getX2() - 
				((ReadObjectAndViewingFiles.vmin + ReadObjectAndViewingFiles.vmax) / 2)) / 
				(-1 * ReadObjectAndViewingFiles.prp.getX3());

		double[][] data = {
				{1, 0, shzx, 0},
				{0, 1, shzy, 0},
				{0, 0, 1, 0},
				{0, 0, 0, 1}
		};

		return new Matrix(data);
	}


	/**
	 * Calculating the Rotating about VRC component for transformation matrix
	 * @return
	 */
	private static Matrix getMatrixRotateVRC() {

		Vector n = new Vector( 
				ReadObjectAndViewingFiles.vpn.getX1(),
				ReadObjectAndViewingFiles.vpn.getX2(),
				ReadObjectAndViewingFiles.vpn.getX3());
		n.normalize();

		Vector u = ReadObjectAndViewingFiles.vup.crossProduct(n);

		u.normalize();

		Vector v = n.crossProduct(u);

		double[][] data = {
				{u.getX1(), u.getX2(), u.getX3(), 0},
				{v.getX1(), v.getX2(), v.getX3(), 0},
				{n.getX1(), n.getX2(), n.getX3(), 0},
				{0,0,0,1}};

		return new Matrix(data);
	}

	/**
	 * General method for translating a vertex to origin
	 * @param x
	 * @return
	 */
	private static Matrix getMatrixTranslateVertexToOrigin(Vertex x) {
		double[][] data = {
				{1,0,0, -1 * x.getX1()},
				{0,1,0, -1 * x.getX2()},
				{0,0,1, -1 * x.getX3()},
				{0,0,0,1}};
		return new Matrix(data);
	}

	/**
	   =============================================================
	   This method is called when the window is resized.
	   =============================================================

	   @param drawable The GLCanvas that will be drawn to

	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{  
		// System.out.println("In reshape");
	}

	/**
	   =============================================================
	   Called by the drawable when the display mode or the display 
	   device associated with the GLDrawable has changed.
	   =============================================================

	   @param drawable The GLCanvas that will be drawn to
	   @param modeChanged  not implemented
	   @param deviceChanged  not implemented

	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged){}

	public void keyReleased(KeyEvent ke) {
	}
	public void keyPressed(KeyEvent ke) {
	}
	public void keyTyped(KeyEvent ke) {
		char ch = ke.getKeyChar();

		Mode[] values = Mode.values();
		for(Mode value : values)
		{
			if(value.getKey() == ch)
			{
				modeChanged = true;
				mode = value;
			}
		}
	} // end keyTyped

	public void mouseReleased(MouseEvent me) { }
	public void mouseEntered(MouseEvent me) { }
	public void mouseExited(MouseEvent me) { }
	public void mouseClicked(MouseEvent me) { 

		// to get the coordinates of the event 
		int x, y;
		// to store which button was clicked, left, right or middle
		int button;

		x = me.getX();
		y = me.getY();

		System.out.println("x = " + x + " y = " + y);
		button = me.getButton();

		// example code for how to check which button was clicked
		if (button == MouseEvent.BUTTON1)
		{
			System.out.println("LEFT click");
		}
		else
			if (button == MouseEvent.BUTTON2)
			{
				System.out.println("MIDDLE click");
			}
			else
				if (button == MouseEvent.BUTTON3)
				{
					System.out.println("RIGHT click");
				}
		Dimension2D d = canvas.getSize();

		System.out.println("The view port is now you active screen");
	}

	public void mousePressed(MouseEvent me) { }

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

} // end class
