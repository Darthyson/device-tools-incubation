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
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

@SuppressWarnings("serial")
public class HardwarePropertiesDialog extends JDialog implements ActionListener, ItemListener, FocusListener {

	private Frame myFrame;
    private Boolean modalResult;
	private final static String ioPinNames[] = { "PE0 (1)", "PE1 (9)", "PE2 (LP)", "PF4 (4)", "PF5 (7)",
			"PF6 (5)", "PF7 (6)", "backlight active", "backlight idle" };
	List<HardwarePanelElement> hweList;
    
    HardwarePropertiesDialog (Frame myFrame) {
        super(myFrame, "Hardware Options", true);
        this.myFrame = myFrame;
        populateWindow ();
        
        modalResult = false;
    }

    void populateWindow () {
    	
    	hweList = new ArrayList<HardwarePanelElement> ();
    	// create scrollpane for all HW elements
    	JPanel elementPanel;
    	elementPanel = new JPanel ();
    	elementPanel.setLayout(new GridBagLayout ());
    	new HardwarePanelElement ("", myFrame, elementPanel);
    	for (int i = 0; i < ioPinNames.length; i++) {
    		hweList .add(new HardwarePanelElement (ioPinNames[i], myFrame, elementPanel));
    	}
  	
    	JScrollPane elementPane = new JScrollPane (elementPanel);
    	add ("Center", elementPane);
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
//FIXME        this.setMinimumSize (new Dimension (600,320));
        pack();
   	
    }
    
    public final static Integer getHardwareID (String name) {
		
		for (int i = 0; i < ioPinNames.length; i++)
			if (name.contains (ioPinNames[i]) )
				return i;
		return -1;
	}
 
    
	public Boolean getModalResult () {
    	return modalResult ;
    }

	@Override
	public void actionPerformed(ActionEvent event) {
    	modalResult = (event.getActionCommand().equals("ok"));
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
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void writeToXML(TransformerHandler hd) {

		// write all hardware settings to XML file
		for (int i = 0; i < hweList.size(); i++) {
			((HardwarePanelElement)hweList.get(i)).writeToXML (hd);
		}
	}

	public void readFromXML(XMLStreamReader parser) throws XMLStreamException {

		// search for element name
		String elementName = parser.getAttributeValue( null, "Name" );
		if (elementName == null) {
			System.err.println ("Error: HWFunction without name.");
			return;
		}
		
		// convenience function for existing projects
		if (elementName.contentEquals("DHT11")) {
			elementName = "DHTxx";
		}

		for (int e = 0; e < hweList.size(); e++) {
			((HardwarePanelElement)hweList.get(e)).readFromXML (elementName, parser);
		}
	}
	
	// legacy function to convert former backlight intensity value
	public void setBacklightDefault (int defaultBacklight) {

		for (int e = 0; e < hweList.size(); e++) {
			if ( ((HardwarePanelElement)hweList.get(e)).getElementName().contains("backlight idle")) {
				((HardwarePanelElement)hweList.get(e)).setBacklightDefault (defaultBacklight);
			}
		}
		
	}

	public void outputToLcdFile(LcdListenerContainer listeners,
			LcdCyclicContainer cyclics, LcdEibAddresses groupAddr) {

		// write elements to the always listening and cycling processing lists
		for (int i = 0; i < hweList.size(); i++) {
			((HardwarePanelElement)hweList.get(i)).outputToLcdFile (listeners, cyclics, groupAddr);
		}
		
	}

	public void registerEibAddresses(LcdEibAddresses groupAddr) {

		// register group addresses
		for (int i = 0; i < hweList.size(); i++) {
			((HardwarePanelElement)hweList.get(i)).registerEibAddresses(groupAddr);
		}
		
	}
}
