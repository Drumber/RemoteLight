package de.lars.remotelightclient.ui.panels.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;

import org.tinylog.Logger;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.ScrollablePanel;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.utils.ui.MenuIconFont;
import de.lars.remotelightclient.utils.ui.MenuIconFont.MenuIcon;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightcore.lang.i18n;
import de.lars.remotelightcore.settings.SettingsManager;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.settings.types.SettingSelection;
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
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton btnBack = new JButton(Style.getFontIcon(MenuIconFont.MenuIcon.BACK, 16));
		btnBack.setBackground(new Color(0, 0, 0, 0));
		btnBack.setOpaque(false);
		btnBack.addActionListener(e -> Main.getInstance().getMainFrame().showMenuPanel("settings"));
		
		JLabel lblTitle = new JLabel("Appearance");
		UiUtils.bindFont(lblTitle, Style.getFontRegualar(14));
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel panelTopBar = new JPanel();
		panelTopBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelTopBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelTopBar.add(btnBack);
		panelTopBar.add(lblTitle);
		panelTopBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelTopBar.getPreferredSize().height));
		add(panelTopBar);
		
		JComboBox<String> boxFilter = new JComboBox<>(new DefaultComboBoxModel<>(new String[] {"All", "Light", "Dark"}));
		boxFilter.setSelectedItem(sThemeFilter.get());
		boxFilter.setMaximumSize(new Dimension(300, boxFilter.getPreferredSize().height));
		boxFilter.addActionListener(e -> {
			sThemeFilter.set(((String)boxFilter.getSelectedItem()));
			updateThemeList();
		});
		
		JLabel lblTitleThemes = new JLabel("Themes");
		
		JPanel panelFilter = new JPanel();
		panelFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelFilter.setLayout(new BoxLayout(panelFilter, BoxLayout.X_AXIS));
		panelFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, boxFilter.getPreferredSize().height));
		panelFilter.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		panelFilter.add(lblTitleThemes);
		panelFilter.add(Box.createHorizontalGlue());
		panelFilter.add(boxFilter);
		
		listThemes = new JList<>();
		listThemes.setCellRenderer(themeListCellRenderer);
		listThemes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		updateThemeList();
		listThemes.addListSelectionListener(this::onThemeSelected);
		
		TScrollPane themeScrollPane = new TScrollPane(listThemes);
		themeScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		themeScrollPane.setMinimumSize(new Dimension(150, 0));
		themeScrollPane.setViewportBorder(null);
		themeScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 5));
		themeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		themeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		JPanel panelThemes = new JPanel();
		panelThemes.setLayout(new BorderLayout());
		panelThemes.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelThemes.add(panelFilter, BorderLayout.NORTH);
		panelThemes.add(themeScrollPane, BorderLayout.CENTER);
		add(panelThemes);
		
		PreviewPanel previewPanel = new PreviewPanel();
		JPanel panelUiOptions = createUiOptionsPanel();
		ScrollablePanel cardWrapper = new ScrollablePanel();
		cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
		cardWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		cardWrapper.add(panelUiOptions);
		cardWrapper.add(Box.createVerticalStrut(5));
		cardWrapper.add(previewPanel);
		cardWrapper.add(Box.createGlue());
		
		TScrollPane cardScrollPane = new TScrollPane(cardWrapper);
		cardScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		cardScrollPane.setViewportBorder(null);
		cardScrollPane.setBorder(BorderFactory.createEmptyBorder());
		cardScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		cardScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		JPanel panelMiddle = new JPanel();
		panelMiddle.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.X_AXIS));
		panelMiddle.add(panelThemes);
		panelMiddle.add(cardScrollPane);
		add(panelMiddle);
	}
	
	private void onThemeSelected(ListSelectionEvent event) {
		LookAndFeelInfo info = listThemes.getSelectedValue();
		if(event.getValueIsAdjusting() || info == null) return;
		sSelectedTheme.set(info.getClassName());
		applyTheme(info);
	}
	
	private void applyTheme(LookAndFeelInfo info) {
		if(info == null || UIManager.getLookAndFeel().getClass().getName().equals(info.getClassName())) return;
		
		EventQueue.invokeLater(() -> {
			try {
				FlatAnimatedLafChange.showSnapshot();
				UIManager.setLookAndFeel(info.getClassName());
				FlatLaf.updateUI();
				FlatAnimatedLafChange.hideSnapshotWithAnimation();
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
	
	private JPanel createUiOptionsPanel() {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel root = new JPanel();
		UiUtils.bindElevation(root, 10);
		root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		wrapper.add(root);
		
		JLabel lblTitle = new JLabel("Options");
		UiUtils.bindFont(lblTitle, Style.getFontBold(14));
		UiUtils.bindForeground(lblTitle, Style.accent());
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		root.add(lblTitle);
		root.add(Box.createVerticalStrut(10));
		
		SettingSelection sLanguage = sm.getSetting(SettingSelection.class, "ui.language");
		SettingBoolean sHideInTray = sm.getSetting(SettingBoolean.class, "ui.hideintray");
		SettingBoolean sWindowDecorations = sm.getSetting(SettingBoolean.class, "ui.windowdecorations");
		SettingBoolean sUnifiedWindowDecorations = sm.getSetting(SettingBoolean.class, "ui.windowdecorations.unified");
		SettingBoolean sUnifiedSideMenu = sm.getSetting(SettingBoolean.class, "ui.sidemenu.unified");
		SettingSelection sFont = sm.getSetting(SettingSelection.class, "ui.font");
		SettingInt sFontSize = sm.getSetting(SettingInt.class, "ui.fontsize");
		SettingBoolean sGlowingEffectButton = sm.getSetting(SettingBoolean.class, "ui.glow.button");
		SettingBoolean sTouchScroll = sm.getSetting(SettingBoolean.class, "ui.touchscroll");
		SettingBoolean sTouchScrollInvert = sm.getSetting(SettingBoolean.class, "ui.touchscroll.invert");
		
		// Language
		JComboBox<String> comboLanguage = new JComboBox<>(sLanguage.getValues());
		comboLanguage.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboLanguage.setSelectedItem(sLanguage.getSelected());
		comboLanguage.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboLanguage.getPreferredSize().height));
		comboLanguage.addActionListener(e -> {
			sLanguage.set(comboLanguage.getSelectedItem());
			i18n.setLocale(sLanguage.get());
			Main.getInstance().getMainFrame().getSideMenu().updateMenu();
		});
		appendUiSettingSection(root, comboLanguage, "Language");
		root.add(Box.createVerticalStrut(10));
		
		// Hide in System Tray
		JCheckBox boxHideInTray = new JCheckBox("Hide in system tray", sHideInTray.get());
		boxHideInTray.setAlignmentX(Component.LEFT_ALIGNMENT);
		boxHideInTray.addActionListener(e -> sHideInTray.set(boxHideInTray.isSelected()));
		appendUiSettingSection(root, boxHideInTray, "System Tray Icon");
		root.add(Box.createVerticalStrut(10));
		
		// Custom Window Decorations
		JPanel panelWindowDecorations = new JPanel();
		panelWindowDecorations.setLayout(new BoxLayout(panelWindowDecorations, BoxLayout.Y_AXIS));
		
		JCheckBox boxWindowDecorations = new JCheckBox("Custom window decorations", sWindowDecorations.get());
		boxWindowDecorations.setAlignmentX(Component.LEFT_ALIGNMENT);
		boxWindowDecorations.addActionListener(e -> {
			boolean value = boxWindowDecorations.isSelected();
			sWindowDecorations.set(value);
			Main.getInstance().setCustomWindowDecorations(value);
		});
		panelWindowDecorations.add(boxWindowDecorations);
		
		JCheckBox boxUnifiedWindowDecorations = new JCheckBox("Unified window background", sUnifiedWindowDecorations.get());
		boxUnifiedWindowDecorations.setAlignmentX(Component.LEFT_ALIGNMENT);
		boxUnifiedWindowDecorations.addActionListener(e -> {
			boolean value = boxUnifiedWindowDecorations.isSelected();
			sUnifiedWindowDecorations.set(value);
			UIManager.put("TitlePane.unifiedBackground", value);
			FlatLaf.updateUILater();
		});
		panelWindowDecorations.add(boxUnifiedWindowDecorations);
		
		if(FlatLaf.supportsNativeWindowDecorations()) {
			appendUiSettingSection(root, panelWindowDecorations, "Window Decorations");
			root.add(Box.createVerticalStrut(10));
		}
		
		// Unified Side Menu Background
		JCheckBox boxUnifiedSideMenu = new JCheckBox("Unified side menu background", sUnifiedSideMenu.get());
		boxUnifiedSideMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
		boxUnifiedSideMenu.addActionListener(e -> {
			sUnifiedSideMenu.set(boxUnifiedSideMenu.isSelected());
			Main.getInstance().getMainFrame().getSideMenu().updateMenu();
		});
		appendUiSettingSection(root, boxUnifiedSideMenu, "Side Menu");
		root.add(Box.createVerticalStrut(10));
		
		// Font Family and Size
		JPanel panelFont = new JPanel();
		panelFont.setLayout(new BoxLayout(panelFont, BoxLayout.X_AXIS));
		
		JComboBox<String> comboFont = new JComboBox<>(sFont.getValues());
		comboFont.setSelectedItem(sFont.get());
		comboFont.addActionListener(e -> {
			sFont.setSelected((String) comboFont.getSelectedItem());
			Style.setSelectedFont();
			FlatLaf.updateUILater();
		});
		panelFont.add(comboFont);
		panelFont.add(Box.createHorizontalStrut(5));
		
		JSpinner spinnerFontSize = new JSpinner();
		spinnerFontSize.setModel(new SpinnerNumberModel(sFontSize.get().intValue(), sFontSize.getMin(), sFontSize.getMax(), sFontSize.getStepsize()));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerFontSize, "#");
		spinnerFontSize.setEditor(editor); // set new spinner editor without thousands separation
		spinnerFontSize.addChangeListener(e -> {
			sFontSize.set(spinnerFontSize.getValue());
			Style.setSelectedFont();
			FlatLaf.updateUILater();
		});
		panelFont.add(spinnerFontSize);
		panelFont.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(comboFont.getPreferredSize().height, spinnerFontSize.getPreferredSize().height)));
		
		appendUiSettingSection(root, panelFont, "Font");
		root.add(Box.createVerticalStrut(10));
		
		// Effects
		JCheckBox boxGlowingEffect = new JCheckBox("Glowing effect buttons", sGlowingEffectButton.get());
		boxGlowingEffect.addActionListener(e -> {
			sGlowingEffectButton.set(boxGlowingEffect.isSelected());
		});
		appendUiSettingSection(root, boxGlowingEffect, "Effects");
		JLabel lblGlowEffectDesc = new JLabel("<html>Shows a preview of the animation when hovering over its button.</html>");
		lblGlowEffectDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblGlowEffectDesc.setEnabled(false);
		lblGlowEffectDesc.setIcon(Style.getFontIcon(MenuIcon.HELP, 12));
		lblGlowEffectDesc.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0));
		root.add(lblGlowEffectDesc);
		root.add(Box.createVerticalStrut(10));
		
		// Touch Scroll
		JCheckBox boxTouchScrollInvert = new JCheckBox("Invert scroll direction", sTouchScrollInvert.get());
		boxTouchScrollInvert.setAlignmentX(Component.LEFT_ALIGNMENT);
		boxTouchScrollInvert.setVisible(sTouchScroll.get());
		boxTouchScrollInvert.addActionListener(e -> {
			sTouchScrollInvert.set(boxTouchScrollInvert.isSelected());
			
		});
		
		JCheckBox boxTouchScroll = new JCheckBox("Touch scroll support", sTouchScrollInvert.get());
		boxTouchScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		boxTouchScroll.addActionListener(e -> {
			sTouchScroll.set(boxTouchScroll.isSelected());
			boxTouchScrollInvert.setVisible(sTouchScroll.get());
			root.revalidate();
			root.repaint();
		});
		appendUiSettingSection(root, boxTouchScroll, "Touch Scroll (Experimental)");
		appendUiSettingSection(root, boxTouchScrollInvert, null);
		
		return wrapper;
	}
	
	private void appendUiSettingSection(JPanel root, JComponent comp, String title) {
		if(title != null) {
			JLabel lblTitle = new JLabel(title);
			lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
			root.add(lblTitle);
			root.add(Box.createVerticalStrut(5));
		}
		if(comp instanceof JPanel) {
			comp.setBackground(null);
		}
		comp.setAlignmentX(Component.LEFT_ALIGNMENT);
		root.add(comp);
	}
	
	
	/**
	 * Panel that contains some UI elements to preview the selected theme.
	 */
	private class PreviewPanel extends JPanel {
		
		public PreviewPanel() {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			JPanel panel = new JPanel();
			UiUtils.bindElevation(panel, 10);
			panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			add(panel);
			
			JLabel lblTitle = new JLabel("Preview");
			UiUtils.bindFont(lblTitle, Style.getFontBold(14));
			UiUtils.bindForeground(lblTitle, Style.accent());
			lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(lblTitle);
			panel.add(Box.createVerticalStrut(10));
			
			JButton button = new JButton("Button");
			button.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(button);
			panel.add(Box.createVerticalStrut(10));
			
			JComboBox<String> comboBox = new JComboBox<>(new String[] {"Item 1", "Item 2", "Item 3"});
			comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			comboBox.setMaximumSize(comboBox.getPreferredSize());
			panel.add(comboBox);
			panel.add(Box.createVerticalStrut(10));
			
			JCheckBox checkBox = new JCheckBox("Check box");
			checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			checkBox.setSelected(true);
			panel.add(checkBox);
			panel.add(Box.createVerticalStrut(10));
			
			JRadioButton rbtn1 = new JRadioButton("Option 1");
			rbtn1.setSelected(true);
			JRadioButton rbtn2 = new JRadioButton("Option 2");
			JRadioButton rbtn3 = new JRadioButton("Option 3");
			ButtonGroup rbtnGroup = new ButtonGroup();
			rbtnGroup.add(rbtn1);
			rbtnGroup.add(rbtn2);
			rbtnGroup.add(rbtn3);
			
			JPanel panelRbtn = new JPanel();
			panelRbtn.setBackground(null);
			panelRbtn.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelRbtn.add(rbtn1);
			panelRbtn.add(rbtn2);
			panelRbtn.add(rbtn3);
			panelRbtn.setMaximumSize(panelRbtn.getPreferredSize());
			panel.add(panelRbtn);
		}
		
	}

}
