package de.lars.remotelightclient.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;

public class TabButtons extends JPanel {
	private static final long serialVersionUID = 1522726986114862071L;
	
	private List<JPanel> listButtons;
	private ActionListener listener;
	protected Supplier<Color> colorSelected = Style.accent();
	protected Supplier<Color> colorHover = Style.hoverBackground();
	protected int height = 30;
	
	public TabButtons() {
		listButtons = new ArrayList<JPanel>();
		UiUtils.bindBackground(this, Style.panelBackground());
		setLayout(new GridLayout(1, 0));
	}
	
	public void addButton(String name) {
		JPanel btn = createHeaderButton(name);
		btn.setName(name);
		listButtons.add(btn);
		add(btn);
	}
	
	public void removeButton(String name) {
		Iterator<JPanel> it = listButtons.iterator();
		while(it.hasNext()) {
			JPanel btn = it.next();
			if(name.equals(btn.getName())) {
				remove(btn);
				it.remove();
				break;
			}
		}
	}
	
	private JPanel createHeaderButton(String title) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(null);
		panel.setPreferredSize(new Dimension(0, height));
		panel.addMouseListener(onButtonClicked);
		UiUtils.addHoverColor(panel, null, colorHover.get());
		
		JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
		lblTitle.setFont(getFont());
		panel.add(lblTitle, BorderLayout.CENTER);
		return panel;
	}
	
	private MouseAdapter onButtonClicked = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			selectButton(e.getComponent().getName());
		};
	};
	
	public void selectButton(String name) {		
		for(JPanel btn : listButtons) {
			if(name.equals(btn.getName())) {
				btn.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Style.accent().get()));
				if(listener != null) {
					ActionEvent event = new ActionEvent(btn, ActionEvent.ACTION_PERFORMED, name);
					listener.actionPerformed(event);
				}
			} else {
				btn.setBorder(null);
			}
		}
		updateUI();
	}
	
	public void setActionListener(ActionListener l) {
		listener = l;
	}

	public Supplier<Color> getColorSelected() {
		return colorSelected;
	}

	public void setColorSelected(Supplier<Color> colorSelected) {
		this.colorSelected = colorSelected;
	}

	public Supplier<Color> getColorHover() {
		return colorHover;
	}

	public void setColorHover(Supplier<Color> colorHover) {
		this.colorHover = colorHover;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
