package com.sun.security.auth;

import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTNumericCredential {
  private long impersonationToken;
  
  public NTNumericCredential(long paramLong) { this.impersonationToken = paramLong; }
  
  public long getToken() { return this.impersonationToken; }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("NTNumericCredential.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { Long.toString(this.impersonationToken) };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof NTNumericCredential))
      return false; 
    NTNumericCredential nTNumericCredential = (NTNumericCredential)paramObject;
    return (this.impersonationToken == nTNumericCredential.getToken());
  }
  
  public int hashCode() { return (int)this.impersonationToken; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\NTNumericCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */