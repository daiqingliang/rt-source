package com.sun.media.sound;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SoftJitterCorrector extends AudioInputStream {
  public SoftJitterCorrector(AudioInputStream paramAudioInputStream, int paramInt1, int paramInt2) { super(new JitterStream(paramAudioInputStream, paramInt1, paramInt2), paramAudioInputStream.getFormat(), paramAudioInputStream.getFrameLength()); }
  
  private static class JitterStream extends InputStream {
    static int MAX_BUFFER_SIZE = 1048576;
    
    boolean active = true;
    
    Thread thread;
    
    AudioInputStream stream;
    
    int writepos = 0;
    
    int readpos = 0;
    
    byte[][] buffers;
    
    private final Object buffers_mutex = new Object();
    
    int w_count = 1000;
    
    int w_min_tol = 2;
    
    int w_max_tol = 10;
    
    int w = 0;
    
    int w_min = -1;
    
    int bbuffer_pos = 0;
    
    int bbuffer_max = 0;
    
    byte[] bbuffer = null;
    
    public byte[] nextReadBuffer() {
      synchronized (this.buffers_mutex) {
        if (this.writepos > this.readpos) {
          int i = this.writepos - this.readpos;
          if (i < this.w_min)
            this.w_min = i; 
          int j = this.readpos;
          this.readpos++;
          return this.buffers[j % this.buffers.length];
        } 
        this.w_min = -1;
        this.w = this.w_count - 1;
      } 
      while (true) {
        try {
          Thread.sleep(1L);
        } catch (InterruptedException interruptedException) {
          return null;
        } 
        synchronized (this.buffers_mutex) {
          if (this.writepos > this.readpos) {
            this.w = 0;
            this.w_min = -1;
            this.w = this.w_count - 1;
            int i = this.readpos;
            this.readpos++;
            return this.buffers[i % this.buffers.length];
          } 
        } 
      } 
    }
    
    public byte[] nextWriteBuffer() {
      synchronized (this.buffers_mutex) {
        return this.buffers[this.writepos % this.buffers.length];
      } 
    }
    
    public void commit() {
      synchronized (this.buffers_mutex) {
        this.writepos++;
        if (this.writepos - this.readpos > this.buffers.length) {
          int i = this.writepos - this.readpos + 10;
          i = Math.max(this.buffers.length * 2, i);
          this.buffers = new byte[i][this.buffers[0].length];
        } 
      } 
    }
    
    JitterStream(AudioInputStream param1AudioInputStream, int param1Int1, int param1Int2) {
      this.w_count = 10 * param1Int1 / param1Int2;
      if (this.w_count < 100)
        this.w_count = 100; 
      this.buffers = new byte[param1Int1 / param1Int2 + 10][param1Int2];
      this.bbuffer_max = MAX_BUFFER_SIZE / param1Int2;
      this.stream = param1AudioInputStream;
      Runnable runnable = new Runnable() {
          public void run() {
            AudioFormat audioFormat = SoftJitterCorrector.JitterStream.this.stream.getFormat();
            int i = SoftJitterCorrector.JitterStream.this.buffers[0].length;
            int j = i / audioFormat.getFrameSize();
            long l1 = (long)(j * 1.0E9D / audioFormat.getSampleRate());
            long l2 = System.nanoTime();
            long l3 = l2 + l1;
            int k = 0;
            while (true) {
              int m;
              synchronized (SoftJitterCorrector.JitterStream.this) {
                if (!SoftJitterCorrector.JitterStream.this.active)
                  break; 
              } 
              synchronized (SoftJitterCorrector.JitterStream.this.buffers) {
                m = SoftJitterCorrector.JitterStream.this.writepos - SoftJitterCorrector.JitterStream.this.readpos;
                if (!k) {
                  SoftJitterCorrector.JitterStream.this.w++;
                  if (SoftJitterCorrector.JitterStream.this.w_min != Integer.MAX_VALUE && SoftJitterCorrector.JitterStream.this.w == SoftJitterCorrector.JitterStream.this.w_count) {
                    k = 0;
                    if (SoftJitterCorrector.JitterStream.this.w_min < SoftJitterCorrector.JitterStream.this.w_min_tol)
                      k = (SoftJitterCorrector.JitterStream.this.w_min_tol + SoftJitterCorrector.JitterStream.this.w_max_tol) / 2 - SoftJitterCorrector.JitterStream.this.w_min; 
                    if (SoftJitterCorrector.JitterStream.this.w_min > SoftJitterCorrector.JitterStream.this.w_max_tol)
                      k = (SoftJitterCorrector.JitterStream.this.w_min_tol + SoftJitterCorrector.JitterStream.this.w_max_tol) / 2 - SoftJitterCorrector.JitterStream.this.w_min; 
                    SoftJitterCorrector.JitterStream.this.w = 0;
                    SoftJitterCorrector.JitterStream.this.w_min = Integer.MAX_VALUE;
                  } 
                } 
              } 
              while (m > SoftJitterCorrector.JitterStream.this.bbuffer_max) {
                synchronized (SoftJitterCorrector.JitterStream.this.buffers) {
                  m = SoftJitterCorrector.JitterStream.this.writepos - SoftJitterCorrector.JitterStream.this.readpos;
                } 
                synchronized (SoftJitterCorrector.JitterStream.this) {
                  if (!SoftJitterCorrector.JitterStream.this.active)
                    break; 
                } 
                try {
                  Thread.sleep(1L);
                } catch (InterruptedException interruptedException) {}
              } 
              if (k < 0) {
                k++;
              } else {
                byte[] arrayOfByte = SoftJitterCorrector.JitterStream.this.nextWriteBuffer();
                try {
                  int n;
                  for (n = 0; n != arrayOfByte.length; n += i1) {
                    int i1 = SoftJitterCorrector.JitterStream.this.stream.read(arrayOfByte, n, arrayOfByte.length - n);
                    if (i1 < 0)
                      throw new EOFException(); 
                    if (i1 == 0)
                      Thread.yield(); 
                  } 
                } catch (IOException iOException) {}
                SoftJitterCorrector.JitterStream.this.commit();
              } 
              if (k > 0) {
                k--;
                l3 = System.nanoTime() + l1;
                continue;
              } 
              long l = l3 - System.nanoTime();
              if (l > 0L)
                try {
                  Thread.sleep(l / 1000000L);
                } catch (InterruptedException interruptedException) {} 
              l3 += l1;
            } 
          }
        };
      this.thread = new Thread(runnable);
      this.thread.setDaemon(true);
      this.thread.setPriority(10);
      this.thread.start();
    }
    
    public void close() {
      synchronized (this) {
        this.active = false;
      } 
      try {
        this.thread.join();
      } catch (InterruptedException interruptedException) {}
      this.stream.close();
    }
    
    public int read() throws IOException {
      byte[] arrayOfByte = new byte[1];
      return (read(arrayOfByte) == -1) ? -1 : (arrayOfByte[0] & 0xFF);
    }
    
    public void fillBuffer() {
      this.bbuffer = nextReadBuffer();
      this.bbuffer_pos = 0;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      if (this.bbuffer == null)
        fillBuffer(); 
      int i = this.bbuffer.length;
      int j = param1Int1 + param1Int2;
      while (param1Int1 < j) {
        if (available() == 0) {
          fillBuffer();
          continue;
        } 
        byte[] arrayOfByte = this.bbuffer;
        int k = this.bbuffer_pos;
        while (param1Int1 < j && k < i)
          param1ArrayOfByte[param1Int1++] = arrayOfByte[k++]; 
        this.bbuffer_pos = k;
      } 
      return param1Int2;
    }
    
    public int available() throws IOException { return this.bbuffer.length - this.bbuffer_pos; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftJitterCorrector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */