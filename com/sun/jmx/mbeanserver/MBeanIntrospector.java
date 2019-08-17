package com.sun.jmx.mbeanserver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;

abstract class MBeanIntrospector<M> extends Object {
  abstract PerInterfaceMap<M> getPerInterfaceMap();
  
  abstract MBeanInfoMap getMBeanInfoMap();
  
  abstract MBeanAnalyzer<M> getAnalyzer(Class<?> paramClass) throws NotCompliantMBeanException;
  
  abstract boolean isMXBean();
  
  abstract M mFrom(Method paramMethod);
  
  abstract String getName(M paramM);
  
  abstract Type getGenericReturnType(M paramM);
  
  abstract Type[] getGenericParameterTypes(M paramM);
  
  abstract String[] getSignature(M paramM);
  
  abstract void checkMethod(M paramM);
  
  abstract Object invokeM2(M paramM, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2) throws InvocationTargetException, IllegalAccessException, MBeanException;
  
  abstract boolean validParameter(M paramM, Object paramObject1, int paramInt, Object paramObject2);
  
  abstract MBeanAttributeInfo getMBeanAttributeInfo(String paramString, M paramM1, M paramM2);
  
  abstract MBeanOperationInfo getMBeanOperationInfo(String paramString, M paramM);
  
  abstract Descriptor getBasicMBeanDescriptor();
  
  abstract Descriptor getMBeanDescriptor(Class<?> paramClass);
  
  final List<Method> getMethods(Class<?> paramClass) {
    ReflectUtil.checkPackageAccess(paramClass);
    return Arrays.asList(paramClass.getMethods());
  }
  
  final PerInterface<M> getPerInterface(Class<?> paramClass) throws NotCompliantMBeanException {
    PerInterfaceMap perInterfaceMap = getPerInterfaceMap();
    synchronized (perInterfaceMap) {
      WeakReference weakReference = (WeakReference)perInterfaceMap.get(paramClass);
      PerInterface perInterface = (weakReference == null) ? null : (PerInterface)weakReference.get();
      if (perInterface == null)
        try {
          MBeanAnalyzer mBeanAnalyzer = getAnalyzer(paramClass);
          MBeanInfo mBeanInfo = makeInterfaceMBeanInfo(paramClass, mBeanAnalyzer);
          perInterface = new PerInterface(paramClass, this, mBeanAnalyzer, mBeanInfo);
          weakReference = new WeakReference(perInterface);
          perInterfaceMap.put(paramClass, weakReference);
        } catch (Exception exception) {
          throw Introspector.throwException(paramClass, exception);
        }  
      return perInterface;
    } 
  }
  
  private MBeanInfo makeInterfaceMBeanInfo(Class<?> paramClass, MBeanAnalyzer<M> paramMBeanAnalyzer) {
    MBeanInfoMaker mBeanInfoMaker = new MBeanInfoMaker(null);
    paramMBeanAnalyzer.visit(mBeanInfoMaker);
    return mBeanInfoMaker.makeMBeanInfo(paramClass, "Information on the management interface of the MBean");
  }
  
  final boolean consistent(M paramM1, M paramM2) { return (paramM1 == null || paramM2 == null || getGenericReturnType(paramM1).equals(getGenericParameterTypes(paramM2)[0])); }
  
  final Object invokeM(M paramM, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2) throws InvocationTargetException, IllegalAccessException, MBeanException {
    try {
      return invokeM2(paramM, paramObject1, paramArrayOfObject, paramObject2);
    } catch (InvocationTargetException invocationTargetException) {
      unwrapInvocationTargetException(invocationTargetException);
      throw new RuntimeException(invocationTargetException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionException(illegalAccessException, illegalAccessException.toString());
    } 
  }
  
  final void invokeSetter(String paramString, M paramM, Object paramObject1, Object paramObject2, Object paramObject3) throws MBeanException, ReflectionException, InvalidAttributeValueException {
    try {
      invokeM2(paramM, paramObject1, new Object[] { paramObject2 }, paramObject3);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionException(illegalAccessException, illegalAccessException.toString());
    } catch (RuntimeException runtimeException) {
      maybeInvalidParameter(paramString, paramM, paramObject2, paramObject3);
      throw runtimeException;
    } catch (InvocationTargetException invocationTargetException) {
      maybeInvalidParameter(paramString, paramM, paramObject2, paramObject3);
      unwrapInvocationTargetException(invocationTargetException);
    } 
  }
  
  private void maybeInvalidParameter(String paramString, M paramM, Object paramObject1, Object paramObject2) throws InvalidAttributeValueException {
    if (!validParameter(paramM, paramObject1, 0, paramObject2)) {
      String str = "Invalid value for attribute " + paramString + ": " + paramObject1;
      throw new InvalidAttributeValueException(str);
    } 
  }
  
  static boolean isValidParameter(Method paramMethod, Object paramObject, int paramInt) {
    Class clazz = paramMethod.getParameterTypes()[paramInt];
    try {
      Object object = Array.newInstance(clazz, 1);
      Array.set(object, 0, paramObject);
      return true;
    } catch (IllegalArgumentException illegalArgumentException) {
      return false;
    } 
  }
  
  private static void unwrapInvocationTargetException(InvocationTargetException paramInvocationTargetException) throws MBeanException {
    Throwable throwable = paramInvocationTargetException.getCause();
    if (throwable instanceof RuntimeException)
      throw (RuntimeException)throwable; 
    if (throwable instanceof Error)
      throw (Error)throwable; 
    throw new MBeanException((Exception)throwable, (throwable == null) ? null : throwable.toString());
  }
  
  final MBeanInfo getMBeanInfo(Object paramObject, PerInterface<M> paramPerInterface) {
    MBeanInfo mBeanInfo = getClassMBeanInfo(paramObject.getClass(), paramPerInterface);
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = findNotifications(paramObject);
    return (arrayOfMBeanNotificationInfo == null || arrayOfMBeanNotificationInfo.length == 0) ? mBeanInfo : new MBeanInfo(mBeanInfo.getClassName(), mBeanInfo.getDescription(), mBeanInfo.getAttributes(), mBeanInfo.getConstructors(), mBeanInfo.getOperations(), arrayOfMBeanNotificationInfo, mBeanInfo.getDescriptor());
  }
  
  final MBeanInfo getClassMBeanInfo(Class<?> paramClass, PerInterface<M> paramPerInterface) {
    MBeanInfoMap mBeanInfoMap = getMBeanInfoMap();
    synchronized (mBeanInfoMap) {
      WeakHashMap weakHashMap = (WeakHashMap)mBeanInfoMap.get(paramClass);
      if (weakHashMap == null) {
        weakHashMap = new WeakHashMap();
        mBeanInfoMap.put(paramClass, weakHashMap);
      } 
      Class clazz = paramPerInterface.getMBeanInterface();
      MBeanInfo mBeanInfo = (MBeanInfo)weakHashMap.get(clazz);
      if (mBeanInfo == null) {
        MBeanInfo mBeanInfo1 = paramPerInterface.getMBeanInfo();
        ImmutableDescriptor immutableDescriptor = ImmutableDescriptor.union(new Descriptor[] { mBeanInfo1.getDescriptor(), getMBeanDescriptor(paramClass) });
        mBeanInfo = new MBeanInfo(paramClass.getName(), mBeanInfo1.getDescription(), mBeanInfo1.getAttributes(), findConstructors(paramClass), mBeanInfo1.getOperations(), (MBeanNotificationInfo[])null, immutableDescriptor);
        weakHashMap.put(clazz, mBeanInfo);
      } 
      return mBeanInfo;
    } 
  }
  
  static MBeanNotificationInfo[] findNotifications(Object paramObject) {
    if (!(paramObject instanceof NotificationBroadcaster))
      return null; 
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo1 = ((NotificationBroadcaster)paramObject).getNotificationInfo();
    if (arrayOfMBeanNotificationInfo1 == null)
      return null; 
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo2 = new MBeanNotificationInfo[arrayOfMBeanNotificationInfo1.length];
    for (byte b = 0; b < arrayOfMBeanNotificationInfo1.length; b++) {
      MBeanNotificationInfo mBeanNotificationInfo = arrayOfMBeanNotificationInfo1[b];
      if (mBeanNotificationInfo.getClass() != MBeanNotificationInfo.class)
        mBeanNotificationInfo = (MBeanNotificationInfo)mBeanNotificationInfo.clone(); 
      arrayOfMBeanNotificationInfo2[b] = mBeanNotificationInfo;
    } 
    return arrayOfMBeanNotificationInfo2;
  }
  
  private static MBeanConstructorInfo[] findConstructors(Class<?> paramClass) {
    Constructor[] arrayOfConstructor = paramClass.getConstructors();
    MBeanConstructorInfo[] arrayOfMBeanConstructorInfo = new MBeanConstructorInfo[arrayOfConstructor.length];
    for (byte b = 0; b < arrayOfConstructor.length; b++)
      arrayOfMBeanConstructorInfo[b] = new MBeanConstructorInfo("Public constructor of the MBean", arrayOfConstructor[b]); 
    return arrayOfMBeanConstructorInfo;
  }
  
  private class MBeanInfoMaker extends Object implements MBeanAnalyzer.MBeanVisitor<M> {
    private final List<MBeanAttributeInfo> attrs = Util.newList();
    
    private final List<MBeanOperationInfo> ops = Util.newList();
    
    private MBeanInfoMaker() {}
    
    public void visitAttribute(String param1String, M param1M1, M param1M2) {
      MBeanAttributeInfo mBeanAttributeInfo = MBeanIntrospector.this.getMBeanAttributeInfo(param1String, param1M1, param1M2);
      this.attrs.add(mBeanAttributeInfo);
    }
    
    public void visitOperation(String param1String, M param1M) {
      MBeanOperationInfo mBeanOperationInfo = MBeanIntrospector.this.getMBeanOperationInfo(param1String, param1M);
      this.ops.add(mBeanOperationInfo);
    }
    
    MBeanInfo makeMBeanInfo(Class<?> param1Class, String param1String) {
      MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = (MBeanAttributeInfo[])this.attrs.toArray(new MBeanAttributeInfo[0]);
      MBeanOperationInfo[] arrayOfMBeanOperationInfo = (MBeanOperationInfo[])this.ops.toArray(new MBeanOperationInfo[0]);
      String str = "interfaceClassName=" + param1Class.getName();
      ImmutableDescriptor immutableDescriptor1 = new ImmutableDescriptor(new String[] { str });
      Descriptor descriptor1 = MBeanIntrospector.this.getBasicMBeanDescriptor();
      Descriptor descriptor2 = Introspector.descriptorForElement(param1Class);
      ImmutableDescriptor immutableDescriptor2 = DescriptorCache.getInstance().union(new Descriptor[] { immutableDescriptor1, descriptor1, descriptor2 });
      return new MBeanInfo(param1Class.getName(), param1String, arrayOfMBeanAttributeInfo, null, arrayOfMBeanOperationInfo, null, immutableDescriptor2);
    }
  }
  
  static class MBeanInfoMap extends WeakHashMap<Class<?>, WeakHashMap<Class<?>, MBeanInfo>> {}
  
  static final class PerInterfaceMap<M> extends WeakHashMap<Class<?>, WeakReference<PerInterface<M>>> {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\MBeanIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */