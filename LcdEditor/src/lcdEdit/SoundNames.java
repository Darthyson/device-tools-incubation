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

public class SoundNames {
	private String soundNames[];
	private int soundIDs[];
	private int selectedSoundID;

	public SoundNames(String[] soundNames, int soundIDs[], int selectedSoundID) {
		super();
		this.soundNames = soundNames;
		this.soundIDs = soundIDs;
		this.selectedSoundID = selectedSoundID;
	}

	public String[] getSoundNames () {
		return soundNames;
	}
	
	private String getSelectedSoundNameFromID (int selectedSoundID) {
		
		for (int i = 0; i < soundIDs.length; i++) {
			int ID = soundIDs[i];
			if (ID == selectedSoundID)
				return soundNames [i];
		}
		return "";
	}
	
	@Override
	public String toString() {
		return getSelectedSoundNameFromID (selectedSoundID);
	}

	public int getSelectedSoundID() {
		return selectedSoundID;
	}

	public void setSelectedSoundIndex(int selectedSoundIndex) {
		if ((selectedSoundIndex >= 0) && (selectedSoundIndex < soundIDs.length))
			selectedSoundID = soundIDs[selectedSoundIndex];
		else selectedSoundID = -1;
	}

	public int getSelectedIndex() {

		for (int i = 0; i < soundIDs.length; i++) {
			int ID = soundIDs[i];
			if (ID == selectedSoundID)
				return i;
		}
		return -1;
	}	

}
