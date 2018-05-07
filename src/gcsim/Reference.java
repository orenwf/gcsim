package gcsim;

public class Reference {
	
	private Object_T address;
	
	public static Reference init(Object_T address) { 
		return new Reference(address); 
	}
	
	public static Reference copy(Reference r) { 
		return new Reference(r.address); 
	}

	public Reference(Object_T _address) { 
		address = _address;
	}
		
	public Object_T deref() { return address; }
	
	public boolean in(Heap x) { return x.addrSpace().contains(address); }
	
}
