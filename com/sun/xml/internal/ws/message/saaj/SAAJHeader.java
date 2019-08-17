package com.sun.xml.internal.ws.message.saaj;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.message.DOMHeader;
import javax.xml.soap.SOAPHeaderElement;

public final class SAAJHeader extends DOMHeader<SOAPHeaderElement> {
  public SAAJHeader(SOAPHeaderElement paramSOAPHeaderElement) { super(paramSOAPHeaderElement); }
  
  @NotNull
  public String getRole(@NotNull SOAPVersion paramSOAPVersion) {
    String str = getAttribute(paramSOAPVersion.nsUri, paramSOAPVersion.roleAttributeName);
    if (str == null || str.equals(""))
      str = paramSOAPVersion.implicitRole; 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\saaj\SAAJHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */