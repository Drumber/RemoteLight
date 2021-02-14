package de.lars.remotelightclient.ui.panels.colors.gradients;

public interface MarkerListener {
	
	public void onMarkerSelected(int index);
	
	public void onMarkerDragged(int index, float newFraction);

}
