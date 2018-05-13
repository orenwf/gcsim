package gcsim;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GCSim {
	
	private static final String logName = "edu.gcsim."+Instant.now()+".log";
	private static final Logger logger = Logger.getLogger(logName);
	
	public static void log(String msg) {
		logger.log(Level.INFO, " - "+msg);
	}
		
	public static void main(String ... args) 
			throws IOException, InvalidObjectException, InterruptedException {
		ArrayList<Integer> sizes = new ArrayList<>();
		Integer objects;
		Integer trials;
		if (args.length < 5) {
			final Scanner in = new Scanner(System.in);
			for (Integer i = 0, j = 100; i < 3; i++) {
				System.out.println("Set percentage of the heap for generation "+i+": "+j+" remaining: ");
				int g = in.nextInt();
				sizes.add(g);
				j-=g;
				if (i == 2 && j > 0) sizes.set(i, sizes.get(i)+j);
				log("Gen"+i+" = "+g+".");
			}
			System.out.println("Please enter the number of objects to be simulated.");
			objects = in.nextInt();
			log("Simulating "+objects+" objects.");
			System.out.println("Please enter the numeber of trials to run: ");
			trials = in.nextInt();
			log("Running "+trials+"simulations");		
			in.close();
		} else {
			sizes.add(new Integer(Integer.parseInt(args[0])));
			sizes.add(new Integer(Integer.parseInt(args[1])));
			sizes.add(new Integer(Integer.parseInt(args[2])));
			objects = new Integer(Integer.parseInt(args[3]));
			trials = new Integer(Integer.parseInt(args[4]));
		}
		
		List<HashMap<String, Double>> statistics = new LinkedList<>();
		
		for (int i = 0; i < trials; i++) {
			Simulator myRandomVarGenerator = Simulator.init(objects);
			VirtualMachine vm = VirtualMachine.init(sizes, myRandomVarGenerator.generate());
			List<Duration> pauseTimes = vm.start();
			Duration totalPause = Duration.ZERO;
			for (Duration d : pauseTimes) {
				System.out.println("Pause "+pauseTimes.indexOf(d)+": "+d.toMillis()+".");
				totalPause = totalPause.plus(d);
			}
			System.out.println("Total Pause Time: "+totalPause.toMillis()+".");
			Double m = totalPause.toMillis()/(pauseTimes.size()*1.0d);
			System.out.println("Average Pause Time: "+m);
			Double v = pauseTimes.stream().map(rvs -> Math.pow(((double)rvs.toMillis() - m),2))
					.reduce(Double::sum).map(sum -> Math.sqrt(sum/pauseTimes.size())).get();
			System.out.println("Variance of Pause Times: "+v+".");
			HashMap<String, Double> hm = new HashMap<>();
			hm.put("count", pauseTimes.size()*1.0);
			hm.put("total", totalPause.toMillis()*1.0);
			hm.put("mean", m);
			hm.put("variance", v);
			statistics.add(hm);
		}
		
		Double mean = statistics.stream().map(stats -> stats.get("total"))
				.reduce(Double::sum).map(sum -> sum/statistics.size()).get();
		
		Double variance = statistics.stream().map(stats -> Math.pow(stats.get("total") - mean, 2))
				.reduce(Double::sum).map(sum -> Math.sqrt(sum/statistics.size())).get();
		Double ci;
	}
}