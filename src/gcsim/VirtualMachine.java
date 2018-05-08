package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.time.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

// TODO: Implement clocks, arrivals, lifetimes

public class VirtualMachine {
	
	private static final Integer memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	private final HashSet<Reference> stack;		// the thread / stack reference pool
	private final Heap gen0, gen1, gen2;
	private boolean running = true;
	private boolean paused = false;
	// TODO: correct implementation of rv stack of HashMaps (Time)
	private Stack<HashMap<String, Object>> rv;
	
	private VirtualMachine(List<Integer> _sizes, Stack<HashMap<String,Object>> _rv) {
		gen0 = Eden.init(_sizes.get(0));
		gen1 = Survivor.init(_sizes.get(1));
		gen2 = Mature.init(_sizes.get(2));
		stack = new HashSet<Reference>();
		rv = _rv;
	}
	
	public static VirtualMachine init(List<Integer> proportions, Stack<HashMap<String, Object>> rv) {
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
				checkLive(stack);
				if (arrival(rv.peek())) {
					stack.add(allocate(Object_T.generate(rv.pop(), stack)));
				}
			}
		}
	}
	
	private Reference allocate(Object_T o) throws InvalidObjectException, InterruptedException {
		try { return gen0.allocate(o);
		} catch (OutOfMemoryException oom) {
			GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
			invokeGC(oom.generation());
			return allocate(o);
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

	private boolean arrival(HashMap<String, Object> a) {
		// TODO : implement comparison of time to clock
		return false;
	}
	
	private void checkLive(HashSet<Reference> s) {
		// TODO
	}
	
	private void invokeGC(Heap gen) throws InvalidObjectException, InterruptedException {
		if (gen == gen0) try {
			pause();
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
		for (Reference i : stack) {
			mark(i, gens);
		}
		GCSim.log("Completed tracing.");
	}
		
	private void mark(Reference ref, List<Heap> gens) throws InterruptedException {
		for (Heap gen : gens) {
			Thread.sleep(5);
			if (ref.in(gen)) {
				ref.deref().mark();
				List<Reference> g = ref.deref().refs().stream()
								.collect(Collectors.toList());
				for (Reference r : g) mark(r, gens);
			}
		}
	}
	
}
