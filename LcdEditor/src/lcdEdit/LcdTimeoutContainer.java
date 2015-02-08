package lcdEdit;
/*
 * Component of the LCD Timeout Object
 * This tool enables the EIB LCD Touch display user to configure the display pages 
 * and save them in a binary format, which can be downloaded into the LCD Touch Display device.
 * 
 * Copyright (c) 2011-2015 Arno Stock <arno.stock@yahoo.de>
 *
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License version 2 as
 *	published by the Free Software Foundation.
 *
 */

import java.util.ArrayList;
import java.util.List;

public class LcdTimeoutContainer {
	
protected List<Integer> timeoutAddrTable = null;

	public LcdTimeoutContainer() {
		super();
		timeoutAddrTable = new ArrayList<Integer>();
	}
	
	/* parameters for the always listening object to trace timeout */
	protected static byte SIZE_TIMEOUT_PARAMETERS = 3;
	protected static byte SIZE_OF_TIMEOUT_OBJECT = (byte) (SIZE_TIMEOUT_PARAMETERS + 2); // +2 for type and size
	protected static byte TIMEOUT_OBJECT_TYPE = 4;
	
	// add new address to list
	public void addAddr (LcdEibAddresses eibAddresses, LcdListenerContainer listener, int addr){

	Integer idx;
	byte[] parameter = new byte [SIZE_TIMEOUT_PARAMETERS];

		// get address index
		idx = eibAddresses.getAddrIndex(addr);
		// is it already observed by timeout object?
		if (timeoutAddrTable.contains(idx))
			return;
				
		timeoutAddrTable.add (idx);
		
		parameter = new byte [SIZE_TIMEOUT_PARAMETERS];
		// EIB Object # for listen
		parameter [0] = (byte) (idx & 0xff);
		// destination page
		parameter [1] = (byte) 0x00;
		parameter [2] = (byte) 0x00;
		listener.addElement(SIZE_OF_TIMEOUT_OBJECT, TIMEOUT_OBJECT_TYPE, parameter);

	}
	
}
