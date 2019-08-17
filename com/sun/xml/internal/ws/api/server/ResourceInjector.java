package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.server.DefaultResourceInjector;

public abstract class ResourceInjector {
  public static final ResourceInjector STANDALONE = new DefaultResourceInjector();
  
  public abstract void inject(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\ResourceInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */