package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class AbstractInstanceResolver<T> extends InstanceResolver<T> {
  protected static ResourceInjector getResourceInjector(WSEndpoint paramWSEndpoint) {
    ResourceInjector resourceInjector = (ResourceInjector)paramWSEndpoint.getContainer().getSPI(ResourceInjector.class);
    if (resourceInjector == null)
      resourceInjector = ResourceInjector.STANDALONE; 
    return resourceInjector;
  }
  
  protected static void invokeMethod(@Nullable final Method method, final Object instance, Object... args) {
    if (paramMethod == null)
      return; 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              if (!method.isAccessible())
                method.setAccessible(true); 
              MethodUtil.invoke(instance, method, args);
            } catch (IllegalAccessException illegalAccessException) {
              throw new ServerRtException("server.rt.err", new Object[] { illegalAccessException });
            } catch (InvocationTargetException invocationTargetException) {
              throw new ServerRtException("server.rt.err", new Object[] { invocationTargetException });
            } 
            return null;
          }
        });
  }
  
  @Nullable
  protected final Method findAnnotatedMethod(Class paramClass1, Class<? extends Annotation> paramClass2) {
    boolean bool = false;
    Method method = null;
    for (Method method1 : paramClass1.getDeclaredMethods()) {
      if (method1.getAnnotation(paramClass2) != null) {
        if (bool)
          throw new ServerRtException(ServerMessages.ANNOTATION_ONLY_ONCE(paramClass2), new Object[0]); 
        if (method1.getParameterTypes().length != 0)
          throw new ServerRtException(ServerMessages.NOT_ZERO_PARAMETERS(method1), new Object[0]); 
        method = method1;
        bool = true;
      } 
    } 
    return method;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\AbstractInstanceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */