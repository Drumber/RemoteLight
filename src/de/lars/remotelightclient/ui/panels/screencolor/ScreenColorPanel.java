package de.lars.remotelightclient.ui.panels.screencolor;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.screencolor.ScreenColorManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsUtil;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.ui.MainFrame.NotificationType;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.ImagePanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;
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
	private ScreenColorManager scm;
	private SettingsManager sm;
	private List<ImagePanel> monitorPanels;
	private ImagePanel selectedMonitorPanel;
	private JPanel panelMonitors;
	private JButton btnEnable;
	
	public ScreenColorPanel() {
		scm = Main.getInstance().getScreenColorManager();
		sm = Main.getInstance().getSettingsManager();
		monitorPanels = new ArrayList<ImagePanel>();
		
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
		
		JPanel panelOptions = new JPanel();
		panelOptions.setBorder(new EmptyBorder(5, 20, 0, 0));
		panelOptions.setBackground(Style.panelBackground);
		bgrOptions.add(panelOptions, BorderLayout.CENTER);
		panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.Y_AXIS));
		
		this.addOptions(panelOptions);
		this.addMonitorsPanel();
	}
	
	private void addMonitorsPanel() {
		try {
			Robot r;
			
			for(GraphicsDevice gd : ScreenColorManager.getMonitors()) {
				r = new Robot(gd);
				BufferedImage img = r.createScreenCapture(gd.getDefaultConfiguration().getBounds());
				
				Dimension size = new Dimension(200, 100);
				ImagePanel ipanel = new ImagePanel(img, gd.getIDstring().substring(1), size);
				ipanel.setName(gd.getIDstring());
				ipanel.addMouseListener(monitorPanelClicked);
				
				this.markMonitorPanel(ipanel, (scm.getCurrentMonitor() == gd));
				
				panelMonitors.add(ipanel);
				monitorPanels.add(ipanel);
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
		} else {
			panel.setBlur(true);
			panel.setBorder(BorderFactory.createEmptyBorder());
		}
	}

	
	private void addOptions(JPanel panel) {
		SettingPanel spDelay = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.delay"));
		panel.add(configureSettingPanel(spDelay));
		SettingPanel spYPos = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.ypos"));
		panel.add(configureSettingPanel(spYPos));
		SettingPanel spInvert = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.invert"));
		panel.add(configureSettingPanel(spInvert));
		
		btnEnable = new JButton();
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
		}
	};
	
	private void enableScreenColor(boolean enable) {
		if(enable) {
			if(selectedMonitorPanel != null) {
				Main.getInstance().getMainFrame().printNotification(null, null);
				btnEnable.setText("Disable");
				
				scm.start(((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue(),	//YPos
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
	
}
