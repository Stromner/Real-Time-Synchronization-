package sigmatechnology.se.realtime_file_synchronisation.plugin.views;


import javax.inject.Inject;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import sigmatechnology.se.realtime_file_synchronisation.plugin.actions.SynchronizeRootAdapter;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class View extends ViewPart {
	public static final String ID = "sigmatechnology.se.realtime_file_synchronisation.plugin.views.View";
	private TableViewer viewer;
	private Button connectButton;
	@Inject Shell shell;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return new String[]{};
		}
	}

	/**
	 * The constructor.
	 */
	public View(){
		
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		//viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(getViewSite());
		
		createButtons(parent);
		
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "Realtime_File_Synchronisation.viewer");
	}
	
	/**
	 * 
	 * @param parent
	 */
	private void createButtons(Composite parent){
		connectButton = new Button((Composite) parent, SWT.PUSH);
		connectButton.addSelectionListener(new SynchronizeRootAdapter());
		connectButton.setText("Connect");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}