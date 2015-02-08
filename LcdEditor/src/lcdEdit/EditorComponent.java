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
import java.awt.image.BufferedImage;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;

public interface EditorComponent {
	public void writeXML(TransformerHandler hd) throws SAXException ;
	
	public void outputToLcdFile (LcdImageContainer imageContainer, LcdPageContainer pageContainer, 
									LcdEibAddresses eibAddresses, LcdSoundContainer soundContainer,
									Component[] backgroundComp, Color backgroundColor,
									DisplayProperties dor, LcdListenerContainer listeners, 
									LcdTimeoutContainer timeout, int myPage);
	
	public boolean isSelected ();
	
	public void setSelected (boolean selectState);

	public EditorComponent getClone();

	public void registerEibAddresses(LcdEibAddresses groupAddr);
	
	public int getXPos ();
	public void setXPos (int x);
	public int getYPos ();
	public void setYPos (int x);

	public int getWidth();

	public int getHeight();
	
	public BufferedImage getElementBackground (Component[] backgroundComp, Color pageBackgroundColor);

	public void changePageName(String oldName, String newName);

	public boolean isPictureInUse(int id);

	public boolean isSoundInUse(int id);

	public boolean isPageNameUsed(String name);
	
	public void fillEditor ();
	
}
