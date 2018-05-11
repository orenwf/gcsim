package gcsim;

import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Object_T {
	
	private List<Reference> references;
	private Long size;
	private boolean marked;
	private Integer age;

	
	public static Object_T ofSize(Long size) {
		Object_T o = new Object_T(size);
		return o;
	}
	
	public static Object_T makeEmpty(Long size) {
		Object_T o = new Object_T(size);
		return o;
	}
	
	public static Object_T generate(Long size, HashMap<Instant, Reference> stack) {
		Object_T o = new Object_T(size);
		Integer refs = new Integer((int) Math.log(size.doubleValue()));
		o.references = stack.values().stream().distinct().limit(refs).collect(Collectors.toList());
		return o;
	}
	
	private Object_T(Long words) {
		size = words;
		age = 0;
		marked = false;
	}
	
	// TODO: implement factory with random variables
	public Long size() { return size; }
	
	public void resize(Long x) throws InvalidObjectException {
		if (x <= size) size -= x;
		else throw new InvalidObjectException();
	}
	
	public boolean marked() { return marked; }
	
	public void mark() { marked = true; }
	
	public void unMark() { marked = false; }
	
	public Integer getAge() { return age; }
	
	public void incAge() { age++; }
	
	public List<Reference> refs() { return references; }
		
}