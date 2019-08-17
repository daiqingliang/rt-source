package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class DataPusher implements Runnable {
  private static final int AUTO_CLOSE_TIME = 5000;
  
  private static final boolean DEBUG = false;
  
  private final SourceDataLine source;
  
  private final AudioFormat format;
  
  private final AudioInputStream ais;
  
  private final byte[] audioData;
  
  private final int audioDataByteLength;
  
  private int pos;
  
  private int newPos = -1;
  
  private boolean looping;
  
  private Thread pushThread = null;
  
  private int wantedState;
  
  private int threadState;
  
  private final int STATE_NONE = 0;
  
  private final int STATE_PLAYING = 1;
  
  private final int STATE_WAITING = 2;
  
  private final int STATE_STOPPING = 3;
  
  private final int STATE_STOPPED = 4;
  
  private final int BUFFER_SIZE = 16384;
  
  public DataPusher(SourceDataLine paramSourceDataLine, AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt) { this(paramSourceDataLine, paramAudioFormat, null, paramArrayOfByte, paramInt); }
  
  public DataPusher(SourceDataLine paramSourceDataLine, AudioInputStream paramAudioInputStream) { this(paramSourceDataLine, paramAudioInputStream.getFormat(), paramAudioInputStream, null, 0); }
  
  private DataPusher(SourceDataLine paramSourceDataLine, AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream, byte[] paramArrayOfByte, int paramInt) {
    this.source = paramSourceDataLine;
    this.format = paramAudioFormat;
    this.ais = paramAudioInputStream;
    this.audioDataByteLength = paramInt;
    this.audioData = (paramArrayOfByte == null) ? null : Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
  }
  
  public void start() { start(false); }
  
  public void start(boolean paramBoolean) {
    try {
      if (this.threadState == 3)
        stop(); 
      this.looping = paramBoolean;
      this.newPos = 0;
      this.wantedState = 1;
      if (!this.source.isOpen())
        this.source.open(this.format); 
      this.source.flush();
      this.source.start();
      if (this.pushThread == null)
        this.pushThread = JSSecurityManager.createThread(this, null, false, -1, true); 
      notifyAll();
    } catch (Exception exception) {}
  }
  
  public void stop() {
    if (this.threadState == 3 || this.threadState == 4 || this.pushThread == null)
      return; 
    this.wantedState = 2;
    if (this.source != null)
      this.source.flush(); 
    notifyAll();
    byte b = 50;
    while (b-- >= 0 && this.threadState == 1) {
      try {
        wait(100L);
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  void close() {
    if (this.source != null)
      this.source.close(); 
  }
  
  public void run() {
    byte[] arrayOfByte = null;
    boolean bool = (this.ais != null) ? 1 : 0;
    if (bool) {
      arrayOfByte = new byte[16384];
    } else {
      arrayOfByte = this.audioData;
    } 
    while (this.wantedState != 3) {
      if (this.wantedState == 2)
        try {
          synchronized (this) {
            this.threadState = 2;
            this.wantedState = 3;
            wait(5000L);
            continue;
          } 
        } catch (InterruptedException interruptedException) {
          continue;
        }  
      if (this.newPos >= 0) {
        this.pos = this.newPos;
        this.newPos = -1;
      } 
      this.threadState = 1;
      int i = 16384;
      if (bool) {
        try {
          this.pos = 0;
          i = this.ais.read(arrayOfByte, 0, arrayOfByte.length);
        } catch (IOException iOException) {
          i = -1;
        } 
      } else {
        if (i > this.audioDataByteLength - this.pos)
          i = this.audioDataByteLength - this.pos; 
        if (i == 0)
          i = -1; 
      } 
      if (i < 0) {
        if (!bool && this.looping) {
          this.pos = 0;
          continue;
        } 
        this.wantedState = 2;
        this.source.drain();
        continue;
      } 
      int j = this.source.write(arrayOfByte, this.pos, i);
      this.pos += j;
    } 
    this.threadState = 3;
    this.source.flush();
    this.source.stop();
    this.source.flush();
    this.source.close();
    this.threadState = 4;
    synchronized (this) {
      this.pushThread = null;
      notifyAll();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DataPusher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */