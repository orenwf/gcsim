package gcsim;


import java.util.LinkedList;
import java.util.List;

public class Young implements Heap {
	
	private Long capacity;
	private LinkedList<Object_T> addrSpace;
	
	private Young(Long size) {
		capacity = size;
		addrSpace = new LinkedList<Object_T>();
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
	
	public static Young init(Long size) {
		Young n = new Young(size);
		GCSim.log("Young generation of size "+n.addrSpace().get(0).size()+" intialized.");
		return n;
	}
	
	@Override
	public Reference allocate(Object_T obj) throws OutOfMemoryException, InvalidObjectException {
		Object_T free = addrSpace.get(0);
		if (free.size() >= obj.size()) {
			addrSpace.add(addrSpace.indexOf(free)+1, obj);
			free.resize(obj.size());
			Reference r = Reference.init(obj);
//			GCSim.log(r.toString()+" initialized, pointing to newly allocated object "
//					+obj.toString()+" located on "+this.toString()+".");
			return r;
		}
		throw new OutOfMemoryException(this);
	}

	public Long size() { return capacity - addrSpace.get(0).size(); }

	@Override
	public List<Object_T> addrSpace() { return addrSpace; }
	
	@Override
	public void GC(Heap target) throws OutOfMemoryException, InvalidObjectException, InterruptedException {
		GCSim.log("Commence garbage collection in "+this.toString()+".");
		Long m = promote(target);
		sweep();
		GCSim.log("Garbage collection in "+this.toString()+" complete, freed "+m+" words.");
	}

	private Long promote(Heap target) throws OutOfMemoryException, InvalidObjectException, InterruptedException {
		Long t = 0L;
		for (Object_T i : addrSpace) {
			Thread.sleep(VirtualMachine.work*VirtualMachine.sweepFactor);
			if (addrSpace.indexOf(i) == 0) ;
			else if (i.marked()) {
				i.incAge();
				t += i.size();
				target.allocate(i);
			} else t += i.size();
		}
		return t;
	}
	
	private void sweep() {
		addrSpace = new LinkedList<Object_T>();
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
}
