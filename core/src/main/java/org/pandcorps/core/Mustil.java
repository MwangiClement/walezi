/*
Copyright (c) 2009-2016, Andrew M. Martin
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of Pandam nor the names of its contributors may be used to endorse or promote products derived from this
   software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/
package org.pandcorps.core;

import java.io.*;

import javax.sound.midi.*;

// Music Utility
public final class Mustil {
	/*
      1  2  3  4  5  (Octave)
    C 24 36 48 60 72
      25 37 49 61 73
    D 26 38 50 62 74
      27 39 51 63 75
    E 28 40 52 64 76
    F 29 41 53 65 77
      30 42 54 66 78
    G 31 43 55 67 79
      32 44 56 68 80
    A 33 45 57 69 81
      34 46 58 70 82
    B 35 47 59 71 83
	*/
	
	public final static int CHN_PERCUSSION = 9;
	
	public final static int VOL_MAX = 127;
	
    public final static int PRG_ACOUSTIC_GRAND_PIANO = 0;
    public final static int PRG_BRIGHT_ACOUSTIC_PIANO = 1;
    public final static int PRG_ELECTRIC_GRAND_PIANO = 2;
    public final static int PRG_HONKY_TONK_PIANO = 3;
    public final static int PRG_ELECTRIC_PIANO_1 = 4;
    public final static int PRG_ELECTRIC_PIANO_2 = 5;
    public final static int PRG_HARPSICHORD = 6;
    public final static int PRG_CLAVINET = 7;
    public final static int PRG_CELESTA = 8;
    public final static int PRG_GLOCKENSPIEL = 9;
    public final static int PRG_MUSIC_BOX = 10;
    public final static int PRG_VIBRAPHONE = 11;
    public final static int PRG_MARIMBA = 12;
    public final static int PRG_XYLOPHONE = 13;
    public final static int PRG_TUBULAR_BELLS = 14;
    public final static int PRG_DULCIMER = 15;
    public final static int PRG_DRAWBAR_ORGAN = 16;
    public final static int PRG_PERCUSSIVE_ORGAN = 17;
    public final static int PRG_ROCK_ORGAN = 18;
    public final static int PRG_CHURCH_ORGAN = 19;
    public final static int PRG_REED_ORGAN = 20;
    public final static int PRG_ACCORDION = 21;
    public final static int PRG_HARMONICA = 22;
    public final static int PRG_TANGO_ACCORDION = 23;
    public final static int PRG_ACOUSTIC_GUITAR_NYLON = 24;
    public final static int PRG_ACOUSTIC_GUITAR_STEEL = 25;
    public final static int PRG_ELECTRIC_GUITAR_JAZZ = 26;
    public final static int PRG_ELECTRIC_GUITAR_CLEAN = 27;
    public final static int PRG_ELECTRIC_GUITAR_MUTED = 28;
    public final static int PRG_OVERDRIVEN_GUITAR = 29;
    public final static int PRG_DISTORTION_GUITAR = 30;
    public final static int PRG_GUITAR_HARMONICS = 31;
    public final static int PRG_ACOUSTIC_BASS = 32;
    public final static int PRG_ELECTRIC_BASS_FINGER = 33;
    public final static int PRG_ELECTRIC_BASS_PICK = 34;
    public final static int PRG_FRETLESS_BASS = 35;
    public final static int PRG_SLAP_BASS_1 = 36;
    public final static int PRG_SLAP_BASS_2 = 37;
    public final static int PRG_SYNTH_BASS_1 = 38;
    public final static int PRG_SYNTH_BASS_2 = 39;
    public final static int PRG_VIOLIN = 40;
    public final static int PRG_VIOLA = 41;
    public final static int PRG_CELLO = 42;
    public final static int PRG_CONTRABASS = 43;
    public final static int PRG_TREMOLO_STRINGS = 44;
    public final static int PRG_PIZZICATO_STRINGS = 45;
    public final static int PRG_ORCHESTRAL_HARP = 46;
    public final static int PRG_TIMPANI = 47;
    public final static int PRG_STRING_ENSEMBLE_1 = 48;
    public final static int PRG_STRING_ENSEMBLE_2 = 49;
    public final static int PRG_SYNTH_STRINGS_1 = 50;
    public final static int PRG_SYNTH_STRINGS_2 = 51;
    public final static int PRG_CHOIR_AAHS = 52;
    public final static int PRG_VOICE_OOHS = 53;
    public final static int PRG_SYNTH_CHOIR = 54;
    public final static int PRG_ORCHESTRA_HIT = 55;
    public final static int PRG_TRUMPET = 56;
    public final static int PRG_TROMBONE = 57;
    public final static int PRG_TUBA = 58;
    public final static int PRG_MUTED_TRUMPET = 59;
    public final static int PRG_FRENCH_HORN = 60;
    public final static int PRG_BRASS_SECTION = 61;
    public final static int PRG_SYNTH_BRASS_1 = 62;
    public final static int PRG_SYNTH_BRASS_2 = 63;
    public final static int PRG_SOPRANO_SAX = 64;
    public final static int PRG_ALTO_SAX = 65;
    public final static int PRG_TENOR_SAX = 66;
    public final static int PRG_BARITONE_SAX = 67;
    public final static int PRG_OBOE = 68;
    public final static int PRG_ENGLISH_HORN = 69;
    public final static int PRG_BASSOON = 70;
    public final static int PRG_CLARINET = 71;
    public final static int PRG_PICCOLO = 72;
    public final static int PRG_FLUTE = 73;
    public final static int PRG_RECORDER = 74;
    public final static int PRG_PAN_FLUTE = 75;
    public final static int PRG_BLOWN_BOTTLE = 76;
    public final static int PRG_SHAKUHACHI = 77;
    public final static int PRG_WHISTLE = 78;
    public final static int PRG_OCARINA = 79;
    public final static int PRG_SQUARE = 80; // LEAD 1
    public final static int PRG_SAWTOOTH = 81; // LEAD 2
    public final static int PRG_CALLIOPE = 82; // LEAD 3
    public final static int PRG_CHIFF = 83; // LEAD 4
    public final static int PRG_CHARANG = 84; // LEAD 5
    public final static int PRG_VOICE = 85; // LEAD 6
    public final static int PRG_FIFTHS = 86; // LEAD 7
    public final static int PRG_BASS_LEAD = 87; // LEAD 8
    public final static int PRG_NEW_AGE = 88; // PAD 1
    public final static int PRG_WARM = 89; // PAD 2
    public final static int PRG_POLYSYNTH = 90; // PAD 3
    public final static int PRG_CHOIR = 91; // PAD 4
    public final static int PRG_BOWED = 92; // PAD 5
    public final static int PRG_METALLIC = 93; // PAD 6
    public final static int PRG_HALO = 94; // PAD 7
    public final static int PRG_SWEEP = 95; // PAD 8
    public final static int PRG_RAIN = 96; // FX 1
    public final static int PRG_SOUNDTRACK = 97; // FX 2
    public final static int PRG_CRYSTAL = 98; // FX 3
    public final static int PRG_ATMOSPHERE = 99; // FX 4
    public final static int PRG_BRIGHTNESS = 100; // FX 5
    public final static int PRG_GOBLINS = 101; // FX 6
    public final static int PRG_ECHOES = 102; // FX 7
    public final static int PRG_SCI_FI = 103; // FX 8
    public final static int PRG_SITAR = 104;
    public final static int PRG_BANJO = 105;
    public final static int PRG_SHAMISEN = 106;
    public final static int PRG_KOTO = 107;
    public final static int PRG_KALIMBA = 108;
    public final static int PRG_BAGPIPE = 109;
    public final static int PRG_FIDDLE = 110;
    public final static int PRG_SHANAI = 111;
    public final static int PRG_TINKLE_BELL = 112;
    public final static int PRG_AGOGO = 113;
    public final static int PRG_STEEL_DRUMS = 114;
    public final static int PRG_WOODBLOCK = 115;
    public final static int PRG_TAIKO_DRUM = 116;
    public final static int PRG_MELODIC_TOM = 117;
    public final static int PRG_SYNTH_DRUM = 118;
    public final static int PRG_REVERSE_CYMBAL = 119;
    public final static int PRG_GUITAR_FRET_NOISE = 120;
    public final static int PRG_BREATH_NOISE = 121;
    public final static int PRG_SEASHORE = 122;
    public final static int PRG_BIRD_TWEET = 123;
    public final static int PRG_TELEPHONE_RING = 124;
    public final static int PRG_HELICOPTER = 125;
    public final static int PRG_APPLAUSE = 126;
    public final static int PRG_GUNSHOT = 127;
    
    public final static int PRC_BASS_DRUM_2 = 35;
    public final static int PRC_BASS_DRUM_1 = 36;
    public final static int PRC_SIDE_STICK_RIMSHOT = 37;
    public final static int PRC_SNARE_DRUM_1 = 38;
    public final static int PRC_HAND_CLAP = 39;
    public final static int PRC_SNARE_DRUM_2 = 40;
    public final static int PRC_LOW_TOM_2 = 41;
    public final static int PRC_CLOSED_HI_HAT = 42;
    public final static int PRC_LOW_TOM_1 = 43;
    public final static int PRC_PEDAL_HI_HAT = 44;
    public final static int PRC_MID_TOM_2 = 45;
    public final static int PRC_OPEM_HI_HAT = 46;
    public final static int PRC_MID_TOM_1 = 47;
    public final static int PRC_HIGH_TOM_2 = 48;
    public final static int PRC_CRASH_CYMBAL_1 = 49;
    public final static int PRC_HIGH_TOM_1 = 50;
    public final static int PRC_RIDE_CYMBAL_1 = 51;
    public final static int PRC_CHINESE_CYMBAL = 52;
    public final static int PRC_RIDE_BELL = 53;
    public final static int PRC_TAMBOURINE = 54;
    public final static int PRC_SPLASH_CYMBAL = 55;
    public final static int PRC_COWBELL = 56;
    public final static int PRC_CRASH_CYMBAL_2 = 57;
    public final static int PRC_VIBRA_SLAP = 58;
    public final static int PRC_RIDE_CYMBAL_2 = 59;
    public final static int PRC_HIGH_BONGO = 60;
    public final static int PRC_LOW_BONGO = 61;
    public final static int PRC_MUTE_HIGH_CONGA = 62;
    public final static int PRC_OPEN_HIGH_CONGA = 63;
    public final static int PRC_LOW_CONGA = 64;
    public final static int PRC_HIGH_TIMBALE = 65;
    public final static int PRC_LOW_TIMBALE = 66;
    public final static int PRC_HIGH_AGOGO = 67;
    public final static int PRC_LOW_AGOGO = 68;
    public final static int PRC_CABASA = 69;
    public final static int PRC_MARACAS = 70;
    public final static int PRC_SHORT_WHISTLE = 71;
    public final static int PRC_LONG_WHISTLE = 72;
    public final static int PRC_SHORT_GUIRO = 73;
    public final static int PRC_LONG_GUIRO = 74;
    public final static int PRC_CLAVES = 75;
    public final static int PRC_HIGH_WOOD_BLOCK = 76;
    public final static int PRC_LOW_WOOD_BLOCK = 77;
    public final static int PRC_MUTE_CUICA = 78;
    public final static int PRC_OPEN_CUICA = 79;
    public final static int PRC_MUTE_TRIANGLE = 80;
    public final static int PRC_OPEN_TRIANGLE = 81;
    
    public final static int DEF_NOTE_DURATION = 30;
    public static int unspecifiedNoteDuration = DEF_NOTE_DURATION;
	
	private Mustil() {
        throw new Error();
    }
	
	private final static void addShort(final Track track, final int cmd, final long tick, final int channel, final int a1, final int a2) throws Exception {
		final ShortMessage message = new ShortMessage();
		message.setMessage(cmd, channel, a1, a2);
		track.add(new MidiEvent(message, tick));
	}
	
	public final static void addNote(final Track track, final long tick, final int channel, final int key, final int vol) throws Exception {
		addNote(track, tick, unspecifiedNoteDuration, channel, key, vol);
	}
	
	public final static void addNote(final Track track, final long tick, final int dur, final int channel, final int key, final int vol) throws Exception {
		//info(tick + " - " + key);
		addShort(track, ShortMessage.NOTE_ON, tick, channel, key, vol);
		addShort(track, ShortMessage.NOTE_OFF, tick + dur, channel, key, 0);
	}
	
	public final static long addNotes(final Track track, final long firstTick, final int channel, final int vol, final int deltaTick, final int... keys) throws Exception {
		return addNotes(track, firstTick, channel, vol, deltaTick, false, keys);
	}
	
	public final static long addNotes(final Track track, final long firstTick, final int channel, final int vol, final int deltaTick, final boolean extend, final int... keys) throws Exception {
		long tick = firstTick;
		final int size = keys.length;
		for (int i = 0; i < size; i++) {
			final int key = keys[i];
			if (key != -1) {
				if (extend && i < (size - 1) && keys[i + 1] == -1) {
					addNote(track, tick, unspecifiedNoteDuration + deltaTick, channel, key, vol);
				} else {
					addNote(track, tick, channel, key, vol);
				}
			}
			tick += deltaTick;
		}
		return tick;
	}
	
	public final static long addRepeatedNotes(final Track track, final long firstTick, final int channel, final int vol, final int deltaTick, final int numberOfRepetitions, final int... keys) throws Exception {
		long tick = firstTick;
		for (int i = 0; i < numberOfRepetitions; i++) {
			tick = addNotes(track, tick, channel, vol, deltaTick, keys);
		}
		return tick;
	}
	
	public final static long addRise(final Track track, final long firstTick, final int channel, final int firstKey, final int vol,
			final int deltaTick, final int deltaKey, final int numberOfDeltas) throws Exception {
		return addRise(track, firstTick, channel, firstKey, vol, deltaTick, deltaKey, numberOfDeltas, 1);
	}
	
	public final static long addRise(final Track track, final long firstTick, final int channel, final int firstKey, final int vol,
			final int deltaTick, final int deltaKey, final int numberOfDeltas, final int notesPerDelta) throws Exception {
		long tick = firstTick;
		int key = firstKey;
		for (int i = 0; i < numberOfDeltas; i++) {
			for (int j = 0; j < notesPerDelta; j++) {
				addNote(track, tick, channel, key, vol);
				tick += deltaTick;
			}
			key += deltaKey;
		}
		return tick;
	}
	
	public final static void addPercussion(final Track track, final long tick, final int key) throws Exception {
		addPercussion(track, tick, 8, key);
	}
	
	public final static void addPercussion(final Track track, final long tick, final int dur, final int key) throws Exception {
		addPercussionAtVolume(track, tick, dur, key, VOL_MAX);
	}
	
	public final static void addPercussionAtVolume(final Track track, final long tick, final int dur, final int key, final int vol) throws Exception {
		addNote(track, tick, dur, CHN_PERCUSSION, key, vol);
	}
	
	public final static long addPercussions(final Track track, final long firstTick, final int deltaTick, final int... keys) throws Exception {
		return addPercussionsAtVolume(track, firstTick, VOL_MAX, deltaTick, keys);
	}
	
	public final static long addPercussionsAtVolume(final Track track, final long firstTick, final int vol, final int deltaTick, final int... keys) throws Exception {
		long tick = firstTick;
		final int size = keys.length;
		for (int i = 0; i < size; i++) {
			final int key = keys[i];
			if (key != -1) {
				int j = i + 1;
				for (; j < size && keys[j] != -1; j++);
				addPercussionAtVolume(track, tick, deltaTick * (j - i), key, vol);
			}
			tick += deltaTick;
		}
		return tick;
	}
	
	public final static long addRepeatedPercussions(final Track track, final long firstTick, final int deltaTick, final int numberOfRepetitions, final int... keys) throws Exception {
		long tick = firstTick;
		for (int i = 0; i < numberOfRepetitions; i++) {
			tick = addPercussions(track, tick, deltaTick, keys);
		}
		return tick;
	}
	
	public final static void setInstrument(final Track track, final int channel, final int program) throws Exception {
		addShort(track, ShortMessage.PROGRAM_CHANGE, 0, channel, program, 0);
	}
	
	public final static void setPitch(final Track track, final int tick, final int channel, final int amt) throws Exception {
		addShort(track, ShortMessage.PITCH_BEND, tick, channel, 0, amt); // < 64 to lower, > 65 to raise
	}
	
	/*
	Don't know how CONTROL_CHANGE 7 is supposed to work.
	This method doesn't seem to work as intended.
	Lowering the volume of a channel seems to permanently mute the Sequence after it has been played once.
	Sometimes Java uses 0-based where spec uses 1-based, so maybe 6 should be used.
	That doesn't seem to do anything though.
	Maybe 7 is for start of track, 11 is for changes during track.
	*/
	/*public final static void setVolume(final Track track, final long tick, final int channel, final int vol) throws Exception {
		addShort(track, ShortMessage.CONTROL_CHANGE, tick, channel, 7, vol);
	}*/
	
	private final static void addMeta(final Track track, final int type, final byte[] data) throws Exception {
		final MetaMessage message = new MetaMessage();
		message.setMessage(type, data, data.length);
		track.add(new MidiEvent(message, 0));
	}
	
	public final static void setDefaultTempo(final Track track) throws Exception {
		//final int microsecondsPerQuarterNote
		//addMeta(track, 81, new byte[] {0x07, (byte) 0xA1, 0x20});
		addMeta(track, 81, new byte[] {0x31, (byte) 0xA1, 0x20});
	}
	
	public final static void setTimeSignature(final Track track,
			final int numerator, final int denominator, final int midiClocksPerMetronomeClick, final int num32ndNotesPerBeat) throws Exception {
		addMeta(track, 88, new byte[] {(byte) numerator, (byte) denominator, (byte) midiClocksPerMetronomeClick, (byte) num32ndNotesPerBeat});
	}
	
	private final static void addMeta(final Track track, final int type, final String s) throws Exception {
		addMeta(track, type, s.getBytes());
	}
	
	public final static void setName(final Track track, final String s) throws Exception {
		addMeta(track, 3, s);
	}
	
	public final static void setCopyright(final Track track, final String s) throws Exception {
		addMeta(track, 2, s);
	}
	
	public final static void save(final Sequence seq, final String loc) throws Exception {
	    OutputStream out = null;
	    try {
    	    out = new FileOutputStream(loc);
    		MidiSystem.write(seq, 0, out);
	    } finally {
	        Iotil.close(out);
	    }
	}
	
	public final static Sequence load(final String loc) throws Exception {
		InputStream in = null;
		try {
			in = Iotil.getInputStream(loc);
			return MidiSystem.getSequence(in);
		} finally {
			Iotil.close(in);
		}
	}
	
	public final static void main(final String[] args) {
		try {
			for (final String loc : args) {
				final Sequence seq = load(loc);
				System.out.println();
				System.out.println(loc);
				System.out.println("MicrosecondLength: " + seq.getMicrosecondLength());
				System.out.println("DivisionType: " + seq.getDivisionType());
				System.out.println("Resolution: " + seq.getResolution());
				final Track[] tracks = seq.getTracks();
				final int tracksSize = Math.min(3, tracks.length);
				for (int j = 0; j < tracksSize; j++) {
					System.out.println("  Track " + j);
					final Track track = tracks[j];
					final int size = Math.min(20, track.size());
					//final int size = track.size();
					for (int i = 0; i < size; i++) {
						final MidiMessage message = track.get(i).getMessage();
						if (message instanceof MetaMessage) {
							final MetaMessage mm = (MetaMessage) message;
							System.out.println("Meta " + mm.getType() + "; " + new String(mm.getData()));
						//} else if (message instanceof SysexMessage) {
						//	((SysexMessage) message).
						} else {
							System.out.println(message.getClass());
						}
					}
				}
			}
		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}
}
