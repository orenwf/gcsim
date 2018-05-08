package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.io.PrintWriter;
import java.time.Instant;

// TODO: Implement clocks, arrivals, heap, size and reference randomization

public class VirtualMachine {
	
	public static final Integer memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	public final PrintWriter VMLog;
	private final HashSet<Reference> stack;		// the thread / stack reference pool
	private final Heap gen0, gen1, gen2;
	private boolean running = true;
	
	private VirtualMachine(List<Integer> _sizes, PrintWriter pw) {
		gen0 = Eden.init(_sizes.get(0));
		gen1 = Survivor.init(_sizes.get(1));
		gen2 = Mature.init(_sizes.get(2));
		stack = new HashSet<Reference>();
		VMLog = pw;
	}
	
	public static VirtualMachine init(List<Integer> proportions, PrintWriter pw) {
		List<Integer> sizes = proportions.stream().map(x -> x * memory / 100)
												.collect(Collectors.toList());
		return new VirtualMachine(sizes, pw);
	}

	public void start() {
		VMLog.println("VM: "+this.toString()+" started at "+Instant.now().toString()+".");
		while (running) {
			
		}
	}
	
	public void pause() {
		running = false;
		VMLog.println("VM: "+this.toString()+" paused at "+Instant.now().toString()+".");
	}
	
	public void resume() {
		running = true;
		VMLog.println("VM: "+this.toString()+" resumed at "+Instant.now().toString()+".");
	}

	public Reference allocate(Object_T o) throws InvalidObjectException {
		try { return gen0.memalloc(o);
		} catch (OutOfMemoryException oom) {
			VMLog.println(oom.time()+": "+oom.generation().toString()+" out of memory.");
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
			VMLog.println(oom.time()+": "+oom.generation().toString()+" out of memory.");
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
			VMLog.println(oom.time()+": "+oom.generation().toString()+" out of memory.");
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
			VMLog.println(oom.time()+": "+oom.generation().toString()+" out of memory.");
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
