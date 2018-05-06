package gcsim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Heap {
	
	private ArrayList<Word>			addressSpace;
	private List<Integer>			generation;
	private List<List<Reference>> 	freelist 	= new ArrayList<>();

	public static Heap init(Integer capacity, List<Integer> proportions) {
		return new Heap(proportions.stream().map(x -> x * capacity / 100)
						.collect(Collectors.toList()));
	}

	public Heap(List<Integer> sizes) {
		addressSpace = new ArrayList<Word>(sizes.stream().reduce(Integer::sum).get());
		generation = new ArrayList<Integer>(sizes);
		Integer total = 0;
		for (Integer i : generation) {
			List<Reference> ll = new LinkedList<>();
			ll.add(Reference.init(total, i));
			freelist.add(ll);
		}
	}
	
	Reference allocate(List<Reference> fl, Object_T obj) throws OutOfMemoryException {
		Reference r = findFree(fl, obj.size());
		Reference c = Reference.copy(r);
		c.resize(obj.size());
		memcopy(c, obj.getContents());
		r.setAddress(c.size());
		r.resize(r.size()-c.size());
		if (r.size() == 0) fl.remove(r);
		return c;
	}
	
	Reference findFree(List<Reference> fl, Integer _words) throws OutOfMemoryException {
		for (Reference i : fl) 
			if (i.size() >= _words) return i;
		throw new OutOfMemoryException();
	}
	
	void memcopy(Reference r, List<Word> from) {
		for (int i = 0; i < from.size(); i++) 
			addressSpace.set(i, from.get(i));
	}

	void sweep() {
		
	}
}