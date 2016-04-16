package src.models;

import java.util.Arrays;

/**
 * Class for a 4x4 matrix
 * @author AnhVuNguyen
 *
 */
public class Matrix {
	private final double[][] data;

	/**
	 * Empty constructor
	 */
	public Matrix()
	{
		this.data = new double[4][4];
	}

	/**
	 * Constructor with initalization to array value
	 * @param data
	 */
	public Matrix(double[][] data)
	{
		this.data = data;
	}
	
	/**
	 * Get value at i row, j column
	 * @param i
	 * @param j
	 * @return
	 */
	public double getValue(int i, int j)
	{
		if(i < 4 && i >= 0 && j < 4 && j >= 0)
		{
			return data[i][j];
		}
		else
		{
			return (Double) null;
		}
	}

	/**
	 * Set value at i row, j column
	 * @param i
	 * @param j
	 * @param value
	 */
	public void setValue(int i, int j, int value)
	{

		if(i < 4 && i >= 0 && j < 4 && j >= 0)
		{
			data[i][j] = value;
		}
		else
		{
			System.out.println("Invalid indices, nothing is changed");
		}
	}

	/**
	 * Method to get a specific row and return that as a double array
	 * @param i
	 * @return
	 */
	public double[] getRow(int i)
	{
		if(i < 4 && i >= 0)
		{
			return data[i];
		}
		else
		{
			System.out.println("Invalid row index");
			return new double[4];
		}
	}

	/**
	 * Method to get a specific column and returns it as a double array
	 * @param j
	 * @return
	 */
	public double[] getColumn(int j)
	{
		double[] col = new double[4];
		if(j < 4 && j >= 0)
		{
			for(int i = 0 ; i < 4 ; i++)
			{
				col[i] = data[i][j];
			}
		}
		return col;
	}
	 
	/**
	 * Method to multiple 2 4x4 matrices together.
	 * @param b
	 * @return
	 */
	public Matrix multiply(Matrix b)
	{
		Matrix res = new Matrix();
		
		for(int j = 0; j < 4; j++)
		{
			double[] colB = b.getColumn(j);
			for(int i = 0; i < 4; i++)
			{
				double[] rowA = this.getRow(i);
				double value = rowA[0] * colB[0] + rowA[1] * colB[1] + rowA[2] * colB[2] + rowA[3] * colB[3];
				res.data[i][j] = value;
			}
		}
		
		return res;
	}

	public Vertex multiply(Vertex v)
	{
		double[] vertexValues = new double[4];
		
		for(int i = 0 ; i < 4; i++)
		{
			double[] rowA = this.getRow(i);
			vertexValues[i] = rowA[0] * v.getX1() + rowA[1] * v.getX2() + rowA[2] * v.getX3() + rowA[3] * v.getX4();
		}
		
		return new Vertex(vertexValues[0], vertexValues[1], vertexValues[2], vertexValues[3]);	
	}
	
	@Override
	public String toString() {
		return "Matrix [data=\n" + Arrays.toString(data[0]) + "\n" + Arrays.toString(data[1]) + "\n" + Arrays.toString(data[2]) + "\n" + Arrays.toString(data[3]) + "]";
	}

	public static void main(String[] args)
	{
		double[][] data1 = {{1,5,1,5},{2,6,2,6},{3,7,3,7},{8,4,8,4}};
		double[][] data2 = {{5,4,2,3},{1,56,9,8},{7,52,12,0},{0,2,2,1}};
		Matrix one = new Matrix(data1);
		Matrix two = new Matrix(data2);
//		double[] row1 = one.getRow(0);
//		for(double d : row1) System.out.println(d);
		Matrix test = one.multiply(two);
		System.out.println(test);
		Vertex v = new Vertex(1,2,3,4);
		System.out.println(test.multiply(v));
		
	}
}
