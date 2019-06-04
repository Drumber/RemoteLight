package de.lars.remotelightclient.scenes;

public class Sunset {
	
	private static boolean active;
	
	
	public static void stop() {
		active = false;
	}
	
	public static void start() {
		if(!active) {
			active = true;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					while(active) {
						
						try {
							Thread.sleep(SceneHandler.DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
		}

}
