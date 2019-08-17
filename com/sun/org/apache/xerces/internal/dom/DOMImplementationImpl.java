package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class DOMImplementationImpl extends CoreDOMImplementationImpl implements DOMImplementation {
  static DOMImplementationImpl singleton = new DOMImplementationImpl();
  
  public static DOMImplementation getDOMImplementation() { return singleton; }
  
  public boolean hasFeature(String paramString1, String paramString2) {
    boolean bool = super.hasFeature(paramString1, paramString2);
    if (!bool) {
      boolean bool1 = (paramString2 == null || paramString2.length() == 0) ? 1 : 0;
      if (paramString1.startsWith("+"))
        paramString1 = paramString1.substring(1); 
      return ((paramString1.equalsIgnoreCase("Events") && (bool1 || paramString2.equals("2.0"))) || (paramString1.equalsIgnoreCase("MutationEvents") && (bool1 || paramString2.equals("2.0"))) || (paramString1.equalsIgnoreCase("Traversal") && (bool1 || paramString2.equals("2.0"))) || (paramString1.equalsIgnoreCase("Range") && (bool1 || paramString2.equals("2.0"))) || (paramString1.equalsIgnoreCase("MutationEvents") && (bool1 || paramString2.equals("2.0"))));
    } 
    return bool;
  }
  
  public Document createDocument(String paramString1, String paramString2, DocumentType paramDocumentType) throws DOMException {
    if (paramString1 == null && paramString2 == null && paramDocumentType == null)
      return new DocumentImpl(); 
    if (paramDocumentType != null && paramDocumentType.getOwnerDocument() != null) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, str);
    } 
    DocumentImpl documentImpl = new DocumentImpl(paramDocumentType);
    Element element = documentImpl.createElementNS(paramString1, paramString2);
    documentImpl.appendChild(element);
    return documentImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMImplementationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */