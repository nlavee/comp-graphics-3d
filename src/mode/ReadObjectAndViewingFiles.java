package src.mode;

/*
   Code to read an object file and a viewing parameter file

   There is also code provided to print an object's information
   and the viewing parameter information to the console.

   For use in program 2, CS325 Computer Graphics
   Spring 2016
   Skidmore College
   Instructor: Michael Eckmann

   example usage:
     java ReadObjectAndViewingFiles house.object view1.view

     OR in Eclipse:
      enter the object file name followed by the view file name separated 
      by a space in the 
      Run, Run Configurations, Arguments tab, Program arguments text area

   Revised on 04/04/2016
      - to have polygon information (vertex indices, color) stored in a
        Polygon class
      - put all the data variables that need to be used in other classes
        as public static global variables
      - allocated space for the Vertex array and Polygon array only after
        we read the size from the object file.

 */
/*
 * file formats for the object and view files modelled after
 * that used in csc313 - Computer Graphics, Lehigh University
 * taught by G. Drew Kessler
 */
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.*;

import src.models.Matrix;
import src.models.Polygon;
import src.models.Vector;
import src.models.Vertex;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

public class ReadObjectAndViewingFiles 
{
	

	public static Vertex v[];
	public static Polygon polygon[];

	/* Viewing parameters */
	public static Vertex vrp;
	public static Vector vpn;
	public static Vector vup;
	public static Vertex prp;
	public static double umin=0, umax=0, vmin=0, vmax=0;
	public static double frontClip=0, backClip=0;

	/* Save the original parameter, just so that we can revert back easily, space-consuming though */
	public static Vertex vrpTemp;
	public static Vector vpnTemp;
	public static Vector vupTemp;
	public static Vertex prpTemp;
	public static double uminTemp=0, umaxTemp=0, vminTemp=0, vmaxTemp=0;
	public static double frontClipTemp=0, backClipTemp=0;
	
	private static DrawAndHandleInput dahi;
	public static Frame testFrame;


	public static void main(String args[]) {
		printInstruction();
		System.out.println("(loading)");
		boolean io = false;

		if(!io) {
			// NOTE: this program as written expects the 2 command line arguments
			// the first is the object file name 
			// the second is the viewing paramater file name
			// in Eclipse, enter these separated by a space in the 
			// Run..., Java Application name, Arguments tab, Program arguments text area

			/* this is Java code to read in an object file and
		 a viewing parameter file */

			BufferedReader objFileBR;
			String line, tempstr;
			StringTokenizer st;

			String objfName = args[0]; // get the object file name from first command line parameter

			int numVs = 0, numPolys = 0;

			try {
				/**
				 * Reading the house object
				 */
				objFileBR = new BufferedReader(new FileReader(objfName));

				/**
				 * Getting an idea of how many vertices there are
				 */
				line = objFileBR.readLine(); // should be the VERTICES line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("VERTICES")) {
					tempstr = st.nextToken();
					numVs = Integer.parseInt(tempstr);
					v = new Vertex[numVs];
				} else {
					numVs = 0;
					System.out.println("Expecting VERTICES line in file "
							+ objfName);
					System.exit(1);
				}

				/**
				 * Getting an idea of how many polygons there are
				 */
				line = objFileBR.readLine(); // should be the POLYGONS line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("POLYGONS")) {
					tempstr = st.nextToken();
					numPolys = Integer.parseInt(tempstr);
					polygon = new Polygon[numPolys];
				} else {
					System.out.println("Expecting POLYGONS line in file "
							+ objfName);
					System.exit(1);
				}

				/**
				 * Go through vertices list
				 */
				line = objFileBR.readLine(); // should be the VERTEX LIST line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("VERTEX")) {
					tempstr = st.nextToken();
					if (!tempstr.equals("LIST")) {
						System.out.println("Expecting VERTEX LIST line in file "
								+ objfName);
						System.exit(1);
					}
				} else {
					System.out.println("Expecting VERTEX LIST line in file "
							+ objfName);
					System.exit(1);
				}
				// if we get here we successfully processed the VERTEX LIST line

				// reads each of the vertex coordinates and creates a Vertex object for each one 
				for (int i = 0; i < numVs; i++) {
					line = objFileBR.readLine();
					st = new StringTokenizer(line, " ");
					double x1=0, y1=0, z1=0;				
					tempstr = st.nextToken();
					x1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					y1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					z1 = Double.parseDouble(tempstr);
					v[i] = new Vertex(x1,y1,z1,1.0);
				}

				/**
				 * Go through polygon list
				 */
				line = objFileBR.readLine(); // should be the POLYGON LIST line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("POLYGON")) {
					tempstr = st.nextToken();
					if (!tempstr.equals("LIST")) {
						System.out.println("Expecting POLYGON LIST line in file "
								+ objfName);
						System.exit(1);
					}
				} else {
					System.out.println("Expecting POLYGON LIST line in file "
							+ objfName);
					System.exit(1);
				}
				// if we get here we successfully processed the POLYGON LIST line

				for (int i = 0; i < numPolys; i++) {
					line = objFileBR.readLine();
					st = new StringTokenizer(line, " ");
					st.nextToken(); // ignore the string COUNT 
					tempstr = st.nextToken(); // this is the value of count (number of vertices for this poly)
					int numVsForThisPoly = Integer.parseInt(tempstr);
					polygon[i] = new Polygon(numVsForThisPoly);
					st.nextToken(); // ignore the string VERTICES 

					//example line: COUNT 5 VERTICES 5 4 3 2 1 COLOR .4 .2 .4

					for (int j = 1; j <=numVsForThisPoly; j++) {
						tempstr = st.nextToken();
						polygon[i].addVIndex(Integer.parseInt(tempstr) - 1);
					}

					st.nextToken(); // ignore the string COLOR

					tempstr = st.nextToken();
					polygon[i].setRed(Double.parseDouble(tempstr));
					tempstr = st.nextToken();
					polygon[i].setGreen(Double.parseDouble(tempstr));
					tempstr = st.nextToken();
					polygon[i].setBlue(Double.parseDouble(tempstr));

				}

				objFileBR.close();

			} catch (FileNotFoundException fnfe) {
				System.out.println("File not found");
			} catch (IOException ioe) {
				System.out.println("couldn't read from file");
			}

			System.out.print("...");
			
//			for(Vertex vertex : v)
//			{
//				System.out.println(vertex);
//			}
//			
//			for (int i = 0; i < numPolys; i++) {
//				System.out.println("Polygon number " + i + ":");
//				for (int j = 0; j < polygon[i].getNumVs(); j++)
//					System.out.println(" " + v[polygon[i].vertexIndices[j]].toString());
//				System.out.println();
//				System.out.println(" with color: " + polygon[i].getColorInfo() + "\n");
//			}

			// ================================================================
			// ------READ VIEWING PARAMETER FILE  
			// ================================================================

			String viewfName = args[1]; // second command line arg
			BufferedReader viewFileBR;

			try {
				viewFileBR = new BufferedReader(new FileReader(viewfName));

				line = viewFileBR.readLine(); // should be the VRP line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("VRP")) {
					double x1=0, y1=0, z1=0;				
					tempstr = st.nextToken();
					x1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					y1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					z1 = Double.parseDouble(tempstr);
					vrp = new Vertex(x1,y1,z1,1.0);
				} else {
					System.out.println("Expecting VRP line in file "
							+ viewfName);
					System.exit(1);
				}

				line = viewFileBR.readLine(); // should be the VPN line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("VPN")) {
					double x1=0, y1=0, z1=0;				
					tempstr = st.nextToken();
					x1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					y1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					z1 = Double.parseDouble(tempstr);
					vpn = new Vector(x1,y1,z1);
				} else {
					System.out.println("Expecting VPN line in file "
							+ viewfName);
					System.exit(1);
				}

				line = viewFileBR.readLine(); // should be the VUP line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("VUP")) {
					double x1=0, y1=0, z1=0;				
					tempstr = st.nextToken();
					x1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					y1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					z1 = Double.parseDouble(tempstr);
					vup = new Vector(x1,y1,z1);
				} else {
					System.out.println("Expecting VUP line in file "
							+ viewfName);
					System.exit(1);
				}

				line = viewFileBR.readLine(); // should be the PRP line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("PRP")) {
					double x1=0, y1=0, z1=0;				
					tempstr = st.nextToken();
					x1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					y1 = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					z1 = Double.parseDouble(tempstr);
					prp = new Vertex(x1,y1,z1,1.0);
				} else {
					System.out.println("Expecting PRP line in file "
							+ viewfName);
					System.exit(1);
				}


				line = viewFileBR.readLine(); // should be the WINDOW line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("WINDOW")) {
					tempstr = st.nextToken();
					umin = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					umax = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					vmin = Double.parseDouble(tempstr);
					tempstr = st.nextToken();
					vmax = Double.parseDouble(tempstr);

				} else {
					System.out.println("Expecting WINDOW line in file "
							+ viewfName);
					System.exit(1);
				}

				line = viewFileBR.readLine(); // should be the FRONT line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("FRONT")) {
					tempstr = st.nextToken();
					frontClip = Double.parseDouble(tempstr);

				} else {
					System.out.println("Expecting FRONT line in file "
							+ viewfName);
					System.exit(1);
				}
				line = viewFileBR.readLine(); // should be the BACK line
				st = new StringTokenizer(line, " ");
				tempstr = st.nextToken();
				if (tempstr.equals("BACK")) {
					tempstr = st.nextToken();
					backClip = Double.parseDouble(tempstr);

				} else {
					System.out.println("Expecting BACK line in file "
							+ viewfName);
					System.exit(1);
				}

				viewFileBR.close();
				System.out.print("...");
			} catch (FileNotFoundException fnfe) {
				System.out.println("File not found");
			} catch (IOException ioe) {
				System.out.println("couldn't read from file");
			}

//			System.out.println("VRP = " + vrp);
//			System.out.println("PRP = " + prp);
//			System.out.println("VUP = " + vup);
//			System.out.println("VPN = " + vpn);
//			
//			System.out.print("WINDOW =");
//			System.out.println(" " + umin + " " + umax + " " + vmin + " " + vmax);
//
//			System.out.print("FRONT =");
//			System.out.println(" " + frontClip);
//
//			System.out.print("BACK =");
//			System.out.println(" " + backClip);

			io = true;
		}

		setTempParam();
		System.out.print("...");
		System.out.println();
		/**
		 * Done with IO, move on to drawing.
		 */
		mainDrawing();
	} // end of main		

	private static void printInstruction() {
		System.out.println("Welcome!");
		System.out.println("We have a combination of keys that you can try.");
		System.out.println("Here is a list of key and its function:");
		System.out.println("\t 1.  . (period) -- Reset Image Back To Original Display");
		System.out.println("\t 2.  j --- Move VRP in Positive u");
		System.out.println("\t 3.  k --- Move VRP in Positive v");
		System.out.println("\t 4.  l --- Move VRP in Positive n");
		System.out.println("\t 5.  n --- Move VRP in Negative u");
		System.out.println("\t 6.  m --- Move VRP in Negative u");
		System.out.println("\t 7.  , --- Move VRP in Negative u");
		System.out.println("\t 8.  a --- Rotate Clockwise Around x-axis");
		System.out.println("\t 9.  s --- Rotate Clockwise Around y-axis");
		System.out.println("\t 10. d --- Rotate Clockwise Around z-axis");
		System.out.println("\t 11. z --- Rotate Counterclockwise Around x-axis");
		System.out.println("\t 12. x --- Rotate Counterclockwise Around y-axis");
		System.out.println("\t 13. c --- Rotate Counterclockwise Around z-axis");
		System.out.println("\t 14. + (Shift & = ) --- Zoom in ");
		System.out.println("\t 15. - ---  Zoom out");
		System.out.println("\t 16. q ---  Exit Program");
		System.out.println("(Please click on the generated window to make it your active window)");
	}

	private static void setTempParam() {
		vrpTemp = new Vertex(vrp.getX1(), vrp.getX2(), vrp.getX3(), vrp.getX4());
		vpnTemp = new Vector(vpn.getX1(), vpn.getX2(), vpn.getX3());
		vupTemp = new Vector(vup.getX1(), vup.getX2(), vup.getX3());
		prpTemp = new Vertex(prp.getX1(), prp.getX2(), prp.getX3(), prp.getX4());
		uminTemp = new Double(umin);
		umaxTemp = new Double(umax); 
		vminTemp = new Double(vmin);
		vmaxTemp = new Double(vmax);
		frontClipTemp = new Double(frontClip);
		backClipTemp = new Double(backClip);
//		System.out.println("Finished allocating temp");
	}

	private static void mainDrawing() {
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

} // end of class

