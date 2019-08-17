package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class HeaderElement1_1Impl extends HeaderElementImpl {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
  
  public HeaderElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName) { super(paramSOAPDocumentImpl, paramName); }
  
  public HeaderElement1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException {
    HeaderElement1_1Impl headerElement1_1Impl;
    return (headerElement1_1Impl = new HeaderElement1_1Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName)).replaceElementWithSOAPElement(this, headerElement1_1Impl);
  }
  
  protected NameImpl getActorAttributeName() { return NameImpl.create("actor", null, "http://schemas.xmlsoap.org/soap/envelope/"); }
  
  protected NameImpl getRoleAttributeName() {
    log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Role" });
    throw new UnsupportedOperationException("Role not supported by SOAP 1.1");
  }
  
  protected NameImpl getMustunderstandAttributeName() { return NameImpl.create("mustUnderstand", null, "http://schemas.xmlsoap.org/soap/envelope/"); }
  
  protected String getMustunderstandLiteralValue(boolean paramBoolean) { return (paramBoolean == true) ? "1" : "0"; }
  
  protected boolean getMustunderstandAttributeValue(String paramString) { return ("1".equals(paramString) || "true".equalsIgnoreCase(paramString)); }
  
  protected NameImpl getRelayAttributeName() {
    log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Relay" });
    throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
  }
  
  protected String getRelayLiteralValue(boolean paramBoolean) {
    log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Relay" });
    throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
  }
  
  protected boolean getRelayAttributeValue(String paramString) {
    log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Relay" });
    throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
  }
  
  protected String getActorOrRole() { return getActor(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\HeaderElement1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */