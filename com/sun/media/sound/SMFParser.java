package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

final class SMFParser {
  private static final int MTrk_MAGIC = 1297379947;
  
  private static final boolean STRICT_PARSER = false;
  
  private static final boolean DEBUG = false;
  
  int tracks;
  
  DataInputStream stream;
  
  private int trackLength = 0;
  
  private byte[] trackData = null;
  
  private int pos = 0;
  
  private int readUnsigned() throws IOException { return this.trackData[this.pos++] & 0xFF; }
  
  private void read(byte[] paramArrayOfByte) throws IOException {
    System.arraycopy(this.trackData, this.pos, paramArrayOfByte, 0, paramArrayOfByte.length);
    this.pos += paramArrayOfByte.length;
  }
  
  private long readVarInt() throws IOException {
    long l = 0L;
    byte b = 0;
    do {
      b = this.trackData[this.pos++] & 0xFF;
      l = (l << 7) + (b & 0x7F);
    } while ((b & 0x80) != 0);
    return l;
  }
  
  private int readIntFromStream() throws IOException {
    try {
      return this.stream.readInt();
    } catch (EOFException eOFException) {
      throw new EOFException("invalid MIDI file");
    } 
  }
  
  boolean nextTrack() throws IOException, InvalidMidiDataException {
    int i;
    this.trackLength = 0;
    do {
      if (this.stream.skipBytes(this.trackLength) != this.trackLength)
        return false; 
      i = readIntFromStream();
      this.trackLength = readIntFromStream();
    } while (i != 1297379947);
    if (this.trackLength < 0)
      return false; 
    try {
      this.trackData = new byte[this.trackLength];
    } catch (OutOfMemoryError outOfMemoryError) {
      throw new IOException("Track length too big", outOfMemoryError);
    } 
    try {
      this.stream.readFully(this.trackData);
    } catch (EOFException eOFException) {
      return false;
    } 
    this.pos = 0;
    return true;
  }
  
  private boolean trackFinished() throws IOException, InvalidMidiDataException { return (this.pos >= this.trackLength); }
  
  void readTrack(Track paramTrack) throws IOException, InvalidMidiDataException {
    try {
      long l = 0L;
      int i = 0;
      boolean bool = false;
      while (!trackFinished() && !bool) {
        MetaMessage metaMessage2;
        byte[] arrayOfByte2;
        int i2;
        int i1;
        SysexMessage sysexMessage2;
        byte[] arrayOfByte1;
        int n;
        FastShortMessage fastShortMessage;
        MetaMessage metaMessage1;
        SysexMessage sysexMessage1;
        int j = -1;
        int k = 0;
        l += readVarInt();
        int m = readUnsigned();
        if (m >= 128) {
          i = m;
        } else {
          j = m;
        } 
        switch (i & 0xF0) {
          case 128:
          case 144:
          case 160:
          case 176:
          case 224:
            if (j == -1)
              j = readUnsigned(); 
            k = readUnsigned();
            fastShortMessage = new FastShortMessage(i | j << 8 | k << 16);
            break;
          case 192:
          case 208:
            if (j == -1)
              j = readUnsigned(); 
            fastShortMessage = new FastShortMessage(i | j << 8);
            break;
          case 240:
            switch (i) {
              case 240:
              case 247:
                n = (int)readVarInt();
                arrayOfByte1 = new byte[n];
                read(arrayOfByte1);
                sysexMessage2 = new SysexMessage();
                sysexMessage2.setMessage(i, arrayOfByte1, n);
                sysexMessage1 = sysexMessage2;
                break;
              case 255:
                i1 = readUnsigned();
                i2 = (int)readVarInt();
                try {
                  arrayOfByte2 = new byte[i2];
                } catch (OutOfMemoryError outOfMemoryError) {
                  throw new IOException("Meta length too big", outOfMemoryError);
                } 
                read(arrayOfByte2);
                metaMessage2 = new MetaMessage();
                metaMessage2.setMessage(i1, arrayOfByte2, i2);
                metaMessage1 = metaMessage2;
                if (i1 == 47)
                  bool = true; 
                break;
            } 
            throw new InvalidMidiDataException("Invalid status byte: " + i);
          default:
            throw new InvalidMidiDataException("Invalid status byte: " + i);
        } 
        paramTrack.add(new MidiEvent(metaMessage1, l));
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new EOFException("invalid MIDI file");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SMFParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */