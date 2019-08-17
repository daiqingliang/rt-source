package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMXSImplementationSourceImpl extends DOMImplementationSourceImpl {
  public DOMImplementation getDOMImplementation(String paramString) {
    DOMImplementation dOMImplementation = super.getDOMImplementation(paramString);
    if (dOMImplementation != null)
      return dOMImplementation; 
    dOMImplementation = PSVIDOMImplementationImpl.getDOMImplementation();
    if (testImpl(dOMImplementation, paramString))
      return dOMImplementation; 
    dOMImplementation = XSImplementationImpl.getDOMImplementation();
    return testImpl(dOMImplementation, paramString) ? dOMImplementation : null;
  }
  
  public DOMImplementationList getDOMImplementationList(String paramString) {
    Vector vector = new Vector();
    DOMImplementationList dOMImplementationList = super.getDOMImplementationList(paramString);
    for (byte b = 0; b < dOMImplementationList.getLength(); b++)
      vector.addElement(dOMImplementationList.item(b)); 
    DOMImplementation dOMImplementation = PSVIDOMImplementationImpl.getDOMImplementation();
    if (testImpl(dOMImplementation, paramString))
      vector.addElement(dOMImplementation); 
    dOMImplementation = XSImplementationImpl.getDOMImplementation();
    if (testImpl(dOMImplementation, paramString))
      vector.addElement(dOMImplementation); 
    return new DOMImplementationListImpl(vector);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMXSImplementationSourceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */