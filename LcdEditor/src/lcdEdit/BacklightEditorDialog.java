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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class BacklightEditorDialog extends JDialog implements ActionListener, FocusListener {

	
    private Boolean modalResult;
    JTextField defaultField;
    JTextField groupAddrField;
    JCheckBox enableBusControl;
    JComboBox<String> formatSelectionBox;
    EIBObj groupAddress;
    
	BacklightEditorDialog (Frame myFrame) {
        super(myFrame, "Backlight Options", true);
        
        populateWindow ();
        
        modalResult = false;
    }

    void populateWindow () {
    	
    	// panel with grid
    	JPanel ePanel = new JPanel();
		GridLayout lm = new GridLayout (4,2);
		lm.setHgap(10);
		ePanel.setLayout( lm );
		add("North", ePanel);

		// Default dimming level
        defaultField = new JTextField (3);
        defaultField.setText("100");
        defaultField.addFocusListener(this);
		ePanel.add (new JLabel ("Default [%]:"));
		ePanel.add (defaultField);

		// enable control from bus
		enableBusControl = new JCheckBox ();
		enableBusControl.addActionListener(this);
		ePanel.add (new JLabel ("Bus control"));
		ePanel.add (enableBusControl);
		
		// Address input
        groupAddrField = new JTextField (8);
        groupAddrField.setText("0/0/0");
        groupAddrField.addFocusListener(this);
        groupAddrField.setEnabled(false);
		ePanel.add (new JLabel ("Group:"));
		ePanel.add (groupAddrField);

    	// Number format selection 
		formatSelectionBox = new JComboBox<String> ();
		for (int i = 0; i < HardwareFunctionType.backlightNumberFormatTypes.length; i++) {
			formatSelectionBox.addItem(HardwareFunctionType.backlightNumberFormatTypes[i]);
		}
		formatSelectionBox.setEnabled(false);
		ePanel.add (new JLabel ("Format:"));
		ePanel.add (formatSelectionBox);

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
   
	public Boolean getModalResult () {
    	return modalResult ;
    }

	@Override
	public void actionPerformed(ActionEvent event) {
		
		if (event.getSource().equals(enableBusControl)) {
	        groupAddrField.setEnabled(enableBusControl.isSelected ());
	        formatSelectionBox.setEnabled(enableBusControl.isSelected ());
	        return;
		}
		
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
	public void focusLost(FocusEvent ev) {

		if (ev.getSource().equals(groupAddrField)) {
			groupAddress.setAddr(AddrTranslator.getAdrValue(groupAddrField.getText()));
			groupAddrField.setText(AddrTranslator.getAdrString(groupAddress) );
		}
		if (ev.getSource().equals(defaultField)) {
			Integer l = new Integer ((String)defaultField.getText());
			if (l < 0)
				defaultField.setText ("0");
			if (l > 100)
				defaultField.setText ("100");
		}
	}

	public void setAddress(EIBObj groupAddress, boolean enabled) {

		this.groupAddress = new EIBObj (groupAddress.getAddr());
		groupAddrField.setText(AddrTranslator.getAdrString(groupAddress) );
		enableBusControl.setSelected (enabled);
		groupAddrField.setEnabled(enableBusControl.isSelected ());
        formatSelectionBox.setEnabled(enableBusControl.isSelected ());
	}

	public void setFormat(Integer format) {
		
		formatSelectionBox.setSelectedIndex(format);
	
	}

	public EIBObj getAddress() {
		return groupAddress;
	}

	public Integer getFormat() {
		return formatSelectionBox.getSelectedIndex();
	}
	
	public Boolean getBusControlEnabled () {
		return enableBusControl.isSelected ();
	}

	public void setDefault(Integer defaultBacklight) {
		defaultField.setText(defaultBacklight.toString());
	}
	public Integer getDefault() {
		return new Integer ((String)defaultField.getText());
	}


}
