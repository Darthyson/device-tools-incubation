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

public class EIBObj {

	char addr = 0;
	public boolean init = false;
	
	public EIBObj(char Addr) {
		addr = Addr;
	}
	
	// dealing with the objects address
	public void setAddr(char address) {
		addr = address;
	}
	
	boolean matchAddr (char address) {
		return (address == addr);
	}

	boolean matchAddr (byte hb, byte lb) {
		char adr = (char)hb;
		adr = (char) ((adr << 8) + lb);
		return (adr == addr);
	}

	public byte getAddrLowByte() {
		return (byte) (addr & 0xff);
	}
	
	public byte getAddrHighByte() {
		return (byte) ((addr >> 8) & 0xff);
	}

	public char getAddr() {
		return addr;
	}
	
}
