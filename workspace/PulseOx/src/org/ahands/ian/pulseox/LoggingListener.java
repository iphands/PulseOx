package org.ahands.ian.pulseox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

public class LoggingListener implements Listener {
	Image handsImage;
	Shell shell;
	GridData gridData;

	@Override
	public void handleEvent(Event arg0) {

		final Display display = Display.getCurrent();
		shell = new Shell(display, SWT.RESIZE | SWT.TITLE | SWT.CLOSE);
		// shell = new Shell(display);
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

		final FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.spacing = 8;
		fillLayout.marginHeight = 8;
		fillLayout.marginWidth = 8;

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 20;
		gridLayout.marginWidth = 8;
		// gridLayout.makeColumnsEqualWidth = true;

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.minimumWidth = 200;
		gridData.grabExcessHorizontalSpace = true;

		shell.setLayout(gridLayout);

		doFileBased();
		doConsoleBased();
		doSave();

		shell.pack();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void doSave() {
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.minimumWidth = 250;
		gridData.grabExcessHorizontalSpace = true;

		final FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.spacing = 8;
		fillLayout.marginHeight = 16;
		fillLayout.marginWidth = 8;

		final Composite saveComp = new Composite(shell, SWT.NONE);
		saveComp.setLayout(fillLayout);
		saveComp.setLayoutData(gridData);

		final Button applyButton = new Button(saveComp, SWT.PUSH);
		applyButton.setText("&Apply");

		final Button saveButton = new Button(saveComp, SWT.PUSH);
		saveButton.setText("&Save");
	}

	private void doFileBased() {

		final Group fileLoggingGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
		fileLoggingGroup.setText("File based");
		fileLoggingGroup.setLayout(new FillLayout(SWT.VERTICAL));
		fileLoggingGroup.setLayoutData(gridData);

		final Composite topComp = new Composite(fileLoggingGroup, SWT.NONE);
		topComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Button enableFileButton = new Button(topComp, SWT.TOGGLE);
		enableFileButton.setText("Enable");
		enableFileButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (enableFileButton.getSelection()) {
					enableFileButton.setText("Disable");
				} else {
					enableFileButton.setText("Enable");
				}
			}
		});

		final Composite pathComp = new Composite(fileLoggingGroup, SWT.NONE);
		pathComp.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label pathTitleLabel = new Label(pathComp, SWT.LEFT);
		pathTitleLabel.setText("Path: ");

		final Label pathLabel = new Label(pathComp, SWT.LEFT);
		pathLabel.setText("/tmp/pulseox.log");

		final Button pathButton = new Button(topComp, SWT.CENTER);
		pathButton.setText("&Browse");

		pathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String path = dialog.open();
				if (path != null) {
					pathLabel.setText(path);
					pathLabel.redraw();
					shell.pack();
				}
			}
		});

		final Composite levelComp = new Composite(fileLoggingGroup, SWT.NONE);
		levelComp.setLayout(new FillLayout(SWT.VERTICAL));

		final Composite levelInnerComp = new Composite(levelComp, SWT.NONE);
		levelInnerComp.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label levelTitleLabel = new Label(levelInnerComp, SWT.LEFT);
		levelTitleLabel.setText("Level: ");

		final Label levelLabel = new Label(levelInnerComp, SWT.LEFT);
		levelLabel.setText(" -- ");

		final Label emptyLabel = new Label(levelInnerComp, SWT.LEFT);
		emptyLabel.redraw();

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

				levelLabel.pack();
			}
		});

		return;
	}

	private void doConsoleBased() {
		final Group consoleLoggingGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
		consoleLoggingGroup.setText("Console based");
		consoleLoggingGroup.setLayout(new FillLayout(SWT.VERTICAL));
		consoleLoggingGroup.setLayoutData(gridData);

		final Composite topComp = new Composite(consoleLoggingGroup, SWT.NONE);
		topComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Button enableFileButton = new Button(topComp, SWT.TOGGLE);
		enableFileButton.setText("Enable");
		enableFileButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (enableFileButton.getSelection()) {
					enableFileButton.setText("Disable");
				} else {
					enableFileButton.setText("Enable");
				}
			}
		});

		final Label emptyLabel = new Label(topComp, SWT.LEFT);
		emptyLabel.redraw();

		final Composite pathComp = new Composite(consoleLoggingGroup, SWT.NONE);
		pathComp.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label fakePathTitleLabel = new Label(pathComp, SWT.LEFT);
		fakePathTitleLabel.redraw();
		// pathTitleLabel.setText("Path: ");

		final Label fakePathLabel = new Label(pathComp, SWT.LEFT);
		fakePathLabel.redraw();
		// pathLabel.setText(" -- ");

		final Composite levelComp = new Composite(consoleLoggingGroup, SWT.NONE);
		levelComp.setLayout(new FillLayout(SWT.VERTICAL));

		final Composite levelInnerComp = new Composite(levelComp, SWT.NONE);
		levelInnerComp.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label levelTitleLabel = new Label(levelInnerComp, SWT.LEFT);
		levelTitleLabel.setText("Level: ");

		final Label levelLabel = new Label(levelInnerComp, SWT.LEFT);
		levelLabel.setText(" -- ");

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
				levelLabel.pack();
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