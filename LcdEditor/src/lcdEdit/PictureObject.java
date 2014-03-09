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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.helpers.AttributesImpl;

public class PictureObject {

protected	ImageIcon	picture; // picture for display and LCD download
protected   String		pictureName; // name of picture in the archive
protected	String		pictureSourceFileName; // source file name of the picture on import
protected	String		pictureArchiveName; // name of picture in the ZIP archive. Only for reading
protected	byte[] 		pictureSource; // source data of picture read on import
protected	int			ID; // unique ID in database
	
    public PictureObject(String pictureName, String pictureSourceFileName, int ID) {
		super();
		this.pictureName = pictureName;
		this.pictureSourceFileName = pictureSourceFileName;
		this.ID = ID;
		pictureArchiveName = "pict_ID_" + ID;
		picture = null;
		pictureSource = null;
	}
    
    public PictureObject(int ID) {
    	super();
    	this.pictureSourceFileName = "";
		this.pictureName = "";
		picture = null;
		pictureSource = null;
		this.ID = ID;
		pictureArchiveName = "pict_ID_" + ID;
    }
    
    public PictureObject( XMLStreamReader parser, ZipFile zipIn) {
    	super();

    	this.pictureSourceFileName = "";
		this.pictureName = "";
		picture = null;
		pictureSource = null;
		ID = 0;
    	
		for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
			boolean processed = false;
			if (parser.getAttributeLocalName( i ) == "pictureName" ) {
				pictureName = parser.getAttributeValue( i );
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "pictureSourceFileName" ) {
				pictureSourceFileName = parser.getAttributeValue( i );
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "pictureArchiveName" ) {
				pictureArchiveName = parser.getAttributeValue( i );
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "ID" ) {
				ID = Integer.decode (parser.getAttributeValue( i ));
				processed = true;
			}
			if (!processed)
				System.out.println ("unprocessed Attribute "+parser.getAttributeLocalName( i ));
		}

		// load icon from stream
		loadArchivePicture (zipIn);
    }

	public boolean importPicture (File file) {

    	pictureSource = new byte [(int)file.length()];
			  			
    	FileInputStream inFile = null;

    	try {
    		inFile = new FileInputStream (file.getAbsolutePath());
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			inFile.read(pictureSource);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					inFile.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return false;
			}
		try {
			inFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
					
		ByteArrayInputStream pictDataIn = new ByteArrayInputStream (pictureSource);
  	    try {
  	    	picture = new ImageIcon( ImageIO.read(pictDataIn) );
  	    }
  	    catch (IOException e1) {
  	    	// TODO Auto-generated catch block
  	    	e1.printStackTrace();
  	    	return false;
  	    }
		pictureName = file.getName();
		pictureSourceFileName = file.getAbsolutePath();
    	return true;
    }
    
    public boolean loadArchivePicture (ZipFile zipIn) {

		ZipEntry pictureData;
		System.out.println ("Open " + pictureArchiveName);
		pictureData = zipIn.getEntry(pictureArchiveName);
		if (pictureData == null) {
			System.err.println ("Can't open " + pictureArchiveName);
			return false;
		}

		InputStream zipInStream;
		try {
			zipInStream = zipIn.getInputStream(pictureData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println ("Can't open inputstream");
			return false;
		}
    	
    	long fileSize = pictureData.getSize();
    	pictureSource = new byte [(int) fileSize];
		int dataPtr = 0;
		
		try {
			while ((zipInStream.available() > 0)&& (fileSize > dataPtr)){
				dataPtr += zipInStream.read(pictureSource, dataPtr, (int)fileSize-dataPtr); 
			}
			zipInStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println ("Can't read inputstream");
			return false;
		}

		ByteArrayInputStream pictDataIn = new ByteArrayInputStream (pictureSource);
		try {
			picture = new ImageIcon( ImageIO.read(pictDataIn) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println ("Can't create icon");
			return false;
		}
		System.out.println ("Read " + pictureArchiveName);
		return true;
    }
    
    public boolean saveArchivePicture (ZipOutputStream zipOut) {

    	ZipEntry entry = new ZipEntry(pictureArchiveName);
		entry.setSize(pictureSource.length);
		try {
			zipOut.putNextEntry(entry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// write byte stream into ZIP
		try {
			zipOut.write(pictureSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			zipOut.closeEntry();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
    }
    
	/**
	 * @return the file name from which picture has been imported
	 */
    public String getPictureSourceFileName () {
    	return pictureSourceFileName;
    }

    public String getPictureArchiveName () {
    	return pictureArchiveName;
    }
    
    public ImageIcon getPicture () {
    	return picture;
    }

    @Override
    public String toString() {
    	return getPictureName ();
    }

    
	/**
	 * @return the pictureName
	 */
	public String getPictureName() {
		return pictureName;
	}

	/**
	 * @param pictureName the pictureName to set
	 */
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	public void savePictureMetaData(AttributesImpl atts) {

		atts.clear();
		atts.addAttribute("","","pictureName","CDATA",pictureName);
		atts.addAttribute("","","pictureSourceFileName","CDATA",pictureSourceFileName);
		atts.addAttribute("","","pictureArchiveName","CDATA",pictureArchiveName);
		atts.addAttribute("","","ID","CDATA",""+ID);
	
	}
    
}
