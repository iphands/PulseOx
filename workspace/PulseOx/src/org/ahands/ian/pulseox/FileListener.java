package org.ahands.ian.pulseox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class FileListener implements Runnable {

	Display display = Display.getCurrent();
	Shell shell = null;
	Label heartBPMLabel = null;
	Label oxySatLabel = null;
	Group heartRateGroup = null;
	Group oxygenSatGroup = null;

	int controlInt = 134;
	int waveY = 0;
	int waveX;
	int heartRate;
	int oxygenSat;
	Canvas canvas;
	GC waveFormGc;

	Rectangle canvasRect;

	int[] coord = new int[] { 0, 0 };

	int x;
	int y;
	int old_y;

	final Color BLACK = display.getSystemColor(SWT.COLOR_BLACK);
	final Color GREEN = display.getSystemColor(SWT.COLOR_GREEN);
	final Color RED = display.getSystemColor(SWT.COLOR_RED);

	// boolean keepRunning = true;

	public FileListener(Display display, Shell shell, Label heartBPMLabel,
			Label oxySatLabel, Group oxygenSatGroup, Group heartRateGroup) {

		this.display = display;
		this.shell = shell;
		this.heartBPMLabel = heartBPMLabel;
		this.oxySatLabel = oxySatLabel;
		this.oxygenSatGroup = oxygenSatGroup;
		this.heartRateGroup = heartRateGroup;
	}

	public void setGC(GC gc, Canvas canvas, Rectangle canvasRect) {
		this.waveFormGc = gc;
		this.canvas = canvas;
		this.canvasRect = canvasRect;
		return;
	}

	public int[] getPoint() {
		return coord;
	}

	public synchronized int getOxygenSat() {
		return oxygenSat;
	}

	public void run() {
		final String DEVICE = "/dev/ttyUSB0";

		BufferedReader deviceReader = null;
		while (deviceReader == null && !shell.isDisposed()) {
			try {
				deviceReader = new BufferedReader(new FileReader(new File(
						DEVICE)));

			} catch (IOException e) {

				System.out.println("Unable to open " + DEVICE
						+ " trying again...");
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
		}

		x = 0;
		y = 0;
		old_y = 0;
		final int Y_MAX = 127;
		final int X_MAX = 200;
		int counter = 0;
		int oldHeartRate = 0;
		int oldOxygenSat = 0;

		final DateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss:S");

		System.out.println("BPM, o2");
		while (!shell.isDisposed()) {
			try {
				if (counter >= 400) {
					deviceReader.close();
					deviceReader = new BufferedReader(new FileReader(new File(
							DEVICE)));
					counter = 0;
					// System.out.println("debug: flush test");
				} else {
					counter++;
				}

				if ((controlInt = deviceReader.read()) > 128) {

					old_y = y;
					waveY = deviceReader.read();
					y = Y_MAX - waveY;
					waveX = deviceReader.read();
					heartRate = deviceReader.read();
					oxygenSat = deviceReader.read();

					if (heartRate > 127 || oxygenSat > 127) {
						continue;
					}

					if ((oldHeartRate != heartRate)
							|| (oldOxygenSat != oxygenSat)) {

						System.out.println(dateFormat.format(new Date()) + ","
								+ heartRate + "," + oxygenSat);
					}

					if (heartRate < 40) {
						heartRate += 127;
					}

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {

							if (x >= X_MAX) {
								x = 0;
								waveFormGc.setForeground(BLACK);
								waveFormGc.drawLine(0, 0, 0, Y_MAX);
								waveFormGc.drawLine(1, 0, 1, Y_MAX);
								waveFormGc.setForeground(GREEN);
								// gc.fillRectangle(canvasRect);
							} else {

								if (x > 0) {
									waveFormGc.setForeground(BLACK);
									waveFormGc.drawLine(x + 1, 0, x + 1, Y_MAX);
									waveFormGc.setForeground(GREEN);
								}

								waveFormGc.setForeground(GREEN);
								waveFormGc.drawLine(x, y, x - 1, old_y);

								waveFormGc.setForeground(RED);
								waveFormGc.drawPoint(x, y);

								x++;
							}

							heartBPMLabel.setText(heartRate + "");
							if (heartRate < 40) {
								heartBPMLabel.setBackground(RED);
							} else {
								heartBPMLabel.setBackground(shell
										.getBackground());
							}

							Color color;
							oxySatLabel.setText(oxygenSat + "");
							if (oxygenSat < 93) {

								if (oxygenSat > 90) {
									color = new Color(display, 255, 150, 0);
								} else {
									color = new Color(display, 255, 0, 0);
								}

							} else {
								color = shell.getBackground();
							}
							oxygenSatGroup.setBackground(color);
							oxySatLabel.setBackground(color);

						}
					});

					Thread.sleep(20);

				} else {
					System.out.println("debug: waiting for control character ("
							+ controlInt + ")");
					Thread.sleep(250);
				}

			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
		}

		if (deviceReader != null) {
			try {
				deviceReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
