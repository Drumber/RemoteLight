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

package de.lars.remotelightcore.animation.animations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.lars.remotelightcore.animation.Animation;
import de.lars.remotelightcore.settings.SettingsManager.SettingCategory;
import de.lars.remotelightcore.settings.types.SettingBoolean;
import de.lars.remotelightcore.settings.types.SettingColor;
import de.lars.remotelightcore.settings.types.SettingInt;
import de.lars.remotelightcore.utils.color.Color;
import de.lars.remotelightcore.utils.color.PixelColorUtils;
import de.lars.remotelightcore.utils.color.RainbowWheel;

public class Snake extends Animation {
	
	private List<Integer> snakePos;
	private List<Color> snakeColor;
	private List<Byte> snakeDirection;
	private Color colorTale, colorHead, colorFruit;
	private byte direction = 1;	// +1 or -1
	private int fruitPos;
	private int rainbowPos = 0;

	public Snake() {
		super("Snake");
		this.addSetting(new SettingBoolean("animation.snake.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingBoolean("animation.snake.rainbow", "Rainbow color", SettingCategory.Intern, null, false));
		this.addSetting(new SettingColor("animation.snake.colortale", "Tail color", SettingCategory.Intern,	null, Color.CYAN));
		this.addSetting(new SettingColor("animation.snake.colorhead", "Head color", SettingCategory.Intern,	null, Color.GREEN));
		this.addSetting(new SettingColor("animation.snake.colorfruit", "Fruit color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingInt("animation.snake.maxlength", "Max length", SettingCategory.Intern, "Maximum tail length", 200, 5, 999, 1));
	}
	
	@Override
	public void onEnable(int pixels) {
		super.onEnable(pixels);
		snakePos = new ArrayList<>();
		snakeColor = new ArrayList<>();
		snakeDirection = new ArrayList<>();
		
		if(!((SettingBoolean) getSetting("animation.snake.randomcolor")).get()) {
			colorTale = ((SettingColor) getSetting("animation.snake.colortale")).get();
			colorHead = ((SettingColor) getSetting("animation.snake.colorhead")).get();
			colorFruit = ((SettingColor) getSetting("animation.snake.colorfruit")).get();
		} else {
			colorTale = RainbowWheel.getRandomColor();
			colorHead = RainbowWheel.getRandomColor();
			colorFruit = Color.RED;
		}
		
		int startPoint = new Random().nextInt(pixels);
		snakePos.add(startPoint);
		snakeColor.add(colorHead);
		
		fruitPos = new Random().nextInt(pixels);
		if(fruitPos > startPoint) {
			direction = 1;
		} else {
			direction = -1;
		}
		snakeDirection.add(direction);
		
		paintSnake();
	}
	
	@Override
	public Color[] onEffect() {
		if(!((SettingBoolean) getSetting("animation.snake.randomcolor")).get() && !((SettingBoolean) getSetting("animation.snake.rainbow")).get()) {
			colorTale = ((SettingColor) getSetting("animation.snake.colortale")).get();
			colorHead = ((SettingColor) getSetting("animation.snake.colorhead")).get();
			colorFruit = ((SettingColor) getSetting("animation.snake.colorfruit")).get();
		} else if(((SettingBoolean) getSetting("animation.snake.rainbow")).get()) {
			colorTale = RainbowWheel.getRainbow()[rainbowPos];
			colorHead = RainbowWheel.getRainbow()[0];
		}
		snakeColor.set(0, colorHead);
		
		int maxLength = ((SettingInt) getSetting("animation.snake.maxlength")).get();
		if(snakePos.size() > maxLength) {
			// reset snake
			onEnable();
		}
		
		// move snake
		moveSnake();
		
		if(snakePos.get(0) == fruitPos) {
			
			// set new fruit position
			fruitPos = new Random().nextInt(getPixel());
			if(fruitPos > snakePos.get(0)) {
				direction = 1;
			} else {
				direction = -1;
			}
			
			// add +1 to the snake
			extendSnake();
			
			// rainbow color
			rainbowPos += 4;
			if(rainbowPos >= RainbowWheel.getRainbow().length) {
				rainbowPos = 0;
			}
		}
		return paintSnake();
	}
	
	
	private Color[] paintSnake() {
		Color[] strip = PixelColorUtils.colorAllPixels(Color.BLACK, getPixel());
		// paint fruit
		strip[fruitPos] = colorFruit;
		// paint snake
		for(int i = snakePos.size() - 1; i >= 0; i--) {
			int pos = snakePos.get(i);
			strip[pos] = snakeColor.get(i);
		}
		return strip;
	}
	
	
	private void moveSnake() {
		for(int i = 0; i < snakePos.size(); i++) {
			int newPos = snakePos.get(i) + snakeDirection.get(i);
			
			if(newPos >= getPixel()) {
				newPos = 0;
			}
			if(newPos < 0) {
				newPos = getPixel() - 1;
			}
			
			snakePos.set(i, newPos);
		}
		
		for(int i = snakeDirection.size() - 1; i > 0; i--) {
			byte prevDir = snakeDirection.get(i - 1);
			snakeDirection.set(i, prevDir);
		}
		
		// set new direction
		snakeDirection.set(0, direction);
	}
	
	
	private void extendSnake() {
		int lastPos = snakePos.get(snakePos.size() - 1);
		byte lastDir = snakeDirection.get(snakeDirection.size() - 1);
		int pos = lastPos - lastDir;
		
		if(pos >= getPixel()) {
			pos = 0;
		}
		if(pos < 0) {
			pos = getPixel() - 1;
		}
		
		snakePos.add(pos);
		snakeColor.add(colorTale);
		
		snakeDirection.add(lastDir);
	}
	
	@Override
	public void onSettingUpdate() {
		boolean hideColors = getSetting(SettingBoolean.class, "animation.snake.randomcolor").get() ||
				getSetting(SettingBoolean.class, "animation.snake.rainbow").get();
		this.hideSetting("animation.snake.colortale", hideColors);
		this.hideSetting("animation.snake.colorhead", hideColors);
		this.hideSetting("animation.snake.colorfruit", hideColors);
		super.onSettingUpdate();
	}

}
