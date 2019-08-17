package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class SoftMidiAudioFileReader extends AudioFileReader {
  public static final AudioFileFormat.Type MIDI = new AudioFileFormat.Type("MIDI", "mid");
  
  private static AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  
  public AudioFileFormat getAudioFileFormat(Sequence paramSequence) throws UnsupportedAudioFileException, IOException {
    long l1 = paramSequence.getMicrosecondLength() / 1000000L;
    long l2 = (long)(format.getFrameRate() * (float)(l1 + 4L));
    return new AudioFileFormat(MIDI, format, (int)l2);
  }
  
  public AudioInputStream getAudioInputStream(Sequence paramSequence) throws UnsupportedAudioFileException, IOException {
    Receiver receiver;
    SoftSynthesizer softSynthesizer = new SoftSynthesizer();
    try {
      null = softSynthesizer.openStream(format, null);
      receiver = softSynthesizer.getReceiver();
    } catch (MidiUnavailableException midiUnavailableException) {
      throw new IOException(midiUnavailableException.toString());
    } 
    float f = paramSequence.getDivisionType();
    Track[] arrayOfTrack = paramSequence.getTracks();
    int[] arrayOfInt = new int[arrayOfTrack.length];
    int i = 500000;
    int j = paramSequence.getResolution();
    long l1 = 0L;
    long l2 = 0L;
    while (true) {
      MidiEvent midiEvent = null;
      byte b1 = -1;
      for (byte b2 = 0; b2 < arrayOfTrack.length; b2++) {
        int k = arrayOfInt[b2];
        Track track = arrayOfTrack[b2];
        if (k < track.size()) {
          MidiEvent midiEvent1 = track.get(k);
          if (midiEvent == null || midiEvent1.getTick() < midiEvent.getTick()) {
            midiEvent = midiEvent1;
            b1 = b2;
          } 
        } 
      } 
      if (b1 == -1)
        break; 
      arrayOfInt[b1] = arrayOfInt[b1] + 1;
      long l = midiEvent.getTick();
      if (f == 0.0F) {
        l2 += (l - l1) * i / j;
      } else {
        l2 = (long)(l * 1000000.0D * f / j);
      } 
      l1 = l;
      MidiMessage midiMessage = midiEvent.getMessage();
      if (midiMessage instanceof MetaMessage) {
        if (f == 0.0F && ((MetaMessage)midiMessage).getType() == 81) {
          byte[] arrayOfByte = ((MetaMessage)midiMessage).getData();
          if (arrayOfByte.length < 3)
            throw new UnsupportedAudioFileException(); 
          i = (arrayOfByte[0] & 0xFF) << 16 | (arrayOfByte[1] & 0xFF) << 8 | arrayOfByte[2] & 0xFF;
        } 
        continue;
      } 
      receiver.send(midiMessage, l2);
    } 
    long l3 = l2 / 1000000L;
    long l4 = (long)(null.getFormat().getFrameRate() * (float)(l3 + 4L));
    return new AudioInputStream(null, null.getFormat(), l4);
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    Sequence sequence;
    paramInputStream.mark(200);
    try {
      sequence = MidiSystem.getSequence(paramInputStream);
    } catch (InvalidMidiDataException invalidMidiDataException) {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    } catch (IOException iOException) {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    } 
    return getAudioInputStream(sequence);
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException {
    Sequence sequence;
    try {
      sequence = MidiSystem.getSequence(paramURL);
    } catch (InvalidMidiDataException invalidMidiDataException) {
      throw new UnsupportedAudioFileException();
    } catch (IOException iOException) {
      throw new UnsupportedAudioFileException();
    } 
    return getAudioFileFormat(sequence);
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException {
    Sequence sequence;
    try {
      sequence = MidiSystem.getSequence(paramFile);
    } catch (InvalidMidiDataException invalidMidiDataException) {
      throw new UnsupportedAudioFileException();
    } catch (IOException iOException) {
      throw new UnsupportedAudioFileException();
    } 
    return getAudioFileFormat(sequence);
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException {
    Sequence sequence;
    try {
      sequence = MidiSystem.getSequence(paramURL);
    } catch (InvalidMidiDataException invalidMidiDataException) {
      throw new UnsupportedAudioFileException();
    } catch (IOException iOException) {
      throw new UnsupportedAudioFileException();
    } 
    return getAudioInputStream(sequence);
  }
  
  public AudioInputStream getAudioInputStream(File paramFile) throws UnsupportedAudioFileException, IOException {
    Sequence sequence;
    if (!paramFile.getName().toLowerCase().endsWith(".mid"))
      throw new UnsupportedAudioFileException(); 
    try {
      sequence = MidiSystem.getSequence(paramFile);
    } catch (InvalidMidiDataException invalidMidiDataException) {
      throw new UnsupportedAudioFileException();
    } catch (IOException iOException) {
      throw new UnsupportedAudioFileException();
    } 
    return getAudioInputStream(sequence);
  }
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    Sequence sequence;
    paramInputStream.mark(200);
    try {
      sequence = MidiSystem.getSequence(paramInputStream);
    } catch (InvalidMidiDataException invalidMidiDataException) {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    } catch (IOException iOException) {
      paramInputStream.reset();
      throw new UnsupportedAudioFileException();
    } 
    return getAudioFileFormat(sequence);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMidiAudioFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */