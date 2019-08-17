package com.sun.security.jgss;

import jdk.Exported;
import sun.misc.HexDumpEncoder;

@Exported
public final class AuthorizationDataEntry {
  private final int type;
  
  private final byte[] data;
  
  public AuthorizationDataEntry(int paramInt, byte[] paramArrayOfByte) {
    this.type = paramInt;
    this.data = (byte[])paramArrayOfByte.clone();
  }
  
  public int getType() { return this.type; }
  
  public byte[] getData() { return (byte[])this.data.clone(); }
  
  public String toString() { return "AuthorizationDataEntry: type=" + this.type + ", data=" + this.data.length + " bytes:\n" + (new HexDumpEncoder()).encodeBuffer(this.data); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\jgss\AuthorizationDataEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */