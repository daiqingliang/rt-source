package java.beans;

import java.lang.ref.Reference;

public class BeanDescriptor extends FeatureDescriptor {
  private Reference<? extends Class<?>> beanClassRef;
  
  private Reference<? extends Class<?>> customizerClassRef;
  
  public BeanDescriptor(Class<?> paramClass) { this(paramClass, null); }
  
  public BeanDescriptor(Class<?> paramClass1, Class<?> paramClass2) {
    this.beanClassRef = getWeakReference(paramClass1);
    this.customizerClassRef = getWeakReference(paramClass2);
    String str;
    for (str = paramClass1.getName(); str.indexOf('.') >= 0; str = str.substring(str.indexOf('.') + 1));
    setName(str);
  }
  
  public Class<?> getBeanClass() { return (this.beanClassRef != null) ? (Class)this.beanClassRef.get() : null; }
  
  public Class<?> getCustomizerClass() { return (this.customizerClassRef != null) ? (Class)this.customizerClassRef.get() : null; }
  
  BeanDescriptor(BeanDescriptor paramBeanDescriptor) {
    super(paramBeanDescriptor);
    this.beanClassRef = paramBeanDescriptor.beanClassRef;
    this.customizerClassRef = paramBeanDescriptor.customizerClassRef;
  }
  
  void appendTo(StringBuilder paramStringBuilder) {
    appendTo(paramStringBuilder, "beanClass", this.beanClassRef);
    appendTo(paramStringBuilder, "customizerClass", this.customizerClassRef);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\BeanDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */