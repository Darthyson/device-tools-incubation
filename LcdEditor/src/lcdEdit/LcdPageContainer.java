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

public class LcdPageContainer {

	static final int SIZE_CHECKSUM = 1;
	static final int SIZE_PAGE_OFFSET = 2;
	static final int SIZE_PAGE_COUNT = 1;
	static final int SIZE_PAGE_NAME = 16;
	static final int SIZE_PAGE_ELEMENT_COUNT = 1;

	protected int PageSize;
	protected List<Byte> PageBuffer = null;
	protected List<Integer> PageOffsets = null;	
	
	public LcdPageContainer () {
		PageSize = 0;
		PageOffsets = new ArrayList<Integer>();
		PageBuffer = new ArrayList<Byte>();
	}

	public void addPage (String pageName) {
		
		// add page offset to list
		PageOffsets.add(PageBuffer.size());
		// add page element counter to array
		PageBuffer.add ((byte)0);
		// add page name to start of page table
		if (pageName.length() == 0) {
			pageName = "Page "+PageOffsets.size();
  		  }
  		byte[] PageNameBytes = pageName.getBytes();
  		
  		for (int i = 0; i < SIZE_PAGE_NAME; i++) {
  			if ((i < PageNameBytes.length) && (i < SIZE_PAGE_NAME-1))
  				PageBuffer.add(PageNameBytes[i]);
  			else PageBuffer.add((byte)0x00);
  		}
//  		System.out.println("Adding page " + pageName);
	}
	
	public void addElement (byte elementSize, byte elementType, byte[] elementParameters){
		// increment element pointer
		int elementCounterPosition = PageOffsets.get(PageOffsets.size()-1);
		byte elementCount = (PageBuffer.get(elementCounterPosition));
		PageBuffer.set(elementCounterPosition, (byte) (elementCount+ 1));
		// add element size
		PageBuffer.add(elementSize);
		// add element type
		PageBuffer.add(elementType);
		// add element parameters
		for (int i = 0; i < elementParameters.length; i++)
			PageBuffer.add(elementParameters[i]);
	}
	
	public void writeToFile (DataOutputStream os) throws IOException {

		// output page count
		os.writeByte(PageOffsets.size());
		// output checksum:TODO: calc checksum
		byte checkSum = (byte) PageOffsets.size();
		// calc checksum of page offsets
		for (int i = 0; i < PageOffsets.size(); i++) {
			checkSum ^= (byte) ((PageOffsets.get(i) >> 8) & 0xff);
			checkSum ^= (byte)(PageOffsets.get(i) & 0xff);
		}
		// calc checksum of output page descriptions
		for (int i = 0; i < PageBuffer.size(); i++) {
			checkSum ^= PageBuffer.get(i);
		}
		os.writeByte(checkSum);
//  		System.out.printf("Pages checksum = %x\n", checkSum);

		// output page offsets
		for (int i = 0; i < PageOffsets.size(); i++) {
			os.writeByte(PageOffsets.get(i) & 0xff);
			os.writeByte((PageOffsets.get(i) >> 8) & 0xff);
		}
		// output page descriptions
		for (int i = 0; i < PageBuffer.size(); i++) {
			os.writeByte(PageBuffer.get(i));
		}
	}
	
	public long getSize () {
		return PageOffsets.size()*SIZE_PAGE_OFFSET + SIZE_CHECKSUM + SIZE_PAGE_COUNT + PageBuffer.size();
	}
	
	
}
