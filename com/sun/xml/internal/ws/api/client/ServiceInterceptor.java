package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public abstract class ServiceInterceptor {
  public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo paramWSPortInfo, @Nullable Class<?> paramClass, @NotNull WSFeatureList paramWSFeatureList) { return Collections.emptyList(); }
  
  public void postCreateProxy(@NotNull WSBindingProvider paramWSBindingProvider, @NotNull Class<?> paramClass) {}
  
  public void postCreateDispatch(@NotNull WSBindingProvider paramWSBindingProvider) {}
  
  public static ServiceInterceptor aggregate(ServiceInterceptor... interceptors) { return (paramVarArgs.length == 1) ? paramVarArgs[0] : new ServiceInterceptor() {
        public List<WebServiceFeature> preCreateBinding(@NotNull WSPortInfo param1WSPortInfo, @Nullable Class<?> param1Class, @NotNull WSFeatureList param1WSFeatureList) {
          ArrayList arrayList = new ArrayList();
          for (ServiceInterceptor serviceInterceptor : interceptors)
            arrayList.addAll(serviceInterceptor.preCreateBinding(param1WSPortInfo, param1Class, param1WSFeatureList)); 
          return arrayList;
        }
        
        public void postCreateProxy(@NotNull WSBindingProvider param1WSBindingProvider, @NotNull Class<?> param1Class) {
          for (ServiceInterceptor serviceInterceptor : interceptors)
            serviceInterceptor.postCreateProxy(param1WSBindingProvider, param1Class); 
        }
        
        public void postCreateDispatch(@NotNull WSBindingProvider param1WSBindingProvider) {
          for (ServiceInterceptor serviceInterceptor : interceptors)
            serviceInterceptor.postCreateDispatch(param1WSBindingProvider); 
        }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\client\ServiceInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */