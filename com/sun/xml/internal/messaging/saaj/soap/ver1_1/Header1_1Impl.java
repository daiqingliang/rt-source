package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

public class Header1_1Impl extends HeaderImpl {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
  
  public Header1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createHeader1_1Name(paramString)); }
  
  protected NameImpl getNotUnderstoodName() {
    log.log(Level.SEVERE, "SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", new String[] { "getNotUnderstoodName" });
    throw new UnsupportedOperationException("Not supported by SOAP 1.1");
  }
  
  protected NameImpl getUpgradeName() { return NameImpl.create("Upgrade", getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/"); }
  
  protected NameImpl getSupportedEnvelopeName() { return NameImpl.create("SupportedEnvelope", getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/"); }
  
  public SOAPHeaderElement addNotUnderstoodHeaderElement(QName paramQName) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", new String[] { "addNotUnderstoodHeaderElement" });
    throw new UnsupportedOperationException("Not supported by SOAP 1.1");
  }
  
  protected SOAPHeaderElement createHeaderElement(Name paramName) { return new HeaderElement1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramName); }
  
  protected SOAPHeaderElement createHeaderElement(QName paramQName) throws SOAPException { return new HeaderElement1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\Header1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */