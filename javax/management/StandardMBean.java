package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.DescriptorCache;
import com.sun.jmx.mbeanserver.Introspector;
import com.sun.jmx.mbeanserver.MBeanSupport;
import com.sun.jmx.mbeanserver.MXBeanSupport;
import com.sun.jmx.mbeanserver.StandardMBeanSupport;
import com.sun.jmx.mbeanserver.Util;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;

public class StandardMBean implements DynamicMBean, MBeanRegistration {
  private static final DescriptorCache descriptors = DescriptorCache.getInstance(JMX.proof);
  
  private static final Map<Class<?>, Boolean> mbeanInfoSafeMap = new WeakHashMap();
  
  private <T> void construct(T paramT, Class<T> paramClass, boolean paramBoolean1, boolean paramBoolean2) throws NotCompliantMBeanException {
    if (paramT == null)
      if (paramBoolean1) {
        paramT = (T)Util.cast(this);
      } else {
        throw new IllegalArgumentException("implementation is null");
      }  
    if (paramBoolean2) {
      if (paramClass == null)
        paramClass = (Class)Util.cast(Introspector.getMXBeanInterface(paramT.getClass())); 
      this.mbean = new MXBeanSupport(paramT, paramClass);
    } else {
      if (paramClass == null)
        paramClass = (Class)Util.cast(Introspector.getStandardMBeanInterface(paramT.getClass())); 
      this.mbean = new StandardMBeanSupport(paramT, paramClass);
    } 
  }
  
  public <T> StandardMBean(T paramT, Class<T> paramClass) throws NotCompliantMBeanException { construct(paramT, paramClass, false, false); }
  
  protected StandardMBean(Class<?> paramClass) throws NotCompliantMBeanException { construct(null, paramClass, true, false); }
  
  public <T> StandardMBean(T paramT, Class<T> paramClass, boolean paramBoolean) {
    try {
      construct(paramT, paramClass, false, paramBoolean);
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw new IllegalArgumentException(notCompliantMBeanException);
    } 
  }
  
  protected StandardMBean(Class<?> paramClass, boolean paramBoolean) {
    try {
      construct(null, paramClass, true, paramBoolean);
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      throw new IllegalArgumentException(notCompliantMBeanException);
    } 
  }
  
  public void setImplementation(Object paramObject) throws NotCompliantMBeanException {
    if (paramObject == null)
      throw new IllegalArgumentException("implementation is null"); 
    if (isMXBean()) {
      this.mbean = new MXBeanSupport(paramObject, (Class)Util.cast(getMBeanInterface()));
    } else {
      this.mbean = new StandardMBeanSupport(paramObject, (Class)Util.cast(getMBeanInterface()));
    } 
  }
  
  public Object getImplementation() { return this.mbean.getResource(); }
  
  public final Class<?> getMBeanInterface() { return this.mbean.getMBeanInterface(); }
  
  public Class<?> getImplementationClass() { return this.mbean.getResource().getClass(); }
  
  public Object getAttribute(String paramString) throws AttributeNotFoundException, MBeanException, ReflectionException { return this.mbean.getAttribute(paramString); }
  
  public void setAttribute(Attribute paramAttribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException { this.mbean.setAttribute(paramAttribute); }
  
  public AttributeList getAttributes(String[] paramArrayOfString) { return this.mbean.getAttributes(paramArrayOfString); }
  
  public AttributeList setAttributes(AttributeList paramAttributeList) { return this.mbean.setAttributes(paramAttributeList); }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws MBeanException, ReflectionException { return this.mbean.invoke(paramString, paramArrayOfObject, paramArrayOfString); }
  
  public MBeanInfo getMBeanInfo() {
    try {
      MBeanInfo mBeanInfo = getCachedMBeanInfo();
      if (mBeanInfo != null)
        return mBeanInfo; 
    } catch (RuntimeException runtimeException) {
      if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", "Failed to get cached MBeanInfo", runtimeException); 
    } 
    if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MISC_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "getMBeanInfo", "Building MBeanInfo for " + getImplementationClass().getName()); 
    MBeanSupport mBeanSupport = this.mbean;
    MBeanInfo mBeanInfo1 = mBeanSupport.getMBeanInfo();
    Object object = mBeanSupport.getResource();
    boolean bool = immutableInfo(getClass());
    String str1 = getClassName(mBeanInfo1);
    String str2 = getDescription(mBeanInfo1);
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = getConstructors(mBeanInfo1, object);
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = getAttributes(mBeanInfo1);
    MBeanOperationInfo[] arrayOfMBeanOperationInfo = getOperations(mBeanInfo1);
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = getNotifications(mBeanInfo1);
    Descriptor descriptor = getDescriptor(mBeanInfo1, bool);
    MBeanInfo mBeanInfo2 = new MBeanInfo(str1, str2, arrayOfMBeanAttributeInfo, arrayOfMBeanConstructorInfo, arrayOfMBeanOperationInfo, arrayOfMBeanNotificationInfo, descriptor);
    try {
      cacheMBeanInfo(mBeanInfo2);
    } catch (RuntimeException runtimeException) {
      if (JmxProperties.MISC_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MISC_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "getMBeanInfo", "Failed to cache MBeanInfo", runtimeException); 
    } 
    return mBeanInfo2;
  }
  
