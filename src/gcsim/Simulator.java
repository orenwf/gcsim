package gcsim;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Simulator {
	
	private Random myRandomObject;
	private Integer count;
	
	private Simulator(Random _r) {
		myRandomObject = _r;
	}
	
	public static Simulator init(Integer count) {
		Simulator s = new Simulator(new Random());
		return s;
	}
	
	public HashMap<Class<?>, Object> generate() {
		
		HashMap<Class<?>, Object> randVarTable = new HashMap<>();
				
		Queue<Long> arrivals = new LinkedList<>();
		myRandomObject.longs(count, 0, 10000).distinct().boxed().sorted()
								.forEach(x -> arrivals.add(x));
		
		Queue<Integer> sizes = new LinkedList<>();
		myRandomObject.ints(count, 1, 1000).boxed()
								.forEach(x -> sizes.add(x));
		
		Queue<Duration> lifetimes = new LinkedList<>();
		myRandomObject.longs(count, 0, 10000).distinct().boxed()
								.forEach(x -> lifetimes.add(Duration.ofMillis(x)));
		
		randVarTable.put(Integer.class, sizes);
		randVarTable.put(Instant.class, arrivals);
		randVarTable.put(Duration.class, lifetimes);
		return randVarTable;
	}
}

