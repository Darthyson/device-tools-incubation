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

//FIXME: add binary output function
//FIXME: add copy-paste for new values
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

import lcdEdit.LcdEditor;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

@SuppressWarnings("serial")
public class ControlElementButton extends EIBComp implements 
						MouseMotionListener, MouseListener {

    private Point lastPos = new Point (0,0);
	PropertiesTableModel inspectorModel = null;
	private boolean selected;
	private ImageIcon imageButtonUp;
	private ImageIcon imageButtonDown;
	private int pictureIDdown;
	private int pictureIDup;
	private int soundIDdown;
	private int soundIDup;
	protected boolean showButtonDown = false;
	private	int TextX;
	private int TextY;
	private int TextDX;
	private int TextDY;
	private Color fontColor;
	private Font buttonFont;
	private ButtonFunctionType buttonFunction;
	private int buttonValue;
	private float deltaSingle; // delta value for edit function, button pressed short
	private float deltaRepeat; // delta value for edit function, button hold
	private float maxValue; // max object value for edit function
	private float minValue; // min object value for edit function

	public ControlElementButton(LcdEditor server, 
				int x, int y, int w, int h, String name, char sAddr0, boolean addr1Used, char sAddr1,
					int pictureIDup, int pictureIDdown, PictureLibrary pictures, 
					int soundIDup, int soundIDdown, SoundLibrary sounds, int buttonFunctionSelection, 
					Font buttonFont, Color fontColor, int TextX, int TextY, int TextDX, int TextDY, int buttonValue,
					float deltaSingle, float deltaRepeat, float maxValue, float minValue) {

		super (server, x, y, w, h, name);

		this.pictures = pictures;
    	this.sounds = sounds;
		eibObj[0] = new EIBObj (sAddr0);
		if (addr1Used)
			eibObj[1] = new EIBObj (sAddr1);
		this.pictureIDup = pictureIDup;
		imageButtonUp = getIcon (pictureIDup);
		this.pictureIDdown = pictureIDdown;
		imageButtonDown = getIcon (pictureIDdown);
		if (imageButtonUp != null) {
			setIconDimension (imageButtonUp);
		}
		this.soundIDup = soundIDup;
		this.soundIDdown = soundIDdown;
		this.TextX = TextX;	TextY = this.TextY = TextY;
		this.TextDX = TextDX; this.TextDY = TextDY;
		this.fontColor = fontColor;
		this.buttonFont = new Font (buttonFont.getFamily(), buttonFont.getStyle(), buttonFont.getSize());
		buttonFunction = new ButtonFunctionType (buttonFunctionSelection);
		this.buttonValue = buttonValue;
		this.deltaSingle = deltaSingle;
		this.deltaRepeat = deltaRepeat; 
		this.maxValue = maxValue;
		this.minValue = minValue;
		setUpElement();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		showButtonDown = false;
	}

	
	public ControlElementButton(LcdEditor server, XMLStreamReader parser, 
				PictureLibrary pictures, SoundLibrary sounds) {
		super(server, parser, pictures, sounds);
	}
	
	public ControlElementButton(LcdEditor server, 
			int x, int y, int w, int h, String name, char sAddr0, boolean addr1Used, char sAddr1,
				int pictureIDup, int pictureIDdown, PictureLibrary pictures, 
				int soundIDup, int soundIDdown, SoundLibrary sounds, int buttonFunctionSelection, int buttonValue,
				float deltaSingle, float deltaRepeat, float maxValue, float minValue) {

		super (server, x, y, w, h, name);

    	this.pictures = pictures;
    	this.sounds = sounds;
		eibObj[0] = new EIBObj (sAddr0);
		if (addr1Used)
			eibObj[1] = new EIBObj (sAddr1);
		this.pictureIDup = pictureIDup;
		imageButtonUp = getIcon (pictureIDup);
		this.pictureIDdown = pictureIDdown;
		imageButtonDown = getIcon (pictureIDdown);
		if (imageButtonUp != null) {
			setIconDimension (imageButtonUp);
		}
		this.soundIDup = soundIDup;
		this.soundIDdown = soundIDdown;
		TextX = (int) getWidth()/10;	TextY = (int) (0.6*getHeight ());
		TextDX = 1; TextDY = 1;
		buttonFont = new Font ("Arial", Font.BOLD, 16);
		fontColor = Color.black;
		buttonFunction = new ButtonFunctionType (buttonFunctionSelection);
		this.buttonValue = buttonValue;
		this.deltaSingle = deltaSingle;
		this.deltaRepeat = deltaRepeat; 
		this.maxValue = maxValue;
		this.minValue = minValue;
		setUpElement();
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		showButtonDown = false;
	}

	@Override
	public EditorComponent getClone () {
		if (eibObj[1] != null)
			return new ControlElementButton ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
										elementName, eibObj[0].getAddr(), true, eibObj[1].getAddr(), pictureIDup, pictureIDdown, pictures, 
										soundIDup, soundIDdown, sounds, buttonFunction.getButtonFunction(), 
										buttonFont, fontColor, TextX, TextY, TextDX, TextDY, buttonValue,
										deltaSingle, deltaRepeat, maxValue, minValue);
		else 
			return new ControlElementButton ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
					elementName, eibObj[0].getAddr(), false, (char) 0, pictureIDup, pictureIDdown, pictures, 
					soundIDup, soundIDdown, sounds, buttonFunction.getButtonFunction(), 
					buttonFont, fontColor, TextX, TextY, TextDX, TextDY, buttonValue,
					deltaSingle, deltaRepeat, maxValue, minValue);

	}

    @Override
	public void setUpElement() {
		if (buttonFont == null) buttonFont = new Font ("Arial", Font.PLAIN, 12);
		if (fontColor == null) fontColor = Color.black;
		if (buttonFunction == null) buttonFunction = new ButtonFunctionType (ButtonFunctionType.FUNCTION_TOGGLE);
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
		atts.addAttribute("","","ButtonValue","CDATA",""+buttonValue);
		atts.addAttribute("","","DeltaValueS","CDATA",""+deltaSingle);
		atts.addAttribute("","","DeltaValueR","CDATA",""+deltaRepeat);
		atts.addAttribute("","","MinValue","CDATA",""+minValue);
		atts.addAttribute("","","MaxValue","CDATA",""+maxValue);
		atts.addAttribute("","","PictureUp","CDATA",""+pictureIDup);
		atts.addAttribute("","","PictureDown","CDATA",""+pictureIDdown);
		atts.addAttribute("","","SoundUp","CDATA",""+soundIDup);
		atts.addAttribute("","","SoundDown","CDATA",""+soundIDdown);

		hd.startElement("","","BTN",atts);
		hd.endElement("","","BTN");
	}
	
    @Override
	protected void handleAttribute (XMLStreamReader parser, int i){
		// handler for derived objects
		if (parser.getAttributeLocalName( i ) == "PictureUp" ) {
			pictureIDup = Integer.decode (parser.getAttributeValue( i ));
			imageButtonUp = getIcon (pictureIDup);
			return;
		}
		if (parser.getAttributeLocalName( i ) == "PictureDown" ) {
			pictureIDdown = Integer.decode (parser.getAttributeValue( i ));
			imageButtonDown = getIcon (pictureIDdown);
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
			buttonFunction = new ButtonFunctionType (Integer.decode (parser.getAttributeValue( i )));
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
		if (parser.getAttributeLocalName( i ) == "ButtonValue" ) {
			buttonValue = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "DeltaValueS" ) {
			deltaSingle = Float.parseFloat (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "DeltaValueR" ) {
			deltaRepeat = Float.parseFloat (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "MinValue" ) {
			minValue = Float.parseFloat (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "MaxValue" ) {
			maxValue = Float.parseFloat (parser.getAttributeValue( i ));
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
		// this function is not required		
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if ((e.getModifiers() == 16) || (e.getModifiers() == 18)) {
			lastPos = e.getPoint();
			((LcdEditor)myParent).selectElement (this,!e.isControlDown());
		}
		else if (e.getModifiers() == 4) {
			showButtonDown = true;
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
		return "Button '" + elementName + "' " + AddrTranslator.getAdrString (eibObj[0]) + " " + AddrTranslator.getAdrString (eibObj[1]);
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
							{"font",  buttonFont }, 
							{"color",  fontColor }, 
							{"text xpos",  TextX }, 
							{"text ypos",  TextY }, 
							{"text dx",  TextDX }, 
							{"text dy",  TextDY }, 
    						{"obj 0", AddrTranslator.getAdrString (eibObj[0]) },
							{"function", buttonFunction},
    						{"obj 1", AddrTranslator.getAdrString (eibObj[1]) },
    						{"value", buttonValue },
    						{"d-value s", deltaSingle },
    						{"d-value r", deltaRepeat },
    						{"max value", maxValue },
    						{"min value", minValue },
//							{"init",  Boolean.valueOf(getInit (eibObj[1])) }, 
							{"btn up", pictures.getPictureNames (pictureIDup) },
							{"btn down", pictures.getPictureNames (pictureIDdown) },
							{"sound up", sounds.getSoundNames (soundIDup) },
							{"sound down", sounds.getSoundNames (soundIDdown) }
    					  };

    	((LcdEditor)myParent).setTable (data, "Button");
	}
	
	
	public boolean setNewValue (Object object, Object value) {

		((LcdEditor)myParent).objectPropertiesChanged ();

		if (object == "label") {
			elementName = (String)value;
			repaint();
			return true;
		}
		if (object == "obj 0") {
			if (value.toString().isEmpty()) {
				return false;
			}
			eibObj[0].setAddr(AddrTranslator.getAdrValue ((String)value));
			return true;
		}
		if (object == "obj 1") {
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
		
		if (object == "value") {
			buttonValue = new Integer((String)value).intValue();
			return true;
		}
		if (object == "d-value s") {
			deltaSingle = new Float((String)value).floatValue();
			return true;
		}
		if (object == "d-value r") {
			deltaRepeat = new Float((String)value).floatValue();
			return true;
		}
		if (object == "max value") {
			maxValue = new Float((String)value).floatValue();
			return true;
		}
		if (object == "min value") {
			minValue = new Float((String)value).floatValue();
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

		if (object == "text dx") {
			int x = new Integer((String)value).intValue();
			if ((x < -10) || (x > 10)) return false;
			TextDX = x;
			return true;
		}

		if (object == "text dy") {
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

		if (object == "btn up") {
			pictureIDup = ((PictureNames)value).getSelectedPictureID();
			imageButtonUp = getIcon (pictureIDup);
			if (imageButtonUp != null) {
				setIconDimension (imageButtonUp);
			}
			repaint();
			return true;
		}
		
		if (object == "btn down") {
			pictureIDdown = ((PictureNames)value).getSelectedPictureID();
			imageButtonDown = getIcon (pictureIDdown);
			if (imageButtonDown != null) {
				setIconDimension (imageButtonDown);
			}
			repaint();
			return true;
		}
		if (object == "function") {
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
		// this function is not required		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// this function is not required		
	}

	@Override 
	public void paint(Graphics g)
	{
		super.paint(g);
		
		int W = getWidth ();
		int H = getHeight ();
		if (!showButtonDown) {
			if (imageButtonUp != null) {
				g.drawImage(imageButtonUp.getImage(), 0, 0, this);
			}
			g.setFont(buttonFont);
			g.setColor(fontColor);
			g.drawString(elementName, TextX, TextY);
		}
		if (showButtonDown) {
			if (imageButtonDown != null) {
				g.drawImage(imageButtonDown.getImage(), 0, 0, this);
			}
			g.setFont(buttonFont);
			g.setColor(fontColor);
			g.drawString(elementName, TextX+TextDX, TextY+TextDY);
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

	protected static byte SIZE_BUTTON_PARAMETERS = 21;
	protected static byte SIZE_OF_BUTTON_OBJECT = (byte) (SIZE_BUTTON_PARAMETERS + 2);
	protected static byte BUTTON_OBJECT_TYPE = 2;
	
	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
			LcdPageContainer pageContainer, LcdEibAddresses eibAddresses,
			LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
			DisplayProperties dor, LcdListenerContainer listener, LcdTimeoutContainer timeout, int myPage) {

		// create background image to support transparency
		BufferedImage localImageButtonUp = getElementBackground (backgroundComp, pageBackgroundColor);
		BufferedImage localImageButtonDown = getElementBackground (backgroundComp, pageBackgroundColor);
		
		// merge icon and text for button up onto background
		Graphics gbi = localImageButtonUp.createGraphics();
		if (imageButtonUp != null) {
			gbi.drawImage(imageButtonUp.getImage(), 0, 0, null);
		}
	    gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(elementName, TextX, TextY);	

		// merge icon and text for button down onto background
		gbi = localImageButtonDown.createGraphics();
		if (imageButtonDown != null) {
			gbi.drawImage(imageButtonDown.getImage(), 0, 0, null);
		}
		gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(elementName, TextX+TextDX, TextY+TextDY);
		
		// add image to image container
		int myImageIndex = 0;
		byte[] parameter = new byte [SIZE_BUTTON_PARAMETERS];
		// image index button up
		myImageIndex = imageContainer.addImage(localImageButtonUp, getWidth(), getHeight());
		parameter [1] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [0] = (byte) ((myImageIndex >> 0) & 0xff);
		// image index button down
		myImageIndex = imageContainer.addImage(localImageButtonDown, getWidth(), getHeight());
		parameter [3] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [2] = (byte) ((myImageIndex >> 0) & 0xff);
		// start pos X
		Point origin = dor.getElementOrigin(getXPos(), getYPos(), new Dimension (getWidth(), getHeight()));
		parameter [5] = (byte) ((origin.x >> 8) & 0xff);
		parameter [4] = (byte) ((origin.x >> 0) & 0xff);
		// start pos X
		parameter [7] = (byte) ((origin.y >> 8) & 0xff);
		parameter [6] = (byte) ((origin.y >> 0) & 0xff);
		// EIB Object #1
		parameter [8] = (byte) (eibAddresses.getAddrIndex(eibObj[0].getAddr()) & 0xff);
		// EIB Object #2
		if (eibObj[1] != null)
			parameter [9] = (byte) (eibAddresses.getAddrIndex(eibObj[1].getAddr()) & 0xff);
		else
			parameter [9] = parameter [8];
		// EIB Object value
		parameter [10] = (byte) buttonFunction.getButtonFunction();
		// sound information
		int mySoundIndex = soundContainer.addSound(soundIDup, sounds);
		parameter [12] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [11] = (byte) ((mySoundIndex >> 0) & 0xff);
		mySoundIndex = soundContainer.addSound(soundIDdown, sounds);
		parameter [14] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [13] = (byte) ((mySoundIndex >> 0) & 0xff);
		// check, if edit button is selected
		if (buttonFunction.getButtonFunction() == ButtonFunctionType.FUNCTION_DELTA_EIS6) {
			parameter [15] = (byte) Math.round(deltaSingle);
			parameter [16] = (byte) Math.round(deltaRepeat);
			Integer i = (int) maxValue;
			// store MAX value for edit function
			parameter [18] = (byte) ((i >> 8) & 0xff);
			parameter [17] = (byte)  (i       & 0xff);
			// store MIN value for edit function
			i = (int) minValue;
			// store MAX value for edit function
			parameter [20] = (byte) ((i >> 8) & 0xff);
			parameter [19] = (byte)  (i       & 0xff);
		}
		else if (buttonFunction.getButtonFunction() == ButtonFunctionType.FUNCTION_DELTA_EIS5) {
			parameter [15] = (byte) Math.round(10*deltaSingle);
			parameter [16] = (byte) Math.round(10*deltaRepeat);
			Integer i = (int) (maxValue * 10);
			// store MAX value for edit function
			parameter [18] = (byte) ((i >> 8) & 0xff);
			parameter [17] = (byte)  (i       & 0xff);
			// store MIN value for edit function
			i = (int) (minValue * 10);
			// store MAX value for edit function
			parameter [20] = (byte) ((i >> 8) & 0xff);
			parameter [19] = (byte)  (i       & 0xff);
		}
		else {
			// store value
			parameter [16] = (byte) ((buttonValue >> 8) & 0xff);
			parameter [15] = (byte) ((buttonValue >> 0) & 0xff);
		}
		// store element to current page
		pageContainer.addElement(SIZE_OF_BUTTON_OBJECT, BUTTON_OBJECT_TYPE, parameter);
	}

	@Override
	public void registerEibAddresses(LcdEibAddresses groupAddr) {
		groupAddr.addAddr(eibObj[0].getAddr());
		if (eibObj[1] != null)
			groupAddr.addAddr(eibObj[1].getAddr());
	}


	@Override
	public void changePageName(String oldName, String newName) {
		// this element has no page name		
	}


	@Override
	public boolean isPictureInUse(int id) {
		
		if (id == pictureIDdown) return true;
		if (id == pictureIDup) return true;

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
