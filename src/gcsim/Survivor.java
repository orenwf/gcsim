package gcsim;

import java.util.LinkedList;
import java.util.List;

public class Survivor implements Heap {
	
	private static Integer AGELIMIT = 2;
	private Integer aSize, bSize;
	private List<Object_T> a;
	private List<Object_T> b;
	private List<Object_T> current;
	
	public static Survivor init(Integer size) {
		Survivor s = new Survivor(size);
		s.current = s.a;
		return s;
	}
	
	public Survivor(Integer size) {
		aSize = size/2 + size%2;
		bSize = size/2;
		a = new LinkedList<>();
		b = new LinkedList<>();
		a.add(Object_T.makeEmpty(aSize));
		b.add(Object_T.makeEmpty(bSize));
	}
	
	@Override
	public Reference allocate(Object_T obj) throws InvalidObjectException, OutOfMemoryException {
		memcopy(obj, working());
		Reference r = Reference.init(obj);
		GCSim.log(r.toString()+" initialized, pointing to newly allocated object "
				+obj.toString()+" located on "+this.toString()+".");
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
	public void GC(Heap target) throws OutOfMemoryException, InvalidObjectException {
		stopAndCopy(working(), target);
		swap();
		sweep();
	}
	
	private List<Object_T> working() { return current; }
	
	private List<Object_T> clean() { if (current == a) return b; else return a; }
	
	private void swap() { if (current == a) current = b; else current = a; }
	
	private void stopAndCopy(List<Object_T> working, Heap target) 
			throws OutOfMemoryException, InvalidObjectException {
		LinkedList<Object_T> agedOut = new LinkedList<>();
		for (Object_T i : working) {
			if (i.marked()) {
				if (i.getAge() > AGELIMIT) agedOut.add(i);
				else memcopy(i, clean());
				i.incAge();
			}
		}
		promote(agedOut, target);
	}
	
	private void memcopy(Object_T obj, List<Object_T> target) 
			throws OutOfMemoryException, InvalidObjectException {
		Object_T free = working().get(0);
		if (free.size() >= obj.size()) {
			current.add(current.indexOf(free)+1, obj);
			free.resize(obj.size());
			return;
		}
		throw new OutOfMemoryException(this);
	}
	
	private void promote(List<Object_T> toPromote, Heap target) 
			throws OutOfMemoryException, InvalidObjectException {
		for (Object_T i : toPromote) target.allocate(i);
	}
	
	private void sweep() {
		Integer size;
		if (clean() == a) size = aSize; else size = bSize;
		LinkedList<Object_T> clean = new LinkedList<Object_T>();
		clean.add(Object_T.makeEmpty(size));
	}
	
}
