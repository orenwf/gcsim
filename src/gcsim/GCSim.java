package gcsim;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GCSim {
	
	private static final String logName = "edu.gcsim."+Instant.now()+".log";
	private static final Logger logger = Logger.getLogger(logName);
	
	public static void log(String msg) {
		logger.log(Level.INFO, " - "+msg);
	}
	
	private static ArrayList<Integer> getSizes(Scanner in) {
		ArrayList<Integer> sample = new ArrayList<>();
		for (Integer h = 0, j = 100; h < 3; h++) {
			System.out.println("Set percentage of the heap for generation "+h+": "+j+" remaining: ");
			int g = in.nextInt();
			sample.add(g);
			j-=g;
			if (h == 2 && j > 0) sample.set(h, sample.get(h)+j);
			log("Gen"+h+" = "+g+".");
		}
		return sample;
	}
	
	public static void main(String ... args) 
			throws IOException, InvalidObjectException, InterruptedException {
		
		Integer samplDists;
		Integer simulations;
		Integer objects;
		List<ArrayList<Integer>> sizes = new LinkedList<>();
		
		final Scanner in = new Scanner(System.in);
		
		if (args.length < 6) {
			System.out.println("How many sampling distributions (heap generation configurations) would you like to test? ");
			samplDists = in.nextInt();
			System.out.println("Please enter the number of simulations to run per sampling distribution: ");
			simulations = in.nextInt();
			System.out.println("Please enter the number of objects to be allocated per simulation: ");
			objects = in.nextInt();
			for (int s = 0; s < samplDists; s++) sizes.add(getSizes(in));
		} else {
			samplDists = new Integer(Integer.parseInt(args[0]));
			simulations = new Integer(Integer.parseInt(args[1]));
			objects = new Integer(Integer.parseInt(args[2]));
			if (args.length % 3 != 0) { 
				System.out.println("You've entered an invalid number of heap configuration arguments based on "+samplDists+" sample distributions.");
				for (int s = 0; s < samplDists; s++) sizes.add(getSizes(in));
			}
			else {
				for ( int s = 3; s < samplDists; s++) {
					ArrayList<Integer> sample = new ArrayList<>();
					for ( int  i = 0; i < 3; i++) sample.add(new Integer(Integer.parseInt(args[s+i])));
					if ( sample.stream().reduce(Integer::sum).get().compareTo(100) == 0) sizes.add(sample);
					else {
						System.out.println("You've entered an invalid heap configuration for some sample distribution.");
						for (int sampl = 0; sampl < samplDists; sampl++) sizes.add(getSizes(in));
					}
				}
			}
		}

		in.close();
		
		log("Simulating "+objects+" objects per simulation.");
		Simulator myRandomVarGenerator = Simulator.init(objects);
		HashMap<String, Queue<Long>> randomVars = myRandomVarGenerator.generate();
		log("Running "+simulations+"simulations per sampling distribution");		

		PrintWriter pw = new PrintWriter(logName);
		
		randomVars.keySet().stream().forEach(x -> {
			pw.println(x.toString());
			randomVars.get(x).stream().forEach(y -> pw.println(y));
			pw.println();
		});
		
		
		List<HashMap<String, Double>> experimentStats = new LinkedList<>();
		
		for (int h = 0; h < samplDists; h++) {

			List<HashMap<String, Double>> sampleStats = new LinkedList<>();

			for (int i = 0; i < simulations; i++) {
				VirtualMachine vm = VirtualMachine.init(sizes.get(h), randomVars);
				List<Duration> pauseTimes = vm.start();
				Duration totalPause = Duration.ZERO;
				for (Duration d : pauseTimes) {
					System.out.println("Pause "+pauseTimes.indexOf(d)+": "+d.toMillis()+".");
					totalPause = totalPause.plus(d);
				}
				System.out.println("Total Pause Time: "+totalPause.toMillis()+".");
				Double max = pauseTimes.stream().map(x -> Long.valueOf(x.toMillis()))
						.max(Comparator.comparing(Long::valueOf)).map(x -> x*1.0).orElse(0d);
				Double mean = totalPause.toMillis()/(pauseTimes.size()*1.0d);
				System.out.println("Average Pause Time: "+mean);
				Double variance = pauseTimes.stream().map(rvs -> Math.pow(((double)rvs.toMillis() - mean),2))
						.reduce(Double::sum).map(sum -> Math.sqrt(sum/pauseTimes.size())).orElse(0d);
				System.out.println("Variance of Pause Times: "+variance+".");
				HashMap<String, Double> simStats = new HashMap<>();
				simStats.put("max", max);
				simStats.put("count", pauseTimes.size()*1.0);		// how many pauses
				simStats.put("total", totalPause.toMillis()*1.0);	// total pause time
				simStats.put("mean", mean);							// mean pause time
				simStats.put("variance", variance);						// variance of pause time
				sampleStats.add(simStats);
			}

			HashMap<String, Double> sampleDist = new HashMap<>();
			
			Double meanPauseTotal = sampleStats.stream().map(stats -> stats.get("total"))
					.reduce(Double::sum).map(sum -> sum/sampleStats.size()).orElse(0d);
			sampleDist.put("meanTotal", meanPauseTotal);

			Double varPauseTotal = sampleStats.stream()
					.map(stats -> Math.pow(stats.get("total") - meanPauseTotal, 2))
					.reduce(Double::sum).map(sum -> Math.sqrt(sum/sampleStats.size())).orElse(0d);
			sampleDist.put("varTotal", varPauseTotal);
			
			Double meanPauseCount =  sampleStats.stream().map(stats -> stats.get("count"))
					.reduce(Double::sum).map(sum -> sum/sampleStats.size()).orElse(0d);
			sampleDist.put("meanCount", meanPauseCount);
						
			Double varPauseCount = sampleStats.stream()
					.map(stats -> Math.pow(stats.get("count") - meanPauseCount, 2))
					.reduce(Double::sum).map(sum -> Math.sqrt(sum/sampleStats.size())).orElse(0d);
			sampleDist.put("varCount", varPauseCount);

			Double meanPauseMax = sampleStats.stream().map(stats -> stats.get("max"))
					.reduce(Double::sum).map(sum -> sum/sampleStats.size()).orElse(0d);
			sampleDist.put("meanMax", meanPauseMax);
			
			Double meanPauseVar = sampleStats.stream().map(stats -> stats.get("variance"))
					.reduce(Double::sum).map(sum -> sum/sampleStats.size()).orElse(0d);
			sampleDist.put("meanVar", meanPauseVar);
			
			Double ci = 0.0;
			sampleDist.put("ci", ci);
			experimentStats.add(sampleDist);
			
		}
		
		for ( int i = 0; i < samplDists; i++) {
			HashMap<String, Double> hm = experimentStats.get(i);
			pw.println(sizes.get(i).toString());
			System.out.println(sizes.get(i).toString());
			hm.keySet().stream().sorted().forEach(x -> pw.println(x.toString()+": "+hm.get(x).toString()));
			hm.keySet().stream().sorted().forEach(x -> System.out.println(x.toString()+": "+hm.get(x).toString()));
		}
		
		pw.close();
	}
}