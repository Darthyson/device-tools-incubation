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

public class LcdFileTOC {

	protected static int TOC_ENTRIES = 6; // Must be adpated to Toc entries!
	protected static int TOC_ENTRY_SIZE = 9;
	protected static int SIZE_OF_TOC = 2 + TOC_ENTRY_SIZE*TOC_ENTRIES;
	
	protected LcdPageContainer pages = null;
	protected LcdImageContainer images = null;
	protected LcdEibAddresses addr = null;
	protected LcdSoundContainer sounds = null;
	protected LcdListenerContainer listeners = null;
	protected LcdCyclicContainer cyclics = null;
	
	public LcdFileTOC(LcdPageContainer pages, LcdImageContainer images, 
				LcdEibAddresses addr, LcdSoundContainer sounds, LcdListenerContainer listeners, LcdCyclicContainer cyclics) {
		
		this.pages = pages;
		this.images = images;
		this.addr = addr;
		this.sounds = sounds;
		this.listeners = listeners;
		this.cyclics = cyclics;
	}
	
	protected static byte TOC_TYPE_ADDR = 1;
	protected static byte TOC_TYPE_PAGES = 3;
	protected static byte TOC_TYPE_IMAGES = 4;
	protected static byte TOC_TYPE_SOUNDS = 5;
	protected static byte TOC_TYPE_LISTENERS = 6;
	protected static byte TOC_TYPE_CYCLICS = 7;

	protected void putLong (byte[] d, int pos, long v) {
		d[pos+3] = (byte) ((v >> 24) & 0xff);
		d[pos+2] = (byte) ((v >> 16) & 0xff);
		d[pos+1] = (byte) ((v >>  8) & 0xff);
		d[pos+0] = (byte) ((v >>  0) & 0xff);
	}
	
	protected void putOffsetLength (byte[] d, int pos, long offset, long size) {
		putLong (d, pos, offset);
		putLong (d, pos+4, size);
	}
	
	public void writeToFile(DataOutputStream os) throws IOException {

		long flashOffset = os.size() + SIZE_OF_TOC;
		// write element count
		os.writeByte(TOC_ENTRIES);
		// assemble TOC information in byte array
		byte[] Toc = new byte [TOC_ENTRIES*TOC_ENTRY_SIZE];
		int tocPos = 0;
		// first entry is the page list [0..8]
		System.out.println("TOC element " + TOC_TYPE_PAGES+ " to offset " + flashOffset);
		Toc[tocPos] = TOC_TYPE_PAGES;
		putLong (Toc, tocPos+1, flashOffset);
		putLong (Toc, tocPos+5, pages.getSize());
		flashOffset += pages.getSize();
		tocPos += TOC_ENTRY_SIZE;
		// 2nd entry is the EIB receive address table
		Toc[tocPos] = TOC_TYPE_ADDR;
		putLong (Toc, tocPos+1, flashOffset);
		putLong (Toc, tocPos+5, addr.getSize());
		flashOffset += addr.getSize();
		tocPos += TOC_ENTRY_SIZE;
		// next entry
		
		// next entry is image section
		System.out.println("TOC element " + TOC_TYPE_IMAGES);
		if (flashOffset % 2 == 1)
			flashOffset++;
		System.out.println("TOC element " + TOC_TYPE_IMAGES+ " to offset " + flashOffset);
		Toc[tocPos] = TOC_TYPE_IMAGES;
		putLong (Toc, tocPos+1, flashOffset);
		putLong (Toc, tocPos+5, images.getSize());
		flashOffset += images.getSize();		
		tocPos += TOC_ENTRY_SIZE;

		// next entry is sound section
		System.out.println("TOC element " + TOC_TYPE_SOUNDS);
		if (flashOffset % 2 == 1)
			flashOffset++;
		System.out.println("TOC element " + TOC_TYPE_SOUNDS+ " to offset " + flashOffset);
		Toc[tocPos] = TOC_TYPE_SOUNDS;
		putLong (Toc, tocPos+1, flashOffset);
		putLong (Toc, tocPos+5, sounds.getSize());
		flashOffset += sounds.getSize();		
		tocPos += TOC_ENTRY_SIZE;

		// next entry is always listening element section
//		System.out.println("TOC element " + TOC_TYPE_LISTENERS);
		System.out.println("TOC element " + TOC_TYPE_LISTENERS+ " to offset " + flashOffset);
		Toc[tocPos] = TOC_TYPE_LISTENERS;
		putLong (Toc, tocPos+1, flashOffset);
		putLong (Toc, tocPos+5, listeners.getSize());
		flashOffset += listeners.getSize();		
		tocPos += TOC_ENTRY_SIZE;

		// next entry is cyclic element section
		System.out.println("TOC element " + TOC_TYPE_CYCLICS+ " to offset " + flashOffset);
		Toc[tocPos] = TOC_TYPE_CYCLICS;
		putLong (Toc, tocPos+1, flashOffset);
		putLong (Toc, tocPos+5, cyclics.getSize());
		flashOffset += cyclics.getSize();		
		tocPos += TOC_ENTRY_SIZE;

		// write checksum of TOC
		byte checkSum = (byte) TOC_ENTRIES;
		for (int i = 0; i < Toc.length; i++)
			checkSum ^= Toc[i];
		os.writeByte(checkSum);
		// output TOC table
		os.write(Toc);
	}

}
