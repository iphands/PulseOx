package org.ahands.ian.pulseox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

	Display display = null;
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
	GC gc;
	Rectangle canvasRect;

	int[] coord = new int[] { 0, 0 };

	boolean keepRunning = true;

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
		this.gc = gc;
		this.canvas = canvas;
		this.canvasRect = canvasRect;
		return;
	}

	public void endThread() {
		keepRunning = false;
		return;
	}

	public int[] getPoint() {
		return coord;
	}

	public void run() {
		final String DEVICE = "/dev/ttyUSB0";

		BufferedReader deviceReader = null;
		while (deviceReader == null && !shell.isDisposed()) {
			try {
				deviceReader = new BufferedReader(new FileReader(new File(
						DEVICE)));

				Thread.sleep(250);
			} catch (Exception e) {
				try {
					deviceReader.close();
				} catch (IOException e1) {

				}
			}
		}

		int x = 0;
		int old_y = 0;

		System.out.println("BPM, o2");
		while (!shell.isDisposed()) {

			try {
				if ((controlInt = deviceReader.read()) > 127) {
					old_y = waveY;
					waveY = deviceReader.read();
					waveX = deviceReader.read();
					heartRate = deviceReader.read();
					oxygenSat = deviceReader.read();

					System.out.println(heartRate + "," + oxygenSat);

					if (x >= 200) {
						x = 0;
						gc.setForeground(new Color(display, 0, 0, 0));
						gc.drawLine(0, 0, 0, 127);
						gc.drawLine(1, 0, 1, 127);
						gc.setForeground(new Color(display, 0, 255, 0));
						// gc.fillRectangle(canvasRect);
					} else {
						// gc.drawPoint(x, waveY);

						if (x > 0) {
							gc.setForeground(new Color(display, 0, 0, 0));
							gc.drawLine(x + 1, 0, x + 1, 127);
							gc.setForeground(new Color(display, 0, 255, 0));
						}

						try {
							gc.drawLine(x, waveY, x - 1, old_y);
						} catch (Exception e) {
							// TODO: handle exception
						}
						// gc.drawPoint(x, waveY);
						x++;
					}

					if (heartRate < 40) {
						heartRate += 127;
					}

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							heartBPMLabel.setText(heartRate + "");
							if (heartRate < 40) {
								heartBPMLabel.setBackground(new Color(display,
										255, 0, 0));
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
					deviceReader.close();
				}

			} catch (IOException e) {

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
