package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class ServiceInterceptorFactory {
  private static ThreadLocal<Set<ServiceInterceptorFactory>> threadLocalFactories = new ThreadLocal<Set<ServiceInterceptorFactory>>() {
      protected Set<ServiceInterceptorFactory> initialValue() { return new HashSet(); }
    };
  
  public abstract ServiceInterceptor create(@NotNull WSService paramWSService);
  
  @NotNull
  public static ServiceInterceptor load(@NotNull WSService paramWSService, @Nullable ClassLoader paramClassLoader) {
    ArrayList arrayList = new ArrayList();
    for (ServiceInterceptorFactory serviceInterceptorFactory : ServiceFinder.find(ServiceInterceptorFactory.class))
      arrayList.add(serviceInterceptorFactory.create(paramWSService)); 
    for (ServiceInterceptorFactory serviceInterceptorFactory : (Set)threadLocalFactories.get())
      arrayList.add(serviceInterceptorFactory.create(paramWSService)); 
    return ServiceInterceptor.aggregate((ServiceInterceptor[])arrayList.toArray(new ServiceInterceptor[arrayList.size()]));
  }
  
  public static boolean registerForThread(ServiceInterceptorFactory paramServiceInterceptorFactory) { return ((Set)threadLocalFactories.get()).add(paramServiceInterceptorFactory); }
  
  public static boolean unregisterForThread(ServiceInterceptorFactory paramServiceInterceptorFactory) { return ((Set)threadLocalFactories.get()).remove(paramServiceInterceptorFactory); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\client\ServiceInterceptorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */