package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class UlawCodec extends SunCodec {
  private static final byte[] ULAW_TABH = new byte[256];
  
  private static final byte[] ULAW_TABL = new byte[256];
  
  private static final AudioFormat.Encoding[] ulawEncodings = { AudioFormat.Encoding.ULAW, AudioFormat.Encoding.PCM_SIGNED };
  
  private static final short[] seg_end = { 255, 511, 1023, 2047, 4095, 8191, 16383, Short.MAX_VALUE };
  
  public UlawCodec() { super(ulawEncodings, ulawEncodings); }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat) {
    if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) {
      if (paramAudioFormat.getSampleSizeInBits() == 16) {
        AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[1];
        arrayOfEncoding[0] = AudioFormat.Encoding.ULAW;
        return arrayOfEncoding;
      } 
      return new AudioFormat.Encoding[0];
    } 
    if (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding())) {
      if (paramAudioFormat.getSampleSizeInBits() == 8) {
        AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[1];
        arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
        return arrayOfEncoding;
      } 
      return new AudioFormat.Encoding[0];
    } 
    return new AudioFormat.Encoding[0];
  }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat) { return ((AudioFormat.Encoding.PCM_SIGNED.equals(paramEncoding) && AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding())) || (AudioFormat.Encoding.ULAW.equals(paramEncoding) && AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()))) ? getOutputFormats(paramAudioFormat) : new AudioFormat[0]; }
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream) {
    AudioFormat audioFormat1 = paramAudioInputStream.getFormat();
    AudioFormat.Encoding encoding = audioFormat1.getEncoding();
    if (encoding.equals(paramEncoding))
      return paramAudioInputStream; 
    AudioFormat audioFormat2 = null;
    if (!isConversionSupported(paramEncoding, paramAudioInputStream.getFormat()))
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString()); 
    if (AudioFormat.Encoding.ULAW.equals(encoding) && AudioFormat.Encoding.PCM_SIGNED.equals(paramEncoding)) {
      audioFormat2 = new AudioFormat(paramEncoding, audioFormat1.getSampleRate(), 16, audioFormat1.getChannels(), 2 * audioFormat1.getChannels(), audioFormat1.getSampleRate(), audioFormat1.isBigEndian());
    } else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding) && AudioFormat.Encoding.ULAW.equals(paramEncoding)) {
      audioFormat2 = new AudioFormat(paramEncoding, audioFormat1.getSampleRate(), 8, audioFormat1.getChannels(), audioFormat1.getChannels(), audioFormat1.getSampleRate(), false);
    } else {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
    } 
    return getAudioInputStream(audioFormat2, paramAudioInputStream);
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream) { return getConvertedStream(paramAudioFormat, paramAudioInputStream); }
  
  private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream) {
    AudioInputStream audioInputStream = null;
    AudioFormat audioFormat = paramAudioInputStream.getFormat();
    if (audioFormat.matches(paramAudioFormat)) {
      audioInputStream = paramAudioInputStream;
    } else {
      audioInputStream = new UlawCodecStream(paramAudioInputStream, paramAudioFormat);
    } 
    return audioInputStream;
  }
  
  private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat) {
    Vector vector = new Vector();
    if (paramAudioFormat.getSampleSizeInBits() == 16 && AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) {
      AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, paramAudioFormat.getSampleRate(), 8, paramAudioFormat.getChannels(), paramAudioFormat.getChannels(), paramAudioFormat.getSampleRate(), false);
      vector.addElement(audioFormat);
    } 
    if (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding())) {
      AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), false);
      vector.addElement(audioFormat);
      audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), true);
      vector.addElement(audioFormat);
    } 
    AudioFormat[] arrayOfAudioFormat = new AudioFormat[vector.size()];
    for (byte b = 0; b < arrayOfAudioFormat.length; b++)
      arrayOfAudioFormat[b] = (AudioFormat)vector.elementAt(b); 
    return arrayOfAudioFormat;
  }
  
  static  {
    for (byte b = 0; b < 'Ā'; b++) {
      short s1 = b ^ 0xFFFFFFFF;
      s1 &= 0xFF;
      short s2 = ((s1 & 0xF) << 3) + 132;
      s2 <<= (s1 & 0x70) >> 4;
      s2 = ((s1 & 0x80) != 0) ? (132 - s2) : (s2 - 132);
      ULAW_TABL[b] = (byte)(s2 & 0xFF);
      ULAW_TABH[b] = (byte)(s2 >> 8 & 0xFF);
    } 
  }
  
  class UlawCodecStream extends AudioInputStream {
    private static final int tempBufferSize = 64;
    
    private byte[] tempBuffer = null;
    
    boolean encode = false;
    
    AudioFormat encodeFormat;
    
    AudioFormat decodeFormat;
    
    byte[] tabByte1 = null;
    
    byte[] tabByte2 = null;
    
    int highByte = 0;
    
    int lowByte = 1;
    
    UlawCodecStream(AudioInputStream param1AudioInputStream, AudioFormat param1AudioFormat) {
      super(param1AudioInputStream, param1AudioFormat, -1L);
      AudioFormat audioFormat = param1AudioInputStream.getFormat();
      if (!this$0.isConversionSupported(param1AudioFormat, audioFormat))
        throw new IllegalArgumentException("Unsupported conversion: " + audioFormat.toString() + " to " + param1AudioFormat.toString()); 
      if (AudioFormat.Encoding.ULAW.equals(audioFormat.getEncoding())) {
        this.encode = false;
        this.encodeFormat = audioFormat;
        this.decodeFormat = param1AudioFormat;
        bool = param1AudioFormat.isBigEndian();
      } else {
        this.encode = true;
        this.encodeFormat = param1AudioFormat;
        this.decodeFormat = audioFormat;
        bool = audioFormat.isBigEndian();
        this.tempBuffer = new byte[64];
      } 
      if (bool) {
        this.tabByte1 = ULAW_TABH;
        this.tabByte2 = ULAW_TABL;
        this.highByte = 0;
        this.lowByte = 1;
      } else {
        this.tabByte1 = ULAW_TABL;
        this.tabByte2 = ULAW_TABH;
        this.highByte = 1;
        this.lowByte = 0;
      } 
      if (param1AudioInputStream instanceof AudioInputStream)
        this.frameLength = param1AudioInputStream.getFrameLength(); 
      this.framePos = 0L;
      this.frameSize = audioFormat.getFrameSize();
      if (this.frameSize == -1)
        this.frameSize = 1; 
    }
    
    private short search(short param1Short1, short[] param1ArrayOfShort, short param1Short2) {
      short s;
      for (s = 0; s < param1Short2; s = (short)(s + 1)) {
        if (param1Short1 <= param1ArrayOfShort[s])
          return s; 
      } 
      return param1Short2;
    }
    
    public int read() throws IOException {
      byte[] arrayOfByte = new byte[1];
      return (read(arrayOfByte, 0, arrayOfByte.length) == 1) ? (arrayOfByte[1] & 0xFF) : -1;
    }
    
    public int read(byte[] param1ArrayOfByte) throws IOException { return read(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (param1Int2 % this.frameSize != 0)
        param1Int2 -= param1Int2 % this.frameSize; 
      if (this.encode) {
        short s = 132;
        int n = 0;
        int i1 = param1Int1;
        int i2 = param1Int2 * 2;
        byte b;
        for (b = (i2 > 64) ? 64 : i2; (n = super.read(this.tempBuffer, 0, b)) > 0; b = (i2 > 64) ? 64 : i2) {
          for (int i3 = 0; i3 < n; i3 += 2) {
            byte b1;
            short s1;
            short s3 = (short)(this.tempBuffer[i3 + this.highByte] << 8 & 0xFF00);
            s3 = (short)(s3 | (short)((short)this.tempBuffer[i3 + this.lowByte] & 0xFF));
            if (s3 < 0) {
              s3 = (short)(s - s3);
              s1 = 127;
            } else {
              s3 = (short)(s3 + s);
              s1 = 255;
            } 
            short s2 = search(s3, seg_end, (short)8);
            if (s2 >= 8) {
              b1 = (byte)(0x7F ^ s1);
            } else {
              b1 = (byte)(s2 << 4 | s3 >> s2 + 3 & 0xF);
              b1 = (byte)(b1 ^ s1);
            } 
            param1ArrayOfByte[i1] = b1;
            i1++;
          } 
          i2 -= n;
        } 
        return (i1 == param1Int1 && n < 0) ? n : (i1 - param1Int1);
      } 
      int j = param1Int2 / 2;
      int k = param1Int1 + param1Int2 / 2;
      int m = super.read(param1ArrayOfByte, k, j);
      if (m < 0)
        return m; 
      int i;
      for (i = param1Int1; i < param1Int1 + m * 2; i += 2) {
        param1ArrayOfByte[i] = this.tabByte1[param1ArrayOfByte[k] & 0xFF];
        param1ArrayOfByte[i + 1] = this.tabByte2[param1ArrayOfByte[k] & 0xFF];
        k++;
      } 
      return i - param1Int1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\UlawCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */