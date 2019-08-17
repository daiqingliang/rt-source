package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveExtensibleFileReader extends AudioFileReader {
  private static final String[] channelnames = { 
      "FL", "FR", "FC", "LF", "BL", "BR", "FLC", "FLR", "BC", "SL", 
      "SR", "TC", "TFL", "TFC", "TFR", "TBL", "TBC", "TBR" };
  
  private static final String[] allchannelnames = { 
      "w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9", "w10", 
      "w11", "w12", "w13", "w14", "w15", "w16", "w17", "w18", "w19", "w20", 
      "w21", "w22", "w23", "w24", "w25", "w26", "w27", "w28", "w29", "w30", 
      "w31", "w32", "w33", "w34", "w35", "w36", "w37", "w38", "w39", "w40", 
      "w41", "w42", "w43", "w44", "w45", "w46", "w47", "w48", "w49", "w50", 
      "w51", "w52", "w53", "w54", "w55", "w56", "w57", "w58", "w59", "w60", 
      "w61", "w62", "w63", "w64" };
  
  private static final GUID SUBTYPE_PCM = new GUID(1L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);
  
  private static final GUID SUBTYPE_IEEE_FLOAT = new GUID(3L, 0, 16, 128, 0, 0, 170, 0, 56, 155, 113);
  
  private String decodeChannelMask(long paramLong) {
    StringBuffer stringBuffer = new StringBuffer();
    long l = 1L;
    for (byte b = 0; b < allchannelnames.length; b++) {
      if ((paramLong & l) != 0L)
        if (b < channelnames.length) {
          stringBuffer.append(channelnames[b] + " ");
        } else {
          stringBuffer.append(allchannelnames[b] + " ");
        }  
      l *= 2L;
    } 
    return (stringBuffer.length() == 0) ? null : stringBuffer.substring(0, stringBuffer.length() - 1);
  }
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat;
    paramInputStream.mark(200);
    try {
      audioFileFormat = internal_getAudioFileFormat(paramInputStream);
    } finally {
      paramInputStream.reset();
    } 
    return audioFileFormat;
  }
  
  private AudioFileFormat internal_getAudioFileFormat(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    RIFFReader rIFFReader = new RIFFReader(paramInputStream);
    if (!rIFFReader.getFormat().equals("RIFF"))
      throw new UnsupportedAudioFileException(); 
    if (!rIFFReader.getType().equals("WAVE"))
      throw new UnsupportedAudioFileException(); 
    boolean bool1 = false;
    boolean bool2 = false;
    int i = 1;
    long l1 = 1L;
    int j = 1;
    int k = 1;
    int m = 1;
    long l2 = 0L;
    GUID gUID = null;
    while (rIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader1 = rIFFReader.nextChunk();
      if (rIFFReader1.getFormat().equals("fmt ")) {
        bool1 = true;
        int n = rIFFReader1.readUnsignedShort();
        if (n != 65534)
          throw new UnsupportedAudioFileException(); 
        i = rIFFReader1.readUnsignedShort();
        l1 = rIFFReader1.readUnsignedInt();
        rIFFReader1.readUnsignedInt();
        j = rIFFReader1.readUnsignedShort();
        k = rIFFReader1.readUnsignedShort();
        int i1 = rIFFReader1.readUnsignedShort();
        if (i1 != 22)
          throw new UnsupportedAudioFileException(); 
        m = rIFFReader1.readUnsignedShort();
        if (m > k)
          throw new UnsupportedAudioFileException(); 
        l2 = rIFFReader1.readUnsignedInt();
        gUID = GUID.read(rIFFReader1);
      } 
      if (rIFFReader1.getFormat().equals("data")) {
        bool2 = true;
        break;
      } 
    } 
    if (!bool1)
      throw new UnsupportedAudioFileException(); 
    if (!bool2)
      throw new UnsupportedAudioFileException(); 
    HashMap hashMap = new HashMap();
    String str = decodeChannelMask(l2);
    if (str != null)
      hashMap.put("channelOrder", str); 
    if (l2 != 0L)
      hashMap.put("channelMask", Long.valueOf(l2)); 
    hashMap.put("validBitsPerSample", Integer.valueOf(m));
    AudioFormat audioFormat = null;
    if (gUID.equals(SUBTYPE_PCM)) {
      if (k == 8) {
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)l1, k, i, j, (float)l1, false, hashMap);
      } else {
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)l1, k, i, j, (float)l1, false, hashMap);
      } 
    } else if (gUID.equals(SUBTYPE_IEEE_FLOAT)) {
      audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l1, k, i, j, (float)l1, false, hashMap);
    } else {
      throw new UnsupportedAudioFileException();
    } 
    return new AudioFileFormat(AudioFileFormat.Type.WAVE, audioFormat, -1);
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat = getAudioFileFormat(paramInputStream);
    RIFFReader rIFFReader = new RIFFReader(paramInputStream);
    if (!rIFFReader.getFormat().equals("RIFF"))
      throw new UnsupportedAudioFileException(); 
    if (!rIFFReader.getType().equals("WAVE"))
      throw new UnsupportedAudioFileException(); 
    while (rIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader1 = rIFFReader.nextChunk();
      if (rIFFReader1.getFormat().equals("data"))
        return new AudioInputStream(rIFFReader1, audioFileFormat.getFormat(), rIFFReader1.getSize()); 
    } 
    throw new UnsupportedAudioFileException();
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat;
    inputStream = paramURL.openStream();
    try {
      audioFileFormat = getAudioFileFormat(new BufferedInputStream(inputStream));
    } finally {
      inputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile) throws UnsupportedAudioFileException, IOException {
    AudioFileFormat audioFileFormat;
    fileInputStream = new FileInputStream(paramFile);
    try {
      audioFileFormat = getAudioFileFormat(new BufferedInputStream(fileInputStream));
    } finally {
      fileInputStream.close();
    } 
    return audioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL) throws UnsupportedAudioFileException, IOException { return getAudioInputStream(new BufferedInputStream(paramURL.openStream())); }
  
  public AudioInputStream getAudioInputStream(File paramFile) throws UnsupportedAudioFileException, IOException { return getAudioInputStream(new BufferedInputStream(new FileInputStream(paramFile))); }
  
  private static class GUID {
    long i1;
    
    int s1;
    
    int s2;
    
    int x1;
    
    int x2;
    
    int x3;
    
    int x4;
    
    int x5;
    
    int x6;
    
    int x7;
    
    int x8;
    
    private GUID() {}
    
    GUID(long param1Long, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, int param1Int9, int param1Int10) {
      this.i1 = param1Long;
      this.s1 = param1Int1;
      this.s2 = param1Int2;
      this.x1 = param1Int3;
      this.x2 = param1Int4;
      this.x3 = param1Int5;
      this.x4 = param1Int6;
      this.x5 = param1Int7;
      this.x6 = param1Int8;
      this.x7 = param1Int9;
      this.x8 = param1Int10;
    }
    
    public static GUID read(RIFFReader param1RIFFReader) throws IOException {
      GUID gUID = new GUID();
      gUID.i1 = param1RIFFReader.readUnsignedInt();
      gUID.s1 = param1RIFFReader.readUnsignedShort();
      gUID.s2 = param1RIFFReader.readUnsignedShort();
      gUID.x1 = param1RIFFReader.readUnsignedByte();
      gUID.x2 = param1RIFFReader.readUnsignedByte();
      gUID.x3 = param1RIFFReader.readUnsignedByte();
      gUID.x4 = param1RIFFReader.readUnsignedByte();
      gUID.x5 = param1RIFFReader.readUnsignedByte();
      gUID.x6 = param1RIFFReader.readUnsignedByte();
      gUID.x7 = param1RIFFReader.readUnsignedByte();
      gUID.x8 = param1RIFFReader.readUnsignedByte();
      return gUID;
    }
    
    public int hashCode() { return (int)this.i1; }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof GUID))
        return false; 
      GUID gUID = (GUID)param1Object;
      return (this.i1 != gUID.i1) ? false : ((this.s1 != gUID.s1) ? false : ((this.s2 != gUID.s2) ? false : ((this.x1 != gUID.x1) ? false : ((this.x2 != gUID.x2) ? false : ((this.x3 != gUID.x3) ? false : ((this.x4 != gUID.x4) ? false : ((this.x5 != gUID.x5) ? false : ((this.x6 != gUID.x6) ? false : ((this.x7 != gUID.x7) ? false : (!(this.x8 != gUID.x8)))))))))));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\WaveExtensibleFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */