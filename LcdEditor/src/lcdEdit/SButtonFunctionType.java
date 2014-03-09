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

public class SButtonFunctionType {

	public static final int FUNCTION_TOGGLE = 0;
	private int buttonFunction;
	private final String functionTypes[] = { "toggle", "on", "off" };

	public SButtonFunctionType(int buttonFunction) {
		this.buttonFunction = buttonFunction;
	}

	private String getButtonFunctionName (int function) {
		
		if ((function >= 0) && (function < functionTypes.length))
				return functionTypes [function];
		else return "";
	}
	
	@Override
	public String toString () {
		return getButtonFunctionName (buttonFunction);
	}
	
	public String [] getFunctionTypes () {
		return functionTypes;
	}

	public int getButtonFunction() {
		return buttonFunction;
	}

	public void setButtonFunction(int buttonFunction) {
		this.buttonFunction = buttonFunction;
	}
	
}
