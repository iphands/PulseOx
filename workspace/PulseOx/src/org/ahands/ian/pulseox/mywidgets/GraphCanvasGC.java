package org.ahands.ian.pulseox.mywidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public abstract class GraphCanvasGC {

	Composite parent;
	int x_max = 200;
	// int y_min = 0;
	int ORIG_Y_MAX = 127;
	int y_max = ORIG_Y_MAX;
	Display display = Display.getCurrent();
	final Color BLACK = display.getSystemColor(SWT.COLOR_BLACK);
	final Color GREEN = display.getSystemColor(SWT.COLOR_GREEN);
	final Color RED = display.getSystemColor(SWT.COLOR_RED);
	final Color ORANGE = new Color(display, 255, 150, 0);
	final Color BLUE = display.getSystemColor(SWT.COLOR_WHITE);
	int timeout = 22;
	int avgTicks = 0;
	Canvas canvas;
	boolean doMarker = false;

	public abstract int getYValue();

	public void setYMin(int y_min) {
		// this.y_min = y_min;
		// y_max -= ORIG_Y_MAX - y_min;
	}

	public void setDoMarker(boolean doMarker) {
		this.doMarker = doMarker;
	}

	public GraphCanvasGC(Composite parent) {
		this.parent = parent;
		addWidget();
		return;
	}

	public synchronized void setTimeout(int timeout) {
		this.timeout = timeout;
		return;
	}

	public synchronized void setAvgTicks(int avgTicks) {
		this.avgTicks = avgTicks;
		return;
	}

	public GraphCanvasGC(Composite parent, int width, int height) {
		this.parent = parent;
		this.x_max = width;
		this.y_max = height;
		this.ORIG_Y_MAX = height;
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
		canvas.setSize(x_max, y_max);
		canvas.setBackground(new Color(Display.getCurrent(), 0, 0, 0));

		final GC waveFormGc = new GC(canvas);
		waveFormGc.setForeground(new Color(Display.getCurrent(), 0, 255, 0));

		class UpdateGC implements Runnable {

			int x, y, old_x, old_y = 0;

			@Override
			public void run() {

				List<Integer> yDataList = new ArrayList<Integer>();

				while (true) {

					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					y = getYValue();

					if (avgTicks > 0) {
						yDataList.add(y);

						if (yDataList.size() > avgTicks) {

							while (yDataList.size() > avgTicks) {
								yDataList.remove(0);
							}

							for (int tmp_y : yDataList) {
								// System.err.println(tmp_y);
								y += tmp_y;
							}

							// System.err.println(y + " - " + avgTicks);
							y = y / (avgTicks + 1);
							// System.err.println("ans: " + y);
							yDataList.clear();
						} else {
							continue;
						}
					}

					if (x >= x_max) {
						x = 0;
					} else {
						x++;
					}

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {

							System.out.println("ymax: " + y_max + ", "
									+ (int) (((float) y / y_max) * 100));

							final int ORIG_Y = y;

							// flip y
							y = y_max - y;

							float h = canvas.getBounds().height;
							float w = canvas.getBounds().width;
							int scaled_x = x;
							int scaled_y = y;

							boolean overSized = false;

							if (w > x_max) {
								scaled_x = (int) (x * (w / x_max));
								overSized = true;
							}

							if (h > y_max) {
								scaled_y = (int) (y * (h / y_max));
								overSized = true;
							}

							waveFormGc.setForeground(BLACK);
							if (overSized) {
								for (int i = 0, ix = ((int) ((w / x_max)) * 5) + 10; i < ix; i++) {
									waveFormGc.drawLine(scaled_x + i, 0,
											scaled_x + i, (int) h);
								}
							} else {
								waveFormGc.drawLine(scaled_x, 0, scaled_x,
										(int) h);
								waveFormGc.drawLine(scaled_x + 1, 0,
										scaled_x + 1, (int) h);
							}

							if (scaled_x > old_x && scaled_x < w) {

								waveFormGc
										.setLineWidth((int) ((h / y_max) * 2));

								if (doMarker) {
									waveFormGc.setBackground(BLACK);
									waveFormGc.fillRoundRectangle(old_x,
											old_y - 5, 10, 10, 10, 10);
									waveFormGc.setBackground(BLUE);
									waveFormGc.fillRoundRectangle(scaled_x,
											scaled_y - 5, 10, 10, 10, 10);
									waveFormGc.setBackground(BLACK);
								}

								if ((ORIG_Y >= 90) && (ORIG_Y < 93)) {
									waveFormGc.setForeground(ORANGE);
								} else if (ORIG_Y < 90) {
									waveFormGc.setForeground(RED);
								} else {
									waveFormGc.setForeground(GREEN);
								}

								// if (y_min > 0) {
								// System.out.println("y: " + scaled_y + ", "
								// + y + " -- " + y_max);
								// }

								waveFormGc.drawLine(scaled_x, scaled_y, old_x,
										old_y);
							}
							old_x = scaled_x;
							old_y = scaled_y;
						}
					});
				}
			}
		}

		Thread thread = new Thread(new UpdateGC());
		thread.start();
	}
}
