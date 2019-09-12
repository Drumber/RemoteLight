package de.lars.remotelightclient.ui.panels.musicsync;

import javax.swing.JPanel;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicSyncManager;
import de.lars.remotelightclient.settings.Setting;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.settings.types.SettingDouble;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.settings.types.SettingObject;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingString;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingBooleanPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingColorPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingDoublePanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingIntPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingSelectionPanel;
import de.lars.remotelightclient.ui.panels.settings.settingComps.SettingStringPanel;
import de.lars.remotelightclient.utils.UiUtils;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JSlider;
import java.awt.GridLayout;

public class MusicSyncOptionsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -100112066309451958L;
	private List<SettingPanel> settingPanels;
	private SettingsManager sm = Main.getInstance().getSettingsManager();
	private MusicSyncManager msm = Main.getInstance().getMusicSyncManager();
	private JPanel bgrEffectOptions;
	private JPanel bgrEffectOptionsScroll;
	private JPanel panelSensitivity;
	private JLabel lblSensitivity;
	private JSlider sliderSensitivity;
	private JPanel panelAdjustment;
	private JLabel lblAdjustment;
	private JSlider sliderAdjustment;

	/**
	 * Create the panel.
	 */
	public MusicSyncOptionsPanel() {
		settingPanels = new ArrayList<SettingPanel>();
		sm.addSetting(new SettingObject("musicsync.sensitivity", null, 20));
		
		Dimension size = new Dimension(Integer.MAX_VALUE, 120);
		setPreferredSize(size);
		setMaximumSize(size);
		setBackground(Style.panelDarkBackground);
		setAlignmentY(Component.TOP_ALIGNMENT);
		
		setLayout(new GridLayout(0, 2, 0, 0));
		JPanel bgrOptions = new JPanel();
		bgrOptions.setLayout(new BoxLayout(bgrOptions, BoxLayout.Y_AXIS));
		bgrOptions.setAlignmentY(Component.TOP_ALIGNMENT);
		bgrOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgrOptions.setBackground(Style.panelDarkBackground);
		add(bgrOptions);
		
		panelSensitivity = new JPanel();
		panelSensitivity.setLayout(new BoxLayout(panelSensitivity, BoxLayout.Y_AXIS));
		panelSensitivity.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelSensitivity.setAlignmentY(Component.TOP_ALIGNMENT);
		panelSensitivity.setBackground(Style.panelDarkBackground);
		bgrOptions.add(panelSensitivity);
		
		lblSensitivity = new JLabel("Sensitivity");
		lblSensitivity.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblSensitivity.setForeground(Style.textColor);
		panelSensitivity.add(lblSensitivity);
		
		size = new Dimension(180, 20);
		
		sliderSensitivity = new JSlider();
		sliderSensitivity.setPreferredSize(size);
		sliderSensitivity.setMaximumSize(size);
		sliderSensitivity.setMinimum(10);
		sliderSensitivity.setMaximum(300);
		sliderSensitivity.setFocusable(false);
		sliderSensitivity.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderSensitivity.setBackground(Style.panelDarkBackground);
		sliderSensitivity.setName("sensitivity");
		sliderSensitivity.addChangeListener(sliderListener);
		UiUtils.addSliderMouseWheelListener(sliderSensitivity);
		sliderSensitivity.setValue((int) sm.getSettingObject("musicsync.sensitivity").getValue());
		panelSensitivity.add(sliderSensitivity);
		
		panelAdjustment = new JPanel();
		panelAdjustment.setLayout(new BoxLayout(panelAdjustment, BoxLayout.Y_AXIS));
		panelAdjustment.setAlignmentY(Component.TOP_ALIGNMENT);
		panelAdjustment.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelAdjustment.setBackground(Style.panelDarkBackground);
		bgrOptions.add(panelAdjustment);
		
		lblAdjustment = new JLabel("Adjustment");
		lblAdjustment.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblAdjustment.setForeground(Style.textColor);
		panelAdjustment.add(lblAdjustment);
		
		sliderAdjustment = new JSlider();
		sliderAdjustment.setPreferredSize(size);
		sliderAdjustment.setMaximumSize(size);
		sliderAdjustment.setFocusable(false);
		sliderAdjustment.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderAdjustment.setBackground(Style.panelDarkBackground);
		sliderAdjustment.setName("adjustment");
		sliderAdjustment.addChangeListener(sliderListener);
		UiUtils.addSliderMouseWheelListener(sliderAdjustment);
		panelAdjustment.add(sliderAdjustment);
		
		bgrEffectOptionsScroll = new JPanel();
		bgrEffectOptionsScroll.setVisible(false);
		add(bgrEffectOptionsScroll);
		bgrEffectOptionsScroll.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		bgrEffectOptionsScroll.add(scrollPane);
		
		bgrEffectOptions = new JPanel();
		bgrEffectOptions.setBackground(Style.panelDarkBackground);
		scrollPane.setViewportView(bgrEffectOptions);
		bgrEffectOptions.setLayout(new BoxLayout(bgrEffectOptions, BoxLayout.Y_AXIS));
	}
	
	
	private ChangeListener sliderListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider slider = (JSlider) e.getSource();
			
			if(slider.getName().equals("sensitivity")) {
				sm.getSettingObject("musicsync.sensitivity").setValue(slider.getValue());
				msm.setSensitivity(slider.getValue() / 100.0);
				
			} else if(slider.getName().equals("adjustment")) {
				//TODO
				
			}
		}
	};
	
	
	public void addMusicEffectOptions(List<Setting> settings) {
		bgrEffectOptionsScroll.setVisible(true);
		bgrEffectOptions.removeAll();
		
		JLabel lblTitle = new JLabel("Effect options", SwingConstants.LEFT);
		lblTitle.setFont(Style.getFontBold(11));
		lblTitle.setForeground(Style.accent);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		bgrEffectOptions.add(lblTitle);
		
		for(Setting s : settings) {
			SettingPanel spanel = this.getSettingPanel(s);
			spanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			spanel.addMouseListener(effectOptionsChangeListener);
			bgrEffectOptions.add(spanel);
			settingPanels.add(spanel);
		}
		updateUI();
	}
	
	public void removeMusicEffectOptions() {
		bgrEffectOptions.removeAll();
		bgrEffectOptionsScroll.setVisible(false);
		updateUI();
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

	private MouseAdapter effectOptionsChangeListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			for(SettingPanel sp : settingPanels) {
				sp.setValue();
			}
		}
	};
	
}
