package gcsim;

import java.time.*;

public class OutOfMemoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Heap generation;
	private Instant time;
	
	public OutOfMemoryException(Heap gen) {
		generation = gen;
		time = Instant.now();
	}

	public Heap generation() { return generation; }
	public Instant time() { return time; }
}
