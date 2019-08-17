package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import java.util.Map;
import java.util.Set;

public abstract class PropertySet extends BasePropertySet {
  protected static PropertyMap parse(Class paramClass) {
    BasePropertySet.PropertyMap propertyMap = BasePropertySet.parse(paramClass);
    PropertyMap propertyMap1 = new PropertyMap();
    propertyMap1.putAll(propertyMap);
    return propertyMap1;
  }
  
  public Object get(Object paramObject) {
    BasePropertySet.Accessor accessor = (BasePropertySet.Accessor)getPropertyMap().get(paramObject);
    if (accessor != null)
      return accessor.get(this); 
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  public Object put(String paramString, Object paramObject) {
    BasePropertySet.Accessor accessor = (BasePropertySet.Accessor)getPropertyMap().get(paramString);
    if (accessor != null) {
      Object object = accessor.get(this);
      accessor.set(this, paramObject);
      return object;
    } 
    throw new IllegalArgumentException("Undefined property " + paramString);
  }
  
  public boolean supports(Object paramObject) { return getPropertyMap().containsKey(paramObject); }
  
  public Object remove(Object paramObject) {
    BasePropertySet.Accessor accessor = (BasePropertySet.Accessor)getPropertyMap().get(paramObject);
    if (accessor != null) {
      Object object = accessor.get(this);
      accessor.set(this, null);
      return object;
    } 
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  protected void createEntrySet(Set<Map.Entry<String, Object>> paramSet) {
    for (Map.Entry entry : getPropertyMap().entrySet()) {
      paramSet.add(new Map.Entry<String, Object>() {
            public String getKey() { return (String)e.getKey(); }
            
            public Object getValue() { return ((BasePropertySet.Accessor)e.getValue()).get(PropertySet.this); }
            
            public Object setValue(Object param1Object) {
              BasePropertySet.Accessor accessor = (BasePropertySet.Accessor)e.getValue();
              Object object = accessor.get(PropertySet.this);
              accessor.set(PropertySet.this, param1Object);
              return object;
            }
          });
    } 
  }
  
  protected abstract PropertyMap getPropertyMap();
  
  protected static class PropertyMap extends BasePropertySet.PropertyMap {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\PropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */