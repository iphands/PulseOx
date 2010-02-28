package org.ahands.ian.pulseox;

import org.ahands.ian.pulseox.mywidgets.GraphCanvasGC;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class TestGUI {

	static Logger logger = Logger.getLogger(TestGUI.class);

	Label heartBPMLabel;
	Label oxygenSatLabel;
	Group heartRateGroup;
	Group oxygenSatGroup;

	Canvas canvas;
	GC waveFormGc;
	GC trayGc;
	Image trayImage;
	TrayItem trayItem;
	FileListener fileListener;
	String[] deviceArray;

	public TestGUI(Display display) {

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("PulseOx");
		// shell.setSize(278, 346);

		center(shell);
		initUI(shell);
		initTray(display);

		shell.pack();
		shell.open();

		fileListener = new FileListener("/dev/ttyUSB0", display, shell,
				heartBPMLabel, oxygenSatLabel, oxygenSatGroup, heartRateGroup);

		Thread guiUpdate = new Thread(fileListener);
		guiUpdate.start();

		Thread titleUpdater = new Thread(new TitleUpdater(display, shell,
				fileListener));
		titleUpdater.start();

		Thread trayUpdater = new Thread(new TrayUpdater(display, shell,
				fileListener, trayImage, trayItem, trayGc));

		trayUpdater.start();

		shell.setSize(400, 400);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return;
	}

	public void initTray(Display display) {
		Tray tray = display.getSystemTray();
		trayImage = new Image(display, 24, 24);
		trayGc = new GC(trayImage);
		trayGc.drawText("err", 3, 3);

		if (tray != null) {
			trayItem = new TrayItem(tray, SWT.NONE);
			// trayItem.setToolTipText("99");
			trayItem.setImage(trayImage);
		}
	}

	public void buildDeviceMenu(final Menu deviceSubMenu) {
		deviceArray = DeviceChooser.getDevList();
		for (final String device : deviceArray) {

			MenuItem deviceMenuItem = new MenuItem(deviceSubMenu, SWT.PUSH);
			deviceMenuItem.setText(device);
			deviceMenuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event arg0) {
					fileListener.setDevice(device);
				}
			});

		}

		MenuItem seperator = new MenuItem(deviceSubMenu, SWT.SEPARATOR);

		MenuItem deviceMenuItem = new MenuItem(deviceSubMenu, SWT.PUSH);
		deviceMenuItem.setText("&Refresh");

		// deviceMenuItem.setImage(Display.getCurrent().getSystemImage(
		// SWT.ICON_SEARCH));

		deviceMenuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				for (MenuItem menuItem : deviceSubMenu.getItems()) {
					menuItem.dispose();
				}
				buildDeviceMenu(deviceSubMenu);
			}
		});

		return;
	}

	public void initUI(final Shell shell) {

		RowLayout vertRowLayout = new RowLayout(SWT.VERTICAL);
		vertRowLayout.marginTop = 10;
		vertRowLayout.marginBottom = 10;
		vertRowLayout.marginLeft = 5;
		vertRowLayout.marginRight = 5;
		vertRowLayout.spacing = 10;
		vertRowLayout.wrap = true;
		vertRowLayout.fill = true;
		vertRowLayout.justify = true;

		// FillLayout fillLayout = new FillLayout(SWT.VERTICAL);

		// shell.setLayout(vertRowLayout);
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
		fileItem.setText("&File");

		Menu fileSubMenu = new Menu(shell, SWT.DROP_DOWN);
		fileItem.setMenu(fileSubMenu);

		MenuItem loggingMenuItem = new MenuItem(fileSubMenu, SWT.PUSH);
		loggingMenuItem.setText("&Logging...");
		loggingMenuItem.addListener(SWT.Selection, LoggingListener
				.getLoggingListener(shell));

		new MenuItem(fileSubMenu, SWT.SEPARATOR);

		MenuItem exitMenuItem = new MenuItem(fileSubMenu, SWT.PUSH);
		exitMenuItem.setText("E&xit");
		exitMenuItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				// System.exit(0);
				shell.dispose();
			}
		});

		final MenuItem deviceItem = new MenuItem(menu, SWT.CASCADE);
		deviceItem.setText("&Device");

		final Menu deviceSubMenu = new Menu(shell, SWT.DROP_DOWN);
		deviceItem.setMenu(deviceSubMenu);

		deviceArray = DeviceChooser.getDevList();

		buildDeviceMenu(deviceSubMenu);

		MenuItem helpItem = new MenuItem(menu, SWT.CASCADE);
		helpItem.setText("&Help");

		Menu helpSubMenu = new Menu(shell, SWT.DROP_DOWN);
		helpItem.setMenu(helpSubMenu);

		MenuItem aboutMenuItem = new MenuItem(helpSubMenu, SWT.PUSH);
		aboutMenuItem.setText("&About");
		// aboutMenuItem.setImage(Display.getCurrent().getSystemImage(SWT.ARROW));
		aboutMenuItem.addListener(SWT.Selection, new AboutListener(shell));

		Composite topComp = new Composite(shell, SWT.NONE);
		topComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		Font initialFont = shell.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData fontD : fontData) {
			fontD.setHeight(30);
		}

		Font bigFont = new Font(Display.getCurrent(), fontData);

		oxygenSatGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN | SWT.CENTER);
		oxygenSatGroup.setText("Oxygen Saturation");
		oxygenSatGroup.setLayout(new FillLayout(SWT.VERTICAL));

		Composite oxygenSatLabelsComp = new Composite(oxygenSatGroup, SWT.NONE);
		oxygenSatLabelsComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		oxygenSatLabel = new Label(oxygenSatLabelsComp, SWT.CENTER);
		oxygenSatLabel.setFont(bigFont);
		oxygenSatLabel.setText("--");

		final Label percentLabel = new Label(oxygenSatLabelsComp, SWT.CENTER);
		percentLabel.setText("%");

		GraphCanvasGC oxygenSatGraph = new GraphCanvasGC(oxygenSatGroup, 200,
				100) {
			@Override
			public int getYValue() {
				return FileListener.getOxygenSat();
			}
		};
		oxygenSatGraph.setAvgTicks(50);
		oxygenSatGraph.setYMin(70);
		oxygenSatGraph.setDoMarker(true);
		oxygenSatGraph.setErrorWarning(90, 93);

		heartRateGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN | SWT.FILL);
		heartRateGroup.setText("Heart Rate");
		heartRateGroup.setLayout(new FillLayout(SWT.VERTICAL));

		Composite heartRateLabelsComp = new Composite(heartRateGroup, SWT.None);
		heartRateLabelsComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		heartBPMLabel = new Label(heartRateLabelsComp, SWT.CENTER);
		heartBPMLabel.setFont(bigFont);
		heartBPMLabel.setText("--");

		final Label bpmLabel = new Label(heartRateLabelsComp, SWT.CENTER);
		bpmLabel.setText("bpm");

		GraphCanvasGC heartRateGraph = new GraphCanvasGC(heartRateGroup) {
			@Override
			public int getYValue() {
				return FileListener.getHeartRate();
			}
		};
		heartRateGraph.setAvgTicks(20);
		heartRateGraph.setYMin(40);
		heartRateGraph.setDoMarker(true);

		final Composite bottomComp = new Composite(shell, SWT.NONE);
		bottomComp.setLayout(new FillLayout());

		Group waveFormGroup = new Group(bottomComp, SWT.SHADOW_ETCHED_IN
				| SWT.FILL);
		waveFormGroup.setText("Wave Form");
		waveFormGroup.setLayout(new FillLayout());
		GraphCanvasGC waveFormGraph = new GraphCanvasGC(waveFormGroup) {
			@Override
			public int getYValue() {
				return FileListener.getWaveYValue();
			}
		};
	}

	public void center(Shell shell) {

		Rectangle bds = shell.getDisplay().getBounds();

		Point p = shell.getSize();

		int nLeft = (bds.width - p.x) / 4;
		int nTop = (bds.height - p.y) / 2;

		shell.setBounds(nLeft, nTop, p.x, p.y);
	}

	private static void initLog() {
		final Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.DEBUG);
		// rootLogger.addAppender(new ConsoleAppender(new SimpleLayout()));
		return;
	}

	public static void main(String[] args) {
		Display display = new Display();

		initLog();

		new TestGUI(display);

		for (final Shell old_shell : Display.getDefault().getShells()) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					System.err.println("disposing: " + old_shell.toString());
					old_shell.dispose();
				}
			});
		}

		display.dispose();
		System.exit(0);
	}
}
