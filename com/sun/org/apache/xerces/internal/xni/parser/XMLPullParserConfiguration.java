package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLPullParserConfiguration extends XMLParserConfiguration {
  void setInputSource(XMLInputSource paramXMLInputSource) throws XMLConfigurationException, IOException;
  
  boolean parse(boolean paramBoolean) throws XNIException, IOException;
  
  void cleanup();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLPullParserConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */