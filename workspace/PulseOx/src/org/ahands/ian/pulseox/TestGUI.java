package org.ahands.ian.pulseox;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
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

	Label heartBPMLabel;
	Label oxySatLabel;
	Group heartRateGroup;
	Group oxygenSatGroup;

	Canvas canvas;
	GC waveFormGc;
	GC trayGc;
	Image trayImage;
	TrayItem trayItem;
	FileListener fileListener;

	public TestGUI(Display display) {

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("PulseOx");
		shell.setSize(278, 346);

		center(shell);
		initUI(shell);
		initTray(display);

		shell.pack();
		shell.open();

		fileListener = new FileListener("/dev/ttyUSB0", display, shell,
				heartBPMLabel, oxySatLabel, oxygenSatGroup, heartRateGroup);

		Thread guiUpdate = new Thread(fileListener);
		guiUpdate.start();

		Thread titleUpdater = new Thread(new TitleUpdater(display, shell,
				fileListener));
		titleUpdater.start();

		Thread trayUpdater = new Thread(new TrayUpdater(display, shell,
				fileListener, trayImage, trayItem, trayGc));
		trayUpdater.start();

		// System.out.println(shell.getBounds());

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
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

	public void initUI(final Shell shell) {

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

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem deviceItem = new MenuItem(menu, SWT.CASCADE);
		deviceItem.setText("&Device");

		Menu deviceSubMenu = new Menu(shell, SWT.DROP_DOWN);
		deviceItem.setMenu(deviceSubMenu);

		String[] deviceArray = DeviceChooser.getDevList();

		// deviceItem.addListener(SWT.PUSH, new Listener() {
		//
		// @Override
		// public void handleEvent(Event arg0) {
		// deviceArray = DeviceChooser.getDevList();
		//
		// }
		// });

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

		MenuItem helpItem = new MenuItem(menu, SWT.CASCADE);
		helpItem.setText("&Help");

		Menu helpSubMenu = new Menu(shell, SWT.DROP_DOWN);
		helpItem.setMenu(helpSubMenu);

		MenuItem aboutMenuItem = new MenuItem(helpSubMenu, SWT.PUSH);
		aboutMenuItem.setText("&About");
		// aboutMenuItem.setImage(Display.getCurrent().getSystemImage(SWT.ARROW));
		aboutMenuItem.addListener(SWT.Selection, new AboutListener());

		Composite topComp = new Composite(shell, SWT.NONE | SWT.FILL);
		topComp.setLayout(new FillLayout(SWT.HORIZONTAL));

		Font initialFont = shell.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData fontD : fontData) {
			fontD.setHeight(30);
		}

		Font bigFont = new Font(Display.getCurrent(), fontData);

		oxygenSatGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN);
		oxygenSatGroup.setText("Oxygen Saturation");

		oxySatLabel = new Label(oxygenSatGroup, SWT.CENTER);
		oxySatLabel.setFont(bigFont);
		oxySatLabel.setText("--");
		oxySatLabel.setBounds(20, 20, 80, 40);

		final Label percentLabel = new Label(oxygenSatGroup, SWT.CENTER);
		percentLabel.setText("%");
		percentLabel.setBounds(oxySatLabel.getBounds().width + 4, (oxySatLabel
				.getBounds().height + 20) - 15, 20, 15);

		heartRateGroup = new Group(topComp, SWT.SHADOW_ETCHED_IN | SWT.FILL);
		heartRateGroup.setText("Heart Rate");

		heartBPMLabel = new Label(heartRateGroup, SWT.CENTER);
		heartBPMLabel.setFont(bigFont);
		heartBPMLabel.setText("--");
		heartBPMLabel.setBounds(20, 20, 80, 40);

		final Label bpmLabel = new Label(heartRateGroup, SWT.CENTER);
		bpmLabel.setText("bpm");
		bpmLabel.setBounds(heartBPMLabel.getBounds().width + 20, (heartBPMLabel
				.getBounds().height + 20) - 15, 30, 15);

		final Composite bottomComp = new Composite(shell, SWT.NONE);
		bottomComp.setLayout(new FillLayout());

		Group waveFormGroup = new Group(bottomComp, SWT.SHADOW_ETCHED_IN
				| SWT.FILL);
		waveFormGroup.setText("Wave Form");

		canvas = new Canvas(waveFormGroup, SWT.NONE);
		canvas.setSize(200, 128);
		canvas.setLocation(31, 30);

		canvas.setBackground(new Color(Display.getCurrent(), 0, 0, 0));

		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent pEvent) {

				waveFormGc = new GC(canvas);
				waveFormGc.setForeground(pEvent.display
						.getSystemColor(SWT.COLOR_GREEN));

				fileListener.setGC(waveFormGc, canvas, canvas.getClientArea());

				final Rectangle canvasRect = canvas.getBounds();
				final Rectangle bottomCompRect = bottomComp.getBounds();
				canvas.setLocation(((bottomCompRect.width / 2))
						- ((canvasRect.width / 2)), 30);

			}
		});
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
