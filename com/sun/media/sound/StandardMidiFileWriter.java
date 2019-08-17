package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.SequenceInputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileWriter;

public final class StandardMidiFileWriter extends MidiFileWriter {
  private static final int MThd_MAGIC = 1297377380;
  
  private static final int MTrk_MAGIC = 1297379947;
  
  private static final int ONE_BYTE = 1;
  
  private static final int TWO_BYTE = 2;
  
  private static final int SYSEX = 3;
  
  private static final int META = 4;
  
  private static final int ERROR = 5;
  
  private static final int IGNORE = 6;
  
  private static final int MIDI_TYPE_0 = 0;
  
  private static final int MIDI_TYPE_1 = 1;
  
  private static final int bufferSize = 16384;
  
  private DataOutputStream tddos;
  
  private static final int[] types = { 0, 1 };
  
  private static final long mask = 127L;
  
  public int[] getMidiFileTypes() {
    int[] arrayOfInt = new int[types.length];
    System.arraycopy(types, 0, arrayOfInt, 0, types.length);
    return arrayOfInt;
  }
  
  public int[] getMidiFileTypes(Sequence paramSequence) {
    int[] arrayOfInt;
    Track[] arrayOfTrack = paramSequence.getTracks();
    if (arrayOfTrack.length == 1) {
      arrayOfInt = new int[2];
      arrayOfInt[0] = 0;
      arrayOfInt[1] = 1;
    } else {
      arrayOfInt = new int[1];
      arrayOfInt[0] = 1;
    } 
    return arrayOfInt;
  }
  
  public boolean isFileTypeSupported(int paramInt) {
    for (byte b = 0; b < types.length; b++) {
      if (paramInt == types[b])
        return true; 
    } 
    return false;
  }
  
  public int write(Sequence paramSequence, int paramInt, OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = null;
    int i = 0;
    long l = 0L;
    if (!isFileTypeSupported(paramInt, paramSequence))
      throw new IllegalArgumentException("Could not write MIDI file"); 
    InputStream inputStream = getFileStream(paramInt, paramSequence);
    if (inputStream == null)
      throw new IllegalArgumentException("Could not write MIDI file"); 
    arrayOfByte = new byte[16384];
    while ((i = inputStream.read(arrayOfByte)) >= 0) {
      paramOutputStream.write(arrayOfByte, 0, i);
      l += i;
    } 
    return (int)l;
  }
  
  public int write(Sequence paramSequence, int paramInt, File paramFile) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
    int i = write(paramSequence, paramInt, fileOutputStream);
    fileOutputStream.close();
    return i;
  }
  
  private InputStream getFileStream(int paramInt, Sequence paramSequence) throws IOException {
    int i;
    Track[] arrayOfTrack = paramSequence.getTracks();
    byte b1 = 0;
    byte b2 = 14;
    byte b3 = 0;
    PipedOutputStream pipedOutputStream = null;
    DataOutputStream dataOutputStream = null;
    PipedInputStream pipedInputStream = null;
    InputStream[] arrayOfInputStream = null;
    InputStream inputStream = null;
    SequenceInputStream sequenceInputStream = null;
    if (paramInt == 0) {
      if (arrayOfTrack.length != 1)
        return null; 
    } else if (paramInt == 1) {
      if (arrayOfTrack.length < 1)
        return null; 
    } else if (arrayOfTrack.length == 1) {
      paramInt = 0;
    } else if (arrayOfTrack.length > 1) {
      paramInt = 1;
    } else {
      return null;
    } 
    arrayOfInputStream = new InputStream[arrayOfTrack.length];
    byte b4 = 0;
    byte b5;
    for (b5 = 0; b5 < arrayOfTrack.length; b5++) {
      try {
        arrayOfInputStream[b4] = writeTrack(arrayOfTrack[b5], paramInt);
        b4++;
      } catch (InvalidMidiDataException invalidMidiDataException) {}
    } 
    if (b4 == 1) {
      inputStream = arrayOfInputStream[0];
    } else if (b4 > 1) {
      inputStream = arrayOfInputStream[0];
      for (b5 = 1; b5 < arrayOfTrack.length; b5++) {
        if (arrayOfInputStream[b5] != null)
          inputStream = new SequenceInputStream(inputStream, arrayOfInputStream[b5]); 
      } 
    } else {
      throw new IllegalArgumentException("invalid MIDI data in sequence");
    } 
    pipedOutputStream = new PipedOutputStream();
    dataOutputStream = new DataOutputStream(pipedOutputStream);
    pipedInputStream = new PipedInputStream(pipedOutputStream);
    dataOutputStream.writeInt(1297377380);
    dataOutputStream.writeInt(b2 - 8);
    if (paramInt == 0) {
      dataOutputStream.writeShort(0);
    } else {
      dataOutputStream.writeShort(1);
    } 
    dataOutputStream.writeShort((short)b4);
    float f = paramSequence.getDivisionType();
    if (f == 0.0F) {
      i = paramSequence.getResolution();
    } else if (f == 24.0F) {
      i = -6144;
      i += (paramSequence.getResolution() & 0xFF);
    } else if (f == 25.0F) {
      i = -6400;
      i += (paramSequence.getResolution() & 0xFF);
    } else if (f == 29.97F) {
      i = -7424;
      i += (paramSequence.getResolution() & 0xFF);
    } else if (f == 30.0F) {
      i = -7680;
      i += (paramSequence.getResolution() & 0xFF);
    } else {
      return null;
    } 
    dataOutputStream.writeShort(i);
    sequenceInputStream = new SequenceInputStream(pipedInputStream, inputStream);
    dataOutputStream.close();
    b3 = b1 + b2;
    return sequenceInputStream;
  }
  
  private int getType(int paramInt) {
    if ((paramInt & 0xF0) == 240) {
      switch (paramInt) {
        case 240:
        case 247:
          return 3;
        case 255:
          return 4;
      } 
      return 6;
    } 
    switch (paramInt & 0xF0) {
      case 128:
      case 144:
      case 160:
      case 176:
      case 224:
        return 2;
      case 192:
      case 208:
        return 1;
    } 
    return 5;
  }
  
  private int writeVarInt(long paramLong) throws IOException {
    byte b1 = 1;
    byte b2;
    for (b2 = 63; b2 > 0 && (paramLong & 127L << b2) == 0L; b2 -= 7);
    while (b2 > 0) {
      this.tddos.writeByte((int)((paramLong & 127L << b2) >> b2 | 0x80L));
      b2 -= 7;
      b1++;
    } 
    this.tddos.writeByte((int)(paramLong & 0x7FL));
    return b1;
  }
  
  private InputStream writeTrack(Track paramTrack, int paramInt) throws IOException, InvalidMidiDataException {
    int i = 0;
    boolean bool = false;
    int j = paramTrack.size();
    PipedOutputStream pipedOutputStream = new PipedOutputStream();
    DataOutputStream dataOutputStream = new DataOutputStream(pipedOutputStream);
    PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    this.tddos = new DataOutputStream(byteArrayOutputStream);
    ByteArrayInputStream byteArrayInputStream = null;
    SequenceInputStream sequenceInputStream = null;
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    int k = -1;
    for (byte b = 0; b < j; b++) {
      int i3;
      int i2;
      int i1;
      MidiEvent midiEvent = paramTrack.get(b);
      byte[] arrayOfByte = null;
      ShortMessage shortMessage = null;
      MetaMessage metaMessage = null;
      SysexMessage sysexMessage = null;
      l3 = midiEvent.getTick();
      l2 = midiEvent.getTick() - l1;
      l1 = midiEvent.getTick();
      int m = midiEvent.getMessage().getStatus();
      int n = getType(m);
      switch (n) {
        case 1:
          shortMessage = (ShortMessage)midiEvent.getMessage();
          i1 = shortMessage.getData1();
          i += writeVarInt(l2);
          if (m != k) {
            k = m;
            this.tddos.writeByte(m);
            i++;
          } 
          this.tddos.writeByte(i1);
          i++;
          break;
        case 2:
          shortMessage = (ShortMessage)midiEvent.getMessage();
          i1 = shortMessage.getData1();
          i2 = shortMessage.getData2();
          i += writeVarInt(l2);
          if (m != k) {
            k = m;
            this.tddos.writeByte(m);
            i++;
          } 
          this.tddos.writeByte(i1);
          i++;
          this.tddos.writeByte(i2);
          i++;
          break;
        case 3:
          sysexMessage = (SysexMessage)midiEvent.getMessage();
          i3 = sysexMessage.getLength();
          arrayOfByte = sysexMessage.getMessage();
          i += writeVarInt(l2);
          k = m;
          this.tddos.writeByte(arrayOfByte[0]);
          i = ++i + writeVarInt((arrayOfByte.length - 1));
          this.tddos.write(arrayOfByte, 1, arrayOfByte.length - 1);
          i += arrayOfByte.length - 1;
          break;
        case 4:
          metaMessage = (MetaMessage)midiEvent.getMessage();
          i3 = metaMessage.getLength();
          arrayOfByte = metaMessage.getMessage();
          i += writeVarInt(l2);
          k = m;
          this.tddos.write(arrayOfByte, 0, arrayOfByte.length);
          i += arrayOfByte.length;
          break;
        case 6:
        case 5:
          break;
        default:
          throw new InvalidMidiDataException("internal file writer error");
      } 
    } 
    dataOutputStream.writeInt(1297379947);
    dataOutputStream.writeInt(i);
    i += 8;
    byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    sequenceInputStream = new SequenceInputStream(pipedInputStream, byteArrayInputStream);
    dataOutputStream.close();
    this.tddos.close();
    return sequenceInputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\StandardMidiFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */