package gcsim;

import java.util.LinkedList;
import java.util.List;

public class Survivor implements Heap{
	
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
		a.add(Object_T.ofSize(aSize));
		b.add(Object_T.ofSize(bSize));
	}
	
	@Override
	public Reference memalloc(Object_T obj) throws InvalidObjectException, OutOfMemoryException {
		// TODO Auto-generated method stub
		return null;
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
		stopAndCopy(swap());
	}
	
	private List<Object_T> swap() {
		if (current == a) current = b; 
		else current = a;
		return current;
	}
	
	private void stopAndCopy(List<Object_T> target) throws OutOfMemoryException, InvalidObjectException {
		
	}
	
	private void promote(Heap target) throws OutOfMemoryException, InvalidObjectException {
		
	}
	
}
