package gcsim;

public class OutOfMemoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Heap generation;
	
	public OutOfMemoryException(Heap gen) {
		generation = gen;
	}

	public Heap generation() { return generation; }
}
