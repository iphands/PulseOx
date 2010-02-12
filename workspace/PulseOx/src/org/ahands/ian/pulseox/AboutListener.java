package org.ahands.ian.pulseox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class AboutListener implements Listener {
	Image handsImage;

	@Override
	public void handleEvent(Event arg0) {

		Display display = Display.getCurrent();
		Shell shell = new Shell(display, SWT.TITLE | SWT.CLOSE);
		shell.pack();
		shell.open();

		RowLayout vertRowLayout = new RowLayout(SWT.VERTICAL);
		vertRowLayout.marginTop = 10;
		vertRowLayout.marginBottom = 10;
		vertRowLayout.marginLeft = 5;
		vertRowLayout.marginRight = 5;
		vertRowLayout.spacing = 10;
		vertRowLayout.wrap = true;
		vertRowLayout.fill = true;
		vertRowLayout.justify = false;

		shell.setLayout(vertRowLayout);

		// Composite handsComp = new Composite(shell, SWT.NONE);
		// handsComp.setLayout(new GridLayout(SWT.CENTER, false));

		try {
			handsImage = new Image(display, new FileInputStream(new File(
					"hands-50.png")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			handsImage = null;
			e.printStackTrace();
		}

		final Canvas canvas = new Canvas(shell, SWT.NO_REDRAW_RESIZE);
		canvas.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(handsImage, (canvas.getSize().x - handsImage
						.getBounds().width) / 2, 10);
			}
		});

		Font initialFont = shell.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData fontD : fontData) {
			fontD.setHeight(18);
			fontD.setStyle(SWT.BOLD);
		}

		Font bigFont = new Font(Display.getCurrent(), fontData);

		Label titleLabel = new Label(shell, SWT.CENTER);
		titleLabel.setFont(bigFont);
		titleLabel.setText("PulseOx 0.01");
		titleLabel.setBounds(20, 20, 800, 100);

		Label commentLabel = new Label(shell, SWT.CENTER);
		commentLabel
				.setText("A GUI application for the CMS-50e pulse oximeter.\n"
						+ "Written by Ian Page Hands, for Yo Adrian!" + "");
		titleLabel.setBounds(20, 20, 800, 100);

		shell.pack();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}
}
