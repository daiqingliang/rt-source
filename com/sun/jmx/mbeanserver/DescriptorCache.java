package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.JMX;

public class DescriptorCache {
  private static final DescriptorCache instance = new DescriptorCache();
  
  private final WeakHashMap<ImmutableDescriptor, WeakReference<ImmutableDescriptor>> map = new WeakHashMap();
  
  static DescriptorCache getInstance() { return instance; }
  
  public static DescriptorCache getInstance(JMX paramJMX) { return (paramJMX != null) ? instance : null; }
  
  public ImmutableDescriptor get(ImmutableDescriptor paramImmutableDescriptor) {
    WeakReference weakReference = (WeakReference)this.map.get(paramImmutableDescriptor);
    ImmutableDescriptor immutableDescriptor = (weakReference == null) ? null : (ImmutableDescriptor)weakReference.get();
    if (immutableDescriptor != null)
      return immutableDescriptor; 
    this.map.put(paramImmutableDescriptor, new WeakReference(paramImmutableDescriptor));
    return paramImmutableDescriptor;
  }
  
  public ImmutableDescriptor union(Descriptor... paramVarArgs) { return get(ImmutableDescriptor.union(paramVarArgs)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\DescriptorCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */