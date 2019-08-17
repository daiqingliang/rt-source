package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import org.w3c.dom.Node;

public abstract class DOMStructure implements XMLStructure {
  public final boolean isFeatureSupported(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    return false;
  }
  
  public abstract void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMStructure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */