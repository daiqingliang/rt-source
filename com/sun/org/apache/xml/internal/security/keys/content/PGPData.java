package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Element;

public class PGPData extends SignatureElementProxy implements KeyInfoContent {
  public PGPData(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public String getBaseLocalName() { return "PGPData"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\PGPData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */