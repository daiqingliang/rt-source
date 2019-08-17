package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public class ActionBasedOperationSignature {
  private final String action;
  
  private final QName payloadQName;
  
  public ActionBasedOperationSignature(@NotNull String paramString, @NotNull QName paramQName) {
    this.action = paramString;
    this.payloadQName = paramQName;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    ActionBasedOperationSignature actionBasedOperationSignature = (ActionBasedOperationSignature)paramObject;
    return !this.action.equals(actionBasedOperationSignature.action) ? false : (!!this.payloadQName.equals(actionBasedOperationSignature.payloadQName));
  }
  
  public int hashCode() {
    null = this.action.hashCode();
    return 31 * null + this.payloadQName.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\ActionBasedOperationSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */