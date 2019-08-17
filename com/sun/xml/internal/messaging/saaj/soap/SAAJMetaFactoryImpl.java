package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.soap.dynamic.SOAPFactoryDynamicImpl;
import com.sun.xml.internal.messaging.saaj.soap.dynamic.SOAPMessageFactoryDynamicImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPFactory1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SAAJMetaFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public class SAAJMetaFactoryImpl extends SAAJMetaFactory {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  protected MessageFactory newMessageFactory(String paramString) throws SOAPException {
    if ("SOAP 1.1 Protocol".equals(paramString))
      return new SOAPMessageFactory1_1Impl(); 
    if ("SOAP 1.2 Protocol".equals(paramString))
      return new SOAPMessageFactory1_2Impl(); 
    if ("Dynamic Protocol".equals(paramString))
      return new SOAPMessageFactoryDynamicImpl(); 
    log.log(Level.SEVERE, "SAAJ0569.soap.unknown.protocol", new Object[] { paramString, "MessageFactory" });
    throw new SOAPException("Unknown Protocol: " + paramString + "  specified for creating MessageFactory");
  }
  
  protected SOAPFactory newSOAPFactory(String paramString) throws SOAPException {
    if ("SOAP 1.1 Protocol".equals(paramString))
      return new SOAPFactory1_1Impl(); 
    if ("SOAP 1.2 Protocol".equals(paramString))
      return new SOAPFactory1_2Impl(); 
    if ("Dynamic Protocol".equals(paramString))
      return new SOAPFactoryDynamicImpl(); 
    log.log(Level.SEVERE, "SAAJ0569.soap.unknown.protocol", new Object[] { paramString, "SOAPFactory" });
    throw new SOAPException("Unknown Protocol: " + paramString + "  specified for creating SOAPFactory");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\SAAJMetaFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */