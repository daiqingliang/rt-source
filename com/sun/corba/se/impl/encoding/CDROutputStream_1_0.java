package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.CacheTable;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueBase;

public class CDROutputStream_1_0 extends CDROutputStreamBase {
  private static final int INDIRECTION_TAG = -1;
  
  protected boolean littleEndian;
  
  protected BufferManagerWrite bufferManagerWrite;
  
  ByteBufferWithInfo bbwi;
  
  protected ORB orb;
  
  protected ORBUtilSystemException wrapper;
  
  protected boolean debug = false;
  
  protected int blockSizeIndex = -1;
  
  protected int blockSizePosition = 0;
  
  protected byte streamFormatVersion;
  
  private static final int DEFAULT_BUFFER_SIZE = 1024;
  
  private static final String kWriteMethod = "write";
  
  private CacheTable codebaseCache = null;
  
  private CacheTable valueCache = null;
  
  private CacheTable repositoryIdCache = null;
  
  private int end_flag = 0;
  
  private int chunkedValueNestingLevel = 0;
  
  private boolean mustChunk = false;
  
  protected boolean inBlock = false;
  
  private int end_flag_position = 0;
  
  private int end_flag_index = 0;
  
  private ValueHandler valueHandler = null;
  
  private RepositoryIdUtility repIdUtil;
  
  private RepositoryIdStrings repIdStrs;
  
  private CodeSetConversion.CTBConverter charConverter;
  
  private CodeSetConversion.CTBConverter wcharConverter;
  
  private static final String _id = "IDL:omg.org/CORBA/DataOutputStream:1.0";
  
  private static final String[] _ids = { "IDL:omg.org/CORBA/DataOutputStream:1.0" };
  
  public void init(ORB paramORB, boolean paramBoolean1, BufferManagerWrite paramBufferManagerWrite, byte paramByte, boolean paramBoolean2) {
    this.orb = (ORB)paramORB;
    this.wrapper = ORBUtilSystemException.get(this.orb, "rpc.encoding");
    this.debug = this.orb.transportDebugFlag;
    this.littleEndian = paramBoolean1;
    this.bufferManagerWrite = paramBufferManagerWrite;
    this.bbwi = new ByteBufferWithInfo(paramORB, paramBufferManagerWrite, paramBoolean2);
    this.streamFormatVersion = paramByte;
    createRepositoryIdHandlers();
  }
  
  public void init(ORB paramORB, boolean paramBoolean, BufferManagerWrite paramBufferManagerWrite, byte paramByte) { init(paramORB, paramBoolean, paramBufferManagerWrite, paramByte, true); }
  
  private final void createRepositoryIdHandlers() {
    this.repIdUtil = RepositoryIdFactory.getRepIdUtility();
    this.repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
  }
  
  public BufferManagerWrite getBufferManager() { return this.bufferManagerWrite; }
  
  public byte[] toByteArray() {
    byte[] arrayOfByte = new byte[this.bbwi.position()];
    for (byte b = 0; b < this.bbwi.position(); b++)
      arrayOfByte[b] = this.bbwi.byteBuffer.get(b); 
    return arrayOfByte;
  }
  
  public GIOPVersion getGIOPVersion() { return GIOPVersion.V1_0; }
  
  void setHeaderPadding(boolean paramBoolean) { throw this.wrapper.giopVersionError(); }
  
  protected void handleSpecialChunkBegin(int paramInt) {}
  
  protected void handleSpecialChunkEnd() {}
  
  protected final int computeAlignment(int paramInt) {
    if (paramInt > 1) {
      int i = this.bbwi.position() & paramInt - 1;
      if (i != 0)
        return paramInt - i; 
    } 
    return 0;
  }
  
  protected void alignAndReserve(int paramInt1, int paramInt2) {
    this.bbwi.position(this.bbwi.position() + computeAlignment(paramInt1));
    if (this.bbwi.position() + paramInt2 > this.bbwi.buflen)
      grow(paramInt1, paramInt2); 
  }
  
  protected void grow(int paramInt1, int paramInt2) {
    this.bbwi.needed = paramInt2;
    this.bufferManagerWrite.overflow(this.bbwi);
  }
  
  public final void putEndian() { write_boolean(this.littleEndian); }
  
  public final boolean littleEndian() { return this.littleEndian; }
  
  void freeInternalCaches() {
    if (this.codebaseCache != null)
      this.codebaseCache.done(); 
    if (this.valueCache != null)
      this.valueCache.done(); 
    if (this.repositoryIdCache != null)
      this.repositoryIdCache.done(); 
  }
  
  public final void write_longdouble(double paramDouble) { throw this.wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE); }
  
  public void write_octet(byte paramByte) {
    alignAndReserve(1, 1);
    this.bbwi.byteBuffer.put(this.bbwi.position(), paramByte);
    this.bbwi.position(this.bbwi.position() + 1);
  }
  
  public final void write_boolean(boolean paramBoolean) { write_octet(paramBoolean ? 1 : 0); }
  
  public void write_char(char paramChar) {
    CodeSetConversion.CTBConverter cTBConverter = getCharConverter();
    cTBConverter.convert(paramChar);
    if (cTBConverter.getNumBytes() > 1)
      throw this.wrapper.invalidSingleCharCtb(CompletionStatus.COMPLETED_MAYBE); 
    write_octet(cTBConverter.getBytes()[0]);
  }
  
  private final void writeLittleEndianWchar(char paramChar) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(paramChar & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(paramChar >>> '\b' & 0xFF));
    this.bbwi.position(this.bbwi.position() + 2);
  }
  
  private final void writeBigEndianWchar(char paramChar) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(paramChar >>> '\b' & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(paramChar & 0xFF));
    this.bbwi.position(this.bbwi.position() + 2);
  }
  
  private final void writeLittleEndianShort(short paramShort) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(paramShort & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(paramShort >>> 8 & 0xFF));
    this.bbwi.position(this.bbwi.position() + 2);
  }
  
  private final void writeBigEndianShort(short paramShort) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(paramShort >>> 8 & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(paramShort & 0xFF));
    this.bbwi.position(this.bbwi.position() + 2);
  }
  
  private final void writeLittleEndianLong(int paramInt) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(paramInt & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(paramInt >>> 8 & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(paramInt >>> 16 & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(paramInt >>> 24 & 0xFF));
    this.bbwi.position(this.bbwi.position() + 4);
  }
  
  private final void writeBigEndianLong(int paramInt) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(paramInt >>> 24 & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(paramInt >>> 16 & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(paramInt >>> 8 & 0xFF));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(paramInt & 0xFF));
    this.bbwi.position(this.bbwi.position() + 4);
  }
  
  private final void writeLittleEndianLongLong(long paramLong) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(int)(paramLong & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(int)(paramLong >>> 8 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(int)(paramLong >>> 16 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(int)(paramLong >>> 24 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 4, (byte)(int)(paramLong >>> 32 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 5, (byte)(int)(paramLong >>> 40 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 6, (byte)(int)(paramLong >>> 48 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 7, (byte)(int)(paramLong >>> 56 & 0xFFL));
    this.bbwi.position(this.bbwi.position() + 8);
  }
  
  private final void writeBigEndianLongLong(long paramLong) {
    this.bbwi.byteBuffer.put(this.bbwi.position(), (byte)(int)(paramLong >>> 56 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 1, (byte)(int)(paramLong >>> 48 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 2, (byte)(int)(paramLong >>> 40 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 3, (byte)(int)(paramLong >>> 32 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 4, (byte)(int)(paramLong >>> 24 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 5, (byte)(int)(paramLong >>> 16 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 6, (byte)(int)(paramLong >>> 8 & 0xFFL));
    this.bbwi.byteBuffer.put(this.bbwi.position() + 7, (byte)(int)(paramLong & 0xFFL));
    this.bbwi.position(this.bbwi.position() + 8);
  }
  
  public void write_wchar(char paramChar) {
    if (ORBUtility.isForeignORB(this.orb))
      throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE); 
    alignAndReserve(2, 2);
    if (this.littleEndian) {
      writeLittleEndianWchar(paramChar);
    } else {
      writeBigEndianWchar(paramChar);
    } 
  }
  
  public void write_short(short paramShort) {
    alignAndReserve(2, 2);
    if (this.littleEndian) {
      writeLittleEndianShort(paramShort);
    } else {
      writeBigEndianShort(paramShort);
    } 
  }
  
  public final void write_ushort(short paramShort) { write_short(paramShort); }
  
  public void write_long(int paramInt) {
    alignAndReserve(4, 4);
    if (this.littleEndian) {
      writeLittleEndianLong(paramInt);
    } else {
      writeBigEndianLong(paramInt);
    } 
  }
  
  public final void write_ulong(int paramInt) { write_long(paramInt); }
  
  public void write_longlong(long paramLong) {
    alignAndReserve(8, 8);
    if (this.littleEndian) {
      writeLittleEndianLongLong(paramLong);
    } else {
      writeBigEndianLongLong(paramLong);
    } 
  }
  
  public final void write_ulonglong(long paramLong) { write_longlong(paramLong); }
  
  public final void write_float(float paramFloat) { write_long(Float.floatToIntBits(paramFloat)); }
  
  public final void write_double(double paramDouble) { write_longlong(Double.doubleToLongBits(paramDouble)); }
  
  public void write_string(String paramString) { writeString(paramString); }
  
  protected int writeString(String paramString) {
    if (paramString == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    CodeSetConversion.CTBConverter cTBConverter = getCharConverter();
    cTBConverter.convert(paramString);
    int i = cTBConverter.getNumBytes() + 1;
    handleSpecialChunkBegin(computeAlignment(4) + 4 + i);
    write_long(i);
    int j = get_offset() - 4;
    internalWriteOctetArray(cTBConverter.getBytes(), 0, cTBConverter.getNumBytes());
    write_octet((byte)0);
    handleSpecialChunkEnd();
    return j;
  }
  
  public void write_wstring(String paramString) {
    if (paramString == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    if (ORBUtility.isForeignORB(this.orb))
      throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE); 
    int i = paramString.length() + 1;
    handleSpecialChunkBegin(4 + i * 2 + computeAlignment(4));
    write_long(i);
    for (byte b = 0; b < i - 1; b++)
      write_wchar(paramString.charAt(b)); 
    write_short((short)0);
    handleSpecialChunkEnd();
  }
  
  void internalWriteOctetArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    int i = paramInt1;
    boolean bool = true;
    while (i < paramInt2 + paramInt1) {
      if (this.bbwi.position() + 1 > this.bbwi.buflen || bool) {
        bool = false;
        alignAndReserve(1, 1);
      } 
      int j = this.bbwi.buflen - this.bbwi.position();
      int m = paramInt2 + paramInt1 - i;
      int k = (m < j) ? m : j;
      for (int n = 0; n < k; n++)
        this.bbwi.byteBuffer.put(this.bbwi.position() + n, paramArrayOfByte[i + n]); 
      this.bbwi.position(this.bbwi.position() + k);
      i += k;
    } 
  }
  
  public final void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(paramInt2);
    internalWriteOctetArray(paramArrayOfByte, paramInt1, paramInt2);
    handleSpecialChunkEnd();
  }
  
  public void write_Principal(Principal paramPrincipal) {
    write_long(paramPrincipal.name().length);
    write_octet_array(paramPrincipal.name(), 0, paramPrincipal.name().length);
  }
  
  public void write_any(Any paramAny) {
    if (paramAny == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    write_TypeCode(paramAny.type());
    paramAny.write_value(this.parent);
  }
  
  public void write_TypeCode(TypeCode paramTypeCode) {
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
  
  public void write_Object(Object paramObject) {
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
  
  public void write_abstract_interface(Object paramObject) {
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
  
  public void write_value(Serializable paramSerializable, Class paramClass) { write_value(paramSerializable); }
  
  private void writeWStringValue(String paramString) {
    int i = writeValueTag(this.mustChunk, true, null);
    write_repositoryId(this.repIdStrs.getWStringValueRepId());
    updateIndirectionTable(i, paramString, paramString);
    if (this.mustChunk) {
      start_block();
      this.end_flag--;
      this.chunkedValueNestingLevel--;
    } else {
      this.end_flag--;
    } 
    write_wstring(paramString);
    if (this.mustChunk)
      end_block(); 
    writeEndTag(this.mustChunk);
  }
  
  private void writeArray(Serializable paramSerializable, Class paramClass) {
    if (this.valueHandler == null)
      this.valueHandler = ORBUtility.createValueHandler(); 
    int i = writeValueTag(this.mustChunk, true, Util.getCodebase(paramClass));
    write_repositoryId(this.repIdStrs.createSequenceRepID(paramClass));
    updateIndirectionTable(i, paramSerializable, paramSerializable);
    if (this.mustChunk) {
      start_block();
      this.end_flag--;
      this.chunkedValueNestingLevel--;
    } else {
      this.end_flag--;
    } 
    if (this.valueHandler instanceof ValueHandlerMultiFormat) {
      ValueHandlerMultiFormat valueHandlerMultiFormat = (ValueHandlerMultiFormat)this.valueHandler;
      valueHandlerMultiFormat.writeValue(this.parent, paramSerializable, this.streamFormatVersion);
    } else {
      this.valueHandler.writeValue(this.parent, paramSerializable);
    } 
    if (this.mustChunk)
      end_block(); 
    writeEndTag(this.mustChunk);
  }
  
  private void writeValueBase(ValueBase paramValueBase, Class paramClass) {
    this.mustChunk = true;
    int i = writeValueTag(true, true, Util.getCodebase(paramClass));
    String str = paramValueBase._truncatable_ids()[0];
    write_repositoryId(str);
    updateIndirectionTable(i, paramValueBase, paramValueBase);
    start_block();
    this.end_flag--;
    this.chunkedValueNestingLevel--;
    writeIDLValue(paramValueBase, str);
    end_block();
    writeEndTag(true);
  }
  
  private void writeRMIIIOPValueType(Serializable paramSerializable, Class paramClass) {
    if (this.valueHandler == null)
      this.valueHandler = ORBUtility.createValueHandler(); 
    Serializable serializable = paramSerializable;
    paramSerializable = this.valueHandler.writeReplace(serializable);
    if (paramSerializable == null) {
      write_long(0);
      return;
    } 
    if (paramSerializable != serializable) {
      if (this.valueCache != null && this.valueCache.containsKey(paramSerializable)) {
        writeIndirection(-1, this.valueCache.getVal(paramSerializable));
        return;
      } 
      paramClass = paramSerializable.getClass();
    } 
    if (this.mustChunk || this.valueHandler.isCustomMarshaled(paramClass))
      this.mustChunk = true; 
    int i = writeValueTag(this.mustChunk, true, Util.getCodebase(paramClass));
    write_repositoryId(this.repIdStrs.createForJavaType(paramClass));
    updateIndirectionTable(i, paramSerializable, serializable);
    if (this.mustChunk) {
      this.end_flag--;
      this.chunkedValueNestingLevel--;
      start_block();
    } else {
      this.end_flag--;
    } 
    if (this.valueHandler instanceof ValueHandlerMultiFormat) {
      ValueHandlerMultiFormat valueHandlerMultiFormat = (ValueHandlerMultiFormat)this.valueHandler;
      valueHandlerMultiFormat.writeValue(this.parent, paramSerializable, this.streamFormatVersion);
    } else {
      this.valueHandler.writeValue(this.parent, paramSerializable);
    } 
    if (this.mustChunk)
      end_block(); 
    writeEndTag(this.mustChunk);
  }
  
  public void write_value(Serializable paramSerializable, String paramString) {
    if (paramSerializable == null) {
      write_long(0);
      return;
    } 
    if (this.valueCache != null && this.valueCache.containsKey(paramSerializable)) {
      writeIndirection(-1, this.valueCache.getVal(paramSerializable));
      return;
    } 
    Class clazz = paramSerializable.getClass();
    boolean bool = this.mustChunk;
    if (this.mustChunk)
      this.mustChunk = true; 
    if (this.inBlock)
      end_block(); 
    if (clazz.isArray()) {
      writeArray(paramSerializable, clazz);
    } else if (paramSerializable instanceof ValueBase) {
      writeValueBase((ValueBase)paramSerializable, clazz);
    } else if (shouldWriteAsIDLEntity(paramSerializable)) {
      writeIDLEntity((IDLEntity)paramSerializable);
    } else if (paramSerializable instanceof String) {
      writeWStringValue((String)paramSerializable);
    } else if (paramSerializable instanceof Class) {
      writeClass(paramString, (Class)paramSerializable);
    } else {
      writeRMIIIOPValueType(paramSerializable, clazz);
    } 
    this.mustChunk = bool;
    if (this.mustChunk)
      start_block(); 
  }
  
  public void write_value(Serializable paramSerializable) { write_value(paramSerializable, (String)null); }
  
  public void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper) {
    if (paramSerializable == null) {
      write_long(0);
      return;
    } 
    if (this.valueCache != null && this.valueCache.containsKey(paramSerializable)) {
      writeIndirection(-1, this.valueCache.getVal(paramSerializable));
      return;
    } 
    boolean bool = this.mustChunk;
    boolean bool1 = false;
    if (paramBoxedValueHelper instanceof ValueHelper) {
      boolean bool2;
      try {
        bool2 = ((ValueHelper)paramBoxedValueHelper).get_type().type_modifier();
      } catch (BadKind badKind) {
        bool2 = false;
      } 
      if (paramSerializable instanceof CustomMarshal && bool2 == true) {
        bool1 = true;
        this.mustChunk = true;
      } 
      if (bool2 == 3)
        this.mustChunk = true; 
    } 
    if (this.mustChunk) {
      if (this.inBlock)
        end_block(); 
      int i = writeValueTag(true, this.orb.getORBData().useRepId(), Util.getCodebase(paramSerializable.getClass()));
      if (this.orb.getORBData().useRepId())
        write_repositoryId(paramBoxedValueHelper.get_id()); 
      updateIndirectionTable(i, paramSerializable, paramSerializable);
      start_block();
      this.end_flag--;
      this.chunkedValueNestingLevel--;
      if (bool1) {
        ((CustomMarshal)paramSerializable).marshal(this.parent);
      } else {
        paramBoxedValueHelper.write_value(this.parent, paramSerializable);
      } 
      end_block();
      writeEndTag(true);
    } else {
      int i = writeValueTag(false, this.orb.getORBData().useRepId(), Util.getCodebase(paramSerializable.getClass()));
      if (this.orb.getORBData().useRepId())
        write_repositoryId(paramBoxedValueHelper.get_id()); 
      updateIndirectionTable(i, paramSerializable, paramSerializable);
      this.end_flag--;
      paramBoxedValueHelper.write_value(this.parent, paramSerializable);
      writeEndTag(false);
    } 
    this.mustChunk = bool;
    if (this.mustChunk)
      start_block(); 
  }
  
  public int get_offset() { return this.bbwi.position(); }
  
  public void start_block() {
    if (this.debug)
      dprint("CDROutputStream_1_0 start_block, position" + this.bbwi.position()); 
    write_long(0);
    this.inBlock = true;
    this.blockSizePosition = get_offset();
    this.blockSizeIndex = this.bbwi.position();
    if (this.debug)
      dprint("CDROutputStream_1_0 start_block, blockSizeIndex " + this.blockSizeIndex); 
  }
  
  protected void writeLongWithoutAlign(int paramInt) {
    if (this.littleEndian) {
      writeLittleEndianLong(paramInt);
    } else {
      writeBigEndianLong(paramInt);
    } 
  }
  
  public void end_block() {
    if (this.debug)
      dprint("CDROutputStream_1_0.java end_block"); 
    if (!this.inBlock)
      return; 
    if (this.debug)
      dprint("CDROutputStream_1_0.java end_block, in a block"); 
    this.inBlock = false;
    if (get_offset() == this.blockSizePosition) {
      this.bbwi.position(this.bbwi.position() - 4);
      this.blockSizeIndex = -1;
      this.blockSizePosition = -1;
      return;
    } 
    int i = this.bbwi.position();
    this.bbwi.position(this.blockSizeIndex - 4);
    writeLongWithoutAlign(i - this.blockSizeIndex);
    this.bbwi.position(i);
    this.blockSizeIndex = -1;
    this.blockSizePosition = -1;
  }
  
  public ORB orb() { return this.orb; }
  
  public final void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) {
    if (paramArrayOfBoolean == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(paramInt2);
    for (int i = 0; i < paramInt2; i++)
      write_boolean(paramArrayOfBoolean[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public final void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramArrayOfChar == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(paramInt2);
    for (int i = 0; i < paramInt2; i++)
      write_char(paramArrayOfChar[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramArrayOfChar == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(computeAlignment(2) + paramInt2 * 2);
    for (int i = 0; i < paramInt2; i++)
      write_wchar(paramArrayOfChar[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public final void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    if (paramArrayOfShort == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(computeAlignment(2) + paramInt2 * 2);
    for (int i = 0; i < paramInt2; i++)
      write_short(paramArrayOfShort[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public final void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { write_short_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public final void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramArrayOfInt == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(computeAlignment(4) + paramInt2 * 4);
    for (int i = 0; i < paramInt2; i++)
      write_long(paramArrayOfInt[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public final void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { write_long_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public final void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    if (paramArrayOfLong == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(computeAlignment(8) + paramInt2 * 8);
    for (int i = 0; i < paramInt2; i++)
      write_longlong(paramArrayOfLong[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public final void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { write_longlong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public final void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    if (paramArrayOfFloat == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(computeAlignment(4) + paramInt2 * 4);
    for (int i = 0; i < paramInt2; i++)
      write_float(paramArrayOfFloat[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public final void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    if (paramArrayOfDouble == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    handleSpecialChunkBegin(computeAlignment(8) + paramInt2 * 8);
    for (int i = 0; i < paramInt2; i++)
      write_double(paramArrayOfDouble[paramInt1 + i]); 
    handleSpecialChunkEnd();
  }
  
  public void write_string_array(String[] paramArrayOfString, int paramInt1, int paramInt2) {
    if (paramArrayOfString == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    for (int i = 0; i < paramInt2; i++)
      write_string(paramArrayOfString[paramInt1 + i]); 
  }
  
  public void write_wstring_array(String[] paramArrayOfString, int paramInt1, int paramInt2) {
    if (paramArrayOfString == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    for (int i = 0; i < paramInt2; i++)
      write_wstring(paramArrayOfString[paramInt1 + i]); 
  }
  
  public final void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      write_any(paramArrayOfAny[paramInt1 + i]); 
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException {
    byte[] arrayOfByte = null;
    if (this.bbwi.byteBuffer.hasArray()) {
      arrayOfByte = this.bbwi.byteBuffer.array();
    } else {
      int i = this.bbwi.position();
      arrayOfByte = new byte[i];
      for (byte b = 0; b < i; b++)
        arrayOfByte[b] = this.bbwi.byteBuffer.get(b); 
    } 
    paramOutputStream.write(arrayOfByte, 0, this.bbwi.position());
  }
  
  public void writeOctetSequenceTo(OutputStream paramOutputStream) {
    byte[] arrayOfByte = null;
    if (this.bbwi.byteBuffer.hasArray()) {
      arrayOfByte = this.bbwi.byteBuffer.array();
    } else {
      int i = this.bbwi.position();
      arrayOfByte = new byte[i];
      for (byte b = 0; b < i; b++)
        arrayOfByte[b] = this.bbwi.byteBuffer.get(b); 
    } 
    paramOutputStream.write_long(this.bbwi.position());
    paramOutputStream.write_octet_array(arrayOfByte, 0, this.bbwi.position());
  }
  
  public final int getSize() { return this.bbwi.position(); }
  
  public int getIndex() { return this.bbwi.position(); }
  
  public boolean isLittleEndian() { return this.littleEndian; }
  
  public void setIndex(int paramInt) { this.bbwi.position(paramInt); }
  
  public ByteBufferWithInfo getByteBufferWithInfo() { return this.bbwi; }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) { this.bbwi = paramByteBufferWithInfo; }
  
  public ByteBuffer getByteBuffer() {
    ByteBuffer byteBuffer = null;
    if (this.bbwi != null)
      byteBuffer = this.bbwi.byteBuffer; 
    return byteBuffer;
  }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer) { this.bbwi.byteBuffer = paramByteBuffer; }
  
  private final void updateIndirectionTable(int paramInt, Object paramObject1, Object paramObject2) {
    if (this.valueCache == null)
      this.valueCache = new CacheTable(this.orb, true); 
    this.valueCache.put(paramObject1, paramInt);
    if (paramObject2 != paramObject1)
      this.valueCache.put(paramObject2, paramInt); 
  }
  
  private final void write_repositoryId(String paramString) {
    if (this.repositoryIdCache != null && this.repositoryIdCache.containsKey(paramString)) {
      writeIndirection(-1, this.repositoryIdCache.getVal(paramString));
      return;
    } 
    int i = writeString(paramString);
    if (this.repositoryIdCache == null)
      this.repositoryIdCache = new CacheTable(this.orb, true); 
    this.repositoryIdCache.put(paramString, i);
  }
  
  private void write_codebase(String paramString, int paramInt) {
    if (this.codebaseCache != null && this.codebaseCache.containsKey(paramString)) {
      writeIndirection(-1, this.codebaseCache.getVal(paramString));
    } else {
      write_string(paramString);
      if (this.codebaseCache == null)
        this.codebaseCache = new CacheTable(this.orb, true); 
      this.codebaseCache.put(paramString, paramInt);
    } 
  }
  
  private final int writeValueTag(boolean paramBoolean1, boolean paramBoolean2, String paramString) {
    int i = 0;
    if (paramBoolean1 && !paramBoolean2) {
      if (paramString == null) {
        write_long(this.repIdUtil.getStandardRMIChunkedNoRepStrId());
        i = get_offset() - 4;
      } else {
        write_long(this.repIdUtil.getCodeBaseRMIChunkedNoRepStrId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      } 
    } else if (paramBoolean1 && paramBoolean2) {
      if (paramString == null) {
        write_long(this.repIdUtil.getStandardRMIChunkedId());
        i = get_offset() - 4;
      } else {
        write_long(this.repIdUtil.getCodeBaseRMIChunkedId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      } 
    } else if (!paramBoolean1 && !paramBoolean2) {
      if (paramString == null) {
        write_long(this.repIdUtil.getStandardRMIUnchunkedNoRepStrId());
        i = get_offset() - 4;
      } else {
        write_long(this.repIdUtil.getCodeBaseRMIUnchunkedNoRepStrId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      } 
    } else if (!paramBoolean1 && paramBoolean2) {
      if (paramString == null) {
        write_long(this.repIdUtil.getStandardRMIUnchunkedId());
        i = get_offset() - 4;
      } else {
        write_long(this.repIdUtil.getCodeBaseRMIUnchunkedId());
        i = get_offset() - 4;
        write_codebase(paramString, get_offset());
      } 
    } 
    return i;
  }
  
  private void writeIDLValue(Serializable paramSerializable, String paramString) {
    if (paramSerializable instanceof StreamableValue) {
      ((StreamableValue)paramSerializable)._write(this.parent);
    } else if (paramSerializable instanceof CustomValue) {
      ((CustomValue)paramSerializable).marshal(this.parent);
    } else {
      BoxedValueHelper boxedValueHelper = Utility.getHelper(paramSerializable.getClass(), null, paramString);
      boolean bool = false;
      if (boxedValueHelper instanceof ValueHelper && paramSerializable instanceof CustomMarshal)
        try {
          if (((ValueHelper)boxedValueHelper).get_type().type_modifier() == 1)
            bool = true; 
        } catch (BadKind badKind) {
          throw this.wrapper.badTypecodeForCustomValue(CompletionStatus.COMPLETED_MAYBE, badKind);
        }  
      if (bool) {
        ((CustomMarshal)paramSerializable).marshal(this.parent);
      } else {
        boxedValueHelper.write_value(this.parent, paramSerializable);
      } 
    } 
  }
  
  private void writeEndTag(boolean paramBoolean) {
    if (paramBoolean) {
      if (get_offset() == this.end_flag_position && this.bbwi.position() == this.end_flag_index)
        this.bbwi.position(this.bbwi.position() - 4); 
      writeNestingLevel();
      this.end_flag_index = this.bbwi.position();
      this.end_flag_position = get_offset();
      this.chunkedValueNestingLevel++;
    } 
    this.end_flag++;
  }
  
  private void writeNestingLevel() {
    if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
      write_long(this.chunkedValueNestingLevel);
    } else {
      write_long(this.end_flag);
    } 
  }
  
  private void writeClass(String paramString, Class paramClass) {
    if (paramString == null)
      paramString = this.repIdStrs.getClassDescValueRepId(); 
    int i = writeValueTag(this.mustChunk, true, null);
    updateIndirectionTable(i, paramClass, paramClass);
    write_repositoryId(paramString);
    if (this.mustChunk) {
      start_block();
      this.end_flag--;
      this.chunkedValueNestingLevel--;
    } else {
      this.end_flag--;
    } 
    writeClassBody(paramClass);
    if (this.mustChunk)
      end_block(); 
    writeEndTag(this.mustChunk);
  }
  
  private void writeClassBody(Class paramClass) {
    if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
      write_value(Util.getCodebase(paramClass));
      write_value(this.repIdStrs.createForAnyType(paramClass));
    } else {
      write_value(this.repIdStrs.createForAnyType(paramClass));
      write_value(Util.getCodebase(paramClass));
    } 
  }
  
  private boolean shouldWriteAsIDLEntity(Serializable paramSerializable) { return (paramSerializable instanceof IDLEntity && !(paramSerializable instanceof ValueBase) && !(paramSerializable instanceof Object)); }
  
  private void writeIDLEntity(IDLEntity paramIDLEntity) {
    this.mustChunk = true;
    String str1 = this.repIdStrs.createForJavaType(paramIDLEntity);
    Class clazz = paramIDLEntity.getClass();
    String str2 = Util.getCodebase(clazz);
    int i = writeValueTag(true, true, str2);
    updateIndirectionTable(i, paramIDLEntity, paramIDLEntity);
    write_repositoryId(str1);
    this.end_flag--;
    this.chunkedValueNestingLevel--;
    start_block();
    try {
      ClassLoader classLoader = (clazz == null) ? null : clazz.getClassLoader();
      final Class helperClass = Utility.loadClassForClass(clazz.getName() + "Helper", str2, classLoader, clazz, classLoader);
      final Class[] argTypes = { OutputStream.class, clazz };
      Method method = null;
      try {
        method = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
              public Object run() throws NoSuchMethodException { return helperClass.getDeclaredMethod("write", argTypes); }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (NoSuchMethodException)privilegedActionException.getException();
      } 
      Object[] arrayOfObject = { this.parent, paramIDLEntity };
      method.invoke(null, arrayOfObject);
    } catch (ClassNotFoundException classNotFoundException) {
      throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, classNotFoundException);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, noSuchMethodException);
    } catch (IllegalAccessException illegalAccessException) {
      throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      throw this.wrapper.errorInvokingHelperWrite(CompletionStatus.COMPLETED_MAYBE, invocationTargetException);
    } 
    end_block();
    writeEndTag(true);
  }
  
  public void write_Abstract(Object paramObject) { write_abstract_interface(paramObject); }
  
  public void write_Value(Serializable paramSerializable) { write_value(paramSerializable); }
  
  public void write_fixed(BigDecimal paramBigDecimal, short paramShort1, short paramShort2) {
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
  
  public void write_fixed(BigDecimal paramBigDecimal) { write_fixed(paramBigDecimal.toString(), paramBigDecimal.signum()); }
  
  public void write_fixed(String paramString, int paramInt) {
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
  
  public String[] _truncatable_ids() { return (_ids == null) ? null : (String[])_ids.clone(); }
  
  public void printBuffer() { printBuffer(this.bbwi); }
  
  public static void printBuffer(ByteBufferWithInfo paramByteBufferWithInfo) {
    System.out.println("+++++++ Output Buffer ++++++++");
    System.out.println();
    System.out.println("Current position: " + paramByteBufferWithInfo.position());
    System.out.println("Total length : " + paramByteBufferWithInfo.buflen);
    System.out.println();
    char[] arrayOfChar = new char[16];
    try {
      for (byte b = 0; b < paramByteBufferWithInfo.position(); b += 16) {
        byte b1;
        for (b1 = 0; b1 < 16 && b1 + b < paramByteBufferWithInfo.position(); b1++) {
          char c = paramByteBufferWithInfo.byteBuffer.get(b + b1);
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
        for (b2 = 0; b2 < 16 && b2 + b < paramByteBufferWithInfo.position(); b2++) {
          if (ORBUtility.isPrintable((char)paramByteBufferWithInfo.byteBuffer.get(b + b2))) {
            arrayOfChar[b2] = (char)paramByteBufferWithInfo.byteBuffer.get(b + b2);
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
  
  public void writeIndirection(int paramInt1, int paramInt2) {
    handleSpecialChunkBegin(computeAlignment(4) + 8);
    write_long(paramInt1);
    write_long(paramInt2 - this.parent.getRealIndex(get_offset()));
    handleSpecialChunkEnd();
  }
  
  protected CodeSetConversion.CTBConverter getCharConverter() {
    if (this.charConverter == null)
      this.charConverter = this.parent.createCharCTBConverter(); 
    return this.charConverter;
  }
  
  protected CodeSetConversion.CTBConverter getWCharConverter() {
    if (this.wcharConverter == null)
      this.wcharConverter = this.parent.createWCharCTBConverter(); 
    return this.wcharConverter;
  }
  
  protected void dprint(String paramString) {
    if (this.debug)
      ORBUtility.dprint(this, paramString); 
  }
  
  void alignOnBoundary(int paramInt) { alignAndReserve(paramInt, 0); }
  
  public void start_value(String paramString) {
    if (this.debug)
      dprint("start_value w/ rep id " + paramString + " called at pos " + get_offset() + " position " + this.bbwi.position()); 
    if (this.inBlock)
      end_block(); 
    writeValueTag(true, true, null);
    write_repositoryId(paramString);
    this.end_flag--;
    this.chunkedValueNestingLevel--;
    start_block();
  }
  
  public void end_value() {
    if (this.debug)
      dprint("end_value called at pos " + get_offset() + " position " + this.bbwi.position()); 
    end_block();
    writeEndTag(true);
    if (this.debug)
      dprint("mustChunk is " + this.mustChunk); 
    if (this.mustChunk)
      start_block(); 
  }
  
  public void close() {
    getBufferManager().close();
    if (getByteBufferWithInfo() != null && getByteBuffer() != null) {
      MessageMediator messageMediator = this.parent.getMessageMediator();
      if (messageMediator != null) {
        CDRInputObject cDRInputObject = (CDRInputObject)messageMediator.getInputObject();
        if (cDRInputObject != null && cDRInputObject.isSharing(getByteBuffer())) {
          cDRInputObject.setByteBuffer(null);
          cDRInputObject.setByteBufferWithInfo(null);
        } 
      } 
      ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
      if (this.debug) {
        int i = System.identityHashCode(this.bbwi.byteBuffer);
        StringBuffer stringBuffer = new StringBuffer(80);
        stringBuffer.append(".close - releasing ByteBuffer id (");
        stringBuffer.append(i).append(") to ByteBufferPool.");
        String str = stringBuffer.toString();
        dprint(str);
      } 
      byteBufferPool.releaseByteBuffer(getByteBuffer());
      this.bbwi.byteBuffer = null;
      this.bbwi = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStream_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */