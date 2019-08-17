package org.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMSignatureProperties extends DOMStructure implements SignatureProperties {
  private final String id;
  
  private final List<SignatureProperty> properties;
  
  public DOMSignatureProperties(List<? extends SignatureProperty> paramList, String paramString) {
    if (paramList == null)
      throw new NullPointerException("properties cannot be null"); 
    if (paramList.isEmpty())
      throw new IllegalArgumentException("properties cannot be empty"); 
    this.properties = Collections.unmodifiableList(new ArrayList(paramList));
    byte b = 0;
    int i = this.properties.size();
    while (b < i) {
      if (!(this.properties.get(b) instanceof SignatureProperty))
        throw new ClassCastException("properties[" + b + "] is not a valid type"); 
      b++;
    } 
    this.id = paramString;
  }
  
  public DOMSignatureProperties(Element paramElement, XMLCryptoContext paramXMLCryptoContext) throws MarshalException {
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null) {
      this.id = attr.getValue();
      paramElement.setIdAttributeNode(attr, true);
    } else {
      this.id = null;
    } 
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      Node node = nodeList.item(b);
      if (node.getNodeType() == 1) {
        String str = node.getLocalName();
        if (!str.equals("SignatureProperty"))
          throw new MarshalException("Invalid element name: " + str + ", expected SignatureProperty"); 
        arrayList.add(new DOMSignatureProperty((Element)node, paramXMLCryptoContext));
      } 
    } 
    if (arrayList.isEmpty())
      throw new MarshalException("properties cannot be empty"); 
    this.properties = Collections.unmodifiableList(arrayList);
  }
  
  public List getProperties() { return this.properties; }
  
  public String getId() { return this.id; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "SignatureProperties", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttributeID(element, "Id", this.id);
    for (SignatureProperty signatureProperty : this.properties)
      ((DOMSignatureProperty)signatureProperty).marshal(element, paramString, paramDOMCryptoContext); 
    paramNode.appendChild(element);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SignatureProperties))
      return false; 
    SignatureProperties signatureProperties = (SignatureProperties)paramObject;
    boolean bool = (this.id == null) ? ((signatureProperties.getId() == null) ? 1 : 0) : this.id.equals(signatureProperties.getId());
    return (this.properties.equals(signatureProperties.getProperties()) && bool);
  }
  
  public int hashCode() {
    null = 17;
    if (this.id != null)
      null = 31 * null + this.id.hashCode(); 
    return 31 * null + this.properties.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMSignatureProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */