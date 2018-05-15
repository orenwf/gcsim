package gcsim;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GCSim {
	
	public static final String logName = "edu.gcsim."+Instant.now()+".log";
	private static final Logger logger = Logger.getLogger(logName);
	
	private static void log(String msg) {
		logger.log(Level.CONFIG, " - "+msg);
	}
	
	private static ArrayList<Integer> getSizes(Scanner in, Integer z) {
		ArrayList<Integer> sample = new ArrayList<>();
		for (Integer h = 0, j = 100; h < 3; h++) {
			System.out.println("["+z+"] Set percentage of the heap for generation "+h+": "+j+" remaining: ");
			int g = in.nextInt();
			sample.add(g);
			j-=g;
			if (h == 2 && j > 0) sample.set(h, sample.get(h)+j);
			System.out.println("Gen"+h+" = "+g+".");
		}
		return sample;
	}
	
	public static void main(String ... args) 
			throws IOException, InvalidObjectException, InterruptedException, ExecutionException {
		
		Integer samplDists;
		Integer simulations;
		Integer objects;
		List<ArrayList<Integer>> sizes = new LinkedList<>();
		
		final Scanner in = new Scanner(System.in);
		PrintWriter pw = new PrintWriter(logName);
		
		if (args.length < 6) {
			System.out.println("How many sampling distributions (heap generation configurations) would you like to test? ");
			samplDists = in.nextInt();
			System.out.println("Please enter the number of simulations to run per sampling distribution: ");
			simulations = in.nextInt();
			System.out.println("Please enter the number of objects to be allocated per simulation: ");
			objects = in.nextInt();
			for (int s = 0; s < samplDists; s++) sizes.add(getSizes(in, s+1));
		} else {
			samplDists = new Integer(Integer.parseInt(args[0]));
			simulations = new Integer(Integer.parseInt(args[1]));
			objects = new Integer(Integer.parseInt(args[2]));
			if (args.length % 3 != 0) { 
				System.out.println("You've entered an invalid number of heap configuration arguments based on "+samplDists+" sample distributions.");
				for (int s = 0; s < samplDists; s++) sizes.add(getSizes(in, s+1));
			}
			else {
				for ( int s = 3; s < samplDists; s++) {
					ArrayList<Integer> sample = new ArrayList<>();
					for ( int  i = 0; i < 3; i++) sample.add(new Integer(Integer.parseInt(args[s+i])));
					if ( sample.stream().reduce(Integer::sum).get().compareTo(100) == 0) sizes.add(sample);
					else {
						System.out.println("You've entered an invalid heap configuration for some sample distribution.");
						for (int sampl = 0; sampl < samplDists; sampl++) sizes.add(getSizes(in, sampl+1));
					}
				}
			}
		}

		in.close();
		
		log("Simulating "+objects+" objects per simulation.");
		Simulator myRandomVarGenerator = Simulator.init(objects);
		HashMap<String, Queue<Long>> randomVars = myRandomVarGenerator.generate();
		log("Running "+simulations+"simulations per sampling distribution");		

		ExecutorService executor = Executors.newFixedThreadPool(samplDists);

		randomVars.keySet().stream().forEach(x -> {
			pw.println(x.toString());
			randomVars.get(x).stream().forEach(y -> pw.println(y));
			pw.println();
		});
		
		List<SampleDistribution> sd = new LinkedList<>();
		List<Future<HashMap<String, Double>>> experimentStats = new LinkedList<>();
		
		for (int h = 0; h < samplDists; h++) { 

			 sd.add(new SampleDistribution(simulations, sizes.get(h), randomVars));
			 experimentStats.add(executor.submit(sd.get(h)));
		}
		
		pw.println("Objects: "+objects.toString());
		System.out.println("Objects: "+objects.toString());
		pw.println("Simulations: "+simulations.toString());
		System.out.println("Simulations: "+simulations.toString());
		
		for ( int i = 0; i < samplDists; i++) {
			HashMap<String, Double> hm = experimentStats.get(i).get();
			pw.println("["+hm.get("Generation 0").toString()+", "+hm.get("Generation 1").toString()+", "+hm.get("Generation 2").toString()+"]");
			System.out.println("["+hm.get("Generation 0").toString()+", "+hm.get("Generation 1").toString()+", "+hm.get("Generation 2").toString()+"]");
			hm.keySet().stream().sorted().forEach(x -> pw.println(x.toString()+": "+hm.get(x).toString()));
			hm.keySet().stream().sorted().forEach(x -> System.out.println(x.toString()+": "+hm.get(x).toString()));
		}
		
		pw.close();
	}
}