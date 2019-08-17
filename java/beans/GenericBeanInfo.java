package java.beans;

import java.awt.Image;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

class GenericBeanInfo extends SimpleBeanInfo {
  private BeanDescriptor beanDescriptor;
  
  private EventSetDescriptor[] events;
  
  private int defaultEvent;
  
  private PropertyDescriptor[] properties;
  
  private int defaultProperty;
  
  private MethodDescriptor[] methods;
  
  private Reference<BeanInfo> targetBeanInfoRef;
  
  public GenericBeanInfo(BeanDescriptor paramBeanDescriptor, EventSetDescriptor[] paramArrayOfEventSetDescriptor, int paramInt1, PropertyDescriptor[] paramArrayOfPropertyDescriptor, int paramInt2, MethodDescriptor[] paramArrayOfMethodDescriptor, BeanInfo paramBeanInfo) {
    this.beanDescriptor = paramBeanDescriptor;
    this.events = paramArrayOfEventSetDescriptor;
    this.defaultEvent = paramInt1;
    this.properties = paramArrayOfPropertyDescriptor;
    this.defaultProperty = paramInt2;
    this.methods = paramArrayOfMethodDescriptor;
    this.targetBeanInfoRef = (paramBeanInfo != null) ? new SoftReference(paramBeanInfo) : null;
  }
  
  GenericBeanInfo(GenericBeanInfo paramGenericBeanInfo) {
    this.beanDescriptor = new BeanDescriptor(paramGenericBeanInfo.beanDescriptor);
    if (paramGenericBeanInfo.events != null) {
      int i = paramGenericBeanInfo.events.length;
      this.events = new EventSetDescriptor[i];
      for (byte b = 0; b < i; b++)
        this.events[b] = new EventSetDescriptor(paramGenericBeanInfo.events[b]); 
    } 
    this.defaultEvent = paramGenericBeanInfo.defaultEvent;
    if (paramGenericBeanInfo.properties != null) {
      int i = paramGenericBeanInfo.properties.length;
      this.properties = new PropertyDescriptor[i];
      for (byte b = 0; b < i; b++) {
        PropertyDescriptor propertyDescriptor = paramGenericBeanInfo.properties[b];
        if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
          this.properties[b] = new IndexedPropertyDescriptor((IndexedPropertyDescriptor)propertyDescriptor);
        } else {
          this.properties[b] = new PropertyDescriptor(propertyDescriptor);
        } 
      } 
    } 
    this.defaultProperty = paramGenericBeanInfo.defaultProperty;
    if (paramGenericBeanInfo.methods != null) {
      int i = paramGenericBeanInfo.methods.length;
      this.methods = new MethodDescriptor[i];
      for (byte b = 0; b < i; b++)
        this.methods[b] = new MethodDescriptor(paramGenericBeanInfo.methods[b]); 
    } 
    this.targetBeanInfoRef = paramGenericBeanInfo.targetBeanInfoRef;
  }
  
  public PropertyDescriptor[] getPropertyDescriptors() { return this.properties; }
  
  public int getDefaultPropertyIndex() { return this.defaultProperty; }
  
  public EventSetDescriptor[] getEventSetDescriptors() { return this.events; }
  
  public int getDefaultEventIndex() { return this.defaultEvent; }
  
  public MethodDescriptor[] getMethodDescriptors() { return this.methods; }
  
  public BeanDescriptor getBeanDescriptor() { return this.beanDescriptor; }
  
  public Image getIcon(int paramInt) {
    BeanInfo beanInfo = getTargetBeanInfo();
    return (beanInfo != null) ? beanInfo.getIcon(paramInt) : super.getIcon(paramInt);
  }
  
  private BeanInfo getTargetBeanInfo() {
    if (this.targetBeanInfoRef == null)
      return null; 
    BeanInfo beanInfo = (BeanInfo)this.targetBeanInfoRef.get();
    if (beanInfo == null) {
      beanInfo = (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(this.beanDescriptor.getBeanClass());
      if (beanInfo != null)
        this.targetBeanInfoRef = new SoftReference(beanInfo); 
    } 
    return beanInfo;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\GenericBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */