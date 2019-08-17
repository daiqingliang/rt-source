package java.lang;

import java.io.InputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstantPool;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ClassRepository;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.scope.ClassScope;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public final class Class<T> extends Object implements Serializable, GenericDeclaration, Type, AnnotatedElement {
  private static final int ANNOTATION = 8192;
  
  private static final int ENUM = 16384;
  
  private static final int SYNTHETIC = 4096;
  
  private String name;
  
  private final ClassLoader classLoader;
  
  private static ProtectionDomain allPermDomain;
  
  private static boolean useCaches;
  
  private static final long serialVersionUID = 3206093459760846163L;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static ReflectionFactory reflectionFactory;
  
  private static boolean initted;
  
  ClassValue.ClassValueMap classValueMap;
  
  private static native void registerNatives();
  
  private Class(ClassLoader paramClassLoader) { this.classLoader = paramClassLoader; }
  
  public String toString() { return (isInterface() ? "interface " : (isPrimitive() ? "" : "class ")) + getName(); }
  
  public String toGenericString() {
    if (isPrimitive())
      return toString(); 
    StringBuilder stringBuilder = new StringBuilder();
    int i = getModifiers() & Modifier.classModifiers();
    if (i != 0) {
      stringBuilder.append(Modifier.toString(i));
      stringBuilder.append(' ');
    } 
    if (isAnnotation())
      stringBuilder.append('@'); 
    if (isInterface()) {
      stringBuilder.append("interface");
    } else if (isEnum()) {
      stringBuilder.append("enum");
    } else {
      stringBuilder.append("class");
    } 
    stringBuilder.append(' ');
    stringBuilder.append(getName());
    TypeVariable[] arrayOfTypeVariable = getTypeParameters();
    if (arrayOfTypeVariable.length > 0) {
      boolean bool = true;
      stringBuilder.append('<');
      for (TypeVariable typeVariable : arrayOfTypeVariable) {
        if (!bool)
          stringBuilder.append(','); 
        stringBuilder.append(typeVariable.getTypeName());
        bool = false;
      } 
      stringBuilder.append('>');
    } 
    return stringBuilder.toString();
  }
  
  @CallerSensitive
  public static Class<?> forName(String paramString) throws ClassNotFoundException {
    Class clazz;
    return (clazz = Reflection.getCallerClass()).forName0(paramString, true, ClassLoader.getClassLoader(clazz), clazz);
  }
  
  @CallerSensitive
  public static Class<?> forName(String paramString, boolean paramBoolean, ClassLoader paramClassLoader) throws ClassNotFoundException {
    Class clazz = null;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      clazz = Reflection.getCallerClass();
      if (VM.isSystemDomainLoader(paramClassLoader)) {
        ClassLoader classLoader1 = ClassLoader.getClassLoader(clazz);
        if (!VM.isSystemDomainLoader(classLoader1))
          securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION); 
      } 
    } 
    return forName0(paramString, paramBoolean, paramClassLoader, clazz);
  }
  
  private static native Class<?> forName0(String paramString, boolean paramBoolean, ClassLoader paramClassLoader, Class<?> paramClass) throws ClassNotFoundException;
  
  @CallerSensitive
  public T newInstance() throws InstantiationException, IllegalAccessException {
    if (System.getSecurityManager() != null)
      checkMemberAccess(0, Reflection.getCallerClass(), false); 
    if (this.cachedConstructor == null) {
      if (this == Class.class)
        throw new IllegalAccessException("Can not call newInstance() on the Class for java.lang.Class"); 
      try {
        Class[] arrayOfClass = new Class[0];
        final Constructor c = getConstructor0(arrayOfClass, 1);
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                c.setAccessible(true);
                return null;
              }
            });
        this.cachedConstructor = constructor1;
      } catch (NoSuchMethodException noSuchMethodException) {
        throw (InstantiationException)(new InstantiationException(getName())).initCause(noSuchMethodException);
      } 
    } 
    Constructor constructor = this.cachedConstructor;
    int i = constructor.getModifiers();
    if (!Reflection.quickCheckMemberAccess(this, i)) {
      Class clazz = Reflection.getCallerClass();
      if (this.newInstanceCallerCache != clazz) {
        Reflection.ensureMemberAccess(clazz, this, null, i);
        this.newInstanceCallerCache = clazz;
      } 
    } 
    try {
      return (T)constructor.newInstance((Object[])null);
    } catch (InvocationTargetException invocationTargetException) {
      Unsafe.getUnsafe().throwException(invocationTargetException.getTargetException());
      return null;
    } 
  }
  
  public native boolean isInstance(Object paramObject);
  
  public native boolean isAssignableFrom(Class<?> paramClass);
  
  public native boolean isInterface();
  
  public native boolean isArray();
  
  public native boolean isPrimitive();
  
  public boolean isAnnotation() { return ((getModifiers() & 0x2000) != 0); }
  
  public boolean isSynthetic() { return ((getModifiers() & 0x1000) != 0); }
  
  public String getName() {
    String str = this.name;
    if (str == null)
      this.name = str = getName0(); 
    return str;
  }
  
  private native String getName0();
  
  @CallerSensitive
  public ClassLoader getClassLoader() {
    ClassLoader classLoader1 = getClassLoader0();
    if (classLoader1 == null)
      return null; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      ClassLoader.checkClassLoaderPermission(classLoader1, Reflection.getCallerClass()); 
    return classLoader1;
  }
  
  ClassLoader getClassLoader0() { return this.classLoader; }
  
  public TypeVariable<Class<T>>[] getTypeParameters() {
    ClassRepository classRepository = getGenericInfo();
    return (classRepository != null) ? (TypeVariable[])classRepository.getTypeParameters() : (TypeVariable[])new TypeVariable[0];
  }
  
  public native Class<? super T> getSuperclass();
  
  public Type getGenericSuperclass() {
    ClassRepository classRepository = getGenericInfo();
    return (classRepository == null) ? getSuperclass() : (isInterface() ? null : classRepository.getSuperclass());
  }
  
  public Package getPackage() { return Package.getPackage(this); }
  
  public Class<?>[] getInterfaces() {
    ReflectionData reflectionData1 = reflectionData();
    if (reflectionData1 == null)
      return getInterfaces0(); 
    Class[] arrayOfClass = reflectionData1.interfaces;
    if (arrayOfClass == null) {
      arrayOfClass = getInterfaces0();
      reflectionData1.interfaces = arrayOfClass;
    } 
    return (Class[])arrayOfClass.clone();
  }
  
  private native Class<?>[] getInterfaces0();
  
  public Type[] getGenericInterfaces() {
    ClassRepository classRepository = getGenericInfo();
    return (classRepository == null) ? getInterfaces() : classRepository.getSuperInterfaces();
  }
  
  public native Class<?> getComponentType();
  
  public native int getModifiers();
  
  public native Object[] getSigners();
  
  native void setSigners(Object[] paramArrayOfObject);
  
  @CallerSensitive
  public Method getEnclosingMethod() throws SecurityException {
    EnclosingMethodInfo enclosingMethodInfo = getEnclosingMethodInfo();
    if (enclosingMethodInfo == null)
      return null; 
    if (!enclosingMethodInfo.isMethod())
      return null; 
    MethodRepository methodRepository = MethodRepository.make(enclosingMethodInfo.getDescriptor(), getFactory());
    Class clazz1 = toClass(methodRepository.getReturnType());
    Type[] arrayOfType = methodRepository.getParameterTypes();
    Class[] arrayOfClass = new Class[arrayOfType.length];
    for (byte b = 0; b < arrayOfClass.length; b++)
      arrayOfClass[b] = toClass(arrayOfType[b]); 
    Class clazz2 = enclosingMethodInfo.getEnclosingClass();
    clazz2.checkMemberAccess(1, Reflection.getCallerClass(), true);
    for (Method method : clazz2.getDeclaredMethods()) {
      if (method.getName().equals(enclosingMethodInfo.getName())) {
        Class[] arrayOfClass1 = method.getParameterTypes();
        if (arrayOfClass1.length == arrayOfClass.length) {
          boolean bool = true;
          for (byte b1 = 0; b1 < arrayOfClass1.length; b1++) {
            if (!arrayOfClass1[b1].equals(arrayOfClass[b1])) {
              bool = false;
              break;
            } 
          } 
          if (bool && method.getReturnType().equals(clazz1))
            return method; 
        } 
      } 
    } 
    throw new InternalError("Enclosing method not found");
  }
  
  private native Object[] getEnclosingMethod0();
  
  private EnclosingMethodInfo getEnclosingMethodInfo() {
    Object[] arrayOfObject = getEnclosingMethod0();
    return (arrayOfObject == null) ? null : new EnclosingMethodInfo(arrayOfObject, null);
  }
  
  private static Class<?> toClass(Type paramType) { return (paramType instanceof GenericArrayType) ? Array.newInstance(toClass(((GenericArrayType)paramType).getGenericComponentType()), 0).getClass() : (Class)paramType; }
  
  @CallerSensitive
  public Constructor<?> getEnclosingConstructor() throws SecurityException {
    EnclosingMethodInfo enclosingMethodInfo = getEnclosingMethodInfo();
    if (enclosingMethodInfo == null)
      return null; 
    if (!enclosingMethodInfo.isConstructor())
      return null; 
    ConstructorRepository constructorRepository = ConstructorRepository.make(enclosingMethodInfo.getDescriptor(), getFactory());
    Type[] arrayOfType = constructorRepository.getParameterTypes();
    Class[] arrayOfClass = new Class[arrayOfType.length];
    for (byte b = 0; b < arrayOfClass.length; b++)
      arrayOfClass[b] = toClass(arrayOfType[b]); 
    Class clazz = enclosingMethodInfo.getEnclosingClass();
    clazz.checkMemberAccess(1, Reflection.getCallerClass(), true);
    for (Constructor constructor : clazz.getDeclaredConstructors()) {
      Class[] arrayOfClass1 = constructor.getParameterTypes();
      if (arrayOfClass1.length == arrayOfClass.length) {
        boolean bool = true;
        for (byte b1 = 0; b1 < arrayOfClass1.length; b1++) {
          if (!arrayOfClass1[b1].equals(arrayOfClass[b1])) {
            bool = false;
            break;
          } 
        } 
        if (bool)
          return constructor; 
      } 
    } 
    throw new InternalError("Enclosing constructor not found");
  }
  
  @CallerSensitive
  public Class<?> getDeclaringClass() {
    Class clazz = getDeclaringClass0();
    if (clazz != null)
      clazz.checkPackageAccess(ClassLoader.getClassLoader(Reflection.getCallerClass()), true); 
    return clazz;
  }
  
  private native Class<?> getDeclaringClass0();
  
  @CallerSensitive
  public Class<?> getEnclosingClass() {
    Class clazz;
    EnclosingMethodInfo enclosingMethodInfo = getEnclosingMethodInfo();
    if (enclosingMethodInfo == null) {
      clazz = getDeclaringClass();
    } else {
      Class clazz1 = enclosingMethodInfo.getEnclosingClass();
      if (clazz1 == this || clazz1 == null)
        throw new InternalError("Malformed enclosing method information"); 
      clazz = clazz1;
    } 
    if (clazz != null)
      clazz.checkPackageAccess(ClassLoader.getClassLoader(Reflection.getCallerClass()), true); 
    return clazz;
  }
  
  public String getSimpleName() {
    if (isArray())
      return getComponentType().getSimpleName() + "[]"; 
    String str = getSimpleBinaryName();
    if (str == null) {
      str = getName();
      return str.substring(str.lastIndexOf(".") + 1);
    } 
    int i = str.length();
    if (i < 1 || str.charAt(0) != '$')
      throw new InternalError("Malformed class name"); 
    byte b;
    for (b = 1; b < i && isAsciiDigit(str.charAt(b)); b++);
    return str.substring(b);
  }
  
  public String getTypeName() {
    if (isArray())
      try {
        Class clazz = this;
        byte b1 = 0;
        while (clazz.isArray()) {
          b1++;
          clazz = clazz.getComponentType();
        } 
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(clazz.getName());
        for (byte b2 = 0; b2 < b1; b2++)
          stringBuilder.append("[]"); 
        return stringBuilder.toString();
      } catch (Throwable throwable) {} 
    return getName();
  }
  
  private static boolean isAsciiDigit(char paramChar) { return ('0' <= paramChar && paramChar <= '9'); }
  
  public String getCanonicalName() {
    if (isArray()) {
      String str1 = getComponentType().getCanonicalName();
      return (str1 != null) ? (str1 + "[]") : null;
    } 
    if (isLocalOrAnonymousClass())
      return null; 
    Class clazz = getEnclosingClass();
    if (clazz == null)
      return getName(); 
    String str = clazz.getCanonicalName();
    return (str == null) ? null : (str + "." + getSimpleName());
  }
  
  public boolean isAnonymousClass() { return "".equals(getSimpleName()); }
  
  public boolean isLocalClass() { return (isLocalOrAnonymousClass() && !isAnonymousClass()); }
  
  public boolean isMemberClass() { return (getSimpleBinaryName() != null && !isLocalOrAnonymousClass()); }
  
  private String getSimpleBinaryName() {
    Class clazz = getEnclosingClass();
    if (clazz == null)
      return null; 
    try {
      return getName().substring(clazz.getName().length());
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new InternalError("Malformed class name", indexOutOfBoundsException);
    } 
  }
  
  private boolean isLocalOrAnonymousClass() { return (getEnclosingMethodInfo() != null); }
  
  @CallerSensitive
  public Class<?>[] getClasses() {
    checkMemberAccess(0, Reflection.getCallerClass(), false);
    return (Class[])AccessController.doPrivileged(new PrivilegedAction<Class<?>[]>() {
          public Class<?>[] run() {
            ArrayList arrayList = new ArrayList();
            for (Class clazz = Class.this; clazz != null; clazz = clazz.getSuperclass()) {
              Class[] arrayOfClass = clazz.getDeclaredClasses();
              for (byte b = 0; b < arrayOfClass.length; b++) {
                if (Modifier.isPublic(arrayOfClass[b].getModifiers()))
                  arrayList.add(arrayOfClass[b]); 
              } 
            } 
            return (Class[])arrayList.toArray(new Class[0]);
          }
        });
  }
  
  @CallerSensitive
  public Field[] getFields() throws SecurityException {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return copyFields(privateGetPublicFields(null));
  }
  
  @CallerSensitive
  public Method[] getMethods() throws SecurityException {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return copyMethods(privateGetPublicMethods());
  }
  
  @CallerSensitive
  public Constructor<?>[] getConstructors() throws SecurityException {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return copyConstructors(privateGetDeclaredConstructors(true));
  }
  
  @CallerSensitive
  public Field getField(String paramString) throws NoSuchFieldException, SecurityException {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    Field field = getField0(paramString);
    if (field == null)
      throw new NoSuchFieldException(paramString); 
    return field;
  }
  
  @CallerSensitive
  public Method getMethod(String paramString, Class<?>... paramVarArgs) throws NoSuchMethodException, SecurityException {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    Method method = getMethod0(paramString, paramVarArgs, true);
    if (method == null)
      throw new NoSuchMethodException(getName() + "." + paramString + argumentTypesToString(paramVarArgs)); 
    return method;
  }
  
  @CallerSensitive
  public Constructor<T> getConstructor(Class<?>... paramVarArgs) throws NoSuchMethodException, SecurityException {
    checkMemberAccess(0, Reflection.getCallerClass(), true);
    return getConstructor0(paramVarArgs, 0);
  }
  
  @CallerSensitive
  public Class<?>[] getDeclaredClasses() {
    checkMemberAccess(1, Reflection.getCallerClass(), false);
    return getDeclaredClasses0();
  }
  
  @CallerSensitive
  public Field[] getDeclaredFields() throws SecurityException {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return copyFields(privateGetDeclaredFields(false));
  }
  
  @CallerSensitive
  public Method[] getDeclaredMethods() throws SecurityException {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return copyMethods(privateGetDeclaredMethods(false));
  }
  
  @CallerSensitive
  public Constructor<?>[] getDeclaredConstructors() throws SecurityException {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return copyConstructors(privateGetDeclaredConstructors(false));
  }
  
  @CallerSensitive
  public Field getDeclaredField(String paramString) throws NoSuchFieldException, SecurityException {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    Field field = searchFields(privateGetDeclaredFields(false), paramString);
    if (field == null)
      throw new NoSuchFieldException(paramString); 
    return field;
  }
  
  @CallerSensitive
  public Method getDeclaredMethod(String paramString, Class<?>... paramVarArgs) throws NoSuchMethodException, SecurityException {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    Method method = searchMethods(privateGetDeclaredMethods(false), paramString, paramVarArgs);
    if (method == null)
      throw new NoSuchMethodException(getName() + "." + paramString + argumentTypesToString(paramVarArgs)); 
    return method;
  }
  
  @CallerSensitive
  public Constructor<T> getDeclaredConstructor(Class<?>... paramVarArgs) throws NoSuchMethodException, SecurityException {
    checkMemberAccess(1, Reflection.getCallerClass(), true);
    return getConstructor0(paramVarArgs, 1);
  }
  
  public InputStream getResourceAsStream(String paramString) {
    paramString = resolveName(paramString);
    ClassLoader classLoader1 = getClassLoader0();
    return (classLoader1 == null) ? ClassLoader.getSystemResourceAsStream(paramString) : classLoader1.getResourceAsStream(paramString);
  }
  
  public URL getResource(String paramString) {
    paramString = resolveName(paramString);
    ClassLoader classLoader1 = getClassLoader0();
    return (classLoader1 == null) ? ClassLoader.getSystemResource(paramString) : classLoader1.getResource(paramString);
  }
  
  public ProtectionDomain getProtectionDomain() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.GET_PD_PERMISSION); 
    ProtectionDomain protectionDomain = getProtectionDomain0();
    if (protectionDomain == null) {
      if (allPermDomain == null) {
        Permissions permissions = new Permissions();
        permissions.add(SecurityConstants.ALL_PERMISSION);
        allPermDomain = new ProtectionDomain(null, permissions);
      } 
      protectionDomain = allPermDomain;
    } 
    return protectionDomain;
  }
  
  private native ProtectionDomain getProtectionDomain0();
  
  static native Class<?> getPrimitiveClass(String paramString) throws ClassNotFoundException;
  
  private void checkMemberAccess(int paramInt, Class<?> paramClass, boolean paramBoolean) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      ClassLoader classLoader1 = ClassLoader.getClassLoader(paramClass);
      ClassLoader classLoader2 = getClassLoader0();
      if (paramInt != 0 && classLoader1 != classLoader2)
        securityManager.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION); 
      checkPackageAccess(classLoader1, paramBoolean);
    } 
  }
  
  private void checkPackageAccess(ClassLoader paramClassLoader, boolean paramBoolean) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      ClassLoader classLoader1 = getClassLoader0();
      if (ReflectUtil.needsPackageAccessCheck(paramClassLoader, classLoader1)) {
        String str = getName();
        int i = str.lastIndexOf('.');
        if (i != -1) {
          String str1 = str.substring(0, i);
          if (!Proxy.isProxyClass(this) || ReflectUtil.isNonPublicProxyClass(this))
            securityManager.checkPackageAccess(str1); 
        } 
      } 
      if (paramBoolean && Proxy.isProxyClass(this))
        ReflectUtil.checkProxyPackageAccess(paramClassLoader, getInterfaces()); 
    } 
  }
  
  private String resolveName(String paramString) {
    if (paramString == null)
      return paramString; 
    if (!paramString.startsWith("/")) {
      Class clazz;
      for (clazz = this; clazz.isArray(); clazz = clazz.getComponentType());
      String str = clazz.getName();
      int i = str.lastIndexOf('.');
      if (i != -1)
        paramString = str.substring(0, i).replace('.', '/') + "/" + paramString; 
    } else {
      paramString = paramString.substring(1);
    } 
    return paramString;
  }
  
  private ReflectionData<T> reflectionData() {
    SoftReference softReference = this.reflectionData;
    int i = this.classRedefinedCount;
    ReflectionData reflectionData1;
    return (useCaches && softReference != null && (reflectionData1 = (ReflectionData)softReference.get()) != null && reflectionData1.redefinedCount == i) ? reflectionData1 : newReflectionData(softReference, i);
  }
  
  private ReflectionData<T> newReflectionData(SoftReference<ReflectionData<T>> paramSoftReference, int paramInt) {
    if (!useCaches)
      return null; 
    ReflectionData reflectionData1;
    do {
      ReflectionData reflectionData2 = new ReflectionData(paramInt);
      if (Atomic.casReflectionData(this, paramSoftReference, new SoftReference(reflectionData2)))
        return reflectionData2; 
      paramSoftReference = this.reflectionData;
      paramInt = this.classRedefinedCount;
    } while (paramSoftReference == null || (reflectionData1 = (ReflectionData)paramSoftReference.get()) == null || reflectionData1.redefinedCount != paramInt);
    return reflectionData1;
  }
  
  private native String getGenericSignature0();
  
  private GenericsFactory getFactory() { return CoreReflectionFactory.make(this, ClassScope.make(this)); }
  
  private ClassRepository getGenericInfo() {
    ClassRepository classRepository = this.genericInfo;
    if (classRepository == null) {
      String str = getGenericSignature0();
      if (str == null) {
        classRepository = ClassRepository.NONE;
      } else {
        classRepository = ClassRepository.make(str, getFactory());
      } 
      this.genericInfo = classRepository;
    } 
    return (classRepository != ClassRepository.NONE) ? classRepository : null;
  }
  
  native byte[] getRawAnnotations();
  
  native byte[] getRawTypeAnnotations();
  
  static byte[] getExecutableTypeAnnotationBytes(Executable paramExecutable) { return getReflectionFactory().getExecutableTypeAnnotationBytes(paramExecutable); }
  
  native ConstantPool getConstantPool();
  
  private Field[] privateGetDeclaredFields(boolean paramBoolean) {
    checkInitted();
    ReflectionData reflectionData1 = reflectionData();
    if (reflectionData1 != null) {
      Field[] arrayOfField1 = paramBoolean ? reflectionData1.declaredPublicFields : reflectionData1.declaredFields;
      if (arrayOfField1 != null)
        return arrayOfField1; 
    } 
    Field[] arrayOfField = Reflection.filterFields(this, getDeclaredFields0(paramBoolean));
    if (reflectionData1 != null)
      if (paramBoolean) {
        reflectionData1.declaredPublicFields = arrayOfField;
      } else {
        reflectionData1.declaredFields = arrayOfField;
      }  
    return arrayOfField;
  }
  
  private Field[] privateGetPublicFields(Set<Class<?>> paramSet) {
    checkInitted();
    ReflectionData reflectionData1 = reflectionData();
    if (reflectionData1 != null) {
      Field[] arrayOfField = reflectionData1.publicFields;
      if (arrayOfField != null)
        return arrayOfField; 
    } 
    ArrayList arrayList = new ArrayList();
    if (paramSet == null)
      paramSet = new HashSet<Class<?>>(); 
    Field[] arrayOfField2 = privateGetDeclaredFields(true);
    addAll(arrayList, arrayOfField2);
    for (Class clazz : getInterfaces()) {
      if (!paramSet.contains(clazz)) {
        paramSet.add(clazz);
        addAll(arrayList, clazz.privateGetPublicFields(paramSet));
      } 
    } 
    if (!isInterface()) {
      Class clazz = getSuperclass();
      if (clazz != null)
        addAll(arrayList, clazz.privateGetPublicFields(paramSet)); 
    } 
    Field[] arrayOfField1 = new Field[arrayList.size()];
    arrayList.toArray(arrayOfField1);
    if (reflectionData1 != null)
      reflectionData1.publicFields = arrayOfField1; 
    return arrayOfField1;
  }
  
  private static void addAll(Collection<Field> paramCollection, Field[] paramArrayOfField) {
    for (byte b = 0; b < paramArrayOfField.length; b++)
      paramCollection.add(paramArrayOfField[b]); 
  }
  
  private Constructor<T>[] privateGetDeclaredConstructors(boolean paramBoolean) {
    Constructor[] arrayOfConstructor;
    checkInitted();
    ReflectionData reflectionData1 = reflectionData();
    if (reflectionData1 != null) {
      arrayOfConstructor = paramBoolean ? reflectionData1.publicConstructors : reflectionData1.declaredConstructors;
      if (arrayOfConstructor != null)
        return arrayOfConstructor; 
    } 
    if (isInterface()) {
      Constructor[] arrayOfConstructor1 = (Constructor[])new Constructor[0];
      arrayOfConstructor = arrayOfConstructor1;
    } else {
      arrayOfConstructor = getDeclaredConstructors0(paramBoolean);
    } 
    if (reflectionData1 != null)
      if (paramBoolean) {
        reflectionData1.publicConstructors = arrayOfConstructor;
      } else {
        reflectionData1.declaredConstructors = arrayOfConstructor;
      }  
    return arrayOfConstructor;
  }
  
  private Method[] privateGetDeclaredMethods(boolean paramBoolean) {
    checkInitted();
    ReflectionData reflectionData1 = reflectionData();
    if (reflectionData1 != null) {
      Method[] arrayOfMethod1 = paramBoolean ? reflectionData1.declaredPublicMethods : reflectionData1.declaredMethods;
      if (arrayOfMethod1 != null)
        return arrayOfMethod1; 
    } 
    Method[] arrayOfMethod = Reflection.filterMethods(this, getDeclaredMethods0(paramBoolean));
    if (reflectionData1 != null)
      if (paramBoolean) {
        reflectionData1.declaredPublicMethods = arrayOfMethod;
      } else {
        reflectionData1.declaredMethods = arrayOfMethod;
      }  
    return arrayOfMethod;
  }
  
  private Method[] privateGetPublicMethods() throws SecurityException {
    checkInitted();
    ReflectionData reflectionData1 = reflectionData();
    if (reflectionData1 != null) {
      Method[] arrayOfMethod = reflectionData1.publicMethods;
      if (arrayOfMethod != null)
        return arrayOfMethod; 
    } 
    MethodArray methodArray1 = new MethodArray();
    Method[] arrayOfMethod2 = privateGetDeclaredMethods(true);
    methodArray1.addAll(arrayOfMethod2);
    MethodArray methodArray2 = new MethodArray();
    for (Class clazz : getInterfaces())
      methodArray2.addInterfaceMethods(clazz.privateGetPublicMethods()); 
    if (!isInterface()) {
      Class clazz = getSuperclass();
      if (clazz != null) {
        MethodArray methodArray = new MethodArray();
        methodArray.addAll(clazz.privateGetPublicMethods());
        for (byte b1 = 0; b1 < methodArray.length(); b1++) {
          Method method = methodArray.get(b1);
          if (method != null && !Modifier.isAbstract(method.getModifiers()) && !method.isDefault())
            methodArray2.removeByNameAndDescriptor(method); 
        } 
        methodArray.addAll(methodArray2);
        methodArray2 = methodArray;
      } 
    } 
    for (byte b = 0; b < methodArray1.length(); b++) {
      Method method = methodArray1.get(b);
      methodArray2.removeByNameAndDescriptor(method);
    } 
    methodArray1.addAllIfNotPresent(methodArray2);
    methodArray1.removeLessSpecifics();
    methodArray1.compactAndTrim();
    Method[] arrayOfMethod1 = methodArray1.getArray();
    if (reflectionData1 != null)
      reflectionData1.publicMethods = arrayOfMethod1; 
    return arrayOfMethod1;
  }
  
  private static Field searchFields(Field[] paramArrayOfField, String paramString) {
    String str = paramString.intern();
    for (byte b = 0; b < paramArrayOfField.length; b++) {
      if (paramArrayOfField[b].getName() == str)
        return getReflectionFactory().copyField(paramArrayOfField[b]); 
    } 
    return null;
  }
  
  private Field getField0(String paramString) throws NoSuchFieldException, SecurityException {
    Field field;
    if ((field = searchFields(privateGetDeclaredFields(true), paramString)) != null)
      return field; 
    Class[] arrayOfClass = getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      Class clazz = arrayOfClass[b];
      if ((field = clazz.getField0(paramString)) != null)
        return field; 
    } 
    if (!isInterface()) {
      Class clazz = getSuperclass();
      if (clazz != null && (field = clazz.getField0(paramString)) != null)
        return field; 
    } 
    return null;
  }
  
  private static Method searchMethods(Method[] paramArrayOfMethod, String paramString, Class<?>[] paramArrayOfClass) {
    Method method = null;
    String str = paramString.intern();
    for (byte b = 0; b < paramArrayOfMethod.length; b++) {
      Method method1 = paramArrayOfMethod[b];
      if (method1.getName() == str && arrayContentsEq(paramArrayOfClass, method1.getParameterTypes()) && (method == null || method.getReturnType().isAssignableFrom(method1.getReturnType())))
        method = method1; 
    } 
    return (method == null) ? method : getReflectionFactory().copyMethod(method);
  }
  
  private Method getMethod0(String paramString, Class<?>[] paramArrayOfClass, boolean paramBoolean) {
    MethodArray methodArray = new MethodArray(2);
    Method method = privateGetMethodRecursive(paramString, paramArrayOfClass, paramBoolean, methodArray);
    if (method != null)
      return method; 
    methodArray.removeLessSpecifics();
    return methodArray.getFirst();
  }
  
  private Method privateGetMethodRecursive(String paramString, Class<?>[] paramArrayOfClass, boolean paramBoolean, MethodArray paramMethodArray) {
    Method method;
    if ((method = searchMethods(privateGetDeclaredMethods(true), paramString, paramArrayOfClass)) != null && (paramBoolean || !Modifier.isStatic(method.getModifiers())))
      return method; 
    if (!isInterface()) {
      Class clazz = getSuperclass();
      if (clazz != null && (method = clazz.getMethod0(paramString, paramArrayOfClass, true)) != null)
        return method; 
    } 
    Class[] arrayOfClass = getInterfaces();
    for (Class clazz : arrayOfClass) {
      if ((method = clazz.getMethod0(paramString, paramArrayOfClass, false)) != null)
        paramMethodArray.add(method); 
    } 
    return null;
  }
  
  private Constructor<T> getConstructor0(Class<?>[] paramArrayOfClass, int paramInt) throws NoSuchMethodException {
    Constructor[] arrayOfConstructor = privateGetDeclaredConstructors((paramInt == 0));
    for (Constructor constructor : arrayOfConstructor) {
      if (arrayContentsEq(paramArrayOfClass, constructor.getParameterTypes()))
        return getReflectionFactory().copyConstructor(constructor); 
    } 
    throw new NoSuchMethodException(getName() + ".<init>" + argumentTypesToString(paramArrayOfClass));
  }
  
  private static boolean arrayContentsEq(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2) {
    if (paramArrayOfObject1 == null)
      return (paramArrayOfObject2 == null || paramArrayOfObject2.length == 0); 
    if (paramArrayOfObject2 == null)
      return (paramArrayOfObject1.length == 0); 
    if (paramArrayOfObject1.length != paramArrayOfObject2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfObject1.length; b++) {
      if (paramArrayOfObject1[b] != paramArrayOfObject2[b])
        return false; 
    } 
    return true;
  }
  
  private static Field[] copyFields(Field[] paramArrayOfField) {
    Field[] arrayOfField = new Field[paramArrayOfField.length];
    ReflectionFactory reflectionFactory1 = getReflectionFactory();
    for (byte b = 0; b < paramArrayOfField.length; b++)
      arrayOfField[b] = reflectionFactory1.copyField(paramArrayOfField[b]); 
    return arrayOfField;
  }
  
  private static Method[] copyMethods(Method[] paramArrayOfMethod) {
    Method[] arrayOfMethod = new Method[paramArrayOfMethod.length];
    ReflectionFactory reflectionFactory1 = getReflectionFactory();
    for (byte b = 0; b < paramArrayOfMethod.length; b++)
      arrayOfMethod[b] = reflectionFactory1.copyMethod(paramArrayOfMethod[b]); 
    return arrayOfMethod;
  }
  
  private static <U> Constructor<U>[] copyConstructors(Constructor<U>[] paramArrayOfConstructor) {
    Constructor[] arrayOfConstructor = (Constructor[])paramArrayOfConstructor.clone();
    ReflectionFactory reflectionFactory1 = getReflectionFactory();
    for (byte b = 0; b < arrayOfConstructor.length; b++)
      arrayOfConstructor[b] = reflectionFactory1.copyConstructor(arrayOfConstructor[b]); 
    return arrayOfConstructor;
  }
  
  private native Field[] getDeclaredFields0(boolean paramBoolean);
  
  private native Method[] getDeclaredMethods0(boolean paramBoolean);
  
  private native Constructor<T>[] getDeclaredConstructors0(boolean paramBoolean);
  
  private native Class<?>[] getDeclaredClasses0();
  
  private static String argumentTypesToString(Class<?>[] paramArrayOfClass) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("(");
    if (paramArrayOfClass != null)
      for (byte b = 0; b < paramArrayOfClass.length; b++) {
        if (b)
          stringBuilder.append(", "); 
        Class<?> clazz = paramArrayOfClass[b];
        stringBuilder.append((clazz == null) ? "null" : clazz.getName());
      }  
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
  
  public boolean desiredAssertionStatus() {
    ClassLoader classLoader1 = getClassLoader();
    if (classLoader1 == null)
      return desiredAssertionStatus0(this); 
    synchronized (classLoader1.assertionLock) {
      if (classLoader1.classAssertionStatus != null)
        return classLoader1.desiredAssertionStatus(getName()); 
    } 
    return desiredAssertionStatus0(this);
  }
  
  private static native boolean desiredAssertionStatus0(Class<?> paramClass);
  
  public boolean isEnum() { return ((getModifiers() & 0x4000) != 0 && getSuperclass() == Enum.class); }
  
  private static ReflectionFactory getReflectionFactory() {
    if (reflectionFactory == null)
      reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction()); 
    return reflectionFactory;
  }
  
  private static void checkInitted() {
    if (initted)
      return; 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            if (System.out == null)
              return null; 
            String str = System.getProperty("sun.reflect.noCaches");
            if (str != null && str.equals("true"))
              useCaches = false; 
            initted = true;
            return null;
          }
        });
  }
  
  public T[] getEnumConstants() {
    Object[] arrayOfObject = getEnumConstantsShared();
    return (T[])((arrayOfObject != null) ? (Object[])arrayOfObject.clone() : null);
  }
  
  T[] getEnumConstantsShared() {
    if (this.enumConstants == null) {
      if (!isEnum())
        return null; 
      try {
        final Method values = getMethod("values", new Class[0]);
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                values.setAccessible(true);
                return null;
              }
            });
        Object[] arrayOfObject = (Object[])method.invoke(null, new Object[0]);
        this.enumConstants = arrayOfObject;
      } catch (InvocationTargetException|NoSuchMethodException|IllegalAccessException invocationTargetException) {
        return null;
      } 
    } 
    return (T[])this.enumConstants;
  }
  
  Map<String, T> enumConstantDirectory() {
    if (this.enumConstantDirectory == null) {
      Object[] arrayOfObject = getEnumConstantsShared();
      if (arrayOfObject == null)
        throw new IllegalArgumentException(getName() + " is not an enum type"); 
      HashMap hashMap = new HashMap(2 * arrayOfObject.length);
      for (Object object : arrayOfObject)
        hashMap.put(((Enum)object).name(), object); 
      this.enumConstantDirectory = hashMap;
    } 
    return this.enumConstantDirectory;
  }
  
  public T cast(Object paramObject) {
    if (paramObject != null && !isInstance(paramObject))
      throw new ClassCastException(cannotCastMsg(paramObject)); 
    return (T)paramObject;
  }
  
  private String cannotCastMsg(Object paramObject) { return "Cannot cast " + paramObject.getClass().getName() + " to " + getName(); }
  
  public <U> Class<? extends U> asSubclass(Class<U> paramClass) {
    if (paramClass.isAssignableFrom(this))
      return this; 
    throw new ClassCastException(toString());
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass) {
    Objects.requireNonNull(paramClass);
    return (A)(Annotation)(annotationData()).annotations.get(paramClass);
  }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> paramClass) { return super.isAnnotationPresent(paramClass); }
  
  public <A extends Annotation> A[] getAnnotationsByType(Class<A> paramClass) {
    Objects.requireNonNull(paramClass);
    AnnotationData annotationData1 = annotationData();
    return (A[])AnnotationSupport.getAssociatedAnnotations(annotationData1.declaredAnnotations, this, paramClass);
  }
  
  public Annotation[] getAnnotations() { return AnnotationParser.toArray((annotationData()).annotations); }
  
  public <A extends Annotation> A getDeclaredAnnotation(Class<A> paramClass) {
    Objects.requireNonNull(paramClass);
    return (A)(Annotation)(annotationData()).declaredAnnotations.get(paramClass);
  }
  
  public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> paramClass) {
    Objects.requireNonNull(paramClass);
    return (A[])AnnotationSupport.getDirectlyAndIndirectlyPresent((annotationData()).declaredAnnotations, paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations() { return AnnotationParser.toArray((annotationData()).declaredAnnotations); }
  
  private AnnotationData annotationData() {
    AnnotationData annotationData2;
    AnnotationData annotationData1;
    do {
      annotationData1 = this.annotationData;
      int i = this.classRedefinedCount;
      if (annotationData1 != null && annotationData1.redefinedCount == i)
        return annotationData1; 
      annotationData2 = createAnnotationData(i);
    } while (!Atomic.casAnnotationData(this, annotationData1, annotationData2));
    return annotationData2;
  }
  
  private AnnotationData createAnnotationData(int paramInt) {
    Map map1 = AnnotationParser.parseAnnotations(getRawAnnotations(), getConstantPool(), this);
    Class clazz = getSuperclass();
    Map map2 = null;
    if (clazz != null) {
      Map map = (clazz.annotationData()).annotations;
      for (Map.Entry entry : map.entrySet()) {
        Class clazz1 = (Class)entry.getKey();
        if (AnnotationType.getInstance(clazz1).isInherited()) {
          if (map2 == null)
            map2 = new LinkedHashMap((Math.max(map1.size(), Math.min(12, map1.size() + map.size())) * 4 + 2) / 3); 
          map2.put(clazz1, entry.getValue());
        } 
      } 
    } 
    if (map2 == null) {
      map2 = map1;
    } else {
      map2.putAll(map1);
    } 
    return new AnnotationData(map2, map1, paramInt);
  }
  
  boolean casAnnotationType(AnnotationType paramAnnotationType1, AnnotationType paramAnnotationType2) { return Atomic.casAnnotationType(this, paramAnnotationType1, paramAnnotationType2); }
  
  AnnotationType getAnnotationType() { return this.annotationType; }
  
  Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap() { return (annotationData()).declaredAnnotations; }
  
  public AnnotatedType getAnnotatedSuperclass() { return (this == Object.class || isInterface() || isArray() || isPrimitive() || this == void.class) ? null : TypeAnnotationParser.buildAnnotatedSuperclass(getRawTypeAnnotations(), getConstantPool(), this); }
  
  public AnnotatedType[] getAnnotatedInterfaces() { return TypeAnnotationParser.buildAnnotatedInterfaces(getRawTypeAnnotations(), getConstantPool(), this); }
  
  static  {
    registerNatives();
    useCaches = true;
    serialPersistentFields = new ObjectStreamField[0];
    initted = false;
  }
  
  private static class AnnotationData {
    final Map<Class<? extends Annotation>, Annotation> annotations;
    
    final Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
    
    final int redefinedCount;
    
    AnnotationData(Map<Class<? extends Annotation>, Annotation> param1Map1, Map<Class<? extends Annotation>, Annotation> param1Map2, int param1Int) {
      this.annotations = param1Map1;
      this.declaredAnnotations = param1Map2;
      this.redefinedCount = param1Int;
    }
  }
  
  private static class Atomic {
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    
    private static final long reflectionDataOffset;
    
    private static final long annotationTypeOffset;
    
    private static final long annotationDataOffset;
    
    private static long objectFieldOffset(Field[] param1ArrayOfField, String param1String) {
      Field field = Class.searchFields(param1ArrayOfField, param1String);
      if (field == null)
        throw new Error("No " + param1String + " field found in java.lang.Class"); 
      return unsafe.objectFieldOffset(field);
    }
    
    static <T> boolean casReflectionData(Class<?> param1Class, SoftReference<Class.ReflectionData<T>> param1SoftReference1, SoftReference<Class.ReflectionData<T>> param1SoftReference2) { return unsafe.compareAndSwapObject(param1Class, reflectionDataOffset, param1SoftReference1, param1SoftReference2); }
    
    static <T> boolean casAnnotationType(Class<?> param1Class, AnnotationType param1AnnotationType1, AnnotationType param1AnnotationType2) { return unsafe.compareAndSwapObject(param1Class, annotationTypeOffset, param1AnnotationType1, param1AnnotationType2); }
    
    static <T> boolean casAnnotationData(Class<?> param1Class, Class.AnnotationData param1AnnotationData1, Class.AnnotationData param1AnnotationData2) { return unsafe.compareAndSwapObject(param1Class, annotationDataOffset, param1AnnotationData1, param1AnnotationData2); }
    
    static  {
      Field[] arrayOfField = Class.class.getDeclaredFields0(false);
      reflectionDataOffset = objectFieldOffset(arrayOfField, "reflectionData");
      annotationTypeOffset = objectFieldOffset(arrayOfField, "annotationType");
      annotationDataOffset = objectFieldOffset(arrayOfField, "annotationData");
    }
  }
  
  private static final class EnclosingMethodInfo {
    private Class<?> enclosingClass;
    
    private String name;
    
    private String descriptor;
    
    private EnclosingMethodInfo(Object[] param1ArrayOfObject) {
      if (param1ArrayOfObject.length != 3)
        throw new InternalError("Malformed enclosing method information"); 
      try {
        this.enclosingClass = (Class)param1ArrayOfObject[0];
        assert this.enclosingClass != null;
        this.name = (String)param1ArrayOfObject[1];
        this.descriptor = (String)param1ArrayOfObject[2];
        assert this.name == this.descriptor;
      } catch (ClassCastException classCastException) {
        throw new InternalError("Invalid type in enclosing method information", classCastException);
      } 
    }
    
    boolean isPartial() { return (this.enclosingClass == null || this.name == null || this.descriptor == null); }
    
    boolean isConstructor() { return (!isPartial() && "<init>".equals(this.name)); }
    
    boolean isMethod() { return (!isPartial() && !isConstructor() && !"<clinit>".equals(this.name)); }
    
    Class<?> getEnclosingClass() { return this.enclosingClass; }
    
    String getName() { return this.name; }
    
    String getDescriptor() { return this.descriptor; }
  }
  
  static class MethodArray {
    private Method[] methods;
    
    private int length;
    
    private int defaults;
    
    MethodArray() { this(20); }
    
    MethodArray(int param1Int) {
      if (param1Int < 2)
        throw new IllegalArgumentException("Size should be 2 or more"); 
      this.methods = new Method[param1Int];
      this.length = 0;
      this.defaults = 0;
    }
    
    boolean hasDefaults() { return (this.defaults != 0); }
    
    void add(Method param1Method) {
      if (this.length == this.methods.length)
        this.methods = (Method[])Arrays.copyOf(this.methods, 2 * this.methods.length); 
      this.methods[this.length++] = param1Method;
      if (param1Method != null && param1Method.isDefault())
        this.defaults++; 
    }
    
    void addAll(Method[] param1ArrayOfMethod) {
      for (byte b = 0; b < param1ArrayOfMethod.length; b++)
        add(param1ArrayOfMethod[b]); 
    }
    
    void addAll(MethodArray param1MethodArray) {
      for (byte b = 0; b < param1MethodArray.length(); b++)
        add(param1MethodArray.get(b)); 
    }
    
    void addIfNotPresent(Method param1Method) {
      for (byte b = 0; b < this.length; b++) {
        Method method = this.methods[b];
        if (method == param1Method || (method != null && method.equals(param1Method)))
          return; 
      } 
      add(param1Method);
    }
    
    void addAllIfNotPresent(MethodArray param1MethodArray) {
      for (byte b = 0; b < param1MethodArray.length(); b++) {
        Method method = param1MethodArray.get(b);
        if (method != null)
          addIfNotPresent(method); 
      } 
    }
    
    void addInterfaceMethods(Method[] param1ArrayOfMethod) {
      for (Method method : param1ArrayOfMethod) {
        if (!Modifier.isStatic(method.getModifiers()))
          add(method); 
      } 
    }
    
    int length() { return this.length; }
    
    Method get(int param1Int) { return this.methods[param1Int]; }
    
    Method getFirst() throws SecurityException {
      for (Method method : this.methods) {
        if (method != null)
          return method; 
      } 
      return null;
    }
    
    void removeByNameAndDescriptor(Method param1Method) {
      for (byte b = 0; b < this.length; b++) {
        Method method = this.methods[b];
        if (method != null && matchesNameAndDescriptor(method, param1Method))
          remove(b); 
      } 
    }
    
    private void remove(int param1Int) {
      if (this.methods[param1Int] != null && this.methods[param1Int].isDefault())
        this.defaults--; 
      this.methods[param1Int] = null;
    }
    
    private boolean matchesNameAndDescriptor(Method param1Method1, Method param1Method2) { return (param1Method1.getReturnType() == param1Method2.getReturnType() && param1Method1.getName() == param1Method2.getName() && Class.arrayContentsEq(param1Method1.getParameterTypes(), param1Method2.getParameterTypes())); }
    
    void compactAndTrim() {
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.length; b2++) {
        Method method = this.methods[b2];
        if (method != null) {
          if (b2 != b1)
            this.methods[b1] = method; 
          b1++;
        } 
      } 
      if (b1 != this.methods.length)
        this.methods = (Method[])Arrays.copyOf(this.methods, b1); 
    }
    
    void removeLessSpecifics() {
      if (!hasDefaults())
        return; 
      for (byte b = 0; b < this.length; b++) {
        Method method = get(b);
        if (method != null && method.isDefault())
          for (byte b1 = 0; b1 < this.length; b1++) {
            if (b != b1) {
              Method method1 = get(b1);
              if (method1 != null && matchesNameAndDescriptor(method, method1) && hasMoreSpecificClass(method, method1))
                remove(b1); 
            } 
          }  
      } 
    }
    
    Method[] getArray() throws SecurityException { return this.methods; }
    
    static boolean hasMoreSpecificClass(Method param1Method1, Method param1Method2) {
      Class clazz1 = param1Method1.getDeclaringClass();
      Class clazz2 = param1Method2.getDeclaringClass();
      return (clazz1 != clazz2 && clazz2.isAssignableFrom(clazz1));
    }
  }
  
  private static class ReflectionData<T> extends Object {
    final int redefinedCount;
    
    ReflectionData(int param1Int) { this.redefinedCount = param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Class.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */