package unsw.graphics.world;

import java.util.ArrayList;

import unsw.graphics.Vector3;

public class Tester {

	public static void main(String[] args) {
		Terrain t = new Terrain(5,5,new Vector3(1f,2f,3f));
		ArrayList<ArrayList<Integer>> x = t.getIndices();
		System.out.println("X");
		for(int i = 0; i < x.size();i++) {
			System.out.print(x.get(i).get(0) + " ");
			System.out.print(x.get(i).get(1) + " ");
			System.out.println(x.get(i).get(2));
		}
	}

}
