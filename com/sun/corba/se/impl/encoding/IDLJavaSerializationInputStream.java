package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.PrincipalImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.Context;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.portable.BoxedValueHelper;

public class IDLJavaSerializationInputStream extends CDRInputStreamBase {
  private ORB orb;
  
  private int bufSize;
  
  private ByteBuffer buffer;
  
  private byte encodingVersion;
  
  private ObjectInputStream is;
  
  private _ByteArrayInputStream bis;
  
  private BufferManagerRead bufferManager;
  
  private final int directReadLength = 16;
  
  private boolean markOn;
  
  private int peekIndex;
  
  private int peekCount;
  
  private LinkedList markedItemQ = new LinkedList();
  
  protected ORBUtilSystemException wrapper;
  
  public IDLJavaSerializationInputStream(byte paramByte) { this.encodingVersion = paramByte; }
  
  public void init(ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, BufferManagerRead paramBufferManagerRead) {
    byte[] arrayOfByte;
    this.orb = (ORB)paramORB;
    this.bufSize = paramInt;
    this.bufferManager = paramBufferManagerRead;
    this.buffer = paramByteBuffer;
    this.wrapper = ORBUtilSystemException.get((ORB)paramORB, "rpc.encoding");
    if (this.buffer.hasArray()) {
      arrayOfByte = this.buffer.array();
    } else {
      arrayOfByte = new byte[paramInt];
      this.buffer.get(arrayOfByte);
    } 
    this.bis = new _ByteArrayInputStream(arrayOfByte);
  }
  
  private void initObjectInputStream() {
    if (this.is != null)
      throw this.wrapper.javaStreamInitFailed(); 
    try {
      this.is = new MarshalObjectInputStream(this.bis, this.orb);
    } catch (Exception exception) {
      throw this.wrapper.javaStreamInitFailed(exception);
    } 
  }
  
  public boolean read_boolean() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Boolean)this.markedItemQ.removeFirst()).booleanValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Boolean)this.markedItemQ.get(this.peekIndex++)).booleanValue(); 
    try {
      boolean bool = this.is.readBoolean();
      if (this.markOn)
        this.markedItemQ.addLast(Boolean.valueOf(bool)); 
      return bool;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_boolean");
    } 
  }
  
  public char read_char() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Character)this.markedItemQ.removeFirst()).charValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Character)this.markedItemQ.get(this.peekIndex++)).charValue(); 
    try {
      char c = this.is.readChar();
      if (this.markOn)
        this.markedItemQ.addLast(new Character(c)); 
      return c;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_char");
    } 
  }
  
  public char read_wchar() { return read_char(); }
  
  public byte read_octet() {
    if (this.bis.getPosition() < 16) {
      byte b = (byte)this.bis.read();
      if (this.bis.getPosition() == 16)
        initObjectInputStream(); 
      return b;
    } 
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Byte)this.markedItemQ.removeFirst()).byteValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Byte)this.markedItemQ.get(this.peekIndex++)).byteValue(); 
    try {
      byte b = this.is.readByte();
      if (this.markOn)
        this.markedItemQ.addLast(new Byte(b)); 
      return b;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_octet");
    } 
  }
  
  public short read_short() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Short)this.markedItemQ.removeFirst()).shortValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Short)this.markedItemQ.get(this.peekIndex++)).shortValue(); 
    try {
      short s = this.is.readShort();
      if (this.markOn)
        this.markedItemQ.addLast(new Short(s)); 
      return s;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_short");
    } 
  }
  
  public short read_ushort() { return read_short(); }
  
  public int read_long() {
    if (this.bis.getPosition() < 16) {
      int i = this.bis.read() << 24 & 0xFF000000;
      int j = this.bis.read() << 16 & 0xFF0000;
      int k = this.bis.read() << 8 & 0xFF00;
      int m = this.bis.read() << 0 & 0xFF;
      if (this.bis.getPosition() == 16) {
        initObjectInputStream();
      } else if (this.bis.getPosition() > 16) {
        this.wrapper.javaSerializationException("read_long");
      } 
      return i | j | k | m;
    } 
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Integer)this.markedItemQ.removeFirst()).intValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Integer)this.markedItemQ.get(this.peekIndex++)).intValue(); 
    try {
      int i = this.is.readInt();
      if (this.markOn)
        this.markedItemQ.addLast(new Integer(i)); 
      return i;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_long");
    } 
  }
  
  public int read_ulong() { return read_long(); }
  
  public long read_longlong() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Long)this.markedItemQ.removeFirst()).longValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Long)this.markedItemQ.get(this.peekIndex++)).longValue(); 
    try {
      long l = this.is.readLong();
      if (this.markOn)
        this.markedItemQ.addLast(new Long(l)); 
      return l;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_longlong");
    } 
  }
  
  public long read_ulonglong() { return read_longlong(); }
  
  public float read_float() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Float)this.markedItemQ.removeFirst()).floatValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Float)this.markedItemQ.get(this.peekIndex++)).floatValue(); 
    try {
      float f = this.is.readFloat();
      if (this.markOn)
        this.markedItemQ.addLast(new Float(f)); 
      return f;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_float");
    } 
  }
  
  public double read_double() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return ((Double)this.markedItemQ.removeFirst()).doubleValue(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return ((Double)this.markedItemQ.get(this.peekIndex++)).doubleValue(); 
    try {
      double d = this.is.readDouble();
      if (this.markOn)
        this.markedItemQ.addLast(new Double(d)); 
      return d;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_double");
    } 
  }
  
  public String read_string() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return (String)this.markedItemQ.removeFirst(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return (String)this.markedItemQ.get(this.peekIndex++); 
    try {
      String str = this.is.readUTF();
      if (this.markOn)
        this.markedItemQ.addLast(str); 
      return str;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_string");
    } 
  }
  
  public String read_wstring() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return (String)this.markedItemQ.removeFirst(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return (String)this.markedItemQ.get(this.peekIndex++); 
    try {
      String str = (String)this.is.readObject();
      if (this.markOn)
        this.markedItemQ.addLast(str); 
      return str;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_wstring");
    } 
  }
  
  public void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfBoolean[i + paramInt1] = read_boolean(); 
  }
  
  public void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfChar[i + paramInt1] = read_char(); 
  }
  
  public void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { read_char_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfByte[i + paramInt1] = read_octet(); 
  }
  
  public void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfShort[i + paramInt1] = read_short(); 
  }
  
  public void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { read_short_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfInt[i + paramInt1] = read_long(); 
  }
  
  public void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { read_long_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfLong[i + paramInt1] = read_longlong(); 
  }
  
  public void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { read_longlong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfFloat[i + paramInt1] = read_float(); 
  }
  
  public void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfDouble[i + paramInt1] = read_double(); 
  }
  
  public Object read_Object() { return read_Object(null); }
  
  public TypeCode read_TypeCode() {
    TypeCodeImpl typeCodeImpl = new TypeCodeImpl(this.orb);
    typeCodeImpl.read_value(this.parent);
    return typeCodeImpl;
  }
  
  public Any read_any() {
    Any any = this.orb.create_any();
    TypeCodeImpl typeCodeImpl = new TypeCodeImpl(this.orb);
    try {
      typeCodeImpl.read_value(this.parent);
    } catch (MARSHAL mARSHAL) {
      if (typeCodeImpl.kind().value() != 29)
        throw mARSHAL; 
      mARSHAL.printStackTrace();
    } 
    any.read_value(this.parent, typeCodeImpl);
    return any;
  }
  
  public Principal read_Principal() {
    int i = read_long();
    byte[] arrayOfByte = new byte[i];
    read_octet_array(arrayOfByte, 0, i);
    PrincipalImpl principalImpl = new PrincipalImpl();
    principalImpl.name(arrayOfByte);
    return principalImpl;
  }
  
  public BigDecimal read_fixed() { return new BigDecimal(read_fixed_buffer().toString()); }
  
  private StringBuffer read_fixed_buffer() {
    StringBuffer stringBuffer = new StringBuffer(64);
    boolean bool1 = false;
    boolean bool2 = true;
    while (bool2) {
      byte b1 = read_octet();
      byte b2 = (b1 & 0xF0) >> 4;
      byte b3 = b1 & 0xF;
      if (bool1 || b2 != 0) {
        stringBuffer.append(Character.forDigit(b2, 10));
        bool1 = true;
      } 
      if (b3 == 12) {
        if (!bool1)
          return new StringBuffer("0.0"); 
        bool2 = false;
        continue;
      } 
      if (b3 == 13) {
        stringBuffer.insert(0, '-');
        bool2 = false;
        continue;
      } 
      stringBuffer.append(Character.forDigit(b3, 10));
      bool1 = true;
    } 
    return stringBuffer;
  }
  
  public Object read_Object(Class paramClass) {
    IOR iOR = IORFactories.makeIOR(this.parent);
    if (iOR.isNil())
      return null; 
    PresentationManager.StubFactoryFactory stubFactoryFactory = ORB.getStubFactoryFactory();
    String str = iOR.getProfile().getCodebase();
    PresentationManager.StubFactory stubFactory = null;
    if (paramClass == null) {
      RepositoryId repositoryId = RepositoryId.cache.getId(iOR.getTypeId());
      String str1 = repositoryId.getClassName();
      boolean bool = repositoryId.isIDLType();
      if (str1 == null || str1.equals("")) {
        stubFactory = null;
      } else {
        try {
          stubFactory = stubFactoryFactory.createStubFactory(str1, bool, str, (Class)null, (ClassLoader)null);
        } catch (Exception exception) {
          stubFactory = null;
        } 
      } 
    } else if (StubAdapter.isStubClass(paramClass)) {
      stubFactory = PresentationDefaults.makeStaticStubFactory(paramClass);
    } else {
      boolean bool = org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(paramClass);
      stubFactory = stubFactoryFactory.createStubFactory(paramClass.getName(), bool, str, paramClass, paramClass.getClassLoader());
    } 
    return CDRInputStream_1_0.internalIORToObject(iOR, stubFactory, this.orb);
  }
  
  public ORB orb() { return this.orb; }
  
  public Serializable read_value() {
    if (!this.markOn && !this.markedItemQ.isEmpty())
      return (Serializable)this.markedItemQ.removeFirst(); 
    if (this.markOn && !this.markedItemQ.isEmpty() && this.peekIndex < this.peekCount)
      return (Serializable)this.markedItemQ.get(this.peekIndex++); 
    try {
      Serializable serializable = (Serializable)this.is.readObject();
      if (this.markOn)
        this.markedItemQ.addLast(serializable); 
      return serializable;
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "read_value");
    } 
  }
  
  public Serializable read_value(Class paramClass) { return read_value(); }
  
  public Serializable read_value(BoxedValueHelper paramBoxedValueHelper) { return read_value(); }
  
  public Serializable read_value(String paramString) { return read_value(); }
  
  public Serializable read_value(Serializable paramSerializable) { return read_value(); }
  
  public Object read_abstract_interface() { return read_abstract_interface(null); }
  
  public Object read_abstract_interface(Class paramClass) {
    boolean bool = read_boolean();
    return bool ? read_Object(paramClass) : read_value();
  }
  
  public void consumeEndian() { throw this.wrapper.giopVersionError(); }
  
  public int getPosition() {
    try {
      return this.bis.getPosition();
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "getPosition");
    } 
  }
  
  public Object read_Abstract() { return read_abstract_interface(); }
  
  public Serializable read_Value() { return read_value(); }
  
  public void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2) { read_any_array(paramAnySeqHolder.value, paramInt1, paramInt2); }
  
  private final void read_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfAny[i + paramInt1] = read_any(); 
  }
  
  public void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2) { read_boolean_array(paramBooleanSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2) { read_char_array(paramCharSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2) { read_wchar_array(paramWCharSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2) { read_octet_array(paramOctetSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2) { read_short_array(paramShortSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2) { read_ushort_array(paramUShortSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2) { read_long_array(paramLongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2) { read_ulong_array(paramULongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2) { read_ulonglong_array(paramULongLongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2) { read_longlong_array(paramLongLongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2) { read_float_array(paramFloatSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2) { read_double_array(paramDoubleSeqHolder.value, paramInt1, paramInt2); }
  
  public String[] _truncatable_ids() { throw this.wrapper.giopVersionError(); }
  
  public void mark(int paramInt) {
    if (this.markOn || this.is == null)
      throw this.wrapper.javaSerializationException("mark"); 
    this.markOn = true;
    if (!this.markedItemQ.isEmpty()) {
      this.peekIndex = 0;
      this.peekCount = this.markedItemQ.size();
    } 
  }
  
  public void reset() {
    this.markOn = false;
    this.peekIndex = 0;
    this.peekCount = 0;
  }
  
  public boolean markSupported() { return true; }
  
  public CDRInputStreamBase dup() {
    CDRInputStreamBase cDRInputStreamBase = null;
    try {
      cDRInputStreamBase = (CDRInputStreamBase)getClass().newInstance();
    } catch (Exception exception) {
      throw this.wrapper.couldNotDuplicateCdrInputStream(exception);
    } 
    cDRInputStreamBase.init(this.orb, this.buffer, this.bufSize, false, null);
    ((IDLJavaSerializationInputStream)cDRInputStreamBase).skipBytes(getPosition());
    ((IDLJavaSerializationInputStream)cDRInputStreamBase).setMarkData(this.markOn, this.peekIndex, this.peekCount, (LinkedList)this.markedItemQ.clone());
    return cDRInputStreamBase;
  }
  
  void skipBytes(int paramInt) {
    try {
      this.is.skipBytes(paramInt);
    } catch (Exception exception) {
      throw this.wrapper.javaSerializationException(exception, "skipBytes");
    } 
  }
  
  void setMarkData(boolean paramBoolean, int paramInt1, int paramInt2, LinkedList paramLinkedList) {
    this.markOn = paramBoolean;
    this.peekIndex = paramInt1;
    this.peekCount = paramInt2;
    this.markedItemQ = paramLinkedList;
  }
  
  public BigDecimal read_fixed(short paramShort1, short paramShort2) {
    StringBuffer stringBuffer = read_fixed_buffer();
    if (paramShort1 != stringBuffer.length())
      throw this.wrapper.badFixed(new Integer(paramShort1), new Integer(stringBuffer.length())); 
    stringBuffer.insert(paramShort1 - paramShort2, '.');
    return new BigDecimal(stringBuffer.toString());
  }
  
  public boolean isLittleEndian() { throw this.wrapper.giopVersionError(); }
  
  void setHeaderPadding(boolean paramBoolean) {}
  
  public ByteBuffer getByteBuffer() { throw this.wrapper.giopVersionError(); }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer) { throw this.wrapper.giopVersionError(); }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) { throw this.wrapper.giopVersionError(); }
  
  public int getBufferLength() { return this.bufSize; }
  
  public void setBufferLength(int paramInt) {}
  
  public int getIndex() { return this.bis.getPosition(); }
  
  public void setIndex(int paramInt) {
    try {
      this.bis.setPosition(paramInt);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw this.wrapper.javaSerializationException(indexOutOfBoundsException, "setIndex");
    } 
  }
  
  public void orb(ORB paramORB) { this.orb = (ORB)paramORB; }
  
  public BufferManagerRead getBufferManager() { return this.bufferManager; }
  
  public GIOPVersion getGIOPVersion() { return GIOPVersion.V1_2; }
  
  CodeBase getCodeBase() { return this.parent.getCodeBase(); }
  
  void printBuffer() {
    byte[] arrayOfByte = this.buffer.array();
    System.out.println("+++++++ Input Buffer ++++++++");
    System.out.println();
    System.out.println("Current position: " + getPosition());
    System.out.println("Total length : " + this.bufSize);
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
  
  void alignOnBoundary(int paramInt) { throw this.wrapper.giopVersionError(); }
  
  void performORBVersionSpecificInit() {}
  
  public void resetCodeSetConverters() {}
  
  public void start_value() { throw this.wrapper.giopVersionError(); }
  
  public void end_value() { throw this.wrapper.giopVersionError(); }
  
  class MarshalObjectInputStream extends ObjectInputStream {
    ORB orb;
    
    MarshalObjectInputStream(InputStream param1InputStream, ORB param1ORB) throws IOException {
      super(param1InputStream);
      this.orb = param1ORB;
      AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              IDLJavaSerializationInputStream.MarshalObjectInputStream.this.enableResolveObject(true);
              return null;
            }
          });
    }
    
    protected final Object resolveObject(Object param1Object) throws IOException {
      try {
        if (StubAdapter.isStub(param1Object))
          StubAdapter.connect(param1Object, this.orb); 
      } catch (RemoteException remoteException) {
        IOException iOException = new IOException("resolveObject failed");
        iOException.initCause(remoteException);
        throw iOException;
      } 
      return param1Object;
    }
  }
  
  class _ByteArrayInputStream extends ByteArrayInputStream {
    _ByteArrayInputStream(byte[] param1ArrayOfByte) { super(param1ArrayOfByte); }
    
    int getPosition() { return this.pos; }
    
    void setPosition(int param1Int) {
      if (param1Int < 0 || param1Int > this.count)
        throw new IndexOutOfBoundsException(); 
      this.pos = param1Int;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\IDLJavaSerializationInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */