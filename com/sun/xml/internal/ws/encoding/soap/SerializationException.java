package com.sun.xml.internal.ws.encoding.soap;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class SerializationException extends JAXWSExceptionBase {
  public SerializationException(String paramString, Object... paramVarArgs) { super(paramString, paramVarArgs); }
  
  public SerializationException(Localizable paramLocalizable) { super("nestedSerializationError", new Object[] { paramLocalizable }); }
  
  public SerializationException(Throwable paramThrowable) { super(paramThrowable); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.encoding"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\soap\SerializationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */