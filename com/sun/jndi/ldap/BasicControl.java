package com.sun.jndi.ldap;

import javax.naming.ldap.Control;

public class BasicControl implements Control {
  protected String id;
  
  protected boolean criticality = false;
  
  protected byte[] value = null;
  
  private static final long serialVersionUID = -5914033725246428413L;
  
  public BasicControl(String paramString) { this.id = paramString; }
  
  public BasicControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte) {
    this.id = paramString;
    this.criticality = paramBoolean;
    if (paramArrayOfByte != null)
      this.value = (byte[])paramArrayOfByte.clone(); 
  }
  
  public String getID() { return this.id; }
  
  public boolean isCritical() { return this.criticality; }
  
  public byte[] getEncodedValue() { return (this.value == null) ? null : (byte[])this.value.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\BasicControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */