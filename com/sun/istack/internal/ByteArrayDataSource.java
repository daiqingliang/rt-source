package com.sun.istack.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public final class ByteArrayDataSource implements DataSource {
  private final String contentType;
  
  private final byte[] buf;
  
  private final int len;
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, String paramString) { this(paramArrayOfByte, paramArrayOfByte.length, paramString); }
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, int paramInt, String paramString) {
    this.buf = paramArrayOfByte;
    this.len = paramInt;
    this.contentType = paramString;
  }
  
  public String getContentType() { return (this.contentType == null) ? "application/octet-stream" : this.contentType; }
  
  public InputStream getInputStream() { return new ByteArrayInputStream(this.buf, 0, this.len); }
  
  public String getName() { return null; }
  
  public OutputStream getOutputStream() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\ByteArrayDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */