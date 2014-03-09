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

public class PictureNames {
	
	private String pictureNames[];
	private int pictureIDs[];
	private int selectedPictureID;

	public PictureNames(String[] pictureNames, int pictureIDs[], int selectedPictureID) {
		super();
		this.pictureNames = pictureNames;
		this .pictureIDs = pictureIDs;
		this.selectedPictureID = selectedPictureID;
	}

	public String[] getPictureNames () {
		return pictureNames;
	}
	
	private String getSelectedPictureNameFromID (int selectedPictureID) {
		
		for (int i = 0; i < pictureIDs.length; i++) {
			int ID = pictureIDs[i];
			if (ID == selectedPictureID)
				return pictureNames [i];
		}
		return "";
	}
	
	@Override
	public String toString() {
		return getSelectedPictureNameFromID (selectedPictureID);
	}

	public int getSelectedPictureID() {
		return selectedPictureID;
	}

	public void setSelectedPictureIndex(int selectedPictureIndex) {
		if ((selectedPictureIndex >= 0) && (selectedPictureIndex < pictureIDs.length))
			selectedPictureID = pictureIDs[selectedPictureIndex];
		else selectedPictureID = -1;
	}

	public int getSelectedIndex() {
		for (int i = 0; i < pictureIDs.length; i++) {
			int ID = pictureIDs[i];
			if (ID == selectedPictureID)
				return i;
		}
		return -1;
	}	

}
