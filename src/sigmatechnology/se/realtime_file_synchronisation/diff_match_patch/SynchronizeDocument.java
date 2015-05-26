package sigmatechnology.se.realtime_file_synchronisation.diff_match_patch;

/**
 * Helper class for SynchronizeRoot, stores a path, its data and a diff list. 
 * 
 * @author David Strömner
 * @version 2015-04-27
 */


import java.io.Serializable;
import java.util.LinkedList;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Diff;

public class SynchronizeDocument implements Serializable{
	private static final long serialVersionUID = 903627564529111834L;
	private String path;
	private String currentData;
	private LinkedList<Diff> diffs;
	
	public SynchronizeDocument(String p, String s){
		path=p;
		currentData=s;
	}
	
	public String getData(){
		return currentData;
	}
	
	public String getPath(){
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