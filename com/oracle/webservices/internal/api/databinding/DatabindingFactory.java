package com.oracle.webservices.internal.api.databinding;

import java.util.Map;

public abstract class DatabindingFactory {
  static final String ImplClass = "com.sun.xml.internal.ws.db.DatabindingFactoryImpl";
  
  public abstract Databinding.Builder createBuilder(Class<?> paramClass1, Class<?> paramClass2);
  
  public abstract Map<String, Object> properties();
  
  public static DatabindingFactory newInstance() {
    try {
      Class clazz = Class.forName("com.sun.xml.internal.ws.db.DatabindingFactoryImpl");
      return convertIfNecessary(clazz);
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  private static DatabindingFactory convertIfNecessary(Class<?> paramClass) throws InstantiationException, IllegalAccessException { return (DatabindingFactory)paramClass.newInstance(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\databinding\DatabindingFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */