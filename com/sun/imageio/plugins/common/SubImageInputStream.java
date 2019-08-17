package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

public final class SubImageInputStream extends ImageInputStreamImpl {
  ImageInputStream stream;
  
  long startingPos;
  
  int startingLength;
  
  int length;
  
  public SubImageInputStream(ImageInputStream paramImageInputStream, int paramInt) throws IOException {
    this.stream = paramImageInputStream;
    this.startingPos = paramImageInputStream.getStreamPosition();
    this.startingLength = this.length = paramInt;
  }
  
  public int read() throws IOException {
    if (this.length == 0)
      return -1; 
    this.length--;
    return this.stream.read();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.length == 0)
      return -1; 
    paramInt2 = Math.min(paramInt2, this.length);
    int i = this.stream.read(paramArrayOfByte, paramInt1, paramInt2);
    this.length -= i;
    return i;
  }
  
  public long length() { return this.startingLength; }
  
  public void seek(long paramLong) throws IOException {
    this.stream.seek(paramLong - this.startingPos);
    this.streamPos = paramLong;
  }
  
  protected void finalize() throws Throwable {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\SubImageInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */