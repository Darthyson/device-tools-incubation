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

public class IOPinType {

	public static final int FUNCTION_TOGGLE = 0;
	private int ioPin;
	private final String ioPinTypes[] = { "PE0 (1)", "PE1 (9)", "PE2 (LP)", "PF4 (4)", "PF5 (7)",
											"PF6 (5)", "PF7 (6)"};

	public IOPinType(int ioPin) {
		this.ioPin = ioPin;
	}

	private String getIOPinName (int ioPin) {
		
		if ((ioPin >= 0) && (ioPin < ioPinTypes.length))
				return ioPinTypes [ioPin];
		else return "";
	}
	
	@Override
	public String toString () {
		return getIOPinName (ioPin);
	}
	
	public String [] getIOPinTypes () {
		return ioPinTypes;
	}

	public int getIOPin() {
		return ioPin;
	}

	public void setIOPin(int ioPin) {
		this.ioPin = ioPin;
	}

}
