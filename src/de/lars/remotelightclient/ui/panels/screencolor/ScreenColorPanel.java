package de.lars.remotelightclient.ui.panels.screencolor;

import de.lars.remotelightclient.screencolor.ScreenColorManager;
import de.lars.remotelightclient.ui.MenuPanel;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.ui.comps.ImagePanel;
import de.lars.remotelightclient.utils.WrapLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.border.EmptyBorder;

import org.tinylog.Logger;

public class ScreenColorPanel extends MenuPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1195460185717058923L;
	private JPanel panelMonitors;
	
	public ScreenColorPanel() {
		setBackground(Style.panelBackground);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Dimension size = new Dimension(Integer.MAX_VALUE, 400);
		JPanel bgrMonitors = new JPanel();
		bgrMonitors.setBorder(new EmptyBorder(20, 20, 0, 20));
		bgrMonitors.setPreferredSize(size);
		bgrMonitors.setMaximumSize(size);
		bgrMonitors.setBackground(Style.panelBackground);
		add(bgrMonitors);
		bgrMonitors.setLayout(new BorderLayout(0, 0));
		
		panelMonitors = new JPanel();
		WrapLayout wlayout = new WrapLayout(FlowLayout.CENTER);
		panelMonitors.setLayout(wlayout);
		panelMonitors.setBackground(Style.panelDarkBackground);
		bgrMonitors.add(panelMonitors, BorderLayout.CENTER);
		
		size = new Dimension(Integer.MAX_VALUE, 500);
		JPanel bgrOptions = new JPanel();
		bgrOptions.setPreferredSize(size);
		bgrOptions.setBackground(Style.panelBackground);
		add(bgrOptions);
		
		this.addMonitorsPanel();
	}
	
	private void addMonitorsPanel() {
		try {
			Robot r;
			
			for(GraphicsDevice gd : ScreenColorManager.getMonitors()) {
				r = new Robot(gd);
				BufferedImage img = r.createScreenCapture(gd.getDefaultConfiguration().getBounds());
				
				Dimension size = new Dimension(200, 100);
				ImagePanel ipanel = new ImagePanel(img, gd.getIDstring().substring(1), size);
				panelMonitors.add(ipanel);
			}
			
			updateUI();
			
		} catch (AWTException e) {
			Logger.error(e, "Could not add monitors panel!");
		}
	}

}
