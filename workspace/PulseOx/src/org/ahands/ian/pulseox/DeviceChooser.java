package org.ahands.ian.pulseox;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DeviceChooser {

	String device = "/dev/ttyUSB0";

	public static String[] getDevList() {
		File[] children = new File("/dev/").listFiles();
		// java.util.List<File> fileList = new ArrayList<File>();
		java.util.List<String> fileStrList = new ArrayList<String>();

		for (File file : children) {
			final String fileStr = file.toString();
			if (fileStr.contains("ttyUSB")) {
				// fileList.add(file);
				fileStrList.add(fileStr);
			}
		}

		return (String[]) fileStrList.toArray(new String[0]);
	}

	public String doGui(Display display) {

		final Shell shell = new Shell(display);
		shell.setLayout(new RowLayout(SWT.VERTICAL));

		Composite composite = new Composite(shell, SWT.NONE | SWT.FILL);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		final List list = new List(composite, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);

		final String[] fileStrArray = this.getDevList();

		for (String deviceFileStr : fileStrArray) {
			list.add(deviceFileStr);
		}

		list.setBounds(0, 0, 100, 200);

		Button okayButton = new Button(composite, SWT.PUSH);
		okayButton.setText("Okay");
		okayButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				device = fileStrArray[list.getSelectionIndex()];
				System.out.println(device);
				shell.dispose();
			}
		});

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
		return device;
	}

	// public static void main(String[] args) {
	// Display display = new Display();
	// new DeviceChooser(display);
	// display.dispose();
	// }

}
