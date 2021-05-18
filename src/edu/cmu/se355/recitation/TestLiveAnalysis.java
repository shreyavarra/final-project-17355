package edu.cmu.se355.recitation;

public class TestLiveAnalysis {
	public void test1() {
		int a, b, c;
		a = 2;
		b = a;
		b = 3;
		a = 4;
		c = b;
	}

	public void test2() {
		int x, y, z;
		y = 5;
		z = 3;
		z = y + z;
		y = 2;
	}

	public void test3() {
		int a, b, c;
		a = 0;
		c = 3;
		b = a + 1;
		c = c + b;
		a = b * 2;
		if (a < 9) {
			b = a + 1;
		}
	}

	public void main(String[] args) {
		
	}
}
