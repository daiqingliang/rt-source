package com.sun.xml.internal.ws.api;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.ws.Service;

public abstract class WSDLLocator {
  public abstract URL locateWSDL(Class<Service> paramClass, String paramString) throws MalformedURLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\WSDLLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */