package gcsim;

import java.util.ArrayList;
import java.util.LinkedList;
import gcsim.Objtypes.Reference;
import gcsim.Objtypes.Word;

public class Heap {
	
	ArrayList<	ArrayList<Word>		> 	heap 		= new ArrayList<>();
	ArrayList<	LinkedList<Reference>> 	freelist 	= new ArrayList<>();
	ArrayList<	Integer				> 	ages 		= new ArrayList<>();
	
	public Heap(int gens, int ... _ages) {
		for (int i = 0; i < gens; i++) {
			heap.add(new ArrayList<Word>());
			freelist.add(new LinkedList<Reference>());
			ages.add(_ages[i]);
		}
	}

}