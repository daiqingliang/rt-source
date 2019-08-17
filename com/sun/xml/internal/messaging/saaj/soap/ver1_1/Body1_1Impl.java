package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;

public class Body1_1Impl extends BodyImpl {
  public Body1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createBody1_1Name(paramString)); }
  
  public SOAPFault addSOAP12Fault(QName paramQName, String paramString, Locale paramLocale) { throw new UnsupportedOperationException("Not supported in SOAP 1.1"); }
  
  protected NameImpl getFaultName(String paramString) { return NameImpl.createFault1_1Name(null); }
  
  protected SOAPBodyElement createBodyElement(Name paramName) { return new BodyElement1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramName); }
  
  protected SOAPBodyElement createBodyElement(QName paramQName) { return new BodyElement1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName); }
  
  protected QName getDefaultFaultCode() { return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server"); }
  
  protected boolean isFault(SOAPElement paramSOAPElement) { return paramSOAPElement.getElementName().equals(getFaultName(null)); }
  
  protected SOAPFault createFaultElement() { return new Fault1_1Impl(((SOAPDocument)getOwnerDocument()).getDocument(), getPrefix()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\Body1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */