package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.spi.copyobject.ObjectCopier;
import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;

public class FallbackObjectCopierImpl implements ObjectCopier {
  private ObjectCopier first;
  
  private ObjectCopier second;
  
  public FallbackObjectCopierImpl(ObjectCopier paramObjectCopier1, ObjectCopier paramObjectCopier2) {
    this.first = paramObjectCopier1;
    this.second = paramObjectCopier2;
  }
  
  public Object copy(Object paramObject) throws ReflectiveCopyException {
    try {
      return this.first.copy(paramObject);
    } catch (ReflectiveCopyException reflectiveCopyException) {
      return this.second.copy(paramObject);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\copyobject\FallbackObjectCopierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */