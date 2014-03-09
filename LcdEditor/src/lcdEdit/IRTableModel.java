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

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class IRTableModel extends AbstractTableModel implements TableModel {

	private String[] columnNames = {"#", "Enable", "Addr", "Cmd", "Function", "Addr 0", "Addr 1", "Value", "Comment"};
	private RC5CommandList rc5Commands;

	
	public IRTableModel() {
		super ();
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return RC5CommandList.IR_FUNCTION_COUNT;
	}
    
	public String getColumnName(int col) {
        return columnNames[col];
    }
	
	@Override
	public Object getValueAt(int arg0, int arg1) {
		if (arg1 == 0) // index column
			return arg0+1;
		if (arg1 == 1) // enable function in this row
			return rc5Commands.getRC5Command(arg0).getIrFunctionEnabled();
		if (arg1 == 2) // RC5 Address
			return rc5Commands.getRC5Command(arg0).getRc5Address();
		if (arg1 == 3) // RC5 Command
			return rc5Commands.getRC5Command(arg0).getRc5Command();
		if (arg1 == 4) // KNX Function
			return rc5Commands.getRC5Command(arg0).getRc5Type();
		if (arg1 == 5) // KNX Addr 0
			return AddrTranslator.getAdrString(rc5Commands.getRC5Command(arg0).getRc5Obj0());
		if (arg1 == 6) // KNX Addr 1
			if (rc5Commands.getRC5Command(arg0).getRc5Obj1() != null)
				return AddrTranslator.getAdrString(rc5Commands.getRC5Command(arg0).getRc5Obj1());
		if (arg1 == 7) // KNX value
			return rc5Commands.getRC5Command(arg0).getRc5KNXValue();
		if (arg1 == 8) // KNX value
			return rc5Commands.getRC5Command(arg0).getComment();

		return "";
	}

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int c) {
    	
   System.out.println (" col "+c);
    	
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {

    	System.out.println ("Value = " + value + " at ("+row+"/"+col+")");
    	
    	if (col == 1) {
    		rc5Commands.getRC5Command(row).setIrFunctionEnabled((Boolean)value);
    	}
    	if (col == 2) {
    		rc5Commands.getRC5Command(row).setRc5Address((Integer) value);
    	}
    	if (col == 3) {
    		rc5Commands.getRC5Command(row).setRc5Command((Integer) value);
    	}
    	
    	if (col == 4) {
    		rc5Commands.getRC5Command(row).setRc5Type ((ButtonFunctionType)value);
    	}
    	
    	if (col == 5) {
    		if (!((String) value).equals ("")) {
    			rc5Commands.getRC5Command(row).getRc5Obj0().setAddr (AddrTranslator.getAdrValue((String)value));
    		}
    	}
    	
    	if (col == 6) {
    		if (!((String) value).equals ("")) {
    			rc5Commands.getRC5Command(row).setRc5Obj1(new EIBObj (AddrTranslator.getAdrValue((String)value)));
    		}
    		else {
    			rc5Commands.getRC5Command(row).setRc5Obj1 (null);
    		}
    	}

    	if (col == 7) {
    		rc5Commands.getRC5Command(row).setRc5KNXValue((Integer) value);
    	}

    	if (col == 8) {
    		rc5Commands.getRC5Command(row).setComment((String) value);
    	}
         fireTableCellUpdated(row, col);

    }

	/**
	 * @return the rc5Commands
	 */
	public RC5CommandList getRc5Commands() {
		return rc5Commands;
	}

	/**
	 * @param rc5Commands the rc5Commands to set
	 */
	public void setRc5Commands(RC5CommandList rc5Commands) {
		this.rc5Commands = rc5Commands;
	}

	
}
