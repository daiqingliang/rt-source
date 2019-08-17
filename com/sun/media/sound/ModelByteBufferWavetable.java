package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class ModelByteBufferWavetable implements ModelWavetable {
  private float loopStart = -1.0F;
  
  private float loopLength = -1.0F;
  
  private final ModelByteBuffer buffer;
  
  private ModelByteBuffer buffer8 = null;
  
  private AudioFormat format = null;
  
  private float pitchcorrection = 0.0F;
  
  private float attenuation = 0.0F;
  
  private int loopType = 0;
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer) { this.buffer = paramModelByteBuffer; }
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer, float paramFloat) {
    this.buffer = paramModelByteBuffer;
    this.pitchcorrection = paramFloat;
  }
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer, AudioFormat paramAudioFormat) {
    this.format = paramAudioFormat;
    this.buffer = paramModelByteBuffer;
  }
  
  public ModelByteBufferWavetable(ModelByteBuffer paramModelByteBuffer, AudioFormat paramAudioFormat, float paramFloat) {
    this.format = paramAudioFormat;
    this.buffer = paramModelByteBuffer;
    this.pitchcorrection = paramFloat;
  }
  
  public void set8BitExtensionBuffer(ModelByteBuffer paramModelByteBuffer) { this.buffer8 = paramModelByteBuffer; }
  
  public ModelByteBuffer get8BitExtensionBuffer() { return this.buffer8; }
  
  public ModelByteBuffer getBuffer() { return this.buffer; }
  
  public AudioFormat getFormat() {
    if (this.format == null) {
      if (this.buffer == null)
        return null; 
      InputStream inputStream = this.buffer.getInputStream();
      AudioFormat audioFormat = null;
      try {
        audioFormat = AudioSystem.getAudioFileFormat(inputStream).getFormat();
      } catch (Exception exception) {}
      try {
        inputStream.close();
      } catch (IOException iOException) {}
      return audioFormat;
    } 
    return this.format;
  }
  
  public AudioFloatInputStream openStream() {
    if (this.buffer == null)
      return null; 
    if (this.format == null) {
      InputStream inputStream = this.buffer.getInputStream();
      AudioInputStream audioInputStream = null;
      try {
        audioInputStream = AudioSystem.getAudioInputStream(inputStream);
      } catch (Exception exception) {
        return null;
      } 
      return AudioFloatInputStream.getInputStream(audioInputStream);
    } 
    if (this.buffer.array() == null)
      return AudioFloatInputStream.getInputStream(new AudioInputStream(this.buffer.getInputStream(), this.format, this.buffer.capacity() / this.format.getFrameSize())); 
    if (this.buffer8 != null && (this.format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) || this.format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) {
      Buffer8PlusInputStream buffer8PlusInputStream = new Buffer8PlusInputStream();
      AudioFormat audioFormat = new AudioFormat(this.format.getEncoding(), this.format.getSampleRate(), this.format.getSampleSizeInBits() + 8, this.format.getChannels(), this.format.getFrameSize() + 1 * this.format.getChannels(), this.format.getFrameRate(), this.format.isBigEndian());
      AudioInputStream audioInputStream = new AudioInputStream(buffer8PlusInputStream, audioFormat, this.buffer.capacity() / this.format.getFrameSize());
      return AudioFloatInputStream.getInputStream(audioInputStream);
    } 
    return AudioFloatInputStream.getInputStream(this.format, this.buffer.array(), (int)this.buffer.arrayOffset(), (int)this.buffer.capacity());
  }
  
  public int getChannels() { return getFormat().getChannels(); }
  
  public ModelOscillatorStream open(float paramFloat) { return null; }
  
  public float getAttenuation() { return this.attenuation; }
  
  public void setAttenuation(float paramFloat) { this.attenuation = paramFloat; }
  
  public float getLoopLength() { return this.loopLength; }
  
  public void setLoopLength(float paramFloat) { this.loopLength = paramFloat; }
  
  public float getLoopStart() { return this.loopStart; }
  
  public void setLoopStart(float paramFloat) { this.loopStart = paramFloat; }
  
  public void setLoopType(int paramInt) { this.loopType = paramInt; }
  
  public int getLoopType() { return this.loopType; }
  
  public float getPitchcorrection() { return this.pitchcorrection; }
  
  public void setPitchcorrection(float paramFloat) { this.pitchcorrection = paramFloat; }
  
  private class Buffer8PlusInputStream extends InputStream {
    private final boolean bigendian;
    
    private final int framesize_pc;
    
    int pos = 0;
    
    int pos2 = 0;
    
    int markpos = 0;
    
    int markpos2 = 0;
    
    Buffer8PlusInputStream() {
      this.framesize_pc = this$0.format.getFrameSize() / this$0.format.getChannels();
      this.bigendian = this$0.format.isBigEndian();
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i = available();
      if (i <= 0)
        return -1; 
      if (param1Int2 > i)
        param1Int2 = i; 
      byte[] arrayOfByte1 = ModelByteBufferWavetable.this.buffer.array();
      byte[] arrayOfByte2 = ModelByteBufferWavetable.this.buffer8.array();
      this.pos = (int)(this.pos + ModelByteBufferWavetable.this.buffer.arrayOffset());
      this.pos2 = (int)(this.pos2 + ModelByteBufferWavetable.this.buffer8.arrayOffset());
      if (this.bigendian) {
        int j;
        for (j = 0; j < param1Int2; j += this.framesize_pc + 1) {
          System.arraycopy(arrayOfByte1, this.pos, param1ArrayOfByte, j, this.framesize_pc);
          System.arraycopy(arrayOfByte2, this.pos2, param1ArrayOfByte, j + this.framesize_pc, 1);
          this.pos += this.framesize_pc;
          this.pos2++;
        } 
      } else {
        int j;
        for (j = 0; j < param1Int2; j += this.framesize_pc + 1) {
          System.arraycopy(arrayOfByte2, this.pos2, param1ArrayOfByte, j, 1);
          System.arraycopy(arrayOfByte1, this.pos, param1ArrayOfByte, j + 1, this.framesize_pc);
          this.pos += this.framesize_pc;
          this.pos2++;
        } 
      } 
      this.pos = (int)(this.pos - ModelByteBufferWavetable.this.buffer.arrayOffset());
      this.pos2 = (int)(this.pos2 - ModelByteBufferWavetable.this.buffer8.arrayOffset());
      return param1Int2;
    }
    
    public long skip(long param1Long) throws IOException {
      int i = available();
      if (i <= 0)
        return -1L; 
      if (param1Long > i)
        param1Long = i; 
      this.pos = (int)(this.pos + param1Long / (this.framesize_pc + 1) * this.framesize_pc);
      this.pos2 = (int)(this.pos2 + param1Long / (this.framesize_pc + 1));
      return super.skip(param1Long);
    }
    
    public int read(byte[] param1ArrayOfByte) throws IOException { return read(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public int read() {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte, 0, 1);
      return (i == -1) ? -1 : 0;
    }
    
    public boolean markSupported() { return true; }
    
    public int available() { return (int)ModelByteBufferWavetable.this.buffer.capacity() + (int)ModelByteBufferWavetable.this.buffer8.capacity() - this.pos - this.pos2; }
    
    public void mark(int param1Int) {
      this.markpos = this.pos;
      this.markpos2 = this.pos2;
    }
    
    public void reset() throws IOException {
      this.pos = this.markpos;
      this.pos2 = this.markpos2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelByteBufferWavetable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */