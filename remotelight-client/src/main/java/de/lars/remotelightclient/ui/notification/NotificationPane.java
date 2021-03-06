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

package de.lars.remotelightclient.ui.notification;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.notification.Notification;
import de.lars.remotelightcore.notification.listeners.NotificationOptionListener;

public class NotificationPane extends JPanel {
	private static final long serialVersionUID = 7418038730375486184L;
	
	public static final int DEFAULT_WIDTH = 240;
	public static final int DEFAULT_HEIGHT = 80;
	
	private Dimension size;
	private Notification noti;
	private NotificationDisplayHandler handler;
	
	/** options text color */
	public static Color colorHyperlink = new Color(88, 157, 246);
	/** padding between border and components */
	private int padding = 5;
	/** horizontal space between components */
	private int space = 2;
	/** if mouse entered the pane */
	private boolean isFocussed;
	
	// swing components
	protected JLabel lblTitle;
	protected JTextArea taMessage;
	protected JLabel[] lblOptions;
	protected JLabel lblClose;
	
	public NotificationPane(Notification notification, NotificationDisplayHandler handler) {
		this.noti = notification;
		this.handler = handler;
		this.size = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setSize(size);
		
		this.initLayout();
		this.placeComponents();
	}
	
	protected void initLayout() {
		// absolute layout
		setLayout(null);
		setBorder(new LineBorder(Style.getNotificationColor(noti.getNotificationType())));
		addMouseListener(mouseListener);
		
		lblTitle = new JLabel(noti.getTitle());
		taMessage = new JTextArea(noti.getMessage());
		taMessage.setLineWrap(true);
		taMessage.setWrapStyleWord(true);
		taMessage.setCaretPosition(0);
		taMessage.setEditable(false);
		taMessage.addMouseListener(mouseListener);
		
		lblClose = new JLabel("X"); // TODO replace with font icon
		lblClose.setVisible(false);
		lblClose.setToolTipText("Hide notification");
		lblClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblClose.addMouseListener(closeClickListener);
		lblClose.addMouseListener(mouseListener);
		
		String[] options = noti.getOptions();
		if(options != null && options.length > 0) {
			lblOptions = new JLabel[noti.getOptions().length];
			for(int i = 0; i < options.length; i++) {
				JLabel lbl = new JLabel(options[i]);
				lbl.setName(options[i]);
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				lbl.setForeground(colorHyperlink);
				lbl.addMouseListener(optionClickListener);
				lbl.addMouseListener(mouseListener);
				lblOptions[i] = lbl;
			}
		}
		
		// set colors
		UiUtils.bindBackground(this, Style.panelAccentBackground());
		UiUtils.bindForeground(taMessage, Style.textColorDarker());
		UiUtils.bindBackground(taMessage, Style.panelAccentBackground());
	}
	
	protected void placeComponents() {
		removeAll();
		
		int closeWidth = lblClose.getPreferredSize().width;
		int labelHeight = 14;
		int optionsHeight = lblOptions == null ? 0 : 14;
		
		lblClose.setBounds(size.width-padding-closeWidth, padding, closeWidth, labelHeight);
		lblClose.setVerticalAlignment(SwingConstants.TOP);
		add(lblClose);
		
		lblTitle.setBounds(padding, padding, size.width-2*padding-closeWidth, labelHeight);
		lblTitle.setVerticalAlignment(SwingConstants.TOP);
		add(lblTitle);
		
		int messageHeight = size.height - 2*padding - labelHeight - 2*space - optionsHeight;
		
		JScrollPane scrollText = new JScrollPane(taMessage, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollText.setBackground(getBackground());
		scrollText.getVerticalScrollBar().setPreferredSize(new Dimension(2, messageHeight));
		scrollText.setViewportBorder(null);
		scrollText.setBorder(BorderFactory.createEmptyBorder());
		scrollText.setBounds(padding, padding + labelHeight + space, size.width-2*padding, messageHeight);
		add(scrollText);
		
		// place options
		if(lblOptions != null) {
			int y = size.height - padding;
			int nextXOffset = padding; // right border x offset for next option element
			for(int i = lblOptions.length - 1; i >= 0; --i) {
				JLabel lbl = lblOptions[i];
				int w = lbl.getPreferredSize().width; // label width
				int x = size.width - nextXOffset - w;
				
				lbl.setBounds(x, y - optionsHeight, w, optionsHeight);
				lbl.setVerticalAlignment(SwingConstants.BOTTOM);
				add(lbl);
				
				nextXOffset += 2*padding + w;
			}
		}
	}
	
	protected MouseAdapter mouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			setBorder(new LineBorder(Style.accent().get()));
			lblClose.setVisible(true);
			isFocussed = true;
			if(handler != null && handler.timer != null) {
				// reset timer when user moved mouse over notification
				handler.resetNotificationTimer();
			}
		};
		
		@Override
		public void mouseExited(MouseEvent e) {
			setBorder(new LineBorder(Style.getNotificationColor(noti.getNotificationType())));
			lblClose.setVisible(false);
			isFocussed = false;
		};
	};
	
	
	protected MouseAdapter closeClickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			setVisible(false);
			if(handler != null)
				handler.hideNotification();
		};
	};
	
	
	protected MouseAdapter optionClickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			JComponent s = (JComponent) e.getSource();
			String name = s.getName();
			String[] options = noti.getOptions();
			for(int i = 0; i < options.length; i++) {
				if(options[i].equals(name)) {
					// fire option click event
					NotificationOptionListener listener = noti.getOptionListener();
					if(listener != null)
						listener.onOptionClicked(name, i);
					
					// hide notification on option click
					if(noti.isHideOnOptionClick()) {
						if(handler != null)
							handler.hideNotification();
					}
				}
			}
		};
	};
	

	public Dimension getSize() {
		return size;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}
	
	public Notification getNotification() {
		return noti;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public void setSpace(int space) {
		this.space = space;
	}
	
	public boolean isFocussed() {
		return isFocussed;
	}

	@Override
	public Dimension getPreferredSize() {
		return size;
	}
	
	@Override
	public Dimension getMaximumSize() {
		return size;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return size;
	}

}
