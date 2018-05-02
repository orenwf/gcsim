package gcsim;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Objtype {
	
	ArrayList<Word> contents = new ArrayList<Word>();
	
	public int size() { return contents.size(); }
	
	static class Reference {
		int address;
		int words;
		
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
	
	public Objtype(int words, Reference ... ref) {
		for (int i = 0; i < words - ref.length; i++) 
			contents.add(new Word(Optional.empty()));
		
		Stream.of(ref).collect(Collectors.toList())
			.forEach(x -> contents.add(new Word(Optional.of(x))));
	}
	
}