package org.ahands.ian.pulseox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class testGraph implements Listener {
	Shell parent;
	final int XSCALE = 200;
	final int YSCALE = 127;
	Display display = Display.getCurrent();
	final Color BLACK = display.getSystemColor(SWT.COLOR_BLACK);
	final Color GREEN = display.getSystemColor(SWT.COLOR_GREEN);
	final Color RED = display.getSystemColor(SWT.COLOR_RED);

	public testGraph(Shell shell) {
		this.parent = shell;
	}

	@Override
	public void handleEvent(Event arg0) {

		final Shell shell = new Shell(parent, SWT.SHELL_TRIM);
		shell.setText("Logging...");

		shell.pack();
		shell.open();

		final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.spacing = 8;
		fillLayout.marginHeight = 8;
		fillLayout.marginWidth = 8;

		shell.setLayout(fillLayout);

		final Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.setBackground(new Color(Display.getCurrent(), 0, 0, 0));

		final GC waveFormGc = new GC(canvas);
		waveFormGc.setForeground(new Color(Display.getCurrent(), 0, 255, 0));

		class UpdateGC implements Runnable {

			int x, y, old_x, old_y = 0;

			@Override
			public void run() {

				while (true) {

					x = FileListener.x;
					y = FileListener.y;
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							float h = canvas.getBounds().height;
							float w = canvas.getBounds().width;

							if (w > XSCALE)
								x = (int) (x * (w / XSCALE));

							if (h > YSCALE)
								y = (int) (y * (h / YSCALE));

							if (x > old_x) {
								waveFormGc.setForeground(BLACK);
								for (int i = 0, ix = (int) ((w / XSCALE) + 1); i < ix; i++) {
									waveFormGc.drawLine(x + i, 0, x + i,
											(int) h);
								}
								waveFormGc.setForeground(GREEN);

								waveFormGc.drawLine(x, y, old_x, old_y);
							} else {
								System.out.println(w + "," + h);
								System.out.println("(" + old_x + ", " + old_y
										+ ")");
							}

						}
					});

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					old_x = x;
					old_y = y;
				}
			}
		}

		Thread thread = new Thread(new UpdateGC());
		thread.start();

		shell.pack();
		Display display = shell.getDisplay();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
