package com.sun.xml.internal.ws.api.handler;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public interface MessageHandler<C extends MessageHandlerContext> extends Handler<C> {
  Set<QName> getHeaders();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\handler\MessageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */