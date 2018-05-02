package gcsim;

import gcsim.Objtypes.Reference;

public class VirualMachine {
	
	final int memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	Stack stack = new Stack();
	Heap heap = new Heap(memory, null);
	
	
	static void makeAnObject(int size, int lifetime) {
		// create a brand new reference
		Reference x = new Reference(size, lifetime);
		// allocate new memory from the heap, get the address, pass it back to the reference
		
	}

}
