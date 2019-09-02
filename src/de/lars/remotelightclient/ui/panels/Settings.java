package de.lars.remotelightclient.ui.panels;

import javax.swing.JPanel;

import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.settings.types.SettingDouble;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingString;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.settingComps.SettingBooleanPanel;
import de.lars.remotelightclient.ui.panels.settingComps.SettingColorPanel;
import de.lars.remotelightclient.ui.panels.settingComps.SettingDoublePanel;
import de.lars.remotelightclient.ui.panels.settingComps.SettingIntPanel;
import de.lars.remotelightclient.ui.panels.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settingComps.SettingSelectionPanel;
import de.lars.remotelightclient.ui.panels.settingComps.SettingStringPanel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class Settings extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3954953325346082615L;
	private SettingsManager sm;
	private List<SettingPanel> settingPanels;

	/**
	 * Create the panel.
	 */
	public Settings(SettingsManager sm) {
		setBorder(null);
		this.sm = sm;
		settingPanels = new ArrayList<SettingPanel>();
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout(0, 0));
		
		JPanel main = new JPanel();
		main.setBackground(Style.panelBackground);
		main.setLayout(new BorderLayout(0, 0));
		main.add(getSettingsBgr());
		
		JButton btnSave = new JButton("Save");
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setFocusPainted(false);
        btnSave.setOpaque(true);
        btnSave.setBackground(Style.buttonBackground);
        btnSave.setForeground(Style.textColor);
        btnSave.addMouseListener(buttonHoverListener);
		main.add(btnSave, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane(main);
		scrollPane.setViewportBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		add(scrollPane, BorderLayout.CENTER);
		
		this.updateUI();
	}
	
	private JPanel getSettingsBgr() {
		JPanel bgr = new JPanel();
		bgr.setBorder(new EmptyBorder(10, 10, 10, 0));
		bgr.setBackground(Style.panelBackground);
		bgr.setLayout(new BoxLayout(bgr, BoxLayout.Y_AXIS));
		bgr.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setBackground(Style.panelBackground);
		panelGeneral.setLayout(new BoxLayout(panelGeneral, BoxLayout.Y_AXIS));
		bgr.add(panelGeneral);
		
		JLabel lblGeneral = new JLabel("General");
		lblGeneral.setForeground(Style.textColor);
		panelGeneral.add(lblGeneral);
		
		for(Setting s : sm.getSettingsFromCategory(SettingCategory.General)) {
			SettingPanel spanel = this.getSettingPanel(s);
			panelGeneral.add(spanel);
			settingPanels.add(spanel);
		}
		
		JLabel lblOthers = new JLabel("Others");
		lblOthers.setForeground(Style.textColor);
		bgr.add(lblOthers);
		
		for(Setting s : sm.getSettingsFromCategory(SettingCategory.Others)) {
			SettingPanel spanel = this.getSettingPanel(s);
			bgr.add(spanel);
			settingPanels.add(spanel);
		}
		
		return bgr;
	}
	
	private SettingPanel getSettingPanel(Setting s) {
		if(s instanceof SettingString) {
			return new SettingStringPanel((SettingString) s);
		}
		if(s instanceof SettingBoolean) {
			return new SettingBooleanPanel((SettingBoolean) s);
		}
		if(s instanceof SettingColor) {
			return new SettingColorPanel((SettingColor) s);
		}
		if(s instanceof SettingDouble) {
			return new SettingDoublePanel((SettingDouble) s);
		}
		if(s instanceof SettingInt) {
			return new SettingIntPanel((SettingInt) s);
		}
		if(s instanceof SettingSelection) {
			return new SettingSelectionPanel((SettingSelection) s);
		}
		return null;
	}
	
	private MouseAdapter buttonHoverListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			btn.setBackground(Style.hoverBackground);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			btn.setBackground(Style.buttonBackground);
		}
	};

}
