package com.sun.xml.internal.messaging.saaj.soap.dynamic;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;

public class SOAPFactoryDynamicImpl extends SOAPFactoryImpl {
  protected SOAPDocumentImpl createDocument() { return null; }
  
  public Detail createDetail() throws SOAPException { throw new UnsupportedOperationException("createDetail() not supported for Dynamic Protocol"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\dynamic\SOAPFactoryDynamicImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */