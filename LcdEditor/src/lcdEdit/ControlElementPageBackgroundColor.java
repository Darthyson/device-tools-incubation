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

import java.awt.Color;
import java.awt.Component;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class ControlElementPageBackgroundColor extends EIBComp  {

	int myRGB;
	
	public ControlElementPageBackgroundColor(int rgb) {
		myRGB = rgb;
	}

	@Override
	public void writeXML(TransformerHandler hd) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	protected static byte SIZE_BACKGROUND_PARAMETERS = 3;
	protected static byte SIZE_OF_BACKGROUND_OBJECT = (byte) (SIZE_BACKGROUND_PARAMETERS + 2);
	protected static byte BACKGROUND_OBJECT_TYPE = 4;

	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
			LcdPageContainer pageContainer, LcdEibAddresses eibAddresses, 
			LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
			DisplayProperties dor, LcdListenerContainer listener, LcdTimeoutContainer timeout, int myPage) {
		// add image to image container
		byte[] parameter = new byte [SIZE_BACKGROUND_PARAMETERS];
		// color information
		parameter [2] = (byte) ((myRGB >> 16) & 0xff);
		parameter [1] = (byte) ((myRGB >> 8) & 0xff);
		parameter [0] = (byte) ((myRGB >> 0) & 0xff);
		// store element to current page
		pageContainer.addElement(SIZE_OF_BACKGROUND_OBJECT, BACKGROUND_OBJECT_TYPE, parameter);
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selectState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EditorComponent getClone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerEibAddresses(LcdEibAddresses groupAddr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePageName(String oldName, String newName) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isPictureInUse(int id) {
		
		return false;
	}

	@Override
	public boolean isSoundInUse(int id) {

		return false;
	}

	@Override
	public boolean isPageNameUsed(String name) {
		return false;
	}

	@Override
	public void fillEditor() {
				
	}


}
