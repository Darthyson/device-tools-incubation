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
public class ControlElementLED extends EIBComp implements 
							MouseMotionListener, MouseListener  {

    private Point lastPos = new Point (0,0);
	PropertiesTableModel inspectorModel = null;
	private boolean selected;
	private ImageIcon imageLedOff;
	private int pictureIDoff;
	private int pictureIDon;
	private int pictureIDwarning;
	private ImageIcon imageLedOn;
	private ImageIcon imageLedWarning;
	protected boolean showLedOn = false;
	private	int TextX;
	private int TextY;
	private int TextDX;
	private int TextDY;
	private Color fontColor;
	private Font buttonFont;
	private String textOff;
	private String textOn;
	private int soundIDdown;
	private int soundIDup;
	private int soundIDwarning;
	private int bitPosition;
	private LedFunctionType ledFunction;
	private LedVisibleType ledVisible;
	private int radioValue;
	private int soundRepetitions;
	
	public ControlElementLED(LcdEditor server, int x, int y, int w, int h, 
				char sAddr0, char sAddr1, int ledFunction, int pictureIDoff, int pictureIDon, 
				PictureLibrary pictures, int ledVisible, int soundIDup, int soundIDdown, SoundLibrary sounds) {

		super (server, x, y, w, h, "");
    	this.pictures = pictures;
    	this.sounds = sounds;
		eibObj[0] = new EIBObj (sAddr0);
		if (sAddr1 > 0) {
			eibObj[1] = new EIBObj (sAddr1);
		}
		bitPosition = 0;
		radioValue = 0;
		this.pictureIDoff = pictureIDoff;
		imageLedOff = getIcon (pictureIDoff);
		this.pictureIDon = pictureIDon;
		imageLedOn = getIcon (pictureIDon);
		imageLedWarning = null;
		pictureIDwarning = PictureLibrary.PICTURE_ID_NONE;
		imageLedWarning = null;
		if (imageLedOff != null) {
			setWidth (imageLedOff.getIconWidth());
			setHeight (imageLedOff.getIconHeight());
		}
		TextX = (int) getWidth ()/10;	TextY = (int) (0.6*getHeight());
		TextDX = 1; TextDY = 1;
		buttonFont = new Font ("Arial", Font.BOLD, 16);
		fontColor = Color.black;
		textOn = "";
		textOff = "";
		this.soundIDup = soundIDup;
		this.soundIDdown = soundIDdown;
		this.soundIDwarning = SoundLibrary.SOUND_ID_SILENT;
		this.soundRepetitions = 0;
		setUpElement ();
		showLedOn = false;
		this.ledFunction = new LedFunctionType (ledFunction);
		this.ledVisible = new LedVisibleType (ledVisible);
	}
	
	public ControlElementLED(LcdEditor server, int x, int y, int w, int h, 
			char sAddr0, char sAddr1, int ledFunction, int bitPosition, int radioValue, 
			int pictureIDoff, int pictureIDon, int pictureIDwarning,
			PictureLibrary pictures, int ledVisible, int soundIDup, int soundIDdown, int soundIDwarning, SoundLibrary sounds,
			Font buttonFont, Color fontColor, int TextX, int TextY, int TextDX, int TextDY, 
			String textOn, String textOff, int soundRepetitions) {

	super (server, x, y, w, h, "");
	this.pictures = pictures;
	this.sounds = sounds;
	eibObj[0] = new EIBObj (sAddr0);
	if (sAddr1 > 0) {
		eibObj[1] = new EIBObj (sAddr1);
	}
	this.ledFunction = new LedFunctionType (ledFunction);
	this.ledVisible = new LedVisibleType (ledVisible);
	this.bitPosition = bitPosition;
	this.radioValue = radioValue;
	this.pictureIDoff = pictureIDoff;
	imageLedOff = getIcon (pictureIDoff);
	this.pictureIDon = pictureIDon;
	imageLedOn = getIcon (pictureIDon);
	this.pictureIDwarning = pictureIDwarning;
	imageLedWarning = getIcon (pictureIDwarning);
	if (imageLedOff != null) {
		setWidth (imageLedOff.getIconWidth());
		setHeight (imageLedOff.getIconHeight());
	}
	this.TextX = TextX;	this.TextY = TextY;
	this.TextDX = TextDX; this.TextDY = TextDY;
	this.fontColor = fontColor;
	this.buttonFont = new Font (buttonFont.getFamily(), buttonFont.getStyle(), buttonFont.getSize());
	this.textOn = textOn;
	this.textOff = textOff;
	this.soundIDup = soundIDup;
	this.soundIDdown = soundIDdown;
	this.soundIDwarning = soundIDwarning;
	this.soundRepetitions = soundRepetitions;
	setUpElement ();
	showLedOn = false;
}

	public ControlElementLED(LcdEditor server, XMLStreamReader parser, 
				PictureLibrary pictures, SoundLibrary sounds) {
		super(server, parser, pictures, sounds);
	}

	@Override
	public EditorComponent getClone () {
		char sAddr1 = 0;
		if (eibObj[1] != null)
			sAddr1 = eibObj[1].getAddr();
		return new ControlElementLED ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
				eibObj[0].getAddr(), sAddr1, ledFunction.getLEDFunction(), bitPosition, radioValue, 
				pictureIDoff, pictureIDon, pictureIDwarning, pictures, ledVisible.getLEDVisible(), soundIDup, soundIDdown, soundIDwarning, sounds,
				buttonFont, fontColor, TextX, TextY, TextDX, TextDY, textOn, textOff, soundRepetitions);
	}

    @Override
	public void setUpElement() {
    	if (ledFunction == null)
    		ledFunction = new LedFunctionType(LedFunctionType.FUNCTION_BINARY);
    	if (ledVisible == null)
    		ledVisible = new LedVisibleType(LedVisibleType.LED_VISIBLE_ALWAYS);
		addMouseListener(this);
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
		setOpaque(false);
	}
	
	@Override
	public void writeXML(TransformerHandler hd) throws SAXException {
		
		AttributesImpl atts = new AttributesImpl();
		atts.clear();
		atts.addAttribute("","","X","CDATA",""+getX());
		atts.addAttribute("","","Y","CDATA",""+getY());
		atts.addAttribute("","","W","CDATA",""+getWidth());
		atts.addAttribute("","","H","CDATA",""+getHeight());
		if (eibObj[0] != null) {
			atts.addAttribute("","","ObjAddr0","CDATA",""+(0+eibObj[0].getAddr()));
			if (eibObj[0].init)
				atts.addAttribute("","","initObj0","CDATA","");
		}
		if (eibObj[1] != null) {
			atts.addAttribute("","","ObjAddr1","CDATA",""+(0+eibObj[1].getAddr()));
		}
		atts.addAttribute("","","LedFunction", "CDATA", ""+ledFunction.getLEDFunction());
		atts.addAttribute("","","LedVisible", "CDATA", ""+ledVisible.getLEDVisible());
		atts.addAttribute("","","BitPosition","CDATA",""+bitPosition);
		atts.addAttribute("","","RadioValue","CDATA",""+radioValue);
		atts.addAttribute("","","PictureOn","CDATA",""+pictureIDon);
		atts.addAttribute("","","PictureOff","CDATA",""+pictureIDoff);
//		if (pictureIDwarning != PictureLibrary.PICTURE_ID_NONE)
			atts.addAttribute("","","PictureWarning","CDATA",""+pictureIDwarning);
		atts.addAttribute("","","Font","CDATA",buttonFont.getFamily()+"-"+
				FontStyleCalc.getFontStyle(buttonFont.getStyle())+"-"+buttonFont.getSize());
		atts.addAttribute("","","FontColor","CDATA",""+fontColor.getRGB());
		atts.addAttribute("","","FontAlpha","CDATA",""+fontColor.getAlpha());
		atts.addAttribute("","","TextX","CDATA",""+TextX);
		atts.addAttribute("","","TextY","CDATA",""+TextY);
		atts.addAttribute("","","TextDX","CDATA",""+TextDX);
		atts.addAttribute("","","TextDY","CDATA",""+TextDY);
		atts.addAttribute("","","TextOn","CDATA",""+textOn);
		atts.addAttribute("","","TextOff","CDATA",""+textOff);
		atts.addAttribute("","","SoundUp","CDATA",""+soundIDup);
		atts.addAttribute("","","SoundDown","CDATA",""+soundIDdown);
		atts.addAttribute("","","SoundWarning","CDATA",""+soundIDwarning);
		atts.addAttribute("","","SoundRepetitions","CDATA",""+soundRepetitions);

		hd.startElement("","","LED",atts);
		hd.endElement("","","LED");
	}
	
    @Override
	protected void handleAttribute (XMLStreamReader parser, int i){
		// handler for derived objects
		if (parser.getAttributeLocalName( i ) == "LedFunction" ) {
			ledFunction = new LedFunctionType (Integer.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "LedVisible" ) {
			ledVisible = new LedVisibleType (Integer.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "BitPosition" ) {
			bitPosition = Integer.decode (parser.getAttributeValue( i ));
			if ((bitPosition < 0) || (bitPosition > 7))
				bitPosition = 0;
			return;
		}
		if (parser.getAttributeLocalName( i ) == "RadioValue" ) {
			radioValue = Integer.decode (parser.getAttributeValue( i ));
			if ((radioValue < 0) || (radioValue > 255))
				radioValue = 0;
			return;
		}
		if (parser.getAttributeLocalName( i ) == "PictureOff" ) {
			pictureIDoff = Integer.decode (parser.getAttributeValue( i ));
			imageLedOff = getIcon (pictureIDoff);
			return;
		}
		if (parser.getAttributeLocalName( i ) == "PictureOn" ) {
			pictureIDon = Integer.decode (parser.getAttributeValue( i ));
			imageLedOn = getIcon (pictureIDon);
			return;
		}
		if ((parser.getAttributeLocalName( i ) == "PictureAlarm" ) || 
			(parser.getAttributeLocalName( i ) == "PictureWarning" )) {
			pictureIDwarning = Integer.decode (parser.getAttributeValue( i ));
			imageLedWarning = getIcon (pictureIDwarning);
			return;
		}
		if (parser.getAttributeLocalName( i ) == "TextOn" ) {
			textOn = parser.getAttributeValue( i );
			return;
		}
		if (parser.getAttributeLocalName( i ) == "TextOff" ) {
			textOff = parser.getAttributeValue( i );
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
		if ((parser.getAttributeLocalName( i ) == "SoundAlarm" ) || 
			(parser.getAttributeLocalName( i ) == "SoundWarning" )) {
			soundIDwarning = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "SoundRepetitions" ) {
			soundRepetitions = Integer.decode (parser.getAttributeValue( i ));
			return;
		}

		super.handleAttribute (parser, i);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = (int)(e.getX() - lastPos.getX());
		int dy = (int)(e.getY() - lastPos.getY());
		((LcdEditor)myParent).moveAllSelected (dx, dy);
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
			showLedOn = true;
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
			showLedOn = false;
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
		return "LED '" + textOn + "' " + AddrTranslator.getAdrString (eibObj[0]) + " " + AddrTranslator.getAdrString (eibObj[1]);
	}

	@Override
	public void fillEditor (){
		((LcdEditor)myParent).flushInspector();
		// fill up inspector
		inspectorModel = ((LcdEditor)myParent).getInspector ();
		inspectorModel.setEibComp(this);
		// populate the window
    	Object[][] data = { 
    						{"text on",  textOn }, 
    						{"text off",  textOff }, 
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
    						{"listen", AddrTranslator.getAdrString (eibObj[0]) },
    						{"send", AddrTranslator.getAdrString (eibObj[1]) },
							{"function",  ledFunction }, 
							{"bit pos",  bitPosition }, 
							{"radio value",  radioValue }, 
							{"led off", pictures.getPictureNames (pictureIDoff) },
							{"led on", pictures.getPictureNames (pictureIDon) },
							{"warning on", pictures.getPictureNames (pictureIDwarning) },
							{"visible",  ledVisible}, 
							{"sound up", sounds.getSoundNames (soundIDup) },
							{"sound down", sounds.getSoundNames (soundIDdown) },
							{"warning sound", sounds.getSoundNames (soundIDwarning) },
							{"sound repetitions", soundRepetitions }
    					  };

    	((LcdEditor)myParent).setTable (data, "LED");
	}
	
	
	public boolean setNewValue (Object object, Object value) {

		((LcdEditor)myParent).objectPropertiesChanged ();

		if (object == "text on") {
			textOn = value.toString();
			repaint();
			return true;
		}
		if (object == "text off") {
			textOff = value.toString();
			return true;
		}

		if (object == "listen") {
			if (value.toString().isEmpty()) {
				return false;
			}
			eibObj[0].setAddr(AddrTranslator.getAdrValue ((String)value));
			return true;
		}

		if (object == "send") {
			if (value.toString().isEmpty()) {
				eibObj[1] = null;
				return true;
			}
			if (eibObj[1] == null)
			{
				eibObj[1] = new EIBObj (AddrTranslator.getAdrValue ((String)value));
			}
			else {
				eibObj[1].setAddr(AddrTranslator.getAdrValue ((String)value));
			}
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
		
		if (object == "bit pos") {
			int b = new Integer((String)value).intValue();
			if ((b < 0) || (b > 7)) return false;
			bitPosition = b;
			return true;
		}
		if (object == "radio value") {
			int b = new Integer((String)value).intValue();
			if ((b < 0) || (b > 255)) return false;
			radioValue = b;
			return true;
		}
	
		if (object == "function") {
			ledFunction = (LedFunctionType)value;
			return true;
		}
		
		if (object == "visible") {
			ledVisible = (LedVisibleType)value;
			return true;
		}

		if (object == "led off") {
			pictureIDoff = ((PictureNames)value).getSelectedPictureID();
			imageLedOff = getIcon (pictureIDoff);
			if (imageLedOff != null) {
				setIconDimension (imageLedOff);
			}
			repaint();
			return true;
		}
		
		if (object == "led on") {
			pictureIDon = ((PictureNames)value).getSelectedPictureID();
			imageLedOn = getIcon (pictureIDon);
			if (imageLedOn != null) {
				setIconDimension (imageLedOn);
			}
			repaint();
			return true;
		}
		if (object == "warning on") {
			pictureIDwarning = ((PictureNames)value).getSelectedPictureID();
			imageLedWarning = getIcon (pictureIDwarning);
			if (imageLedWarning != null) {
				setIconDimension (imageLedWarning);
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
		if (object == "warning sound") {
			soundIDwarning = ((SoundNames)value).getSelectedSoundID();
			return true;
		}
		if (object == "sound repetitions") {
			int b = new Integer((String)value).intValue();
			if ((b < 0) || (b > 127)) return false;
			soundRepetitions = b;
			return true;
		}
		
		return false;
	}

	@Override 
	public void paint(Graphics g)
	{
		super.paint(g);
		
		int W = getWidth ();
		int H = getHeight ();
		
		if (!showLedOn) {
			if (imageLedOff != null)
				g.drawImage(imageLedOff.getImage(), 0, 0, this);
			else {
				g.setColor(Color.darkGray);
				g.fillRect(0, 0, W, H);
			}
			g.setFont(buttonFont);
			g.setColor(fontColor);
			g.drawString(textOff, TextX, TextY);
		}
		if (showLedOn) {
			if (imageLedOn != null)
				g.drawImage(imageLedOn.getImage(), 0, 0, this);
			else {
				g.setColor(Color.lightGray);
				g.fillRect(0, 0, W, H);
			}
			g.setFont(buttonFont);
			g.setColor(fontColor);
			g.drawString(textOn, TextX + TextDX, TextY + TextDY);
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

	protected static byte SIZE_LED_PARAMETERS = 20;
	protected static byte SIZE_OF_LED_OBJECT = (byte) (SIZE_LED_PARAMETERS + 2); // +2 for type and size
	protected static byte IMAGE_OBJECT_TYPE = 3;
	// for parameters
	protected static byte LED_FLAG_SEND_ENABLE = 0x40;
	protected static byte LED_FUNCTION_RADIO = 0x20;
	protected static byte LED_FUNCTION_WARNING = 0x10;
	
	/* parameters for the always listening object to jump to page with warning element */
	protected static byte SIZE_WARNING_PARAMETERS = 2;
	protected static byte SIZE_OF_WARNING_OBJECT = (byte) (SIZE_WARNING_PARAMETERS + 2); // +2 for type and size
	protected static byte WARNING_OBJECT_TYPE = 3;

	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
			LcdPageContainer pageContainer, LcdEibAddresses eibAddresses, 
			LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
			DisplayProperties dor, LcdListenerContainer listener, LcdTimeoutContainer timeout, int myPage) {

		// create background image to support transparency
		BufferedImage localImageLedOff = getElementBackground (backgroundComp, pageBackgroundColor);
		BufferedImage localImageLedOn = getElementBackground (backgroundComp, pageBackgroundColor);
		BufferedImage localImageLedWarning = getElementBackground (backgroundComp, pageBackgroundColor);
		
		// merge icon and text for button up onto background
		Graphics gbi = localImageLedOff.createGraphics();
		if (imageLedOff != null) {
			gbi.drawImage(imageLedOff.getImage(), 0, 0, null);
		}
	    gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(textOff, TextX, TextY);	

		// merge icon and text for button down onto background
		gbi = localImageLedOn.createGraphics();
		if (imageLedOn != null) {
			gbi.drawImage(imageLedOn.getImage(), 0, 0, null);
		}
		gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(textOn, TextX+TextDX, TextY+TextDY);

		// merge icon and text for button down onto background
		gbi = localImageLedWarning.createGraphics();
		if (imageLedWarning != null) {
			gbi.drawImage(imageLedWarning.getImage(), 0, 0, null);
		}
		gbi.setFont(buttonFont);
	    gbi.setColor(fontColor);
	    gbi.drawString(textOn, TextX+TextDX, TextY+TextDY);
	    
		// add image to image container
		int myImageIndex = 0;
		byte[] parameter = new byte [SIZE_LED_PARAMETERS];
		// image index LED off
		if ((ledVisible.getLEDVisible() == LedVisibleType.LED_VISIBLE_ALWAYS) || 
				(ledVisible.getLEDVisible() == LedVisibleType.LED_VISIBLE_OFF)) { 
			myImageIndex = imageContainer.addImage(localImageLedOff, getWidth(), getHeight());
			parameter [1] = (byte) ((myImageIndex >> 8) & 0xff);
			parameter [0] = (byte) ((myImageIndex >> 0) & 0xff);
		}
		else {
			parameter [1] = (byte) 0xff;
			parameter [0] = (byte) 0xff;
		}
		// image index LED on
		if ((ledVisible.getLEDVisible() == LedVisibleType.LED_VISIBLE_ALWAYS) || 
				(ledVisible.getLEDVisible() == LedVisibleType.LED_VISIBLE_ON)) { 
			myImageIndex = imageContainer.addImage(localImageLedOn, getWidth(), getHeight());
			parameter [3] = (byte) ((myImageIndex >> 8) & 0xff);
			parameter [2] = (byte) ((myImageIndex >> 0) & 0xff);
			// image index LED warning
			if (ledFunction.getLEDFunction() == LedFunctionType.FUNCTION_WARNING) {
				myImageIndex = imageContainer.addImage(localImageLedWarning, getWidth(), getHeight());
			}
			parameter [5] = (byte) ((myImageIndex >> 8) & 0xff);
			parameter [4] = (byte) ((myImageIndex >> 0) & 0xff);
		}
		else {
			// on
			parameter [3] = (byte) 0xff;
			parameter [2] = (byte) 0xff;
			// warning
			parameter [5] = (byte) 0xff;
			parameter [4] = (byte) 0xff;
		}
		// start pos x
		Point origin = dor.getElementOrigin(getXPos(), getYPos(), new Dimension (getWidth(), getHeight()));
		parameter [7] = (byte) ((origin.x >> 8) & 0xff);
		parameter [6] = (byte) ((origin.x >> 0) & 0xff);
		// start pos y
		parameter [9] = (byte) ((origin.y >> 8) & 0xff);
		parameter [8] = (byte) ((origin.y >> 0) & 0xff);
		// EIB Object # for listen
		parameter [10] = (byte) (eibAddresses.getAddrIndex(eibObj[0].getAddr()) & 0xff);
		// EIB Object # for send
		parameter [12] = (byte) bitPosition;
		if (eibObj[1] != null) {
			parameter [11] = (byte) (eibAddresses.getAddrIndex(eibObj[1].getAddr()) & 0xff);
			parameter [12] |= LED_FLAG_SEND_ENABLE;
		}
		parameter [13] = (byte)0x00;
		if (ledFunction.getLEDFunction() == LedFunctionType.FUNCTION_RADIO) {
			parameter [12] |= LED_FUNCTION_RADIO;
			parameter [13] = (byte)radioValue;
		}
		if (ledFunction.getLEDFunction() == LedFunctionType.FUNCTION_WARNING) {
			parameter [12] |= LED_FUNCTION_WARNING;
			parameter [13] = (byte)soundRepetitions;
		}
		int mySoundIndex = soundContainer.addSound(soundIDup, sounds);
		parameter [15] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [14] = (byte) ((mySoundIndex >> 0) & 0xff);
		mySoundIndex = soundContainer.addSound(soundIDdown, sounds);
		parameter [17] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [16] = (byte) ((mySoundIndex >> 0) & 0xff);
		mySoundIndex = soundContainer.addSound(soundIDwarning, sounds);
		parameter [19] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [18] = (byte) ((mySoundIndex >> 0) & 0xff);

		pageContainer.addElement(SIZE_OF_LED_OBJECT, IMAGE_OBJECT_TYPE, parameter);
		
		/* add auto-jump to page containing the warning element */
		if (ledFunction.getLEDFunction() == LedFunctionType.FUNCTION_WARNING) {
			parameter = new byte [SIZE_WARNING_PARAMETERS];
			// EIB Object # for listen
			parameter [0] = (byte) (eibAddresses.getAddrIndex(eibObj[0].getAddr()) & 0xff);
			// destination page
			parameter [1] = (byte) (myPage & 0xff);
			listener.addElement(SIZE_OF_WARNING_OBJECT, WARNING_OBJECT_TYPE, parameter);
		}
	}

	@Override
	public void registerEibAddresses(LcdEibAddresses groupAddr) {
		groupAddr.addAddr(eibObj[0].getAddr());
		if (eibObj[1] != null)
			groupAddr.addAddr(eibObj[1].getAddr());
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// this function is not required		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// this function is not required		
	}

	@Override
	public void changePageName(String oldName, String newName) {
		// this function is not required		
	}
	
	@Override
	public boolean isPictureInUse(int id) {
		
		if (id == pictureIDoff) return true;
		if (id == pictureIDon) return true;
		if (id == pictureIDwarning) return true;

		return false;
	}
	
	@Override
	public boolean isSoundInUse(int id) {
		
		if (id == soundIDdown) return true;
		if (id == soundIDup) return true;
		if (id == soundIDwarning) return true;

		return false;
	}
	
	@Override
	public boolean isPageNameUsed(String name) {
		return false;
	}

}
