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

import java.awt.Dimension;
import java.awt.Point;

public class DisplayOrientation {

    enum Orientations {horizontal, left, right, upside };
    Orientations myOrientation;
    Integer xSize;
    Integer ySize;
    public static final Integer TFT_X_SIZE = 320;
    public static final Integer TFT_Y_SIZE = 240;
    
    public DisplayOrientation () {
    	myOrientation = Orientations.horizontal;
    	xSize = TFT_X_SIZE;
    	ySize = TFT_Y_SIZE;
    }
    
    public DisplayOrientation (Orientations o) {
    	myOrientation = o;
    	xSize = TFT_X_SIZE;
    	ySize = TFT_Y_SIZE;
		if ((o == Orientations.left) || (o == Orientations.right)) {
	    	xSize = TFT_Y_SIZE;
	    	ySize = TFT_X_SIZE;
		}
    }
    
    public void setOrientation (Orientations o) {
    	myOrientation = o;
    }
    
    public Orientations getOrientation () {
    	return myOrientation;
    }

	@Override
	public String toString () {
		return getOrientationString (myOrientation);
	}

	private String getOrientationString(Orientations o) {
		
		if (o == Orientations.horizontal)
			return "horizontal";
		if (o == Orientations.left)
			return "90° left";
		if (o == Orientations.right)
			return "90° right";
		if (o == Orientations.upside)
			return "180°";
		return "unknown";
	}

	public String getXmlOrientationString() {
		
		if (myOrientation == Orientations.horizontal)
			return "horizontal";
		if (myOrientation == Orientations.left)
			return "90left";
		if (myOrientation == Orientations.right)
			return "90right";
		if (myOrientation == Orientations.upside)
			return "180";
		return "unknown";
	}

	public void setFromXmlString (String s) {
		if (s.equals("horizontal")) {
			setOrientation (Orientations.horizontal);
			return;
		}
		if (s.equals("90left")) {
			setOrientation (Orientations.left);
			return;
		}
		if (s.equals("90right")) {
			setOrientation (Orientations.right);
			return;
		}
		if (s.equals("180")) {
			setOrientation (Orientations.upside);
			return;
		}
	}

	public Integer getXSize() {
		return xSize;
	}
	public Integer getYSize() {
		return ySize;
	}

	public void setXSize(Integer xSize) {
		this.xSize = xSize;
	}
	public void setYSize(Integer ySize) {
		this.ySize = ySize;
	}
	
	public Point getElementOrigin (Integer x, Integer y, Dimension size) {

		if (myOrientation == Orientations.left) {
			return new Point (TFT_X_SIZE -y-size.height,x);
		}
		if (myOrientation == Orientations.right) {
			return new Point (y, TFT_Y_SIZE -x-size.width);
		}
		if (myOrientation == Orientations.upside) {
			return new Point (TFT_X_SIZE-x-size.width, TFT_Y_SIZE-y-size.height);
		}
		return new Point (x,y);
	
	}

	// returns orientation code for the LCD module
	public int getOrientationCode() {

		if (myOrientation == Orientations.left)
			return 1;
		if (myOrientation == Orientations.right)
			return 2;
		if (myOrientation == Orientations.upside)
			return 3;
		return 0;
	}
	
}
