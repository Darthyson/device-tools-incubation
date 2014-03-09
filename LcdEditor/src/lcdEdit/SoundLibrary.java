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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SoundLibrary implements ActionListener, ListSelectionListener {
	
	public static final int SOUND_ID_BEEP = 0;
	public static final int SOUND_ID_SILENT = -1;
	int	nextID;
	JFileChooser fc = new JFileChooser();
	JLabel soundDescriptorLabel1;
	JLabel soundDescriptorLabel2;
	SoundObject selectedSound;
	JList<SoundObject> soundChoiceList;
	DefaultListModel<SoundObject> soundListModel;
	private final static String SOUND_LIBRARY_FILE_NAME = "Sounds.xml";
	Config config;
	private LcdEditor editor;
	
	public SoundLibrary(LcdEditor editor, Config config) {
		super();
		nextID = 1; // ID = 0: beep, ID = -1: no sound
		selectedSound = null;
		this.config = config;
		this.editor = editor;
		
		fc.setCurrentDirectory(config.getSoundDirectory());
  	    fc.setDialogTitle("Read sound file (16kHz)");
  	    fc.setFileFilter( new FileFilter() 
  	    { 
  	      @Override public boolean accept( File f ) 
  	      { 
  	        return f.isDirectory() || 
  	          f.getName().toLowerCase().endsWith( ".wav" ) ||
  	          f.getName().toLowerCase().endsWith( ".aiff" ) ||
  	          f.getName().toLowerCase().endsWith( ".au" ) ||
  	          f.getName().toLowerCase().endsWith( ".mid" ); 
  	      } 
  	      @Override public String getDescription() 
  	      { 
  	        return "Sound"; 
  	      } 
  	    } ); 
	}
	
	public boolean loadLibraryFromArchive (ZipFile zipIn) {
		
		if (zipIn == null)
			return false;
		ZipEntry soundLibraryMetaData;
		System.out.println ("Open "+ SOUND_LIBRARY_FILE_NAME);		    	
		soundLibraryMetaData = zipIn.getEntry(SOUND_LIBRARY_FILE_NAME);
		if (soundLibraryMetaData == null) {
			System.err.println (SOUND_LIBRARY_FILE_NAME + " not found in Archive");		    	
			return false;
		}
		InputStream zipInStream;
		try {
			zipInStream = zipIn.getInputStream(soundLibraryMetaData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println (SOUND_LIBRARY_FILE_NAME + " IO Exception " + e);		    	
			return false;
		}

		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser;
		try {
			parser = factory.createXMLStreamReader( zipInStream );
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				zipInStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			return false;
		}			

		try {
			while ( parser.hasNext() ) 
			{ 
			  int event = parser.next(); 
			  switch ( event ) 
			  { 
			    case XMLStreamConstants.END_DOCUMENT: 
			      parser.close(); 
			      break; 
			    case XMLStreamConstants.START_ELEMENT: 
			    	String Element = parser.getLocalName();
			    	// start new visu?
			    	if (Element.equals("Sound")) {
			    		
			    		SoundObject newSound = new SoundObject (parser, zipIn);
			    		if (newSound != null) {
							soundListModel.addElement (newSound);
							if (newSound.getID() >= nextID)
								nextID = newSound.getID()+1;
						}
			    	}
			      break; 
			  } 
			}
		soundChoiceList.setSelectedIndex(0);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			zipInStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		refreshDialog ();
		return true;
	}
	
	// loadSoundFromArchive (ZipEntry zipObj)
	
	public boolean storeSoundLibraryToArchive (ZipOutputStream zipOut) {
		
		// save raw data of all sound files with file name "sound_ID_<n>"
		for ( int i = 0; i < soundListModel.size(); i++ ) {  
			((SoundObject) soundListModel.get(i)).saveArchiveSound(zipOut);
		}
		
		// save meta data of all sound files as xml
		// create memory stream to collect XML information
        ByteArrayOutputStream mos = new  ByteArrayOutputStream ();
		StreamResult streamResult = new StreamResult(mos);
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		// SAX2.0 ContentHandler.
		TransformerHandler hd;
		try {
			hd = tf.newTransformerHandler();
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		Transformer serializer = hd.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		hd.setResult(streamResult);
		try {
			hd.startDocument();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		AttributesImpl atts = new AttributesImpl();
		// LCD Data tag.
		try {
			hd.startElement("","","SoundLibrary",atts);
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		// Loop all sounds
		for ( int i = 0; i < soundListModel.size(); i++ ) {  
			
			((SoundObject) soundListModel.get(i)).saveSoundMetaData (atts);

			try {
				hd.startElement("","","Sound",atts);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			// end of page description
			try {
				hd.endElement("","","Sound");
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		try {
			hd.endElement("","","SoundLibrary");
			hd.endDocument();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		// save XML document to ZIP file
		ZipEntry entry = new ZipEntry(SOUND_LIBRARY_FILE_NAME);
		try {
			zipOut.putNextEntry(entry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// write xml stream into ZIP
		try {
			zipOut.write(mos.toByteArray(), 0, mos.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try {
			mos.close();
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
	 * @return the sound object selected by its ID in the database. 
	 * 			The ID will not change by any library operation.
	 */
	public SoundObject getSoundFromLibrary (int soundID) {
		
		for ( int i = 0; i < soundListModel.size(); i++ ) {  
			if (((SoundObject) soundListModel.get(i)).getID() == soundID)
				return (SoundObject) soundListModel.get(i);
		}
		return null;
	}

	public String[] getSoundNames () {

		String[] soundNames = new String[soundListModel.size()+2];
		soundNames[0] = "<beep>";
		soundNames[1] = "<none>";
		for ( int i = 0; i < soundListModel.size(); i++ ) {
			soundNames[i+2] = ((SoundObject) soundListModel.get(i)).getSoundName();
		}
		return soundNames;
		
	}

	public int[] getSoundIDs () {
		
		int[] soundIDs = new int[soundListModel.size()+2];
		soundIDs[0] = SOUND_ID_BEEP;
		soundIDs[1] = SOUND_ID_SILENT;
		for ( int i = 0; i < soundListModel.size(); i++ ) {
			soundIDs[i+2] = ((SoundObject) soundListModel.get(i)).getID();
		}
		return soundIDs;
	}
	
	public JDialog createSoundLibraryEditor (Frame mainWindow) {
		
    	final JDialog dialog = new JDialog(mainWindow,
                "Sound Library");

    			//Add contents to it. It must have a close button,
    			//since some L&Fs (notably Java/Metal) don't provide one
    			//in the window decorations for dialogs.

				JPanel contentPanel = new JPanel();
				contentPanel.setLayout(new BorderLayout());
				contentPanel.add(Box.createHorizontalGlue());
				contentPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));

				JPanel contentElementPanel = new JPanel();
				contentElementPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
				contentElementPanel.setLayout(new BorderLayout ());
				
				soundListModel = new DefaultListModel<SoundObject>();
				soundChoiceList = new JList<SoundObject> (soundListModel);
				soundChoiceList.addListSelectionListener(this);
				soundChoiceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        JScrollPane listScrollPane = new JScrollPane(soundChoiceList);
				contentPanel.add (listScrollPane);
				
				JPanel upDnButtonPanel = new JPanel();
				upDnButtonPanel.setLayout(new BoxLayout (upDnButtonPanel, BoxLayout.PAGE_AXIS));
				JButton upBtn = new JButton ("Up");
				upBtn.setActionCommand("SoundUp");
				upBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
				upBtn.addActionListener(this);
				JButton dnBtn = new JButton ("Down");
				dnBtn.setActionCommand("SoundDown");
				dnBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
				dnBtn.addActionListener(this);
				upDnButtonPanel.add (upBtn);
				upDnButtonPanel.add (dnBtn);

				JPanel soundLabelPanel = new JPanel();
				soundLabelPanel.setLayout(new BoxLayout (soundLabelPanel, BoxLayout.PAGE_AXIS));
				soundDescriptorLabel1 = new JLabel("");
				soundDescriptorLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
				soundDescriptorLabel2 = new JLabel("");
				soundDescriptorLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
				soundLabelPanel.add(soundDescriptorLabel1);
				soundLabelPanel.add(soundDescriptorLabel2);

				JButton playSoundsoundButton = new JButton ("Play");
				playSoundsoundButton.setActionCommand("PlaySound");
				playSoundsoundButton.addActionListener(this);
				playSoundsoundButton.setAlignmentY(Component.CENTER_ALIGNMENT);
				playSoundsoundButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				contentElementPanel.add(playSoundsoundButton, BorderLayout.CENTER);
				contentElementPanel.add(soundLabelPanel, BorderLayout.PAGE_END);
				contentElementPanel.add(upDnButtonPanel, BorderLayout.LINE_START);
				
				JPanel buttonPanel = new JPanel();
				JButton fileOpenButton = new JButton("Add");
				fileOpenButton.setActionCommand("AddSound");
				fileOpenButton.addActionListener(this);
				buttonPanel.add(fileOpenButton);
				JButton fileReloadButton = new JButton("Reload");
				fileReloadButton.setActionCommand("ReloadSound");
				fileReloadButton.addActionListener(this);
				buttonPanel.add(fileReloadButton);
				JButton fileRemoveButton = new JButton("Remove");
				fileRemoveButton.setActionCommand("RemoveSound");
				fileRemoveButton.addActionListener(this);
				buttonPanel.add(fileRemoveButton);
				contentPanel.add(contentElementPanel, BorderLayout.LINE_END);
				contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
				
    	    	JScrollPane scrollPane = new JScrollPane(contentPanel);
    			
    			JButton closeButton = new JButton("Close");
    			closeButton.addActionListener(new ActionListener() {
    				public void actionPerformed(ActionEvent e) {
    					dialog.setVisible(false);
    				}
    			});
    			JPanel closePanel = new JPanel();
    			closePanel.setLayout(new BoxLayout(closePanel,
    			            BoxLayout.LINE_AXIS));
    			closePanel.add(Box.createHorizontalGlue());
    			closePanel.add(closeButton);
    			closePanel.setBorder(BorderFactory.
    			createEmptyBorder(0,0,5,5));
    			
    			JPanel contentPane = new JPanel(new BorderLayout());
    			contentPane.add(scrollPane, BorderLayout.CENTER);
    			contentPane.add(closePanel, BorderLayout.PAGE_END);
    			contentPane.setOpaque(true);
    			dialog.setContentPane(contentPane);
    			
    			//Show it.
    			dialog.setSize(new Dimension(250, 300));
    			dialog.setLocationRelativeTo(null);
    			dialog.setVisible(true);	
    			dialog.setSize(350, 300);
    			return dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String str = e.getActionCommand();
	  	dialogAction (str);

	}

	private void dialogAction(String str) {
		
		if (str == "AddSound") {
		
			int state = fc.showOpenDialog( null ); 
			if ( state == JFileChooser.APPROVE_OPTION ) 
			{
				File file = fc.getSelectedFile();
				config.setSoundDirectory(fc.getCurrentDirectory());
				SoundObject newSound = new SoundObject (nextID);
				if (newSound.importSound (file)) {
					soundListModel.addElement (newSound);
					soundChoiceList.setSelectedIndex(soundListModel.size()-1);
					soundChoiceList.requestFocusInWindow();
					selectedSound = newSound;
					nextID = nextID +1;
					refreshDialog ();
				}
			}
		}
		if (str == "ReloadSound") {
			SoundObject selectedSoundObject = (SoundObject) soundChoiceList.getSelectedValue();
			if (selectedSoundObject != null) {
				System.out.println ("Reloading "+ selectedSoundObject.getSoundSourceFileName());
				String soundName = selectedSoundObject.getSoundName();
				File file = new File (selectedSoundObject.getSoundSourceFileName());
				selectedSoundObject.importSound(file);
				selectedSoundObject.setSoundName (soundName);
				refreshDialog ();
			}
		}
		if (str == "RemoveSound") {
			if (selectedSound == null) {
				return;
			}
			if (editor.checkSoundInUse (selectedSound.getID())) {
					JOptionPane.showMessageDialog(null,
						"This sound is still in use.",
						"Can not remove sound",
						JOptionPane.ERROR_MESSAGE);
    				return;
			}
			int selectedElementPos = soundChoiceList.getSelectedIndices()[0];
			soundListModel.remove(selectedElementPos);
			if (selectedElementPos > soundChoiceList.getComponentCount())
				selectedElementPos = selectedElementPos-1;
			if (selectedElementPos > 0)
				soundChoiceList.setSelectedIndex(selectedElementPos);
			soundChoiceList.requestFocusInWindow();
			refreshDialog ();
		}
		
		if (str == "SoundUp") {
			int selectedElementPos = soundChoiceList.getSelectedIndex();
			soundListModel.add(selectedElementPos-1, soundListModel.get(selectedElementPos));
			soundListModel.remove(selectedElementPos+1);
			soundChoiceList.requestFocusInWindow();
			soundChoiceList.setSelectedIndex(selectedElementPos-1);
			soundChoiceList.grabFocus();
			refreshDialog ();
		}
		if (str == "SoundDown") {
			int selectedElementPos = soundChoiceList.getSelectedIndex();
			soundListModel.add(selectedElementPos+2, soundListModel.get(selectedElementPos));
			soundListModel.remove(selectedElementPos);
			soundChoiceList.requestFocusInWindow();
			soundChoiceList.setSelectedIndex(selectedElementPos+1);
			soundChoiceList.grabFocus();
			refreshDialog ();
		}

		if (str == "PlaySound") {
			SoundObject selectedSoundObject = (SoundObject) soundChoiceList.getSelectedValue();
			if (selectedSoundObject == null)
				return;
			// play sound
			selectedSoundObject.playClip();
		}

	}

	private void refreshDialog() {

		SoundObject selectedSoundObject = (SoundObject) soundChoiceList.getSelectedValue();
		if (selectedSoundObject == null)
			return;
		soundDescriptorLabel1.setText(selectedSoundObject.getSoundSourceFileName());
		soundDescriptorLabel2.setText(selectedSoundObject.getClipPropertiesString());
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting())
			return;
		selectedSound = (SoundObject) soundChoiceList.getSelectedValue();
		refreshDialog();
	}

	/**
	 * @param i 
	 * @return the sound ID of the currently selected sound +soundOffset or -1, if none is selected. 
	 */
	public int getSelectedSoundID(int soundOffset) {
		SoundObject selectedSoundObject = (SoundObject) soundChoiceList.getSelectedValue();
		if (selectedSoundObject != null)
			return selectedSoundObject.getID();
		return -1;
	}

	public void clearArchive() {
		// clear all sounds from the list
		soundDescriptorLabel1.setText("");
		soundDescriptorLabel2.setText("");
		soundListModel.clear();
		selectedSound = null;
		nextID = 1;
	}

	public byte[] getSoundAsWave(int soundID) {
		
		SoundObject sourceSound = getSoundFromLibrary (soundID);
		if (sourceSound == null)
			return null;
		
		return sourceSound.getSoundAsWave();
	}

	public Object getSoundNames(int soundID) {
		return new SoundNames (getSoundNames(), getSoundIDs(), soundID);
	}

	public void playSound(int soundID) {
		SoundObject selectedSoundObject = getSoundFromLibrary (soundID);
		if (selectedSoundObject == null)
			return;
		// play sound
		selectedSoundObject.playClip();
	}

}
