package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.oracle.webservices.internal.api.databinding.DatabindingFactory;
import java.util.Map;

public abstract class DatabindingFactory extends DatabindingFactory {
  static final String ImplClass = com.sun.xml.internal.ws.db.DatabindingFactoryImpl.class.getName();
  
  public abstract Databinding createRuntime(DatabindingConfig paramDatabindingConfig);
  
  public abstract Map<String, Object> properties();
  
  public static DatabindingFactory newInstance() {
    try {
      Class clazz = Class.forName(ImplClass);
      return (DatabindingFactory)clazz.newInstance();
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\DatabindingFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */