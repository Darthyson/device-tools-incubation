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

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import lcdEdit.LcdEditor;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@SuppressWarnings("serial")
public class ControlElementText extends EIBComp implements 
						MouseMotionListener, MouseListener {

    private Point lastPos = new Point (0,0);
	PropertiesTableModel inspectorModel = null;
	private boolean selected;
	private	int TextX;
	private int TextY;
	private Font fontType;
	private Color fontColor;
	private Color backgroundColor;
	private boolean opac;
	private static final int DEFAULT_TEXT_SIZE = 14;

	public ControlElementText(LcdEditor server, 
			int x, int y, int w, int h, String name ) {

	super (server, x, y, w, h, name);
	state = 1;
	TextX = 3;	TextY = getHeight()-4;
	fontType = new Font ("Arial", Font.PLAIN, DEFAULT_TEXT_SIZE);
	fontColor = Color.black;
	backgroundColor = Color.white;
	setUpElement();
	addMouseMotionListener(this);
	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}

	public ControlElementText(LcdEditor server, 
				int x, int y, int w, int h, String name, Font fontType, Color fontColor, Color backgroundColor, boolean opac,
						int TextX, int TextY) {

    	myParent = server;
    	setXPos (x); setYPos (y);
    	setWidth (w); setHeight (h);
		state = 1;
		elementName = name;
		this.TextX = TextX;	this.TextY = TextY;
		this.fontType = new Font (fontType.getFamily(), fontType.getStyle(), fontType.getSize());
		this.fontColor = fontColor;
		this.backgroundColor = backgroundColor;
		this.opac = opac;
		setUpElement();
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}


	public ControlElementText(LcdEditor server, XMLStreamReader parser) {
		super(server, parser, null, null);
		if (fontType == null) fontType = new Font ("Arial", Font.PLAIN, DEFAULT_TEXT_SIZE);
		if (fontColor == null) fontColor = Color.black;
		if (backgroundColor == null) backgroundColor = Color.white;
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
	}

	@Override
	public EditorComponent getClone () {
		return new ControlElementText ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
										elementName, fontType, fontColor, backgroundColor, opac, TextX, TextY);
	}

    @Override
	public void setUpElement() {
		addMouseListener(this);
		setOpaque(opac);
        selected = false;
    }
	
	@Override
	public void writeXML(TransformerHandler hd) throws SAXException {
		
		AttributesImpl atts = new AttributesImpl();
		atts.clear();
		atts.addAttribute("","","Name","CDATA", elementName);
		atts.addAttribute("","","X","CDATA",""+getX());
		atts.addAttribute("","","Y","CDATA",""+getY());
		atts.addAttribute("","","W","CDATA",""+getWidth());
		atts.addAttribute("","","H","CDATA",""+getHeight());
		atts.addAttribute("","","F","CDATA",""+state);
		atts.addAttribute("","","Font","CDATA",fontType.getFamily()+"-"+
				FontStyleCalc.getFontStyle(fontType.getStyle())+"-"+fontType.getSize());
		atts.addAttribute("","","FontColor","CDATA",""+fontColor.getRGB());
		atts.addAttribute("","","FontAlpha","CDATA",""+fontColor.getAlpha());
		if (opac)
			atts.addAttribute("","","Opac","CDATA","");
		atts.addAttribute("","","BackgroundColor","CDATA",""+backgroundColor.getRGB());
		atts.addAttribute("","","BackgroundAlpha","CDATA",""+backgroundColor.getAlpha());
		atts.addAttribute("","","TextX","CDATA",""+TextX);
		atts.addAttribute("","","TextY","CDATA",""+TextY);
			
		hd.startElement("","","TEXT",atts);
		hd.endElement("","","TEXT");
	}
	
    @Override
	protected void handleAttribute (XMLStreamReader parser, int i){
		// handler for derived objects
		if (parser.getAttributeLocalName( i ) == "Font" ) {
			fontType = Font.decode(parser.getAttributeValue( i ));
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
		if (parser.getAttributeLocalName( i ) == "Opac" ) {
			opac = true;
			return;
		}
		if (parser.getAttributeLocalName( i ) == "BackgroundColor" ) {
			backgroundColor = setColorKeepAlpha (backgroundColor, Color.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "BackgroundAlpha" ) {
			backgroundColor = setAlphaKeepColor (backgroundColor, Integer.decode(parser.getAttributeValue( i )));
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
		super.handleAttribute (parser, i);
	}

	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getModifiers() == 16) {
			int dx = (int)(e.getX() - lastPos.getX());
			int dy = (int)(e.getY() - lastPos.getY());
			((LcdEditor)myParent).moveAllSelected (dx, dy);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if ((e.getModifiers() == 16) || (e.getModifiers() == 18)) {
			lastPos = e.getPoint();
			((LcdEditor)myParent).selectElement (this,!e.isControlDown());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		if (e.getModifiers() == 16) {
			fillEditor();
			getParent().invalidate();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getModifiers() != 16) return;
		fillEditor();
	}	

	@Override 
	public String toString () {
		return "Text '" + elementName + "'";
	}
	
	@Override
	public void fillEditor (){
		// fill up inspector
		((LcdEditor)myParent).flushInspector();
		inspectorModel = ((LcdEditor)myParent).getInspector ();
		inspectorModel.setEibComp(this);
		// populate the window
    	Object[][] data = { 
							{"label",  elementName }, 
							{"width",  Math.round(getWidth()) }, 
							{"height",  Math.round(getHeight()) }, 
							{"x-pos",  getX() }, 
							{"y-pos",  getY() }, 
							{"font",  fontType }, 
							{"color",  fontColor }, 
							{"background",  backgroundColor }, 
							{"Text xpos",  TextX }, 
							{"Text ypos",  TextY }, 
							{"opac",  opac }
    					  };

    	((LcdEditor)myParent).setTable (data, "Text");
	}
	
	
	public boolean setNewValue (Object object, Object value) {

		((LcdEditor)myParent).objectPropertiesChanged ();
		
		if (object == "label") {
			elementName = (String)value;
			repaint();
			return true;
		}

		if (object == "x-pos") {
			int x = new Integer((String)value).intValue();
			if (!checkLocation (x, getY())) return false;
			setLocation (x, getY() );
			repaint();
			return true;
		}

		if (object == "y-pos") {
			int y = new Integer((String)value).intValue();
			if (!checkLocation (getX(), y)) return false;
			setLocation (getX(), y);
			repaint();
			return true;
		}
		
		if (object == "Text xpos") {
			int x = new Integer((String)value).intValue();
			if ((x < 0) || (x > this.getParent().getWidth())) return false;
			TextX = x;
			repaint();
			return true;
		}

		if (object == "Text ypos") {
			int y = new Integer((String)value).intValue();
			if ((y < 0) || (y > this.getParent().getHeight())) return false;
			TextY = y;
			repaint();
			return true;
		}

		if (object == "font") {
			fontType = (Font) value;
			repaint();
			return true;
		}

		if (object == "color") {
			fontColor = (Color) value;
			repaint();
			return true;
		}
		
		if (object == "background") {
			backgroundColor = (Color) value;
			repaint();
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
			setHeight (H);
			repaint();
			return true;
		}
				
		if ((object == "opac") && (value instanceof Boolean)) {
			opac = (Boolean)value;
			setOpaque(opac);
			repaint();
			return true;
		}

		return false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override 
	public void paint(Graphics g)
	{
		super.paint(g);
		
		int W = getWidth ();
		int H = getHeight ();
		
		if (opac) {
			g.setColor(backgroundColor);
			g.fillRect(0, 0, W, H);	
		}
		g.setFont(fontType);
		g.setColor(fontColor);
		g.drawString(elementName, TextX, TextY);
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

	// we store text as static picture
	protected static byte SIZE_IMAGE_PARAMETERS = 6;
	protected static byte SIZE_OF_IMAGE_OBJECT = (byte) (SIZE_IMAGE_PARAMETERS + 2);
	protected static byte IMAGE_OBJECT_TYPE = 0;
	
	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
			LcdPageContainer pageContainer, LcdEibAddresses eibAddresses, 
			LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
			DisplayProperties dor, LcdListenerContainer listener, LcdTimeoutContainer timeout, int myPage) {

	    BufferedImage backgroundImage = new BufferedImage(((LcdEditor)myParent).getMaxX(), ((LcdEditor)myParent).getMaxY(), BufferedImage.TYPE_INT_RGB);
	    Graphics gbi = backgroundImage.createGraphics();
	    // fill with page background color
		gbi.setColor(pageBackgroundColor);
		gbi.fillRect(0, 0, ((LcdEditor)myParent).getMaxX(), ((LcdEditor)myParent).getMaxY());
	    // create Image with all background components
		for ( Component thisComp : backgroundComp ) {
				  if (EIBComp.class.isInstance (thisComp)) {
					  EIBComp co = (EIBComp)thisComp;
					  co.paint (gbi);
				  }
		}
		BufferedImage bufferedImage = backgroundImage.getSubimage(getXPos(), getYPos(), getWidth(), getHeight());
		
		// merge text into image
	    Graphics gb = bufferedImage.createGraphics();
		if (opac) {
			gb.setColor(backgroundColor);
			gb.fillRect(0, 0, getWidth(), getHeight());	
		}
		gb.setFont(fontType);
		gb.setColor(fontColor);
		gb.drawString(elementName, TextX, TextY);
	    gb.dispose();		

		// add image to image container
		int myImageIndex = 0;
		byte[] parameter = new byte [SIZE_IMAGE_PARAMETERS];
		// image index text
		myImageIndex = imageContainer.addImage(bufferedImage, getWidth(), getHeight());
		parameter [1] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [0] = (byte) ((myImageIndex >> 0) & 0xff);
		// start pos x
		Point origin = dor.getElementOrigin(getXPos(), getYPos(), new Dimension (getWidth(), getHeight()));
		parameter [3] = (byte) ((origin.x >> 8) & 0xff);
		parameter [2] = (byte) ((origin.x >> 0) & 0xff);
		// start pos y
		parameter [5] = (byte) ((origin.y >> 8) & 0xff);
		parameter [4] = (byte) ((origin.y >> 0) & 0xff);
		// store element to current page
		pageContainer.addElement(SIZE_OF_IMAGE_OBJECT, IMAGE_OBJECT_TYPE, parameter);
	}

	@Override
	public void registerEibAddresses(LcdEibAddresses groupAddr) {
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
