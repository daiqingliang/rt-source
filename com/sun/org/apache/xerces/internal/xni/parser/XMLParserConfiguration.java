package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import java.util.Locale;

public interface XMLParserConfiguration extends XMLComponentManager {
  void parse(XMLInputSource paramXMLInputSource) throws XNIException, IOException;
  
  void addRecognizedFeatures(String[] paramArrayOfString);
  
  void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException;
  
  boolean getFeature(String paramString) throws XMLConfigurationException;
  
  void addRecognizedProperties(String[] paramArrayOfString);
  
  void setProperty(String paramString, Object paramObject) throws XMLConfigurationException;
  
  Object getProperty(String paramString) throws XMLConfigurationException;
  
  void setErrorHandler(XMLErrorHandler paramXMLErrorHandler);
  
  XMLErrorHandler getErrorHandler();
  
  void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler);
  
  XMLDocumentHandler getDocumentHandler();
  
  void setDTDHandler(XMLDTDHandler paramXMLDTDHandler);
  
  XMLDTDHandler getDTDHandler();
  
  void setDTDContentModelHandler(XMLDTDContentModelHandler paramXMLDTDContentModelHandler);
  
  XMLDTDContentModelHandler getDTDContentModelHandler();
  
  void setEntityResolver(XMLEntityResolver paramXMLEntityResolver);
  
  XMLEntityResolver getEntityResolver();
  
  void setLocale(Locale paramLocale) throws XNIException;
  
  Locale getLocale();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */