package gcsim;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.PrintWriter;

public class GCSim {
	
	private static final String logName = "edu.gcsim."+Instant.now()+".log";
	private static final Logger logger = Logger.getLogger(logName);
	
	public static void log(String msg) {
		logger.log(Level.INFO, " - "+msg);
	}
		
	public static void main(String ... args) 
			throws IOException, InvalidObjectException, InterruptedException {
		
		final Scanner in = new Scanner(System.in);
		ArrayList<Integer> sizes = new ArrayList<>();
		for (Integer i = 0, j = 100; i < 3; i++) {
			System.out.println("Set percentage of the heap for generation "+i+": "+j+" remaining: ");
			int g = in.nextInt();
			sizes.add(g);
			j-=g;
			if (i == 2 && j > 0) sizes.set(i, sizes.get(i)+j);
			log("Gen"+i+" = "+g+".");
		}
		System.out.println("Please enter the number of objects to be simulated.");
		Integer x = in.nextInt();
		log("Simulating "+x+" objects.");

                String filename = "out.txt";
                PrintWriter outputStream = new PrintWriter(filename);
                
 
                for(int i = 0; i < 100; i++){		
			Simulator myRandomVarGenerator = Simulator.init(x);
			VirtualMachine vm = VirtualMachine.init(sizes, myRandomVarGenerator.generate());
			List<Duration> pauseTimes = vm.start();		
			in.close();
			Duration totalPause = Duration.ZERO;
			for (Duration d : pauseTimes) {
				System.out.println("Pause "+pauseTimes.indexOf(d)+": "+d.toMillis()+".");
				totalPause = totalPause.plus(d);
			}
			System.out.println("Total Pause Time: "+totalPause.toMillis()+".");
                        outputStream.println(totalPause.toMillis()+",");
			System.out.println("Average Pause Time: "+m);
			Double v = pauseTimes.stream().map(rvs -> Math.pow(((double)rvs.toMillis() - m),2))
					.reduce(Double::sum).map(sum -> Math.sqrt(sum/pauseTimes.size())).get();
			System.out.println("Variance of Pause Times: "+v+".");
                }

	}
}