  protected String getClassName(MBeanInfo paramMBeanInfo) { return (paramMBeanInfo == null) ? getImplementationClass().getName() : paramMBeanInfo.getClassName(); }
  
  protected String getDescription(MBeanInfo paramMBeanInfo) { return (paramMBeanInfo == null) ? null : paramMBeanInfo.getDescription(); }
  
  protected String getDescription(MBeanFeatureInfo paramMBeanFeatureInfo) { return (paramMBeanFeatureInfo == null) ? null : paramMBeanFeatureInfo.getDescription(); }
  
  protected String getDescription(MBeanAttributeInfo paramMBeanAttributeInfo) { return getDescription(paramMBeanAttributeInfo); }
  
  protected String getDescription(MBeanConstructorInfo paramMBeanConstructorInfo) { return getDescription(paramMBeanConstructorInfo); }
  
  protected String getDescription(MBeanConstructorInfo paramMBeanConstructorInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt) { return (paramMBeanParameterInfo == null) ? null : paramMBeanParameterInfo.getDescription(); }
  
  protected String getParameterName(MBeanConstructorInfo paramMBeanConstructorInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt) { return (paramMBeanParameterInfo == null) ? null : paramMBeanParameterInfo.getName(); }
  
  protected String getDescription(MBeanOperationInfo paramMBeanOperationInfo) { return getDescription(paramMBeanOperationInfo); }
  
  protected int getImpact(MBeanOperationInfo paramMBeanOperationInfo) { return (paramMBeanOperationInfo == null) ? 3 : paramMBeanOperationInfo.getImpact(); }
  
  protected String getParameterName(MBeanOperationInfo paramMBeanOperationInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt) { return (paramMBeanParameterInfo == null) ? null : paramMBeanParameterInfo.getName(); }
  
  protected String getDescription(MBeanOperationInfo paramMBeanOperationInfo, MBeanParameterInfo paramMBeanParameterInfo, int paramInt) { return (paramMBeanParameterInfo == null) ? null : paramMBeanParameterInfo.getDescription(); }
  
  protected MBeanConstructorInfo[] getConstructors(MBeanConstructorInfo[] paramArrayOfMBeanConstructorInfo, Object paramObject) { return (paramArrayOfMBeanConstructorInfo == null) ? null : ((paramObject != null && paramObject != this) ? null : paramArrayOfMBeanConstructorInfo); }
  
  MBeanNotificationInfo[] getNotifications(MBeanInfo paramMBeanInfo) { return null; }
  
  Descriptor getDescriptor(MBeanInfo paramMBeanInfo, boolean paramBoolean) {
    ImmutableDescriptor immutableDescriptor;
    if (paramMBeanInfo == null || paramMBeanInfo.getDescriptor() == null || paramMBeanInfo.getDescriptor().getFieldNames().length == 0) {
      String str1 = "interfaceClassName=" + getMBeanInterface().getName();
      String str2 = "immutableInfo=" + paramBoolean;
      immutableDescriptor = new ImmutableDescriptor(new String[] { str1, str2 });
      immutableDescriptor = descriptors.get(immutableDescriptor);
    } else {
      Descriptor descriptor = paramMBeanInfo.getDescriptor();
      HashMap hashMap = new HashMap();
      for (String str : descriptor.getFieldNames()) {
        if (str.equals("immutableInfo")) {
          hashMap.put(str, Boolean.toString(paramBoolean));
        } else {
          hashMap.put(str, descriptor.getFieldValue(str));
        } 
      } 
      immutableDescriptor = new ImmutableDescriptor(hashMap);
    } 
    return immutableDescriptor;
  }
  
  protected MBeanInfo getCachedMBeanInfo() { return this.cachedMBeanInfo; }
  
  protected void cacheMBeanInfo(MBeanInfo paramMBeanInfo) { this.cachedMBeanInfo = paramMBeanInfo; }
  
  private boolean isMXBean() { return this.mbean.isMXBean(); }
  
  private static <T> boolean identicalArrays(T[] paramArrayOfT1, T[] paramArrayOfT2) {
    if (paramArrayOfT1 == paramArrayOfT2)
      return true; 
    if (paramArrayOfT1 == null || paramArrayOfT2 == null || paramArrayOfT1.length != paramArrayOfT2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfT1.length; b++) {
      if (paramArrayOfT1[b] != paramArrayOfT2[b])
        return false; 
    } 
    return true;
  }
  
  private static <T> boolean equal(T paramT1, T paramT2) { return (paramT1 == paramT2) ? true : ((paramT1 == null || paramT2 == null) ? false : paramT1.equals(paramT2)); }
  
  private static MBeanParameterInfo customize(MBeanParameterInfo paramMBeanParameterInfo, String paramString1, String paramString2) {
    if (equal(paramString1, paramMBeanParameterInfo.getName()) && equal(paramString2, paramMBeanParameterInfo.getDescription()))
      return paramMBeanParameterInfo; 
    if (paramMBeanParameterInfo instanceof OpenMBeanParameterInfo) {
      OpenMBeanParameterInfo openMBeanParameterInfo = (OpenMBeanParameterInfo)paramMBeanParameterInfo;
      return new OpenMBeanParameterInfoSupport(paramString1, paramString2, openMBeanParameterInfo.getOpenType(), paramMBeanParameterInfo.getDescriptor());
    } 
    return new MBeanParameterInfo(paramString1, paramMBeanParameterInfo.getType(), paramString2, paramMBeanParameterInfo.getDescriptor());
  }
  
  private static MBeanConstructorInfo customize(MBeanConstructorInfo paramMBeanConstructorInfo, String paramString, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo) {
    if (equal(paramString, paramMBeanConstructorInfo.getDescription()) && identicalArrays(paramArrayOfMBeanParameterInfo, paramMBeanConstructorInfo.getSignature()))
      return paramMBeanConstructorInfo; 
    if (paramMBeanConstructorInfo instanceof javax.management.openmbean.OpenMBeanConstructorInfo) {
      OpenMBeanParameterInfo[] arrayOfOpenMBeanParameterInfo = paramsToOpenParams(paramArrayOfMBeanParameterInfo);
      return new OpenMBeanConstructorInfoSupport(paramMBeanConstructorInfo.getName(), paramString, arrayOfOpenMBeanParameterInfo, paramMBeanConstructorInfo.getDescriptor());
    } 
    return new MBeanConstructorInfo(paramMBeanConstructorInfo.getName(), paramString, paramArrayOfMBeanParameterInfo, paramMBeanConstructorInfo.getDescriptor());
  }
  
  private static MBeanOperationInfo customize(MBeanOperationInfo paramMBeanOperationInfo, String paramString, MBeanParameterInfo[] paramArrayOfMBeanParameterInfo, int paramInt) {
    if (equal(paramString, paramMBeanOperationInfo.getDescription()) && identicalArrays(paramArrayOfMBeanParameterInfo, paramMBeanOperationInfo.getSignature()) && paramInt == paramMBeanOperationInfo.getImpact())
      return paramMBeanOperationInfo; 
    if (paramMBeanOperationInfo instanceof OpenMBeanOperationInfo) {
      OpenMBeanOperationInfo openMBeanOperationInfo = (OpenMBeanOperationInfo)paramMBeanOperationInfo;
      OpenMBeanParameterInfo[] arrayOfOpenMBeanParameterInfo = paramsToOpenParams(paramArrayOfMBeanParameterInfo);
      return new OpenMBeanOperationInfoSupport(paramMBeanOperationInfo.getName(), paramString, arrayOfOpenMBeanParameterInfo, openMBeanOperationInfo.getReturnOpenType(), paramInt, paramMBeanOperationInfo.getDescriptor());
    } 
    return new MBeanOperationInfo(paramMBeanOperationInfo.getName(), paramString, paramArrayOfMBeanParameterInfo, paramMBeanOperationInfo.getReturnType(), paramInt, paramMBeanOperationInfo.getDescriptor());
  }
  
  private static MBeanAttributeInfo customize(MBeanAttributeInfo paramMBeanAttributeInfo, String paramString) {
    if (equal(paramString, paramMBeanAttributeInfo.getDescription()))
      return paramMBeanAttributeInfo; 
    if (paramMBeanAttributeInfo instanceof OpenMBeanAttributeInfo) {
      OpenMBeanAttributeInfo openMBeanAttributeInfo = (OpenMBeanAttributeInfo)paramMBeanAttributeInfo;
      return new OpenMBeanAttributeInfoSupport(paramMBeanAttributeInfo.getName(), paramString, openMBeanAttributeInfo.getOpenType(), paramMBeanAttributeInfo.isReadable(), paramMBeanAttributeInfo.isWritable(), paramMBeanAttributeInfo.isIs(), paramMBeanAttributeInfo.getDescriptor());
    } 
    return new MBeanAttributeInfo(paramMBeanAttributeInfo.getName(), paramMBeanAttributeInfo.getType(), paramString, paramMBeanAttributeInfo.isReadable(), paramMBeanAttributeInfo.isWritable(), paramMBeanAttributeInfo.isIs(), paramMBeanAttributeInfo.getDescriptor());
  }
  
  private static OpenMBeanParameterInfo[] paramsToOpenParams(MBeanParameterInfo[] paramArrayOfMBeanParameterInfo) {
    if (paramArrayOfMBeanParameterInfo instanceof OpenMBeanParameterInfo[])
      return (OpenMBeanParameterInfo[])paramArrayOfMBeanParameterInfo; 
    OpenMBeanParameterInfoSupport[] arrayOfOpenMBeanParameterInfoSupport = new OpenMBeanParameterInfoSupport[paramArrayOfMBeanParameterInfo.length];
    System.arraycopy(paramArrayOfMBeanParameterInfo, 0, arrayOfOpenMBeanParameterInfoSupport, 0, paramArrayOfMBeanParameterInfo.length);
    return arrayOfOpenMBeanParameterInfoSupport;
  }
  
  private MBeanConstructorInfo[] getConstructors(MBeanInfo paramMBeanInfo, Object paramObject) {
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo1 = getConstructors(paramMBeanInfo.getConstructors(), paramObject);
    if (arrayOfMBeanConstructorInfo1 == null)
      return null; 
    int i = arrayOfMBeanConstructorInfo1.length;
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo2 = new MBeanConstructorInfo[i];
    for (byte b = 0; b < i; b++) {
      MBeanParameterInfo[] arrayOfMBeanParameterInfo2;
      MBeanConstructorInfo mBeanConstructorInfo = arrayOfMBeanConstructorInfo1[b];
      MBeanParameterInfo[] arrayOfMBeanParameterInfo1 = mBeanConstructorInfo.getSignature();
      if (arrayOfMBeanParameterInfo1 != null) {
        int j = arrayOfMBeanParameterInfo1.length;
        arrayOfMBeanParameterInfo2 = new MBeanParameterInfo[j];
        for (byte b1 = 0; b1 < j; b1++) {
          MBeanParameterInfo mBeanParameterInfo = arrayOfMBeanParameterInfo1[b1];
          arrayOfMBeanParameterInfo2[b1] = customize(mBeanParameterInfo, getParameterName(mBeanConstructorInfo, mBeanParameterInfo, b1), getDescription(mBeanConstructorInfo, mBeanParameterInfo, b1));
        } 
      } else {
        arrayOfMBeanParameterInfo2 = null;
      } 
      arrayOfMBeanConstructorInfo2[b] = customize(mBeanConstructorInfo, getDescription(mBeanConstructorInfo), arrayOfMBeanParameterInfo2);
    } 
    return arrayOfMBeanConstructorInfo2;
  }
  
