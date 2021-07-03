package de.lars.remotelightclient.ui.panels.settings;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;

import org.tinylog.Logger;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingString;

public class ThemeSettingsPanel extends JPanel {
	private static final long serialVersionUID = 7726418702255797582L;
	
	private SettingsManager sm;
	private SettingString sSelectedTheme;
	private SettingString sThemeFilter;
	
	private JList<LookAndFeelInfo> listThemes;
	
	public ThemeSettingsPanel() {
		sm = Main.getInstance().getSettingsManager();
		sSelectedTheme = sm.getSetting(SettingString.class, "ui.theme");
		sThemeFilter = sm.addSetting(new SettingString("settings.themes.filter", null, SettingCategory.Intern, null, "All"));
		
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel lblTitle = new JLabel("Application Theme");
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblTitle.setFont(Style.getFontRegualar(14));
		lblTitle.setForeground(Style.textColor);
		lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(lblTitle);
		
		JComboBox<String> boxFilter = new JComboBox<>(new DefaultComboBoxModel<>(new String[] {"All", "Light", "Dark"}));
		boxFilter.setSelectedItem(sThemeFilter.get());
		boxFilter.setMaximumSize(new Dimension(300, boxFilter.getPreferredSize().height));
		boxFilter.addActionListener(e -> {
			sThemeFilter.set(((String)boxFilter.getSelectedItem()));
			updateThemeList();
		});
		
		JPanel panelFilter = new JPanel();
		panelFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelFilter.setLayout(new BoxLayout(panelFilter, BoxLayout.X_AXIS));
		panelFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, boxFilter.getPreferredSize().height));
		panelFilter.add(Box.createHorizontalGlue());
		panelFilter.add(boxFilter);
		add(panelFilter);
		
		listThemes = new JList<>();
		listThemes.setCellRenderer(themeListCellRenderer);
		listThemes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listThemes.addListSelectionListener(this::onThemeSelected);
		updateThemeList();
		
		TScrollPane themeScrollPane = new TScrollPane(listThemes);
		themeScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		themeScrollPane.setViewportBorder(null);
		themeScrollPane.setBorder(BorderFactory.createEmptyBorder());
		themeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		themeScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		themeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(themeScrollPane);
	}
	
	private void onThemeSelected(ListSelectionEvent event) {
		LookAndFeelInfo info = listThemes.getSelectedValue();
		if(event.getValueIsAdjusting()) return;
		applyTheme(info);
	}
	
	private void applyTheme(LookAndFeelInfo info) {
		if(info == null || UIManager.getLookAndFeel().getClass().getName().equals(info.getClassName())) return;
		
		EventQueue.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(info.getClassName());
				sSelectedTheme.set(info.getClassName());
				FlatLaf.updateUI();
			} catch (Exception e) {
				Logger.error(e, "Failed to set LookAndFeel to class '" + info.getClassName() + "'.");
			}
		});
	}
	
	
	private DefaultListCellRenderer themeListCellRenderer = new DefaultListCellRenderer() {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			String name = ((LookAndFeelInfo) value).getName();
			JComponent comp = (JComponent) super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
			return comp;
		};
	};
	
	private void updateThemeList() {
		String filterTxt = sThemeFilter.get();
		boolean lightThemes = filterTxt.equalsIgnoreCase("all") || filterTxt.equalsIgnoreCase("light");
		boolean darkThemes = filterTxt.equalsIgnoreCase("all") || filterTxt.equalsIgnoreCase("dark");
		
		List<LookAndFeelInfo> themeInfo = Style.getLookAndFeelInfo().stream()
				.filter( i -> isFlatLaf(i) && ((darkThemes && isDark(i)) || (lightThemes && !isDark(i))) )
				.sorted((i1, i2) -> i1.getName().compareToIgnoreCase(i2.getName()))
				.collect(Collectors.toList());
		
		listThemes.setModel(new AbstractListModel<LookAndFeelInfo>() {
			@Override
			public int getSize() {
				return themeInfo.size();
			}
			@Override
			public LookAndFeelInfo getElementAt(int index) {
				return themeInfo.get(index);
			}
		});
		
		// highlight selected theme
		String selectedThemeClass = sSelectedTheme.get();
		for(int i = 0; i < themeInfo.size(); i++) {
			if(themeInfo.get(i).getClassName().equals(selectedThemeClass)) {
				listThemes.setSelectedIndex(i);
				break;
			}
		}
		
		int selectedIndex = listThemes.getSelectedIndex();
		if(selectedIndex != -1) {
			Rectangle cellBounds = listThemes.getCellBounds(selectedIndex, selectedIndex);
			if(cellBounds != null) {
				listThemes.scrollRectToVisible(cellBounds);
			}
		}
	}
	
	private boolean isFlatLaf(LookAndFeelInfo info) {
		return info instanceof FlatIJLookAndFeelInfo || info.getClassName().startsWith("com.formdev.flatlaf");
	}
	
	private boolean isDark(LookAndFeelInfo info) {
		if(info instanceof FlatIJLookAndFeelInfo) {
			return ((FlatIJLookAndFeelInfo) info).isDark();
		}
		try {
			// create a new instance of the class to invoke its isDark() method
			Class<?> clazz = Class.forName(info.getClassName());
			if(clazz != null) {
				Object o = clazz.newInstance();
				if(o instanceof FlatLaf) {
					return ((FlatLaf) o).isDark();
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		}
		return false;
	}

}
