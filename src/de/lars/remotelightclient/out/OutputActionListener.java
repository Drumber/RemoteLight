package de.lars.remotelightclient.out;

public interface OutputActionListener {
	
	public enum OutputActionType {
		ACTIVATED, DEACTIVATED, ACTIVE_OUTPUT_CHANGED
	}
	
	public void onOutputAction(Output output, OutputActionType type);
	
}
