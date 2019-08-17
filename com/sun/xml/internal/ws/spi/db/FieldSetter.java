package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FieldSetter extends PropertySetterBase {
  protected Field field;
  
  public FieldSetter(Field paramField) {
    this.field = paramField;
    this.type = paramField.getType();
  }
  
  public Field getField() { return this.field; }
  
  public void set(final Object instance, final Object resource) {
    if (this.field.isAccessible()) {
      try {
        this.field.set(paramObject1, paramObject2);
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
    } else {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              public Object run() throws IllegalAccessException {
                if (!FieldSetter.this.field.isAccessible())
                  FieldSetter.this.field.setAccessible(true); 
                FieldSetter.this.field.set(instance, resource);
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        privilegedActionException.printStackTrace();
      } 
    } 
  }
  
  public <A> A getAnnotation(Class<A> paramClass) {
    Class<A> clazz = paramClass;
    return (A)this.field.getAnnotation(clazz);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\FieldSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */