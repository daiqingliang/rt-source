package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.net.URL;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

public interface LocatorEx extends Locator {
  ValidationEventLocator getLocation();
  
  public static final class Snapshot implements LocatorEx, ValidationEventLocator {
    private final int columnNumber;
    
    private final int lineNumber;
    
    private final int offset;
    
    private final String systemId;
    
    private final String publicId;
    
    private final URL url;
    
    private final Object object;
    
    private final Node node;
    
    public Snapshot(LocatorEx param1LocatorEx) {
      this.columnNumber = param1LocatorEx.getColumnNumber();
      this.lineNumber = param1LocatorEx.getLineNumber();
      this.systemId = param1LocatorEx.getSystemId();
      this.publicId = param1LocatorEx.getPublicId();
      ValidationEventLocator validationEventLocator = param1LocatorEx.getLocation();
      this.offset = validationEventLocator.getOffset();
      this.url = validationEventLocator.getURL();
      this.object = validationEventLocator.getObject();
      this.node = validationEventLocator.getNode();
    }
    
    public Object getObject() { return this.object; }
    
    public Node getNode() { return this.node; }
    
    public int getOffset() { return this.offset; }
    
    public URL getURL() { return this.url; }
    
    public int getColumnNumber() { return this.columnNumber; }
    
    public int getLineNumber() { return this.lineNumber; }
    
    public String getSystemId() { return this.systemId; }
    
    public String getPublicId() { return this.publicId; }
    
    public ValidationEventLocator getLocation() { return this; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\LocatorEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */