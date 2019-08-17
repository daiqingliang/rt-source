package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class MBeanInfo implements Cloneable, Serializable, DescriptorRead {
  static final long serialVersionUID = -6451021435135161911L;
  
  private Descriptor descriptor;
  
  private final String description;
  
  private final String className;
  
  private final MBeanAttributeInfo[] attributes;
  
  private final MBeanOperationInfo[] operations;
  
  private final MBeanConstructorInfo[] constructors;
  
  private final MBeanNotificationInfo[] notifications;
  
  private int hashCode;
  
  private final boolean arrayGettersSafe;
  
  private static final Map<Class<?>, Boolean> arrayGettersSafeMap = new WeakHashMap();
  
  public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo) throws IllegalArgumentException { this(paramString1, paramString2, paramArrayOfMBeanAttributeInfo, paramArrayOfMBeanConstructorInfo, paramArrayOfMBeanOperationInfo, paramArrayOfMBeanNotificationInfo, null); }
  
  public MBeanInfo(String paramString1, String paramString2, MBeanAttributeInfo[] paramArrayOfMBeanAttributeInfo, MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, MBeanOperationInfo[] paramArrayOfMBeanOperationInfo, MBeanNotificationInfo[] paramArrayOfMBeanNotificationInfo, Descriptor paramDescriptor) throws IllegalArgumentException {
    this.className = paramString1;
    this.description = paramString2;
    if (paramArrayOfMBeanAttributeInfo == null)
      paramArrayOfMBeanAttributeInfo = MBeanAttributeInfo.NO_ATTRIBUTES; 
    this.attributes = paramArrayOfMBeanAttributeInfo;
    if (paramArrayOfMBeanOperationInfo == null)
      paramArrayOfMBeanOperationInfo = MBeanOperationInfo.NO_OPERATIONS; 
    this.operations = paramArrayOfMBeanOperationInfo;
    if (paramArrayOfMBeanConstructorInfo == null)
      paramArrayOfMBeanConstructorInfo = MBeanConstructorInfo.NO_CONSTRUCTORS; 
    this.constructors = paramArrayOfMBeanConstructorInfo;
    if (paramArrayOfMBeanNotificationInfo == null)
      paramArrayOfMBeanNotificationInfo = MBeanNotificationInfo.NO_NOTIFICATIONS; 
    this.notifications = paramArrayOfMBeanNotificationInfo;
    if (paramDescriptor == null)
      paramDescriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR; 
    this.descriptor = paramDescriptor;
    this.arrayGettersSafe = arrayGettersSafe(getClass(), MBeanInfo.class);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String getClassName() { return this.className; }
  
  public String getDescription() { return this.description; }
  
  public MBeanAttributeInfo[] getAttributes() {
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = nonNullAttributes();
    return (arrayOfMBeanAttributeInfo.length == 0) ? arrayOfMBeanAttributeInfo : (MBeanAttributeInfo[])arrayOfMBeanAttributeInfo.clone();
  }
  
  private MBeanAttributeInfo[] fastGetAttributes() { return this.arrayGettersSafe ? nonNullAttributes() : getAttributes(); }
  
  private MBeanAttributeInfo[] nonNullAttributes() { return (this.attributes == null) ? MBeanAttributeInfo.NO_ATTRIBUTES : this.attributes; }
  
  public MBeanOperationInfo[] getOperations() {
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = nonNullOperations();
    return (arrayOfMBeanOperationInfo.length == 0) ? arrayOfMBeanOperationInfo : (MBeanOperationInfo[])arrayOfMBeanOperationInfo.clone();
  }
  
  private MBeanOperationInfo[] fastGetOperations() { return this.arrayGettersSafe ? nonNullOperations() : getOperations(); }
  
  private MBeanOperationInfo[] nonNullOperations() { return (this.operations == null) ? MBeanOperationInfo.NO_OPERATIONS : this.operations; }
  
  public MBeanConstructorInfo[] getConstructors() {
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = nonNullConstructors();
    return (arrayOfMBeanConstructorInfo.length == 0) ? arrayOfMBeanConstructorInfo : (MBeanConstructorInfo[])arrayOfMBeanConstructorInfo.clone();
  }
  
  private MBeanConstructorInfo[] fastGetConstructors() { return this.arrayGettersSafe ? nonNullConstructors() : getConstructors(); }
  
  private MBeanConstructorInfo[] nonNullConstructors() { return (this.constructors == null) ? MBeanConstructorInfo.NO_CONSTRUCTORS : this.constructors; }
  
  public MBeanNotificationInfo[] getNotifications() {
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = nonNullNotifications();
    return (arrayOfMBeanNotificationInfo.length == 0) ? arrayOfMBeanNotificationInfo : (MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone();
  }
  
  private MBeanNotificationInfo[] fastGetNotifications() { return this.arrayGettersSafe ? nonNullNotifications() : getNotifications(); }
  
  private MBeanNotificationInfo[] nonNullNotifications() { return (this.notifications == null) ? MBeanNotificationInfo.NO_NOTIFICATIONS : this.notifications; }
  
  public Descriptor getDescriptor() { return (Descriptor)ImmutableDescriptor.nonNullDescriptor(this.descriptor).clone(); }
  
  public String toString() { return getClass().getName() + "[description=" + getDescription() + ", attributes=" + Arrays.asList(fastGetAttributes()) + ", constructors=" + Arrays.asList(fastGetConstructors()) + ", operations=" + Arrays.asList(fastGetOperations()) + ", notifications=" + Arrays.asList(fastGetNotifications()) + ", descriptor=" + getDescriptor() + "]"; }
  
  public boolean equals(Object paramObject) {
    MBeanInfo mBeanInfo;
    return (paramObject == this) ? true : (!(paramObject instanceof MBeanInfo) ? false : ((!(mBeanInfo = (MBeanInfo)paramObject).isEqual(getClassName(), mBeanInfo.getClassName()) || !isEqual(getDescription(), mBeanInfo.getDescription()) || !getDescriptor().equals(mBeanInfo.getDescriptor())) ? false : ((Arrays.equals(mBeanInfo.fastGetAttributes(), fastGetAttributes()) && Arrays.equals(mBeanInfo.fastGetOperations(), fastGetOperations()) && Arrays.equals(mBeanInfo.fastGetConstructors(), fastGetConstructors()) && Arrays.equals(mBeanInfo.fastGetNotifications(), fastGetNotifications())))));
  }
  
  public int hashCode() {
    if (this.hashCode != 0)
      return this.hashCode; 
    this.hashCode = Objects.hash(new Object[] { getClassName(), getDescriptor() }) ^ Arrays.hashCode(fastGetAttributes()) ^ Arrays.hashCode(fastGetOperations()) ^ Arrays.hashCode(fastGetConstructors()) ^ Arrays.hashCode(fastGetNotifications());
    return this.hashCode;
  }
  
  static boolean arrayGettersSafe(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == paramClass2)
      return true; 
    synchronized (arrayGettersSafeMap) {
      Boolean bool = (Boolean)arrayGettersSafeMap.get(paramClass1);
      if (bool == null) {
        try {
          ArrayGettersSafeAction arrayGettersSafeAction = new ArrayGettersSafeAction(paramClass1, paramClass2);
          bool = (Boolean)AccessController.doPrivileged(arrayGettersSafeAction);
        } catch (Exception exception) {
          bool = Boolean.valueOf(false);
        } 
        arrayGettersSafeMap.put(paramClass1, bool);
      } 
      return bool.booleanValue();
    } 
  }
  
  private static boolean isEqual(String paramString1, String paramString2) {
    boolean bool;
    if (paramString1 == null) {
      bool = (paramString2 == null);
    } else {
      bool = paramString1.equals(paramString2);
    } 
    return bool;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.descriptor.getClass() == ImmutableDescriptor.class) {
      paramObjectOutputStream.write(1);
      String[] arrayOfString = this.descriptor.getFieldNames();
      paramObjectOutputStream.writeObject(arrayOfString);
      paramObjectOutputStream.writeObject(this.descriptor.getFieldValues(arrayOfString));
    } else {
      paramObjectOutputStream.write(0);
      paramObjectOutputStream.writeObject(this.descriptor);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    Object[] arrayOfObject;
    String[] arrayOfString;
    paramObjectInputStream.defaultReadObject();
    switch (paramObjectInputStream.read()) {
      case 1:
        arrayOfString = (String[])paramObjectInputStream.readObject();
        arrayOfObject = (Object[])paramObjectInputStream.readObject();
        this.descriptor = (arrayOfString.length == 0) ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(arrayOfString, arrayOfObject);
        return;
      case 0:
        this.descriptor = (Descriptor)paramObjectInputStream.readObject();
        if (this.descriptor == null)
          this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR; 
        return;
      case -1:
        this.descriptor = ImmutableDescriptor.EMPTY_DESCRIPTOR;
        return;
    } 
    throw new StreamCorruptedException("Got unexpected byte.");
  }
  
  private static class ArrayGettersSafeAction extends Object implements PrivilegedAction<Boolean> {
    private final Class<?> subclass;
    
    private final Class<?> immutableClass;
    
    ArrayGettersSafeAction(Class<?> param1Class1, Class<?> param1Class2) {
      this.subclass = param1Class1;
      this.immutableClass = param1Class2;
    }
    
    public Boolean run() {
      Method[] arrayOfMethod = this.immutableClass.getMethods();
      for (byte b = 0; b < arrayOfMethod.length; b++) {
        Method method = arrayOfMethod[b];
        String str = method.getName();
        if (str.startsWith("get") && method.getParameterTypes().length == 0 && method.getReturnType().isArray())
          try {
            Method method1 = this.subclass.getMethod(str, new Class[0]);
            if (!method1.equals(method))
              return Boolean.valueOf(false); 
          } catch (NoSuchMethodException noSuchMethodException) {
            return Boolean.valueOf(false);
          }  
      } 
      return Boolean.valueOf(true);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */