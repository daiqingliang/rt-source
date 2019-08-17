package com.sun.jmx.mbeanserver;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;

public abstract class MXBeanMappingFactory {
  public static final MXBeanMappingFactory DEFAULT = new DefaultMXBeanMappingFactory();
  
  public abstract MXBeanMapping mappingForType(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MXBeanMappingFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */