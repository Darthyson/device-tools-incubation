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

public class PhysicalAddress {

	protected int physicalAddress;
	
	public PhysicalAddress (){
		physicalAddress = 0xffff;
	}
	
	public PhysicalAddress (int physicalAddress){
		if (!setPhysicalAddress (physicalAddress))
			physicalAddress = 0xffff;
	}

	public PhysicalAddress (String physicalAddressString){
		if (!setPhysicalAddress (physicalAddressString))
			physicalAddress = 0xffff;
	}

	public boolean setPhysicalAddress (int physicalAddress) {
		if ((physicalAddress <= 0xffff) && (physicalAddress >= 0)) {
			this.physicalAddress = physicalAddress;	
			return true;
		}
		return false;
	}

	public boolean setPhysicalAddress (String adr) {
		int main = 0;
		int mid = 0;
		int sub = 0;

		String[] result = adr.split("\\.");
		if (result.length != 3) 
			return false;
		
		main = Integer.parseInt(result[0]);
		mid = Integer.parseInt(result[1]);
		sub = Integer.parseInt(result[2]);
		
		if ( (main >= 0) && (main < 16)
		  && (mid >= 0) && (mid < 16)
		  && (sub >= 0) && (sub < 256) ) {
			physicalAddress = main << 12 | mid << 8 | sub;
			return true;
		}
		return false;
	}
	
	public String getPhysicalAddressString () {
		return ""+((physicalAddress >> 12) & 0x0f)+"."+((physicalAddress >> 8) & 0x0f)+"."+(physicalAddress & 0xff);
	}

	public int getPhysicalAddress() {
		return physicalAddress;
	}
	
}
