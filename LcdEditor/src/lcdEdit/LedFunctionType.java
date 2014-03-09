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

public class LedFunctionType {

	public static final int FUNCTION_BINARY = 0;
	public static final int FUNCTION_RADIO = 1;
	public static final int FUNCTION_WARNING = 2;
	private int ledFunction;
	private final static String functionTypes[] = { "binary", "radio", "warning" };

	public LedFunctionType(int ledFunction) {
		this.ledFunction = ledFunction;
	}

	private String getLedFunctionName (int function) {
		
		if ((function >= 0) && (function < functionTypes.length))
				return functionTypes [function];
		else return "";
	}
	
	@Override
	public String toString () {
		return getLedFunctionName (ledFunction);
	}
	
	public String [] getFunctionTypes () {
		return functionTypes;
	}

	public int getLEDFunction() {
		return ledFunction;
	}

	public void setLedFunction(int ledFunction) {
		this.ledFunction = ledFunction;
	}
	
}
