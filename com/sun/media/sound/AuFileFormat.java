package com.sun.media.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

final class AuFileFormat extends AudioFileFormat {
  static final int AU_SUN_MAGIC = 779316836;
  
  static final int AU_SUN_INV_MAGIC = 1684960046;
  
  static final int AU_DEC_MAGIC = 779314176;
  
  static final int AU_DEC_INV_MAGIC = 6583086;
  
  static final int AU_ULAW_8 = 1;
  
  static final int AU_LINEAR_8 = 2;
  
  static final int AU_LINEAR_16 = 3;
  
  static final int AU_LINEAR_24 = 4;
  
  static final int AU_LINEAR_32 = 5;
  
  static final int AU_FLOAT = 6;
  
  static final int AU_DOUBLE = 7;
  
  static final int AU_ADPCM_G721 = 23;
  
  static final int AU_ADPCM_G722 = 24;
  
  static final int AU_ADPCM_G723_3 = 25;
  
  static final int AU_ADPCM_G723_5 = 26;
  
  static final int AU_ALAW_8 = 27;
  
  static final int AU_HEADERSIZE = 24;
  
  private int auType;
  
  AuFileFormat(AudioFileFormat paramAudioFileFormat) { this(paramAudioFileFormat.getType(), paramAudioFileFormat.getByteLength(), paramAudioFileFormat.getFormat(), paramAudioFileFormat.getFrameLength()); }
  
  AuFileFormat(AudioFileFormat.Type paramType, int paramInt1, AudioFormat paramAudioFormat, int paramInt2) {
    super(paramType, paramInt1, paramAudioFormat, paramInt2);
    AudioFormat.Encoding encoding = paramAudioFormat.getEncoding();
    this.auType = -1;
    if (AudioFormat.Encoding.ALAW.equals(encoding)) {
      if (paramAudioFormat.getSampleSizeInBits() == 8)
        this.auType = 27; 
    } else if (AudioFormat.Encoding.ULAW.equals(encoding)) {
      if (paramAudioFormat.getSampleSizeInBits() == 8)
        this.auType = 1; 
    } else if (AudioFormat.Encoding.PCM_SIGNED.equals(encoding)) {
      if (paramAudioFormat.getSampleSizeInBits() == 8) {
        this.auType = 2;
      } else if (paramAudioFormat.getSampleSizeInBits() == 16) {
        this.auType = 3;
      } else if (paramAudioFormat.getSampleSizeInBits() == 24) {
        this.auType = 4;
      } else if (paramAudioFormat.getSampleSizeInBits() == 32) {
        this.auType = 5;
      } 
    } 
  }
  
  public int getAuType() { return this.auType; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AuFileFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */