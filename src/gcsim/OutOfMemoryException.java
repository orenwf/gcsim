package gcsim;

public class OutOfMemoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer generation;
	
	public OutOfMemoryException(Integer _generation) {
		generation = _generation;
	}

	public Integer generation() { return generation; }
}
