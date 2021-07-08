/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightclient.ui.panels.controlbars.comps;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightcore.RemoteLightCore;
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
		om = RemoteLightCore.getInstance().getOutputManager();
		//om.addOutputActionListener(outputListener);
		
		setBorder(new EmptyBorder(8, 0, 10, 0));
		setBackground(c);
		setLayout(new GridLayout(2, 0, 0, 0));
		
		lblOutput = new JLabel("", SwingConstants.CENTER);
		lblOutput.setFont(Style.getFontRegualar(12));
		add(lblOutput);
		
		lblConnection = new JLabel("", SwingConstants.CENTER);
		lblConnection.setFont(Style.getFontRegualar(11));
		lblConnection.setEnabled(false);
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