  private MBeanOperationInfo[] getOperations(MBeanInfo paramMBeanInfo) {
    MBeanOperationInfo[] arrayOfMBeanOperationInfo1 = paramMBeanInfo.getOperations();
    if (arrayOfMBeanOperationInfo1 == null)
      return null; 
    int i = arrayOfMBeanOperationInfo1.length;
    MBeanOperationInfo[] arrayOfMBeanOperationInfo2 = new MBeanOperationInfo[i];
    for (byte b = 0; b < i; b++) {
      MBeanParameterInfo[] arrayOfMBeanParameterInfo2;
      MBeanOperationInfo mBeanOperationInfo = arrayOfMBeanOperationInfo1[b];
      MBeanParameterInfo[] arrayOfMBeanParameterInfo1 = mBeanOperationInfo.getSignature();
      if (arrayOfMBeanParameterInfo1 != null) {
        int j = arrayOfMBeanParameterInfo1.length;
        arrayOfMBeanParameterInfo2 = new MBeanParameterInfo[j];
        for (byte b1 = 0; b1 < j; b1++) {
          MBeanParameterInfo mBeanParameterInfo = arrayOfMBeanParameterInfo1[b1];
          arrayOfMBeanParameterInfo2[b1] = customize(mBeanParameterInfo, getParameterName(mBeanOperationInfo, mBeanParameterInfo, b1), getDescription(mBeanOperationInfo, mBeanParameterInfo, b1));
        } 
      } else {
        arrayOfMBeanParameterInfo2 = null;
      } 
      arrayOfMBeanOperationInfo2[b] = customize(mBeanOperationInfo, getDescription(mBeanOperationInfo), arrayOfMBeanParameterInfo2, getImpact(mBeanOperationInfo));
    } 
    return arrayOfMBeanOperationInfo2;
  }
  
  private MBeanAttributeInfo[] getAttributes(MBeanInfo paramMBeanInfo) {
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo1 = paramMBeanInfo.getAttributes();
    if (arrayOfMBeanAttributeInfo1 == null)
      return null; 
    int i = arrayOfMBeanAttributeInfo1.length;
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo2 = new MBeanAttributeInfo[i];
    for (byte b = 0; b < i; b++) {
      MBeanAttributeInfo mBeanAttributeInfo = arrayOfMBeanAttributeInfo1[b];
      arrayOfMBeanAttributeInfo2[b] = customize(mBeanAttributeInfo, getDescription(mBeanAttributeInfo));
    } 
    return arrayOfMBeanAttributeInfo2;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    this.mbean.register(paramMBeanServer, paramObjectName);
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {
    if (!paramBoolean.booleanValue())
      this.mbean.unregister(); 
  }
  
  public void preDeregister() throws Exception {}
  
  public void postDeregister() throws Exception { this.mbean.unregister(); }
  
  static boolean immutableInfo(Class<? extends StandardMBean> paramClass) {
    if (paramClass == StandardMBean.class || paramClass == StandardEmitterMBean.class)
      return true; 
    synchronized (mbeanInfoSafeMap) {
      Boolean bool = (Boolean)mbeanInfoSafeMap.get(paramClass);
      if (bool == null) {
        try {
          MBeanInfoSafeAction mBeanInfoSafeAction = new MBeanInfoSafeAction(paramClass);
          bool = (Boolean)AccessController.doPrivileged(mBeanInfoSafeAction);
        } catch (Exception exception) {
          bool = Boolean.valueOf(false);
        } 
        mbeanInfoSafeMap.put(paramClass, bool);
      } 
      return bool.booleanValue();
    } 
  }
  
  static boolean overrides(Class<?> paramClass1, Class<?> paramClass2, String paramString, Class<?>... paramVarArgs) {
    Class<?> clazz = paramClass1;
    while (clazz != paramClass2) {
      try {
        clazz.getDeclaredMethod(paramString, paramVarArgs);
        return true;
      } catch (NoSuchMethodException noSuchMethodException) {
        clazz = clazz.getSuperclass();
      } 
    } 
    return false;
  }
  
  private static class MBeanInfoSafeAction extends Object implements PrivilegedAction<Boolean> {
    private final Class<?> subclass;
    
    MBeanInfoSafeAction(Class<?> param1Class) throws NotCompliantMBeanException { this.subclass = param1Class; }
    
    public Boolean run() { return StandardMBean.overrides(this.subclass, StandardMBean.class, "cacheMBeanInfo", new Class[] { MBeanInfo.class }) ? Boolean.valueOf(false) : (StandardMBean.overrides(this.subclass, StandardMBean.class, "getCachedMBeanInfo", (Class[])null) ? Boolean.valueOf(false) : (StandardMBean.overrides(this.subclass, StandardMBean.class, "getMBeanInfo", (Class[])null) ? Boolean.valueOf(false) : ((StandardEmitterMBean.class.isAssignableFrom(this.subclass) && StandardMBean.overrides(this.subclass, StandardEmitterMBean.class, "getNotificationInfo", (Class[])null)) ? Boolean.valueOf(false) : Boolean.valueOf(true)))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\StandardMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */