package de.lars.remotelightclient.ui.panels.colors.gradients;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import de.lars.colorpicker.ColorPicker;
import de.lars.colorpicker.listener.ColorListener;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.components.TScrollPane;
import de.lars.remotelightclient.ui.components.TabButtons;
import de.lars.remotelightclient.utils.ColorTool;
import de.lars.remotelightclient.utils.ui.CustomStyledDocument;
import de.lars.remotelightclient.utils.ui.UiUtils;
import de.lars.remotelightclient.utils.ui.WrapLayout;
import de.lars.remotelightcore.utils.ExceptionHandler;
import de.lars.remotelightcore.utils.color.palette.AbstractPalette;
import de.lars.remotelightcore.utils.color.palette.PaletteData;

public class GradientEditPanel extends JPanel {
	private static final long serialVersionUID = 7638664136509067917L;
	
	private GradientBar gradientBar;
	private PaletteData palette;
	private JTextField fieldName;
	private MarkerEditPanel panelMarkerEdit;
	private ColorPicker colorPicker;
	
	public GradientEditPanel() {
		setBackground(Style.panelBackground);
		setLayout(new BorderLayout());
		
		JPanel panelHeader = new JPanel();
		panelHeader.setBackground(Style.panelBackground);
		panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
		add(panelHeader, BorderLayout.NORTH);
		
		fieldName = new JTextField();
		fieldName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		fieldName.setPreferredSize(new Dimension(0, 40));
		fieldName.setBackground(Style.panelDarkBackground);
		fieldName.setForeground(Style.textColor);
		fieldName.setCaretColor(Style.accent);
		fieldName.setFont(Style.getFontRegualar(16));
		fieldName.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, Style.textColorDarker), 
				BorderFactory.createEmptyBorder(5, 10, 0, 10)));
		panelHeader.add(fieldName);
		
		gradientBar = new GradientBar(null);
		gradientBar.setMinimumSize(new Dimension(100, 20));
		gradientBar.setPreferredSize(new Dimension(0, 30));
		gradientBar.setShowMarkers(true);
		gradientBar.setCornerRadius(5);
		gradientBar.setPaddingHorizontal(5);
		gradientBar.setMarkerListener(onGradientMarkerChange);
		panelHeader.add(Box.createVerticalStrut(20)); // add spacer
		panelHeader.add(gradientBar);
		
		panelMarkerEdit = new MarkerEditPanel();
		panelHeader.add(panelMarkerEdit);
		
		JPanel panelSetup = new JPanel();
		panelSetup.setBackground(Style.panelBackground);
		panelSetup.setLayout(new BorderLayout());
		
		TScrollPane scrollPane = new TScrollPane(panelSetup);
		//scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setViewportBorder(null);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		add(scrollPane, BorderLayout.CENTER);
		
		colorPicker =  new ColorPicker(Color.RED, 0, true, true, false, false);
		colorPicker.setBackground(Style.panelBackground);
		colorPicker.addColorListener(colorChangeListener);
		colorPicker.setPreferredSize(new Dimension(300, 200));
		colorPicker.setMinimumSize(new Dimension(100, 200));
		
		JPanel panelCodeEditor = new JPanel();
		panelCodeEditor.setLayout(new BorderLayout());
		panelCodeEditor.setBackground(Style.panelBackground);
		
		JTextPane editor = new JTextPane();
		editor.setBackground(Style.panelDarkBackground);
		editor.setForeground(Style.textColor);
		editor.setCaretColor(Style.accent);
		editor.setStyledDocument(createStyledDocument());
		panelCodeEditor.add(editor, BorderLayout.CENTER);
		
		TabButtons tabBtns = new TabButtons();
		tabBtns.setBackground(Style.panelBackground);
		tabBtns.setHeight(25);
		tabBtns.addButton("ColorPicker");
		tabBtns.addButton("Code Editor");
		tabBtns.setActionListener(l -> {
			Component centerComp = ((BorderLayout) panelSetup.getLayout()).getLayoutComponent(BorderLayout.CENTER);
			if(centerComp != null) {
				panelSetup.remove(centerComp);
			}
			if("ColorPicker".equals(l.getActionCommand())) {
				panelSetup.add(colorPicker, BorderLayout.CENTER);
			} else {
				panelSetup.add(panelCodeEditor, BorderLayout.CENTER);
			}
			panelSetup.updateUI();
		});
		panelSetup.add(tabBtns, BorderLayout.NORTH);
		tabBtns.selectButton("ColorPicker");
		
	}
	
	private ColorListener colorChangeListener = new ColorListener() {
		@Override
		public void onColorChanged(Color color) {
			int selectedIndex = gradientBar.getSelectedMarkerIndex();
			if(palette != null && selectedIndex >= 0 && selectedIndex < palette.getPalette().size()) {
				palette.getPalette().setColorAtIndex(selectedIndex, ColorTool.convert(color));
				updateGradientBar();
			}
		}
	};
	
	private MarkerListener onGradientMarkerChange = new MarkerListener() {
		@Override
		public void onMarkerSelected(int index) {
			Color color = ColorTool.convert(palette.getPalette().getColorAtIndex(index));
			colorPicker.setSelectedColor(color);
		}
		@Override
		public void onMarkerDragged(int index, float newFraction) {
		}
	};
	
	protected void removeSelectedMarker() {
		int index = gradientBar.getSelectedMarkerIndex();
		if(index != -1 && palette != null) {
			palette.getPalette().removeColorAtIndex(index);
			updateGradientBar();
			gradientBar.setSelectedMarker(index - 1);
		}
	}
	
	protected void addMarker() {
		if(palette != null) {
			try {
				AbstractPalette p = palette.getPalette();
				de.lars.remotelightcore.utils.color.Color color = p.size() > 0 ? p.getColorAtIndex(p.size() - 1) : de.lars.remotelightcore.utils.color.Color.RED;
				palette.getPalette().addColor(color);
				gradientBar.setSelectedMarker(palette.getPalette().size() - 1);
				updateGradientBar();
			} catch(Exception e) {
				ExceptionHandler.handle(new Exception("Could not add color to palette.", e));
			}
		}
	}
	
	public void updateValues() {
		if(palette != null) {
			fieldName.setText(palette.getName());
			gradientBar.setColorPalette(palette.getPalette());
			gradientBar.resetMarkerSelection();
			gradientBar.repaint();
			if(palette.getPalette().size() > 0) {
				// select first marker and display its color in the color picker
				gradientBar.setSelectedMarker(0);
				colorPicker.setSelectedColor(ColorTool.convert(palette.getPalette().getColorAtIndex(0)));
			}
		}
	}
	
	public void updateGradientBar() {
		if(palette != null) {
			gradientBar.setColorPalette(palette.getPalette());
			gradientBar.repaint();
		}
	}

	public PaletteData getPalette() {
		return palette;
	}

	public void setPalette(PaletteData palette) {
		this.palette = palette;
	}
	
	private CustomStyledDocument createStyledDocument() {
		CustomStyledDocument doc = new CustomStyledDocument();
		doc
			.addRegEx("[{}]", doc.brackets) // brackets
			.addRegEx("^.*(?=([{]))", doc.highlighted) // everything before '{'
			.addRegEx("(?<=([:]))\\W*\\d+\\W*\\d+\\W*\\d+", doc.value) // first 3 numbers after ':' (ignoring not words)
			.addRegEx("\\d*\\.?\\d+\\s*(?=([:]))", doc.key) // any (decimal) numbers before ':'
			.addRegEx("/\\*(?:.|[\\n\\r])*?\\*/", doc.comment); // comments
		return doc;
	}
	
	
	private class MarkerEditPanel extends JPanel {
		private static final long serialVersionUID = 3800579001431712108L;
		
		private JButton btnAddMarker;
		private JButton btnRemoveMarker;
		
		public MarkerEditPanel() {
			setBackground(Style.panelBackground);
			setLayout(new WrapLayout(WrapLayout.LEFT));
			
			btnAddMarker = new JButton("Add marker");
			UiUtils.configureButton(btnAddMarker);
			btnAddMarker.addActionListener(l -> addMarker());
			add(btnAddMarker);
			
			btnRemoveMarker = new JButton("Remove marker");
			UiUtils.configureButton(btnRemoveMarker);
			btnRemoveMarker.addActionListener(l -> removeSelectedMarker());
			add(btnRemoveMarker);
		}
		
	}

}
