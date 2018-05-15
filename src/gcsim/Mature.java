package gcsim;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Mature implements Heap {
	
	private final SampleDistribution sampDist;
	List<Object_T> addrSpace;
	Long capacity;
	
	public static Mature init(Long size, SampleDistribution sd) {
		Mature x = new Mature(size, sd);
		sd.log("Mature generation of size "+x.addrSpace().get(0).size()+" intialized.");
		return x;
	}

	private Mature(Long _size, SampleDistribution sd) {
		capacity = _size;
		addrSpace = new LinkedList<>();
		addrSpace.add(Object_T.makeEmpty(capacity));
		sampDist = sd;
	}
	
	@Override
	public Reference allocate(Object_T obj) throws InvalidObjectException, OutOfMemoryException {
		memcopy(obj);
		Reference r = Reference.init(obj);
//		GCSim.log(r.toString()+" initialized, pointing to newly allocated object "
//				+obj.toString()+" located on "+this.toString()+".");
		return r;
	}

	@Override
	public List<Object_T> addrSpace() {
		return addrSpace.stream().collect(Collectors.toList());
	}
	
	@Override
	public Long size() { return capacity - addrSpace.get(0).size(); }
	

	@Override
	public void GC(Heap target) throws OutOfMemoryException, InvalidObjectException, InterruptedException {
		sampDist.log("Commence garbage collection in "+this.toString()+".");
		sampDist.log("Garbage collection in "+this.toString()+" complete, reclaiming "+sweepCompact()+" words.");
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
	
	private Long sweepCompact() throws InterruptedException {
		Long reclaimed = 0L;
		for (int x = 1; x < addrSpace.size(); x++) {
			Object_T i = addrSpace.get(x);
			Thread.sleep(VirtualMachine.work*VirtualMachine.sweepFactor);
			if (!i.marked()) {
				reclaimed += i.size();
				addrSpace.remove(i);
			}
			i.incAge();
			i.unMark();
		}
		addrSpace.set(0, Object_T.makeEmpty(reclaimed));
		return reclaimed;
	}
}
