package gcsim;

import java.time.Instant;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

class Simulator {
	
	private Random r;
	private Integer count;
	
	private Simulator(Random _r) {
		r = _r;
	}
	
	public static Simulator init(Integer count) {
		Simulator s = new Simulator(new Random());
		return s;
	}
	
	public HashMap<Class<?>, Object> generate() {
		HashMap<Class<?>, Object> randVarTable = new HashMap<>();
		Instant offset = Instant.now().plusSeconds(30);
		Stack<Instant> arrivals = new Stack<>();
		r.longs(count, 0, 10000).boxed().map(x -> offset.plusMillis(x)).sorted().forEach(x -> arrivals.push(x));
		Stack<Long> sizes = new Stack<>();
		r.longs(count, 0, 1000).boxed().sorted().forEach(x -> sizes.push(x));
		Stack<Long> lifetimes = new Stack<>();
		r.longs(count, 0, 10000).boxed().sorted().forEach(x -> lifetimes.push(x));
		randVarTable.put(Integer.class, sizes);
		randVarTable.put(Instant.class, arrivals);
		randVarTable.put(Long.class, lifetimes);
		return randVarTable;
	}
}

