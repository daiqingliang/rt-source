package com.sun.org.apache.xerces.internal.dom;

import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementationSource;

public class DOMImplementationSourceImpl implements DOMImplementationSource {
  public DOMImplementation getDOMImplementation(String paramString) {
    DOMImplementation dOMImplementation = CoreDOMImplementationImpl.getDOMImplementation();
    if (testImpl(dOMImplementation, paramString))
      return dOMImplementation; 
    dOMImplementation = DOMImplementationImpl.getDOMImplementation();
    return testImpl(dOMImplementation, paramString) ? dOMImplementation : null;
  }
  
  public DOMImplementationList getDOMImplementationList(String paramString) {
    DOMImplementation dOMImplementation = CoreDOMImplementationImpl.getDOMImplementation();
    Vector vector = new Vector();
    if (testImpl(dOMImplementation, paramString))
      vector.addElement(dOMImplementation); 
    dOMImplementation = DOMImplementationImpl.getDOMImplementation();
    if (testImpl(dOMImplementation, paramString))
      vector.addElement(dOMImplementation); 
    return new DOMImplementationListImpl(vector);
  }
  
  boolean testImpl(DOMImplementation paramDOMImplementation, String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    String str1 = null;
    String str2 = null;
    if (stringTokenizer.hasMoreTokens())
      str1 = stringTokenizer.nextToken(); 
    while (str1 != null) {
      boolean bool = false;
      if (stringTokenizer.hasMoreTokens()) {
        str2 = stringTokenizer.nextToken();
        char c = str2.charAt(0);
        switch (c) {
          case '0':
          case '1':
          case '2':
          case '3':
          case '4':
          case '5':
          case '6':
          case '7':
          case '8':
          case '9':
            bool = true;
            break;
        } 
      } else {
        str2 = null;
      } 
      if (bool) {
        if (!paramDOMImplementation.hasFeature(str1, str2))
          return false; 
        if (stringTokenizer.hasMoreTokens()) {
          str1 = stringTokenizer.nextToken();
          continue;
        } 
        str1 = null;
        continue;
      } 
      if (!paramDOMImplementation.hasFeature(str1, null))
        return false; 
      str1 = str2;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMImplementationSourceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */