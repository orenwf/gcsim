package tests;

import gcsim.Eden;
import gcsim.InvalidObjectException;
import gcsim.Object_T;
import gcsim.OutOfMemoryException;

public class HeapTests {

	public static void main(String ... args) {
		
		Eden x = Eden.init(100_000);
		while (true) {
			Object_T obj = Object_T.ofSize(100);
			try { 
				x.allocate(obj);
			} catch (OutOfMemoryException | InvalidObjectException exc) {
			System.out.println(exc.getMessage());
			break;
			}
		}
		
		System.out.println(x.addrSpace().toString());
		
	}
	
}
