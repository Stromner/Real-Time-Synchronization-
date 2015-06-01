package sigmatechnology.se.realtime_file_synchronisation;

/**
 * Utility class for misc methods.
 * 
 * @author David Strömner
 * @version 2015-05-08
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Diff;
import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.fraser_neil.diff_match_patch.Operation;
import sigmatechnology.se.realtime_file_synchronisation.plugin.Controller;

public class Util {
	private static IEditorReference[] ii;
	private static int offset;
	
	/**
	 * Creates a new file, with belonging directories if they do not already
	 * exist.
	 * 
	 * @param filePath full path including the file name and extension.
	 */
	public static void createFile(Path filePath){
		File f = new File(filePath.toString());
		if(!f.exists()){
			System.out.println("File: " + filePath);
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			refreashEclipse();
		}
	}
	
	/**
	 * Delete method provided by http://stackoverflow.com/users/1112963/zon
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir){
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
		refreashEclipse();
	    return dir.delete(); // The directory is empty now and can be deleted.
	}
	
	/**
	 * Opens and read all data from the file.
	 * 
	 * @param filePath Path to the file to be read.
	 * @return The data from the file. null if the file could not be opened.
	 */
	public static String openReadFile(Path filePath){
		String line;
		StringBuilder sb = new StringBuilder();
		// Read the document
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(filePath, SynchronizeRoot.ENCODING);
			// Append each line in the document to our string
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n"); // readLine discards new-row characters, re-append them.
			}
			if(sb.length() != 0){
				sb.deleteCharAt(sb.length()-1); // However last line doesn't contain one, remove it.
			}
			reader.close();
			return new String(sb.toString());
		} catch (IOException e) {
			Controller.getInstance().getLauncher().updatePane("Could not read from " + filePath.toString());
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Open and write all new data to a file.
	 * 
	 * @param filePath Path to the file to write too.
	 * @param s String containing the data to be written.
	 */
	public static void openWriteFile(Path filePath, String s){
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(filePath, SynchronizeRoot.ENCODING);
			writer.write(s, 0, s.length());
			writer.close();
		} catch (IOException e) {
			Controller.getInstance().getLauncher().updatePane("Could not write to " + filePath.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String openEclipseGetContent(String path){
		IEditorPart p;
		if( (p = isOpenInEclipse(path)) != null){
			IDocumentProvider provider = ((ITextEditor)p).getDocumentProvider();
			IDocument document = provider.getDocument(p.getEditorInput());
			return document.get();
		} else{
			return null;
		}
	}
	
	/**
	 * Tries to open up the path in Eclipse and patch the document with the given data.
	 * 
	 * @param path to be checked if open.
	 * @param data to overwrite the file's content.
	 */
	public static void openEclipseWriteContent(String path, String data, LinkedList<Diff> diffList){
		IEditorPart p;
		if( (p = isOpenInEclipse(path)) != null){
			IDocumentProvider provider = ((ITextEditor)p).getDocumentProvider();
			IDocument document = provider.getDocument(p.getEditorInput());
			offset = 0;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					// Get cursor location
					ISelection selection = ((ITextEditor)p).getSelectionProvider().getSelection();
					if(selection instanceof ITextSelection){
						ITextSelection textSelection = (ITextSelection)selection;
						offset = textSelection.getOffset();
					}
				}
			});
			
			// Calculate new cursor location
			int location = 0;
			int i = 0;
			// As long as location is less than offset we're interested in the diffs
			// The latter argument is for the borderline case when an insertion or deletion happen exactly after where the cursor is placed.
			while( i < diffList.size() && ((location < offset) || (location == offset && diffList.get(i).operation == Operation.INSERT)) ){
				Diff d = diffList.get(i);
				Operation op = d.operation;
				// Equal, move location forward
				if(op == Operation.EQUAL){
					location += d.text.length();
				}
				// Delete, move offset back. Since the delete is before the location no need to move its point backwards.
				else if(op == Operation.DELETE){
					offset -= d.text.length();
				}
				// Insert, move both location and offset forward.
				else{
					location += d.text.length();
					offset += d.text.length();
				}
				i++;
			}
			
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					document.set(data);
					
					// Set cursor location back to where it was
					((ITextEditor)p).selectAndReveal(offset, 0);
				}
			});
		}
	}
	
	
	/**
	 * Checks if the given path is open in Eclipse and if so returns the editor for the path.
	 * 
	 * @param path to be checked if open.
	 * @return the path's editor if open, otherwise null.
	 */
	private static IEditorPart isOpenInEclipse(String path){
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				ii = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
			}
		});
		
		for(IEditorReference ier : ii){
			// Fetch the editor's page
			try {
				IEditorInput iei = ier.getEditorInput();
				if(iei instanceof IURIEditorInput){
					File file = new File(((IURIEditorInput)iei).getURI());
					IEditorPart p;
					
					// If the editor got the same path and is a text editor, return its editor
			    	if(path.contains(file.getAbsolutePath()) && 
			    			(p = ier.getEditor(false)) instanceof ITextEditor){
						return p;
			    	}
				}
				/*
				if(iei instanceof FileEditorInput){
					IFile file = ((FileEditorInput)iei).getFile();
					IEditorPart p;
					// If the editor got the same path and is a text editor, return its data
			    	if(path.contains(file.getRawLocation().toOSString()) && 
			    			(p = ier.getEditor(false)) instanceof ITextEditor){
						return p;
			    	}
				}
				else if(iei instanceof FileStoreEditorInput){
					System.out.println(((FileStoreEditorInput)iei).getURI());
					// URI uri = ((FileStoreEditorInput)iei).getURI();
					// IFileStore location = EFS.getLocalFileSystem().getStore(uri);
					// File file = location.toLocalFile(EFS.NONE, null); 
				}*/
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static void refreashEclipse(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject ip: projects){
			try {
				ip.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}