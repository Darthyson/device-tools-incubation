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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ProjectPropertiesDialog extends JDialog implements ActionListener, ItemListener, FocusListener {

    JComboBox<DisplayProperties> orientationBox;
    JTextField physAddrField;
    JCheckBox cbMirrorTouchX;
    JCheckBox cbMirrorTouchY;
    Boolean modalResult;
    PhysicalAddress physicalAddress;
    Integer dimmingLevel;
	
	ProjectPropertiesDialog(Frame myFrame)
    {
        super(myFrame, "Project Options", true);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        JPanel sizePanel = new JPanel ();
        sizePanel.setLayout(new BoxLayout (sizePanel, BoxLayout.Y_AXIS));
        JPanel usrPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));
        JPanel oriPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));

        orientationBox = new JComboBox<DisplayProperties> (); 
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.horizontal, DisplayProperties.TFTTypes.tft_320_240));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.left, DisplayProperties.TFTTypes.tft_320_240));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.right, DisplayProperties.TFTTypes.tft_320_240));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.upside, DisplayProperties.TFTTypes.tft_320_240));
      
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.horizontal, DisplayProperties.TFTTypes.tft_800_480));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.left, DisplayProperties.TFTTypes.tft_800_480));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.right, DisplayProperties.TFTTypes.tft_800_480));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.upside, DisplayProperties.TFTTypes.tft_800_480));

       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.horizontal, DisplayProperties.TFTTypes.tft_480_272));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.left, DisplayProperties.TFTTypes.tft_480_272));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.right, DisplayProperties.TFTTypes.tft_480_272));
       	orientationBox.addItem(new DisplayProperties(DisplayProperties.Orientations.upside, DisplayProperties.TFTTypes.tft_480_272));
        orientationBox.addFocusListener(this);

        cbMirrorTouchX = new JCheckBox ("Mirror Touch (X)");
        cbMirrorTouchY = new JCheckBox ("Mirror Touch (Y)");
        
        physAddrField = new JTextField (6);
        physAddrField.addFocusListener(this);

        usrPanel.add(new JLabel ("Phys. Addr:"));
        usrPanel.add(physAddrField);
        oriPanel.add(new JLabel ("Display"));
        oriPanel.add(orientationBox);
       
        sizePanel.add("North", usrPanel);
        sizePanel.add("Center", oriPanel);
        sizePanel.add("South", cbMirrorTouchX);
        sizePanel.add("South", cbMirrorTouchY);
        
        add("Center", sizePanel);
        
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
	
    public void actionPerformed(ActionEvent event) {
    	modalResult = (event.getActionCommand().equals("ok"));
    	setVisible(false);
    	physicalAddress.setPhysicalAddress(physAddrField.getText());
    }
    
    public Boolean getModalResult () {
    	return modalResult;
    }

    public void processEvent(AWTEvent e) {
        if (e.getID() == Event.WINDOW_DESTROY) {
            dispose();
        } else {
            super.processEvent(e);
        }
    }

	@Override
	public void itemStateChanged(ItemEvent e) {
	}
    
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == physAddrField) {
			physicalAddress.setPhysicalAddress(physAddrField.getText());
		}
	}
	
	public PhysicalAddress getPhysicalAddress() {
		return physicalAddress;
	}

	public void setPhysicalAddress(PhysicalAddress physicalAddress) {
		this.physicalAddress = new PhysicalAddress (physicalAddress.getPhysicalAddress());
		physAddrField.setText(physicalAddress.getPhysicalAddressString());
	}
	
	public void setOrientation(DisplayProperties dP) {
		if ( dP.getDisplayTypeCode() == 0) {
			if ( dP.getOrientation() == DisplayProperties.Orientations.horizontal) 
				orientationBox.setSelectedIndex(0);
			if ( dP.getOrientation() == DisplayProperties.Orientations.left) 
				orientationBox.setSelectedIndex(1);
			if ( dP.getOrientation() == DisplayProperties.Orientations.right) 
				orientationBox.setSelectedIndex(2);
			if ( dP.getOrientation() == DisplayProperties.Orientations.upside) 
				orientationBox.setSelectedIndex(3);
		}
		if ( dP.getDisplayTypeCode() == 1) {
			if ( dP.getOrientation() == DisplayProperties.Orientations.horizontal) 
				orientationBox.setSelectedIndex(4);
			if ( dP.getOrientation() == DisplayProperties.Orientations.left) 
				orientationBox.setSelectedIndex(5);
			if ( dP.getOrientation() == DisplayProperties.Orientations.right) 
				orientationBox.setSelectedIndex(6);
			if ( dP.getOrientation() == DisplayProperties.Orientations.upside) 
				orientationBox.setSelectedIndex(7);
		}
		if ( dP.getDisplayTypeCode() == 2) {
			if ( dP.getOrientation() == DisplayProperties.Orientations.horizontal) 
				orientationBox.setSelectedIndex(8);
			if ( dP.getOrientation() == DisplayProperties.Orientations.left) 
				orientationBox.setSelectedIndex(9);
			if ( dP.getOrientation() == DisplayProperties.Orientations.right) 
				orientationBox.setSelectedIndex(10);
			if ( dP.getOrientation() == DisplayProperties.Orientations.upside) 
				orientationBox.setSelectedIndex(11);
		}
	}
	
	public DisplayProperties getDisplayOrientation () {
		return (DisplayProperties) orientationBox.getSelectedItem();
	}

	public Boolean getTouchMirrorX () {
		return cbMirrorTouchX.isSelected();
	}
	
	public Boolean getTouchMirrorY () {
		return cbMirrorTouchY.isSelected();
	}
	
	public void setTouchMirrorX (Boolean b) {
		cbMirrorTouchX.setSelected(b);
	}

	public void setTouchMirrorY (Boolean b) {
		cbMirrorTouchY.setSelected(b);
	}

}
