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

import java.awt.Font;

public final class FontStyleCalc {

	public static String getFontStyle (int s) {
		
		if (s == Font.PLAIN) return "Plain";
		if (s == Font.BOLD) return "Bold";
		if (s == Font.ITALIC) return "Italic";
		if (s == (Font.BOLD | Font.ITALIC)) return "BoldItalic";
		return "";
	}

}
