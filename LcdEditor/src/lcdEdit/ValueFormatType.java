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

public class ValueFormatType {

	private int valueFormat;
	private final String formatTypes[] = { "EIS6: 0-100%", "EIS6: 0-255", "EIS5: v.d", "EIS9: v.d",
										"EIS10: 0-65k", "EIS10: −32k - 32k", 
										"EIS11: 0-4G", "EIS11: -2G - 2G", 
										"EIS3: time", "EIS4: date"};

	public ValueFormatType(int valueFunction) {
		this.valueFormat = valueFunction;
	}

	private String getValueFormatName (int function) {
		
		if ((function >= 0) && (function < formatTypes.length))
				return formatTypes [function];
		else return "";
	}
	
	@Override
	public String toString () {
		return getValueFormatName (valueFormat);
	}
	
	public String [] getFunctionTypes () {
		return formatTypes;
	}

	public int getValueFormat() {
		return valueFormat;
	}

	public void setValueFormat(int valueFunction) {
		this.valueFormat = valueFunction;
	}

	public String getValueFormatExample(int integers, int decimals) {

		String s;
		
		// time
		if (valueFormat == 8) {
			return "23:59";
		}
		// date
		if (valueFormat == 9) {
			return "31.12.2012";
		}
		// 16 bit unsigned
		if (valueFormat == 4) {
			s = "1";
			if (integers > 0)
				for (int i = 0; i < integers-1; i++ )
					s = s + i;
			return s;
		}
		// 16 bit signed
		if (valueFormat == 5) {
			s = "-1";
			if (integers > 0)
				for (int i = 0; i < integers-1; i++ )
					s = s + i;
			return s;
		}
		// 32 bit unsigned
		if (valueFormat == 6) {
			s = "1";
			if (integers > 0)
				for (int i = 0; i < integers-1; i++ )
					s = s + i;
			return s;
		}
		// 32 bit signed
		if (valueFormat == 7) {
			s = "-1";
			if (integers > 0)
				for (int i = 0; i < integers-1; i++ )
					s = s + i;
			return s;
		}
		
		if ((valueFormat == 0) || (valueFormat == 1)) {
			decimals = 0;
			s = "";
		}
		else {
			s = "-";
		}
		
		for (int i = 1; i <= integers; i++) {
			s = s + i;
		}
		if (decimals > 0) {
			s = s+ ".";
			for (int d = 1; d <= decimals; d++) {
				s = s + d;
			}
		}
		return s;
	}
}
