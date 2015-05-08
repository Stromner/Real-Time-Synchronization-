package sigmatechnology.se.realtime_file_synchronisation.plugin.actions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import sigmatechnology.se.realtime_file_synchronisation.diff_match_patch.SynchronizeRoot;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		new Thread(){
			public void run(){
				String sRepo1 = "C:/Users/David/Desktop/Test/src/sigmatechnology/se/realtime_file_synchronisation/TestRepo1/",
						sRepo2 = "C:/Users/David/Desktop/Test/src/sigmatechnology/se/realtime_file_synchronisation/TestRepo2/";
				Path repo1 = Paths.get(sRepo1);
				Path repo2 = Paths.get(sRepo2);
				
				SynchronizeRoot sync1 = new SynchronizeRoot(repo1, null);
				SynchronizeRoot sync2 = new SynchronizeRoot(repo2, null);
				Scanner s = new Scanner(System.in);
				System.out.println("Press any button + 'enter' to sync");
				while(true){
					s.nextLine();
					System.out.println("New diff cycle:");
					sync2.applyDiffs(sync1.getDiffs());
				}
			}
		}.start();
	}
	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}