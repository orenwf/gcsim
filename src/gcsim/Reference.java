package gcsim;

public class Reference {
	
	private Object_T address;
	
	public static Reference init(Object_T address) { 
		Reference r = new Reference(address);
//		GCSim.log(r.toString()+" initialized, pointing to object: "+r.deref().toString()+".");
		return r;
	}
	
	public static Reference copy(Reference r) { 
		Reference c = new Reference(r.address);
//		GCSim.log(r.toString()+" copied as "+c.toString()+", pointing to object "+c.deref().toString()+".");
		return c;
	}

	public Reference(Object_T _address) { 
		address = _address;
	}
		
	public Object_T deref() { return address; }
	
	public boolean in(Heap x) { return x.addrSpace().contains(address); }
	
}
