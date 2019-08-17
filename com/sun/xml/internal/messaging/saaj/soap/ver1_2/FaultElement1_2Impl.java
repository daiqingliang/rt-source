package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class FaultElement1_2Impl extends FaultElementImpl {
  public FaultElement1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl) { super(paramSOAPDocumentImpl, paramNameImpl); }
  
  public FaultElement1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
  
  public FaultElement1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createSOAP12Name(paramString)); }
  
  protected boolean isStandardFaultElement() {
    String str = this.elementQName.getLocalPart();
    return (str.equalsIgnoreCase("code") || str.equalsIgnoreCase("reason") || str.equalsIgnoreCase("node") || str.equalsIgnoreCase("role"));
  }
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException { return !isStandardFaultElement() ? (faultElement1_2Impl = new FaultElement1_2Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName)).replaceElementWithSOAPElement(this, faultElement1_2Impl) : super.setElementQName(paramQName); }
  
  public void setEncodingStyle(String paramString) throws SOAPException {
    log.severe("SAAJ0408.ver1_2.no.encodingStyle.in.fault.child");
    throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on a Fault child element");
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\FaultElement1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */