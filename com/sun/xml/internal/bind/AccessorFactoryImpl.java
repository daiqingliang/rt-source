package com.sun.xml.internal.bind;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AccessorFactoryImpl implements InternalAccessorFactory {
  private static AccessorFactoryImpl instance = new AccessorFactoryImpl();
  
  public static AccessorFactoryImpl getInstance() { return instance; }
  
  public Accessor createFieldAccessor(Class paramClass, Field paramField, boolean paramBoolean) { return paramBoolean ? new Accessor.ReadOnlyFieldReflection(paramField) : new Accessor.FieldReflection(paramField); }
  
  public Accessor createFieldAccessor(Class paramClass, Field paramField, boolean paramBoolean1, boolean paramBoolean2) { return paramBoolean1 ? new Accessor.ReadOnlyFieldReflection(paramField, paramBoolean2) : new Accessor.FieldReflection(paramField, paramBoolean2); }
  
  public Accessor createPropertyAccessor(Class paramClass, Method paramMethod1, Method paramMethod2) { return (paramMethod1 == null) ? new Accessor.SetterOnlyReflection(paramMethod2) : ((paramMethod2 == null) ? new Accessor.GetterOnlyReflection(paramMethod1) : new Accessor.GetterSetterReflection(paramMethod1, paramMethod2)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\AccessorFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */