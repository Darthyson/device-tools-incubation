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

public class DisplayProperties {

    enum Orientations {horizontal, left, right, upside };
    Orientations myOrientation;
    enum TFTTypes {tft_320_240, tft_800_480, tft_480_272};
    TFTTypes myTFTType;
    
    Integer xSize;
    Integer ySize;
    
    Boolean mirrorTouchX;
    Boolean mirrorTouchY;
    
    public DisplayProperties () {
    	myOrientation = Orientations.horizontal;
    	myTFTType = TFTTypes.tft_320_240;
    	xSize = getTFTXSize ();
    	ySize = getTFTYSize ();
    	mirrorTouchX = false;
    	mirrorTouchY = false;
    }
    
    public DisplayProperties (Orientations o, TFTTypes t) {
    	myOrientation = o;
    	myTFTType = t;
    	xSize = getTFTXSize ();
    	ySize = getTFTYSize ();
		if ((o == Orientations.left) || (o == Orientations.right)) {
	    	xSize = getTFTYSize ();
	    	ySize = getTFTXSize ();
		}
    	mirrorTouchX = false;
    	mirrorTouchY = false;
    }
    
    public void setOrientation (Orientations o) {
    	myOrientation = o;
    }
    
    public Orientations getOrientation () {
    	return myOrientation;
    }
    
    public void setTFTType (TFTTypes t) {
    	myTFTType = t;
    }

	@Override
	public String toString () {
		return getOrientationString (myOrientation);
	}

	private String getOrientationString(Orientations o) {
		
		String os = "unknown";
		
		if (o == Orientations.horizontal)
			os = "horizontal";
		if (o == Orientations.left)
			os = "90° left";
		if (o == Orientations.right)
			os = "90° right";
		if (o == Orientations.upside)
			os = "180°";
		
		if (myTFTType == TFTTypes.tft_320_240) {
			os = os + " (320x240)";
		}
		
		if (myTFTType == TFTTypes.tft_800_480) {
			os = os + " (800x480)";
		}

		if (myTFTType == TFTTypes.tft_480_272) {
			os = os + " (480x272)";
		}

		return os;
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

	public String getXmlTypeString() {

		if (myTFTType == TFTTypes.tft_320_240)
			return "320x240";

		if (myTFTType == TFTTypes.tft_800_480)
			return "800x480";

		if (myTFTType == TFTTypes.tft_480_272)
			return "480x272";

		return "unknown";
	}

	
	public void setOrientationFromXmlString (String s) {
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

	public void setTypeFromXmlString(String s) {

		if (s.equals("320x240")) {
			setTFTType (TFTTypes.tft_320_240);
			return;
		}
		if (s.equals("800x480")) {
			setTFTType (TFTTypes.tft_800_480);
			return;
		}
		if (s.equals("480x272")) {
			setTFTType (TFTTypes.tft_480_272);
			return;
		}
	}
	
	public void setTouchMirrorXFromXmlString(String s) {
		setMirrorTouchX(s.equalsIgnoreCase("true"));
	}
	
	public void setTouchMirrorYFromXmlString(String s) {
		setMirrorTouchY(s.equalsIgnoreCase("true"));
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
			return new Point (getTFTXSize () -y-size.height,x);
		}
		if (myOrientation == Orientations.right) {
			return new Point (y, getTFTYSize () -x-size.width);
		}
		if (myOrientation == Orientations.upside) {
			return new Point (getTFTXSize ()-x-size.width, getTFTYSize ()-y-size.height);
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
	
	public int getDisplayTypeCode() {
		if (myTFTType == TFTTypes.tft_320_240)
			return 0;
		if (myTFTType == TFTTypes.tft_800_480)
			return 1;
		if (myTFTType == TFTTypes.tft_480_272)
			return 2;
		return 0;
	}

	
	public int getTFTXSize () {
		
		if (myTFTType == TFTTypes.tft_320_240)
			return 320;
		
		if (myTFTType == TFTTypes.tft_800_480)
			return 800;

		if (myTFTType == TFTTypes.tft_480_272)
			return 480;
		
		return 0;
	}
	
	public int getTFTYSize () {
		
		if (myTFTType == TFTTypes.tft_320_240)
			return 240;
		
		if (myTFTType == TFTTypes.tft_800_480)
			return 480;
		
		if (myTFTType == TFTTypes.tft_480_272)
			return 272;

		return 0;
	}

	public Boolean getMirrorTouchX() {
		return mirrorTouchX;
	}

	public void setMirrorTouchX(Boolean mirrorTouchX) {
		this.mirrorTouchX = mirrorTouchX;
	}

	public Boolean getMirrorTouchY() {
		return mirrorTouchY;
	}

	public void setMirrorTouchY(Boolean mirrorTouchY) {
		this.mirrorTouchY = mirrorTouchY;
	}


}
