package gcsim;

import java.time.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GCSim {
	
	public static void main(String ... args) {
		
		Scanner in = new Scanner(System.in);
		ArrayList<Integer> p = new ArrayList<>();
		for (int i = 0, t = 100; t > 0; i++) {
			System.out.println("Please enter a whole number percentage of the heap for generation "+i+": ");
			int e = in.nextInt();
			if (t<e) e = t;
			t -= e;
			p.add(e);
		}
		for (int i = 0; i < p.size(); i++) {
			System.out.println("Please enter a whole number percentage of remaining run time for ");
		}
	}
}