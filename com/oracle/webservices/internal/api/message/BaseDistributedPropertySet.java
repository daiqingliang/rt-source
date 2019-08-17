package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseDistributedPropertySet extends BasePropertySet implements DistributedPropertySet {
  private final Map<Class<? extends PropertySet>, PropertySet> satellites = new IdentityHashMap();
  
  private final Map<String, Object> viewthis = super.createView();
  
  public void addSatellite(@NotNull PropertySet paramPropertySet) { addSatellite(paramPropertySet.getClass(), paramPropertySet); }
  
  public void addSatellite(@NotNull Class<? extends PropertySet> paramClass, @NotNull PropertySet paramPropertySet) { this.satellites.put(paramClass, paramPropertySet); }
  
  public void removeSatellite(PropertySet paramPropertySet) { this.satellites.remove(paramPropertySet.getClass()); }
  
  public void copySatelliteInto(@NotNull DistributedPropertySet paramDistributedPropertySet) {
    for (Map.Entry entry : this.satellites.entrySet())
      paramDistributedPropertySet.addSatellite((Class)entry.getKey(), (PropertySet)entry.getValue()); 
  }
  
  public void copySatelliteInto(MessageContext paramMessageContext) { copySatelliteInto(paramMessageContext); }
  
  @Nullable
  public <T extends PropertySet> T getSatellite(Class<T> paramClass) {
    PropertySet propertySet = (PropertySet)this.satellites.get(paramClass);
    if (propertySet != null)
      return (T)propertySet; 
    for (PropertySet propertySet1 : this.satellites.values()) {
      if (paramClass.isInstance(propertySet1))
        return (T)(PropertySet)paramClass.cast(propertySet1); 
      if (DistributedPropertySet.class.isInstance(propertySet1)) {
        propertySet = ((DistributedPropertySet)DistributedPropertySet.class.cast(propertySet1)).getSatellite(paramClass);
        if (propertySet != null)
          return (T)propertySet; 
      } 
    } 
    return null;
  }
  
  public Map<Class<? extends PropertySet>, PropertySet> getSatellites() { return this.satellites; }
  
  public Object get(Object paramObject) {
    for (PropertySet propertySet : this.satellites.values()) {
      if (propertySet.supports(paramObject))
        return propertySet.get(paramObject); 
    } 
    return super.get(paramObject);
  }
  
  public Object put(String paramString, Object paramObject) {
    for (PropertySet propertySet : this.satellites.values()) {
      if (propertySet.supports(paramString))
        return propertySet.put(paramString, paramObject); 
    } 
    return super.put(paramString, paramObject);
  }
  
  public boolean containsKey(Object paramObject) {
    if (this.viewthis.containsKey(paramObject))
      return true; 
    for (PropertySet propertySet : this.satellites.values()) {
      if (propertySet.containsKey(paramObject))
        return true; 
    } 
    return false;
  }
  
  public boolean supports(Object paramObject) {
    for (PropertySet propertySet : this.satellites.values()) {
      if (propertySet.supports(paramObject))
        return true; 
    } 
    return super.supports(paramObject);
  }
  
  public Object remove(Object paramObject) {
    for (PropertySet propertySet : this.satellites.values()) {
      if (propertySet.supports(paramObject))
        return propertySet.remove(paramObject); 
    } 
    return super.remove(paramObject);
  }
  
  protected void createEntrySet(Set<Map.Entry<String, Object>> paramSet) {
    super.createEntrySet(paramSet);
    for (PropertySet propertySet : this.satellites.values())
      ((BasePropertySet)propertySet).createEntrySet(paramSet); 
  }
  
  protected Map<String, Object> asMapLocal() { return this.viewthis; }
  
  protected boolean supportsLocal(Object paramObject) { return super.supports(paramObject); }
  
  protected Map<String, Object> createView() { return new DistributedMapView(); }
  
  class DistributedMapView extends AbstractMap<String, Object> {
    public Object get(Object param1Object) {
      for (PropertySet propertySet : BaseDistributedPropertySet.this.satellites.values()) {
        if (propertySet.supports(param1Object))
          return propertySet.get(param1Object); 
      } 
      return BaseDistributedPropertySet.this.viewthis.get(param1Object);
    }
    
    public int size() {
      int i = BaseDistributedPropertySet.this.viewthis.size();
      for (PropertySet propertySet : BaseDistributedPropertySet.this.satellites.values())
        i += propertySet.asMap().size(); 
      return i;
    }
    
    public boolean containsKey(Object param1Object) {
      if (BaseDistributedPropertySet.this.viewthis.containsKey(param1Object))
        return true; 
      for (PropertySet propertySet : BaseDistributedPropertySet.this.satellites.values()) {
        if (propertySet.containsKey(param1Object))
          return true; 
      } 
      return false;
    }
    
    public Set<Map.Entry<String, Object>> entrySet() {
      HashSet hashSet = new HashSet();
      for (PropertySet propertySet : BaseDistributedPropertySet.this.satellites.values()) {
        for (Map.Entry entry : propertySet.asMap().entrySet())
          hashSet.add(new AbstractMap.SimpleImmutableEntry(entry.getKey(), entry.getValue())); 
      } 
      for (Map.Entry entry : BaseDistributedPropertySet.this.viewthis.entrySet())
        hashSet.add(new AbstractMap.SimpleImmutableEntry(entry.getKey(), entry.getValue())); 
      return hashSet;
    }
    
    public Object put(String param1String, Object param1Object) {
      for (PropertySet propertySet : BaseDistributedPropertySet.this.satellites.values()) {
        if (propertySet.supports(param1String))
          return propertySet.put(param1String, param1Object); 
      } 
      return BaseDistributedPropertySet.this.viewthis.put(param1String, param1Object);
    }
    
    public void clear() {
      BaseDistributedPropertySet.this.satellites.clear();
      BaseDistributedPropertySet.this.viewthis.clear();
    }
    
    public Object remove(Object param1Object) {
      for (PropertySet propertySet : BaseDistributedPropertySet.this.satellites.values()) {
        if (propertySet.supports(param1Object))
          return propertySet.remove(param1Object); 
      } 
      return BaseDistributedPropertySet.this.viewthis.remove(param1Object);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\BaseDistributedPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */