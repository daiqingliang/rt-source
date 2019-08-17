package org.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMKeyInfo extends DOMStructure implements KeyInfo {
  private final String id;
  
  private final List<XMLStructure> keyInfoTypes;
  
  public DOMKeyInfo(List<? extends XMLStructure> paramList, String paramString) {
    if (paramList == null)
      throw new NullPointerException("content cannot be null"); 
    this.keyInfoTypes = Collections.unmodifiableList(new ArrayList(paramList));
    if (this.keyInfoTypes.isEmpty())
      throw new IllegalArgumentException("content cannot be empty"); 
    byte b = 0;
    int i = this.keyInfoTypes.size();
    while (b < i) {
      if (!(this.keyInfoTypes.get(b) instanceof XMLStructure))
        throw new ClassCastException("content[" + b + "] is not a valid KeyInfo type"); 
      b++;
    } 
    this.id = paramString;
  }
  
  public DOMKeyInfo(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider) throws MarshalException {
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null) {
      this.id = attr.getValue();
      paramElement.setIdAttributeNode(attr, true);
    } else {
      this.id = null;
    } 
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    if (i < 1)
      throw new MarshalException("KeyInfo must contain at least one type"); 
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      Node node = nodeList.item(b);
      if (node.getNodeType() == 1) {
        Element element = (Element)node;
        String str = element.getLocalName();
        if (str.equals("X509Data")) {
          arrayList.add(new DOMX509Data(element));
        } else if (str.equals("KeyName")) {
          arrayList.add(new DOMKeyName(element));
        } else if (str.equals("KeyValue")) {
          arrayList.add(DOMKeyValue.unmarshal(element));
        } else if (str.equals("RetrievalMethod")) {
          arrayList.add(new DOMRetrievalMethod(element, paramXMLCryptoContext, paramProvider));
        } else if (str.equals("PGPData")) {
          arrayList.add(new DOMPGPData(element));
        } else {
          arrayList.add(new DOMStructure(element));
        } 
      } 
    } 
    this.keyInfoTypes = Collections.unmodifiableList(arrayList);
  }
  
  public String getId() { return this.id; }
  
  public List getContent() { return this.keyInfoTypes; }
  
  public void marshal(XMLStructure paramXMLStructure, XMLCryptoContext paramXMLCryptoContext) throws MarshalException {
    if (paramXMLStructure == null)
      throw new NullPointerException("parent is null"); 
    if (!(paramXMLStructure instanceof DOMStructure))
      throw new ClassCastException("parent must be of type DOMStructure"); 
    Node node = ((DOMStructure)paramXMLStructure).getNode();
    String str = DOMUtils.getSignaturePrefix(paramXMLCryptoContext);
    Element element = DOMUtils.createElement(DOMUtils.getOwnerDocument(node), "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", str);
    if (str == null || str.length() == 0) {
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
    } else {
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, "http://www.w3.org/2000/09/xmldsig#");
    } 
    marshal(node, element, null, str, (DOMCryptoContext)paramXMLCryptoContext);
  }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException { marshal(paramNode, null, paramString, paramDOMCryptoContext); }
  
  public void marshal(Node paramNode1, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode1);
    Element element = DOMUtils.createElement(document, "KeyInfo", "http://www.w3.org/2000/09/xmldsig#", paramString);
    marshal(paramNode1, element, paramNode2, paramString, paramDOMCryptoContext);
  }
  
  private void marshal(Node paramNode1, Element paramElement, Node paramNode2, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    for (XMLStructure xMLStructure : this.keyInfoTypes) {
      if (xMLStructure instanceof DOMStructure) {
        ((DOMStructure)xMLStructure).marshal(paramElement, paramString, paramDOMCryptoContext);
        continue;
      } 
      DOMUtils.appendChild(paramElement, ((DOMStructure)xMLStructure).getNode());
    } 
    DOMUtils.setAttributeID(paramElement, "Id", this.id);
    paramNode1.insertBefore(paramElement, paramNode2);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof KeyInfo))
      return false; 
    KeyInfo keyInfo = (KeyInfo)paramObject;
    boolean bool = (this.id == null) ? ((keyInfo.getId() == null) ? 1 : 0) : this.id.equals(keyInfo.getId());
    return (this.keyInfoTypes.equals(keyInfo.getContent()) && bool);
  }
  
  public int hashCode() {
    null = 17;
    if (this.id != null)
      null = 31 * null + this.id.hashCode(); 
    return 31 * null + this.keyInfoTypes.hashCode();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMKeyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */