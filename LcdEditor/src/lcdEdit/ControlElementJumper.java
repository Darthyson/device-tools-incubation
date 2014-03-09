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
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


@SuppressWarnings("serial")
public class ControlElementJumper extends EIBComp implements 
						MouseMotionListener, MouseListener {

    private Point lastPos = new Point (0,0);
	PropertiesTableModel inspectorModel = null;
	private boolean selected;
	private ImageIcon imageButtonUp;
	private ImageIcon imageButtonDown;
	private int pictureIDdown;
	private int pictureIDup;
	protected boolean showButtonDown = false;
	private	int TextX;
	private int TextY;
	private int TextDX;
	private int TextDY;
	private Color fontColor;
	private Font buttonFont;
	private int frameWidth;
	private Color frameColorUp;
	private Color frameColorDown;
	private Color elementColorUp;
	private Color elementColorDown;
	private Boolean showColorUp;
	private Boolean showColorDown;
	private int alphaUp;
	private int alphaDown;
	private int soundIDdown;
	private int soundIDup;
	private int autoJumpDelay;
	
	public ControlElementJumper(LcdEditor server, 
				int x, int y, int w, int h, String name, int pictureIDup, int pictureIDdown,
					PictureLibrary pictures, int soundIDup, int soundIDdown, SoundLibrary sounds,
					Font buttonFont, Color fontColor, int TextX, int TextY, int TextDX, int TextDY, boolean hideText,
					int frameWidth, Color frameColorUp, Color frameColorDown, Color elementColorUp, Color elementColorDown,
					Boolean showColorUp, Boolean showColorDown, int alphaUp, int alphaDown, int autoJumpDelay) {

		super (server, x, y, w, h, name);

		this.pictures = pictures;
    	this.sounds = sounds;
		this.pictureIDup = pictureIDup;
		imageButtonUp = getIcon (pictureIDup);
		this.pictureIDdown = pictureIDdown;
		imageButtonDown = getIcon (pictureIDdown);
		if (imageButtonUp != null) {
			setIconDimension (imageButtonUp);
		}
		this.soundIDup = soundIDup;
		this.soundIDdown = soundIDdown;
		
		this.TextX = TextX;	this.TextY = TextY;
		this.TextDX = TextDX; this.TextDY = TextDY;
		this.fontColor = fontColor;
		this.buttonFont = new Font (buttonFont.getFamily(), buttonFont.getStyle(), buttonFont.getSize());
		this.hideText = hideText;
		this.autoJumpDelay = autoJumpDelay;
		
		this.frameWidth = frameWidth;
		this.frameColorUp = frameColorUp;
		this.frameColorDown = frameColorDown;
		this.elementColorUp = elementColorUp;
		this.elementColorDown = elementColorDown;
		this.showColorUp = showColorUp;
		this.showColorDown = showColorDown;
		this.alphaUp = alphaUp;
		this.alphaDown = alphaDown;
		
		setUpElement();
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		showButtonDown = false;
	}
	public ControlElementJumper(LcdEditor server, 
			int x, int y, int w, int h, String name, int pictureIDup, int pictureIDdown,
				PictureLibrary pictures, int soundIDup, int soundIDdown, SoundLibrary sounds) {

	super (server, x, y, w, h, name);
	this.pictures = pictures;
	this.sounds = sounds;
	this.pictureIDup = pictureIDup;
	imageButtonUp = getIcon (pictureIDup);
	this.pictureIDdown = pictureIDdown;
	imageButtonDown = getIcon (pictureIDdown);
	if (imageButtonUp != null) {
		setIconDimension (imageButtonUp);
	}
	this.soundIDup = soundIDup;
	this.soundIDdown = soundIDdown;
	TextX = (int) getWidth()/10;	TextY = (int) (0.6*getHeight());
	TextDX = 1; TextDY = 1;
	hideText = false;
	buttonFont = new Font ("Arial", Font.BOLD, 16);
	fontColor = Color.black;
	frameWidth = 0;
	frameColorUp = Color.gray;
	frameColorDown = Color.white;
	elementColorUp = Color.gray;
	elementColorDown = Color.darkGray;
	showColorUp = false;
	showColorDown = false;
	alphaUp = 0;
	alphaDown = 0;
	autoJumpDelay = 0;
	setUpElement();
	addMouseMotionListener(this);
	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	showButtonDown = false;
}

	public ControlElementJumper(LcdEditor server, XMLStreamReader parser, 
					PictureLibrary pictures, SoundLibrary sounds) {
		super(server, parser, pictures, sounds);
		if (frameColorUp == null) 
			frameColorUp = Color.gray;
		if (frameColorDown == null) 
			frameColorDown = Color.white;
		if (elementColorUp == null) 
			elementColorUp = Color.gray;
		if (elementColorDown == null) 
			elementColorDown = Color.darkGray;
		if (showColorUp == null) 
			showColorUp = false;
		if (showColorDown == null) 
			showColorDown = false;
		addMouseMotionListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public EditorComponent getClone () {
		return new ControlElementJumper ((LcdEditor)myParent, getX()+5, getY()+5, getWidth(), getHeight(), 
										elementName, pictureIDup, pictureIDdown, pictures, soundIDup, soundIDdown, sounds,
										buttonFont, fontColor, TextX, TextY, TextDX, TextDY, hideText,
										frameWidth, frameColorUp, frameColorDown, elementColorUp, elementColorDown,
										showColorUp, showColorDown, alphaUp, alphaDown, autoJumpDelay);
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
		atts.addAttribute("","","PictureUp","CDATA",""+pictureIDup);
		atts.addAttribute("","","PictureDown","CDATA",""+pictureIDdown);
		atts.addAttribute("","","Font","CDATA",buttonFont.getFamily()+"-"+
				FontStyleCalc.getFontStyle(buttonFont.getStyle())+"-"+buttonFont.getSize());
		atts.addAttribute("","","FontColor","CDATA",""+fontColor.getRGB());
		atts.addAttribute("","","FontAlpha","CDATA",""+fontColor.getAlpha());
		atts.addAttribute("","","TextX","CDATA",""+TextX);
		atts.addAttribute("","","TextY","CDATA",""+TextY);
		atts.addAttribute("","","TextDX","CDATA",""+TextDX);
		atts.addAttribute("","","TextDY","CDATA",""+TextDY);
		if (hideText)
			atts.addAttribute("","","HideText","CDATA","");
		atts.addAttribute("","","FrameWidth","CDATA",""+frameWidth);
		atts.addAttribute("","","FrameColorUp","CDATA",""+frameColorUp.getRGB());
		atts.addAttribute("","","FrameAlphaUp","CDATA",""+frameColorUp.getAlpha());
		atts.addAttribute("","","FrameColorDown","CDATA",""+frameColorDown.getRGB());
		atts.addAttribute("","","FrameAlphaDown","CDATA",""+frameColorDown.getAlpha());
		if (showColorUp)
			atts.addAttribute("","","ShowColorUp","CDATA","");
		if (showColorDown)
			atts.addAttribute("","","ShowColorDown","CDATA","");
		atts.addAttribute("","","ElementColorUp","CDATA",""+elementColorUp.getRGB());
		atts.addAttribute("","","ElementAlphaUp","CDATA",""+elementColorUp.getAlpha());
		atts.addAttribute("","","ElementColorDown","CDATA",""+elementColorDown.getRGB());
		atts.addAttribute("","","ElementAlphaDown","CDATA",""+elementColorDown.getAlpha());
		atts.addAttribute("","","AlphaUp","CDATA",""+alphaUp);
		atts.addAttribute("","","AlphaDown","CDATA",""+alphaDown);
		atts.addAttribute("","","SoundUp","CDATA",""+soundIDup);
		atts.addAttribute("","","SoundDown","CDATA",""+soundIDdown);
		atts.addAttribute("","","AutoJumpDelay","CDATA",""+autoJumpDelay);
		
		hd.startElement("","","JMP",atts);
		hd.endElement("","","JMP");
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
		if (parser.getAttributeLocalName( i ) == "HideText" ) {
			hideText = true;
			return;
		}
		if (parser.getAttributeLocalName( i ) == "FrameWidth" ) {
			frameWidth = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "FrameColorUp" ) {
			frameColorUp = setColorKeepAlpha (frameColorUp, Color.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "FrameAlphaUp" ) {
			frameColorUp = setAlphaKeepColor (frameColorUp, Integer.decode(parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "FrameColorDown" ) {
			frameColorDown = setColorKeepAlpha (frameColorDown, Color.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "FrameAlphaDown" ) {
			frameColorDown = setAlphaKeepColor (frameColorDown, Integer.decode(parser.getAttributeValue( i )));
			return;
		}

		if (parser.getAttributeLocalName( i ) == "ShowColorUp" ) {
			showColorUp = true;
			return;
		}
		if (parser.getAttributeLocalName( i ) == "ShowColorDown" ) {
			showColorDown = true;
			return;
		}
		if (parser.getAttributeLocalName( i ) == "ElementColorUp" ) {
			elementColorUp = setColorKeepAlpha (elementColorUp, Color.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "ElementAlphaUp" ) {
			elementColorUp = setAlphaKeepColor (elementColorUp, Integer.decode(parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "ElementColorDown" ) {
			elementColorDown = setColorKeepAlpha (elementColorDown, Color.decode (parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "ElementAlphaDown" ) {
			elementColorDown = setAlphaKeepColor (elementColorDown, Integer.decode(parser.getAttributeValue( i )));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "AlphaUp" ) {
			alphaUp = Integer.decode (parser.getAttributeValue( i ));
			return;
		}
		if (parser.getAttributeLocalName( i ) == "AlphaDown" ) {
			alphaDown = Integer.decode (parser.getAttributeValue( i ));
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
		if (parser.getAttributeLocalName( i ) == "AutoJumpDelay" ) {
			autoJumpDelay = Integer.decode (parser.getAttributeValue( i ));
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
			((LcdEditor)myParent).selectPage (elementName);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getModifiers() != 16) return;
		fillEditor();
	}	

	@Override
	public String toString () {
		return "Jumper '" + elementName + "'";
	}
	
	@Override
	public void fillEditor (){
		((LcdEditor)myParent).flushInspector();
		// fill up inspector
		inspectorModel = ((LcdEditor)myParent).getInspector ();
		inspectorModel.setEibComp(this);
		
		PictureLibrary pictures = ((LcdEditor)myParent).getPictureLibrary();
		// populate the window
    	Object[][] data = { 
							{"target", new PageNames ( ((LcdEditor)myParent).getPageNames(), elementName) }, 
							{"hide",  hideText },
							{"auto jump",  autoJumpDelay },
							{"width",  Math.round(getWidth()) }, 
							{"height",  Math.round(getHeight()) }, 
							{"x-pos",  getX() }, 
							{"y-pos",  getY() }, 
							{"font",  buttonFont }, 
							{"font color",  fontColor }, 
							{"Text xpos",  TextX }, 
							{"Text ypos",  TextY }, 
							{"Text dx",  TextDX }, 
							{"Text dy",  TextDY }, 
							{"btn up", pictures.getPictureNames (pictureIDup) },
							{"btn down", pictures.getPictureNames (pictureIDdown) },
							{"frame width", frameWidth },
							{"frame color up", frameColorUp },
							{"frame color down", frameColorDown },
							{"show color up", showColorUp },
							{"color up", elementColorUp },
							{"show color down", showColorDown },
							{"color down", elementColorDown },
							{"alpha up", alphaUp },
							{"alpha down", alphaDown },
							{"sound up", sounds.getSoundNames (soundIDup) },
							{"sound down", sounds.getSoundNames (soundIDdown) }
    					  };

    	((LcdEditor)myParent).setTable (data, "Jumper");
	}
	
	
	public boolean setNewValue (Object object, Object value) {
		
		((LcdEditor)myParent).objectPropertiesChanged ();

		if (object == "target") {
			elementName = (String)value.toString();
			repaint();
			return true;
		}

		if (object == "auto jump") {
			int d = new Integer((String)value).intValue();
			if ((d < 0) || (d > 255))  return false;
			autoJumpDelay = d;
			return true;
		}

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
		
		if (object == "width") {
			int W = new Integer((String)value).intValue();
			setWidth (W);
			return true;
		}
		if (object == "height") {
			int H = new Integer((String)value).intValue();
			setHeight(H);
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

		if (object == "font color") {
			fontColor = (Color) value;
			repaint();
			return true;
		}
		if (object == "btn up") {
			pictureIDup = ((PictureNames)value).getSelectedPictureID();
			imageButtonUp = getIcon (pictureIDup);
			if (imageButtonUp != null) {
				setWidth (imageButtonUp.getIconWidth());
				setHeight (imageButtonUp.getIconHeight());
			}
			repaint();
			return true;
		}

		if (object == "btn down") {
			pictureIDdown = ((PictureNames)value).getSelectedPictureID();
			imageButtonDown = getIcon (pictureIDdown);
			if (imageButtonDown != null) {
				setWidth (imageButtonDown.getIconWidth());
				setHeight (imageButtonDown.getIconHeight());
			}
			repaint();
			return true;
		}
		if (object == "hide") {
			hideText = (Boolean)value;
			repaint();
			return true;
		}
		if (object == "frame width") {
			int w = new Integer((String)value).intValue();
			if ((w < 0) || (w > 10)) return false;
			frameWidth = w;
			repaint();
			return true;
		}
		if (object == "frame color up") {
			frameColorUp = (Color) value;
			repaint();
			return true;
		}
		if (object == "frame color down") {
			frameColorDown = (Color) value;
			return true;
		}
		if (object == "show color up") {
			showColorUp = (Boolean)value;
			repaint();
			return true;
		}
		if (object == "color up") {
			elementColorUp = (Color) value;
			repaint();
			return true;
		}
		if (object == "show color down") {
			showColorDown = (Boolean)value;
			return true;
		}
		if (object == "color down") {
			elementColorDown = (Color) value;
			return true;
		}
		if (object == "alpha up") {
			int a = new Integer((String)value).intValue();
			if ((a < -100) || (a > 100)) return false;
			alphaUp = a;
			repaint();
			return true;
		}
		if (object == "alpha down") {
			int a = new Integer((String)value).intValue();
			if ((a < -100) || (a > 100)) return false;
			alphaDown = a;
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
	
	public void paint_button_up (Graphics g) {

		int W = getWidth ();
		int H = getHeight();

		if (imageButtonUp != null)
			g.drawImage(alphaMultiply ((BufferedImage)imageButtonUp.getImage(), alphaUp), 0, 0, this);
		if (showColorUp) {
			g.setColor(elementColorUp);
			g.fillRect(0, 0, W, H);
		}
		if (frameWidth >0) {
			g.setColor(frameColorUp);
			for (int i = 0; i < frameWidth; i++)
				g.drawRect(i, i, W-2*i-1, H-2*i-1);
		}
		if (!hideText) {
			g.setFont(buttonFont);
			g.setColor(fontColor);
			g.drawString(elementName, TextX, TextY);
		}
	}
	
	public void paint_button_down (Graphics g) {

		int W = getWidth ();
		int H = getHeight();

		if (imageButtonDown != null) {
			g.drawImage(alphaMultiply ((BufferedImage)imageButtonDown.getImage(), alphaDown), 0, 0, this);
		}
		if (showColorDown)	{
			g.setColor(elementColorDown);
			g.fillRect(0, 0, W, H);
		}
		if (frameWidth >0) {
			g.setColor(frameColorDown);
			for (int i = 0; i < frameWidth; i++)
				g.drawRect(i, i, W-2*i-1, H-2*i-1);
		}
		if (!hideText) {
			g.setFont(buttonFont);
			g.setColor(fontColor);
			g.drawString(elementName, TextX+TextDX, TextY+TextDY);
		}
	}
	
	@Override 
	public void paint(Graphics g)
	{
		super.paint(g);
		
		int W = getWidth ();
		int H = getHeight();
		
		if (!showButtonDown) {
			paint_button_up (g);
		}
		if (showButtonDown) {
			paint_button_down (g);
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

	protected static byte SIZE_JUMPER_PARAMETERS = 14;
	protected static byte SIZE_OF_JUMPER_OBJECT = (byte) (SIZE_JUMPER_PARAMETERS + 2);
	protected static byte IMAGE_OBJECT_TYPE = 1;
	
	@Override
	public void outputToLcdFile(LcdImageContainer imageContainer,
			LcdPageContainer pageContainer, LcdEibAddresses eibAddresses,
			LcdSoundContainer soundContainer, Component[] backgroundComp, Color pageBackgroundColor,
			DisplayProperties dor, LcdListenerContainer listener, int myPage) {
		// create background image to support transparency
		BufferedImage localImageButtonUp = getElementBackground (backgroundComp, pageBackgroundColor);
		BufferedImage localImageButtonDown = getElementBackground (backgroundComp, pageBackgroundColor);
		// merge icon and text for button up onto background
		Graphics gbi = localImageButtonUp.createGraphics();
		paint_button_up (gbi);
		// merge icon and text for button down onto background
		gbi = localImageButtonDown.createGraphics();
		paint_button_down (gbi);
		// add image to image container
		int myImageIndex = 0;
		byte[] parameter = new byte [SIZE_JUMPER_PARAMETERS];
		// image index button up
		myImageIndex = imageContainer.addImage(localImageButtonUp, getWidth(), getHeight());
		parameter [1] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [0] = (byte) ((myImageIndex >> 0) & 0xff);
		// image index button down
		myImageIndex = imageContainer.addImage(localImageButtonDown, getWidth(), getHeight());
		parameter [3] = (byte) ((myImageIndex >> 8) & 0xff);
		parameter [2] = (byte) ((myImageIndex >> 0) & 0xff);
		// start pos x
		Point origin = dor.getElementOrigin(getXPos(), getYPos(), new Dimension (getWidth(), getHeight()));
		parameter [5] = (byte) ((origin.x >> 8) & 0xff);
		parameter [4] = (byte) ((origin.x >> 0) & 0xff);
		// start pos y
		parameter [7] = (byte) ((origin.y >> 8) & 0xff);
		parameter [6] = (byte) ((origin.y >> 0) & 0xff);
		// Target page
		int myTargetPage = ((LcdEditor) myParent).getPageIndex (elementName);
		parameter [8] = (byte) myTargetPage;
		// add sound
		int mySoundIndex = soundContainer.addSound(soundIDup, sounds);
		parameter [10] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [9] = (byte) ((mySoundIndex >> 0) & 0xff);
		mySoundIndex = soundContainer.addSound(soundIDdown, sounds);
		parameter [12] = (byte) ((mySoundIndex >> 8) & 0xff);
		parameter [11] = (byte) ((mySoundIndex >> 0) & 0xff);
		parameter [13] = (byte) autoJumpDelay;
		// store element to current page
		pageContainer.addElement(SIZE_OF_JUMPER_OBJECT, IMAGE_OBJECT_TYPE, parameter);
	}

	@Override
	public void registerEibAddresses(LcdEibAddresses groupAddr) {
		// this function is not required		
	}
	
	@Override
	public void changePageName(String oldName, String newName) {
		// check, if this jumper refers to the page name to be changed
		if (elementName.endsWith(oldName)) {
			elementName = newName;
		}
		
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
		return elementName.equals(name);
	}


}
