package gcsim;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;


public class GCSim {
		
	public static void main(String ... args) throws IOException {
		
		Scanner in = new Scanner(System.in);
		PrintWriter VMLog = new PrintWriter("edu.gcsim"+Instant.now().toString()+".log");
		ArrayList<Integer> sizes = new ArrayList<>();
		for (Integer i = 0, j = 100; i < 3; i++) {
			System.out.println("Set percentage of the heap for generation "+i+"- "+j+" remaining: ");
			int g = in.nextInt();
			sizes.add(g);
			j-=g;
			if (i == 2 && j > 0) sizes.set(i, sizes.get(i)+j);
			VMLog.println("Gen"+i+" = "+g+".");
		}
		
		Simulator sim;
		VirtualMachine vm = VirtualMachine.init(sizes, VMLog);
		vm.start();
		
		
		in.close();
		VMLog.close();
	}
}