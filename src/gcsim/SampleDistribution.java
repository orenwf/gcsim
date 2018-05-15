package gcsim;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SampleDistribution implements Callable<HashMap<String, Double>> {
	
	private final String logName = GCSim.logName+"-"+this.toString();
	private final Logger logger = Logger.getLogger(logName);
	
	public void log(String msg) {
		logger.log(Level.CONFIG, this.toString()+" - "+msg);
	}
	
	private Integer simulations;
	private HashMap<String, Queue<Long>> randomVars;
	private ArrayList<Integer> sizes;

	public SampleDistribution(Integer _simulations, ArrayList<Integer> _sizes, HashMap<String, Queue<Long>> _randomVars) {
		simulations = _simulations;
		randomVars = _randomVars;
		sizes = _sizes;
	}
	
	@Override
	public HashMap<String, Double> call() throws Exception {
		
		List<HashMap<String, Double>> sampleStats = new LinkedList<>();

		for (int i = 0; i < simulations; i++) {
			HashMap<String, Queue<Long>> mhm = new HashMap<>();
			Queue<Long> sizesQ = randomVars.get("sizes").stream().collect(Collectors.toCollection(LinkedList::new));
			mhm.put("sizes", sizesQ);
			Queue<Long> arrivalsQ = randomVars.get("arrivals").stream().collect(Collectors.toCollection(LinkedList::new));
			mhm.put("arrivals", arrivalsQ);
			Queue<Long> lifetimesQ = randomVars.get("lifetimes").stream().collect(Collectors.toCollection(LinkedList::new));
			mhm.put("lifetimes", lifetimesQ);
			VirtualMachine vm = VirtualMachine.init(sizes, mhm, this);
			List<Duration> pauseTimes = vm.start();
			Duration totalPause = Duration.ZERO;
			for (Duration d : pauseTimes) {
				System.out.println("["+this.toString()+"] Pause "+pauseTimes.indexOf(d)+": "+d.toMillis()+".");
				totalPause = totalPause.plus(d);
			}
			System.out.println("["+this.toString()+"] Total Pause Time: "+totalPause.toMillis()+".");
			Double mean = totalPause.toMillis()/(pauseTimes.size()*1.0d);
			System.out.println("["+this.toString()+"] Average Pause Time: "+mean);
			Double variance = pauseTimes.stream().map(rvs -> Math.pow(((double)rvs.toMillis() - mean),2))
					.reduce(Double::sum).map(sum -> Math.sqrt(sum/pauseTimes.size())).orElse(0d);
			System.out.println("["+this.toString()+"] Variance of Pause Times: "+variance+".");
			HashMap<String, Double> simStats = new HashMap<>();
			simStats.put("count", pauseTimes.size()*1.0);		// how many pauses
			simStats.put("total", totalPause.toMillis()*1.0);	// total pause time
			simStats.put("mean", mean);							// mean pause time
			simStats.put("variance", variance);					// variance of pause time
			sampleStats.add(simStats);
		}

		HashMap<String, Double> sampleDist = new HashMap<>();
		
		sampleDist.put("Generation 0", sizes.get(0)*1.0d);
		sampleDist.put("Generation 1", sizes.get(1)*1.0d);
		sampleDist.put("Generation 2", sizes.get(2)*1.0d);
		
		Double meanPauseTotal = sampleStats.stream()	// mean of the total pause times for a sample distribution
				.map(stats -> stats.get("total"))
				.reduce(Double::sum)
				.map(sum -> sum/sampleStats.size()).orElse(0d);
		sampleDist.put("Expected total pause time", meanPauseTotal);

		Double varPauseTotal = sampleStats.stream()		// variance of the total pause times for a sample distribution
				.map(stats -> Math.pow(stats.get("total") - meanPauseTotal, 2))
				.reduce(Double::sum).map(sum -> Math.sqrt(sum/sampleStats.size())).orElse(0d);
		sampleDist.put("Variance total pause time", varPauseTotal);
		
//		Double meanPauseCount =  sampleStats.stream()	// mean of the total number of pauses for a sample distribution
//				.map(stats -> stats.get("count"))
//				.reduce(Double::sum).map(sum -> sum/sampleStats.size()).orElse(0d);
//		sampleDist.put("Mean pause count", meanPauseCount);
//					
//		Double varPauseCount = sampleStats.stream()		// variance of the total number of pauses for a sample dist.
//				.map(stats -> Math.pow(stats.get("count") - meanPauseCount, 2))
//				.reduce(Double::sum).map(sum -> Math.sqrt(sum/sampleStats.size())).orElse(0d);
//		sampleDist.put("Variance pause count", varPauseCount);
		
		Double meanPauseVar = sampleStats.stream()		// mean of the variances of pause times for a sample dist.
				.map(stats -> stats.get("variance"))
				.reduce(Double::sum).map(sum -> sum/sampleStats.size()).orElse(0d);
		sampleDist.put("Expected variance of pause times", meanPauseVar);
		
//		Double ci = 0.0;
//		sampleDist.put("Confidence Interval", ci);
		return sampleDist;
	
	}

}
