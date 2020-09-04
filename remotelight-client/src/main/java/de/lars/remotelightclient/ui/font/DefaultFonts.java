package de.lars.remotelightclient.ui.font;

import java.awt.Font;

import de.lars.remotelightclient.utils.ui.UiUtils;

public class DefaultFonts {
	
	/**
	 * Add default font resources to the FontManager.
	 */
	public static void registerDefaultFonts() {
		FontManager.addFont("Muli", new Muli());
		FontManager.addFont("Roboto", new Roboto());
		FontManager.addFont("Open Sans", new OpenSans());
		FontManager.addFont("Montserrat", new Montserrat());
	}
	
	static class Muli implements FontResource {

		@Override
		public String getName() {
			return "Muli";
		}

		@Override
		public Font getRegular() {
			return UiUtils.loadFont("muli/Muli-Regular.ttf", Font.PLAIN);
		}

		@Override
		public Font getBold() {
			return UiUtils.loadFont("muli/Muli-ExtraBold.ttf", Font.BOLD); 
		}

		@Override
		public Font getItalic() {
			return UiUtils.loadFont("muli/Muli-Light.ttf", Font.ITALIC);
		}
		
	}
	
	static class Roboto implements FontResource {

		@Override
		public String getName() {
			return "Roboto";
		}

		@Override
		public Font getRegular() {
			return UiUtils.loadFont("roboto/Roboto-Regular.ttf", Font.PLAIN);
		}

		@Override
		public Font getBold() {
			return UiUtils.loadFont("roboto/Roboto-Bold.ttf", Font.BOLD); 
		}

		@Override
		public Font getItalic() {
			return UiUtils.loadFont("roboto/Roboto-Light.ttf", Font.ITALIC);
		}
		
	}
	
	static class OpenSans implements FontResource {

		@Override
		public String getName() {
			return "Open Sans";
		}

		@Override
		public Font getRegular() {
			return UiUtils.loadFont("opensans/OpenSans-Regular.ttf", Font.PLAIN);
		}

		@Override
		public Font getBold() {
			return UiUtils.loadFont("opensans/OpenSans-Bold.ttf", Font.BOLD); 
		}

		@Override
		public Font getItalic() {
			return UiUtils.loadFont("opensans/OpenSans-Light.ttf", Font.ITALIC);
		}
		
	}
	
	static class Montserrat implements FontResource {

		@Override
		public String getName() {
			return "Montserrat";
		}

		@Override
		public Font getRegular() {
			return UiUtils.loadFont("montserrat/Montserrat-Regular.ttf", Font.PLAIN);
		}

		@Override
		public Font getBold() {
			return UiUtils.loadFont("montserrat/Montserrat-Bold.ttf", Font.BOLD); 
		}

		@Override
		public Font getItalic() {
			return UiUtils.loadFont("montserrat/Montserrat-Light.ttf", Font.ITALIC);
		}
		
	}

}
