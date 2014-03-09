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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;


public class Config {

	private	String	fileName = "";
	String	server = "";
	String  configName = System.getProperty("user.dir")+ 
						 System.getProperty("file.separator")+ 
						 "LcdEditor.conf";
    String pictureDirectory = System.getProperty("user.dir");
    String soundDirectory = System.getProperty("user.dir");
    String outDirectory = System.getProperty("user.dir");
	
	public Config () {
		loadConfig();
	}
	
	public boolean loadConfig () {
		
		System.out.println ("Reading config from "+configName);
		// ---- Parse XML file ----
	      DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
	      DocumentBuilder builder = null;
	      Document document = null; 
	    try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return false;
		}
	      try {
			document = builder.parse( new File( configName ) );
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		String myServer = document.getElementsByTagName("Server").item(0).getTextContent();
		if (myServer.length() != 0)
			server = myServer;

		if (document.getElementsByTagName("LastFile") != null) {
			String myFile = document.getElementsByTagName("LastFile").item(0).getTextContent();
			if (myFile.length() != 0)
				fileName = myFile;
		}

		if (document.getElementsByTagName("LastPictureDirectory").item(0) != null) {
			String myDir = document.getElementsByTagName("LastPictureDirectory").item(0).getTextContent();
			if (myDir.length() != 0)
				pictureDirectory = myDir;
		}
		
		if (document.getElementsByTagName("LastSoundDirectory").item(0) != null) {
			String myDir = document.getElementsByTagName("LastSoundDirectory").item(0).getTextContent();
			if (myDir.length() != 0)
				soundDirectory = myDir;
		}
		
		if (document.getElementsByTagName("LastOutputDirectory").item(0) != null) {
			String myDir = document.getElementsByTagName("LastOutputDirectory").item(0).getTextContent();
			if (myDir.length() != 0)
				outDirectory = myDir;
		}
		
		return true;
	}
	
	public String getFileName () {
		return fileName;
	}
	
	public String getServer () {
		return server;
	}

	public boolean setFileName (String newFile) {
		fileName = newFile;
		return saveConfig ();
	}
	
	public boolean setServer (String newServer) {
		server = newServer;
		return saveConfig ();
	}
	
	

	public File getPictureDirectory() {
		return new File (pictureDirectory);
	}
	
	public File getSoundDirectory() {
		return new File (soundDirectory);
	}

	public void setPictureDirectory(File pictureDirectory) {
		this.pictureDirectory = pictureDirectory.getAbsolutePath();
		saveConfig ();
	}

	public void setSoundDirectory(File soundDirectory) {
		this.soundDirectory = soundDirectory.getAbsolutePath();
		saveConfig ();
	}

	public File getOutDirectory() {
		return new File (outDirectory);
	}

	public void setOutDirectory(File outDirectory) {
		this.outDirectory = outDirectory.getAbsolutePath();
		saveConfig ();
	}

	public boolean saveConfig () {
		FileOutputStream out; // declare a file output object
        PrintStream p; // declare a print stream object

        System.out.println ("Storing config to "+configName);

        try
        {
                out = new FileOutputStream(configName);

                // Connect print stream to the output stream
                p = new PrintStream( out );

                p.println ("<?xml version='1.0' encoding='utf-8'?>");
                p.println ("<uServerConfig>");
                p.println ("<Server>"+server+"</Server>");
                p.println ("<LastFile>"+fileName+"</LastFile>");
				p.println ("<LastPictureDirectory>"+pictureDirectory+"</LastPictureDirectory>");
				p.println ("<LastSoundDirectory>"+soundDirectory+"</LastSoundDirectory>");
				p.println ("<LastOutputDirectory>"+outDirectory+"</LastOutputDirectory>");
                p.println ("</uServerConfig>");

                p.close();
        }
        catch (Exception e)
        {
                System.err.println ("Can't save config.");
                return false;
        }
		
		return true;
	}
	
	
}
