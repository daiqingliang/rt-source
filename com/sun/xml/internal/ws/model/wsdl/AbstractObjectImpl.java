package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

abstract class AbstractObjectImpl implements WSDLObject {
  private final int lineNumber;
  
  private final String systemId;
  
  AbstractObjectImpl(XMLStreamReader paramXMLStreamReader) {
    Location location = paramXMLStreamReader.getLocation();
    this.lineNumber = location.getLineNumber();
    this.systemId = location.getSystemId();
  }
  
  AbstractObjectImpl(String paramString, int paramInt) {
    this.systemId = paramString;
    this.lineNumber = paramInt;
  }
  
  @NotNull
  public final Locator getLocation() {
    LocatorImpl locatorImpl = new LocatorImpl();
    locatorImpl.setSystemId(this.systemId);
    locatorImpl.setLineNumber(this.lineNumber);
    return locatorImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\AbstractObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */