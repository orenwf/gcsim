package gcsim;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GCSim {
	
	private static final String logName = "edu.gcsim."+Instant.now()+".log";
	
	public static void log(String msg) {
		Logger logger = Logger.getLogger(logName);
		logger.log(Level.INFO, Instant.now().toString()+" - "+msg);
	}
		
	public static void main(String ... args) 
			throws IOException, InvalidObjectException, InterruptedException {
		
		final Scanner in = new Scanner(System.in);
		ArrayList<Integer> sizes = new ArrayList<>();
		for (Integer i = 0, j = 100; i < 3; i++) {
			System.out.println("Set percentage of the heap for generation "+i+"- "+j+" remaining: ");
			int g = in.nextInt();
			sizes.add(g);
			j-=g;
			if (i == 2 && j > 0) sizes.set(i, sizes.get(i)+j);
			log("Gen"+i+" = "+g+".");
		}
		
		Simulator rv = Simulator.init();
		VirtualMachine vm = VirtualMachine.init(sizes, rv.generate());
		vm.start();		
		in.close();
	}
}