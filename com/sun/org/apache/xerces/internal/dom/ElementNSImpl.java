package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public class ElementNSImpl extends ElementImpl {
  static final long serialVersionUID = -9142310625494392642L;
  
  static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
  
  protected String namespaceURI;
  
  protected String localName;
  
  XSTypeDefinition type;
  
  protected ElementNSImpl() {}
  
  protected ElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2) throws DOMException {
    super(paramCoreDocumentImpl, paramString2);
    setName(paramString1, paramString2);
  }
  
  private void setName(String paramString1, String paramString2) {
    this.namespaceURI = paramString1;
    if (paramString1 != null)
      this.namespaceURI = (paramString1.length() == 0) ? null : paramString1; 
    if (paramString2 == null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, str);
    } 
    int i = paramString2.indexOf(':');
    int j = paramString2.lastIndexOf(':');
    this.ownerDocument.checkNamespaceWF(paramString2, i, j);
    if (i < 0) {
      this.localName = paramString2;
      if (this.ownerDocument.errorChecking) {
        this.ownerDocument.checkQName(null, this.localName);
        if ((paramString2.equals("xmlns") && (paramString1 == null || !paramString1.equals(NamespaceContext.XMLNS_URI))) || (paramString1 != null && paramString1.equals(NamespaceContext.XMLNS_URI) && !paramString2.equals("xmlns"))) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str);
        } 
      } 
    } else {
      String str = paramString2.substring(0, i);
      this.localName = paramString2.substring(j + 1);
      if (this.ownerDocument.errorChecking) {
        if (paramString1 == null || (str.equals("xml") && !paramString1.equals(NamespaceContext.XML_URI))) {
          String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str1);
        } 
        this.ownerDocument.checkQName(str, this.localName);
        this.ownerDocument.checkDOMNSErr(str, paramString1);
      } 
    } 
  }
  
  protected ElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3) throws DOMException {
    super(paramCoreDocumentImpl, paramString2);
    this.localName = paramString3;
    this.namespaceURI = paramString1;
  }
  
  protected ElementNSImpl(CoreDocumentImpl paramCoreDocumentImpl, String paramString) { super(paramCoreDocumentImpl, paramString); }
  
  void rename(String paramString1, String paramString2) {
    if (needsSyncData())
      synchronizeData(); 
    this.name = paramString2;
    setName(paramString1, paramString2);
    reconcileDefaultAttributes();
  }
  
  protected void setValues(CoreDocumentImpl paramCoreDocumentImpl, String paramString1, String paramString2, String paramString3) throws DOMException {
    this.firstChild = null;
    this.previousSibling = null;
    this.nextSibling = null;
    this.fNodeListCache = null;
    this.attributes = null;
    this.flags = 0;
    setOwnerDocument(paramCoreDocumentImpl);
    needsSyncData(true);
    this.name = paramString2;
    this.localName = paramString3;
    this.namespaceURI = paramString1;
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
    if (this.ownerDocument.errorChecking) {
      if (isReadOnly()) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      } 
      if (paramString != null && paramString.length() != 0) {
        if (!CoreDocumentImpl.isXMLName(paramString, this.ownerDocument.isXML11Version())) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
          throw new DOMException((short)5, str);
        } 
        if (this.namespaceURI == null || paramString.indexOf(':') >= 0) {
          String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
          throw new DOMException((short)14, str);
        } 
        if (paramString.equals("xml") && !this.namespaceURI.equals("http://www.w3.org/XML/1998/namespace")) {
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
  
  public String getBaseURI() {
    if (needsSyncData())
      synchronizeData(); 
    if (this.attributes != null) {
      Attr attr = (Attr)this.attributes.getNamedItemNS("http://www.w3.org/XML/1998/namespace", "base");
      if (attr != null) {
        String str = attr.getNodeValue();
        if (str.length() != 0) {
          try {
            str = (new URI(str)).toString();
          } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
            NodeImpl nodeImpl = (parentNode() != null) ? parentNode() : this.ownerNode;
            String str3 = (nodeImpl != null) ? nodeImpl.getBaseURI() : null;
            if (str3 != null) {
              try {
                str = (new URI(new URI(str3), str)).toString();
              } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException1) {
                return null;
              } 
              return str;
            } 
            return null;
          } 
          return str;
        } 
      } 
    } 
    String str1 = (parentNode() != null) ? parentNode().getBaseURI() : null;
    if (str1 != null)
      try {
        return (new URI(str1)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
        return null;
      }  
    String str2 = (this.ownerNode != null) ? this.ownerNode.getBaseURI() : null;
    if (str2 != null)
      try {
        return (new URI(str2)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
        return null;
      }  
    return null;
  }
  
  public String getTypeName() {
    if (this.type != null) {
      if (this.type instanceof XSSimpleTypeDecl)
        return ((XSSimpleTypeDecl)this.type).getTypeName(); 
      if (this.type instanceof XSComplexTypeDecl)
        return ((XSComplexTypeDecl)this.type).getTypeName(); 
    } 
    return null;
  }
  
  public String getTypeNamespace() { return (this.type != null) ? this.type.getNamespace() : null; }
  
  public boolean isDerivedFrom(String paramString1, String paramString2, int paramInt) {
    if (needsSyncData())
      synchronizeData(); 
    if (this.type != null) {
      if (this.type instanceof XSSimpleTypeDecl)
        return ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(paramString1, paramString2, paramInt); 
      if (this.type instanceof XSComplexTypeDecl)
        return ((XSComplexTypeDecl)this.type).isDOMDerivedFrom(paramString1, paramString2, paramInt); 
    } 
    return false;
  }
  
  public void setType(XSTypeDefinition paramXSTypeDefinition) { this.type = paramXSTypeDefinition; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\ElementNSImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */