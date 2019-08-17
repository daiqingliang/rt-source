package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLDTDScanner extends XMLDTDSource, XMLDTDContentModelSource {
  void setInputSource(XMLInputSource paramXMLInputSource) throws IOException;
  
  boolean scanDTDInternalSubset(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) throws IOException, XNIException;
  
  boolean scanDTDExternalSubset(boolean paramBoolean) throws IOException, XNIException;
  
  boolean skipDTD(boolean paramBoolean) throws IOException, XNIException;
  
  void setLimitAnalyzer(XMLLimitAnalyzer paramXMLLimitAnalyzer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\parser\XMLDTDScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */