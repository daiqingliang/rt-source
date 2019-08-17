package com.sun.jndi.ldap;

import java.io.IOException;

public final class PersistentSearchControl extends BasicControl {
  public static final String OID = "2.16.840.1.113730.3.4.3";
  
  public static final int ADD = 1;
  
  public static final int DELETE = 2;
  
  public static final int MODIFY = 4;
  
  public static final int RENAME = 8;
  
  public static final int ANY = 15;
  
  private int changeTypes = 15;
  
  private boolean changesOnly = false;
  
  private boolean returnControls = true;
  
  private static final long serialVersionUID = 6335140491154854116L;
  
  public PersistentSearchControl() throws IOException {
    super("2.16.840.1.113730.3.4.3");
    this.value = setEncodedValue();
  }
  
  public PersistentSearchControl(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) throws IOException {
    super("2.16.840.1.113730.3.4.3", paramBoolean3, null);
    this.changeTypes = paramInt;
    this.changesOnly = paramBoolean1;
    this.returnControls = paramBoolean2;
    this.value = setEncodedValue();
  }
  
  private byte[] setEncodedValue() throws IOException {
    BerEncoder berEncoder = new BerEncoder(32);
    berEncoder.beginSeq(48);
    berEncoder.encodeInt(this.changeTypes);
    berEncoder.encodeBoolean(this.changesOnly);
    berEncoder.encodeBoolean(this.returnControls);
    berEncoder.endSeq();
    return berEncoder.getTrimmedBuf();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\PersistentSearchControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */