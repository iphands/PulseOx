package org.ahands.ian.pulseox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
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

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("PulseOx");
		shell.setSize(320, 240);

		center(shell);
		initUI(shell);

		shell.open();

		FileListener fileListener = new FileListener(display, shell,
				heartBPMLabel, oxySatLabel, oxygenSatGroup, heartRateGroup);
		Thread guiUpdate = new Thread(fileListener);

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

		Composite topComp = new Composite(shell, SWT.BORDER | SWT.FILL);
		topComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		Font initialFont = shell.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData fontD : fontData) {
			fontD.setHeight(30);
		}

		Font bigFont = new Font(Display.getCurrent(), fontData);

		heartRateGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN | SWT.FILL);
		heartRateGroup.setText("Heart Rate");

		heartBPMLabel = new Label(heartRateGroup, SWT.CENTER);
		heartBPMLabel.setFont(bigFont);
		heartBPMLabel.setText("--");
		heartBPMLabel.setBounds(20, 20, 80, 40);

		oxygenSatGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN);
		oxygenSatGroup.setText("Oxygen Saturation");

		oxySatLabel = new Label(oxygenSatGroup, SWT.CENTER);
		oxySatLabel.setFont(bigFont);
		oxySatLabel.setText("--");
		oxySatLabel.setBounds(20, 20, 80, 40);

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
