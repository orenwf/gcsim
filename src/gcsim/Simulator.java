package gcsim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Simulator {
	
	private Random myRandomObject;
	private Integer count;
	
	private Simulator(Random _r) {
		myRandomObject = _r;
	}
	
	public static Simulator init(Integer _count) {
		Simulator s = new Simulator(new Random());
		s.count = _count;
		return s;
	}
	
	public HashMap<String, Queue<Long>> generate() {
		
		HashMap<String, Queue<Long>> randVarTable = new HashMap<>();
				
		Queue<Long> arrivals = new LinkedList<>();
		arrivals.addAll(Stream.generate(myRandomObject::nextLong)
				.map(x -> Math.abs(x)%1000+10)
				.limit(count).sorted()
				.collect(Collectors.toList()));

		Queue<Long> sizes = new LinkedList<>();
		sizes.addAll(Stream.generate(myRandomObject::nextLong)
				.map(x -> Math.abs(x)%1000000+100000)
				.limit(count)
				.collect(Collectors.toList()));

		Queue<Long> lifetimes = new LinkedList<>();
		lifetimes.addAll(Stream.generate(myRandomObject::nextLong)
				.map(x -> Math.abs(x)%10000+500)
				.limit(count)
				.collect(Collectors.toList()));

		randVarTable.put("sizes", sizes);
		randVarTable.put("arrivals", arrivals);
		randVarTable.put("lifetimes", lifetimes);
		return randVarTable;
	}
}

