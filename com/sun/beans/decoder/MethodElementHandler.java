package com.sun.beans.decoder;

import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;

final class MethodElementHandler extends NewElementHandler {
  private String name;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("name")) {
      this.name = paramString2;
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  protected ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject) throws Exception {
    Object object1 = getContextBean();
    Class[] arrayOfClass = getArgumentTypes(paramArrayOfObject);
    Method method = (paramClass != null) ? MethodFinder.findStaticMethod(paramClass, this.name, arrayOfClass) : MethodFinder.findMethod(object1.getClass(), this.name, arrayOfClass);
    if (method.isVarArgs())
      paramArrayOfObject = getArguments(paramArrayOfObject, method.getParameterTypes()); 
    Object object2 = MethodUtil.invoke(method, object1, paramArrayOfObject);
    return method.getReturnType().equals(void.class) ? ValueObjectImpl.VOID : ValueObjectImpl.create(object2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\MethodElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */