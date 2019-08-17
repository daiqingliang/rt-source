package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.api.message.Packet;
import org.xml.sax.ErrorHandler;

public abstract class ValidationErrorHandler implements ErrorHandler {
  protected Packet packet;
  
  public void setPacket(Packet paramPacket) { this.packet = paramPacket; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\ValidationErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */