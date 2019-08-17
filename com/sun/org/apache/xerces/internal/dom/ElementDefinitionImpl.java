package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementDefinitionImpl extends ParentNode {
  static final long serialVersionUID = -8373890672670022714L;
  
  protected String name;
  
  protected NamedNodeMapImpl attributes;
  
  public ElementDefinitionImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) {
    super(paramCoreDocumentImpl);
    this.name = paramString;
    this.attributes = new NamedNodeMapImpl(paramCoreDocumentImpl);
  }
  
  public short getNodeType() { return 21; }
  
  public String getNodeName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public Node cloneNode(boolean paramBoolean) {
    ElementDefinitionImpl elementDefinitionImpl = (ElementDefinitionImpl)super.cloneNode(paramBoolean);
    elementDefinitionImpl.attributes = this.attributes.cloneMap(elementDefinitionImpl);
    return elementDefinitionImpl;
  }
  
  public NamedNodeMap getAttributes() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this.attributes;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\ElementDefinitionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */