package javax.management.openmbean;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

public interface OpenMBeanInfo {
  String getClassName();
  
  String getDescription();
  
  MBeanAttributeInfo[] getAttributes();
  
  MBeanOperationInfo[] getOperations();
  
  MBeanConstructorInfo[] getConstructors();
  
  MBeanNotificationInfo[] getNotifications();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\OpenMBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */