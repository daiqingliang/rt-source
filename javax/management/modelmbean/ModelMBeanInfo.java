package javax.management.modelmbean;

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.RuntimeOperationsException;

public interface ModelMBeanInfo {
  Descriptor[] getDescriptors(String paramString) throws MBeanException, RuntimeOperationsException;
  
  void setDescriptors(Descriptor[] paramArrayOfDescriptor) throws MBeanException, RuntimeOperationsException;
  
  Descriptor getDescriptor(String paramString1, String paramString2) throws MBeanException, RuntimeOperationsException;
  
  void setDescriptor(Descriptor paramDescriptor, String paramString) throws MBeanException, RuntimeOperationsException;
  
  Descriptor getMBeanDescriptor() throws MBeanException, RuntimeOperationsException;
  
  void setMBeanDescriptor(Descriptor paramDescriptor) throws MBeanException, RuntimeOperationsException;
  
  ModelMBeanAttributeInfo getAttribute(String paramString) throws MBeanException, RuntimeOperationsException;
  
  ModelMBeanOperationInfo getOperation(String paramString) throws MBeanException, RuntimeOperationsException;
  
  ModelMBeanNotificationInfo getNotification(String paramString) throws MBeanException, RuntimeOperationsException;
  
  Object clone();
  
  MBeanAttributeInfo[] getAttributes();
  
  String getClassName();
  
  MBeanConstructorInfo[] getConstructors();
  
  String getDescription();
  
  MBeanNotificationInfo[] getNotifications();
  
  MBeanOperationInfo[] getOperations();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\ModelMBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */