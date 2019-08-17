package com.sun.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.text.MessageFormat;
import jdk.Exported;
import sun.security.util.ResourcesMgr;

@Exported
public class NTSid implements Principal, Serializable {
  private static final long serialVersionUID = 4412290580770249885L;
  
  private String sid;
  
  public NTSid(String paramString) {
    if (paramString == null) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources"));
      Object[] arrayOfObject = { "stringSid" };
      throw new NullPointerException(messageFormat.format(arrayOfObject));
    } 
    if (paramString.length() == 0)
      throw new IllegalArgumentException(ResourcesMgr.getString("Invalid.NTSid.value", "sun.security.util.AuthResources")); 
    this.sid = new String(paramString);
  }
  
  public String getName() { return this.sid; }
  
  public String toString() {
    MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("NTSid.name", "sun.security.util.AuthResources"));
    Object[] arrayOfObject = { this.sid };
    return messageFormat.format(arrayOfObject);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof NTSid))
      return false; 
    NTSid nTSid = (NTSid)paramObject;
    return this.sid.equals(nTSid.sid);
  }
  
  public int hashCode() { return this.sid.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\NTSid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */