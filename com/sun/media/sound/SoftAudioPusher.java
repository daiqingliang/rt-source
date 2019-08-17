package com.sun.media.sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class SoftAudioPusher implements Runnable {
  private SourceDataLine sourceDataLine = null;
  
  private Thread audiothread;
  
  private final AudioInputStream ais;
  
  private final byte[] buffer;
  
  public SoftAudioPusher(SourceDataLine paramSourceDataLine, AudioInputStream paramAudioInputStream, int paramInt) {
    this.ais = paramAudioInputStream;
    this.buffer = new byte[paramInt];
    this.sourceDataLine = paramSourceDataLine;
  }
  
  public void start() {
    if (this.active)
      return; 
    this.active = true;
    this.audiothread = new Thread(this);
    this.audiothread.setDaemon(true);
    this.audiothread.setPriority(10);
    this.audiothread.start();
  }
  
  public void stop() {
    if (!this.active)
      return; 
    this.active = false;
    try {
      this.audiothread.join();
    } catch (InterruptedException interruptedException) {}
  }
  
  public void run() {
    byte[] arrayOfByte = this.buffer;
    AudioInputStream audioInputStream = this.ais;
    SourceDataLine sourceDataLine1 = this.sourceDataLine;
    try {
      while (this.active) {
        int i = audioInputStream.read(arrayOfByte);
        if (i < 0)
          break; 
        sourceDataLine1.write(arrayOfByte, 0, i);
      } 
    } catch (IOException iOException) {
      this.active = false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftAudioPusher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */