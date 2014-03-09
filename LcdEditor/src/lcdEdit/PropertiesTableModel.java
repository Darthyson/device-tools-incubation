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

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class PropertiesTableModel extends DefaultTableModel {

	EIBComp clientComponent = null;
	
	public boolean isCellEditable( int rowIndex, int columnIndex ) 
	{ 
	  return columnIndex == 1; 
	}
	
	public void setEibComp (EIBComp clientComp) {
		clientComponent = clientComp;
	}

	public void setValueAt(Object aValue,
            int row,
            int column) {
		if (clientComponent == null) return;
		if (aValue == null) return;
		if (clientComponent.setNewValue (getValueAt (row,0) , aValue))
			super.setValueAt(aValue, row, column);
	}
	
    
    public EIBComp getInspectorOwner (){
    	return clientComponent;
    }

}
