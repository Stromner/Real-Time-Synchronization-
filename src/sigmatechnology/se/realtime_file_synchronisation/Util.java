package sigmatechnology.se.realtime_file_synchronisation;

/**
 * Utility class for misc methods.
 * 
 * @author David Strömner
 * @version 2015-05-08
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;

public class Util {
	static private IEditorReference[] ii;
	
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
			// TODO Print to console
			System.out.println("Could not write to " + filePath.toString());
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
			// TODO Print to console
			System.out.println("Could not write to " + filePath.toString());
			e.printStackTrace();
		}
	}
	
	public static String getEclipseOpenFileContent(String path){
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
	public static void openEclipseWriteContent(String path, String data){
		IEditorPart p;
		if( (p = isOpenInEclipse(path)) != null){
			IDocumentProvider provider = ((ITextEditor)p).getDocumentProvider();
			IDocument document = provider.getDocument(p.getEditorInput());
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					document.set(data);
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
            IEditorInput iei;
			try {
				iei = ier.getEditorInput();
				
				if(iei instanceof IFileEditorInput){
					IFile file = ((IFileEditorInput)iei).getFile();
					IEditorPart p;
					// If the editor got the same path and is an text editor, return its data
			    	if(path.contains(file.getRawLocation().toOSString()) && 
			    			(p = ier.getEditor(false)) instanceof ITextEditor){
						return p;
			    	}
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
