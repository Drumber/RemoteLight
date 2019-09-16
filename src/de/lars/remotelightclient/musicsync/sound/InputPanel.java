/*
 * Adapted from TarosDSP PitchDetector example
 * Github: https://github.com/JorenSix/TarsosDSP
 */

package de.lars.remotelightclient.musicsync.sound;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import de.lars.remotelightclient.DataStorage;

public class InputPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Mixer mixer = null;
	
	public InputPanel() {
		super(new BorderLayout());
		this.setBorder(new TitledBorder("Choose a input"));
		JPanel buttonPanel = new JPanel(new GridLayout(0,1));
		ButtonGroup group = new ButtonGroup();
		String data = (String) DataStorage.getData(DataStorage.SOUND_INPUT_STOREKEY);
		for(Mixer.Info info : Shared.getMixerInfo(false, true)) {
			Mixer mixer = AudioSystem.getMixer(info);

			if(this.isLineSupported(mixer)) {
				JRadioButton button = new JRadioButton();
				button.setText(Shared.toLocalString(info));
				buttonPanel.add(button);
				group.add(button);
				button.setActionCommand(info.toString());
				button.addActionListener(setInput);
				//set last time input as selected
				if(data != null) {
					if(data.equals(info.toString()))
						button.setSelected(true);
				}
			}
		}
		this.add(new JScrollPane(buttonPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
		this.setMaximumSize(new Dimension(300,150));
		this.setPreferredSize(new Dimension(300,150));
	}
	
	private ActionListener setInput = new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			for(Mixer.Info info : Shared.getMixerInfo(false, true)){
				if(arg0.getActionCommand().equals(info.toString())){
					System.out.println("action");
					Mixer newValue = AudioSystem.getMixer(info);
					InputPanel.this.firePropertyChange("mixer", mixer, newValue);
					InputPanel.this.mixer = newValue;
					//save last selected to data file
					DataStorage.store(DataStorage.SOUND_INPUT_STOREKEY, info.toString());
					break;
				}
			}
		}
	};
	
	private boolean isLineSupported(Mixer mixer) {
		try {
			mixer.open();
	        Line.Info linfo = new Line.Info(TargetDataLine.class);
	        TargetDataLine line = null;
	        try {
	            line = (TargetDataLine)mixer.getLine(linfo);
	            line.open();
	        } catch (IllegalArgumentException ex) {
	        	return false;
	        } catch (LineUnavailableException ex) {
	        	return false;
	        }
			mixer.close();
		} catch(LineUnavailableException e) {
			return false;
		}
		return true;
	}

}
