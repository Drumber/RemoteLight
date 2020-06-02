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

package de.lars.remotelightclient.utils.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/*
 *  Simple implementation of a Glass Pane that will capture and ignore all
 *  events as well paint the glass pane to give the frame a "disabled" look.
 *
 *  The background color of the glass pane should use a color with an
 *  alpha value to create the disabled look.
 *
 * source: https://tips4java.wordpress.com/2008/11/07/disabled-glass-pane/
 */
public class DisabledGlassPane extends JComponent
	implements KeyListener
{
	private static final long serialVersionUID = -4814089895221840119L;
	private final static Border MESSAGE_BORDER = new EmptyBorder(10, 10, 10, 10);
	private JLabel message = new JLabel();

	public DisabledGlassPane()
	{
		//  Set glass pane properties

		setOpaque( false );
		Color base = UIManager.getColor("inactiveCaptionBorder");
		Color background = new Color(base.getRed(), base.getGreen(), base.getBlue(), 128);
		setBackground( background );
		setLayout( new GridBagLayout() );

		//  Add a message label to the glass pane

		add(message, new GridBagConstraints());
		message.setOpaque(true);
		message.setBorder(MESSAGE_BORDER);

		//  Disable Mouse, Key and Focus events for the glass pane

		addMouseListener( new MouseAdapter() {} );
		addMouseMotionListener( new MouseMotionAdapter() {} );

		addKeyListener( this );

		setFocusTraversalKeysEnabled(false);
	}

	/*
	 *  The component is transparent but we want to paint the background
	 *  to give it the disabled look.
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor( getBackground() );
		g.fillRect(0, 0, getSize().width, getSize().height);
	}

	/*
	 *  The	background color of the message label will be the same as the
	 *  background of the glass pane without the alpha value
	 */
	@Override
	public void setBackground(Color background)
	{
		super.setBackground( background );

		Color messageBackground = new Color(background.getRGB());
		message.setBackground( messageBackground );
	}
//
//  Implement the KeyListener to consume events
//
	public void keyPressed(KeyEvent e)
	{
		e.consume();
	}

	public void keyTyped(KeyEvent e) {}

	public void keyReleased(KeyEvent e)
	{
		e.consume();
	}

	/*
	 *  Make the glass pane visible and change the cursor to the wait cursor
	 *
	 *  A message can be displayed and it will be centered on the frame.
	 */
	public void activate(String text)
	{
		if  (text != null && text.length() > 0)
		{
			message.setVisible( true );
			message.setText( text );
			message.setForeground( getForeground() );
		}
		else
			message.setVisible( false );

		setVisible( true );
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		requestFocusInWindow();
	}

	/*
	 *  Hide the glass pane and restore the cursor
	 */
	public void deactivate()
	{
		setCursor(null);
		setVisible( false );
	}
}
