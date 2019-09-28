package de.lars.remotelightclient.ui.panels.screencolor;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.screencolor.ScreenColorManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsUtil;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.ui.MainFrame.NotificationType;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.ImagePanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;
import de.lars.remotelightclient.utils.UiUtils;
import de.lars.remotelightclient.utils.WrapLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.border.EmptyBorder;

import org.tinylog.Logger;
import javax.swing.border.TitledBorder;
import javax.swing.ScrollPaneConstants;

public class ScreenColorPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1195460185717058923L;
	private final int MONITOR_SCALE = 10;
	private ScreenColorManager scm;
	private SettingsManager sm;
	private List<ImagePanel> monitorPanels;
	private List<SettingPanel> settingPanels;
	private ImagePanel selectedMonitorPanel;
	private JPanel panelMonitors;
	private JPanel panelOptions;
	private JButton btnEnable;
	
	public ScreenColorPanel() {
		scm = Main.getInstance().getScreenColorManager();
		sm = Main.getInstance().getSettingsManager();
		monitorPanels = new ArrayList<ImagePanel>();
		settingPanels = new ArrayList<SettingPanel>();
		MainFrame mainFrame = Main.getInstance().getMainFrame();
		mainFrame.showControlBar(true);
		mainFrame.setControlBarPanel(new DefaultControlBar());
		
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Dimension size = new Dimension(Integer.MAX_VALUE, 700);
		JPanel bgrMonitors = new JPanel();
		bgrMonitors.setBorder(new EmptyBorder(20, 20, 0, 20));
		bgrMonitors.setPreferredSize(size);
		bgrMonitors.setMaximumSize(size);
		bgrMonitors.setBackground(Style.panelBackground);
		add(bgrMonitors);
		bgrMonitors.setLayout(new BorderLayout(0, 0));
		
		
		panelMonitors = new JPanel();
		panelMonitors.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), "Select a monitor", TitledBorder.LEADING, TitledBorder.TOP, null, Style.accent));
		WrapLayout wlayout = new WrapLayout(FlowLayout.CENTER);
		panelMonitors.setLayout(wlayout);
		panelMonitors.setBackground(Style.panelDarkBackground);
		
		JScrollPane scrollPane = new JScrollPane(panelMonitors);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrMonitors.add(scrollPane, BorderLayout.CENTER);
		
		size = new Dimension(Integer.MAX_VALUE, 400);
		JPanel bgrOptions = new JPanel();
		bgrOptions.setPreferredSize(size);
		bgrOptions.setBackground(Style.panelBackground);
		add(bgrOptions);
		bgrOptions.setLayout(new BorderLayout(0, 0));
		
		panelOptions = new JPanel();
		panelOptions.setBorder(new EmptyBorder(5, 20, 0, 0));
		panelOptions.setBackground(Style.panelBackground);
		bgrOptions.add(panelOptions, BorderLayout.CENTER);
		panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.Y_AXIS));
		
		this.addOptions();
		this.addMonitorsPanel();
	}
	
	private void addMonitorsPanel() {
		try {
			Robot r;
			
			for(GraphicsDevice gd : ScreenColorManager.getMonitors()) {
				r = new Robot(gd);
				BufferedImage img = r.createScreenCapture(gd.getDefaultConfiguration().getBounds());
				
				Rectangle bounds = gd.getDefaultConfiguration().getBounds();
				Dimension size = new Dimension(bounds.width / MONITOR_SCALE, bounds.height / MONITOR_SCALE);
				ImagePanel ipanel = new ImagePanel(img, gd.getIDstring().substring(1), size);
				ipanel.setName(gd.getIDstring());
				ipanel.addMouseListener(monitorPanelClicked);
				
				monitorPanels.add(ipanel);
				this.markMonitorPanel(ipanel, (scm.getCurrentMonitor() == gd));
				
				panelMonitors.add(ipanel);
			}
			updateUI();
			
		} catch (AWTException e) {
			Logger.error(e, "Could not add monitors panel!");
		}
	}
	
	private MouseAdapter monitorPanelClicked = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			ImagePanel panel = (ImagePanel) e.getSource();
			
			scm.setMonitor(ScreenColorManager.getMonitorByID(panel.getName()));
			
			for(ImagePanel ip : monitorPanels) {
				markMonitorPanel(ip, false);
			}
			markMonitorPanel(panel, true);
		}
	};
	
	private void markMonitorPanel(ImagePanel panel, boolean selected) {
		if(selected) {
			panel.setBlur(false);
			panel.setBorder(BorderFactory.createLineBorder(Style.accent));
			selectedMonitorPanel = panel;
			drawYPosLine();
		} else {
			panel.setBlur(true);
			panel.setBorder(BorderFactory.createEmptyBorder());
		}
	}

	private void drawYPosLine() {
		for(ImagePanel ip : monitorPanels) {
			if(ip.getName().equals(scm.getCurrentMonitor().getIDstring())) {
				int ypos = ((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue();
				int yHeight = ((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue();
				
				int y = ypos / MONITOR_SCALE;
				ip.enableLine(0, y, ip.getWidth(), yHeight / MONITOR_SCALE);
				ip.repaint();
			} else {
				ip.disableLine();
			}
		}
	}
	
	private void addOptions() {
		JPanel panel = panelOptions;
		panel.removeAll();
		settingPanels.clear();
		
		SettingPanel spDelay = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.delay"));
		panel.add(configureSettingPanel(spDelay));
		settingPanels.add(spDelay);
		SettingPanel spYPos = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.ypos"));
		panel.add(configureSettingPanel(spYPos));
		settingPanels.add(spYPos);
		SettingPanel spYHeight = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.yheight"));
		panel.add(configureSettingPanel(spYHeight));
		settingPanels.add(spYHeight);
		SettingPanel spInvert = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.invert"));
		panel.add(configureSettingPanel(spInvert));
		settingPanels.add(spInvert);
		
		btnEnable = new JButton();
		UiUtils.configureButtonWithBorder(btnEnable, Style.accent);
		Dimension size = new Dimension(80, 25);
		btnEnable.setPreferredSize(size);
		btnEnable.setMinimumSize(size);
		btnEnable.setMaximumSize(size);
		panel.add(btnEnable);
		
		if(scm.isActive()) {
			btnEnable.setText("Disable");
		} else {
			btnEnable.setText("Enable");
		}
		btnEnable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableScreenColor(!scm.isActive());
			}
		});
	}
	
	private SettingPanel configureSettingPanel(SettingPanel spanel) {
		spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		spanel.setSettingChangedListener(settingChangeListener);
		spanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		spanel.setBackground(Style.panelBackground);
		return spanel;
	}
	
	private SettingChangedListener settingChangeListener = new SettingChangedListener() {
		@Override
		public void onSettingChanged(SettingPanel settingPanel) {
			settingPanel.setValue();
			setScreenColorSettings();
			drawYPosLine();
			
			if(scm.getCurrentMonitor() != null) {
				int height = scm.getCurrentMonitor().getDefaultConfiguration().getBounds().height;
				int yPos = ((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue();
				int yHeight = ((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue();
				
				((SettingInt) sm.getSettingFromId("screencolor.ypos")).setMax(height - yHeight);
				((SettingInt) sm.getSettingFromId("screencolor.yheight")).setMax(height - yPos);
				
				for(SettingPanel sp : settingPanels) {
					sp.updateComponents();
				}
			}
		}
	};
	
	private void enableScreenColor(boolean enable) {
		if(enable) {
			if(selectedMonitorPanel != null) {
				Main.getInstance().getMainFrame().printNotification(null, null);
				btnEnable.setText("Disable");
				
				scm.start(((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue(),	//YPos
						((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue(),	//YHeight
						((SettingInt) sm.getSettingFromId("screencolor.delay")).getValue(),		//Delay
						((SettingBoolean) sm.getSettingFromId("screencolor.invert")).getValue(),//Invert
						ScreenColorManager.getMonitorByID(selectedMonitorPanel.getName()));		//Monitor
				
			} else {
				Main.getInstance().getMainFrame().printNotification("You need to select a monitor", NotificationType.Error);
			}
		} else {
			btnEnable.setText("Enable");
			scm.stop();
		}
	}
	
	private void setScreenColorSettings() {
		scm.setInverted(((SettingBoolean) sm.getSettingFromId("screencolor.invert")).getValue());
		scm.setDelay(((SettingInt) sm.getSettingFromId("screencolor.delay")).getValue());
		scm.setYPos(((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue());
		scm.setYHeight(((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue());
	}
	
}
