package test;

import static org.junit.Assert.*;

import org.junit.Test;

import src.models.Vector;

public class VectorTest {

	@Test
	public void testGetX1() {
		Vector v = new Vector(4,34,120);
		assertTrue( v.getX1() == 4);
	}

	@Test
	public void testSetX1() {
		Vector v = new Vector(4,34,120);
		v.setX1(200);
		assertTrue( v.getX1() == 200);
	}

	@Test
	public void testGetX2() {
		Vector v = new Vector(4,34,120);
		assertTrue( v.getX2() == 34);
	}

	@Test
	public void testSetX2() {
		Vector v = new Vector(4,34,120);
		v.setX2(200);
		assertTrue( v.getX2() == 200);
	}

	@Test
	public void testGetX3() {
		Vector v = new Vector(4,34,120);
		assertTrue( v.getX3() == 120);
	}

	@Test
	public void testSetX3() {
		Vector v = new Vector(4,34,120);
		v.setX3(200);
		assertTrue( v.getX3() == 200 );
	}

	@Test
	public void testCrossProduct() {
		Vector v = new Vector(4,34,120);
		Vector v2 = new Vector(3,17,22);
		
		Vector res = v.crossProduct(v2);
		assertTrue(res.getX1() == -1292);
		assertTrue(res.getX2() == 272);
		assertTrue(res.getX3() == -34);
	}

	@Test
	public void testDotProduct() {
		Vector v = new Vector(4,34,120);
		Vector v2 = new Vector(3,17,22);
		
		double res = v.dotProduct(v2);
		assertTrue(res == 3230);
	}

	@Test
	public void testMagnitude() {
		Vector v = new Vector(4,34,120);
		double mag = v.magnitude();
		assertTrue(mag == Math.sqrt(4 * 4 + 34 * 34 + 120 * 120));
	}

	@Test
	public void testNormalize() {
		Vector v = new Vector(4,34,120);
		double mag = v.magnitude();
		v.normalize();
		
		assertTrue(v.getX1() == (4 / mag));
		assertTrue(v.getX2() == (34 / mag));
		assertTrue(v.getX3() == (120 / mag));
	}

}
