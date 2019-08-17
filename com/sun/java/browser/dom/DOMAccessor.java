package com.sun.java.browser.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public interface DOMAccessor {
  Document getDocument(Object paramObject) throws DOMException;
  
  DOMImplementation getDOMImplementation();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\browser\dom\DOMAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */