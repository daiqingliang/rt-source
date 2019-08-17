package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class Envelope1_2Impl extends EnvelopeImpl {
  protected static final Logger log = Logger.getLogger(Envelope1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  
  public Envelope1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createEnvelope1_2Name(paramString)); }
  
  public Envelope1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString, boolean paramBoolean1, boolean paramBoolean2) throws SOAPException { super(paramSOAPDocumentImpl, NameImpl.createEnvelope1_2Name(paramString), paramBoolean1, paramBoolean2); }
  
  protected NameImpl getBodyName(String paramString) { return NameImpl.createBody1_2Name(paramString); }
  
  protected NameImpl getHeaderName(String paramString) { return NameImpl.createHeader1_2Name(paramString); }
  
  public void setEncodingStyle(String paramString) throws SOAPException {
    log.severe("SAAJ0404.ver1_2.no.encodingStyle.in.envelope");
    throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Envelope");
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
  
  public SOAPElement addChildElement(Name paramName) throws SOAPException {
    if (getBody() != null) {
      log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
      throw new SOAPExceptionImpl("Body must be the last element in SOAP Envelope");
    } 
    return super.addChildElement(paramName);
  }
  
  public SOAPElement addChildElement(QName paramQName) throws SOAPException {
    if (getBody() != null) {
      log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
      throw new SOAPExceptionImpl("Body must be the last element in SOAP Envelope");
    } 
    return super.addChildElement(paramQName);
  }
  
  public SOAPElement addTextNode(String paramString) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", getElementQName());
    throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Envelope is not legal");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\Envelope1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */