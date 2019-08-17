package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLReaderException extends JAXWSExceptionBase {
  public XMLReaderException(String paramString, Object... paramVarArgs) { super(paramString, paramVarArgs); }
  
  public XMLReaderException(Throwable paramThrowable) { super(paramThrowable); }
  
  public XMLReaderException(Localizable paramLocalizable) { super("xmlreader.nestedError", new Object[] { paramLocalizable }); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.streaming"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\XMLReaderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */