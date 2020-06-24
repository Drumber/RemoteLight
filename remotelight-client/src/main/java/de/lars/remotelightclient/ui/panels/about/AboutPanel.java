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

package de.lars.remotelightclient.ui.panels.about;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.*;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.RemoteLightCore;
import de.lars.remotelightcore.lang.i18n;

public class AboutPanel extends MenuPanel {
	private static final long serialVersionUID = -1227084275389857291L;
	
	JPanel panelContent;
	
	public AboutPanel() {
		MainFrame mainFrame = Main.getInstance().getMainFrame();
		mainFrame.showControlBar(false);
		
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel bgrHeader = new JPanel();
		WrapLayout wl_bgrHeader = new WrapLayout(FlowLayout.CENTER);
		bgrHeader.setLayout(wl_bgrHeader);
		bgrHeader.setBackground(Style.panelBackground);
		add(bgrHeader);
		
		Box verticalBox = Box.createVerticalBox();
		bgrHeader.add(verticalBox);
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		JLabel lblRemoteLight = new JLabel("RemoteLight"); //$NON-NLS-1$
		lblRemoteLight.setFont(Style.getFontBold(30));
		lblRemoteLight.setForeground(new Color(20, 110, 170));
		horizontalBox.add(lblRemoteLight);
		
		Component horizontalStrut = Box.createHorizontalStrut(5);
		horizontalBox.add(horizontalStrut);
		
		JLabel lblVersion = new JLabel(Main.VERSION);
		lblVersion.setFont(Style.getFontRegualar(15));
		lblVersion.setForeground(Style.textColorDarker);
		horizontalBox.add(lblVersion);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		horizontalBox.add(horizontalStrut_1);
		
		JLabel lblCopyright = new JLabel("by Lars Obrath"); //$NON-NLS-1$
		lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(lblCopyright);
		lblCopyright.setFont(Style.getFontLight(13));
		lblCopyright.setForeground(Style.textColorDarker);
		
		Component verticalStrut = Box.createVerticalStrut(8);
		verticalBox.add(verticalStrut);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox.add(horizontalBox_1);
		
		JLabel lblWebsite = new JLabel(i18n.getString("AboutPanel.Website")); //$NON-NLS-1$
		lblWebsite.setForeground(Style.accent);
		UiUtils.addWebsiteHyperlink(lblWebsite, RemoteLightCore.WEBSITE);
		lblWebsite.setFont(Style.getFontRegualar(14));
		horizontalBox_1.add(lblWebsite);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(10);
		horizontalBox_1.add(horizontalStrut_2);
		
		JLabel lblGithub = new JLabel("GitHub"); //$NON-NLS-1$
		lblGithub.setForeground(Style.accent);
		UiUtils.addWebsiteHyperlink(lblGithub, RemoteLightCore.GITHUB);
		lblGithub.setFont(Style.getFontRegualar(14));
		horizontalBox_1.add(lblGithub);
		
		JPanel bgrContent = new JPanel();
		bgrContent.setBackground(Style.panelBackground);
		add(bgrContent);
		bgrContent.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrContent.add(scrollPane, BorderLayout.CENTER);
		
		panelContent = new JPanel();
		panelContent.setBackground(Style.panelBackground);
		scrollPane.setViewportView(panelContent);
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
		
		Component verticalStrut_2 = Box.createVerticalStrut(20);
		panelContent.add(verticalStrut_2);
		
		JLabel lblCredits = new JLabel(i18n.getString("AboutPanel.Credits")); //$NON-NLS-1$
		lblCredits.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblCredits.setFont(Style.getFontBold(18));
		lblCredits.setForeground(Style.accent);
		panelContent.add(lblCredits);
		
		Component verticalStrut_1 = Box.createVerticalStrut(10);
		panelContent.add(verticalStrut_1);
		
		addLabel("jSerialComm", "https://fazecast.github.io/jSerialComm/");
		addLabel("SimpleFileStorage", "https://github.com/DeBukkIt/SimpleFileStorage");
		addLabel("TarsosDSP", "https://github.com/JorenSix/TarsosDSP");
		addLabel("tinylog", "https://tinylog.org");
		addLabel("Gson", "https://github.com/google/gson");
		addLabel("artnet4j", "https://github.com/cansik/artnet4j");
		addLabel("Luaj", "https://sourceforge.net/projects/luaj");
		addLabel("FlatLaf", "https://github.com/JFormDesigner/FlatLaf");
		addLabel("XT-Audio", "https://sjoerdvankreel.github.io/xt-audio/");
	}
	
	private void addLabel(String name, String url) {
		JLabel lbl = new JLabel(name); //$NON-NLS-1$
		lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		lbl.setForeground(Style.textColor);
		UiUtils.addWebsiteHyperlink(lbl, url); //$NON-NLS-1$
		lbl.setFont(Style.getFontRegualar(15));
		panelContent.add(lbl);
	}

}
