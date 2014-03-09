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

public class ButtonFunctionType {

	public static final int FUNCTION_TOGGLE = 0;
	public static final int FUNCTION_DELTA_EIS6 = 15;
	public static final int FUNCTION_DELTA_EIS5 = 16;
	private int buttonFunction;
	private final static String functionTypes[] = { "toggle", "on", "off", "brighter", "darker",
											"on/brighter", "off/darker", "up", "down", "step up", 
											"step down", "up/step up", "down/step down", 
											"8 bit value", "16 bit value", "EIS 6 +/-", "EIS 5 +/-"};

	public ButtonFunctionType(int buttonFunction) {
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
	
	public static final String [] getFunctionTypes () {
		return functionTypes;
	}

	public int getButtonFunction() {
		return buttonFunction;
	}

	public void setButtonFunction(int buttonFunction) {
		this.buttonFunction = buttonFunction;
	}
	
}
