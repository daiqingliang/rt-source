package com.sun.xml.internal.ws.spi.db;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FieldGetter extends PropertyGetterBase {
  protected Field field;
  
  public FieldGetter(Field paramField) {
    this.field = paramField;
    this.type = paramField.getType();
  }
  
  public Field getField() { return this.field; }
  
  public Object get(Object paramObject) {
    if (this.field.isAccessible())
      try {
        return this.field.get(paramObject);
      } catch (Exception exception) {
        exception.printStackTrace();
        return null;
      }  
    PrivilegedGetter privilegedGetter = new PrivilegedGetter(this.field, paramObject);
    try {
      AccessController.doPrivileged(privilegedGetter);
    } catch (PrivilegedActionException privilegedActionException) {
      privilegedActionException.printStackTrace();
    } 
    return privilegedGetter.value;
  }
  
  public <A> A getAnnotation(Class<A> paramClass) {
    Class<A> clazz = paramClass;
    return (A)this.field.getAnnotation(clazz);
  }
  
  static class PrivilegedGetter implements PrivilegedExceptionAction {
    private Object value;
    
    private Field field;
    
    private Object instance;
    
    public PrivilegedGetter(Field param1Field, Object param1Object) {
      this.field = param1Field;
      this.instance = param1Object;
    }
    
    public Object run() throws IllegalAccessException {
      if (!this.field.isAccessible())
        this.field.setAccessible(true); 
      this.value = this.field.get(this.instance);
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\FieldGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */