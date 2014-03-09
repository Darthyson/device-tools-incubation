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

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class IREditorDialog extends JDialog implements ActionListener, ItemListener, FocusListener {

	
    private Boolean dialogResult;
    IRTableModel tableModel;
    
    JTable irButtonAssignmentTable;
    
	IREditorDialog (Frame myFrame) {
        super(myFrame, "IR Options", true);
        
        populateWindow ();
        
        dialogResult = false;
    }

    void populateWindow () {
    	
		// Table to assign KNX functions to key codes
    	tableModel = new IRTableModel();
		irButtonAssignmentTable = new JTable (tableModel);
		TableColumn buttonTypeColumn = irButtonAssignmentTable.getColumnModel().getColumn(4);
		
		JComboBox<ButtonFunctionType> comboBox = new JComboBox<ButtonFunctionType>();
		for (int i = 0; i < ButtonFunctionType.getFunctionTypes().length; i++)
			comboBox.addItem(new ButtonFunctionType(i));
		buttonTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));

		irButtonAssignmentTable.setPreferredScrollableViewportSize(new Dimension(600, 300));
		irButtonAssignmentTable.setFillsViewportHeight(true);
		setMinimumSize (new Dimension (620,400));
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(irButtonAssignmentTable);
	 	 
        //Add the scroll pane to this panel.
		add("Center", scrollPane);

        JPanel btnPanel = new JPanel ();
        JButton Ok = new JButton("OK");
        Ok.addActionListener(this);
        Ok.setActionCommand("ok");
        
        JButton Cancel = new JButton("Cancel");
        Cancel.addActionListener(this);
        Cancel.setActionCommand("cancel");

        btnPanel.add(Ok);
        btnPanel.add(Cancel);
        add("South", btnPanel);
        
        pack();
   	
    }
   
	public Boolean getDialogResult () {
   System.out.println (dialogResult);
    	return dialogResult;
    }

	@Override
	public void actionPerformed(ActionEvent event) {
    	dialogResult = (event.getActionCommand().equals("ok"));
    	setVisible(false);
	}
	
    public void processEvent(AWTEvent e) {
        if (e.getID() == Event.WINDOW_DESTROY) {
            dispose();
        } else {
            super.processEvent(e);
        }
    }

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent ev) {


	}

	@Override
	public void itemStateChanged(ItemEvent ev) {
		
	}

	public void setRc5Commands(RC5CommandList rc5Commands) {
		// we assign a clone to enable Cancel operation
		tableModel.setRc5Commands(new RC5CommandList (rc5Commands));
	}

	public RC5CommandList getRc5Commands() {
		// Return the current list from editing
		return tableModel.getRc5Commands();
	}


}
