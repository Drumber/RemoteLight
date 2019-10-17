package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.RainbowWheel;
import de.lars.remotelightclient.utils.TimeUtil;

public class Shake extends Animation {

	private final int TIME_PER_MODE = 5000;
	private int mode = 0; //0 = single color, 1 = random color
	private Color[] strip;
	private Color[] singleColors = {Color.RED, Color.BLUE, Color.MAGENTA, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.CYAN};
	private int space;
	private boolean directionRight = true;
	private TimeUtil timeMode;
	private int modeChangeCounter = 0;
	private int sequenceCounter = 0;
	private int sequenceMode = 0; //0 = shake, 1 = right, 2 = left, 3 = shake
	
	public Shake() {
		super("Shake", 115);
	}
	
	@Override
	public void onEnable() {
		space = Main.getLedNum() / 10;
		timeMode = new TimeUtil(TIME_PER_MODE);
		strip = Main.getInstance().getOutputManager().getLastColors();
		
		Color c = singleColors[new Random().nextInt(singleColors.length)];
		int counter = 0;
		for(int i = 0; i < Main.getLedNum(); i++) {
			if(counter == space && strip[0] == Color.BLACK) {
				strip[i] = c;
				
				if(i < Main.getLedNum() - 1) {
					strip[++i] = c;
				}
			} else {
				strip[i] = Color.BLACK;
			}
			
			counter++;
			if(counter > space) {
				counter = 0;
			}
		}
		
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(timeMode.hasReached()) {
			System.out.println(modeChangeCounter);
			if(++modeChangeCounter >= 10) {
				if(++mode > 1) {
					mode = 0;
				}
				modeChangeCounter = 0;
			}
			
			if(mode == 0) {
				Color c = singleColors[new Random().nextInt(singleColors.length)];
				this.changeSingleColor(c);
			}
			else if(mode == 1) {
				this.changeRandomColor();
			}
		}
		
		if(--sequenceCounter <= 0) {
			sequenceMode = new Random().nextInt(4);
		}
		
		if(sequenceMode == 0 || sequenceMode == 3) {
			directionRight = !directionRight;
		}
		else if(sequenceMode == 1) {
			directionRight = true;
		}
		else if(sequenceMode == 2) {
			directionRight = false;
		}
		
		this.move(directionRight);
		
		super.onLoop();
	}
	
	private void move(boolean right) {
		if(right) {
			Color tmp = strip[strip.length-1];
			for(int i = strip.length-1; i > 0; i--) {
				strip[i] = strip[i-1];
			}
			strip[0] = tmp;
		} else {
			Color tmp = strip[0];
			for(int i = 0; i < strip.length-1; i++) {
				strip[i] = strip[i+1];
			}
			strip[strip.length-1] = tmp;
		}
		
		OutputManager.addToOutput(strip);
	}
	
	
	private void changeSingleColor(Color c) {
		for(int i = 0; i < strip.length; i++) {
			if(strip[i] != Color.BLACK) {
				strip[i] = c;
			}
		}
	}
	
	private void changeRandomColor() {
		for(int i = 0; i < strip.length; i++) {
			if(strip[i] != Color.BLACK) {
				Color[] rnbw = RainbowWheel.getRainbow();
				strip[i] = rnbw[new Random().nextInt(rnbw.length)];
			}
		}
	}

}
