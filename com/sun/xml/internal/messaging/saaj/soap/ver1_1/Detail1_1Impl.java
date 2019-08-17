package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;

public class Detail1_1Impl extends DetailImpl {
  public Detail1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createDetail1_1Name(paramString)); }
  
  public Detail1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl) { super(paramSOAPDocumentImpl, NameImpl.createDetail1_1Name()); }
  
  protected DetailEntry createDetailEntry(Name paramName) { return new DetailEntry1_1Impl((SOAPDocumentImpl)getOwnerDocument(), paramName); }
  
  protected DetailEntry createDetailEntry(QName paramQName) { return new DetailEntry1_1Impl((SOAPDocumentImpl)getOwnerDocument(), paramQName); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\Detail1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */