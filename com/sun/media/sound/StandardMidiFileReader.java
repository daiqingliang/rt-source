package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;
import javax.sound.midi.spi.MidiFileReader;

public final class StandardMidiFileReader extends MidiFileReader {
  private static final int MThd_MAGIC = 1297377380;
  
  private static final int bisBufferSize = 1024;
  
  public MidiFileFormat getMidiFileFormat(InputStream paramInputStream) throws InvalidMidiDataException, IOException { return getMidiFileFormatFromStream(paramInputStream, -1, null); }
  
  private MidiFileFormat getMidiFileFormatFromStream(InputStream paramInputStream, int paramInt, SMFParser paramSMFParser) throws InvalidMidiDataException, IOException {
    short s2;
    float f;
    short s1;
    byte b = 16;
    byte b1 = -1;
    if (paramInputStream instanceof DataInputStream) {
      dataInputStream = (DataInputStream)paramInputStream;
    } else {
      dataInputStream = new DataInputStream(paramInputStream);
    } 
    if (paramSMFParser == null) {
      dataInputStream.mark(b);
    } else {
      paramSMFParser.stream = dataInputStream;
    } 
    try {
      int i = dataInputStream.readInt();
      if (i != 1297377380)
        throw new InvalidMidiDataException("not a valid MIDI file"); 
      int j = dataInputStream.readInt() - 6;
      s1 = dataInputStream.readShort();
      short s3 = dataInputStream.readShort();
      short s4 = dataInputStream.readShort();
      if (s4 > 0) {
        f = 0.0F;
        s2 = s4;
      } else {
        short s = -1 * (s4 >> 8);
        switch (s) {
          case 24:
            f = 24.0F;
            break;
          case 25:
            f = 25.0F;
            break;
          case 29:
            f = 29.97F;
            break;
          case 30:
            f = 30.0F;
            break;
          default:
            throw new InvalidMidiDataException("Unknown frame code: " + s);
        } 
        s2 = s4 & 0xFF;
      } 
      if (paramSMFParser != null) {
        dataInputStream.skip(j);
        paramSMFParser.tracks = s3;
      } 
    } finally {
      if (paramSMFParser == null)
        dataInputStream.reset(); 
    } 
    return new MidiFileFormat(s1, f, s2, paramInt, b1);
  }
  
  public MidiFileFormat getMidiFileFormat(URL paramURL) throws InvalidMidiDataException, IOException {
    InputStream inputStream = paramURL.openStream();
    bufferedInputStream = new BufferedInputStream(inputStream, 1024);
    MidiFileFormat midiFileFormat = null;
    try {
      midiFileFormat = getMidiFileFormat(bufferedInputStream);
    } finally {
      bufferedInputStream.close();
    } 
    return midiFileFormat;
  }
  
  public MidiFileFormat getMidiFileFormat(File paramFile) throws InvalidMidiDataException, IOException {
    FileInputStream fileInputStream = new FileInputStream(paramFile);
    bufferedInputStream = new BufferedInputStream(fileInputStream, 1024);
    long l = paramFile.length();
    if (l > 2147483647L)
      l = -1L; 
    MidiFileFormat midiFileFormat = null;
    try {
      midiFileFormat = getMidiFileFormatFromStream(bufferedInputStream, (int)l, null);
    } finally {
      bufferedInputStream.close();
    } 
    return midiFileFormat;
  }
  
  public Sequence getSequence(InputStream paramInputStream) throws InvalidMidiDataException, IOException {
    SMFParser sMFParser = new SMFParser();
    MidiFileFormat midiFileFormat = getMidiFileFormatFromStream(paramInputStream, -1, sMFParser);
    if (midiFileFormat.getType() != 0 && midiFileFormat.getType() != 1)
      throw new InvalidMidiDataException("Invalid or unsupported file type: " + midiFileFormat.getType()); 
    Sequence sequence = new Sequence(midiFileFormat.getDivisionType(), midiFileFormat.getResolution());
    for (byte b = 0; b < sMFParser.tracks && sMFParser.nextTrack(); b++)
      sMFParser.readTrack(sequence.createTrack()); 
    return sequence;
  }
  
  public Sequence getSequence(URL paramURL) throws InvalidMidiDataException, IOException {
    inputStream = paramURL.openStream();
    inputStream = new BufferedInputStream(inputStream, 1024);
    Sequence sequence = null;
    try {
      sequence = getSequence(inputStream);
    } finally {
      inputStream.close();
    } 
    return sequence;
  }
  
  public Sequence getSequence(File paramFile) throws InvalidMidiDataException, IOException {
    FileInputStream fileInputStream = new FileInputStream(paramFile);
    bufferedInputStream = new BufferedInputStream(fileInputStream, 1024);
    Sequence sequence = null;
    try {
      sequence = getSequence(bufferedInputStream);
    } finally {
      bufferedInputStream.close();
    } 
    return sequence;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\StandardMidiFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */