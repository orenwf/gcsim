package gcsim;

import java.util.List;

public interface Heap {

	List<Object_T> addrSpace();
	
	Reference allocate(Object_T o) throws OutOfMemoryException, InvalidObjectException;

	void GC(Heap target) throws OutOfMemoryException, InvalidObjectException;

}