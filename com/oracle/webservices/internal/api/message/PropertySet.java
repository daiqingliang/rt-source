package com.oracle.webservices.internal.api.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

public interface PropertySet {
  boolean containsKey(Object paramObject);
  
  Object get(Object paramObject);
  
  Object put(String paramString, Object paramObject);
  
  boolean supports(Object paramObject);
  
  Object remove(Object paramObject);
  
  @Deprecated
  Map<String, Object> createMapView();
  
  Map<String, Object> asMap();
  
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.METHOD})
  public static @interface Property {
    String[] value();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\PropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */