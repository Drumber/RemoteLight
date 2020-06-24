/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui.panels.screencolor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.ImagePanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;
import de.lars.remotelightclient.utils.SettingsUtil;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.screencolor.ScreenColorManager;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;

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
		scm = RemoteLightCore.getInstance().getScreenColorManager();
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
		panelMonitors.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), i18n.getString("ScreenColorPanel.SelectMonitor"), TitledBorder.LEADING, TitledBorder.TOP, null, Style.accent)); //$NON-NLS-1$
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
			Logger.error(e, i18n.getString("ScreenColorPanel.CouldNotAddMonitor")); //$NON-NLS-1$
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
				int ypos = ((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue(); //$NON-NLS-1$
				int yHeight = ((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue(); //$NON-NLS-1$
				
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
		
		SettingPanel spDelay = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.delay")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spDelay));
		settingPanels.add(spDelay);
		SettingPanel spYPos = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.ypos")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spYPos));
		settingPanels.add(spYPos);
		SettingPanel spYHeight = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.yheight")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spYHeight));
		settingPanels.add(spYHeight);
		SettingPanel spInvert = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.invert")); //$NON-NLS-1$
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
			btnEnable.setText(i18n.getString("ScreenColorPanel.Disable")); //$NON-NLS-1$
		} else {
			btnEnable.setText(i18n.getString("ScreenColorPanel.Enable")); //$NON-NLS-1$
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
				int yPos = ((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue(); //$NON-NLS-1$
				int yHeight = ((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue(); //$NON-NLS-1$
				
				((SettingInt) sm.getSettingFromId("screencolor.ypos")).setMax(height - yHeight); //$NON-NLS-1$
				((SettingInt) sm.getSettingFromId("screencolor.yheight")).setMax(height - yPos); //$NON-NLS-1$
				
				for(SettingPanel sp : settingPanels) {
					sp.updateComponents();
				}
			}
		}
	};
	
	private void enableScreenColor(boolean enable) {
		if(enable) {
			if(selectedMonitorPanel != null) {
				btnEnable.setText(i18n.getString("ScreenColorPanel.Disable")); //$NON-NLS-1$
				
				scm.start(((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue(),	//YPos //$NON-NLS-1$
						((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue(),	//YHeight //$NON-NLS-1$
						((SettingInt) sm.getSettingFromId("screencolor.delay")).getValue(),		//Delay //$NON-NLS-1$
						((SettingBoolean) sm.getSettingFromId("screencolor.invert")).getValue(),//Invert //$NON-NLS-1$
						ScreenColorManager.getMonitorByID(selectedMonitorPanel.getName()));		//Monitor
				
			} else {
				Main.getInstance().showNotification(NotificationType.ERROR, i18n.getString("ScreenColorPanel.NeedSelectMonitor"));
			}
		} else {
			btnEnable.setText(i18n.getString("ScreenColorPanel.Enable")); //$NON-NLS-1$
			scm.stop();
		}
	}
	
	private void setScreenColorSettings() {
		scm.setInverted(((SettingBoolean) sm.getSettingFromId("screencolor.invert")).getValue()); //$NON-NLS-1$
		scm.setDelay(((SettingInt) sm.getSettingFromId("screencolor.delay")).getValue()); //$NON-NLS-1$
		scm.setYPos(((SettingInt) sm.getSettingFromId("screencolor.ypos")).getValue()); //$NON-NLS-1$
		scm.setYHeight(((SettingInt) sm.getSettingFromId("screencolor.yheight")).getValue()); //$NON-NLS-1$
	}
	
}
