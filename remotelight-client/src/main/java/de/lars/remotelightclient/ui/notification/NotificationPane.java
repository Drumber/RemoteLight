package de.lars.remotelightclient.ui.notification;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.LineBorder;

import de.lars.remotelightclient.ui.Style;
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
		setBorder(new LineBorder(Color.gray));
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
		lblClose.setForeground(Style.textColor);
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
		setBackground(Style.panelAccentBackground);
		lblTitle.setForeground(Style.textColor);
		taMessage.setForeground(Style.textColorDarker);
		taMessage.setBackground(getBackground());
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
			setBorder(new LineBorder(Style.accent));
			lblClose.setVisible(true);
			isFocussed = true;
			if(handler != null && handler.timer != null) {
				// reset timer
				handler.timer.setInitialDelay(handler.timer.getDelay());
				handler.timer.restart();
				handler.timer.setInitialDelay(0);
			}
		};
		
		@Override
		public void mouseExited(MouseEvent e) {
			setBorder(new LineBorder(Color.gray));
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
