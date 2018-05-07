package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

// TODO: Implement clocks, arrivals, heap, size and reference randomization

public class VirtualMachine {
	
	static final Integer memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	private Stack<Reference> stack;		// the stack machine
	private Heap gen0, gen1, gen2;
	
	public static VirtualMachine init(List<Integer> proportions) {
		List<Integer> sizes = proportions.stream().map(x -> x * memory / 100)
												.collect(Collectors.toList());
		return new VirtualMachine(sizes);
	}

	private VirtualMachine(List<Integer> _sizes) {
		gen0 = Eden.init(_sizes.get(0));
		gen1 = Survivor.init(_sizes.get(1));
		gen2 = Mature.init(_sizes.get(2));
		stack = new Stack<Reference>();
	}
	
	public Reference allocate(Object_T o) throws InvalidObjectException {
		try { return gen0.memalloc(o);
		} catch (OutOfMemoryException oom) {
			invokeGC(oom.generation());
			return allocate(o);
		}
	}

	private void pause() {
		
	}
	
	private void resume() {
		
	}

	
	private void invokeGC(Heap gen) throws InvalidObjectException {
		pause();
		if (gen == gen0) try {
			List<Heap> l = new LinkedList<Heap>();
			l.add(gen0);
			trace(l);
			gen.GC(gen1); 
		} catch (OutOfMemoryException oom) {
			invokeGC(oom.generation());
		}
		else if (gen == gen1) try { 
			List<Heap> l = new LinkedList<>();
			l.add(gen0);
			l.add(gen1);
			trace(l);
			gen.GC(gen2);
		} catch (OutOfMemoryException oom) {
			invokeGC(oom.generation());
		}
		else if (gen == gen2) try { 
			List<Heap> l = new LinkedList<>();
			l.add(gen0);
			l.add(gen1);
			l.add(gen2);
			gen.GC(gen2);
		} catch (OutOfMemoryException oom) {
			invokeGC(oom.generation());
		}
		resume();
	}
	
	private void trace(List<Heap> gens) {
		for (Reference i : stack) {
			mark(i, gens);
		}
	}
		
	private void mark(Reference root, List<Heap> gens) {
		for (Heap gen : gens) 
			if (root.in(gen)) {
				root.deref().mark();
				List<Reference> g = root.deref().refs().stream()
								.collect(Collectors.toList());
				for (Reference r : g) mark(r, gens);
				}
	}
	
}
