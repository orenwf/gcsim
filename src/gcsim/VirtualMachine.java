package gcsim;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.time.*;

public class VirtualMachine {
	
	final Integer memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	private Stack<Reference> stack;
	private Heap heap;
	
	public VirtualMachine(List<Integer> _sizes) {
		heap = Heap.init(memory, _sizes.stream().limit(3).collect(Collectors.toList()));
		stack = new Stack<Reference>();
	}
	
	void allocate(Object_T o) {
		// check if memory manager can allocate a new object
		// create a brand new reference
		// allocate new memory from the heap, get the address, pass it back to the reference		
	}
		
	void trace() {
		
	}
	
}
