package org.ahands.ian.pulseox;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TitleUpdater implements Runnable {

	Shell shell;
	FileListener fileListener;
	Display display;

	public TitleUpdater(Display display, Shell shell, FileListener fileListener) {
		this.display = display;
		this.shell = shell;
		this.fileListener = fileListener;
	}

	public void run() {

		while (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					shell.setText("PulseOx -- " + fileListener.getOxygenSat());
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}
	}
}
