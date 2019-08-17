package com.sun.jndi.ldap;

import java.util.Vector;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.UnsolicitedNotification;

final class UnsolicitedResponseImpl implements UnsolicitedNotification {
  private String oid;
  
  private String[] referrals;
  
  private byte[] extensionValue;
  
  private NamingException exception;
  
  private Control[] controls;
  
  private static final long serialVersionUID = 5913778898401784775L;
  
  UnsolicitedResponseImpl(String paramString1, byte[] paramArrayOfByte, Vector<Vector<String>> paramVector, int paramInt, String paramString2, String paramString3, Control[] paramArrayOfControl) {
    this.oid = paramString1;
    this.extensionValue = paramArrayOfByte;
    if (paramVector != null && paramVector.size() > 0) {
      int i = paramVector.size();
      this.referrals = new String[i];
      for (byte b = 0; b < i; b++)
        this.referrals[b] = (String)((Vector)paramVector.elementAt(b)).elementAt(0); 
    } 
    this.exception = LdapCtx.mapErrorCode(paramInt, paramString2);
    this.controls = paramArrayOfControl;
  }
  
  public String getID() { return this.oid; }
  
  public byte[] getEncodedValue() { return this.extensionValue; }
  
  public String[] getReferrals() { return this.referrals; }
  
  public NamingException getException() { return this.exception; }
  
  public Control[] getControls() throws NamingException { return this.controls; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\UnsolicitedResponseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */