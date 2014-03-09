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

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class RC5CommandList {
	
	public final static Integer IR_FUNCTION_COUNT = 32;
	
	private RC5Command[] rc5Commands = new RC5Command [IR_FUNCTION_COUNT];
	
	RC5CommandList () {
		for (int i = 0; i < IR_FUNCTION_COUNT; i++) {
			rc5Commands[i] = new RC5Command();
		}
	}
	
	RC5CommandList (RC5CommandList rc5list) {
		for (int i = 0; i < IR_FUNCTION_COUNT; i++) {
			rc5Commands[i] = new RC5Command(rc5list.getRC5Command(i));
		}
	}
	
	public RC5Command getRC5Command (Integer i) {
		if ((i>= 0) && (i < IR_FUNCTION_COUNT))
			return rc5Commands [i];
		return null;
	}
	
	public void writeToXML(TransformerHandler hd, String elementName) {
		
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("","","Name","CDATA", elementName);
		atts.addAttribute("","","FunctionType","CDATA", "IR");

		try {
			hd.startElement("","","HWResource",atts);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < IR_FUNCTION_COUNT; i++) {
			rc5Commands[i].writeToXML(hd, elementName);
		}

		// end of hardware description
		try {
			hd.endElement("","","HWResource");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readFromXML(XMLStreamReader parser) throws XMLStreamException {
		// iterate all RC5 commands
		Integer rc5Count = 0;
		int event = parser.next();
		while ((event != XMLStreamConstants.END_DOCUMENT) && (rc5Count < IR_FUNCTION_COUNT)) {

			if (event == XMLStreamConstants.START_ELEMENT) { 
		    	String Element = parser.getLocalName();
		    	// start new RC5 command?
		    	if (Element.equals("RC5Command")) {
		    		rc5Commands[rc5Count].readFromXML (parser);
		    		rc5Count++;
		    	}
		    	else { return; };
			}
			event = parser.next();
		}
	}

	public Integer getEnabledCount() {

	int c = 0;
	
		for (int i = 0; i < IR_FUNCTION_COUNT; i++)
			if (rc5Commands[i].getIrFunctionEnabled())
				c++;
		
		return c;
	}

	public void registerEibAddresses(LcdEibAddresses groupAddr) {

		for (int i = 0; i < IR_FUNCTION_COUNT; i++) {
			rc5Commands[i].registerEibAddresses (groupAddr);
		}
		
	}

	public void outputToLcdFile(String elementName,
			LcdListenerContainer listeners, LcdCyclicContainer cyclics,
			LcdEibAddresses groupAddr) {

		for (int i = 0; i < IR_FUNCTION_COUNT; i++) {
			rc5Commands[i].outputToLcdFile( elementName, listeners, cyclics, groupAddr);
		}
		
	}
	
}
