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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LcdEibAddresses {

	protected List<Integer> eibAddrTable = null;

	public LcdEibAddresses () {

		eibAddrTable = new ArrayList<Integer>();

	}
	
	// determine length of table
	public long getSize () {
		return eibAddrTable.size() *2 ;
	}

	// write address table to file
	public void writeToFile (DataOutputStream os) throws IOException {

		for (int i = 0; i < eibAddrTable.size(); i++) {
			os.writeByte ( (byte) ((eibAddrTable.get(i) >> 8) & 0xff));
			os.writeByte ( (byte) ((eibAddrTable.get(i) >> 0) & 0xff));
		}			
	}
	
	// get sorted index of address from list
	public int getAddrIndex (int addr) {
		
		// sort list
		Collections.sort(eibAddrTable);
		
		return eibAddrTable.indexOf(addr);
	}

	// add new address to list
	public void addAddr (int addr){
		if (!eibAddrTable.contains(addr))
			eibAddrTable.add (addr);
	}
	
}
