package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BasePropertySet implements PropertySet {
  private Map<String, Object> mapView;
  
  protected abstract PropertyMap getPropertyMap();
  
  protected static PropertyMap parse(final Class clazz) { return (PropertyMap)AccessController.doPrivileged(new PrivilegedAction<PropertyMap>() {
          public BasePropertySet.PropertyMap run() {
            BasePropertySet.PropertyMap propertyMap = new BasePropertySet.PropertyMap();
            for (Class clazz = clazz; clazz != null; clazz = clazz.getSuperclass()) {
              for (Field field : clazz.getDeclaredFields()) {
                PropertySet.Property property = (PropertySet.Property)field.getAnnotation(PropertySet.Property.class);
                if (property != null)
                  for (String str : property.value())
                    propertyMap.put(str, new BasePropertySet.FieldAccessor(field, str));  
              } 
              for (Method method : clazz.getDeclaredMethods()) {
                PropertySet.Property property = (PropertySet.Property)method.getAnnotation(PropertySet.Property.class);
                if (property != null) {
                  Method method1;
                  String str1 = method.getName();
                  assert str1.startsWith("get") || str1.startsWith("is");
                  String str2 = str1.startsWith("is") ? ("set" + str1.substring(2)) : ('s' + str1.substring(1));
                  try {
                    method1 = clazz.getMethod(str2, new Class[] { method.getReturnType() });
                  } catch (NoSuchMethodException noSuchMethodException) {
                    method1 = null;
                  } 
                  for (String str : property.value())
                    propertyMap.put(str, new BasePropertySet.MethodAccessor(method, method1, str)); 
                } 
              } 
            } 
            return propertyMap;
          }
        }); }
  
  public boolean containsKey(Object paramObject) {
    Accessor accessor = (Accessor)getPropertyMap().get(paramObject);
    return (accessor != null) ? ((accessor.get(this) != null)) : false;
  }
  
  public Object get(Object paramObject) {
    Accessor accessor = (Accessor)getPropertyMap().get(paramObject);
    if (accessor != null)
      return accessor.get(this); 
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  public Object put(String paramString, Object paramObject) {
    Accessor accessor = (Accessor)getPropertyMap().get(paramString);
    if (accessor != null) {
      Object object = accessor.get(this);
      accessor.set(this, paramObject);
      return object;
    } 
    throw new IllegalArgumentException("Undefined property " + paramString);
  }
  
  public boolean supports(Object paramObject) { return getPropertyMap().containsKey(paramObject); }
  
  public Object remove(Object paramObject) {
    Accessor accessor = (Accessor)getPropertyMap().get(paramObject);
    if (accessor != null) {
      Object object = accessor.get(this);
      accessor.set(this, null);
      return object;
    } 
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  @Deprecated
  public final Map<String, Object> createMapView() {
    final HashSet core = new HashSet();
    createEntrySet(hashSet);
    return new AbstractMap<String, Object>() {
        public Set<Map.Entry<String, Object>> entrySet() { return core; }
      };
  }
  
  public Map<String, Object> asMap() {
    if (this.mapView == null)
      this.mapView = createView(); 
    return this.mapView;
  }
  
  protected Map<String, Object> createView() { return new MapView(mapAllowsAdditionalProperties()); }
  
  protected boolean mapAllowsAdditionalProperties() { return false; }
  
  protected void createEntrySet(Set<Map.Entry<String, Object>> paramSet) {
    for (Map.Entry entry : getPropertyMap().entrySet()) {
      paramSet.add(new Map.Entry<String, Object>() {
            public String getKey() { return (String)e.getKey(); }
            
            public Object getValue() { return ((BasePropertySet.Accessor)e.getValue()).get(BasePropertySet.this); }
            
            public Object setValue(Object param1Object) {
              BasePropertySet.Accessor accessor = (BasePropertySet.Accessor)e.getValue();
              Object object = accessor.get(BasePropertySet.this);
              accessor.set(BasePropertySet.this, param1Object);
              return object;
            }
          });
    } 
  }
  
  protected static interface Accessor {
    String getName();
    
    boolean hasValue(PropertySet param1PropertySet);
    
    Object get(PropertySet param1PropertySet);
    
    void set(PropertySet param1PropertySet, Object param1Object);
  }
  
  static final class FieldAccessor implements Accessor {
    private final Field f;
    
    private final String name;
    
    protected FieldAccessor(Field param1Field, String param1String) {
      this.f = param1Field;
      param1Field.setAccessible(true);
      this.name = param1String;
    }
    
    public String getName() { return this.name; }
    
    public boolean hasValue(PropertySet param1PropertySet) { return (get(param1PropertySet) != null); }
    
    public Object get(PropertySet param1PropertySet) {
      try {
        return this.f.get(param1PropertySet);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError();
      } 
    }
    
    public void set(PropertySet param1PropertySet, Object param1Object) {
      try {
        this.f.set(param1PropertySet, param1Object);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError();
      } 
    }
  }
  
  final class MapView extends HashMap<String, Object> {
    boolean extensible;
    
    MapView(boolean param1Boolean) {
      super(this$0.getPropertyMap().getPropertyMapEntries().length);
      this.extensible = param1Boolean;
      initialize();
    }
    
    public void initialize() {
      BasePropertySet.PropertyMapEntry[] arrayOfPropertyMapEntry = BasePropertySet.this.getPropertyMap().getPropertyMapEntries();
      for (BasePropertySet.PropertyMapEntry propertyMapEntry : arrayOfPropertyMapEntry)
        super.put(propertyMapEntry.key, propertyMapEntry.value); 
    }
    
    public Object get(Object param1Object) {
      Object object = super.get(param1Object);
      return (object instanceof BasePropertySet.Accessor) ? ((BasePropertySet.Accessor)object).get(BasePropertySet.this) : object;
    }
    
    public Set<Map.Entry<String, Object>> entrySet() {
      HashSet hashSet = new HashSet();
      for (String str : keySet())
        hashSet.add(new AbstractMap.SimpleImmutableEntry(str, get(str))); 
      return hashSet;
    }
    
    public Object put(String param1String, Object param1Object) {
      Object object = super.get(param1String);
      if (object != null && object instanceof BasePropertySet.Accessor) {
        Object object1 = ((BasePropertySet.Accessor)object).get(BasePropertySet.this);
        ((BasePropertySet.Accessor)object).set(BasePropertySet.this, param1Object);
        return object1;
      } 
      if (this.extensible)
        return super.put(param1String, param1Object); 
      throw new IllegalStateException("Unknown property [" + param1String + "] for PropertySet [" + BasePropertySet.this.getClass().getName() + "]");
    }
    
    public void clear() {
      for (String str : keySet())
        remove(str); 
    }
    
    public Object remove(Object param1Object) {
      Object object = super.get(param1Object);
      if (object instanceof BasePropertySet.Accessor)
        ((BasePropertySet.Accessor)object).set(BasePropertySet.this, null); 
      return super.remove(param1Object);
    }
  }
  
  static final class MethodAccessor implements Accessor {
    @NotNull
    private final Method getter;
    
    @Nullable
    private final Method setter;
    
    private final String name;
    
    protected MethodAccessor(Method param1Method1, Method param1Method2, String param1String) {
      this.getter = param1Method1;
      this.setter = param1Method2;
      this.name = param1String;
      param1Method1.setAccessible(true);
      if (param1Method2 != null)
        param1Method2.setAccessible(true); 
    }
    
    public String getName() { return this.name; }
    
    public boolean hasValue(PropertySet param1PropertySet) { return (get(param1PropertySet) != null); }
    
    public Object get(PropertySet param1PropertySet) {
      try {
        return this.getter.invoke(param1PropertySet, new Object[0]);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError();
      } catch (InvocationTargetException invocationTargetException) {
        handle(invocationTargetException);
        return Integer.valueOf(0);
      } 
    }
    
    public void set(PropertySet param1PropertySet, Object param1Object) {
      if (this.setter == null)
        throw new ReadOnlyPropertyException(getName()); 
      try {
        this.setter.invoke(param1PropertySet, new Object[] { param1Object });
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError();
      } catch (InvocationTargetException invocationTargetException) {
        handle(invocationTargetException);
      } 
    }
    
    private Exception handle(InvocationTargetException param1InvocationTargetException) {
      Throwable throwable = param1InvocationTargetException.getTargetException();
      if (throwable instanceof Error)
        throw (Error)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      throw new Error(param1InvocationTargetException);
    }
  }
  
  protected static class PropertyMap extends HashMap<String, Accessor> {
    BasePropertySet.PropertyMapEntry[] cachedEntries = null;
    
    BasePropertySet.PropertyMapEntry[] getPropertyMapEntries() {
      if (this.cachedEntries == null)
        this.cachedEntries = createPropertyMapEntries(); 
      return this.cachedEntries;
    }
    
    private BasePropertySet.PropertyMapEntry[] createPropertyMapEntries() {
      BasePropertySet.PropertyMapEntry[] arrayOfPropertyMapEntry = new BasePropertySet.PropertyMapEntry[size()];
      byte b = 0;
      for (Map.Entry entry : entrySet())
        arrayOfPropertyMapEntry[b++] = new BasePropertySet.PropertyMapEntry((String)entry.getKey(), (BasePropertySet.Accessor)entry.getValue()); 
      return arrayOfPropertyMapEntry;
    }
  }
  
  public static class PropertyMapEntry {
    String key;
    
    BasePropertySet.Accessor value;
    
    public PropertyMapEntry(String param1String, BasePropertySet.Accessor param1Accessor) {
      this.key = param1String;
      this.value = param1Accessor;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\BasePropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */