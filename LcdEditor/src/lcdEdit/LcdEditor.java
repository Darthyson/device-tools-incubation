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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
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

public class LcdEditor extends JFrame 
	implements ActionListener, MouseListener, MouseMotionListener, ChangeListener, ListSelectionListener  {

	private static final long serialVersionUID = -8248529614931942015L;
	private static final int MAX_FLASH_SIZE = 8 * 0x100000;

	/**
	 * @param args
	 */
		protected final String PROGRAM_VERSION = "V1.842";
		protected int MAX_PICT_SIZE = 0x800000;
	    protected int sizeX = 500;
	    protected int sizeY = 500;
	    PhysicalAddress physicalAddress;
  	    JFileChooser fc = new JFileChooser();
  	    JFileChooser ec = new JFileChooser();
  	    ProjectPropertiesDialog proOptDlg;
  	    HardwarePropertiesDialog hardwareOptDlg;
  	    PagePropertiesDialog pageOptDlg;
  	    String exportFileName = "";
  	    // window main component
  	    JTabbedPane visuPages = new JTabbedPane();
        JToolBar btnPanel = new JToolBar ();
  	    JPanel statusBar = new JPanel (new FlowLayout(FlowLayout.LEFT));
  	    JMenuBar menuBar = new JMenuBar();
  	    protected Color defaultColor = new Color (80,90,180);
  	    BufferedOutputStream out;
  	    InputStreamReader in;
  	    boolean connected;
  	    JDialog inspector;
  	    JDialog formatter;
  	    JDialog elements;
  	    ObjectSelectionDialog objectSelectionDialog;
  	    JTable myElementsTable;
  	    JTable myPropertiesTable;
  	    PropertiesTableModel tableModel = null;
  	    DefaultTableModel elementsTableModel = null;
        ZipOutputStream zipOut = null;
        ZipFile zipIn = null;
        boolean fileNameValid = false;
        boolean fileChanged = false;
        Config config = new Config ();
        DisplayProperties dProps;
        ArrayList<EditorComponent> copyElements;
        
  	    JDialog pictureLibraryInspector;
        PictureLibrary pictures;
  	    JDialog soundLibraryInspector;
        SoundLibrary sounds;
        
        //data for area marking on visu page
        Point startPoint;
        Point currentPoint;
        
        static public void main (String[] args) {
	        new LcdEditor().setVisible(true);
	    }
	    
	    public LcdEditor () {
	        setTitle("LCD Modul Editor");
	        addMenu ();
	        setJMenuBar(menuBar);
	        setSize(sizeX, sizeY);

		  	dProps = new DisplayProperties();
		  	physicalAddress = new PhysicalAddress ();
		  	  
		  	proOptDlg = new ProjectPropertiesDialog (this); 
		  	proOptDlg.setLocation (200,100);
		  	objectSelectionDialog = new ObjectSelectionDialog (this);
		  	
		  	hardwareOptDlg = new HardwarePropertiesDialog (this);
		  	hardwareOptDlg.setLocation (220, 120);

		  	pageOptDlg = new PagePropertiesDialog (this); 
		  	pageOptDlg.setLocation (200,100);

	        //add tool bar on top of window
	        btnPanel.setBackground(new Color(100, 100, 100));
	        getContentPane().add( btnPanel, BorderLayout.NORTH );	        // buttons label, command
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/media/Stop24.gif","Quit", "Quit"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Open24.gif","File open", "Load"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Save24.gif","File save", "Save"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/SaveAs24.gif","File save as", "SaveAs"));
	        btnPanel.addSeparator();  
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Copy24.gif","Copy", "Copy"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Paste24.gif","Paste", "Paste"));
	        btnPanel.addSeparator();  
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Import24.gif","Export Project", "Export"));
	        btnPanel.addSeparator();  
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/History24.gif","Element Bar", "ElementBar"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Remove24.gif","Delete Selected Object(s)", "Delete"));
	        btnPanel.addSeparator();  
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Properties24.gif","Object Inspector", "Inspector"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/AlignJustifyHorizontal24.gif","Alignment Tool", "Alignment"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/History24.gif","Picture Library", "PictureLibrary"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/History24.gif","Sound Library", "SoundLibrary"));
	        btnPanel.addSeparator();  
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Add24.gif","Add New Page", "Page"));
	        btnPanel.add(addIconButton ("toolbarButtonGraphics/general/Delete24.gif","Delete Page", "DelPage"));
	        objectSelectionDialog.addButton ("Text", "Text");
	        objectSelectionDialog.addButton ("Jumper", "Jumper");
	        objectSelectionDialog.addButton ("Button", "Button");
	        objectSelectionDialog.addButton ("S-Button", "S-Button");
	        objectSelectionDialog.addButton ("LED", "LED");
	        objectSelectionDialog.addButton ("Value", "Value");
	        objectSelectionDialog.addButton ("Picture", "Picture");
	        objectSelectionDialog.setLocation(500, 20);
	        objectSelectionDialog.setVisible(true);
	       	
	        inspector = addPropertiesDialog ();
	        inspector.setLocation(500,100);
	        inspector.setVisible(true);
	        
	        elements = addElementsDialog ();
	        elements.setLocation(500,300);
	        elements.setVisible(true);
	        	        
	        formatter = addAlignmentDialog ();
 
	        addPage ("Page 1", defaultColor, true);
	        getContentPane().add( visuPages, BorderLayout.CENTER);	        // buttons label, command
	        visuPages.addMouseListener(this);
	        visuPages.addChangeListener(this);
	        
	        statusBar.setBackground(new Color(100, 100, 100));
	        getContentPane().add( statusBar, BorderLayout.SOUTH);	        // buttons label, command
	        
	        pictures = new PictureLibrary (this, config);
	        pictureLibraryInspector = pictures.createPictureLibraryEditor(this);
	        pictureLibraryInspector.setLocation(760, 100);
	        pictureLibraryInspector.setVisible(true);
	        sounds = new SoundLibrary (this, config);
	        soundLibraryInspector = sounds.createSoundLibraryEditor(this);
	        soundLibraryInspector.setVisible(false);
	          
	  	    fc.setFileFilter( new FileFilter() 
	  	    { 
	  	      @Override public boolean accept( File f ) 
	  	      { 
	  	        return f.isDirectory() || 
	  	          f.getName().toLowerCase().endsWith( ".lcd" ); 
	  	      } 
	  	      @Override public String getDescription() 
	  	      { 
	  	        return "LCD Modul Project"; 
	  	      } 
	  	    } );

	  	    ec.setDialogTitle ("Export to file");
	  	    ec.setFileFilter( new FileFilter() 
	  	    { 
	  	      @Override public boolean accept( File f ) 
	  	      { 
	  	        return f.isDirectory() || 
	  	          f.getName().toLowerCase().endsWith( ".lcdb" ); 
	  	      } 
	  	      @Override public String getDescription() 
	  	      { 
	  	        return "LCD Modul Binary File"; 
	  	      } 
	  	    } ); 
	  	    
	    }
	    
        @SuppressWarnings("serial")
		private void addMenuItemWithAccelerator (JMenu menu, String name, final String a, int men, int acc) {
            JMenuItem mi;
	        mi = menu.add(new AbstractAction(name) { 
	              public void actionPerformed(ActionEvent e) {
	            	  guiAction (a);
	              } 
	        });
	        mi.setMnemonic(men);
	        mi.setAccelerator(KeyStroke.getKeyStroke(acc, Event.CTRL_MASK));
        }
        
        @SuppressWarnings("serial")
		private void addMenuItemWithAccelerator (JMenu menu, String name, final String a, int men) {
            JMenuItem mi;
	        mi = menu.add(new AbstractAction(name) { 
	              public void actionPerformed(ActionEvent e) {
	            	  guiAction (a);
	              } 
	        });
	        mi.setMnemonic(men);
        }
	    
		private void addMenu() {
	        JMenu file = menuBar.add(new JMenu("File"));
	        file.setMnemonic('f');
	        JMenu edit = menuBar.add(new JMenu("Edit"));
	        edit.setMnemonic('e');
	        JMenu view = menuBar.add(new JMenu("View"));
	        view.setMnemonic('v');
	        JMenu project = menuBar.add(new JMenu("Project"));
	        project.setMnemonic('p');
	        JMenu component = menuBar.add(new JMenu("Components"));
	        component.setMnemonic('c');
	        JMenu page = menuBar.add(new JMenu("Page"));
	        page.setMnemonic('a');
	        JMenu help = menuBar.add(new JMenu("Help"));
	        help.setMnemonic('h');
	        
	        // file menu
	        addMenuItemWithAccelerator (file, "New", "New", KeyEvent.VK_N, KeyEvent.VK_N);
	        addMenuItemWithAccelerator (file, "Open", "Load", KeyEvent.VK_O, KeyEvent.VK_O);
	        addMenuItemWithAccelerator (file, "Save", "Save", KeyEvent.VK_S, KeyEvent.VK_S);
	        addMenuItemWithAccelerator (file, "Save as", "SaveAs", KeyEvent.VK_A, KeyEvent.VK_A);
	        // Separator einf�gen
	        file.addSeparator();
	        // Close application
	        addMenuItemWithAccelerator (file, "Exit", "Quit", KeyEvent.VK_X);

	        //Setup project options
	        addMenuItemWithAccelerator (project, "Options", "ProjectOptions", KeyEvent.VK_O);
	        addMenuItemWithAccelerator (project, "Hardware", "HardwareOptions", KeyEvent.VK_H);
	        view.addSeparator();
	        addMenuItemWithAccelerator (project, "Export", "Export", KeyEvent.VK_E);
	        
	        //Setup Edit options
	        addMenuItemWithAccelerator (edit, "Copy", "Copy", KeyEvent.VK_C, KeyEvent.VK_C);
	        addMenuItemWithAccelerator (edit, "Paste", "Paste", KeyEvent.VK_P, KeyEvent.VK_V);
	        addMenuItemWithAccelerator (edit, "Delete", "Delete", KeyEvent.VK_D);

	        //Setup View options
	        addMenuItemWithAccelerator (view, "Components Bar", "ElementBar", KeyEvent.VK_C);
	        addMenuItemWithAccelerator (view, "Object Inspector", "Inspector", KeyEvent.VK_O);
	        addMenuItemWithAccelerator (view, "Alignment Tool", "Alignment", KeyEvent.VK_A);
	        addMenuItemWithAccelerator (view, "Page Elements", "Elements", KeyEvent.VK_E);
	        // Separator einf�gen
	        view.addSeparator();
	        addMenuItemWithAccelerator (view, "Picture Library", "PictureLibrary", KeyEvent.VK_P);
	        addMenuItemWithAccelerator (view, "Sound Library", "SoundLibrary", KeyEvent.VK_S);

	        //component Edit options
	        addMenuItemWithAccelerator (component, "New Text", "Text", KeyEvent.VK_T, KeyEvent.VK_T);
	        addMenuItemWithAccelerator (component, "New Jumper", "Jumper", KeyEvent.VK_J, KeyEvent.VK_J);
	        addMenuItemWithAccelerator (component, "New Button", "Button", KeyEvent.VK_B, KeyEvent.VK_B);
	        addMenuItemWithAccelerator (component, "New S-Button", "S-Button", KeyEvent.VK_S, KeyEvent.VK_Z);
	        addMenuItemWithAccelerator (component, "New LED", "LED", KeyEvent.VK_L, KeyEvent.VK_L);
	        addMenuItemWithAccelerator (component, "New Value", "Value", KeyEvent.VK_V, KeyEvent.VK_W);
	        addMenuItemWithAccelerator (component, "New Picture", "Picture", KeyEvent.VK_P, KeyEvent.VK_P);
	        // Separator einf�gen
	        component.addSeparator();
	        addMenuItemWithAccelerator (component, "Delete", "Delete", KeyEvent.VK_D);

	        //component Page options
	        addMenuItemWithAccelerator (page, "New Page", "Page", KeyEvent.VK_N);
	        addMenuItemWithAccelerator (page, "Rename Page", "RenamePage", KeyEvent.VK_R);
	        addMenuItemWithAccelerator (page, "Delete Page", "DelPage", KeyEvent.VK_D);
	        page.addSeparator();
	        addMenuItemWithAccelerator (page, "Setup Page", "SetupPage", KeyEvent.VK_S);
	        addMenuItemWithAccelerator (page, "Make 1st Page", "MakeStartPage", KeyEvent.VK_1);

	        // help menu
	        addMenuItemWithAccelerator (help, "About", "About", KeyEvent.VK_A);
		}

		JLayeredPane addPage (String pageName, Color pageColor, boolean opaque) {

			JLayeredVisuPane layeredPane = new JLayeredVisuPane();
			layeredPane.setPreferredSize(new Dimension(dProps.getXSize(),dProps.getYSize()));
			layeredPane.setOpaque(opaque);
			layeredPane.setBackground(pageColor);
			layeredPane.addMouseListener(this);
			layeredPane.addMouseMotionListener(this);

	    	JScrollPane scrollpane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
	        		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
	    	scrollpane.setViewportView(layeredPane);
	    	scrollpane.setWheelScrollingEnabled(true);
	    	
	        visuPages.addTab( pageName, scrollpane );
	        
	        return layeredPane;
	    }
		
		boolean pageNameIsUnique (String newName) {
			boolean isUnique = true;
			for (int i = 0; i < visuPages.getTabCount(); i++) {
				if (newName.equals(visuPages.getTitleAt(i)))
					isUnique = false;
			}
			return isUnique;
		}
		
		// this function scans all Jumpers and exchanges the Target name,
		// if the user renamed the respective page
		void changePageName (String oldName, String newName) {
			// Loop all pages
			for ( int i = 0; i < visuPages.getTabCount(); i++ ) {  

				// get Layered pane
				JScrollPane scrollPane = (JScrollPane)visuPages.getComponentAt(i);
				JViewport viewPort = (JViewport)scrollPane.getComponent(0);
				JLayeredPane layeredPane = (JLayeredPane)viewPort.getComponent(0);
				
				// get components on this page
				Component[] comp = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);

				// Loop all elements on page
				for ( Component thisComp : comp ) {
					if (EIBComp.class.isInstance (thisComp)) {
						EditorComponent co = (EditorComponent)thisComp;
						co.changePageName (oldName, newName);
					}
				}
			}
		}
		
		// This function scans all pages, all objects on pages
		// and writes the setup to an XML file
		void saveToZip ( String fileName ) throws SAXException, TransformerConfigurationException {
			// create new ZIP file
	        try {
				zipOut = new ZipOutputStream(new FileOutputStream(fileName));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
	        // enable compression
	        zipOut.setMethod(ZipOutputStream.DEFLATED);
	        // set compression level to max.
	        zipOut.setLevel (9);
			// save picture library
	        pictures.storePictureLibraryToArchive (zipOut);
			// save sound library
	        sounds.storeSoundLibraryToArchive (zipOut);
			
			// create memory stream to collect XML information
	        ByteArrayOutputStream mos = new  ByteArrayOutputStream ();
			StreamResult streamResult = new StreamResult(mos);
			SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			// SAX2.0 ContentHandler.
			TransformerHandler hd;
			hd = tf.newTransformerHandler();
			Transformer serializer = hd.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");
			hd.setResult(streamResult);
			hd.startDocument();
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("","","X","CDATA",""+dProps.getXSize());
			atts.addAttribute("","","Y","CDATA",""+dProps.getYSize());
			atts.addAttribute("","","DisplayOrienation","CDATA", ""+dProps.getXmlOrientationString());
			atts.addAttribute("","","DisplayType","CDATA", ""+dProps.getXmlTypeString());
			atts.addAttribute("","","MirrorTouchX","CDATA", ""+dProps.getMirrorTouchX());
			atts.addAttribute("","","MirrorTouchY","CDATA", ""+dProps.getMirrorTouchY());
			atts.addAttribute("","","HardwareMirrorScreen","CDATA", ""+dProps.getMirrorHWLCD());
			atts.addAttribute("", "", "PhysAddr", "CDATA", physicalAddress.getPhysicalAddressString());
			atts.addAttribute("", "", "ExportFile", "CDATA", ""+exportFileName);
			// LCD Data tag.
			hd.startElement("","","LcdData",atts);

			// Loop all pages
			for ( int i = 0; i < visuPages.getTabCount(); i++ ) {  

				// get Layered pane
				JScrollPane scrollPane = (JScrollPane)visuPages.getComponentAt(i);
				JViewport viewPort = (JViewport)scrollPane.getComponent(0);
				JLayeredPane layeredPane = (JLayeredPane)viewPort.getComponent(0);
				
				atts.clear();
				atts.addAttribute("","","Name","CDATA",visuPages.getTitleAt(i));
				atts.addAttribute("","","Color","CDATA",""+layeredPane.getBackground().getRGB());
				if (layeredPane.isOpaque())
					atts.addAttribute("","","Opaque","CDATA","");
				hd.startElement("","","Page",atts);

				// get components on this page
				Component[] comp = layeredPane.getComponentsInLayer(new Integer (-1));
				// Loop all background elements on page
				for ( Component thisComp : comp ) {
					if (EIBComp.class.isInstance (thisComp)) {
						EditorComponent co = (EditorComponent)thisComp;
						co.writeXML (hd);
					}
				}
				
				// get components on this page
				comp = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);

				// Loop all elements on page
				for ( Component thisComp : comp ) {
					if (EIBComp.class.isInstance (thisComp)) {
						EditorComponent co = (EditorComponent)thisComp;
						co.writeXML (hd);
					}
				}
				// end of page description
				hd.endElement("","","Page");
			}
			
			// write hardware configuration to XML file
			hardwareOptDlg.writeToXML (hd);
		
			// close XML file
			hd.endElement("","","LcdData");
			hd.endDocument();

			// save XML document to ZIP file
			ZipEntry entry = new ZipEntry("Visu.xml");
			try {
				zipOut.putNextEntry(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// write xml stream into ZIP
			try {
				zipOut.write(mos.toByteArray(), 0, mos.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				mos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				zipOut.closeEntry();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				zipOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			zipOut = null;
		}
	    
		// This function parses an XML file and sets up all pages and 
		// all objects of these pages
		void readFromZip ( String fileName ) throws SAXException, TransformerConfigurationException, XMLStreamException, IOException {

			// open the ZIP file
			try {
				zipIn = new ZipFile (fileName);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        
			if (zipIn != null) {
				// clear the existing contents
				while (visuPages.getTabCount() > 0)
					visuPages.removeTabAt (0);
				
				// clear the hardware settings
				hardwareOptDlg.dispose();
			  	hardwareOptDlg = new HardwarePropertiesDialog (this);
			  	hardwareOptDlg.setLocation (220, 120);
		        // load picture archive from project ZIP file
				pictures.clearArchive();
		        pictures.loadLibraryFromArchive (zipIn);
		        // load sound archive from project ZIP file
				sounds.clearArchive();
		        sounds.loadLibraryFromArchive (zipIn);
		        
		        // pull visu from zip
				ZipEntry visuZipObj = zipIn.getEntry("Visu.xml");
				InputStream zipInStream = zipIn.getInputStream(visuZipObj);
				parseXMLStream (zipInStream);
				zipIn.close();
			}
		}

		private void parseXMLStream ( InputStream in ) throws XMLStreamException {
			XMLInputFactory factory = XMLInputFactory.newInstance(); 
			XMLStreamReader parser = factory.createXMLStreamReader( in );			
			JLayeredPane layeredPane = null;
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
			    	if (Element.equals("LcdData")) {
			    		for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
			    			boolean processed = false;
			    			if (parser.getAttributeLocalName( i ) == "X" ) {
			    				dProps.setXSize (Integer.decode (parser.getAttributeValue( i )));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "Y" ) {
			    				dProps.setYSize (Integer.decode (parser.getAttributeValue( i )));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "DisplayOrienation") {
			    				dProps.setOrientationFromXmlString( parser.getAttributeValue(i));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "DisplayType") {
			    				dProps.setTypeFromXmlString( parser.getAttributeValue(i));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "MirrorTouchX") {
			    				dProps.setTouchMirrorXFromXmlString( parser.getAttributeValue(i));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "MirrorTouchY") {
			    				dProps.setTouchMirrorYFromXmlString( parser.getAttributeValue(i));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "HardwareMirrorScreen") {
			    				dProps.setHwMirrorScreenFromXmlString( parser.getAttributeValue(i));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "IdleDimming") {
			    				hardwareOptDlg.setBacklightDefault (Integer.decode( parser.getAttributeValue(i)));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "PhysAddr" ) {
			    				physicalAddress.setPhysicalAddress(parser.getAttributeValue( i ));
			    				processed = true;
			    			}
			    			if (parser.getAttributeLocalName( i ) == "ExportFile" ) {
			    				exportFileName = parser.getAttributeValue( i );
			    				processed = true;
			    			}			    			
			    			if (!processed)
			    				System.out.println ("unprocessed Attribute "+parser.getAttributeLocalName( i ));
			    		}
			    	}
			    	// start new page?
			    	if (Element == "Page") {
			    		layeredPane = loadAddPage (parser);
			    	}
			    	// put element to current page
			    	if (Element == "LED") {
				  		layeredPane.add (new ControlElementLED(this, parser, pictures, sounds));
			    	}
			    	if (Element == "Value") {
				  		layeredPane.add (new ControlElementValue(this, parser));
			    	}
			    	if (Element == "BTN") {
				  		layeredPane.add (new ControlElementButton(this, parser, pictures, sounds));
			    	}
			    	if (Element == "SBTN") {
				  		layeredPane.add (new ControlElementStateButton(this, parser, pictures, sounds));
			    	}
			    	if (Element == "JMP") {
				  		layeredPane.add (new ControlElementJumper(this, parser, pictures, sounds));
			    	}
			    	if (Element == "PICT") {
				  		layeredPane.add (new ControlElementPicture(this, parser, pictures), new Integer (-1));
			    	}
			    	if (Element == "TEXT") {
				  		layeredPane.add (new ControlElementText(this, parser));
			    	}
			    	if (Element == "HWResource") {
			    		hardwareOptDlg.readFromXML (parser);
			    	}
			      break; 
			  } 
			}
			
		}
		
		private JLayeredPane loadAddPage(XMLStreamReader parser) {
			String pageName = "";
			Color pageColor = new Color(80,90,190);
			boolean pageOpaque = false;
			for ( int i = 0; i < parser.getAttributeCount(); i++ ) {
				if (parser.getAttributeLocalName( i ) == "Name")
					pageName = parser.getAttributeValue( i );
				if (parser.getAttributeLocalName( i ) == "Color")
					pageColor = Color.decode (parser.getAttributeValue( i ));
				if (parser.getAttributeLocalName( i ) == "Opaque")
					pageOpaque = true;
			}
			return addPage (pageName, pageColor, pageOpaque);
		}
		
		public void editPageName () {
			int i = visuPages.getSelectedIndex();
			if (i < 0) return;
			
			String s = (String)JOptionPane.showInputDialog(
	                this,
	                "Page name",
	                "Page name:", JOptionPane.PLAIN_MESSAGE,
	                null,
	                null,
	                visuPages.getTitleAt(i));
	
			if ((s != null) && (s.length() > 0)) {
				if (!(visuPages.getTitleAt(i)).equals(s)) {
					if (pageNameIsUnique (s)) {
						fileChanged = true;
						changePageName (visuPages.getTitleAt(i), s);
						visuPages.setTitleAt(i, s);
					}
					else {
						JOptionPane.showMessageDialog(this,"Page names must be unique!", "Page name exists",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}

    
	    public void windowActivated(WindowEvent e) {}
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}

		@Override
		public void actionPerformed(ActionEvent e) {
		  	String str = e.getActionCommand();
		  	guiAction (str);
		}
		
		protected void addPageElement (EIBComp obj) {
	  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  		JLayeredPane layeredPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  		layeredPane.add (obj);
	  		unselectElementsTable ();
	  		validate();
	  		fileChanged = true;
	  		addElementsTable (obj);
	  		selectElementsTableObj (obj);
		}

		public void guiAction (String str) {
		  	if (str == "Quit") {
		  		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		  	}
		  	if (str == "Save") {
		  		if (fileNameValid) {
		  	      File file = fc.getSelectedFile(); 
		  	      System.out.println( "Saving "+file.getAbsolutePath() );
		  	      try {
					saveToZip (file.getAbsolutePath());
			        fileChanged = false;
		  	      } catch (TransformerConfigurationException e1) {
					// TODO Auto-generated catch block
		  	    	  e1.printStackTrace();
		  	      } catch (SAXException e1) {
		  	    	  // TODO Auto-generated catch block
		  	    	  e1.printStackTrace();
		  	      }
		  	    }
		  		else str = "SaveAs";
		  	}

		  	if (str == "SaveAs") {
		  		fc.setDialogTitle ("Save LCD project as...");
		  		int state = fc.showSaveDialog( null ); 
		  	    if ( state == JFileChooser.APPROVE_OPTION ) 
		  	    { 
		  	      File file = fc.getSelectedFile();
		  	      if (!file.getAbsolutePath().endsWith(".lcd") )
		  	    	  file = new File (file.getAbsolutePath() + ".lcd");
		  	      if (file.exists()) {
	    			if ( JOptionPane.showConfirmDialog(null,
	    					"Overwrite file "+ file.getAbsolutePath() +"?", 
	    					"File already exists", 
	    					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION ) {
	    				return;
	    			}
		  	      }
		  	      System.out.println( "Saving "+file.getAbsolutePath() );
		  	      try {
					saveToZip (file.getAbsolutePath());
					fileNameValid = true;
			        setTitle("LCD Modul Editor - "+ file.getName());
			        config.setFileName(file.getAbsolutePath());
			        fileChanged = false;
			      } catch (TransformerConfigurationException e1) {
					// TODO Auto-generated catch block
		  	    	  e1.printStackTrace();
		  	      } catch (SAXException e1) {
		  	    	  // TODO Auto-generated catch block
		  	    	  e1.printStackTrace();
		  	      }
		  	    }
		  	}

		  	if (str == "New") {
	    		if( fileChanged ) {
	    			if ( JOptionPane.showConfirmDialog(null,
	    					"Discard without saving?", 
	    					"Visu not saved", 
	    					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION ) {
	    				return;
	    			}
	    		}
	    		fileChanged = false;
	    		
		        setTitle("LCD Modul Editor");
			  	dProps = new DisplayProperties();
			  	physicalAddress = new PhysicalAddress ();
			  	proOptDlg = new ProjectPropertiesDialog (this); 
			  	proOptDlg.setLocation (200,100);
				// clear the hardware settings
				hardwareOptDlg.dispose();
			  	hardwareOptDlg = new HardwarePropertiesDialog (this);
			  	hardwareOptDlg.setLocation (220, 120);

			  	while (visuPages.getTabCount() > 0)
			  		visuPages.removeTabAt(0);
			  	addPage ("Page 1", defaultColor, true);
		        
		        pictures = new PictureLibrary (this, config);
		        pictureLibraryInspector = pictures.createPictureLibraryEditor(this);
		        pictureLibraryInspector.setLocation(760, 100);
		        pictureLibraryInspector.setVisible(true);
		        sounds = new SoundLibrary (this, config);
		        soundLibraryInspector = sounds.createSoundLibraryEditor(this);
		        soundLibraryInspector.setVisible(false);
		  	}

		  	if (str == "Load") {
	    		if( fileChanged ) {
	    			if ( JOptionPane.showConfirmDialog(null,
	    					"Discard without saving?", 
	    					"Visu not saved", 
	    					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION ) {
	    				return;
	    			}
	    		}
		  		if (config.getFileName().length() != 0) {
		  			File setFile = new File (config.getFileName()); 
		  			fc.setSelectedFile(setFile);
		  		}
		  		fc.setDialogTitle ("Open LCD project");
		  		int state = fc.showOpenDialog( null ); 
		  	    if ( state == JFileChooser.APPROVE_OPTION ) 
		  	    { 
		  	      File file = fc.getSelectedFile(); 
		  	      System.out.println( "Loding "+file.getName() );
		  	      try {
					try {
						readFromZip (file.getAbsolutePath());
				  	  	fileNameValid = true;
				        setTitle("LCD Modul Editor - "+ file.getName());
				        config.setFileName(file.getAbsolutePath());
				        fileChanged = false;
					} catch (XMLStreamException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		  	      } catch (TransformerConfigurationException e1) {
		  	    	  // TODO Auto-generated catch block
		  	    	  e1.printStackTrace();
		  	      } catch (SAXException e1) {
		  	    	  // TODO Auto-generated catch block
		  	    	  e1.printStackTrace();
		  	      }

		  	      refeshElementsTable ();

		  	    } 		  	
		  	}

		  	if (str == "ProjectOptions") {
		  		proOptDlg.setPhysicalAddress (physicalAddress);
		  		proOptDlg.setOrientation (dProps);
		  		proOptDlg.setTouchMirrorX(dProps.getMirrorTouchX());
		  		proOptDlg.setTouchMirrorY(dProps.getMirrorTouchY());
		  		proOptDlg.setHWMirrorLcd(dProps.getMirrorHWLCD());
		  		proOptDlg.setVisible(true);
		  		if (proOptDlg.getModalResult()) {
		  			physicalAddress = proOptDlg.getPhysicalAddress();
		  			dProps = proOptDlg.getDisplayOrientation();
		  			dProps.setMirrorTouchX(proOptDlg.getTouchMirrorX());
		  			dProps.setMirrorTouchY(proOptDlg.getTouchMirrorY());
		  			dProps.setMirrorHWLcd(proOptDlg.getHWMirrorLcd());
		  			setPageSize (new Dimension (dProps.getXSize(), dProps.getYSize()));
		  			fileChanged = true;
		  		}
		  	}

		  	if (str == "HardwareOptions") {
		  		hardwareOptDlg.setVisible(true);
		  		if (hardwareOptDlg.getModalResult()) {
		  			fileChanged = true;
		  		}
		  	}
		  	
		  	if (str == "Delete") {
	  			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  			  			layerdPane.remove(visuElement);
	  			  			removeElementsTable (visuElement);
	  					}
	  				}
	  			}
	  			layerdPane.setVisible(false);
	  			validate();
	  			layerdPane.setVisible(true);
	  			fileChanged = true;
		  	}

		  	if (str == "DelPage") {
		  		if (visuPages.getComponentCount() > 1) {
		  			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
		  			if (isPageNameUsed (visuPages.getTitleAt(visuPages.getSelectedIndex()))) {
						JOptionPane.showMessageDialog(null,
								"This page is still target of a Jumper element.",
								"Can not remove page",
								JOptionPane.ERROR_MESSAGE);	
		  				return;
		  			}
	    			if ( JOptionPane.showConfirmDialog(null,
	    					"Remove selected page?", 
	    					"Delete page", 
	    					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
	    				visuPages.remove(thisPane);
	    				fileChanged = true;
	    			}
		  		}
		  	}
		  	
		  	if (str == "RenamePage") {
		  		editPageName();
		  	}
		  	if (str == "SetupPage") {
		  		editPageProperties();
		  	}
		  	if (str == "MakeStartPage") {
		  		if (visuPages.getSelectedIndex() > 0) {
		  			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
		  			String pageName = visuPages.getTitleAt(visuPages.getSelectedIndex());
    				visuPages.remove(thisPane);
    				visuPages.insertTab(pageName, null, thisPane, null, 0);
    				fileChanged = true;
		  		}
		  	}
		  	
		  	if (str == "Copy") {
		  		// create empty list of copied elements
		  		copyElements = new ArrayList<EditorComponent>();

		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  						copyElements.add(myVisuElement.getClone());
	  					}
	  				}
	  			}
		  	}
		  	
		  	if (str == "Paste") {
		  		if (copyElements != null) {
		  			unselectAllElements ();
					unselectElementsTable ();
		  			for (EditorComponent comp : copyElements) {
				  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
				  		JLayeredPane layeredPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
				  		EditorComponent newComp = (EditorComponent)comp.getClone();
				  		layeredPane.add ( (Component)newComp);
				  		addElementsTable (newComp);
				  		selectElementsTableObj (newComp);
		  			}
			  		validate();
			  		fileChanged = true;
		  		}
		  	}

		  	if (str == "Jumper") {
		  		ControlElementJumper myJumper = new ControlElementJumper(this, 50, 50, 100, 20, getPageNames()[0],
		  																	pictures.getSelectedPictureID(0),pictures.getSelectedPictureID(1), pictures, 
		  																	sounds.getSelectedSoundID(0),sounds.getSelectedSoundID(1), sounds);
		  		addPageElement (myJumper);
		  	}

		  	if (str == "Text") {
		  		ControlElementText myLabel = new ControlElementText(this, 50, 50, 100, 20, "Text?");
		  		addPageElement (myLabel);
		  	}

		  	if (str == "Button") {
		  		ControlElementButton myButton = new ControlElementButton(this, 50, 50, 80, 20, "Caption?", '\u0001', false, '\u0000',
		  											pictures.getSelectedPictureID(0),pictures.getSelectedPictureID(1), pictures,
		  											sounds.getSelectedSoundID(0),sounds.getSelectedSoundID(1), sounds, ButtonFunctionType.FUNCTION_TOGGLE, 0, 0, 0, 0, 0);
		  		addPageElement (myButton);
		  	}
		  	if (str == "S-Button") {
		  		ControlElementStateButton myButton = new ControlElementStateButton(this, 50, 50, 80, 20, "Caption?", '\u0001', false, '\u0002',
		  											pictures.getSelectedPictureID(0),pictures.getSelectedPictureID(1), pictures.getSelectedPictureID(2),
		  											pictures.getSelectedPictureID(3), pictures, 
		  											sounds.getSelectedSoundID(0),sounds.getSelectedSoundID(1), sounds, SButtonFunctionType.FUNCTION_TOGGLE);
		  		addPageElement (myButton);
		  	}

		  	if (str == "LED") {
		  		ControlElementLED myLED = new ControlElementLED(this, 20, 20, 20, 20, '\u0000', '\u0001', 0,
		  								pictures.getSelectedPictureID(0),pictures.getSelectedPictureID(1), pictures, LedVisibleType.LED_VISIBLE_ALWAYS,
		  								sounds.getSelectedSoundID(0),sounds.getSelectedSoundID(1), sounds);
		  		addPageElement (myLED);
		  	}

		  	if (str == "Value") {
		  		ControlElementValue myValue = new ControlElementValue(this, 20, 20, 20, 20, '\u0000');
		  		addPageElement (myValue);
		  	}

		  	if (str == "Inspector") {
		  		if (inspector.isVisible())
		  			inspector.setVisible(false);
		  		else inspector.setVisible(true);
		  	}

		  	if (str == "Elements") {
		  		if (elements.isVisible())
		  			elements.setVisible(false);
		  		else elements.setVisible(true);
		  	}
		  	
		  	if (str == "ElementBar") {
		  		if (objectSelectionDialog.isVisible())
		  			objectSelectionDialog.setVisible(false);
		  		else objectSelectionDialog.setVisible(true);
		  	}

		  	if (str == "Alignment") {
		  		if (formatter.isVisible())
		  			formatter.setVisible(false);
		  		else formatter.setVisible(true);
		  	}

		  	if (str == "PictureLibrary") {
		  		if (pictureLibraryInspector.isVisible())
		  			pictureLibraryInspector.setVisible(false);
		  		else pictureLibraryInspector.setVisible(true);
		  	}

		  	if (str == "SoundLibrary") {
		  		if (soundLibraryInspector.isVisible())
		  			soundLibraryInspector.setVisible(false);
		  		else soundLibraryInspector.setVisible(true);
		  	}
		  	
		  	if (str == "Page") {
		  		String newName = "Page "+(visuPages.getTabCount()+1);
		  		while (!pageNameIsUnique (newName))
		  			newName = '_'+newName;
		        addPage (newName, defaultColor, true);
		  		fileChanged = true;
		  	}
		  	
		  	if (str == "About") {
		  		JOptionPane.showMessageDialog(null,
		  		    "LCD Editor "+PROGRAM_VERSION+'\n' + " (c) 2011-2014 by Arno Stock\n (c) 2014 by Stefan Haller",
		  		    "About LCD Editor",
		  		    JOptionPane.PLAIN_MESSAGE);
		  	}

		  	if (str == "Picture") {
		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
		  		JLayeredPane layeredPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
			  		
		  			// get selected picture from library
		  			int selectedPicureID = pictures.getSelectedPictureID(0);
		  			if (selectedPicureID > 0) {
		  				ControlElementPicture myPict = new ControlElementPicture (this, 0, 0, selectedPicureID, pictures);
		  				layeredPane.add (myPict, new Integer (-1));
						unselectElementsTable ();
		  				validate();
		  				fileChanged = true;
		  		  		addElementsTable (myPict);
		  		  		selectElementsTableObj (myPict);
		  			}
		  	}
		  	
		  	if (str == "Left") {
		  		// create empty list of adjusted elements
		  		ArrayList<EditorComponent> adjustElements = new ArrayList<EditorComponent>();
		  		int lowestX = 319;
		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  						EditorComponent selectedComponent = myVisuElement;
	  						adjustElements.add(selectedComponent);
	  						if (selectedComponent.getXPos() < lowestX)
	  							lowestX = selectedComponent.getXPos();
	  					}
	  				}
	  			}
	  			for (int i = 0; i < adjustElements.size(); i++) {
	  				adjustElements.get(i).setXPos (lowestX);
	  			}
  				fileChanged = true;
		  	}
		  	if (str == "Right") {
		  		// create empty list of adjusted elements
		  		ArrayList<EditorComponent> adjustElements = new ArrayList<EditorComponent>();
		  		int highestX = 0;
		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  						EditorComponent selectedComponent = myVisuElement;
	  						adjustElements.add(selectedComponent);
	  						if ((selectedComponent.getXPos() + selectedComponent.getWidth()) > highestX)
	  							highestX = selectedComponent.getXPos() + selectedComponent.getWidth();
	  					}
	  				}
	  			}
	  			for (int i = 0; i < adjustElements.size(); i++) {
	  				adjustElements.get(i).setXPos (highestX - adjustElements.get(i).getWidth());
	  			}
  				fileChanged = true;
		  	}
		  	if (str == "Top") {
		  		// create empty list of adjusted elements
		  		ArrayList<EditorComponent> adjustElements = new ArrayList<EditorComponent>();
		  		int lowestY = 239;
		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  						EditorComponent selectedComponent = myVisuElement;
	  						adjustElements.add(selectedComponent);
	  						if (selectedComponent.getYPos() < lowestY)
	  							lowestY = selectedComponent.getYPos();
	  					}
	  				}
	  			}
	  			for (int i = 0; i < adjustElements.size(); i++) {
	  				adjustElements.get(i).setYPos (lowestY);
	  			}
  				fileChanged = true;
		  	}
		  	if (str == "Bottom") {
		  		// create empty list of adjusted elements
		  		ArrayList<EditorComponent> adjustElements = new ArrayList<EditorComponent>();
		  		int highestY = 0;
		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  						EditorComponent selectedComponent = myVisuElement;
	  						adjustElements.add(selectedComponent);
	  						if ((selectedComponent.getYPos() + selectedComponent.getHeight() ) > highestY)
	  							highestY = selectedComponent.getYPos() + selectedComponent.getHeight();
	  					}
	  				}
	  			}
	  			for (int i = 0; i < adjustElements.size(); i++) {
	  				adjustElements.get(i).setYPos (highestY - adjustElements.get(i).getHeight());
	  			}
  				fileChanged = true;
		  	}
		  	if (str == "Vertical") {
		  		// create empty list of adjusted elements
		  		ArrayList<EditorComponent> adjustElements = new ArrayList<EditorComponent>();
		  		int lowestY = 239;
		  		int highestY = 0;
		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  						EditorComponent selectedComponent = myVisuElement;
	  						adjustElements.add(selectedComponent);
	  						if (selectedComponent.getYPos() < lowestY)
	  							lowestY = selectedComponent.getYPos();
	  						if (selectedComponent.getYPos() > highestY)
	  							highestY = selectedComponent.getYPos();
	  					}
	  				}
	  			}
	  			if (adjustElements.size() > 1) {
	  				int delta = (highestY - lowestY) / (adjustElements.size()-1);
	  				Comparator<EditorComponent> comparator = new YposComparator();
	  				java.util.Collections.sort( adjustElements, comparator );
	  				for (int i = 0; i < adjustElements.size(); i++) {
	  					adjustElements.get(i).setYPos (lowestY + i*delta);
	  				}
	  			}
	  			else {
	  				adjustElements.get(0).setYPos ( ( dProps.getYSize() - adjustElements.get(0).getHeight()) / 2);
	  			}
  				fileChanged = true;
		  	}

		  	if (str == "Horizontal") {
		  		// create empty list of adjusted elements
		  		ArrayList<EditorComponent> adjustElements = new ArrayList<EditorComponent>();
		  		int lowestX = 319;
		  		int highestX = 0;
		  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  			Component[] visuElements = layerdPane.getComponents();
	  			for (int i = 0; i < visuElements.length; i++) { 
	  				Component visuElement = visuElements[i];
	  				if (EIBComp.class.isInstance (visuElement)) {
	  					EditorComponent myVisuElement = (EditorComponent) visuElement;
	  					if (myVisuElement.isSelected()) {
	  						EditorComponent selectedComponent = myVisuElement;
	  						adjustElements.add(selectedComponent);
	  						if (selectedComponent.getXPos() < lowestX)
	  							lowestX = selectedComponent.getXPos();
	  						if (selectedComponent.getXPos() > highestX)
	  							highestX = selectedComponent.getXPos();
	  					}
	  				}
	  			}
	  			if (adjustElements.size() > 1) {
		  			int delta = (highestX - lowestX) / (adjustElements.size()-1);
		  			Comparator<EditorComponent> comparator = new XposComparator();
		  			java.util.Collections.sort( adjustElements, comparator );
		  			for (int i = 0; i < adjustElements.size(); i++) {
		  				adjustElements.get(i).setXPos (lowestX + i*delta);
		  			}
	  			}
	  			else {
	  				adjustElements.get(0).setXPos ( ( dProps.getXSize() - adjustElements.get(0).getWidth()) / 2);
	  			}
  				fileChanged = true;
		  	}

		  	
		  	if (str == "Export") {
		  		
		  		int fileSize = 0;

		  		// create file for data export to SD card
		
		  		if (exportFileName == "") {
		  			int dot = config.getFileName().lastIndexOf('.');
		  			exportFileName = (dot == -1) ? config.getFileName() : config.getFileName().substring(0, dot);
		  		}
		  		ec.setCurrentDirectory(config.getOutDirectory());
		  		ec.setSelectedFile (new File (exportFileName));
		  		int state = ec.showSaveDialog( null );
		  	    if ( state != JFileChooser.APPROVE_OPTION ) {
		  	    	return;
		  	    }
		  	    config.setOutDirectory(ec.getCurrentDirectory());
		  	    
		  	    File file = ec.getSelectedFile();
		  	    if (!file.getAbsolutePath().endsWith(".lcdb") )
		  	    	file = new File (file.getAbsolutePath() + ".lcdb");
		  	    if (file.exists()) {
		  	    	if ( JOptionPane.showConfirmDialog(null,
	    				"Overwrite file "+ file.getAbsolutePath() +"?", 
	    				"File already exists", 
	    				JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION )
	    			return;
	    		}
		  	    System.out.println( "Exporting LCD data to "+file.getAbsolutePath() );
		  	    if (file.getAbsolutePath() != exportFileName) {
		  	    	  exportFileName = file.getAbsolutePath();
		  	    }
		  	      // open binary file
		  	    DataOutputStream os;
		  	    BufferedOutputStream bs;
		  	    try {
						bs = new BufferedOutputStream (new FileOutputStream(file.getAbsoluteFile()));
						os = new DataOutputStream(bs);
	  	    	  } catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return;
	  	    	  }
								
	  	    	  try {
			  	      LcdFileHeader Header = new LcdFileHeader();
	  	    		  // set project source file name
			  	      System.out.println( "Writing project file name " + config.getFileName());
	  	    		  Header.setSourceFileName (config.getFileName());
	  	    		  Header.setSourceFileComment("");
	  	    		  Header.setPhysicalAddress(physicalAddress.getPhysicalAddress());
	  	    		  Header.setDisplayOrientation(dProps.getOrientationCode(), dProps.getDisplayTypeCode(),
	  	    				  dProps.getMirrorTouchX(), dProps.getMirrorTouchY(), dProps.getMirrorHWLCD());

	  	    		  // create data table objects

	  	    		  // pages
	  	    		  LcdPageContainer Pages = new LcdPageContainer();

	  	    		  // EIB objects
	  	    		  // EIB Addresses (for ACK and receive)
	  	    		  LcdEibAddresses GroupAddr = new LcdEibAddresses(); 
	  	    		  
	  	    		  // always listening objects
	  	    		  LcdListenerContainer Listeners = new LcdListenerContainer ();

	  	    		  // always listening objects
	  	    		  LcdTimeoutContainer Timeouts = new LcdTimeoutContainer ();

	  	    		  // cyclic objects
	  	    		  LcdCyclicContainer Cyclics = new LcdCyclicContainer ();
	  	    		  
	  	    		  // Images
	  	    		  LcdImageContainer Images = new LcdImageContainer();
	  	    		  
	  	    		  // Sounds
	  	    		  LcdSoundContainer Sounds = new LcdSoundContainer();

	  	    		  //TOC
	  	    		  LcdFileTOC Toc = new LcdFileTOC (Pages, Images, GroupAddr, Sounds, Listeners, Cyclics);
	  	    		  
	  	    		  // pre-process visu pages to collect all EIB addresses from the objects
	  	    		  for ( int i = 0; i < visuPages.getTabCount(); i++ ) {  

	  	    			  // get Layered pane
	  	    			  JScrollPane scrollPane = (JScrollPane)visuPages.getComponentAt(i);
	  	    			  JViewport viewPort = (JViewport)scrollPane.getComponent(0);
	  	    			  JLayeredPane layeredPane = (JLayeredPane)viewPort.getComponent(0);

	  	    			  // get components on this page
	  	    			  Component[] comp = layeredPane.getComponentsInLayer(new Integer (-1));
	  	    			  // Loop all background elements on page
	  	    			  for ( Component thisComp : comp ) {
	  	    				  if (EIBComp.class.isInstance (thisComp)) {
	  	    					  EditorComponent co = (EditorComponent)thisComp;
	  	    					  co.registerEibAddresses (GroupAddr);
	  	    				  }
	  	    			  }

	  	    			  // get components on this page
	  	    			  comp = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);

	  	    			  // Loop all elements on page
	  	    			  for ( Component thisComp : comp ) {
	  	    				  if (EIBComp.class.isInstance (thisComp)) {
	  	    					  EditorComponent co = (EditorComponent)thisComp;
	  	    					  co.registerEibAddresses (GroupAddr);
	  	    				  }
	  	    			  }
	  	    		  }
	  	    		  
	  	    		  // parse HW options and feed Listener and Cyclic processing objects
	  	    		  hardwareOptDlg.registerEibAddresses (GroupAddr);
	  	    		  
	  	    		  // parse visu data and feed tables
	  	    		  for ( int i = 0; i < visuPages.getTabCount(); i++ ) {  

	  	    			  // get Layered pane
	  	    			  JScrollPane scrollPane = (JScrollPane)visuPages.getComponentAt(i);
	  	    			  JViewport viewPort = (JViewport)scrollPane.getComponent(0);
	  	    			  JLayeredPane layeredPane = (JLayeredPane)viewPort.getComponent(0);
	  					
	  	    			  Pages.addPage(visuPages.getTitleAt(i));
	  	    			  // set background color, if enabled
	  	    			  if (layeredPane.isOpaque()) {
	  	    				  // set page color
	  	    				  ControlElementPageBackgroundColor backgroundComponent = new ControlElementPageBackgroundColor (layeredPane.getBackground().getRGB());
	  	    				  backgroundComponent.outputToLcdFile(Images, Pages, GroupAddr, Sounds, null, null, dProps, Listeners, null, i);
	  	    			  }

	  	    			  // get background components on this page
	  	    			  Component[] backgroundComp = layeredPane.getComponentsInLayer(new Integer (-1));
	  	    			  // Loop all background elements on page
	  	    			  for ( Component thisComp : backgroundComp ) {
	  	    				  if (EIBComp.class.isInstance (thisComp)) {
	  	    					  EditorComponent co = (EditorComponent)thisComp;
	  	    					  co.outputToLcdFile(Images, Pages, GroupAddr, Sounds, backgroundComp, layeredPane.getBackground(),dProps, Listeners, Timeouts, i);
	  	    				  }
	  	    			  }
	  					
	  	    			  // get components on this page
	  	    			  Component[] comp = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);

	  	    			  // Loop all elements on page
	  	    			  for ( Component thisComp : comp ) {
	  	    				  if (EIBComp.class.isInstance (thisComp)) {
	  	    					  EditorComponent co = (EditorComponent)thisComp;
	  	    					  co.outputToLcdFile(Images, Pages, GroupAddr, Sounds, backgroundComp, layeredPane.getBackground(), dProps, Listeners, Timeouts, i);
	  	    				  }
	  	    			  }
	  	    			  
	  	    		  }  // end of page description
	  	    		  
	  	    		  // parse HW options and feed Listener and Cyclic processing objects
	  	    		  hardwareOptDlg.outputToLcdFile (Listeners, Cyclics, GroupAddr);

	  	    		  // write containers to file
	  	    		  System.out.println( "Writing file components.");
	  	    		  Header.writeToFile(os);
	  	    		  Toc.writeToFile(os);
	  	    		  Pages.writeToFile(os);
	  	    		  GroupAddr.writeToFile(os);
	  	    		  Images.writeToFile(os, dProps);
	  	    		  Sounds.writeToFile(os);
	  	    		  Listeners.writeToFile(os);
	  	    		  Cyclics.writeToFile(os);
	  	    		  
	  	    	  } 
	  	    	  catch (IOException e) {
	  	    		  // TODO Auto-generated catch block
	  	    		  e.printStackTrace();
			  	      System.out.println( "Error writing to file.");
	  	    	  }
	  	    	  try {
			  	      System.out.println( "Closing file.");
			  	      bs.flush();
			  	      fileSize = os.size();
	  	    		  os.close();
	  	    	  } catch (IOException e) {
	  	    		  // TODO Auto-generated catch block
	  	    		  e.printStackTrace();
	  	    	  }
	  	      
	  	      // end of export
	  	      if (fileSize > MAX_FLASH_SIZE) {
	  	      JOptionPane.showMessageDialog(null, "Export finished. File size too long!", "Warning", JOptionPane.WARNING_MESSAGE);
	  	      }
	  	      else {
		  	      JOptionPane.showMessageDialog(null, "Export finished.", "Information", JOptionPane.INFORMATION_MESSAGE);
	  	      }
		  	}
	  	}
		
		public JButton addButton(String btnLabel, String btnCmd) {
	        JButton btn = new JButton (btnLabel);
	        btn.setActionCommand(btnCmd);
	        btn.setBackground(Color.lightGray);
	        btn.addActionListener(this);
	        return btn;
		}

	    public JButton addIconButton(String iconName, String btnLabel, String btnCmd) {
	    	JButton btn;
	    	if (ClassLoader.getSystemResource(iconName) != null)
	    		btn = new JButton (new ImageIcon (ClassLoader.getSystemResource(iconName)));
    		else btn = new JButton (new ImageIcon (iconName));
	        btn.setToolTipText(btnLabel);
	        btn.setActionCommand(btnCmd);
	        btn.setBackground(Color.lightGray);
	        btn.addActionListener(this);
	        return btn;
		}
	    
	    public JDialog addPropertiesDialog (){
	    	final JDialog dialog = new JDialog(this,
            "Element Inspector");
			dialog.setVisible(false);	 

			//Add contents to it. It must have a close button,
			//since some L&Fs (notably Java/Metal) don't provide one
			//in the window decorations for dialogs.
	    	tableModel = new PropertiesTableModel();
	    	tableModel.addColumn("Item");
	    	tableModel.addColumn("Value");
	    	myPropertiesTable = new JTable (tableModel);
	    	myPropertiesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	    	myPropertiesTable.setDefaultRenderer(Object.class, new PropertiesTableCellRenderer ());
	    	myPropertiesTable.setDefaultEditor(Object.class, new ExtendedTableCellEditor());
	    	Object[][] data = {{}};
	    	setTable (data, null);
	    	
	    	JScrollPane scrollPane = new JScrollPane(myPropertiesTable);
	    	myPropertiesTable.setFillsViewportHeight(true);
			
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
			return dialog;
	    }
	    
	    public JDialog addAlignmentDialog (){
	    	final JDialog dialog = new JDialog(this,
            "Alignment");

			dialog.setVisible(false);	 
			//Add contents to it. It must have a close button,
			//since some L&Fs (notably Java/Metal) don't provide one
			//in the window decorations for dialogs.
	    	
	    	JPanel myButtonPanel = new JPanel();
	    	myButtonPanel.add(addIconButton ("toolbarButtonGraphics/general/AlignLeft24.gif","Align Left", "Left"));
	    	myButtonPanel.add(addIconButton ("toolbarButtonGraphics/general/AlignRight24.gif","Align Right", "Right"));
	    	myButtonPanel.add(addIconButton ("toolbarButtonGraphics/general/AlignBottom24.gif","Align Bottom", "Bottom"));
	    	myButtonPanel.add(addIconButton ("toolbarButtonGraphics/general/AlignTop24.gif","Align Top", "Top"));
	    	myButtonPanel.add(addIconButton ("toolbarButtonGraphics/general/AlignJustifyHorizontal24.gif","Justify Horizontal", "Horizontal"));
	    	myButtonPanel.add(addIconButton ("toolbarButtonGraphics/general/AlignJustifyVertical24.gif","Justify Vertical", "Vertical"));
	    	JScrollPane scrollPane = new JScrollPane(myButtonPanel);
			
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
			dialog.setSize(new Dimension(420, 110));
			return dialog;
	    }
	
	    public JDialog addElementsDialog (){
	    	final JDialog dialog = new JDialog(this,
            "Page Elements");
			dialog.setVisible(false);	 

			//Add contents to it. It must have a close button,
			//since some L&Fs (notably Java/Metal) don't provide one
			//in the window decorations for dialogs.
	    	elementsTableModel = new DefaultTableModel() {

				private static final long serialVersionUID = -2304903819507076010L;

				@Override
	    		   public boolean isCellEditable(int row, int column) {
	    		       //Only the third column
	    		       return column == 3;
	    		   }
	    		};
	    	elementsTableModel.addColumn("Element");
	    	myElementsTable = new JTable (elementsTableModel);
	    	myElementsTable.getSelectionModel().addListSelectionListener(this);
	    	
	    	JScrollPane scrollPane = new JScrollPane(myElementsTable);
	    	myElementsTable.setFillsViewportHeight(true);
			
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
			dialog.setSize(new Dimension(150, 200));
			dialog.setLocationRelativeTo(null);
			return dialog;
	    }

	    
	    public void setTable (Object[][] tableData, String elementType) {
    		// remove old contents
	    	flushInspector ();
    		// add new data
    		for (int row = 0; row < tableData.length; row++)
    			tableModel.addRow (tableData[row]);
    		// set window title
    		if (elementType != null)
    			inspector.setTitle("Element Inspector: " + elementType);
	    }
	    
	    public void clearElementsTable () {

	    	// remove old contents
    		elementsTableModel.setRowCount (0);
	    
	    }
	    
	    public void removeElementsTable (Object obj) {
	    	
			for (int row = 0; row < myElementsTable.getRowCount(); row++) {
				if (elementsTableModel.getValueAt(row, 0).equals(obj)) {
			    	elementsTableModel.removeRow(row);
				}
			}
	    }
	    
	    public void addElementsTable (Object obj) {
    		// add new data
	    	Object[] data = {obj};
	    	elementsTableModel.addRow (data);
	    }

	    
	    protected boolean getHostAddress () {
	    	String s = (String)JOptionPane.showInputDialog(
                this,
                "Address:",
                "Host Address", JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                config.getServer());

	    	//If a string was returned, say so.
	    	if ((s != null) && (s.length() > 0)) {
	    		config.setServer(s);
	    		return true;
	    	}
	    	return false;
	    }

	    public PropertiesTableModel getInspector () {
	    	return tableModel;
	    }

	    public void flushInspector () {
	    	
	    	if (myPropertiesTable.isEditing()) {
	    		try {
	    			myPropertiesTable.getCellEditor().stopCellEditing();
	    		}
	    		catch (NumberFormatException e) {
	    			myPropertiesTable.getCellEditor().cancelCellEditing();
	    		}
	    	}
	    	// remove old contents
	    	tableModel.setRowCount (0);
   	
	    }
	    
	    protected void processWindowEvent(WindowEvent e) {
	    	if (e.getID() == WindowEvent.WINDOW_CLOSING) {
	    		if( fileChanged ) {
	    			if ( JOptionPane.showConfirmDialog(null,
	    					"Quit without saving?", 
	    					"Visu not saved", 
	    					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION ) {
	    				return;
	    			}
	    		}
    	        dispose(); // nicht unbedingt n�tig.
	    	    System.exit(0);
	    	}
	    	super.processWindowEvent(e);
	    }
	       
	    public void unselectAllElements () {
  			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
  			if (thisPane == null)
  				return;
  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
  			Component[] visuElements = layerdPane.getComponents();
  			for (int i = 0; i < visuElements.length; i++) { 
  				Component visuElement = visuElements[i];
  				if (EIBComp.class.isInstance (visuElement)) {
  					EditorComponent myVisuElement = (EditorComponent) visuElement;
  					myVisuElement.setSelected (false);
  				}
  			}
  			layerdPane.setVisible(false);
  			layerdPane.setVisible(true);
	    }
	    
	    private void unselectElementsTable() {
	    	myElementsTable.clearSelection();
    		flushInspector ();
		}

		public void moveAllSelected (int dx, int dy){
  			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
  			JLayeredPane layerdPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
  			Component[] visuElements = layerdPane.getComponents();
  			for (int i = 0; i < visuElements.length; i++) { 
  				Component visuElement = visuElements[i];
  				if (EIBComp.class.isInstance (visuElement)) {
  					EditorComponent myVisuElement = (EditorComponent) visuElement;
  					if (myVisuElement.isSelected()) {
  	  					Point p = ((EIBComp)myVisuElement).getLocation();
  	  					p.translate(dx, dy);
  	  					((EIBComp)myVisuElement).setLocation( p );
  					}
  				}
  				invalidate();
  			}
  			layerdPane.setVisible(false);
  			layerdPane.setVisible(true);
			fileChanged = true;
	    }
	    
	    public void selectElement (Object sender, boolean unselectOthers) {

			if (!((EditorComponent)sender).isSelected()) {
				if (unselectOthers) {
					unselectElementsTable();
				}
				selectElementsTableObj (sender);
			}
			else if (!unselectOthers) {
				unselectElementsTableObj (sender);
			}
			((EIBComp)sender).setVisible (false); 
			((EIBComp)sender).setVisible (true);
	    }

	    void editPageProperties () {
	  		JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
	  		JLayeredPane layeredPane = (JLayeredPane )thisPane.getViewport().getComponent(0);
	  		pageOptDlg.setColor(layeredPane.getBackground());
	  		pageOptDlg.setOpac(layeredPane.isOpaque());
	  		pageOptDlg.setVisible(true);
	  		if (pageOptDlg.getModalResult()) {
	  			layeredPane.setBackground(pageOptDlg.getColor());
	  			layeredPane.setOpaque(pageOptDlg.getOpac());
		  		layeredPane.setVisible(false);
		  		layeredPane.setVisible(true);
		  		fileChanged = true;
	  		}
	    }
	    
		final int SYSTEM_PAGES = 2;
		final int SYSTEM_PAGE_SETUP = 0;
		final String SYSTEM_PAGE_SETUP_NAME = "Setup";
		final int SYSTEM_PAGE_LOCK = 1;
		final String SYSTEM_PAGE_LOCK_NAME = "Lock";

		public String[] getPageNames() {
			
			String[] pageNames = new String[visuPages.getTabCount() + SYSTEM_PAGES];
			// define fixed system pages
			pageNames[SYSTEM_PAGE_SETUP] = SYSTEM_PAGE_SETUP_NAME;
			pageNames[SYSTEM_PAGE_LOCK] = SYSTEM_PAGE_LOCK_NAME;
			for ( int i = 0; i < visuPages.getTabCount(); i++ ) {
				pageNames[i + SYSTEM_PAGES] = visuPages.getTitleAt(i);
			}
			return pageNames;
		}
		
		public PictureLibrary getPictureLibrary () {
			return pictures;
		}
		
		public int getPageIndex (String pageName) {
			
			for ( int i = 0; i < visuPages.getTabCount(); i++ ) {  
				if (pageName.equals(visuPages.getTitleAt(i)))
					return i + SYSTEM_PAGES;
			}
			if (pageName.equals(SYSTEM_PAGE_SETUP_NAME))
				return SYSTEM_PAGE_SETUP;
			if (pageName.equals(SYSTEM_PAGE_LOCK_NAME))
				return SYSTEM_PAGE_LOCK;
			System.err.println ("Couldn't find page " + pageName);
			return 0;
		}
		
		public void selectPage(String page) {

			int i;
			i = getPageIndex (page);
			if (i < SYSTEM_PAGES)
				return;
			visuPages.setSelectedIndex(i-SYSTEM_PAGES);			
		}
		
	    public void mouseReleased(MouseEvent event) {
	
	        Rectangle markedArea = null;

	        if (startPoint != null) {
				markedArea = new Rectangle (Math.min (startPoint.x, event.getPoint().x),
													  Math.min (startPoint.y, event.getPoint().y),
													  Math.abs (event.getPoint().x - startPoint.x),
													  Math.abs (event.getPoint().y - startPoint.y ));
	        }
			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
  			JLayeredVisuPane layerdPane = (JLayeredVisuPane )thisPane.getViewport().getComponent(0);
  			layerdPane.setSelectionRect (null);
  			Component[] visuElements = layerdPane.getComponents();
  			for (int i = 0; i < visuElements.length; i++) { 
  				Component visuElement = visuElements[i];
  				if (EIBComp.class.isInstance (visuElement)) {
  					JPanel myVisuElement = (JPanel) visuElement;
  					//check, if element is in the marked area
  					if ((markedArea != null) && (markedArea.contains ( myVisuElement.getBounds() ) ) ) {
  	  					selectElementsTableObj (myVisuElement);
  					}
  				}
  				invalidate();
  			}
  			layerdPane.setVisible(false);
  			layerdPane.setVisible(true);
	    }

	    public void mouseEntered(MouseEvent event) {}
	    public void mouseExited(MouseEvent event) {}

	    @Override
		public void mouseDragged(MouseEvent event) {
	    	//paint recangle
	    	currentPoint = event.getPoint();
	    	
			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
  			JLayeredVisuPane layerdPane = (JLayeredVisuPane )thisPane.getViewport().getComponent(0);
	        if (startPoint != null) {
	        	Rectangle markedArea = new Rectangle (Math.min (startPoint.x, event.getPoint().x),
	        			Math.min (startPoint.y, event.getPoint().y),
	        			Math.abs (event.getPoint().x - startPoint.x),
	        			Math.abs (event.getPoint().y - startPoint.y ));
	        	layerdPane.setSelectionRect (markedArea);
	  			layerdPane.repaint();
	        }
		}

		@Override
		public void mouseMoved(MouseEvent event) {
		}
		
		@Override
		public void mousePressed(MouseEvent event) {
			if (event.getClickCount() == 1) {
				unselectElementsTable ();
		        startPoint = event.getPoint();
		        currentPoint = startPoint;
			}
		}

		public void mouseClicked(MouseEvent e) {
			int i = visuPages.getSelectedIndex();
			if (i < 0) return;
			if (e.getClickCount() != 2)
					return;
			if (JLayeredVisuPane.class.isInstance(e.getSource()) ){
				editPageProperties(); 
			}
			else {
				editPageName();
			}
	    }

		public void objectPropertiesChanged() {
	  		fileChanged = true;
		}
		
		public ArrayList<EditorComponent> getAllVisuComponents () {

			ArrayList<EditorComponent> c = new ArrayList<EditorComponent>();
			// parse visu data and collect all objects
    		for ( int i = 0; i < visuPages.getTabCount(); i++ ) {  

    			  // get Layered pane
    			  JScrollPane scrollPane = (JScrollPane)visuPages.getComponentAt(i);
    			  JViewport viewPort = (JViewport)scrollPane.getComponent(0);
    			  JLayeredPane layeredPane = (JLayeredPane)viewPort.getComponent(0);
				
    			  // get background components on this page
    			  Component[] backgroundComp = layeredPane.getComponentsInLayer(new Integer (-1));
    			  // Loop all background elements on page
    			  for ( Component thisComp : backgroundComp ) {
    				  if (EIBComp.class.isInstance (thisComp)) {
    					  EditorComponent co = (EditorComponent)thisComp;
    					  c.add(co);
    				  }
    			  }
				
    			  // get components on this page
    			Component[] comp = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
    			  // Loop all elements on page
    			  for ( Component thisComp : comp ) {
    				  if (EIBComp.class.isInstance (thisComp)) {
    					  EditorComponent co = (EditorComponent)thisComp;
    					  c.add(co);
    				  }
    			  }
    			  
    		  }  // end of page description
    		return c;
		}

		public boolean checkImageInUse (int id) {
			
			ArrayList<EditorComponent> comp = getAllVisuComponents ();
			for (int i = 0; i < comp.size(); i++) {
				if (comp.get(i).isPictureInUse (id)) {
					return true;
				}
			}
			return false;
		}

		public boolean checkSoundInUse(int id) {
			ArrayList<EditorComponent> comp = getAllVisuComponents ();
			for (int i = 0; i < comp.size(); i++) {
				if (comp.get(i).isSoundInUse (id)) {
					return true;
				}
			}
			return false;
		}

		// this function scans all Jumpers and exchanges the Target name,
		// if the user renamed the respective page
		private boolean isPageNameUsed (String name) {
			// Loop all pages
			for ( int i = 0; i < visuPages.getTabCount(); i++ ) {  

				// get Layered pane
				JScrollPane scrollPane = (JScrollPane)visuPages.getComponentAt(i);
				JViewport viewPort = (JViewport)scrollPane.getComponent(0);
				JLayeredPane layeredPane = (JLayeredPane)viewPort.getComponent(0);
				
				// get components on this page
				Component[] comp = layeredPane.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);

				// Loop all elements on page
				for ( Component thisComp : comp ) {
					if (EIBComp.class.isInstance (thisComp)) {
						EditorComponent co = (EditorComponent)thisComp;
						if (co.isPageNameUsed (name)) {
							return true;
						}
					}
				}
			}
			return false;
		}

		public void setPageSize(Dimension d) {
			
			for ( int i = 0; i < visuPages.getTabCount(); i++ ) {
				// get Layered pane
				JScrollPane scrollPane = (JScrollPane)visuPages.getComponentAt(i);
				JViewport viewPort = (JViewport)scrollPane.getComponent(0);
				JLayeredVisuPane layeredPane = (JLayeredVisuPane)viewPort.getComponent(0);
				layeredPane.setPreferredSize(d);
				scrollPane.invalidate();
			}
			visuPages.repaint();
		}

		public Integer getMaxX () {
			return dProps.getXSize();
		}

		public Integer getMaxY () {
			return dProps.getYSize();
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {

			flushInspector();
			unselectAllElements ();
			refeshElementsTable ();

		}
		
		private void refeshElementsTable () {
			
			clearElementsTable ();
			
			JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
			if (thisPane == null) {
				return;
			}
  			JLayeredVisuPane layerdPane = (JLayeredVisuPane )thisPane.getViewport().getComponent(0);
  			layerdPane.setSelectionRect (null);
  			Component[] visuElements = layerdPane.getComponents();
  			for (int i = 0; i < visuElements.length; i++) { 
  				Component visuElement = visuElements[i];
  				if (EIBComp.class.isInstance (visuElement)) {
  					addElementsTable (visuElement);
  				}
  			}
			
		}

		private void selectElementsTableObj (Object obj) {

			for (int row = 0; row < myElementsTable.getRowCount(); row++) {
				
				if (elementsTableModel.getValueAt(row, 0).equals(obj)) {
					myElementsTable.addRowSelectionInterval(row, row);
				}
			}
			
		}
		
		private void unselectElementsTableObj (Object obj) {

			for (int row = 0; row < myElementsTable.getRowCount(); row++) {
				
				if (elementsTableModel.getValueAt(row, 0).equals(obj)) {
					myElementsTable.removeRowSelectionInterval(row, row);
				}
			}
			
		}

		@Override
		public void valueChanged(ListSelectionEvent selEvt) {
			// TODO Auto-generated method stub
			if (!selEvt.getValueIsAdjusting()) {
				unselectAllElements ();
				boolean isFirst = true;
				for (int row = 0; row < myElementsTable.getRowCount(); row++) {
					if (myElementsTable.isRowSelected(row)) {
						((EditorComponent)elementsTableModel.getValueAt(row, 0)).setSelected (true);
						if (isFirst) {
							((EIBComp)elementsTableModel.getValueAt(row, 0)).fillEditor ();
							isFirst = false;
						}
					}
				}
				JScrollPane thisPane = (JScrollPane)visuPages.getSelectedComponent();
		  		JLayeredVisuPane layerdPane = (JLayeredVisuPane )thisPane.getViewport().getComponent(0);
		  		layerdPane.setVisible(false);
	  			layerdPane.setVisible(true);
			}
		}

		
}
