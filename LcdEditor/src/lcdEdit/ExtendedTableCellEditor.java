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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class ExtendedTableCellEditor extends AbstractCellEditor implements
TableCellEditor, ItemListener, ActionListener{

	final JTextField textField = new JTextField ();
	final JCheckBox booleanField;
	private JFontChooser fontChooser;
	private JComboBox<String> selectBox;
	private Object value;
    JButton colorButton;
    JButton fontButton;
    JColorChooser colorChooser;
    protected static final String EDIT_COLOR = "editColor";
    protected static final String EDIT_FONT = "editFont";
    JDialog colorDialog;
    JDialog fontDialog;
	
	ExtendedTableCellEditor () {
		super ();
		colorButton = new JButton();
		colorButton.setActionCommand(EDIT_COLOR);
		colorButton.addActionListener(this);
		colorButton.setBorderPainted(false);
		
		//Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        colorDialog = JColorChooser.createDialog(colorButton,
                                        "Pick a Color",
                                        true,  //modal
                                        colorChooser,
                                        this,  //OK button handler
                                        null); //no CANCEL button handler
        
		fontButton = new JButton();
		fontButton.setActionCommand(EDIT_FONT);
		fontButton.addActionListener(this);
		fontButton.setBorderPainted(false);
		
		fontChooser = new JFontChooser ();
		fontDialog = fontChooser.createDialog(null);
		
		booleanField = new JCheckBox();
		booleanField.addActionListener(this);
		
		String defaultPage[] = {""};
		selectBox = new JComboBox<String> (defaultPage);
		selectBox.addItemListener(this);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int rowIndex, int colIndex) {
		
		this.value = value;

		if (value.getClass().getName().contains("Boolean")) {
			booleanField.setSelected((Boolean) value);
			return booleanField;
		}
		if (value.getClass().getName().contains("PageNames")) {
			String pageNames[] = ((PageNames) value).getPageNames();
			selectBox.removeItemListener(this);
			selectBox.removeAllItems();
			for (int i = 0; i < pageNames.length; i++)
				selectBox.addItem(pageNames[i]);
			selectBox.addItemListener(this);
			selectBox.setSelectedItem((String)value.toString());
			return selectBox;
		}
		if (value.getClass().getName().contains("PictureNames")) {
			selectBox.removeItemListener(this);
			String pictureNames[] = ((PictureNames)value).getPictureNames();
			selectBox.removeAllItems();
			for (int i = 0; i < pictureNames.length; i++)
				selectBox.addItem(pictureNames[i]);
			selectBox.addItemListener(this);
			selectBox.setSelectedIndex(((PictureNames)value).getSelectedIndex());
			return selectBox;
		}
		if (value.getClass().getName().contains("SoundNames")) {
			String soundNames[] = ((SoundNames)value).getSoundNames();
			selectBox.removeItemListener(this);
			selectBox.removeAllItems();
			for (int i = 0; i < soundNames.length; i++)
				selectBox.addItem(soundNames[i]);
			selectBox.addItemListener(this);
			selectBox.setSelectedIndex(((SoundNames)value).getSelectedIndex());
			return selectBox;
		}
		if (value.getClass().getName().contains("Font")) {
		    fontChooser.setSelectedFont((Font) value);
			return fontButton;
		}
		if (value.getClass().getName().contains("Color")) {
			colorChooser.setColor((Color) value);
			return colorButton;
		}
		if (value.getClass().getName().contains("ButtonFunctionType")) {
			String functionNames[] = ButtonFunctionType.getFunctionTypes();
			selectBox.removeItemListener(this);
			selectBox.removeAllItems();
			for (int i = 0; i < functionNames.length; i++)
				selectBox.addItem(functionNames[i]);
			selectBox.addItemListener(this);
			selectBox.setSelectedIndex(((ButtonFunctionType)value).getButtonFunction());
			return selectBox;
		}
		if (value.getClass().getName().contains("ValueFormatType")) {
			String functionNames[] = ((ValueFormatType)value).getFunctionTypes();
			selectBox.removeItemListener(this);
			selectBox.removeAllItems();
			for (int i = 0; i < functionNames.length; i++)
				selectBox.addItem(functionNames[i]);
			selectBox.addItemListener(this);
			selectBox.setSelectedIndex(((ValueFormatType)value).getValueFormat());
			return selectBox;
		}
		if (value.getClass().getName().contains("LedFunctionType")) {
			String functionNames[] = ((LedFunctionType)value).getFunctionTypes();
			selectBox.removeItemListener(this);
			selectBox.removeAllItems();
			for (int i = 0; i < functionNames.length; i++)
				selectBox.addItem(functionNames[i]);
			selectBox.addItemListener(this);
			selectBox.setSelectedIndex(((LedFunctionType)value).getLEDFunction());
			return selectBox;
		}
		if (value.getClass().getName().contains("LedVisibleType")) {
			String functionNames[] = ((LedVisibleType)value).getVisibleTypes();
			selectBox.removeItemListener(this);
			selectBox.removeAllItems();
			for (int i = 0; i < functionNames.length; i++)
				selectBox.addItem(functionNames[i]);
			selectBox.addItemListener(this);
			selectBox.setSelectedIndex(((LedVisibleType)value).getLEDVisible());
			return selectBox;
		}
		textField.setText(value == null ? null : value.toString());
		return textField;
	}

	@Override
	public Object getCellEditorValue() {
		
		return value;
	}

    @Override
    public boolean stopCellEditing() {

		if (value.getClass().getName().contains("PageNames")) {
			((PageNames)value).setPageName ((String) selectBox.getSelectedItem());
			fireEditingStopped();
			return true;
		}

    	if (value.getClass().getName().contains("Boolean")) {
			value = booleanField.isSelected();
			fireEditingStopped();
			return true;
		}
		if (value.getClass().getName().contains("Color")) {
			value = colorChooser.getColor();
			fireEditingStopped();
			return true;
		}		
		
		if (value.getClass().getName().contains("Font")) {
			value = fontChooser.getSelectedFont();
			fireEditingStopped();
			return true;
		}		
		if (value.getClass().getName().contains("PictureNames")) {
			((PictureNames)value).setSelectedPictureIndex (selectBox.getSelectedIndex());
			fireEditingStopped();
			return true;
		}
		if (value.getClass().getName().contains("SoundNames")) {
			((SoundNames)value).setSelectedSoundIndex (selectBox.getSelectedIndex());
			fireEditingStopped();
			return true;
		}
		if (value.getClass().getName().contains("ButtonFunctionType")) {
			((ButtonFunctionType)value).setButtonFunction (selectBox.getSelectedIndex());
			fireEditingStopped();
			return true;
		}
		if (value.getClass().getName().contains("ValueFormatType")) {
			((ValueFormatType)value).setValueFormat (selectBox.getSelectedIndex());
			fireEditingStopped();
			return true;
		}
		if (value.getClass().getName().contains("LedFunctionType")) {
			((LedFunctionType)value).setLedFunction(selectBox.getSelectedIndex());
			fireEditingStopped();
			return true;
		}
		if (value.getClass().getName().contains("LedVisibleType")) {
			((LedVisibleType)value).setLedVisible(selectBox.getSelectedIndex());
			fireEditingStopped();
			return true;
		}
		value = textField.getText();
		fireEditingStopped();
		return true;
    } 

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (value.getClass().getName().contains("PageNames")) {
						((PageNames)value).setPageName ((String) selectBox.getSelectedItem());
						fireEditingStopped();
			} 
			if (value.getClass().getName().contains("PictureNames")) {
						((PictureNames)value).setSelectedPictureIndex (selectBox.getSelectedIndex());
						fireEditingStopped();
			} 
			if (value.getClass().getName().contains("SoundNames")) {
						((SoundNames)value).setSelectedSoundIndex (selectBox.getSelectedIndex());
						fireEditingStopped();
			}
			if (value.getClass().getName().contains("ButtonFunctionType")) {
						((ButtonFunctionType)value).setButtonFunction(selectBox.getSelectedIndex());
						fireEditingStopped();
			}
			if (value.getClass().getName().contains("ValueFormatType")) {
						((ValueFormatType)value).setValueFormat(selectBox.getSelectedIndex());
						fireEditingStopped();
			}
			if (value.getClass().getName().contains("LedFunctionType")) {
				((LedFunctionType)value).setLedFunction(selectBox.getSelectedIndex());
				fireEditingStopped();
			}
			if (value.getClass().getName().contains("LedVisibleType")) {
				((LedVisibleType)value).setLedVisible(selectBox.getSelectedIndex());
				fireEditingStopped();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (value.getClass().getName().contains("Color")) {
	        if (EDIT_COLOR.equals(e.getActionCommand())) {
	            //The user has clicked the cell, so
	            //bring up the dialog.
	            colorButton.setBackground((Color) value);
	            colorChooser.setColor((Color) value);
	            colorDialog.setVisible(true);
	
	            fireEditingStopped(); //Make the renderer reappear.
	
	        } else { //User pressed dialog's "OK" button.
	            value = colorChooser.getColor();
	        }
		}
		
		if (value.getClass().getName().contains("Font")) {
	        if (EDIT_FONT.equals(e.getActionCommand())) {
	            //The user has clicked the cell, so
	            //bring up the dialog.
	        	fontChooser.setSelectedFont((Font) value);
	        	if (fontChooser.showDialog(null) == JFontChooser.OK_OPTION)
		            value = fontChooser.getSelectedFont();
	
	            fireEditingStopped(); //Make the renderer reappear.
	
	        } 
	        else { //User pressed dialog's "OK" button.
	            value = fontChooser.getSelectedFont();
	        }
		}
		if (value.getClass().getName().contains("Boolean")) {
			value = booleanField.isSelected();
			fireEditingStopped();
		}
		if (value.getClass().getName().contains("PageNames")) {
			((PageNames)value).setPageName ((String) selectBox.getSelectedItem());
		}
    }
}
