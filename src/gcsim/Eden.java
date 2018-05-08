package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Eden implements Heap {
	
	private Integer capacity;
	private LinkedList<Object_T> addrSpace;
	
	private Eden(Integer size) {
		capacity = size;
		addrSpace = new LinkedList<Object_T>();
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
	
	public static Eden init(Integer size) {
		return new Eden(size);
	}

	@Override
	public Reference memalloc(Object_T obj) throws OutOfMemoryException, InvalidObjectException {
		for (Object_T i : addrSpace ) {
			if (i.size() >= obj.size() && i.empty()) {
				addrSpace.add(addrSpace.indexOf(i), obj);
				i.resize(obj.size());
				return Reference.init(obj);
			}
		}
		throw new OutOfMemoryException(this);
	}

	@Override
	public List<Object_T> addrSpace() { return addrSpace; }
	
	@Override
	public void GC(Heap target) throws OutOfMemoryException, InvalidObjectException {
		promote(target);
		sweep();
	}

	private void promote(Heap target) throws OutOfMemoryException, InvalidObjectException {
		for (Object_T i : addrSpace) {
			if (i.marked()) target.memalloc(i);
		}
	}
	
	private void sweep() {
		addrSpace = new LinkedList<Object_T>();
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
}
