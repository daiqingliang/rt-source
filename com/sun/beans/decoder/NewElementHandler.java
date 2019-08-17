package com.sun.beans.decoder;

import com.sun.beans.finder.ConstructorFinder;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

class NewElementHandler extends ElementHandler {
  private List<Object> arguments = new ArrayList();
  
  private ValueObject value = ValueObjectImpl.VOID;
  
  private Class<?> type;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("class")) {
      this.type = getOwner().findClass(paramString2);
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  protected final void addArgument(Object paramObject) {
    if (this.arguments == null)
      throw new IllegalStateException("Could not add argument to evaluated element"); 
    this.arguments.add(paramObject);
  }
  
  protected final Object getContextBean() { return (this.type != null) ? this.type : super.getContextBean(); }
  
  protected final ValueObject getValueObject() {
    if (this.arguments != null)
      try {
        this.value = getValueObject(this.type, this.arguments.toArray());
      } catch (Exception exception) {
        getOwner().handleException(exception);
      } finally {
        this.arguments = null;
      }  
    return this.value;
  }
  
  ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject) throws Exception {
    if (paramClass == null)
      throw new IllegalArgumentException("Class name is not set"); 
    Class[] arrayOfClass = getArgumentTypes(paramArrayOfObject);
    Constructor constructor = ConstructorFinder.findConstructor(paramClass, arrayOfClass);
    if (constructor.isVarArgs())
      paramArrayOfObject = getArguments(paramArrayOfObject, constructor.getParameterTypes()); 
    return ValueObjectImpl.create(constructor.newInstance(paramArrayOfObject));
  }
  
  static Class<?>[] getArgumentTypes(Object[] paramArrayOfObject) {
    Class[] arrayOfClass = new Class[paramArrayOfObject.length];
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] != null)
        arrayOfClass[b] = paramArrayOfObject[b].getClass(); 
    } 
    return arrayOfClass;
  }
  
  static Object[] getArguments(Object[] paramArrayOfObject, Class<?>[] paramArrayOfClass) {
    int i = paramArrayOfClass.length - 1;
    if (paramArrayOfClass.length == paramArrayOfObject.length) {
      Object object1 = paramArrayOfObject[i];
      if (object1 == null)
        return paramArrayOfObject; 
      Class<?> clazz1 = paramArrayOfClass[i];
      if (clazz1.isAssignableFrom(object1.getClass()))
        return paramArrayOfObject; 
    } 
    int j = paramArrayOfObject.length - i;
    Class clazz = paramArrayOfClass[i].getComponentType();
    Object object = Array.newInstance(clazz, j);
    System.arraycopy(paramArrayOfObject, i, object, 0, j);
    Object[] arrayOfObject = new Object[paramArrayOfClass.length];
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, i);
    arrayOfObject[i] = object;
    return arrayOfObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\NewElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */