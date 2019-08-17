package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class UtilException extends JAXWSExceptionBase {
  public UtilException(String paramString, Object... paramVarArgs) { super(paramString, paramVarArgs); }
  
  public UtilException(Throwable paramThrowable) { super(paramThrowable); }
  
  public UtilException(Localizable paramLocalizable) { super("nestedUtilError", new Object[] { paramLocalizable }); }
  
  public String getDefaultResourceBundleName() { return "com.sun.xml.internal.ws.resources.util"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\UtilException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */