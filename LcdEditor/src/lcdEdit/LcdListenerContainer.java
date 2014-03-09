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
import java.util.List;

public class LcdListenerContainer {

	static final int SIZE_CHECKSUM = 1;
	static final int SIZE_ELEMENT_COUNT = 1;

	protected List<Byte> listnersBuffer = null;
	protected Integer listenersCount;
	
	public LcdListenerContainer () {
		listnersBuffer = new ArrayList<Byte>();
		listenersCount = 0;
	}

	public void addElement (byte elementSize, byte elementType, byte[] elementParameters){

		// add element size
		listnersBuffer.add(elementSize);
		// add element type
		listnersBuffer.add(elementType);
		// add element parameters
		for (int i = 0; i < elementParameters.length; i++)
			listnersBuffer.add(elementParameters[i]);
		listenersCount++;
	}
	
	public void writeToFile (DataOutputStream os) throws IOException {

		// output element count
		os.writeByte(listenersCount);
		
		byte checkSum = (byte)(listenersCount & 0xff);
		// calc checksum of output page descriptions
		for (int i = 0; i < listnersBuffer.size(); i++) {
			checkSum ^= listnersBuffer.get(i);
		}
		os.writeByte(checkSum);
//  		System.out.printf("Pages checksum = %x\n", checkSum);

		// output listeners descriptions
		for (int i = 0; i < listnersBuffer.size(); i++) {
			os.writeByte(listnersBuffer.get(i));
		}
	}
	
	public long getSize () {
		return SIZE_CHECKSUM + SIZE_ELEMENT_COUNT + listnersBuffer.size();
	}
	
}
