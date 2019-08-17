package javax.management.openmbean;

public interface OpenMBeanAttributeInfo extends OpenMBeanParameterInfo {
  boolean isReadable();
  
  boolean isWritable();
  
  boolean isIs();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\OpenMBeanAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */