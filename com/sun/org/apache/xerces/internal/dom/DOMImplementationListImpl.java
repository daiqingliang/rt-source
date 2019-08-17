package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMImplementationListImpl implements DOMImplementationList {
  private Vector fImplementations = new Vector();
  
  public DOMImplementationListImpl() {}
  
  public DOMImplementationListImpl(Vector paramVector) {}
  
  public DOMImplementation item(int paramInt) {
    try {
      return (DOMImplementation)this.fImplementations.elementAt(paramInt);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return null;
    } 
  }
  
  public int getLength() { return this.fImplementations.size(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMImplementationListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */