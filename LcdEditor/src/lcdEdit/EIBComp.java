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
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamReader;

	/**
	 * @author Arno Stock
	 *
	 */
	@SuppressWarnings("serial")
	public abstract class EIBComp extends JPanel implements EditorComponent {

		// These variables are commonly used across all elements
		protected Object myParent;
		protected EIBObj[] eibObj = {	null, null, null, null, null,
										null, null, null, null, null	};
	    protected int state; // 0=off, 1=on, overwritten by attribute "V"
		protected String elementName;
		protected PictureLibrary pictures;
		protected SoundLibrary sounds;
		protected boolean hideText;
		
		public EIBComp() {
		}

		public EIBComp(LcdEditor myParent, int x, int y, int w, int h, String name) {
			
	    	this.myParent = myParent;
	    	setXPos (x); setYPos (y);
	    	setWidth (w); setHeight (h);
			elementName = name;
			
		}
		
		public EIBComp(LcdEditor parent, XMLStreamReader parser, 
				PictureLibrary pictures, SoundLibrary sounds) {
			boolean processed = false;
			myParent = parent;
			this.pictures = pictures;
			this.sounds = sounds;
			for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
				processed = false;

				if (parser.getAttributeLocalName( i ) == "X" ) {
					int X = Integer.decode (parser.getAttributeValue( i ));
					setXPos (X);
					processed = true;
				}
				if (parser.getAttributeLocalName( i ) == "Y" ) {
					int Y = Integer.decode (parser.getAttributeValue( i ));
					setYPos (Y);
					processed = true;
				}
				if (parser.getAttributeLocalName( i ) == "W" ) {
					int W = Integer.decode (parser.getAttributeValue( i ));
					setWidth (W);
					processed = true;
				}
				if (parser.getAttributeLocalName( i ) == "H" ) {
					int H = Integer.decode (parser.getAttributeValue( i ));
					setHeight (H);
					processed = true;
				}
				if (parser.getAttributeLocalName( i ) == "F" ) {
					state = Integer.decode (parser.getAttributeValue( i ));
					processed = true;
				}
				
				if (parser.getAttributeLocalName( i ).matches("ObjAddr\\d")) {
					int o = Integer.decode (parser.getAttributeLocalName( i ).substring(7));
					char Addr = (char) (Integer.decode (parser.getAttributeValue( i )) &0xFFFF);
					eibObj[o] = new EIBObj (Addr);
					processed = true;
				}
				if (parser.getAttributeLocalName( i ).matches("initObj\\d")) {
					int o = Integer.decode (parser.getAttributeLocalName( i ).substring(7));
					eibObj[o].init = true;
					processed = true;
				}
				if (parser.getAttributeLocalName( i ) == "Name" ) {
					elementName = parser.getAttributeValue( i );
					processed = true;
				}
				
				if (!processed)
					handleAttribute (parser, i);
		    }
			setUpElement ();
		}
		
		public void setUpElement() {
	    	((FlowLayout)getLayout()).setHgap( 0 );
	    	((FlowLayout)getLayout()).setVgap( 0 );
		}

	    protected JButton makeButton (JPanel element, String Caption, int w, int h){
	    	JButton btn = new JButton (Caption);
			btn.setPreferredSize(new Dimension (w, h));
			btn.setMargin(new Insets (0,0,0,0));
			add (btn);
			if (element != null)
				btn.addMouseListener((MouseListener) element);
			return btn;
	    }
		
		void new_msg (char addr, int len, byte[] data) {
			return;
		}

		public boolean setNewValue (Object object, Object value) {
			return true;
		}

		protected void handleAttribute (XMLStreamReader parser, int i){
			// handler for derived objects
			System.out.println ("?"+parser.getAttributeLocalName( i ));
		}
		
		protected ImageIcon getIcon (int pictureID) {

			if (pictureID == -1)
				return null;
			PictureObject picture = pictures.getPictureFromLibrary(pictureID);
			if (picture != null) {
				return picture.getPicture();
			}
			System.err.println ("ControlElementPicture can't refer picure ID " + pictureID);
			return null;
		}
			
		@Override
		public int getXPos() {
			Point p = getLocation();
			return p.x;
		}
		@Override
		public void setXPos(int x) {
			if (x < 0) x = 0;
			if (x + getWidth() > ((LcdEditor)myParent).getMaxX ()) x = ((LcdEditor)myParent).getMaxX () - getWidth();
			setLocation (x, getY() );
			repaint();
		}
		@Override
		public int getYPos() {
			Point p = getLocation();
			return p.y;
		}
		@Override
		public void setYPos(int y) {
			if (y < 0) y = 0;
			if (y + getHeight() > ((LcdEditor)myParent).getMaxY ()) y = ((LcdEditor)myParent).getMaxY ();
			setLocation (getX(), y );
			repaint();
		}

		public void setWidth (int w) {
			setSize ( w, getHeight());
		}
		
		public void setHeight (int h) {
			setSize ( getWidth(), h);
		}
		
		@Override
		public int getWidth() {
			return getBounds().width;
		}
		
		@Override
		public int getHeight() {
			return getBounds().height;
		}
		
		public void setIconDimension (ImageIcon img) {
			setWidth (img.getIconWidth());
			setHeight (img.getIconHeight());
		}
		
		@Override
		public void setLocation (Point p) {
			if (p.x < 0) p.x = 0;
			if (p.x + getWidth() > ((LcdEditor)myParent).getMaxX ()) p.x = ((LcdEditor)myParent).getMaxX () - getWidth();
			if (p.y < 0) p.y = 0;
			if (p.y + getHeight() > ((LcdEditor)myParent).getMaxY ()) p.y = ((LcdEditor)myParent).getMaxY () - getHeight();
			super.setLocation (p);
		}
		public boolean checkLocation (Point p) {
			if (p.x < 0) return false;
			if (p.x + getWidth() > ((LcdEditor)myParent).getMaxX ()) return false;
			if (p.y < 0) return false;
			if (p.y + getHeight() > ((LcdEditor)myParent).getMaxY ()) return false;
			return true;
		}
		public boolean checkLocation (int x, int y) {
			Point p = new Point (x, y);
			return checkLocation (p);
		}
		
		static BufferedImage deepCopy(BufferedImage bi) {
			 ColorModel cm = bi.getColorModel();
			 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			 WritableRaster raster = bi.copyData(null);
			 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
		
		public BufferedImage getElementBackground (Component[] backgroundComp, Color pageBackgroundColor) {
		
		    BufferedImage backgroundImage = new BufferedImage(((LcdEditor)myParent).getMaxX (), ((LcdEditor)myParent).getMaxY (), BufferedImage.TYPE_INT_RGB);
		    Graphics gbi = backgroundImage.createGraphics();
		    // fill with page background color
			gbi.setColor(pageBackgroundColor);
			gbi.fillRect(0, 0, ((LcdEditor)myParent).getMaxX (), ((LcdEditor)myParent).getMaxY ());
		    // create Image with all background components
			for ( Component thisComp : backgroundComp ) {
					  if (EIBComp.class.isInstance (thisComp)) {
						  EIBComp co = (EIBComp)thisComp;
						  Graphics cog = gbi.create (co.getXPos(), co.getYPos(), co.getWidth(), co.getHeight());
						  co.paint (cog);
						  cog.dispose();
					  }
			}
			gbi.dispose();
			return backgroundImage.getSubimage(getXPos(), getYPos(), getWidth(), getHeight());
		}
		
		/**
		 * @param c : RGB color information
		 * @param a : new alpha channel information
		 * @return new color object of old RGB information and new alpha value
		 */
		public Color setAlphaKeepColor (Color c, Integer a) {
			if (c == null)
				c = Color.BLACK;
			return new Color (c.getRed(), c.getGreen(), c.getBlue(), a);

		}

		
		/**
		 * @param c new RGB color information
		 * @param n old color information with alpha value
		 * @return color object of new RGB and old alpha information
		 */
		public Color setColorKeepAlpha (Color c, Color n) {
			if (c == null)
				c = Color.BLACK;
			return new Color (n.getRed(), n.getGreen(), n.getBlue(), c.getAlpha());

		}
		
		public BufferedImage alphaMultiply (BufferedImage b, int a) {
			BufferedImage bmp = deepCopy (b);
			for (int x = 0; x < bmp.getWidth(); x++)
				for (int y = 0; y < bmp.getHeight(); y++) {
					int rgb = bmp.getRGB(x, y);
					int red = (int) (((rgb >> 16) & 0xff)   * ((100.0 + a)/100.0));
					if (red > 0xff) red = 0xff;
					int green = (int) (((rgb >>  8) & 0xff) * ((100.0 + a)/100.0));
					if (green > 0xff) green = 0xff;
					int blue = (int) (((rgb >>  0) & 0xff)  * ((100.0 + a)/100.0));
					if (blue > 0xff) blue = 0xff;
					int alpha = ((rgb >> 24) & 0xff);
					rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
					bmp.setRGB(x, y, rgb);
				}
			return bmp;
		}
		
}
