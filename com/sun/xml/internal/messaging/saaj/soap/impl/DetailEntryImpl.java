package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;

public abstract class DetailEntryImpl extends ElementImpl implements DetailEntry {
  public DetailEntryImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName) { super(paramSOAPDocumentImpl, paramName); }
  
  public DetailEntryImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\DetailEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */