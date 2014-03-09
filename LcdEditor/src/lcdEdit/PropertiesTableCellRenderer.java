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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class PropertiesTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent (
			JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		
		if (value instanceof Boolean) {
            boolean enabled = ((Boolean) value).booleanValue();
            final JCheckBox box = new JCheckBox();
            box.setSelected(enabled);
            return box;
        }

		if (value instanceof Color) {
			JPanel panel = new JPanel ();
			panel.setBackground((Color) value);
            return panel;
        }
        
		if (value instanceof Font) {
			Font font = (Font) value;
			value = "" + font.getFamily() + ", " + FontStyleCalc.getFontStyle(font.getStyle()) + ", " + font.getSize();
        }

        return super.getTableCellRendererComponent(table, 
                value, isSelected, hasFocus, row, column);
		
	}
	
}
