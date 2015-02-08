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

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@SuppressWarnings("serial")
public class ControlElementStateButton extends EIBComp implements 
						MouseMotionListener, MouseListener {

    private Point lastPos = new Point (0,0);
	PropertiesTableModel inspectorModel = null;
	private boolean selected;
	private int pictureIDupOff;
	private int pictureIDdownOff;
	private int pictureIDupOn;
	private int pictureIDdownOn;
	private ImageIcon imageButtonUpOff;
	private ImageIcon imageButtonDownOff;
	private ImageIcon imageButtonUpOn;
	private ImageIcon imageButtonDownOn;
	private	int TextX;
	private int TextY;
	private int TextDX;
	private int TextDY;
	private Font buttonFont;
	private Color fontColor;
	protected boolean showButtonDown = false;
	private SButtonFunctionType buttonFunction;
	private int soundIDdown;
	private int soundIDup;


	public ControlElementStateButton(LcdEditor server, 
			int x, int y, int w, int h, String name, char sAddr0, boolean addr1Used, char sAddr1,
			int pictureIDupOff, int pictureIDdownOff, 
			int pictureIDupOn, int pictureIDdownOn, PictureLibrary pictures, 
			int soundIDup, int soundIDdown, SoundLibrary sounds, int buttonFunctionSelection, 
			Font buttonFont, Color fontColor, int TextX, int TextY, int TextDX, int TextDY) {

		super (server, x, y, w, h, name);
    	this.pictures = pictures;
    	this.sounds = sounds;
    	this.pictureIDupOff = pictureIDupOff;
    	this.pictureIDdownOff = pictureIDdownOff;
    	this.pictureIDupOn = pictureIDupOn;
    	this.pictureIDdownOn = pictureIDdownOn;
		eibObj[0] = new EIBObj (sAddr0);
		eibObj[1] = new EIBObj (sAddr1);
		imageButtonUpOff = getIcon (pictureIDupOff);
		imageButtonDownOff = getIcon (pictureIDdownOff);
		imageButtonUpOn = getIcon (pictureIDupOn);
		imageButtonDownOn = getIcon (pictureIDdownOn);
		if (imageButtonUpOff != null) {
			setIconDimension (imageButtonUpOff);
		}
		this.TextX = TextX;	TextY = this.TextY = TextY;
		this.TextDX = TextDX; this.TextDY = TextDY;
		this.fontColor = fontColor;
		this.buttonFont = new Font (buttonFont.getFamily(), buttonFont.getStyle(), buttonFont.getSize());
		buttonFunction = new SButtonFunctionType (buttonFunctionSelection);
		this.soundIDup = soundIDup;
		this.soundIDdown = soundIDdown;
		setUpElement();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		showButtonDown = false;
	}

	public ControlElementStateButton(LcdEditor server, 
			int x, int y, int w, int h, String name, char sAddr0, boolean addr1Used, char sAddr1,
			int pictureIDupOff, int pictureIDdownOff, 
			int pictureIDupOn, int pictureIDdownOn, PictureLibrary pictures, 
			int soundIDup, int soundIDdown, SoundLibrary sounds, int buttonFunctionSelection) {

		super (server, x, y, w, h, name);

    	this.pictures = pictures;
    	this.sounds = sounds;
		eibObj[0] = new EIBObj (sAddr0);
		if (addr1Used)
			eibObj[1] = new EIBObj (sAddr1);
		this.pictureIDupOff = pictureIDupOff;
		imageButtonUpOff = getIcon (pictureIDupOff);
		this.pictureIDdownOff = pictureIDdownOff;
		imageButtonDownOff = getIcon (pictureIDdownOff);
		this.pictureIDupOn = pictureIDupOn;
		imageButtonUpOn = getIcon (pictureIDupOn);
		this.pictureIDdownOn = pictureIDdownOn;
		imageButtonDownOn = getIcon (pictureIDdownOn);
		if (imageButtonUpOff != null) {
			setIconDimension (imageButtonUpOff);
		}
		TextX = (int) getWidth()/10;	TextY = (int) (0.6*getHeight ());
		TextDX = 1; TextDY = 1;
		buttonFont = new Font ("Arial", Font.BOLD, 16);
		fontColor = Color.black;
		buttonFunction = new SButtonFunctionType (buttonFunctionSelection);
		this.soundIDup = soundIDup;
		this.soundIDdown = soundIDdown;
		setUpElement();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		showButtonDown = false;
	}


	public ControlElementStateButton(LcdEditor server, XMLStreamReader parser, 
					PictureLibrary pictures, SoundLibrary sounds) {
		super(server, parser, pictures, sounds);
	}

	@Override
	public EditorComponent getClone () {
		if (eibObj[1] != null)
			return new ControlElementStateButton ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
										elementName, eibObj[0].getAddr(), true, eibObj[1].getAddr(),
										pictureIDupOff, pictureIDdownOff, pictureIDupOn, pictureIDdownOn, pictures, 
										soundIDup, soundIDdown, sounds, buttonFunction.getButtonFunction(),
										buttonFont, fontColor, TextX, TextY, TextDX, TextDY);
		else 
			return new ControlElementStateButton ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
					elementName, eibObj[0].getAddr(), false, (char) 0, 
					pictureIDupOff, pictureIDdownOff, pictureIDupOn, pictureIDdownOn, pictures, 
					soundIDup, soundIDdown, sounds, buttonFunction.getButtonFunction(), 
					buttonFont, fontColor, TextX, TextY, TextDX, TextDY);
	}

	@Override
	public void setUpElement() {
		if (buttonFont == null) buttonFont = new Font ("Arial", Font.PLAIN, 12);
		if (fontColor == null) fontColor = Color.black;
		if (buttonFunction == null) buttonFunction = new SButtonFunctionType (ButtonFunctionType.FUNCTION_TOGGLE);
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
		addMouseListener(this);
		setOpaque(false);
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
		atts.addAttribute("","","Font","CDATA",buttonFont.getFamily()+"-"+
				FontStyleCalc.getFontStyle(buttonFont.getStyle())+"-"+buttonFont.getSize());
		atts.addAttribute("","","FontColor","CDATA",""+fontColor.getRGB());
		atts.addAttribute("","","FontAlpha","CDATA",""+fontColor.getAlpha());
		atts.addAttribute("","","TextX","CDATA",""+TextX);
		atts.addAttribute("","","TextY","CDATA",""+TextY);
		atts.addAttribute("","","TextDX","CDATA",""+TextDX);
		atts.addAttribute("","","TextDY","CDATA",""+TextDY);
		if (eibObj[0] != null) {
			atts.addAttribute("","","ObjAddr0","CDATA",""+(0+eibObj[0].getAddr()));
		}
		if (eibObj[1] != null) {
			atts.addAttribute("","","ObjAddr1","CDATA",""+(0+eibObj[1].getAddr()));
			if (eibObj[1].init)
				atts.addAttribute("","","initObj1","CDATA","");
		}
		atts.addAttribute("","","ButtonFunction","CDATA",""+buttonFunction.getButtonFunction());
		atts.addAttribute("","","PictureUpOff","CDATA",""+pictureIDupOff);
		atts.addAttribute("","","PictureDownOff","CDATA",""+pictureIDdownOff);
		atts.addAttribute("","","PictureUpOn","CDATA",""+pictureIDupOn);
		atts.addAttribute("","","PictureDownOn","CDATA",""+pictureIDdownOn);
		atts.addAttribute("","","SoundUp","CDATA",""+soundIDup);
		atts.addAttribute("","","SoundDown","CDATA",""+soundIDdown);

		hd.startElement("","","SBTN",atts);
		hd.endElement("","","SBTN");
	}

	@Override
	protected void handleAttribute (XMLStreamReader parser, int i){
		// handler for derived objects
		if (parser.getAttributeLocalName( i ) == "PictureUpOff" ) {
			pictureIDupOff = Integer.decode (parser.getAttributeValue( i ));
			imageButtonUpOff = getIcon (pictureIDupOff);
			return;
		}
		if (parser.getAttributeLocalName( i ) == "PictureDownOff" ) {
			pictureIDdownOff = Integer.decode (parser.getAttributeValue( i ));
			imageButtonDownOff = getIcon (pictureIDdownOff);
			return;
		}
		if (parser.getAttributeLocalName( i ) == "PictureUpOn" ) {
			pictureIDupOn = Integer.decode (parser.getAttributeValue( i ));
			imageButtonUpOn = getIcon (pictureIDupOn);
			return;
		}
		if (parser.getAttributeLocalName( i ) == "PictureDownOn" ) {
			pictureIDdownOn = Integer.decode (parser.getAttributeValue( i ));
			imageButtonDownOn = getIcon (pictureIDdownOn);
			return;
		}
		if (parser.getAttributeLocalName( i ) == "Font" ) {
			buttonFont = Font.decode(parser.getAttributeValue( i ));
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
		if (parser.getAttributeLocalName( i ) == "TextDX" ) {
			TextDX = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "TextDY" ) {
			TextDY = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "ButtonFunction" ) {
			buttonFunction = new SButtonFunctionType (Integer.decode (parser.getAttributeValue( i )));
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
		if (parser.getAttributeLocalName( i ) == "SoundUp" ) {
			soundIDup = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "SoundDown" ) {
			soundIDdown = Integer.decode (parser.getAttributeValue( i ));
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if ((e.getModifiers() == 16) || (e.getModifiers() == 18)) {
			lastPos = e.getPoint();
			((LcdEditor)myParent).selectElement (this,!e.isControlDown());
		}
		else if (e.getModifiers() == 4) {
			showButtonDown = true;
			state = (state + 1) & 1;
			sounds.playSound (soundIDdown);
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
			showButtonDown = false;
			sounds.playSound (soundIDup);
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
		return "SButton '" + elementName + "' " + AddrTranslator.getAdrString (eibObj[0]) + " " + AddrTranslator.getAdrString (eibObj[1]);
	}

	@Override
	public void fillEditor (){
		((LcdEditor)myParent).flushInspector();
		// fill up inspector
		inspectorModel = ((LcdEditor)myParent).getInspector ();
		inspectorModel.setEibComp(this);
		// populate the window
    	Object[][] data = { 
							{"label",  elementName }, 
							{"width",  Math.round(getWidth()) }, 
							{"height",  Math.round(getHeight()) }, 
							{"x-pos",  getX() }, 
							{"y-pos",  getY() }, 
							{"font",  buttonFont }, 
							{"color",  fontColor }, 
							{"Text xpos",  TextX }, 
							{"Text ypos",  TextY }, 
							{"Text dx",  TextDX }, 
							{"Text dy",  TextDY }, 
    						{"send", AddrTranslator.getAdrString (eibObj[0]) },
    						{"listen", AddrTranslator.getAdrString (eibObj[1]) },
							{"btn up off", pictures.getPictureNames (pictureIDupOff) },
							{"btn down off", pictures.getPictureNames (pictureIDdownOff) },
							{"btn up on", pictures.getPictureNames (pictureIDupOn) },
							{"btn down on", pictures.getPictureNames (pictureIDdownOn) },
							{"sound up", sounds.getSoundNames (soundIDup) },
							{"sound down", sounds.getSoundNames (soundIDdown) }
    					  };

    	((LcdEditor)myParent).setTable (data, "State Button");
	}
	
	
	public boolean setNewValue (Object object, Object value) {
		
		((LcdEditor)myParent).objectPropertiesChanged ();

		if (object == "label") {
			elementName = (String)value;
			repaint();
			return true;
		}
		if (object == "send") {
			if (value.toString().isEmpty()) {
				return false;
			}
			eibObj[0].setAddr(AddrTranslator.getAdrValue ((String)value));
			return true;
		}
		if (object == "listen") {
			if (value.toString().isEmpty()) {
				eibObj[1] = null;
			}
			else {
				if (eibObj[1] == null) {
					eibObj[1] = new EIBObj (AddrTranslator.getAdrValue ((String)value));
				}
				else eibObj[1].setAddr(AddrTranslator.getAdrValue ((String)value));
			}
			return true;
		}

		if (object == "x-pos") {
			int x = new Integer((String)value).intValue();
			if (!checkLocation (x, getY())) return false;
			setXPos (x);
			return true;
		}

		if (object == "y-pos") {
			int y = new Integer((String)value).intValue();
			if (!checkLocation (getX(), y)) return false;
			setYPos (y);
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

		if (object == "Text dx") {
			int x = new Integer((String)value).intValue();
			if ((x < -10) || (x > 10)) return false;
			TextDX = x;
			return true;
		}

		if (object == "Text dy") {
			int y = new Integer((String)value).intValue();
			if ((y < -10) || (y > 10)) return false;
			TextDY = y;
			return true;
		}

		if (object == "font") {
			buttonFont = (Font) value;
			repaint();
			return true;
		}

		if (object == "color") {
			fontColor = (Color) value;
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
				
		if ((object == "init") && (value instanceof Boolean)) {
			if ((eibObj[1]) != null)
				eibObj[1].init = (Boolean)value;
			return true;
		}
		if (object == "function") {
			return true;
		}
		if (object == "btn up off") {
			pictureIDupOff = ((PictureNames)value).getSelectedPictureID();
			imageButtonUpOff = getIcon (pictureIDupOff);
			if (imageButtonUpOff != null) {
				setIconDimension (imageButtonUpOff);
			}
			repaint();
			return true;
		}
		if (object == "btn down off") {
			pictureIDdownOff = ((PictureNames)value).getSelectedPictureID();
			imageButtonDownOff = getIcon (pictureIDdownOff);
			if (imageButtonDownOff != null) {
				setIconDimension (imageButtonDownOff);
			}
			repaint();
			return true;
		}
		if (object == "btn up on") {
			pictureIDupOn = ((PictureNames)value).getSelectedPictureID();
			imageButtonUpOn = getIcon (pictureIDupOn);
			if (imageButtonUpOn != null) {
				setIconDimension (imageButtonUpOn);
			}
			repaint();
			return true;
		}
		if (object == "btn down on") {
			pictureIDdownOn = ((PictureNames)value).getSelectedPictureID();
			imageButtonDownOn = getIcon (pictureIDdownOn);
			if (imageButtonDownOn != null) {
				setIconDimension (imageButtonDownOn);
			}
			repaint();
			return true;
		}
		if (object == "sound up") {
			soundIDup = ((SoundNames)value).getSelectedSoundID();
			return true;
		}
		if (object == "sound down") {
			soundIDdown = ((SoundNames)value).getSelectedSoundID();
			return true;
		}

		return false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override 
	public void paint(Graphics g)
	{
		ImageIcon showIcon;
		int textDx = TextX;
		int textDy = TextY;
		int W = getWidth ();
		int H = getHeight();
		super.paint(g);
		if (!showButtonDown) {
			if (state == 0) {
				showIcon = imageButtonUpOff;
			}
			else {
				showIcon = imageButtonUpOn;
			}
		}
		else {
			textDx += TextDX; 
			textDy += TextDY; 
			if (state == 0) {
				showIcon = imageButtonDownOff;
			}
			else {
				showIcon = imageButtonDownOn;
			}
		}		
		
		if (showIcon != null) {
			g.drawImage(showIcon.getImage(), 0, 0, this);
		}
		g.setFont(buttonFont);
		g.setColor(fontColor);
		g.drawString(elementName, textDx, textDy);
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

	protected static byte SIZE_SBUTTON_PARAMETERS = 19;
	protected static byte SIZE_OF_SBUTTON_OBJECT = (byte) (SIZE_SBUTTON_PARAMETERS + 2);
	protected static byte SBUTTON_OBJECT_TYPE = 6;

	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
			LcdPageContainer pageContainer, LcdEibAddresses eibAddresses,
			LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
			DisplayProperties dor, LcdListenerContainer listener, LcdTimeoutContainer timeout, int myPage) {

		// create background image to support transparency
		BufferedImage localImageButtonUpOff = getElementBackground (backgroundComp, pageBackgroundColor);
		BufferedImage localImageButtonDownOff = getElementBackground (backgroundComp, pageBackgroundColor);
		BufferedImage localImageButtonUpOn = getElementBackground (backgroundComp, pageBackgroundColor);
		BufferedImage localImageButtonDownOn = getElementBackground (backgroundComp, pageBackgroundColor);
		
		// merge icon and text for button up onto background
		Graphics gbi = localImageButtonUpOff.createGraphics();
		if (imageButtonUpOff != null) {
			gbi.drawImage(imageButtonUpOff.getImage(), 0, 0, null);
		}
		gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(elementName, TextX, TextY);	

		// merge icon and text for button down onto background
		gbi = localImageButtonDownOff.createGraphics();
		if (imageButtonDownOff != null) {
			gbi.drawImage(imageButtonDownOff.getImage(), 0, 0, null);
		}
	    gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(elementName, TextX+TextDX, TextY+TextDY);
		
		// merge icon and text for button up onto background
		gbi = localImageButtonUpOn.createGraphics();
		if (imageButtonUpOn != null) {
			gbi.drawImage(imageButtonUpOn.getImage(), 0, 0, null);
		}
		gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(elementName, TextX, TextY);	

		// merge icon and text for button down onto background
		gbi = localImageButtonDownOn.createGraphics();
		if (imageButtonDownOn != null) {
			gbi.drawImage(imageButtonDownOn.getImage(), 0, 0, null);
		}
	    gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(elementName, TextX+TextDX, TextY+TextDY);
		
	    // add image to image container
		int myImageIndex = 0;
		byte[] parameter = new byte [SIZE_SBUTTON_PARAMETERS];
		// image index button up off
		myImageIndex = imageContainer.addImage(localImageButtonUpOff, getWidth(), getHeight());
		parameter [1] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [0] = (byte) ((myImageIndex >> 0) & 0xff);
		// image index button down off
		myImageIndex = imageContainer.addImage(localImageButtonDownOff, getWidth(), getHeight());
		parameter [3] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [2] = (byte) ((myImageIndex >> 0) & 0xff);
		// image index button up on
		myImageIndex = imageContainer.addImage(localImageButtonUpOn, getWidth(), getHeight());
		parameter [5] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [4] = (byte) ((myImageIndex >> 0) & 0xff);
		// image index button down on
		myImageIndex = imageContainer.addImage(localImageButtonDownOn, getWidth(), getHeight());
		parameter [7] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [6] = (byte) ((myImageIndex >> 0) & 0xff);
		// start pos x
		Point origin = dor.getElementOrigin(getXPos(), getYPos(), new Dimension (getWidth(), getHeight()));
		parameter [9] = (byte) ((origin.x >> 8) & 0xff);
		parameter [8] = (byte) ((origin.x >> 0) & 0xff);
		// start pos y
		parameter [11] = (byte) ((origin.y >> 8) & 0xff);
		parameter [10] = (byte) ((origin.y >> 0) & 0xff);
		// EIB Object #1
		parameter [12] = (byte) (eibAddresses.getAddrIndex(eibObj[0].getAddr()) & 0xff);
		// EIB Object #2
		if (eibObj[1] != null)
			parameter [13] = (byte) (eibAddresses.getAddrIndex(eibObj[1].getAddr()) & 0xff);
		else
			parameter [13] = parameter [12];
		// EIB Object value
		parameter [14] = (byte) buttonFunction.getButtonFunction();
		// store sounds
		int mySoundIndex = soundContainer.addSound(soundIDup, sounds);
		parameter [16] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [15] = (byte) ((mySoundIndex >> 0) & 0xff);
		mySoundIndex = soundContainer.addSound(soundIDdown, sounds);
		parameter [18] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [17] = (byte) ((mySoundIndex >> 0) & 0xff);
		// store element to current page
		pageContainer.addElement(SIZE_OF_SBUTTON_OBJECT, SBUTTON_OBJECT_TYPE, parameter);
	}

	@Override
	public void registerEibAddresses(LcdEibAddresses groupAddr) {
		groupAddr.addAddr(eibObj[0].getAddr());
		if (eibObj[1] != null)
			groupAddr.addAddr(eibObj[1].getAddr());
	}

	@Override
	public void changePageName(String oldName, String newName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPictureInUse(int id) {
		
		if (id == pictureIDdownOff) return true;
		if (id == pictureIDdownOn) return true;
		if (id == pictureIDupOff) return true;
		if (id == pictureIDupOn) return true;

		return false;
	}
	
	@Override
	public boolean isSoundInUse(int id) {
		
		if (id == soundIDdown) return true;
		if (id == soundIDup) return true;

		return false;
	}
	
	@Override
	public boolean isPageNameUsed(String name) {
		return false;
	}

}
