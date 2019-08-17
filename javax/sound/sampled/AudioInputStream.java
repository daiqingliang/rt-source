package javax.sound.sampled;

import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream extends InputStream {
  private InputStream stream;
  
  protected AudioFormat format;
  
  protected long frameLength;
  
  protected int frameSize;
  
  protected long framePos;
  
  private long markpos;
  
  private byte[] pushBackBuffer = null;
  
  private int pushBackLen = 0;
  
  private byte[] markPushBackBuffer = null;
  
  private int markPushBackLen = 0;
  
  public AudioInputStream(InputStream paramInputStream, AudioFormat paramAudioFormat, long paramLong) {
    this.format = paramAudioFormat;
    this.frameLength = paramLong;
    this.frameSize = paramAudioFormat.getFrameSize();
    if (this.frameSize == -1 || this.frameSize <= 0)
      this.frameSize = 1; 
    this.stream = paramInputStream;
    this.framePos = 0L;
    this.markpos = 0L;
  }
  
  public AudioInputStream(TargetDataLine paramTargetDataLine) {
    TargetDataLineInputStream targetDataLineInputStream = new TargetDataLineInputStream(paramTargetDataLine);
    this.format = paramTargetDataLine.getFormat();
    this.frameLength = -1L;
    this.frameSize = this.format.getFrameSize();
    if (this.frameSize == -1 || this.frameSize <= 0)
      this.frameSize = 1; 
    this.stream = targetDataLineInputStream;
    this.framePos = 0L;
    this.markpos = 0L;
  }
  
  public AudioFormat getFormat() { return this.format; }
  
  public long getFrameLength() { return this.frameLength; }
  
  public int read() throws IOException {
    if (this.frameSize != 1)
      throw new IOException("cannot read a single byte if frame size > 1"); 
    byte[] arrayOfByte = new byte[1];
    int i = read(arrayOfByte);
    return (i <= 0) ? -1 : (arrayOfByte[0] & 0xFF);
  }
  
  public int read(byte[] paramArrayOfByte) throws IOException { return read(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 % this.frameSize != 0) {
      paramInt2 -= paramInt2 % this.frameSize;
      if (paramInt2 == 0)
        return 0; 
    } 
    if (this.frameLength != -1L) {
      if (this.framePos >= this.frameLength)
        return -1; 
      if ((paramInt2 / this.frameSize) > this.frameLength - this.framePos)
        paramInt2 = (int)(this.frameLength - this.framePos) * this.frameSize; 
    } 
    int i = 0;
    int j = paramInt1;
    if (this.pushBackLen > 0 && paramInt2 >= this.pushBackLen) {
      System.arraycopy(this.pushBackBuffer, 0, paramArrayOfByte, paramInt1, this.pushBackLen);
      j += this.pushBackLen;
      paramInt2 -= this.pushBackLen;
      i += this.pushBackLen;
      this.pushBackLen = 0;
    } 
    int k = this.stream.read(paramArrayOfByte, j, paramInt2);
    if (k == -1)
      return -1; 
    if (k > 0)
      i += k; 
    if (i > 0) {
      this.pushBackLen = i % this.frameSize;
      if (this.pushBackLen > 0) {
        if (this.pushBackBuffer == null)
          this.pushBackBuffer = new byte[this.frameSize]; 
        System.arraycopy(paramArrayOfByte, paramInt1 + i - this.pushBackLen, this.pushBackBuffer, 0, this.pushBackLen);
        i -= this.pushBackLen;
      } 
      this.framePos += (i / this.frameSize);
    } 
    return i;
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong % this.frameSize != 0L)
      paramLong -= paramLong % this.frameSize; 
    if (this.frameLength != -1L && paramLong / this.frameSize > this.frameLength - this.framePos)
      paramLong = (this.frameLength - this.framePos) * this.frameSize; 
    long l = this.stream.skip(paramLong);
    if (l % this.frameSize != 0L)
      throw new IOException("Could not skip an integer number of frames."); 
    if (l >= 0L)
      this.framePos += l / this.frameSize; 
    return l;
  }
  
  public int available() throws IOException {
    int i = this.stream.available();
    return (this.frameLength != -1L && (i / this.frameSize) > this.frameLength - this.framePos) ? ((int)(this.frameLength - this.framePos) * this.frameSize) : i;
  }
  
  public void close() throws IOException { this.stream.close(); }
  
  public void mark(int paramInt) {
    this.stream.mark(paramInt);
    if (markSupported()) {
      this.markpos = this.framePos;
      this.markPushBackLen = this.pushBackLen;
      if (this.markPushBackLen > 0) {
        if (this.markPushBackBuffer == null)
          this.markPushBackBuffer = new byte[this.frameSize]; 
        System.arraycopy(this.pushBackBuffer, 0, this.markPushBackBuffer, 0, this.markPushBackLen);
      } 
    } 
  }
  
  public void reset() throws IOException {
    this.stream.reset();
    this.framePos = this.markpos;
    this.pushBackLen = this.markPushBackLen;
    if (this.pushBackLen > 0) {
      if (this.pushBackBuffer == null)
        this.pushBackBuffer = new byte[this.frameSize - 1]; 
      System.arraycopy(this.markPushBackBuffer, 0, this.pushBackBuffer, 0, this.pushBackLen);
    } 
  }
  
  public boolean markSupported() { return this.stream.markSupported(); }
  
  private class TargetDataLineInputStream extends InputStream {
    TargetDataLine line;
    
    TargetDataLineInputStream(TargetDataLine param1TargetDataLine) { this.line = param1TargetDataLine; }
    
    public int available() throws IOException { return this.line.available(); }
    
    public void close() throws IOException {
      if (this.line.isActive()) {
        this.line.flush();
        this.line.stop();
      } 
      this.line.close();
    }
    
    public int read() throws IOException {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte, 0, 1);
      if (i == -1)
        return -1; 
      i = arrayOfByte[0];
      if (this.line.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
        i += 128; 
      return i;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      try {
        return this.line.read(param1ArrayOfByte, param1Int1, param1Int2);
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new IOException(illegalArgumentException.getMessage());
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\sampled\AudioInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */