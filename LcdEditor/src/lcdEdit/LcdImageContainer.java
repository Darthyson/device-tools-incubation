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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LcdImageContainer {

	protected long ImageSize;
	protected List<BufferedImage> Images = null;
	protected List<Long> ImageOffsets = null;
	protected List<Short> ImageSizesX = null;
	protected List<Short> ImageSizesY = null;

	public LcdImageContainer () {
		ImageSize = 0;
		Images = new ArrayList<BufferedImage>();
		ImageOffsets = new ArrayList<Long>();
		ImageSizesX = new ArrayList<Short>();
		ImageSizesY = new ArrayList<Short>();
}
	
	public long getSize () {
		return ImageSize + 4*Images.size() + 2*ImageSizesX.size() + 2*ImageSizesY.size();
	}
	
	protected void putLong (byte[] d, int pos, long v) {
		d[pos+3] = (byte) ((v >> 24) & 0xff);
		d[pos+2] = (byte) ((v >> 16) & 0xff);
		d[pos+1] = (byte) ((v >>  8) & 0xff);
		d[pos+0] = (byte) ((v >>  0) & 0xff);
	}

	private void writePointToFile (DataOutputStream os, int p) throws IOException  {
		
		int R = ((p >> 16) & 0xff);
		int G = ((p >>  8) & 0xff);
		int B = ( p        & 0xff);
		
		int p16 = ((R & 0xF8) << 8 ) | ((G & 0xFC) << 3 ) | ((B & 0xF8) >> 3);
		
		os.writeShort(p16);
	}
	
	public void writeToFile (DataOutputStream os, DisplayProperties dor) throws IOException {

		// we must start on even byte address
		if (os.size() % 2 == 1)
			os.writeByte (0x00);
		// output table with picture offsets to file
		long offset = 8*Images.size();
		for (int i = 0; i < ImageOffsets.size(); i++) {
			
			long ImgOfs = ImageOffsets.get(i) + offset;
//		System.out.printf ("Picture %d: %x -> %x\n", i, ImageOffsets.get(i), ImgOfs);
			os.writeByte ( (byte) ((ImgOfs >>  0) & 0xff));
			os.writeByte ( (byte) ((ImgOfs >>  8) & 0xff));
			os.writeByte ( (byte) ((ImgOfs >> 16) & 0xff));
			os.writeByte ( (byte) ((ImgOfs >> 24) & 0xff));
			
			if ((dor.getOrientation() == DisplayProperties.Orientations.horizontal) || 
				(dor.getOrientation() == DisplayProperties.Orientations.upside)) {
				os.writeByte ( (byte) ((ImageSizesX.get(i) >> 0) & 0xff));
				os.writeByte ( (byte) ((ImageSizesX.get(i) >> 8) & 0xff));
				
				os.writeByte ( (byte) ((ImageSizesY.get(i) >> 0) & 0xff));
				os.writeByte ( (byte) ((ImageSizesY.get(i) >> 8) & 0xff));
			}
			else {
				os.writeByte ( (byte) ((ImageSizesY.get(i) >> 0) & 0xff));
				os.writeByte ( (byte) ((ImageSizesY.get(i) >> 8) & 0xff));
				
				os.writeByte ( (byte) ((ImageSizesX.get(i) >> 0) & 0xff));
				os.writeByte ( (byte) ((ImageSizesX.get(i) >> 8) & 0xff));
			}
		}
		// output images to stream
		for (int i = 0; i < Images.size(); i++) {

			// non rotated, horizontal pictures
			if (dor.getOrientation() == DisplayProperties.Orientations.horizontal) { 
				for (int y = 0; y <Images.get(i).getHeight(); y++) {
					for (int x = 0; x < Images.get(i).getWidth(); x++) {
						writePointToFile (os, Images.get(i).getRGB(x, y));
					}
				}
			}
			// 180� rotated, horizontal pictures
			if (dor.getOrientation() == DisplayProperties.Orientations.upside) { 

				for (int y = Images.get(i).getHeight()-1; y >= 0; y--) {
					for (int x = Images.get(i).getWidth()-1; x >= 0; x--) {
						writePointToFile (os, Images.get(i).getRGB(x, y));
					}
				}
			}
			// 90� right rotated, vertical pictures
			if (dor.getOrientation() == DisplayProperties.Orientations.left) { 
				for (int x = 0; x < Images.get(i).getWidth(); x++) {
					for (int y = Images.get(i).getHeight()-1; y >= 0 ; y--) {
						writePointToFile (os, Images.get(i).getRGB(x, y));
					}
				}
			}
			// 90� left rotated, vertical pictures
			if (dor.getOrientation() == DisplayProperties.Orientations.right) { 
				for (int x = Images.get(i).getWidth()-1; x >= 0; x--) {
					for (int y = 0; y < Images.get(i).getHeight(); y++) {
						writePointToFile (os, Images.get(i).getRGB(x, y));
					}
				}
			}

		}
	}

	public int addImage (Image image, int width, int height){
		
		BufferedImage scaledImage;
		scaledImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		double scale = 1;
		AffineTransform xform = AffineTransform.getScaleInstance(scale, scale);
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(image, xform, null);
		graphics2D.dispose();
		
		// store offset
		ImageOffsets.add(ImageSize);
		// adjust buffer size
		ImageSize += scaledImage.getHeight() * scaledImage.getWidth() * 2;
		// add X and Y sizes
		ImageSizesX.add ((short)scaledImage.getWidth());
		ImageSizesY.add ((short)scaledImage.getHeight());
		// append image to the list
		Images.add(scaledImage);
		// return index of new image
		return Images.size()-1;
	}
}
