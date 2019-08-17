package com.sun.xml.internal.ws.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceException;

public abstract class InjectionPlan<T, R> extends Object {
  public abstract void inject(T paramT, R paramR);
  
  public void inject(T paramT, Callable<R> paramCallable) {
    try {
      inject(paramT, paramCallable.call());
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
  }
  
  private static void invokeMethod(final Method method, final Object instance, Object... args) {
    if (paramMethod == null)
      return; 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              if (!method.isAccessible())
                method.setAccessible(true); 
              method.invoke(instance, args);
            } catch (IllegalAccessException illegalAccessException) {
              throw new WebServiceException(illegalAccessException);
            } catch (InvocationTargetException invocationTargetException) {
              throw new WebServiceException(invocationTargetException);
            } 
            return null;
          }
        });
  }
  
  public static <T, R> InjectionPlan<T, R> buildInjectionPlan(Class<? extends T> paramClass1, Class<R> paramClass2, boolean paramBoolean) {
    ArrayList arrayList = new ArrayList();
    Class<? extends T> clazz;
    for (clazz = paramClass1; clazz != Object.class; clazz = clazz.getSuperclass()) {
      for (Field field : clazz.getDeclaredFields()) {
        Resource resource = (Resource)field.getAnnotation(Resource.class);
        if (resource != null && isInjectionPoint(resource, field.getType(), "Incorrect type for field" + field.getName(), paramClass2)) {
          if (paramBoolean && !Modifier.isStatic(field.getModifiers()))
            throw new WebServiceException("Static resource " + paramClass2 + " cannot be injected to non-static " + field); 
          arrayList.add(new FieldInjectionPlan(field));
        } 
      } 
    } 
    for (clazz = paramClass1; clazz != Object.class; clazz = clazz.getSuperclass()) {
      for (Method method : clazz.getDeclaredMethods()) {
        Resource resource = (Resource)method.getAnnotation(Resource.class);
        if (resource != null) {
          Class[] arrayOfClass = method.getParameterTypes();
          if (arrayOfClass.length != 1)
            throw new WebServiceException("Incorrect no of arguments for method " + method); 
          if (isInjectionPoint(resource, arrayOfClass[0], "Incorrect argument types for method" + method.getName(), paramClass2)) {
            if (paramBoolean && !Modifier.isStatic(method.getModifiers()))
              throw new WebServiceException("Static resource " + paramClass2 + " cannot be injected to non-static " + method); 
            arrayList.add(new MethodInjectionPlan(method));
          } 
        } 
      } 
    } 
    return new Compositor(arrayList);
  }
  
  private static boolean isInjectionPoint(Resource paramResource, Class paramClass1, String paramString, Class paramClass2) {
    Class clazz = paramResource.type();
    if (clazz.equals(Object.class))
      return paramClass1.equals(paramClass2); 
    if (clazz.equals(paramClass2)) {
      if (paramClass1.isAssignableFrom(paramClass2))
        return true; 
      throw new WebServiceException(paramString);
    } 
    return false;
  }
  
  private static class Compositor<T, R> extends InjectionPlan<T, R> {
    private final Collection<InjectionPlan<T, R>> children;
    
    public Compositor(Collection<InjectionPlan<T, R>> param1Collection) { this.children = param1Collection; }
    
    public void inject(T param1T, R param1R) {
      for (InjectionPlan injectionPlan : this.children)
        injectionPlan.inject(param1T, param1R); 
    }
    
    public void inject(T param1T, Callable<R> param1Callable) {
      if (!this.children.isEmpty())
        super.inject(param1T, param1Callable); 
    }
  }
  
  public static class FieldInjectionPlan<T, R> extends InjectionPlan<T, R> {
    private final Field field;
    
    public FieldInjectionPlan(Field param1Field) { this.field = param1Field; }
    
    public void inject(final T instance, final R resource) { AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
              try {
                if (!InjectionPlan.FieldInjectionPlan.this.field.isAccessible())
                  InjectionPlan.FieldInjectionPlan.this.field.setAccessible(true); 
                InjectionPlan.FieldInjectionPlan.this.field.set(instance, resource);
                return null;
              } catch (IllegalAccessException illegalAccessException) {
                throw new WebServiceException(illegalAccessException);
              } 
            }
          }); }
  }
  
  public static class MethodInjectionPlan<T, R> extends InjectionPlan<T, R> {
    private final Method method;
    
    public MethodInjectionPlan(Method param1Method) { this.method = param1Method; }
    
    public void inject(T param1T, R param1R) { InjectionPlan.invokeMethod(this.method, param1T, new Object[] { param1R }); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\InjectionPlan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */