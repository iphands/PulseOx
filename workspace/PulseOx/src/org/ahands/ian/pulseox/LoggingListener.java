package org.ahands.ian.pulseox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

public class LoggingListener implements Listener {
	Image handsImage;
	Shell shell;

	@Override
	public void handleEvent(Event arg0) {

		Display display = Display.getCurrent();
		// shell = new Shell(display, SWT.TITLE | SWT.CLOSE);
		shell = new Shell(display);
		shell.setText("Logging...");

		try {
			handsImage = new Image(display, new FileInputStream(new File(
					"hands-50.png")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			handsImage = null;
			e.printStackTrace();
		}

		shell.setImage(handsImage);

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
		vertRowLayout.justify = true;

		// GridLayout gridLayout = new GridLayout();
		// gridLayout.numColumns = 3;

		// shell.setLayout(vertRowLayout);
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		// Group outerGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);

		Button enableLoggingButton = new Button(shell, SWT.CHECK);
		enableLoggingButton.setText("Enable logging?");

		doFileBased();
		doConsoleBased();

		shell.pack();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void doFileBased() {
		Group fileLoggingGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
		fileLoggingGroup.setText("File based");
		fileLoggingGroup.setLayout(new FillLayout(SWT.VERTICAL));

		Button enableFileButton = new Button(fileLoggingGroup, SWT.CHECK);
		enableFileButton.setText("Enable/Disable");

		Composite pathComp = new Composite(fileLoggingGroup, SWT.NONE);
		pathComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		Label pathTitleLabel = new Label(pathComp, SWT.LEFT);
		pathTitleLabel.setText("Path: ");

		Label pathLabel = new Label(pathComp, SWT.LEFT);
		pathLabel.setText(" -- ");

		Button pathButton = new Button(pathComp, SWT.CENTER);
		pathButton.setText("Browse");

		Composite levelComp = new Composite(fileLoggingGroup, SWT.NONE);
		levelComp.setLayout(new FillLayout(SWT.VERTICAL));

		Composite levelInnerComp = new Composite(levelComp, SWT.NONE);
		levelInnerComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		Label levelTitleLabel = new Label(levelInnerComp, SWT.LEFT);
		levelTitleLabel.setText("Level: ");

		final Label levelLabel = new Label(levelInnerComp, SWT.LEFT);
		levelLabel.setText(" -- ");

		final Label emptyLabel = new Label(levelInnerComp, SWT.LEFT);

		final Scale levelScale = new Scale(levelComp, SWT.BORDER_DASH);
		levelScale.setMinimum(0);
		levelScale.setMaximum(4);
		levelScale.setIncrement(1);
		levelScale.setPageIncrement(1);
		levelScale.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				final int SELECTION = levelScale.getSelection();
				if (SELECTION <= 0) {
					levelLabel.setText("Debug");
				} else if (SELECTION == 1) {
					levelLabel.setText("Info");
				} else if (SELECTION == 2) {
					levelLabel.setText("Warn");
				} else if (SELECTION == 3) {
					levelLabel.setText("Error");
				} else if (SELECTION == 4) {
					levelLabel.setText("Fatal");
				}
			}
		});

		return;
	}

	private void doConsoleBased() {
		Group consoleLoggingGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
		consoleLoggingGroup.setText("Console based");
		consoleLoggingGroup.setLayout(new FillLayout(SWT.VERTICAL));

		Button enableFileButton = new Button(consoleLoggingGroup, SWT.CHECK);
		enableFileButton.setText("Enable/Disable");

		Composite levelComp = new Composite(consoleLoggingGroup, SWT.NONE);
		levelComp.setLayout(new FillLayout(SWT.VERTICAL));

		Composite levelInnerComp = new Composite(levelComp, SWT.NONE);
		levelInnerComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		Label levelTitleLabel = new Label(levelInnerComp, SWT.LEFT);
		levelTitleLabel.setText("Level: ");

		final Label levelLabel = new Label(levelInnerComp, SWT.LEFT);
		levelLabel.setText(" -- ");

		final Label emptyLabel = new Label(levelInnerComp, SWT.LEFT);

		final Scale levelScale = new Scale(levelComp, SWT.BORDER_DASH);
		levelScale.setMinimum(0);
		levelScale.setMaximum(4);
		levelScale.setIncrement(1);
		levelScale.setPageIncrement(1);
		levelScale.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				final int SELECTION = levelScale.getSelection();
				if (SELECTION <= 0) {
					levelLabel.setText("Debug");
				} else if (SELECTION == 1) {
					levelLabel.setText("Info");
				} else if (SELECTION == 2) {
					levelLabel.setText("Warn");
				} else if (SELECTION == 3) {
					levelLabel.setText("Error");
				} else if (SELECTION == 4) {
					levelLabel.setText("Fatal");
				}
			}
		});

		return;
	}
}
// Font initialFont = shell.getFont();
// FontData[] fontData = initialFont.getFontData();
// for (FontData fontD : fontData) {
// fontD.setHeight(18);
// fontD.setStyle(SWT.BOLD);
// }
//
// Font bigFont = new Font(Display.getCurrent(), fontData);