package de.lars.remotelightclient.arduino;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.AnimationManager;
import de.lars.remotelightclient.animation.animations.Rainbow;
import de.lars.remotelightclient.animation.animations.RunningLight;
import de.lars.remotelightclient.animation.animations.Scanner;
import de.lars.remotelightclient.animation.animations.Wipe;
import de.lars.remotelightclient.network.Identifier;

public class ArduinoHandler {

	public static void handleIdentifier(String[] msg) {
		// System.out.println("[ArduinoHandler] Identifier: " + msg[0]);

		if (msg.length > 0) {
			AnimationManager aniManager = Main.getInstance().getAnimationManager();

			switch (msg[0]) {

			// WS281x
			case Identifier.WS_COLOR_ALL:
				Arduino.setColorAll(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]), Integer.parseInt(msg[3]));
				break;
			case Identifier.WS_COLOR_PIXEL:
				Arduino.setColorPixel(Integer.parseInt(msg[1]), Integer.parseInt(msg[2]), Integer.parseInt(msg[3]),
						Integer.parseInt(msg[4]));
				break;
			case Identifier.WS_COLOR_OFF:
				Arduino.setColorAll(Color.BLACK);
				break;
			case Identifier.WS_COLOR_DIM:
				Arduino.setDimValue(Integer.parseInt(msg[1]));
				break;
			case Identifier.WS_ANI_STOP:
				aniManager.stop();
				break;
			case Identifier.WS_ANI_SPEED:
				aniManager.setDelay(Integer.parseInt(msg[1]));
				break;
			case Identifier.WS_ANI_RAINBOW:
				aniManager.start(new Rainbow());
				break;
			case Identifier.WS_ANI_RUNNING:
				aniManager.start(new RunningLight());
				break;
			case Identifier.WS_ANI_WIPE:
				aniManager.start(new Wipe());
				break;
			case Identifier.WS_ANI_SCAN:
				aniManager.start(new Scanner());
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
				System.out.println("[ServerInputHandler] Identifier '" + msg[0] + "' not found!");
				break;
			}
		}

	}

}
