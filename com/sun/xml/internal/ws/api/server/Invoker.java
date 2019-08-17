package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.server.sei.Invoker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;

public abstract class Invoker extends Invoker {
  private static final Method invokeMethod;
  
  private static final Method asyncInvokeMethod;
  
  public void start(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull WSEndpoint paramWSEndpoint) { start(paramWSWebServiceContext); }
  
  public void start(@NotNull WebServiceContext paramWebServiceContext) { throw new IllegalStateException("deprecated version called"); }
  
  public void dispose() {}
  
  public <T> T invokeProvider(@NotNull Packet paramPacket, T paramT) throws IllegalAccessException, InvocationTargetException { return (T)invoke(paramPacket, invokeMethod, new Object[] { paramT }); }
  
  public <T> void invokeAsyncProvider(@NotNull Packet paramPacket, T paramT, AsyncProviderCallback paramAsyncProviderCallback, WebServiceContext paramWebServiceContext) throws IllegalAccessException, InvocationTargetException { invoke(paramPacket, asyncInvokeMethod, new Object[] { paramT, paramAsyncProviderCallback, paramWebServiceContext }); }
  
  static  {
    try {
      invokeMethod = javax.xml.ws.Provider.class.getMethod("invoke", new Class[] { Object.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new AssertionError(noSuchMethodException);
    } 
    try {
      asyncInvokeMethod = AsyncProvider.class.getMethod("invoke", new Class[] { Object.class, AsyncProviderCallback.class, WebServiceContext.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new AssertionError(noSuchMethodException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\Invoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */