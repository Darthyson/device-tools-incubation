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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DS18x20EditorDialog extends JDialog implements ActionListener, ItemListener, FocusListener {

	
    private Boolean dialogResult;
    
    JTextField groupAddrField;
    JTextField tempOffsetField;
    JComboBox<String> formatSelectionBox;
    JComboBox<String> repeatSelectionBox;
    EIBObj groupAddress;
    Float temperatureOffset;
    
	DS18x20EditorDialog (Frame myFrame, String header) {
        super(myFrame, header, true);
        
        populateWindow ();
        
        dialogResult = false;
    }

    void populateWindow () {
    	
    	// panel with grid
    	JPanel ePanel = new JPanel();
		GridLayout lm = new GridLayout (4,2);
		lm.setHgap(10);
		ePanel.setLayout( lm );
		add("North", ePanel);
    	
    	// Address input
        groupAddrField = new JTextField (8);
        groupAddrField.setText("0/0/0");
        groupAddrField.addFocusListener(this);
		ePanel.add (new JLabel ("Group:"));
		ePanel.add (groupAddrField);

    	// Function selection (1=high/0=low, 1=low/0=high, 1=high/0=flash, 1=low/1=flash)
		formatSelectionBox = new JComboBox<String> ();
		for (int i = 0; i < HardwareFunctionType.numberFormatTypes.length; i++) {
			formatSelectionBox.addItem(HardwareFunctionType.numberFormatTypes[i]);
		}
		ePanel.add (new JLabel ("Function:"));
		ePanel.add (formatSelectionBox);

		// temperature offset
		tempOffsetField = new JTextField (4);
		tempOffsetField.setText("0.0");
		tempOffsetField.addFocusListener(this);
		ePanel.add (new JLabel ("Offset:"));
		ePanel.add (tempOffsetField);
		
    	// flash frequency
		repeatSelectionBox = new JComboBox<String> ();
		for (int i = 0; i < HardwareFunctionType.repeatTimes.length; i++) {
			repeatSelectionBox.addItem(HardwareFunctionType.repeatTimes[i]);
		}
		ePanel.add (new JLabel ("Repetition:"));
		ePanel.add (repeatSelectionBox);
		
		formatSelectionBox.addItemListener(this);
		repeatSelectionBox.addItemListener(this);
    	
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

		if (ev.getSource().equals(groupAddrField)) {
			groupAddress.setAddr(AddrTranslator.getAdrValue(groupAddrField.getText()));
			groupAddrField.setText(AddrTranslator.getAdrString(groupAddress) );
		}
		if (ev.getSource().equals(tempOffsetField)) {
			temperatureOffset = Float.valueOf(tempOffsetField.getText());
			temperatureOffset = ((float) Math.round(10*temperatureOffset)) / 10;
			if (temperatureOffset > 12.7)
				temperatureOffset = (float) 12.7;
			if (temperatureOffset < -12.7)
				temperatureOffset = (float) -12.7;
			tempOffsetField.setText(temperatureOffset.toString());
		}
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {
		
	}

	public void setAddress(EIBObj groupAddress) {

		this.groupAddress = new EIBObj (groupAddress.getAddr());
		groupAddrField.setText(AddrTranslator.getAdrString(groupAddress) );
	}
	
	public void setTemperatureOffset (Float temperatureOffset) {
		
		this.temperatureOffset = new Float (temperatureOffset);
		tempOffsetField.setText(this.temperatureOffset.toString());
		
	}

	public Float getTemperatureOffset () {
		
		return temperatureOffset;
		
	}
	
	public void setFormat(Integer format) {
		
		formatSelectionBox.setSelectedIndex(format);
	
	}

	public void setRepetitionRate(Integer rate) {

		repeatSelectionBox.setSelectedIndex(rate);

	}

	public EIBObj getAddress() {
		return groupAddress;
	}

	public Integer getFormat() {
		return formatSelectionBox.getSelectedIndex();
	}

	public Integer getRepetitionRate() {
		return repeatSelectionBox.getSelectedIndex();
	}

}
