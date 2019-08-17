package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.soap.SOAPException;

public class Envelope1_1Impl extends EnvelopeImpl {
  public Envelope1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createEnvelope1_1Name(paramString)); }
  
  Envelope1_1Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString, boolean paramBoolean1, boolean paramBoolean2) throws SOAPException { super(paramSOAPDocumentImpl, NameImpl.createEnvelope1_1Name(paramString), paramBoolean1, paramBoolean2); }
  
  protected NameImpl getBodyName(String paramString) { return NameImpl.createBody1_1Name(paramString); }
  
  protected NameImpl getHeaderName(String paramString) { return NameImpl.createHeader1_1Name(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_1\Envelope1_1Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */