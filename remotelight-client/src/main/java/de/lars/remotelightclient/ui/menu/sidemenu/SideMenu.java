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

package de.lars.remotelightclient.ui.menu.sidemenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.util.Animator;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.utils.maths.MathHelper;

public abstract class SideMenu extends JPanel {
	private static final long serialVersionUID = -1648378491673862404L;
	
	protected final MainFrame mainFrame;
	protected JPanel root;
	
	protected static Animator animator;
	public static int animationDuration = 150;
	
	public SideMenu(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		
		setLayout(new BorderLayout());
		
		root = new JPanel();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		
		TScrollPane scrollPane = new TScrollPane(root);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(8);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, root.getHeight()));
		add(scrollPane, BorderLayout.CENTER);
		
		this.addPropertyChangeListener("UI", e -> updateMenu());
		updateBackground();
		
		// add all menu items
		addMenuItems();
	}
	
	/**
	 * Update the background colors of the side menu.
	 */
	public void updateBackground() {
		Color background = isUnifiedBackground() ? Style.panelBackground().get() : Style.panelAccentBackground().get();
		this.setBackground(background);
		root.setBackground(background);
	}
	
	/**
	 * Check if the background color should have the default panel background or an accent background.
	 * @return	true if the background color should be the default panel color.
	 */
	protected boolean isUnifiedBackground() {
		return Main.getInstance().getSettingsManager().getSetting(SettingBoolean.class, "ui.sidemenu.unified").get();
	}

	/**
	 * Will update the side menu and remove and re-add all menu items.
	 */
	public void updateMenu() {
		root.removeAll();
		updateBackground();
		addMenuItems();
		root.revalidate();
		root.repaint();
	}
	
	protected abstract void addMenuItems();
	
	protected void configureMenuButton(JButton btn) {
		btn.setForeground(Style.textColor().get());
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setFocusable(false);
        btn.setBackground(null);
        btn.setMaximumSize(new Dimension(150, 30));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(buttonHoverListener);
        btn.addActionListener(buttonActionListener);
        btn.setRolloverEnabled(false);
        if(mainFrame.getSelectedMenu().equals(btn.getName())) {
        	btn.setBackground(Style.accent().get());
        }
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			if(!mainFrame.getSelectedMenu().equals(btn.getName())) {
				btn.setBackground(Style.hoverBackground().get());
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			if(!mainFrame.getSelectedMenu().equals(btn.getName())) {
				btn.setBackground(null);
			}
		}
	};
	
	private ActionListener buttonActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			onMenuItemClicked(btn, btn.getName());
		}
	};
	
	protected void onMenuItemClicked(JButton btn, String name) {
		if(!name.equals("extend")) {
			UiUtils.getComponentByName(root, new JButton(), mainFrame.getSelectedMenu()).setBackground(null); //reset background of previous selected button
			btn.setBackground(Style.accent().get());
			mainFrame.setSelectedMenu(btn.getName());
			mainFrame.showMenuPanel(btn.getName());
		}
	}
	
	
	public void animateExpand(SideMenu target) {
		if(animator != null) return;
		
		final int startWidth = getPreferredSize().width;
		final int endWidth = target.getPreferredSize().width;
		
		animator = new Animator(animationDuration, fraction -> {
			int width = (int) MathHelper.map(fraction, 0.0, 1.0, startWidth, endWidth);
			setPreferredSize(new Dimension(width, getPreferredSize().height));
			revalidate();
			repaint();
		}, () -> {
			animator = null;
			mainFrame.replaceSideMenu(target);
		});
		
		animator.setResolution(10);
		animator.start();
	}

}
