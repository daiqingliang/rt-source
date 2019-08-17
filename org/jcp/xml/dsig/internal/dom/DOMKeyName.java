package org.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMKeyName extends DOMStructure implements KeyName {
  private final String name;
  
  public DOMKeyName(String paramString) {
    if (paramString == null)
      throw new NullPointerException("name cannot be null"); 
    this.name = paramString;
  }
  
  public DOMKeyName(Element paramElement) { this.name = paramElement.getFirstChild().getNodeValue(); }
  
  public String getName() { return this.name; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "KeyName", "http://www.w3.org/2000/09/xmldsig#", paramString);
    element.appendChild(document.createTextNode(this.name));
    paramNode.appendChild(element);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof KeyName))
      return false; 
    KeyName keyName = (KeyName)paramObject;
    return this.name.equals(keyName.getName());
  }
  
  public int hashCode() {
    null = 17;
    return 31 * null + this.name.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMKeyName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */