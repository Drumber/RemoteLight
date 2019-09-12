package de.lars.remotelightclient.ui.panels.settings;

import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.settings.types.SettingDouble;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingString;
import de.lars.remotelightclient.ui.MainFrame;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.MainFrame.NotificationType;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingBooleanPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingColorPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingDoublePanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingIntPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingSelectionPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingStringPanel;
import de.lars.remotelightclient.ui.Style;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class SettingsPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3954953325346082615L;
	private SettingsManager sm;
	private MainFrame mainFrame;
	private List<SettingPanel> settingPanels;

	/**
	 * Create the panel.
	 */
	public SettingsPanel(MainFrame mainFrame , SettingsManager sm) {
		this.mainFrame = mainFrame;
		this.sm = sm;
		mainFrame.showControlBar(false);
		settingPanels = new ArrayList<SettingPanel>();
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout(0, 0));
		
		JPanel main = new JPanel();
		main.setBackground(Style.panelBackground);
		main.setAlignmentX(Component.LEFT_ALIGNMENT);
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.add(getSettingsBgr(SettingCategory.General, "General"));
		main.add(getSettingsBgr(SettingCategory.Others, "Others"));
		
		JButton btnSave = new JButton("Save");
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setFocusPainted(false);
        btnSave.setOpaque(true);
        btnSave.setBackground(Style.buttonBackground);
        btnSave.setForeground(Style.textColor);
        btnSave.addMouseListener(buttonHoverListener);
        btnSave.addActionListener(btnSaveListener);
		add(btnSave, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane(main);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);
		
		this.updateUI();
	}
	
	private JPanel getSettingsBgr(SettingCategory category, String title) {
		JPanel bgr = new JPanel();
		bgr.setBorder(new EmptyBorder(10, 10, 10, 0));
		bgr.setBackground(Style.panelBackground);
		bgr.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgr.setLayout(new BoxLayout(bgr, BoxLayout.Y_AXIS));
		
		JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
		lblTitle.setFont(Style.getFontBold(12));
		lblTitle.setForeground(Style.accent);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgr.add(lblTitle);
		
		for(Setting s : sm.getSettingsFromCategory(category)) {
			SettingPanel spanel = this.getSettingPanel(s);
			spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
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
	
	private ActionListener btnSaveListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//save values
			for(SettingPanel sp : settingPanels) {
				sp.setValue();
			}
			//repaint ui
			mainFrame.updateFrame();
			//display settings
			mainFrame.displayPanel(new SettingsPanel(mainFrame, Main.getInstance().getSettingsManager()));
			mainFrame.printNotification("Saved settings", NotificationType.Info);
		}
	};
	
	@Override
	public void onEnd(MenuPanel newPanel) {
		//save values
		for(SettingPanel sp : settingPanels) {
			sp.setValue();
		}
		super.onEnd(newPanel);
	}

}
