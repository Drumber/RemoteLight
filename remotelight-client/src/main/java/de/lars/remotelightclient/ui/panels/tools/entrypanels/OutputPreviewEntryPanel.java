package de.lars.remotelightclient.ui.panels.tools.entrypanels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Timer;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.panels.tools.ToolsPanelEntry;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;

public class OutputPreviewEntryPanel extends ToolsPanelEntry {
	
	private OutputPreviewFrame frame;

	@Override
	public String getName() {
		return "Output Preview";
	}

	@Override
	public void onClick() {
		if(frame == null) {
			frame = new OutputPreviewFrame();
		}
		frame.setVisible(!frame.isVisible());
	}
	
	/**
	 * A frame that displays the current output in a RGB graph and a optional strip preview.
	 */
	private class OutputPreviewFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		private OutputManager om;
		private SettingsManager sm;
		
		private final Panel panel;
		private final Timer timer;
		
		private SettingBoolean sStripPreview;
		private SettingBoolean sAlwaysTop;
		private SettingBoolean sBlackBackground;
		private SettingInt sFPS;
		
		public OutputPreviewFrame() {
			om = Main.getInstance().getCore().getOutputManager();
			sm = Main.getInstance().getCore().getSettingsManager();
			initSettings();
			this.setTitle("Output Preview");
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setSize(600, 300);
			this.setMinimumSize(new Dimension(200, 100));
			panel = new Panel();
			this.setContentPane(panel);
			
			// context menu
			JPopupMenu menu = new JPopupMenu();
			addMenuItemsToContextMenu(menu);
			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					showPopup(e);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					showPopup(e);
				}
				private void showPopup(MouseEvent e) {
					if(e.isPopupTrigger()) {
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
			
			// draw every 35 milliseconds (= 1000/35 = 29 FPS) (will be overwritten by setSettingValues())
			timer = new Timer(35, e -> {
				if(isVisible()) {
					panel.repaint();
				}
			});
			timer.start();
			
			setSettingValues();
		}
		
		/**
		 * Initialize the settings object.
		 * Create the settings on the first run, or load previous values.
		 */
		private void initSettings() {
			sStripPreview = sm.addSetting(new SettingBoolean("outputpreview.strippreview", "Show strip preview", SettingCategory.Intern, null, true));
			sAlwaysTop = sm.addSetting(new SettingBoolean("outputpreview.alwaystop", "Window always on top", SettingCategory.Intern, null, false));
			sBlackBackground = sm.addSetting(new SettingBoolean("outputpreview.blackbackground", "Black background", SettingCategory.Intern, null, false));
			sFPS = sm.addSetting(new SettingInt("outputpreview,fps", "FPS", SettingCategory.Intern, null, 60, 15, 120, 1));
		}
		
		/**
		 * Apply the settings to the UI.
		 */
		private void setSettingValues() {
			this.setAlwaysOnTop(sAlwaysTop.get());
			timer.setDelay(1000 / sFPS.get());
			panel.setBackground(sBlackBackground.get() ? Color.BLACK : null);
		}
		
		/**
		 * Create the menu items for the context menu.
		 * @param menu	the {@link JPopupMenu} the menu items will be added to.
		 */
		private void addMenuItemsToContextMenu(JPopupMenu menu) {
			// create menu items
			JCheckBoxMenuItem itemStripPreview = new JCheckBoxMenuItem("Show strip preview", sStripPreview.get());
			JCheckBoxMenuItem itemAlwaysTop = new JCheckBoxMenuItem("Show window always on top", sAlwaysTop.get());
			JCheckBoxMenuItem itemBlackBgr = new JCheckBoxMenuItem("Black background", sBlackBackground.get());
			JMenu menuFPS = new JMenu("FPS");
			JRadioButtonMenuItem itemFPS15 = new JRadioButtonMenuItem("15 FPS", sFPS.get() == 15);
			JRadioButtonMenuItem itemFPS30 = new JRadioButtonMenuItem("30 FPS", sFPS.get() == 30);
			JRadioButtonMenuItem itemFPS60 = new JRadioButtonMenuItem("60 FPS", sFPS.get() == 60);
			JRadioButtonMenuItem itemFPS120 = new JRadioButtonMenuItem("120 FPS", sFPS.get() == 120);
			
			// add menu items to the menu
			menu.add(itemStripPreview);
			menu.add(itemAlwaysTop);
			menu.add(itemBlackBgr);
			menu.add(menuFPS);
			menuFPS.add(itemFPS15);
			menuFPS.add(itemFPS30);
			menuFPS.add(itemFPS60);
			menuFPS.add(itemFPS120);
			
			// button group for the FPS options
			ButtonGroup groupFPS = new ButtonGroup();
			groupFPS.add(itemFPS15);
			groupFPS.add(itemFPS30);
			groupFPS.add(itemFPS60);
			groupFPS.add(itemFPS120);
			
			// add listeners to the menu items which updates the setting values
			itemStripPreview.addActionListener(e -> sStripPreview.set(itemStripPreview.isSelected()));
			itemAlwaysTop.addActionListener(e -> {
				sAlwaysTop.set(itemAlwaysTop.isSelected());
				setSettingValues();
			});
			itemBlackBgr.addActionListener(e -> {
				sBlackBackground.set(itemBlackBgr.isSelected());
				setSettingValues();
			});
			itemFPS15.addActionListener(e -> onFPSChanged(15));
			itemFPS30.addActionListener(e -> onFPSChanged(30));
			itemFPS60.addActionListener(e -> onFPSChanged(60));
			itemFPS120.addActionListener(e -> onFPSChanged(120));
		}
		
		private void onFPSChanged(int fps) {
			sFPS.set(fps);
			setSettingValues();
		}
		
		/**
		 * {@link JPanel} that displays the RGB graph and the strip preview.
		 */
		private class Panel extends JPanel {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				de.lars.remotelightcore.utils.color.Color[] strip = om.getLastColors();
				if(strip == null || strip.length < 1) return;
				
				int width = this.getBounds().width;
				int height = this.getBounds().height;
				
				// if the strip preview is enabled: margin-bottom = strip height; otherwise = 0
				final int stripHeight = 20;
				int marginBottom = stripHeight;
				if(!sStripPreview.get()) {
					marginBottom = 0;
				}
				
				int graphHeight = height - marginBottom;
				
				// calculate the fraction for a single LED
				float fraction = 1.0f * width / strip.length;
				// the length of the displayed LEDs
				int length = strip.length;
				// if there are more LEDs than pixels of the window width, set the length to the width
				if(fraction <= 0) {
					length = width;
				}
				
				int[] x = new int[length];
				int[] yRed = new int[length];
				int[] yGreen = new int[length];
				int[] yBlue = new int[length];
				
				float[] gFractions = new float[length];
				Color[] gColors = new Color[length];
				
				for(int i = 0; i < length; i++) {
					x[i] = (int) Math.floor(fraction * i);
					yRed[i] = (int) (graphHeight - graphHeight / 255.0f * strip[i].getRed());
					yGreen[i] = (int) (graphHeight - graphHeight / 255.0f * strip[i].getGreen());
					yBlue[i] = (int) (graphHeight - graphHeight / 255.0f * strip[i].getBlue());
					
					 gFractions[i] = 1.0f / width * (fraction * i);
					 gColors[i] = ColorTool.convert(strip[i]);
				}
				
				// draw the RGB graph using polylines
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.RED);
				g2.drawPolyline(x, yRed, length);
				g2.setColor(Color.GREEN);
				g2.drawPolyline(x, yGreen, length);
				g2.setColor(Color.BLUE);
				g2.drawPolyline(x, yBlue, length);
				
				// draw the strip preview if enabled
				if(sStripPreview.get()) {
					Point2D gStart = new Point2D.Float(0, height - stripHeight);
					Point2D gEnd = new Point2D.Float(width, height);
					LinearGradientPaint gradient = new LinearGradientPaint(gStart, gEnd, gFractions, gColors);
					g2.setPaint(gradient);
					g2.fillRect((int) gStart.getX(), (int) gStart.getY(), (int) (gEnd.getX() - gStart.getX()), (int) (gEnd.getY() - gStart.getY()));
				}
				
				g2.dispose();
			}
		}
		
	}
	
}
