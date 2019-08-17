package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

public class Header1_2Impl extends HeaderImpl {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_2", "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  
  public Header1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createHeader1_2Name(paramString)); }
  
  protected NameImpl getNotUnderstoodName() { return NameImpl.createNotUnderstood1_2Name(null); }
  
  protected NameImpl getUpgradeName() { return NameImpl.createUpgrade1_2Name(null); }
  
  protected NameImpl getSupportedEnvelopeName() { return NameImpl.createSupportedEnvelope1_2Name(null); }
  
  public SOAPHeaderElement addNotUnderstoodHeaderElement(QName paramQName) throws SOAPException {
    if (paramQName == null) {
      log.severe("SAAJ0410.ver1_2.no.null.to.addNotUnderstoodHeader");
      throw new SOAPException("Cannot pass NULL to addNotUnderstoodHeaderElement");
    } 
    if ("".equals(paramQName.getNamespaceURI())) {
      log.severe("SAAJ0417.ver1_2.qname.not.ns.qualified");
      throw new SOAPException("The qname passed to addNotUnderstoodHeaderElement must be namespace-qualified");
    } 
    String str = paramQName.getPrefix();
    if ("".equals(str))
      str = "ns1"; 
    NameImpl nameImpl = getNotUnderstoodName();
    SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)addChildElement(nameImpl);
    sOAPHeaderElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), getQualifiedName(new QName(paramQName.getNamespaceURI(), paramQName.getLocalPart(), str)));
    sOAPHeaderElement.addNamespaceDeclaration(str, paramQName.getNamespaceURI());
    return sOAPHeaderElement;
  }
  
  public SOAPElement addTextNode(String paramString) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", getElementQName());
    throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Header is not legal");
  }
  
  protected SOAPHeaderElement createHeaderElement(Name paramName) throws SOAPException {
    String str = paramName.getURI();
    if (str == null || str.equals("")) {
      log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
      throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
    } 
    return new HeaderElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramName);
  }
  
  protected SOAPHeaderElement createHeaderElement(QName paramQName) throws SOAPException {
    String str = paramQName.getNamespaceURI();
    if (str == null || str.equals("")) {
      log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
      throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
    } 
    return new HeaderElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName);
  }
  
  public void setEncodingStyle(String paramString) throws SOAPException {
    log.severe("SAAJ0409.ver1_2.no.encodingstyle.in.header");
    throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Header");
  }
  
  public SOAPElement addAttribute(Name paramName, String paramString) throws SOAPException {
    if (paramName.getLocalName().equals("encodingStyle") && paramName.getURI().equals("http://www.w3.org/2003/05/soap-envelope"))
      setEncodingStyle(paramString); 
    return super.addAttribute(paramName, paramString);
  }
  
  public SOAPElement addAttribute(QName paramQName, String paramString) throws SOAPException {
    if (paramQName.getLocalPart().equals("encodingStyle") && paramQName.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope"))
      setEncodingStyle(paramString); 
    return super.addAttribute(paramQName, paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\Header1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */