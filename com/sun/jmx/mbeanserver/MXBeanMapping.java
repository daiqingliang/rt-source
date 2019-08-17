package com.sun.jmx.mbeanserver;

import java.io.InvalidObjectException;
import java.lang.reflect.Type;
import javax.management.openmbean.OpenType;

public abstract class MXBeanMapping {
  private final Type javaType;
  
  private final OpenType<?> openType;
  
  private final Class<?> openClass;
  
  protected MXBeanMapping(Type paramType, OpenType<?> paramOpenType) {
    if (paramType == null || paramOpenType == null)
      throw new NullPointerException("Null argument"); 
    this.javaType = paramType;
    this.openType = paramOpenType;
    this.openClass = makeOpenClass(paramType, paramOpenType);
  }
  
  public final Type getJavaType() { return this.javaType; }
  
  public final OpenType<?> getOpenType() { return this.openType; }
  
  public final Class<?> getOpenClass() { return this.openClass; }
  
  private static Class<?> makeOpenClass(Type paramType, OpenType<?> paramOpenType) {
    if (paramType instanceof Class && ((Class)paramType).isPrimitive())
      return (Class)paramType; 
    try {
      String str = paramOpenType.getClassName();
      return Class.forName(str, false, MXBeanMapping.class.getClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException(classNotFoundException);
    } 
  }
  
  public abstract Object fromOpenValue(Object paramObject) throws InvalidObjectException;
  
  public abstract Object toOpenValue(Object paramObject) throws InvalidObjectException;
  
  public void checkReconstructible() throws InvalidObjectException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MXBeanMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */