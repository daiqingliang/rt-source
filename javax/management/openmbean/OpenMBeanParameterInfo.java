package javax.management.openmbean;

import java.util.Set;

public interface OpenMBeanParameterInfo {
  String getDescription();
  
  String getName();
  
  OpenType<?> getOpenType();
  
  Object getDefaultValue();
  
  Set<?> getLegalValues();
  
  Comparable<?> getMinValue();
  
  Comparable<?> getMaxValue();
  
  boolean hasDefaultValue();
  
  boolean hasLegalValues();
  
  boolean hasMinValue();
  
  boolean hasMaxValue();
  
  boolean isValue(Object paramObject);
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\OpenMBeanParameterInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */