package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.time.*;
import java.util.HashMap;
import java.util.stream.Collectors;

public class VirtualMachine {

	public static final Integer work = 5;
	public static final Integer markFactor = 1;
	public static final Integer sweepFactor = 2;
	private static final Long memory = 134_217_728L;	// this is 1 GB divided into 8 byte words
	private HashMap<Instant, Reference> stack;			// the thread-or-stack reference pool
	private Heap gen0, gen1, gen2;
	private HashMap<String, Queue<Long>> randVarTable;
	private Stack<Duration> pauseTimes;
	
	private VirtualMachine(List<Long> _sizes, HashMap<String, Queue<Long>> _rv) {
		gen0 = Eden.init(_sizes.get(0));
		gen1 = Survivor.init(_sizes.get(1));
		gen2 = Mature.init(_sizes.get(2));
		stack = new HashMap<Instant, Reference>();
		randVarTable = _rv;
		pauseTimes = new Stack<>();
	}
	
	public static VirtualMachine init(List<Integer> proportions, HashMap<String, Queue<Long>> rv) {
		List<Long> sizes = new LinkedList<>();
		for (Integer i : proportions) {
			Long m = new Long(memory);
			Long x = (long) (i*m/100);
			sizes.add(x);
		}
		VirtualMachine vm = new VirtualMachine(sizes, rv);
		GCSim.log(vm.toString()+" initialized.");
		return vm;
	}

	public List<Duration> start() throws InvalidObjectException, InterruptedException {
		GCSim.log(this.toString()+" started.");
		while (randVarTable.get("arrivals").size() > 0) {
				Instant arrival = Instant.now().plusMillis(randVarTable.get("arrivals").poll());
/*spin-lock*/	while (waitingArrival(arrival)) /* spin-lock*/ ;
				Long size = randVarTable.get("sizes").poll();
				Instant lifetime = Instant.now().plusMillis(randVarTable.get("lifetimes").poll());
				Reference newRef = allocate(Object_T.generate(size.longValue(), stack));
				stack.put(lifetime, newRef);
				GCSim.log("Arrival of "+newRef.deref().toString()+" on Eden at "+arrival+" with size "+size+" referenced by "+newRef.toString()+" with lifetime "+lifetime+".");
			}
		GCSim.log("The last arrival has occurred, VM halting!");
		System.out.println(gen0.toString()+" state: "+gen0.addrSpace().stream().map(x -> x.size()).reduce(Long::sum)+" of "+gen0.addrSpace().size()+" words in use.");
		System.out.println(gen1.toString()+" state: "+gen1.addrSpace().stream().map(x -> x.size()).reduce(Long::sum)+" of "+gen1.addrSpace().size()+" words in use.");
		System.out.println(gen2.toString()+" state: "+gen2.addrSpace().stream().map(x -> x.size()).reduce(Long::sum)+" of "+gen2.addrSpace().size()+" words in use.");
		return pauseTimes;
	}
	
	private Reference allocate(Object_T o) throws InvalidObjectException, InterruptedException {
		try { 
			return gen0.allocate(o);
		} catch (OutOfMemoryException oom) {
			GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
			Duration d = invokeGC(oom.generation(), pause());
			pauseTimes.push(d);
			return allocate(o);
		}
	}
	
	private Instant pause() {
		Instant now = Instant.now();
		GCSim.log(this.toString()+" paused.");
		return now;
	}
	
	private Instant resume() {
		Instant now = Instant.now();
		GCSim.log(this.toString()+" resumed.");
		return now;
	}

	private boolean waitingArrival(Instant arr) {
		// this looks up the current clock time as the key of an element in the reference pool
		return (Instant.now().isBefore(arr));
	}
	
	private HashMap<Instant, Reference> checkLive(HashMap<Instant, Reference> stack, Instant paused) {
		// this checks the pool of references and drops any expired references
		HashMap<Instant, Reference> r = new HashMap<>();
		if (!pauseTimes.isEmpty()) 
			paused = paused.minus(pauseTimes.peek());		// adjust for the time elapsed during the previous pause
		for (Instant i : stack.keySet()) 
			if (paused.isBefore(i)) r.put(i, stack.get(i));
		return r;
	}
	
	private Duration invokeGC(Heap gen, Instant pause) throws InvalidObjectException, InterruptedException {
		if (gen == gen0) 
			try {
				stack = checkLive(stack, pause);
				initTrace(gen0);
				gen.GC(gen1);
			} catch (OutOfMemoryException oom) {
				GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
				return invokeGC(oom.generation(), pause); 
		} else if (gen == gen1) 
			try {
				initTrace(gen1);
				gen.GC(gen2);
			} catch (OutOfMemoryException oom) {
				GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
				return invokeGC(oom.generation(), pause); 
		} else if (gen == gen2) 
			try {
				initTrace(gen2);
				gen.GC(gen2);
			} catch (OutOfMemoryException oom) { 
				GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
				return invokeGC(oom.generation(), pause);
			} 
		Instant resume = resume();
		return Duration.between(pause, resume);
	}
	
	private void initTrace(Heap gen) throws InterruptedException {
		String sg = gen.toString();
		GCSim.log("Commenced tracing generation(s): "+sg+".");
		for (Reference i : stack.values()) {
			trace(i);
		}
		GCSim.log("Completed tracing.");
	}
		
	private void trace(Reference ref) throws InterruptedException {
		Thread.sleep(work*markFactor);
		if (!ref.deref().marked()) {
			GCSim.log("Now visiting "+ref.deref().toString()+".");
			ref.deref().mark();
			List<Reference> g = ref.deref().refs().stream().collect(Collectors.toList());
			for (Reference r : g) trace(r);
		}
	}
	
}
