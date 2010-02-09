package org.ahands.ian.pulseox;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TrayUpdater implements Runnable {

	Shell shell;
	FileListener fileListener;
	Display display;
	GC trayGc;
	Image image;

	public TrayUpdater(Display display, Shell shell, FileListener fileListener,
			Image image, GC trayGc) {
		this.display = display;
		this.shell = shell;
		this.fileListener = fileListener;
		this.image = image;
		this.trayGc = trayGc;
	}

	public void run() {

		while (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {

					trayGc = new GC(image);
					trayGc.drawText("" + fileListener.getOxygenSat(), 3, 0);
					trayGc.dispose();
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}
	}
}
