package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.dom.CoreDOMImplementationImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.XSImplementation;
import com.sun.org.apache.xerces.internal.xs.XSLoader;
import org.w3c.dom.DOMImplementation;

public class XSImplementationImpl extends CoreDOMImplementationImpl implements XSImplementation {
  static XSImplementationImpl singleton = new XSImplementationImpl();
  
  public static DOMImplementation getDOMImplementation() { return singleton; }
  
  public boolean hasFeature(String paramString1, String paramString2) { return ((paramString1.equalsIgnoreCase("XS-Loader") && (paramString2 == null || paramString2.equals("1.0"))) || super.hasFeature(paramString1, paramString2)); }
  
  public XSLoader createXSLoader(StringList paramStringList) throws XSException {
    XSLoaderImpl xSLoaderImpl = new XSLoaderImpl();
    if (paramStringList == null)
      return xSLoaderImpl; 
    for (byte b = 0; b < paramStringList.getLength(); b++) {
      if (!paramStringList.item(b).equals("1.0")) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[] { paramStringList.item(b) });
        throw new XSException((short)1, str);
      } 
    } 
    return xSLoaderImpl;
  }
  
  public StringList getRecognizedVersions() { return new StringListImpl(new String[] { "1.0" }, 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\XSImplementationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */