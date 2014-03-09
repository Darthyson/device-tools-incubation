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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class PagePropertiesDialog extends JDialog implements ActionListener, ItemListener, FocusListener {

    JRadioButton btnTransparent; 
    JRadioButton btnOpac;
    JButton colorButton;
    Boolean modalResult;
    PhysicalAddress physicalAddress;
		
    PagePropertiesDialog(Frame myFrame) {
        super(myFrame, "Page Options", true);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        JPanel transparencyPanel = new JPanel ();
        transparencyPanel.setLayout(new BoxLayout (transparencyPanel, BoxLayout.Y_AXIS));
        JPanel stdPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));
        JPanel usrPanel = new JPanel (new FlowLayout (FlowLayout.LEFT));
	        
        btnTransparent = new JRadioButton ("Transparent");
        btnOpac = new JRadioButton ("Fill");
        btnTransparent.addItemListener(this);
        btnOpac.addItemListener(this);
        ButtonGroup bg = new ButtonGroup();
			bg.add( btnTransparent );
			bg.add( btnOpac );
        colorButton = new JButton("Color");
        colorButton.addActionListener(this);
	        
        stdPanel.add(new JLabel ("Page background:"));
        usrPanel.add(btnTransparent);
        usrPanel.add(btnOpac);
	        
        usrPanel.add (colorButton);
	    transparencyPanel.add("North", stdPanel);
	    transparencyPanel.add("South", usrPanel);
        add("Center", transparencyPanel);
	        
        JPanel btnPanel = new JPanel ();
        JButton Ok = new JButton("OK");
        Ok.addActionListener(this);
        Ok.setActionCommand("ok");
	        
        JButton Cancel = new JButton("Cancel");
        Cancel.addActionListener(this);
        Cancel.setActionCommand("cancel");

        btnPanel.add(Ok);
        btnPanel.add(Cancel);
        add("South", btnPanel);
	        
        pack();
    }
		
    public void actionPerformed(ActionEvent event) {
    	if (event.getSource().equals(colorButton)) {
	  		Color newColor = JColorChooser.showDialog( 
 					null, "Choose new page background color", colorButton.getBackground() ); 
	  		colorButton.setBackground(newColor);
	   		return;
	   	}
	    		
	   	modalResult = (event.getActionCommand().equals("ok"));
	   	setVisible(false);
    }
	    
    public Boolean getModalResult () {
    	return modalResult;
    }

    public void processEvent(AWTEvent e) {
        if (e.getID() == Event.WINDOW_DESTROY) {
            dispose();
        } else {
            super.processEvent(e);
        }
    }

	@Override
	public void itemStateChanged(ItemEvent e) {
		colorButton.setEnabled(btnOpac.isSelected());
	}
	    
	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void focusLost(FocusEvent e) {
	}
		
	public Boolean getOpac() {
		return btnOpac.isSelected();
	}
		
	public void setOpac (boolean Opac) {
		if (Opac)
			btnOpac.setSelected(true);
		else btnTransparent.setSelected (true);
		colorButton.setEnabled(btnOpac.isSelected());
	}
		
	public Color getColor () {
		return colorButton.getBackground();
	}
		
	public void setColor (Color color) {
		colorButton.setBackground (color);
	}
}
