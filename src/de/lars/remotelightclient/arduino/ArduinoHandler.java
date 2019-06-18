package de.lars.remotelightclient.arduino;

import java.awt.Color;

import de.lars.remotelightclient.arduino.animations.AnimationHandler_Arduino;
import de.lars.remotelightclient.network.Identifier;


public class ArduinoHandler {
	
	public static void handleIdentifier(String[] msg) {
		
		if (msg.length > 0) {
			
				switch (msg[0]) {
				
				// WS281x
				case Identifier.WS_COLOR_ALL:
					Arduino.setColorAll(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]), Integer.parseInt(msg[3]));
					break;
				case Identifier.WS_COLOR_PIXEL:
					Arduino.setColorPixel(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]), Integer.parseInt(msg[3]), Integer.parseInt(msg[4]));
					break;
				case Identifier.WS_COLOR_OFF:
					Arduino.setColorAll(Color.BLACK);
					break;
				case Identifier.WS_COLOR_DIM:
					Arduino.setDimValue(Integer.parseInt(msg[1]));
					break;
				case Identifier.WS_ANI_STOP:
					AnimationHandler_Arduino.stop();
					break;
				case Identifier.WS_ANI_SPEED:
					AnimationHandler_Arduino.setSpeed(Integer.parseInt(msg[1]));
					break;
				case Identifier.WS_ANI_RAINBOW:
					AnimationHandler_Arduino.startRainbow();
					break;
				case Identifier.WS_ANI_RUNNING:
					AnimationHandler_Arduino.startRunningLight();
					break;
				case Identifier.WS_ANI_WIPE:
					AnimationHandler_Arduino.startWipe();
					break;
				case Identifier.WS_ANI_SCAN:
					AnimationHandler_Arduino.startScan();
					break;
				case Identifier.WS_ANI_SNAKES:
					
					break;
				case Identifier.WS_SHIFT_LEFT:
					Arduino.shiftLeft(Integer.parseInt(msg[1]));
					break;
				case Identifier.WS_SHIFT_RIGHT:
					Arduino.shiftRight(Integer.parseInt(msg[1]));
					break;
				
				// COLOR
				case Identifier.COLOR_RED:
					
					break;
				case Identifier.COLOR_GREEN:
					
					break;
				case Identifier.COLOR_BLUE:
					
					break;
				case Identifier.COLOR_COLOR:
					Arduino.setColorAll(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]), Integer.parseInt(msg[3]));
					break;
				case Identifier.COLOR_LEFT:
					
					break;
				case Identifier.COLOR_RIGHT:
					
					break;


				// SCREEN COLOR
				case Identifier.SC_START:
					Arduino.setColorAll(Color.BLACK);
					break;
				case Identifier.SC_STOP:
					Arduino.setColorAll(Color.BLACK);
					break;
				case Identifier.SC_COLOR_LEFT:

					break;
				case Identifier.SC_COLOR_RIGHT:

					break;
					
				// SCENES
				case Identifier.SCENE_STOP:
					Arduino.setColorAll(Color.BLACK);
					break;
					

				default:
					break;
				}
		}
				
	}

}
