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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@SuppressWarnings("serial")
public class ControlElementPicture extends EIBComp
		implements MouseMotionListener, MouseListener {

	private Point lastPos;
	PropertiesTableModel inspectorModel = null;
	private boolean selected;
	protected ImageIcon myPict;
	protected int pictureID;

	public ControlElementPicture(LcdEditor parent, int x, int y,
							int picureID, PictureLibrary pictures) {

		myParent = parent;
		this.pictures = pictures;
		this.pictureID = picureID;
		myPict = pictures.getPictureFromLibrary(picureID).getPicture();
		if (myPict != null) {
			setIconDimension (myPict);
		}
		else {
			setWidth (50);
			setHeight (50);
			System.err.println ("ControlElementPicture can't refer image " + pictureID);
		}
		setXPos (x); setYPos (y);
		setUpElement();
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public ControlElementPicture(LcdEditor parent, XMLStreamReader parser,
			PictureLibrary pictures) {
		super (parent, parser, pictures, null);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	@Override
	protected void handleAttribute (XMLStreamReader parser, int i){
		// handler for derived objects
		if (parser.getAttributeLocalName( i ) == "PictureID" ) {
			pictureID = Integer.decode (parser.getAttributeValue( i ));
			myPict = pictures.getPictureFromLibrary(pictureID).getPicture();
			setWidth (myPict.getIconWidth());
			setHeight (myPict.getIconHeight());
			return;
		}
		super.handleAttribute (parser, i);
	}

    @Override
	public void setUpElement() {
		addMouseListener(this);
		setOpaque(false);
	}
	
	@Override
	public EditorComponent getClone () {
		return new ControlElementPicture ((LcdEditor)myParent, getX()+2, getY()+2, pictureID, pictures );
	}

	@Override
	public void writeXML(TransformerHandler hd) throws SAXException {
		
		AttributesImpl atts = new AttributesImpl();
		atts.clear();
		atts.addAttribute("","","X","CDATA",""+getX());
		atts.addAttribute("","","Y","CDATA",""+getY());
		atts.addAttribute("","","PictureID","CDATA",""+ pictureID);
		hd.startElement("","","PICT",atts);
		hd.endElement("","","PICT");
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = (int)(e.getX() - lastPos.getX());
		int dy = (int)(e.getY() - lastPos.getY());
		((LcdEditor)myParent).moveAllSelected (dx, dy);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastPos = e.getPoint();
		((LcdEditor)myParent).selectElement (this,!e.isControlDown());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		fillEditor();
		getParent().invalidate();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		fillEditor();
	}	

	@Override
	public String toString () {
		return "Picture '" + (String)pictures.getPictureNames(pictureID).toString() + "'";
	}
	
	@Override
	public void fillEditor (){
		((LcdEditor)myParent).flushInspector();
		// fill up inspector
		inspectorModel = ((LcdEditor)myParent).getInspector ();
		inspectorModel.setEibComp(this);
		// populate the window
    	Object[][] data = { 
							{"x-pos",  getX() }, 
							{"y-pos",  getY() },
							{"Name", (String)pictures.getPictureNames(pictureID).toString()},
							{"ID", pictureID}
    					  };

    	((LcdEditor)myParent).setTable (data, "Picture");
	}
	
	
	public boolean setNewValue (Object object, Object value) {
		
		((LcdEditor)myParent).objectPropertiesChanged ();

		if (object == "x-pos") {
			int x = new Integer((String)value).intValue();
			if (!checkLocation (x, getY())) return false;
			setLocation (x, getY() );
			return true;
		}

		if (object == "y-pos") {
			int y = new Integer((String)value).intValue();
			if (!checkLocation (getX(), y)) return false;
			setLocation (getX(), y);
			return true;
		}
		
		return false;
	}
	
	public ImageIcon getPicture () {
		return myPict;
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override 
	public void paint(Graphics g)
	{
		super.paint(g);
		int W = getWidth ();
		int H = getHeight();

		if (myPict != null) {
			g.drawImage(myPict.getImage(), 0, 0, this);
		}
		else {
			g.setColor(Color.darkGray);
			g.fillRect(0, 0, W, H);
		}
		
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

	protected static byte SIZE_IMAGE_PARAMETERS = 6;
	protected static byte SIZE_OF_IMAGE_OBJECT = (byte) (SIZE_IMAGE_PARAMETERS + 2);
	protected static byte IMAGE_OBJECT_TYPE = 0;
	
	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
		LcdPageContainer pageContainer, LcdEibAddresses eibAddresses, 
		LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
		DisplayProperties dor, LcdListenerContainer listener, int myPage) {

		BufferedImage localImage = getElementBackground (backgroundComp, pageBackgroundColor);

		// add image to image container
		int myImageIndex = 0;
		myImageIndex = imageContainer.addImage(localImage, myPict.getIconWidth(), myPict.getIconHeight());
		byte[] parameter = new byte [SIZE_IMAGE_PARAMETERS];
		// image index
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changePageName(String oldName, String newName) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isPictureInUse(int id) {
		
		if (id == pictureID) return true;

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
