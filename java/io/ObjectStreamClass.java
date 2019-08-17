package java.io;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.misc.ReflectUtil;

public class ObjectStreamClass implements Serializable {
  public static final ObjectStreamField[] NO_FIELDS = new ObjectStreamField[0];
  
  private static final long serialVersionUID = -6120832682080437368L;
  
  private static final ObjectStreamField[] serialPersistentFields = NO_FIELDS;
  
  private static boolean disableSerialConstructorChecks = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() {
          String str = "jdk.disableSerialConstructorChecks";
          return "true".equals(System.getProperty(str)) ? Boolean.TRUE : Boolean.FALSE;
        }
      })).booleanValue();
  
  private static final ReflectionFactory reflFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
  
  private Class<?> cl;
  
  private String name;
  
  private boolean isProxy;
  
  private boolean isEnum;
  
  private boolean serializable;
  
  private boolean externalizable;
  
  private boolean hasWriteObjectData;
  
  private boolean hasBlockExternalData = true;
  
  private ClassNotFoundException resolveEx;
  
  private ExceptionInfo deserializeEx;
  
  private ExceptionInfo serializeEx;
  
  private ExceptionInfo defaultSerializeEx;
  
  private ObjectStreamField[] fields;
  
  private int primDataSize;
  
  private int numObjFields;
  
  private FieldReflector fieldRefl;
  
  private Constructor<?> cons;
  
  private ProtectionDomain[] domains;
  
  private Method writeObjectMethod;
  
  private Method readObjectMethod;
  
  private Method readObjectNoDataMethod;
  
  private Method writeReplaceMethod;
  
  private Method readResolveMethod;
  
  private ObjectStreamClass localDesc;
  
  private ObjectStreamClass superDesc;
  
  private boolean initialized;
  
  private static native void initNative();
  
  public static ObjectStreamClass lookup(Class<?> paramClass) { return lookup(paramClass, false); }
  
  public static ObjectStreamClass lookupAny(Class<?> paramClass) { return lookup(paramClass, true); }
  
  public String getName() { return this.name; }
  
  public long getSerialVersionUID() {
    if (this.suid == null)
      this.suid = (Long)AccessController.doPrivileged(new PrivilegedAction<Long>() {
            public Long run() { return Long.valueOf(ObjectStreamClass.computeDefaultSUID(ObjectStreamClass.this.cl)); }
          }); 
    return this.suid.longValue();
  }
  
  @CallerSensitive
  public Class<?> forClass() {
    if (this.cl == null)
      return null; 
    requireInitialized();
    if (System.getSecurityManager() != null) {
      Class clazz = Reflection.getCallerClass();
      if (ReflectUtil.needsPackageAccessCheck(clazz.getClassLoader(), this.cl.getClassLoader()))
        ReflectUtil.checkPackageAccess(this.cl); 
    } 
    return this.cl;
  }
  
  public ObjectStreamField[] getFields() { return getFields(true); }
  
  public ObjectStreamField getField(String paramString) { return getField(paramString, null); }
  
  public String toString() { return this.name + ": static final long serialVersionUID = " + getSerialVersionUID() + "L;"; }
  
  static ObjectStreamClass lookup(Class<?> paramClass, boolean paramBoolean) {
    if (!paramBoolean && !Serializable.class.isAssignableFrom(paramClass))
      return null; 
    processQueue(localDescsQueue, Caches.localDescs);
    WeakClassKey weakClassKey = new WeakClassKey(paramClass, localDescsQueue);
    Reference reference = (Reference)Caches.localDescs.get(weakClassKey);
    Object object = null;
    if (reference != null)
      object = reference.get(); 
    EntryFuture entryFuture = null;
    if (object == null) {
      EntryFuture entryFuture1 = new EntryFuture(null);
      SoftReference softReference = new SoftReference(entryFuture1);
      do {
        if (reference != null)
          Caches.localDescs.remove(weakClassKey, reference); 
        reference = (Reference)Caches.localDescs.putIfAbsent(weakClassKey, softReference);
        if (reference == null)
          continue; 
        object = reference.get();
      } while (reference != null && object == null);
      if (object == null)
        entryFuture = entryFuture1; 
    } 
    if (object instanceof ObjectStreamClass)
      return (ObjectStreamClass)object; 
    if (object instanceof EntryFuture) {
      entryFuture = (EntryFuture)object;
      if (entryFuture.getOwner() == Thread.currentThread()) {
        object = null;
      } else {
        object = entryFuture.get();
      } 
    } 
    if (object == null) {
      try {
        object = new ObjectStreamClass(paramClass);
      } catch (Throwable throwable) {
        object = throwable;
      } 
      if (entryFuture.set(object)) {
        Caches.localDescs.put(weakClassKey, new SoftReference(object));
      } else {
        object = entryFuture.get();
      } 
    } 
    if (object instanceof ObjectStreamClass)
      return (ObjectStreamClass)object; 
    if (object instanceof RuntimeException)
      throw (RuntimeException)object; 
    if (object instanceof Error)
      throw (Error)object; 
    throw new InternalError("unexpected entry: " + object);
  }
  
  private ObjectStreamClass(final Class<?> cl) {
    this.cl = paramClass;
    this.name = paramClass.getName();
    this.isProxy = Proxy.isProxyClass(paramClass);
    this.isEnum = Enum.class.isAssignableFrom(paramClass);
    this.serializable = Serializable.class.isAssignableFrom(paramClass);
    this.externalizable = Externalizable.class.isAssignableFrom(paramClass);
    Class clazz = paramClass.getSuperclass();
    this.superDesc = (clazz != null) ? lookup(clazz, false) : null;
    this.localDesc = this;
    if (this.serializable) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              if (ObjectStreamClass.this.isEnum) {
                ObjectStreamClass.this.suid = Long.valueOf(0L);
                ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
                return null;
              } 
              if (cl.isArray()) {
                ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
                return null;
              } 
              ObjectStreamClass.this.suid = ObjectStreamClass.getDeclaredSUID(cl);
              try {
                ObjectStreamClass.this.fields = ObjectStreamClass.getSerialFields(cl);
                ObjectStreamClass.this.computeFieldOffsets();
              } catch (InvalidClassException invalidClassException) {
                ObjectStreamClass.this.serializeEx = ObjectStreamClass.this.deserializeEx = new ObjectStreamClass.ExceptionInfo(invalidClassException.classname, invalidClassException.getMessage());
                ObjectStreamClass.this.fields = ObjectStreamClass.NO_FIELDS;
              } 
              if (ObjectStreamClass.this.externalizable) {
                ObjectStreamClass.this.cons = ObjectStreamClass.getExternalizableConstructor(cl);
              } else {
                ObjectStreamClass.this.cons = ObjectStreamClass.getSerializableConstructor(cl);
                ObjectStreamClass.this.writeObjectMethod = ObjectStreamClass.getPrivateMethod(cl, "writeObject", new Class[] { ObjectOutputStream.class }, void.class);
                ObjectStreamClass.this.readObjectMethod = ObjectStreamClass.getPrivateMethod(cl, "readObject", new Class[] { ObjectInputStream.class }, void.class);
                ObjectStreamClass.this.readObjectNoDataMethod = ObjectStreamClass.getPrivateMethod(cl, "readObjectNoData", null, void.class);
                ObjectStreamClass.this.hasWriteObjectData = (ObjectStreamClass.this.writeObjectMethod != null);
              } 
              ObjectStreamClass.this.domains = ObjectStreamClass.this.getProtectionDomains(ObjectStreamClass.this.cons, cl);
              ObjectStreamClass.this.writeReplaceMethod = ObjectStreamClass.getInheritableMethod(cl, "writeReplace", null, Object.class);
              ObjectStreamClass.this.readResolveMethod = ObjectStreamClass.getInheritableMethod(cl, "readResolve", null, Object.class);
              return null;
            }
          });
    } else {
      this.suid = Long.valueOf(0L);
      this.fields = NO_FIELDS;
    } 
    try {
      this.fieldRefl = getReflector(this.fields, this);
    } catch (InvalidClassException invalidClassException) {
      throw new InternalError(invalidClassException);
    } 
    if (this.deserializeEx == null)
      if (this.isEnum) {
        this.deserializeEx = new ExceptionInfo(this.name, "enum type");
      } else if (this.cons == null) {
        this.deserializeEx = new ExceptionInfo(this.name, "no valid constructor");
      }  
    for (byte b = 0; b < this.fields.length; b++) {
      if (this.fields[b].getField() == null)
        this.defaultSerializeEx = new ExceptionInfo(this.name, "unmatched serializable field(s) declared"); 
    } 
    this.initialized = true;
  }
  
  ObjectStreamClass() {}
  
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
  
  void initProxy(Class<?> paramClass, ClassNotFoundException paramClassNotFoundException, ObjectStreamClass paramObjectStreamClass) throws InvalidClassException {
    ObjectStreamClass objectStreamClass = null;
    if (paramClass != null) {
      objectStreamClass = lookup(paramClass, true);
      if (!objectStreamClass.isProxy)
        throw new InvalidClassException("cannot bind proxy descriptor to a non-proxy class"); 
    } 
    this.cl = paramClass;
    this.resolveEx = paramClassNotFoundException;
    this.superDesc = paramObjectStreamClass;
    this.isProxy = true;
    this.serializable = true;
    this.suid = Long.valueOf(0L);
    this.fields = NO_FIELDS;
    if (objectStreamClass != null) {
      this.localDesc = objectStreamClass;
      this.name = this.localDesc.name;
      this.externalizable = this.localDesc.externalizable;
      this.writeReplaceMethod = this.localDesc.writeReplaceMethod;
      this.readResolveMethod = this.localDesc.readResolveMethod;
      this.deserializeEx = this.localDesc.deserializeEx;
      this.domains = this.localDesc.domains;
      this.cons = this.localDesc.cons;
    } 
    this.fieldRefl = getReflector(this.fields, this.localDesc);
    this.initialized = true;
  }
  
  void initNonProxy(ObjectStreamClass paramObjectStreamClass1, Class<?> paramClass, ClassNotFoundException paramClassNotFoundException, ObjectStreamClass paramObjectStreamClass2) throws InvalidClassException {
    long l = Long.valueOf(paramObjectStreamClass1.getSerialVersionUID()).longValue();
    ObjectStreamClass objectStreamClass = null;
    if (paramClass != null) {
      objectStreamClass = lookup(paramClass, true);
      if (objectStreamClass.isProxy)
        throw new InvalidClassException("cannot bind non-proxy descriptor to a proxy class"); 
      if (paramObjectStreamClass1.isEnum != objectStreamClass.isEnum)
        throw new InvalidClassException(paramObjectStreamClass1.isEnum ? "cannot bind enum descriptor to a non-enum class" : "cannot bind non-enum descriptor to an enum class"); 
      if (paramObjectStreamClass1.serializable == objectStreamClass.serializable && !paramClass.isArray() && l != objectStreamClass.getSerialVersionUID())
        throw new InvalidClassException(objectStreamClass.name, "local class incompatible: stream classdesc serialVersionUID = " + l + ", local class serialVersionUID = " + objectStreamClass.getSerialVersionUID()); 
      if (!classNamesEqual(paramObjectStreamClass1.name, objectStreamClass.name))
        throw new InvalidClassException(objectStreamClass.name, "local class name incompatible with stream class name \"" + paramObjectStreamClass1.name + "\""); 
      if (!paramObjectStreamClass1.isEnum) {
        if (paramObjectStreamClass1.serializable == objectStreamClass.serializable && paramObjectStreamClass1.externalizable != objectStreamClass.externalizable)
          throw new InvalidClassException(objectStreamClass.name, "Serializable incompatible with Externalizable"); 
        if (paramObjectStreamClass1.serializable != objectStreamClass.serializable || paramObjectStreamClass1.externalizable != objectStreamClass.externalizable || (!paramObjectStreamClass1.serializable && !paramObjectStreamClass1.externalizable))
          this.deserializeEx = new ExceptionInfo(objectStreamClass.name, "class invalid for deserialization"); 
      } 
    } 
    this.cl = paramClass;
    this.resolveEx = paramClassNotFoundException;
    this.superDesc = paramObjectStreamClass2;
    this.name = paramObjectStreamClass1.name;
    this.suid = Long.valueOf(l);
    this.isProxy = false;
    this.isEnum = paramObjectStreamClass1.isEnum;
    this.serializable = paramObjectStreamClass1.serializable;
    this.externalizable = paramObjectStreamClass1.externalizable;
    this.hasBlockExternalData = paramObjectStreamClass1.hasBlockExternalData;
    this.hasWriteObjectData = paramObjectStreamClass1.hasWriteObjectData;
    this.fields = paramObjectStreamClass1.fields;
    this.primDataSize = paramObjectStreamClass1.primDataSize;
    this.numObjFields = paramObjectStreamClass1.numObjFields;
    if (objectStreamClass != null) {
      this.localDesc = objectStreamClass;
      this.writeObjectMethod = this.localDesc.writeObjectMethod;
      this.readObjectMethod = this.localDesc.readObjectMethod;
      this.readObjectNoDataMethod = this.localDesc.readObjectNoDataMethod;
      this.writeReplaceMethod = this.localDesc.writeReplaceMethod;
      this.readResolveMethod = this.localDesc.readResolveMethod;
      if (this.deserializeEx == null)
        this.deserializeEx = this.localDesc.deserializeEx; 
      this.domains = this.localDesc.domains;
      this.cons = this.localDesc.cons;
    } 
    this.fieldRefl = getReflector(this.fields, this.localDesc);
    this.fields = this.fieldRefl.getFields();
    this.initialized = true;
  }
  
  void readNonProxy(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.name = paramObjectInputStream.readUTF();
    this.suid = Long.valueOf(paramObjectInputStream.readLong());
    this.isProxy = false;
    byte b = paramObjectInputStream.readByte();
    this.hasWriteObjectData = ((b & true) != 0);
    this.hasBlockExternalData = ((b & 0x8) != 0);
    this.externalizable = ((b & 0x4) != 0);
    boolean bool = ((b & 0x2) != 0) ? 1 : 0;
    if (this.externalizable && bool)
      throw new InvalidClassException(this.name, "serializable and externalizable flags conflict"); 
    this.serializable = (this.externalizable || bool);
    this.isEnum = ((b & 0x10) != 0);
    if (this.isEnum && this.suid.longValue() != 0L)
      throw new InvalidClassException(this.name, "enum descriptor has non-zero serialVersionUID: " + this.suid); 
    short s = paramObjectInputStream.readShort();
    if (this.isEnum && s != 0)
      throw new InvalidClassException(this.name, "enum descriptor has non-zero field count: " + s); 
    this.fields = (s > 0) ? new ObjectStreamField[s] : NO_FIELDS;
    for (byte b1 = 0; b1 < s; b1++) {
      char c = (char)paramObjectInputStream.readByte();
      String str1 = paramObjectInputStream.readUTF();
      String str2 = (c == 'L' || c == '[') ? paramObjectInputStream.readTypeString() : new String(new char[] { c });
      try {
        this.fields[b1] = new ObjectStreamField(str1, str2, false);
      } catch (RuntimeException runtimeException) {
        throw (IOException)(new InvalidClassException(this.name, "invalid descriptor for field " + str1)).initCause(runtimeException);
      } 
    } 
    computeFieldOffsets();
  }
  
  void writeNonProxy(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeUTF(this.name);
    paramObjectOutputStream.writeLong(getSerialVersionUID());
    byte b = 0;
    if (this.externalizable) {
      b = (byte)(b | 0x4);
      int i = paramObjectOutputStream.getProtocolVersion();
      if (i != 1)
        b = (byte)(b | 0x8); 
    } else if (this.serializable) {
      b = (byte)(b | 0x2);
    } 
    if (this.hasWriteObjectData)
      b = (byte)(b | true); 
    if (this.isEnum)
      b = (byte)(b | 0x10); 
    paramObjectOutputStream.writeByte(b);
    paramObjectOutputStream.writeShort(this.fields.length);
    for (byte b1 = 0; b1 < this.fields.length; b1++) {
      ObjectStreamField objectStreamField = this.fields[b1];
      paramObjectOutputStream.writeByte(objectStreamField.getTypeCode());
      paramObjectOutputStream.writeUTF(objectStreamField.getName());
      if (!objectStreamField.isPrimitive())
        paramObjectOutputStream.writeTypeString(objectStreamField.getTypeString()); 
    } 
  }
  
  ClassNotFoundException getResolveException() { return this.resolveEx; }
  
  private final void requireInitialized() {
    if (!this.initialized)
      throw new InternalError("Unexpected call when not initialized"); 
  }
  
  void checkDeserialize() {
    requireInitialized();
    if (this.deserializeEx != null)
      throw this.deserializeEx.newInvalidClassException(); 
  }
  
  void checkSerialize() {
    requireInitialized();
    if (this.serializeEx != null)
      throw this.serializeEx.newInvalidClassException(); 
  }
  
  void checkDefaultSerialize() {
    requireInitialized();
    if (this.defaultSerializeEx != null)
      throw this.defaultSerializeEx.newInvalidClassException(); 
  }
  
  ObjectStreamClass getSuperDesc() {
    requireInitialized();
    return this.superDesc;
  }
  
  ObjectStreamClass getLocalDesc() {
    requireInitialized();
    return this.localDesc;
  }
  
  ObjectStreamField[] getFields(boolean paramBoolean) { return paramBoolean ? (ObjectStreamField[])this.fields.clone() : this.fields; }
  
  ObjectStreamField getField(String paramString, Class<?> paramClass) {
    for (byte b = 0; b < this.fields.length; b++) {
      ObjectStreamField objectStreamField = this.fields[b];
      if (objectStreamField.getName().equals(paramString)) {
        if (paramClass == null || (paramClass == Object.class && !objectStreamField.isPrimitive()))
          return objectStreamField; 
        Class clazz = objectStreamField.getType();
        if (clazz != null && paramClass.isAssignableFrom(clazz))
          return objectStreamField; 
      } 
    } 
    return null;
  }
  
  boolean isProxy() {
    requireInitialized();
    return this.isProxy;
  }
  
  boolean isEnum() {
    requireInitialized();
    return this.isEnum;
  }
  
  boolean isExternalizable() {
    requireInitialized();
    return this.externalizable;
  }
  
  boolean isSerializable() {
    requireInitialized();
    return this.serializable;
  }
  
  boolean hasBlockExternalData() {
    requireInitialized();
    return this.hasBlockExternalData;
  }
  
  boolean hasWriteObjectData() {
    requireInitialized();
    return this.hasWriteObjectData;
  }
  
  boolean isInstantiable() {
    requireInitialized();
    return (this.cons != null);
  }
  
  boolean hasWriteObjectMethod() {
    requireInitialized();
    return (this.writeObjectMethod != null);
  }
  
  boolean hasReadObjectMethod() {
    requireInitialized();
    return (this.readObjectMethod != null);
  }
  
  boolean hasReadObjectNoDataMethod() {
    requireInitialized();
    return (this.readObjectNoDataMethod != null);
  }
  
  boolean hasWriteReplaceMethod() {
    requireInitialized();
    return (this.writeReplaceMethod != null);
  }
  
  boolean hasReadResolveMethod() {
    requireInitialized();
    return (this.readResolveMethod != null);
  }
  
  Object newInstance() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
    requireInitialized();
    if (this.cons != null)
      try {
        if (this.domains == null || this.domains.length == 0)
          return this.cons.newInstance(new Object[0]); 
        JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
        PrivilegedAction privilegedAction = () -> {
            try {
              return this.cons.newInstance(new Object[0]);
            } catch (InstantiationException|InvocationTargetException|IllegalAccessException instantiationException) {
              throw new UndeclaredThrowableException(instantiationException);
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
        throw new InternalError(illegalAccessException);
      }  
    throw new UnsupportedOperationException();
  }
  
  void invokeWriteObject(Object paramObject, ObjectOutputStream paramObjectOutputStream) throws IOException, UnsupportedOperationException {
    requireInitialized();
    if (this.writeObjectMethod != null) {
      try {
        this.writeObjectMethod.invoke(paramObject, new Object[] { paramObjectOutputStream });
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof IOException)
          throw (IOException)throwable; 
        throwMiscException(throwable);
      } catch (IllegalAccessException illegalAccessException) {
        throw new InternalError(illegalAccessException);
      } 
    } else {
      throw new UnsupportedOperationException();
    } 
  }
  
  void invokeReadObject(Object paramObject, ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, UnsupportedOperationException {
    requireInitialized();
    if (this.readObjectMethod != null) {
      try {
        this.readObjectMethod.invoke(paramObject, new Object[] { paramObjectInputStream });
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof ClassNotFoundException)
          throw (ClassNotFoundException)throwable; 
        if (throwable instanceof IOException)
          throw (IOException)throwable; 
        throwMiscException(throwable);
      } catch (IllegalAccessException illegalAccessException) {
        throw new InternalError(illegalAccessException);
      } 
    } else {
      throw new UnsupportedOperationException();
    } 
  }
  
  void invokeReadObjectNoData(Object paramObject) throws IOException, UnsupportedOperationException {
    requireInitialized();
    if (this.readObjectNoDataMethod != null) {
      try {
        this.readObjectNoDataMethod.invoke(paramObject, (Object[])null);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof ObjectStreamException)
          throw (ObjectStreamException)throwable; 
        throwMiscException(throwable);
      } catch (IllegalAccessException illegalAccessException) {
        throw new InternalError(illegalAccessException);
      } 
    } else {
      throw new UnsupportedOperationException();
    } 
  }
  
  Object invokeWriteReplace(Object paramObject) throws IOException, UnsupportedOperationException {
    requireInitialized();
    if (this.writeReplaceMethod != null)
      try {
        return this.writeReplaceMethod.invoke(paramObject, (Object[])null);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof ObjectStreamException)
          throw (ObjectStreamException)throwable; 
        throwMiscException(throwable);
        throw new InternalError(throwable);
      } catch (IllegalAccessException illegalAccessException) {
        throw new InternalError(illegalAccessException);
      }  
    throw new UnsupportedOperationException();
  }
  
  Object invokeReadResolve(Object paramObject) throws IOException, UnsupportedOperationException {
    requireInitialized();
    if (this.readResolveMethod != null)
      try {
        return this.readResolveMethod.invoke(paramObject, (Object[])null);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof ObjectStreamException)
          throw (ObjectStreamException)throwable; 
        throwMiscException(throwable);
        throw new InternalError(throwable);
      } catch (IllegalAccessException illegalAccessException) {
        throw new InternalError(illegalAccessException);
      }  
    throw new UnsupportedOperationException();
  }
  
  ClassDataSlot[] getClassDataLayout() throws InvalidClassException {
    if (this.dataLayout == null)
      this.dataLayout = getClassDataLayout0(); 
    return this.dataLayout;
  }
  
  private ClassDataSlot[] getClassDataLayout0() throws InvalidClassException {
    ArrayList arrayList = new ArrayList();
    Class clazz1 = this.cl;
    Class clazz2;
    for (clazz2 = this.cl; clazz2 != null && Serializable.class.isAssignableFrom(clazz2); clazz2 = clazz2.getSuperclass());
    HashSet hashSet = new HashSet(3);
    for (ObjectStreamClass objectStreamClass = this; objectStreamClass != null; objectStreamClass = objectStreamClass.superDesc) {
      if (hashSet.contains(objectStreamClass.name))
        throw new InvalidClassException("Circular reference."); 
      hashSet.add(objectStreamClass.name);
      String str = (objectStreamClass.cl != null) ? objectStreamClass.cl.getName() : objectStreamClass.name;
      Class clazz4 = null;
      Class clazz5;
      for (clazz5 = clazz1; clazz5 != clazz2; clazz5 = clazz5.getSuperclass()) {
        if (str.equals(clazz5.getName())) {
          clazz4 = clazz5;
          break;
        } 
      } 
      if (clazz4 != null) {
        for (clazz5 = clazz1; clazz5 != clazz4; clazz5 = clazz5.getSuperclass())
          arrayList.add(new ClassDataSlot(lookup(clazz5, true), false)); 
        clazz1 = clazz4.getSuperclass();
      } 
      arrayList.add(new ClassDataSlot(objectStreamClass.getVariantFor(clazz4), true));
    } 
    for (Class clazz3 = clazz1; clazz3 != clazz2; clazz3 = clazz3.getSuperclass())
      arrayList.add(new ClassDataSlot(lookup(clazz3, true), false)); 
    Collections.reverse(arrayList);
    return (ClassDataSlot[])arrayList.toArray(new ClassDataSlot[arrayList.size()]);
  }
  
  int getPrimDataSize() { return this.primDataSize; }
  
  int getNumObjFields() { return this.numObjFields; }
  
  void getPrimFieldValues(Object paramObject, byte[] paramArrayOfByte) { this.fieldRefl.getPrimFieldValues(paramObject, paramArrayOfByte); }
  
  void setPrimFieldValues(Object paramObject, byte[] paramArrayOfByte) { this.fieldRefl.setPrimFieldValues(paramObject, paramArrayOfByte); }
  
  void getObjFieldValues(Object paramObject, Object[] paramArrayOfObject) { this.fieldRefl.getObjFieldValues(paramObject, paramArrayOfObject); }
  
  void setObjFieldValues(Object paramObject, Object[] paramArrayOfObject) { this.fieldRefl.setObjFieldValues(paramObject, paramArrayOfObject); }
  
  private void computeFieldOffsets() {
    this.primDataSize = 0;
    this.numObjFields = 0;
    int i = -1;
    for (byte b = 0; b < this.fields.length; b++) {
      ObjectStreamField objectStreamField = this.fields[b];
      switch (objectStreamField.getTypeCode()) {
        case 'B':
        case 'Z':
          objectStreamField.setOffset(this.primDataSize++);
          break;
        case 'C':
        case 'S':
          objectStreamField.setOffset(this.primDataSize);
          this.primDataSize += 2;
          break;
        case 'F':
        case 'I':
          objectStreamField.setOffset(this.primDataSize);
          this.primDataSize += 4;
          break;
        case 'D':
        case 'J':
          objectStreamField.setOffset(this.primDataSize);
          this.primDataSize += 8;
          break;
        case 'L':
        case '[':
          objectStreamField.setOffset(this.numObjFields++);
          if (i == -1)
            i = b; 
          break;
        default:
          throw new InternalError();
      } 
    } 
    if (i != -1 && i + this.numObjFields != this.fields.length)
      throw new InvalidClassException(this.name, "illegal field order"); 
  }
  
  private ObjectStreamClass getVariantFor(Class<?> paramClass) {
    if (this.cl == paramClass)
      return this; 
    ObjectStreamClass objectStreamClass = new ObjectStreamClass();
    if (this.isProxy) {
      objectStreamClass.initProxy(paramClass, null, this.superDesc);
    } else {
      objectStreamClass.initNonProxy(this, paramClass, null, this.superDesc);
    } 
    return objectStreamClass;
  }
  
  private static Constructor<?> getExternalizableConstructor(Class<?> paramClass) {
    try {
      Constructor constructor = paramClass.getDeclaredConstructor((Class[])null);
      constructor.setAccessible(true);
      return ((constructor.getModifiers() & true) != 0) ? constructor : null;
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } 
  }
  
  private static boolean superHasAccessibleConstructor(Class<?> paramClass) {
    Class clazz = paramClass.getSuperclass();
    assert Serializable.class.isAssignableFrom(paramClass);
    assert clazz != null;
    if (packageEquals(paramClass, clazz)) {
      for (Constructor constructor : clazz.getDeclaredConstructors()) {
        if ((constructor.getModifiers() & 0x2) == 0)
          return true; 
      } 
      return false;
    } 
    if ((clazz.getModifiers() & 0x5) == 0)
      return false; 
    for (Constructor constructor : clazz.getDeclaredConstructors()) {
      if ((constructor.getModifiers() & 0x5) != 0)
        return true; 
    } 
    return false;
  }
  
  private static Constructor<?> getSerializableConstructor(Class<?> paramClass) {
    Class<?> clazz = paramClass;
    while (Serializable.class.isAssignableFrom(clazz)) {
      Class<?> clazz1 = clazz;
      if ((clazz = clazz.getSuperclass()) == null || (!disableSerialConstructorChecks && !superHasAccessibleConstructor(clazz1)))
        return null; 
    } 
    try {
      Constructor constructor = clazz.getDeclaredConstructor((Class[])null);
      int i = constructor.getModifiers();
      if ((i & 0x2) != 0 || ((i & 0x5) == 0 && !packageEquals(paramClass, clazz)))
        return null; 
      constructor = reflFactory.newConstructorForSerialization(paramClass, constructor);
      constructor.setAccessible(true);
      return constructor;
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
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
  
  private static boolean packageEquals(Class<?> paramClass1, Class<?> paramClass2) { return (paramClass1.getClassLoader() == paramClass2.getClassLoader() && getPackageName(paramClass1).equals(getPackageName(paramClass2))); }
  
  private static String getPackageName(Class<?> paramClass) {
    String str = paramClass.getName();
    int i = str.lastIndexOf('[');
    if (i >= 0)
      str = str.substring(i + 2); 
    i = str.lastIndexOf('.');
    return (i >= 0) ? str.substring(0, i) : "";
  }
  
  private static boolean classNamesEqual(String paramString1, String paramString2) {
    paramString1 = paramString1.substring(paramString1.lastIndexOf('.') + 1);
    paramString2 = paramString2.substring(paramString2.lastIndexOf('.') + 1);
    return paramString1.equals(paramString2);
  }
  
  private static String getClassSignature(Class<?> paramClass) {
    StringBuilder stringBuilder = new StringBuilder();
    while (paramClass.isArray()) {
      stringBuilder.append('[');
      paramClass = paramClass.getComponentType();
    } 
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class) {
        stringBuilder.append('I');
      } else if (paramClass == byte.class) {
        stringBuilder.append('B');
      } else if (paramClass == long.class) {
        stringBuilder.append('J');
      } else if (paramClass == float.class) {
        stringBuilder.append('F');
      } else if (paramClass == double.class) {
        stringBuilder.append('D');
      } else if (paramClass == short.class) {
        stringBuilder.append('S');
      } else if (paramClass == char.class) {
        stringBuilder.append('C');
      } else if (paramClass == boolean.class) {
        stringBuilder.append('Z');
      } else if (paramClass == void.class) {
        stringBuilder.append('V');
      } else {
        throw new InternalError();
      } 
    } else {
      stringBuilder.append('L' + paramClass.getName().replace('.', '/') + ';');
    } 
    return stringBuilder.toString();
  }
  
  private static String getMethodSignature(Class<?>[] paramArrayOfClass, Class<?> paramClass) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('(');
    for (byte b = 0; b < paramArrayOfClass.length; b++)
      stringBuilder.append(getClassSignature(paramArrayOfClass[b])); 
    stringBuilder.append(')');
    stringBuilder.append(getClassSignature(paramClass));
    return stringBuilder.toString();
  }
  
  private static void throwMiscException(Throwable paramThrowable) throws IOException {
    if (paramThrowable instanceof RuntimeException)
      throw (RuntimeException)paramThrowable; 
    if (paramThrowable instanceof Error)
      throw (Error)paramThrowable; 
    IOException iOException = new IOException("unexpected exception type");
    iOException.initCause(paramThrowable);
    throw iOException;
  }
  
  private static ObjectStreamField[] getSerialFields(Class<?> paramClass) throws InvalidClassException {
    ObjectStreamField[] arrayOfObjectStreamField;
    if (Serializable.class.isAssignableFrom(paramClass) && !Externalizable.class.isAssignableFrom(paramClass) && !Proxy.isProxyClass(paramClass) && !paramClass.isInterface()) {
      if ((arrayOfObjectStreamField = getDeclaredSerialFields(paramClass)) == null)
        arrayOfObjectStreamField = getDefaultSerialFields(paramClass); 
      Arrays.sort(arrayOfObjectStreamField);
    } else {
      arrayOfObjectStreamField = NO_FIELDS;
    } 
    return arrayOfObjectStreamField;
  }
  
  private static ObjectStreamField[] getDeclaredSerialFields(Class<?> paramClass) throws InvalidClassException {
    ObjectStreamField[] arrayOfObjectStreamField1 = null;
    try {
      Field field = paramClass.getDeclaredField("serialPersistentFields");
      int i = 26;
      if ((field.getModifiers() & i) == i) {
        field.setAccessible(true);
        arrayOfObjectStreamField1 = (ObjectStreamField[])field.get(null);
      } 
    } catch (Exception exception) {}
    if (arrayOfObjectStreamField1 == null)
      return null; 
    if (arrayOfObjectStreamField1.length == 0)
      return NO_FIELDS; 
    ObjectStreamField[] arrayOfObjectStreamField2 = new ObjectStreamField[arrayOfObjectStreamField1.length];
    HashSet hashSet = new HashSet(arrayOfObjectStreamField1.length);
    for (byte b = 0; b < arrayOfObjectStreamField1.length; b++) {
      ObjectStreamField objectStreamField = arrayOfObjectStreamField1[b];
      String str = objectStreamField.getName();
      if (hashSet.contains(str))
        throw new InvalidClassException("multiple serializable fields named " + str); 
      hashSet.add(str);
      try {
        Field field = paramClass.getDeclaredField(str);
        if (field.getType() == objectStreamField.getType() && (field.getModifiers() & 0x8) == 0)
          arrayOfObjectStreamField2[b] = new ObjectStreamField(field, objectStreamField.isUnshared(), true); 
      } catch (NoSuchFieldException noSuchFieldException) {}
      if (arrayOfObjectStreamField2[b] == null)
        arrayOfObjectStreamField2[b] = new ObjectStreamField(str, objectStreamField.getType(), objectStreamField.isUnshared()); 
    } 
    return arrayOfObjectStreamField2;
  }
  
  private static ObjectStreamField[] getDefaultSerialFields(Class<?> paramClass) throws InvalidClassException {
    Field[] arrayOfField = paramClass.getDeclaredFields();
    ArrayList arrayList = new ArrayList();
    int i = 136;
    int j;
    for (j = 0; j < arrayOfField.length; j++) {
      if ((arrayOfField[j].getModifiers() & i) == 0)
        arrayList.add(new ObjectStreamField(arrayOfField[j], false, true)); 
    } 
    j = arrayList.size();
    return (j == 0) ? NO_FIELDS : (ObjectStreamField[])arrayList.toArray(new ObjectStreamField[j]);
  }
  
  private static Long getDeclaredSUID(Class<?> paramClass) {
    try {
      Field field = paramClass.getDeclaredField("serialVersionUID");
      int i = 24;
      if ((field.getModifiers() & i) == i) {
        field.setAccessible(true);
        return Long.valueOf(field.getLong(null));
      } 
    } catch (Exception exception) {}
    return null;
  }
  
  private static long computeDefaultSUID(Class<?> paramClass) {
    if (!Serializable.class.isAssignableFrom(paramClass) || Proxy.isProxyClass(paramClass))
      return 0L; 
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      dataOutputStream.writeUTF(paramClass.getName());
      int i = paramClass.getModifiers() & 0x611;
      Method[] arrayOfMethod = paramClass.getDeclaredMethods();
      if ((i & 0x200) != 0)
        i = (arrayOfMethod.length > 0) ? (i | 0x400) : (i & 0xFFFFFBFF); 
      dataOutputStream.writeInt(i);
      if (!paramClass.isArray()) {
        Class[] arrayOfClass = paramClass.getInterfaces();
        String[] arrayOfString = new String[arrayOfClass.length];
        byte b;
        for (b = 0; b < arrayOfClass.length; b++)
          arrayOfString[b] = arrayOfClass[b].getName(); 
        Arrays.sort(arrayOfString);
        for (b = 0; b < arrayOfString.length; b++)
          dataOutputStream.writeUTF(arrayOfString[b]); 
      } 
      Field[] arrayOfField = paramClass.getDeclaredFields();
      MemberSignature[] arrayOfMemberSignature1 = new MemberSignature[arrayOfField.length];
      byte b1;
      for (b1 = 0; b1 < arrayOfField.length; b1++)
        arrayOfMemberSignature1[b1] = new MemberSignature(arrayOfField[b1]); 
      Arrays.sort(arrayOfMemberSignature1, new Comparator<MemberSignature>() {
            public int compare(ObjectStreamClass.MemberSignature param1MemberSignature1, ObjectStreamClass.MemberSignature param1MemberSignature2) { return param1MemberSignature1.name.compareTo(param1MemberSignature2.name); }
          });
      for (b1 = 0; b1 < arrayOfMemberSignature1.length; b1++) {
        MemberSignature memberSignature = arrayOfMemberSignature1[b1];
        int k = memberSignature.member.getModifiers() & 0xDF;
        if ((k & 0x2) == 0 || (k & 0x88) == 0) {
          dataOutputStream.writeUTF(memberSignature.name);
          dataOutputStream.writeInt(k);
          dataOutputStream.writeUTF(memberSignature.signature);
        } 
      } 
      if (hasStaticInitializer(paramClass)) {
        dataOutputStream.writeUTF("<clinit>");
        dataOutputStream.writeInt(8);
        dataOutputStream.writeUTF("()V");
      } 
      Constructor[] arrayOfConstructor = paramClass.getDeclaredConstructors();
      MemberSignature[] arrayOfMemberSignature2 = new MemberSignature[arrayOfConstructor.length];
      byte b2;
      for (b2 = 0; b2 < arrayOfConstructor.length; b2++)
        arrayOfMemberSignature2[b2] = new MemberSignature(arrayOfConstructor[b2]); 
      Arrays.sort(arrayOfMemberSignature2, new Comparator<MemberSignature>() {
            public int compare(ObjectStreamClass.MemberSignature param1MemberSignature1, ObjectStreamClass.MemberSignature param1MemberSignature2) { return param1MemberSignature1.signature.compareTo(param1MemberSignature2.signature); }
          });
      for (b2 = 0; b2 < arrayOfMemberSignature2.length; b2++) {
        MemberSignature memberSignature = arrayOfMemberSignature2[b2];
        int k = memberSignature.member.getModifiers() & 0xD3F;
        if ((k & 0x2) == 0) {
          dataOutputStream.writeUTF("<init>");
          dataOutputStream.writeInt(k);
          dataOutputStream.writeUTF(memberSignature.signature.replace('/', '.'));
        } 
      } 
      MemberSignature[] arrayOfMemberSignature3 = new MemberSignature[arrayOfMethod.length];
      byte b3;
      for (b3 = 0; b3 < arrayOfMethod.length; b3++)
        arrayOfMemberSignature3[b3] = new MemberSignature(arrayOfMethod[b3]); 
      Arrays.sort(arrayOfMemberSignature3, new Comparator<MemberSignature>() {
            public int compare(ObjectStreamClass.MemberSignature param1MemberSignature1, ObjectStreamClass.MemberSignature param1MemberSignature2) {
              int i = param1MemberSignature1.name.compareTo(param1MemberSignature2.name);
              if (i == 0)
                i = param1MemberSignature1.signature.compareTo(param1MemberSignature2.signature); 
              return i;
            }
          });
      for (b3 = 0; b3 < arrayOfMemberSignature3.length; b3++) {
        MemberSignature memberSignature = arrayOfMemberSignature3[b3];
        int k = memberSignature.member.getModifiers() & 0xD3F;
        if ((k & 0x2) == 0) {
          dataOutputStream.writeUTF(memberSignature.name);
          dataOutputStream.writeInt(k);
          dataOutputStream.writeUTF(memberSignature.signature.replace('/', '.'));
        } 
      } 
      dataOutputStream.flush();
      MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      byte[] arrayOfByte = messageDigest.digest(byteArrayOutputStream.toByteArray());
      long l = 0L;
      for (int j = Math.min(arrayOfByte.length, 8) - 1; j >= 0; j--)
        l = l << 8 | (arrayOfByte[j] & 0xFF); 
      return l;
    } catch (IOException iOException) {
      throw new InternalError(iOException);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SecurityException(noSuchAlgorithmException.getMessage());
    } 
  }
  
  private static native boolean hasStaticInitializer(Class<?> paramClass);
  
  private static FieldReflector getReflector(ObjectStreamField[] paramArrayOfObjectStreamField, ObjectStreamClass paramObjectStreamClass) throws InvalidClassException {
    Class clazz = (paramObjectStreamClass != null && paramArrayOfObjectStreamField.length > 0) ? paramObjectStreamClass.cl : null;
    processQueue(reflectorsQueue, Caches.reflectors);
    FieldReflectorKey fieldReflectorKey = new FieldReflectorKey(clazz, paramArrayOfObjectStreamField, reflectorsQueue);
    Reference reference = (Reference)Caches.reflectors.get(fieldReflectorKey);
    Object object = null;
    if (reference != null)
      object = reference.get(); 
    EntryFuture entryFuture = null;
    if (object == null) {
      EntryFuture entryFuture1 = new EntryFuture(null);
      SoftReference softReference = new SoftReference(entryFuture1);
      do {
        if (reference != null)
          Caches.reflectors.remove(fieldReflectorKey, reference); 
        reference = (Reference)Caches.reflectors.putIfAbsent(fieldReflectorKey, softReference);
        if (reference == null)
          continue; 
        object = reference.get();
      } while (reference != null && object == null);
      if (object == null)
        entryFuture = entryFuture1; 
    } 
    if (object instanceof FieldReflector)
      return (FieldReflector)object; 
    if (object instanceof EntryFuture) {
      object = ((EntryFuture)object).get();
    } else if (object == null) {
      try {
        object = new FieldReflector(matchFields(paramArrayOfObjectStreamField, paramObjectStreamClass));
      } catch (Throwable throwable) {
        object = throwable;
      } 
      entryFuture.set(object);
      Caches.reflectors.put(fieldReflectorKey, new SoftReference(object));
    } 
    if (object instanceof FieldReflector)
      return (FieldReflector)object; 
    if (object instanceof InvalidClassException)
      throw (InvalidClassException)object; 
    if (object instanceof RuntimeException)
      throw (RuntimeException)object; 
    if (object instanceof Error)
      throw (Error)object; 
    throw new InternalError("unexpected entry: " + object);
  }
  
  private static ObjectStreamField[] matchFields(ObjectStreamField[] paramArrayOfObjectStreamField, ObjectStreamClass paramObjectStreamClass) throws InvalidClassException {
    ObjectStreamField[] arrayOfObjectStreamField1 = (paramObjectStreamClass != null) ? paramObjectStreamClass.fields : NO_FIELDS;
    ObjectStreamField[] arrayOfObjectStreamField2 = new ObjectStreamField[paramArrayOfObjectStreamField.length];
    for (byte b = 0; b < paramArrayOfObjectStreamField.length; b++) {
      ObjectStreamField objectStreamField1 = paramArrayOfObjectStreamField[b];
      ObjectStreamField objectStreamField2 = null;
      for (byte b1 = 0; b1 < arrayOfObjectStreamField1.length; b1++) {
        ObjectStreamField objectStreamField = arrayOfObjectStreamField1[b1];
        if (objectStreamField1.getName().equals(objectStreamField.getName())) {
          if ((objectStreamField1.isPrimitive() || objectStreamField.isPrimitive()) && objectStreamField1.getTypeCode() != objectStreamField.getTypeCode())
            throw new InvalidClassException(paramObjectStreamClass.name, "incompatible types for field " + objectStreamField1.getName()); 
          if (objectStreamField.getField() != null) {
            objectStreamField2 = new ObjectStreamField(objectStreamField.getField(), objectStreamField.isUnshared(), false);
          } else {
            objectStreamField2 = new ObjectStreamField(objectStreamField.getName(), objectStreamField.getSignature(), objectStreamField.isUnshared());
          } 
        } 
      } 
      if (objectStreamField2 == null)
        objectStreamField2 = new ObjectStreamField(objectStreamField1.getName(), objectStreamField1.getSignature(), false); 
      objectStreamField2.setOffset(objectStreamField1.getOffset());
      arrayOfObjectStreamField2[b] = objectStreamField2;
    } 
    return arrayOfObjectStreamField2;
  }
  
  static void processQueue(ReferenceQueue<Class<?>> paramReferenceQueue, ConcurrentMap<? extends WeakReference<Class<?>>, ?> paramConcurrentMap) {
    Reference reference;
    while ((reference = paramReferenceQueue.poll()) != null)
      paramConcurrentMap.remove(reference); 
  }
  
  static  {
    initNative();
  }
  
  private static class Caches {
    static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Reference<?>> localDescs = new ConcurrentHashMap();
    
    static final ConcurrentMap<ObjectStreamClass.FieldReflectorKey, Reference<?>> reflectors = new ConcurrentHashMap();
    
    private static final ReferenceQueue<Class<?>> localDescsQueue = new ReferenceQueue();
    
    private static final ReferenceQueue<Class<?>> reflectorsQueue = new ReferenceQueue();
  }
  
  static class ClassDataSlot {
    final ObjectStreamClass desc;
    
    final boolean hasData;
    
    ClassDataSlot(ObjectStreamClass param1ObjectStreamClass, boolean param1Boolean) {
      this.desc = param1ObjectStreamClass;
      this.hasData = param1Boolean;
    }
  }
  
  private static class EntryFuture {
    private static final Object unset = new Object();
    
    private final Thread owner = Thread.currentThread();
    
    private Object entry = unset;
    
    private EntryFuture() {}
    
    boolean set(Object param1Object) {
      if (this.entry != unset)
        return false; 
      this.entry = param1Object;
      notifyAll();
      return true;
    }
    
    Object get() throws InstantiationException, InvocationTargetException, UnsupportedOperationException {
      boolean bool = false;
      while (this.entry == unset) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {
          bool = true;
        } 
      } 
      if (bool)
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                Thread.currentThread().interrupt();
                return null;
              }
            }); 
      return this.entry;
    }
    
    Thread getOwner() { return this.owner; }
  }
  
  private static class ExceptionInfo {
    private final String className;
    
    private final String message;
    
    ExceptionInfo(String param1String1, String param1String2) {
      this.className = param1String1;
      this.message = param1String2;
    }
    
    InvalidClassException newInvalidClassException() { return new InvalidClassException(this.className, this.message); }
  }
  
  private static class FieldReflector {
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    
    private final ObjectStreamField[] fields;
    
    private final int numPrimFields;
    
    private final long[] readKeys;
    
    private final long[] writeKeys;
    
    private final int[] offsets;
    
    private final char[] typeCodes;
    
    private final Class<?>[] types;
    
    FieldReflector(ObjectStreamField[] param1ArrayOfObjectStreamField) {
      this.fields = param1ArrayOfObjectStreamField;
      int i = param1ArrayOfObjectStreamField.length;
      this.readKeys = new long[i];
      this.writeKeys = new long[i];
      this.offsets = new int[i];
      this.typeCodes = new char[i];
      ArrayList arrayList = new ArrayList();
      HashSet hashSet = new HashSet();
      for (byte b = 0; b < i; b++) {
        ObjectStreamField objectStreamField = param1ArrayOfObjectStreamField[b];
        Field field = objectStreamField.getField();
        long l = (field != null) ? unsafe.objectFieldOffset(field) : -1L;
        this.readKeys[b] = l;
        this.writeKeys[b] = hashSet.add(Long.valueOf(l)) ? l : -1L;
        this.offsets[b] = objectStreamField.getOffset();
        this.typeCodes[b] = objectStreamField.getTypeCode();
        if (!objectStreamField.isPrimitive())
          arrayList.add((field != null) ? field.getType() : null); 
      } 
      this.types = (Class[])arrayList.toArray(new Class[arrayList.size()]);
      this.numPrimFields = i - this.types.length;
    }
    
    ObjectStreamField[] getFields() { return this.fields; }
    
    void getPrimFieldValues(Object param1Object, byte[] param1ArrayOfByte) {
      if (param1Object == null)
        throw new NullPointerException(); 
      for (byte b = 0; b < this.numPrimFields; b++) {
        long l = this.readKeys[b];
        int i = this.offsets[b];
        switch (this.typeCodes[b]) {
          case 'Z':
            Bits.putBoolean(param1ArrayOfByte, i, unsafe.getBoolean(param1Object, l));
            break;
          case 'B':
            param1ArrayOfByte[i] = unsafe.getByte(param1Object, l);
            break;
          case 'C':
            Bits.putChar(param1ArrayOfByte, i, unsafe.getChar(param1Object, l));
            break;
          case 'S':
            Bits.putShort(param1ArrayOfByte, i, unsafe.getShort(param1Object, l));
            break;
          case 'I':
            Bits.putInt(param1ArrayOfByte, i, unsafe.getInt(param1Object, l));
            break;
          case 'F':
            Bits.putFloat(param1ArrayOfByte, i, unsafe.getFloat(param1Object, l));
            break;
          case 'J':
            Bits.putLong(param1ArrayOfByte, i, unsafe.getLong(param1Object, l));
            break;
          case 'D':
            Bits.putDouble(param1ArrayOfByte, i, unsafe.getDouble(param1Object, l));
            break;
          default:
            throw new InternalError();
        } 
      } 
    }
    
    void setPrimFieldValues(Object param1Object, byte[] param1ArrayOfByte) {
      if (param1Object == null)
        throw new NullPointerException(); 
      for (byte b = 0; b < this.numPrimFields; b++) {
        long l = this.writeKeys[b];
        if (l != -1L) {
          int i = this.offsets[b];
          switch (this.typeCodes[b]) {
            case 'Z':
              unsafe.putBoolean(param1Object, l, Bits.getBoolean(param1ArrayOfByte, i));
              break;
            case 'B':
              unsafe.putByte(param1Object, l, param1ArrayOfByte[i]);
              break;
            case 'C':
              unsafe.putChar(param1Object, l, Bits.getChar(param1ArrayOfByte, i));
              break;
            case 'S':
              unsafe.putShort(param1Object, l, Bits.getShort(param1ArrayOfByte, i));
              break;
            case 'I':
              unsafe.putInt(param1Object, l, Bits.getInt(param1ArrayOfByte, i));
              break;
            case 'F':
              unsafe.putFloat(param1Object, l, Bits.getFloat(param1ArrayOfByte, i));
              break;
            case 'J':
              unsafe.putLong(param1Object, l, Bits.getLong(param1ArrayOfByte, i));
              break;
            case 'D':
              unsafe.putDouble(param1Object, l, Bits.getDouble(param1ArrayOfByte, i));
              break;
            default:
              throw new InternalError();
          } 
        } 
      } 
    }
    
    void getObjFieldValues(Object param1Object, Object[] param1ArrayOfObject) {
      if (param1Object == null)
        throw new NullPointerException(); 
      for (int i = this.numPrimFields; i < this.fields.length; i++) {
        switch (this.typeCodes[i]) {
          case 'L':
          case '[':
            param1ArrayOfObject[this.offsets[i]] = unsafe.getObject(param1Object, this.readKeys[i]);
            break;
          default:
            throw new InternalError();
        } 
      } 
    }
    
    void setObjFieldValues(Object param1Object, Object[] param1ArrayOfObject) {
      if (param1Object == null)
        throw new NullPointerException(); 
      for (int i = this.numPrimFields; i < this.fields.length; i++) {
        long l = this.writeKeys[i];
        if (l != -1L) {
          Object object;
          switch (this.typeCodes[i]) {
            case 'L':
            case '[':
              object = param1ArrayOfObject[this.offsets[i]];
              if (object != null && !this.types[i - this.numPrimFields].isInstance(object)) {
                Field field = this.fields[i].getField();
                throw new ClassCastException("cannot assign instance of " + object.getClass().getName() + " to field " + field.getDeclaringClass().getName() + "." + field.getName() + " of type " + field.getType().getName() + " in instance of " + param1Object.getClass().getName());
              } 
              unsafe.putObject(param1Object, l, object);
              break;
            default:
              throw new InternalError();
          } 
        } 
      } 
    }
  }
  
  private static class FieldReflectorKey extends WeakReference<Class<?>> {
    private final String sigs;
    
    private final int hash;
    
    private final boolean nullClass;
    
    FieldReflectorKey(Class<?> param1Class, ObjectStreamField[] param1ArrayOfObjectStreamField, ReferenceQueue<Class<?>> param1ReferenceQueue) {
      super(param1Class, param1ReferenceQueue);
      this.nullClass = (param1Class == null);
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b = 0; b < param1ArrayOfObjectStreamField.length; b++) {
        ObjectStreamField objectStreamField = param1ArrayOfObjectStreamField[b];
        stringBuilder.append(objectStreamField.getName()).append(objectStreamField.getSignature());
      } 
      this.sigs = stringBuilder.toString();
      this.hash = System.identityHashCode(param1Class) + this.sigs.hashCode();
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (param1Object instanceof FieldReflectorKey) {
        FieldReflectorKey fieldReflectorKey = (FieldReflectorKey)param1Object;
        Class clazz;
        return ((this.nullClass ? fieldReflectorKey.nullClass : ((clazz = (Class)get()) != null && clazz == fieldReflectorKey.get())) && this.sigs.equals(fieldReflectorKey.sigs));
      } 
      return false;
    }
  }
  
  private static class MemberSignature {
    public final Member member;
    
    public final String name;
    
    public final String signature;
    
    public MemberSignature(Field param1Field) {
      this.member = param1Field;
      this.name = param1Field.getName();
      this.signature = ObjectStreamClass.getClassSignature(param1Field.getType());
    }
    
    public MemberSignature(Constructor<?> param1Constructor) {
      this.member = param1Constructor;
      this.name = param1Constructor.getName();
      this.signature = ObjectStreamClass.getMethodSignature(param1Constructor.getParameterTypes(), void.class);
    }
    
    public MemberSignature(Method param1Method) {
      this.member = param1Method;
      this.name = param1Method.getName();
      this.signature = ObjectStreamClass.getMethodSignature(param1Method.getParameterTypes(), param1Method.getReturnType());
    }
  }
  
  static class WeakClassKey extends WeakReference<Class<?>> {
    private final int hash;
    
    WeakClassKey(Class<?> param1Class, ReferenceQueue<Class<?>> param1ReferenceQueue) {
      super(param1Class, param1ReferenceQueue);
      this.hash = System.identityHashCode(param1Class);
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (param1Object instanceof WeakClassKey) {
        Object object = get();
        return (object != null && object == ((WeakClassKey)param1Object).get());
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ObjectStreamClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */