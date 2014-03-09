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
public class DHTxxEditorDialog extends JDialog implements ActionListener, ItemListener, FocusListener {

	// supports DHT11/DHT22/DHT21
	
    private Boolean dialogResult;
    
    JTextField groupAddrField;
    JTextField groupAddrField2;
    JTextField tempOffsetField;
    JTextField humidityOffsetField;
    JComboBox<String> formatSelectionBox;
    JComboBox<String> repeatSelectionBox;
    JComboBox<String> dhtTypeSelectionBox;
    EIBObj groupAddress;
    EIBObj groupAddress2;
    Float temperatureOffset;
    Float humidityOffset;
    
	DHTxxEditorDialog (Frame myFrame, String header) {
        super(myFrame, header, true);
        
        populateWindow ();
        
        dialogResult = false;
    }

    void populateWindow () {
    	
    	// panel with grid
    	JPanel ePanel = new JPanel();
		GridLayout lm = new GridLayout (7,2);
		lm.setHgap(10);
		ePanel.setLayout( lm );
		add("North", ePanel);
    	
    	// Address input
        groupAddrField = new JTextField (8);
        groupAddrField.setText("0/0/0");
        groupAddrField.addFocusListener(this);
		ePanel.add (new JLabel ("Group (T):"));
		ePanel.add (groupAddrField);

		// Address input
        groupAddrField2 = new JTextField (8);
        groupAddrField2.setText("0/0/0");
        groupAddrField2.addFocusListener(this);
		ePanel.add (new JLabel ("Group (H):"));
		ePanel.add (groupAddrField2);

    	// Function selection (1=high/0=low, 1=low/0=high, 1=high/0=flash, 1=low/1=flash)
		formatSelectionBox = new JComboBox<String> ();
		for (int i = 0; i < HardwareFunctionType.numberFormatTypes.length; i++) {
			formatSelectionBox.addItem(HardwareFunctionType.numberFormatTypes[i]);
		}
		ePanel.add (new JLabel ("Function:"));
		ePanel.add (formatSelectionBox);
		
    	// DHT sensor type selection (0=DHT11, 1=DHT21, 2=DHT22)
		dhtTypeSelectionBox = new JComboBox<String> ();
		for (int i = 0; i < HardwareFunctionType.dhtSensorTypes.length; i++) {
			dhtTypeSelectionBox.addItem(HardwareFunctionType.dhtSensorTypes[i]);
		}
		ePanel.add (new JLabel ("Sensor:"));
		ePanel.add (dhtTypeSelectionBox);

		// temperature offset
		tempOffsetField = new JTextField (4);
		tempOffsetField.setText("0.0");
		tempOffsetField.addFocusListener(this);
		ePanel.add (new JLabel ("T-Offset:"));
		ePanel.add (tempOffsetField);

		// humidity offset
		humidityOffsetField = new JTextField (4);
		humidityOffsetField.setText("0.0");
		humidityOffsetField.addFocusListener(this);
		ePanel.add (new JLabel ("H-Offset:"));
		ePanel.add (humidityOffsetField);
		
    	// measurement interval. Start at 5s due to the limited speed of the sensor
		repeatSelectionBox = new JComboBox<String> ();
		for (int i = 2; i < HardwareFunctionType.repeatTimes.length; i++) {
			repeatSelectionBox.addItem(HardwareFunctionType.repeatTimes[i]);
		}
		ePanel.add (new JLabel ("Repetition:"));
		ePanel.add (repeatSelectionBox);
		
		formatSelectionBox.addItemListener(this);
		repeatSelectionBox.addItemListener(this);
		dhtTypeSelectionBox.addItemListener(this);
    	
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
		if (ev.getSource().equals(groupAddrField2)) {
			groupAddress2.setAddr(AddrTranslator.getAdrValue(groupAddrField2.getText()));
			groupAddrField2.setText(AddrTranslator.getAdrString(groupAddress2) );
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
		if (ev.getSource().equals(humidityOffsetField)) {
			humidityOffset = Float.valueOf(humidityOffsetField.getText());
			humidityOffset = ((float) Math.round(10*humidityOffset)) / 10;
			if (humidityOffset > 12.7)
				humidityOffset = (float) 12.7;
			if (humidityOffset < -12.7)
				humidityOffset = (float) -12.7;
			humidityOffsetField.setText(humidityOffset.toString());
		}
	}

	@Override
	public void itemStateChanged(ItemEvent ev) {
		
	}

	public void setAddress(EIBObj groupAddress) {

		this.groupAddress = new EIBObj (groupAddress.getAddr());
		groupAddrField.setText(AddrTranslator.getAdrString(groupAddress) );
	}

	public void setAddress2(EIBObj groupAddress) {

		this.groupAddress2 = new EIBObj (groupAddress.getAddr());
		groupAddrField2.setText(AddrTranslator.getAdrString(groupAddress2) );
	}
	
	public void setTemperatureOffset (Float temperatureOffset) {
		
		this.temperatureOffset = new Float (temperatureOffset);
		tempOffsetField.setText(this.temperatureOffset.toString());
		
	}

	public Float getTemperatureOffset () {
		
		return temperatureOffset;
		
	}
	
	public void setHumidityOffset (Float humidityOffset) {
		
		this.humidityOffset = new Float (humidityOffset);
		humidityOffsetField.setText(this.humidityOffset.toString());
		
	}

	public Float getHumidityOffset () {
		
		return humidityOffset;
		
	}

	public void setFormat(Integer format) {
		
		formatSelectionBox.setSelectedIndex(format);
	
	}

	public void setRepetitionRate(Integer rate) {

		// rate less than 5s makes no sense for this sensor
		if (rate < 2)
			rate = 2;
		repeatSelectionBox.setSelectedIndex(rate-2);

	}

	public EIBObj getAddress() {
		return groupAddress;
	}

	public EIBObj getAddress2() {
		return groupAddress2;
	}

	public Integer getFormat() {
		return formatSelectionBox.getSelectedIndex();
	}

	public Integer getRepetitionRate() {
		// rate less than 5s makes no sense for this sensor
		return repeatSelectionBox.getSelectedIndex()+2;
	}

	public void setDhtSensorType(Integer dhtSensorType) {
		
		dhtTypeSelectionBox.setSelectedIndex(dhtSensorType);
		
	}

	public Integer getDhtSensorType() {

		return dhtTypeSelectionBox.getSelectedIndex();
	}

}
