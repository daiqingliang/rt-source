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
import javax.xml.crypto.dsig.XMLObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMXMLObject extends DOMStructure implements XMLObject {
  private final String id;
  
  private final String mimeType;
  
  private final String encoding;
  
  private final List<XMLStructure> content;
  
  private Element objectElem;
  
  public DOMXMLObject(List<? extends XMLStructure> paramList, String paramString1, String paramString2, String paramString3) {
    if (paramList == null || paramList.isEmpty()) {
      this.content = Collections.emptyList();
    } else {
      this.content = Collections.unmodifiableList(new ArrayList(paramList));
      byte b = 0;
      int i = this.content.size();
      while (b < i) {
        if (!(this.content.get(b) instanceof XMLStructure))
          throw new ClassCastException("content[" + b + "] is not a valid type"); 
        b++;
      } 
    } 
    this.id = paramString1;
    this.mimeType = paramString2;
    this.encoding = paramString3;
  }
  
  public DOMXMLObject(Element paramElement, XMLCryptoContext paramXMLCryptoContext, Provider paramProvider) throws MarshalException {
    this.encoding = DOMUtils.getAttributeValue(paramElement, "Encoding");
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null) {
      this.id = attr.getValue();
      paramElement.setIdAttributeNode(attr, true);
    } else {
      this.id = null;
    } 
    this.mimeType = DOMUtils.getAttributeValue(paramElement, "MimeType");
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      Node node = nodeList.item(b);
      if (node.getNodeType() == 1) {
        Element element = (Element)node;
        String str = element.getLocalName();
        if (str.equals("Manifest")) {
          arrayList.add(new DOMManifest(element, paramXMLCryptoContext, paramProvider));
          continue;
        } 
        if (str.equals("SignatureProperties")) {
          arrayList.add(new DOMSignatureProperties(element, paramXMLCryptoContext));
          continue;
        } 
        if (str.equals("X509Data")) {
          arrayList.add(new DOMX509Data(element));
          continue;
        } 
      } 
      arrayList.add(new DOMStructure(node));
      continue;
    } 
    if (arrayList.isEmpty()) {
      this.content = Collections.emptyList();
    } else {
      this.content = Collections.unmodifiableList(arrayList);
    } 
    this.objectElem = paramElement;
  }
  
  public List getContent() { return this.content; }
  
  public String getId() { return this.id; }
  
  public String getMimeType() { return this.mimeType; }
  
  public String getEncoding() { return this.encoding; }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = (this.objectElem != null) ? this.objectElem : null;
    if (element == null) {
      element = DOMUtils.createElement(document, "Object", "http://www.w3.org/2000/09/xmldsig#", paramString);
      DOMUtils.setAttributeID(element, "Id", this.id);
      DOMUtils.setAttribute(element, "MimeType", this.mimeType);
      DOMUtils.setAttribute(element, "Encoding", this.encoding);
      for (XMLStructure xMLStructure : this.content) {
        if (xMLStructure instanceof DOMStructure) {
          ((DOMStructure)xMLStructure).marshal(element, paramString, paramDOMCryptoContext);
          continue;
        } 
        DOMStructure dOMStructure = (DOMStructure)xMLStructure;
        DOMUtils.appendChild(element, dOMStructure.getNode());
      } 
    } 
    paramNode.appendChild(element);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof XMLObject))
      return false; 
    XMLObject xMLObject = (XMLObject)paramObject;
    boolean bool1 = (this.id == null) ? ((xMLObject.getId() == null) ? 1 : 0) : this.id.equals(xMLObject.getId());
    boolean bool2 = (this.encoding == null) ? ((xMLObject.getEncoding() == null) ? 1 : 0) : this.encoding.equals(xMLObject.getEncoding());
    boolean bool3 = (this.mimeType == null) ? ((xMLObject.getMimeType() == null) ? 1 : 0) : this.mimeType.equals(xMLObject.getMimeType());
    List list = xMLObject.getContent();
    return (bool1 && bool2 && bool3 && equalsContent(list));
  }
  
  public int hashCode() {
    null = 17;
    if (this.id != null)
      null = 31 * null + this.id.hashCode(); 
    if (this.encoding != null)
      null = 31 * null + this.encoding.hashCode(); 
    if (this.mimeType != null)
      null = 31 * null + this.mimeType.hashCode(); 
    return 31 * null + this.content.hashCode();
  }
  
  private boolean equalsContent(List<XMLStructure> paramList) {
    if (this.content.size() != paramList.size())
      return false; 
    byte b = 0;
    int i = paramList.size();
    while (b < i) {
      XMLStructure xMLStructure1 = (XMLStructure)paramList.get(b);
      XMLStructure xMLStructure2 = (XMLStructure)this.content.get(b);
      if (xMLStructure1 instanceof DOMStructure) {
        if (!(xMLStructure2 instanceof DOMStructure))
          return false; 
        Node node1 = ((DOMStructure)xMLStructure1).getNode();
        Node node2 = ((DOMStructure)xMLStructure2).getNode();
        if (!DOMUtils.nodesEqual(node2, node1))
          return false; 
      } else if (!xMLStructure2.equals(xMLStructure1)) {
        return false;
      } 
      b++;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMXMLObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */