package de.lars.remotelightclient.ui.comps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.ui.Style;

public class BigTextButton extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7302225939932211684L;
	private JPanel bgrText;
	private JPanel bgrTitle;
	private JLabel lblText;
	private JLabel lblTitle;
	
	private Color bg = Style.buttonBackground;

	/**
	 * Create the panel.
	 */
	public BigTextButton(String title, String text) {
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(100, 100));
		
		bgrText = new JPanel();
		bgrText.setBackground(Style.buttonBackground);
		add(bgrText, BorderLayout.SOUTH);
		
		lblText = new JLabel(text);
		lblText.setForeground(Style.textColorDarker);
		bgrText.add(lblText);
		
		bgrTitle = new JPanel();
		bgrTitle.setBackground(Style.buttonBackground);
		add(bgrTitle, BorderLayout.CENTER);
		bgrTitle.setLayout(new BorderLayout(0, 0));
		
		lblTitle = new JLabel(title, SwingConstants.CENTER);
		lblTitle.setForeground(Style.textColor);
		lblTitle.setFont(Style.getFontBold(12));
		bgrTitle.add(lblTitle);
		
		addMouseListener(buttonHoverListener);
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			bgrText.setBackground(Style.hoverBackground);
			bgrTitle.setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			bgrText.setBackground(bg);
			bgrTitle.setBackground(bg);
		}
	};
	
	
	public void setText(String s) {
		lblText.setText(s);
	}
	
	public String getText() {
		return lblText.getText();
	}
	
	public void setTextColor(Color c) {
		lblText.setForeground(c);
	}
	
	public Color getTextColor() {
		return lblText.getForeground();
	}
	
	public void setTitle(String s) {
		lblTitle.setText(s);
	}
	
	public String getTitle() {
		return lblTitle.getText();
	}
	
	public void setTitleColor(Color c) {
		lblTitle.setForeground(c);
	}
	
	public Color getTitleColor() {
		return lblTitle.getForeground();
	}
	
}
