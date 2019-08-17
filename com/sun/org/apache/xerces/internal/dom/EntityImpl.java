package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;

public class EntityImpl extends ParentNode implements Entity {
  static final long serialVersionUID = -3575760943444303423L;
  
  protected String name;
  
  protected String publicId;
  
  protected String systemId;
  
  protected String encoding;
  
  protected String inputEncoding;
  
  protected String version;
  
  protected String notationName;
  
  protected String baseURI;
  
  public EntityImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) {
    super(paramCoreDocumentImpl);
    this.name = paramString;
    isReadOnly(true);
  }
  
  public short getNodeType() { return 6; }
  
  public String getNodeName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
  }
  
  public void setNodeValue(String paramString) throws DOMException {
    if (this.ownerDocument.errorChecking && isReadOnly()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
      throw new DOMException((short)7, str);
    } 
  }
  
  public void setPrefix(String paramString) throws DOMException {
    if (this.ownerDocument.errorChecking && isReadOnly())
      throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
  }
  
  public Node cloneNode(boolean paramBoolean) {
    EntityImpl entityImpl = (EntityImpl)super.cloneNode(paramBoolean);
    entityImpl.setReadOnly(true, paramBoolean);
    return entityImpl;
  }
  
  public String getPublicId() {
    if (needsSyncData())
      synchronizeData(); 
    return this.publicId;
  }
  
  public String getSystemId() {
    if (needsSyncData())
      synchronizeData(); 
    return this.systemId;
  }
  
  public String getXmlVersion() {
    if (needsSyncData())
      synchronizeData(); 
    return this.version;
  }
  
  public String getXmlEncoding() {
    if (needsSyncData())
      synchronizeData(); 
    return this.encoding;
  }
  
  public String getNotationName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.notationName;
  }
  
  public void setPublicId(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    this.publicId = paramString;
  }
  
  public void setXmlEncoding(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    this.encoding = paramString;
  }
  
  public String getInputEncoding() {
    if (needsSyncData())
      synchronizeData(); 
    return this.inputEncoding;
  }
  
  public void setInputEncoding(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    this.inputEncoding = paramString;
  }
  
  public void setXmlVersion(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    this.version = paramString;
  }
  
  public void setSystemId(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    this.systemId = paramString;
  }
  
  public void setNotationName(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    this.notationName = paramString;
  }
  
  public String getBaseURI() {
    if (needsSyncData())
      synchronizeData(); 
    return (this.baseURI != null) ? this.baseURI : ((CoreDocumentImpl)getOwnerDocument()).getBaseURI();
  }
  
  public void setBaseURI(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    this.baseURI = paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\EntityImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */