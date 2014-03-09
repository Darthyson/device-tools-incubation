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

import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class EditorPageTreeNode extends DefaultMutableTreeNode {

	Object myObject;
	
	public EditorPageTreeNode(String pageName) {
		super (pageName);
	}

	public void setObject (Object o) {
		myObject = o;
	}
	
	public Object getObject () {
		return myObject;
	}
	
}
