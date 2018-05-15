package gcsim;

import java.util.LinkedList;
import java.util.List;

public class Survivor implements Heap {
	
	private static Integer AGELIMIT = 2;
	private Long aSize, bSize;
	private List<Object_T> a;
	private List<Object_T> b;
	private List<Object_T> current;
	private final SampleDistribution sampDist;
	
	public static Survivor init(Long size, SampleDistribution sd) {
		Survivor s = new Survivor(size, sd);
		s.current = s.a;
		sd.log("Survivor generation of size "+s.a.get(0).size()+" + "+s.b.get(0).size()+" intialized.");
		return s;
	}
	
	public Survivor(Long size, SampleDistribution sd) {
		aSize = size/2 + size%2;
		bSize = size/2;
		a = new LinkedList<>();
		b = new LinkedList<>();
		a.add(Object_T.makeEmpty(aSize));
		b.add(Object_T.makeEmpty(bSize));
		sampDist = sd;
	}
	
	@Override
	public Reference allocate(Object_T obj) throws InvalidObjectException, OutOfMemoryException {
		memcopy(obj, working());
		Reference r = Reference.init(obj);
//		GCSim.log(r.toString()+" initialized, pointing to newly allocated object "
//				+obj.toString()+" located on "+this.toString()+".");
		return r;
	}

	@Override
	public List<Object_T> addrSpace() {
		List<Object_T> l = new LinkedList<Object_T>();
		l.addAll(a);
		l.addAll(b);
		return l;
	}

	@Override
	public Long size() { return aSize + bSize - a.get(0).size() - b.get(0).size(); }
	
	@Override
	public void GC(Heap target) throws OutOfMemoryException, InvalidObjectException, InterruptedException {
		sampDist.log("Commence garbage collection in "+this.toString()+".");
		Long m = 0L;
		swap();		// sets the newly swept and copied-into side as the working side
		m = stopAndCopy(target);
		sweep();	// sweeps the non-working side
		sampDist.log("Garbage collection in "+this.toString()+" complete, freed "+m+" words.");
	}
	
	private List<Object_T> working() { return current; }
	
	private List<Object_T> nonWorking() { if (current == a) return b; else return a; }
	
	private void swap() { if (current == a) current = b; else current = a; }
	
	private Long stopAndCopy(Heap target) 
			throws OutOfMemoryException, InvalidObjectException, InterruptedException {
		LinkedList<Object_T> agedOut = new LinkedList<>();
		Long t = 0L;
		for (int x=1; x < nonWorking().size(); x++) {
			Object_T i = nonWorking().get(x);
			Thread.sleep(VirtualMachine.work*VirtualMachine.sweepFactor);
			if (i.marked()) {
				i.incAge();
				if (i.getAge() > AGELIMIT) agedOut.add(i);
				else memcopy(i, working());
			} else t += i.size();
		}
		promote(agedOut, target);
		return t;
	}
	
	private void memcopy(Object_T obj, List<Object_T> target) 
			throws OutOfMemoryException, InvalidObjectException {
		Object_T free = target.get(0);
		if (free.size() >= obj.size()) {
			target.add(target.indexOf(free)+1, obj);
			free.resize(obj.size());
			obj.unMark();
			return;
		}
		throw new OutOfMemoryException(this);
	}
	
	private void promote(List<Object_T> toPromote, Heap target) 
			throws OutOfMemoryException, InvalidObjectException {
		for (Object_T i : toPromote) target.allocate(i);
	}
	
	private void sweep() {
		Long size;
		if (nonWorking() == a) size = aSize; else size = bSize;
		LinkedList<Object_T> clean = new LinkedList<Object_T>();
		clean.add(Object_T.makeEmpty(size));
	}
	
}
