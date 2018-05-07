package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

// TODO: Implement clocks, arrivals, heap, size and reference randomization

public class VirtualMachine {
	
	final Integer memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	private Stack<Reference> stack;		// the stack machine
	@SuppressWarnings("unused")
	private List<Object_T> scope;		// for selecting references
	private Heap heap;
	
	public static VirtualMachine init(List<Integer> sizes) {
		return new VirtualMachine(sizes);
	}
	
	private VirtualMachine(List<Integer> _sizes) {
		heap = Heap.init(memory, _sizes.stream().limit(3).collect(Collectors.toList()));
		stack = new Stack<Reference>();
	}
	
	public Reference allocate(Object_T o) throws InvalidObjectException {
		try { return heap.newalloc(o);
		} catch (OutOfMemoryException oom) {
			oom.printStackTrace();
			invokeGC(oom.generation());
			return allocate(o);
		}
	}
	
	private void invokeGC(Integer gen) {
		List<Reference> graph = new LinkedList<>();
		for (Reference i : stack) {
			graph = trace(gen, i);
			graph = graph.stream().distinct()
					.filter(s -> heap.in(gen, s))
					.collect(Collectors.toList());
		}
		if (gen < 2) heap.promote(gen, graph);
		else heap.sweep(gen);
	}
	
	private List<Reference> trace(Integer gen, Reference root) {
		List<Reference> l = new LinkedList<Reference>();
		//TODO : finish tracing algorithm
		return l;
	}
	
}
