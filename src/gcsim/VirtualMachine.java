package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

// TODO: Implement clocks, arrivals, heap, size and reference randomization

public class VirtualMachine {
	
	private static final Integer memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	private final HashSet<Reference> stack;		// the thread / stack reference pool
	private final Heap gen0, gen1, gen2;
	private boolean running = true;
	private boolean paused = false;
	
	private VirtualMachine(List<Integer> _sizes) {
		gen0 = Eden.init(_sizes.get(0));
		gen1 = Survivor.init(_sizes.get(1));
		gen2 = Mature.init(_sizes.get(2));
		stack = new HashSet<Reference>();
	}
	
	public static VirtualMachine init(List<Integer> proportions) {
		List<Integer> sizes = proportions.stream().map(x -> x * memory / 100)
												.collect(Collectors.toList());
		VirtualMachine vm = new VirtualMachine(sizes);
		GCSim.log(vm.toString()+" initialized.");
		return vm;
	}

	public void start() {
		GCSim.log(this.toString()+" started.");
		while (running) {
			while (!paused) {
				
			}
		}
	}
	
	public void pause() {
		paused = true;
		GCSim.log(this.toString()+" paused.");
	}
	
	public void resume() {
		paused = false;
		GCSim.log(this.toString()+" resumed.");
	}

	public Reference allocate(Object_T o) throws InvalidObjectException {
		try { return gen0.allocate(o);
		} catch (OutOfMemoryException oom) {
			GCSim.log(oom.generation().toString()+" out of memory at "+oom.time()+".");
			invokeGC(oom.generation());
			return allocate(o);
		}
	}
	
	private void invokeGC(Heap gen) throws InvalidObjectException {
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
	
	private void trace(List<Heap> gens) {
		for (Reference i : stack) {
			mark(i, gens);
		}
	}
		
	private void mark(Reference ref, List<Heap> gens) {
		for (Heap gen : gens) 
			if (ref.in(gen)) {
				ref.deref().mark();
				List<Reference> g = ref.deref().refs().stream()
								.collect(Collectors.toList());
				for (Reference r : g) mark(r, gens);
			}
	}
	
}
