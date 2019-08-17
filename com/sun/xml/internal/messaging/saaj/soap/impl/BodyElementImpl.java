package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public abstract class BodyElementImpl extends ElementImpl implements SOAPBodyElement {
  public BodyElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName) { super(paramSOAPDocumentImpl, paramName); }
  
  public BodyElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
  
  public void setParentElement(SOAPElement paramSOAPElement) throws SOAPException {
    if (!(paramSOAPElement instanceof javax.xml.soap.SOAPBody)) {
      log.severe("SAAJ0101.impl.parent.of.body.elem.mustbe.body");
      throw new SOAPException("Parent of a SOAPBodyElement has to be a SOAPBody");
    } 
    super.setParentElement(paramSOAPElement);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\BodyElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */