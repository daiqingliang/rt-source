package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class ClientTransportException extends JAXWSExceptionBase {
  public ClientTransportException(Localizable paramLocalizable) { super(paramLocalizable); }
  
  public ClientTransportException(Localizable paramLocalizable, Throwable paramThrowable) { super(paramLocalizable, paramThrowable); }
  
  public ClientTransportException(Throwable paramThrowable) { super(paramThrowable); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.client"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\ClientTransportException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */