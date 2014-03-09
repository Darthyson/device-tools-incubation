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
import javax.swing.ImageIcon;
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

public class PictureLibrary implements ActionListener, ListSelectionListener {
	
	public final static int PICTURE_ID_NONE =	-1;
	
	int	nextID;
	JFileChooser fc = new JFileChooser();
	ImageIcon selectedPictureIcon;
	JLabel pictureLabel;
	JLabel pictureDescriptorLabel1;
	JLabel pictureDescriptorLabel2;
	PictureObject selectedPicture;
	JList<PictureObject> pictureChoiceList;
	DefaultListModel<PictureObject> pictureListModel;
	private final static String PICTURE_LIBRARY_FILE_NAME = "Pictures.xml";
	Config config;
	private LcdEditor editor;
	
	public PictureLibrary(LcdEditor editor, Config config) {
		super();
		nextID = 1;
		selectedPicture = null;
		this.config = config;
		this.editor = editor;
		
		fc.setCurrentDirectory(config.getPictureDirectory());
  	    fc.setDialogTitle("Read image file");
  	    fc.setFileFilter( new FileFilter() 
  	    { 
  	      @Override public boolean accept( File f ) 
  	      { 
  	        return f.isDirectory() || 
  	          f.getName().toLowerCase().endsWith( ".bmp" ) ||
  	          f.getName().toLowerCase().endsWith( ".png" ) ||
  	          f.getName().toLowerCase().endsWith( ".gif" ) ||
  	          f.getName().toLowerCase().endsWith( ".jpg" ) ||
  	          f.getName().toLowerCase().endsWith( ".jpeg" ); 
  	      } 
  	      @Override public String getDescription() 
  	      { 
  	        return "Picture"; 
  	      } 
  	    } ); 
	}
	
	// createPictureLibrary ()
	
	public boolean loadLibraryFromArchive (ZipFile zipIn) {
		
		if (zipIn == null)
			return false;
		ZipEntry pictureLibraryMetaData;
		pictureLibraryMetaData = zipIn.getEntry(PICTURE_LIBRARY_FILE_NAME);
		if (pictureLibraryMetaData == null) {
			System.err.println (PICTURE_LIBRARY_FILE_NAME + " not found in Archive");		    	
			return false;
		}
		InputStream zipInStream;
		try {
			zipInStream = zipIn.getInputStream(pictureLibraryMetaData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println (PICTURE_LIBRARY_FILE_NAME + " IO Exception " + e);		    	
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
			    	if (Element.equals("Picture")) {
			    		
			    		PictureObject newPicture = new PictureObject (parser, zipIn);
			    		if (newPicture != null) {
							pictureListModel.addElement (newPicture);
							if (newPicture.getID() >= nextID)
								nextID = newPicture.getID()+1;
						}
			    	}
			      break; 
			  } 
			}
		pictureChoiceList.setSelectedIndex(0);
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
	
	public boolean storePictureLibraryToArchive (ZipOutputStream zipOut) {
		
		// save raw data of all image files with file name "pict_ID_<n>"
		for ( int i = 0; i < pictureListModel.size(); i++ ) {  
			((PictureObject) pictureListModel.get(i)).saveArchivePicture(zipOut);
		}
		
		// save meta data of all image files as xml
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
			hd.startElement("","","PictureLibrary",atts);
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		// Loop all pictures
		for ( int i = 0; i < pictureListModel.size(); i++ ) {  
			
			((PictureObject) pictureListModel.get(i)).savePictureMetaData (atts);

			try {
				hd.startElement("","","Picture",atts);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

			// end of page description
			try {
				hd.endElement("","","Picture");
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		try {
			hd.endElement("","","PictureLibrary");
			hd.endDocument();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		// save XML document to ZIP file
		ZipEntry entry = new ZipEntry(PICTURE_LIBRARY_FILE_NAME);
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
	
	// clearPicureLibrary ();
	
	/**
	 * @return the picture object selected by its ID in the database. 
	 * 			The ID will not change by any library operation.
	 */
	public PictureObject getPictureFromLibrary (int pictureID) {
		
		for ( int i = 0; i < pictureListModel.size(); i++ ) {  
			if (((PictureObject) pictureListModel.get(i)).getID() == pictureID)
				return (PictureObject) pictureListModel.get(i);
		}
		return null;
	}

	public String[] getPictureNames () {
		
		String[] pictureNames = new String[pictureListModel.size()+1];
		pictureNames[0] = "<none>";
		for ( int i = 0; i < pictureListModel.size(); i++ ) {
			pictureNames[i+1] = ((PictureObject) pictureListModel.get(i)).getPictureName();
		}
		return pictureNames;
		
	}
	
	public int[] getPictureIDs () {
		
		int[] pictureIDs = new int[pictureListModel.size()+1];
		pictureIDs[0] = -1;
		for ( int i = 0; i < pictureListModel.size(); i++ ) {
			pictureIDs[i+1] = ((PictureObject) pictureListModel.get(i)).getID();
		}
		return pictureIDs;
		
	}

	
	public JDialog createPictureLibraryEditor (Frame mainWindow) {
		
    	final JDialog dialog = new JDialog(mainWindow,
                "Picture Library");

    			//Add contents to it. It must have a close button,
    			//since some L&Fs (notably Java/Metal) don't provide one
    			//in the window decorations for dialogs.
				dialog.setVisible(false);	

				JPanel contentPanel = new JPanel();
				contentPanel.setLayout(new BorderLayout());
				contentPanel.add(Box.createHorizontalGlue());
				contentPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));

				JPanel contentElementPanel = new JPanel();
				contentElementPanel.setBorder(BorderFactory.createEmptyBorder(0,0,5,5));
				contentElementPanel.setLayout(new BorderLayout ());
				
				pictureListModel = new DefaultListModel<PictureObject>();
				pictureChoiceList = new JList<PictureObject> (pictureListModel);
				pictureChoiceList.addListSelectionListener(this);
				pictureChoiceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		        JScrollPane listScrollPane = new JScrollPane(pictureChoiceList);
				contentPanel.add (listScrollPane);
				
				JPanel upDnButtonPanel = new JPanel();
				upDnButtonPanel.setLayout(new BoxLayout (upDnButtonPanel, BoxLayout.PAGE_AXIS));
				JButton upBtn = new JButton ("Up");
				upBtn.setActionCommand("PictureUp");
				upBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
				upBtn.addActionListener(this);
				JButton dnBtn = new JButton ("Down");
				dnBtn.setActionCommand("PictureDown");
				dnBtn.setAlignmentY(Component.CENTER_ALIGNMENT);
				dnBtn.addActionListener(this);
				upDnButtonPanel.add (upBtn);
				upDnButtonPanel.add (dnBtn);

				JPanel pictureLabelPanel = new JPanel();
				pictureLabelPanel.setLayout(new BoxLayout (pictureLabelPanel, BoxLayout.PAGE_AXIS));
				pictureDescriptorLabel1 = new JLabel("");
				pictureDescriptorLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
				pictureDescriptorLabel2 = new JLabel("");
				pictureDescriptorLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
				pictureLabelPanel.add(pictureDescriptorLabel1);
				pictureLabelPanel.add(pictureDescriptorLabel2);

				selectedPictureIcon = null;
				pictureLabel = new JLabel("", selectedPictureIcon, JLabel.CENTER);
				pictureLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
				pictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				contentElementPanel.add(pictureLabel, BorderLayout.CENTER);
				contentElementPanel.add(pictureLabelPanel, BorderLayout.PAGE_END);
				contentElementPanel.add(upDnButtonPanel, BorderLayout.LINE_START);
				
				JPanel buttonPanel = new JPanel();
				JButton fileOpenButton = new JButton("Add");
				fileOpenButton.setActionCommand("AddPicture");
				fileOpenButton.addActionListener(this);
				buttonPanel.add(fileOpenButton);
				JButton fileReloadButton = new JButton("Reload");
				fileReloadButton.setActionCommand("ReloadPicture");
				fileReloadButton.addActionListener(this);
				buttonPanel.add(fileReloadButton);
				JButton fileRemoveButton = new JButton("Remove");
				fileRemoveButton.setActionCommand("RemovePicture");
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
    			dialog.setSize(350, 300);
    			return dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String str = e.getActionCommand();
	  	dialogAction (str);

	}

	private void dialogAction(String str) {
		
		if (str == "AddPicture") {
		
			int state = fc.showOpenDialog( null ); 
			if ( state == JFileChooser.APPROVE_OPTION ) 
			{ 
				config.setPictureDirectory(fc.getCurrentDirectory());
				File file = fc.getSelectedFile();
				PictureObject newPicture = new PictureObject (nextID);
				if (newPicture.importPicture (file)) {
					pictureListModel.addElement (newPicture);
					pictureChoiceList.setSelectedIndex(pictureListModel.size()-1);
					pictureChoiceList.requestFocusInWindow();
					selectedPicture = newPicture;
					nextID = nextID +1;
					refreshDialog ();
				}
			}
		}
		if (str == "ReloadPicture") {
			PictureObject selectedPictureObject = (PictureObject) pictureChoiceList.getSelectedValue();
			if (selectedPictureObject != null) {
				System.out.println ("Reloading "+ selectedPictureObject.getPictureSourceFileName());
				String pictureName = selectedPictureObject.getPictureName();
				File file = new File (selectedPictureObject.getPictureSourceFileName());
				selectedPictureObject.importPicture(file);
				selectedPictureObject.setPictureName (pictureName);
				refreshDialog ();
			}
		}
		if (str == "RemovePicture") {
			//check, if the picture is still in use
			if (selectedPicture == null) {
				return;
			}
			if (editor.checkImageInUse (selectedPicture.getID())) {
					JOptionPane.showMessageDialog(null,
						"This picture is still in use.",
						"Can not remove picture",
						JOptionPane.ERROR_MESSAGE);
    				return;
			}
			
			int selectedElementPos = pictureChoiceList.getSelectedIndices()[0];
			pictureListModel.remove(selectedElementPos);
			if (selectedElementPos >= pictureListModel.size())
				selectedElementPos = selectedElementPos-1;
			if (selectedElementPos > 0)
				pictureChoiceList.setSelectedIndex(selectedElementPos);
			pictureChoiceList.requestFocusInWindow();
			refreshDialog ();
		}
		
		if (str == "PictureUp") {
			int selectedElementPos = pictureChoiceList.getSelectedIndex();
			pictureListModel.add(selectedElementPos-1, pictureListModel.get(selectedElementPos));
			pictureListModel.remove(selectedElementPos+1);
			pictureChoiceList.requestFocusInWindow();
			pictureChoiceList.setSelectedIndex(selectedElementPos-1);
			pictureChoiceList.grabFocus();
			refreshDialog ();
		}
		if (str == "PictureDown") {
			int selectedElementPos = pictureChoiceList.getSelectedIndex();
			pictureListModel.add(selectedElementPos+2, pictureListModel.get(selectedElementPos));
			pictureListModel.remove(selectedElementPos);
			pictureChoiceList.requestFocusInWindow();
			pictureChoiceList.setSelectedIndex(selectedElementPos+1);
			pictureChoiceList.grabFocus();
			refreshDialog ();
		}

	}

	private void refreshDialog() {

		PictureObject selectedPictureObject = (PictureObject) pictureChoiceList.getSelectedValue();
		if (selectedPictureObject == null)
			return;
		pictureLabel.setIcon(selectedPictureObject.getPicture());
		pictureDescriptorLabel1.setText(selectedPictureObject.getPictureSourceFileName());
		String description;
		description = selectedPictureObject.getPicture().getIconWidth() + " x " + selectedPictureObject.getPicture().getIconHeight(); 
		pictureDescriptorLabel2.setText(description);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting())
			return;
		selectedPicture = (PictureObject) pictureChoiceList.getSelectedValue();
		refreshDialog();
	}

	/**
	 * @param i 
	 * @return the picture ID of the currently selected image +imageOffset or -1, if none is selected. 
	 */
	public int getSelectedPictureID(int imageOffset) {
		
		int i = pictureChoiceList.getSelectedIndex();
		if (i < 0)
			return -1;
		if (imageOffset + i >= pictureListModel.size())
			return -1;
		PictureObject selectedPictureObject = (PictureObject) pictureListModel.get(imageOffset + i);
		if (selectedPictureObject != null)
			return selectedPictureObject.getID();
		return -1;
	}

	public void clearArchive() {
		// clear all pictures from the list
		pictureLabel.setIcon(null);
		pictureDescriptorLabel1.setText("");
		pictureDescriptorLabel2.setText("");
		pictureListModel.clear();
		selectedPicture = null;
		nextID = 1;
	}

	public Object getPictureNames(int pictureID) {
		return new PictureNames (getPictureNames(), getPictureIDs(), pictureID);
	}
	
}
