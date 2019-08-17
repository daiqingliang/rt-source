package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.ServiceDelegate;

public abstract class WSService extends ServiceDelegate implements ComponentRegistry {
  private final Set<Component> components = new CopyOnWriteArraySet();
  
  protected static final ThreadLocal<InitParams> INIT_PARAMS = new ThreadLocal();
  
  protected static final InitParams EMPTY_PARAMS = new InitParams();
  
  public abstract <T> T getPort(WSEndpointReference paramWSEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs);
  
  public abstract <T> Dispatch<T> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  public abstract Dispatch<Object> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  @NotNull
  public abstract Container getContainer();
  
  @Nullable
  public <S> S getSPI(@NotNull Class<S> paramClass) {
    for (Component component : this.components) {
      Object object = component.getSPI(paramClass);
      if (object != null)
        return (S)object; 
    } 
    return (S)getContainer().getSPI(paramClass);
  }
  
  @NotNull
  public Set<Component> getComponents() { return this.components; }
  
  public static WSService create(URL paramURL, QName paramQName) { return new WSServiceDelegate(paramURL, paramQName, Service.class, new WebServiceFeature[0]); }
  
  public static WSService create(QName paramQName) { return create(null, paramQName); }
  
  public static WSService create() { return create(null, new QName(WSService.class.getName(), "dummy")); }
  
  public static Service create(URL paramURL, QName paramQName, InitParams paramInitParams) {
    if (INIT_PARAMS.get() != null)
      throw new IllegalStateException("someone left non-null InitParams"); 
    INIT_PARAMS.set(paramInitParams);
    try {
      Service service = Service.create(paramURL, paramQName);
      if (INIT_PARAMS.get() != null)
        throw new IllegalStateException("Service " + service + " didn't recognize InitParams"); 
      return service;
    } finally {
      INIT_PARAMS.set(null);
    } 
  }
  
  public static WSService unwrap(final Service svc) { return (WSService)AccessController.doPrivileged(new PrivilegedAction<WSService>() {
          public WSService run() {
            try {
              Field field = svc.getClass().getField("delegate");
              field.setAccessible(true);
              Object object = field.get(svc);
              if (!(object instanceof WSService))
                throw new IllegalArgumentException(); 
              return (WSService)object;
            } catch (NoSuchFieldException noSuchFieldException) {
              AssertionError assertionError = new AssertionError("Unexpected service API implementation");
              assertionError.initCause(noSuchFieldException);
              throw assertionError;
            } catch (IllegalAccessException illegalAccessException) {
              IllegalAccessError illegalAccessError = new IllegalAccessError(illegalAccessException.getMessage());
              illegalAccessError.initCause(illegalAccessException);
              throw illegalAccessError;
            } 
          }
        }); }
  
  public static final class InitParams {
    private Container container;
    
    public void setContainer(Container param1Container) { this.container = param1Container; }
    
    public Container getContainer() { return this.container; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\WSService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */