/*
 * Adapted from TarosDSP PitchDetector example
 * Github: https://github.com/JorenSix/TarsosDSP
 */

package de.lars.remotelightclient.musicsync.sound;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.lars.remotelightclient.DataStorage;
import de.lars.remotelightclient.musicsync.MusicSyncManager;

public class InputFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7773535499823730841L;
	private final JTextArea textArea;

	public InputFrame(MusicSyncManager manager) {
		this.setLayout(new GridLayout(0, 1));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Select a Input");
		
		JPanel inputPanel = new InputPanel();
		add(inputPanel);
		inputPanel.addPropertyChangeListener("mixer",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						System.out.println("listener");
						SoundProcessing.setMixer((Mixer) arg0.getNewValue());
						manager.newSoundProcessor();
					}
				});
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		add(new JScrollPane(textArea));
		
		String data = (String) DataStorage.getData(DataStorage.SOUND_INPUT_STOREKEY);
		if(data != null) {
			for(Mixer.Info info : Shared.getMixerInfo(false, true)){
				if(data.equals(info.toString())){
					Mixer newValue = AudioSystem.getMixer(info);
					SoundProcessing.setMixer(newValue);
					break;
				}
			}
		}
	}
	
	public void addText(String text) {
		textArea.append(text);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

}
