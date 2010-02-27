package org.ahands.ian.pulseox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.SimpleLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

public class LoggingListener implements Listener {
	private static LoggingListener singleton = null;
	Image handsImage;
	Display display = null;
	Shell shell;
	Shell parent;
	GridData gridData;
	Logger logger = Logger.getLogger(LoggingListener.class);

	boolean fileOn = false;
	boolean fileChange = false;
	String filePath = "/tmp/pulseox.log";
	Level fileLevel = Level.INFO;
	FileAppender fileAppender = null;

	boolean consoleOn = false;
	Level consoleLevel = Level.DEBUG;
	final ConsoleAppender consoleAppender = new ConsoleAppender(
			new SimpleLayout());

	Button applyButton;

	private LoggingListener() {
		this.parent = new Shell(Display.getCurrent());
	}

	private LoggingListener(Shell shell) {
		this.parent = shell;
		this.display = shell.getDisplay();
	}

	public static LoggingListener getLoggingListener(Shell shell) {
		if (singleton == null) {
			System.err.println("TEST");
			singleton = new LoggingListener(shell);
		}
		return singleton;
	}

	@Override
	public void handleEvent(Event arg0) {
		shell = new Shell(parent, SWT.RESIZE | SWT.TITLE | SWT.CLOSE);
		shell.setText("Logging...");

		try {
			handsImage = new Image(display, new FileInputStream(new File(
					"./resources/hands-50.png")));
		} catch (FileNotFoundException e) {
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

		applyButton = new Button(saveComp, SWT.PUSH);
		applyButton.setText("&Apply");
		applyButton.setEnabled(false);

		final Button saveButton = new Button(saveComp, SWT.PUSH);
		saveButton.setText("&Save");
		saveButton.setEnabled(false);

		applyButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				Logger rootLogger = Logger.getRootLogger();

				if (consoleOn) {
					rootLogger.removeAppender(consoleAppender);
					rootLogger.addAppender(consoleAppender);
					consoleAppender.setThreshold(consoleLevel);
				} else {
					rootLogger.removeAppender(consoleAppender);
				}

				if (fileOn) {
					if (fileAppender == null || fileChange) {

						rootLogger.removeAppender(fileAppender);
						fileAppender = null;

						Layout fileLayout = new PatternLayout("%m\n");

						try {
							fileAppender = new FileAppender(fileLayout,
									filePath);

						} catch (IOException e) {
							final String ERROR_MESSAGE = "Unable to open file "
									+ filePath + " for logging";
							if (consoleOn) {
								logger.error(ERROR_MESSAGE);
							} else {
								System.err.println(ERROR_MESSAGE);
							}
							e.printStackTrace();
						}

						if (fileAppender != null) {
							fileAppender.setThreshold(fileLevel);
							rootLogger.addAppender(fileAppender);
						}
					}

				} else {
					rootLogger.removeAppender(fileAppender);
					fileAppender = null;
				}

				// Save button not ready for use yet
				// saveButton.setEnabled(true);
				applyButton.setEnabled(false);
			}
		});

		saveButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				// Logger rootLogger = Logger.getRootLogger();

			}
		});

		final Button closeButton = new Button(saveComp, SWT.PUSH);
		closeButton.setText("&Close");
		closeButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				shell.dispose();
			}
		});
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
					fileOn = true;
				} else {
					enableFileButton.setText("Enable");
					fileOn = false;
				}
				applyButton.setEnabled(true);
			}
		});

		final Composite pathComp = new Composite(fileLoggingGroup, SWT.NONE);
		pathComp.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label pathTitleLabel = new Label(pathComp, SWT.LEFT);
		pathTitleLabel.setText("Path: ");

		final Label pathLabel = new Label(pathComp, SWT.LEFT);
		pathLabel.setText(filePath);

		final Button pathButton = new Button(topComp, SWT.CENTER);
		pathButton.setText("&Browse");

		pathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				filePath = dialog.open();
				if (filePath != null) {
					fileChange = true;
					pathLabel.setText(filePath);
					shell.pack();
				}
				pathLabel.pack();
				applyButton.setEnabled(true);
			}
		});

		final Composite levelComp = new Composite(fileLoggingGroup, SWT.NONE);
		levelComp.setLayout(new FillLayout(SWT.VERTICAL));

		final Composite levelInnerComp = new Composite(levelComp, SWT.NONE);
		levelInnerComp.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label levelTitleLabel = new Label(levelInnerComp, SWT.LEFT);
		levelTitleLabel.setText("Level: ");

		final Label levelLabel = new Label(levelInnerComp, SWT.LEFT);
		levelLabel.setText(fileLevel.toString());

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
					fileLevel = Level.DEBUG;
				} else if (SELECTION == 1) {
					fileLevel = Level.INFO;
				} else if (SELECTION == 2) {
					fileLevel = Level.WARN;
				} else if (SELECTION == 3) {
					fileLevel = Level.ERROR;
				} else if (SELECTION == 4) {
					fileLevel = Level.FATAL;
				}

				levelLabel.setText(fileLevel.toString());
				levelLabel.pack();
				applyButton.setEnabled(true);
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
					consoleOn = true;
				} else {
					enableFileButton.setText("Enable");
					consoleOn = false;
				}
				applyButton.setEnabled(true);
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
		levelLabel.setText(consoleLevel.toString());

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
					consoleLevel = Level.DEBUG;
				} else if (SELECTION == 1) {
					consoleLevel = Level.INFO;
				} else if (SELECTION == 2) {
					consoleLevel = Level.WARN;
				} else if (SELECTION == 3) {
					consoleLevel = Level.ERROR;
				} else if (SELECTION == 4) {
					consoleLevel = Level.FATAL;
				}

				levelLabel.setText(consoleLevel.toString());
				levelLabel.pack();
				applyButton.setEnabled(true);
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