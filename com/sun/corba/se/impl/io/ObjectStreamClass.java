package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.util.RepositoryId;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import org.omg.CORBA.ValueMember;
import sun.corba.Bridge;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;

public class ObjectStreamClass implements Serializable {
  private static final boolean DEBUG_SVUID = false;
  
  public static final long kDefaultUID = -1L;
  
  private static Object[] noArgsList = new Object[0];
  
  private static Class<?>[] noTypesList = new Class[0];
  
  private boolean isEnum;
  
  private static final Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction<Bridge>() {
        public Bridge run() { return Bridge.get(); }
      });
  
  private static final PersistentFieldsValue persistentFieldsValue = new PersistentFieldsValue();
  
  public static final int CLASS_MASK = 1553;
  
  public static final int FIELD_MASK = 223;
  
  public static final int METHOD_MASK = 3391;
  
  private static ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];
  
  private String name;
  
  private ObjectStreamClass superclass;
  
  private boolean serializable;
  
  private boolean externalizable;
  
  private ObjectStreamField[] fields;
  
  private Class<?> ofClass;
  
  boolean forProxyClass;
  
  private long suid = -1L;
  
  private String suidStr = null;
  
  private long actualSuid = -1L;
  
  private String actualSuidStr = null;
  
  int primBytes;
  
  int objFields;
  
  private boolean initialized = false;
  
  private Object lock = new Object();
  
  private boolean hasExternalizableBlockData;
  
  Method writeObjectMethod;
  
  Method readObjectMethod;
  
  private Method writeReplaceObjectMethod;
  
  private Method readResolveObjectMethod;
  
  private Constructor<?> cons;
  
  private ProtectionDomain[] domains;
  
  private String rmiiiopOptionalDataRepId = null;
  
  private ObjectStreamClass localClassDesc;
  
  private static Method hasStaticInitializerMethod = null;
  
  private static final long serialVersionUID = -6120832682080437368L;
  
  public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
  
  private static Comparator compareClassByName = new CompareClassByName(null);
  
  private static final Comparator compareObjStrFieldsByName = new CompareObjStrFieldsByName(null);
  
  private static Comparator compareMemberByName = new CompareMemberByName(null);
  
  static final ObjectStreamClass lookup(Class<?> paramClass) {
    ObjectStreamClass objectStreamClass = lookupInternal(paramClass);
    return (objectStreamClass.isSerializable() || objectStreamClass.isExternalizable()) ? objectStreamClass : null;
  }
  
  static ObjectStreamClass lookupInternal(Class<?> paramClass) {
    ObjectStreamClass objectStreamClass = null;
    synchronized (descriptorFor) {
      objectStreamClass = findDescriptorFor(paramClass);
      if (objectStreamClass == null) {
        boolean bool1 = Serializable.class.isAssignableFrom(paramClass);
        ObjectStreamClass objectStreamClass1 = null;
        if (bool1) {
          Class clazz = paramClass.getSuperclass();
          if (clazz != null)
            objectStreamClass1 = lookup(clazz); 
        } 
        boolean bool2 = false;
        if (bool1) {
          bool2 = ((objectStreamClass1 != null && objectStreamClass1.isExternalizable()) || java.io.Externalizable.class.isAssignableFrom(paramClass));
          if (bool2)
            bool1 = false; 
        } 
        objectStreamClass = new ObjectStreamClass(paramClass, objectStreamClass1, bool1, bool2);
      } 
      objectStreamClass.init();
    } 
    return objectStreamClass;
  }
  
  public final String getName() { return this.name; }
  
  public static final long getSerialVersionUID(Class<?> paramClass) {
    ObjectStreamClass objectStreamClass = lookup(paramClass);
    return (objectStreamClass != null) ? objectStreamClass.getSerialVersionUID() : 0L;
  }
  
  public final long getSerialVersionUID() { return this.suid; }
  
  public final String getSerialVersionUIDStr() {
    if (this.suidStr == null)
      this.suidStr = Long.toHexString(this.suid).toUpperCase(); 
    return this.suidStr;
  }
  
  public static final long getActualSerialVersionUID(Class<?> paramClass) {
    ObjectStreamClass objectStreamClass = lookup(paramClass);
    return (objectStreamClass != null) ? objectStreamClass.getActualSerialVersionUID() : 0L;
  }
  
  public final long getActualSerialVersionUID() { return this.actualSuid; }
  
  public final String getActualSerialVersionUIDStr() {
    if (this.actualSuidStr == null)
      this.actualSuidStr = Long.toHexString(this.actualSuid).toUpperCase(); 
    return this.actualSuidStr;
  }
  
  public final Class<?> forClass() { return this.ofClass; }
  
  public ObjectStreamField[] getFields() {
    if (this.fields.length > 0) {
      ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[this.fields.length];
      System.arraycopy(this.fields, 0, arrayOfObjectStreamField, 0, this.fields.length);
      return arrayOfObjectStreamField;
    } 
    return this.fields;
  }
  
  public boolean hasField(ValueMember paramValueMember) {
    try {
      for (byte b = 0; b < this.fields.length; b++) {
        if (this.fields[b].getName().equals(paramValueMember.name) && this.fields[b].getSignature().equals(ValueUtility.getSignature(paramValueMember)))
          return true; 
      } 
    } catch (Exception exception) {}
    return false;
  }
  
  final ObjectStreamField[] getFieldsNoCopy() { return this.fields; }
  
  public final ObjectStreamField getField(String paramString) {
    for (int i = this.fields.length - 1; i >= 0; i--) {
      if (paramString.equals(this.fields[i].getName()))
        return this.fields[i]; 
    } 
    return null;
  }
  
  public Serializable writeReplace(Serializable paramSerializable) {
    if (this.writeReplaceObjectMethod != null)
      try {
        return (Serializable)this.writeReplaceObjectMethod.invoke(paramSerializable, noArgsList);
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }  
    return paramSerializable;
  }
  
  public Object readResolve(Object paramObject) {
    if (this.readResolveObjectMethod != null)
      try {
        return this.readResolveObjectMethod.invoke(paramObject, noArgsList);
      } catch (Throwable throwable) {
        throw new RuntimeException(throwable);
      }  
    return paramObject;
  }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.name);
    stringBuffer.append(": static final long serialVersionUID = ");
    stringBuffer.append(Long.toString(this.suid));
    stringBuffer.append("L;");
    return stringBuffer.toString();
  }
  
  private ObjectStreamClass(Class<?> paramClass, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean1, boolean paramBoolean2) {
    this.ofClass = paramClass;
    if (Proxy.isProxyClass(paramClass))
      this.forProxyClass = true; 
    this.name = paramClass.getName();
    this.isEnum = Enum.class.isAssignableFrom(paramClass);
    this.superclass = paramObjectStreamClass;
    this.serializable = paramBoolean1;
    if (!this.forProxyClass)
      this.externalizable = paramBoolean2; 
    insertDescriptorFor(this);
  }
  
  private ProtectionDomain noPermissionsDomain() {
    Permissions permissions = new Permissions();
    permissions.setReadOnly();
    return new ProtectionDomain(null, permissions);
  }
  
  private ProtectionDomain[] getProtectionDomains(Constructor<?> paramConstructor, Class<?> paramClass) {
    ProtectionDomain[] arrayOfProtectionDomain = null;
    if (paramConstructor != null && paramClass.getClassLoader() != null && System.getSecurityManager() != null) {
      Class<?> clazz1 = paramClass;
      Class clazz2 = paramConstructor.getDeclaringClass();
      HashSet hashSet = null;
      while (clazz1 != clazz2) {
        ProtectionDomain protectionDomain = clazz1.getProtectionDomain();
        if (protectionDomain != null) {
          if (hashSet == null)
            hashSet = new HashSet(); 
          hashSet.add(protectionDomain);
        } 
        clazz1 = clazz1.getSuperclass();
        if (clazz1 == null) {
          if (hashSet == null) {
            hashSet = new HashSet();
          } else {
            hashSet.clear();
          } 
          hashSet.add(noPermissionsDomain());
          break;
        } 
      } 
      if (hashSet != null)
        arrayOfProtectionDomain = (ProtectionDomain[])hashSet.toArray(new ProtectionDomain[0]); 
    } 
    return arrayOfProtectionDomain;
  }
  
  private void init() {
    synchronized (this.lock) {
      if (this.initialized)
        return; 
      final Class cl = this.ofClass;
      if (!this.serializable || this.externalizable || this.forProxyClass || this.name.equals("java.lang.String")) {
        this.fields = NO_FIELDS;
      } else if (this.serializable) {
        AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
                ObjectStreamClass.this.fields = (ObjectStreamField[])persistentFieldsValue.get(cl);
                if (ObjectStreamClass.this.fields == null) {
                  Field[] arrayOfField = cl.getDeclaredFields();
                  byte b1 = 0;
                  ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[arrayOfField.length];
                  for (byte b2 = 0; b2 < arrayOfField.length; b2++) {
                    Field field = arrayOfField[b2];
                    int i = field.getModifiers();
                    if (!Modifier.isStatic(i) && !Modifier.isTransient(i)) {
                      field.setAccessible(true);
                      arrayOfObjectStreamField[b1++] = new ObjectStreamField(field);
                    } 
                  } 
                  ObjectStreamClass.this.fields = new ObjectStreamField[b1];
                  System.arraycopy(arrayOfObjectStreamField, 0, ObjectStreamClass.this.fields, 0, b1);
                } else {
                  for (int i = ObjectStreamClass.this.fields.length - 1; i >= 0; i--) {
                    try {
                      Field field = cl.getDeclaredField(ObjectStreamClass.this.fields[i].getName());
                      if (ObjectStreamClass.this.fields[i].getType() == field.getType()) {
                        field.setAccessible(true);
                        ObjectStreamClass.this.fields[i].setField(field);
                      } 
                    } catch (NoSuchFieldException noSuchFieldException) {}
                  } 
                } 
                return null;
              }
            });
        if (this.fields.length > 1)
          Arrays.sort(this.fields); 
        computeFieldInfo();
      } 
      if (isNonSerializable() || this.isEnum) {
        this.suid = 0L;
      } else {
        AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
                if (ObjectStreamClass.this.forProxyClass) {
                  ObjectStreamClass.this.suid = 0L;
                } else {
                  try {
                    Field field = cl.getDeclaredField("serialVersionUID");
                    int i = field.getModifiers();
                    if (Modifier.isStatic(i) && Modifier.isFinal(i)) {
                      field.setAccessible(true);
                      ObjectStreamClass.this.suid = field.getLong(cl);
                    } else {
                      ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(cl);
                    } 
                  } catch (NoSuchFieldException noSuchFieldException) {
                    ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(cl);
                  } catch (IllegalAccessException illegalAccessException) {
                    ObjectStreamClass.this.suid = ObjectStreamClass._computeSerialVersionUID(cl);
                  } 
                } 
                ObjectStreamClass.this.writeReplaceObjectMethod = ObjectStreamClass.getInheritableMethod(cl, "writeReplace", noTypesList, Object.class);
                ObjectStreamClass.this.readResolveObjectMethod = ObjectStreamClass.getInheritableMethod(cl, "readResolve", noTypesList, Object.class);
                ObjectStreamClass.this.domains = new ProtectionDomain[] { ObjectStreamClass.access$900(ObjectStreamClass.this) };
                if (ObjectStreamClass.this.externalizable) {
                  ObjectStreamClass.this.cons = ObjectStreamClass.getExternalizableConstructor(cl);
                } else {
                  ObjectStreamClass.this.cons = ObjectStreamClass.getSerializableConstructor(cl);
                } 
                ObjectStreamClass.this.domains = ObjectStreamClass.this.getProtectionDomains(ObjectStreamClass.this.cons, cl);
                if (ObjectStreamClass.this.serializable && !ObjectStreamClass.this.forProxyClass) {
                  ObjectStreamClass.this.writeObjectMethod = ObjectStreamClass.getPrivateMethod(cl, "writeObject", new Class[] { java.io.ObjectOutputStream.class }, void.class);
                  ObjectStreamClass.this.readObjectMethod = ObjectStreamClass.getPrivateMethod(cl, "readObject", new Class[] { java.io.ObjectInputStream.class }, void.class);
                } 
                return null;
              }
            });
      } 
      this.actualSuid = computeStructuralUID(this, clazz);
      if (hasWriteObject())
        this.rmiiiopOptionalDataRepId = computeRMIIIOPOptionalDataRepId(); 
      this.initialized = true;
    } 
  }
  
  private static Method getPrivateMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass, Class<?> paramClass2) {
    try {
      Method method = paramClass1.getDeclaredMethod(paramString, paramArrayOfClass);
      method.setAccessible(true);
      int i = method.getModifiers();
      return (method.getReturnType() == paramClass2 && (i & 0x8) == 0 && (i & 0x2) != 0) ? method : null;
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
  
  private String computeRMIIIOPOptionalDataRepId() {
    StringBuffer stringBuffer = new StringBuffer("RMI:org.omg.custom.");
    stringBuffer.append(RepositoryId.convertToISOLatin1(getName()));
    stringBuffer.append(':');
    stringBuffer.append(getActualSerialVersionUIDStr());
    stringBuffer.append(':');
    stringBuffer.append(getSerialVersionUIDStr());
    return stringBuffer.toString();
  }
  
  public final String getRMIIIOPOptionalDataRepId() { return this.rmiiiopOptionalDataRepId; }
  
  ObjectStreamClass(String paramString, long paramLong) {
    this.name = paramString;
    this.suid = paramLong;
    this.superclass = null;
  }
  
  final void setClass(Class<?> paramClass) throws InvalidClassException {
    if (paramClass == null) {
      this.localClassDesc = null;
      this.ofClass = null;
      computeFieldInfo();
      return;
    } 
    this.localClassDesc = lookupInternal(paramClass);
    if (this.localClassDesc == null)
      throw new InvalidClassException(paramClass.getName(), "Local class not compatible"); 
    if (this.suid != this.localClassDesc.suid) {
      boolean bool1 = (isNonSerializable() || this.localClassDesc.isNonSerializable()) ? 1 : 0;
      boolean bool2 = (paramClass.isArray() && !paramClass.getName().equals(this.name)) ? 1 : 0;
      if (!bool2 && !bool1)
        throw new InvalidClassException(paramClass.getName(), "Local class not compatible: stream classdesc serialVersionUID=" + this.suid + " local class serialVersionUID=" + this.localClassDesc.suid); 
    } 
    if (!compareClassNames(this.name, paramClass.getName(), '.'))
      throw new InvalidClassException(paramClass.getName(), "Incompatible local class name. Expected class name compatible with " + this.name); 
    if (this.serializable != this.localClassDesc.serializable || this.externalizable != this.localClassDesc.externalizable || (!this.serializable && !this.externalizable))
      throw new InvalidClassException(paramClass.getName(), "Serialization incompatible with Externalization"); 
    ObjectStreamField[] arrayOfObjectStreamField1 = (ObjectStreamField[])this.localClassDesc.fields;
    ObjectStreamField[] arrayOfObjectStreamField2 = (ObjectStreamField[])this.fields;
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfObjectStreamField2.length; b2++) {
      for (byte b = b1; b < arrayOfObjectStreamField1.length; b++) {
        if (arrayOfObjectStreamField2[b2].getName().equals(arrayOfObjectStreamField1[b].getName())) {
          if (arrayOfObjectStreamField2[b2].isPrimitive() && !arrayOfObjectStreamField2[b2].typeEquals(arrayOfObjectStreamField1[b]))
            throw new InvalidClassException(paramClass.getName(), "The type of field " + arrayOfObjectStreamField2[b2].getName() + " of class " + this.name + " is incompatible."); 
          b1 = b;
          arrayOfObjectStreamField2[b2].setField(arrayOfObjectStreamField1[b1].getField());
          break;
        } 
      } 
    } 
    computeFieldInfo();
    this.ofClass = paramClass;
    this.readObjectMethod = this.localClassDesc.readObjectMethod;
    this.readResolveObjectMethod = this.localClassDesc.readResolveObjectMethod;
  }
  
  static boolean compareClassNames(String paramString1, String paramString2, char paramChar) {
    int i = paramString1.lastIndexOf(paramChar);
    if (i < 0)
      i = 0; 
    int j = paramString2.lastIndexOf(paramChar);
    if (j < 0)
      j = 0; 
    return paramString1.regionMatches(false, i, paramString2, j, paramString1.length() - i);
  }
  
  final boolean typeEquals(ObjectStreamClass paramObjectStreamClass) { return (this.suid == paramObjectStreamClass.suid && compareClassNames(this.name, paramObjectStreamClass.name, '.')); }
  
  final void setSuperclass(ObjectStreamClass paramObjectStreamClass) { this.superclass = paramObjectStreamClass; }
  
  final ObjectStreamClass getSuperclass() { return this.superclass; }
  
  final boolean hasReadObject() { return (this.readObjectMethod != null); }
  
  final boolean hasWriteObject() { return (this.writeObjectMethod != null); }
  
  final boolean isCustomMarshaled() { return (hasWriteObject() || isExternalizable() || (this.superclass != null && this.superclass.isCustomMarshaled())); }
  
  boolean hasExternalizableBlockDataMode() { return this.hasExternalizableBlockData; }
  
  Object newInstance() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
    if (!this.initialized)
      throw new InternalError("Unexpected call when not initialized"); 
    if (this.cons != null)
      try {
        if (this.domains == null || this.domains.length == 0)
          return this.cons.newInstance(new Object[0]); 
        JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
        PrivilegedAction privilegedAction = new PrivilegedAction() {
            public Object run() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
              try {
                return ObjectStreamClass.this.cons.newInstance(new Object[0]);
              } catch (InstantiationException|InvocationTargetException|IllegalAccessException instantiationException) {
                throw new UndeclaredThrowableException(instantiationException);
              } 
            }
          };
        try {
          return javaSecurityAccess.doIntersectionPrivilege(privilegedAction, AccessController.getContext(), new AccessControlContext(this.domains));
        } catch (UndeclaredThrowableException undeclaredThrowableException) {
          Throwable throwable = undeclaredThrowableException.getCause();
          if (throwable instanceof InstantiationException)
            throw (InstantiationException)throwable; 
          if (throwable instanceof InvocationTargetException)
            throw (InvocationTargetException)throwable; 
          if (throwable instanceof IllegalAccessException)
            throw (IllegalAccessException)throwable; 
          throw undeclaredThrowableException;
        } 
      } catch (IllegalAccessException illegalAccessException) {
        InternalError internalError = new InternalError();
        internalError.initCause(illegalAccessException);
        throw internalError;
      }  
    throw new UnsupportedOperationException();
  }
  
  private static Constructor getExternalizableConstructor(Class<?> paramClass) {
    try {
      Constructor constructor = paramClass.getDeclaredConstructor(new Class[0]);
      constructor.setAccessible(true);
      return ((constructor.getModifiers() & true) != 0) ? constructor : null;
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
  
  private static Constructor getSerializableConstructor(Class<?> paramClass) {
    Class<?> clazz = paramClass;
    while (Serializable.class.isAssignableFrom(clazz)) {
      if ((clazz = clazz.getSuperclass()) == null)
        return null; 
    } 
    try {
      Constructor constructor = clazz.getDeclaredConstructor(new Class[0]);
      int i = constructor.getModifiers();
      if ((i & 0x2) != 0 || ((i & 0x5) == 0 && !packageEquals(paramClass, clazz)))
        return null; 
      constructor = bridge.newConstructorForSerialization(paramClass, constructor);
      constructor.setAccessible(true);
      return constructor;
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
  
  final ObjectStreamClass localClassDescriptor() { return this.localClassDesc; }
  
  boolean isSerializable() { return this.serializable; }
  
  boolean isExternalizable() { return this.externalizable; }
  
  boolean isNonSerializable() { return (!this.externalizable && !this.serializable); }
  
  private void computeFieldInfo() {
    this.primBytes = 0;
    this.objFields = 0;
    for (byte b = 0; b < this.fields.length; b++) {
      switch (this.fields[b].getTypeCode()) {
        case 'B':
        case 'Z':
          this.primBytes++;
          break;
        case 'C':
        case 'S':
          this.primBytes += 2;
          break;
        case 'F':
        case 'I':
          this.primBytes += 4;
          break;
        case 'D':
        case 'J':
          this.primBytes += 8;
          break;
        case 'L':
        case '[':
          this.objFields++;
          break;
      } 
    } 
  }
  
  private static void msg(String paramString) { System.out.println(paramString); }
  
  private static long _computeSerialVersionUID(Class<?> paramClass) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      DigestOutputStream digestOutputStream = new DigestOutputStream(byteArrayOutputStream, messageDigest);
      DataOutputStream dataOutputStream = new DataOutputStream(digestOutputStream);
      dataOutputStream.writeUTF(paramClass.getName());
      int i = paramClass.getModifiers();
      i &= 0x611;
      Method[] arrayOfMethod = paramClass.getDeclaredMethods();
      if ((i & 0x200) != 0) {
        i &= 0xFFFFFBFF;
        if (arrayOfMethod.length > 0)
          i |= 0x400; 
      } 
      i &= 0x611;
      dataOutputStream.writeInt(i);
      if (!paramClass.isArray()) {
        Class[] arrayOfClass = paramClass.getInterfaces();
        Arrays.sort(arrayOfClass, compareClassByName);
        for (byte b = 0; b < arrayOfClass.length; b++)
          dataOutputStream.writeUTF(arrayOfClass[b].getName()); 
      } 
      Field[] arrayOfField = paramClass.getDeclaredFields();
      Arrays.sort(arrayOfField, compareMemberByName);
      for (byte b1 = 0; b1 < arrayOfField.length; b1++) {
        Field field = arrayOfField[b1];
        int j = field.getModifiers();
        if (!Modifier.isPrivate(j) || (!Modifier.isTransient(j) && !Modifier.isStatic(j))) {
          dataOutputStream.writeUTF(field.getName());
          j &= 0xDF;
          dataOutputStream.writeInt(j);
          dataOutputStream.writeUTF(getSignature(field.getType()));
        } 
      } 
      if (hasStaticInitializer(paramClass)) {
        dataOutputStream.writeUTF("<clinit>");
        dataOutputStream.writeInt(8);
        dataOutputStream.writeUTF("()V");
      } 
      MethodSignature[] arrayOfMethodSignature1 = MethodSignature.removePrivateAndSort(paramClass.getDeclaredConstructors());
      for (byte b2 = 0; b2 < arrayOfMethodSignature1.length; b2++) {
        MethodSignature methodSignature = arrayOfMethodSignature1[b2];
        String str1 = "<init>";
        String str2 = methodSignature.signature;
        str2 = str2.replace('/', '.');
        dataOutputStream.writeUTF(str1);
        int j = methodSignature.member.getModifiers() & 0xD3F;
        dataOutputStream.writeInt(j);
        dataOutputStream.writeUTF(str2);
      } 
      MethodSignature[] arrayOfMethodSignature2 = MethodSignature.removePrivateAndSort(arrayOfMethod);
      for (byte b3 = 0; b3 < arrayOfMethodSignature2.length; b3++) {
        MethodSignature methodSignature = arrayOfMethodSignature2[b3];
        String str = methodSignature.signature;
        str = str.replace('/', '.');
        dataOutputStream.writeUTF(methodSignature.member.getName());
        int j = methodSignature.member.getModifiers() & 0xD3F;
        dataOutputStream.writeInt(j);
        dataOutputStream.writeUTF(str);
      } 
      dataOutputStream.flush();
      byte[] arrayOfByte = messageDigest.digest();
      for (byte b4 = 0; b4 < Math.min(8, arrayOfByte.length); b4++)
        l += ((arrayOfByte[b4] & 0xFF) << b4 * 8); 
    } catch (IOException iOException) {
      l = -1L;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      SecurityException securityException = new SecurityException();
      securityException.initCause(noSuchAlgorithmException);
      throw securityException;
    } 
    return l;
  }
  
  private static long computeStructuralUID(ObjectStreamClass paramObjectStreamClass, Class<?> paramClass) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
    long l = 0L;
    try {
      if (!Serializable.class.isAssignableFrom(paramClass) || paramClass.isInterface())
        return 0L; 
      if (java.io.Externalizable.class.isAssignableFrom(paramClass))
        return 1L; 
      MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      DigestOutputStream digestOutputStream = new DigestOutputStream(byteArrayOutputStream, messageDigest);
      DataOutputStream dataOutputStream = new DataOutputStream(digestOutputStream);
      Class clazz = paramClass.getSuperclass();
      if (clazz != null)
        dataOutputStream.writeLong(computeStructuralUID(lookup(clazz), clazz)); 
      if (paramObjectStreamClass.hasWriteObject()) {
        dataOutputStream.writeInt(2);
      } else {
        dataOutputStream.writeInt(1);
      } 
      ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields();
      if (arrayOfObjectStreamField.length > 1)
        Arrays.sort(arrayOfObjectStreamField, compareObjStrFieldsByName); 
      for (byte b1 = 0; b1 < arrayOfObjectStreamField.length; b1++) {
        dataOutputStream.writeUTF(arrayOfObjectStreamField[b1].getName());
        dataOutputStream.writeUTF(arrayOfObjectStreamField[b1].getSignature());
      } 
      dataOutputStream.flush();
      byte[] arrayOfByte = messageDigest.digest();
      for (byte b2 = 0; b2 < Math.min(8, arrayOfByte.length); b2++)
        l += ((arrayOfByte[b2] & 0xFF) << b2 * 8); 
    } catch (IOException iOException) {
      l = -1L;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      SecurityException securityException = new SecurityException();
      securityException.initCause(noSuchAlgorithmException);
      throw securityException;
    } 
    return l;
  }
  
  static String getSignature(Class<?> paramClass) {
    String str = null;
    if (paramClass.isArray()) {
      Class<?> clazz = paramClass;
      byte b1 = 0;
      while (clazz.isArray()) {
        b1++;
        clazz = clazz.getComponentType();
      } 
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b2 = 0; b2 < b1; b2++)
        stringBuffer.append("["); 
      stringBuffer.append(getSignature(clazz));
      str = stringBuffer.toString();
    } else if (paramClass.isPrimitive()) {
      if (paramClass == int.class) {
        str = "I";
      } else if (paramClass == byte.class) {
        str = "B";
      } else if (paramClass == long.class) {
        str = "J";
      } else if (paramClass == float.class) {
        str = "F";
      } else if (paramClass == double.class) {
        str = "D";
      } else if (paramClass == short.class) {
        str = "S";
      } else if (paramClass == char.class) {
        str = "C";
      } else if (paramClass == boolean.class) {
        str = "Z";
      } else if (paramClass == void.class) {
        str = "V";
      } 
    } else {
      str = "L" + paramClass.getName().replace('.', '/') + ";";
    } 
    return str;
  }
  
  static String getSignature(Method paramMethod) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(");
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getSignature(arrayOfClass[b])); 
    stringBuffer.append(")");
    stringBuffer.append(getSignature(paramMethod.getReturnType()));
    return stringBuffer.toString();
  }
  
  static String getSignature(Constructor paramConstructor) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(");
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    for (byte b = 0; b < arrayOfClass.length; b++)
      stringBuffer.append(getSignature(arrayOfClass[b])); 
    stringBuffer.append(")V");
    return stringBuffer.toString();
  }
  
  private static ObjectStreamClass findDescriptorFor(Class<?> paramClass) {
    int i = paramClass.hashCode();
    int j = (i & 0x7FFFFFFF) % descriptorFor.length;
    ObjectStreamClassEntry objectStreamClassEntry1;
    while ((objectStreamClassEntry1 = descriptorFor[j]) != null && objectStreamClassEntry1.get() == null)
      descriptorFor[j] = objectStreamClassEntry1.next; 
    ObjectStreamClassEntry objectStreamClassEntry2 = objectStreamClassEntry1;
    while (objectStreamClassEntry1 != null) {
      ObjectStreamClass objectStreamClass = (ObjectStreamClass)objectStreamClassEntry1.get();
      if (objectStreamClass == null) {
        objectStreamClassEntry2.next = objectStreamClassEntry1.next;
      } else {
        if (objectStreamClass.ofClass == paramClass)
          return objectStreamClass; 
        objectStreamClassEntry2 = objectStreamClassEntry1;
      } 
      objectStreamClassEntry1 = objectStreamClassEntry1.next;
    } 
    return null;
  }
  
  private static void insertDescriptorFor(ObjectStreamClass paramObjectStreamClass) {
    if (findDescriptorFor(paramObjectStreamClass.ofClass) != null)
      return; 
    int i = paramObjectStreamClass.ofClass.hashCode();
    int j = (i & 0x7FFFFFFF) % descriptorFor.length;
    ObjectStreamClassEntry objectStreamClassEntry = new ObjectStreamClassEntry(paramObjectStreamClass);
    objectStreamClassEntry.next = descriptorFor[j];
    descriptorFor[j] = objectStreamClassEntry;
  }
  
  private static Field[] getDeclaredFields(final Class<?> clz) { return (Field[])AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() throws InstantiationException, InvocationTargetException, UnsupportedOperationException { return clz.getDeclaredFields(); }
        }); }
  
  private static boolean hasStaticInitializer(Class<?> paramClass) {
    if (hasStaticInitializerMethod == null) {
      Class clazz = null;
      try {
        if (clazz == null)
          clazz = java.io.ObjectStreamClass.class; 
        hasStaticInitializerMethod = clazz.getDeclaredMethod("hasStaticInitializer", new Class[] { Class.class });
      } catch (NoSuchMethodException noSuchMethodException) {}
      if (hasStaticInitializerMethod == null)
        throw new InternalError("Can't find hasStaticInitializer method on " + clazz.getName()); 
      hasStaticInitializerMethod.setAccessible(true);
    } 
    try {
      Boolean bool = (Boolean)hasStaticInitializerMethod.invoke(null, new Object[] { paramClass });
      return bool.booleanValue();
    } catch (Exception exception) {
      InternalError internalError = new InternalError("Error invoking hasStaticInitializer");
      internalError.initCause(exception);
      throw internalError;
    } 
  }
  
  private static Method getInheritableMethod(Class<?> paramClass1, String paramString, Class<?>[] paramArrayOfClass, Class<?> paramClass2) {
    Method method = null;
    Class<?> clazz = paramClass1;
    while (clazz != null) {
      try {
        method = clazz.getDeclaredMethod(paramString, paramArrayOfClass);
        break;
      } catch (NoSuchMethodException noSuchMethodException) {
        clazz = clazz.getSuperclass();
      } 
    } 
    if (method == null || method.getReturnType() != paramClass2)
      return null; 
    method.setAccessible(true);
    int i = method.getModifiers();
    return ((i & 0x408) != 0) ? null : (((i & 0x5) != 0) ? method : (((i & 0x2) != 0) ? ((paramClass1 == clazz) ? method : null) : (packageEquals(paramClass1, clazz) ? method : null)));
  }
  
  private static boolean packageEquals(Class<?> paramClass1, Class<?> paramClass2) {
    Package package1 = paramClass1.getPackage();
    Package package2 = paramClass2.getPackage();
    return (package1 == package2 || (package1 != null && package1.equals(package2)));
  }
  
  private static class CompareClassByName implements Comparator {
    private CompareClassByName() {}
    
    public int compare(Object param1Object1, Object param1Object2) {
      Class clazz1 = (Class)param1Object1;
      Class clazz2 = (Class)param1Object2;
      return clazz1.getName().compareTo(clazz2.getName());
    }
  }
  
  private static class CompareMemberByName implements Comparator {
    private CompareMemberByName() {}
    
    public int compare(Object param1Object1, Object param1Object2) {
      String str1 = ((Member)param1Object1).getName();
      String str2 = ((Member)param1Object2).getName();
      if (param1Object1 instanceof Method) {
        str1 = str1 + ObjectStreamClass.getSignature((Method)param1Object1);
        str2 = str2 + ObjectStreamClass.getSignature((Method)param1Object2);
      } else if (param1Object1 instanceof Constructor) {
        str1 = str1 + ObjectStreamClass.getSignature((Constructor)param1Object1);
        str2 = str2 + ObjectStreamClass.getSignature((Constructor)param1Object2);
      } 
      return str1.compareTo(str2);
    }
  }
  
  private static class CompareObjStrFieldsByName implements Comparator {
    private CompareObjStrFieldsByName() {}
    
    public int compare(Object param1Object1, Object param1Object2) {
      ObjectStreamField objectStreamField1 = (ObjectStreamField)param1Object1;
      ObjectStreamField objectStreamField2 = (ObjectStreamField)param1Object2;
      return objectStreamField1.getName().compareTo(objectStreamField2.getName());
    }
  }
  
  private static class MethodSignature implements Comparator {
    Member member;
    
    String signature;
    
    static MethodSignature[] removePrivateAndSort(Member[] param1ArrayOfMember) {
      byte b1 = 0;
      for (byte b2 = 0; b2 < param1ArrayOfMember.length; b2++) {
        if (!Modifier.isPrivate(param1ArrayOfMember[b2].getModifiers()))
          b1++; 
      } 
      MethodSignature[] arrayOfMethodSignature = new MethodSignature[b1];
      byte b3 = 0;
      for (byte b4 = 0; b4 < param1ArrayOfMember.length; b4++) {
        if (!Modifier.isPrivate(param1ArrayOfMember[b4].getModifiers())) {
          arrayOfMethodSignature[b3] = new MethodSignature(param1ArrayOfMember[b4]);
          b3++;
        } 
      } 
      if (b3 > 0)
        Arrays.sort(arrayOfMethodSignature, arrayOfMethodSignature[0]); 
      return arrayOfMethodSignature;
    }
    
    public int compare(Object param1Object1, Object param1Object2) {
      int i;
      if (param1Object1 == param1Object2)
        return 0; 
      MethodSignature methodSignature1 = (MethodSignature)param1Object1;
      MethodSignature methodSignature2 = (MethodSignature)param1Object2;
      if (isConstructor()) {
        i = methodSignature1.signature.compareTo(methodSignature2.signature);
      } else {
        i = methodSignature1.member.getName().compareTo(methodSignature2.member.getName());
        if (i == 0)
          i = methodSignature1.signature.compareTo(methodSignature2.signature); 
      } 
      return i;
    }
    
    private final boolean isConstructor() { return this.member instanceof Constructor; }
    
    private MethodSignature(Member param1Member) {
      this.member = param1Member;
      if (isConstructor()) {
        this.signature = ObjectStreamClass.getSignature((Constructor)param1Member);
      } else {
        this.signature = ObjectStreamClass.getSignature((Method)param1Member);
      } 
    }
  }
  
  private static class ObjectStreamClassEntry {
    ObjectStreamClassEntry next;
    
    private ObjectStreamClass c;
    
    ObjectStreamClassEntry(ObjectStreamClass param1ObjectStreamClass) { this.c = param1ObjectStreamClass; }
    
    public Object get() throws InstantiationException, InvocationTargetException, UnsupportedOperationException { return this.c; }
  }
  
  private static final class PersistentFieldsValue extends ClassValue<ObjectStreamField[]> {
    protected ObjectStreamField[] computeValue(Class<?> param1Class) {
      try {
        Field field = param1Class.getDeclaredField("serialPersistentFields");
        int i = field.getModifiers();
        if (Modifier.isPrivate(i) && Modifier.isStatic(i) && Modifier.isFinal(i)) {
          field.setAccessible(true);
          ObjectStreamField[] arrayOfObjectStreamField = (ObjectStreamField[])field.get(param1Class);
          return translateFields(arrayOfObjectStreamField);
        } 
      } catch (NoSuchFieldException|IllegalAccessException|IllegalArgumentException|ClassCastException noSuchFieldException) {}
      return null;
    }
    
    private static ObjectStreamField[] translateFields(ObjectStreamField[] param1ArrayOfObjectStreamField) {
      ObjectStreamField[] arrayOfObjectStreamField = new ObjectStreamField[param1ArrayOfObjectStreamField.length];
      for (byte b = 0; b < param1ArrayOfObjectStreamField.length; b++)
        arrayOfObjectStreamField[b] = new ObjectStreamField(param1ArrayOfObjectStreamField[b].getName(), param1ArrayOfObjectStreamField[b].getType()); 
      return arrayOfObjectStreamField;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\ObjectStreamClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */