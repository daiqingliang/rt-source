package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class EncryptionElementProxy extends ElementProxy {
  public EncryptionElementProxy(Document paramDocument) { super(paramDocument); }
  
  public EncryptionElementProxy(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public final String getBaseNamespace() { return "http://www.w3.org/2001/04/xmlenc#"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\EncryptionElementProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */