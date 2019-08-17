package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Notation;

public class NotationImpl extends NodeImpl implements Notation {
  static final long serialVersionUID = -764632195890658402L;
  
  protected String name;
  
  protected String publicId;
  
  protected String systemId;
  
  protected String baseURI;
  
  public NotationImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) {
    super(paramCoreDocumentImpl);
    this.name = paramString;
  }
  
  public short getNodeType() { return 12; }
  
  public String getNodeName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.name;
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
  
  public void setPublicId(String paramString) {
    if (isReadOnly())
      throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
    if (needsSyncData())
      synchronizeData(); 
    this.publicId = paramString;
  }
  
  public void setSystemId(String paramString) {
    if (isReadOnly())
      throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null)); 
    if (needsSyncData())
      synchronizeData(); 
    this.systemId = paramString;
  }
  
  public String getBaseURI() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.baseURI != null && this.baseURI.length() != 0)
      try {
        return (new URI(this.baseURI)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
        return null;
      }  
    return this.baseURI;
  }
  
  public void setBaseURI(String paramString) {
    if (needsSyncData())
      synchronizeData(); 
    this.baseURI = paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\NotationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */