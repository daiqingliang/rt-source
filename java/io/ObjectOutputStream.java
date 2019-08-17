package java.io;

import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetBooleanAction;

public class ObjectOutputStream extends OutputStream implements ObjectOutput, ObjectStreamConstants {
  private final BlockDataOutputStream bout;
  
  private final HandleTable handles;
  
  private final ReplaceTable subs;
  
  private int protocol = 2;
  
  private int depth;
  
  private byte[] primVals;
  
  private final boolean enableOverride;
  
  private boolean enableReplace;
  
  private SerialCallbackContext curContext;
  
  private PutFieldImpl curPut;
  
  private final DebugTraceInfoStack debugInfoStack;
  
  private static final boolean extendedDebugInfo = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.io.serialization.extendedDebugInfo"))).booleanValue();
  
  public ObjectOutputStream(OutputStream paramOutputStream) throws IOException {
    verifySubclass();
    this.bout = new BlockDataOutputStream(paramOutputStream);
    this.handles = new HandleTable(10, 3.0F);
    this.subs = new ReplaceTable(10, 3.0F);
    this.enableOverride = false;
    writeStreamHeader();
    this.bout.setBlockDataMode(true);
    if (extendedDebugInfo) {
      this.debugInfoStack = new DebugTraceInfoStack();
    } else {
      this.debugInfoStack = null;
    } 
  }
  
  protected ObjectOutputStream() throws IOException, SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION); 
    this.bout = null;
    this.handles = null;
    this.subs = null;
    this.enableOverride = true;
    this.debugInfoStack = null;
  }
  
  public void useProtocolVersion(int paramInt) throws IOException {
    if (this.handles.size() != 0)
      throw new IllegalStateException("stream non-empty"); 
    switch (paramInt) {
      case 1:
      case 2:
        this.protocol = paramInt;
        return;
    } 
    throw new IllegalArgumentException("unknown version: " + paramInt);
  }
  
  public final void writeObject(Object paramObject) throws IOException {
    if (this.enableOverride) {
      writeObjectOverride(paramObject);
      return;
    } 
    try {
      writeObject0(paramObject, false);
    } catch (IOException iOException) {
      if (this.depth == 0)
        writeFatalException(iOException); 
      throw iOException;
    } 
  }
  
  protected void writeObjectOverride(Object paramObject) throws IOException {}
  
  public void writeUnshared(Object paramObject) throws IOException {
    try {
      writeObject0(paramObject, true);
    } catch (IOException iOException) {
      if (this.depth == 0)
        writeFatalException(iOException); 
      throw iOException;
    } 
  }
  
  public void defaultWriteObject() throws IOException, SecurityException {
    SerialCallbackContext serialCallbackContext = this.curContext;
    if (serialCallbackContext == null)
      throw new NotActiveException("not in call to writeObject"); 
    Object object = serialCallbackContext.getObj();
    ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
    this.bout.setBlockDataMode(false);
    defaultWriteFields(object, objectStreamClass);
    this.bout.setBlockDataMode(true);
  }
  
  public PutField putFields() throws IOException {
    if (this.curPut == null) {
      SerialCallbackContext serialCallbackContext = this.curContext;
      if (serialCallbackContext == null)
        throw new NotActiveException("not in call to writeObject"); 
      Object object = serialCallbackContext.getObj();
      ObjectStreamClass objectStreamClass = serialCallbackContext.getDesc();
      this.curPut = new PutFieldImpl(objectStreamClass);
    } 
    return this.curPut;
  }
  
  public void writeFields() throws IOException, SecurityException {
    if (this.curPut == null)
      throw new NotActiveException("no current PutField object"); 
    this.bout.setBlockDataMode(false);
    this.curPut.writeFields();
    this.bout.setBlockDataMode(true);
  }
  
  public void reset() throws IOException, SecurityException {
    if (this.depth != 0)
      throw new IOException("stream active"); 
    this.bout.setBlockDataMode(false);
    this.bout.writeByte(121);
    clear();
    this.bout.setBlockDataMode(true);
  }
  
  protected void annotateClass(Class<?> paramClass) throws IOException {}
  
  protected void annotateProxyClass(Class<?> paramClass) throws IOException {}
  
  protected Object replaceObject(Object paramObject) throws IOException { return paramObject; }
  
  protected boolean enableReplaceObject(boolean paramBoolean) throws SecurityException {
    if (paramBoolean == this.enableReplace)
      return paramBoolean; 
    if (paramBoolean) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(SUBSTITUTION_PERMISSION); 
    } 
    this.enableReplace = paramBoolean;
    return !this.enableReplace;
  }
  
  protected void writeStreamHeader() throws IOException, SecurityException {
    this.bout.writeShort(-21267);
    this.bout.writeShort(5);
  }
  
  protected void writeClassDescriptor(ObjectStreamClass paramObjectStreamClass) throws IOException { paramObjectStreamClass.writeNonProxy(this); }
  
  public void write(int paramInt) throws IOException { this.bout.write(paramInt); }
  
  public void write(byte[] paramArrayOfByte) throws IOException { this.bout.write(paramArrayOfByte, 0, paramArrayOfByte.length, false); }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    int i = paramInt1 + paramInt2;
    if (paramInt1 < 0 || paramInt2 < 0 || i > paramArrayOfByte.length || i < 0)
      throw new IndexOutOfBoundsException(); 
    this.bout.write(paramArrayOfByte, paramInt1, paramInt2, false);
  }
  
  public void flush() throws IOException, SecurityException { this.bout.flush(); }
  
  protected void drain() throws IOException, SecurityException { this.bout.drain(); }
  
  public void close() throws IOException, SecurityException {
    flush();
    clear();
    this.bout.close();
  }
  
  public void writeBoolean(boolean paramBoolean) throws IOException { this.bout.writeBoolean(paramBoolean); }
  
  public void writeByte(int paramInt) throws IOException { this.bout.writeByte(paramInt); }
  
  public void writeShort(int paramInt) throws IOException { this.bout.writeShort(paramInt); }
  
  public void writeChar(int paramInt) throws IOException { this.bout.writeChar(paramInt); }
  
  public void writeInt(int paramInt) throws IOException { this.bout.writeInt(paramInt); }
  
  public void writeLong(long paramLong) throws IOException { this.bout.writeLong(paramLong); }
  
  public void writeFloat(float paramFloat) throws IOException { this.bout.writeFloat(paramFloat); }
  
  public void writeDouble(double paramDouble) throws IOException { this.bout.writeDouble(paramDouble); }
  
  public void writeBytes(String paramString) throws IOException { this.bout.writeBytes(paramString); }
  
  public void writeChars(String paramString) throws IOException { this.bout.writeChars(paramString); }
  
  public void writeUTF(String paramString) throws IOException { this.bout.writeUTF(paramString); }
  
  int getProtocolVersion() { return this.protocol; }
  
  void writeTypeString(String paramString) throws IOException {
    if (paramString == null) {
      writeNull();
    } else {
      int i;
      if ((i = this.handles.lookup(paramString)) != -1) {
        writeHandle(i);
      } else {
        writeString(paramString, false);
      } 
    } 
  }
  
  private void verifySubclass() throws IOException, SecurityException {
    Class clazz = getClass();
    if (clazz == ObjectOutputStream.class)
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
            while (clazz != ObjectOutputStream.class) {
              try {
                clazz.getDeclaredMethod("writeUnshared", new Class[] { Object.class });
                return Boolean.FALSE;
              } catch (NoSuchMethodException noSuchMethodException) {
                try {
                  clazz.getDeclaredMethod("putFields", (Class[])null);
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
    this.subs.clear();
    this.handles.clear();
  }
  
  private void writeObject0(Object paramObject, boolean paramBoolean) throws IOException {
    bool = this.bout.setBlockDataMode(false);
    this.depth++;
    try {
      ObjectStreamClass objectStreamClass;
      if ((paramObject = this.subs.lookup(paramObject)) == null) {
        writeNull();
        return;
      } 
      int i;
      if (!paramBoolean && (i = this.handles.lookup(paramObject)) != -1) {
        writeHandle(i);
        return;
      } 
      if (paramObject instanceof Class) {
        writeClass((Class)paramObject, paramBoolean);
        return;
      } 
      if (paramObject instanceof ObjectStreamClass) {
        writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
        return;
      } 
      Object object = paramObject;
      Class clazz;
      for (clazz = paramObject.getClass();; clazz = clazz1) {
        objectStreamClass = ObjectStreamClass.lookup(clazz, true);
        Class clazz1;
        if (!objectStreamClass.hasWriteReplaceMethod() || (paramObject = objectStreamClass.invokeWriteReplace(paramObject)) == null || (clazz1 = paramObject.getClass()) == clazz)
          break; 
      } 
      if (this.enableReplace) {
        Object object1 = replaceObject(paramObject);
        if (object1 != paramObject && object1 != null) {
          clazz = object1.getClass();
          objectStreamClass = ObjectStreamClass.lookup(clazz, true);
        } 
        paramObject = object1;
      } 
      if (paramObject != object) {
        this.subs.assign(object, paramObject);
        if (paramObject == null) {
          writeNull();
          return;
        } 
        if (!paramBoolean && (i = this.handles.lookup(paramObject)) != -1) {
          writeHandle(i);
          return;
        } 
        if (paramObject instanceof Class) {
          writeClass((Class)paramObject, paramBoolean);
          return;
        } 
        if (paramObject instanceof ObjectStreamClass) {
          writeClassDesc((ObjectStreamClass)paramObject, paramBoolean);
          return;
        } 
      } 
      if (paramObject instanceof String) {
        writeString((String)paramObject, paramBoolean);
      } else if (clazz.isArray()) {
        writeArray(paramObject, objectStreamClass, paramBoolean);
      } else if (paramObject instanceof Enum) {
        writeEnum((Enum)paramObject, objectStreamClass, paramBoolean);
      } else if (paramObject instanceof Serializable) {
        writeOrdinaryObject(paramObject, objectStreamClass, paramBoolean);
      } else {
        if (extendedDebugInfo)
          throw new NotSerializableException(clazz.getName() + "\n" + this.debugInfoStack.toString()); 
        throw new NotSerializableException(clazz.getName());
      } 
    } finally {
      this.depth--;
      this.bout.setBlockDataMode(bool);
    } 
  }
  
  private void writeNull() throws IOException, SecurityException { this.bout.writeByte(112); }
  
  private void writeHandle(int paramInt) throws IOException {
    this.bout.writeByte(113);
    this.bout.writeInt(8257536 + paramInt);
  }
  
  private void writeClass(Class<?> paramClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(118);
    writeClassDesc(ObjectStreamClass.lookup(paramClass, true), false);
    this.handles.assign(paramBoolean ? null : paramClass);
  }
  
  private void writeClassDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    if (paramObjectStreamClass == null) {
      writeNull();
    } else {
      int i;
      if (!paramBoolean && (i = this.handles.lookup(paramObjectStreamClass)) != -1) {
        writeHandle(i);
      } else if (paramObjectStreamClass.isProxy()) {
        writeProxyDesc(paramObjectStreamClass, paramBoolean);
      } else {
        writeNonProxyDesc(paramObjectStreamClass, paramBoolean);
      } 
    } 
  }
  
  private boolean isCustomSubclass() { return (getClass().getClassLoader() != ObjectOutputStream.class.getClassLoader()); }
  
  private void writeProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(125);
    this.handles.assign(paramBoolean ? null : paramObjectStreamClass);
    Class clazz = paramObjectStreamClass.forClass();
    Class[] arrayOfClass = clazz.getInterfaces();
    this.bout.writeInt(arrayOfClass.length);
    for (byte b = 0; b < arrayOfClass.length; b++)
      this.bout.writeUTF(arrayOfClass[b].getName()); 
    this.bout.setBlockDataMode(true);
    if (clazz != null && isCustomSubclass())
      ReflectUtil.checkPackageAccess(clazz); 
    annotateProxyClass(clazz);
    this.bout.setBlockDataMode(false);
    this.bout.writeByte(120);
    writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
  }
  
  private void writeNonProxyDesc(ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(114);
    this.handles.assign(paramBoolean ? null : paramObjectStreamClass);
    if (this.protocol == 1) {
      paramObjectStreamClass.writeNonProxy(this);
    } else {
      writeClassDescriptor(paramObjectStreamClass);
    } 
    Class clazz = paramObjectStreamClass.forClass();
    this.bout.setBlockDataMode(true);
    if (clazz != null && isCustomSubclass())
      ReflectUtil.checkPackageAccess(clazz); 
    annotateClass(clazz);
    this.bout.setBlockDataMode(false);
    this.bout.writeByte(120);
    writeClassDesc(paramObjectStreamClass.getSuperDesc(), false);
  }
  
  private void writeString(String paramString, boolean paramBoolean) throws IOException {
    this.handles.assign(paramBoolean ? null : paramString);
    long l = this.bout.getUTFLength(paramString);
    if (l <= 65535L) {
      this.bout.writeByte(116);
      this.bout.writeUTF(paramString, l);
    } else {
      this.bout.writeByte(124);
      this.bout.writeLongUTF(paramString, l);
    } 
  }
  
  private void writeArray(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(117);
    writeClassDesc(paramObjectStreamClass, false);
    this.handles.assign(paramBoolean ? null : paramObject);
    Class clazz = paramObjectStreamClass.forClass().getComponentType();
    if (clazz.isPrimitive()) {
      if (clazz == int.class) {
        int[] arrayOfInt = (int[])paramObject;
        this.bout.writeInt(arrayOfInt.length);
        this.bout.writeInts(arrayOfInt, 0, arrayOfInt.length);
      } else if (clazz == byte.class) {
        byte[] arrayOfByte = (byte[])paramObject;
        this.bout.writeInt(arrayOfByte.length);
        this.bout.write(arrayOfByte, 0, arrayOfByte.length, true);
      } else if (clazz == long.class) {
        long[] arrayOfLong = (long[])paramObject;
        this.bout.writeInt(arrayOfLong.length);
        this.bout.writeLongs(arrayOfLong, 0, arrayOfLong.length);
      } else if (clazz == float.class) {
        float[] arrayOfFloat = (float[])paramObject;
        this.bout.writeInt(arrayOfFloat.length);
        this.bout.writeFloats(arrayOfFloat, 0, arrayOfFloat.length);
      } else if (clazz == double.class) {
        double[] arrayOfDouble = (double[])paramObject;
        this.bout.writeInt(arrayOfDouble.length);
        this.bout.writeDoubles(arrayOfDouble, 0, arrayOfDouble.length);
      } else if (clazz == short.class) {
        short[] arrayOfShort = (short[])paramObject;
        this.bout.writeInt(arrayOfShort.length);
        this.bout.writeShorts(arrayOfShort, 0, arrayOfShort.length);
      } else if (clazz == char.class) {
        char[] arrayOfChar = (char[])paramObject;
        this.bout.writeInt(arrayOfChar.length);
        this.bout.writeChars(arrayOfChar, 0, arrayOfChar.length);
      } else if (clazz == boolean.class) {
        boolean[] arrayOfBoolean = (boolean[])paramObject;
        this.bout.writeInt(arrayOfBoolean.length);
        this.bout.writeBooleans(arrayOfBoolean, 0, arrayOfBoolean.length);
      } else {
        throw new InternalError();
      } 
    } else {
      Object[] arrayOfObject = (Object[])paramObject;
      int i = arrayOfObject.length;
      this.bout.writeInt(i);
      if (extendedDebugInfo)
        this.debugInfoStack.push("array (class \"" + paramObject.getClass().getName() + "\", size: " + i + ")"); 
      try {
        for (byte b = 0; b < i; b++) {
          if (extendedDebugInfo)
            this.debugInfoStack.push("element of array (index: " + b + ")"); 
          try {
            writeObject0(arrayOfObject[b], false);
          } finally {
            if (extendedDebugInfo)
              this.debugInfoStack.pop(); 
          } 
        } 
      } finally {
        if (extendedDebugInfo)
          this.debugInfoStack.pop(); 
      } 
    } 
  }
  
  private void writeEnum(Enum<?> paramEnum, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    this.bout.writeByte(126);
    ObjectStreamClass objectStreamClass = paramObjectStreamClass.getSuperDesc();
    writeClassDesc((objectStreamClass.forClass() == Enum.class) ? paramObjectStreamClass : objectStreamClass, false);
    this.handles.assign(paramBoolean ? null : paramEnum);
    writeString(paramEnum.name(), false);
  }
  
  private void writeOrdinaryObject(Object paramObject, ObjectStreamClass paramObjectStreamClass, boolean paramBoolean) throws IOException {
    if (extendedDebugInfo)
      this.debugInfoStack.push(((this.depth == 1) ? "root " : "") + "object (class \"" + paramObject.getClass().getName() + "\", " + paramObject.toString() + ")"); 
    try {
      paramObjectStreamClass.checkSerialize();
      this.bout.writeByte(115);
      writeClassDesc(paramObjectStreamClass, false);
      this.handles.assign(paramBoolean ? null : paramObject);
      if (paramObjectStreamClass.isExternalizable() && !paramObjectStreamClass.isProxy()) {
        writeExternalData((Externalizable)paramObject);
      } else {
        writeSerialData(paramObject, paramObjectStreamClass);
      } 
    } finally {
      if (extendedDebugInfo)
        this.debugInfoStack.pop(); 
    } 
  }
  
  private void writeExternalData(Externalizable paramExternalizable) throws IOException {
    PutFieldImpl putFieldImpl = this.curPut;
    this.curPut = null;
    if (extendedDebugInfo)
      this.debugInfoStack.push("writeExternal data"); 
    serialCallbackContext = this.curContext;
    try {
      this.curContext = null;
      if (this.protocol == 1) {
        paramExternalizable.writeExternal(this);
      } else {
        this.bout.setBlockDataMode(true);
        paramExternalizable.writeExternal(this);
        this.bout.setBlockDataMode(false);
        this.bout.writeByte(120);
      } 
    } finally {
      this.curContext = serialCallbackContext;
      if (extendedDebugInfo)
        this.debugInfoStack.pop(); 
    } 
    this.curPut = putFieldImpl;
  }
  
  private void writeSerialData(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    ObjectStreamClass.ClassDataSlot[] arrayOfClassDataSlot = paramObjectStreamClass.getClassDataLayout();
    for (byte b = 0; b < arrayOfClassDataSlot.length; b++) {
      ObjectStreamClass objectStreamClass = (arrayOfClassDataSlot[b]).desc;
      if (objectStreamClass.hasWriteObjectMethod()) {
        PutFieldImpl putFieldImpl = this.curPut;
        this.curPut = null;
        serialCallbackContext = this.curContext;
        if (extendedDebugInfo)
          this.debugInfoStack.push("custom writeObject data (class \"" + objectStreamClass.getName() + "\")"); 
        try {
          this.curContext = new SerialCallbackContext(paramObject, objectStreamClass);
          this.bout.setBlockDataMode(true);
          objectStreamClass.invokeWriteObject(paramObject, this);
          this.bout.setBlockDataMode(false);
          this.bout.writeByte(120);
        } finally {
          this.curContext.setUsed();
          this.curContext = serialCallbackContext;
          if (extendedDebugInfo)
            this.debugInfoStack.pop(); 
        } 
        this.curPut = putFieldImpl;
      } else {
        defaultWriteFields(paramObject, objectStreamClass);
      } 
    } 
  }
  
  private void defaultWriteFields(Object paramObject, ObjectStreamClass paramObjectStreamClass) throws IOException {
    Class clazz = paramObjectStreamClass.forClass();
    if (clazz != null && paramObject != null && !clazz.isInstance(paramObject))
      throw new ClassCastException(); 
    paramObjectStreamClass.checkDefaultSerialize();
    int i = paramObjectStreamClass.getPrimDataSize();
    if (this.primVals == null || this.primVals.length < i)
      this.primVals = new byte[i]; 
    paramObjectStreamClass.getPrimFieldValues(paramObject, this.primVals);
    this.bout.write(this.primVals, 0, i, false);
    ObjectStreamField[] arrayOfObjectStreamField = paramObjectStreamClass.getFields(false);
    Object[] arrayOfObject = new Object[paramObjectStreamClass.getNumObjFields()];
    int j = arrayOfObjectStreamField.length - arrayOfObject.length;
    paramObjectStreamClass.getObjFieldValues(paramObject, arrayOfObject);
    for (int k = 0; k < arrayOfObject.length; k++) {
      if (extendedDebugInfo)
        this.debugInfoStack.push("field (class \"" + paramObjectStreamClass.getName() + "\", name: \"" + arrayOfObjectStreamField[j + k].getName() + "\", type: \"" + arrayOfObjectStreamField[j + k].getType() + "\")"); 
      try {
        writeObject0(arrayOfObject[k], arrayOfObjectStreamField[j + k].isUnshared());
      } finally {
        if (extendedDebugInfo)
          this.debugInfoStack.pop(); 
      } 
    } 
  }
  
  private void writeFatalException(IOException paramIOException) throws IOException {
    clear();
    bool = this.bout.setBlockDataMode(false);
    try {
      this.bout.writeByte(123);
      writeObject0(paramIOException, false);
      clear();
    } finally {
      this.bout.setBlockDataMode(bool);
    } 
  }
  
  private static native void floatsToBytes(float[] paramArrayOfFloat, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static native void doublesToBytes(double[] paramArrayOfDouble, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  
  private static class BlockDataOutputStream extends OutputStream implements DataOutput {
    private static final int MAX_BLOCK_SIZE = 1024;
    
    private static final int MAX_HEADER_SIZE = 5;
    
    private static final int CHAR_BUF_SIZE = 256;
    
    private final byte[] buf = new byte[1024];
    
    private final byte[] hbuf = new byte[5];
    
    private final char[] cbuf = new char[256];
    
    private boolean blkmode = false;
    
    private int pos = 0;
    
    private final OutputStream out;
    
    private final DataOutputStream dout;
    
    BlockDataOutputStream(OutputStream param1OutputStream) throws IOException {
      this.out = param1OutputStream;
      this.dout = new DataOutputStream(this);
    }
    
    boolean setBlockDataMode(boolean param1Boolean) throws SecurityException {
      if (this.blkmode == param1Boolean)
        return this.blkmode; 
      drain();
      this.blkmode = param1Boolean;
      return !this.blkmode;
    }
    
    boolean getBlockDataMode() { return this.blkmode; }
    
    public void write(int param1Int) throws IOException {
      if (this.pos >= 1024)
        drain(); 
      this.buf[this.pos++] = (byte)param1Int;
    }
    
    public void write(byte[] param1ArrayOfByte) throws IOException { write(param1ArrayOfByte, 0, param1ArrayOfByte.length, false); }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException { write(param1ArrayOfByte, param1Int1, param1Int2, false); }
    
    public void flush() throws IOException, SecurityException {
      drain();
      this.out.flush();
    }
    
    public void close() throws IOException, SecurityException {
      flush();
      this.out.close();
    }
    
    void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2, boolean param1Boolean) throws IOException {
      if (!param1Boolean && !this.blkmode) {
        drain();
        this.out.write(param1ArrayOfByte, param1Int1, param1Int2);
        return;
      } 
      while (param1Int2 > 0) {
        if (this.pos >= 1024)
          drain(); 
        if (param1Int2 >= 1024 && !param1Boolean && this.pos == 0) {
          writeBlockHeader(1024);
          this.out.write(param1ArrayOfByte, param1Int1, 1024);
          param1Int1 += 1024;
          param1Int2 -= 1024;
          continue;
        } 
        int i = Math.min(param1Int2, 1024 - this.pos);
        System.arraycopy(param1ArrayOfByte, param1Int1, this.buf, this.pos, i);
        this.pos += i;
        param1Int1 += i;
        param1Int2 -= i;
      } 
    }
    
    void drain() throws IOException, SecurityException {
      if (this.pos == 0)
        return; 
      if (this.blkmode)
        writeBlockHeader(this.pos); 
      this.out.write(this.buf, 0, this.pos);
      this.pos = 0;
    }
    
    private void writeBlockHeader(int param1Int) throws IOException {
      if (param1Int <= 255) {
        this.hbuf[0] = 119;
        this.hbuf[1] = (byte)param1Int;
        this.out.write(this.hbuf, 0, 2);
      } else {
        this.hbuf[0] = 122;
        Bits.putInt(this.hbuf, 1, param1Int);
        this.out.write(this.hbuf, 0, 5);
      } 
    }
    
    public void writeBoolean(boolean param1Boolean) throws IOException {
      if (this.pos >= 1024)
        drain(); 
      Bits.putBoolean(this.buf, this.pos++, param1Boolean);
    }
    
    public void writeByte(int param1Int) throws IOException {
      if (this.pos >= 1024)
        drain(); 
      this.buf[this.pos++] = (byte)param1Int;
    }
    
    public void writeChar(int param1Int) throws IOException {
      if (this.pos + 2 <= 1024) {
        Bits.putChar(this.buf, this.pos, (char)param1Int);
        this.pos += 2;
      } else {
        this.dout.writeChar(param1Int);
      } 
    }
    
    public void writeShort(int param1Int) throws IOException {
      if (this.pos + 2 <= 1024) {
        Bits.putShort(this.buf, this.pos, (short)param1Int);
        this.pos += 2;
      } else {
        this.dout.writeShort(param1Int);
      } 
    }
    
    public void writeInt(int param1Int) throws IOException {
      if (this.pos + 4 <= 1024) {
        Bits.putInt(this.buf, this.pos, param1Int);
        this.pos += 4;
      } else {
        this.dout.writeInt(param1Int);
      } 
    }
    
    public void writeFloat(float param1Float) throws IOException {
      if (this.pos + 4 <= 1024) {
        Bits.putFloat(this.buf, this.pos, param1Float);
        this.pos += 4;
      } else {
        this.dout.writeFloat(param1Float);
      } 
    }
    
    public void writeLong(long param1Long) throws IOException {
      if (this.pos + 8 <= 1024) {
        Bits.putLong(this.buf, this.pos, param1Long);
        this.pos += 8;
      } else {
        this.dout.writeLong(param1Long);
      } 
    }
    
    public void writeDouble(double param1Double) throws IOException {
      if (this.pos + 8 <= 1024) {
        Bits.putDouble(this.buf, this.pos, param1Double);
        this.pos += 8;
      } else {
        this.dout.writeDouble(param1Double);
      } 
    }
    
    public void writeBytes(String param1String) throws IOException {
      int i = param1String.length();
      int j = 0;
      int k = 0;
      int m;
      for (m = 0; m < i; m += n) {
        if (j >= k) {
          j = 0;
          k = Math.min(i - m, 256);
          param1String.getChars(m, m + k, this.cbuf, 0);
        } 
        if (this.pos >= 1024)
          drain(); 
        int n = Math.min(k - j, 1024 - this.pos);
        int i1 = this.pos + n;
        while (this.pos < i1)
          this.buf[this.pos++] = (byte)this.cbuf[j++]; 
      } 
    }
    
    public void writeChars(String param1String) throws IOException {
      int i = param1String.length();
      for (int j = 0; j < i; j += k) {
        int k = Math.min(i - j, 256);
        param1String.getChars(j, j + k, this.cbuf, 0);
        writeChars(this.cbuf, 0, k);
      } 
    }
    
    public void writeUTF(String param1String) throws IOException { writeUTF(param1String, getUTFLength(param1String)); }
    
    void writeBooleans(boolean[] param1ArrayOfBoolean, int param1Int1, int param1Int2) throws IOException {
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        if (this.pos >= 1024)
          drain(); 
        int j = Math.min(i, param1Int1 + 1024 - this.pos);
        while (param1Int1 < j)
          Bits.putBoolean(this.buf, this.pos++, param1ArrayOfBoolean[param1Int1++]); 
      } 
    }
    
    void writeChars(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws IOException {
      char c = 'Ͼ';
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        if (this.pos <= c) {
          int j = 1024 - this.pos >> 1;
          int k = Math.min(i, param1Int1 + j);
          while (param1Int1 < k) {
            Bits.putChar(this.buf, this.pos, param1ArrayOfChar[param1Int1++]);
            this.pos += 2;
          } 
          continue;
        } 
        this.dout.writeChar(param1ArrayOfChar[param1Int1++]);
      } 
    }
    
    void writeShorts(short[] param1ArrayOfShort, int param1Int1, int param1Int2) throws IOException {
      char c = 'Ͼ';
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        if (this.pos <= c) {
          int j = 1024 - this.pos >> 1;
          int k = Math.min(i, param1Int1 + j);
          while (param1Int1 < k) {
            Bits.putShort(this.buf, this.pos, param1ArrayOfShort[param1Int1++]);
            this.pos += 2;
          } 
          continue;
        } 
        this.dout.writeShort(param1ArrayOfShort[param1Int1++]);
      } 
    }
    
    void writeInts(int[] param1ArrayOfInt, int param1Int1, int param1Int2) throws IOException {
      char c = 'ϼ';
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        if (this.pos <= c) {
          int j = 1024 - this.pos >> 2;
          int k = Math.min(i, param1Int1 + j);
          while (param1Int1 < k) {
            Bits.putInt(this.buf, this.pos, param1ArrayOfInt[param1Int1++]);
            this.pos += 4;
          } 
          continue;
        } 
        this.dout.writeInt(param1ArrayOfInt[param1Int1++]);
      } 
    }
    
    void writeFloats(float[] param1ArrayOfFloat, int param1Int1, int param1Int2) throws IOException {
      char c = 'ϼ';
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        if (this.pos <= c) {
          int j = 1024 - this.pos >> 2;
          int k = Math.min(i - param1Int1, j);
          ObjectOutputStream.floatsToBytes(param1ArrayOfFloat, param1Int1, this.buf, this.pos, k);
          param1Int1 += k;
          this.pos += (k << 2);
          continue;
        } 
        this.dout.writeFloat(param1ArrayOfFloat[param1Int1++]);
      } 
    }
    
    void writeLongs(long[] param1ArrayOfLong, int param1Int1, int param1Int2) throws IOException {
      char c = 'ϸ';
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        if (this.pos <= c) {
          int j = 1024 - this.pos >> 3;
          int k = Math.min(i, param1Int1 + j);
          while (param1Int1 < k) {
            Bits.putLong(this.buf, this.pos, param1ArrayOfLong[param1Int1++]);
            this.pos += 8;
          } 
          continue;
        } 
        this.dout.writeLong(param1ArrayOfLong[param1Int1++]);
      } 
    }
    
    void writeDoubles(double[] param1ArrayOfDouble, int param1Int1, int param1Int2) throws IOException {
      char c = 'ϸ';
      int i = param1Int1 + param1Int2;
      while (param1Int1 < i) {
        if (this.pos <= c) {
          int j = 1024 - this.pos >> 3;
          int k = Math.min(i - param1Int1, j);
          ObjectOutputStream.doublesToBytes(param1ArrayOfDouble, param1Int1, this.buf, this.pos, k);
          param1Int1 += k;
          this.pos += (k << 3);
          continue;
        } 
        this.dout.writeDouble(param1ArrayOfDouble[param1Int1++]);
      } 
    }
    
    long getUTFLength(String param1String) {
      int i = param1String.length();
      long l = 0L;
      int j;
      for (j = 0; j < i; j += k) {
        int k = Math.min(i - j, 256);
        param1String.getChars(j, j + k, this.cbuf, 0);
        for (byte b = 0; b < k; b++) {
          char c = this.cbuf[b];
          if (c >= '\001' && c <= '') {
            l++;
          } else if (c > '߿') {
            l += 3L;
          } else {
            l += 2L;
          } 
        } 
      } 
      return l;
    }
    
    void writeUTF(String param1String, long param1Long) throws IOException {
      if (param1Long > 65535L)
        throw new UTFDataFormatException(); 
      writeShort((int)param1Long);
      if (param1Long == param1String.length()) {
        writeBytes(param1String);
      } else {
        writeUTFBody(param1String);
      } 
    }
    
    void writeLongUTF(String param1String) throws IOException { writeLongUTF(param1String, getUTFLength(param1String)); }
    
    void writeLongUTF(String param1String, long param1Long) throws IOException {
      writeLong(param1Long);
      if (param1Long == param1String.length()) {
        writeBytes(param1String);
      } else {
        writeUTFBody(param1String);
      } 
    }
    
    private void writeUTFBody(String param1String) throws IOException {
      char c = 'Ͻ';
      int i = param1String.length();
      int j;
      for (j = 0; j < i; j += k) {
        int k = Math.min(i - j, 256);
        param1String.getChars(j, j + k, this.cbuf, 0);
        for (byte b = 0; b < k; b++) {
          char c1 = this.cbuf[b];
          if (this.pos <= c) {
            if (c1 <= '' && c1 != '\000') {
              this.buf[this.pos++] = (byte)c1;
            } else if (c1 > '߿') {
              this.buf[this.pos + 2] = (byte)(0x80 | c1 >> Character.MIN_VALUE & 0x3F);
              this.buf[this.pos + 1] = (byte)(0x80 | c1 >> '\006' & 0x3F);
              this.buf[this.pos + 0] = (byte)(0xE0 | c1 >> '\f' & 0xF);
              this.pos += 3;
            } else {
              this.buf[this.pos + 1] = (byte)(0x80 | c1 >> Character.MIN_VALUE & 0x3F);
              this.buf[this.pos + 0] = (byte)(0xC0 | c1 >> '\006' & 0x1F);
              this.pos += 2;
            } 
          } else if (c1 <= '' && c1 != '\000') {
            write(c1);
          } else if (c1 > '߿') {
            write(0xE0 | c1 >> '\f' & 0xF);
            write(0x80 | c1 >> '\006' & 0x3F);
            write(0x80 | c1 >> Character.MIN_VALUE & 0x3F);
          } else {
            write(0xC0 | c1 >> '\006' & 0x1F);
            write(0x80 | c1 >> Character.MIN_VALUE & 0x3F);
          } 
        } 
      } 
    }
  }
  
  private static class Caches {
    static final ConcurrentMap<ObjectStreamClass.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
    
    static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
  }
  
  private static class DebugTraceInfoStack {
    private final List<String> stack = new ArrayList();
    
    void clear() throws IOException, SecurityException { this.stack.clear(); }
    
    void pop() throws IOException, SecurityException { this.stack.remove(this.stack.size() - 1); }
    
    void push(String param1String) throws IOException { this.stack.add("\t- " + param1String); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (!this.stack.isEmpty())
        for (int i = this.stack.size(); i > 0; i--)
          stringBuilder.append((String)this.stack.get(i - 1) + ((i != 1) ? "\n" : ""));  
      return stringBuilder.toString();
    }
  }
  
  private static class HandleTable {
    private int size;
    
    private int threshold;
    
    private final float loadFactor;
    
    private int[] spine;
    
    private int[] next;
    
    private Object[] objs;
    
    HandleTable(int param1Int, float param1Float) {
      this.loadFactor = param1Float;
      this.spine = new int[param1Int];
      this.next = new int[param1Int];
      this.objs = new Object[param1Int];
      this.threshold = (int)(param1Int * param1Float);
      clear();
    }
    
    int assign(Object param1Object) {
      if (this.size >= this.next.length)
        growEntries(); 
      if (this.size >= this.threshold)
        growSpine(); 
      insert(param1Object, this.size);
      return this.size++;
    }
    
    int lookup(Object param1Object) {
      if (this.size == 0)
        return -1; 
      int i = hash(param1Object) % this.spine.length;
      for (int j = this.spine[i]; j >= 0; j = this.next[j]) {
        if (this.objs[j] == param1Object)
          return j; 
      } 
      return -1;
    }
    
    void clear() throws IOException, SecurityException {
      Arrays.fill(this.spine, -1);
      Arrays.fill(this.objs, 0, this.size, null);
      this.size = 0;
    }
    
    int size() { return this.size; }
    
    private void insert(Object param1Object, int param1Int) {
      int i = hash(param1Object) % this.spine.length;
      this.objs[param1Int] = param1Object;
      this.next[param1Int] = this.spine[i];
      this.spine[i] = param1Int;
    }
    
    private void growSpine() throws IOException, SecurityException {
      this.spine = new int[(this.spine.length << 1) + 1];
      this.threshold = (int)(this.spine.length * this.loadFactor);
      Arrays.fill(this.spine, -1);
      for (byte b = 0; b < this.size; b++)
        insert(this.objs[b], b); 
    }
    
    private void growEntries() throws IOException, SecurityException {
      int i = (this.next.length << 1) + 1;
      int[] arrayOfInt = new int[i];
      System.arraycopy(this.next, 0, arrayOfInt, 0, this.size);
      this.next = arrayOfInt;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(this.objs, 0, arrayOfObject, 0, this.size);
      this.objs = arrayOfObject;
    }
    
    private int hash(Object param1Object) { return System.identityHashCode(param1Object) & 0x7FFFFFFF; }
  }
  
  public static abstract class PutField {
    public abstract void put(String param1String, boolean param1Boolean) throws IOException;
    
    public abstract void put(String param1String, byte param1Byte);
    
    public abstract void put(String param1String, char param1Char);
    
    public abstract void put(String param1String, short param1Short);
    
    public abstract void put(String param1String, int param1Int);
    
    public abstract void put(String param1String, long param1Long) throws IOException;
    
    public abstract void put(String param1String, float param1Float);
    
    public abstract void put(String param1String, double param1Double);
    
    public abstract void put(String param1String, Object param1Object);
    
    @Deprecated
    public abstract void write(ObjectOutput param1ObjectOutput) throws IOException;
  }
  
  private class PutFieldImpl extends PutField {
    private final ObjectStreamClass desc;
    
    private final byte[] primVals;
    
    private final Object[] objVals;
    
    PutFieldImpl(ObjectStreamClass param1ObjectStreamClass) {
      this.desc = param1ObjectStreamClass;
      this.primVals = new byte[param1ObjectStreamClass.getPrimDataSize()];
      this.objVals = new Object[param1ObjectStreamClass.getNumObjFields()];
    }
    
    public void put(String param1String, boolean param1Boolean) throws IOException { Bits.putBoolean(this.primVals, getFieldOffset(param1String, boolean.class), param1Boolean); }
    
    public void put(String param1String, byte param1Byte) { this.primVals[getFieldOffset(param1String, byte.class)] = param1Byte; }
    
    public void put(String param1String, char param1Char) { Bits.putChar(this.primVals, getFieldOffset(param1String, char.class), param1Char); }
    
    public void put(String param1String, short param1Short) { Bits.putShort(this.primVals, getFieldOffset(param1String, short.class), param1Short); }
    
    public void put(String param1String, int param1Int) { Bits.putInt(this.primVals, getFieldOffset(param1String, int.class), param1Int); }
    
    public void put(String param1String, float param1Float) { Bits.putFloat(this.primVals, getFieldOffset(param1String, float.class), param1Float); }
    
    public void put(String param1String, long param1Long) throws IOException { Bits.putLong(this.primVals, getFieldOffset(param1String, long.class), param1Long); }
    
    public void put(String param1String, double param1Double) { Bits.putDouble(this.primVals, getFieldOffset(param1String, double.class), param1Double); }
    
    public void put(String param1String, Object param1Object) { this.objVals[getFieldOffset(param1String, Object.class)] = param1Object; }
    
    public void write(ObjectOutput param1ObjectOutput) throws IOException {
      if (ObjectOutputStream.this != param1ObjectOutput)
        throw new IllegalArgumentException("wrong stream"); 
      param1ObjectOutput.write(this.primVals, 0, this.primVals.length);
      ObjectStreamField[] arrayOfObjectStreamField = this.desc.getFields(false);
      int i = arrayOfObjectStreamField.length - this.objVals.length;
      for (int j = 0; j < this.objVals.length; j++) {
        if (arrayOfObjectStreamField[i + j].isUnshared())
          throw new IOException("cannot write unshared object"); 
        param1ObjectOutput.writeObject(this.objVals[j]);
      } 
    }
    
    void writeFields() throws IOException, SecurityException {
      ObjectOutputStream.this.bout.write(this.primVals, 0, this.primVals.length, false);
      ObjectStreamField[] arrayOfObjectStreamField = this.desc.getFields(false);
      int i = arrayOfObjectStreamField.length - this.objVals.length;
      for (int j = 0; j < this.objVals.length; j++) {
        if (extendedDebugInfo)
          ObjectOutputStream.this.debugInfoStack.push("field (class \"" + this.desc.getName() + "\", name: \"" + arrayOfObjectStreamField[i + j].getName() + "\", type: \"" + arrayOfObjectStreamField[i + j].getType() + "\")"); 
        try {
          ObjectOutputStream.this.writeObject0(this.objVals[j], arrayOfObjectStreamField[i + j].isUnshared());
        } finally {
          if (extendedDebugInfo)
            ObjectOutputStream.this.debugInfoStack.pop(); 
        } 
      } 
    }
    
    private int getFieldOffset(String param1String, Class<?> param1Class) {
      ObjectStreamField objectStreamField = this.desc.getField(param1String, param1Class);
      if (objectStreamField == null)
        throw new IllegalArgumentException("no such field " + param1String + " with type " + param1Class); 
      return objectStreamField.getOffset();
    }
  }
  
  private static class ReplaceTable {
    private final ObjectOutputStream.HandleTable htab;
    
    private Object[] reps;
    
    ReplaceTable(int param1Int, float param1Float) {
      this.htab = new ObjectOutputStream.HandleTable(param1Int, param1Float);
      this.reps = new Object[param1Int];
    }
    
    void assign(Object param1Object1, Object param1Object2) {
      int i = this.htab.assign(param1Object1);
      while (i >= this.reps.length)
        grow(); 
      this.reps[i] = param1Object2;
    }
    
    Object lookup(Object param1Object) throws IOException {
      int i = this.htab.lookup(param1Object);
      return (i >= 0) ? this.reps[i] : param1Object;
    }
    
    void clear() throws IOException, SecurityException {
      Arrays.fill(this.reps, 0, this.htab.size(), null);
      this.htab.clear();
    }
    
    int size() { return this.htab.size(); }
    
    private void grow() throws IOException, SecurityException {
      Object[] arrayOfObject = new Object[(this.reps.length << 1) + 1];
      System.arraycopy(this.reps, 0, arrayOfObject, 0, this.reps.length);
      this.reps = arrayOfObject;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ObjectOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */