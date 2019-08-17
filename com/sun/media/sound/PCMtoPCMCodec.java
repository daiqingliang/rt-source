package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class PCMtoPCMCodec extends SunCodec {
  private static final AudioFormat.Encoding[] inputEncodings = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
  
  private static final AudioFormat.Encoding[] outputEncodings = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED };
  
  private static final int tempBufferSize = 64;
  
  private byte[] tempBuffer = null;
  
  public PCMtoPCMCodec() { super(inputEncodings, outputEncodings); }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat) {
    if (paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      AudioFormat.Encoding[] arrayOfEncoding = new AudioFormat.Encoding[2];
      arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
      arrayOfEncoding[1] = AudioFormat.Encoding.PCM_UNSIGNED;
      return arrayOfEncoding;
    } 
    return new AudioFormat.Encoding[0];
  }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat) {
    AudioFormat[] arrayOfAudioFormat1 = getOutputFormats(paramAudioFormat);
    Vector vector = new Vector();
    for (byte b1 = 0; b1 < arrayOfAudioFormat1.length; b1++) {
      if (arrayOfAudioFormat1[b1].getEncoding().equals(paramEncoding))
        vector.addElement(arrayOfAudioFormat1[b1]); 
    } 
    AudioFormat[] arrayOfAudioFormat2 = new AudioFormat[vector.size()];
    for (byte b2 = 0; b2 < arrayOfAudioFormat2.length; b2++)
      arrayOfAudioFormat2[b2] = (AudioFormat)vector.elementAt(b2); 
    return arrayOfAudioFormat2;
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream) {
    if (isConversionSupported(paramEncoding, paramAudioInputStream.getFormat())) {
      AudioFormat audioFormat1 = paramAudioInputStream.getFormat();
      AudioFormat audioFormat2 = new AudioFormat(paramEncoding, audioFormat1.getSampleRate(), audioFormat1.getSampleSizeInBits(), audioFormat1.getChannels(), audioFormat1.getFrameSize(), audioFormat1.getFrameRate(), audioFormat1.isBigEndian());
      return getAudioInputStream(audioFormat2, paramAudioInputStream);
    } 
    throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream) { return getConvertedStream(paramAudioFormat, paramAudioInputStream); }
  
  private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream) {
    AudioInputStream audioInputStream = null;
    AudioFormat audioFormat = paramAudioInputStream.getFormat();
    if (audioFormat.matches(paramAudioFormat)) {
      audioInputStream = paramAudioInputStream;
    } else {
      audioInputStream = new PCMtoPCMCodecStream(paramAudioInputStream, paramAudioFormat);
      this.tempBuffer = new byte[64];
    } 
    return audioInputStream;
  }
  
  private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat) {
    AudioFormat[] arrayOfAudioFormat;
    Vector vector = new Vector();
    int i = paramAudioFormat.getSampleSizeInBits();
    boolean bool = paramAudioFormat.isBigEndian();
    if (i == 8) {
      if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())) {
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
      } 
      if (AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding())) {
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
      } 
    } else if (i == 16) {
      if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()) && bool) {
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
      } 
      if (AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding()) && bool) {
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
      } 
      if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()) && !bool) {
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        vector.addElement(audioFormat);
      } 
      if (AudioFormat.Encoding.PCM_UNSIGNED.equals(paramAudioFormat.getEncoding()) && !bool) {
        AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), false);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        vector.addElement(audioFormat);
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), paramAudioFormat.getSampleSizeInBits(), paramAudioFormat.getChannels(), paramAudioFormat.getFrameSize(), paramAudioFormat.getFrameRate(), true);
        vector.addElement(audioFormat);
      } 
    } 
    synchronized (vector) {
      arrayOfAudioFormat = new AudioFormat[vector.size()];
      for (byte b = 0; b < arrayOfAudioFormat.length; b++)
        arrayOfAudioFormat[b] = (AudioFormat)vector.elementAt(b); 
    } 
    return arrayOfAudioFormat;
  }
  
  class PCMtoPCMCodecStream extends AudioInputStream {
    private final int PCM_SWITCH_SIGNED_8BIT = 1;
    
    private final int PCM_SWITCH_ENDIAN = 2;
    
    private final int PCM_SWITCH_SIGNED_LE = 3;
    
    private final int PCM_SWITCH_SIGNED_BE = 4;
    
    private final int PCM_UNSIGNED_LE2SIGNED_BE = 5;
    
    private final int PCM_SIGNED_LE2UNSIGNED_BE = 6;
    
    private final int PCM_UNSIGNED_BE2SIGNED_LE = 7;
    
    private final int PCM_SIGNED_BE2UNSIGNED_LE = 8;
    
    private final int sampleSizeInBytes;
    
    private int conversionType = 0;
    
    PCMtoPCMCodecStream(AudioInputStream param1AudioInputStream, AudioFormat param1AudioFormat) {
      super(param1AudioInputStream, param1AudioFormat, -1L);
      int i = 0;
      AudioFormat.Encoding encoding1 = null;
      AudioFormat.Encoding encoding2 = null;
      AudioFormat audioFormat = param1AudioInputStream.getFormat();
      if (!this$0.isConversionSupported(audioFormat, param1AudioFormat))
        throw new IllegalArgumentException("Unsupported conversion: " + audioFormat.toString() + " to " + param1AudioFormat.toString()); 
      encoding1 = audioFormat.getEncoding();
      encoding2 = param1AudioFormat.getEncoding();
      boolean bool1 = audioFormat.isBigEndian();
      boolean bool2 = param1AudioFormat.isBigEndian();
      i = audioFormat.getSampleSizeInBits();
      this.sampleSizeInBytes = i / 8;
      if (i == 8) {
        if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding1) && AudioFormat.Encoding.PCM_SIGNED.equals(encoding2)) {
          this.conversionType = 1;
        } else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding1) && AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding2)) {
          this.conversionType = 1;
        } 
      } else if (encoding1.equals(encoding2) && bool1 != bool2) {
        this.conversionType = 2;
      } else if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding1) && !bool1 && AudioFormat.Encoding.PCM_SIGNED.equals(encoding2) && bool2) {
        this.conversionType = 5;
      } else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding1) && !bool1 && AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding2) && bool2) {
        this.conversionType = 6;
      } else if (AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding1) && bool1 && AudioFormat.Encoding.PCM_SIGNED.equals(encoding2) && !bool2) {
        this.conversionType = 7;
      } else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding1) && bool1 && AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding2) && !bool2) {
        this.conversionType = 8;
      } 
      this.frameSize = audioFormat.getFrameSize();
      if (this.frameSize == -1)
        this.frameSize = 1; 
      if (param1AudioInputStream instanceof AudioInputStream) {
        this.frameLength = param1AudioInputStream.getFrameLength();
      } else {
        this.frameLength = -1L;
      } 
      this.framePos = 0L;
    }
    
    public int read() throws IOException {
      if (this.frameSize == 1) {
        if (this.conversionType == 1) {
          null = super.read();
          if (null < 0)
            return null; 
          byte b = (byte)(null & 0xF);
          b = (b >= 0) ? (byte)(0x80 | b) : (byte)(0x7F & b);
          return b & 0xF;
        } 
        throw new IOException("cannot read a single byte if frame size > 1");
      } 
      throw new IOException("cannot read a single byte if frame size > 1");
    }
    
    public int read(byte[] param1ArrayOfByte) throws IOException { return read(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (param1Int2 % this.frameSize != 0)
        param1Int2 -= param1Int2 % this.frameSize; 
      if (this.frameLength != -1L && (param1Int2 / this.frameSize) > this.frameLength - this.framePos)
        param1Int2 = (int)(this.frameLength - this.framePos) * this.frameSize; 
      int i = super.read(param1ArrayOfByte, param1Int1, param1Int2);
      if (i < 0)
        return i; 
      switch (this.conversionType) {
        case 1:
          switchSigned8bit(param1ArrayOfByte, param1Int1, param1Int2, i);
          break;
        case 2:
          switchEndian(param1ArrayOfByte, param1Int1, param1Int2, i);
          break;
        case 3:
          switchSignedLE(param1ArrayOfByte, param1Int1, param1Int2, i);
          break;
        case 4:
          switchSignedBE(param1ArrayOfByte, param1Int1, param1Int2, i);
          break;
        case 5:
        case 6:
          switchSignedLE(param1ArrayOfByte, param1Int1, param1Int2, i);
          switchEndian(param1ArrayOfByte, param1Int1, param1Int2, i);
          break;
        case 7:
        case 8:
          switchSignedBE(param1ArrayOfByte, param1Int1, param1Int2, i);
          switchEndian(param1ArrayOfByte, param1Int1, param1Int2, i);
          break;
      } 
      return i;
    }
    
    private void switchSigned8bit(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, int param1Int3) {
      for (int i = param1Int1; i < param1Int1 + param1Int3; i++)
        param1ArrayOfByte[i] = (param1ArrayOfByte[i] >= 0) ? (byte)(0x80 | param1ArrayOfByte[i]) : (byte)(0x7F & param1ArrayOfByte[i]); 
    }
    
    private void switchSignedBE(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, int param1Int3) {
      int i;
      for (i = param1Int1; i < param1Int1 + param1Int3; i += this.sampleSizeInBytes)
        param1ArrayOfByte[i] = (param1ArrayOfByte[i] >= 0) ? (byte)(0x80 | param1ArrayOfByte[i]) : (byte)(0x7F & param1ArrayOfByte[i]); 
    }
    
    private void switchSignedLE(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, int param1Int3) {
      int i;
      for (i = param1Int1 + this.sampleSizeInBytes - 1; i < param1Int1 + param1Int3; i += this.sampleSizeInBytes)
        param1ArrayOfByte[i] = (param1ArrayOfByte[i] >= 0) ? (byte)(0x80 | param1ArrayOfByte[i]) : (byte)(0x7F & param1ArrayOfByte[i]); 
    }
    
    private void switchEndian(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, int param1Int3) {
      if (this.sampleSizeInBytes == 2) {
        int i;
        for (i = param1Int1; i < param1Int1 + param1Int3; i += this.sampleSizeInBytes) {
          byte b = param1ArrayOfByte[i];
          param1ArrayOfByte[i] = param1ArrayOfByte[i + 1];
          param1ArrayOfByte[i + 1] = b;
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\PCMtoPCMCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */