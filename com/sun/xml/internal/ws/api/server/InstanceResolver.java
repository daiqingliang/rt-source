package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.server.SingletonResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InstanceResolver<T> extends Object {
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server");
  
  @NotNull
  public abstract T resolve(@NotNull Packet paramPacket);
  
  public void postInvoke(@NotNull Packet paramPacket, @NotNull T paramT) {}
  
  public void start(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull WSEndpoint paramWSEndpoint) { start(paramWSWebServiceContext); }
  
  public void start(@NotNull WebServiceContext paramWebServiceContext) {}
  
  public void dispose() {}
  
  public static <T> InstanceResolver<T> createSingleton(T paramT) {
    assert paramT != null;
    InstanceResolver instanceResolver = createFromInstanceResolverAnnotation(paramT.getClass());
    if (instanceResolver == null)
      instanceResolver = new SingletonResolver(paramT); 
    return instanceResolver;
  }
  
  public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> paramClass, boolean paramBoolean) { return createDefault(paramClass); }
  
  public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> paramClass) {
    InstanceResolver instanceResolver = createFromInstanceResolverAnnotation(paramClass);
    if (instanceResolver == null)
      instanceResolver = new SingletonResolver(createNewInstance(paramClass)); 
    return instanceResolver;
  }
  
  public static <T> InstanceResolver<T> createFromInstanceResolverAnnotation(@NotNull Class<T> paramClass) {
    Annotation[] arrayOfAnnotation = paramClass.getAnnotations();
    int i = arrayOfAnnotation.length;
    byte b = 0;
    while (b < i) {
      Annotation annotation = arrayOfAnnotation[b];
      InstanceResolverAnnotation instanceResolverAnnotation = (InstanceResolverAnnotation)annotation.annotationType().getAnnotation(InstanceResolverAnnotation.class);
      if (instanceResolverAnnotation == null) {
        b++;
        continue;
      } 
      Class clazz = instanceResolverAnnotation.value();
      try {
        return (InstanceResolver)clazz.getConstructor(new Class[] { Class.class }).newInstance(new Object[] { paramClass });
      } catch (InstantiationException instantiationException) {
        throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(clazz.getName(), annotation.annotationType(), paramClass.getName()));
      } catch (IllegalAccessException illegalAccessException) {
        throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(clazz.getName(), annotation.annotationType(), paramClass.getName()));
      } catch (InvocationTargetException invocationTargetException) {
        throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(clazz.getName(), annotation.annotationType(), paramClass.getName()));
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(clazz.getName(), annotation.annotationType(), paramClass.getName()));
      } 
    } 
    return null;
  }
  
  protected static <T> T createNewInstance(Class<T> paramClass) {
    try {
      return (T)paramClass.newInstance();
    } catch (InstantiationException instantiationException) {
      logger.log(Level.SEVERE, instantiationException.getMessage(), instantiationException);
      throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(paramClass), new Object[0]);
    } catch (IllegalAccessException illegalAccessException) {
      logger.log(Level.SEVERE, illegalAccessException.getMessage(), illegalAccessException);
      throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(paramClass), new Object[0]);
    } 
  }
  
  @NotNull
  public Invoker createInvoker() { return new Invoker() {
        public void start(@NotNull WSWebServiceContext param1WSWebServiceContext, @NotNull WSEndpoint param1WSEndpoint) { InstanceResolver.this.start(param1WSWebServiceContext, param1WSEndpoint); }
        
        public void dispose() { InstanceResolver.this.dispose(); }
        
        public Object invoke(Packet param1Packet, Method param1Method, Object... param1VarArgs) throws InvocationTargetException, IllegalAccessException {
          object = InstanceResolver.this.resolve(param1Packet);
          try {
            return MethodUtil.invoke(object, param1Method, param1VarArgs);
          } finally {
            InstanceResolver.this.postInvoke(param1Packet, object);
          } 
        }
        
        public <U> U invokeProvider(@NotNull Packet param1Packet, U param1U) {
          object = InstanceResolver.this.resolve(param1Packet);
          try {
            object1 = ((Provider)object).invoke(param1U);
            return (U)object1;
          } finally {
            InstanceResolver.this.postInvoke(param1Packet, object);
          } 
        }
        
        public String toString() { return "Default Invoker over " + InstanceResolver.this.toString(); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\InstanceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */