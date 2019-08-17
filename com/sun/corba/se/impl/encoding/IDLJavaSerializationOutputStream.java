package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.OutputStream;

final class IDLJavaSerializationOutputStream extends CDROutputStreamBase {
  private ORB orb;
  
  private byte encodingVersion;
  
  private ObjectOutputStream os;
  
  private _ByteArrayOutputStream bos;
  
  private BufferManagerWrite bufferManager;
  
  private final int directWriteLength = 16;
  
  protected ORBUtilSystemException wrapper;
  
  public IDLJavaSerializationOutputStream(byte paramByte) { this.encodingVersion = paramByte; }
  
  public void init(ORB paramORB, boolean paramBoolean1, BufferManagerWrite paramBufferManagerWrite, byte paramByte, boolean paramBoolean2) {
    this.orb = (ORB)paramORB;
    this.bufferManager = paramBufferManagerWrite;
    this.wrapper = ORBUtilSystemException.get((ORB)paramORB, "rpc.encoding");
    this.bos = new _ByteArrayOutputStream(1024);
  }
  
  private void initObjectOutputStream() {
    if (this.os != null)
      throw this.wrapper.javaStreamInitFailed(); 
    try {
      this.os = new MarshalObjectOutputStream(this.bos, this.orb);
    } catch (Exception exception) {
      throw this.wrapper.javaStreamInitFailed(exception);
    } 
  }
  
  public final void write_boolean(boolean paramBoolean) {
    try {
      this.os.writeBoolean(paramBoolean);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_boolean");
    } 
  }
  
  public final void write_char(char paramChar) {
    try {
      this.os.writeChar(paramChar);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_char");
    } 
  }
  
  public final void write_wchar(char paramChar) { write_char(paramChar); }
  
  public final void write_octet(byte paramByte) {
    if (this.bos.size() < 16) {
      this.bos.write(paramByte);
      if (this.bos.size() == 16)
        initObjectOutputStream(); 
      return;
    } 
    try {
      this.os.writeByte(paramByte);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_octet");
    } 
  }
  
  public final void write_short(short paramShort) {
    try {
      this.os.writeShort(paramShort);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_short");
    } 
  }
  
  public final void write_ushort(short paramShort) { write_short(paramShort); }
  
  public final void write_long(int paramInt) {
    if (this.bos.size() < 16) {
      this.bos.write((byte)(paramInt >>> 24 & 0xFF));
      this.bos.write((byte)(paramInt >>> 16 & 0xFF));
      this.bos.write((byte)(paramInt >>> 8 & 0xFF));
      this.bos.write((byte)(paramInt >>> 0 & 0xFF));
      if (this.bos.size() == 16) {
        initObjectOutputStream();
      } else if (this.bos.size() > 16) {
        this.wrapper.javaSerializationException("write_long");
      } 
      return;
    } 
    try {
      this.os.writeInt(paramInt);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_long");
    } 
  }
  
  public final void write_ulong(int paramInt) { write_long(paramInt); }
  
  public final void write_longlong(long paramLong) {
    try {
      this.os.writeLong(paramLong);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_longlong");
    } 
  }
  
  public final void write_ulonglong(long paramLong) { write_longlong(paramLong); }
  
  public final void write_float(float paramFloat) {
    try {
      this.os.writeFloat(paramFloat);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_float");
    } 
  }
  
  public final void write_double(double paramDouble) {
    try {
      this.os.writeDouble(paramDouble);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_double");
    } 
  }
  
  public final void write_string(String paramString) {
    try {
      this.os.writeUTF(paramString);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_string");
    } 
  }
  
  public final void write_wstring(String paramString) {
    try {
      this.os.writeObject(paramString);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_wstring");
    } 
  }
  
  public final void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_boolean(paramArrayOfBoolean[paramInt1 + i]); 
  }
  
  public final void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_char(paramArrayOfChar[paramInt1 + i]); 
  }
  
  public final void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { write_char_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public final void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      this.os.write(paramArrayOfByte, paramInt1, paramInt2);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_octet_array");
    } 
  }
  
  public final void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_short(paramArrayOfShort[paramInt1 + i]); 
  }
  
  public final void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { write_short_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public final void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_long(paramArrayOfInt[paramInt1 + i]); 
  }
  
  public final void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { write_long_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public final void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_longlong(paramArrayOfLong[paramInt1 + i]); 
  }
  
  public final void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { write_longlong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public final void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_float(paramArrayOfFloat[paramInt1 + i]); 
  }
  
  public final void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_double(paramArrayOfDouble[paramInt1 + i]); 
  }
  
  public final void write_Object(Object paramObject) {
    if (paramObject == null) {
      IOR iOR1 = IORFactories.makeIOR(this.orb);
      iOR1.write(this.parent);
      return;
    } 
    if (paramObject instanceof org.omg.CORBA.LocalObject)
      throw this.wrapper.writeLocalObject(CompletionStatus.COMPLETED_MAYBE); 
    IOR iOR = ORBUtility.connectAndGetIOR(this.orb, paramObject);
    iOR.write(this.parent);
  }
  
  public final void write_TypeCode(TypeCode paramTypeCode) {
    TypeCodeImpl typeCodeImpl;
    if (paramTypeCode == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    if (paramTypeCode instanceof TypeCodeImpl) {
      typeCodeImpl = (TypeCodeImpl)paramTypeCode;
    } else {
      typeCodeImpl = new TypeCodeImpl(this.orb, paramTypeCode);
    } 
    typeCodeImpl.write_value(this.parent);
  }
  
  public final void write_any(Any paramAny) {
    if (paramAny == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    write_TypeCode(paramAny.type());
    paramAny.write_value(this.parent);
  }
  
  public final void write_Principal(Principal paramPrincipal) {
    write_long(paramPrincipal.name().length);
    write_octet_array(paramPrincipal.name(), 0, paramPrincipal.name().length);
  }
  
  public final void write_fixed(BigDecimal paramBigDecimal) { write_fixed(paramBigDecimal.toString(), paramBigDecimal.signum()); }
  
  private void write_fixed(String paramString, int paramInt) {
    int i = paramString.length();
    byte b = 0;
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if (c != '-' && c != '+' && c != '.')
        b1++; 
    } 
    for (b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if (c != '-' && c != '+' && c != '.') {
        byte b3 = (byte)Character.digit(c, 10);
        if (b3 == -1)
          throw this.wrapper.badDigitInFixed(CompletionStatus.COMPLETED_MAYBE); 
        if (b1 % 2 == 0) {
          b = (byte)(b | b3);
          write_octet(b);
          b = 0;
        } else {
          b = (byte)(b | b3 << 4);
        } 
        b1--;
      } 
    } 
    if (paramInt == -1) {
      b = (byte)(b | 0xD);
    } else {
      b = (byte)(b | 0xC);
    } 
    write_octet(b);
  }
  
  public final ORB orb() { return this.orb; }
  
  public final void write_value(Serializable paramSerializable) { write_value(paramSerializable, (String)null); }
  
  public final void write_value(Serializable paramSerializable, Class paramClass) { write_value(paramSerializable); }
  
  public final void write_value(Serializable paramSerializable, String paramString) {
    try {
      this.os.writeObject(paramSerializable);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_value");
    } 
  }
  
  public final void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper) { write_value(paramSerializable, (String)null); }
  
  public final void write_abstract_interface(Object paramObject) {
    boolean bool = false;
    Object object = null;
    if (paramObject != null && paramObject instanceof Object) {
      object = (Object)paramObject;
      bool = true;
    } 
    write_boolean(bool);
    if (bool) {
      write_Object(object);
    } else {
      try {
        write_value((Serializable)paramObject);
      } catch (ClassCastException classCastException) {
        if (paramObject instanceof Serializable)
          throw classCastException; 
        ORBUtility.throwNotSerializableForCorba(paramObject.getClass().getName());
      } 
    } 
  }
  
  public final void start_block() { throw this.wrapper.giopVersionError(); }
  
  public final void end_block() { throw this.wrapper.giopVersionError(); }
  
  public final void putEndian() { throw this.wrapper.giopVersionError(); }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException {
    try {
      this.os.flush();
      this.bos.writeTo(paramOutputStream);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "writeTo");
    } 
  }
  
  public final byte[] toByteArray() {
    try {
      this.os.flush();
      return this.bos.toByteArray();
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "toByteArray");
    } 
  }
  
  public final void write_Abstract(Object paramObject) { write_abstract_interface(paramObject); }
  
  public final void write_Value(Serializable paramSerializable) { write_value(paramSerializable); }
  
  public final void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_any(paramArrayOfAny[paramInt1 + i]); 
  }
  
  public final String[] _truncatable_ids() { throw this.wrapper.giopVersionError(); }
  
  public final int getSize() {
    try {
      this.os.flush();
      return this.bos.size();
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "write_boolean");
    } 
  }
  
  public final int getIndex() { return getSize(); }
  
  protected int getRealIndex(int paramInt) { return getSize(); }
  
  public final void setIndex(int paramInt) { throw this.wrapper.giopVersionError(); }
  
  public final ByteBuffer getByteBuffer() { throw this.wrapper.giopVersionError(); }
  
  public final void setByteBuffer(ByteBuffer paramByteBuffer) { throw this.wrapper.giopVersionError(); }
  
  public final boolean isLittleEndian() { return false; }
  
  public ByteBufferWithInfo getByteBufferWithInfo() {
    try {
      this.os.flush();
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "getByteBufferWithInfo");
    } 
    ByteBuffer byteBuffer = ByteBuffer.wrap(this.bos.getByteArray());
    byteBuffer.limit(this.bos.size());
    return new ByteBufferWithInfo(this.orb, byteBuffer, this.bos.size());
  }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) { throw this.wrapper.giopVersionError(); }
  
  public final BufferManagerWrite getBufferManager() { return this.bufferManager; }
  
  public final void write_fixed(BigDecimal paramBigDecimal, short paramShort1, short paramShort2) {
    String str3;
    String str2;
    String str1 = paramBigDecimal.toString();
    if (str1.charAt(0) == '-' || str1.charAt(0) == '+')
      str1 = str1.substring(1); 
    int i = str1.indexOf('.');
    if (i == -1) {
      str2 = str1;
      str3 = null;
    } else if (i == 0) {
      str2 = null;
      str3 = str1;
    } else {
      str2 = str1.substring(0, i);
      str3 = str1.substring(i + 1);
    } 
    StringBuffer stringBuffer = new StringBuffer(paramShort1);
    if (str3 != null)
      stringBuffer.append(str3); 
    while (stringBuffer.length() < paramShort2)
      stringBuffer.append('0'); 
    if (str2 != null)
      stringBuffer.insert(0, str2); 
    while (stringBuffer.length() < paramShort1)
      stringBuffer.insert(0, '0'); 
    write_fixed(stringBuffer.toString(), paramBigDecimal.signum());
  }
  
  public final void writeOctetSequenceTo(OutputStream paramOutputStream) {
    byte[] arrayOfByte = toByteArray();
    paramOutputStream.write_long(arrayOfByte.length);
    paramOutputStream.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public final GIOPVersion getGIOPVersion() { return GIOPVersion.V1_2; }
  
  public final void writeIndirection(int paramInt1, int paramInt2) { throw this.wrapper.giopVersionError(); }
  
  void freeInternalCaches() {}
  
  void printBuffer() {
    byte[] arrayOfByte = toByteArray();
    System.out.println("+++++++ Output Buffer ++++++++");
    System.out.println();
    System.out.println("Current position: " + arrayOfByte.length);
    System.out.println();
    char[] arrayOfChar = new char[16];
    try {
      for (byte b = 0; b < arrayOfByte.length; b += 16) {
        byte b1;
        for (b1 = 0; b1 < 16 && b1 + b < arrayOfByte.length; b1++) {
          char c = arrayOfByte[b + b1];
          if (c < 0)
            c = 'Ā' + c; 
          String str = Integer.toHexString(c);
          if (str.length() == 1)
            str = "0" + str; 
          System.out.print(str + " ");
        } 
        while (b1 < 16) {
          System.out.print("   ");
          b1++;
        } 
        byte b2;
        for (b2 = 0; b2 < 16 && b2 + b < arrayOfByte.length; b2++) {
          if (ORBUtility.isPrintable((char)arrayOfByte[b + b2])) {
            arrayOfChar[b2] = (char)arrayOfByte[b + b2];
          } else {
            arrayOfChar[b2] = '.';
          } 
        } 
        System.out.println(new String(arrayOfChar, 0, b2));
      } 
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
    System.out.println("++++++++++++++++++++++++++++++");
  }
  
  public void alignOnBoundary(int paramInt) { throw this.wrapper.giopVersionError(); }
  
  public void setHeaderPadding(boolean paramBoolean) {}
  
  public void start_value(String paramString) { throw this.wrapper.giopVersionError(); }
  
  public void end_value() { throw this.wrapper.giopVersionError(); }
  
  class MarshalObjectOutputStream extends ObjectOutputStream {
    ORB orb;
    
    MarshalObjectOutputStream(OutputStream param1OutputStream, ORB param1ORB) throws IOException {
      super(param1OutputStream);
      this.orb = param1ORB;
      AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              IDLJavaSerializationOutputStream.MarshalObjectOutputStream.this.enableReplaceObject(true);
              return null;
            }
          });
    }
    
    protected final Object replaceObject(Object param1Object) throws IOException {
      try {
        if (param1Object instanceof java.rmi.Remote && !StubAdapter.isStub(param1Object))
          return Utility.autoConnect(param1Object, this.orb, true); 
      } catch (Exception exception) {
        IOException iOException = new IOException("replaceObject failed");
        iOException.initCause(exception);
        throw iOException;
      } 
      return param1Object;
    }
  }
  
  class _ByteArrayOutputStream extends ByteArrayOutputStream {
    _ByteArrayOutputStream(int param1Int) { super(param1Int); }
    
    byte[] getByteArray() { return this.buf; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\IDLJavaSerializationOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */