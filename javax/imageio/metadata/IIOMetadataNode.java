package javax.imageio.metadata;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class IIOMetadataNode implements Element, NodeList {
  private String nodeName = null;
  
  private String nodeValue = null;
  
  private Object userObject = null;
  
  private IIOMetadataNode parent = null;
  
  private int numChildren = 0;
  
  private IIOMetadataNode firstChild = null;
  
  private IIOMetadataNode lastChild = null;
  
  private IIOMetadataNode nextSibling = null;
  
  private IIOMetadataNode previousSibling = null;
  
  private List attributes = new ArrayList();
  
  public IIOMetadataNode() {}
  
  public IIOMetadataNode(String paramString) { this.nodeName = paramString; }
  
  private void checkNode(Node paramNode) throws DOMException {
    if (paramNode == null)
      return; 
    if (!(paramNode instanceof IIOMetadataNode))
      throw new IIODOMException((short)4, "Node not an IIOMetadataNode!"); 
  }
  
  public String getNodeName() { return this.nodeName; }
  
  public String getNodeValue() { return this.nodeValue; }
  
  public void setNodeValue(String paramString) { this.nodeValue = paramString; }
  
  public short getNodeType() { return 1; }
  
  public Node getParentNode() { return this.parent; }
  
  public NodeList getChildNodes() { return this; }
  
  public Node getFirstChild() { return this.firstChild; }
  
  public Node getLastChild() { return this.lastChild; }
  
  public Node getPreviousSibling() { return this.previousSibling; }
  
  public Node getNextSibling() { return this.nextSibling; }
  
  public NamedNodeMap getAttributes() { return new IIONamedNodeMap(this.attributes); }
  
  public Document getOwnerDocument() { return null; }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) {
    if (paramNode1 == null)
      throw new IllegalArgumentException("newChild == null!"); 
    checkNode(paramNode1);
    checkNode(paramNode2);
    IIOMetadataNode iIOMetadataNode1 = (IIOMetadataNode)paramNode1;
    IIOMetadataNode iIOMetadataNode2 = (IIOMetadataNode)paramNode2;
    IIOMetadataNode iIOMetadataNode3 = null;
    IIOMetadataNode iIOMetadataNode4 = null;
    if (paramNode2 == null) {
      iIOMetadataNode3 = this.lastChild;
      iIOMetadataNode4 = null;
      this.lastChild = iIOMetadataNode1;
    } else {
      iIOMetadataNode3 = iIOMetadataNode2.previousSibling;
      iIOMetadataNode4 = iIOMetadataNode2;
    } 
    if (iIOMetadataNode3 != null)
      iIOMetadataNode3.nextSibling = iIOMetadataNode1; 
    if (iIOMetadataNode4 != null)
      iIOMetadataNode4.previousSibling = iIOMetadataNode1; 
    iIOMetadataNode1.parent = this;
    iIOMetadataNode1.previousSibling = iIOMetadataNode3;
    iIOMetadataNode1.nextSibling = iIOMetadataNode4;
    if (this.firstChild == iIOMetadataNode2)
      this.firstChild = iIOMetadataNode1; 
    this.numChildren++;
    return iIOMetadataNode1;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) {
    if (paramNode1 == null)
      throw new IllegalArgumentException("newChild == null!"); 
    checkNode(paramNode1);
    checkNode(paramNode2);
    IIOMetadataNode iIOMetadataNode1 = (IIOMetadataNode)paramNode1;
    IIOMetadataNode iIOMetadataNode2 = (IIOMetadataNode)paramNode2;
    IIOMetadataNode iIOMetadataNode3 = iIOMetadataNode2.previousSibling;
    IIOMetadataNode iIOMetadataNode4 = iIOMetadataNode2.nextSibling;
    if (iIOMetadataNode3 != null)
      iIOMetadataNode3.nextSibling = iIOMetadataNode1; 
    if (iIOMetadataNode4 != null)
      iIOMetadataNode4.previousSibling = iIOMetadataNode1; 
    iIOMetadataNode1.parent = this;
    iIOMetadataNode1.previousSibling = iIOMetadataNode3;
    iIOMetadataNode1.nextSibling = iIOMetadataNode4;
    if (this.firstChild == iIOMetadataNode2)
      this.firstChild = iIOMetadataNode1; 
    if (this.lastChild == iIOMetadataNode2)
      this.lastChild = iIOMetadataNode1; 
    iIOMetadataNode2.parent = null;
    iIOMetadataNode2.previousSibling = null;
    iIOMetadataNode2.nextSibling = null;
    return iIOMetadataNode2;
  }
  
  public Node removeChild(Node paramNode) {
    if (paramNode == null)
      throw new IllegalArgumentException("oldChild == null!"); 
    checkNode(paramNode);
    IIOMetadataNode iIOMetadataNode1 = (IIOMetadataNode)paramNode;
    IIOMetadataNode iIOMetadataNode2 = iIOMetadataNode1.previousSibling;
    IIOMetadataNode iIOMetadataNode3 = iIOMetadataNode1.nextSibling;
    if (iIOMetadataNode2 != null)
      iIOMetadataNode2.nextSibling = iIOMetadataNode3; 
    if (iIOMetadataNode3 != null)
      iIOMetadataNode3.previousSibling = iIOMetadataNode2; 
    if (this.firstChild == iIOMetadataNode1)
      this.firstChild = iIOMetadataNode3; 
    if (this.lastChild == iIOMetadataNode1)
      this.lastChild = iIOMetadataNode2; 
    iIOMetadataNode1.parent = null;
    iIOMetadataNode1.previousSibling = null;
    iIOMetadataNode1.nextSibling = null;
    this.numChildren--;
    return iIOMetadataNode1;
  }
  
  public Node appendChild(Node paramNode) {
    if (paramNode == null)
      throw new IllegalArgumentException("newChild == null!"); 
    checkNode(paramNode);
    return insertBefore(paramNode, null);
  }
  
  public boolean hasChildNodes() { return (this.numChildren > 0); }
  
  public Node cloneNode(boolean paramBoolean) {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(this.nodeName);
    iIOMetadataNode.setUserObject(getUserObject());
    if (paramBoolean)
      for (IIOMetadataNode iIOMetadataNode1 = this.firstChild; iIOMetadataNode1 != null; iIOMetadataNode1 = iIOMetadataNode1.nextSibling)
        iIOMetadataNode.appendChild(iIOMetadataNode1.cloneNode(true));  
    return iIOMetadataNode;
  }
  
  public void normalize() {}
  
  public boolean isSupported(String paramString1, String paramString2) { return false; }
  
  public String getNamespaceURI() { return null; }
  
  public String getPrefix() { return null; }
  
  public void setPrefix(String paramString) {}
  
  public String getLocalName() { return this.nodeName; }
  
  public String getTagName() { return this.nodeName; }
  
  public String getAttribute(String paramString) {
    Attr attr = getAttributeNode(paramString);
    return (attr == null) ? "" : attr.getValue();
  }
  
  public String getAttributeNS(String paramString1, String paramString2) { return getAttribute(paramString2); }
  
  public void setAttribute(String paramString1, String paramString2) {
    boolean bool = true;
    char[] arrayOfChar = paramString1.toCharArray();
    for (byte b = 0; b < arrayOfChar.length; b++) {
      if (arrayOfChar[b] >= '￾') {
        bool = false;
        break;
      } 
    } 
    if (!bool)
      throw new IIODOMException((short)5, "Attribute name is illegal!"); 
    removeAttribute(paramString1, false);
    this.attributes.add(new IIOAttr(this, paramString1, paramString2));
  }
  
  public void setAttributeNS(String paramString1, String paramString2, String paramString3) { setAttribute(paramString2, paramString3); }
  
  public void removeAttribute(String paramString) { removeAttribute(paramString, true); }
  
  private void removeAttribute(String paramString, boolean paramBoolean) {
    int i = this.attributes.size();
    for (byte b = 0; b < i; b++) {
      IIOAttr iIOAttr = (IIOAttr)this.attributes.get(b);
      if (paramString.equals(iIOAttr.getName())) {
        iIOAttr.setOwnerElement(null);
        this.attributes.remove(b);
        return;
      } 
    } 
    if (paramBoolean)
      throw new IIODOMException((short)8, "No such attribute!"); 
  }
  
  public void removeAttributeNS(String paramString1, String paramString2) { removeAttribute(paramString2); }
  
  public Attr getAttributeNode(String paramString) {
    Node node = getAttributes().getNamedItem(paramString);
    return (Attr)node;
  }
  
  public Attr getAttributeNodeNS(String paramString1, String paramString2) { return getAttributeNode(paramString2); }
  
  public Attr setAttributeNode(Attr paramAttr) throws DOMException {
    IIOAttr iIOAttr;
    Element element = paramAttr.getOwnerElement();
    if (element != null) {
      if (element == this)
        return null; 
      throw new DOMException((short)10, "Attribute is already in use");
    } 
    if (paramAttr instanceof IIOAttr) {
      iIOAttr = (IIOAttr)paramAttr;
      iIOAttr.setOwnerElement(this);
    } else {
      iIOAttr = new IIOAttr(this, paramAttr.getName(), paramAttr.getValue());
    } 
    Attr attr = getAttributeNode(iIOAttr.getName());
    if (attr != null)
      removeAttributeNode(attr); 
    this.attributes.add(iIOAttr);
    return attr;
  }
  
  public Attr setAttributeNodeNS(Attr paramAttr) throws DOMException { return setAttributeNode(paramAttr); }
  
  public Attr removeAttributeNode(Attr paramAttr) throws DOMException {
    removeAttribute(paramAttr.getName());
    return paramAttr;
  }
  
  public NodeList getElementsByTagName(String paramString) {
    ArrayList arrayList = new ArrayList();
    getElementsByTagName(paramString, arrayList);
    return new IIONodeList(arrayList);
  }
  
  private void getElementsByTagName(String paramString, List paramList) {
    if (this.nodeName.equals(paramString))
      paramList.add(this); 
    for (Node node = getFirstChild(); node != null; node = node.getNextSibling())
      ((IIOMetadataNode)node).getElementsByTagName(paramString, paramList); 
  }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) { return getElementsByTagName(paramString2); }
  
  public boolean hasAttributes() { return (this.attributes.size() > 0); }
  
  public boolean hasAttribute(String paramString) { return (getAttributeNode(paramString) != null); }
  
  public boolean hasAttributeNS(String paramString1, String paramString2) { return hasAttribute(paramString2); }
  
  public int getLength() { return this.numChildren; }
  
  public Node item(int paramInt) {
    if (paramInt < 0)
      return null; 
    Node node;
    for (node = getFirstChild(); node != null && paramInt-- > 0; node = node.getNextSibling());
    return node;
  }
  
  public Object getUserObject() { return this.userObject; }
  
  public void setUserObject(Object paramObject) { this.userObject = paramObject; }
  
  public void setIdAttribute(String paramString, boolean paramBoolean) { throw new DOMException((short)9, "Method not supported"); }
  
  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public TypeInfo getSchemaTypeInfo() throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Object getUserData(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Object getFeature(String paramString1, String paramString2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean isSameNode(Node paramNode) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean isEqualNode(Node paramNode) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public String lookupNamespaceURI(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean isDefaultNamespace(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public String lookupPrefix(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public String getTextContent() { throw new DOMException((short)9, "Method not supported"); }
  
  public void setTextContent(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public short compareDocumentPosition(Node paramNode) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public String getBaseURI() { throw new DOMException((short)9, "Method not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\metadata\IIOMetadataNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */