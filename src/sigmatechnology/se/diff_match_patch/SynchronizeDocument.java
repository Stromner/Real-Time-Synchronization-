package sigmatechnology.se.diff_match_patch;

/**
 * Helper class for SynchronizeRoot, stores a path, its data and a diff list. 
 * 
 * @author David Strömner
 * @version 2015-04-27
 */


import java.nio.file.Path;
import java.util.LinkedList;

import sigmatechnology.se.diff_match_patch.fraser_neil.diff_match_patch.Diff;

public class SynchronizeDocument {
	private Path path;
	private String currentData;
	private LinkedList<Diff> diffs;
	
	public SynchronizeDocument(Path p, String s){
		path=p;
		currentData=s;
	}
	
	public String getData(){
		return currentData;
	}
	
	public Path getPath(){
		return path;
	}
	
	public LinkedList<Diff> getDiffs(){
		return diffs;
	}
	
	public void setData(String s){
		currentData = s;
	}
	
	public void setDiffs(LinkedList<Diff> newDiffs){
		diffs = newDiffs;
	}
}