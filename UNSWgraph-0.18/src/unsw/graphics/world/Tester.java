package unsw.graphics.world;

import java.lang.reflect.Array;
import java.util.ArrayList;

import unsw.graphics.Vector3;

public class Tester {

	public static void main(String[] args) {
		Terrain t = new Terrain(5,5,new Vector3(1f,2f,3f));
		int[] x = t.getIndices();
		int m = Array.getLength(x);
		for(int i = 0; i < m; i +=3) {
			System.out.print(x[i] + " ");
			System.out.print(x[i+1] + " ");
			System.out.println(x[i+2]);
		}
	}

}
