package test;

import static org.junit.Assert.*;

import org.junit.Test;

import src.models.Matrix;
import src.models.Vertex;

public class MatrixTest {

	@Test
	public void testGetValue() {
		double[][] data = {{1,0,0,1},{1,0,1,0},{1,1,1,1},{0,0,0,0}};
		Matrix temp = new Matrix(data);
		assertTrue( temp.getValue(3, 3) == 0);
	}

	@Test
	public void testSetValue() {
		double[][] data = {{1,0,0,1},{1,0,1,0},{1,1,1,1},{0,0,0,0}};
		Matrix temp = new Matrix(data);
		temp.setValue(3, 3, 100);
		assertTrue( temp.getValue(3, 3) == 100);
	}

	@Test
	public void testGetRow() {
		double[][] data = {{1,0,0,1},{1,0,1,0},{1,1,1,1},{0,0,0,0}};
		Matrix temp = new Matrix(data);
		double[] firstRow = temp.getRow(0);
		
		assertTrue( firstRow[0] == 1);
		assertTrue( firstRow[1] == 0);
		assertTrue( firstRow[2] == 0);
		assertTrue( firstRow[3] == 1);
	}

	@Test
	public void testGetColumn() {
		double[][] data = {{1,0,0,1},{1,0,1,0},{1,1,1,1},{0,0,0,0}};
		Matrix temp = new Matrix(data);
		double[] firstCol = temp.getColumn(0);
		
		assertTrue( firstCol[0] == 1);
		assertTrue( firstCol[1] == 1);
		assertTrue( firstCol[2] == 1);
		assertTrue( firstCol[3] == 0);
	}

	@Test
	public void testMultiplyMatrix() {
		double[][] data = {{1,0,0,1},{1,0,1,0},{1,1,1,1},{0,0,0,0}};
		Matrix temp = new Matrix(data);
		
		double[][] data2 = {{10,23,0,11},{44,56,1,9},{0,1,0,1},{0,0,0,0}};
		Matrix temp2 = new Matrix(data2);
		
		Matrix mulRes = temp.multiply(temp2);
		
		assertTrue(mulRes.getValue(0, 0) == 10);
		assertTrue(mulRes.getValue(0, 1) == 23);
		assertTrue(mulRes.getValue(0, 2) == 0);
		assertTrue(mulRes.getValue(0, 3) == 11);
		assertTrue(mulRes.getValue(1, 0) == 10);
		assertTrue(mulRes.getValue(1, 1) == 24);
		assertTrue(mulRes.getValue(1, 2) == 0);
		assertTrue(mulRes.getValue(1, 3) == 12);
		assertTrue(mulRes.getValue(2, 0) == 54);
		assertTrue(mulRes.getValue(2, 1) == 80);
		assertTrue(mulRes.getValue(2, 2) == 1);
		assertTrue(mulRes.getValue(2, 3) == 21);
		assertTrue(mulRes.getValue(3, 0) == 0);
		assertTrue(mulRes.getValue(3, 1) == 0);
		assertTrue(mulRes.getValue(3, 2) == 0);
		assertTrue(mulRes.getValue(3, 3) == 0);
	}

	@Test
	public void testMultiplyVertex() {
		double[][] data = {{1,0,0,1},{1,0,1,0},{1,1,1,1},{0,0,0,0}};
		Matrix temp = new Matrix(data);
		
		Vertex temp2 = new Vertex(6,8,34,199);
		
		Vertex mulRes = temp.multiply(temp2);
		
		assertTrue( mulRes.getX1() == 205 );
		assertTrue( mulRes.getX2() == 40 );
		assertTrue( mulRes.getX3() == 247 );
		assertTrue( mulRes.getX4() == 0 );
	}

}
