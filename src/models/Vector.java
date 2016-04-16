package src.models;

/**
 * Class for a three dimensional vector
 * @author AnhVuNguyen
 *
 */
public class Vector {

	private double x1;
	private double x2;
	private double x3;

	public Vector(double x1, double x2, double x3) {
		super();
		this.x1 = x1;
		this.x2 = x2;
		this.x3 = x3;
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


	public Vector crossProduct(Vector secondVector)
	{
		double y1 = secondVector.getX1();
		double y2 = secondVector.getX2();
		double y3 = secondVector.getX3();

		double z1 = x2 * y3 - x3 * y2;
		double z2 = x3 * y1 - x1 * y3;
		double z3 = x1 * y2 - x2 * y1;

		return new Vector(z1, z2, z3);
	}

	public double dotProduct(Vector secondVector)
	{
		double y1 = secondVector.getX1();
		double y2 = secondVector.getX2();
		double y3 = secondVector.getX3();

		return (x1 * y1) + (x2 * y2) + (x3 * y3);
	}

	public double magnitude()
	{
		return Math.sqrt(x1*x1 + x2*x2 + x3*x3);
	}

	public void normalize()
	{
		double magnitude = magnitude();
		if(magnitude > 0)
		{
			this.x1 = this.x1 / magnitude;
			this.x2 = this.x2 / magnitude;
			this.x3 = this.x3 / magnitude;
		}
	}

	@Override
	public String toString() {
		return "Vector [x1=" + x1 + ", x2=" + x2 + ", x3=" + x3 + "]";
	}


}
