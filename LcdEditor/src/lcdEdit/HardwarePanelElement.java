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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

public class HardwarePanelElement extends Object implements ItemListener, ActionListener {

	String elementName;
	JComboBox<HardwareFunctionType> functionSelectionBox;
	JButton setupButton;
	JLabel parameterLabel;
	Frame myFrame;

	public HardwarePanelElement(String elementName, Frame myFrame, JPanel elementPanel) {
		super( );
		this.myFrame = myFrame;
		
		this.elementName = elementName;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = GridBagConstraints.RELATIVE;
		c.insets = new Insets (2, 2, 2, 2);
		
		if (elementName.isEmpty()) {
			elementPanel.add (new JLabel ("Hardware"), c);
			elementPanel.add (new JLabel ("Function"), c);
			elementPanel.add (new JLabel ("Setting"), c);
			elementPanel.add (new JLabel ("Summary"), c);
		}
		else {
			c.gridx = 0;
			elementPanel.add (new JLabel (" "+elementName),c);
			functionSelectionBox = new JComboBox<HardwareFunctionType> ();

			if (elementName.contains ("backlight")) {
				for (int i = 0; i < HardwareFunctionType.backLightFunctionTypes.length; i++) {
					HardwareFunctionType h = new HardwareFunctionType (HardwareFunctionType.backLightFunctionTypes[i]);
					functionSelectionBox.addItem(h);
					if (elementName.contains("active"))
						h.setDefaultBacklight(100);
					else if (elementName.contains("idle"))
						h.setDefaultBacklight(6);
					else if (elementName.contains("control"))
						h.setDefaultBacklightTime(30);
				}
			}
			else if (elementName.contains ("PE1")) {
				for (int i = 0; i < HardwareFunctionType.PE1functionTypes.length; i++) {
					functionSelectionBox.addItem(new HardwareFunctionType (HardwareFunctionType.PE1functionTypes[i]));
				}
			}
			else {
				for (int i = 0; i < HardwareFunctionType.functionTypes.length; i++) {
					functionSelectionBox.addItem(new HardwareFunctionType (HardwareFunctionType.functionTypes[i]));
				}		
			}
			c.gridx = 1;
			elementPanel.add (functionSelectionBox, c);
			setupButton = new JButton ("Setup");
			setupButton.setEnabled(((HardwareFunctionType) functionSelectionBox.getSelectedItem()).hasParameters());
			setupButton.addActionListener(this);
			c.gridx = 2;
			elementPanel.add (setupButton, c);
			parameterLabel = new JLabel ( ((HardwareFunctionType) functionSelectionBox.getSelectedItem()).getFunctionParameters() );
			c.gridx = 3;
			elementPanel.add (parameterLabel, c);
//FIXME: this is a trial
//			elementPanel.add(Box.createHorizontalGlue());
		
			functionSelectionBox.addItemListener(this);
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent changedItem) {
		if ((changedItem.getSource().equals(functionSelectionBox)) && (changedItem.getStateChange() == ItemEvent.SELECTED)) {
			setupButton.setEnabled(((HardwareFunctionType) functionSelectionBox.getSelectedItem()).hasParameters());
			parameterLabel.setText (((HardwareFunctionType) functionSelectionBox.getSelectedItem()).getFunctionParameters() );
//FIXME
//			myFrame.setPreferredSize(getPreferredSize());
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource().equals(setupButton)) {
			// open modal dialog to edit my properties
			((HardwareFunctionType) functionSelectionBox.getSelectedItem()).showEditDialog (myFrame);
			parameterLabel.setText(   ((HardwareFunctionType) functionSelectionBox.getSelectedItem()).getFunctionParameters());
//FIXME
//			myFrame.setPreferredSize(getPreferredSize());
		}
		
	}

	public void writeToXML(TransformerHandler hd) {
		
		if (functionSelectionBox != null)
			((HardwareFunctionType)functionSelectionBox.getSelectedItem()).writeToXML (hd, elementName);
	}
	
	// legacy function to convert former backlight intensity value
	public void setBacklightDefault (int defaultBacklight) {
		((HardwareFunctionType) functionSelectionBox.getSelectedItem()).setDefaultBacklight(defaultBacklight);
		parameterLabel.setText (((HardwareFunctionType) functionSelectionBox.getSelectedItem()).getFunctionParameters() );
	}

	public void readFromXML(String elementName, XMLStreamReader parser) throws XMLStreamException {

		// check, if data belongs to this element 
		if (!elementName.equals(this.elementName)) {
//System.out.println (elementName + " != " + this.elementName);
			return;
		}
		
		// set function accordingly
		String function = parser.getAttributeValue ( null, "FunctionType");

		if (elementName.contains ("backlight")) {
			for (int i = 0; i < HardwareFunctionType.backLightFunctionTypes.length; i++) {
				if (HardwareFunctionType.backLightFunctionTypes[i].equals(function)) {
					functionSelectionBox.setSelectedIndex(i);
					((HardwareFunctionType) functionSelectionBox.getSelectedItem()).readFromXML (parser, function);
				}
			}
		}
		else if (elementName.contains ("PE1")) {
			for (int i = 0; i < HardwareFunctionType.PE1functionTypes.length; i++) {
				if (HardwareFunctionType.PE1functionTypes[i].equals(function)) {
					functionSelectionBox.setSelectedIndex(i);
					((HardwareFunctionType) functionSelectionBox.getSelectedItem()).readFromXML (parser, function);
				}
			}
		}
		else {
			for (int i = 0; i < HardwareFunctionType.functionTypes.length; i++) {
				if (HardwareFunctionType.functionTypes[i].equals(function)) {
					functionSelectionBox.setSelectedIndex(i);
					((HardwareFunctionType) functionSelectionBox.getSelectedItem()).readFromXML (parser, function);
				}
			}		
		}
		// update parameter list
		parameterLabel.setText (((HardwareFunctionType) functionSelectionBox.getSelectedItem()).getFunctionParameters() );
	}

	public String getElementName() {
		return elementName;
	}

	public void outputToLcdFile(LcdListenerContainer listeners,
			LcdCyclicContainer cyclics, LcdEibAddresses groupAddr) {
		
		// check my function
		if (functionSelectionBox != null)
			((HardwareFunctionType)functionSelectionBox.getSelectedItem()).outputToLcdFile(elementName, listeners, cyclics, groupAddr);
	}

	public void registerEibAddresses(LcdEibAddresses groupAddr) {

		// check my function
		if (functionSelectionBox != null)
			((HardwareFunctionType)functionSelectionBox.getSelectedItem()).registerEibAddresses(groupAddr);
		
	}
	

}
