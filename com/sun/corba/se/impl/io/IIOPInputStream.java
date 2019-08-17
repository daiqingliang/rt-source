package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.Utility;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.EOFException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.Bridge;

public class IIOPInputStream extends InputStreamHook {
  private static Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction() {
        public Object run() throws IOException { return Bridge.get(); }
      });
  
  private static OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
  
  private static UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  
  private ValueMember[] defaultReadObjectFVDMembers = null;
  
  private InputStream orbStream;
  
  private CodeBase cbSender;
  
  private ValueHandlerImpl vhandler;
  
  private Object currentObject = null;
  
  private ObjectStreamClass currentClassDesc = null;
  
  private Class currentClass = null;
  
  private int recursionDepth = 0;
  
  private int simpleReadDepth = 0;
  
  ActiveRecursionManager activeRecursionMgr = new ActiveRecursionManager();
  
  private IOException abortIOException = null;
  
  private ClassNotFoundException abortClassNotFoundException = null;
  
  private Vector callbacks;
  
  ObjectStreamClass[] classdesc;
  
  Class[] classes;
  
  int spClass;
  
  private static final String kEmptyStr = "";
  
  public static final TypeCode kRemoteTypeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
  
  public static final TypeCode kValueTypeCode = ORB.init().get_primitive_tc(TCKind.tk_value);
  
  private static final boolean useFVDOnly = false;
  
  private byte streamFormatVersion;
  
  private static final Constructor OPT_DATA_EXCEPTION_CTOR = getOptDataExceptionCtor();
  
  private Object[] readObjectArgList = { this };
  
  private static Constructor getOptDataExceptionCtor() {
    try {
      Constructor constructor = (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
              Constructor constructor = OptionalDataException.class.getDeclaredConstructor(new Class[] { boolean.class });
              constructor.setAccessible(true);
              return constructor;
            }
          });
      if (constructor == null)
        throw new Error("Unable to find OptionalDataException constructor"); 
      return constructor;
    } catch (Exception exception) {
      throw new ExceptionInInitializerError(exception);
    } 
  }
  
  private OptionalDataException createOptionalDataException() {
    try {
      OptionalDataException optionalDataException = (OptionalDataException)OPT_DATA_EXCEPTION_CTOR.newInstance(new Object[] { Boolean.TRUE });
      if (optionalDataException == null)
        throw new Error("Created null OptionalDataException"); 
      return optionalDataException;
    } catch (Exception exception) {
      throw new Error("Couldn't create OptionalDataException", exception);
    } 
  }
  
  protected byte getStreamFormatVersion() { return this.streamFormatVersion; }
  
  private void readFormatVersion() throws IOException {
    this.streamFormatVersion = this.orbStream.read_octet();
    if (this.streamFormatVersion < 1 || this.streamFormatVersion > this.vhandler.getMaximumStreamFormatVersion()) {
      MARSHAL mARSHAL = omgWrapper.unsupportedFormatVersion(CompletionStatus.COMPLETED_MAYBE);
      IOException iOException = new IOException("Unsupported format version: " + this.streamFormatVersion);
      iOException.initCause(mARSHAL);
      throw iOException;
    } 
    if (this.streamFormatVersion == 2 && !(this.orbStream instanceof ValueInputStream)) {
      BAD_PARAM bAD_PARAM = omgWrapper.notAValueinputstream(CompletionStatus.COMPLETED_MAYBE);
      IOException iOException = new IOException("Not a ValueInputStream");
      iOException.initCause(bAD_PARAM);
      throw iOException;
    } 
  }
  
  public static void setTestFVDFlag(boolean paramBoolean) {}
  
  public IIOPInputStream() throws IOException { resetStream(); }
  
  final void setOrbStream(InputStream paramInputStream) { this.orbStream = paramInputStream; }
  
  final InputStream getOrbStream() { return this.orbStream; }
  
  public final void setSender(CodeBase paramCodeBase) { this.cbSender = paramCodeBase; }
  
  public final CodeBase getSender() { return this.cbSender; }
  
  public final void setValueHandler(ValueHandler paramValueHandler) { this.vhandler = (ValueHandlerImpl)paramValueHandler; }
  
  public final ValueHandler getValueHandler() { return this.vhandler; }
  
  final void increaseRecursionDepth() throws IOException { this.recursionDepth++; }
  
  final int decreaseRecursionDepth() { return --this.recursionDepth; }
  
  public final Object readObjectDelegate() throws IOException {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_abstract_interface();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, true);
      throw mARSHAL;
    } catch (IndirectionException indirectionException) {
      return this.activeRecursionMgr.getObject(indirectionException.offset);
    } 
  }
  
  final Object simpleReadObject(Class paramClass, String paramString, CodeBase paramCodeBase, int paramInt) {
    object1 = this.currentObject;
    objectStreamClass = this.currentClassDesc;
    clazz = this.currentClass;
    b = this.streamFormatVersion;
    this.simpleReadDepth++;
    Object object2 = null;
    try {
      if (this.vhandler.useFullValueDescription(paramClass, paramString)) {
        object2 = inputObjectUsingFVD(paramClass, paramString, paramCodeBase, paramInt);
      } else {
        object2 = inputObject(paramClass, paramString, paramCodeBase, paramInt);
      } 
      object2 = this.currentClassDesc.readResolve(object2);
    } catch (ClassNotFoundException classNotFoundException1) {
      bridge.throwException(classNotFoundException1);
      return null;
    } catch (IOException iOException1) {
      bridge.throwException(iOException1);
      return null;
    } finally {
      this.simpleReadDepth--;
      this.currentObject = object1;
      this.currentClassDesc = objectStreamClass;
      this.currentClass = clazz;
      this.streamFormatVersion = b;
    } 
    IOException iOException = this.abortIOException;
    if (this.simpleReadDepth == 0)
      this.abortIOException = null; 
    if (iOException != null) {
      bridge.throwException(iOException);
      return null;
    } 
    ClassNotFoundException classNotFoundException = this.abortClassNotFoundException;
    if (this.simpleReadDepth == 0)
      this.abortClassNotFoundException = null; 
    if (classNotFoundException != null) {
      bridge.throwException(classNotFoundException);
      return null;
    } 
    return object2;
  }
  
  public final void simpleSkipObject(String paramString, CodeBase paramCodeBase) {
    object = this.currentObject;
    objectStreamClass = this.currentClassDesc;
    clazz = this.currentClass;
    b = this.streamFormatVersion;
    this.simpleReadDepth++;
    Object object1 = null;
    try {
      skipObjectUsingFVD(paramString, paramCodeBase);
    } catch (ClassNotFoundException classNotFoundException1) {
      bridge.throwException(classNotFoundException1);
      return;
    } catch (IOException iOException1) {
      bridge.throwException(iOException1);
      return;
    } finally {
      this.simpleReadDepth--;
      this.streamFormatVersion = b;
      this.currentObject = object;
      this.currentClassDesc = objectStreamClass;
      this.currentClass = clazz;
    } 
    IOException iOException = this.abortIOException;
    if (this.simpleReadDepth == 0)
      this.abortIOException = null; 
    if (iOException != null) {
      bridge.throwException(iOException);
      return;
    } 
    ClassNotFoundException classNotFoundException = this.abortClassNotFoundException;
    if (this.simpleReadDepth == 0)
      this.abortClassNotFoundException = null; 
    if (classNotFoundException != null) {
      bridge.throwException(classNotFoundException);
      return;
    } 
  }
  
  protected final Object readObjectOverride() throws IOException { return readObjectDelegate(); }
  
  final void defaultReadObjectDelegate() throws IOException {
    try {
      if (this.currentObject == null || this.currentClassDesc == null)
        throw new NotActiveException("defaultReadObjectDelegate"); 
      if (!this.currentClassDesc.forClass().isAssignableFrom(this.currentObject.getClass()))
        throw new IOException("Object Type mismatch"); 
      if (this.defaultReadObjectFVDMembers != null && this.defaultReadObjectFVDMembers.length > 0) {
        inputClassFields(this.currentObject, this.currentClass, this.currentClassDesc, this.defaultReadObjectFVDMembers, this.cbSender);
      } else {
        ObjectStreamField[] arrayOfObjectStreamField = this.currentClassDesc.getFieldsNoCopy();
        if (arrayOfObjectStreamField.length > 0)
          inputClassFields(this.currentObject, this.currentClass, arrayOfObjectStreamField, this.cbSender); 
      } 
    } catch (NotActiveException notActiveException) {
      bridge.throwException(notActiveException);
    } catch (IOException iOException) {
      bridge.throwException(iOException);
    } catch (ClassNotFoundException classNotFoundException) {
      bridge.throwException(classNotFoundException);
    } 
  }
  
  public final boolean enableResolveObjectDelegate(boolean paramBoolean) { return false; }
  
  public final void mark(int paramInt) { this.orbStream.mark(paramInt); }
  
  public final boolean markSupported() { return this.orbStream.markSupported(); }
  
  public final void reset() throws IOException {
    try {
      this.orbStream.reset();
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final int available() { return 0; }
  
  public final void close() throws IOException {}
  
  public final int read() {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_octet() << 0 & 0xFF;
    } catch (MARSHAL mARSHAL) {
      if (mARSHAL.minor == 1330446344) {
        setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
        return -1;
      } 
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    try {
      this.readObjectState.readData(this);
      this.orbStream.read_octet_array(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt2;
    } catch (MARSHAL mARSHAL) {
      if (mARSHAL.minor == 1330446344) {
        setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
        return -1;
      } 
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final boolean readBoolean() {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_boolean();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final byte readByte() {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_octet();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final char readChar() throws IOException {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_wchar();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final double readDouble() throws IOException {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_double();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final float readFloat() throws IOException {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_float();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void readFully(byte[] paramArrayOfByte) throws IOException { readFully(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    try {
      this.readObjectState.readData(this);
      this.orbStream.read_octet_array(paramArrayOfByte, paramInt1, paramInt2);
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final int readInt() {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_long();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final String readLine() throws IOException { throw new IOException("Method readLine not supported"); }
  
  public final long readLong() throws IOException {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_longlong();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final short readShort() throws IOException {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_short();
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  protected final void readStreamHeader() throws IOException {}
  
  public final int readUnsignedByte() {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_octet() << 0 & 0xFF;
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final int readUnsignedShort() {
    try {
      this.readObjectState.readData(this);
      return this.orbStream.read_ushort() << 0 & 0xFFFF;
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  protected String internalReadUTF(InputStream paramInputStream) { return paramInputStream.read_wstring(); }
  
  public final String readUTF() throws IOException {
    try {
      this.readObjectState.readData(this);
      return internalReadUTF(this.orbStream);
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  private void handleOptionalDataMarshalException(MARSHAL paramMARSHAL, boolean paramBoolean) throws IOException {
    if (paramMARSHAL.minor == 1330446344) {
      OptionalDataException optionalDataException;
      if (!paramBoolean) {
        optionalDataException = new EOFException("No more optional data");
      } else {
        optionalDataException = createOptionalDataException();
      } 
      optionalDataException.initCause(paramMARSHAL);
      setState(IN_READ_OBJECT_NO_MORE_OPT_DATA);
      throw optionalDataException;
    } 
  }
  
  public final void registerValidation(ObjectInputValidation paramObjectInputValidation, int paramInt) throws NotActiveException, InvalidObjectException { throw new Error("Method registerValidation not supported"); }
  
  protected final Class resolveClass(ObjectStreamClass paramObjectStreamClass) throws IOException, ClassNotFoundException { throw new IOException("Method resolveClass not supported"); }
  
  protected final Object resolveObject(Object paramObject) throws IOException { throw new IOException("Method resolveObject not supported"); }
  
  public final int skipBytes(int paramInt) throws IOException {
    try {
      this.readObjectState.readData(this);
      byte[] arrayOfByte = new byte[paramInt];
      this.orbStream.read_octet_array(arrayOfByte, 0, paramInt);
      return paramInt;
    } catch (MARSHAL mARSHAL) {
      handleOptionalDataMarshalException(mARSHAL, false);
      throw mARSHAL;
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  private Object inputObject(Class paramClass, String paramString, CodeBase paramCodeBase, int paramInt) {
    this.currentClassDesc = ObjectStreamClass.lookup(paramClass);
    this.currentClass = this.currentClassDesc.forClass();
    if (this.currentClass == null)
      throw new ClassNotFoundException(this.currentClassDesc.getName()); 
    try {
      if (Enum.class.isAssignableFrom(paramClass)) {
        int i = this.orbStream.read_long();
        String str = (String)this.orbStream.read_value(String.class);
        return Enum.valueOf(paramClass, str);
      } 
      if (this.currentClassDesc.isExternalizable()) {
        try {
          this.currentObject = (this.currentClass == null) ? null : this.currentClassDesc.newInstance();
          if (this.currentObject != null) {
            this.activeRecursionMgr.addObject(paramInt, this.currentObject);
            readFormatVersion();
            Externalizable externalizable = (Externalizable)this.currentObject;
            externalizable.readExternal(this);
          } 
        } catch (InvocationTargetException invocationTargetException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          invalidClassException.initCause(invocationTargetException);
          throw invalidClassException;
        } catch (UnsupportedOperationException unsupportedOperationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          invalidClassException.initCause(unsupportedOperationException);
          throw invalidClassException;
        } catch (InstantiationException instantiationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
          invalidClassException.initCause(instantiationException);
          throw invalidClassException;
        } 
      } else {
        ObjectStreamClass objectStreamClass = this.currentClassDesc;
        Class clazz = this.currentClass;
        i = this.spClass;
        if (this.currentClass.getName().equals("java.lang.String"))
          return readUTF(); 
        objectStreamClass = this.currentClassDesc;
        clazz = this.currentClass;
        while (objectStreamClass != null && objectStreamClass.isSerializable()) {
          Class clazz1 = objectStreamClass.forClass();
          Class clazz2;
          for (clazz2 = clazz; clazz2 != null && clazz1 != clazz2; clazz2 = clazz2.getSuperclass());
          this.spClass++;
          if (this.spClass >= this.classes.length) {
            int j = this.classes.length * 2;
            Class[] arrayOfClass = new Class[j];
            ObjectStreamClass[] arrayOfObjectStreamClass = new ObjectStreamClass[j];
            System.arraycopy(this.classes, 0, arrayOfClass, 0, this.classes.length);
            System.arraycopy(this.classdesc, 0, arrayOfObjectStreamClass, 0, this.classes.length);
            this.classes = arrayOfClass;
            this.classdesc = arrayOfObjectStreamClass;
          } 
          if (clazz2 == null) {
            this.classdesc[this.spClass] = objectStreamClass;
            this.classes[this.spClass] = null;
          } else {
            this.classdesc[this.spClass] = objectStreamClass;
            this.classes[this.spClass] = clazz2;
            clazz = clazz2.getSuperclass();
          } 
          objectStreamClass = objectStreamClass.getSuperclass();
        } 
        try {
          this.currentObject = (this.currentClass == null) ? null : this.currentClassDesc.newInstance();
          this.activeRecursionMgr.addObject(paramInt, this.currentObject);
        } catch (InvocationTargetException invocationTargetException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          invalidClassException.initCause(invocationTargetException);
          throw invalidClassException;
        } catch (UnsupportedOperationException unsupportedOperationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          invalidClassException.initCause(unsupportedOperationException);
          throw invalidClassException;
        } catch (InstantiationException instantiationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
          invalidClassException.initCause(instantiationException);
          throw invalidClassException;
        } 
        try {
          this.spClass = this.spClass;
          while (this.spClass > i) {
            this.currentClassDesc = this.classdesc[this.spClass];
            this.currentClass = this.classes[this.spClass];
            if (this.classes[this.spClass] != null) {
              readObjectState = this.readObjectState;
              setState(DEFAULT_STATE);
              try {
                if (this.currentClassDesc.hasWriteObject()) {
                  readFormatVersion();
                  boolean bool = readBoolean();
                  this.readObjectState.beginUnmarshalCustomValue(this, bool, (this.currentClassDesc.readObjectMethod != null));
                } else if (this.currentClassDesc.hasReadObject()) {
                  setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED);
                } 
                if (!invokeObjectReader(this.currentClassDesc, this.currentObject, this.currentClass) || this.readObjectState == IN_READ_OBJECT_DEFAULTS_SENT) {
                  ObjectStreamField[] arrayOfObjectStreamField = this.currentClassDesc.getFieldsNoCopy();
                  if (arrayOfObjectStreamField.length > 0)
                    inputClassFields(this.currentObject, this.currentClass, arrayOfObjectStreamField, paramCodeBase); 
                } 
                if (this.currentClassDesc.hasWriteObject())
                  this.readObjectState.endUnmarshalCustomValue(this); 
              } finally {
                setState(readObjectState);
              } 
            } else {
              ObjectStreamField[] arrayOfObjectStreamField = this.currentClassDesc.getFieldsNoCopy();
              if (arrayOfObjectStreamField.length > 0)
                inputClassFields(null, this.currentClass, arrayOfObjectStreamField, paramCodeBase); 
            } 
            this.spClass--;
          } 
        } finally {
          this.spClass = i;
        } 
      } 
    } finally {
      this.activeRecursionMgr.removeObject(paramInt);
    } 
    return this.currentObject;
  }
  
  private Vector getOrderedDescriptions(String paramString, CodeBase paramCodeBase) {
    Vector vector = new Vector();
    if (paramCodeBase == null)
      return vector; 
    FullValueDescription fullValueDescription = paramCodeBase.meta(paramString);
    while (fullValueDescription != null) {
      vector.insertElementAt(fullValueDescription, 0);
      if (fullValueDescription.base_value != null && !"".equals(fullValueDescription.base_value)) {
        fullValueDescription = paramCodeBase.meta(fullValueDescription.base_value);
        continue;
      } 
      return vector;
    } 
    return vector;
  }
  
  private Object inputObjectUsingFVD(Class paramClass, String paramString, CodeBase paramCodeBase, int paramInt) {
    i = this.spClass;
    try {
      ObjectStreamClass objectStreamClass = this.currentClassDesc = ObjectStreamClass.lookup(paramClass);
      Class clazz = this.currentClass = paramClass;
      if (this.currentClassDesc.isExternalizable()) {
        try {
          this.currentObject = (this.currentClass == null) ? null : this.currentClassDesc.newInstance();
          if (this.currentObject != null) {
            this.activeRecursionMgr.addObject(paramInt, this.currentObject);
            readFormatVersion();
            Externalizable externalizable = (Externalizable)this.currentObject;
            externalizable.readExternal(this);
          } 
        } catch (InvocationTargetException invocationTargetException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          invalidClassException.initCause(invocationTargetException);
          throw invalidClassException;
        } catch (UnsupportedOperationException unsupportedOperationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          invalidClassException.initCause(unsupportedOperationException);
          throw invalidClassException;
        } catch (InstantiationException instantiationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
          invalidClassException.initCause(instantiationException);
          throw invalidClassException;
        } 
      } else {
        objectStreamClass = this.currentClassDesc;
        clazz = this.currentClass;
        while (objectStreamClass != null && objectStreamClass.isSerializable()) {
          Class clazz1 = objectStreamClass.forClass();
          Class clazz2;
          for (clazz2 = clazz; clazz2 != null && clazz1 != clazz2; clazz2 = clazz2.getSuperclass());
          this.spClass++;
          if (this.spClass >= this.classes.length) {
            int j = this.classes.length * 2;
            Class[] arrayOfClass = new Class[j];
            ObjectStreamClass[] arrayOfObjectStreamClass = new ObjectStreamClass[j];
            System.arraycopy(this.classes, 0, arrayOfClass, 0, this.classes.length);
            System.arraycopy(this.classdesc, 0, arrayOfObjectStreamClass, 0, this.classes.length);
            this.classes = arrayOfClass;
            this.classdesc = arrayOfObjectStreamClass;
          } 
          if (clazz2 == null) {
            this.classdesc[this.spClass] = objectStreamClass;
            this.classes[this.spClass] = null;
          } else {
            this.classdesc[this.spClass] = objectStreamClass;
            this.classes[this.spClass] = clazz2;
            clazz = clazz2.getSuperclass();
          } 
          objectStreamClass = objectStreamClass.getSuperclass();
        } 
        try {
          this.currentObject = (this.currentClass == null) ? null : this.currentClassDesc.newInstance();
          this.activeRecursionMgr.addObject(paramInt, this.currentObject);
        } catch (InvocationTargetException invocationTargetException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InvocationTargetException accessing no-arg constructor");
          invalidClassException.initCause(invocationTargetException);
          throw invalidClassException;
        } catch (UnsupportedOperationException unsupportedOperationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "UnsupportedOperationException accessing no-arg constructor");
          invalidClassException.initCause(unsupportedOperationException);
          throw invalidClassException;
        } catch (InstantiationException instantiationException) {
          InvalidClassException invalidClassException = new InvalidClassException(this.currentClass.getName(), "InstantiationException accessing no-arg constructor");
          invalidClassException.initCause(instantiationException);
          throw invalidClassException;
        } 
        Enumeration enumeration = getOrderedDescriptions(paramString, paramCodeBase).elements();
        while (enumeration.hasMoreElements() && this.spClass > i) {
          FullValueDescription fullValueDescription = (FullValueDescription)enumeration.nextElement();
          String str1 = this.vhandler.getClassName(fullValueDescription.id);
          String str2 = this.vhandler.getClassName(this.vhandler.getRMIRepositoryID(this.currentClass));
          while (this.spClass > i && !str1.equals(str2)) {
            int j = findNextClass(str1, this.classes, this.spClass, i);
            if (j != -1) {
              this.spClass = j;
              clazz = this.currentClass = this.classes[this.spClass];
              str2 = this.vhandler.getClassName(this.vhandler.getRMIRepositoryID(this.currentClass));
              continue;
            } 
            if (fullValueDescription.is_custom) {
              readFormatVersion();
              boolean bool = readBoolean();
              if (bool)
                inputClassFields(null, null, null, fullValueDescription.members, paramCodeBase); 
              if (getStreamFormatVersion() == 2) {
                ((ValueInputStream)getOrbStream()).start_value();
                ((ValueInputStream)getOrbStream()).end_value();
              } 
            } else {
              inputClassFields(null, this.currentClass, null, fullValueDescription.members, paramCodeBase);
            } 
            if (enumeration.hasMoreElements()) {
              fullValueDescription = (FullValueDescription)enumeration.nextElement();
              str1 = this.vhandler.getClassName(fullValueDescription.id);
              continue;
            } 
            return this.currentObject;
          } 
          objectStreamClass = this.currentClassDesc = ObjectStreamClass.lookup(this.currentClass);
          if (!str2.equals("java.lang.Object")) {
            readObjectState = this.readObjectState;
            setState(DEFAULT_STATE);
            try {
              if (fullValueDescription.is_custom) {
                readFormatVersion();
                boolean bool1 = readBoolean();
                this.readObjectState.beginUnmarshalCustomValue(this, bool1, (this.currentClassDesc.readObjectMethod != null));
              } 
              boolean bool = false;
              try {
                if (!fullValueDescription.is_custom && this.currentClassDesc.hasReadObject())
                  setState(IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED); 
                this.defaultReadObjectFVDMembers = fullValueDescription.members;
                bool = invokeObjectReader(this.currentClassDesc, this.currentObject, this.currentClass);
              } finally {
                this.defaultReadObjectFVDMembers = null;
              } 
              if (!bool || this.readObjectState == IN_READ_OBJECT_DEFAULTS_SENT)
                inputClassFields(this.currentObject, this.currentClass, objectStreamClass, fullValueDescription.members, paramCodeBase); 
              if (fullValueDescription.is_custom)
                this.readObjectState.endUnmarshalCustomValue(this); 
            } finally {
              setState(readObjectState);
            } 
            clazz = this.currentClass = this.classes[--this.spClass];
            continue;
          } 
          inputClassFields(null, this.currentClass, null, fullValueDescription.members, paramCodeBase);
          while (enumeration.hasMoreElements()) {
            fullValueDescription = (FullValueDescription)enumeration.nextElement();
            if (fullValueDescription.is_custom) {
              skipCustomUsingFVD(fullValueDescription.members, paramCodeBase);
              continue;
            } 
            inputClassFields(null, this.currentClass, null, fullValueDescription.members, paramCodeBase);
          } 
        } 
        while (enumeration.hasMoreElements()) {
          FullValueDescription fullValueDescription = (FullValueDescription)enumeration.nextElement();
          if (fullValueDescription.is_custom) {
            skipCustomUsingFVD(fullValueDescription.members, paramCodeBase);
            continue;
          } 
          throwAwayData(fullValueDescription.members, paramCodeBase);
        } 
      } 
      return this.currentObject;
    } finally {
      this.spClass = i;
      this.activeRecursionMgr.removeObject(paramInt);
    } 
  }
  
  private Object skipObjectUsingFVD(String paramString, CodeBase paramCodeBase) throws IOException, ClassNotFoundException {
    Enumeration enumeration = getOrderedDescriptions(paramString, paramCodeBase).elements();
    while (enumeration.hasMoreElements()) {
      FullValueDescription fullValueDescription = (FullValueDescription)enumeration.nextElement();
      String str = this.vhandler.getClassName(fullValueDescription.id);
      if (!str.equals("java.lang.Object")) {
        if (fullValueDescription.is_custom) {
          readFormatVersion();
          boolean bool = readBoolean();
          if (bool)
            inputClassFields(null, null, null, fullValueDescription.members, paramCodeBase); 
          if (getStreamFormatVersion() == 2) {
            ((ValueInputStream)getOrbStream()).start_value();
            ((ValueInputStream)getOrbStream()).end_value();
          } 
          continue;
        } 
        inputClassFields(null, null, null, fullValueDescription.members, paramCodeBase);
      } 
    } 
    return null;
  }
  
  private int findNextClass(String paramString, Class[] paramArrayOfClass, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i > paramInt2; i--) {
      if (paramString.equals(paramArrayOfClass[i].getName()))
        return i; 
    } 
    return -1;
  }
  
  private boolean invokeObjectReader(ObjectStreamClass paramObjectStreamClass, Object paramObject, Class paramClass) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    if (paramObjectStreamClass.readObjectMethod == null)
      return false; 
    try {
      paramObjectStreamClass.readObjectMethod.invoke(paramObject, this.readObjectArgList);
      return true;
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (throwable instanceof ClassNotFoundException)
        throw (ClassNotFoundException)throwable; 
      if (throwable instanceof IOException)
        throw (IOException)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new Error("internal error");
    } catch (IllegalAccessException illegalAccessException) {
      return false;
    } 
  }
  
  private void resetStream() throws IOException {
    if (this.classes == null) {
      this.classes = new Class[20];
    } else {
      for (byte b = 0; b < this.classes.length; b++)
        this.classes[b] = null; 
    } 
    if (this.classdesc == null) {
      this.classdesc = new ObjectStreamClass[20];
    } else {
      for (byte b = 0; b < this.classdesc.length; b++)
        this.classdesc[b] = null; 
    } 
    this.spClass = 0;
    if (this.callbacks != null)
      this.callbacks.setSize(0); 
  }
  
  private void inputPrimitiveField(Object paramObject, Class paramClass, ObjectStreamField paramObjectStreamField) throws InvalidClassException, IOException {
    try {
      double d;
      float f;
      long l;
      int i;
      short s;
      char c;
      boolean bool;
      byte b;
      switch (paramObjectStreamField.getTypeCode()) {
        case 'B':
          b = this.orbStream.read_octet();
          if (paramObjectStreamField.getField() != null)
            bridge.putByte(paramObject, paramObjectStreamField.getFieldID(), b); 
          return;
        case 'Z':
          bool = this.orbStream.read_boolean();
          if (paramObjectStreamField.getField() != null)
            bridge.putBoolean(paramObject, paramObjectStreamField.getFieldID(), bool); 
          return;
        case 'C':
          c = this.orbStream.read_wchar();
          if (paramObjectStreamField.getField() != null)
            bridge.putChar(paramObject, paramObjectStreamField.getFieldID(), c); 
          return;
        case 'S':
          s = this.orbStream.read_short();
          if (paramObjectStreamField.getField() != null)
            bridge.putShort(paramObject, paramObjectStreamField.getFieldID(), s); 
          return;
        case 'I':
          i = this.orbStream.read_long();
          if (paramObjectStreamField.getField() != null)
            bridge.putInt(paramObject, paramObjectStreamField.getFieldID(), i); 
          return;
        case 'J':
          l = this.orbStream.read_longlong();
          if (paramObjectStreamField.getField() != null)
            bridge.putLong(paramObject, paramObjectStreamField.getFieldID(), l); 
          return;
        case 'F':
          f = this.orbStream.read_float();
          if (paramObjectStreamField.getField() != null)
            bridge.putFloat(paramObject, paramObjectStreamField.getFieldID(), f); 
          return;
        case 'D':
          d = this.orbStream.read_double();
          if (paramObjectStreamField.getField() != null)
            bridge.putDouble(paramObject, paramObjectStreamField.getFieldID(), d); 
          return;
      } 
      throw new InvalidClassException(paramClass.getName());
    } catch (IllegalArgumentException illegalArgumentException) {
      ClassCastException classCastException = new ClassCastException("Assigning instance of class " + paramObjectStreamField.getType().getName() + " to field " + this.currentClassDesc.getName() + '#' + paramObjectStreamField.getField().getName());
      classCastException.initCause(illegalArgumentException);
      throw classCastException;
    } 
  }
  
  private Object inputObjectField(ValueMember paramValueMember, CodeBase paramCodeBase) throws IndirectionException, ClassNotFoundException, IOException, StreamCorruptedException {
    Object object = null;
    Class clazz = null;
    String str1 = paramValueMember.id;
    try {
      clazz = this.vhandler.getClassFromType(str1);
    } catch (ClassNotFoundException classNotFoundException) {
      clazz = null;
    } 
    String str2 = null;
    if (clazz != null)
      str2 = ValueUtility.getSignature(paramValueMember); 
    if (str2 != null && (str2.equals("Ljava/lang/Object;") || str2.equals("Ljava/io/Serializable;") || str2.equals("Ljava/io/Externalizable;"))) {
      object = Util.readAny(this.orbStream);
    } else {
      byte b = 2;
      if (!this.vhandler.isSequence(str1))
        if (paramValueMember.type.kind().value() == kRemoteTypeCode.kind().value()) {
          b = 0;
        } else if (clazz != null && clazz.isInterface() && (this.vhandler.isAbstractBase(clazz) || ObjectStreamClassCorbaExt.isAbstractInterface(clazz))) {
          b = 1;
        }  
      switch (b) {
        case 0:
          if (clazz != null) {
            object = Utility.readObjectAndNarrow(this.orbStream, clazz);
          } else {
            object = this.orbStream.read_Object();
          } 
          return object;
        case 1:
          if (clazz != null) {
            object = Utility.readAbstractAndNarrow(this.orbStream, clazz);
          } else {
            object = this.orbStream.read_abstract_interface();
          } 
          return object;
        case 2:
          if (clazz != null) {
            object = this.orbStream.read_value(clazz);
          } else {
            object = this.orbStream.read_value();
          } 
          return object;
      } 
      throw new StreamCorruptedException("Unknown callType: " + b);
    } 
    return object;
  }
  
  private Object inputObjectField(ObjectStreamField paramObjectStreamField) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IndirectionException, IOException {
    if (ObjectStreamClassCorbaExt.isAny(paramObjectStreamField.getTypeString()))
      return Util.readAny(this.orbStream); 
    null = null;
    Class clazz1 = paramObjectStreamField.getType();
    Class clazz2 = clazz1;
    byte b = 2;
    boolean bool = false;
    if (clazz1.isInterface()) {
      boolean bool1 = false;
      if (java.rmi.Remote.class.isAssignableFrom(clazz1)) {
        b = 0;
      } else if (org.omg.CORBA.Object.class.isAssignableFrom(clazz1)) {
        b = 0;
        bool1 = true;
      } else if (this.vhandler.isAbstractBase(clazz1)) {
        b = 1;
        bool1 = true;
      } else if (ObjectStreamClassCorbaExt.isAbstractInterface(clazz1)) {
        b = 1;
      } 
      if (bool1) {
        try {
          String str1 = Util.getCodebase(clazz1);
          String str2 = this.vhandler.createForAnyType(clazz1);
          Class clazz = Utility.loadStubClass(str2, str1, clazz1);
          clazz2 = clazz;
        } catch (ClassNotFoundException classNotFoundException) {
          bool = true;
        } 
      } else {
        bool = true;
      } 
    } 
    switch (b) {
      case 0:
        if (!bool) {
          null = this.orbStream.read_Object(clazz2);
        } else {
          null = Utility.readObjectAndNarrow(this.orbStream, clazz2);
        } 
        return null;
      case 1:
        if (!bool) {
          null = this.orbStream.read_abstract_interface(clazz2);
        } else {
          null = Utility.readAbstractAndNarrow(this.orbStream, clazz2);
        } 
        return null;
      case 2:
        return this.orbStream.read_value(clazz2);
    } 
    throw new StreamCorruptedException("Unknown callType: " + b);
  }
  
  private final boolean mustUseRemoteValueMembers() { return (this.defaultReadObjectFVDMembers != null); }
  
  void readFields(Map paramMap) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    if (mustUseRemoteValueMembers()) {
      inputRemoteMembersForReadFields(paramMap);
    } else {
      inputCurrentClassFieldsForReadFields(paramMap);
    } 
  }
  
  private final void inputRemoteMembersForReadFields(Map paramMap) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    ValueMember[] arrayOfValueMember = this.defaultReadObjectFVDMembers;
    try {
      for (byte b = 0; b < arrayOfValueMember.length; b++) {
        Object object;
        double d;
        float f;
        long l;
        int i;
        short s;
        char c;
        boolean bool;
        byte b1;
        switch ((arrayOfValueMember[b]).type.kind().value()) {
          case 10:
            b1 = this.orbStream.read_octet();
            paramMap.put((arrayOfValueMember[b]).name, new Byte(b1));
            break;
          case 8:
            bool = this.orbStream.read_boolean();
            paramMap.put((arrayOfValueMember[b]).name, new Boolean(bool));
            break;
          case 9:
          case 26:
            c = this.orbStream.read_wchar();
            paramMap.put((arrayOfValueMember[b]).name, new Character(c));
            break;
          case 2:
            s = this.orbStream.read_short();
            paramMap.put((arrayOfValueMember[b]).name, new Short(s));
            break;
          case 3:
            i = this.orbStream.read_long();
            paramMap.put((arrayOfValueMember[b]).name, new Integer(i));
            break;
          case 23:
            l = this.orbStream.read_longlong();
            paramMap.put((arrayOfValueMember[b]).name, new Long(l));
            break;
          case 6:
            f = this.orbStream.read_float();
            paramMap.put((arrayOfValueMember[b]).name, new Float(f));
            break;
          case 7:
            d = this.orbStream.read_double();
            paramMap.put((arrayOfValueMember[b]).name, new Double(d));
            break;
          case 14:
          case 29:
          case 30:
            object = null;
            try {
              object = inputObjectField(arrayOfValueMember[b], this.cbSender);
            } catch (IndirectionException indirectionException) {
              object = this.activeRecursionMgr.getObject(indirectionException.offset);
            } 
            paramMap.put((arrayOfValueMember[b]).name, object);
            break;
          default:
            throw new StreamCorruptedException("Unknown kind: " + (arrayOfValueMember[b]).type.kind().value());
        } 
      } 
    } catch (Throwable throwable) {
      StreamCorruptedException streamCorruptedException = new StreamCorruptedException(throwable.getMessage());
      streamCorruptedException.initCause(throwable);
      throw streamCorruptedException;
    } 
  }
  
  private final void inputCurrentClassFieldsForReadFields(Map paramMap) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    ObjectStreamField[] arrayOfObjectStreamField = this.currentClassDesc.getFieldsNoCopy();
    int i = arrayOfObjectStreamField.length - this.currentClassDesc.objFields;
    int j;
    for (j = 0; j < i; j++) {
      double d;
      float f;
      long l;
      int k;
      short s;
      char c;
      boolean bool;
      byte b;
      switch (arrayOfObjectStreamField[j].getTypeCode()) {
        case 'B':
          b = this.orbStream.read_octet();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Byte(b));
          break;
        case 'Z':
          bool = this.orbStream.read_boolean();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Boolean(bool));
          break;
        case 'C':
          c = this.orbStream.read_wchar();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Character(c));
          break;
        case 'S':
          s = this.orbStream.read_short();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Short(s));
          break;
        case 'I':
          k = this.orbStream.read_long();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Integer(k));
          break;
        case 'J':
          l = this.orbStream.read_longlong();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Long(l));
          break;
        case 'F':
          f = this.orbStream.read_float();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Float(f));
          break;
        case 'D':
          d = this.orbStream.read_double();
          paramMap.put(arrayOfObjectStreamField[j].getName(), new Double(d));
          break;
        default:
          throw new InvalidClassException(this.currentClassDesc.getName());
      } 
    } 
    if (this.currentClassDesc.objFields > 0)
      for (j = i; j < arrayOfObjectStreamField.length; j++) {
        Object object = null;
        try {
          object = inputObjectField(arrayOfObjectStreamField[j]);
        } catch (IndirectionException indirectionException) {
          object = this.activeRecursionMgr.getObject(indirectionException.offset);
        } 
        paramMap.put(arrayOfObjectStreamField[j].getName(), object);
      }  
  }
  
  private void inputClassFields(Object paramObject, Class<?> paramClass, ObjectStreamField[] paramArrayOfObjectStreamField, CodeBase paramCodeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    int i = paramArrayOfObjectStreamField.length - this.currentClassDesc.objFields;
    if (paramObject != null)
      for (byte b = 0; b < i; b++)
        inputPrimitiveField(paramObject, paramClass, paramArrayOfObjectStreamField[b]);  
    if (this.currentClassDesc.objFields > 0)
      for (int j = i; j < paramArrayOfObjectStreamField.length; j++) {
        Object object = null;
        try {
          object = inputObjectField(paramArrayOfObjectStreamField[j]);
        } catch (IndirectionException indirectionException) {
          object = this.activeRecursionMgr.getObject(indirectionException.offset);
        } 
        if (paramObject != null && paramArrayOfObjectStreamField[j].getField() != null)
          try {
            Class clazz = paramArrayOfObjectStreamField[j].getClazz();
            if (object != null && !clazz.isAssignableFrom(object.getClass()))
              throw new IllegalArgumentException("Field mismatch"); 
            Field field = null;
            String str = paramArrayOfObjectStreamField[j].getName();
            try {
              field = getDeclaredField(paramClass, str);
            } catch (PrivilegedActionException privilegedActionException) {
              throw new IllegalArgumentException((NoSuchFieldException)privilegedActionException.getException());
            } catch (SecurityException securityException) {
              throw new IllegalArgumentException(securityException);
            } catch (NullPointerException nullPointerException) {
            
            } catch (NoSuchFieldException noSuchFieldException) {}
            if (field != null) {
              Class clazz1 = field.getType();
              if (!clazz1.isAssignableFrom(clazz))
                throw new IllegalArgumentException("Field Type mismatch"); 
              if (object != null && !clazz.isInstance(object))
                throw new IllegalArgumentException(); 
              bridge.putObject(paramObject, paramArrayOfObjectStreamField[j].getFieldID(), object);
            } 
          } catch (IllegalArgumentException illegalArgumentException) {
            String str1 = "null";
            String str2 = "null";
            String str3 = "null";
            if (object != null)
              str1 = object.getClass().getName(); 
            if (this.currentClassDesc != null)
              str2 = this.currentClassDesc.getName(); 
            if (paramArrayOfObjectStreamField[j] != null && paramArrayOfObjectStreamField[j].getField() != null)
              str3 = paramArrayOfObjectStreamField[j].getField().getName(); 
            ClassCastException classCastException = new ClassCastException("Assigning instance of class " + str1 + " to field " + str2 + '#' + str3);
            classCastException.initCause(illegalArgumentException);
            throw classCastException;
          }  
      }  
  }
  
  private void inputClassFields(Object paramObject, Class paramClass, ObjectStreamClass paramObjectStreamClass, ValueMember[] paramArrayOfValueMember, CodeBase paramCodeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    try {
      for (byte b = 0; b < paramArrayOfValueMember.length; b++) {
        try {
          Object object;
          double d;
          float f;
          long l;
          int i;
          short s;
          char c;
          boolean bool;
          byte b1;
          switch ((paramArrayOfValueMember[b]).type.kind().value()) {
            case 10:
              b1 = this.orbStream.read_octet();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setByteField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, b1); 
              break;
            case 8:
              bool = this.orbStream.read_boolean();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setBooleanField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, bool); 
              break;
            case 9:
            case 26:
              c = this.orbStream.read_wchar();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setCharField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, c); 
              break;
            case 2:
              s = this.orbStream.read_short();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setShortField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, s); 
              break;
            case 3:
              i = this.orbStream.read_long();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setIntField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, i); 
              break;
            case 23:
              l = this.orbStream.read_longlong();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setLongField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, l); 
              break;
            case 6:
              f = this.orbStream.read_float();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setFloatField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, f); 
              break;
            case 7:
              d = this.orbStream.read_double();
              if (paramObject != null && paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                setDoubleField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, d); 
              break;
            case 14:
            case 29:
            case 30:
              object = null;
              try {
                object = inputObjectField(paramArrayOfValueMember[b], paramCodeBase);
              } catch (IndirectionException indirectionException) {
                object = this.activeRecursionMgr.getObject(indirectionException.offset);
              } 
              if (paramObject == null)
                break; 
              try {
                if (paramObjectStreamClass.hasField(paramArrayOfValueMember[b]))
                  setObjectField(paramObject, paramClass, (paramArrayOfValueMember[b]).name, object); 
              } catch (IllegalArgumentException illegalArgumentException) {
                ClassCastException classCastException = new ClassCastException("Assigning instance of class " + object.getClass().getName() + " to field " + (paramArrayOfValueMember[b]).name);
                classCastException.initCause(illegalArgumentException);
                throw classCastException;
              } 
              break;
            default:
              throw new StreamCorruptedException("Unknown kind: " + (paramArrayOfValueMember[b]).type.kind().value());
          } 
        } catch (IllegalArgumentException illegalArgumentException) {
          ClassCastException classCastException = new ClassCastException("Assigning instance of class " + (paramArrayOfValueMember[b]).id + " to field " + this.currentClassDesc.getName() + '#' + (paramArrayOfValueMember[b]).name);
          classCastException.initCause(illegalArgumentException);
          throw classCastException;
        } 
      } 
    } catch (Throwable throwable) {
      StreamCorruptedException streamCorruptedException = new StreamCorruptedException(throwable.getMessage());
      streamCorruptedException.initCause(throwable);
      throw streamCorruptedException;
    } 
  }
  
  private void skipCustomUsingFVD(ValueMember[] paramArrayOfValueMember, CodeBase paramCodeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    readFormatVersion();
    boolean bool = readBoolean();
    if (bool)
      throwAwayData(paramArrayOfValueMember, paramCodeBase); 
    if (getStreamFormatVersion() == 2) {
      ((ValueInputStream)getOrbStream()).start_value();
      ((ValueInputStream)getOrbStream()).end_value();
    } 
  }
  
  private void throwAwayData(ValueMember[] paramArrayOfValueMember, CodeBase paramCodeBase) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException {
    for (byte b = 0; b < paramArrayOfValueMember.length; b++) {
      try {
        String str2;
        String str1;
        Class clazz;
        switch ((paramArrayOfValueMember[b]).type.kind().value()) {
          case 10:
            this.orbStream.read_octet();
            break;
          case 8:
            this.orbStream.read_boolean();
            break;
          case 9:
          case 26:
            this.orbStream.read_wchar();
            break;
          case 2:
            this.orbStream.read_short();
            break;
          case 3:
            this.orbStream.read_long();
            break;
          case 23:
            this.orbStream.read_longlong();
            break;
          case 6:
            this.orbStream.read_float();
            break;
          case 7:
            this.orbStream.read_double();
            break;
          case 14:
          case 29:
          case 30:
            clazz = null;
            str1 = (paramArrayOfValueMember[b]).id;
            try {
              clazz = this.vhandler.getClassFromType(str1);
            } catch (ClassNotFoundException classNotFoundException) {
              clazz = null;
            } 
            str2 = null;
            if (clazz != null)
              str2 = ValueUtility.getSignature(paramArrayOfValueMember[b]); 
            try {
              if (str2 != null && (str2.equals("Ljava/lang/Object;") || str2.equals("Ljava/io/Serializable;") || str2.equals("Ljava/io/Externalizable;"))) {
                Util.readAny(this.orbStream);
                break;
              } 
              byte b1 = 2;
              if (!this.vhandler.isSequence(str1)) {
                FullValueDescription fullValueDescription = paramCodeBase.meta((paramArrayOfValueMember[b]).id);
                if (kRemoteTypeCode == (paramArrayOfValueMember[b]).type) {
                  b1 = 0;
                } else if (fullValueDescription.is_abstract) {
                  b1 = 1;
                } 
              } 
              switch (b1) {
                case 0:
                  this.orbStream.read_Object();
                  break;
                case 1:
                  this.orbStream.read_abstract_interface();
                  break;
                case 2:
                  if (clazz != null) {
                    this.orbStream.read_value(clazz);
                    break;
                  } 
                  this.orbStream.read_value();
                  break;
              } 
              throw new StreamCorruptedException("Unknown callType: " + b1);
            } catch (IndirectionException indirectionException) {
              break;
            } 
          default:
            throw new StreamCorruptedException("Unknown kind: " + (paramArrayOfValueMember[b]).type.kind().value());
        } 
      } catch (IllegalArgumentException illegalArgumentException) {
        ClassCastException classCastException = new ClassCastException("Assigning instance of class " + (paramArrayOfValueMember[b]).id + " to field " + this.currentClassDesc.getName() + '#' + (paramArrayOfValueMember[b]).name);
        classCastException.initCause(illegalArgumentException);
        throw classCastException;
      } 
    } 
  }
  
  private static void setObjectField(Object paramObject1, Class<?> paramClass, String paramString, Object paramObject2) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      Class clazz = field.getType();
      if (paramObject2 != null && !clazz.isInstance(paramObject2))
        throw new Exception(); 
      long l = bridge.objectFieldOffset(field);
      bridge.putObject(paramObject1, l, paramObject2);
    } catch (Exception exception) {
      if (paramObject1 != null)
        throw utilWrapper.errorSetObjectField(exception, paramString, paramObject1.toString(), paramObject2.toString()); 
      throw utilWrapper.errorSetObjectField(exception, paramString, "null " + paramClass.getName() + " object", paramObject2.toString());
    } 
  }
  
  private static void setBooleanField(Object paramObject, Class<?> paramClass, String paramString, boolean paramBoolean) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == boolean.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putBoolean(paramObject, l, paramBoolean);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetBooleanField(exception, paramString, paramObject.toString(), new Boolean(paramBoolean)); 
      throw utilWrapper.errorSetBooleanField(exception, paramString, "null " + paramClass.getName() + " object", new Boolean(paramBoolean));
    } 
  }
  
  private static void setByteField(Object paramObject, Class<?> paramClass, String paramString, byte paramByte) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == byte.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putByte(paramObject, l, paramByte);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetByteField(exception, paramString, paramObject.toString(), new Byte(paramByte)); 
      throw utilWrapper.errorSetByteField(exception, paramString, "null " + paramClass.getName() + " object", new Byte(paramByte));
    } 
  }
  
  private static void setCharField(Object paramObject, Class<?> paramClass, String paramString, char paramChar) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == char.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putChar(paramObject, l, paramChar);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetCharField(exception, paramString, paramObject.toString(), new Character(paramChar)); 
      throw utilWrapper.errorSetCharField(exception, paramString, "null " + paramClass.getName() + " object", new Character(paramChar));
    } 
  }
  
  private static void setShortField(Object paramObject, Class<?> paramClass, String paramString, short paramShort) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == short.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putShort(paramObject, l, paramShort);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetShortField(exception, paramString, paramObject.toString(), new Short(paramShort)); 
      throw utilWrapper.errorSetShortField(exception, paramString, "null " + paramClass.getName() + " object", new Short(paramShort));
    } 
  }
  
  private static void setIntField(Object paramObject, Class<?> paramClass, String paramString, int paramInt) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == int.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putInt(paramObject, l, paramInt);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetIntField(exception, paramString, paramObject.toString(), new Integer(paramInt)); 
      throw utilWrapper.errorSetIntField(exception, paramString, "null " + paramClass.getName() + " object", new Integer(paramInt));
    } 
  }
  
  private static void setLongField(Object paramObject, Class<?> paramClass, String paramString, long paramLong) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == long.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putLong(paramObject, l, paramLong);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetLongField(exception, paramString, paramObject.toString(), new Long(paramLong)); 
      throw utilWrapper.errorSetLongField(exception, paramString, "null " + paramClass.getName() + " object", new Long(paramLong));
    } 
  }
  
  private static void setFloatField(Object paramObject, Class<?> paramClass, String paramString, float paramFloat) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == float.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putFloat(paramObject, l, paramFloat);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetFloatField(exception, paramString, paramObject.toString(), new Float(paramFloat)); 
      throw utilWrapper.errorSetFloatField(exception, paramString, "null " + paramClass.getName() + " object", new Float(paramFloat));
    } 
  }
  
  private static void setDoubleField(Object paramObject, Class<?> paramClass, String paramString, double paramDouble) {
    try {
      Field field = getDeclaredField(paramClass, paramString);
      if (field != null && field.getType() == double.class) {
        long l = bridge.objectFieldOffset(field);
        bridge.putDouble(paramObject, l, paramDouble);
      } else {
        throw new InvalidObjectException("Field Type mismatch");
      } 
    } catch (Exception exception) {
      if (paramObject != null)
        throw utilWrapper.errorSetDoubleField(exception, paramString, paramObject.toString(), new Double(paramDouble)); 
      throw utilWrapper.errorSetDoubleField(exception, paramString, "null " + paramClass.getName() + " object", new Double(paramDouble));
    } 
  }
  
  private static Field getDeclaredField(final Class<?> c, final String fieldName) throws PrivilegedActionException, NoSuchFieldException, SecurityException { return (System.getSecurityManager() == null) ? paramClass.getDeclaredField(paramString) : (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
          public Field run() throws NoSuchFieldException { return c.getDeclaredField(fieldName); }
        }); }
  
  static class ActiveRecursionManager {
    private Map<Integer, Object> offsetToObjectMap = new HashMap();
    
    public void addObject(int param1Int, Object param1Object) { this.offsetToObjectMap.put(new Integer(param1Int), param1Object); }
    
    public Object getObject(int param1Int) throws IOException {
      Integer integer = new Integer(param1Int);
      if (!this.offsetToObjectMap.containsKey(integer))
        throw new IOException("Invalid indirection to offset " + param1Int); 
      return this.offsetToObjectMap.get(integer);
    }
    
    public void removeObject(int param1Int) { this.offsetToObjectMap.remove(new Integer(param1Int)); }
    
    public boolean containsObject(int param1Int) { return this.offsetToObjectMap.containsKey(new Integer(param1Int)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\IIOPInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */