package gcsim;

import java.util.List;

public class Object_T {
	
	private List<Reference> refs;
	private Integer size;
	private boolean marked;
	private Integer age;

	
	public static Object_T ofSize(Integer size) {
		Object_T o = new Object_T(size);
		return o;
	}
	
	public static Object_T makeEmpty(Integer size) {
		Object_T o = new Object_T(size);
		return o;
	}
	
	private Object_T(Integer words) {
		size = words;
		age = 0;
		marked = false;
	}
	
	// TODO: implement factory with random variables
	public int size() { return size; }
	
	public void resize(Integer x) throws InvalidObjectException {
		if (x <= size) size -= x;
		else throw new InvalidObjectException();
	}
	
	public boolean marked() { return marked; }
	
	public void mark() { marked = true; }
	
	public Integer getAge() { return age; }
	
	public void incAge() { age++; }
	
	public List<Reference> refs() { return refs; }
		
}