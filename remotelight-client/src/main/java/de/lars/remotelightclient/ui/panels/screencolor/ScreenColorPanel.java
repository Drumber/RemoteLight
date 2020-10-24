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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.screencolor.ScreenColorManager;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.ImagePanel;
import de.lars.remotelightclient.ui.panels.MenuPanel;
import de.lars.remotelightclient.ui.panels.controlbars.DefaultControlBar;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel.SettingChangedListener;
import de.lars.remotelightclient.utils.SettingsUtil;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.notification.NotificationType;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingDouble;
import de.lars.remotelightcore.settings.types.SettingInt;

public class ScreenColorPanel extends MenuPanel {
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
		scm = (ScreenColorManager) RemoteLightCore.getInstance().getScreenColorManager();
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
		
		JScrollPane scrollPaneMonitors = new JScrollPane(panelMonitors);
		scrollPaneMonitors.setViewportBorder(null);
		scrollPaneMonitors.setBorder(BorderFactory.createEmptyBorder());
		scrollPaneMonitors.getVerticalScrollBar().setUnitIncrement(16);
		bgrMonitors.add(scrollPaneMonitors, BorderLayout.CENTER);
		
		size = new Dimension(Integer.MAX_VALUE, 1000);
		JPanel bgrOptions = new JPanel();
		bgrOptions.setPreferredSize(size);
		bgrOptions.setBackground(Style.panelBackground);
		add(bgrOptions);
		bgrOptions.setLayout(new BorderLayout(0, 0));
		
		panelOptions = new JPanel();
		panelOptions.setBorder(new EmptyBorder(5, 20, 0, 0));
		panelOptions.setBackground(Style.panelBackground);
		panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(panelOptions);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(8);
		bgrOptions.add(scrollPane, BorderLayout.CENTER);
		
		this.addOptions();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				addMonitorsPanel();
			}
		});
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
			Main.getInstance().showNotification(NotificationType.ERROR, i18n.getString("ScreenColorPanel.CouldNotAddMonitor"));
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
			drawOverlayRect();
		} else {
			panel.setBlur(true);
			panel.setBorder(BorderFactory.createEmptyBorder());
		}
	}

	private void drawOverlayRect() {
		for(ImagePanel ip : monitorPanels) {
			if(ip.getName().equals(scm.getCurrentMonitor().getIDstring())) {
				Rectangle scanArea = scm.getScanArea();
				int y = scanArea.y / MONITOR_SCALE;
				int x = scanArea.x / MONITOR_SCALE;
				int w = scanArea.width / MONITOR_SCALE;
				int h = scanArea.height / MONITOR_SCALE;
				
				ip.enableLine(x, y, w, h);
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
		
		SettingPanel spX = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.area.x")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spX));
		settingPanels.add(spX);
		SettingPanel spY = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.area.y")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spY));
		settingPanels.add(spY);
		SettingPanel spW = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.area.width")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spW));
		settingPanels.add(spW);
		SettingPanel spH = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.area.height")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spH));
		settingPanels.add(spH);
		
		SettingPanel spInvert = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.invert")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spInvert));
		settingPanels.add(spInvert);
		
		btnEnable = new JButton();
		UiUtils.configureButtonWithBorder(btnEnable, Style.accent);
		Dimension size = new Dimension(100, 25);
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
		
		JLabel lblAdvanced = new JLabel("Advanced options");
		lblAdvanced.setForeground(Style.textColor);
		panel.add(Box.createVerticalStrut(20));
		panel.add(lblAdvanced);
		
		SettingPanel spBrghtThreshold = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.filter.brightness.threshold")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spBrghtThreshold));
		settingPanels.add(spBrghtThreshold);
		SettingPanel spSaturationMulplr = SettingsUtil.getSettingPanel(sm.getSettingFromId("screencolor.filter.saturation.multiplier")); //$NON-NLS-1$
		panel.add(configureSettingPanel(spSaturationMulplr));
		settingPanels.add(spSaturationMulplr);
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
			drawOverlayRect();
			
			scm.setBrightnessThreshold(((SettingInt) sm.getSettingFromId("screencolor.filter.brightness.threshold")).get());
			scm.setSaturationMultiplier(((SettingDouble) sm.getSettingFromId("screencolor.filter.saturation.multiplier")).get());
			
			if(scm.getCurrentMonitor() != null) {
				// set max settings values
				Rectangle bounds = scm.getCurrentMonitor().getDefaultConfiguration().getBounds();
				Rectangle area = scm.getScanArea();
				
				((SettingInt) sm.getSettingFromId("screencolor.area.x")).setMax(bounds.width - area.width);
				((SettingInt) sm.getSettingFromId("screencolor.area.width")).setMax(bounds.width - area.x);
				((SettingInt) sm.getSettingFromId("screencolor.area.y")).setMax(bounds.height - area.height);
				((SettingInt) sm.getSettingFromId("screencolor.area.height")).setMax(bounds.height - area.y);
				
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
				// start screen color
				scm.start();
				
			} else {
				Main.getInstance().showNotification(NotificationType.ERROR, i18n.getString("ScreenColorPanel.NeedSelectMonitor"));
			}
		} else {
			btnEnable.setText(i18n.getString("ScreenColorPanel.Enable")); //$NON-NLS-1$
			scm.stop();
		}
	}
	
	private void setScreenColorSettings() {
		scm.setInverted(((SettingBoolean) sm.getSettingFromId("screencolor.invert")).get()); //$NON-NLS-1$
		scm.setDelay(((SettingInt) sm.getSettingFromId("screencolor.delay")).get()); //$NON-NLS-1$
		scm.setScanArea(
				((SettingInt) sm.getSettingFromId("screencolor.area.x")).get(),
				((SettingInt) sm.getSettingFromId("screencolor.area.y")).get(),
				((SettingInt) sm.getSettingFromId("screencolor.area.width")).get(),
				((SettingInt) sm.getSettingFromId("screencolor.area.height")).get());
	}
	
	@Override
	public String getName() {
		return i18n.getString("Basic.ScreenColor");
	}
	
}
