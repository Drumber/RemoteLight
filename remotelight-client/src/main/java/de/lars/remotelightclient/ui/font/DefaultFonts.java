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
