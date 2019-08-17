package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;
import javax.xml.transform.SourceLocator;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class SAXSourceLocator extends LocatorImpl implements SourceLocator, Serializable {
  static final long serialVersionUID = 3181680946321164112L;
  
  Locator m_locator;
  
  public SAXSourceLocator() {}
  
  public SAXSourceLocator(Locator paramLocator) {
    this.m_locator = paramLocator;
    setColumnNumber(paramLocator.getColumnNumber());
    setLineNumber(paramLocator.getLineNumber());
    setPublicId(paramLocator.getPublicId());
    setSystemId(paramLocator.getSystemId());
  }
  
  public SAXSourceLocator(SourceLocator paramSourceLocator) {
    this.m_locator = null;
    setColumnNumber(paramSourceLocator.getColumnNumber());
    setLineNumber(paramSourceLocator.getLineNumber());
    setPublicId(paramSourceLocator.getPublicId());
    setSystemId(paramSourceLocator.getSystemId());
  }
  
  public SAXSourceLocator(SAXParseException paramSAXParseException) {
    setLineNumber(paramSAXParseException.getLineNumber());
    setColumnNumber(paramSAXParseException.getColumnNumber());
    setPublicId(paramSAXParseException.getPublicId());
    setSystemId(paramSAXParseException.getSystemId());
  }
  
  public String getPublicId() { return (null == this.m_locator) ? super.getPublicId() : this.m_locator.getPublicId(); }
  
  public String getSystemId() { return (null == this.m_locator) ? super.getSystemId() : this.m_locator.getSystemId(); }
  
  public int getLineNumber() { return (null == this.m_locator) ? super.getLineNumber() : this.m_locator.getLineNumber(); }
  
  public int getColumnNumber() { return (null == this.m_locator) ? super.getColumnNumber() : this.m_locator.getColumnNumber(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\SAXSourceLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */