package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.util.InjectionPlan;

public final class DefaultResourceInjector extends ResourceInjector {
  public void inject(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull Object paramObject) { InjectionPlan.buildInjectionPlan(paramObject.getClass(), javax.xml.ws.WebServiceContext.class, false).inject(paramObject, paramWSWebServiceContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\DefaultResourceInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */