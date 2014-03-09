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

public class LcdCyclicContainer {

	static final int SIZE_CHECKSUM = 1;
	static final int SIZE_ELEMENT_COUNT = 1;

	protected List<Byte> cyclicsBuffer = null;
	protected Integer cyclicsCount;
	
	public LcdCyclicContainer () {
		cyclicsBuffer = new ArrayList<Byte>();
		cyclicsCount = 0;
	}

	public void addElement (byte elementSize, byte elementType, byte[] elementParameters){

		// add element size
		cyclicsBuffer.add(elementSize);
		// add element type
		cyclicsBuffer.add(elementType);
		// add element parameters
		for (int i = 0; i < elementParameters.length; i++)
			cyclicsBuffer.add(elementParameters[i]);
		cyclicsCount++;
	}
	
	public void writeToFile (DataOutputStream os) throws IOException {

		// output element count
		os.writeByte(cyclicsCount);
		
		byte checkSum = (byte)(cyclicsCount & 0xff);
		// calc checksum of output page descriptions
		for (int i = 0; i < cyclicsBuffer.size(); i++) {
			checkSum ^= cyclicsBuffer.get(i);
		}
		os.writeByte(checkSum);

		// output cyclics descriptions
		for (int i = 0; i < cyclicsBuffer.size(); i++) {
			os.writeByte(cyclicsBuffer.get(i));
		}
	}
	
	public long getSize () {
		return SIZE_CHECKSUM + SIZE_ELEMENT_COUNT + cyclicsBuffer.size();
	}
	
}
