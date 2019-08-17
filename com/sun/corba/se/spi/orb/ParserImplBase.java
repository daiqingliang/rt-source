package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public abstract class ParserImplBase {
  private ORBUtilSystemException wrapper = ORBUtilSystemException.get("orb.lifecycle");
  
  protected abstract PropertyParser makeParser();
  
  protected void complete() {}
  
  public void init(DataCollector paramDataCollector) {
    PropertyParser propertyParser = makeParser();
    paramDataCollector.setParser(propertyParser);
    Properties properties = paramDataCollector.getProperties();
    Map map = propertyParser.parse(properties);
    setFields(map);
  }
  
  private Field getAnyField(String paramString) {
    Field field = null;
    try {
      Class clazz = getClass();
      for (field = clazz.getDeclaredField(paramString); field == null; field = clazz.getDeclaredField(paramString)) {
        clazz = clazz.getSuperclass();
        if (clazz == null)
          break; 
      } 
    } catch (Exception exception) {
      throw this.wrapper.fieldNotFound(exception, paramString);
    } 
    if (field == null)
      throw this.wrapper.fieldNotFound(paramString); 
    return field;
  }
  
  protected void setFields(Map paramMap) {
    Set set = paramMap.entrySet();
    for (Map.Entry entry : set) {
      final String name = (String)entry.getKey();
      final Object value = entry.getValue();
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction() {
              public Object run() throws IllegalAccessException, IllegalArgumentException {
                Field field = ParserImplBase.this.getAnyField(name);
                field.setAccessible(true);
                field.set(ParserImplBase.this, value);
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw this.wrapper.errorSettingField(privilegedActionException.getCause(), str, object.toString());
      } 
    } 
    complete();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\ParserImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */