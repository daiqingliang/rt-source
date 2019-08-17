package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.sun.istack.internal.NotNull;

public abstract class DistributedPropertySet extends BaseDistributedPropertySet {
  public void addSatellite(@NotNull PropertySet paramPropertySet) { addSatellite(paramPropertySet); }
  
  public void addSatellite(@NotNull Class paramClass, @NotNull PropertySet paramPropertySet) { addSatellite(paramClass, paramPropertySet); }
  
  public void copySatelliteInto(@NotNull DistributedPropertySet paramDistributedPropertySet) { copySatelliteInto(paramDistributedPropertySet); }
  
  public void removeSatellite(PropertySet paramPropertySet) { removeSatellite(paramPropertySet); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\DistributedPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */