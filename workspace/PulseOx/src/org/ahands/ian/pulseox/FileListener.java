package org.ahands.ian.pulseox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class FileListener implements Runnable {

	Logger logger = Logger.getLogger(FileListener.class);

	Display display = Display.getCurrent();
	Shell shell = null;
	Label heartBPMLabel = null;
	Label oxygenSatLabel = null;
	Group heartRateGroup = null;
	Group oxygenSatGroup = null;

	String DEVICE = "/dev/ttyUSB0";
	int controlInt = 134;
	int waveY = 0;
	int waveX;
	static int heartRate;
	static int oxygenSat;
	Canvas canvas;
	// GC waveFormGc;

	Rectangle canvasRect;

	int counter = 0;

	static int x;
	static int y;
	int old_y;

	final Color BLACK = display.getSystemColor(SWT.COLOR_BLACK);
	final Color GREEN = display.getSystemColor(SWT.COLOR_GREEN);
	final Color RED = display.getSystemColor(SWT.COLOR_RED);

	// boolean keepRunning = true;

	public FileListener(String device, Display display, Shell shell,
			Label heartBPMLabel, Label oxySatLabel, Group oxygenSatGroup,
			Group heartRateGroup) {

		this.DEVICE = device;
		this.display = display;
		this.shell = shell;
		this.heartBPMLabel = heartBPMLabel;
		this.oxygenSatLabel = oxySatLabel;
		this.oxygenSatGroup = oxygenSatGroup;
		this.heartRateGroup = heartRateGroup;
	}

	public void setDevice(String device) {
		this.DEVICE = device;
		this.counter = 500;
		return;
	}

	public void setGC(GC gc, Canvas canvas, Rectangle canvasRect) {
		// this.waveFormGc = gc;
		this.canvas = canvas;
		this.canvasRect = canvasRect;
		return;
	}

	public synchronized static int getWaveYValue() {
		return y;
	}

	public synchronized static int getOxygenSat() {
		return oxygenSat;
	}

	public synchronized static int getHeartRate() {
		return heartRate;
	}

	public synchronized void blankLabels() {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				oxygenSatLabel.setText("--");
				heartBPMLabel.setText("--");

				for (Control comp : oxygenSatGroup.getParent().getChildren()) {
					comp.setBackground(shell.getBackground());
				}

				for (Control comp : oxygenSatGroup.getChildren()) {
					comp.setBackground(shell.getBackground());
				}
			}
		});
	}

	public void run() {

		BufferedReader deviceReader = null;
		while (deviceReader == null && !shell.isDisposed()) {
			try {
				deviceReader = new BufferedReader(new FileReader(new File(
						DEVICE)));

			} catch (IOException e) {

				logger.debug("Unable to open " + DEVICE + " trying again...");
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
		int oldHeartRate = 0;
		int oldOxygenSat = 0;
		int failCount = 0;

		final DateFormat dateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss.S");

		logger.info("BPM, o2");
		while (!shell.isDisposed()) {
			try {
				if (counter >= 400) {
					logger.debug("flushing buffer");
					while (deviceReader.ready()) {
						deviceReader.read();
					}
					counter = 0;
				} else {
					counter++;
				}

				if ((controlInt = deviceReader.read()) > 128) {

					old_y = y;
					waveY = deviceReader.read();
					y = Y_MAX - waveY;
					y = waveY;
					waveX = deviceReader.read();
					final int tmp_heartRate = deviceReader.read();
					if (tmp_heartRate < 40) {
						heartRate = tmp_heartRate + 127;
					} else {
						heartRate = tmp_heartRate;
					}

					oxygenSat = deviceReader.read();

					if (heartRate > 127 || oxygenSat > 127 || heartRate <= 0
							|| oxygenSat <= 0) {

						failCount++;

						if (failCount >= 10) {
							blankLabels();
						}
						continue;
					} else {
						failCount = 0;
					}

					if ((oldHeartRate != heartRate)
							|| (oldOxygenSat != oxygenSat)) {

						logger.info(dateFormat.format(new Date()) + ","
								+ heartRate + "," + oxygenSat);

					}

					oldHeartRate = heartRate;
					oldOxygenSat = oxygenSat;

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {

							if (x >= X_MAX) {
								x = 0;
							} else {
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
							oxygenSatLabel.setText(oxygenSat + "");
							if (oxygenSat < 93) {

								if (oxygenSat > 90) {
									color = new Color(display, 255, 150, 0);
								} else {
									color = new Color(display, 255, 0, 0);
								}

							} else {
								color = shell.getBackground();
							}
							oxygenSatLabel.getParent().setBackground(color);

							for (Control comp : oxygenSatLabel.getParent()
									.getChildren()) {
								comp.setBackground(color);
							}

						}
					});

					Thread.sleep(20);

				} else {

					deviceReader.close();
					deviceReader = new BufferedReader(new FileReader(new File(
							DEVICE)));

					blankLabels();
					logger.debug("waiting for control character (" + controlInt
							+ ")");
					Thread.sleep(100);
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
