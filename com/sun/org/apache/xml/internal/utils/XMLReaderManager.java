package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import java.util.HashMap;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLReaderManager {
  private static final XMLReaderManager m_singletonManager = new XMLReaderManager();
  
  private static final String property = "org.xml.sax.driver";
  
  private ThreadLocal<ReaderWrapper> m_readers;
  
  private boolean m_overrideDefaultParser;
  
  private HashMap m_inUse;
  
  private boolean _secureProcessing;
  
  private String _accessExternalDTD = "all";
  
  private XMLSecurityManager _xmlSecurityManager;
  
  public static XMLReaderManager getInstance(boolean paramBoolean) {
    m_singletonManager.setOverrideDefaultParser(paramBoolean);
    return m_singletonManager;
  }
  
  public XMLReader getXMLReader() throws SAXException {
    if (this.m_readers == null)
      this.m_readers = new ThreadLocal(); 
    if (this.m_inUse == null)
      this.m_inUse = new HashMap(); 
    ReaderWrapper readerWrapper = (ReaderWrapper)this.m_readers.get();
    boolean bool = (readerWrapper != null) ? 1 : 0;
    XMLReader xMLReader = bool ? readerWrapper.reader : null;
    String str1 = SecuritySupport.getSystemProperty("org.xml.sax.driver");
    if (bool && this.m_inUse.get(xMLReader) != Boolean.TRUE && readerWrapper.overrideDefaultParser == this.m_overrideDefaultParser && (str1 == null || xMLReader.getClass().getName().equals(str1))) {
      this.m_inUse.put(xMLReader, Boolean.TRUE);
    } else {
      xMLReader = JdkXmlUtils.getXMLReader(this.m_overrideDefaultParser, this._secureProcessing);
      if (!bool) {
        this.m_readers.set(new ReaderWrapper(xMLReader, this.m_overrideDefaultParser));
        this.m_inUse.put(xMLReader, Boolean.TRUE);
      } 
    } 
    try {
      xMLReader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", this._accessExternalDTD);
    } catch (SAXException sAXException) {
      XMLSecurityManager.printWarning(xMLReader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", sAXException);
    } 
    String str2 = "";
    try {
      if (this._xmlSecurityManager != null) {
        for (XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
          str2 = limit.apiProperty();
          xMLReader.setProperty(str2, this._xmlSecurityManager.getLimitValueAsString(limit));
        } 
        if (this._xmlSecurityManager.printEntityCountInfo()) {
          str2 = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
          xMLReader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
        } 
      } 
    } catch (SAXException sAXException) {
      XMLSecurityManager.printWarning(xMLReader.getClass().getName(), str2, sAXException);
    } 
    return xMLReader;
  }
  
  public void releaseXMLReader(XMLReader paramXMLReader) {
    ReaderWrapper readerWrapper = (ReaderWrapper)this.m_readers.get();
    if (readerWrapper.reader == paramXMLReader && paramXMLReader != null)
      this.m_inUse.remove(paramXMLReader); 
  }
  
  public boolean overrideDefaultParser() { return this.m_overrideDefaultParser; }
  
  public void setOverrideDefaultParser(boolean paramBoolean) { this.m_overrideDefaultParser = paramBoolean; }
  
  public void setFeature(String paramString, boolean paramBoolean) {
    if (paramString.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
      this._secureProcessing = paramBoolean; 
  }
  
  public Object getProperty(String paramString) { return paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD") ? this._accessExternalDTD : (paramString.equals("http://apache.org/xml/properties/security-manager") ? this._xmlSecurityManager : null); }
  
  public void setProperty(String paramString, Object paramObject) {
    if (paramString.equals("http://javax.xml.XMLConstants/property/accessExternalDTD")) {
      this._accessExternalDTD = (String)paramObject;
    } else if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      this._xmlSecurityManager = (XMLSecurityManager)paramObject;
    } 
  }
  
  class ReaderWrapper {
    XMLReader reader;
    
    boolean overrideDefaultParser;
    
    public ReaderWrapper(XMLReader param1XMLReader, boolean param1Boolean) {
      this.reader = param1XMLReader;
      this.overrideDefaultParser = param1Boolean;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\XMLReaderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */