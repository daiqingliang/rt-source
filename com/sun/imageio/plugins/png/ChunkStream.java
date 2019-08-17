package com.sun.imageio.plugins.png;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class ChunkStream extends ImageOutputStreamImpl {
  private ImageOutputStream stream;
  
  private long startPos;
  
  private CRC crc = new CRC();
  
  public ChunkStream(int paramInt, ImageOutputStream paramImageOutputStream) throws IOException {
    this.stream = paramImageOutputStream;
    this.startPos = paramImageOutputStream.getStreamPosition();
    paramImageOutputStream.writeInt(-1);
    writeInt(paramInt);
  }
  
  public int read() throws IOException { throw new RuntimeException("Method not available"); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { throw new RuntimeException("Method not available"); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
    this.stream.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void write(int paramInt) throws IOException {
    this.crc.update(paramInt);
    this.stream.write(paramInt);
  }
  
  public void finish() throws IOException {
    this.stream.writeInt(this.crc.getValue());
    long l = this.stream.getStreamPosition();
    this.stream.seek(this.startPos);
    this.stream.writeInt((int)(l - this.startPos) - 12);
    this.stream.seek(l);
    this.stream.flushBefore(l);
  }
  
  protected void finalize() throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\ChunkStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */