package src.models;

/**
 * Class for a vertex
 * @author AnhVuNguyen
 *
 */
public class Vertex {

	public double x1;
	public double x2;
	public double x3;
	public double x4;
	
	/**
	 * Constructor
	 * @param x1
	 * @param x2
	 * @param x3
	 * @param x4
	 */
	public Vertex(double x1, double x2, double x3, double x4) {
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
		this.x4 = x4;
	}

	/**
	 * Constructor without homogeneous coordinate, default to 1
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vertex(double x, double y, double z)
	{
		this.x1 = x;
		this.x2 = y;
		this.x3 = z;
		this.x4 = 1.0;
	}

	/**
	 * Enpty constructor
	 */
	public Vertex() {
		
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getX3() {
		return x3;
	}

	public void setX3(double x3) {
		this.x3 = x3;
	}

	public double getX4() {
		return x4;
	}

	public void setX4(double x4) {
		this.x4 = x4;
	}

	/**
	 * Returns the vector that represent the differences between the class instance and the vertex being passed in as parameter
	 * @param v2
	 * @return
	 */
	public Vector subtractVertices(Vertex v2)
	{
		double y1 = v2.getX1();
		double y2 = v2.getX2();
		double y3 = v2.getX3();
		
		return new Vector(x1 - y1, x2 - y2, x3 - y3);
	}

	@Override
	public String toString() {
		return "Vertex [x1=" + x1 + ", x2=" + x2 + ", x3=" + x3 + ", x4=" + x4
				+ "]";
	}
	
}
