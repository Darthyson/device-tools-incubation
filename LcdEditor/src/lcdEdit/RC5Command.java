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

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class RC5Command {

	private Boolean irFunctionEnabled;
	private Integer rc5Address;
	private Integer rc5Command;
	private ButtonFunctionType rc5Type;
	private Integer rc5KNXValue;
	private EIBObj rc5Obj0;
	private EIBObj rc5Obj1;
	private String Comment;
	
	public RC5Command () {
		irFunctionEnabled = false;
		rc5Address = 0;
		rc5Command = 0;
		rc5Type = new ButtonFunctionType (ButtonFunctionType.FUNCTION_TOGGLE);
		rc5KNXValue = 0;
		rc5Obj0 = new EIBObj ((char)0);
		Comment = "";
	}
	
	public RC5Command (RC5Command cmd) {
		irFunctionEnabled = cmd.getIrFunctionEnabled ();
		rc5Address = cmd.rc5Address;
		rc5Command = cmd.rc5Command;
		rc5Type = new ButtonFunctionType (cmd.getRc5Type().getButtonFunction());
		rc5KNXValue = cmd.getRc5KNXValue();
		rc5Obj0 = new EIBObj (cmd.getRc5Obj0().getAddr());
		if (cmd.getRc5Obj1() != null) {
			rc5Obj1 = new EIBObj (cmd.getRc5Obj1().getAddr());
		}
		Comment = cmd.Comment;
	}

	
	public RC5Command (Boolean irFunctionEnabled, Integer rc5Address, Integer rc5Command, ButtonFunctionType rc5Type, 
							Integer rc5KNXValue, EIBObj rc5Obj0, EIBObj rc5Obj1, String Comment) {
		this.irFunctionEnabled = irFunctionEnabled;
		this.rc5Address = rc5Address;
		this.rc5Command = rc5Command;
		this.rc5Type = new ButtonFunctionType (rc5Type.getButtonFunction());
		this.rc5KNXValue = rc5KNXValue;
		this.rc5Obj0 = new EIBObj (rc5Obj0.getAddr());
		if (rc5Obj1 != null)
			rc5Obj1 = new EIBObj (rc5Obj1.getAddr());
		this.Comment = Comment;
	}

	
	
	/**
	 * @return the irFunctionEnabled
	 */
	public Boolean getIrFunctionEnabled() {
		return irFunctionEnabled;
	}

	/**
	 * @return the rc5Address
	 */
	public Integer getRc5Address() {
		return rc5Address;
	}

	/**
	 * @param rc5Address the rc5Address to set
	 */
	public void setRc5Address(Integer rc5Address) {
		if ((rc5Address >= 0) && (rc5Address < 32))
			this.rc5Address = rc5Address;
	}

	/**
	 * @return the rc5Command
	 */
	public Integer getRc5Command() {
		return rc5Command;
	}

	/**
	 * @param rc5Command the rc5Command to set
	 */
	public void setRc5Command(Integer rc5Command) {
		if ((rc5Command >= 0) && (rc5Command < 128))
			this.rc5Command = rc5Command;
	}

	/**
	 * @return the rc5Type
	 */
	public ButtonFunctionType getRc5Type() {
		return rc5Type;
	}

	/**
	 * @param rc5Type the rc5Type to set
	 */
	public void setRc5Type(ButtonFunctionType rc5Type) {
		this.rc5Type = rc5Type;
	}

	/**
	 * @return the rc5KNXValue
	 */
	public Integer getRc5KNXValue() {
		return rc5KNXValue;
	}

	/**
	 * @param rc5knxValue the rc5KNXValue to set
	 */
	public void setRc5KNXValue(Integer rc5knxValue) {
		rc5KNXValue = rc5knxValue;
	}

	/**
	 * @return the rc5Obj0
	 */
	public EIBObj getRc5Obj0() {
		return rc5Obj0;
	}

	/**
	 * @param rc5Obj0 the rc5Obj0 to set
	 */
	public void setRc5Obj0(EIBObj rc5Obj0) {
		this.rc5Obj0 = rc5Obj0;
	}

	/**
	 * @return the rc5Obj1
	 */
	public EIBObj getRc5Obj1() {
		return rc5Obj1;
	}

	/**
	 * @param rc5Obj1 the rc5Obj1 to set
	 */
	public void setRc5Obj1(EIBObj rc5Obj1) {
		this.rc5Obj1 = rc5Obj1;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return Comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		Comment = comment;
	}

	/**
	 * @param irFunctionEnabled the irFunctionEnabled to set
	 */
	public void setIrFunctionEnabled(Boolean irFunctionEnabled) {
		this.irFunctionEnabled = irFunctionEnabled;
	}
	
	public void writeToXML(TransformerHandler hd, String elementName) {
		
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("","","FunctionEnabled","CDATA", ""+irFunctionEnabled);
		atts.addAttribute("","","Address","CDATA", ""+rc5Address);
		atts.addAttribute("","","Command","CDATA", ""+rc5Command);
		atts.addAttribute("","","Type","CDATA", ""+rc5Type.getButtonFunction());
		atts.addAttribute("","","Value","CDATA", ""+rc5KNXValue);
		atts.addAttribute("","","ObjAddr0","CDATA", ""+(0+rc5Obj0.getAddr()));
		if (rc5Obj1 != null)
			atts.addAttribute("","","ObjAddr1","CDATA", ""+(0+rc5Obj1.getAddr()));
		atts.addAttribute("","","Comment","CDATA", Comment);

		try {
			hd.startElement("","","RC5Command",atts);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// end of hardware description
		try {
			hd.endElement("","","RC5Command");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void readFromXML(XMLStreamReader parser) {

	Boolean processed;
		
		for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
			processed = false;
			String aName = parser.getAttributeLocalName( i );

			if (aName.equals("FunctionEnabled")) {
				irFunctionEnabled = Boolean.valueOf(parser.getAttributeValue( i ));
				processed = true;
			}
			if (aName.equals("Address")) {
				rc5Address = Integer.valueOf(parser.getAttributeValue( i ));
				processed = true;
			}
			if (aName.equals("Command")) {
				rc5Command = Integer.valueOf(parser.getAttributeValue( i ));
				processed = true;
			}
			if (aName.equals("Type")) {
				rc5Type.setButtonFunction(Integer.valueOf(parser.getAttributeValue( i )));
				processed = true;
			}
			if (aName.equals("Value")) {
				rc5KNXValue = Integer.valueOf(parser.getAttributeValue( i ));
				processed = true;
			}
			if (aName.matches("ObjAddr\\d")) {
				int o = Integer.decode (aName.substring(7));
				char Addr = (char) (Integer.decode (parser.getAttributeValue( i )) &0xFFFF);
				if (o == 0)
					rc5Obj0 = new EIBObj (Addr);
				else 
					rc5Obj1 = new EIBObj (Addr);
				processed = true;
			}
			if (aName.equals("Comment")) {
				Comment = parser.getAttributeValue( i );
				processed = true;
			}

			if (!processed)
				System.err.println ("unprocessed RC5 Element: " + aName);
	    }
	}

	
	public void registerEibAddresses(LcdEibAddresses groupAddr) {

		if (irFunctionEnabled) {
			groupAddr.addAddr(rc5Obj0.getAddr());
			if (rc5Obj1 != null)
				groupAddr.addAddr(rc5Obj1.getAddr());
		}
	}

	protected static byte SIZE_IR_PARAMETERS = 8;
	protected static byte SIZE_OF_IR_OBJECT = (byte) (SIZE_IR_PARAMETERS + 2); // +2 for type and size
	protected static byte IR_OBJECT_TYPE = 2;

	public void outputToLcdFile(String elementName,
			LcdListenerContainer listeners, LcdCyclicContainer cyclics,
			LcdEibAddresses groupAddr) {

		if (!irFunctionEnabled)
			return;
		
		byte[] parameter = new byte [SIZE_IR_PARAMETERS];
		// ID of hardware resource
		Integer h = HardwarePropertiesDialog.getHardwareID (elementName);
		if (h < 0) {
			System.err.println ("Invalid hardware ID for "+elementName);
			return;
		}
		// hardware ID
		parameter [0] = (byte) (h & 0xff);
		// rc5 address
		parameter [1] = (byte) (rc5Address & 0xff);
		// rc5 command
		parameter [2] = (byte) (rc5Command & 0xff);
		// KNX function
		parameter [3] = (byte) (rc5Type.getButtonFunction() & 0xff);
		// EIB Objects
		parameter [4] = (byte) (groupAddr.getAddrIndex(rc5Obj0.getAddr()) & 0xff);
		parameter [5] = (byte) 0;
		if (rc5Obj1 != null)
			parameter [5] = (byte) (groupAddr.getAddrIndex(rc5Obj1.getAddr()) & 0xff);
		else 
			parameter [5] = (byte) (groupAddr.getAddrIndex(rc5Obj0.getAddr()) & 0xff);
		// KNX Value
		parameter [6] = (byte) (rc5KNXValue & 0xff);
		parameter [7] = (byte) ((rc5KNXValue >> 8) & 0xff);
		
		cyclics.addElement(SIZE_OF_IR_OBJECT, IR_OBJECT_TYPE, parameter);
	}
}
