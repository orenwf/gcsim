package gcsim;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Objtype {
	
	ArrayList<Word> contents = new ArrayList<Word>();
	
	public int size() { return contents.size(); }
	
	static class Word {
		
		boolean marked;
		Reference ptr;
		
		public Word(Reference _ptr) { 
			 ptr = _ptr;
		}
	}
	
	public Objtype(int words, Reference ... ref) {
		for (int i = 0; i < ref.length; i++) 
			contents.add(new Word(ref[i]));
		for (int i = 0; i < words - ref.length; i++)
			contents.add(new Word(null));
	}
	
}