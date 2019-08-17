package com.sun.xml.internal.ws.wsdl;

import com.sun.xml.internal.ws.api.message.Message;

public final class DispatchException extends Exception {
  public final Message fault;
  
  public DispatchException(Message paramMessage) { this.fault = paramMessage; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\DispatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */