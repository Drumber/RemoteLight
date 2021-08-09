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
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.StartUp;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.panels.MenuPanel;
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
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel bgrHeader = new JPanel();
		WrapLayout wl_bgrHeader = new WrapLayout(FlowLayout.CENTER);
		bgrHeader.setLayout(wl_bgrHeader);
		add(bgrHeader);
		
		Box verticalBox = Box.createVerticalBox();
		bgrHeader.add(verticalBox);
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		JLabel lblRemoteLight = new JLabel("RemoteLight"); //$NON-NLS-1$
		lblRemoteLight.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblRemoteLight.setFont(Style.getFontBold(30));
		lblRemoteLight.setForeground(new Color(20, 110, 170));
		horizontalBox.add(lblRemoteLight);
		
		Component horizontalStrut = Box.createHorizontalStrut(5);
		horizontalBox.add(horizontalStrut);
		
		JLabel lblVersion = new JLabel(Main.VERSION);
		lblVersion.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblVersion.setFont(Style.getFontRegualar(15));
		UiUtils.bindForeground(lblVersion, Style.textColorDarker());
		lblVersion.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblVersion.setToolTipText("Click to check for updates");
		lblVersion.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// check for updates
				StartUp.checkForUpdates(true);
			}
		});
		horizontalBox.add(lblVersion);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		horizontalBox.add(horizontalStrut_1);
		
		JLabel lblCopyright = new JLabel("by Lars Obrath"); //$NON-NLS-1$
		lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(lblCopyright);
		lblCopyright.setFont(Style.getFontLight(13));
		UiUtils.bindForeground(lblCopyright, Style.textColorDarker());
		
		Component verticalStrut = Box.createVerticalStrut(8);
		verticalBox.add(verticalStrut);
		
		Box boxLinks = Box.createHorizontalBox();
		verticalBox.add(boxLinks);
		
		JLabel lblWebsite = new JLabel(i18n.getString("AboutPanel.Website")); //$NON-NLS-1$
		UiUtils.bindForeground(lblWebsite, Style.accent());
		UiUtils.addWebsiteHyperlink(lblWebsite, RemoteLightCore.WEBSITE);
		lblWebsite.setFont(Style.getFontRegualar(14));
		boxLinks.add(lblWebsite);
		boxLinks.add(Box.createHorizontalStrut(10));
		
		JLabel lblWiki = new JLabel("Wiki"); //$NON-NLS-1$
		UiUtils.bindForeground(lblWiki, Style.accent());
		UiUtils.addWebsiteHyperlink(lblWiki, RemoteLightCore.WIKI);
		lblWiki.setFont(Style.getFontRegualar(14));
		boxLinks.add(lblWiki);
		boxLinks.add(Box.createHorizontalStrut(10));
		
		JLabel lblDiscord = new JLabel("Discord");
		UiUtils.bindForeground(lblDiscord, Style.accent());
		UiUtils.addWebsiteHyperlink(lblDiscord, RemoteLightCore.DISCORD);
		lblDiscord.setFont(Style.getFontRegualar(14));
		boxLinks.add(lblDiscord);
		boxLinks.add(Box.createHorizontalStrut(10));
		
		JLabel lblGithub = new JLabel("GitHub"); //$NON-NLS-1$
		UiUtils.bindForeground(lblGithub, Style.accent());
		UiUtils.addWebsiteHyperlink(lblGithub, RemoteLightCore.GITHUB);
		lblGithub.setFont(Style.getFontRegualar(14));
		boxLinks.add(lblGithub);
		
		JPanel bgrContent = new JPanel();
		add(bgrContent);
		bgrContent.setLayout(new BorderLayout(0, 0));
		
		TScrollPane scrollPane = new TScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		bgrContent.add(scrollPane, BorderLayout.CENTER);
		
		panelContent = new JPanel();
		scrollPane.setViewportView(panelContent);
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
		
		Component verticalStrut_2 = Box.createVerticalStrut(20);
		panelContent.add(verticalStrut_2);
		
		JLabel lblCredits = new JLabel(i18n.getString("AboutPanel.Credits")); //$NON-NLS-1$
		lblCredits.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblCredits.setFont(Style.getFontBold(18));
		UiUtils.bindForeground(lblCredits, Style.accent());
		panelContent.add(lblCredits);
		
		Component verticalStrut_1 = Box.createVerticalStrut(10);
		panelContent.add(verticalStrut_1);
		
		addLabel("jSerialComm", "https://fazecast.github.io/jSerialComm/");
		addLabel("TarsosDSP", "https://github.com/JorenSix/TarsosDSP");
		addLabel("tinylog", "https://tinylog.org");
		addLabel("Gson", "https://github.com/google/gson");
		addLabel("artnet4j", "https://github.com/cansik/artnet4j");
		addLabel("Luaj", "https://sourceforge.net/projects/luaj");
		addLabel("FlatLaf", "https://github.com/JFormDesigner/FlatLaf");
		addLabel("XT-Audio", "https://sjoerdvankreel.github.io/xt-audio/");
		addLabel("jIconFont", "https://github.com/jIconFont/jiconfont-swing");
		addLabel("NanoHTTPD", "https://github.com/NanoHttpd/nanohttpd");
	}
	
	private void addLabel(String name, String url) {
		JLabel lbl = new JLabel(name); //$NON-NLS-1$
		lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		UiUtils.addWebsiteHyperlink(lbl, url); //$NON-NLS-1$
		lbl.setFont(Style.getFontRegualar(15));
		panelContent.add(lbl);
	}

	@Override
	public String getName() {
		return i18n.getString("Basic.About");
	}

}
