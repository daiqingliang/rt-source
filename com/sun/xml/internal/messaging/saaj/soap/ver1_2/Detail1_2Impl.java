package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class Detail1_2Impl extends DetailImpl {
  protected static final Logger log = Logger.getLogger(Detail1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  
  public Detail1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createSOAP12Name("Detail", paramString)); }
  
  public Detail1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl) { super(paramSOAPDocumentImpl, NameImpl.createSOAP12Name("Detail")); }
  
  protected DetailEntry createDetailEntry(Name paramName) { return new DetailEntry1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramName); }
  
  protected DetailEntry createDetailEntry(QName paramQName) { return new DetailEntry1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName); }
  
  public void setEncodingStyle(String paramString) throws SOAPException {
    log.severe("SAAJ0403.ver1_2.no.encodingStyle.in.detail");
    throw new SOAPExceptionImpl("EncodingStyle attribute cannot appear in Detail");
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\Detail1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */