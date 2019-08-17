package java.io;

import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.misc.JavaOISAccess;
import sun.misc.ObjectInputFilter;
import sun.misc.ObjectStreamClassValidator;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.reflect.misc.ReflectUtil;
import sun.util.logging.PlatformLogger;

public class ObjectInputStream extends InputStream implements ObjectInput, ObjectStreamConstants {
  private static final int NULL_HANDLE = -1;
  
  private static final Object unsharedMarker = new Object();
  
  private static final HashMap<String, Class<?>> primClasses = new HashMap(8, 1.0F);
  
  private final BlockDataInputStream bin;
  
  private final ValidationList vlist;
  
  private long depth;
  
  private long totalObjectRefs;
  
  private boolean closed;
  
  private final HandleTable handles;
  
  private int passHandle = -1;
  
  private boolean defaultDataEnd = false;
  
  private byte[] primVals;
  
  private final boolean enableOverride;
  
  private boolean enableResolve;
  
  private SerialCallbackContext curContext;
  
  private ObjectInputFilter serialFilter;
  
  public ObjectInputStream(InputStream paramInputStream) throws IOException {
    verifySubclass();
    this.bin = new BlockDataInputStream(paramInputStream);
    this.handles = new HandleTable(10);
    this.vlist = new ValidationList();
    this.serialFilter = ObjectInputFilter.Config.getSerialFilter();
    this.enableOverride = false;
    readStreamHeader();
    this.bin.setBlockDataMode(true);
  }
  
  protected ObjectInputStream() throws IOException, SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION); 
    this.bin = null;
    this.handles = null;
    this.vlist = null;
    this.serialFilter = ObjectInputFilter.Config.getSerialFilter();
    this.enableOverride = true;
  }
  
  public final Object readObject() throws IOException, ClassNotFoundException {
    if (this.enableOverride)
      return readObjectOverride(); 
    i = this.passHandle;
    try {
      Object object = readObject0(false);
      this.handles.markDependency(i, this.passHandle);
      ClassNotFoundException classNotFoundException = this.handles.lookupException(this.passHandle);
      if (classNotFoundException != null)
        throw classNotFoundException; 
      if (this.depth == 0L)
        this.vlist.doCallbacks(); 
      return object;
    } finally {
      this.passHandle = i;
      if (this.closed && this.depth == 0L)
        clear(); 
    } 
  }
  
  protected Object readObjectOverride() throws IOException, ClassNotFoundException { return null; }
  
  public Object readUnshared() throws IOException, ClassNotFoundException {
    i = this.passHandle;
    try {
      Object object = readObject0(true);
      this.handles.markDependency(i, this.passHandle);
      ClassNotFoundException classNotFoundException = this.handles.lookupException(this.passHandle);
      if (classNotFoundException != null)
        throw classNotFoundException; 
      if (this.depth == 0L)
        this.vlist.doCallbacks(); 
      return object;
    } finally {
      this.passHandle = i;
      if (this.closed && this.depth == 0L)
        clear(); 
    } 
  }
  
  public void defaultReadObject() throws IOException, SecurityException {
    SerialCallbackContext serialCallbackContext = this.curContext;
    if (serialCallbackContext == null)
      throw new NotActiveException("not in call to readObject"); 
    Object object = serialCallbackContext.getObj();
    ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
    this.bin.setBlockDataMode(false);
    defaultReadFields(object, objectStreamClass);
    this.bin.setBlockDataMode(true);
    if (!objectStreamClass.hasWriteObjectData())
      this.defaultDataEnd = true; 
    ClassNotFoundException classNotFoundException = this.handles.lookupException(this.passHandle);
    if (classNotFoundException != null)
      throw classNotFoundException; 
  }
  
  public GetField readFields() throws IOException, ClassNotFoundException {
    SerialCallbackContext serialCallbackContext = this.curContext;
    if (serialCallbackContext == null)
      throw new NotActiveException("not in call to readObject"); 
    Object object = serialCallbackContext.getObj();
    ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
    this.bin.setBlockDataMode(false);
    GetFieldImpl getFieldImpl = new GetFieldImpl(objectStreamClass);
    getFieldImpl.readFields();
    this.bin.setBlockDataMode(true);
    if (!objectStreamClass.hasWriteObjectData())
      this.defaultDataEnd = true; 
    return getFieldImpl;
  }
  
  public void registerValidation(ObjectInputValidation paramObjectInputValidation, int paramInt) throws NotActiveException, InvalidObjectException {
    if (this.depth == 0L)
      throw new NotActiveException("stream inactive"); 
    this.vlist.register(paramObjectInputValidation, paramInt);
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass) throws IOException, ClassNotFoundException {
    String str = paramObjectStreamClass.getName();
    try {
      return Class.forName(str, false, latestUserDefinedLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      Class clazz = (Class)primClasses.get(str);
      if (clazz != null)
        return clazz; 
      throw classNotFoundException;
    } 
  }
  
  protected Class<?> resolveProxyClass(String[] paramArrayOfString) throws IOException, ClassNotFoundException {
    ClassLoader classLoader1 = latestUserDefinedLoader();
    ClassLoader classLoader2 = null;
    boolean bool = false;
    Class[] arrayOfClass = new Class[paramArrayOfString.length];
    for (b = 0; b < paramArrayOfString.length; b++) {
      Class clazz = Class.forName(paramArrayOfString[b], false, classLoader1);
      if ((clazz.getModifiers() & true) == 0)
        if (bool) {
          if (classLoader2 != clazz.getClassLoader())
            throw new IllegalAccessError("conflicting non-public interface class loaders"); 
        } else {
          classLoader2 = clazz.getClassLoader();
          bool = true;
        }  
      arrayOfClass[b] = clazz;
    } 
    try {
      return Proxy.getProxyClass(bool ? classLoader2 : classLoader1, arrayOfClass);
    } catch (IllegalArgumentException b) {
      IllegalArgumentException illegalArgumentException;
      throw new ClassNotFoundException(null, illegalArgumentException);
    } 
  }
  
  protected Object resolveObject(Object paramObject) throws IOException { return paramObject; }
  
  protected boolean enableResolveObject(boolean paramBoolean) throws SecurityException {
    if (paramBoolean == this.enableResolve)
      return paramBoolean; 
    if (paramBoolean) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SUBSTITUTION_PERMISSION); 
    } 
    this.enableResolve = paramBoolean;
    return !this.enableResolve;
  }
  
  protected void readStreamHeader() throws IOException, SecurityException {
    short s1 = this.bin.readShort();
    short s2 = this.bin.readShort();
    if (s1 != -21267 || s2 != 5)
      throw new StreamCorruptedException(String.format("invalid stream header: %04X%04X", new Object[] { Short.valueOf(s1), Short.valueOf(s2) })); 
  }
  
  protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
    ObjectStreamClass objectStreamClass = new ObjectStreamClass();
    objectStreamClass.readNonProxy(this);
    return objectStreamClass;
  }
  
  public int read() throws IOException { return this.bin.read(); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    int i = paramInt1 + paramInt2;
    if (paramInt1 < 0 || paramInt2 < 0 || i > paramArrayOfByte.length || i < 0)
      throw new IndexOutOfBoundsException(); 
    return this.bin.read(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public int available() throws IOException { return this.bin.available(); }
  
  public void close() throws IOException, SecurityException {
    this.closed = true;
    if (this.depth == 0L)
      clear(); 
    this.bin.close();
  }
  
  public boolean readBoolean() throws IOException { return this.bin.readBoolean(); }
  
  public byte readByte() throws IOException { return this.bin.readByte(); }
  
  public int readUnsignedByte() throws IOException { return this.bin.readUnsignedByte(); }
  
  public char readChar() throws IOException { return this.bin.readChar(); }
  
  public short readShort() throws IOException { return this.bin.readShort(); }
  
  public int readUnsignedShort() throws IOException { return this.bin.readUnsignedShort(); }
  
  public int readInt() throws IOException { return this.bin.readInt(); }
  
  public long readLong() throws IOException { return this.bin.readLong(); }
  
  public float readFloat() throws IOException { return this.bin.readFloat(); }
  
  public double readDouble() throws IOException { return this.bin.readDouble(); }
  
  public void readFully(byte[] paramArrayOfByte) throws IOException { this.bin.readFully(paramArrayOfByte, 0, paramArrayOfByte.length, false); }
  
  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    int i = paramInt1 + paramInt2;
    if (paramInt1 < 0 || paramInt2 < 0 || i > paramArrayOfByte.length || i < 0)
      throw new IndexOutOfBoundsException(); 
    this.bin.readFully(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public int skipBytes(int paramInt) throws IOException { return this.bin.skipBytes(paramInt); }
  
  @Deprecated
  public String readLine() throws IOException { return this.bin.readLine(); }
  
  public String readUTF() throws IOException { return this.bin.readUTF(); }
  
  private final ObjectInputFilter getInternalObjectInputFilter() { return this.serialFilter; }
  
  private final void setInternalObjectInputFilter(ObjectInputFilter paramObjectInputFilter) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new SerializablePermission("serialFilter")); 
    if (this.serialFilter != null && this.serialFilter != ObjectInputFilter.Config.getSerialFilter())
      throw new IllegalStateException("filter can not be set more than once"); 
    this.serialFilter = paramObjectInputFilter;
  }
  
  private void filterCheck(Class<?> paramClass, int paramInt) throws InvalidClassException {
    if (this.serialFilter != null) {
      ObjectInputFilter.Status status;
      RuntimeException runtimeException = null;
      long l = (this.bin == null) ? 0L : this.bin.getBytesRead();
      try {
        status = this.serialFilter.checkInput(new FilterValues(paramClass, paramInt, this.totalObjectRefs, this.depth, l));
      } catch (RuntimeException runtimeException1) {
        status = ObjectInputFilter.Status.REJECTED;
        runtimeException = runtimeException1;
      } 
      if (status == null || status == ObjectInputFilter.Status.REJECTED) {
        if (infoLogger != null)
          infoLogger.info("ObjectInputFilter {0}: {1}, array length: {2}, nRefs: {3}, depth: {4}, bytes: {5}, ex: {6}", new Object[] { status, paramClass, Integer.valueOf(paramInt), Long.valueOf(this.totalObjectRefs), Long.valueOf(this.depth), Long.valueOf(l), Objects.toString(runtimeException, "n/a") }); 
        InvalidClassException invalidClassException = new InvalidClassException("filter status: " + status);
        invalidClassException.initCause(runtimeException);
        throw invalidClassException;
      } 
      if (traceLogger != null)
        traceLogger.finer("ObjectInputFilter {0}: {1}, array length: {2}, nRefs: {3}, depth: {4}, bytes: {5}, ex: {6}", new Object[] { status, paramClass, Integer.valueOf(paramInt), Long.valueOf(this.totalObjectRefs), Long.valueOf(this.depth), Long.valueOf(l), Objects.toString(runtimeException, "n/a") }); 
    } 
  }
  
  private void checkArray(Class<?> paramClass, int paramInt) throws InvalidClassException {
    Objects.requireNonNull(paramClass);
    if (!paramClass.isArray())
      throw new IllegalArgumentException("not an array type"); 
    if (paramInt < 0)
      throw new NegativeArraySizeException(); 
    filterCheck(paramClass, paramInt);
  }
  
  private void verifySubclass() throws IOException, SecurityException {
    Class clazz = getClass();
    if (clazz == ObjectInputStream.class)
      return; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return; 
    ObjectStreamClass.processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
    ObjectStreamClass.WeakClassKey weakClassKey = new ObjectStreamClass.WeakClassKey(clazz, Caches.subclassAuditsQueue);
    Boolean bool = (Boolean)Caches.subclassAudits.get(weakClassKey);
    if (bool == null) {
      bool = Boolean.valueOf(auditSubclass(clazz));
      Caches.subclassAudits.putIfAbsent(weakClassKey, bool);
    } 
    if (bool.booleanValue())
      return; 
    securityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
  }
  
  private static boolean auditSubclass(final Class<?> subcl) {
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            Class clazz = subcl;
            while (clazz != ObjectInputStream.class) {
              try {
                clazz.getDeclaredMethod("readUnshared", (Class[])null);
                return Boolean.FALSE;
              } catch (NoSuchMethodException noSuchMethodException) {
                try {
                  clazz.getDeclaredMethod("readFields", (Class[])null);
                  return Boolean.FALSE;
                } catch (NoSuchMethodException noSuchMethodException) {
                  clazz = clazz.getSuperclass();
                } 
              } 
            } 
            return Boolean.TRUE;
          }
        });
    return bool.booleanValue();
  }
  
  private void clear() throws IOException, SecurityException {
    this.handles.clear();
    this.vlist.clear();
  }
  
  private Object readObject0(boolean paramBoolean) throws IOException {
    bool = this.bin.getBlockDataMode();
    if (bool) {
      int i = this.bin.currentBlockRemaining();
      if (i > 0)
        throw new OptionalDataException(i); 
      if (this.defaultDataEnd)
        throw new OptionalDataException(true); 
      this.bin.setBlockDataMode(false);
    } 
    byte b;
    while ((b = this.bin.peekByte()) == 121) {
      this.bin.readByte();
      handleReset();
    } 
    this.depth++;
    this.totalObjectRefs++;
    try {
      switch (b) {
        case 112:
          return readNull();
        case 113:
          return readHandle(paramBoolean);
        case 118:
          return readClass(paramBoolean);
        case 114:
        case 125:
          return readClassDesc(paramBoolean);
        case 116:
        case 124:
          return checkResolve(readString(paramBoolean));
        case 117:
          return checkResolve(readArray(paramBoolean));
        case 126:
          return checkResolve(readEnum(paramBoolean));
        case 115:
          return checkResolve(readOrdinaryObject(paramBoolean));
        case 123:
          null = readFatalException();
          throw new WriteAbortedException("writing aborted", null);
        case 119:
        case 122:
          if (bool) {
            this.bin.setBlockDataMode(true);
            this.bin.peek();
            throw new OptionalDataException(this.bin.currentBlockRemaining());
          } 
          throw new StreamCorruptedException("unexpected block data");
        case 120:
          if (bool)
            throw new OptionalDataException(true); 
          throw new StreamCorruptedException("unexpected end of block data");
      } 
      throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
    } finally {
      this.depth--;
      this.bin.setBlockDataMode(bool);
    } 
  }
  
  private Object checkResolve(Object paramObject) throws IOException {
    if (!this.enableResolve || this.handles.lookupException(this.passHandle) != null)
      return paramObject; 
    Object object = resolveObject(paramObject);
    if (object != paramObject) {
      if (object != null)
        if (object.getClass().isArray()) {
          filterCheck(object.getClass(), Array.getLength(object));
        } else {
          filterCheck(object.getClass(), -1);
        }  
      this.handles.setObject(this.passHandle, object);
    } 
    return object;
  }
  
  String readTypeString() throws IOException {
    i = this.passHandle;
    try {
      byte b = this.bin.peekByte();
      switch (b) {
        case 112:
          return (String)readNull();
        case 113:
          return (String)readHandle(false);
        case 116:
        case 124:
          return readString(false);
      } 
      throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
    } finally {
      this.passHandle = i;
    } 
  }
  
  private Object readNull() throws IOException, ClassNotFoundException {
    if (this.bin.readByte() != 112)
      throw new InternalError(); 
    this.passHandle = -1;
    return null;
  }
  
  private Object readHandle(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 113)
      throw new InternalError(); 
    this.passHandle = this.bin.readInt() - 8257536;
    if (this.passHandle < 0 || this.passHandle >= this.handles.size())
      throw new StreamCorruptedException(String.format("invalid handle value: %08X", new Object[] { Integer.valueOf(this.passHandle + 8257536) })); 
    if (paramBoolean)
      throw new InvalidObjectException("cannot read back reference as unshared"); 
    Object object = this.handles.lookupObject(this.passHandle);
    if (object == unsharedMarker)
      throw new InvalidObjectException("cannot read back reference to unshared object"); 
    filterCheck(null, -1);
    return object;
  }
  
  private Class<?> readClass(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 118)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    Class clazz = objectStreamClass.forClass();
    this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : clazz);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(this.passHandle, classNotFoundException); 
    this.handles.finish(this.passHandle);
    return clazz;
  }
  
  private ObjectStreamClass readClassDesc(boolean paramBoolean) throws IOException {
    ObjectStreamClass objectStreamClass;
    byte b = this.bin.peekByte();
    switch (b) {
      case 112:
        objectStreamClass = (ObjectStreamClass)readNull();
        break;
      case 113:
        objectStreamClass = (ObjectStreamClass)readHandle(paramBoolean);
        break;
      case 125:
        objectStreamClass = readProxyDesc(paramBoolean);
        break;
      case 114:
        objectStreamClass = readNonProxyDesc(paramBoolean);
        break;
      default:
        throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
    } 
    if (objectStreamClass != null)
      validateDescriptor(objectStreamClass); 
    return objectStreamClass;
  }
  
  private boolean isCustomSubclass() throws IOException { return (getClass().getClassLoader() != ObjectInputStream.class.getClassLoader()); }
  
  private ObjectStreamClass readProxyDesc(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 125)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = new ObjectStreamClass();
    int i = this.handles.assign(paramBoolean ? unsharedMarker : objectStreamClass);
    this.passHandle = -1;
    int j = this.bin.readInt();
    if (j > 65535)
      throw new InvalidObjectException("interface limit exceeded: " + j); 
    String[] arrayOfString = new String[j];
    for (byte b = 0; b < j; b++)
      arrayOfString[b] = this.bin.readUTF(); 
    Class clazz = null;
    ClassNotFoundException classNotFoundException = null;
    this.bin.setBlockDataMode(true);
    try {
      if ((clazz = resolveProxyClass(arrayOfString)) == null) {
        classNotFoundException = new ClassNotFoundException("null class");
      } else {
        if (!Proxy.isProxyClass(clazz))
          throw new InvalidClassException("Not a proxy"); 
        ReflectUtil.checkProxyPackageAccess(getClass().getClassLoader(), clazz.getInterfaces());
        for (Class clazz1 : clazz.getInterfaces())
          filterCheck(clazz1, -1); 
      } 
    } catch (ClassNotFoundException classNotFoundException1) {
      classNotFoundException = classNotFoundException1;
    } 
    filterCheck(clazz, -1);
    skipCustomData();
    try {
      this.totalObjectRefs++;
      this.depth++;
      objectStreamClass.initProxy(clazz, classNotFoundException, readClassDesc(false));
    } finally {
      this.depth--;
    } 
    this.handles.finish(i);
    this.passHandle = i;
    return objectStreamClass;
  }
  
  private ObjectStreamClass readNonProxyDesc(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 114)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass1 = new ObjectStreamClass();
    int i = this.handles.assign(paramBoolean ? unsharedMarker : objectStreamClass1);
    this.passHandle = -1;
    ObjectStreamClass objectStreamClass2 = null;
    try {
      objectStreamClass2 = readClassDescriptor();
    } catch (ClassNotFoundException classNotFoundException1) {
      throw (IOException)(new InvalidClassException("failed to read class descriptor")).initCause(classNotFoundException1);
    } 
    Class clazz = null;
    ClassNotFoundException classNotFoundException = null;
    this.bin.setBlockDataMode(true);
    boolean bool = isCustomSubclass();
    try {
      if ((clazz = resolveClass(objectStreamClass2)) == null) {
        classNotFoundException = new ClassNotFoundException("null class");
      } else if (bool) {
        ReflectUtil.checkPackageAccess(clazz);
      } 
    } catch (ClassNotFoundException classNotFoundException1) {
      classNotFoundException = classNotFoundException1;
    } 
    filterCheck(clazz, -1);
    skipCustomData();
    try {
      this.totalObjectRefs++;
      this.depth++;
      objectStreamClass1.initNonProxy(objectStreamClass2, clazz, classNotFoundException, readClassDesc(false));
    } finally {
      this.depth--;
    } 
    this.handles.finish(i);
    this.passHandle = i;
    return objectStreamClass1;
  }
  
  private String readString(boolean paramBoolean) throws IOException {
    String str;
    byte b = this.bin.readByte();
    switch (b) {
      case 116:
        str = this.bin.readUTF();
        break;
      case 124:
        str = this.bin.readLongUTF();
        break;
      default:
        throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Byte.valueOf(b) }));
    } 
    this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : str);
    this.handles.finish(this.passHandle);
    return str;
  }
  
  private Object readArray(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 117)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    int i = this.bin.readInt();
    filterCheck(objectStreamClass.forClass(), i);
    Object object = null;
    Class clazz2 = null;
    Class clazz1;
    if ((clazz1 = objectStreamClass.forClass()) != null) {
      clazz2 = clazz1.getComponentType();
      object = Array.newInstance(clazz2, i);
    } 
    int j = this.handles.assign(paramBoolean ? unsharedMarker : object);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(j, classNotFoundException); 
    if (clazz2 == null) {
      for (byte b = 0; b < i; b++)
        readObject0(false); 
    } else if (clazz2.isPrimitive()) {
      if (clazz2 == int.class) {
        this.bin.readInts((int[])object, 0, i);
      } else if (clazz2 == byte.class) {
        this.bin.readFully((byte[])object, 0, i, true);
      } else if (clazz2 == long.class) {
        this.bin.readLongs((long[])object, 0, i);
      } else if (clazz2 == float.class) {
        this.bin.readFloats((float[])object, 0, i);
      } else if (clazz2 == double.class) {
        this.bin.readDoubles((double[])object, 0, i);
      } else if (clazz2 == short.class) {
        this.bin.readShorts((short[])object, 0, i);
      } else if (clazz2 == char.class) {
        this.bin.readChars((char[])object, 0, i);
      } else if (clazz2 == boolean.class) {
        this.bin.readBooleans((boolean[])object, 0, i);
      } else {
        throw new InternalError();
      } 
    } else {
      Object[] arrayOfObject = (Object[])object;
      for (byte b = 0; b < i; b++) {
        arrayOfObject[b] = readObject0(false);
        this.handles.markDependency(j, this.passHandle);
      } 
    } 
    this.handles.finish(j);
    this.passHandle = j;
    return object;
  }
  
  private Enum<?> readEnum(boolean paramBoolean) throws IOException {
    if (this.bin.readByte() != 126)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    if (!objectStreamClass.isEnum())
      throw new InvalidClassException("non-enum class: " + objectStreamClass); 
    int i = this.handles.assign(paramBoolean ? unsharedMarker : null);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(i, classNotFoundException); 
    String str = readString(false);
    Enum enum = null;
    Class clazz = objectStreamClass.forClass();
    if (clazz != null) {
      try {
        Enum enum1 = Enum.valueOf(clazz, str);
        enum = enum1;
      } catch (IllegalArgumentException illegalArgumentException) {
        throw (IOException)(new InvalidObjectException("enum constant " + str + " does not exist in " + clazz)).initCause(illegalArgumentException);
      } 
      if (!paramBoolean)
        this.handles.setObject(i, enum); 
    } 
    this.handles.finish(i);
    this.passHandle = i;
    return enum;
  }
  
  private Object readOrdinaryObject(boolean paramBoolean) throws IOException {
    Object object;
    if (this.bin.readByte() != 115)
      throw new InternalError(); 
    ObjectStreamClass objectStreamClass = readClassDesc(false);
    objectStreamClass.checkDeserialize();
    Class clazz = objectStreamClass.forClass();
    if (clazz == String.class || clazz == Class.class || clazz == ObjectStreamClass.class)
      throw new InvalidClassException("invalid class descriptor"); 
    try {
      object = objectStreamClass.isInstantiable() ? objectStreamClass.newInstance() : null;
    } catch (Exception exception) {
      throw (IOException)(new InvalidClassException(objectStreamClass.forClass().getName(), "unable to create instance")).initCause(exception);
    } 
    this.passHandle = this.handles.assign(paramBoolean ? unsharedMarker : object);
    ClassNotFoundException classNotFoundException = objectStreamClass.getResolveException();
    if (classNotFoundException != null)
      this.handles.markException(this.passHandle, classNotFoundException); 
    if (objectStreamClass.isExternalizable()) {
      readExternalData((Externalizable)object, objectStreamClass);
    } else {
      readSerialData(object, objectStreamClass);
    } 
    this.handles.finish(this.passHandle);
    if (object != null && this.handles.lookupException(this.passHandle) == null && objectStreamClass.hasReadResolveMethod()) {
      Object object1 = objectStreamClass.invokeReadResolve(object);
      if (paramBoolean && object1.getClass().isArray())
        object1 = cloneArray(object1); 
      if (object1 != object) {
        if (object1 != null)
          if (object1.getClass().isArray()) {
            filterCheck(object1.getClass(), Array.getLength(object1));
          } else {
            filterCheck(object1.getClass(), -1);
          }  
        this.handles.setObject(this.passHandle, object = object1);
      } 
    } 
    return object;
  }
  
  private void readExternalData(Externalizable paramExternalizable, ObjectStreamClass paramObjectStreamClass) throws IOException {
    serialCallbackContext = this.curContext;
    if (serialCallbackContext != null)
      serialCallbackContext.check(); 
    this.curContext = null;
    try {
      boolean bool = paramObjectStreamClass.hasBlockExternalData();
      if (bool)
        this.bin.setBlockDataMode(true); 
      if (paramExternalizable != null)
        try {
          paramExternalizable.readExternal(this);
        } catch (ClassNotFoundException classNotFoundException) {
          this.handles.markException(this.passHandle, classNotFoundException);
        }  
      if (bool)
        skipCustomData(); 
    } finally {
      if (serialCallbackContext != null)
        serialCallbackContext.check(); 
      this.curContext = serialCallbackContext;
    } 
  }
  
  private void readSerialData(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    ObjectStreamClass.ClassDataSlot[] arrayOfClassDataSlot = paramObjectStreamClass.getClassDataLayout();
    for (byte b = 0; b < arrayOfClassDataSlot.length; b++) {
      ObjectStreamClass objectStreamClass = (arrayOfClassDataSlot[b]).desc;
      if ((arrayOfClassDataSlot[b]).hasData) {
        if (paramObject == null || this.handles.lookupException(this.passHandle) != null) {
          defaultReadFields(null, objectStreamClass);
        } else if (objectStreamClass.hasReadObjectMethod()) {
          threadDeath = null;
          bool = false;
          serialCallbackContext = this.curContext;
          if (serialCallbackContext != null)
            serialCallbackContext.check(); 
          try {
            this.curContext = new SerialCallbackContext(paramObject, objectStreamClass);
            this.bin.setBlockDataMode(true);
            objectStreamClass.invokeReadObject(paramObject, this);
          } catch (ClassNotFoundException classNotFoundException2) {
            this.handles.markException(this.passHandle, classNotFoundException2);
          } finally {
            do {
              try {
                this.curContext.setUsed();
                if (serialCallbackContext != null)
                  serialCallbackContext.check(); 
                this.curContext = serialCallbackContext;
                bool = true;
              } catch (ThreadDeath threadDeath1) {
                threadDeath = threadDeath1;
              } 
            } while (!bool);
            if (threadDeath != null)
              throw threadDeath; 
          } 
          this.defaultDataEnd = false;
        } else {
          defaultReadFields(paramObject, objectStreamClass);
        } 
        if (objectStreamClass.hasWriteObjectData()) {
          skipCustomData();
        } else {
          this.bin.setBlockDataMode(false);
        } 
      } else if (paramObject != null && objectStreamClass.hasReadObjectNoDataMethod() && this.handles.lookupException(this.passHandle) == null) {
        objectStreamClass.invokeReadObjectNoData(paramObject);
      } 
    } 
  }
  
  private void skipCustomData() throws IOException, SecurityException {
    int i = this.passHandle;
    while (true) {
      if (this.bin.getBlockDataMode()) {
        this.bin.skipBlockData();
        this.bin.setBlockDataMode(false);
      } 
      switch (this.bin.peekByte()) {
        case 119:
        case 122:
          this.bin.setBlockDataMode(true);
          continue;
        case 120:
          this.bin.readByte();
          this.passHandle = i;
          return;
      } 
      readObject0(false);
    } 
  }
  
  private void defaultReadFields(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    Class clazz = paramObjectStreamClass.forClass();
    if (clazz != null && paramObject != null && !clazz.isInstance(paramObject))
      throw new ClassCastException(); 
    int i = paramObjectStreamClass.getPrimDataSize();
    if (this.primVals == null || this.primVals.length < i)
      this.primVals = new byte[i]; 
    this.bin.readFully(this.primVals, 0, i, false);
    if (paramObject != null)
      paramObjectStreamClass.setPrimFieldValues(paramObject, this.primVals); 
    int j = this.passHandle;
    ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields(false);
    Object[] arrayOfObject = new Object[paramObjectStreamClass.getNumObjFields()];
    int k = arrayOfObjectStreamField.length - arrayOfObject.length;
    for (int m = 0; m < arrayOfObject.length; m++) {
      ObjectStreamField objectStreamField = arrayOfObjectStreamField[k + m];
      arrayOfObject[m] = readObject0(objectStreamField.isUnshared());
      if (objectStreamField.getField() != null)
        this.handles.markDependency(j, this.passHandle); 
    } 
    if (paramObject != null)
      paramObjectStreamClass.setObjFieldValues(paramObject, arrayOfObject); 
    this.passHandle = j;
  }
  
  private IOException readFatalException() throws IOException {
    if (this.bin.readByte() != 123)
      throw new InternalError(); 
    clear();
    return (IOException)readObject0(false);
  }
  
  private void handleReset() throws IOException, SecurityException {
    if (this.depth > 0L)
      throw new StreamCorruptedException("unexpected reset; recursion depth: " + this.depth); 
    clear();
  }
  
  private static native void bytesToFloats(byte[] paramArrayOfByte, int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
  
  private static native void bytesToDoubles(byte[] paramArrayOfByte, int paramInt1, double[] paramArrayOfDouble, int paramInt2, int paramInt3);
  
  private static ClassLoader latestUserDefinedLoader() { return VM.latestUserDefinedLoader(); }
  
  private static Object cloneArray(Object paramObject) throws IOException {
    if (paramObject instanceof Object[])
      return ((Object[])paramObject).clone(); 
    if (paramObject instanceof boolean[])
      return ((boolean[])paramObject).clone(); 
    if (paramObject instanceof byte[])
      return ((byte[])paramObject).clone(); 
    if (paramObject instanceof char[])
      return ((char[])paramObject).clone(); 
    if (paramObject instanceof double[])
      return ((double[])paramObject).clone(); 
    if (paramObject instanceof float[])
      return ((float[])paramObject).clone(); 
    if (paramObject instanceof int[])
      return ((int[])paramObject).clone(); 
    if (paramObject instanceof long[])
      return ((long[])paramObject).clone(); 
    if (paramObject instanceof short[])
      return ((short[])paramObject).clone(); 
    throw new AssertionError();
  }
  
  private void validateDescriptor(ObjectStreamClass paramObjectStreamClass) {
    ObjectStreamClassValidator objectStreamClassValidator = this.validator;
    if (objectStreamClassValidator != null)
      objectStreamClassValidator.validateDescriptor(paramObjectStreamClass); 
  }
  
  private static void setValidator(ObjectInputStream paramObjectInputStream, ObjectStreamClassValidator paramObjectStreamClassValidator) { paramObjectInputStream.validator = paramObjectStreamClassValidator; }
  
  static  {
    primClasses.put("boolean", boolean.class);
    primClasses.put("byte", byte.class);
    primClasses.put("char", char.class);
    primClasses.put("short", short.class);
    primClasses.put("int", int.class);
    primClasses.put("long", long.class);
    primClasses.put("float", float.class);
    primClasses.put("double", double.class);
    primClasses.put("void", void.class);
    JavaOISAccess javaOISAccess = new JavaOISAccess() {
        public void setObjectInputFilter(ObjectInputStream param1ObjectInputStream, ObjectInputFilter param1ObjectInputFilter) { param1ObjectInputStream.setInternalObjectInputFilter(param1ObjectInputFilter); }
        
        public ObjectInputFilter getObjectInputFilter(ObjectInputStream param1ObjectInputStream) { return param1ObjectInputStream.getInternalObjectInputFilter(); }
        
        public void checkArray(ObjectInputStream param1ObjectInputStream, Class<?> param1Class, int param1Int) throws InvalidClassException { param1ObjectInputStream.checkArray(param1Class, param1Int); }
      };
    SharedSecrets.setJavaOISAccess(javaOISAccess);
    SharedSecrets.setJavaObjectInputStreamAccess(ObjectInputStream::setValidator);
  }
  
  private class BlockDataInputStream extends InputStream implements DataInput {
    private static final int MAX_BLOCK_SIZE = 1024;
    
    private static final int MAX_HEADER_SIZE = 5;
    
    private static final int CHAR_BUF_SIZE = 256;
    
    private static final int HEADER_BLOCKED = -2;
    
    private final byte[] buf = new byte[1024];
    
    private final byte[] hbuf = new byte[5];
    
    private final char[] cbuf = new char[256];
    
    private boolean blkmode = false;
    
    private int pos = 0;
    
    private int end = -1;
    
    private int unread = 0;
    
    private final ObjectInputStream.PeekInputStream in;
    
    private final DataInputStream din;
    
    BlockDataInputStream(InputStream param1InputStream) {
      this.in = new ObjectInputStream.PeekInputStream(param1InputStream);
      this.din = new DataInputStream(this);
    }
    
    boolean setBlockDataMode(boolean param1Boolean) throws SecurityException {
      if (this.blkmode == param1Boolean)
        return this.blkmode; 
      if (param1Boolean) {
        this.pos = 0;
        this.end = 0;
        this.unread = 0;
      } else if (this.pos < this.end) {
        throw new IllegalStateException("unread block data");
      } 
      this.blkmode = param1Boolean;
      return !this.blkmode;
    }
    
    boolean getBlockDataMode() throws IOException { return this.blkmode; }
    
    void skipBlockData() throws IOException, SecurityException {
      if (!this.blkmode)
        throw new IllegalStateException("not in block data mode"); 
      while (this.end >= 0)
        refill(); 
    }
    
    private int readBlockHeader(boolean param1Boolean) throws IOException {
      if (ObjectInputStream.this.defaultDataEnd)
        return -1; 
      try {
        int i;
        while (true) {
          int k;
          int j = param1Boolean ? Integer.MAX_VALUE : this.in.available();
          if (j == 0)
            return -2; 
          i = this.in.peek();
          switch (i) {
            case 119:
              if (j < 2)
                return -2; 
              this.in.readFully(this.hbuf, 0, 2);
              return this.hbuf[1] & 0xFF;
            case 122:
              if (j < 5)
                return -2; 
              this.in.readFully(this.hbuf, 0, 5);
              k = Bits.getInt(this.hbuf, 1);
              if (k < 0)
                throw new StreamCorruptedException("illegal block data header length: " + k); 
              return k;
            case 121:
              this.in.read();
              ObjectInputStream.this.handleReset();
              continue;
          } 
          break;
        } 
        if (i >= 0 && (i < 112 || i > 126))
          throw new StreamCorruptedException(String.format("invalid type code: %02X", new Object[] { Integer.valueOf(i) })); 
        return -1;
      } catch (EOFException eOFException) {
        throw new StreamCorruptedException("unexpected EOF while reading block data header");
      } 
    }
    
    private void refill() throws IOException, SecurityException {
      try {
        do {
          this.pos = 0;
          if (this.unread > 0) {
            int i = this.in.read(this.buf, 0, Math.min(this.unread, 1024));
            if (i >= 0) {
              this.end = i;
              this.unread -= i;
            } else {
              throw new StreamCorruptedException("unexpected EOF in middle of data block");
            } 
          } else {
            int i = readBlockHeader(true);
            if (i >= 0) {
              this.end = 0;
              this.unread = i;
            } else {
              this.end = -1;
              this.unread = 0;
            } 
          } 
        } while (this.pos == this.end);
      } catch (IOException iOException) {
        this.pos = 0;
        this.end = -1;
        this.unread = 0;
        throw iOException;
      } 
    }
    
    int currentBlockRemaining() throws IOException {
      if (this.blkmode)
        return (this.end >= 0) ? (this.end - this.pos + this.unread) : 0; 
      throw new IllegalStateException();
    }
    
    int peek() throws IOException {
      if (this.blkmode) {
        if (this.pos == this.end)
          refill(); 
        return (this.end >= 0) ? (this.buf[this.pos] & 0xFF) : -1;
      } 
      return this.in.peek();
    }
    
    byte peekByte() throws IOException {
      int i = peek();
      if (i < 0)
        throw new EOFException(); 
      return (byte)i;
    }
    
    public int read() throws IOException {
      if (this.blkmode) {
        if (this.pos == this.end)
          refill(); 
        return (this.end >= 0) ? (this.buf[this.pos++] & 0xFF) : -1;
      } 
      return this.in.read();
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException { return read(param1ArrayOfByte, param1Int1, param1Int2, false); }
    
    public long skip(long param1Long) throws IOException {
      long l;
      for (l = param1Long; l > 0L; l -= i) {
        if (this.blkmode) {
          if (this.pos == this.end)
            refill(); 
          if (this.end < 0)
            break; 
          int j = (int)Math.min(l, (this.end - this.pos));
          l -= j;
          this.pos += j;
          continue;
        } 
        int i = (int)Math.min(l, 1024L);
        if ((i = this.in.read(this.buf, 0, i)) < 0)
          break; 
      } 
      return param1Long - l;
    }
    
    public int available() throws IOException {
      if (this.blkmode) {
        if (this.pos == this.end && this.unread == 0) {
          int j;
          while ((j = readBlockHeader(false)) == 0);
          switch (j) {
            case -2:
              break;
            case -1:
              this.pos = 0;
              this.end = -1;
              break;
            default:
              this.pos = 0;
              this.end = 0;
              this.unread = j;
              break;
          } 
        } 
        int i = (this.unread > 0) ? Math.min(this.in.available(), this.unread) : 0;
        return (this.end >= 0) ? (this.end - this.pos + i) : 0;
      } 
      return this.in.available();
    }
    
    public void close() throws IOException, SecurityException {
      if (this.blkmode) {
        this.pos = 0;
        this.end = -1;
        this.unread = 0;
      } 
      this.in.close();
    }
    
    int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, boolean param1Boolean) throws IOException {
      if (param1Int2 == 0)
        return 0; 
      if (this.blkmode) {
        if (this.pos == this.end)
          refill(); 
        if (this.end < 0)
          return -1; 
        int i = Math.min(param1Int2, this.end - this.pos);
        System.arraycopy(this.buf, this.pos, param1ArrayOfByte, param1Int1, i);
        this.pos += i;
        return i;
      } 
      if (param1Boolean) {
        int i = this.in.read(this.buf, 0, Math.min(param1Int2, 1024));
        if (i > 0)
          System.arraycopy(this.buf, 0, param1ArrayOfByte, param1Int1, i); 
        return i;
      } 
      return this.in.read(param1ArrayOfByte, param1Int1, param1Int2);
    }
    
    public void readFully(byte[] param1ArrayOfByte) throws IOException { readFully(param1ArrayOfByte, 0, param1ArrayOfByte.length, false); }
    
    public void readFully(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException { readFully(param1ArrayOfByte, param1Int1, param1Int2, false); }
    
    public void readFully(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, boolean param1Boolean) throws IOException {
      while (param1Int2 > 0) {
        int i = read(param1ArrayOfByte, param1Int1, param1Int2, param1Boolean);
        if (i < 0)
          throw new EOFException(); 
        param1Int1 += i;
        param1Int2 -= i;
      } 
    }
    
    public int skipBytes(int param1Int) throws IOException { return this.din.skipBytes(param1Int); }
    
    public boolean readBoolean() throws IOException {
      int i = read();
      if (i < 0)
        throw new EOFException(); 
      return (i != 0);
    }
    
    public byte readByte() throws IOException {
      int i = read();
      if (i < 0)
        throw new EOFException(); 
      return (byte)i;
    }
    
    public int readUnsignedByte() throws IOException {
      int i = read();
      if (i < 0)
        throw new EOFException(); 
      return i;
    }
    
    public char readChar() throws IOException {
      if (!this.blkmode) {
        this.pos = 0;
        this.in.readFully(this.buf, 0, 2);
      } else if (this.end - this.pos < 2) {
        return this.din.readChar();
      } 
      char c = Bits.getChar(this.buf, this.pos);
      this.pos += 2;
      return c;
    }
    
    public short readShort() throws IOException {
      if (!this.blkmode) {
        this.pos = 0;
        this.in.readFully(this.buf, 0, 2);
      } else if (this.end - this.pos < 2) {
        return this.din.readShort();
      } 
      short s = Bits.getShort(this.buf, this.pos);
      this.pos += 2;
      return s;
    }
    
    public int readUnsignedShort() throws IOException {
      if (!this.blkmode) {
        this.pos = 0;
        this.in.readFully(this.buf, 0, 2);
      } else if (this.end - this.pos < 2) {
        return this.din.readUnsignedShort();
      } 
      short s = Bits.getShort(this.buf, this.pos) & 0xFFFF;
      this.pos += 2;
      return s;
    }
    
    public int readInt() throws IOException {
      if (!this.blkmode) {
        this.pos = 0;
        this.in.readFully(this.buf, 0, 4);
      } else if (this.end - this.pos < 4) {
        return this.din.readInt();
      } 
      int i = Bits.getInt(this.buf, this.pos);
      this.pos += 4;
      return i;
    }
    
    public float readFloat() throws IOException {
      if (!this.blkmode) {
        this.pos = 0;
        this.in.readFully(this.buf, 0, 4);
      } else if (this.end - this.pos < 4) {
        return this.din.readFloat();
      } 
      float f = Bits.getFloat(this.buf, this.pos);
      this.pos += 4;
      return f;
    }
    
    public long readLong() throws IOException {
      if (!this.blkmode) {
        this.pos = 0;
        this.in.readFully(this.buf, 0, 8);
      } else if (this.end - this.pos < 8) {
        return this.din.readLong();
      } 
      long l = Bits.getLong(this.buf, this.pos);
      this.pos += 8;
      return l;
    }
    
    public double readDouble() throws IOException {
      if (!this.blkmode) {
        this.pos = 0;
        this.in.readFully(this.buf, 0, 8);
      } else if (this.end - this.pos < 8) {
        return this.din.readDouble();
      } 
      double d = Bits.getDouble(this.buf, this.pos);
      this.pos += 8;
      return d;
    }
    
    public String readUTF() throws IOException { return readUTFBody(readUnsignedShort()); }
    
    public String readLine() throws IOException { return this.din.readLine(); }
    
    void readBooleans(boolean[] param1ArrayOfBoolean, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        int j;
        if (!this.blkmode) {
          int k = Math.min(i - param1Int1, 1024);
          this.in.readFully(this.buf, 0, k);
          j = param1Int1 + k;
          this.pos = 0;
        } else {
          if (this.end - this.pos < 1) {
            param1ArrayOfBoolean[param1Int1++] = this.din.readBoolean();
            continue;
          } 
          j = Math.min(i, param1Int1 + this.end - this.pos);
        } 
        while (param1Int1 < j)
          param1ArrayOfBoolean[param1Int1++] = Bits.getBoolean(this.buf, this.pos++); 
      } 
    }
    
    void readChars(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        int j;
        if (!this.blkmode) {
          int k = Math.min(i - param1Int1, 512);
          this.in.readFully(this.buf, 0, k << 1);
          j = param1Int1 + k;
          this.pos = 0;
        } else {
          if (this.end - this.pos < 2) {
            param1ArrayOfChar[param1Int1++] = this.din.readChar();
            continue;
          } 
          j = Math.min(i, param1Int1 + (this.end - this.pos >> 1));
        } 
        while (param1Int1 < j) {
          param1ArrayOfChar[param1Int1++] = Bits.getChar(this.buf, this.pos);
          this.pos += 2;
        } 
      } 
    }
    
    void readShorts(short[] param1ArrayOfShort, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        int j;
        if (!this.blkmode) {
          int k = Math.min(i - param1Int1, 512);
          this.in.readFully(this.buf, 0, k << 1);
          j = param1Int1 + k;
          this.pos = 0;
        } else {
          if (this.end - this.pos < 2) {
            param1ArrayOfShort[param1Int1++] = this.din.readShort();
            continue;
          } 
          j = Math.min(i, param1Int1 + (this.end - this.pos >> 1));
        } 
        while (param1Int1 < j) {
          param1ArrayOfShort[param1Int1++] = Bits.getShort(this.buf, this.pos);
          this.pos += 2;
        } 
      } 
    }
    
    void readInts(int[] param1ArrayOfInt, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        int j;
        if (!this.blkmode) {
          int k = Math.min(i - param1Int1, 256);
          this.in.readFully(this.buf, 0, k << 2);
          j = param1Int1 + k;
          this.pos = 0;
        } else {
          if (this.end - this.pos < 4) {
            param1ArrayOfInt[param1Int1++] = this.din.readInt();
            continue;
          } 
          j = Math.min(i, param1Int1 + (this.end - this.pos >> 2));
        } 
        while (param1Int1 < j) {
          param1ArrayOfInt[param1Int1++] = Bits.getInt(this.buf, this.pos);
          this.pos += 4;
        } 
      } 
    }
    
    void readFloats(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        int j;
        if (!this.blkmode) {
          j = Math.min(i - param1Int1, 256);
          this.in.readFully(this.buf, 0, j << 2);
          this.pos = 0;
        } else {
          if (this.end - this.pos < 4) {
            param1ArrayOfFloat[param1Int1++] = this.din.readFloat();
            continue;
          } 
          j = Math.min(i - param1Int1, this.end - this.pos >> 2);
        } 
        ObjectInputStream.bytesToFloats(this.buf, this.pos, param1ArrayOfFloat, param1Int1, j);
        param1Int1 += j;
        this.pos += (j << 2);
      } 
    }
    
    void readLongs(long[] param1ArrayOfLong, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        int j;
        if (!this.blkmode) {
          int k = Math.min(i - param1Int1, 128);
          this.in.readFully(this.buf, 0, k << 3);
          j = param1Int1 + k;
          this.pos = 0;
        } else {
          if (this.end - this.pos < 8) {
            param1ArrayOfLong[param1Int1++] = this.din.readLong();
            continue;
          } 
          j = Math.min(i, param1Int1 + (this.end - this.pos >> 3));
        } 
        while (param1Int1 < j) {
          param1ArrayOfLong[param1Int1++] = Bits.getLong(this.buf, this.pos);
          this.pos += 8;
        } 
      } 
    }
    
    void readDoubles(double[] param1ArrayOfDouble, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        int j;
        if (!this.blkmode) {
          j = Math.min(i - param1Int1, 128);
          this.in.readFully(this.buf, 0, j << 3);
          this.pos = 0;
        } else {
          if (this.end - this.pos < 8) {
            param1ArrayOfDouble[param1Int1++] = this.din.readDouble();
            continue;
          } 
          j = Math.min(i - param1Int1, this.end - this.pos >> 3);
        } 
        ObjectInputStream.bytesToDoubles(this.buf, this.pos, param1ArrayOfDouble, param1Int1, j);
        param1Int1 += j;
        this.pos += (j << 3);
      } 
    }
    
    String readLongUTF() throws IOException { return readUTFBody(readLong()); }
    
    private String readUTFBody(long param1Long) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();
      if (!this.blkmode)
        this.end = this.pos = 0; 
      while (param1Long > 0L) {
        int i = this.end - this.pos;
        if (i >= 3 || i == param1Long) {
          param1Long -= readUTFSpan(stringBuilder, param1Long);
          continue;
        } 
        if (this.blkmode) {
          param1Long -= readUTFChar(stringBuilder, param1Long);
          continue;
        } 
        if (i > 0)
          System.arraycopy(this.buf, this.pos, this.buf, 0, i); 
        this.pos = 0;
        this.end = (int)Math.min(1024L, param1Long);
        this.in.readFully(this.buf, i, this.end - i);
      } 
      return stringBuilder.toString();
    }
    
    private long readUTFSpan(StringBuilder param1StringBuilder, long param1Long) throws IOException {
      byte b = 0;
      i = this.pos;
      int j = Math.min(this.end - this.pos, 256);
      int k = this.pos + ((param1Long > j) ? (j - 2) : (int)param1Long);
      bool = false;
      try {
        while (this.pos < k) {
          byte b3;
          byte b2;
          byte b1 = this.buf[this.pos++] & 0xFF;
          switch (b1 >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
              this.cbuf[b++] = (char)b1;
              continue;
            case 12:
            case 13:
              b2 = this.buf[this.pos++];
              if ((b2 & 0xC0) != 128)
                throw new UTFDataFormatException(); 
              this.cbuf[b++] = (char)((b1 & 0x1F) << 6 | (b2 & 0x3F) << 0);
              continue;
            case 14:
              b3 = this.buf[this.pos + 1];
              b2 = this.buf[this.pos + 0];
              this.pos += 2;
              if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128)
                throw new UTFDataFormatException(); 
              this.cbuf[b++] = (char)((b1 & 0xF) << 12 | (b2 & 0x3F) << 6 | (b3 & 0x3F) << 0);
              continue;
          } 
          throw new UTFDataFormatException();
        } 
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        bool = true;
      } finally {
        if (bool || (this.pos - i) > param1Long) {
          this.pos = i + (int)param1Long;
          throw new UTFDataFormatException();
        } 
      } 
      param1StringBuilder.append(this.cbuf, 0, b);
      return (this.pos - i);
    }
    
    private int readUTFChar(StringBuilder param1StringBuilder, long param1Long) throws IOException {
      byte b3;
      byte b2;
      byte b1 = readByte() & 0xFF;
      switch (b1 >> 4) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
          param1StringBuilder.append((char)b1);
          return 1;
        case 12:
        case 13:
          if (param1Long < 2L)
            throw new UTFDataFormatException(); 
          b2 = readByte();
          if ((b2 & 0xC0) != 128)
            throw new UTFDataFormatException(); 
          param1StringBuilder.append((char)((b1 & 0x1F) << 6 | (b2 & 0x3F) << 0));
          return 2;
        case 14:
          if (param1Long < 3L) {
            if (param1Long == 2L)
              readByte(); 
            throw new UTFDataFormatException();
          } 
          b2 = readByte();
          b3 = readByte();
          if ((b2 & 0xC0) != 128 || (b3 & 0xC0) != 128)
            throw new UTFDataFormatException(); 
          param1StringBuilder.append((char)((b1 & 0xF) << 12 | (b2 & 0x3F) << 6 | (b3 & 0x3F) << 0));
          return 3;
      } 
      throw new UTFDataFormatException();
    }
    
    long getBytesRead() throws IOException { return this.in.getBytesRead(); }
  }
  
  private static class Caches {
    static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
    
    static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
  }
  
  static class FilterValues implements ObjectInputFilter.FilterInfo {
    final Class<?> clazz;
    
    final long arrayLength;
    
    final long totalObjectRefs;
    
    final long depth;
    
    final long streamBytes;
    
    public FilterValues(Class<?> param1Class, long param1Long1, long param1Long2, long param1Long3, long param1Long4) {
      this.clazz = param1Class;
      this.arrayLength = param1Long1;
      this.totalObjectRefs = param1Long2;
      this.depth = param1Long3;
      this.streamBytes = param1Long4;
    }
    
    public Class<?> serialClass() { return this.clazz; }
    
    public long arrayLength() throws IOException { return this.arrayLength; }
    
    public long references() throws IOException { return this.totalObjectRefs; }
    
    public long depth() throws IOException { return this.depth; }
    
    public long streamBytes() throws IOException { return this.streamBytes; }
  }
  
  public static abstract class GetField {
    public abstract ObjectStreamClass getObjectStreamClass() throws IOException, ClassNotFoundException;
    
    public abstract boolean defaulted(String param1String) throws IOException;
    
    public abstract boolean get(String param1String, boolean param1Boolean) throws IOException;
    
    public abstract byte get(String param1String, byte param1Byte) throws IOException;
    
    public abstract char get(String param1String, char param1Char) throws IOException;
    
    public abstract short get(String param1String, short param1Short) throws IOException;
    
    public abstract int get(String param1String, int param1Int) throws IOException;
    
    public abstract long get(String param1String, long param1Long) throws IOException;
    
    public abstract float get(String param1String, float param1Float) throws IOException;
    
    public abstract double get(String param1String, double param1Double) throws IOException;
    
    public abstract Object get(String param1String, Object param1Object) throws IOException;
  }
  
  private class GetFieldImpl extends GetField {
    private final ObjectStreamClass desc;
    
    private final byte[] primVals;
    
    private final Object[] objVals;
    
    private final int[] objHandles;
    
    GetFieldImpl(ObjectStreamClass param1ObjectStreamClass) {
      this.desc = param1ObjectStreamClass;
      this.primVals = new byte[param1ObjectStreamClass.getPrimDataSize()];
      this.objVals = new Object[param1ObjectStreamClass.getNumObjFields()];
      this.objHandles = new int[this.objVals.length];
    }
    
    public ObjectStreamClass getObjectStreamClass() throws IOException, ClassNotFoundException { return this.desc; }
    
    public boolean defaulted(String param1String) throws IOException { return (getFieldOffset(param1String, null) < 0); }
    
    public boolean get(String param1String, boolean param1Boolean) throws IOException {
      int i = getFieldOffset(param1String, boolean.class);
      return (i >= 0) ? Bits.getBoolean(this.primVals, i) : param1Boolean;
    }
    
    public byte get(String param1String, byte param1Byte) throws IOException {
      int i = getFieldOffset(param1String, byte.class);
      return (i >= 0) ? this.primVals[i] : param1Byte;
    }
    
    public char get(String param1String, char param1Char) throws IOException {
      int i = getFieldOffset(param1String, char.class);
      return (i >= 0) ? Bits.getChar(this.primVals, i) : param1Char;
    }
    
    public short get(String param1String, short param1Short) throws IOException {
      int i = getFieldOffset(param1String, short.class);
      return (i >= 0) ? Bits.getShort(this.primVals, i) : param1Short;
    }
    
    public int get(String param1String, int param1Int) throws IOException {
      int i = getFieldOffset(param1String, int.class);
      return (i >= 0) ? Bits.getInt(this.primVals, i) : param1Int;
    }
    
    public float get(String param1String, float param1Float) throws IOException {
      int i = getFieldOffset(param1String, float.class);
      return (i >= 0) ? Bits.getFloat(this.primVals, i) : param1Float;
    }
    
    public long get(String param1String, long param1Long) throws IOException {
      int i = getFieldOffset(param1String, long.class);
      return (i >= 0) ? Bits.getLong(this.primVals, i) : param1Long;
    }
    
    public double get(String param1String, double param1Double) throws IOException {
      int i = getFieldOffset(param1String, double.class);
      return (i >= 0) ? Bits.getDouble(this.primVals, i) : param1Double;
    }
    
    public Object get(String param1String, Object param1Object) throws IOException {
      int i = getFieldOffset(param1String, Object.class);
      if (i >= 0) {
        int j = this.objHandles[i];
        ObjectInputStream.this.handles.markDependency(ObjectInputStream.this.passHandle, j);
        return (ObjectInputStream.this.handles.lookupException(j) == null) ? this.objVals[i] : null;
      } 
      return param1Object;
    }
    
    void readFields() throws IOException, SecurityException {
      ObjectInputStream.this.bin.readFully(this.primVals, 0, this.primVals.length, false);
      int i = ObjectInputStream.this.passHandle;
      ObjectStreamField[] arrayOfObjectStreamField = this.desc.getFields(false);
      int j = arrayOfObjectStreamField.length - this.objVals.length;
      for (int k = 0; k < this.objVals.length; k++) {
        this.objVals[k] = ObjectInputStream.this.readObject0(arrayOfObjectStreamField[j + k].isUnshared());
        this.objHandles[k] = ObjectInputStream.this.passHandle;
      } 
      ObjectInputStream.this.passHandle = i;
    }
    
    private int getFieldOffset(String param1String, Class<?> param1Class) {
      ObjectStreamField objectStreamField = this.desc.getField(param1String, param1Class);
      if (objectStreamField != null)
        return objectStreamField.getOffset(); 
      if (this.desc.getLocalDesc().getField(param1String, param1Class) != null)
        return -1; 
      throw new IllegalArgumentException("no such field " + param1String + " with type " + param1Class);
    }
  }
  
  private static class HandleTable {
    private static final byte STATUS_OK = 1;
    
    private static final byte STATUS_UNKNOWN = 2;
    
    private static final byte STATUS_EXCEPTION = 3;
    
    byte[] status;
    
    Object[] entries;
    
    HandleList[] deps;
    
    int lowDep = -1;
    
    int size = 0;
    
    HandleTable(int param1Int) {
      this.status = new byte[param1Int];
      this.entries = new Object[param1Int];
      this.deps = new HandleList[param1Int];
    }
    
    int assign(Object param1Object) {
      if (this.size >= this.entries.length)
        grow(); 
      this.status[this.size] = 2;
      this.entries[this.size] = param1Object;
      return this.size++;
    }
    
    void markDependency(int param1Int1, int param1Int2) {
      if (param1Int1 == -1 || param1Int2 == -1)
        return; 
      switch (this.status[param1Int1]) {
        case 2:
          switch (this.status[param1Int2]) {
            case 1:
              return;
            case 3:
              markException(param1Int1, (ClassNotFoundException)this.entries[param1Int2]);
            case 2:
              if (this.deps[param1Int2] == null)
                this.deps[param1Int2] = new HandleList(); 
              this.deps[param1Int2].add(param1Int1);
              if (this.lowDep < 0 || this.lowDep > param1Int2)
                this.lowDep = param1Int2; 
          } 
          throw new InternalError();
        case 3:
        
      } 
      throw new InternalError();
    }
    
    void markException(int param1Int, ClassNotFoundException param1ClassNotFoundException) {
      HandleList handleList;
      switch (this.status[param1Int]) {
        case 2:
          this.status[param1Int] = 3;
          this.entries[param1Int] = param1ClassNotFoundException;
          handleList = this.deps[param1Int];
          if (handleList != null) {
            int i = handleList.size();
            for (byte b = 0; b < i; b++)
              markException(handleList.get(b), param1ClassNotFoundException); 
            this.deps[param1Int] = null;
          } 
        case 3:
          return;
      } 
      throw new InternalError();
    }
    
    void finish(int param1Int) {
      int i;
      if (this.lowDep < 0) {
        i = param1Int + 1;
      } else if (this.lowDep >= param1Int) {
        i = this.size;
        this.lowDep = -1;
      } else {
        return;
      } 
      for (int j = param1Int; j < i; j++) {
        switch (this.status[j]) {
          case 2:
            this.status[j] = 1;
            this.deps[j] = null;
            break;
          case 1:
          case 3:
            break;
          default:
            throw new InternalError();
        } 
      } 
    }
    
    void setObject(int param1Int, Object param1Object) {
      switch (this.status[param1Int]) {
        case 1:
        case 2:
          this.entries[param1Int] = param1Object;
        case 3:
          return;
      } 
      throw new InternalError();
    }
    
    Object lookupObject(int param1Int) { return (param1Int != -1 && this.status[param1Int] != 3) ? this.entries[param1Int] : null; }
    
    ClassNotFoundException lookupException(int param1Int) { return (param1Int != -1 && this.status[param1Int] == 3) ? (ClassNotFoundException)this.entries[param1Int] : null; }
    
    void clear() throws IOException, SecurityException {
      Arrays.fill(this.status, 0, this.size, (byte)0);
      Arrays.fill(this.entries, 0, this.size, null);
      Arrays.fill(this.deps, 0, this.size, null);
      this.lowDep = -1;
      this.size = 0;
    }
    
    int size() throws IOException { return this.size; }
    
    private void grow() throws IOException, SecurityException {
      int i = (this.entries.length << 1) + 1;
      byte[] arrayOfByte = new byte[i];
      Object[] arrayOfObject = new Object[i];
      HandleList[] arrayOfHandleList = new HandleList[i];
      System.arraycopy(this.status, 0, arrayOfByte, 0, this.size);
      System.arraycopy(this.entries, 0, arrayOfObject, 0, this.size);
      System.arraycopy(this.deps, 0, arrayOfHandleList, 0, this.size);
      this.status = arrayOfByte;
      this.entries = arrayOfObject;
      this.deps = arrayOfHandleList;
    }
    
    private static class HandleList {
      private int[] list = new int[4];
      
      private int size = 0;
      
      public void add(int param2Int) {
        if (this.size >= this.list.length) {
          int[] arrayOfInt = new int[this.list.length << 1];
          System.arraycopy(this.list, 0, arrayOfInt, 0, this.list.length);
          this.list = arrayOfInt;
        } 
        this.list[this.size++] = param2Int;
      }
      
      public int get(int param2Int) throws IOException {
        if (param2Int >= this.size)
          throw new ArrayIndexOutOfBoundsException(); 
        return this.list[param2Int];
      }
      
      public int size() throws IOException { return this.size; }
    }
  }
  
  private static class Logging {
    private static final PlatformLogger traceLogger;
    
    private static final PlatformLogger infoLogger;
    
    static  {
      PlatformLogger platformLogger = PlatformLogger.getLogger("java.io.serialization");
      infoLogger = (platformLogger != null && platformLogger.isLoggable(PlatformLogger.Level.INFO)) ? platformLogger : null;
      traceLogger = (platformLogger != null && platformLogger.isLoggable(PlatformLogger.Level.FINER)) ? platformLogger : null;
    }
  }
  
  private static class PeekInputStream extends InputStream {
    private final InputStream in;
    
    private int peekb = -1;
    
    private long totalBytesRead = 0L;
    
    PeekInputStream(InputStream param1InputStream) throws IOException { this.in = param1InputStream; }
    
    int peek() throws IOException {
      if (this.peekb >= 0)
        return this.peekb; 
      this.peekb = this.in.read();
      this.totalBytesRead += ((this.peekb >= 0) ? 1L : 0L);
      return this.peekb;
    }
    
    public int read() throws IOException {
      if (this.peekb >= 0) {
        int j = this.peekb;
        this.peekb = -1;
        return j;
      } 
      int i = this.in.read();
      this.totalBytesRead += ((i >= 0) ? 1L : 0L);
      return i;
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      if (param1Int2 == 0)
        return 0; 
      if (this.peekb < 0) {
        int j = this.in.read(param1ArrayOfByte, param1Int1, param1Int2);
        this.totalBytesRead += ((j >= 0) ? j : 0L);
        return j;
      } 
      param1ArrayOfByte[param1Int1++] = (byte)this.peekb;
      param1Int2--;
      this.peekb = -1;
      int i = this.in.read(param1ArrayOfByte, param1Int1, param1Int2);
      this.totalBytesRead += ((i >= 0) ? i : 0L);
      return (i >= 0) ? (i + 1) : 1;
    }
    
    void readFully(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i;
      for (i = 0; i < param1Int2; i += j) {
        int j = read(param1ArrayOfByte, param1Int1 + i, param1Int2 - i);
        if (j < 0)
          throw new EOFException(); 
      } 
    }
    
    public long skip(long param1Long) throws IOException {
      if (param1Long <= 0L)
        return 0L; 
      byte b = 0;
      if (this.peekb >= 0) {
        this.peekb = -1;
        b++;
        param1Long--;
      } 
      param1Long = b + this.in.skip(param1Long);
      this.totalBytesRead += param1Long;
      return param1Long;
    }
    
    public int available() throws IOException { return this.in.available() + ((this.peekb >= 0) ? 1 : 0); }
    
    public void close() throws IOException, SecurityException { this.in.close(); }
    
    public long getBytesRead() throws IOException { return this.totalBytesRead; }
  }
  
  private static class ValidationList {
    private Callback list;
    
    void register(ObjectInputValidation param1ObjectInputValidation, int param1Int) throws NotActiveException, InvalidObjectException {
      if (param1ObjectInputValidation == null)
        throw new InvalidObjectException("null callback"); 
      Callback callback1 = null;
      Callback callback2;
      for (callback2 = this.list; callback2 != null && param1Int < callback2.priority; callback2 = callback2.next)
        callback1 = callback2; 
      AccessControlContext accessControlContext = AccessController.getContext();
      if (callback1 != null) {
        callback1.next = new Callback(param1ObjectInputValidation, param1Int, callback2, accessControlContext);
      } else {
        this.list = new Callback(param1ObjectInputValidation, param1Int, this.list, accessControlContext);
      } 
    }
    
    void doCallbacks() throws IOException, SecurityException {
      try {
        while (this.list != null) {
          AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                public Void run() throws InvalidObjectException {
                  this.this$0.list.obj.validateObject();
                  return null;
                }
              },  this.list.acc);
          this.list = this.list.next;
        } 
      } catch (PrivilegedActionException privilegedActionException) {
        this.list = null;
        throw (InvalidObjectException)privilegedActionException.getException();
      } 
    }
    
    public void clear() throws IOException, SecurityException { this.list = null; }
    
    private static class Callback {
      final ObjectInputValidation obj;
      
      final int priority;
      
      Callback next;
      
      final AccessControlContext acc;
      
      Callback(ObjectInputValidation param2ObjectInputValidation, int param2Int, Callback param2Callback, AccessControlContext param2AccessControlContext) {
        this.obj = param2ObjectInputValidation;
        this.priority = param2Int;
        this.next = param2Callback;
        this.acc = param2AccessControlContext;
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */