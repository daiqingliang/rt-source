package org.jcp.xml.dsig.internal;

import com.sun.org.apache.xml.internal.security.utils.UnsyncByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigesterOutputStream extends OutputStream {
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal");
  
  private final boolean buffer;
  
  private UnsyncByteArrayOutputStream bos;
  
  private final MessageDigest md;
  
  public DigesterOutputStream(MessageDigest paramMessageDigest) { this(paramMessageDigest, false); }
  
  public DigesterOutputStream(MessageDigest paramMessageDigest, boolean paramBoolean) {
    this.md = paramMessageDigest;
    this.buffer = paramBoolean;
    if (paramBoolean)
      this.bos = new UnsyncByteArrayOutputStream(); 
  }
  
  public void write(int paramInt) {
    if (this.buffer)
      this.bos.write(paramInt); 
    this.md.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (this.buffer)
      this.bos.write(paramArrayOfByte, paramInt1, paramInt2); 
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Pre-digested input:");
      StringBuilder stringBuilder = new StringBuilder(paramInt2);
      for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
        stringBuilder.append((char)paramArrayOfByte[i]); 
      log.log(Level.FINE, stringBuilder.toString());
    } 
    this.md.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public byte[] getDigestValue() { return this.md.digest(); }
  
  public InputStream getInputStream() { return this.buffer ? new ByteArrayInputStream(this.bos.toByteArray()) : null; }
  
  public void close() throws IOException {
    if (this.buffer)
      this.bos.close(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\DigesterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */