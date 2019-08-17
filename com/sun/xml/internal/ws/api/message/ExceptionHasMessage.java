package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public abstract class ExceptionHasMessage extends JAXWSExceptionBase {
  public ExceptionHasMessage(String paramString, Object... paramVarArgs) { super(paramString, paramVarArgs); }
  
  public abstract Message getFaultMessage();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\ExceptionHasMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */