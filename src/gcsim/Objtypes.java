package gcsim;

import java.util.Optional;

public class Objtypes {
	
	static class Reference {
		int address;
		int words;
		int lifetime;
		
		public Reference(int _address, int _words) { 
			address = _address;
			words = _words;
		}
		
		public void initRef(int size, int duration) {
			
		}
	}
		
	static class Word {
		
		boolean marked;
		Optional<Reference> ptr;
		
		public Word(Optional<Reference> _ptr) { 
			 ptr = _ptr;
		}
	}
}