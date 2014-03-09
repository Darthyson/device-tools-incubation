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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@SuppressWarnings("serial")
public class ControlElementValue extends EIBComp implements 
							MouseMotionListener, MouseListener  {

    private Point lastPos = new Point (0,0);
	PropertiesTableModel inspectorModel = null;
	private boolean selected;
	protected Color cBackGround;
	private Color fontColor;
	private Font elementFont;
	private String postFixUnit;
	private	int TextX;
	private int TextY;
	private boolean opac;
	private static final int DEFAULT_TEXT_SIZE = 14;
	private ValueFormatType valueFormat;
	private int integers;
	private int decimals;

	public ControlElementValue(LcdEditor server, 
				int x, int y, int w, int h, char sAddr0,
				Font elementFont, Color fontColor, Color cBackGround, int TextX, int TextY, 
				String postFixUnit, boolean opac, int valueFormatSelection, int integers, int decimals) {

		super (server, x, y, w, h, "");
		eibObj[0] = new EIBObj (sAddr0);
		this.cBackGround = cBackGround;
		this.TextX = TextX;	TextY = this.TextY = TextY;
		this.fontColor = fontColor;
		this.elementFont = new Font (elementFont.getFamily(), elementFont.getStyle(), elementFont.getSize());
		this.postFixUnit = postFixUnit;
		this.opac = opac;
		this.valueFormat = new ValueFormatType (valueFormatSelection);
		this.integers = integers;
		this.decimals = decimals;
		setUpElement ();
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public ControlElementValue(LcdEditor server, 
			int x, int y, int w, int h, char sAddr0) {

		super (server, x, y, w, h, "");
		eibObj[0] = new EIBObj (sAddr0);
		cBackGround = Color.white;
		TextX = (int) getWidth()/10;	TextY = (int) (0.6*getHeight ());
		this.fontColor = Color.black;
		this.elementFont = new Font ("Arial", Font.BOLD, 16);
		postFixUnit = "";
		opac = true;
		this.valueFormat = new ValueFormatType (0);
		integers = 2;
		decimals = 1;
		setUpElement ();
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	
    @Override
	public void setUpElement() {
		addMouseListener(this);
		setOpaque(opac);
        selected = false;
	}

	protected void handleAttribute (XMLStreamReader parser, int i){
		// handler for derived objects
		if (parser.getAttributeLocalName( i ) == "BGColor" ) {
			cBackGround = setColorKeepAlpha (cBackGround, Color.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "BGAlpha" ) {
			cBackGround = setAlphaKeepColor (cBackGround, Integer.decode(parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "Unit" ) {
			postFixUnit = parser.getAttributeValue( i );
			return;
		}
		if (parser.getAttributeLocalName( i ) == "FontColor" ) {
			fontColor = setColorKeepAlpha (fontColor, Color.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "FontAlpha" ) {
			fontColor = setAlphaKeepColor (fontColor, Integer.decode(parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "Font" ) {
			elementFont = Font.decode(parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "TextX" ) {
			TextX = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "TextY" ) {
			TextY = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "Opac" ) {
			opac = true;
			return;
		}
		if (parser.getAttributeLocalName( i ) == "ValueFormat" ) {
			valueFormat = new ValueFormatType (Integer.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "Integers" ) {
			integers = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "Decimals" ) {
			decimals = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		super.handleAttribute (parser, i);
	}


	public ControlElementValue(LcdEditor server, XMLStreamReader parser) {
		super (server, parser, null, null);
		if (elementFont == null) elementFont = new Font ("Arial", Font.PLAIN, DEFAULT_TEXT_SIZE);
		if (fontColor == null) fontColor = Color.black;
		if (cBackGround == null) cBackGround = Color.white;
		if (postFixUnit == null) postFixUnit = "";
		if (valueFormat == null) valueFormat = new ValueFormatType (0);
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
	}

	@Override
	public EditorComponent getClone () {
		return new ControlElementValue ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
				eibObj[0].getAddr(), elementFont, fontColor, cBackGround, TextX, TextY, 
				postFixUnit, opac, valueFormat.getValueFormat(), integers, decimals );
	}

	@Override
	public void writeXML(TransformerHandler hd) throws SAXException {
		
		AttributesImpl atts = new AttributesImpl();
		atts.clear();
		atts.addAttribute("","","X","CDATA",""+getX());
		atts.addAttribute("","","Y","CDATA",""+getY());
		atts.addAttribute("","","W","CDATA",""+getWidth());
		atts.addAttribute("","","H","CDATA",""+getHeight());
		atts.addAttribute("","","TextX","CDATA",""+TextX);
		atts.addAttribute("","","TextY","CDATA",""+TextY);
		if (eibObj[0] != null) {
			atts.addAttribute("","","ObjAddr0","CDATA",""+(0+eibObj[0].getAddr()));
			if (eibObj[0].init)
				atts.addAttribute("","","initObj0","CDATA","");
		}
		atts.addAttribute("","","BGColor","CDATA",""+cBackGround.getRGB());
		atts.addAttribute("","","BGAlpha","CDATA",""+cBackGround.getAlpha());
		atts.addAttribute("","","Font","CDATA",elementFont.getFamily()+"-"+
				FontStyleCalc.getFontStyle(elementFont.getStyle())+"-"+elementFont.getSize());
		atts.addAttribute("","","FontColor","CDATA",""+fontColor.getRGB());
		atts.addAttribute("","","FontAlpha","CDATA",""+fontColor.getAlpha());
		atts.addAttribute("","","Unit","CDATA",""+postFixUnit);
		atts.addAttribute("","","ValueFormat","CDATA",""+valueFormat.getValueFormat());
		atts.addAttribute("","","Integers","CDATA",""+integers);
		atts.addAttribute("","","Decimals","CDATA",""+decimals);
		if (opac)
			atts.addAttribute("","","Opac","CDATA","");
		hd.startElement("","","Value",atts);
		hd.endElement("","","Value");
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = (int)(e.getX() - lastPos.getX());
		int dy = (int)(e.getY() - lastPos.getY());
		((LcdEditor)myParent).moveAllSelected (dx, dy);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if ((e.getModifiers() == 16) || (e.getModifiers() == 18)) {
			lastPos = e.getPoint();
			((LcdEditor)myParent).selectElement (this,!e.isControlDown());
		}
		else if (e.getModifiers() == 4) {
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if (e.getModifiers() == 16) {
			fillEditor();
			getParent().invalidate();
		}
		else if (e.getModifiers() == 4) {
			repaint();
		}

	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getModifiers() != 16) return;
		if (e.getClickCount() > 1) {
			String s = (String)JOptionPane.showInputDialog(
	                this,
	                "Address:",
	                "Object Address", JOptionPane.PLAIN_MESSAGE,
	                null,
	                null,
	                AddrTranslator.getAdrString (eibObj[0]));
	
			//If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
				eibObj[0].setAddr( AddrTranslator.getAdrValue (s) );
				setToolTipText( AddrTranslator.getAdrString (eibObj[0]) );
			}
		}
		fillEditor();
	}	

	@Override 
	public String toString () {
		return "Value '" + valueFormat + "' " + AddrTranslator.getAdrString (eibObj[0]);
	}

	@Override
	public void fillEditor (){
		((LcdEditor)myParent).flushInspector();
		// fill up inspector
		inspectorModel = ((LcdEditor)myParent).getInspector ();
		inspectorModel.setEibComp(this);
		// populate the window
    	Object[][] data = { {"font",  elementFont },
							{"width",  getWidth() }, 
							{"height",  getHeight() }, 
							{"x-pos",  getX() }, 
							{"y-pos",  getY() },
							{"value", valueFormat},
							{"integers",  integers }, 
							{"decimals",  decimals }, 
							{"unit",  postFixUnit }, 
							{"text xpos",  TextX }, 
							{"text ypos",  TextY }, 
							{"listen", AddrTranslator.getAdrString (eibObj[0]) },
							{"init",  eibObj[0].init },
							{"text color", fontColor },
							{"background", cBackGround },
							{"opac",  opac }
    					  }; 

    	((LcdEditor)myParent).setTable (data, "Value");
	}
	
	
	public boolean setNewValue (Object object, Object value) {

		((LcdEditor)myParent).objectPropertiesChanged ();
		if (object == "listen") {
			if (value.toString().isEmpty()) {
				return false;
			}
			eibObj[0].setAddr(AddrTranslator.getAdrValue ((String)value));
			return true;
		}

		if (object == "unit") {
			postFixUnit = (String)value;
			repaint();
			return true;
		}

		if (object == "x-pos") {
			int x = new Integer((String)value).intValue();
			if (!checkLocation (x, getY())) return false;
			repaint();
			return true;
		}

		if (object == "y-pos") {
			int y = new Integer((String)value).intValue();
			if (!checkLocation (getX(), y)) return false;
			setLocation (getX(), y);
			return true;
		}
		
		if (object == "width") {
			int W = new Integer((String)value).intValue();
			setWidth (W);
			repaint();
			return true;
		}
		if (object == "height") {
			int H = new Integer((String)value).intValue();
			setHeight(H);
			repaint();
			return true;
		}
		
		if ((object == "init")  && (value instanceof Boolean)){
			eibObj[0].init = (Boolean)value;
			return true;
		}

		if (object == "text color") {
			fontColor = (Color) value;
			repaint();
			return true;
		}
		
		if (object == "background") {
			cBackGround = (Color) value;
			repaint();
			return true;
		}

		if (object == "font") {
			elementFont = (Font) value;
			repaint();
			return true;
		}

		if (object == "text xpos") {
			int x = new Integer((String)value).intValue();
			if ((x < 0) || (x > this.getParent().getWidth())) return false;
			TextX = x;
			repaint();
			return true;
		}

		if (object == "text ypos") {
			int y = new Integer((String)value).intValue();
			if ((y < 0) || (y > this.getParent().getHeight())) return false;
			TextY = y;
			repaint();
			return true;
		}
		if ((object == "opac") && (value instanceof Boolean)) {
			opac = (Boolean)value;
			setOpaque(opac);
			repaint();
			return true;
		}
		if (object == "value") {
			repaint();
			return true;
		}
		if (object == "integers") {
			int i = new Integer((String)value).intValue();
			if ((i < 0) || (i > 7)) return false;
			integers = i;
			repaint();
			return true;
		}
		if (object == "decimals") {
			int d = new Integer((String)value).intValue();
			if ((d < 0) || (d > 7)) return false;
			decimals = d;
			repaint();
			return true;
		}

		return false;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int W = getWidth();
		int H = getHeight();
		
		if (opac) {
			g.setColor(cBackGround);
			g.fillRect(0, 0, W, H);	
		}
		
		String s = valueFormat.getValueFormatExample(integers, decimals);
		g.setColor(fontColor);
		g.setFont (elementFont);
		g.drawString(s + postFixUnit, TextX, TextY);

		if (selected) {
			g.setColor(Color.ORANGE);
			g.drawRect(0, 0, W-1, H-1);
			g.drawRect(1, 1, W-3, H-3);
			g.drawRect(2, 2, W-5, H-5);
		}
	}
	
	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selectState) {
		selected = selectState;
	}
	
	@Override
	void new_msg(char addr, int len, byte[] data) {
		if ((eibObj[0] != null) && eibObj[0].matchAddr(addr)) {
			state = data [0] & 0x01;
			repaint();
		}
	}
	
	protected static byte SIZE_VALUE_PARAMETERS = 10;
	protected static byte SIZE_OF_VALUE_OBJECT = (byte) (SIZE_VALUE_PARAMETERS + 2); // +2 for type and size
	protected static byte VALUE_OBJECT_TYPE = 5;
	// for parameters
	protected static byte VALUE_FLAG_INIT_OBJECT = (byte) 0x80;
	protected static final String numbers [] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "+", "-", ":"};

	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
			LcdPageContainer pageContainer, LcdEibAddresses eibAddresses, 
			LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
			DisplayProperties dor, LcdListenerContainer listener, int myPage) {

		// create background image to support transparency
		BufferedImage localBackground = getElementBackground (backgroundComp, pageBackgroundColor);
		
		// merge icon and text for button up onto background
		Graphics gbi = localBackground.createGraphics();
		if (opac) {
			gbi.setColor(cBackGround);
			gbi.fillRect(0, 0, getWidth(), getHeight());	
		}
		int first = imageContainer.addImage(localBackground, getWidth(), getHeight());
		// create font information
		for (int i = 0; i < numbers.length; i++) {
			// create background image to support transparency
			BufferedImage localChar = getElementBackground (backgroundComp, pageBackgroundColor);
			// merge icon and text for button up onto background
			gbi = localChar.createGraphics();
			if (opac) {
				gbi.setColor(cBackGround);
				gbi.fillRect(0, 0, getWidth(), getHeight());	
			}
		    gbi.setFont(elementFont);
		    gbi.setColor(fontColor);
		    gbi.drawString(numbers[i], TextX, TextY);
			int charWidth = (int) gbi.getFontMetrics().getStringBounds(numbers[i], gbi).getWidth();  
			// add image to image container
			imageContainer.addImage(localChar.getSubimage(TextX, 0, charWidth, getHeight()), charWidth, getHeight());
		}

		// create postFixUnit image
		BufferedImage postFix = getElementBackground (backgroundComp, pageBackgroundColor);
		// merge icon and text for button up onto background
		gbi = postFix.createGraphics();
		if (opac) {
			gbi.setColor(cBackGround);
			gbi.fillRect(0, 0, getWidth(), getHeight());	
		}
	    gbi.setFont(elementFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(postFixUnit, TextX, TextY);
		int postFixWidth = (int) gbi.getFontMetrics().getStringBounds(postFixUnit, gbi).getWidth();
		if (postFixWidth == 0)
			postFixWidth = 1;
		// add image to image container
		imageContainer.addImage(postFix.getSubimage(TextX, 0, postFixWidth, getHeight()), postFixWidth, getHeight());

		// add value element to output file
		byte[] parameter = new byte [SIZE_VALUE_PARAMETERS];
		// index of 1st image (= empty background)
		// images: empty background, characters, postFixUnit
		parameter [1] = (byte) ((first >> 8) & 0xff);
		parameter [0] = (byte) ((first >> 0) & 0xff);
		// start pos x
		Point origin = dor.getElementOrigin(getXPos(), getYPos(), new Dimension (getWidth(), getHeight()));
		parameter [3] = (byte) ((origin.x >> 8) & 0xff);
		parameter [2] = (byte) ((origin.x >> 0) & 0xff);
		// start pos y
		parameter [5] = (byte) ((origin.y >> 8) & 0xff);
		parameter [4] = (byte) ((origin.y >> 0) & 0xff);
		// start Xpos offset of first character
		parameter [6] = (byte) TextX;
		// character length
		parameter [7] = (byte) ((byte) integers << 4 | (byte) decimals);
		// EIB Object # for listen
		parameter [8] = (byte) (eibAddresses.getAddrIndex(eibObj[0].getAddr()) & 0xff);
		//Parameter: d0-d3 = value format, d7=init flag
		parameter [9] = (byte) valueFormat.getValueFormat();
		if (eibObj[0].init)
			parameter [9] |= VALUE_FLAG_INIT_OBJECT; 
		// store element to current page
		pageContainer.addElement(SIZE_OF_VALUE_OBJECT, VALUE_OBJECT_TYPE, parameter);
	}

	@Override
	public void registerEibAddresses(LcdEibAddresses groupAddr) {
		groupAddr.addAddr(eibObj[0].getAddr());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
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

}
