package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;

public abstract class PropertySetterBase implements PropertySetter {
  protected Class type;
  
  public Class getType() { return this.type; }
  
  public static boolean setterPattern(Method paramMethod) { return (paramMethod.getName().startsWith("set") && paramMethod.getName().length() > 3 && paramMethod.getReturnType().equals(void.class) && paramMethod.getParameterTypes() != null && paramMethod.getParameterTypes().length == 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertySetterBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */