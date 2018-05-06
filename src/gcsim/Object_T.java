package gcsim;

import java.util.ArrayList;

public class Object_T {
	
	private ArrayList<Word> contents = new ArrayList<Word>();

	public Object_T(int words, Reference ... ref) {
		for (int i = 0; i < ref.length; i++) 
			contents.add(new Word(ref[i]));
		for (int i = 0; i < words - ref.length; i++)
			contents.add(new Word(null));
	}
		
	public int size() { return contents.size(); }
	
	public ArrayList<Word> getContents() { return contents; }
	
}