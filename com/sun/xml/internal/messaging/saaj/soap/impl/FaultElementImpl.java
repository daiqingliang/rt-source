package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFaultElement;

public abstract class FaultElementImpl extends ElementImpl implements SOAPFaultElement {
  protected FaultElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl) { super(paramSOAPDocumentImpl, paramNameImpl); }
  
  protected FaultElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
  
  protected abstract boolean isStandardFaultElement();
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\FaultElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */