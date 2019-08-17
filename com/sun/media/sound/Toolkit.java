package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class Toolkit {
  static void getUnsigned8(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
      paramArrayOfByte[i] = (byte)(paramArrayOfByte[i] + 128); 
  }
  
  static void getByteSwapped(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt1 + paramInt2; i += 2) {
      byte b = paramArrayOfByte[i];
      paramArrayOfByte[i] = paramArrayOfByte[i + 1];
      paramArrayOfByte[i + 1] = b;
    } 
  }
  
  static float linearToDB(float paramFloat) { return (float)(Math.log((paramFloat == 0.0D) ? 1.0E-4D : paramFloat) / Math.log(10.0D) * 20.0D); }
  
  static float dBToLinear(float paramFloat) { return (float)Math.pow(10.0D, paramFloat / 20.0D); }
  
  static long align(long paramLong, int paramInt) { return (paramInt <= 1) ? paramLong : (paramLong - paramLong % paramInt); }
  
  static int align(int paramInt1, int paramInt2) { return (paramInt2 <= 1) ? paramInt1 : (paramInt1 - paramInt1 % paramInt2); }
  
  static long millis2bytes(AudioFormat paramAudioFormat, long paramLong) {
    long l = (long)((float)paramLong * paramAudioFormat.getFrameRate() / 1000.0F * paramAudioFormat.getFrameSize());
    return align(l, paramAudioFormat.getFrameSize());
  }
  
  static long bytes2millis(AudioFormat paramAudioFormat, long paramLong) { return (long)((float)paramLong / paramAudioFormat.getFrameRate() * 1000.0F / paramAudioFormat.getFrameSize()); }
  
  static long micros2bytes(AudioFormat paramAudioFormat, long paramLong) {
    long l = (long)((float)paramLong * paramAudioFormat.getFrameRate() / 1000000.0F * paramAudioFormat.getFrameSize());
    return align(l, paramAudioFormat.getFrameSize());
  }
  
  static long bytes2micros(AudioFormat paramAudioFormat, long paramLong) { return (long)((float)paramLong / paramAudioFormat.getFrameRate() * 1000000.0F / paramAudioFormat.getFrameSize()); }
  
  static long micros2frames(AudioFormat paramAudioFormat, long paramLong) { return (long)((float)paramLong * paramAudioFormat.getFrameRate() / 1000000.0F); }
  
  static long frames2micros(AudioFormat paramAudioFormat, long paramLong) { return (long)(paramLong / paramAudioFormat.getFrameRate() * 1000000.0D); }
  
  static void isFullySpecifiedAudioFormat(AudioFormat paramAudioFormat) {
    if (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED) && !paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ULAW) && !paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.ALAW))
      return; 
    if (paramAudioFormat.getFrameRate() <= 0.0F)
      throw new IllegalArgumentException("invalid frame rate: " + ((paramAudioFormat.getFrameRate() == -1.0F) ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getFrameRate()))); 
    if (paramAudioFormat.getSampleRate() <= 0.0F)
      throw new IllegalArgumentException("invalid sample rate: " + ((paramAudioFormat.getSampleRate() == -1.0F) ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getSampleRate()))); 
    if (paramAudioFormat.getSampleSizeInBits() <= 0)
      throw new IllegalArgumentException("invalid sample size in bits: " + ((paramAudioFormat.getSampleSizeInBits() == -1) ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getSampleSizeInBits()))); 
    if (paramAudioFormat.getFrameSize() <= 0)
      throw new IllegalArgumentException("invalid frame size: " + ((paramAudioFormat.getFrameSize() == -1) ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getFrameSize()))); 
    if (paramAudioFormat.getChannels() <= 0)
      throw new IllegalArgumentException("invalid number of channels: " + ((paramAudioFormat.getChannels() == -1) ? "NOT_SPECIFIED" : String.valueOf(paramAudioFormat.getChannels()))); 
  }
  
  static boolean isFullySpecifiedPCMFormat(AudioFormat paramAudioFormat) { return (!paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !paramAudioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) ? false : (!(paramAudioFormat.getFrameRate() <= 0.0F || paramAudioFormat.getSampleRate() <= 0.0F || paramAudioFormat.getSampleSizeInBits() <= 0 || paramAudioFormat.getFrameSize() <= 0 || paramAudioFormat.getChannels() <= 0)); }
  
  public static AudioInputStream getPCMConvertedAudioInputStream(AudioInputStream paramAudioInputStream) {
    AudioFormat audioFormat = paramAudioInputStream.getFormat();
    if (!audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
      try {
        AudioFormat audioFormat1 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), Platform.isBigEndian());
        paramAudioInputStream = AudioSystem.getAudioInputStream(audioFormat1, paramAudioInputStream);
      } catch (Exception exception) {
        paramAudioInputStream = null;
      }  
    return paramAudioInputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\Toolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */