package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.Nullable;
import java.util.Map;

public interface DistributedPropertySet extends PropertySet {
  @Nullable
  <T extends PropertySet> T getSatellite(Class<T> paramClass);
  
  Map<Class<? extends PropertySet>, PropertySet> getSatellites();
  
  void addSatellite(PropertySet paramPropertySet);
  
  void addSatellite(Class<? extends PropertySet> paramClass, PropertySet paramPropertySet);
  
  void removeSatellite(PropertySet paramPropertySet);
  
  void copySatelliteInto(MessageContext paramMessageContext);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\DistributedPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */