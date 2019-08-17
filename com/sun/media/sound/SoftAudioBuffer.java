package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.sampled.AudioFormat;

public final class SoftAudioBuffer {
  private int size;
  
  private float[] buffer;
  
  private boolean empty = true;
  
  private AudioFormat format;
  
  private AudioFloatConverter converter;
  
  private byte[] converter_buffer;
  
  public SoftAudioBuffer(int paramInt, AudioFormat paramAudioFormat) {
    this.size = paramInt;
    this.format = paramAudioFormat;
    this.converter = AudioFloatConverter.getConverter(paramAudioFormat);
  }
  
  public void swap(SoftAudioBuffer paramSoftAudioBuffer) {
    int i = this.size;
    float[] arrayOfFloat = this.buffer;
    boolean bool = this.empty;
    AudioFormat audioFormat = this.format;
    AudioFloatConverter audioFloatConverter = this.converter;
    byte[] arrayOfByte = this.converter_buffer;
    this.size = paramSoftAudioBuffer.size;
    this.buffer = paramSoftAudioBuffer.buffer;
    this.empty = paramSoftAudioBuffer.empty;
    this.format = paramSoftAudioBuffer.format;
    this.converter = paramSoftAudioBuffer.converter;
    this.converter_buffer = paramSoftAudioBuffer.converter_buffer;
    paramSoftAudioBuffer.size = i;
    paramSoftAudioBuffer.buffer = arrayOfFloat;
    paramSoftAudioBuffer.empty = bool;
    paramSoftAudioBuffer.format = audioFormat;
    paramSoftAudioBuffer.converter = audioFloatConverter;
    paramSoftAudioBuffer.converter_buffer = arrayOfByte;
  }
  
  public AudioFormat getFormat() { return this.format; }
  
  public int getSize() { return this.size; }
  
  public void clear() {
    if (!this.empty) {
      Arrays.fill(this.buffer, 0.0F);
      this.empty = true;
    } 
  }
  
  public boolean isSilent() { return this.empty; }
  
  public float[] array() {
    this.empty = false;
    if (this.buffer == null)
      this.buffer = new float[this.size]; 
    return this.buffer;
  }
  
  public void get(byte[] paramArrayOfByte, int paramInt) {
    int i = this.format.getFrameSize() / this.format.getChannels();
    int j = this.size * i;
    if (this.converter_buffer == null || this.converter_buffer.length < j)
      this.converter_buffer = new byte[j]; 
    if (this.format.getChannels() == 1) {
      this.converter.toByteArray(array(), this.size, paramArrayOfByte);
    } else {
      this.converter.toByteArray(array(), this.size, this.converter_buffer);
      if (paramInt >= this.format.getChannels())
        return; 
      int k = this.format.getChannels() * i;
      int m = i;
      for (int n = 0; n < i; n++) {
        int i1 = n;
        int i2 = paramInt * i + n;
        for (byte b = 0; b < this.size; b++) {
          paramArrayOfByte[i2] = this.converter_buffer[i1];
          i2 += k;
          i1 += m;
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftAudioBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */