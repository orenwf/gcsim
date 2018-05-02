package gcsim;

import java.util.ArrayList;
import gcsim.Objtype.Reference;

public class VirualMachine {
	
	final int memory = 134_217_728;	// this is 1 Gigabyte of memory divided into 8 byte words
	int generations;
	ArrayList<Reference> stack = new ArrayList<>();
	MemManager heap;
	
	public VirualMachine(int _generations, int ... _ages) {
		heap = new MemManager(_generations, _ages);
	}
	
	void allocate(Objtype o) {
		// check if memory manager can allocate a new object
		if (heap.hasSpace(o.contents.size())) {
			
		} else {
			
		}
		// create a brand new reference
		// allocate new memory from the heap, get the address, pass it back to the reference
		
	}
	
	void collectgarbage() {}
	
	void trace() {
		
	}
	
}
