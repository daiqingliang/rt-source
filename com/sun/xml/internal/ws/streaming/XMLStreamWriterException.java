package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLStreamWriterException extends JAXWSExceptionBase {
  public XMLStreamWriterException(String paramString, Object... paramVarArgs) { super(paramString, paramVarArgs); }
  
  public XMLStreamWriterException(Throwable paramThrowable) { super(paramThrowable); }
  
  public XMLStreamWriterException(Localizable paramLocalizable) { super("xmlwriter.nestedError", new Object[] { paramLocalizable }); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.streaming"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\XMLStreamWriterException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */