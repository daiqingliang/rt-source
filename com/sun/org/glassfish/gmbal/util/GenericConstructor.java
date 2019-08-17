package com.sun.org.glassfish.gmbal.util;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericConstructor<T> extends Object {
  private final Object lock = new Object();
  
  private String typeName;
  
  private Class<T> resultType;
  
  private Class<?> type;
  
  private Class<?>[] signature;
  
  private Constructor constructor;
  
  public GenericConstructor(Class<T> paramClass, String paramString, Class<?>... paramVarArgs) {
    this.resultType = paramClass;
    this.typeName = paramString;
    this.signature = (Class[])paramVarArgs.clone();
  }
  
  private void getConstructor() {
    synchronized (this.lock) {
      if (this.type == null || this.constructor == null)
        try {
          this.type = Class.forName(this.typeName);
          this.constructor = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor>() {
                public Constructor run() throws Exception {
                  synchronized (GenericConstructor.this.lock) {
                    return GenericConstructor.this.type.getDeclaredConstructor(GenericConstructor.this.signature);
                  } 
                }
              });
        } catch (Exception exception) {
          Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.FINE, "Failure in getConstructor", exception);
        }  
    } 
  }
  
  public T create(Object... paramVarArgs) {
    synchronized (this.lock) {
      Object object = null;
      byte b = 0;
      while (b <= 1) {
        getConstructor();
        if (this.constructor == null)
          break; 
        try {
          object = this.resultType.cast(this.constructor.newInstance(paramVarArgs));
          break;
        } catch (Exception exception) {
          this.constructor = null;
          Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.WARNING, "Error invoking constructor", exception);
          b++;
        } 
      } 
      return (T)object;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\gmba\\util\GenericConstructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */