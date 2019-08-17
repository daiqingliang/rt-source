package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailEntryImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

public class DetailEntry1_2Impl extends DetailEntryImpl {
  public DetailEntry1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName) { super(paramSOAPDocumentImpl, paramName); }
  
  public DetailEntry1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException {
    DetailEntry1_2Impl detailEntry1_2Impl;
    return (detailEntry1_2Impl = new DetailEntry1_2Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName)).replaceElementWithSOAPElement(this, detailEntry1_2Impl);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\DetailEntry1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */