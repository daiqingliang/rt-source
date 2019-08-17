package com.sun.xml.internal.ws.server.sei;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public interface InvokerSource<T extends Invoker> {
  @NotNull
  T getInvoker(Packet paramPacket);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\sei\InvokerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */