package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import java.util.Locale;

public interface XMLGrammarLoader {
  String[] getRecognizedFeatures();
  
  boolean getFeature(String paramString) throws XMLConfigurationException;
  
  void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException;
  
  String[] getRecognizedProperties();
  
  Object getProperty(String paramString) throws XMLConfigurationException;
  
  void setProperty(String paramString, Object paramObject) throws XMLConfigurationException;
  
  void setLocale(Locale paramLocale);
  
  Locale getLocale();
  
  void setErrorHandler(XMLErrorHandler paramXMLErrorHandler);
  
  XMLErrorHandler getErrorHandler();
  
  void setEntityResolver(XMLEntityResolver paramXMLEntityResolver);
  
  XMLEntityResolver getEntityResolver();
  
  Grammar loadGrammar(XMLInputSource paramXMLInputSource) throws IOException, XNIException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XMLGrammarLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */