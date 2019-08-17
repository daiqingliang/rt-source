package sun.reflect;

import java.io.OptionalDataException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Objects;
import sun.reflect.misc.ReflectUtil;

public class ReflectionFactory {
  private static boolean initted = false;
  
  private static final Permission reflectionFactoryAccessPerm = new RuntimePermission("reflectionFactoryAccess");
  
  private static final ReflectionFactory soleInstance = new ReflectionFactory();
  
  private static boolean noInflation = false;
  
  private static int inflationThreshold = 15;
  
  public static ReflectionFactory getReflectionFactory() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(reflectionFactoryAccessPerm); 
    return soleInstance;
  }
  
  public void setLangReflectAccess(LangReflectAccess paramLangReflectAccess) { langReflectAccess = paramLangReflectAccess; }
  
  public FieldAccessor newFieldAccessor(Field paramField, boolean paramBoolean) {
    checkInitted();
    return UnsafeFieldAccessorFactory.newFieldAccessor(paramField, paramBoolean);
  }
  
  public MethodAccessor newMethodAccessor(Method paramMethod) {
    checkInitted();
    if (noInflation && !ReflectUtil.isVMAnonymousClass(paramMethod.getDeclaringClass()))
      return (new MethodAccessorGenerator()).generateMethod(paramMethod.getDeclaringClass(), paramMethod.getName(), paramMethod.getParameterTypes(), paramMethod.getReturnType(), paramMethod.getExceptionTypes(), paramMethod.getModifiers()); 
    NativeMethodAccessorImpl nativeMethodAccessorImpl = new NativeMethodAccessorImpl(paramMethod);
    DelegatingMethodAccessorImpl delegatingMethodAccessorImpl = new DelegatingMethodAccessorImpl(nativeMethodAccessorImpl);
    nativeMethodAccessorImpl.setParent(delegatingMethodAccessorImpl);
    return delegatingMethodAccessorImpl;
  }
  
  public ConstructorAccessor newConstructorAccessor(Constructor<?> paramConstructor) {
    checkInitted();
    Class clazz = paramConstructor.getDeclaringClass();
    if (Modifier.isAbstract(clazz.getModifiers()))
      return new InstantiationExceptionConstructorAccessorImpl(null); 
    if (clazz == Class.class)
      return new InstantiationExceptionConstructorAccessorImpl("Can not instantiate java.lang.Class"); 
    if (Reflection.isSubclassOf(clazz, ConstructorAccessorImpl.class))
      return new BootstrapConstructorAccessorImpl(paramConstructor); 
    if (noInflation && !ReflectUtil.isVMAnonymousClass(paramConstructor.getDeclaringClass()))
      return (new MethodAccessorGenerator()).generateConstructor(paramConstructor.getDeclaringClass(), paramConstructor.getParameterTypes(), paramConstructor.getExceptionTypes(), paramConstructor.getModifiers()); 
    NativeConstructorAccessorImpl nativeConstructorAccessorImpl = new NativeConstructorAccessorImpl(paramConstructor);
    DelegatingConstructorAccessorImpl delegatingConstructorAccessorImpl = new DelegatingConstructorAccessorImpl(nativeConstructorAccessorImpl);
    nativeConstructorAccessorImpl.setParent(delegatingConstructorAccessorImpl);
    return delegatingConstructorAccessorImpl;
  }
  
  public Field newField(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte) { return langReflectAccess().newField(paramClass1, paramString1, paramClass2, paramInt1, paramInt2, paramString2, paramArrayOfByte); }
  
  public Method newMethod(Class<?> paramClass1, String paramString1, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) { return langReflectAccess().newMethod(paramClass1, paramString1, paramArrayOfClass1, paramClass2, paramArrayOfClass2, paramInt1, paramInt2, paramString2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3); }
  
  public Constructor<?> newConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) { return langReflectAccess().newConstructor(paramClass, paramArrayOfClass1, paramArrayOfClass2, paramInt1, paramInt2, paramString, paramArrayOfByte1, paramArrayOfByte2); }
  
  public MethodAccessor getMethodAccessor(Method paramMethod) { return langReflectAccess().getMethodAccessor(paramMethod); }
  
  public void setMethodAccessor(Method paramMethod, MethodAccessor paramMethodAccessor) { langReflectAccess().setMethodAccessor(paramMethod, paramMethodAccessor); }
  
  public ConstructorAccessor getConstructorAccessor(Constructor<?> paramConstructor) { return langReflectAccess().getConstructorAccessor(paramConstructor); }
  
  public void setConstructorAccessor(Constructor<?> paramConstructor, ConstructorAccessor paramConstructorAccessor) { langReflectAccess().setConstructorAccessor(paramConstructor, paramConstructorAccessor); }
  
  public Method copyMethod(Method paramMethod) { return langReflectAccess().copyMethod(paramMethod); }
  
  public Field copyField(Field paramField) { return langReflectAccess().copyField(paramField); }
  
  public <T> Constructor<T> copyConstructor(Constructor<T> paramConstructor) { return langReflectAccess().copyConstructor(paramConstructor); }
  
  public byte[] getExecutableTypeAnnotationBytes(Executable paramExecutable) { return langReflectAccess().getExecutableTypeAnnotationBytes(paramExecutable); }
  
  public Constructor<?> newConstructorForSerialization(Class<?> paramClass, Constructor<?> paramConstructor) { return (paramConstructor.getDeclaringClass() == paramClass) ? paramConstructor : generateConstructor(paramClass, paramConstructor); }
  
  public final Constructor<?> newConstructorForSerialization(Class<?> paramClass) {
    Constructor constructor;
    Class<?> clazz = paramClass;
    while (java.io.Serializable.class.isAssignableFrom(clazz)) {
      if ((clazz = clazz.getSuperclass()) == null)
        return null; 
    } 
    try {
      constructor = clazz.getDeclaredConstructor(new Class[0]);
      int i = constructor.getModifiers();
      if ((i & 0x2) != 0 || ((i & 0x5) == 0 && !packageEquals(paramClass, clazz)))
        return null; 
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
    return generateConstructor(paramClass, constructor);
  }
  
  private final Constructor<?> generateConstructor(Class<?> paramClass, Constructor<?> paramConstructor) {
    SerializationConstructorAccessorImpl serializationConstructorAccessorImpl = (new MethodAccessorGenerator()).generateSerializationConstructor(paramClass, paramConstructor.getParameterTypes(), paramConstructor.getExceptionTypes(), paramConstructor.getModifiers(), paramConstructor.getDeclaringClass());
    Constructor constructor = newConstructor(paramConstructor.getDeclaringClass(), paramConstructor.getParameterTypes(), paramConstructor.getExceptionTypes(), paramConstructor.getModifiers(), langReflectAccess().getConstructorSlot(paramConstructor), langReflectAccess().getConstructorSignature(paramConstructor), langReflectAccess().getConstructorAnnotations(paramConstructor), langReflectAccess().getConstructorParameterAnnotations(paramConstructor));
    setConstructorAccessor(constructor, serializationConstructorAccessorImpl);
    constructor.setAccessible(true);
    return constructor;
  }
  
  public final Constructor<?> newConstructorForExternalization(Class<?> paramClass) {
    if (!java.io.Externalizable.class.isAssignableFrom(paramClass))
      return null; 
    try {
      Constructor constructor = paramClass.getConstructor(new Class[0]);
      constructor.setAccessible(true);
      return constructor;
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
  
  public final MethodHandle readObjectForSerialization(Class<?> paramClass) { return findReadWriteObjectForSerialization(paramClass, "readObject", java.io.ObjectInputStream.class); }
  
  public final MethodHandle readObjectNoDataForSerialization(Class<?> paramClass) { return findReadWriteObjectForSerialization(paramClass, "readObjectNoData", java.io.ObjectInputStream.class); }
  
  public final MethodHandle writeObjectForSerialization(Class<?> paramClass) { return findReadWriteObjectForSerialization(paramClass, "writeObject", java.io.ObjectOutputStream.class); }
  
  private final MethodHandle findReadWriteObjectForSerialization(Class<?> paramClass1, String paramString, Class<?> paramClass2) {
    if (!java.io.Serializable.class.isAssignableFrom(paramClass1))
      return null; 
    try {
      Method method = paramClass1.getDeclaredMethod(paramString, new Class[] { paramClass2 });
      int i = method.getModifiers();
      if (method.getReturnType() != void.class || Modifier.isStatic(i) || !Modifier.isPrivate(i))
        return null; 
      method.setAccessible(true);
      return MethodHandles.lookup().unreflect(method);
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } catch (IllegalAccessException illegalAccessException) {
      throw new InternalError("Error", illegalAccessException);
    } 
  }
  
  public final MethodHandle readResolveForSerialization(Class<?> paramClass) { return getReplaceResolveForSerialization(paramClass, "readResolve"); }
  
  public final MethodHandle writeReplaceForSerialization(Class<?> paramClass) { return getReplaceResolveForSerialization(paramClass, "writeReplace"); }
  
  private MethodHandle getReplaceResolveForSerialization(Class<?> paramClass, String paramString) {
    if (!java.io.Serializable.class.isAssignableFrom(paramClass))
      return null; 
    Class<?> clazz = paramClass;
    while (clazz != null) {
      try {
        Method method = clazz.getDeclaredMethod(paramString, new Class[0]);
        if (method.getReturnType() != Object.class)
          return null; 
        int i = method.getModifiers();
        if (Modifier.isStatic(i) | Modifier.isAbstract(i))
          return null; 
        if (!(Modifier.isPublic(i) | Modifier.isProtected(i))) {
          if (Modifier.isPrivate(i) && paramClass != clazz)
            return null; 
          if (!packageEquals(paramClass, clazz))
            return null; 
        } 
        try {
          method.setAccessible(true);
          return MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException illegalAccessException) {
          throw new InternalError("Error", illegalAccessException);
        } 
      } catch (NoSuchMethodException noSuchMethodException) {
        clazz = clazz.getSuperclass();
      } 
    } 
    return null;
  }
  
  public final boolean hasStaticInitializerForSerialization(Class<?> paramClass) {
    Method method = hasStaticInitializerMethod;
    if (method == null)
      try {
        method = java.io.ObjectStreamClass.class.getDeclaredMethod("hasStaticInitializer", new Class[] { Class.class });
        method.setAccessible(true);
        hasStaticInitializerMethod = method;
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new InternalError("No such method hasStaticInitializer on " + java.io.ObjectStreamClass.class, noSuchMethodException);
      }  
    try {
      return ((Boolean)method.invoke(null, new Object[] { paramClass })).booleanValue();
    } catch (InvocationTargetException|IllegalAccessException invocationTargetException) {
      throw new InternalError("Exception invoking hasStaticInitializer", invocationTargetException);
    } 
  }
  
  public final OptionalDataException newOptionalDataExceptionForSerialization(boolean paramBoolean) {
    try {
      Constructor constructor = OptionalDataException.class.getDeclaredConstructor(new Class[] { boolean.class });
      constructor.setAccessible(true);
      return (OptionalDataException)constructor.newInstance(new Object[] { Boolean.valueOf(paramBoolean) });
    } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException noSuchMethodException) {
      throw new InternalError("unable to create OptionalDataException", noSuchMethodException);
    } 
  }
  
  static int inflationThreshold() { return inflationThreshold; }
  
  private static void checkInitted() {
    if (initted)
      return; 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            if (System.out == null)
              return null; 
            String str = System.getProperty("sun.reflect.noInflation");
            if (str != null && str.equals("true"))
              noInflation = true; 
            str = System.getProperty("sun.reflect.inflationThreshold");
            if (str != null)
              try {
                inflationThreshold = Integer.parseInt(str);
              } catch (NumberFormatException numberFormatException) {
                throw new RuntimeException("Unable to parse property sun.reflect.inflationThreshold", numberFormatException);
              }  
            initted = true;
            return null;
          }
        });
  }
  
  private static LangReflectAccess langReflectAccess() {
    if (langReflectAccess == null)
      Modifier.isPublic(1); 
    return langReflectAccess;
  }
  
  private static boolean packageEquals(Class<?> paramClass1, Class<?> paramClass2) { return (paramClass1.getClassLoader() == paramClass2.getClassLoader() && Objects.equals(paramClass1.getPackage(), paramClass2.getPackage())); }
  
  public static final class GetReflectionFactoryAction extends Object implements PrivilegedAction<ReflectionFactory> {
    public ReflectionFactory run() { return ReflectionFactory.getReflectionFactory(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\ReflectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */