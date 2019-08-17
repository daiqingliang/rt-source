package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMImplementation;

public class DeferredDOMImplementationImpl extends DOMImplementationImpl {
  static DeferredDOMImplementationImpl singleton = new DeferredDOMImplementationImpl();
  
  public static DOMImplementation getDOMImplementation() { return singleton; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredDOMImplementationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */