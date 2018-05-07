package gcsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Heap {
	
	private ArrayList<Word>							addressSpace;
	private Map<Integer, HashMap<String, Integer>>	generation;
	private List<List<Reference>> 					freelist 	= new ArrayList<>();

	public static Heap init(Integer capacity, List<Integer> proportions) {
		return new Heap(proportions.stream().map(x -> x * capacity / 100)
						.collect(Collectors.toList()));
	}

	public Reference newalloc(Object_T obj) throws InvalidObjectException, OutOfMemoryException {
		List<Word> contents = obj.getContents();
		if (contents.isEmpty()) throw new InvalidObjectException();
		if (contents.size() > generation.get(0).get("max")) throw new InvalidObjectException();
		return memalloc(freelist.get(0), contents);
	}
	
	public boolean in(Integer gen, Reference x) {
		return x.address().compareTo(generation.get(gen).get("min")) >= 0
		&&
		x.address().compareTo(generation.get(gen).get("max")) <= 0;
	}
		
	private Heap(List<Integer> sizes) {
		addressSpace = new ArrayList<Word>(sizes.stream().reduce(Integer::sum).get());
		generation = new HashMap<Integer, HashMap<String, Integer>>();
		Integer total = 0;
		for (int i = 0; i < sizes.size(); i++) {
			HashMap<String, Integer> map = new HashMap<>();
			map.put("min", total);
			map.put("max", sizes.get(i)-1);
			generation.put(i, map);
			List<Reference> ll = new LinkedList<>();
			ll.add(Reference.init(total, generation.get(i).get("max")));
			freelist.add(ll);
			total += sizes.get(i);
		}
	}
	
	private Reference memalloc(List<Reference> fl, List<Word> contents) throws OutOfMemoryException {
		Reference r = findFree(fl, contents.size());
		Reference c = Reference.copy(r);
		c.resize(contents.size());
		memcopy(c, contents);
		r.setAddress(c.size());
		r.resize(r.size()-c.size());
		if (r.size() == 0) fl.remove(r);
		return c;
	}
	
	private Reference findFree(List<Reference> fl, Integer size) throws OutOfMemoryException {
		for (Reference i : fl) 
			if (i.size() >= size) return i;
		throw new OutOfMemoryException(freelist.indexOf(fl));
	}
	
	private void memcopy(Reference r, List<Word> from) {
		for (int i = 0; i < from.size(); i++) 
			addressSpace.set(r.address()+i, from.get(i));
	}

	protected void promote(Integer _generation, List<Reference> refs) {
		//TODO: implement stop and copy to next gen
	}
	
	protected void sweep(Integer gen) {
		int from = generation.get(gen).get("min");
		int to = generation.get(gen).get("max");
		List<Reference> newlist = new LinkedList<Reference>();
		Reference nfr = Reference.init(from, 0);
		for (int current = from; current < to; current++) {
			if (!addressSpace.get(current).marked()) {
				addressSpace.remove(current);
				nfr.resize(nfr.size()+1);
			} else {
				newlist.add(nfr);
				nfr = Reference.init(current, 0);
			}
		}
		freelist.set(gen, newlist);
	}
}