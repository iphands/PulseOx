package org.ahands.ian.pulseox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TestGUI {

	Label heartBPMLabel;
	Label oxySatLabel;
	Group heartRateGroup;
	Group oxygenSatGroup;

	public TestGUI(Display display) {

		Shell shell = new Shell(display);
		shell.setText("PulseOx");
		shell.setSize(320, 240);

		center(shell);
		initUI(shell);

		shell.open();

		Thread guiUpdate = new Thread(new FileListener(display, shell,
				heartBPMLabel, oxySatLabel, oxygenSatGroup, heartRateGroup));

		guiUpdate.start();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void initUI(Shell shell) {

		RowLayout vertRowLayout = new RowLayout(SWT.VERTICAL);
		vertRowLayout.marginTop = 10;
		vertRowLayout.marginBottom = 10;
		vertRowLayout.marginLeft = 5;
		vertRowLayout.marginRight = 5;
		vertRowLayout.spacing = 10;
		shell.setLayout(vertRowLayout);

		Composite topComp = new Composite(shell, SWT.BORDER);
		topComp.setLayout(new RowLayout(SWT.HORIZONTAL));

		heartRateGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN);
		heartRateGroup.setText("Hear Rate");

		heartBPMLabel = new Label(heartRateGroup, SWT.CENTER);
		heartBPMLabel.setText("--");
		heartBPMLabel.setBounds(20, 20, 20, 20);

		oxygenSatGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN);
		oxygenSatGroup.setText("Oxygen Saturation");

		oxySatLabel = new Label(oxygenSatGroup, SWT.CENTER);
		oxySatLabel.setText("--");
		oxySatLabel.setBounds(20, 20, 20, 20);

		Composite bottomComp = new Composite(shell, SWT.BORDER);
		bottomComp.setLayout(new RowLayout(SWT.FILL));

		Group waveFormGroup = new Group(bottomComp, SWT.SHADOW_ETCHED_IN);
		waveFormGroup.setText("Wave Form");
	}

	public void center(Shell shell) {

		Rectangle bds = shell.getDisplay().getBounds();

		Point p = shell.getSize();

		int nLeft = (bds.width - p.x) / 4;
		int nTop = (bds.height - p.y) / 2;

		shell.setBounds(nLeft, nTop, p.x, p.y);
	}

	public static void main(String[] args) {
		Display display = new Display();
		new TestGUI(display);
		display.dispose();
	}

}
