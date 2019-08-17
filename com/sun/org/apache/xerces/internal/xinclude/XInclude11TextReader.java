package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;

public class XInclude11TextReader extends XIncludeTextReader {
  public XInclude11TextReader(XMLInputSource paramXMLInputSource, XIncludeHandler paramXIncludeHandler, int paramInt) throws IOException { super(paramXMLInputSource, paramXIncludeHandler, paramInt); }
  
  protected boolean isValid(int paramInt) { return XML11Char.isXML11Valid(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XInclude11TextReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */