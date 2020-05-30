package de.lars.remotelightclient.ui.comps;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.notification.Notification;

public class NotificationPane extends JPanel {
	private static final long serialVersionUID = 7418038730375486184L;
	
	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 60;
	
	private Dimension size;
	private Notification noti;
	
	/** options text color */
	public static Color colorHyperlink = new Color(88, 157, 246);
	/** padding between border and components */
	private int padding = 5;
	/** horizontal space between components */
	private int space = 2;
	
	// swing components
	protected JLabel lblTitle;
	protected JLabel lblMessage;
	protected JLabel[] lblOptions;
	
	public NotificationPane(Notification notification) {
		this.noti = notification;
		this.size = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setSize(size);
		
		this.initLayout();
		this.placeComponents();
	}
	
	protected void initLayout() {
		// absolute layout
		setLayout(null);
		
		lblTitle = new JLabel(noti.getTitle());
		lblMessage = new JLabel(noti.getMessage());
		
		String[] options = noti.getOptions();
		if(options != null && options.length > 0) {
			lblOptions = new JLabel[noti.getOptions().length];
			for(int i = 0; i < options.length; i++) {
				JLabel lbl = new JLabel(options[i]);
				lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				lbl.setForeground(colorHyperlink);
				lblOptions[i] = lbl;
			}
		}
		
		// set colors
		setBackground(Style.panelAccentBackground);
		lblTitle.setForeground(Style.textColor);
		lblMessage.setForeground(Style.textColorDarker);
	}
	
	protected void placeComponents() {
		removeAll();
		
		lblTitle.setLocation(padding, padding);
		add(lblTitle);
		lblMessage.setLocation(padding, lblTitle.getHeight() + space);
		add(lblMessage);
		
		// place options
		if(lblOptions != null) {
			int y = size.height - padding;
			int nextXOffset = padding; // right border x offset for next option element
			for(int i = lblOptions.length - 1; i >= 0; --i) {
				JLabel lbl = lblOptions[i];
				int w = lbl.getWidth(); // label width
				int x = size.width - nextXOffset - w;
				
				lbl.setLocation(x, y - lbl.getHeight());
				add(lbl);
				
				nextXOffset += space + w;
			}
		}
	}
	
	

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
	
	@Override
	public void update(Graphics g) {
		this.placeComponents(); // update component positions
		super.update(g);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		noti.setDisplayed(true);
		super.paintComponent(g);
	}

}
