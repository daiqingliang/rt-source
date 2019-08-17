package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;

public final class SoftMixingMainMixer {
  public static final int CHANNEL_LEFT = 0;
  
  public static final int CHANNEL_RIGHT = 1;
  
  public static final int CHANNEL_EFFECT1 = 2;
  
  public static final int CHANNEL_EFFECT2 = 3;
  
  public static final int CHANNEL_EFFECT3 = 4;
  
  public static final int CHANNEL_EFFECT4 = 5;
  
  public static final int CHANNEL_LEFT_DRY = 10;
  
  public static final int CHANNEL_RIGHT_DRY = 11;
  
  public static final int CHANNEL_SCRATCH1 = 12;
  
  public static final int CHANNEL_SCRATCH2 = 13;
  
  public static final int CHANNEL_CHANNELMIXER_LEFT = 14;
  
  public static final int CHANNEL_CHANNELMIXER_RIGHT = 15;
  
  private final SoftMixingMixer mixer;
  
  private final AudioInputStream ais;
  
  private final SoftAudioBuffer[] buffers;
  
  private final SoftAudioProcessor reverb;
  
  private final SoftAudioProcessor chorus;
  
  private final SoftAudioProcessor agc;
  
  private final int nrofchannels;
  
  private final Object control_mutex;
  
  private final List<SoftMixingDataLine> openLinesList = new ArrayList();
  
  private SoftMixingDataLine[] openLines = new SoftMixingDataLine[0];
  
  public AudioInputStream getInputStream() { return this.ais; }
  
  void processAudioBuffers() {
    SoftMixingDataLine[] arrayOfSoftMixingDataLine;
    for (byte b1 = 0; b1 < this.buffers.length; b1++)
      this.buffers[b1].clear(); 
    synchronized (this.control_mutex) {
      arrayOfSoftMixingDataLine = this.openLines;
      for (byte b = 0; b < arrayOfSoftMixingDataLine.length; b++)
        arrayOfSoftMixingDataLine[b].processControlLogic(); 
      this.chorus.processControlLogic();
      this.reverb.processControlLogic();
      this.agc.processControlLogic();
    } 
    for (byte b2 = 0; b2 < arrayOfSoftMixingDataLine.length; b2++)
      arrayOfSoftMixingDataLine[b2].processAudioLogic(this.buffers); 
    this.chorus.processAudio();
    this.reverb.processAudio();
    this.agc.processAudio();
  }
  
  public SoftMixingMainMixer(SoftMixingMixer paramSoftMixingMixer) {
    this.mixer = paramSoftMixingMixer;
    this.nrofchannels = paramSoftMixingMixer.getFormat().getChannels();
    int i = (int)(paramSoftMixingMixer.getFormat().getSampleRate() / paramSoftMixingMixer.getControlRate());
    this.control_mutex = paramSoftMixingMixer.control_mutex;
    this.buffers = new SoftAudioBuffer[16];
    for (byte b = 0; b < this.buffers.length; b++)
      this.buffers[b] = new SoftAudioBuffer(i, paramSoftMixingMixer.getFormat()); 
    this.reverb = new SoftReverb();
    this.chorus = new SoftChorus();
    this.agc = new SoftLimiter();
    float f1 = paramSoftMixingMixer.getFormat().getSampleRate();
    float f2 = paramSoftMixingMixer.getControlRate();
    this.reverb.init(f1, f2);
    this.chorus.init(f1, f2);
    this.agc.init(f1, f2);
    this.reverb.setMixMode(true);
    this.chorus.setMixMode(true);
    this.agc.setMixMode(false);
    this.chorus.setInput(0, this.buffers[3]);
    this.chorus.setOutput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.chorus.setOutput(1, this.buffers[1]); 
    this.chorus.setOutput(2, this.buffers[2]);
    this.reverb.setInput(0, this.buffers[2]);
    this.reverb.setOutput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.reverb.setOutput(1, this.buffers[1]); 
    this.agc.setInput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.agc.setInput(1, this.buffers[1]); 
    this.agc.setOutput(0, this.buffers[0]);
    if (this.nrofchannels != 1)
      this.agc.setOutput(1, this.buffers[1]); 
    InputStream inputStream = new InputStream() {
        private final SoftAudioBuffer[] buffers = SoftMixingMainMixer.this.buffers;
        
        private final int nrofchannels = SoftMixingMainMixer.this.mixer.getFormat().getChannels();
        
        private final int buffersize = this.buffers[0].getSize();
        
        private final byte[] bbuffer = new byte[this.buffersize * SoftMixingMainMixer.this.mixer.getFormat().getSampleSizeInBits() / 8 * this.nrofchannels];
        
        private int bbuffer_pos = 0;
        
        private final byte[] single = new byte[1];
        
        public void fillBuffer() {
          SoftMixingMainMixer.this.processAudioBuffers();
          for (byte b = 0; b < this.nrofchannels; b++)
            this.buffers[b].get(this.bbuffer, b); 
          this.bbuffer_pos = 0;
        }
        
        public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
          int i = this.bbuffer.length;
          int j = param1Int1 + param1Int2;
          byte[] arrayOfByte = this.bbuffer;
          while (param1Int1 < j) {
            if (available() == 0) {
              fillBuffer();
              continue;
            } 
            int k = this.bbuffer_pos;
            while (param1Int1 < j && k < i)
              param1ArrayOfByte[param1Int1++] = arrayOfByte[k++]; 
            this.bbuffer_pos = k;
          } 
          return param1Int2;
        }
        
        public int read() throws IOException {
          int i = read(this.single);
          return (i == -1) ? -1 : (this.single[0] & 0xFF);
        }
        
        public int available() throws IOException { return this.bbuffer.length - this.bbuffer_pos; }
        
        public void close() { SoftMixingMainMixer.this.mixer.close(); }
      };
    this.ais = new AudioInputStream(inputStream, paramSoftMixingMixer.getFormat(), -1L);
  }
  
  public void openLine(SoftMixingDataLine paramSoftMixingDataLine) {
    synchronized (this.control_mutex) {
      this.openLinesList.add(paramSoftMixingDataLine);
      this.openLines = (SoftMixingDataLine[])this.openLinesList.toArray(new SoftMixingDataLine[this.openLinesList.size()]);
    } 
  }
  
  public void closeLine(SoftMixingDataLine paramSoftMixingDataLine) {
    synchronized (this.control_mutex) {
      this.openLinesList.remove(paramSoftMixingDataLine);
      this.openLines = (SoftMixingDataLine[])this.openLinesList.toArray(new SoftMixingDataLine[this.openLinesList.size()]);
      if (this.openLines.length == 0 && this.mixer.implicitOpen)
        this.mixer.close(); 
    } 
  }
  
  public SoftMixingDataLine[] getOpenLines() {
    synchronized (this.control_mutex) {
      return this.openLines;
    } 
  }
  
  public void close() {
    SoftMixingDataLine[] arrayOfSoftMixingDataLine = this.openLines;
    for (byte b = 0; b < arrayOfSoftMixingDataLine.length; b++)
      arrayOfSoftMixingDataLine[b].close(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftMixingMainMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */