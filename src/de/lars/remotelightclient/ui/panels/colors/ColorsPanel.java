package de.lars.remotelightclient.ui.panels.colors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.UiUtils;
import de.lars.remotelightclient.utils.WrapLayout;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ColorsPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2572544853394733969L;
	private Color[] colors = {Color.ORANGE, Color.RED, Color.MAGENTA, Color.GREEN, Color.BLUE, Color.CYAN, Color.WHITE, Color.BLACK};
	private SettingsManager sm = Main.getInstance().getSettingsManager();
	private final int STEP_SIZE = 10;
	private JPanel bgrColors;
	private JLabel lblCurrentSize;
	private JButton btnMinus, btnPlus;

	/**
	 * Create the panel.
	 */
	public ColorsPanel() {
		sm.addSetting(new SettingObject("colorspanel.colors", null, colors)); //register setting if not already registered
		colors = (Color[]) sm.getSettingObject("colorspanel.colors").getValue();
		CustomColorPanel.reset();
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);
		
		bgrColors = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.LEFT);
		bgrColors.setLayout(wlayout);
		bgrColors.setBackground(Style.panelBackground);
		scrollPane.setViewportView(bgrColors);
		
		JPanel bgrMenu = new JPanel();
		bgrMenu.setBackground(Style.panelDarkBackground);
		add(bgrMenu, BorderLayout.SOUTH);
		bgrMenu.setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtons = new JPanel();
		panelButtons.setBackground(Style.panelDarkBackground);
		bgrMenu.add(panelButtons, BorderLayout.WEST);
		
		JPanel panelSize = new JPanel();
		panelSize.setBackground(Style.panelDarkBackground);
		bgrMenu.add(panelSize, BorderLayout.EAST);
		
		btnMinus = new JButton("-");
		btnMinus.setName("minus");
		btnMinus.addActionListener(btnListener);
		UiUtils.configureButton(btnMinus);
		panelSize.add(btnMinus);
		
		lblCurrentSize = new JLabel("50%");
		lblCurrentSize.setName("50");
		lblCurrentSize.setForeground(Style.textColor);
		panelSize.add(lblCurrentSize);
		
		btnPlus = new JButton("+");
		btnPlus.setName("plus");
		btnPlus.addActionListener(btnListener);
		UiUtils.configureButton(btnPlus);
		panelSize.add(btnPlus);
		
		addColorPanels();
	}
	
	public void addColorPanels() {
		bgrColors.removeAll();
		for(Color c : colors) {
			bgrColors.add(new CustomColorPanel(c));
		}
		this.updateUI();
	}
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		sm.getSettingObject("colorspanel.colors").setValue(CustomColorPanel.getAllBackgroundColors());
		super.onEnd(newPanel);
	}
	
	private ActionListener btnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			
			switch (btn.getName()) {
			case "minus":
				changePanelSize(-STEP_SIZE);
				break;
			case "plus":
				changePanelSize(STEP_SIZE);
				break;
			}
		}
	};
	
	
	private void changePanelSize(int factor) {
		Dimension curSize = CustomColorPanel.getPanelSize();
		int label = Integer.parseInt(lblCurrentSize.getName());
		
		if(label >= 0 && label <= 100) {
			int newValue = label + factor;
			if(newValue >= 0 && newValue <= 100) {
				CustomColorPanel.setPanelSize(new Dimension(curSize.width + factor, curSize.height + factor));
				addColorPanels();
				
				lblCurrentSize.setText(String.valueOf(newValue) + "%");
				lblCurrentSize.setName(String.valueOf(newValue));
			}
		}
	}

}
