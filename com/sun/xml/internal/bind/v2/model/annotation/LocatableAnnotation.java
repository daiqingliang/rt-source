package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class LocatableAnnotation implements InvocationHandler, Locatable, Location {
  private final Annotation core;
  
  private final Locatable upstream;
  
  private static final Map<Class, Quick> quicks = new HashMap();
  
  public static <A extends Annotation> A create(A paramA, Locatable paramLocatable) {
    if (paramA == null)
      return null; 
    Class clazz = paramA.annotationType();
    if (quicks.containsKey(clazz))
      return (A)((Quick)quicks.get(clazz)).newInstance(paramLocatable, paramA); 
    ClassLoader classLoader = SecureLoader.getClassClassLoader(LocatableAnnotation.class);
    try {
      Class clazz1 = Class.forName(clazz.getName(), false, classLoader);
      return (clazz1 != clazz) ? paramA : (A)(Annotation)Proxy.newProxyInstance(classLoader, new Class[] { clazz, Locatable.class }, new LocatableAnnotation(paramA, paramLocatable));
    } catch (ClassNotFoundException classNotFoundException) {
      return paramA;
    } catch (IllegalArgumentException illegalArgumentException) {
      return paramA;
    } 
  }
  
  LocatableAnnotation(Annotation paramAnnotation, Locatable paramLocatable) {
    this.core = paramAnnotation;
    this.upstream = paramLocatable;
  }
  
  public Locatable getUpstream() { return this.upstream; }
  
  public Location getLocation() { return this; }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    try {
      if (paramMethod.getDeclaringClass() == Locatable.class)
        return paramMethod.invoke(this, paramArrayOfObject); 
      if (Modifier.isStatic(paramMethod.getModifiers()))
        throw new IllegalArgumentException(); 
      return paramMethod.invoke(this.core, paramArrayOfObject);
    } catch (InvocationTargetException invocationTargetException) {
      if (invocationTargetException.getTargetException() != null)
        throw invocationTargetException.getTargetException(); 
      throw invocationTargetException;
    } 
  }
  
  public String toString() { return this.core.toString(); }
  
  static  {
    for (Quick quick : Init.getAll())
      quicks.put(quick.annotationType(), quick); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\LocatableAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */