package org.ahands.ian.pulseox;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

public class TrayUpdater implements Runnable {

	Shell shell;
	FileListener fileListener;
	Display display;
	GC trayGc;
	Image image;
	TrayItem trayItem;

	public TrayUpdater(Display display, Shell shell, FileListener fileListener,
			Image image, TrayItem trayItem, GC trayGc) {
		this.display = display;
		this.shell = shell;
		this.fileListener = fileListener;
		this.image = image;
		this.trayItem = trayItem;
		this.trayGc = trayGc;
	}

	public void run() {

		final Color ORANGE = new Color(display, 255, 170, 0);
		final Color GREEN = new Color(display, 0, 255, 0);
		final Color RED = new Color(display, 255, 0, 0);

		while (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {

					int oxygenSat = fileListener.getOxygenSat();

					if (oxygenSat < 0) {
						oxygenSat = 0;

					} else {

						final String oxygenSatStr = "" + oxygenSat;

						if (oxygenSat > 93) {
							trayGc.setBackground(GREEN);
						} else if (oxygenSat > 90) {
							trayGc.setBackground(ORANGE);
						} else {
							trayGc.setBackground(RED);
						}

						trayGc.fillRectangle(image.getBounds());
						trayGc.drawText(oxygenSatStr, 3, 3);
						trayItem.setImage(image);
					}
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
		}
	}
}
