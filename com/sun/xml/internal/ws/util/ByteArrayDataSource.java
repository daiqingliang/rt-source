package com.sun.xml.internal.ws.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public final class ByteArrayDataSource implements DataSource {
  private final String contentType;
  
  private final byte[] buf;
  
  private final int start;
  
  private final int len;
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, String paramString) { this(paramArrayOfByte, 0, paramArrayOfByte.length, paramString); }
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, int paramInt, String paramString) { this(paramArrayOfByte, 0, paramInt, paramString); }
  
  public ByteArrayDataSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString) {
    this.buf = paramArrayOfByte;
    this.start = paramInt1;
    this.len = paramInt2;
    this.contentType = paramString;
  }
  
  public String getContentType() { return (this.contentType == null) ? "application/octet-stream" : this.contentType; }
  
  public InputStream getInputStream() { return new ByteArrayInputStream(this.buf, this.start, this.len); }
  
  public String getName() { return null; }
  
  public OutputStream getOutputStream() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\ByteArrayDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */