package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.RepositoryId;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotActiveException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Stack;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.Bridge;

public class IIOPOutputStream extends OutputStreamHook {
  private UtilSystemException wrapper = UtilSystemException.get("rpc.encoding");
  
  private static Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction() {
        public Object run() { return Bridge.get(); }
      });
  
  private OutputStream orbStream;
  
  private Object currentObject = null;
  
  private ObjectStreamClass currentClassDesc = null;
  
  private int recursionDepth = 0;
  
  private int simpleWriteDepth = 0;
  
  private IOException abortIOException = null;
  
  private Stack classDescStack = new Stack();
  
  private Object[] writeObjectArgList = { this };
  
  protected void beginOptionalCustomData() throws IOException {
    if (this.streamFormatVersion == 2) {
      ValueOutputStream valueOutputStream = (ValueOutputStream)this.orbStream;
      valueOutputStream.start_value(this.currentClassDesc.getRMIIIOPOptionalDataRepId());
    } 
  }
  
  final void setOrbStream(OutputStream paramOutputStream) { this.orbStream = paramOutputStream; }
  
  final OutputStream getOrbStream() { return this.orbStream; }
  
  final void increaseRecursionDepth() throws IOException { this.recursionDepth++; }
  
  final int decreaseRecursionDepth() { return --this.recursionDepth; }
  
  public final void writeObjectOverride(Object paramObject) throws IOException {
    this.writeObjectState.writeData(this);
    Util.writeAbstractObject(this.orbStream, paramObject);
  }
  
  public final void simpleWriteObject(Object paramObject, byte paramByte) {
    b = this.streamFormatVersion;
    this.streamFormatVersion = paramByte;
    object = this.currentObject;
    objectStreamClass = this.currentClassDesc;
    this.simpleWriteDepth++;
    try {
      outputObject(paramObject);
    } catch (IOException iOException1) {
      if (this.abortIOException == null)
        this.abortIOException = iOException1; 
    } finally {
      this.streamFormatVersion = b;
      this.simpleWriteDepth--;
      this.currentObject = object;
      this.currentClassDesc = objectStreamClass;
    } 
    IOException iOException = this.abortIOException;
    if (this.simpleWriteDepth == 0)
      this.abortIOException = null; 
    if (iOException != null)
      bridge.throwException(iOException); 
  }
  
  ObjectStreamField[] getFieldsNoCopy() { return this.currentClassDesc.getFieldsNoCopy(); }
  
  public final void defaultWriteObjectDelegate() throws IOException {
    try {
      if (this.currentObject == null || this.currentClassDesc == null)
        throw new NotActiveException("defaultWriteObjectDelegate"); 
      ObjectStreamField[] arrayOfObjectStreamField = this.currentClassDesc.getFieldsNoCopy();
      if (arrayOfObjectStreamField.length > 0)
        outputClassFields(this.currentObject, this.currentClassDesc.forClass(), arrayOfObjectStreamField); 
    } catch (IOException iOException) {
      bridge.throwException(iOException);
    } 
  }
  
  public final boolean enableReplaceObjectDelegate(boolean paramBoolean) { return false; }
  
  protected final void annotateClass(Class<?> paramClass) throws IOException { throw new IOException("Method annotateClass not supported"); }
  
  public final void close() throws IOException {}
  
  protected final void drain() throws IOException {}
  
  public final void flush() throws IOException {
    try {
      this.orbStream.flush();
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  protected final Object replaceObject(Object paramObject) throws IOException { throw new IOException("Method replaceObject not supported"); }
  
  public final void reset() throws IOException {
    try {
      if (this.currentObject != null || this.currentClassDesc != null)
        throw new IOException("Illegal call to reset"); 
      this.abortIOException = null;
      if (this.classDescStack == null) {
        this.classDescStack = new Stack();
      } else {
        this.classDescStack.setSize(0);
      } 
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void write(byte[] paramArrayOfByte) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_octet_array(paramArrayOfByte, 0, paramArrayOfByte.length);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_octet_array(paramArrayOfByte, paramInt1, paramInt2);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void write(int paramInt) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_octet((byte)(paramInt & 0xFF));
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeBoolean(boolean paramBoolean) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_boolean(paramBoolean);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeByte(int paramInt) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_octet((byte)paramInt);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeBytes(String paramString) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      byte[] arrayOfByte = paramString.getBytes();
      this.orbStream.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeChar(int paramInt) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_wchar((char)paramInt);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeChars(String paramString) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      char[] arrayOfChar = paramString.toCharArray();
      this.orbStream.write_wchar_array(arrayOfChar, 0, arrayOfChar.length);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeDouble(double paramDouble) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_double(paramDouble);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeFloat(float paramFloat) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_float(paramFloat);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeInt(int paramInt) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_long(paramInt);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeLong(long paramLong) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_longlong(paramLong);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  public final void writeShort(int paramInt) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      this.orbStream.write_short((short)paramInt);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  protected final void writeStreamHeader() throws IOException {}
  
  protected void internalWriteUTF(OutputStream paramOutputStream, String paramString) { paramOutputStream.write_wstring(paramString); }
  
  public final void writeUTF(String paramString) throws IOException {
    try {
      this.writeObjectState.writeData(this);
      internalWriteUTF(this.orbStream, paramString);
    } catch (Error error) {
      IOException iOException = new IOException(error.getMessage());
      iOException.initCause(error);
      throw iOException;
    } 
  }
  
  private boolean checkSpecialClasses(Object paramObject) throws IOException {
    if (paramObject instanceof ObjectStreamClass)
      throw new IOException("Serialization of ObjectStreamClass not supported"); 
    return false;
  }
  
  private boolean checkSubstitutableSpecialClasses(Object paramObject) throws IOException {
    if (paramObject instanceof String) {
      this.orbStream.write_value((Serializable)paramObject);
      return true;
    } 
    return false;
  }
  
  private void outputObject(Object paramObject) throws IOException {
    this.currentObject = paramObject;
    Class clazz = paramObject.getClass();
    this.currentClassDesc = ObjectStreamClass.lookup(clazz);
    if (this.currentClassDesc == null)
      throw new NotSerializableException(clazz.getName()); 
    if (this.currentClassDesc.isExternalizable()) {
      this.orbStream.write_octet(this.streamFormatVersion);
      Externalizable externalizable = (Externalizable)paramObject;
      externalizable.writeExternal(this);
    } else {
      if (this.currentClassDesc.forClass().getName().equals("java.lang.String")) {
        writeUTF((String)paramObject);
        return;
      } 
      i = this.classDescStack.size();
      try {
        ObjectStreamClass objectStreamClass;
        while ((objectStreamClass = this.currentClassDesc.getSuperclass()) != null) {
          this.classDescStack.push(this.currentClassDesc);
          this.currentClassDesc = objectStreamClass;
        } 
        do {
          writeObjectState = this.writeObjectState;
          try {
            setState(NOT_IN_WRITE_OBJECT);
            if (this.currentClassDesc.hasWriteObject()) {
              invokeObjectWriter(this.currentClassDesc, paramObject);
            } else {
              defaultWriteObjectDelegate();
            } 
          } finally {
            setState(writeObjectState);
          } 
        } while (this.classDescStack.size() > i && (this.currentClassDesc = (ObjectStreamClass)this.classDescStack.pop()) != null);
      } finally {
        this.classDescStack.setSize(i);
      } 
    } 
  }
  
  private void invokeObjectWriter(ObjectStreamClass paramObjectStreamClass, Object paramObject) throws IOException {
    Class clazz = paramObjectStreamClass.forClass();
    try {
      this.orbStream.write_octet(this.streamFormatVersion);
      this.writeObjectState.enterWriteObject(this);
      paramObjectStreamClass.writeObjectMethod.invoke(paramObject, this.writeObjectArgList);
      this.writeObjectState.exitWriteObject(this);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (throwable instanceof IOException)
        throw (IOException)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new Error("invokeObjectWriter internal error", invocationTargetException);
    } catch (IllegalAccessException illegalAccessException) {}
  }
  
  void writeField(ObjectStreamField paramObjectStreamField, Object paramObject) throws IOException {
    switch (paramObjectStreamField.getTypeCode()) {
      case 'B':
        if (paramObject == null) {
          this.orbStream.write_octet((byte)0);
        } else {
          this.orbStream.write_octet(((Byte)paramObject).byteValue());
        } 
        return;
      case 'C':
        if (paramObject == null) {
          this.orbStream.write_wchar(false);
        } else {
          this.orbStream.write_wchar(((Character)paramObject).charValue());
        } 
        return;
      case 'F':
        if (paramObject == null) {
          this.orbStream.write_float(0.0F);
        } else {
          this.orbStream.write_float(((Float)paramObject).floatValue());
        } 
        return;
      case 'D':
        if (paramObject == null) {
          this.orbStream.write_double(0.0D);
        } else {
          this.orbStream.write_double(((Double)paramObject).doubleValue());
        } 
        return;
      case 'I':
        if (paramObject == null) {
          this.orbStream.write_long(0);
        } else {
          this.orbStream.write_long(((Integer)paramObject).intValue());
        } 
        return;
      case 'J':
        if (paramObject == null) {
          this.orbStream.write_longlong(0L);
        } else {
          this.orbStream.write_longlong(((Long)paramObject).longValue());
        } 
        return;
      case 'S':
        if (paramObject == null) {
          this.orbStream.write_short((short)0);
        } else {
          this.orbStream.write_short(((Short)paramObject).shortValue());
        } 
        return;
      case 'Z':
        if (paramObject == null) {
          this.orbStream.write_boolean(false);
        } else {
          this.orbStream.write_boolean(((Boolean)paramObject).booleanValue());
        } 
        return;
      case 'L':
      case '[':
        writeObjectField(paramObjectStreamField, paramObject);
        return;
    } 
    throw new InvalidClassException(this.currentClassDesc.getName());
  }
  
  private void writeObjectField(ObjectStreamField paramObjectStreamField, Object paramObject) throws IOException {
    if (ObjectStreamClassCorbaExt.isAny(paramObjectStreamField.getTypeString())) {
      Util.writeAny(this.orbStream, paramObject);
    } else {
      Class clazz = paramObjectStreamField.getType();
      byte b = 2;
      if (clazz.isInterface()) {
        String str = clazz.getName();
        if (java.rmi.Remote.class.isAssignableFrom(clazz)) {
          b = 0;
        } else if (org.omg.CORBA.Object.class.isAssignableFrom(clazz)) {
          b = 0;
        } else if (RepositoryId.isAbstractBase(clazz)) {
          b = 1;
        } else if (ObjectStreamClassCorbaExt.isAbstractInterface(clazz)) {
          b = 1;
        } 
      } 
      switch (b) {
        case 0:
          Util.writeRemoteObject(this.orbStream, paramObject);
          break;
        case 1:
          Util.writeAbstractObject(this.orbStream, paramObject);
          break;
        case 2:
          try {
            this.orbStream.write_value((Serializable)paramObject, clazz);
          } catch (ClassCastException classCastException) {}
          break;
      } 
    } 
  }
  
  private void outputClassFields(Object paramObject, Class paramClass, ObjectStreamField[] paramArrayOfObjectStreamField) throws IOException, InvalidClassException {
    for (byte b = 0; b < paramArrayOfObjectStreamField.length; b++) {
      if (paramArrayOfObjectStreamField[b].getField() == null)
        throw new InvalidClassException(paramClass.getName(), "Nonexistent field " + paramArrayOfObjectStreamField[b].getName()); 
      try {
        Object object;
        boolean bool;
        short s;
        long l;
        int i;
        double d;
        float f;
        char c;
        byte b1;
        switch (paramArrayOfObjectStreamField[b].getTypeCode()) {
          case 'B':
            b1 = paramArrayOfObjectStreamField[b].getField().getByte(paramObject);
            this.orbStream.write_octet(b1);
            break;
          case 'C':
            c = paramArrayOfObjectStreamField[b].getField().getChar(paramObject);
            this.orbStream.write_wchar(c);
            break;
          case 'F':
            f = paramArrayOfObjectStreamField[b].getField().getFloat(paramObject);
            this.orbStream.write_float(f);
            break;
          case 'D':
            d = paramArrayOfObjectStreamField[b].getField().getDouble(paramObject);
            this.orbStream.write_double(d);
            break;
          case 'I':
            i = paramArrayOfObjectStreamField[b].getField().getInt(paramObject);
            this.orbStream.write_long(i);
            break;
          case 'J':
            l = paramArrayOfObjectStreamField[b].getField().getLong(paramObject);
            this.orbStream.write_longlong(l);
            break;
          case 'S':
            s = paramArrayOfObjectStreamField[b].getField().getShort(paramObject);
            this.orbStream.write_short(s);
            break;
          case 'Z':
            bool = paramArrayOfObjectStreamField[b].getField().getBoolean(paramObject);
            this.orbStream.write_boolean(bool);
            break;
          case 'L':
          case '[':
            object = paramArrayOfObjectStreamField[b].getField().get(paramObject);
            writeObjectField(paramArrayOfObjectStreamField[b], object);
            break;
          default:
            throw new InvalidClassException(paramClass.getName());
        } 
      } catch (IllegalAccessException illegalAccessException) {
        throw this.wrapper.illegalFieldAccess(illegalAccessException, paramArrayOfObjectStreamField[b].getName());
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\IIOPOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */