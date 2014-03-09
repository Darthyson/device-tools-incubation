package lcdEdit;
/*
 * Component of the LCD Editor tool
 * This tool enables the EIB LCD Touch display user to configure the display pages 
 * and save them in a binary format, which can be downloaded into the LCD Touch Display device.
 * 
 * Copyright (c) 2011-2014 Arno Stock <arno.stock@yahoo.de>
 *
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License version 2 as
 *	published by the Free Software Foundation.
 *
 */

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;

@SuppressWarnings("serial")
public class JLayeredVisuPane extends JLayeredPane {

	Rectangle selectionRect;
	
	public Rectangle getSelectionRect() {
		return selectionRect;
	}

	public void setSelectionRect(Rectangle selectionRect) {
		this.selectionRect = selectionRect;
	}
	
	@Override
	public void paint (Graphics g) {
		super.paint (g);
		Dimension r = getPreferredSize ();
		g.setXORMode(Color.white);
		g.drawRect(0, 0, r.width, r.height);
		
		if (selectionRect != null) {
	        AlphaComposite ta = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
	        Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(ta);
	        g2d.setColor(Color.BLUE);
	        g2d.fill(selectionRect);
	        g2d.setComposite(AlphaComposite.SrcOver);
	        g2d.setColor(Color.BLACK);
	        g2d.draw(selectionRect);
		}
	}
}
