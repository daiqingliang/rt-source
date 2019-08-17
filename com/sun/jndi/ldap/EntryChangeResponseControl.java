package com.sun.jndi.ldap;

import java.io.IOException;

public final class EntryChangeResponseControl extends BasicControl {
  public static final String OID = "2.16.840.1.113730.3.4.7";
  
  public static final int ADD = 1;
  
  public static final int DELETE = 2;
  
  public static final int MODIFY = 4;
  
  public static final int RENAME = 8;
  
  private int changeType;
  
  private String previousDN = null;
  
  private long changeNumber = -1L;
  
  private static final long serialVersionUID = -2087354136750180511L;
  
  public EntryChangeResponseControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte) throws IOException {
    super(paramString, paramBoolean, paramArrayOfByte);
    if (paramArrayOfByte != null && paramArrayOfByte.length > 0) {
      BerDecoder berDecoder = new BerDecoder(paramArrayOfByte, 0, paramArrayOfByte.length);
      berDecoder.parseSeq(null);
      this.changeType = berDecoder.parseEnumeration();
      if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 4)
        this.previousDN = berDecoder.parseString(true); 
      if (berDecoder.bytesLeft() > 0 && berDecoder.peekByte() == 2)
        this.changeNumber = berDecoder.parseInt(); 
    } 
  }
  
  public int getChangeType() { return this.changeType; }
  
  public String getPreviousDN() { return this.previousDN; }
  
  public long getChangeNumber() { return this.changeNumber; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\EntryChangeResponseControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */