package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMDOMException;
import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.Objects;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class DTMNodeProxy implements Node, Document, Text, Element, Attr, ProcessingInstruction, Comment, DocumentFragment {
  public DTM dtm;
  
  int node;
  
  private static final String EMPTYSTRING = "";
  
  static final DOMImplementation implementation = new DTMNodeProxyImplementation();
  
  protected String fDocumentURI;
  
  protected String actualEncoding;
  
  private String xmlEncoding;
  
  private boolean xmlStandalone;
  
  private String xmlVersion;
  
  public DTMNodeProxy(DTM paramDTM, int paramInt) {
    this.dtm = paramDTM;
    this.node = paramInt;
  }
  
  public final DTM getDTM() { return this.dtm; }
  
  public final int getDTMNodeNumber() { return this.node; }
  
  public final boolean equals(Node paramNode) {
    try {
      DTMNodeProxy dTMNodeProxy = (DTMNodeProxy)paramNode;
      return (dTMNodeProxy.node == this.node && dTMNodeProxy.dtm == this.dtm);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public final boolean equals(Object paramObject) { return (paramObject instanceof Node && equals((Node)paramObject)); }
  
  public int hashCode() {
    null = 7;
    null = 29 * null + Objects.hashCode(this.dtm);
    return 29 * null + this.node;
  }
  
  public final boolean sameNodeAs(Node paramNode) {
    if (!(paramNode instanceof DTMNodeProxy))
      return false; 
    DTMNodeProxy dTMNodeProxy = (DTMNodeProxy)paramNode;
    return (this.dtm == dTMNodeProxy.dtm && this.node == dTMNodeProxy.node);
  }
  
  public final String getNodeName() { return this.dtm.getNodeName(this.node); }
  
  public final String getTarget() { return this.dtm.getNodeName(this.node); }
  
  public final String getLocalName() { return this.dtm.getLocalName(this.node); }
  
  public final String getPrefix() { return this.dtm.getPrefix(this.node); }
  
  public final void setPrefix(String paramString) throws DOMException { throw new DTMDOMException((short)7); }
  
  public final String getNamespaceURI() { return this.dtm.getNamespaceURI(this.node); }
  
  public final boolean supports(String paramString1, String paramString2) { return implementation.hasFeature(paramString1, paramString2); }
  
  public final boolean isSupported(String paramString1, String paramString2) { return implementation.hasFeature(paramString1, paramString2); }
  
  public final String getNodeValue() { return this.dtm.getNodeValue(this.node); }
  
  public final String getStringValue() { return this.dtm.getStringValue(this.node).toString(); }
  
  public final void setNodeValue(String paramString) throws DOMException { throw new DTMDOMException((short)7); }
  
  public final short getNodeType() { return this.dtm.getNodeType(this.node); }
  
  public final Node getParentNode() {
    if (getNodeType() == 2)
      return null; 
    int i = this.dtm.getParent(this.node);
    return (i == -1) ? null : this.dtm.getNode(i);
  }
  
  public final Node getOwnerNode() {
    int i = this.dtm.getParent(this.node);
    return (i == -1) ? null : this.dtm.getNode(i);
  }
  
  public final NodeList getChildNodes() { return new DTMChildIterNodeList(this.dtm, this.node); }
  
  public final Node getFirstChild() {
    int i = this.dtm.getFirstChild(this.node);
    return (i == -1) ? null : this.dtm.getNode(i);
  }
  
  public final Node getLastChild() {
    int i = this.dtm.getLastChild(this.node);
    return (i == -1) ? null : this.dtm.getNode(i);
  }
  
  public final Node getPreviousSibling() {
    int i = this.dtm.getPreviousSibling(this.node);
    return (i == -1) ? null : this.dtm.getNode(i);
  }
  
  public final Node getNextSibling() {
    if (this.dtm.getNodeType(this.node) == 2)
      return null; 
    int i = this.dtm.getNextSibling(this.node);
    return (i == -1) ? null : this.dtm.getNode(i);
  }
  
  public final NamedNodeMap getAttributes() { return new DTMNamedNodeMap(this.dtm, this.node); }
  
  public boolean hasAttribute(String paramString) { return (-1 != this.dtm.getAttributeNode(this.node, null, paramString)); }
  
  public boolean hasAttributeNS(String paramString1, String paramString2) { return (-1 != this.dtm.getAttributeNode(this.node, paramString1, paramString2)); }
  
  public final Document getOwnerDocument() { return (Document)this.dtm.getNode(this.dtm.getOwnerDocument(this.node)); }
  
  public final Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException { throw new DTMDOMException((short)7); }
  
  public final Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException { throw new DTMDOMException((short)7); }
  
  public final Node removeChild(Node paramNode) throws DOMException { throw new DTMDOMException((short)7); }
  
  public final Node appendChild(Node paramNode) throws DOMException { throw new DTMDOMException((short)7); }
  
  public final boolean hasChildNodes() { return (-1 != this.dtm.getFirstChild(this.node)); }
  
  public final Node cloneNode(boolean paramBoolean) { throw new DTMDOMException((short)9); }
  
  public final DocumentType getDoctype() { return null; }
  
  public final DOMImplementation getImplementation() { return implementation; }
  
  public final Element getDocumentElement() {
    int i = this.dtm.getDocument();
    int j = -1;
    for (int k = this.dtm.getFirstChild(i); k != -1; k = this.dtm.getNextSibling(k)) {
      switch (this.dtm.getNodeType(k)) {
        case 1:
          if (j != -1) {
            j = -1;
            k = this.dtm.getLastChild(i);
            break;
          } 
          j = k;
          break;
        case 7:
        case 8:
        case 10:
          break;
        default:
          j = -1;
          k = this.dtm.getLastChild(i);
          break;
      } 
    } 
    if (j == -1)
      throw new DTMDOMException((short)9); 
    return (Element)this.dtm.getNode(j);
  }
  
  public final Element createElement(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final DocumentFragment createDocumentFragment() { throw new DTMDOMException((short)9); }
  
  public final Text createTextNode(String paramString) { throw new DTMDOMException((short)9); }
  
  public final Comment createComment(String paramString) { throw new DTMDOMException((short)9); }
  
  public final CDATASection createCDATASection(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final Attr createAttribute(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final EntityReference createEntityReference(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final NodeList getElementsByTagName(String paramString) {
    Vector vector = new Vector();
    Node node1 = this.dtm.getNode(this.node);
    if (node1 != null) {
      boolean bool = "*".equals(paramString);
      if (1 == node1.getNodeType()) {
        NodeList nodeList = node1.getChildNodes();
        for (byte b1 = 0; b1 < nodeList.getLength(); b1++)
          traverseChildren(vector, nodeList.item(b1), paramString, bool); 
      } else if (9 == node1.getNodeType()) {
        traverseChildren(vector, this.dtm.getNode(this.node), paramString, bool);
      } 
    } 
    int i = vector.size();
    NodeSet nodeSet = new NodeSet(i);
    for (byte b = 0; b < i; b++)
      nodeSet.addNode((Node)vector.elementAt(b)); 
    return nodeSet;
  }
  
  private final void traverseChildren(Vector paramVector, Node paramNode, String paramString, boolean paramBoolean) {
    if (paramNode == null)
      return; 
    if (paramNode.getNodeType() == 1 && (paramBoolean || paramNode.getNodeName().equals(paramString)))
      paramVector.add(paramNode); 
    if (paramNode.hasChildNodes()) {
      NodeList nodeList = paramNode.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++)
        traverseChildren(paramVector, nodeList.item(b), paramString, paramBoolean); 
    } 
  }
  
  public final Node importNode(Node paramNode, boolean paramBoolean) throws DOMException { throw new DTMDOMException((short)7); }
  
  public final Element createElementNS(String paramString1, String paramString2) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final Attr createAttributeNS(String paramString1, String paramString2) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final NodeList getElementsByTagNameNS(String paramString1, String paramString2) {
    Vector vector = new Vector();
    Node node1 = this.dtm.getNode(this.node);
    if (node1 != null) {
      boolean bool1 = "*".equals(paramString1);
      boolean bool2 = "*".equals(paramString2);
      if (1 == node1.getNodeType()) {
        NodeList nodeList = node1.getChildNodes();
        for (byte b1 = 0; b1 < nodeList.getLength(); b1++)
          traverseChildren(vector, nodeList.item(b1), paramString1, paramString2, bool1, bool2); 
      } else if (9 == node1.getNodeType()) {
        traverseChildren(vector, this.dtm.getNode(this.node), paramString1, paramString2, bool1, bool2);
      } 
    } 
    int i = vector.size();
    NodeSet nodeSet = new NodeSet(i);
    for (byte b = 0; b < i; b++)
      nodeSet.addNode((Node)vector.elementAt(b)); 
    return nodeSet;
  }
  
  private final void traverseChildren(Vector paramVector, Node paramNode, String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramNode == null)
      return; 
    if (paramNode.getNodeType() == 1 && (paramBoolean2 || paramNode.getLocalName().equals(paramString2))) {
      String str = paramNode.getNamespaceURI();
      if ((paramString1 == null && str == null) || paramBoolean1 || (paramString1 != null && paramString1.equals(str)))
        paramVector.add(paramNode); 
    } 
    if (paramNode.hasChildNodes()) {
      NodeList nodeList = paramNode.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++)
        traverseChildren(paramVector, nodeList.item(b), paramString1, paramString2, paramBoolean1, paramBoolean2); 
    } 
  }
  
  public final Element getElementById(String paramString) throws DOMException { return (Element)this.dtm.getNode(this.dtm.getElementById(paramString)); }
  
  public final Text splitText(int paramInt) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final String getData() { return this.dtm.getNodeValue(this.node); }
  
  public final void setData(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final int getLength() { return this.dtm.getNodeValue(this.node).length(); }
  
  public final String substringData(int paramInt1, int paramInt2) throws DOMException { return getData().substring(paramInt1, paramInt1 + paramInt2); }
  
  public final void appendData(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final void insertData(int paramInt, String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final void deleteData(int paramInt1, int paramInt2) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final void replaceData(int paramInt1, int paramInt2, String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final String getTagName() { return this.dtm.getNodeName(this.node); }
  
  public final String getAttribute(String paramString) {
    DTMNamedNodeMap dTMNamedNodeMap = new DTMNamedNodeMap(this.dtm, this.node);
    Node node1 = dTMNamedNodeMap.getNamedItem(paramString);
    return (null == node1) ? "" : node1.getNodeValue();
  }
  
  public final void setAttribute(String paramString1, String paramString2) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final void removeAttribute(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final Attr getAttributeNode(String paramString) throws DOMException {
    DTMNamedNodeMap dTMNamedNodeMap = new DTMNamedNodeMap(this.dtm, this.node);
    return (Attr)dTMNamedNodeMap.getNamedItem(paramString);
  }
  
  public final Attr setAttributeNode(Attr paramAttr) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final Attr removeAttributeNode(Attr paramAttr) throws DOMException { throw new DTMDOMException((short)9); }
  
  public boolean hasAttributes() { return (-1 != this.dtm.getFirstAttribute(this.node)); }
  
  public final void normalize() { throw new DTMDOMException((short)9); }
  
  public final String getAttributeNS(String paramString1, String paramString2) {
    Node node1 = null;
    int i = this.dtm.getAttributeNode(this.node, paramString1, paramString2);
    if (i != -1)
      node1 = this.dtm.getNode(i); 
    return (null == node1) ? "" : node1.getNodeValue();
  }
  
  public final void setAttributeNS(String paramString1, String paramString2, String paramString3) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final void removeAttributeNS(String paramString1, String paramString2) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final Attr getAttributeNodeNS(String paramString1, String paramString2) throws DOMException {
    Attr attr = null;
    int i = this.dtm.getAttributeNode(this.node, paramString1, paramString2);
    if (i != -1)
      attr = (Attr)this.dtm.getNode(i); 
    return attr;
  }
  
  public final Attr setAttributeNodeNS(Attr paramAttr) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final String getName() { return this.dtm.getNodeName(this.node); }
  
  public final boolean getSpecified() { return true; }
  
  public final String getValue() { return this.dtm.getNodeValue(this.node); }
  
  public final void setValue(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public final Element getOwnerElement() {
    if (getNodeType() != 2)
      return null; 
    int i = this.dtm.getParent(this.node);
    return (i == -1) ? null : (Element)this.dtm.getNode(i);
  }
  
  public Node adoptNode(Node paramNode) throws DOMException { throw new DTMDOMException((short)9); }
  
  public String getInputEncoding() { throw new DTMDOMException((short)9); }
  
  public void setEncoding(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public boolean getStandalone() { throw new DTMDOMException((short)9); }
  
  public void setStandalone(boolean paramBoolean) { throw new DTMDOMException((short)9); }
  
  public boolean getStrictErrorChecking() { throw new DTMDOMException((short)9); }
  
  public void setStrictErrorChecking(boolean paramBoolean) { throw new DTMDOMException((short)9); }
  
  public String getVersion() { throw new DTMDOMException((short)9); }
  
  public void setVersion(String paramString) throws DOMException { throw new DTMDOMException((short)9); }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler) { return getOwnerDocument().setUserData(paramString, paramObject, paramUserDataHandler); }
  
  public Object getUserData(String paramString) { return getOwnerDocument().getUserData(paramString); }
  
  public Object getFeature(String paramString1, String paramString2) { return isSupported(paramString1, paramString2) ? this : null; }
  
  public boolean isEqualNode(Node paramNode) {
    if (paramNode == this)
      return true; 
    if (paramNode.getNodeType() != getNodeType())
      return false; 
    if (getNodeName() == null) {
      if (paramNode.getNodeName() != null)
        return false; 
    } else if (!getNodeName().equals(paramNode.getNodeName())) {
      return false;
    } 
    if (getLocalName() == null) {
      if (paramNode.getLocalName() != null)
        return false; 
    } else if (!getLocalName().equals(paramNode.getLocalName())) {
      return false;
    } 
    if (getNamespaceURI() == null) {
      if (paramNode.getNamespaceURI() != null)
        return false; 
    } else if (!getNamespaceURI().equals(paramNode.getNamespaceURI())) {
      return false;
    } 
    if (getPrefix() == null) {
      if (paramNode.getPrefix() != null)
        return false; 
    } else if (!getPrefix().equals(paramNode.getPrefix())) {
      return false;
    } 
    if (getNodeValue() == null) {
      if (paramNode.getNodeValue() != null)
        return false; 
    } else if (!getNodeValue().equals(paramNode.getNodeValue())) {
      return false;
    } 
    return true;
  }
  
  public String lookupNamespaceURI(String paramString) {
    String str2;
    String str1;
    short s = getNodeType();
    switch (s) {
      case 1:
        str1 = getNamespaceURI();
        str2 = getPrefix();
        if (str1 != null) {
          if (paramString == null && str2 == paramString)
            return str1; 
          if (str2 != null && str2.equals(paramString))
            return str1; 
        } 
        if (hasAttributes()) {
          NamedNodeMap namedNodeMap = getAttributes();
          int i = namedNodeMap.getLength();
          for (byte b = 0; b < i; b++) {
            Node node1 = namedNodeMap.item(b);
            String str3 = node1.getPrefix();
            String str4 = node1.getNodeValue();
            str1 = node1.getNamespaceURI();
            if (str1 != null && str1.equals("http://www.w3.org/2000/xmlns/")) {
              if (paramString == null && node1.getNodeName().equals("xmlns"))
                return str4; 
              if (str3 != null && str3.equals("xmlns") && node1.getLocalName().equals(paramString))
                return str4; 
            } 
          } 
        } 
        return null;
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        return (getOwnerElement().getNodeType() == 1) ? getOwnerElement().lookupNamespaceURI(paramString) : null;
    } 
    return null;
  }
  
  public boolean isDefaultNamespace(String paramString) { return false; }
  
  public String lookupPrefix(String paramString) {
    if (paramString == null)
      return null; 
    short s = getNodeType();
    switch (s) {
      case 6:
      case 10:
      case 11:
      case 12:
        return null;
      case 2:
        return (getOwnerElement().getNodeType() == 1) ? getOwnerElement().lookupPrefix(paramString) : null;
    } 
    return null;
  }
  
  public boolean isSameNode(Node paramNode) { return (this == paramNode); }
  
  public void setTextContent(String paramString) throws DOMException { setNodeValue(paramString); }
  
  public String getTextContent() { return this.dtm.getStringValue(this.node).toString(); }
  
  public short compareDocumentPosition(Node paramNode) throws DOMException { return 0; }
  
  public String getBaseURI() { return null; }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2) throws DOMException { return paramNode; }
  
  public void normalizeDocument() {}
  
  public DOMConfiguration getDomConfig() { return null; }
  
  public void setDocumentURI(String paramString) throws DOMException { this.fDocumentURI = paramString; }
  
  public String getDocumentURI() { return this.fDocumentURI; }
  
  public String getActualEncoding() { return this.actualEncoding; }
  
  public void setActualEncoding(String paramString) throws DOMException { this.actualEncoding = paramString; }
  
  public Text replaceWholeText(String paramString) { return null; }
  
  public String getWholeText() { return null; }
  
  public boolean isElementContentWhitespace() { return false; }
  
  public void setIdAttribute(boolean paramBoolean) {}
  
  public void setIdAttribute(String paramString, boolean paramBoolean) {}
  
  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean) {}
  
  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean) {}
  
  public TypeInfo getSchemaTypeInfo() { return null; }
  
  public boolean isId() { return false; }
  
  public String getXmlEncoding() { return this.xmlEncoding; }
  
  public void setXmlEncoding(String paramString) throws DOMException { this.xmlEncoding = paramString; }
  
  public boolean getXmlStandalone() { return this.xmlStandalone; }
  
  public void setXmlStandalone(boolean paramBoolean) { this.xmlStandalone = paramBoolean; }
  
  public String getXmlVersion() { return this.xmlVersion; }
  
  public void setXmlVersion(String paramString) throws DOMException { this.xmlVersion = paramString; }
  
  static class DTMNodeProxyImplementation implements DOMImplementation {
    public DocumentType createDocumentType(String param1String1, String param1String2, String param1String3) { throw new DTMDOMException((short)9); }
    
    public Document createDocument(String param1String1, String param1String2, DocumentType param1DocumentType) { throw new DTMDOMException((short)9); }
    
    public boolean hasFeature(String param1String1, String param1String2) { return (("CORE".equals(param1String1.toUpperCase()) || "XML".equals(param1String1.toUpperCase())) && ("1.0".equals(param1String2) || "2.0".equals(param1String2))); }
    
    public Object getFeature(String param1String1, String param1String2) { return null; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMNodeProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */