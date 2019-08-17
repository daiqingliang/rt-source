package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import javax.crypto.Mac;

public class MacOutputStream extends ByteArrayOutputStream {
  private final Mac mac;
  
  public MacOutputStream(Mac paramMac) { this.mac = paramMac; }
  
  public void write(int paramInt) {
    super.write(paramInt);
    this.mac.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    super.write(paramArrayOfByte, paramInt1, paramInt2);
    this.mac.update(paramArrayOfByte, paramInt1, paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\MacOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */