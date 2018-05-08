package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Mature implements Heap {
	
	List<Object_T> addrSpace;
	Integer capacity;
	
	public static Mature init(Integer size) {
		return new Mature(size);
	}

	private Mature(Integer _size) {
		capacity = _size;
		addrSpace = new LinkedList<>();
		addrSpace.add(Object_T.ofSize(capacity));
	}
	
	@Override
	public Reference memalloc(Object_T obj) throws InvalidObjectException, OutOfMemoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object_T> addrSpace() {
		return addrSpace.stream().collect(Collectors.toList());
	}
	
	private void sweep() {
//		int from = generation.get(gen).get("min");
//		int to = generation.get(gen).get("max");
//		List<Reference> newlist = new LinkedList<Reference>();
//		Reference nfr = Reference.init(from, 0);
//		for (int current = from; current < to; current++) {
//			if (!addressSpace.get(current).marked()) {
//				addressSpace.remove(current);
//				nfr.resize(nfr.size()+1);
//			} else {
//				newlist.add(nfr);
//				nfr = Reference.init(current, 0);
//			}
//		}
//		freelist.set(gen, newlist);
	}

	@Override
	public void GC(Heap target) throws OutOfMemoryException, InvalidObjectException {	}

}
