package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.w3c.dom.DOMException;

public class AttrNSImpl extends AttrImpl {
  static final long serialVersionUID = -781906615369795414L;
  
  static final String xmlnsURI = "http://www.w3.org/2000/xmlns/";
  
  static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
  
  protected String namespaceURI;
  
  protected String localName;
  
  public AttrNSImpl() {}
  
  protected AttrNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2) {
    super(paramCoreDocumentImpl, paramString2);
    setName(paramString1, paramString2);
  }
  
  private void setName(String paramString1, String paramString2) {
    CoreDocumentImpl coreDocumentImpl = ownerDocument();
    this.namespaceURI = paramString1;
    if (paramString1 != null)
      this.namespaceURI = (paramString1.length() == 0) ? null : paramString1; 
    int i = paramString2.indexOf(':');
    int j = paramString2.lastIndexOf(':');
    coreDocumentImpl.checkNamespaceWF(paramString2, i, j);
    if (i < 0) {
      this.localName = paramString2;
      if (coreDocumentImpl.errorChecking) {
        coreDocumentImpl.checkQName(null, this.localName);
        if ((paramString2.equals("xmlns") && (paramString1 == null || !paramString1.equals(NamespaceContext.XMLNS_URI))) || (paramString1 != null && paramString1.equals(NamespaceContext.XMLNS_URI) && !paramString2.equals("xmlns"))) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str);
        } 
      } 
    } else {
      String str = paramString2.substring(0, i);
      this.localName = paramString2.substring(j + 1);
      coreDocumentImpl.checkQName(str, this.localName);
      coreDocumentImpl.checkDOMNSErr(str, paramString1);
    } 
  }
  
  public AttrNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3) {
    super(paramCoreDocumentImpl, paramString2);
    this.localName = paramString3;
    this.namespaceURI = paramString1;
  }
  
  protected AttrNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) { super(paramCoreDocumentImpl, paramString); }
  
  void rename(String paramString1, String paramString2) {
    if (needsSyncData())
      synchronizeData(); 
    this.name = paramString2;
    setName(paramString1, paramString2);
  }
  
  public void setValues(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3) {
    this.textNode = null;
    this.flags = 0;
    isSpecified(true);
    hasStringValue(true);
    setOwnerDocument(paramCoreDocumentImpl);
    this.localName = paramString3;
    this.namespaceURI = paramString1;
    this.name = paramString2;
    this.value = null;
  }
  
  public String getNamespaceURI() {
    if (needsSyncData())
      synchronizeData(); 
    return this.namespaceURI;
  }
  
  public String getPrefix() {
    if (needsSyncData())
      synchronizeData(); 
    int i = this.name.indexOf(':');
    return (i < 0) ? null : this.name.substring(0, i);
  }
  
  public void setPrefix(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    if ((ownerDocument()).errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramString != null && paramString.length() != 0) {
        if (!CoreDocumentImpl.isXMLName(paramString, ownerDocument().isXML11Version())) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
          throw new DOMException((short)5, str);
        } 
        if (this.namespaceURI == null || paramString.indexOf(':') >= 0) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str);
        } 
        if (paramString.equals("xmlns")) {
          if (!this.namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
            String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, str);
          } 
        } else if (paramString.equals("xml")) {
          if (!this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
            String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException((short)14, str);
          } 
        } else if (this.name.equals("xmlns")) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str);
        } 
      } 
    } 
    if (paramString != null && paramString.length() != 0) {
      this.name = paramString + ":" + this.localName;
    } else {
      this.name = this.localName;
    } 
  }
  
  public String getLocalName() {
    if (needsSyncData())
      synchronizeData(); 
    return this.localName;
  }
  
  public String getTypeName() { return (this.type != null) ? ((this.type instanceof XSSimpleTypeDecl) ? ((XSSimpleTypeDecl)this.type).getName() : (String)this.type) : null; }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt) { return (this.type != null && this.type instanceof XSSimpleTypeDecl) ? ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(paramString1, paramString2, paramInt) : 0; }
  
  public String getTypeNamespace() { return (this.type != null) ? ((this.type instanceof XSSimpleTypeDecl) ? ((XSSimpleTypeDecl)this.type).getNamespace() : "http://www.w3.org/TR/REC-xml") : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\AttrNSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */