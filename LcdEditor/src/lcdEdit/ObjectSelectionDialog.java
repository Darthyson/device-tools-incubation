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
	import java.awt.Color;
	import java.awt.Event;
	import javax.swing.JButton;
	import javax.swing.JDialog;
	import javax.swing.JPanel;
	import javax.swing.JTextField;

	@SuppressWarnings("serial")
		public class ObjectSelectionDialog extends JDialog {

	    JTextField physAddrField;
	    Boolean modalResult;
	    PhysicalAddress physicalAddress;
	    JPanel usrPanel;
	    LcdEditor myParent;
		
	    ObjectSelectionDialog(LcdEditor myParent)
	    {
	        super(myParent, "Elements", false);
	        setVisible (false);
	        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
	        this.myParent = myParent;
	        usrPanel = new JPanel ();
	        add("Center", usrPanel);
	    }
		
	    public void processEvent(AWTEvent e) {
	        if (e.getID() == Event.WINDOW_DESTROY) {
	            dispose();
	        } else {
	            super.processEvent(e);
	        }
	    }

		public void addButton(String btnLabel, String btnCmd) {
	        JButton btn = new JButton (btnLabel);
	        btn.setActionCommand(btnCmd);
	        btn.setBackground(Color.lightGray);
	        btn.addActionListener(myParent);
	        usrPanel.add (btn);
	        pack();
		}
}
