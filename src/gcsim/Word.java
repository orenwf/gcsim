package gcsim;

public class Word {
	
	private boolean marked;
	private Reference ptr;
	
	public Word(Reference _ptr) { 
		 ptr = _ptr;
	}
	
	public boolean marked() { return marked; }
	public void mark() { marked = true; }
	public boolean isRef() { return ptr != null; }
	public Reference deRef() { return ptr; }
	
}
