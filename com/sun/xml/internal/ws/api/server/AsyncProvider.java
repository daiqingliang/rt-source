package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import javax.xml.ws.WebServiceContext;

public interface AsyncProvider<T> {
  void invoke(@NotNull T paramT, @NotNull AsyncProviderCallback<T> paramAsyncProviderCallback, @NotNull WebServiceContext paramWebServiceContext);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\AsyncProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */