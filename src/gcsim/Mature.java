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
		addrSpace.add(Object_T.makeEmpty(capacity));
	}
	
	@Override
	public Reference allocate(Object_T obj) throws InvalidObjectException, OutOfMemoryException {
		memcopy(obj);
		Reference r = Reference.init(obj);
		GCSim.log(r.toString()+" initialized, pointing to newly allocated object "
				+obj.toString()+" located on "+this.toString()+".");
		return r;
	}

	@Override
	public List<Object_T> addrSpace() {
		return addrSpace.stream().collect(Collectors.toList());
	}
	

	@Override
	public void GC(Heap target) throws OutOfMemoryException, InvalidObjectException, InterruptedException {
		GCSim.log("Commence garbage collection in "+this.toString()+".");
		GCSim.log("Garbage collection in "+this.toString()+" complete, reclaiming "+sweepCompact()+" words.");
	}
	
	private void memcopy(Object_T obj) throws OutOfMemoryException, InvalidObjectException {
		Object_T free = addrSpace.get(0);
		if (free.size() >= obj.size()) {
			addrSpace.add(addrSpace.indexOf(free)+1, obj);
			free.resize(obj.size());
			return;
		}
		throw new OutOfMemoryException(this);
	}
	
	private Integer sweepCompact() throws InterruptedException {
		Integer reclaimed = 0;
		for (Object_T i : addrSpace) {
			Thread.sleep(10);
			if (addrSpace.indexOf(i) == 0) ;
			else if (!i.marked()) {
				reclaimed += i.size();
				addrSpace.remove(i);
			}
			i.incAge();
		}
		addrSpace.set(0, Object_T.makeEmpty(reclaimed));
		return reclaimed;
	}
}
