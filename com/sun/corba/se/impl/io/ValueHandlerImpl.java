package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.SendingContext.RunTime;

public final class ValueHandlerImpl implements ValueHandlerMultiFormat {
  public static final String FORMAT_VERSION_PROPERTY = "com.sun.CORBA.MaxStreamFormatVersion";
  
  private static final byte MAX_SUPPORTED_FORMAT_VERSION = 2;
  
  private static final byte STREAM_FORMAT_VERSION_1 = 1;
  
  private static final byte MAX_STREAM_FORMAT_VERSION = getMaxStreamFormatVersion();
  
  public static final short kRemoteType = 0;
  
  public static final short kAbstractType = 1;
  
  public static final short kValueType = 2;
  
  private Hashtable inputStreamPairs = null;
  
  private Hashtable outputStreamPairs = null;
  
  private CodeBase codeBase = null;
  
  private boolean useHashtables = true;
  
  private boolean isInputStream = true;
  
  private IIOPOutputStream outputStreamBridge = null;
  
  private IIOPInputStream inputStreamBridge = null;
  
  private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
  
  private UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  
  private static byte getMaxStreamFormatVersion() {
    try {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() { return System.getProperty("com.sun.CORBA.MaxStreamFormatVersion"); }
          });
      if (str == null)
        return 2; 
      byte b = Byte.parseByte(str);
      if (b < 1 || b > 2)
        throw new ExceptionInInitializerError("Invalid stream format version: " + b + ".  Valid range is 1 through " + '\002'); 
      return b;
    } catch (Exception exception) {
      ExceptionInInitializerError exceptionInInitializerError = new ExceptionInInitializerError(exception);
      exceptionInInitializerError.initCause(exception);
      throw exceptionInInitializerError;
    } 
  }
  
  public byte getMaximumStreamFormatVersion() { return MAX_STREAM_FORMAT_VERSION; }
  
  public void writeValue(OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte) {
    if (paramByte == 2) {
      if (!(paramOutputStream instanceof org.omg.CORBA.portable.ValueOutputStream))
        throw this.omgWrapper.notAValueoutputstream(); 
    } else if (paramByte != 1) {
      throw this.omgWrapper.invalidStreamFormatVersion(new Integer(paramByte));
    } 
    writeValueWithVersion(paramOutputStream, paramSerializable, paramByte);
  }
  
  private ValueHandlerImpl() {}
  
  private ValueHandlerImpl(boolean paramBoolean) {
    this();
    this.useHashtables = false;
    this.isInputStream = paramBoolean;
  }
  
  static ValueHandlerImpl getInstance() { return new ValueHandlerImpl(); }
  
  static ValueHandlerImpl getInstance(boolean paramBoolean) { return new ValueHandlerImpl(paramBoolean); }
  
  public void writeValue(OutputStream paramOutputStream, Serializable paramSerializable) { writeValueWithVersion(paramOutputStream, paramSerializable, (byte)1); }
  
  private void writeValueWithVersion(OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte) {
    OutputStream outputStream = (OutputStream)paramOutputStream;
    if (!this.useHashtables) {
      if (this.outputStreamBridge == null) {
        this.outputStreamBridge = createOutputStream();
        this.outputStreamBridge.setOrbStream(outputStream);
      } 
      try {
        this.outputStreamBridge.increaseRecursionDepth();
        writeValueInternal(this.outputStreamBridge, outputStream, paramSerializable, paramByte);
      } finally {
        this.outputStreamBridge.decreaseRecursionDepth();
      } 
      return;
    } 
    iIOPOutputStream = null;
    if (this.outputStreamPairs == null)
      this.outputStreamPairs = new Hashtable(); 
    iIOPOutputStream = (IIOPOutputStream)this.outputStreamPairs.get(paramOutputStream);
    if (iIOPOutputStream == null) {
      iIOPOutputStream = createOutputStream();
      iIOPOutputStream.setOrbStream(outputStream);
      this.outputStreamPairs.put(paramOutputStream, iIOPOutputStream);
    } 
    try {
      iIOPOutputStream.increaseRecursionDepth();
      writeValueInternal(iIOPOutputStream, outputStream, paramSerializable, paramByte);
    } finally {
      if (iIOPOutputStream.decreaseRecursionDepth() == 0)
        this.outputStreamPairs.remove(paramOutputStream); 
    } 
  }
  
  private void writeValueInternal(IIOPOutputStream paramIIOPOutputStream, OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte) {
    Class clazz = paramSerializable.getClass();
    if (clazz.isArray()) {
      write_Array(paramOutputStream, paramSerializable, clazz.getComponentType());
    } else {
      paramIIOPOutputStream.simpleWriteObject(paramSerializable, paramByte);
    } 
  }
  
  public Serializable readValue(InputStream paramInputStream, int paramInt, Class paramClass, String paramString, RunTime paramRunTime) {
    CodeBase codeBase1 = CodeBaseHelper.narrow(paramRunTime);
    InputStream inputStream = (InputStream)paramInputStream;
    if (!this.useHashtables) {
      if (this.inputStreamBridge == null) {
        this.inputStreamBridge = createInputStream();
        this.inputStreamBridge.setOrbStream(inputStream);
        this.inputStreamBridge.setSender(codeBase1);
        this.inputStreamBridge.setValueHandler(this);
      } 
      Serializable serializable1 = null;
      try {
        this.inputStreamBridge.increaseRecursionDepth();
        serializable1 = readValueInternal(this.inputStreamBridge, inputStream, paramInt, paramClass, paramString, codeBase1);
      } finally {
        if (this.inputStreamBridge.decreaseRecursionDepth() == 0);
      } 
      return serializable1;
    } 
    iIOPInputStream = null;
    if (this.inputStreamPairs == null)
      this.inputStreamPairs = new Hashtable(); 
    iIOPInputStream = (IIOPInputStream)this.inputStreamPairs.get(paramInputStream);
    if (iIOPInputStream == null) {
      iIOPInputStream = createInputStream();
      iIOPInputStream.setOrbStream(inputStream);
      iIOPInputStream.setSender(codeBase1);
      iIOPInputStream.setValueHandler(this);
      this.inputStreamPairs.put(paramInputStream, iIOPInputStream);
    } 
    Serializable serializable = null;
    try {
      iIOPInputStream.increaseRecursionDepth();
      serializable = readValueInternal(iIOPInputStream, inputStream, paramInt, paramClass, paramString, codeBase1);
    } finally {
      if (iIOPInputStream.decreaseRecursionDepth() == 0)
        this.inputStreamPairs.remove(paramInputStream); 
    } 
    return serializable;
  }
  
  private Serializable readValueInternal(IIOPInputStream paramIIOPInputStream, InputStream paramInputStream, int paramInt, Class paramClass, String paramString, CodeBase paramCodeBase) {
    Serializable serializable = null;
    if (paramClass == null) {
      if (isArray(paramString)) {
        read_Array(paramIIOPInputStream, paramInputStream, null, paramCodeBase, paramInt);
      } else {
        paramIIOPInputStream.simpleSkipObject(paramString, paramCodeBase);
      } 
      return serializable;
    } 
    if (paramClass.isArray()) {
      serializable = (Serializable)read_Array(paramIIOPInputStream, paramInputStream, paramClass, paramCodeBase, paramInt);
    } else {
      serializable = (Serializable)paramIIOPInputStream.simpleReadObject(paramClass, paramString, paramCodeBase, paramInt);
    } 
    return serializable;
  }
  
  public String getRMIRepositoryID(Class paramClass) { return RepositoryId.createForJavaType(paramClass); }
  
  public boolean isCustomMarshaled(Class paramClass) { return ObjectStreamClass.lookup(paramClass).isCustomMarshaled(); }
  
  public RunTime getRunTimeCodeBase() {
    if (this.codeBase != null)
      return this.codeBase; 
    this.codeBase = new FVDCodeBaseImpl();
    FVDCodeBaseImpl fVDCodeBaseImpl = (FVDCodeBaseImpl)this.codeBase;
    fVDCodeBaseImpl.setValueHandler(this);
    return this.codeBase;
  }
  
  public boolean useFullValueDescription(Class paramClass, String paramString) throws IOException { return RepositoryId.useFullValueDescription(paramClass, paramString); }
  
  public String getClassName(String paramString) {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    return repositoryId.getClassName();
  }
  
  public Class getClassFromType(String paramString) throws ClassNotFoundException {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    return repositoryId.getClassFromType();
  }
  
  public Class getAnyClassFromType(String paramString) throws ClassNotFoundException {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    return repositoryId.getAnyClassFromType();
  }
  
  public String createForAnyType(Class paramClass) { return RepositoryId.createForAnyType(paramClass); }
  
  public String getDefinedInId(String paramString) {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    return repositoryId.getDefinedInId();
  }
  
  public String getUnqualifiedName(String paramString) {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    return repositoryId.getUnqualifiedName();
  }
  
  public String getSerialVersionUID(String paramString) {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    return repositoryId.getSerialVersionUID();
  }
  
  public boolean isAbstractBase(Class paramClass) { return RepositoryId.isAbstractBase(paramClass); }
  
  public boolean isSequence(String paramString) {
    RepositoryId repositoryId = RepositoryId.cache.getId(paramString);
    return repositoryId.isSequence();
  }
  
  public Serializable writeReplace(Serializable paramSerializable) { return ObjectStreamClass.lookup(paramSerializable.getClass()).writeReplace(paramSerializable); }
  
  private void writeCharArray(OutputStream paramOutputStream, char[] paramArrayOfChar, int paramInt1, int paramInt2) { paramOutputStream.write_wchar_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  private void write_Array(OutputStream paramOutputStream, Serializable paramSerializable, Class paramClass) {
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class) {
        int[] arrayOfInt = (int[])paramSerializable;
        int i = arrayOfInt.length;
        paramOutputStream.write_ulong(i);
        paramOutputStream.write_long_array(arrayOfInt, 0, i);
      } else if (paramClass == byte.class) {
        byte[] arrayOfByte = (byte[])paramSerializable;
        int i = arrayOfByte.length;
        paramOutputStream.write_ulong(i);
        paramOutputStream.write_octet_array(arrayOfByte, 0, i);
      } else if (paramClass == long.class) {
        long[] arrayOfLong = (long[])paramSerializable;
        int i = arrayOfLong.length;
        paramOutputStream.write_ulong(i);
        paramOutputStream.write_longlong_array(arrayOfLong, 0, i);
      } else if (paramClass == float.class) {
        float[] arrayOfFloat = (float[])paramSerializable;
        int i = arrayOfFloat.length;
        paramOutputStream.write_ulong(i);
        paramOutputStream.write_float_array(arrayOfFloat, 0, i);
      } else if (paramClass == double.class) {
        double[] arrayOfDouble = (double[])paramSerializable;
        int i = arrayOfDouble.length;
        paramOutputStream.write_ulong(i);
        paramOutputStream.write_double_array(arrayOfDouble, 0, i);
      } else if (paramClass == short.class) {
        short[] arrayOfShort = (short[])paramSerializable;
        int i = arrayOfShort.length;
        paramOutputStream.write_ulong(i);
        paramOutputStream.write_short_array(arrayOfShort, 0, i);
      } else if (paramClass == char.class) {
        char[] arrayOfChar = (char[])paramSerializable;
        int i = arrayOfChar.length;
        paramOutputStream.write_ulong(i);
        writeCharArray(paramOutputStream, arrayOfChar, 0, i);
      } else if (paramClass == boolean.class) {
        boolean[] arrayOfBoolean = (boolean[])paramSerializable;
        int i = arrayOfBoolean.length;
        paramOutputStream.write_ulong(i);
        paramOutputStream.write_boolean_array(arrayOfBoolean, 0, i);
      } else {
        throw new Error("Invalid primitive type : " + paramSerializable.getClass().getName());
      } 
    } else if (paramClass == Object.class) {
      Object[] arrayOfObject = (Object[])paramSerializable;
      int i = arrayOfObject.length;
      paramOutputStream.write_ulong(i);
      for (byte b = 0; b < i; b++)
        Util.writeAny(paramOutputStream, arrayOfObject[b]); 
    } else {
      Object[] arrayOfObject = (Object[])paramSerializable;
      int i = arrayOfObject.length;
      paramOutputStream.write_ulong(i);
      byte b2 = 2;
      if (paramClass.isInterface()) {
        String str = paramClass.getName();
        if (java.rmi.Remote.class.isAssignableFrom(paramClass)) {
          b2 = 0;
        } else if (org.omg.CORBA.Object.class.isAssignableFrom(paramClass)) {
          b2 = 0;
        } else if (RepositoryId.isAbstractBase(paramClass)) {
          b2 = 1;
        } else if (ObjectStreamClassCorbaExt.isAbstractInterface(paramClass)) {
          b2 = 1;
        } 
      } 
      for (byte b1 = 0; b1 < i; b1++) {
        switch (b2) {
          case 0:
            Util.writeRemoteObject(paramOutputStream, arrayOfObject[b1]);
            break;
          case 1:
            Util.writeAbstractObject(paramOutputStream, arrayOfObject[b1]);
            break;
          case 2:
            try {
              paramOutputStream.write_value((Serializable)arrayOfObject[b1]);
            } catch (ClassCastException classCastException) {}
            break;
        } 
      } 
    } 
  }
  
  private void readCharArray(InputStream paramInputStream, char[] paramArrayOfChar, int paramInt1, int paramInt2) { paramInputStream.read_wchar_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  private Object read_Array(IIOPInputStream paramIIOPInputStream, InputStream paramInputStream, Class paramClass, CodeBase paramCodeBase, int paramInt) {
    try {
      int i = paramInputStream.read_ulong();
      if (paramClass == null) {
        for (byte b = 0; b < i; b++)
          paramInputStream.read_value(); 
        return null;
      } 
      Class clazz1 = paramClass.getComponentType();
      Class clazz2 = clazz1;
      if (clazz1.isPrimitive()) {
        if (clazz1 == int.class) {
          int[] arrayOfInt = new int[i];
          paramInputStream.read_long_array(arrayOfInt, 0, i);
          return (Serializable)arrayOfInt;
        } 
        if (clazz1 == byte.class) {
          byte[] arrayOfByte = new byte[i];
          paramInputStream.read_octet_array(arrayOfByte, 0, i);
          return (Serializable)arrayOfByte;
        } 
        if (clazz1 == long.class) {
          long[] arrayOfLong = new long[i];
          paramInputStream.read_longlong_array(arrayOfLong, 0, i);
          return (Serializable)arrayOfLong;
        } 
        if (clazz1 == float.class) {
          float[] arrayOfFloat = new float[i];
          paramInputStream.read_float_array(arrayOfFloat, 0, i);
          return (Serializable)arrayOfFloat;
        } 
        if (clazz1 == double.class) {
          double[] arrayOfDouble = new double[i];
          paramInputStream.read_double_array(arrayOfDouble, 0, i);
          return (Serializable)arrayOfDouble;
        } 
        if (clazz1 == short.class) {
          short[] arrayOfShort = new short[i];
          paramInputStream.read_short_array(arrayOfShort, 0, i);
          return (Serializable)arrayOfShort;
        } 
        if (clazz1 == char.class) {
          char[] arrayOfChar = new char[i];
          readCharArray(paramInputStream, arrayOfChar, 0, i);
          return (Serializable)arrayOfChar;
        } 
        if (clazz1 == boolean.class) {
          boolean[] arrayOfBoolean = new boolean[i];
          paramInputStream.read_boolean_array(arrayOfBoolean, 0, i);
          return (Serializable)arrayOfBoolean;
        } 
        throw new Error("Invalid primitive componentType : " + paramClass.getName());
      } 
      if (clazz1 == Object.class) {
        Object[] arrayOfObject1 = (Object[])Array.newInstance(clazz1, i);
        paramIIOPInputStream.activeRecursionMgr.addObject(paramInt, arrayOfObject1);
        for (byte b = 0; b < i; b++) {
          Object object = null;
          try {
            object = Util.readAny(paramInputStream);
          } catch (IndirectionException indirectionException) {
            try {
              object = paramIIOPInputStream.activeRecursionMgr.getObject(indirectionException.offset);
            } catch (IOException iOException) {
              throw this.utilWrapper.invalidIndirection(iOException, new Integer(indirectionException.offset));
            } 
          } 
          arrayOfObject1[b] = object;
        } 
        return (Serializable)arrayOfObject1;
      } 
      Object[] arrayOfObject = (Object[])Array.newInstance(clazz1, i);
      paramIIOPInputStream.activeRecursionMgr.addObject(paramInt, arrayOfObject);
      byte b2 = 2;
      boolean bool = false;
      if (clazz1.isInterface()) {
        boolean bool1 = false;
        if (java.rmi.Remote.class.isAssignableFrom(clazz1)) {
          b2 = 0;
          bool1 = true;
        } else if (org.omg.CORBA.Object.class.isAssignableFrom(clazz1)) {
          b2 = 0;
          bool1 = true;
        } else if (RepositoryId.isAbstractBase(clazz1)) {
          b2 = 1;
          bool1 = true;
        } else if (ObjectStreamClassCorbaExt.isAbstractInterface(clazz1)) {
          b2 = 1;
        } 
        if (bool1) {
          try {
            String str1 = Util.getCodebase(clazz1);
            String str2 = RepositoryId.createForAnyType(clazz1);
            Class clazz = Utility.loadStubClass(str2, str1, clazz1);
            clazz2 = clazz;
          } catch (ClassNotFoundException classNotFoundException) {
            bool = true;
          } 
        } else {
          bool = true;
        } 
      } 
      for (byte b1 = 0; b1 < i; b1++) {
        try {
          switch (b2) {
            case 0:
              if (!bool) {
                arrayOfObject[b1] = paramInputStream.read_Object(clazz2);
                break;
              } 
              arrayOfObject[b1] = Utility.readObjectAndNarrow(paramInputStream, clazz2);
              break;
            case 1:
              if (!bool) {
                arrayOfObject[b1] = paramInputStream.read_abstract_interface(clazz2);
                break;
              } 
              arrayOfObject[b1] = Utility.readAbstractAndNarrow(paramInputStream, clazz2);
              break;
            case 2:
              arrayOfObject[b1] = paramInputStream.read_value(clazz2);
              break;
          } 
        } catch (IndirectionException indirectionException) {
          try {
            arrayOfObject[b1] = paramIIOPInputStream.activeRecursionMgr.getObject(indirectionException.offset);
          } catch (IOException iOException) {
            throw this.utilWrapper.invalidIndirection(iOException, new Integer(indirectionException.offset));
          } 
        } 
      } 
      return (Serializable)arrayOfObject;
    } finally {
      paramIIOPInputStream.activeRecursionMgr.removeObject(paramInt);
    } 
  }
  
  private boolean isArray(String paramString) { return RepositoryId.cache.getId(paramString).isSequence(); }
  
  private String getOutputStreamClassName() { return "com.sun.corba.se.impl.io.IIOPOutputStream"; }
  
  private IIOPOutputStream createOutputStream() {
    String str = getOutputStreamClassName();
    try {
      IIOPOutputStream iIOPOutputStream = createOutputStreamBuiltIn(str);
      return (iIOPOutputStream != null) ? iIOPOutputStream : (IIOPOutputStream)createCustom(IIOPOutputStream.class, str);
    } catch (Throwable throwable) {
      InternalError internalError = new InternalError("Error loading " + str);
      internalError.initCause(throwable);
      throw internalError;
    } 
  }
  
  private IIOPOutputStream createOutputStreamBuiltIn(final String name) throws Throwable {
    try {
      return (IIOPOutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<IIOPOutputStream>() {
            public IIOPOutputStream run() { return ValueHandlerImpl.this.createOutputStreamBuiltInNoPriv(name); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw privilegedActionException.getCause();
    } 
  }
  
  private IIOPOutputStream createOutputStreamBuiltInNoPriv(String paramString) throws Throwable { return paramString.equals(IIOPOutputStream.class.getName()) ? new IIOPOutputStream() : null; }
  
  private String getInputStreamClassName() { return "com.sun.corba.se.impl.io.IIOPInputStream"; }
  
  private IIOPInputStream createInputStream() {
    String str = getInputStreamClassName();
    try {
      IIOPInputStream iIOPInputStream = createInputStreamBuiltIn(str);
      return (iIOPInputStream != null) ? iIOPInputStream : (IIOPInputStream)createCustom(IIOPInputStream.class, str);
    } catch (Throwable throwable) {
      InternalError internalError = new InternalError("Error loading " + str);
      internalError.initCause(throwable);
      throw internalError;
    } 
  }
  
  private IIOPInputStream createInputStreamBuiltIn(final String name) throws Throwable {
    try {
      return (IIOPInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<IIOPInputStream>() {
            public IIOPInputStream run() { return ValueHandlerImpl.this.createInputStreamBuiltInNoPriv(name); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw privilegedActionException.getCause();
    } 
  }
  
  private IIOPInputStream createInputStreamBuiltInNoPriv(String paramString) throws Throwable { return paramString.equals(IIOPInputStream.class.getName()) ? new IIOPInputStream() : null; }
  
  private <T> T createCustom(Class<T> paramClass, String paramString) throws Throwable {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null)
      classLoader = ClassLoader.getSystemClassLoader(); 
    Class clazz1 = classLoader.loadClass(paramString);
    Class clazz2 = clazz1.asSubclass(paramClass);
    return (T)clazz2.newInstance();
  }
  
  TCKind getJavaCharTCKind() { return TCKind.tk_wchar; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\ValueHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */