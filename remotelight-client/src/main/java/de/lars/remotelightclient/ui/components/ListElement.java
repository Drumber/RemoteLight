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

package de.lars.remotelightclient.ui.components;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import de.lars.remotelightclient.ui.Style;
import de.lars.remotelightclient.utils.ui.UiUtils;

public class ListElement extends JPanel {
	private static final long serialVersionUID = -1204571375225572068L;
	
	private int height;
	
	public ListElement(int height) {
		this.height = height;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(Style.buttonBackground);
		setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		UiUtils.addHoverColor(this, Style.buttonBackground, Style.hoverBackground);
	}
	
	public ListElement() {
		this(50);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(super.getPreferredSize().width, height);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, height);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(super.getMinimumSize().width, height);
	}

}
