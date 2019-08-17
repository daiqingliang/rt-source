package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.transform.Source;

public final class XMLInputSourceAdaptor implements Source {
  public final XMLInputSource fSource;
  
  public XMLInputSourceAdaptor(XMLInputSource paramXMLInputSource) { this.fSource = paramXMLInputSource; }
  
  public void setSystemId(String paramString) { this.fSource.setSystemId(paramString); }
  
  public String getSystemId() {
    try {
      return XMLEntityManager.expandSystemId(this.fSource.getSystemId(), this.fSource.getBaseSystemId(), false);
    } catch (MalformedURIException malformedURIException) {
      return this.fSource.getSystemId();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLInputSourceAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */