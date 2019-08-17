package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface AsyncProviderCallback<T> {
  void send(@Nullable T paramT);
  
  void sendError(@NotNull Throwable paramThrowable);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\AsyncProviderCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */