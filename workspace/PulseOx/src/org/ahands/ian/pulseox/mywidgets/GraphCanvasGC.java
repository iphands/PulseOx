package org.ahands.ian.pulseox.mywidgets;

import org.ahands.ian.pulseox.FileListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public abstract class GraphCanvasGC {

	Composite parent;
	int X_MAX = 200;
	int Y_MAX = 127;
	Display display = Display.getCurrent();
	final Color BLACK = display.getSystemColor(SWT.COLOR_BLACK);
	final Color GREEN = display.getSystemColor(SWT.COLOR_GREEN);
	final Color RED = display.getSystemColor(SWT.COLOR_RED);
	int timeout = 25;
	Canvas canvas;

	public abstract int getYValue();

	public GraphCanvasGC(Composite parent) {
		this.parent = parent;
		addWidget();
		return;
	}

	public void setSize(int width, int height) {
		canvas.setSize(width, height);
		return;
	}

	public void setSize(Point size) {
		canvas.setSize(size);
		return;
	}

	private void addWidget() {
		canvas = new Canvas(parent, SWT.NONE);
		canvas.setSize(X_MAX, Y_MAX);
		canvas.setBackground(new Color(Display.getCurrent(), 0, 0, 0));

		final GC waveFormGc = new GC(canvas);
		waveFormGc.setForeground(new Color(Display.getCurrent(), 0, 255, 0));

		class UpdateGC implements Runnable {

			int x, y, old_x, old_y = 0;

			@Override
			public void run() {

				while (true) {

					if (x >= X_MAX) {
						x = 0;
					} else {
						x++;
					}

					y = getYValue();

					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							float h = canvas.getBounds().height;
							float w = canvas.getBounds().width;
							int scaled_x = x;
							int scaled_y = y;

							boolean overSized = false;

							if (w > X_MAX) {
								scaled_x = (int) (x * (w / X_MAX));
								overSized = true;
							}

							if (h > Y_MAX) {
								scaled_y = (int) (y * (h / Y_MAX));
								overSized = true;
							}

							waveFormGc.setForeground(BLACK);

							if (overSized) {
								for (int i = 0, ix = ((int) ((w / X_MAX)) * 5); i < ix; i++) {
									waveFormGc.drawLine(scaled_x + i, 0,
											scaled_x + i, (int) h);
								}
							} else {
								waveFormGc.drawLine(scaled_x, 0, scaled_x,
										(int) h);
								waveFormGc.drawLine(scaled_x + 1, 0,
										scaled_x + 1, (int) h);
							}

							waveFormGc.setForeground(GREEN);

							if (scaled_x > old_x && scaled_x < w) {
								waveFormGc.drawLine(scaled_x, scaled_y, old_x,
										old_y);
							}
							old_x = scaled_x;
							old_y = scaled_y;
						}
					});

					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Thread thread = new Thread(new UpdateGC());
		thread.start();
	}
}
