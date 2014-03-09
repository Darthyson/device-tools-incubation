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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class HardwareFunctionType {

	final static String  backLightFunctionTypes[] = {"n.a." };
	private String functionType;
	final static String functionTypes[] = { "none", "output", "input", "DS18S20", "DS18B20", "DHTxx" };
	final static String PE1functionTypes[] = { "none", "output", "input", "DS18S20", "DS18B20", "IR", "DHTxx" };
	
	// Common
	EIBObj groupAddress;
	// for input and DS18S20
	final static String repeatTimes[] = { "1s", "2s", "5s", "10s", "30s", "60s" };
	Integer repeatFrequency;
	
	// output
	final static String outputFunctionTypes[] = { "1=high/0=low", "1=low/0=high", "1=high/0=flash", "1=low/0=flash", "1=flash/0=low", "1=flash/0=high" };
	Integer outputFunction;
	final static String flashTimes[] = { "0.5Hz",  "1Hz", "2Hz", "4Hz" };
	Integer outputFrequency;
	
	// input
	final static String inputFunctionTypes[] = { "high=toggle", "low=toggle", "high&low=toggle", "high=1/low=0", "low=1/high=0", "high=1", "low=1", "high=0", "low=0"};
	final static String inputRepetitionTypes[] = { "none", "repeat 0", "repeat 1", "repeat 0&1"};
	Integer inputFunction;
	Integer inputRepetitionMode;
	
	// DS18S20
	final static String numberFormatTypes[] = { "EIS 5" };
	Integer numberFormat;
	Float temperatureOffset;

	// backlight
	final static String backlightNumberFormatTypes[] = { "EIS 6" };
	public static final String dhtSensorTypes[] = { "DHT11", "DHT21", "DHT22" };
	private Boolean busControlEnabled;
	Integer backlightNumberFormat;
	Integer defaultBacklight;
	
	// IR
	private RC5CommandList rc5Commands;
	
	// DHTxx
	EIBObj groupAddress2;
	Float humidityOffset;
	Integer dhtSensorType;
	
	public HardwareFunctionType(String functionType) {
		this.functionType = functionType;
		
		groupAddress = new EIBObj ((char) 0);
		groupAddress2 = new EIBObj ((char) 0);
		busControlEnabled = false;
		outputFunction = 0;
		outputFrequency = 2;
		repeatFrequency = 3;
		inputFunction = 0;
		inputRepetitionMode = 0;
		numberFormat = 0;
		temperatureOffset = (float) 0;
		humidityOffset = (float) 0;
		dhtSensorType = 0;
		backlightNumberFormat = 0;
		defaultBacklight = 50;
		rc5Commands = new RC5CommandList ();
	}
	
	public void setDefaultBacklight (int defaultBacklight) {
		this.defaultBacklight = defaultBacklight;
	}
	
	public String getFunctionParameters () {
		
		if (functionType.contentEquals("none")) {
			return "no parameters";
		}
		if (functionType.contentEquals("output")) {
			if (outputFunctionTypes [outputFunction].contains ("flash"))
				return AddrTranslator.getAdrString (groupAddress) + " " + outputFunctionTypes [outputFunction] + " " + flashTimes [outputFrequency];
			else 				
				return AddrTranslator.getAdrString (groupAddress) + " " + outputFunctionTypes [outputFunction];
		}
		if (functionType.contentEquals("input")) {
			if (!inputRepetitionTypes [inputRepetitionMode].contains ("none"))
				return AddrTranslator.getAdrString (groupAddress) + " " + inputFunctionTypes [inputFunction] + " " + inputRepetitionTypes [inputRepetitionMode] + " " + repeatTimes [repeatFrequency];
			else 				
				return AddrTranslator.getAdrString (groupAddress) + " " + inputFunctionTypes [inputFunction];
		}
		if (functionType.contentEquals("DS18S20")) {

			return AddrTranslator.getAdrString (groupAddress) + " " + numberFormatTypes [numberFormat] + " " + temperatureOffset + " " + repeatTimes [repeatFrequency];

		}
		if (functionType.contentEquals("DS18B20")) {

			return AddrTranslator.getAdrString (groupAddress) + " " + numberFormatTypes [numberFormat] + " " + temperatureOffset + " " + repeatTimes [repeatFrequency];

		}
		if (functionType.contentEquals("IR")) {
			return rc5Commands.getEnabledCount() + " IR functions";
		}
		if (functionType.contentEquals("DHTxx")) {

			return dhtSensorTypes [dhtSensorType] + " " + AddrTranslator.getAdrString (groupAddress) + " " + AddrTranslator.getAdrString (groupAddress2) + " " + numberFormatTypes [numberFormat] + " " + temperatureOffset + " "+ humidityOffset + " "  + repeatTimes [repeatFrequency];

		}
		if (functionType.contentEquals("n.a.")) {
			if (busControlEnabled)
				return AddrTranslator.getAdrString (groupAddress) + " " + backlightNumberFormatTypes [backlightNumberFormat] + " " + defaultBacklight+"%";
			else return "" + defaultBacklight+"%";
		}
		
		return "error";
	}

	public boolean hasParameters () {

		if (functionType.contentEquals("none")) {
			return false;
		}
		return true;
	}
	
	
	public String getFunctionType () {
		return functionType;
	}

	
	@Override
	public String toString () {
		return functionType;
	}

	public void showEditDialog(Frame parentFrame) {
		// open editor dialog for my properties
		if (functionType.contentEquals("output")) {
			// open editor dialog for output function
			OutputEditorDialog eDiag = new OutputEditorDialog (parentFrame);
			eDiag.setLocation (260, 180);
			// set output parameters to dialog
			eDiag.setAddress (groupAddress);
			eDiag.setOutputFunction (outputFunction);
			eDiag.setOutputFrequency (outputFrequency);
			
			// show dialog
			eDiag.setVisible(true);
			//get output parameters from dialog
			
			if (eDiag.getModalResult()) {
				groupAddress = eDiag.getAddress ();
				outputFunction = eDiag.getOutputFunction ();
				outputFrequency = eDiag.getOutputFrequency ();
			}
			//destroy dialog			
			eDiag.dispose();			
		}
		if (functionType.contentEquals("input")) {
			// open editor dialog for input function
			InputEditorDialog iDiag = new InputEditorDialog (parentFrame);
			iDiag.setLocation (260, 180);
			// set output parameters to dialog
			iDiag.setAddress (groupAddress);
			iDiag.setInputFunction (inputFunction);
			iDiag.setInputRepetition (inputRepetitionMode);
			iDiag.setRepetitionRate (repeatFrequency);
			
			// show dialog
			iDiag.setVisible(true);
			//get output parameters from dialog
			
			if (iDiag.getModalResult()) {
				groupAddress = iDiag.getAddress ();
				inputFunction = iDiag.getInputFunction ();
				inputRepetitionMode = iDiag.getInputRepetition();
				repeatFrequency = iDiag.getRepetitionRate ();
			}
			//destroy dialog			
			iDiag.dispose();			
		}
		if (functionType.contentEquals("DS18S20")) {
			// open editor dialog for temperature sensor DS18S20
			DS18x20EditorDialog dDiag = new DS18x20EditorDialog (parentFrame, "DS18S20 Options");
			dDiag.setLocation (260, 180);
			// set output parameters to dialog
			dDiag.setAddress (groupAddress);
			dDiag.setFormat (numberFormat);
			dDiag.setRepetitionRate (repeatFrequency);
			dDiag.setTemperatureOffset(temperatureOffset);
			
			// show dialog
			dDiag.setVisible(true);
			//get output parameters from dialog
			
			if (dDiag.getDialogResult()) {
				groupAddress = dDiag.getAddress ();
				numberFormat = dDiag.getFormat ();
				repeatFrequency = dDiag.getRepetitionRate ();
				temperatureOffset = dDiag.getTemperatureOffset();
			}
			//destroy dialog			
			dDiag.dispose();			
		}
		if (functionType.contentEquals("DS18B20")) {
			// open editor dialog for temperature sensor DS18B20
			DS18x20EditorDialog dDiag = new DS18x20EditorDialog (parentFrame, "DS18B20 Options");
			dDiag.setLocation (260, 180);
			// set output parameters to dialog
			dDiag.setAddress (groupAddress);
			dDiag.setFormat (numberFormat);
			dDiag.setRepetitionRate (repeatFrequency);
			dDiag.setTemperatureOffset(temperatureOffset);
			
			// show dialog
			dDiag.setVisible(true);
			//get output parameters from dialog
			
			if (dDiag.getDialogResult()) {
				groupAddress = dDiag.getAddress ();
				numberFormat = dDiag.getFormat ();
				repeatFrequency = dDiag.getRepetitionRate ();
				temperatureOffset = dDiag.getTemperatureOffset();
			}
			//destroy dialog			
			dDiag.dispose();			
		}
		if (functionType.contentEquals("IR")) {
			// open editor dialog for IR sensor (RC5 code)
			IREditorDialog irDiag = new IREditorDialog (parentFrame);
			irDiag.setLocation (260, 180);
			// set output parameters to dialog
			irDiag.setRc5Commands (rc5Commands);
			// show dialog
			irDiag.setVisible(true);
			//get output parameters from dialog
			if (irDiag.getDialogResult()) {
				rc5Commands = irDiag.getRc5Commands ();
			}
			//destroy dialog			
			irDiag.dispose();			
		}
		if (functionType.contentEquals("DHTxx")) {
			// open editor dialog for temperature sensor DHT11/22/21
			DHTxxEditorDialog dDiag = new DHTxxEditorDialog (parentFrame, "DHTxx Options");
			dDiag.setLocation (260, 180);
			// set output parameters to dialog
			dDiag.setAddress (groupAddress);
			dDiag.setAddress2 (groupAddress2);
			dDiag.setFormat (numberFormat);
			dDiag.setRepetitionRate (repeatFrequency);
			dDiag.setTemperatureOffset(temperatureOffset);
			dDiag.setHumidityOffset(humidityOffset);
			dDiag.setDhtSensorType (dhtSensorType);
			
			// show dialog
			dDiag.setVisible(true);
			//get output parameters from dialog
			
			if (dDiag.getDialogResult()) {
				groupAddress = dDiag.getAddress ();
				groupAddress2 = dDiag.getAddress2 ();
				numberFormat = dDiag.getFormat ();
				repeatFrequency = dDiag.getRepetitionRate ();
				temperatureOffset = dDiag.getTemperatureOffset();
				humidityOffset = dDiag.getHumidityOffset();
				dhtSensorType = dDiag.getDhtSensorType ();
			}
			//destroy dialog			
			dDiag.dispose();			
		}
		if (functionType.contentEquals("n.a.")) {
			// open editor dialog for backlight control
			BacklightEditorDialog bDiag = new BacklightEditorDialog (parentFrame);
			bDiag.setLocation (260, 180);
			// set output parameters to dialog
			bDiag.setAddress (groupAddress, busControlEnabled);
			bDiag.setFormat (backlightNumberFormat);
			bDiag.setDefault (defaultBacklight);
			
			// show dialog
			bDiag.setVisible(true);
			//get output parameters from dialog
			
			if (bDiag.getModalResult()) {
				groupAddress = bDiag.getAddress ();
				backlightNumberFormat = bDiag.getFormat ();
				busControlEnabled = bDiag.getBusControlEnabled();
				defaultBacklight = bDiag.getDefault();
			}
			//destroy dialog			
			bDiag.dispose();			
		}
		
	}

	public void writeToXML(TransformerHandler hd, String elementName) {

		if (functionType.contentEquals("none")) {
			return;
		}
		if (functionType.contentEquals("IR")) {
			rc5Commands.writeToXML(hd, elementName);
			return;
		}

		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("","","Name","CDATA", elementName);
		atts.addAttribute("","","FunctionType","CDATA", functionType);

		if (functionType.contentEquals("output")) {

			atts.addAttribute("","","Address","CDATA",AddrTranslator.getAdrString (groupAddress));
			atts.addAttribute("","","OutputFunction","CDATA",outputFunctionTypes[outputFunction]);
			if (outputFunctionTypes [outputFunction].contains ("flash"))
				atts.addAttribute("","","Frequency","CDATA",flashTimes[outputFrequency]);
		}
		if (functionType.contentEquals("input")) {

			atts.addAttribute("","","Address","CDATA",AddrTranslator.getAdrString (groupAddress));
			atts.addAttribute("","","InputFunction","CDATA",inputFunctionTypes[inputFunction]);
			atts.addAttribute("","","InputRepetitionMode","CDATA",inputRepetitionTypes[inputRepetitionMode]);
			if (!inputRepetitionTypes [inputRepetitionMode].contains ("none")) {
				atts.addAttribute("","","Repeat","CDATA",repeatTimes[repeatFrequency]);
			}
		}
		if (functionType.contentEquals("DS18S20")) {
			atts.addAttribute("","","Address","CDATA",AddrTranslator.getAdrString (groupAddress));
			atts.addAttribute("","","TempFormat","CDATA",numberFormatTypes[numberFormat]);
			atts.addAttribute("","","Repeat","CDATA",repeatTimes[repeatFrequency]);
			atts.addAttribute("","","Offset","CDATA",""+temperatureOffset);
		}
		if (functionType.contentEquals("DS18B20")) {
			atts.addAttribute("","","Address","CDATA",AddrTranslator.getAdrString (groupAddress));
			atts.addAttribute("","","TempFormat","CDATA",numberFormatTypes[numberFormat]);
			atts.addAttribute("","","Repeat","CDATA",repeatTimes[repeatFrequency]);
			atts.addAttribute("","","Offset","CDATA",""+temperatureOffset);
		}
		if (functionType.contentEquals("DHTxx")) {
			atts.addAttribute("","","Address","CDATA",AddrTranslator.getAdrString (groupAddress));
			atts.addAttribute("","","Address2","CDATA",AddrTranslator.getAdrString (groupAddress2));
			atts.addAttribute("","","TempFormat","CDATA",numberFormatTypes[numberFormat]);
			atts.addAttribute("","","Repeat","CDATA",repeatTimes[repeatFrequency]);
			atts.addAttribute("","","Offset","CDATA",""+temperatureOffset);
			atts.addAttribute("","","HOffset","CDATA",""+humidityOffset);
			atts.addAttribute("","","SensorType", "CDATA", ""+dhtSensorType);
		}
		if (functionType.contentEquals("n.a.")) {
			atts.addAttribute("","","DefaultDimming","CDATA", ""+defaultBacklight);
			atts.addAttribute("","","BusControl","CDATA", ""+busControlEnabled);
			atts.addAttribute("","","Address","CDATA",AddrTranslator.getAdrString (groupAddress));
			atts.addAttribute("","","BacklightFormat","CDATA",backlightNumberFormatTypes[backlightNumberFormat]);
		}
		
		try {
			hd.startElement("","","HWResource",atts);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// end of hardware description
		try {
			hd.endElement("","","HWResource");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getArrayIndex (String f, String[] sa) {
		for (int i = 0; i < sa.length; i++) {
			if (sa[i].equals(f))
				return i;
		}
		return 0;
	}
	
	public void readFromXML (XMLStreamReader parser, String function) throws XMLStreamException {
			
		if (function.equals("IR")) {
			rc5Commands.readFromXML (parser);
			return;
		}
		
		for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
			boolean processed = false;

			if (parser.getAttributeLocalName( i ) == "DefaultDimming") {
				defaultBacklight = new Integer((String)parser.getAttributeValue( i )).intValue();
				if ((defaultBacklight > 100) || (defaultBacklight < 0))
					defaultBacklight = 50;
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "BusControl") {
				busControlEnabled = parser.getAttributeValue( i ).equals("true");
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "Address") {
				groupAddress.setAddr(AddrTranslator.getAdrValue(parser.getAttributeValue( i )));
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "Address2") {
				groupAddress2.setAddr(AddrTranslator.getAdrValue(parser.getAttributeValue( i )));
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "Frequency" ) {
				outputFrequency = getArrayIndex (parser.getAttributeValue( i ), flashTimes);
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "OutputFunction" ) {
				outputFunction = getArrayIndex (parser.getAttributeValue( i ), outputFunctionTypes);
				processed = true;
			}

			if (parser.getAttributeLocalName( i ) == "InputFunction" ) {
				inputFunction = getArrayIndex (parser.getAttributeValue( i ), inputFunctionTypes);
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "InputRepetitionMode" ) {
				inputRepetitionMode = getArrayIndex (parser.getAttributeValue( i ), inputRepetitionTypes);
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "Repeat" ) {
				repeatFrequency = getArrayIndex (parser.getAttributeValue( i ), repeatTimes);
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "TempFormat" ) {
				numberFormat = getArrayIndex (parser.getAttributeValue( i ), numberFormatTypes);
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "Offset" ) {
				temperatureOffset = Float.valueOf(parser.getAttributeValue( i ));
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "HOffset" ) {
				humidityOffset = Float.valueOf(parser.getAttributeValue( i ));
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "SensorType" ) {
				dhtSensorType = Integer.valueOf(parser.getAttributeValue( i ));
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "BacklightFormat" ) {
				backlightNumberFormat = getArrayIndex (parser.getAttributeValue( i ), backlightNumberFormatTypes);
				processed = true;
			}
			
			if ((!processed) && ( parser.getAttributeLocalName( i ) != "Name" ) && ( parser.getAttributeLocalName( i ) != "FunctionType" ) ) 
				System.err.println ("Unprocessed token: " + parser.getAttributeLocalName( i ) + " = " + parser.getAttributeValue( i ));
		}
	}
	
	private Integer getFlashCode () {
		return outputFrequency;
	}
	
	private final Integer OUT_LOW = 0x00;
	private final Integer OUT_HIGH = 0x01;
	private final Integer OUT_FLASH = 0x02;
	
	private Integer getStateCode () {
		//"1=high/0=low", "1=low/0=high", "1=high/0=flash", "1=low/0=flash", "1=flash/0=low", "1=flash/0=high"
		
		if (outputFunction == 0)
			return (OUT_HIGH << 2) | (OUT_LOW << 0);
		if (outputFunction == 1)
			return (OUT_LOW << 2) | (OUT_HIGH << 0);
		if (outputFunction == 2)
			return (OUT_HIGH << 2) | (OUT_FLASH << 0);
		if (outputFunction == 3)
			return (OUT_LOW << 2) | (OUT_FLASH << 0);
		if (outputFunction == 4)
			return (OUT_FLASH << 2) | (OUT_LOW << 0);
		if (outputFunction == 5)
			return (OUT_FLASH << 2) | (OUT_HIGH << 0);
		// error, should not happen
		return 0;
	}

	private Integer getRepetitionInterval () {
		// "1s", "2s", "5s", "10s", "30s", "60s"
		if (repeatFrequency == 0)
			return 1;
		if (repeatFrequency == 1)
			return 2;
		if (repeatFrequency == 2)
			return 5;
		if (repeatFrequency == 3)
			return 10;
		if (repeatFrequency == 4)
			return 30;
		if (repeatFrequency == 5)
			return 60;
		// error, should not happen
		return 0;
	}
	
	private final Integer IN_NOTHING = 0x00; // do not send anything
	private final Integer IN_TOGGLE  = 0x01; // toggle object value
	private final Integer IN_SEND_0  = 0x02; // send 0 on external edge
	private final Integer IN_SEND_1  = 0x03; // send 1 on external edge
	private final Integer IN_SEND_0R = 0x06; // send 0 and repeat
	private final Integer IN_SEND_1R = 0x07; // send 1 and repeat
	
	// d5-d3: rising edge /high
	// d2-d0: fallig edge /low
	
	private Integer getInputStateCode () {
		// inputFunctionTypes[] = { "high=toggle", "low=toggle", "high&low=toggle", "high=1/low=0", "low=1/high=0", "high=1", "low=1", "high=0", "low=0"};
		// inputRepetitionTypes[] = { "none", "repeat 0", "repeat 1", "repeat 0&1"};
		
		int send_0; // on input low
		int send_1; // on input high
		
		if ((inputRepetitionMode == 1) || (inputRepetitionMode == 3))
			send_0 = IN_SEND_0R;
		else send_0 = IN_SEND_0;
		if ((inputRepetitionMode == 2) || (inputRepetitionMode == 3))
			send_1 = IN_SEND_1R;
		else send_1 = IN_SEND_1;
		
		if (inputFunction == 0)
			return ( IN_TOGGLE << 3 )	|  IN_NOTHING;
		if (inputFunction == 1)
			return ( IN_NOTHING << 3 )	|  IN_TOGGLE;
		if (inputFunction == 2)
			return ( IN_TOGGLE << 3 )	|  IN_TOGGLE;
		if (inputFunction == 3)
			return ( send_1 << 3 )	|  send_0;
		if (inputFunction == 4)
			return ( send_0 << 3 )	|  send_1;
		if (inputFunction == 5)
			return ( send_1 << 3 )	|  IN_NOTHING;
		if (inputFunction == 6)
			return ( IN_NOTHING << 3 )	|  send_1;
		if (inputFunction == 7)
			return ( send_0 << 3 )	|  IN_NOTHING;
		if (inputFunction == 8)
			return ( IN_NOTHING << 3 )	|  send_0;
		// error, should not happen
		return 0;
	}
	
	protected static byte SIZE_BACKLIGHT_PARAMETERS = 3;
	protected static byte SIZE_OF_BACKLIGHT_OBJECT = (byte) (SIZE_BACKLIGHT_PARAMETERS + 2); // +2 for type and size
	protected static byte BACKLIGHT_IDLE_OBJECT_TYPE = 0;
	protected static byte BACKLIGHT_ACTIVE_OBJECT_TYPE = 1;
	protected static byte BACKLIGHT_FLAG_CONTROL_ENABLE = 0x01;
	
	protected static byte SIZE_OUTPUT_PARAMETERS = 3;
	protected static byte SIZE_OF_OUTPUT_OBJECT = (byte) (SIZE_OUTPUT_PARAMETERS + 2); // +2 for type and size
	protected static byte OUTPUT_OBJECT_TYPE = 2;

	protected static byte SIZE_INPUT_PARAMETERS = 6;
	protected static byte SIZE_OF_INPUT_OBJECT = (byte) (SIZE_INPUT_PARAMETERS + 2); // +2 for type and size
	protected static byte INPUT_OBJECT_TYPE = 0;
	
	protected static byte SIZE_DS18x20_PARAMETERS = 5;
	protected static byte SIZE_OF_DS18x20_OBJECT = (byte) (SIZE_DS18x20_PARAMETERS + 2); // +2 for type and size
	protected static byte DS18S20_OBJECT_TYPE = 1;
	protected static byte DS18B20_OBJECT_TYPE = 3;

	protected static byte SIZE_DHTXX_PARAMETERS = 8;
	protected static byte SIZE_OF_DHTXX_OBJECT = (byte) (SIZE_DHTXX_PARAMETERS + 2); // +2 for type and size
	protected static byte DHTXX_OBJECT_TYPE = 4;

	public void outputToLcdFile(String elementName,
			LcdListenerContainer listeners, LcdCyclicContainer cyclics, LcdEibAddresses groupAddr) {
		// output my data to the LCD binary file
		
		// backlight active element
		if (elementName.contains ("active")) {
			byte[] parameter = new byte [SIZE_BACKLIGHT_PARAMETERS];
			// default backlight
			parameter [0] = (byte) (((defaultBacklight * 255)/100) & 0xff);
			// EIB Object # for listen
			parameter [1] = (byte) (groupAddr.getAddrIndex(groupAddress.getAddr()) & 0xff);
			// flag for bus control enable
			parameter [2] = (byte) 0x00;
			if (busControlEnabled) {
				parameter [2] = (byte) BACKLIGHT_FLAG_CONTROL_ENABLE;
			}
			listeners.addElement(SIZE_OF_BACKLIGHT_OBJECT, BACKLIGHT_ACTIVE_OBJECT_TYPE, parameter);
		}
		
		// backlight idle element
		if (elementName.contains ("idle")) {
			byte[] parameter = new byte [SIZE_BACKLIGHT_PARAMETERS];
			// default backlight
			parameter [0] = (byte) (((defaultBacklight * 255)/100) & 0xff);
			// EIB Object # for listen
			parameter [1] = (byte) (groupAddr.getAddrIndex(groupAddress.getAddr()) & 0xff);
			// flag for bus control enable
			parameter [2] = (byte) 0x00;
			if (busControlEnabled) {
				parameter [2] = (byte) BACKLIGHT_FLAG_CONTROL_ENABLE;
			}
			listeners.addElement(SIZE_OF_BACKLIGHT_OBJECT, BACKLIGHT_IDLE_OBJECT_TYPE, parameter);
		}
		
		// output element
		if (functionType.contains ("output")) {
			byte[] parameter = new byte [SIZE_OUTPUT_PARAMETERS];
			// ID of hardware resource
			Integer h = HardwarePropertiesDialog.getHardwareID (elementName);
			if (h < 0) {
				System.err.println ("Invalid hardware ID for "+elementName);
				return;
			}
			// EIB Object # for listen
			parameter [1] = (byte) (groupAddr.getAddrIndex(groupAddress.getAddr()) & 0xff);
			// hardware ID
			parameter [0] = (byte) (h & 0xff);
			// flag for output function: d5,d4 = Flash frequency, d3,d2=state@1, d1,d0=state@0
			byte f = (byte) ((getFlashCode() & 0x03) << 4);
			byte s = (byte) (getStateCode() & 0x0f);
			parameter [2] = (byte) (f | s);
			listeners.addElement(SIZE_OF_OUTPUT_OBJECT, OUTPUT_OBJECT_TYPE, parameter);
		}
		// input element
		if (functionType.contains ("input")) {
			byte[] parameter = new byte [SIZE_INPUT_PARAMETERS];
			// ID of hardware resource
			Integer h = HardwarePropertiesDialog.getHardwareID (elementName);
			if (h < 0) {
				System.err.println ("Invalid hardware ID for "+elementName);
				return;
			}
			// EIB Object # for listen
			parameter [1] = (byte) (groupAddr.getAddrIndex(groupAddress.getAddr()) & 0xff);
			// hardware ID
			parameter [0] = (byte) (h & 0xff);
			// flag for input function: d5-d3 = input high, d2-d0 = input low
			parameter [2] = (byte) (getInputStateCode() & 0x3f);
			parameter [3] = (byte) (getRepetitionInterval () & 0xff);
			// space for object to store its current state
			parameter [4] = (byte) 0x00;
			parameter [5] = (byte) 0x00;
			cyclics.addElement(SIZE_OF_INPUT_OBJECT, INPUT_OBJECT_TYPE, parameter);
		}
		// input element
		if (functionType.contains ("IR")) {
			rc5Commands.outputToLcdFile(elementName, listeners, cyclics, groupAddr);
		}
		// DS18S20 temperature sensor element
		if (functionType.contains ("DS18S20")) {
			byte[] parameter = new byte [SIZE_DS18x20_PARAMETERS];
			// ID of hardware resource
			Integer h = HardwarePropertiesDialog.getHardwareID (elementName);
			if (h < 0) {
				System.err.println ("Invalid hardware ID for "+elementName);
				return;
			}
			// EIB Object # for sending temperature values
			parameter [1] = (byte) (groupAddr.getAddrIndex(groupAddress.getAddr()) & 0xff);
			// hardware ID
			parameter [0] = (byte) (h & 0xff);
			// time interval between two measurements
			parameter [2] = (byte) (repeatFrequency & 0x3f);
			// EIS data format
			parameter [3] = (byte) (numberFormat & 0xff);
			// EIS data format
			parameter [4] = (byte) ( ( Math.round (temperatureOffset*10)) & 0xff );
			cyclics.addElement(SIZE_OF_DS18x20_OBJECT, DS18S20_OBJECT_TYPE, parameter);
		}
		// DS18B20 temperature sensor element
		if (functionType.contains ("DS18B20")) {
			byte[] parameter = new byte [SIZE_DS18x20_PARAMETERS];
			// ID of hardware resource
			Integer h = HardwarePropertiesDialog.getHardwareID (elementName);
			if (h < 0) {
				System.err.println ("Invalid hardware ID for "+elementName);
				return;
			}
			// EIB Object # for sending temperature values
			parameter [1] = (byte) (groupAddr.getAddrIndex(groupAddress.getAddr()) & 0xff);
			// hardware ID
			parameter [0] = (byte) (h & 0xff);
			// time interval between two measurements
			parameter [2] = (byte) (repeatFrequency & 0x3f);
			// EIS data format
			parameter [3] = (byte) (numberFormat & 0xff);
			// EIS data format
			parameter [4] = (byte) ( ( Math.round (temperatureOffset*10)) & 0xff );
			cyclics.addElement(SIZE_OF_DS18x20_OBJECT, DS18B20_OBJECT_TYPE, parameter);
		}
		// DHT11/22/21 climate sensor element
		if (functionType.contains ("DHTxx")) {
			byte[] parameter = new byte [SIZE_DHTXX_PARAMETERS];
			// ID of hardware resource
			Integer h = HardwarePropertiesDialog.getHardwareID (elementName);
			if (h < 0) {
				System.err.println ("Invalid hardware ID for "+elementName);
				return;
			}
			// EIB Object # for sending temperature values
			parameter [1] = (byte) (groupAddr.getAddrIndex(groupAddress.getAddr()) & 0xff);
			// EIB Object # for sending humidity values
			parameter [2] = (byte) (groupAddr.getAddrIndex(groupAddress2.getAddr()) & 0xff);
			// hardware ID
			parameter [0] = (byte) (h & 0xff);
			// time interval between two measurements
			parameter [3] = (byte) (repeatFrequency & 0x3f);
			// EIS data format
			parameter [4] = (byte) (numberFormat & 0xff);
			// temperature offset
			parameter [5] = (byte) ( ( Math.round (temperatureOffset*10)) & 0xff );
			// humidity offset
			parameter [6] = (byte) ( ( Math.round (humidityOffset*10)) & 0xff );
			// sensor type
			parameter [7] = (byte) (dhtSensorType & 0xff);
			cyclics.addElement(SIZE_OF_DHTXX_OBJECT, DHTXX_OBJECT_TYPE, parameter);
		}

	}

	public void registerEibAddresses(LcdEibAddresses groupAddr) {
		
		if (functionType.contentEquals("n.a.") && (busControlEnabled) ||  functionType.contains ("output") ||
							functionType.contains ("input") || functionType.contains ("DS18S20") || 
							functionType.contains ("DS18B20") || functionType.contains ("DHTxx")) {
			groupAddr.addAddr(groupAddress.getAddr());
		}
		if (functionType.contains("DHTxx") ) {
			groupAddr.addAddr(groupAddress2.getAddr());
		}
		if (functionType.contains ("IR")) {
			rc5Commands.registerEibAddresses (groupAddr);
		}
		
	}

}
