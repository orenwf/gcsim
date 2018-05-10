package tests;

import gcsim.Eden;
import gcsim.InvalidObjectException;
import gcsim.Object_T;
import gcsim.OutOfMemoryException;
import gcsim.Survivor;

public class HeapTests {

	public static void main(String ... args) throws InvalidObjectException, InterruptedException {
		
		// TODO: Init Eden
		Eden x = Eden.init(25);
		// TODO :allocate to Eden
		
		// TODO :overflow Eden
				
		// TODO :Init Survivor
		Survivor y = Survivor.init(50);
		
		// TODO :allocate to Survivor
		
		// TODO :overflow survivor
		
		// TODO :overflow survivor into survivor 2
		
		// TODO :overflow survivor 2 into survivor
		
		// TODO :overflow Eden into survivor
		x = Eden.init(25);
		y = Survivor.init(50);
		for (int i = 0; i < 6; i++) {
			Object_T obj = Object_T.ofSize(5);
			obj.mark();
			try { 
				x.allocate(obj);
			} catch (OutOfMemoryException oom) {
				try {
					x.GC(y);
					x.allocate(obj);
				} catch (OutOfMemoryException e) {}
			}
		}

		// TODO :overflow Eden into survivor and survivor into survivor 2
		x = Eden.init(25);
		y = Survivor.init(50);
		for (int i = 0; i < 6; i++) {
			Object_T obj = Object_T.ofSize(5);
			obj.mark();
			try { 
				x.allocate(obj);
			} catch (OutOfMemoryException oom) {
				try {
					x.GC(y);
					x.allocate(obj);
				} catch (OutOfMemoryException e) {}
			}
		}		
		for (int i = 0; i < 5; i++) {
			Object_T obj = Object_T.ofSize(5);
			try {
				x.allocate(obj);
			} catch (OutOfMemoryException e) {
				try { 
					x.GC(y);
					x.allocate(obj);
				} catch (OutOfMemoryException f) {}
			}
		}

				
		// TODO :init Mature
		
		// TODO :allocate to Mature
		
		// TODO :overflow Mature
		
		// TODO :overflow Mature with garbage collection
		
		// TODO :overflow Eden into survivor, survivor into survivor2, survivor2 into Mature
		
		// TODO: overflow Eden into survivor, survivor into survivor2, survivor2 into Mature, overflow Mature with garbage collection
				
		
	}
	
}
