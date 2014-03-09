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


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.helpers.AttributesImpl;

public class SoundObject {

	protected	Clip		audioClip;
	protected	byte[]		audioSource; // sound data for LCD download
	protected   String		soundName; // name of sound in the archive
	protected	String		soundSourceFileName; // source file name of the sound on import
	protected	String		soundArchiveName; // name of sound in the ZIP archive. Only for reading
	protected	int			ID; // unique ID in database
	private boolean clipWasPlayed;
			
    public SoundObject(String soundName, String soundSourceFileName, int ID) {
		super();
		this.soundName = soundName;
		this.soundSourceFileName = soundSourceFileName;
		this.ID = ID;
		soundArchiveName = "sound_ID_" + ID;
		audioSource = null;
		audioClip = null;
	}
		    
    public SoundObject(int ID) {
    	super();
		this.soundSourceFileName = "";
		this.soundName = "";
		audioSource = null;
		this.ID = ID;
		soundArchiveName = "sound_ID_" + ID;
    }
		    
    public SoundObject( XMLStreamReader parser, ZipFile zipIn) {
    	super();

    	this.soundSourceFileName = "";
		this.soundName = "";
		audioSource = null;
		ID = 0;
		    	
		for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
			boolean processed = false;
			if (parser.getAttributeLocalName( i ) == "soundName" ) {
				soundName = parser.getAttributeValue( i );
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "soundSourceFileName" ) {
				soundSourceFileName = parser.getAttributeValue( i );
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "soundArchiveName" ) {
				soundArchiveName = parser.getAttributeValue( i );
				processed = true;
			}
			if (parser.getAttributeLocalName( i ) == "ID" ) {
				ID = Integer.decode (parser.getAttributeValue( i ));
				processed = true;
			}
			if (!processed)
				System.out.println ("unprocessed Attribute "+parser.getAttributeLocalName( i ));
		}

		// load audio data from stream
		loadArchiveSound (zipIn);
    }

	public boolean importSound (File file) {

    	audioSource = new byte [(int)file.length()];
					  			
    	FileInputStream inFile = null;

    	try {
    		inFile = new FileInputStream (file.getAbsolutePath());
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			inFile.read(audioSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				inFile.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
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

		if (!makeAudioClip ())
			return false;
		soundName = file.getName();
		soundSourceFileName = file.getAbsolutePath();
    	return true;
    }
	
	public boolean makeAudioClip (){
		InputStream byteArray = new ByteArrayInputStream(audioSource);
        AudioInputStream sourceStream;
        
		try {
			sourceStream = AudioSystem.getAudioInputStream(byteArray);
			AudioFormat audioClipFormat = sourceStream.getFormat();
			if ((audioClipFormat.getSampleRate() > 32500) || (audioClipFormat.getSampleRate() < 15500)) {
				JOptionPane.showMessageDialog(null,"Can't use sample rate","Format error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (audioClipFormat.getChannels() != 1) {
				JOptionPane.showMessageDialog(null,"Need mono wave","Format error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
	        Line.Info linfo = new Line.Info(Clip.class);
	        Line line = AudioSystem.getLine(linfo);
	        audioClip = (Clip) line;
	        audioClip.open (sourceStream);
	        return true;
	        
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
		    
	public boolean loadArchiveSound (ZipFile zipIn) {

		ZipEntry soundData;
		System.out.println ("Open " + soundArchiveName);
		soundData = zipIn.getEntry(soundArchiveName);
		if (soundData == null) {
			System.err.println ("Can't open " + soundArchiveName);
			return false;
		}

		InputStream zipInStream;
		try {
			zipInStream = zipIn.getInputStream(soundData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println ("Can't open inputstream");
			return false;
		}
		    	
    	long fileSize = soundData.getSize();
    	audioSource = new byte [(int) fileSize];
		int dataPtr = 0;
		
		try {
			while ((zipInStream.available() > 0)&& (fileSize > dataPtr)){
				dataPtr += zipInStream.read(audioSource, dataPtr, (int)fileSize-dataPtr); 
			}
			zipInStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println ("Can't read inputstream");
			return false;
		}

		makeAudioClip ();

		System.out.println ("Read " + soundArchiveName);
		return true;
    }
		    
    public boolean saveArchiveSound (ZipOutputStream zipOut) {

    	ZipEntry entry = new ZipEntry(soundArchiveName);
		entry.setSize(audioSource.length);
		try {
			zipOut.putNextEntry(entry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// write byte stream into ZIP
		try {
			zipOut.write(audioSource);
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
    public String getSoundSourceFileName () {
    	return soundSourceFileName;
    }

    public String getSoundArchiveName () {
    	return soundArchiveName;
    }
		    
    public byte[] getAudio () {
    	return audioSource;
    }

    @Override
    public String toString() {
    	return getSoundName ();
    }

		    
	/**
	 * @return the soundName
	 */
	public String getSoundName() {
		return soundName;
	}

	/**
	 * @param pictureName the pictureName to set
	 */
	public void setSoundName(String soundName) {
		this.soundName = soundName;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}
	
	public void playClip () {
		if (audioClip == null)
			return;
		
		if (clipWasPlayed == true)
			audioClip.loop(1);
		else audioClip.start();
		clipWasPlayed = true;
	}

	public void saveSoundMetaData(AttributesImpl atts) {
		atts.clear();
		atts.addAttribute("","","soundName","CDATA",soundName);
		atts.addAttribute("","","soundSourceFileName","CDATA",soundSourceFileName);
		atts.addAttribute("","","soundArchiveName","CDATA",soundArchiveName);
		atts.addAttribute("","","ID","CDATA",""+ID);
	}

	public byte[] getSoundAsWave() {
	
		audioClip.getLineInfo();
		InputStream byteArray = new ByteArrayInputStream(audioSource);
        AudioInputStream sourceStream;
       
		try {
			sourceStream = AudioSystem.getAudioInputStream(byteArray);
			byte[] buffer = new byte [200000];
			int readBytes = sourceStream.read(buffer);
			byte[] outbuffer = new byte [readBytes +2];
			for (int i = 0; i < readBytes/2; i++) {
				int sound = (buffer[2*i+1]);
				sound = (sound << 8) | buffer[2*i+0];
				sound = sound >> 7; // 16 bit -> 9 bit
				sound = sound + 0x100;
				outbuffer[2*i+1] = (byte) (sound >> 8); 
				outbuffer[2*i  ] = (byte) sound;
			}
			// set marker for end of sound
			outbuffer [outbuffer.length -1] = (byte) 0x7F;
			outbuffer [outbuffer.length -2] = (byte) 0xff;
			return outbuffer;
			
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public String getClipPropertiesString() {
		if (audioClip == null)
			return "";
		return audioClip.getFormat().toString();
	}
		    
}