package com.sun.xml.internal.messaging.saaj.client.p2p;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;

public class HttpSOAPConnectionFactory extends SOAPConnectionFactory {
  public SOAPConnection createConnection() throws SOAPException { return new HttpSOAPConnection(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\client\p2p\HttpSOAPConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */