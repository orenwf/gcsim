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
	public Reference memalloc(Object_T obj) throws OutOfMemoryException, InvalidObjectException {
		for (Object_T i : addrSpace ) {
			if (i.size() >= obj.size() && i.empty()) {
				addrSpace.add(addrSpace.indexOf(i)+1, obj);
				i.resize(obj.size());
				Reference r = Reference.init(obj);
				GCSim.log(r.toString()+" initialized, pointing to newly allocated object "
						+obj.toString()+" located on "+this.toString()+".");
				return r;
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
			if (!i.empty() && i.marked()) {
				i.incAge();
				target.memalloc(i);
			}
		}
	}
	
	private void sweep() {
		addrSpace = new LinkedList<Object_T>();
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
}
