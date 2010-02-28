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
	final Color WHITE = display.getSystemColor(SWT.COLOR_WHITE);
	int timeout = 22;
	int avgTicks = 0;
	Canvas canvas;
	boolean doMarker = false;

	float low_percentage = 0;
	float med_percentage = 0;

	public abstract int getYValue();

	public void setPercentages(float low_percentage, float med_percentage) {
		this.low_percentage = low_percentage;
		this.med_percentage = med_percentage;
	}

	public void setYMin(int y_min) {
		y_max = ORIG_Y_MAX - y_min;
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

			int x, y, old_x_point, old_y_point = 0;

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

							final float y_percent = ((float) (y - (ORIG_Y_MAX - y_max)) / y_max);
							final float x_percent = ((float) x / x_max);

							final float canvas_height = canvas.getBounds().height;
							final float canvas_width = canvas.getBounds().width;

							final int y_point = ((int) canvas_height)
									- ((int) (canvas_height * y_percent));
							final int x_point = (int) (canvas_width * x_percent);

							final int scaled_unit = (int) ((canvas_width / x_max) + 1);

							waveFormGc.setForeground(BLACK);
							for (int i = 0; i < scaled_unit; i++) {
								waveFormGc.drawLine(x_point + i, 0,
										x_point + i, (int) canvas_height);
							}

							if (x_point > old_x_point) {

								if ((y_percent >= low_percentage)
										&& (y_percent < med_percentage)) {
									waveFormGc.setForeground(ORANGE);
								} else if (y_percent < low_percentage) {
									waveFormGc.setForeground(RED);
								} else {
									waveFormGc.setForeground(GREEN);
								}

								waveFormGc.setLineWidth(scaled_unit);
								waveFormGc.drawLine(x_point, y_point,
										old_x_point, old_y_point);

								if (doMarker) {
									waveFormGc.setBackground(WHITE);
									waveFormGc.fillRoundRectangle(x_point + 1,
											y_point - 5, 10, 10, 10, 10);

									waveFormGc.setBackground(BLACK);
								}
							}

							old_x_point = x_point;
							old_y_point = y_point;
						}
					});
				}
			}
		}

		Thread thread = new Thread(new UpdateGC());
		thread.start();
	}
}
