package gcsim;


import java.util.LinkedList;
import java.util.List;

public class Eden implements Heap {
	
	private Integer capacity;
	private LinkedList<Object_T> addrSpace;
	
	private Eden(Integer size) {
		capacity = size;
		addrSpace = new LinkedList<Object_T>();
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
	
	public static Eden init(Integer size) {
		Eden n = new Eden(size);
		GCSim.log("Eden generation intialized.");
		return n;
	}

	@Override
	public Reference allocate(Object_T obj) throws OutOfMemoryException, InvalidObjectException {
		Object_T free = addrSpace.get(0);
		if (free.size() >= obj.size()) {
			addrSpace.add(addrSpace.indexOf(free)+1, obj);
			free.resize(obj.size());
			Reference r = Reference.init(obj);
			GCSim.log(r.toString()+" initialized, pointing to newly allocated object "
					+obj.toString()+" located on "+this.toString()+".");
			return r;
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
			if (i.marked()) {
				i.incAge();
				target.allocate(i);
			}
		}
	}
	
	private void sweep() {
		addrSpace = new LinkedList<Object_T>();
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
}
