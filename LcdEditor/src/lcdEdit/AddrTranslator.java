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

public class AddrTranslator {

	
	public static char getAdrValue(String s) {
		int main = 0;
		int mid = 0;
		int sub = 0;

		String[] result = s.split("/");
		
		if (result.length == 3) {
			main = Integer.parseInt(result[0]);
			mid = Integer.parseInt(result[1]);
			sub = Integer.parseInt(result[2]);
		}
		
		if ( (main >= 0) && (main < 16)
		  && (mid >= 0) && (mid < 8)
		  && (sub >= 0) && (sub < 256) )
			return (char)( (main << 11) + (mid << 8) + sub  );
		
		return 0;
	}
	public static String getAdrString(EIBObj obj) {
		String addrString = "";
		if (obj == null)
			return "";

		char address = obj.getAddr();
		
		addrString = ((address >> 11) & 0x0F) + "/" + 
					 ((address >> 8 ) & 0x07) + "/" +
					 ((address      ) & 0xFF);
		return addrString;
	}

}