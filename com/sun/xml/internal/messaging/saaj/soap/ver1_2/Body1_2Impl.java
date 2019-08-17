package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Node;

public class Body1_2Impl extends BodyImpl {
  protected static final Logger log = Logger.getLogger(Body1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  
  public Body1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createBody1_2Name(paramString)); }
  
  protected NameImpl getFaultName(String paramString) { return NameImpl.createFault1_2Name(paramString, null); }
  
  protected SOAPBodyElement createBodyElement(Name paramName) { return new BodyElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramName); }
  
  protected SOAPBodyElement createBodyElement(QName paramQName) { return new BodyElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName); }
  
  protected QName getDefaultFaultCode() { return SOAPConstants.SOAP_RECEIVER_FAULT; }
  
  public SOAPFault addFault() throws SOAPException {
    if (hasAnyChildElement()) {
      log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
      throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
    } 
    return super.addFault();
  }
  
  public void setEncodingStyle(String paramString) throws SOAPException {
    log.severe("SAAJ0401.ver1_2.no.encodingstyle.in.body");
    throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Body");
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
  
  protected boolean isFault(SOAPElement paramSOAPElement) { return (paramSOAPElement.getElementName().getURI().equals("http://www.w3.org/2003/05/soap-envelope") && paramSOAPElement.getElementName().getLocalName().equals("Fault")); }
  
  protected SOAPFault createFaultElement() throws SOAPException { return new Fault1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), getPrefix()); }
  
  public SOAPBodyElement addBodyElement(Name paramName) {
    if (hasFault()) {
      log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
      throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
    } 
    return super.addBodyElement(paramName);
  }
  
  public SOAPBodyElement addBodyElement(QName paramQName) {
    if (hasFault()) {
      log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
      throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
    } 
    return super.addBodyElement(paramQName);
  }
  
  protected SOAPElement addElement(Name paramName) throws SOAPException {
    if (hasFault()) {
      log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
      throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
    } 
    return super.addElement(paramName);
  }
  
  protected SOAPElement addElement(QName paramQName) throws SOAPException {
    if (hasFault()) {
      log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
      throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
    } 
    return super.addElement(paramQName);
  }
  
  public SOAPElement addChildElement(Name paramName) throws SOAPException {
    if (hasFault()) {
      log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
      throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
    } 
    return super.addChildElement(paramName);
  }
  
  public SOAPElement addChildElement(QName paramQName) throws SOAPException {
    if (hasFault()) {
      log.severe("SAAJ0402.ver1_2.only.fault.allowed.in.body");
      throw new SOAPExceptionImpl("No other element except Fault allowed in SOAPBody");
    } 
    return super.addChildElement(paramQName);
  }
  
  private boolean hasAnyChildElement() {
    for (Node node = getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeType() == 1)
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\Body1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */