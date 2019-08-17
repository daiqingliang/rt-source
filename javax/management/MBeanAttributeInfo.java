package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Introspector;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Objects;

public class MBeanAttributeInfo extends MBeanFeatureInfo implements Cloneable {
  private static final long serialVersionUID;
  
  static final MBeanAttributeInfo[] NO_ATTRIBUTES;
  
  private final String attributeType;
  
  private final boolean isWrite;
  
  private final boolean isRead;
  
  private final boolean is;
  
  public MBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) { this(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2, paramBoolean3, (Descriptor)null); }
  
  public MBeanAttributeInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Descriptor paramDescriptor) {
    super(paramString1, paramString3, paramDescriptor);
    this.attributeType = paramString2;
    this.isRead = paramBoolean1;
    this.isWrite = paramBoolean2;
    if (paramBoolean3 && !paramBoolean1)
      throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-readable attribute"); 
    if (paramBoolean3 && !paramString2.equals("java.lang.Boolean") && !paramString2.equals("boolean"))
      throw new IllegalArgumentException("Cannot have an \"is\" getter for a non-boolean attribute"); 
    this.is = paramBoolean3;
  }
  
  public MBeanAttributeInfo(String paramString1, String paramString2, Method paramMethod1, Method paramMethod2) throws IntrospectionException { this(paramString1, attributeType(paramMethod1, paramMethod2), paramString2, (paramMethod1 != null), (paramMethod2 != null), isIs(paramMethod1), ImmutableDescriptor.union(new Descriptor[] { Introspector.descriptorForElement(paramMethod1), Introspector.descriptorForElement(paramMethod2) })); }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String getType() { return this.attributeType; }
  
  public boolean isReadable() { return this.isRead; }
  
  public boolean isWritable() { return this.isWrite; }
  
  public boolean isIs() { return this.is; }
  
  public String toString() {
    String str;
    if (isReadable()) {
      if (isWritable()) {
        str = "read/write";
      } else {
        str = "read-only";
      } 
    } else if (isWritable()) {
      str = "write-only";
    } else {
      str = "no-access";
    } 
    return getClass().getName() + "[description=" + getDescription() + ", name=" + getName() + ", type=" + getType() + ", " + str + ", " + (isIs() ? "isIs, " : "") + "descriptor=" + getDescriptor() + "]";
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanAttributeInfo))
      return false; 
    MBeanAttributeInfo mBeanAttributeInfo = (MBeanAttributeInfo)paramObject;
    return (Objects.equals(mBeanAttributeInfo.getName(), getName()) && Objects.equals(mBeanAttributeInfo.getType(), getType()) && Objects.equals(mBeanAttributeInfo.getDescription(), getDescription()) && Objects.equals(mBeanAttributeInfo.getDescriptor(), getDescriptor()) && mBeanAttributeInfo.isReadable() == isReadable() && mBeanAttributeInfo.isWritable() == isWritable() && mBeanAttributeInfo.isIs() == isIs());
  }
  
  public int hashCode() { return Objects.hash(new Object[] { getName(), getType() }); }
  
  private static boolean isIs(Method paramMethod) { return (paramMethod != null && paramMethod.getName().startsWith("is") && (paramMethod.getReturnType().equals(boolean.class) || paramMethod.getReturnType().equals(Boolean.class))); }
  
  private static String attributeType(Method paramMethod1, Method paramMethod2) throws IntrospectionException {
    Class clazz = null;
    if (paramMethod1 != null) {
      if (paramMethod1.getParameterTypes().length != 0)
        throw new IntrospectionException("bad getter arg count"); 
      clazz = paramMethod1.getReturnType();
      if (clazz == void.class)
        throw new IntrospectionException("getter " + paramMethod1.getName() + " returns void"); 
    } 
    if (paramMethod2 != null) {
      Class[] arrayOfClass = paramMethod2.getParameterTypes();
      if (arrayOfClass.length != 1)
        throw new IntrospectionException("bad setter arg count"); 
      if (clazz == null) {
        clazz = arrayOfClass[0];
      } else if (clazz != arrayOfClass[false]) {
        throw new IntrospectionException("type mismatch between getter and setter");
      } 
    } 
    if (clazz == null)
      throw new IntrospectionException("getter and setter cannot both be null"); 
    return clazz.getName();
  }
  
  static  {
    long l = 8644704819898565848L;
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      if ("1.0".equals(str))
        l = 7043855487133450673L; 
    } catch (Exception exception) {}
    serialVersionUID = l;
    NO_ATTRIBUTES = new MBeanAttributeInfo[0];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */