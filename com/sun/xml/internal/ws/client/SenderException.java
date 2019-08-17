package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class SenderException extends JAXWSExceptionBase {
  public SenderException(String paramString, Object... paramVarArgs) { super(paramString, paramVarArgs); }
  
  public SenderException(Throwable paramThrowable) { super(paramThrowable); }
  
  public SenderException(Localizable paramLocalizable) { super("sender.nestedError", new Object[] { paramLocalizable }); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.sender"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\SenderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */