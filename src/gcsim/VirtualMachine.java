package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.time.*;
import java.util.HashMap;
import java.util.stream.Collectors;

public class VirtualMachine {

	public static final Integer work = 5;
	public static final Integer markFactor = 1;
	public static final Integer sweepFactor = 2;
	private static final Integer memory = 134_217_728;	// this is 1 GB divided into 8 byte words
	private HashMap<Instant, Reference> stack;			// the thread-or-stack reference pool
	private Heap gen0, gen1, gen2;
	private HashMap<Class<?>, Object> randVarTable;
	private boolean running = true;
	private boolean paused = false;
	
	private VirtualMachine(List<Integer> _sizes, HashMap<Class<?>, Object> _rv) {
		gen0 = Eden.init(_sizes.get(0));
		gen1 = Survivor.init(_sizes.get(1));
		gen2 = Mature.init(_sizes.get(2));
		stack = new HashMap<Instant, Reference>();
		randVarTable = _rv;
	}
	
	public static VirtualMachine init(List<Integer> proportions, HashMap<Class<?>, Object> rv) {
		List<Integer> sizes = proportions.stream().map(x -> x * memory / 100)
												.collect(Collectors.toList());
		VirtualMachine vm = new VirtualMachine(sizes, rv);
		GCSim.log(vm.toString()+" initialized.");
		return vm;
	}

	public void start() throws InvalidObjectException, InterruptedException {
		GCSim.log(this.toString()+" started.");
		while (running) {
			while (!paused) {
				@SuppressWarnings("unchecked")
				Instant arrival = ((Stack<Instant>) randVarTable.get(Instant.class)).pop();
/*spin-lock*/	while (waitingArrival(arrival)) /* spin-lock*/ ;
				@SuppressWarnings("unchecked")
				Integer size = ((Stack<Integer>)randVarTable.get(Integer.class)).pop();
				@SuppressWarnings("unchecked")
				Instant lifetime = Instant.now().plusMillis(((Stack<Long>)randVarTable.get(Long.class)).pop());
				Reference newRef = allocate(Object_T.generate(size, stack), lifetime);
				stack.put(lifetime, newRef);
				GCSim.log("Arrival of "+newRef.deref().toString()+" at "+arrival+" with lifetime "+newRef.toString()+".");
			}
		}
	}
	
	private Reference allocate(Object_T o, Instant lifetime) throws InvalidObjectException, InterruptedException {
		try { return gen0.allocate(o);
		} catch (OutOfMemoryException oom) {
			GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
			invokeGC(oom.generation());
			return allocate(o, lifetime);
		}
	}
	
	private void pause() {
		paused = true;
		GCSim.log(this.toString()+" paused.");
	}
	
	private void resume() {
		paused = false;
		GCSim.log(this.toString()+" resumed.");
	}

	private boolean waitingArrival(Instant arr) {
		// this looks up the current clock time as the key of an element in the reference pool
		// TODO : implement comparison of time to clock
		return false;
	}
	
	private void checkLive(HashMap<Instant, Reference> s) {
		// TODO: this checks the pool of references and drops any expired references
	}
	
	private void invokeGC(Heap gen) throws InvalidObjectException, InterruptedException {
		if (gen == gen0) try {
			pause();
			checkLive(stack);
			List<Heap> l = new LinkedList<Heap>();
			l.add(gen0);
			trace(l);
			gen.GC(gen1);
		} catch (OutOfMemoryException oom) {
			resume();
			GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
			invokeGC(oom.generation()); 
		} else if (gen == gen1) try {
			pause();
			checkLive(stack);
			List<Heap> l = new LinkedList<>();
			l.add(gen0);
			l.add(gen1);
			trace(l);
			gen.GC(gen2);
		} catch (OutOfMemoryException oom) {
			resume();
			GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
			invokeGC(oom.generation()); 
		} else if (gen == gen2) try {
			pause();
			checkLive(stack);
			List<Heap> l = new LinkedList<>();
			l.add(gen0);
			l.add(gen1);
			l.add(gen2);
			trace(l);
			gen.GC(gen2);
		} catch (OutOfMemoryException oom) { 
			resume();
			GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
			invokeGC(oom.generation());
		} resume();
		
	}
	
	private void trace(List<Heap> gens) throws InterruptedException {
		String sg = "";
		for (Heap gen : gens) {
			if (gens.indexOf(gen) != gens.size()-1) sg=sg+gen.toString()+", ";
			else sg=sg+gen.toString();
		}
		GCSim.log("Commenced tracing generation(s): "+sg+".");
		for (Reference i : stack.values()) {
			mark(i, gens);
		}
		GCSim.log("Completed tracing.");
	}
		
	private void mark(Reference ref, List<Heap> gens) throws InterruptedException {
		for (Heap gen : gens) {
			Thread.sleep(work*markFactor);
			if (ref.in(gen)) {
				ref.deref().mark();
				List<Reference> g = ref.deref().refs().stream()
								.collect(Collectors.toList());
				for (Reference r : g) mark(r, gens);
			}
		}
	}
	
}
