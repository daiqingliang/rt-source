package javax.management.openmbean;

import javax.management.MBeanParameterInfo;

public interface OpenMBeanOperationInfo {
  String getDescription();
  
  String getName();
  
  MBeanParameterInfo[] getSignature();
  
  int getImpact();
  
  String getReturnType();
  
  OpenType<?> getReturnOpenType();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\OpenMBeanOperationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */