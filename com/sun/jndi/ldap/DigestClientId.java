package com.sun.jndi.ldap;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Hashtable;
import javax.naming.ldap.Control;

class DigestClientId extends SimpleClientId {
  private static final String[] SASL_PROPS = { 
      "java.naming.security.sasl.authorizationId", "java.naming.security.sasl.realm", "javax.security.sasl.qop", "javax.security.sasl.strength", "javax.security.sasl.reuse", "javax.security.sasl.server.authentication", "javax.security.sasl.maxbuffer", "javax.security.sasl.policy.noplaintext", "javax.security.sasl.policy.noactive", "javax.security.sasl.policy.nodictionary", 
      "javax.security.sasl.policy.noanonymous", "javax.security.sasl.policy.forward", "javax.security.sasl.policy.credentials" };
  
  private final String[] propvals;
  
  private final int myHash;
  
  DigestClientId(int paramInt1, String paramString1, int paramInt2, String paramString2, Control[] paramArrayOfControl, OutputStream paramOutputStream, String paramString3, String paramString4, Object paramObject, Hashtable<?, ?> paramHashtable) {
    super(paramInt1, paramString1, paramInt2, paramString2, paramArrayOfControl, paramOutputStream, paramString3, paramString4, paramObject);
    if (paramHashtable == null) {
      this.propvals = null;
    } else {
      this.propvals = new String[SASL_PROPS.length];
      for (byte b = 0; b < SASL_PROPS.length; b++)
        this.propvals[b] = (String)paramHashtable.get(SASL_PROPS[b]); 
    } 
    this.myHash = super.hashCode() ^ Arrays.hashCode(this.propvals);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DigestClientId))
      return false; 
    DigestClientId digestClientId = (DigestClientId)paramObject;
    return (this.myHash == digestClientId.myHash && super.equals(paramObject) && Arrays.equals(this.propvals, digestClientId.propvals));
  }
  
  public int hashCode() { return this.myHash; }
  
  public String toString() {
    if (this.propvals != null) {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.propvals.length; b++) {
        stringBuffer.append(':');
        if (this.propvals[b] != null)
          stringBuffer.append(this.propvals[b]); 
      } 
      return super.toString() + stringBuffer.toString();
    } 
    return super.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\DigestClientId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */