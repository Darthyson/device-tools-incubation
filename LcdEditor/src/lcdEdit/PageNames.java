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

public class PageNames extends Object {
	
	private String pageNames[];
	private String selectedPageName;

	public PageNames(String[] pageNames, String selectedPageName) {
		super();
		this.pageNames = pageNames;
		this.selectedPageName = selectedPageName;
	}

	public String[] getPageNames () {
		return pageNames;
	}
	
	@Override
	public String toString() {
		return selectedPageName;
	}

	public void setPageName(String selectedPageName) {
		this.selectedPageName = selectedPageName;		
	}	

}
