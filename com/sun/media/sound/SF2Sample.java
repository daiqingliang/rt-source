package com.sun.media.sound;

import java.io.InputStream;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SF2Sample extends SoundbankResource {
  String name = "";
  
  long startLoop = 0L;
  
  long endLoop = 0L;
  
  long sampleRate = 44100L;
  
  int originalPitch = 60;
  
  byte pitchCorrection = 0;
  
  int sampleLink = 0;
  
  int sampleType = 0;
  
  ModelByteBuffer data;
  
  ModelByteBuffer data24;
  
  public SF2Sample(Soundbank paramSoundbank) { super(paramSoundbank, null, AudioInputStream.class); }
  
  public SF2Sample() { super(null, null, AudioInputStream.class); }
  
  public Object getData() {
    AudioFormat audioFormat = getFormat();
    InputStream inputStream = this.data.getInputStream();
    return (inputStream == null) ? null : new AudioInputStream(inputStream, audioFormat, this.data.capacity());
  }
  
  public ModelByteBuffer getDataBuffer() { return this.data; }
  
  public ModelByteBuffer getData24Buffer() { return this.data24; }
  
  public AudioFormat getFormat() { return new AudioFormat((float)this.sampleRate, 16, 1, true, false); }
  
  public void setData(ModelByteBuffer paramModelByteBuffer) { this.data = paramModelByteBuffer; }
  
  public void setData(byte[] paramArrayOfByte) { this.data = new ModelByteBuffer(paramArrayOfByte); }
  
  public void setData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.data = new ModelByteBuffer(paramArrayOfByte, paramInt1, paramInt2); }
  
  public void setData24(ModelByteBuffer paramModelByteBuffer) { this.data24 = paramModelByteBuffer; }
  
  public void setData24(byte[] paramArrayOfByte) { this.data24 = new ModelByteBuffer(paramArrayOfByte); }
  
  public void setData24(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.data24 = new ModelByteBuffer(paramArrayOfByte, paramInt1, paramInt2); }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public long getEndLoop() { return this.endLoop; }
  
  public void setEndLoop(long paramLong) { this.endLoop = paramLong; }
  
  public int getOriginalPitch() { return this.originalPitch; }
  
  public void setOriginalPitch(int paramInt) { this.originalPitch = paramInt; }
  
  public byte getPitchCorrection() { return this.pitchCorrection; }
  
  public void setPitchCorrection(byte paramByte) { this.pitchCorrection = paramByte; }
  
  public int getSampleLink() { return this.sampleLink; }
  
  public void setSampleLink(int paramInt) { this.sampleLink = paramInt; }
  
  public long getSampleRate() { return this.sampleRate; }
  
  public void setSampleRate(long paramLong) { this.sampleRate = paramLong; }
  
  public int getSampleType() { return this.sampleType; }
  
  public void setSampleType(int paramInt) { this.sampleType = paramInt; }
  
  public long getStartLoop() { return this.startLoop; }
  
  public void setStartLoop(long paramLong) { this.startLoop = paramLong; }
  
  public String toString() { return "Sample: " + this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SF2Sample.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */