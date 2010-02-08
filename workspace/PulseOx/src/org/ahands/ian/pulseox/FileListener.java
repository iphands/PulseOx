package org.ahands.ian.pulseox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.swt.graphics.Color;
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
	int waveY;
	int waveX;
	int heartRate;
	int oxygenSat;

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

	public void endThread() {
		keepRunning = false;
		return;
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

			}
		}

		while (!shell.isDisposed()) {
			try {
				if ((controlInt = deviceReader.read()) > 127) {
					waveY = deviceReader.read();
					waveX = deviceReader.read();
					heartRate = deviceReader.read();
					oxygenSat = deviceReader.read();

					System.out.println("contr: " + controlInt);
					System.out.println("wavex: " + waveY);
					System.out.println("wavey: " + waveX);
					System.out.println("pulse: " + heartRate);
					System.out.println("oxy  : " + oxygenSat + "\n");

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
