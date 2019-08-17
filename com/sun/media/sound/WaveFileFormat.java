package com.sun.media.sound;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

final class WaveFileFormat extends AudioFileFormat {
  private final int waveType;
  
  private static final int STANDARD_HEADER_SIZE = 28;
  
  private static final int STANDARD_FMT_CHUNK_SIZE = 16;
  
  static final int RIFF_MAGIC = 1380533830;
  
  static final int WAVE_MAGIC = 1463899717;
  
  static final int FMT_MAGIC = 1718449184;
  
  static final int DATA_MAGIC = 1684108385;
  
  static final int WAVE_FORMAT_UNKNOWN = 0;
  
  static final int WAVE_FORMAT_PCM = 1;
  
  static final int WAVE_FORMAT_ADPCM = 2;
  
  static final int WAVE_FORMAT_ALAW = 6;
  
  static final int WAVE_FORMAT_MULAW = 7;
  
  static final int WAVE_FORMAT_OKI_ADPCM = 16;
  
  static final int WAVE_FORMAT_DIGISTD = 21;
  
  static final int WAVE_FORMAT_DIGIFIX = 22;
  
  static final int WAVE_IBM_FORMAT_MULAW = 257;
  
  static final int WAVE_IBM_FORMAT_ALAW = 258;
  
  static final int WAVE_IBM_FORMAT_ADPCM = 259;
  
  static final int WAVE_FORMAT_DVI_ADPCM = 17;
  
  static final int WAVE_FORMAT_SX7383 = 7175;
  
  WaveFileFormat(AudioFileFormat paramAudioFileFormat) { this(paramAudioFileFormat.getType(), paramAudioFileFormat.getByteLength(), paramAudioFileFormat.getFormat(), paramAudioFileFormat.getFrameLength()); }
  
  WaveFileFormat(AudioFileFormat.Type paramType, int paramInt1, AudioFormat paramAudioFormat, int paramInt2) {
    super(paramType, paramInt1, paramAudioFormat, paramInt2);
    AudioFormat.Encoding encoding = paramAudioFormat.getEncoding();
    if (encoding.equals(AudioFormat.Encoding.ALAW)) {
      this.waveType = 6;
    } else if (encoding.equals(AudioFormat.Encoding.ULAW)) {
      this.waveType = 7;
    } else if (encoding.equals(AudioFormat.Encoding.PCM_SIGNED) || encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      this.waveType = 1;
    } else {
      this.waveType = 0;
    } 
  }
  
  int getWaveType() { return this.waveType; }
  
  int getHeaderSize() { return getHeaderSize(getWaveType()); }
  
  static int getHeaderSize(int paramInt) { return 28 + getFmtChunkSize(paramInt); }
  
  static int getFmtChunkSize(int paramInt) {
    byte b = 16;
    if (paramInt != 1)
      b += 2; 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\WaveFileFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */