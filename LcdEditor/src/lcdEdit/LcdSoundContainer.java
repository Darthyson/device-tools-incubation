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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class LcdSoundContainer {

	public static final int SOUND_BEEP = 0xffff;
	public static final int SOUND_SILENT = 0xfffe;
	
	protected long SoundSize;
	protected List<byte[]> Sounds = null;
	protected List<Long> SoundOffsets = null;
	protected Hashtable<Integer,Integer> containedSounds;

	public LcdSoundContainer () {
			SoundSize = 0;
			Sounds = new ArrayList<byte[]>();
			SoundOffsets = new ArrayList<Long>();
			containedSounds = new Hashtable<Integer,Integer>();
	}
		
	public long getSize () {
		return SoundSize + 4*Sounds.size();
	}
		
	protected void putLong (byte[] d, int pos, long v) {
		d[pos+3] = (byte) ((v >>> 24) & 0xff);
		d[pos+2] = (byte) ((v >>> 16) & 0xff);
		d[pos+1] = (byte) ((v >>>  8) & 0xff);
		d[pos+0] = (byte) ((v >>>  0) & 0xff);
	}

	public void writeToFile (DataOutputStream os) throws IOException {

		// we must start on even byte address
		if (os.size() % 2 == 1)
			os.writeByte (0x00);
		// output table with sound offsets to file
		long offset = 4*Sounds.size();
		for (int i = 0; i < SoundOffsets.size(); i++) {
			
				long SndOfs = SoundOffsets.get(i) + offset;
			System.out.printf ("Sound %d: %x -> %x\n", i, SoundOffsets.get(i), SndOfs);
				os.writeByte ( (byte) ((SndOfs >>  0) & 0xff));
				os.writeByte ( (byte) ((SndOfs >>  8) & 0xff));
				os.writeByte ( (byte) ((SndOfs >> 16) & 0xff));
				os.writeByte ( (byte) ((SndOfs >> 24) & 0xff));
		}
		// output Sounds to stream
		for (int i = 0; i < Sounds.size(); i++) {
			for (int sample = 0; sample <(Sounds.get(i).length); sample++) {
				os.writeByte(Sounds.get(i)[sample]);
			}
		}
	}

	public int addSound (byte[] sound ){
			
			if (sound == null) {
				return SoundLibrary.SOUND_ID_BEEP;
			}
			// store offset
			SoundOffsets.add(SoundSize);
			// adjust buffer size
			SoundSize += sound.length;
			// append image to the list
			Sounds.add(sound);
			// return index of new image
			return Sounds.size()-1;
		}

	public int addSound(int soundID, SoundLibrary soundLib) {
		// TODO Auto-generated method stub
		// check, if sound is already registered
		if (soundID == SoundLibrary.SOUND_ID_BEEP)
			return SOUND_BEEP;
		if (soundID == SoundLibrary.SOUND_ID_SILENT)
			return SOUND_SILENT;
		
		if (containedSounds.containsKey(soundID)) {
			return containedSounds.get(soundID);
		}
		int newSound = addSound (soundLib.getSoundAsWave (soundID));
		containedSounds.put(soundID, newSound);
		return newSound;
	}
}
