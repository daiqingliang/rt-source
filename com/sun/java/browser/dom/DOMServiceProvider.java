package com.sun.java.browser.dom;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public abstract class DOMServiceProvider {
  public abstract boolean canHandle(Object paramObject);
  
  public abstract Document getDocument(Object paramObject) throws DOMUnsupportedException;
  
  public abstract DOMImplementation getDOMImplementation();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\browser\dom\DOMServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */