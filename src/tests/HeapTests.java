package tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import gcsim.Young;
import gcsim.InvalidObjectException;
import gcsim.Object_T;
import gcsim.OutOfMemoryException;
import gcsim.Survivor;

public class HeapTests {

	public static void main(String ... args) throws InvalidObjectException, InterruptedException {
		Logger logger = Logger.getLogger("edu.gcsim.tests");
		// TODO: Init Eden
		Young x = Young.init(25L, null);
		
		// TODO :allocate to Eden
		try { x.allocate(Object_T.ofSize(25L)); 
		}  catch (OutOfMemoryException e1) {	logger.log(Level.INFO, x.addrSpace().toString()); }
		
		// TODO :overflow Eden
		try { x.allocate(Object_T.ofSize(1L)); 
		} catch (OutOfMemoryException e1) { logger.log(Level.INFO, x.addrSpace().toString()); }
		
		// TODO :Init Survivor
		Survivor y = Survivor.init(50L, null);
		
		// TODO :allocate to Survivor
		try { y.allocate(Object_T.ofSize(20L)); 
		} catch (OutOfMemoryException e1) { logger.log(Level.INFO, y.addrSpace().toString()); }
		
		// TODO :overflow survivor 1
		try { 
			Object_T o = Object_T.ofSize(15L);
			o.mark();
			y.allocate(o);
			y.allocate(Object_T.ofSize(6L));
		} catch (OutOfMemoryException oom) { logger.log(Level.INFO, y.addrSpace().toString());}
		
		// TODO :overflow survivor into survivor 2
		try {
			Object_T o = Object_T.ofSize(5L);
			Object_T p = Object_T.ofSize(5L);
			o.mark();
			p.mark();
			y.allocate(o);
			y.allocate(p);
		} catch (OutOfMemoryException oom) { 
			try {
				y.GC(oom.generation());
				logger.log(Level.INFO, y.addrSpace().toString());
			} catch (OutOfMemoryException e) {
				logger.log(Level.INFO, y.addrSpace().toString()); 
			}
		}
		// TODO :overflow survivor 2 into survivor
		
		// TODO :overflow Eden into survivor
		x = Young.init(5L, null);
		y = Survivor.init(50L, null);
		Object_T obj = Object_T.ofSize(5L);
		obj.mark();
		try { 
			x.allocate(obj);
		} catch (OutOfMemoryException oom) {
			try {
				x.GC(y);
				logger.log(Level.INFO, y.addrSpace().toString());
			} catch (OutOfMemoryException e) { }
		}

		// TODO :overflow Eden into survivor and survivor into survivor 2
		
		// TODO :init Mature
		
		// TODO :allocate to Mature
		
		// TODO :overflow Mature
		
		// TODO :overflow Mature with garbage collection
		
		// TODO :overflow Eden into survivor, survivor into survivor2, survivor2 into Mature
		
		// TODO: overflow Eden into survivor, survivor into survivor2, survivor2 into Mature, overflow Mature with garbage collection
				
		
	}
	
}
