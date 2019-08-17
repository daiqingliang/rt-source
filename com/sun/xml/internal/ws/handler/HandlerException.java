package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class HandlerException extends JAXWSExceptionBase {
  public HandlerException(String paramString, Object... paramVarArgs) { super(paramString, paramVarArgs); }
  
  public HandlerException(Throwable paramThrowable) { super(paramThrowable); }
  
  public HandlerException(Localizable paramLocalizable) { super("handler.nestedError", new Object[] { paramLocalizable }); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.handler"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\HandlerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */