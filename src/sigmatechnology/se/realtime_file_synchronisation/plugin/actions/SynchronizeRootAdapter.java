package sigmatechnology.se.realtime_file_synchronisation.plugin.actions;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SynchronizeRootAdapter extends SelectionAdapter{
	private Boolean connected;
	/**
	 * 
	 */
	public SynchronizeRootAdapter(){
		connected = false;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		connected = true;
		System.out.println("Connect button");
	}
}
