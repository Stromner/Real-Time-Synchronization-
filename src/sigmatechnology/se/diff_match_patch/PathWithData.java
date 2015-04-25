package sigmatechnology.se.diff_match_patch;

/**
 * Helper class that contains a path and the data for the file that the path point 
 * to.
 * 
 * @author David Strömner
 * @version 2015-04-22
 */


import java.nio.file.Path;

class PathWithData {
	private Path path;
	private String currentData;
	
	public PathWithData(Path p, String s){
		path=p;
		currentData=s;
	}
	
	public void setData(String s){
		currentData = s;
	}
	
	public String getData(){
		return currentData;
	}
	
	public Path getPath(){
		return path;
	}
}
