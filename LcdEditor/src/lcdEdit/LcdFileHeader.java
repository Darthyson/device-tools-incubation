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


public class LcdFileHeader {

	static final int SIZE_MAGIC = 6;
	static final int SIZE_VERSION = 1;
	static final int SIZE_PHYSICAL_ADDRESS = 2;
	static final int SIZE_ORIENTATION = 1;
	static final int SIZE_DIMMING = 1;
	static final int SIZE_SOURCE_FILE = 128;
	static final int SIZE_SOURCE_FILE_COMMENT = 40;
	static final int SIZE_TIME_DATE = 4;
	static final int SIZE_CHECKSUM = 1;
	
	static final int H_SIZE = SIZE_MAGIC + SIZE_VERSION + SIZE_PHYSICAL_ADDRESS + SIZE_ORIENTATION + SIZE_DIMMING + SIZE_SOURCE_FILE + SIZE_SOURCE_FILE_COMMENT;
	static final int HEADERSIZE = H_SIZE + SIZE_TIME_DATE + SIZE_CHECKSUM;
	protected String SourceFileName;
	protected String SourceFileComment;
	protected byte[] HeaderBuffer = new byte [HEADERSIZE];

	public LcdFileHeader () {
		// magic
		HeaderBuffer [0] = 'E';
		HeaderBuffer [1] = 'I';
		HeaderBuffer [2] = 'B';
		HeaderBuffer [3] = 'L';
		HeaderBuffer [4] = 'C';
		HeaderBuffer [5] = 'D';
		// file structure version
		HeaderBuffer [6] = 0x1E;
		// physical address of device
		HeaderBuffer [7] = (byte) 0xff;
		HeaderBuffer [8] = (byte) 0xff;
		// display orientation
		HeaderBuffer [9] = 0x00;
		// display dimming
		HeaderBuffer [10] = 0x10;
		// fill file name space
		for (int i = 0; i < SIZE_SOURCE_FILE + SIZE_SOURCE_FILE_COMMENT; i++)
			HeaderBuffer [i+11] = 0x00;		// fill file name space
	}
	
	public int getSize () {
		return HEADERSIZE;
	}
	
	public void writeToFile (DataOutputStream os) throws IOException {

		// add time-date info (UNIX like seconds since 1.1.1970)
		int time = (int) (System.currentTimeMillis() / 1000L);
		HeaderBuffer [H_SIZE + 0] = (byte) ( time        & 0xff);
		HeaderBuffer [H_SIZE + 1] = (byte) ((time >> 8 ) & 0xff);
		HeaderBuffer [H_SIZE + 2] = (byte) ((time >> 16) & 0xff);
		HeaderBuffer [H_SIZE + 3] = (byte) ((time >> 24) & 0xff);
		
		// calc checksum
		byte cs = 0;
		for (int i = 0; i <  + H_SIZE + SIZE_TIME_DATE; i++) {
			cs = (byte) (cs ^ HeaderBuffer [i]);
		}
		// output buffer to stream
		HeaderBuffer [H_SIZE + SIZE_TIME_DATE] = cs;
		os.write(HeaderBuffer);
	}

	public void setSourceFileName(String fileName) {

		SourceFileName = fileName;
		
		  if (SourceFileName.length() == 0) {
  			  SourceFileName = "no project file defined!";
  		  }
  		  byte[] SourceFileNameBytes = SourceFileName.getBytes();

  		  if (SourceFileNameBytes.length <= SIZE_SOURCE_FILE) {
  			  for (int i = 0; i < SourceFileNameBytes.length; i++)
  				  HeaderBuffer [SIZE_MAGIC+ SIZE_PHYSICAL_ADDRESS + SIZE_ORIENTATION + SIZE_DIMMING + SIZE_VERSION +i] = SourceFileNameBytes [i];
  		  }
  		  else {
  			  for (int i = SourceFileNameBytes.length - SIZE_SOURCE_FILE; i < SourceFileNameBytes.length; i++)
  				  HeaderBuffer [SIZE_MAGIC+ SIZE_PHYSICAL_ADDRESS + SIZE_ORIENTATION + SIZE_DIMMING + SIZE_VERSION +i] = SourceFileNameBytes [i];
  		  }
	}
	
	public void setSourceFileComment(String fileComment) {

		SourceFileComment = fileComment;
		
		  if (SourceFileComment.length() == 0) {
			  SourceFileComment = "no comment defined!";
  		  }
  		  byte[] SourceFileCommentBytes = SourceFileComment.getBytes();

  		  if (SourceFileCommentBytes.length <= SIZE_SOURCE_FILE_COMMENT) {
  			  for (int i = 0; i < SourceFileCommentBytes.length; i++)
  				  HeaderBuffer [SIZE_MAGIC + SIZE_PHYSICAL_ADDRESS + SIZE_ORIENTATION + SIZE_DIMMING + SIZE_VERSION + SIZE_SOURCE_FILE +i] = SourceFileCommentBytes [i];
  		  }
  		  else {
  			  for (int i = SourceFileCommentBytes.length - SIZE_SOURCE_FILE_COMMENT; i < SourceFileCommentBytes.length; i++)
  				  HeaderBuffer [SIZE_MAGIC + SIZE_PHYSICAL_ADDRESS + SIZE_ORIENTATION + SIZE_DIMMING + SIZE_VERSION + SIZE_SOURCE_FILE +i] = SourceFileCommentBytes [i];
  		  }
	}
	
	public void setPhysicalAddress (int physicalAddress) {
		// physical address of device
		HeaderBuffer [7] = (byte) ((physicalAddress >> 8) & 0xff);
		HeaderBuffer [8] = (byte) ((physicalAddress >> 0) & 0xff);
	}

	public void setDisplayOrientation (int orientation, int tft, boolean touchMirrorX, boolean touchMirrorY, boolean HWmirrorLCD) {
		// display configuration
		// d7: invert x, d6: invert y, d5: HW-mirror, d3-2: Display Type, d1-0: display orientation
		HeaderBuffer [9] = (byte) ((orientation & 0x0f) | ((tft & 0x0f) << 2));
		if (touchMirrorX)
			HeaderBuffer [9] |= (byte) 0x80;
		if (touchMirrorY)
			HeaderBuffer [9] |= (byte) 0x40;
		if (HWmirrorLCD)
			HeaderBuffer [9] |= (byte) 0x20;
	}

}
