package com.sun.imageio.plugins.png;

import java.io.IOException;
import java.util.zip.Deflater;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class IDATOutputStream extends ImageOutputStreamImpl {
  private static byte[] chunkType = { 73, 68, 65, 84 };
  
  private ImageOutputStream stream;
  
  private int chunkLength;
  
  private long startPos;
  
  private CRC crc = new CRC();
  
  Deflater def = new Deflater(9);
  
  byte[] buf = new byte[512];
  
  private int bytesRemaining;
  
  public IDATOutputStream(ImageOutputStream paramImageOutputStream, int paramInt) throws IOException {
    this.stream = paramImageOutputStream;
    this.chunkLength = paramInt;
    startChunk();
  }
  
  private void startChunk() throws IOException {
    this.crc.reset();
    this.startPos = this.stream.getStreamPosition();
    this.stream.writeInt(-1);
    this.crc.update(chunkType, 0, 4);
    this.stream.write(chunkType, 0, 4);
    this.bytesRemaining = this.chunkLength;
  }
  
  private void finishChunk() throws IOException {
    this.stream.writeInt(this.crc.getValue());
    long l = this.stream.getStreamPosition();
    this.stream.seek(this.startPos);
    this.stream.writeInt((int)(l - this.startPos) - 12);
    this.stream.seek(l);
    this.stream.flushBefore(l);
  }
  
  public int read() throws IOException { throw new RuntimeException("Method not available"); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { throw new RuntimeException("Method not available"); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 == 0)
      return; 
    if (!this.def.finished()) {
      this.def.setInput(paramArrayOfByte, paramInt1, paramInt2);
      while (!this.def.needsInput())
        deflate(); 
    } 
  }
  
  public void deflate() throws IOException {
    int i = this.def.deflate(this.buf, 0, this.buf.length);
    int j = 0;
    while (i > 0) {
      if (this.bytesRemaining == 0) {
        finishChunk();
        startChunk();
      } 
      int k = Math.min(i, this.bytesRemaining);
      this.crc.update(this.buf, j, k);
      this.stream.write(this.buf, j, k);
      j += k;
      i -= k;
      this.bytesRemaining -= k;
    } 
  }
  
  public void write(int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = (byte)paramInt;
    write(arrayOfByte, 0, 1);
  }
  
  public void finish() throws IOException {
    try {
      if (!this.def.finished()) {
        this.def.finish();
        while (!this.def.finished())
          deflate(); 
      } 
      finishChunk();
    } finally {
      this.def.end();
    } 
  }
  
  protected void finalize() throws IOException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\IDATOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */