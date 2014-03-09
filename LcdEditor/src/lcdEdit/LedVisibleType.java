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

public class LedVisibleType {

	public static final int LED_VISIBLE_ALWAYS = 0;
	public static final int LED_VISIBLE_ON = 1;
	public static final int LED_VISIBLE_OFF = 2;
	private int ledVisible;
	private final static String visibleTypes[] = { "always", "on", "off" };

	public LedVisibleType(int ledVisible) {
		this.ledVisible = ledVisible;
	}

	private String getLedVisibleString (int visible) {
		
		if ((visible >= 0) && (visible < visibleTypes.length))
				return visibleTypes [visible];
		else return "";
	}
	
	@Override
	public String toString () {
		return getLedVisibleString (ledVisible);
	}
	
	public String [] getVisibleTypes () {
		return visibleTypes;
	}

	public int getLEDVisible() {
		return ledVisible;
	}

	public void setLedVisible(int ledVisible) {
		this.ledVisible = ledVisible;
	}
	
}
