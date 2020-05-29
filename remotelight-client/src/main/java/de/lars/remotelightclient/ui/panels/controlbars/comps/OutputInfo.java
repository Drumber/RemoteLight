/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.ui.panels.controlbars.comps;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.out.Output;
import de.lars.remotelightcore.out.OutputManager;
import de.lars.remotelightcore.utils.OutputUtil;

public class OutputInfo extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3003101936829285402L;
	private OutputManager om;
	private JLabel lblOutput;
	private JLabel lblConnection;

	/**
	 * @param c Background color
	 */
	public OutputInfo(Color c) {
		om = Main.getInstance().getOutputManager();
		//om.addOutputActionListener(outputListener);
		
		setBorder(new EmptyBorder(8, 0, 10, 0));
		setBackground(c);
		setLayout(new GridLayout(2, 0, 0, 0));
		
		lblOutput = new JLabel("", SwingConstants.CENTER);
		lblOutput.setFont(Style.getFontRegualar(12));
		lblOutput.setForeground(Style.textColor);
		add(lblOutput);
		
		lblConnection = new JLabel("", SwingConstants.CENTER);
		lblConnection.setFont(Style.getFontRegualar(11));
		lblConnection.setForeground(Style.textColorDarker);
		add(lblConnection);
		
		setLabelInfo();
	}
	
	public void setLabelInfo() {
		if(om.getActiveOutput() != null) {
			Output o = om.getActiveOutput();
			String output = OutputUtil.getOutputTypeAsString(o);
			String id = o.getId();
			
			lblOutput.setText(output + " (" + id + ")");
			
			String state = OutputUtil.getConnectionStateAsString(o.getState()).toLowerCase();
			
			lblConnection.setText(state);
		}
	}
	
//	private OutputActionListener outputListener = new OutputActionListener() {
//		@Override
//		public void onOutputAction(Output output, OutputActionType type) {
//			setLabelInfo();
//		}
//	};

}
