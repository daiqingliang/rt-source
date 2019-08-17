package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Method;

public abstract class PropertyGetterBase implements PropertyGetter {
  protected Class type;
  
  public Class getType() { return this.type; }
  
  public static boolean getterPattern(Method paramMethod) {
    if (!paramMethod.getReturnType().equals(void.class) && (paramMethod.getParameterTypes() == null || paramMethod.getParameterTypes().length == 0)) {
      if (paramMethod.getName().startsWith("get") && paramMethod.getName().length() > 3)
        return true; 
      if (paramMethod.getReturnType().equals(boolean.class) && paramMethod.getName().startsWith("is") && paramMethod.getName().length() > 2)
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertyGetterBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */