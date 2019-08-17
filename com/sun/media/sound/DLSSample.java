package com.sun.media.sound;

import java.io.InputStream;
import java.util.Arrays;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class DLSSample extends SoundbankResource {
  byte[] guid = null;
  
  DLSInfo info = new DLSInfo();
  
  DLSSampleOptions sampleoptions;
  
  ModelByteBuffer data;
  
  AudioFormat format;
  
  public DLSSample(Soundbank paramSoundbank) { super(paramSoundbank, null, AudioInputStream.class); }
  
  public DLSSample() { super(null, null, AudioInputStream.class); }
  
  public DLSInfo getInfo() { return this.info; }
  
  public Object getData() {
    AudioFormat audioFormat = getFormat();
    InputStream inputStream = this.data.getInputStream();
    return (inputStream == null) ? null : new AudioInputStream(inputStream, audioFormat, this.data.capacity());
  }
  
  public ModelByteBuffer getDataBuffer() { return this.data; }
  
  public AudioFormat getFormat() { return this.format; }
  
  public void setFormat(AudioFormat paramAudioFormat) { this.format = paramAudioFormat; }
  
  public void setData(ModelByteBuffer paramModelByteBuffer) { this.data = paramModelByteBuffer; }
  
  public void setData(byte[] paramArrayOfByte) { this.data = new ModelByteBuffer(paramArrayOfByte); }
  
  public void setData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.data = new ModelByteBuffer(paramArrayOfByte, paramInt1, paramInt2); }
  
  public String getName() { return this.info.name; }
  
  public void setName(String paramString) { this.info.name = paramString; }
  
  public DLSSampleOptions getSampleoptions() { return this.sampleoptions; }
  
  public void setSampleoptions(DLSSampleOptions paramDLSSampleOptions) { this.sampleoptions = paramDLSSampleOptions; }
  
  public String toString() { return "Sample: " + this.info.name; }
  
  public byte[] getGuid() { return (this.guid == null) ? null : Arrays.copyOf(this.guid, this.guid.length); }
  
  public void setGuid(byte[] paramArrayOfByte) { this.guid = (paramArrayOfByte == null) ? null : Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSSample.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */