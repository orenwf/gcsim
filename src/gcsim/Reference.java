package gcsim;

public class Reference {
	
	private Integer address;
	private Integer words;
	
	public static Reference init(Integer address, Integer words) { 
		return new Reference(address, words); 
	}
	
	public static Reference copy(Reference r) { 
		return new Reference(r.address, r.words); 
	}

	public Reference(Integer _address, Integer _words) { 
		address = _address;
		words = _words;
	}
		
	public Integer address() { return address; }
	
	public Integer size() { return words; }
	
	public void setAddress(Integer _address) { address = _address; }
	
	public void resize(Integer newSize) { words = newSize; }
}
