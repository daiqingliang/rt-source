package com.sun.media.sound;

import java.util.Random;
import javax.sound.midi.Patch;
import javax.sound.sampled.AudioFormat;

public final class EmergencySoundbank {
  private static final String[] general_midi_instruments = { 
      "Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano", "Honky-tonk Piano", "Electric Piano 1", "Electric Piano 2", "Harpsichord", "Clavi", "Celesta", "Glockenspiel", 
      "Music Box", "Vibraphone", "Marimba", "Xylophone", "Tubular Bells", "Dulcimer", "Drawbar Organ", "Percussive Organ", "Rock Organ", "Church Organ", 
      "Reed Organ", "Accordion", "Harmonica", "Tango Accordion", "Acoustic Guitar (nylon)", "Acoustic Guitar (steel)", "Electric Guitar (jazz)", "Electric Guitar (clean)", "Electric Guitar (muted)", "Overdriven Guitar", 
      "Distortion Guitar", "Guitar harmonics", "Acoustic Bass", "Electric Bass (finger)", "Electric Bass (pick)", "Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Synth Bass 1", "Synth Bass 2", 
      "Violin", "Viola", "Cello", "Contrabass", "Tremolo Strings", "Pizzicato Strings", "Orchestral Harp", "Timpani", "String Ensemble 1", "String Ensemble 2", 
      "SynthStrings 1", "SynthStrings 2", "Choir Aahs", "Voice Oohs", "Synth Voice", "Orchestra Hit", "Trumpet", "Trombone", "Tuba", "Muted Trumpet", 
      "French Horn", "Brass Section", "SynthBrass 1", "SynthBrass 2", "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax", "Oboe", "English Horn", 
      "Bassoon", "Clarinet", "Piccolo", "Flute", "Recorder", "Pan Flute", "Blown Bottle", "Shakuhachi", "Whistle", "Ocarina", 
      "Lead 1 (square)", "Lead 2 (sawtooth)", "Lead 3 (calliope)", "Lead 4 (chiff)", "Lead 5 (charang)", "Lead 6 (voice)", "Lead 7 (fifths)", "Lead 8 (bass + lead)", "Pad 1 (new age)", "Pad 2 (warm)", 
      "Pad 3 (polysynth)", "Pad 4 (choir)", "Pad 5 (bowed)", "Pad 6 (metallic)", "Pad 7 (halo)", "Pad 8 (sweep)", "FX 1 (rain)", "FX 2 (soundtrack)", "FX 3 (crystal)", "FX 4 (atmosphere)", 
      "FX 5 (brightness)", "FX 6 (goblins)", "FX 7 (echoes)", "FX 8 (sci-fi)", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba", "Bag pipe", 
      "Fiddle", "Shanai", "Tinkle Bell", "Agogo", "Steel Drums", "Woodblock", "Taiko Drum", "Melodic Tom", "Synth Drum", "Reverse Cymbal", 
      "Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet", "Telephone Ring", "Helicopter", "Applause", "Gunshot" };
  
  public static SF2Soundbank createSoundbank() throws Exception {
    SF2Soundbank sF2Soundbank = new SF2Soundbank();
    sF2Soundbank.setName("Emergency GM sound set");
    sF2Soundbank.setVendor("Generated");
    sF2Soundbank.setDescription("Emergency generated soundbank");
    SF2Layer sF2Layer1 = new_bass_drum(sF2Soundbank);
    SF2Layer sF2Layer2 = new_snare_drum(sF2Soundbank);
    SF2Layer sF2Layer3 = new_tom(sF2Soundbank);
    SF2Layer sF2Layer4 = new_open_hihat(sF2Soundbank);
    SF2Layer sF2Layer5 = new_closed_hihat(sF2Soundbank);
    SF2Layer sF2Layer6 = new_crash_cymbal(sF2Soundbank);
    SF2Layer sF2Layer7 = new_side_stick(sF2Soundbank);
    SF2Layer[] arrayOfSF2Layer = new SF2Layer[128];
    arrayOfSF2Layer[35] = sF2Layer1;
    arrayOfSF2Layer[36] = sF2Layer1;
    arrayOfSF2Layer[38] = sF2Layer2;
    arrayOfSF2Layer[40] = sF2Layer2;
    arrayOfSF2Layer[41] = sF2Layer3;
    arrayOfSF2Layer[43] = sF2Layer3;
    arrayOfSF2Layer[45] = sF2Layer3;
    arrayOfSF2Layer[47] = sF2Layer3;
    arrayOfSF2Layer[48] = sF2Layer3;
    arrayOfSF2Layer[50] = sF2Layer3;
    arrayOfSF2Layer[42] = sF2Layer5;
    arrayOfSF2Layer[44] = sF2Layer5;
    arrayOfSF2Layer[46] = sF2Layer4;
    arrayOfSF2Layer[49] = sF2Layer6;
    arrayOfSF2Layer[51] = sF2Layer6;
    arrayOfSF2Layer[52] = sF2Layer6;
    arrayOfSF2Layer[55] = sF2Layer6;
    arrayOfSF2Layer[57] = sF2Layer6;
    arrayOfSF2Layer[59] = sF2Layer6;
    arrayOfSF2Layer[37] = sF2Layer7;
    arrayOfSF2Layer[39] = sF2Layer7;
    arrayOfSF2Layer[53] = sF2Layer7;
    arrayOfSF2Layer[54] = sF2Layer7;
    arrayOfSF2Layer[56] = sF2Layer7;
    arrayOfSF2Layer[58] = sF2Layer7;
    arrayOfSF2Layer[69] = sF2Layer7;
    arrayOfSF2Layer[70] = sF2Layer7;
    arrayOfSF2Layer[75] = sF2Layer7;
    arrayOfSF2Layer[60] = sF2Layer7;
    arrayOfSF2Layer[61] = sF2Layer7;
    arrayOfSF2Layer[62] = sF2Layer7;
    arrayOfSF2Layer[63] = sF2Layer7;
    arrayOfSF2Layer[64] = sF2Layer7;
    arrayOfSF2Layer[65] = sF2Layer7;
    arrayOfSF2Layer[66] = sF2Layer7;
    arrayOfSF2Layer[67] = sF2Layer7;
    arrayOfSF2Layer[68] = sF2Layer7;
    arrayOfSF2Layer[71] = sF2Layer7;
    arrayOfSF2Layer[72] = sF2Layer7;
    arrayOfSF2Layer[73] = sF2Layer7;
    arrayOfSF2Layer[74] = sF2Layer7;
    arrayOfSF2Layer[76] = sF2Layer7;
    arrayOfSF2Layer[77] = sF2Layer7;
    arrayOfSF2Layer[78] = sF2Layer7;
    arrayOfSF2Layer[79] = sF2Layer7;
    arrayOfSF2Layer[80] = sF2Layer7;
    arrayOfSF2Layer[81] = sF2Layer7;
    SF2Instrument sF2Instrument1 = new SF2Instrument(sF2Soundbank);
    sF2Instrument1.setName("Standard Kit");
    sF2Instrument1.setPatch(new ModelPatch(0, 0, true));
    sF2Soundbank.addInstrument(sF2Instrument1);
    for (byte b = 0; b < arrayOfSF2Layer.length; b++) {
      if (arrayOfSF2Layer[b] != null) {
        SF2InstrumentRegion sF2InstrumentRegion1 = new SF2InstrumentRegion();
        sF2InstrumentRegion1.setLayer(arrayOfSF2Layer[b]);
        sF2InstrumentRegion1.putBytes(43, new byte[] { (byte)b, (byte)b });
        sF2Instrument1.getRegions().add(sF2InstrumentRegion1);
      } 
    } 
    SF2Layer sF2Layer8 = new_gpiano(sF2Soundbank);
    SF2Layer sF2Layer9 = new_gpiano2(sF2Soundbank);
    SF2Layer sF2Layer10 = new_piano_hammer(sF2Soundbank);
    SF2Layer sF2Layer11 = new_piano1(sF2Soundbank);
    SF2Layer sF2Layer12 = new_epiano1(sF2Soundbank);
    SF2Layer sF2Layer13 = new_epiano2(sF2Soundbank);
    SF2Layer sF2Layer14 = new_guitar1(sF2Soundbank);
    SF2Layer sF2Layer15 = new_guitar_pick(sF2Soundbank);
    SF2Layer sF2Layer16 = new_guitar_dist(sF2Soundbank);
    SF2Layer sF2Layer17 = new_bass1(sF2Soundbank);
    SF2Layer sF2Layer18 = new_bass2(sF2Soundbank);
    SF2Layer sF2Layer19 = new_synthbass(sF2Soundbank);
    SF2Layer sF2Layer20 = new_string2(sF2Soundbank);
    SF2Layer sF2Layer21 = new_orchhit(sF2Soundbank);
    SF2Layer sF2Layer22 = new_choir(sF2Soundbank);
    SF2Layer sF2Layer23 = new_solostring(sF2Soundbank);
    SF2Layer sF2Layer24 = new_organ(sF2Soundbank);
    SF2Layer sF2Layer25 = new_ch_organ(sF2Soundbank);
    SF2Layer sF2Layer26 = new_bell(sF2Soundbank);
    SF2Layer sF2Layer27 = new_flute(sF2Soundbank);
    SF2Layer sF2Layer28 = new_timpani(sF2Soundbank);
    SF2Layer sF2Layer29 = new_melodic_toms(sF2Soundbank);
    SF2Layer sF2Layer30 = new_trumpet(sF2Soundbank);
    SF2Layer sF2Layer31 = new_trombone(sF2Soundbank);
    SF2Layer sF2Layer32 = new_brass_section(sF2Soundbank);
    SF2Layer sF2Layer33 = new_horn(sF2Soundbank);
    SF2Layer sF2Layer34 = new_sax(sF2Soundbank);
    SF2Layer sF2Layer35 = new_oboe(sF2Soundbank);
    SF2Layer sF2Layer36 = new_bassoon(sF2Soundbank);
    SF2Layer sF2Layer37 = new_clarinet(sF2Soundbank);
    SF2Layer sF2Layer38 = new_reverse_cymbal(sF2Soundbank);
    SF2Layer sF2Layer39 = sF2Layer11;
    newInstrument(sF2Soundbank, "Piano", new Patch(0, 0), new SF2Layer[] { sF2Layer8, sF2Layer10 });
    newInstrument(sF2Soundbank, "Piano", new Patch(0, 1), new SF2Layer[] { sF2Layer9, sF2Layer10 });
    newInstrument(sF2Soundbank, "Piano", new Patch(0, 2), new SF2Layer[] { sF2Layer11 });
    SF2Instrument sF2Instrument2 = newInstrument(sF2Soundbank, "Honky-tonk Piano", new Patch(0, 3), new SF2Layer[] { sF2Layer11, sF2Layer11 });
    SF2InstrumentRegion sF2InstrumentRegion = (SF2InstrumentRegion)sF2Instrument2.getRegions().get(0);
    sF2InstrumentRegion.putInteger(8, 80);
    sF2InstrumentRegion.putInteger(52, 30);
    sF2InstrumentRegion = (SF2InstrumentRegion)sF2Instrument2.getRegions().get(1);
    sF2InstrumentRegion.putInteger(8, 30);
    newInstrument(sF2Soundbank, "Rhodes", new Patch(0, 4), new SF2Layer[] { sF2Layer13 });
    newInstrument(sF2Soundbank, "Rhodes", new Patch(0, 5), new SF2Layer[] { sF2Layer13 });
    newInstrument(sF2Soundbank, "Clavinet", new Patch(0, 6), new SF2Layer[] { sF2Layer12 });
    newInstrument(sF2Soundbank, "Clavinet", new Patch(0, 7), new SF2Layer[] { sF2Layer12 });
    newInstrument(sF2Soundbank, "Rhodes", new Patch(0, 8), new SF2Layer[] { sF2Layer13 });
    newInstrument(sF2Soundbank, "Bell", new Patch(0, 9), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Bell", new Patch(0, 10), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Vibraphone", new Patch(0, 11), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Marimba", new Patch(0, 12), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Marimba", new Patch(0, 13), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Bell", new Patch(0, 14), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Rock Organ", new Patch(0, 15), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Rock Organ", new Patch(0, 16), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Perc Organ", new Patch(0, 17), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Rock Organ", new Patch(0, 18), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Church Organ", new Patch(0, 19), new SF2Layer[] { sF2Layer25 });
    newInstrument(sF2Soundbank, "Accordion", new Patch(0, 20), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Accordion", new Patch(0, 21), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Accordion", new Patch(0, 22), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Accordion", new Patch(0, 23), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Guitar", new Patch(0, 24), new SF2Layer[] { sF2Layer14, sF2Layer15 });
    newInstrument(sF2Soundbank, "Guitar", new Patch(0, 25), new SF2Layer[] { sF2Layer14, sF2Layer15 });
    newInstrument(sF2Soundbank, "Guitar", new Patch(0, 26), new SF2Layer[] { sF2Layer14, sF2Layer15 });
    newInstrument(sF2Soundbank, "Guitar", new Patch(0, 27), new SF2Layer[] { sF2Layer14, sF2Layer15 });
    newInstrument(sF2Soundbank, "Guitar", new Patch(0, 28), new SF2Layer[] { sF2Layer14, sF2Layer15 });
    newInstrument(sF2Soundbank, "Distorted Guitar", new Patch(0, 29), new SF2Layer[] { sF2Layer16 });
    newInstrument(sF2Soundbank, "Distorted Guitar", new Patch(0, 30), new SF2Layer[] { sF2Layer16 });
    newInstrument(sF2Soundbank, "Guitar", new Patch(0, 31), new SF2Layer[] { sF2Layer14, sF2Layer15 });
    newInstrument(sF2Soundbank, "Finger Bass", new Patch(0, 32), new SF2Layer[] { sF2Layer17 });
    newInstrument(sF2Soundbank, "Finger Bass", new Patch(0, 33), new SF2Layer[] { sF2Layer17 });
    newInstrument(sF2Soundbank, "Finger Bass", new Patch(0, 34), new SF2Layer[] { sF2Layer17 });
    newInstrument(sF2Soundbank, "Frettless Bass", new Patch(0, 35), new SF2Layer[] { sF2Layer18 });
    newInstrument(sF2Soundbank, "Frettless Bass", new Patch(0, 36), new SF2Layer[] { sF2Layer18 });
    newInstrument(sF2Soundbank, "Frettless Bass", new Patch(0, 37), new SF2Layer[] { sF2Layer18 });
    newInstrument(sF2Soundbank, "Synth Bass1", new Patch(0, 38), new SF2Layer[] { sF2Layer19 });
    newInstrument(sF2Soundbank, "Synth Bass2", new Patch(0, 39), new SF2Layer[] { sF2Layer19 });
    newInstrument(sF2Soundbank, "Solo String", new Patch(0, 40), new SF2Layer[] { sF2Layer20, sF2Layer23 });
    newInstrument(sF2Soundbank, "Solo String", new Patch(0, 41), new SF2Layer[] { sF2Layer20, sF2Layer23 });
    newInstrument(sF2Soundbank, "Solo String", new Patch(0, 42), new SF2Layer[] { sF2Layer20, sF2Layer23 });
    newInstrument(sF2Soundbank, "Solo String", new Patch(0, 43), new SF2Layer[] { sF2Layer20, sF2Layer23 });
    newInstrument(sF2Soundbank, "Solo String", new Patch(0, 44), new SF2Layer[] { sF2Layer20, sF2Layer23 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 45), new SF2Layer[] { sF2Layer39 });
    newInstrument(sF2Soundbank, "Harp", new Patch(0, 46), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Timpani", new Patch(0, 47), new SF2Layer[] { sF2Layer28 });
    newInstrument(sF2Soundbank, "Strings", new Patch(0, 48), new SF2Layer[] { sF2Layer20 });
    sF2Instrument2 = newInstrument(sF2Soundbank, "Slow Strings", new Patch(0, 49), new SF2Layer[] { sF2Layer20 });
    sF2InstrumentRegion = (SF2InstrumentRegion)sF2Instrument2.getRegions().get(0);
    sF2InstrumentRegion.putInteger(34, 2500);
    sF2InstrumentRegion.putInteger(38, 2000);
    newInstrument(sF2Soundbank, "Synth Strings", new Patch(0, 50), new SF2Layer[] { sF2Layer20 });
    newInstrument(sF2Soundbank, "Synth Strings", new Patch(0, 51), new SF2Layer[] { sF2Layer20 });
    newInstrument(sF2Soundbank, "Choir", new Patch(0, 52), new SF2Layer[] { sF2Layer22 });
    newInstrument(sF2Soundbank, "Choir", new Patch(0, 53), new SF2Layer[] { sF2Layer22 });
    newInstrument(sF2Soundbank, "Choir", new Patch(0, 54), new SF2Layer[] { sF2Layer22 });
    SF2Instrument sF2Instrument3 = newInstrument(sF2Soundbank, "Orch Hit", new Patch(0, 55), new SF2Layer[] { sF2Layer21, sF2Layer21, sF2Layer28 });
    sF2InstrumentRegion = (SF2InstrumentRegion)sF2Instrument3.getRegions().get(0);
    sF2InstrumentRegion.putInteger(51, -12);
    sF2InstrumentRegion.putInteger(48, -100);
    newInstrument(sF2Soundbank, "Trumpet", new Patch(0, 56), new SF2Layer[] { sF2Layer30 });
    newInstrument(sF2Soundbank, "Trombone", new Patch(0, 57), new SF2Layer[] { sF2Layer31 });
    newInstrument(sF2Soundbank, "Trombone", new Patch(0, 58), new SF2Layer[] { sF2Layer31 });
    newInstrument(sF2Soundbank, "Trumpet", new Patch(0, 59), new SF2Layer[] { sF2Layer30 });
    newInstrument(sF2Soundbank, "Horn", new Patch(0, 60), new SF2Layer[] { sF2Layer33 });
    newInstrument(sF2Soundbank, "Brass Section", new Patch(0, 61), new SF2Layer[] { sF2Layer32 });
    newInstrument(sF2Soundbank, "Brass Section", new Patch(0, 62), new SF2Layer[] { sF2Layer32 });
    newInstrument(sF2Soundbank, "Brass Section", new Patch(0, 63), new SF2Layer[] { sF2Layer32 });
    newInstrument(sF2Soundbank, "Sax", new Patch(0, 64), new SF2Layer[] { sF2Layer34 });
    newInstrument(sF2Soundbank, "Sax", new Patch(0, 65), new SF2Layer[] { sF2Layer34 });
    newInstrument(sF2Soundbank, "Sax", new Patch(0, 66), new SF2Layer[] { sF2Layer34 });
    newInstrument(sF2Soundbank, "Sax", new Patch(0, 67), new SF2Layer[] { sF2Layer34 });
    newInstrument(sF2Soundbank, "Oboe", new Patch(0, 68), new SF2Layer[] { sF2Layer35 });
    newInstrument(sF2Soundbank, "Horn", new Patch(0, 69), new SF2Layer[] { sF2Layer33 });
    newInstrument(sF2Soundbank, "Bassoon", new Patch(0, 70), new SF2Layer[] { sF2Layer36 });
    newInstrument(sF2Soundbank, "Clarinet", new Patch(0, 71), new SF2Layer[] { sF2Layer37 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 72), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 73), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 74), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 75), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 76), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 77), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 78), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 79), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 80), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 81), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Flute", new Patch(0, 82), new SF2Layer[] { sF2Layer27 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 83), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 84), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Choir", new Patch(0, 85), new SF2Layer[] { sF2Layer22 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 86), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 87), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Synth Strings", new Patch(0, 88), new SF2Layer[] { sF2Layer20 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 89), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 90), new SF2Layer[] { sF2Layer39 });
    newInstrument(sF2Soundbank, "Choir", new Patch(0, 91), new SF2Layer[] { sF2Layer22 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 92), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 93), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 94), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 95), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 96), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 97), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Bell", new Patch(0, 98), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 99), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 100), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Organ", new Patch(0, 101), new SF2Layer[] { sF2Layer24 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 102), new SF2Layer[] { sF2Layer39 });
    newInstrument(sF2Soundbank, "Synth Strings", new Patch(0, 103), new SF2Layer[] { sF2Layer20 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 104), new SF2Layer[] { sF2Layer39 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 105), new SF2Layer[] { sF2Layer39 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 106), new SF2Layer[] { sF2Layer39 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 107), new SF2Layer[] { sF2Layer39 });
    newInstrument(sF2Soundbank, "Marimba", new Patch(0, 108), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Sax", new Patch(0, 109), new SF2Layer[] { sF2Layer34 });
    newInstrument(sF2Soundbank, "Solo String", new Patch(0, 110), new SF2Layer[] { sF2Layer20, sF2Layer23 });
    newInstrument(sF2Soundbank, "Oboe", new Patch(0, 111), new SF2Layer[] { sF2Layer35 });
    newInstrument(sF2Soundbank, "Bell", new Patch(0, 112), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Melodic Toms", new Patch(0, 113), new SF2Layer[] { sF2Layer29 });
    newInstrument(sF2Soundbank, "Marimba", new Patch(0, 114), new SF2Layer[] { sF2Layer26 });
    newInstrument(sF2Soundbank, "Melodic Toms", new Patch(0, 115), new SF2Layer[] { sF2Layer29 });
    newInstrument(sF2Soundbank, "Melodic Toms", new Patch(0, 116), new SF2Layer[] { sF2Layer29 });
    newInstrument(sF2Soundbank, "Melodic Toms", new Patch(0, 117), new SF2Layer[] { sF2Layer29 });
    newInstrument(sF2Soundbank, "Reverse Cymbal", new Patch(0, 118), new SF2Layer[] { sF2Layer38 });
    newInstrument(sF2Soundbank, "Reverse Cymbal", new Patch(0, 119), new SF2Layer[] { sF2Layer38 });
    newInstrument(sF2Soundbank, "Guitar", new Patch(0, 120), new SF2Layer[] { sF2Layer14 });
    newInstrument(sF2Soundbank, "Def", new Patch(0, 121), new SF2Layer[] { sF2Layer39 });
    sF2Instrument3 = newInstrument(sF2Soundbank, "Seashore/Reverse Cymbal", new Patch(0, 122), new SF2Layer[] { sF2Layer38 });
    sF2InstrumentRegion = (SF2InstrumentRegion)sF2Instrument3.getRegions().get(0);
    sF2InstrumentRegion.putInteger(37, 1000);
    sF2InstrumentRegion.putInteger(36, 18500);
    sF2InstrumentRegion.putInteger(38, 4500);
    sF2InstrumentRegion.putInteger(8, -4500);
    sF2Instrument3 = newInstrument(sF2Soundbank, "Bird/Flute", new Patch(0, 123), new SF2Layer[] { sF2Layer27 });
    sF2InstrumentRegion = (SF2InstrumentRegion)sF2Instrument3.getRegions().get(0);
    sF2InstrumentRegion.putInteger(51, 24);
    sF2InstrumentRegion.putInteger(36, -3000);
    sF2InstrumentRegion.putInteger(37, 1000);
    newInstrument(sF2Soundbank, "Def", new Patch(0, 124), new SF2Layer[] { sF2Layer7 });
    sF2Instrument3 = newInstrument(sF2Soundbank, "Seashore/Reverse Cymbal", new Patch(0, 125), new SF2Layer[] { sF2Layer38 });
    sF2InstrumentRegion = (SF2InstrumentRegion)sF2Instrument3.getRegions().get(0);
    sF2InstrumentRegion.putInteger(37, 1000);
    sF2InstrumentRegion.putInteger(36, 18500);
    sF2InstrumentRegion.putInteger(38, 4500);
    sF2InstrumentRegion.putInteger(8, -4500);
    newInstrument(sF2Soundbank, "Applause/crash_cymbal", new Patch(0, 126), new SF2Layer[] { sF2Layer6 });
    newInstrument(sF2Soundbank, "Gunshot/side_stick", new Patch(0, 127), new SF2Layer[] { sF2Layer7 });
    for (SF2Instrument sF2Instrument : sF2Soundbank.getInstruments()) {
      Patch patch = sF2Instrument.getPatch();
      if (!(patch instanceof ModelPatch) || !((ModelPatch)patch).isPercussion())
        sF2Instrument.setName(general_midi_instruments[patch.getProgram()]); 
    } 
    return sF2Soundbank;
  }
  
  public static SF2Layer new_bell(SF2Soundbank paramSF2Soundbank) {
    Random random = new Random(102030201L);
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.01D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 1.0E-5D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (byte b2 = 0; b2 < 40; b2++) {
      double d8 = 1.0D + (random.nextDouble() * 2.0D - 1.0D) * 0.01D;
      double d9 = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble, d1 * (b2 + true) * d8, d9, d6);
      d6 *= d7;
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "EPiano", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, 1200);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -9000);
    sF2Region.putInteger(8, 16000);
    return sF2Layer;
  }
  
  public static SF2Layer new_guitar1(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 2.0D;
    double d5 = 0.01D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    double[] arrayOfDouble2 = new double[40];
    byte b2;
    for (b2 = 0; b2 < 40; b2++) {
      arrayOfDouble2[b2] = d6;
      d6 *= d7;
    } 
    arrayOfDouble2[0] = 2.0D;
    arrayOfDouble2[1] = 0.5D;
    arrayOfDouble2[2] = 0.45D;
    arrayOfDouble2[3] = 0.2D;
    arrayOfDouble2[4] = 1.0D;
    arrayOfDouble2[5] = 0.5D;
    arrayOfDouble2[6] = 2.0D;
    arrayOfDouble2[7] = 1.0D;
    arrayOfDouble2[8] = 0.5D;
    arrayOfDouble2[9] = 1.0D;
    arrayOfDouble2[9] = 0.5D;
    arrayOfDouble2[10] = 0.2D;
    arrayOfDouble2[11] = 1.0D;
    arrayOfDouble2[12] = 0.7D;
    arrayOfDouble2[13] = 0.5D;
    arrayOfDouble2[14] = 1.0D;
    for (b2 = 0; b2 < 40; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), d, arrayOfDouble2[b2]);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Guitar", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Guitar", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 2400);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, -100);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -6000);
    sF2Region.putInteger(8, 16000);
    sF2Region.putInteger(48, -20);
    return sF2Layer;
  }
  
  public static SF2Layer new_guitar_dist(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 2.0D;
    double d5 = 0.01D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    double[] arrayOfDouble2 = new double[40];
    byte b2;
    for (b2 = 0; b2 < 40; b2++) {
      arrayOfDouble2[b2] = d6;
      d6 *= d7;
    } 
    arrayOfDouble2[0] = 5.0D;
    arrayOfDouble2[1] = 2.0D;
    arrayOfDouble2[2] = 0.45D;
    arrayOfDouble2[3] = 0.2D;
    arrayOfDouble2[4] = 1.0D;
    arrayOfDouble2[5] = 0.5D;
    arrayOfDouble2[6] = 2.0D;
    arrayOfDouble2[7] = 1.0D;
    arrayOfDouble2[8] = 0.5D;
    arrayOfDouble2[9] = 1.0D;
    arrayOfDouble2[9] = 0.5D;
    arrayOfDouble2[10] = 0.2D;
    arrayOfDouble2[11] = 1.0D;
    arrayOfDouble2[12] = 0.7D;
    arrayOfDouble2[13] = 0.5D;
    arrayOfDouble2[14] = 1.0D;
    for (b2 = 0; b2 < 40; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), d, arrayOfDouble2[b2]);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample_dist(paramSF2Soundbank, "Distorted Guitar", arrayOfDouble1, d1, 10000.0D);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Distorted Guitar", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(8, 8000);
    return sF2Layer;
  }
  
  public static SF2Layer new_guitar_pick(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 2;
    char c1 = 'က' * b1;
    double[] arrayOfDouble2 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble2.length; c2 += true)
      arrayOfDouble2[c2] = 2.0D * (random.nextDouble() - 0.5D); 
    fft(arrayOfDouble2);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble2.length; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'ࠀ' * b1; c2++)
      arrayOfDouble2[c2] = arrayOfDouble2[c2] * (Math.exp(-Math.abs((c2 - '\027') / b1) * 1.2D) + Math.exp(-Math.abs((c2 - '(') / b1) * 0.9D)); 
    randomPhase(arrayOfDouble2, new Random(3049912L));
    ifft(arrayOfDouble2);
    normalize(arrayOfDouble2, 0.8D);
    arrayOfDouble2 = realPart(arrayOfDouble2);
    double d = 1.0D;
    for (byte b2 = 0; b2 < arrayOfDouble2.length; b2++) {
      arrayOfDouble2[b2] = arrayOfDouble2[b2] * d;
      d *= 0.9994D;
    } 
    double[] arrayOfDouble1 = arrayOfDouble2;
    fadeUp(arrayOfDouble2, 80);
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Guitar Noise", arrayOfDouble1);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Guitar Noise");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_gpiano(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.2D;
    double d3 = 0.001D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.06666666666666667D);
    double[] arrayOfDouble2 = new double[30];
    byte b2;
    for (b2 = 0; b2 < 30; b2++) {
      arrayOfDouble2[b2] = d4;
      d4 *= d5;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 2.0D;
    arrayOfDouble2[4] = arrayOfDouble2[4] * 2.0D;
    arrayOfDouble2[12] = arrayOfDouble2[12] * 0.9D;
    arrayOfDouble2[13] = arrayOfDouble2[13] * 0.7D;
    for (b2 = 14; b2 < 30; b2++)
      arrayOfDouble2[b2] = arrayOfDouble2[b2] * 0.5D; 
    for (b2 = 0; b2 < 30; b2++) {
      double d6 = 0.2D;
      double d7 = arrayOfDouble2[b2];
      if (b2 > 10) {
        d6 = 5.0D;
        d7 *= 10.0D;
      } 
      byte b = 0;
      if (b2 > 5)
        b = (b2 - 5) * 7; 
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1) + b, d6, d7);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Grand Piano", arrayOfDouble1, d1, 200);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Grand Piano", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -7000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, -6000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -5500);
    sF2Region.putInteger(8, 18000);
    return sF2Layer;
  }
  
  public static SF2Layer new_gpiano2(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.2D;
    double d3 = 0.001D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.05D);
    double[] arrayOfDouble2 = new double[30];
    byte b2;
    for (b2 = 0; b2 < 30; b2++) {
      arrayOfDouble2[b2] = d4;
      d4 *= d5;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 1.0D;
    arrayOfDouble2[4] = arrayOfDouble2[4] * 2.0D;
    arrayOfDouble2[12] = arrayOfDouble2[12] * 0.9D;
    arrayOfDouble2[13] = arrayOfDouble2[13] * 0.7D;
    for (b2 = 14; b2 < 30; b2++)
      arrayOfDouble2[b2] = arrayOfDouble2[b2] * 0.5D; 
    for (b2 = 0; b2 < 30; b2++) {
      double d6 = 0.2D;
      double d7 = arrayOfDouble2[b2];
      if (b2 > 10) {
        d6 = 5.0D;
        d7 *= 10.0D;
      } 
      byte b = 0;
      if (b2 > 5)
        b = (b2 - 5) * 7; 
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1) + b, d6, d7);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Grand Piano", arrayOfDouble1, d1, 200);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Grand Piano", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -7000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, -6000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -5500);
    sF2Region.putInteger(8, 18000);
    return sF2Layer;
  }
  
  public static SF2Layer new_piano_hammer(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 2;
    char c1 = 'က' * b1;
    double[] arrayOfDouble2 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble2.length; c2 += true)
      arrayOfDouble2[c2] = 2.0D * (random.nextDouble() - 0.5D); 
    fft(arrayOfDouble2);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble2.length; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'ࠀ' * b1; c2++)
      arrayOfDouble2[c2] = arrayOfDouble2[c2] * Math.exp(-Math.abs((c2 - '%') / b1) * 0.05D); 
    randomPhase(arrayOfDouble2, new Random(3049912L));
    ifft(arrayOfDouble2);
    normalize(arrayOfDouble2, 0.6D);
    arrayOfDouble2 = realPart(arrayOfDouble2);
    double d = 1.0D;
    for (byte b2 = 0; b2 < arrayOfDouble2.length; b2++) {
      arrayOfDouble2[b2] = arrayOfDouble2[b2] * d;
      d *= 0.9997D;
    } 
    double[] arrayOfDouble1 = arrayOfDouble2;
    fadeUp(arrayOfDouble2, 80);
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Piano Hammer", arrayOfDouble1);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Piano Hammer");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_piano1(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.2D;
    double d3 = 1.0E-4D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.025D);
    double[] arrayOfDouble2 = new double[30];
    byte b2;
    for (b2 = 0; b2 < 30; b2++) {
      arrayOfDouble2[b2] = d4;
      d4 *= d5;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 5.0D;
    arrayOfDouble2[2] = arrayOfDouble2[2] * 0.1D;
    arrayOfDouble2[7] = arrayOfDouble2[7] * 5.0D;
    for (b2 = 0; b2 < 30; b2++) {
      double d6 = 0.2D;
      double d7 = arrayOfDouble2[b2];
      if (b2 > 12) {
        d6 = 5.0D;
        d7 *= 10.0D;
      } 
      byte b = 0;
      if (b2 > 5)
        b = (b2 - 5) * 7; 
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1) + b, d6, d7);
    } 
    complexGaussianDist(arrayOfDouble1, d1 * 15.5D, 1.0D, 0.1D);
    complexGaussianDist(arrayOfDouble1, d1 * 17.5D, 1.0D, 0.01D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble1, d1, 200);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "EPiano", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, -1200);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -5500);
    sF2Region.putInteger(8, 16000);
    return sF2Layer;
  }
  
  public static SF2Layer new_epiano1(SF2Soundbank paramSF2Soundbank) {
    Random random = new Random(302030201L);
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 1.0E-4D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (byte b2 = 0; b2 < 40; b2++) {
      double d8 = 1.0D + (random.nextDouble() * 2.0D - 1.0D) * 1.0E-4D;
      double d9 = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble, d1 * (b2 + true) * d8, d9, d6);
      d6 *= d7;
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "EPiano", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, 1200);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -9000);
    sF2Region.putInteger(8, 16000);
    return sF2Layer;
  }
  
  public static SF2Layer new_epiano2(SF2Soundbank paramSF2Soundbank) {
    Random random = new Random(302030201L);
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.01D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 1.0E-5D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (byte b2 = 0; b2 < 40; b2++) {
      double d8 = 1.0D + (random.nextDouble() * 2.0D - 1.0D) * 1.0E-4D;
      double d9 = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble, d1 * (b2 + true) * d8, d9, d6);
      d6 *= d7;
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "EPiano", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "EPiano", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 8000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, 2400);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -9000);
    sF2Region.putInteger(8, 16000);
    sF2Region.putInteger(48, -100);
    return sF2Layer;
  }
  
  public static SF2Layer new_bass1(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 0.02D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.04D);
    double[] arrayOfDouble2 = new double[25];
    byte b2;
    for (b2 = 0; b2 < 25; b2++) {
      arrayOfDouble2[b2] = d6;
      d6 *= d7;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 8.0D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 4.0D;
    arrayOfDouble2[3] = arrayOfDouble2[3] * 8.0D;
    arrayOfDouble2[5] = arrayOfDouble2[5] * 8.0D;
    for (b2 = 0; b2 < 25; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), d, arrayOfDouble2[b2]);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Bass", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Bass", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, -3000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -5000);
    sF2Region.putInteger(8, 11000);
    sF2Region.putInteger(48, -100);
    return sF2Layer;
  }
  
  public static SF2Layer new_synthbass(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 0.02D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.04D);
    double[] arrayOfDouble2 = new double[25];
    byte b2;
    for (b2 = 0; b2 < 25; b2++) {
      arrayOfDouble2[b2] = d6;
      d6 *= d7;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 16.0D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 4.0D;
    arrayOfDouble2[3] = arrayOfDouble2[3] * 16.0D;
    arrayOfDouble2[5] = arrayOfDouble2[5] * 8.0D;
    for (b2 = 0; b2 < 25; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), d, arrayOfDouble2[b2]);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Bass", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Bass", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -12000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, -3000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, -3000);
    sF2Region.putInteger(9, 100);
    sF2Region.putInteger(8, 8000);
    sF2Region.putInteger(48, -100);
    return sF2Layer;
  }
  
  public static SF2Layer new_bass2(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 0.05D;
    double d3 = 0.05D;
    double d4 = 0.2D;
    double d5 = 0.002D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.04D);
    double[] arrayOfDouble2 = new double[25];
    byte b2;
    for (b2 = 0; b2 < 25; b2++) {
      arrayOfDouble2[b2] = d6;
      d6 *= d7;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 8.0D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 4.0D;
    arrayOfDouble2[3] = arrayOfDouble2[3] * 8.0D;
    arrayOfDouble2[5] = arrayOfDouble2[5] * 8.0D;
    for (b2 = 0; b2 < 25; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), d, arrayOfDouble2[b2]);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Bass2", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Bass2", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -8000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(26, -6000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(8, 5000);
    sF2Region.putInteger(48, -100);
    return sF2Layer;
  }
  
  public static SF2Layer new_solostring(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 2.0D;
    double d3 = 2.0D;
    double d4 = 0.2D;
    double d5 = 0.01D;
    double[] arrayOfDouble2 = new double[18];
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    byte b2;
    for (b2 = 0; b2 < arrayOfDouble2.length; b2++) {
      d6 *= d7;
      arrayOfDouble2[b2] = d6;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 5.0D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 5.0D;
    arrayOfDouble2[2] = arrayOfDouble2[2] * 5.0D;
    arrayOfDouble2[3] = arrayOfDouble2[3] * 4.0D;
    arrayOfDouble2[4] = arrayOfDouble2[4] * 4.0D;
    arrayOfDouble2[5] = arrayOfDouble2[5] * 3.0D;
    arrayOfDouble2[6] = arrayOfDouble2[6] * 3.0D;
    arrayOfDouble2[7] = arrayOfDouble2[7] * 2.0D;
    for (b2 = 0; b2 < arrayOfDouble2.length; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), d, d6);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Strings", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Strings", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -5000);
    sF2Region.putInteger(38, 1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    sF2Region.putInteger(24, -1000);
    sF2Region.putInteger(6, 15);
    return sF2Layer;
  }
  
  public static SF2Layer new_orchhit(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 2.0D;
    double d3 = 80.0D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (byte b2 = 0; b2 < 40; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble, d1 * (b2 + true), d, d6);
      d6 *= d7;
    } 
    complexGaussianDist(arrayOfDouble, d1 * 4.0D, 300.0D, 1.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Och Strings", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Och Strings", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -5000);
    sF2Region.putInteger(38, 200);
    sF2Region.putInteger(36, 200);
    sF2Region.putInteger(37, 1000);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_string2(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 2.0D;
    double d3 = 80.0D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (byte b2 = 0; b2 < 40; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble, d1 * (b2 + true), d, d6);
      d6 *= d7;
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Strings", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Strings", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -5000);
    sF2Region.putInteger(38, 1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_choir(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 25);
    double d2 = 2.0D;
    double d3 = 80.0D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    double[] arrayOfDouble2 = new double[40];
    byte b2;
    for (b2 = 0; b2 < arrayOfDouble2.length; b2++) {
      d6 *= d7;
      arrayOfDouble2[b2] = d6;
    } 
    arrayOfDouble2[5] = arrayOfDouble2[5] * 0.1D;
    arrayOfDouble2[6] = arrayOfDouble2[6] * 0.01D;
    arrayOfDouble2[7] = arrayOfDouble2[7] * 0.1D;
    arrayOfDouble2[8] = arrayOfDouble2[8] * 0.1D;
    for (b2 = 0; b2 < arrayOfDouble2.length; b2++) {
      double d = d2 + (d3 - d2) * b2 / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), d, arrayOfDouble2[b2]);
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Strings", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Strings", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -5000);
    sF2Region.putInteger(38, 1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_organ(SF2Soundbank paramSF2Soundbank) {
    Random random = new Random(102030201L);
    boolean bool = true;
    char c = 'က' * bool;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (bool * 15);
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.025D);
    for (byte b = 0; b < 12; b++) {
      double d = d2 + (d3 - d2) * b / 40.0D;
      complexGaussianDist(arrayOfDouble, d1 * (b + true), d, d6 * (0.5D + 3.0D * random.nextDouble()));
      d6 *= d7;
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Organ", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Organ", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -6000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_ch_organ(SF2Soundbank paramSF2Soundbank) {
    boolean bool = true;
    char c = 'က' * bool;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (bool * 15);
    double d2 = 0.01D;
    double d3 = 0.01D;
    double d4 = 0.2D;
    double d5 = 0.001D;
    double d6 = d4;
    double d7 = Math.pow(d5 / d4, 0.016666666666666666D);
    double[] arrayOfDouble2 = new double[60];
    byte b;
    for (b = 0; b < arrayOfDouble2.length; b++) {
      d6 *= d7;
      arrayOfDouble2[b] = d6;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 5.0D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 2.0D;
    arrayOfDouble2[2] = 0.0D;
    arrayOfDouble2[4] = 0.0D;
    arrayOfDouble2[5] = 0.0D;
    arrayOfDouble2[7] = arrayOfDouble2[7] * 7.0D;
    arrayOfDouble2[9] = 0.0D;
    arrayOfDouble2[10] = 0.0D;
    arrayOfDouble2[12] = 0.0D;
    arrayOfDouble2[15] = arrayOfDouble2[15] * 7.0D;
    arrayOfDouble2[18] = 0.0D;
    arrayOfDouble2[20] = 0.0D;
    arrayOfDouble2[24] = 0.0D;
    arrayOfDouble2[27] = arrayOfDouble2[27] * 5.0D;
    arrayOfDouble2[29] = 0.0D;
    arrayOfDouble2[30] = 0.0D;
    arrayOfDouble2[33] = 0.0D;
    arrayOfDouble2[36] = arrayOfDouble2[36] * 4.0D;
    arrayOfDouble2[37] = 0.0D;
    arrayOfDouble2[39] = 0.0D;
    arrayOfDouble2[42] = 0.0D;
    arrayOfDouble2[43] = 0.0D;
    arrayOfDouble2[47] = 0.0D;
    arrayOfDouble2[50] = arrayOfDouble2[50] * 4.0D;
    arrayOfDouble2[52] = 0.0D;
    arrayOfDouble2[55] = 0.0D;
    arrayOfDouble2[57] = 0.0D;
    arrayOfDouble2[10] = arrayOfDouble2[10] * 0.1D;
    arrayOfDouble2[11] = arrayOfDouble2[11] * 0.1D;
    arrayOfDouble2[12] = arrayOfDouble2[12] * 0.1D;
    arrayOfDouble2[13] = arrayOfDouble2[13] * 0.1D;
    arrayOfDouble2[17] = arrayOfDouble2[17] * 0.1D;
    arrayOfDouble2[18] = arrayOfDouble2[18] * 0.1D;
    arrayOfDouble2[19] = arrayOfDouble2[19] * 0.1D;
    arrayOfDouble2[20] = arrayOfDouble2[20] * 0.1D;
    for (b = 0; b < 60; b++) {
      double d = d2 + (d3 - d2) * b / 40.0D;
      complexGaussianDist(arrayOfDouble1, d1 * (b + 1), d, arrayOfDouble2[b]);
      d6 *= d7;
    } 
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Organ", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Organ", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -10000);
    sF2Region.putInteger(38, -1000);
    return sF2Layer;
  }
  
  public static SF2Layer new_flute(SF2Soundbank paramSF2Soundbank) {
    byte b = 8;
    char c = 'က' * b;
    double[] arrayOfDouble = new double[c * '\002'];
    double d = (b * 15);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 100.0D, 120.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 100.0D, 40.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 100.0D, 80.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.001D, 0.05D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.001D, 0.06D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.001D, 0.04D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.005D, 0.06D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.005D, 0.06D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.08D, 0.7D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.08D, 0.6D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.08D, 0.6D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.08D, 0.6D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.08D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.08D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.08D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 20.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 30.0D, 6.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Flute", arrayOfDouble, d);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Flute", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -6000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_horn(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (b1 * 15);
    double d2 = 0.5D;
    double d3 = 1.0E-11D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.025D);
    for (byte b2 = 0; b2 < 40; b2++) {
      if (!b2) {
        complexGaussianDist(arrayOfDouble, d1 * (b2 + true), 0.1D, d4 * 0.2D);
      } else {
        complexGaussianDist(arrayOfDouble, d1 * (b2 + true), 0.1D, d4);
      } 
      d4 *= d5;
    } 
    complexGaussianDist(arrayOfDouble, d1 * 2.0D, 100.0D, 1.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Horn", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Horn", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -6000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(26, -500);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, 5000);
    sF2Region.putInteger(8, 4500);
    return sF2Layer;
  }
  
  public static SF2Layer new_trumpet(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 15);
    double d2 = 0.5D;
    double d3 = 1.0E-5D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.0125D);
    double[] arrayOfDouble2 = new double[80];
    byte b2;
    for (b2 = 0; b2 < 80; b2++) {
      arrayOfDouble2[b2] = d4;
      d4 *= d5;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 0.05D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 0.2D;
    arrayOfDouble2[2] = arrayOfDouble2[2] * 0.5D;
    arrayOfDouble2[3] = arrayOfDouble2[3] * 0.85D;
    for (b2 = 0; b2 < 80; b2++)
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), 0.1D, arrayOfDouble2[b2]); 
    complexGaussianDist(arrayOfDouble1, d1 * 5.0D, 300.0D, 3.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Trumpet", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Trumpet", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -10000);
    sF2Region.putInteger(38, 0);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(26, -4000);
    sF2Region.putInteger(30, -2500);
    sF2Region.putInteger(11, 5000);
    sF2Region.putInteger(8, 4500);
    sF2Region.putInteger(9, 10);
    return sF2Layer;
  }
  
  public static SF2Layer new_brass_section(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 15);
    double d2 = 0.5D;
    double d3 = 0.005D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.03333333333333333D);
    double[] arrayOfDouble2 = new double[30];
    for (byte b2 = 0; b2 < 30; b2++) {
      arrayOfDouble2[b2] = d4;
      d4 *= d5;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 0.8D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 0.9D;
    double d6 = 5.0D;
    for (byte b3 = 0; b3 < 30; b3++) {
      complexGaussianDist(arrayOfDouble1, d1 * (b3 + true), 0.1D * d6, arrayOfDouble2[b3] * d6);
      d6 += 6.0D;
    } 
    complexGaussianDist(arrayOfDouble1, d1 * 6.0D, 300.0D, 2.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Brass Section", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Brass Section", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -9200);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(26, -3000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, 5000);
    sF2Region.putInteger(8, 4500);
    return sF2Layer;
  }
  
  public static SF2Layer new_trombone(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble1 = new double[c * '\002'];
    double d1 = (b1 * 15);
    double d2 = 0.5D;
    double d3 = 0.001D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.0125D);
    double[] arrayOfDouble2 = new double[80];
    byte b2;
    for (b2 = 0; b2 < 80; b2++) {
      arrayOfDouble2[b2] = d4;
      d4 *= d5;
    } 
    arrayOfDouble2[0] = arrayOfDouble2[0] * 0.3D;
    arrayOfDouble2[1] = arrayOfDouble2[1] * 0.7D;
    for (b2 = 0; b2 < 80; b2++)
      complexGaussianDist(arrayOfDouble1, d1 * (b2 + 1), 0.1D, arrayOfDouble2[b2]); 
    complexGaussianDist(arrayOfDouble1, d1 * 6.0D, 300.0D, 2.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Trombone", arrayOfDouble1, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Trombone", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -8000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(26, -2000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, 5000);
    sF2Region.putInteger(8, 4500);
    sF2Region.putInteger(9, 10);
    return sF2Layer;
  }
  
  public static SF2Layer new_sax(SF2Soundbank paramSF2Soundbank) {
    byte b1 = 8;
    char c = 'က' * b1;
    double[] arrayOfDouble = new double[c * '\002'];
    double d1 = (b1 * 15);
    double d2 = 0.5D;
    double d3 = 0.01D;
    double d4 = d2;
    double d5 = Math.pow(d3 / d2, 0.025D);
    for (byte b2 = 0; b2 < 40; b2++) {
      if (!b2 || b2 == 2) {
        complexGaussianDist(arrayOfDouble, d1 * (b2 + true), 0.1D, d4 * 4.0D);
      } else {
        complexGaussianDist(arrayOfDouble, d1 * (b2 + true), 0.1D, d4);
      } 
      d4 *= d5;
    } 
    complexGaussianDist(arrayOfDouble, d1 * 4.0D, 200.0D, 1.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Sax", arrayOfDouble, d1);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Sax", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -6000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(26, -3000);
    sF2Region.putInteger(30, 12000);
    sF2Region.putInteger(11, 5000);
    sF2Region.putInteger(8, 4500);
    return sF2Layer;
  }
  
  public static SF2Layer new_oboe(SF2Soundbank paramSF2Soundbank) {
    byte b = 8;
    char c = 'က' * b;
    double[] arrayOfDouble = new double[c * '\002'];
    double d = (b * 15);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 100.0D, 80.0D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.01D, 0.53D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.49D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.01D, 5.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.01D, 0.59D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.01D, 0.61D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.52D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.01D, 0.49D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.01D, 0.46D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.01D, 0.35D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.01D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 18.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 19.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 20.0D, 0.01D, 0.1D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Oboe", arrayOfDouble, d);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Oboe", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -6000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_bassoon(SF2Soundbank paramSF2Soundbank) {
    byte b = 8;
    char c = 'က' * b;
    double[] arrayOfDouble = new double[c * '\002'];
    double d = (b * 15);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 100.0D, 40.0D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 100.0D, 20.0D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.01D, 0.53D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.01D, 5.0D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.01D, 1.49D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.01D, 0.59D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.01D, 0.61D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.52D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.01D, 0.49D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.01D, 0.48D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.01D, 0.51D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.01D, 0.46D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.01D, 0.35D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.01D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 18.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 19.0D, 0.01D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 20.0D, 0.01D, 0.1D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Flute", arrayOfDouble, d);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Flute", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -6000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_clarinet(SF2Soundbank paramSF2Soundbank) {
    byte b = 8;
    char c = 'က' * b;
    double[] arrayOfDouble = new double[c * '\002'];
    double d = (b * 15);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 0.001D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 0.001D, 0.02D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 0.001D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 0.01D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 100.0D, 60.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 100.0D, 20.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 100.0D, 20.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 0.001D, 0.1D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 0.001D, 0.09D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 0.001D, 0.02D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 0.005D, 0.16D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 0.005D, 0.96D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 0.01D, 0.9D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 0.08D, 1.2D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 0.08D, 1.8D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 0.08D, 1.6D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 0.08D, 1.2D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 0.08D, 0.9D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 0.08D, 0.5D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 0.08D, 0.2D);
    complexGaussianDist(arrayOfDouble, d * 1.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 2.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 3.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 4.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 5.0D, 10.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 6.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 7.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 8.0D, 20.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 9.0D, 20.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 10.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 11.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 12.0D, 30.0D, 9.0D);
    complexGaussianDist(arrayOfDouble, d * 13.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 14.0D, 30.0D, 8.0D);
    complexGaussianDist(arrayOfDouble, d * 15.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 16.0D, 30.0D, 7.0D);
    complexGaussianDist(arrayOfDouble, d * 17.0D, 30.0D, 6.0D);
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Clarinet", arrayOfDouble, d);
    SF2Layer sF2Layer = newLayer(paramSF2Soundbank, "Clarinet", sF2Sample);
    SF2Region sF2Region = (SF2Region)sF2Layer.getRegions().get(0);
    sF2Region.putInteger(54, 1);
    sF2Region.putInteger(34, -6000);
    sF2Region.putInteger(38, -1000);
    sF2Region.putInteger(36, 4000);
    sF2Region.putInteger(37, -100);
    sF2Region.putInteger(8, 9500);
    return sF2Layer;
  }
  
  public static SF2Layer new_timpani(SF2Soundbank paramSF2Soundbank) {
    char c1 = '耀';
    double[] arrayOfDouble3 = new double['\002' * c1];
    double d1 = 48.0D;
    complexGaussianDist(arrayOfDouble3, d1 * 2.0D, 0.2D, 1.0D);
    complexGaussianDist(arrayOfDouble3, d1 * 3.0D, 0.2D, 0.7D);
    complexGaussianDist(arrayOfDouble3, d1 * 5.0D, 10.0D, 1.0D);
    complexGaussianDist(arrayOfDouble3, d1 * 6.0D, 9.0D, 1.0D);
    complexGaussianDist(arrayOfDouble3, d1 * 8.0D, 15.0D, 1.0D);
    complexGaussianDist(arrayOfDouble3, d1 * 9.0D, 18.0D, 0.8D);
    complexGaussianDist(arrayOfDouble3, d1 * 11.0D, 21.0D, 0.5D);
    complexGaussianDist(arrayOfDouble3, d1 * 13.0D, 28.0D, 0.3D);
    complexGaussianDist(arrayOfDouble3, d1 * 14.0D, 22.0D, 0.1D);
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.5D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d3 = arrayOfDouble3.length;
    for (byte b2 = 0; b2 < arrayOfDouble3.length; b2++) {
      double d = 1.0D - b2 / d3;
      arrayOfDouble3[b2] = arrayOfDouble3[b2] * d * d;
    } 
    fadeUp(arrayOfDouble3, 40);
    double[] arrayOfDouble1 = arrayOfDouble3;
    c1 = '䀀';
    arrayOfDouble3 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble3.length; c2 += true)
      arrayOfDouble3[c2] = 2.0D * (random.nextDouble() - 0.5D) * 0.1D; 
    fft(arrayOfDouble3);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble3.length; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    for (c2 = 'က'; c2 < ' '; c2++)
      arrayOfDouble3[c2] = 1.0D - (c2 - 'က') / 4096.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'Ĭ'; c2++) {
      d3 = 1.0D - c2 / 300.0D;
      arrayOfDouble3[c2] = arrayOfDouble3[c2] * (1.0D + 20.0D * d3 * d3);
    } 
    for (c2 = Character.MIN_VALUE; c2 < '\030'; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.9D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d2 = 1.0D;
    for (byte b1 = 0; b1 < arrayOfDouble3.length; b1++) {
      arrayOfDouble3[b1] = arrayOfDouble3[b1] * d2;
      d2 *= 0.9998D;
    } 
    double[] arrayOfDouble2 = arrayOfDouble3;
    for (c1 = Character.MIN_VALUE; c1 < arrayOfDouble2.length; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] + arrayOfDouble2[c1] * 0.02D; 
    normalize(arrayOfDouble1, 0.9D);
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Timpani", arrayOfDouble1);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Timpani");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.putInteger(48, -100);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_melodic_toms(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble3 = new double['\002' * c1];
    complexGaussianDist(arrayOfDouble3, 30.0D, 0.5D, 1.0D);
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.8D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d1 = arrayOfDouble3.length;
    for (byte b1 = 0; b1 < arrayOfDouble3.length; b1++)
      arrayOfDouble3[b1] = arrayOfDouble3[b1] * (1.0D - b1 / d1); 
    double[] arrayOfDouble1 = arrayOfDouble3;
    c1 = '䀀';
    arrayOfDouble3 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble3.length; c2 += true)
      arrayOfDouble3[c2] = 2.0D * (random.nextDouble() - 0.5D) * 0.1D; 
    fft(arrayOfDouble3);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble3.length; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    for (c2 = 'က'; c2 < ' '; c2++)
      arrayOfDouble3[c2] = 1.0D - (c2 - 'က') / 4096.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'È'; c2++) {
      double d = 1.0D - c2 / 200.0D;
      arrayOfDouble3[c2] = arrayOfDouble3[c2] * (1.0D + 20.0D * d * d);
    } 
    for (c2 = Character.MIN_VALUE; c2 < '\036'; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.9D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d2 = 1.0D;
    for (byte b2 = 0; b2 < arrayOfDouble3.length; b2++) {
      arrayOfDouble3[b2] = arrayOfDouble3[b2] * d2;
      d2 *= 0.9996D;
    } 
    double[] arrayOfDouble2 = arrayOfDouble3;
    for (c1 = Character.MIN_VALUE; c1 < arrayOfDouble2.length; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] + arrayOfDouble2[c1] * 0.5D; 
    for (c1 = Character.MIN_VALUE; c1 < '\005'; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] * c1 / 5.0D; 
    normalize(arrayOfDouble1, 0.99D);
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Melodic Toms", arrayOfDouble1);
    sF2Sample.setOriginalPitch(63);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Melodic Toms");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.putInteger(48, -100);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_reverse_cymbal(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble2 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble2.length; c2 += true)
      arrayOfDouble2[c2] = 2.0D * (random.nextDouble() - 0.5D); 
    for (c2 = c1 / '\002'; c2 < arrayOfDouble2.length; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'd'; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'Ѐ'; c2++) {
      double d = c2 / 1024.0D;
      arrayOfDouble2[c2] = 1.0D - d;
    } 
    double[] arrayOfDouble1 = arrayOfDouble2;
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Reverse Cymbal", arrayOfDouble1, 100.0D, 20);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Reverse Cymbal");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(34, -200);
    sF2LayerRegion.putInteger(36, -12000);
    sF2LayerRegion.putInteger(54, 1);
    sF2LayerRegion.putInteger(38, -1000);
    sF2LayerRegion.putInteger(37, 1000);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_snare_drum(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble3 = new double['\002' * c1];
    complexGaussianDist(arrayOfDouble3, 24.0D, 0.5D, 1.0D);
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.5D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d1 = arrayOfDouble3.length;
    for (byte b1 = 0; b1 < arrayOfDouble3.length; b1++)
      arrayOfDouble3[b1] = arrayOfDouble3[b1] * (1.0D - b1 / d1); 
    double[] arrayOfDouble1 = arrayOfDouble3;
    c1 = '䀀';
    arrayOfDouble3 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble3.length; c2 += true)
      arrayOfDouble3[c2] = 2.0D * (random.nextDouble() - 0.5D) * 0.1D; 
    fft(arrayOfDouble3);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble3.length; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    for (c2 = 'က'; c2 < ' '; c2++)
      arrayOfDouble3[c2] = 1.0D - (c2 - 'က') / 4096.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'Ĭ'; c2++) {
      double d = 1.0D - c2 / 300.0D;
      arrayOfDouble3[c2] = arrayOfDouble3[c2] * (1.0D + 20.0D * d * d);
    } 
    for (c2 = Character.MIN_VALUE; c2 < '\030'; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.9D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d2 = 1.0D;
    for (byte b2 = 0; b2 < arrayOfDouble3.length; b2++) {
      arrayOfDouble3[b2] = arrayOfDouble3[b2] * d2;
      d2 *= 0.9998D;
    } 
    double[] arrayOfDouble2 = arrayOfDouble3;
    for (c1 = Character.MIN_VALUE; c1 < arrayOfDouble2.length; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] + arrayOfDouble2[c1]; 
    for (c1 = Character.MIN_VALUE; c1 < '\005'; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] * c1 / 5.0D; 
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Snare Drum", arrayOfDouble1);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Snare Drum");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.putInteger(56, 0);
    sF2LayerRegion.putInteger(48, -100);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_bass_drum(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble3 = new double['\002' * c1];
    complexGaussianDist(arrayOfDouble3, 10.0D, 2.0D, 1.0D);
    complexGaussianDist(arrayOfDouble3, 17.2D, 2.0D, 1.0D);
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.9D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d1 = arrayOfDouble3.length;
    for (byte b1 = 0; b1 < arrayOfDouble3.length; b1++)
      arrayOfDouble3[b1] = arrayOfDouble3[b1] * (1.0D - b1 / d1); 
    double[] arrayOfDouble1 = arrayOfDouble3;
    c1 = 'က';
    arrayOfDouble3 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble3.length; c2 += true)
      arrayOfDouble3[c2] = 2.0D * (random.nextDouble() - 0.5D) * 0.1D; 
    fft(arrayOfDouble3);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble3.length; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    for (c2 = 'Ѐ'; c2 < 'ࠀ'; c2++)
      arrayOfDouble3[c2] = 1.0D - (c2 - 'Ѐ') / 1024.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'Ȁ'; c2++)
      arrayOfDouble3[c2] = ('\n' * c2) / 512.0D; 
    for (c2 = Character.MIN_VALUE; c2 < '\n'; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.9D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d2 = 1.0D;
    for (byte b2 = 0; b2 < arrayOfDouble3.length; b2++) {
      arrayOfDouble3[b2] = arrayOfDouble3[b2] * d2;
      d2 *= 0.999D;
    } 
    double[] arrayOfDouble2 = arrayOfDouble3;
    for (c1 = Character.MIN_VALUE; c1 < arrayOfDouble2.length; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] + arrayOfDouble2[c1] * 0.5D; 
    for (c1 = Character.MIN_VALUE; c1 < '\005'; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] * c1 / 5.0D; 
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Bass Drum", arrayOfDouble1);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Bass Drum");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.putInteger(56, 0);
    sF2LayerRegion.putInteger(48, -100);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_tom(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble3 = new double['\002' * c1];
    complexGaussianDist(arrayOfDouble3, 30.0D, 0.5D, 1.0D);
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.8D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d1 = arrayOfDouble3.length;
    for (byte b1 = 0; b1 < arrayOfDouble3.length; b1++)
      arrayOfDouble3[b1] = arrayOfDouble3[b1] * (1.0D - b1 / d1); 
    double[] arrayOfDouble1 = arrayOfDouble3;
    c1 = '䀀';
    arrayOfDouble3 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble3.length; c2 += true)
      arrayOfDouble3[c2] = 2.0D * (random.nextDouble() - 0.5D) * 0.1D; 
    fft(arrayOfDouble3);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble3.length; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    for (c2 = 'က'; c2 < ' '; c2++)
      arrayOfDouble3[c2] = 1.0D - (c2 - 'က') / 4096.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'È'; c2++) {
      double d = 1.0D - c2 / 200.0D;
      arrayOfDouble3[c2] = arrayOfDouble3[c2] * (1.0D + 20.0D * d * d);
    } 
    for (c2 = Character.MIN_VALUE; c2 < '\036'; c2++)
      arrayOfDouble3[c2] = 0.0D; 
    randomPhase(arrayOfDouble3, new Random(3049912L));
    ifft(arrayOfDouble3);
    normalize(arrayOfDouble3, 0.9D);
    arrayOfDouble3 = realPart(arrayOfDouble3);
    double d2 = 1.0D;
    for (byte b2 = 0; b2 < arrayOfDouble3.length; b2++) {
      arrayOfDouble3[b2] = arrayOfDouble3[b2] * d2;
      d2 *= 0.9996D;
    } 
    double[] arrayOfDouble2 = arrayOfDouble3;
    for (c1 = Character.MIN_VALUE; c1 < arrayOfDouble2.length; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] + arrayOfDouble2[c1] * 0.5D; 
    for (c1 = Character.MIN_VALUE; c1 < '\005'; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] * c1 / 5.0D; 
    normalize(arrayOfDouble1, 0.99D);
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Tom", arrayOfDouble1);
    sF2Sample.setOriginalPitch(50);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Tom");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.putInteger(48, -100);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_closed_hihat(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble2 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble2.length; c2 += true)
      arrayOfDouble2[c2] = 2.0D * (random.nextDouble() - 0.5D) * 0.1D; 
    fft(arrayOfDouble2);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble2.length; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = 'က'; c2 < ' '; c2++)
      arrayOfDouble2[c2] = 1.0D - (c2 - 'က') / 4096.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'ࠀ'; c2++)
      arrayOfDouble2[c2] = 0.2D + 0.8D * c2 / 2048.0D; 
    randomPhase(arrayOfDouble2, new Random(3049912L));
    ifft(arrayOfDouble2);
    normalize(arrayOfDouble2, 0.9D);
    arrayOfDouble2 = realPart(arrayOfDouble2);
    double d = 1.0D;
    for (byte b = 0; b < arrayOfDouble2.length; b++) {
      arrayOfDouble2[b] = arrayOfDouble2[b] * d;
      d *= 0.9996D;
    } 
    double[] arrayOfDouble1 = arrayOfDouble2;
    for (c1 = Character.MIN_VALUE; c1 < '\005'; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] * c1 / 5.0D; 
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Closed Hi-Hat", arrayOfDouble1);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Closed Hi-Hat");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.putInteger(56, 0);
    sF2LayerRegion.putInteger(57, 1);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_open_hihat(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble2 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble2.length; c2 += true)
      arrayOfDouble2[c2] = 2.0D * (random.nextDouble() - 0.5D); 
    for (c2 = c1 / '\002'; c2 < arrayOfDouble2.length; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'È'; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < ' '; c2++) {
      double d = c2 / 8192.0D;
      arrayOfDouble2[c2] = d;
    } 
    double[] arrayOfDouble1 = arrayOfDouble2;
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Open Hi-Hat", arrayOfDouble1, 1000.0D, 5);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Open Hi-Hat");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(36, 1500);
    sF2LayerRegion.putInteger(54, 1);
    sF2LayerRegion.putInteger(38, 1500);
    sF2LayerRegion.putInteger(37, 1000);
    sF2LayerRegion.putInteger(56, 0);
    sF2LayerRegion.putInteger(57, 1);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_crash_cymbal(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble2 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble2.length; c2 += true)
      arrayOfDouble2[c2] = 2.0D * (random.nextDouble() - 0.5D); 
    for (c2 = c1 / '\002'; c2 < arrayOfDouble2.length; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'd'; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'Ѐ'; c2++) {
      double d = c2 / 1024.0D;
      arrayOfDouble2[c2] = d;
    } 
    double[] arrayOfDouble1 = arrayOfDouble2;
    SF2Sample sF2Sample = newSimpleFFTSample(paramSF2Soundbank, "Crash Cymbal", arrayOfDouble1, 1000.0D, 5);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Crash Cymbal");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(36, 1800);
    sF2LayerRegion.putInteger(54, 1);
    sF2LayerRegion.putInteger(38, 1800);
    sF2LayerRegion.putInteger(37, 1000);
    sF2LayerRegion.putInteger(56, 0);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Layer new_side_stick(SF2Soundbank paramSF2Soundbank) {
    char c1 = '䀀';
    double[] arrayOfDouble2 = new double['\002' * c1];
    Random random = new Random(3049912L);
    char c2;
    for (c2 = Character.MIN_VALUE; c2 < arrayOfDouble2.length; c2 += true)
      arrayOfDouble2[c2] = 2.0D * (random.nextDouble() - 0.5D) * 0.1D; 
    fft(arrayOfDouble2);
    for (c2 = c1 / '\002'; c2 < arrayOfDouble2.length; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    for (c2 = 'က'; c2 < ' '; c2++)
      arrayOfDouble2[c2] = 1.0D - (c2 - 'က') / 4096.0D; 
    for (c2 = Character.MIN_VALUE; c2 < 'È'; c2++) {
      double d1 = 1.0D - c2 / 200.0D;
      arrayOfDouble2[c2] = arrayOfDouble2[c2] * (1.0D + 20.0D * d1 * d1);
    } 
    for (c2 = Character.MIN_VALUE; c2 < '\036'; c2++)
      arrayOfDouble2[c2] = 0.0D; 
    randomPhase(arrayOfDouble2, new Random(3049912L));
    ifft(arrayOfDouble2);
    normalize(arrayOfDouble2, 0.9D);
    arrayOfDouble2 = realPart(arrayOfDouble2);
    double d = 1.0D;
    for (byte b = 0; b < arrayOfDouble2.length; b++) {
      arrayOfDouble2[b] = arrayOfDouble2[b] * d;
      d *= 0.9996D;
    } 
    double[] arrayOfDouble1 = arrayOfDouble2;
    for (c1 = Character.MIN_VALUE; c1 < '\n'; c1++)
      arrayOfDouble1[c1] = arrayOfDouble1[c1] * c1 / 10.0D; 
    SF2Sample sF2Sample = newSimpleDrumSample(paramSF2Soundbank, "Side Stick", arrayOfDouble1);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName("Side Stick");
    SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
    sF2Layer.setGlobalZone(sF2GlobalRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.putInteger(38, 12000);
    sF2LayerRegion.putInteger(56, 0);
    sF2LayerRegion.putInteger(48, -50);
    sF2LayerRegion.setSample(sF2Sample);
    sF2Layer.getRegions().add(sF2LayerRegion);
    return sF2Layer;
  }
  
  public static SF2Sample newSimpleFFTSample(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble, double paramDouble) { return newSimpleFFTSample(paramSF2Soundbank, paramString, paramArrayOfDouble, paramDouble, 10); }
  
  public static SF2Sample newSimpleFFTSample(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble, double paramDouble, int paramInt) {
    int i = paramArrayOfDouble.length / 2;
    AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
    double d1 = paramDouble / i * audioFormat.getSampleRate() * 0.5D;
    randomPhase(paramArrayOfDouble);
    ifft(paramArrayOfDouble);
    paramArrayOfDouble = realPart(paramArrayOfDouble);
    normalize(paramArrayOfDouble, 0.9D);
    float[] arrayOfFloat = toFloat(paramArrayOfDouble);
    arrayOfFloat = loopExtend(arrayOfFloat, arrayOfFloat.length + 512);
    fadeUp(arrayOfFloat, paramInt);
    byte[] arrayOfByte = toBytes(arrayOfFloat, audioFormat);
    SF2Sample sF2Sample = new SF2Sample(paramSF2Soundbank);
    sF2Sample.setName(paramString);
    sF2Sample.setData(arrayOfByte);
    sF2Sample.setStartLoop(256L);
    sF2Sample.setEndLoop((i + 256));
    sF2Sample.setSampleRate((long)audioFormat.getSampleRate());
    double d2 = 81.0D + 12.0D * Math.log(d1 / 440.0D) / Math.log(2.0D);
    sF2Sample.setOriginalPitch((int)d2);
    sF2Sample.setPitchCorrection((byte)(int)(-(d2 - (int)d2) * 100.0D));
    paramSF2Soundbank.addResource(sF2Sample);
    return sF2Sample;
  }
  
  public static SF2Sample newSimpleFFTSample_dist(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble, double paramDouble1, double paramDouble2) {
    int i = paramArrayOfDouble.length / 2;
    AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
    double d1 = paramDouble1 / i * audioFormat.getSampleRate() * 0.5D;
    randomPhase(paramArrayOfDouble);
    ifft(paramArrayOfDouble);
    paramArrayOfDouble = realPart(paramArrayOfDouble);
    for (byte b = 0; b < paramArrayOfDouble.length; b++)
      paramArrayOfDouble[b] = (1.0D - Math.exp(-Math.abs(paramArrayOfDouble[b] * paramDouble2))) * Math.signum(paramArrayOfDouble[b]); 
    normalize(paramArrayOfDouble, 0.9D);
    float[] arrayOfFloat = toFloat(paramArrayOfDouble);
    arrayOfFloat = loopExtend(arrayOfFloat, arrayOfFloat.length + 512);
    fadeUp(arrayOfFloat, 80);
    byte[] arrayOfByte = toBytes(arrayOfFloat, audioFormat);
    SF2Sample sF2Sample = new SF2Sample(paramSF2Soundbank);
    sF2Sample.setName(paramString);
    sF2Sample.setData(arrayOfByte);
    sF2Sample.setStartLoop(256L);
    sF2Sample.setEndLoop((i + 256));
    sF2Sample.setSampleRate((long)audioFormat.getSampleRate());
    double d2 = 81.0D + 12.0D * Math.log(d1 / 440.0D) / Math.log(2.0D);
    sF2Sample.setOriginalPitch((int)d2);
    sF2Sample.setPitchCorrection((byte)(int)(-(d2 - (int)d2) * 100.0D));
    paramSF2Soundbank.addResource(sF2Sample);
    return sF2Sample;
  }
  
  public static SF2Sample newSimpleDrumSample(SF2Soundbank paramSF2Soundbank, String paramString, double[] paramArrayOfDouble) {
    int i = paramArrayOfDouble.length;
    AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
    byte[] arrayOfByte = toBytes(toFloat(realPart(paramArrayOfDouble)), audioFormat);
    SF2Sample sF2Sample = new SF2Sample(paramSF2Soundbank);
    sF2Sample.setName(paramString);
    sF2Sample.setData(arrayOfByte);
    sF2Sample.setStartLoop(256L);
    sF2Sample.setEndLoop((i + 256));
    sF2Sample.setSampleRate((long)audioFormat.getSampleRate());
    sF2Sample.setOriginalPitch(60);
    paramSF2Soundbank.addResource(sF2Sample);
    return sF2Sample;
  }
  
  public static SF2Layer newLayer(SF2Soundbank paramSF2Soundbank, String paramString, SF2Sample paramSF2Sample) {
    SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
    sF2LayerRegion.setSample(paramSF2Sample);
    SF2Layer sF2Layer = new SF2Layer(paramSF2Soundbank);
    sF2Layer.setName(paramString);
    sF2Layer.getRegions().add(sF2LayerRegion);
    paramSF2Soundbank.addResource(sF2Layer);
    return sF2Layer;
  }
  
  public static SF2Instrument newInstrument(SF2Soundbank paramSF2Soundbank, String paramString, Patch paramPatch, SF2Layer... paramVarArgs) {
    SF2Instrument sF2Instrument = new SF2Instrument(paramSF2Soundbank);
    sF2Instrument.setPatch(paramPatch);
    sF2Instrument.setName(paramString);
    paramSF2Soundbank.addInstrument(sF2Instrument);
    for (byte b = 0; b < paramVarArgs.length; b++) {
      SF2InstrumentRegion sF2InstrumentRegion = new SF2InstrumentRegion();
      sF2InstrumentRegion.setLayer(paramVarArgs[b]);
      sF2Instrument.getRegions().add(sF2InstrumentRegion);
    } 
    return sF2Instrument;
  }
  
  public static void ifft(double[] paramArrayOfDouble) { (new FFT(paramArrayOfDouble.length / 2, 1)).transform(paramArrayOfDouble); }
  
  public static void fft(double[] paramArrayOfDouble) { (new FFT(paramArrayOfDouble.length / 2, -1)).transform(paramArrayOfDouble); }
  
  public static void complexGaussianDist(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3) {
    for (byte b = 0; b < paramArrayOfDouble.length / 4; b++)
      paramArrayOfDouble[b * 2] = paramArrayOfDouble[b * 2] + paramDouble3 * 1.0D / paramDouble2 * Math.sqrt(6.283185307179586D) * Math.exp(-0.5D * Math.pow((b - paramDouble1) / paramDouble2, 2.0D)); 
  }
  
  public static void randomPhase(double[] paramArrayOfDouble) {
    for (boolean bool = false; bool < paramArrayOfDouble.length; bool += true) {
      double d1 = Math.random() * 2.0D * Math.PI;
      double d2 = paramArrayOfDouble[bool];
      paramArrayOfDouble[bool] = Math.sin(d1) * d2;
      paramArrayOfDouble[bool + true] = Math.cos(d1) * d2;
    } 
  }
  
  public static void randomPhase(double[] paramArrayOfDouble, Random paramRandom) {
    for (boolean bool = false; bool < paramArrayOfDouble.length; bool += true) {
      double d1 = paramRandom.nextDouble() * 2.0D * Math.PI;
      double d2 = paramArrayOfDouble[bool];
      paramArrayOfDouble[bool] = Math.sin(d1) * d2;
      paramArrayOfDouble[bool + true] = Math.cos(d1) * d2;
    } 
  }
  
  public static void normalize(double[] paramArrayOfDouble, double paramDouble) {
    double d1 = 0.0D;
    for (byte b1 = 0; b1 < paramArrayOfDouble.length; b1++) {
      if (paramArrayOfDouble[b1] > d1)
        d1 = paramArrayOfDouble[b1]; 
      if (-paramArrayOfDouble[b1] > d1)
        d1 = -paramArrayOfDouble[b1]; 
    } 
    if (d1 == 0.0D)
      return; 
    double d2 = paramDouble / d1;
    for (byte b2 = 0; b2 < paramArrayOfDouble.length; b2++)
      paramArrayOfDouble[b2] = paramArrayOfDouble[b2] * d2; 
  }
  
  public static void normalize(float[] paramArrayOfFloat, double paramDouble) {
    double d1 = 0.5D;
    for (byte b1 = 0; b1 < paramArrayOfFloat.length; b1++) {
      if (paramArrayOfFloat[b1 * 2] > d1)
        d1 = paramArrayOfFloat[b1 * 2]; 
      if (-paramArrayOfFloat[b1 * 2] > d1)
        d1 = -paramArrayOfFloat[b1 * 2]; 
    } 
    double d2 = paramDouble / d1;
    for (byte b2 = 0; b2 < paramArrayOfFloat.length; b2++)
      paramArrayOfFloat[b2 * 2] = (float)(paramArrayOfFloat[b2 * 2] * d2); 
  }
  
  public static double[] realPart(double[] paramArrayOfDouble) {
    double[] arrayOfDouble = new double[paramArrayOfDouble.length / 2];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = paramArrayOfDouble[b * 2]; 
    return arrayOfDouble;
  }
  
  public static double[] imgPart(double[] paramArrayOfDouble) {
    double[] arrayOfDouble = new double[paramArrayOfDouble.length / 2];
    for (byte b = 0; b < arrayOfDouble.length; b++)
      arrayOfDouble[b] = paramArrayOfDouble[b * 2]; 
    return arrayOfDouble;
  }
  
  public static float[] toFloat(double[] paramArrayOfDouble) {
    float[] arrayOfFloat = new float[paramArrayOfDouble.length];
    for (byte b = 0; b < arrayOfFloat.length; b++)
      arrayOfFloat[b] = (float)paramArrayOfDouble[b]; 
    return arrayOfFloat;
  }
  
  public static byte[] toBytes(float[] paramArrayOfFloat, AudioFormat paramAudioFormat) {
    byte[] arrayOfByte = new byte[paramArrayOfFloat.length * paramAudioFormat.getFrameSize()];
    return AudioFloatConverter.getConverter(paramAudioFormat).toByteArray(paramArrayOfFloat, arrayOfByte);
  }
  
  public static void fadeUp(double[] paramArrayOfDouble, int paramInt) {
    double d = paramInt;
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfDouble[b] = paramArrayOfDouble[b] * b / d; 
  }
  
  public static void fadeUp(float[] paramArrayOfFloat, int paramInt) {
    double d = paramInt;
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfFloat[b] = (float)(paramArrayOfFloat[b] * b / d); 
  }
  
  public static double[] loopExtend(double[] paramArrayOfDouble, int paramInt) {
    double[] arrayOfDouble = new double[paramInt];
    int i = paramArrayOfDouble.length;
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfDouble.length; b2++) {
      arrayOfDouble[b2] = paramArrayOfDouble[b1];
      if (++b1 == i)
        b1 = 0; 
    } 
    return arrayOfDouble;
  }
  
  public static float[] loopExtend(float[] paramArrayOfFloat, int paramInt) {
    float[] arrayOfFloat = new float[paramInt];
    int i = paramArrayOfFloat.length;
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfFloat.length; b2++) {
      arrayOfFloat[b2] = paramArrayOfFloat[b1];
      if (++b1 == i)
        b1 = 0; 
    } 
    return arrayOfFloat;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\EmergencySoundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */