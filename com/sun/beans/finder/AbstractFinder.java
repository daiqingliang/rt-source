package com.sun.beans.finder;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.util.HashMap;

abstract class AbstractFinder<T extends Executable> extends Object {
  private final Class<?>[] args;
  
  protected AbstractFinder(Class<?>[] paramArrayOfClass) { this.args = paramArrayOfClass; }
  
  protected boolean isValid(T paramT) { return Modifier.isPublic(paramT.getModifiers()); }
  
  final T find(T[] paramArrayOfT) throws NoSuchMethodException {
    T t;
    HashMap hashMap = new HashMap();
    Object object = null;
    Class[] arrayOfClass = null;
    boolean bool = false;
    for (T t1 : paramArrayOfT) {
      if (isValid(t1)) {
        Class[] arrayOfClass1 = t1.getParameterTypes();
        if (arrayOfClass1.length == this.args.length) {
          PrimitiveWrapperMap.replacePrimitivesWithWrappers(arrayOfClass1);
          if (isAssignable(arrayOfClass1, this.args))
            if (object == null) {
              t = t1;
              arrayOfClass = arrayOfClass1;
            } else {
              boolean bool1 = isAssignable(arrayOfClass, arrayOfClass1);
              boolean bool2 = isAssignable(arrayOfClass1, arrayOfClass);
              if (bool2 && bool1) {
                bool1 = !t1.isSynthetic();
                bool2 = !t.isSynthetic();
              } 
              if (bool2 == bool1) {
                bool = true;
              } else if (bool1) {
                t = t1;
                arrayOfClass = arrayOfClass1;
                bool = false;
              } 
            }  
        } 
        if (t1.isVarArgs()) {
          int i = arrayOfClass1.length - 1;
          if (i <= this.args.length) {
            Class[] arrayOfClass2 = new Class[this.args.length];
            System.arraycopy(arrayOfClass1, 0, arrayOfClass2, 0, i);
            if (i < this.args.length) {
              Class clazz = arrayOfClass1[i].getComponentType();
              if (clazz.isPrimitive())
                clazz = PrimitiveWrapperMap.getType(clazz.getName()); 
              for (int j = i; j < this.args.length; j++)
                arrayOfClass2[j] = clazz; 
            } 
            hashMap.put(t1, arrayOfClass2);
          } 
        } 
      } 
    } 
    for (T t1 : paramArrayOfT) {
      Class[] arrayOfClass1 = (Class[])hashMap.get(t1);
      if (arrayOfClass1 != null && isAssignable(arrayOfClass1, this.args))
        if (t == null) {
          t = t1;
          arrayOfClass = arrayOfClass1;
        } else {
          boolean bool1 = isAssignable(arrayOfClass, arrayOfClass1);
          boolean bool2 = isAssignable(arrayOfClass1, arrayOfClass);
          if (bool2 && bool1) {
            bool1 = !t1.isSynthetic();
            bool2 = !t.isSynthetic();
          } 
          if (bool2 == bool1) {
            if (arrayOfClass == hashMap.get(t))
              bool = true; 
          } else if (bool1) {
            t = t1;
            arrayOfClass = arrayOfClass1;
            bool = false;
          } 
        }  
    } 
    if (bool)
      throw new NoSuchMethodException("Ambiguous methods are found"); 
    if (t == null)
      throw new NoSuchMethodException("Method is not found"); 
    return t;
  }
  
  private boolean isAssignable(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2) {
    for (byte b = 0; b < this.args.length; b++) {
      if (null != this.args[b] && !paramArrayOfClass1[b].isAssignableFrom(paramArrayOfClass2[b]))
        return false; 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\AbstractFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */