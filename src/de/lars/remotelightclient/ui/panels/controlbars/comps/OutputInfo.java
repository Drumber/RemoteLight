package de.lars.remotelightclient.ui.panels.controlbars.comps;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.out.Output;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.OutputUtil;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

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
		setBorder(new EmptyBorder(8, 0, 10, 0));
		om = Main.getInstance().getOutputManager();
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

}
