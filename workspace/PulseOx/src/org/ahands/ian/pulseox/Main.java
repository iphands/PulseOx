package org.ahands.ian.pulseox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {

		System.out.println("Test");

		final String DEVICE = "/dev/ttyUSB0";

		BufferedReader deviceReader = new BufferedReader(new FileReader(
				new File(DEVICE)));

		int controlInt = 134;
		int waveY;
		int waveX;
		int heartRate;
		int oxygenSat;

		while (true) {
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

				Thread.sleep(200);
			} else {
				System.out.println("debug: waiting for control character ("
						+ controlInt + ")\n\n");
			}
		}
	}
}
