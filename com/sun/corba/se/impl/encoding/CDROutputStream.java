package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class CDROutputStream extends OutputStream implements MarshalOutputStream, DataOutputStream, ValueOutputStream {
  private CDROutputStreamBase impl;
  
  protected ORB orb;
  
  protected ORBUtilSystemException wrapper;
  
  protected CorbaMessageMediator corbaMessageMediator;
  
  public CDROutputStream(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte1, boolean paramBoolean1, BufferManagerWrite paramBufferManagerWrite, byte paramByte2, boolean paramBoolean2) {
    this.impl = OutputStreamFactory.newOutputStream(paramORB, paramGIOPVersion, paramByte1);
    this.impl.init(paramORB, paramBoolean1, paramBufferManagerWrite, paramByte2, paramBoolean2);
    this.impl.setParent(this);
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
  }
  
  public CDROutputStream(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte1, boolean paramBoolean, BufferManagerWrite paramBufferManagerWrite, byte paramByte2) { this(paramORB, paramGIOPVersion, paramByte1, paramBoolean, paramBufferManagerWrite, paramByte2, true); }
  
  public abstract InputStream create_input_stream();
  
  public final void write_boolean(boolean paramBoolean) { this.impl.write_boolean(paramBoolean); }
  
  public final void write_char(char paramChar) { this.impl.write_char(paramChar); }
  
  public final void write_wchar(char paramChar) { this.impl.write_wchar(paramChar); }
  
  public final void write_octet(byte paramByte) { this.impl.write_octet(paramByte); }
  
  public final void write_short(short paramShort) { this.impl.write_short(paramShort); }
  
  public final void write_ushort(short paramShort) { this.impl.write_ushort(paramShort); }
  
  public final void write_long(int paramInt) { this.impl.write_long(paramInt); }
  
  public final void write_ulong(int paramInt) { this.impl.write_ulong(paramInt); }
  
  public final void write_longlong(long paramLong) { this.impl.write_longlong(paramLong); }
  
  public final void write_ulonglong(long paramLong) { this.impl.write_ulonglong(paramLong); }
  
  public final void write_float(float paramFloat) { this.impl.write_float(paramFloat); }
  
  public final void write_double(double paramDouble) { this.impl.write_double(paramDouble); }
  
  public final void write_string(String paramString) { this.impl.write_string(paramString); }
  
  public final void write_wstring(String paramString) { this.impl.write_wstring(paramString); }
  
  public final void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) { this.impl.write_boolean_array(paramArrayOfBoolean, paramInt1, paramInt2); }
  
  public final void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this.impl.write_char_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public final void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this.impl.write_wchar_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public final void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.impl.write_octet_array(paramArrayOfByte, paramInt1, paramInt2); }
  
  public final void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { this.impl.write_short_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public final void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { this.impl.write_ushort_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public final void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { this.impl.write_long_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public final void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { this.impl.write_ulong_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public final void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { this.impl.write_longlong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public final void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { this.impl.write_ulonglong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public final void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) { this.impl.write_float_array(paramArrayOfFloat, paramInt1, paramInt2); }
  
  public final void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) { this.impl.write_double_array(paramArrayOfDouble, paramInt1, paramInt2); }
  
  public final void write_Object(Object paramObject) { this.impl.write_Object(paramObject); }
  
  public final void write_TypeCode(TypeCode paramTypeCode) { this.impl.write_TypeCode(paramTypeCode); }
  
  public final void write_any(Any paramAny) { this.impl.write_any(paramAny); }
  
  public final void write_Principal(Principal paramPrincipal) { this.impl.write_Principal(paramPrincipal); }
  
  public final void write(int paramInt) { this.impl.write(paramInt); }
  
  public final void write_fixed(BigDecimal paramBigDecimal) { this.impl.write_fixed(paramBigDecimal); }
  
  public final void write_Context(Context paramContext, ContextList paramContextList) { this.impl.write_Context(paramContext, paramContextList); }
  
  public final ORB orb() { return this.impl.orb(); }
  
  public final void write_value(Serializable paramSerializable) { this.impl.write_value(paramSerializable); }
  
  public final void write_value(Serializable paramSerializable, Class paramClass) { this.impl.write_value(paramSerializable, paramClass); }
  
  public final void write_value(Serializable paramSerializable, String paramString) { this.impl.write_value(paramSerializable, paramString); }
  
  public final void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper) { this.impl.write_value(paramSerializable, paramBoxedValueHelper); }
  
  public final void write_abstract_interface(Object paramObject) { this.impl.write_abstract_interface(paramObject); }
  
  public final void write(byte[] paramArrayOfByte) throws IOException { this.impl.write(paramArrayOfByte); }
  
  public final void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.impl.write(paramArrayOfByte, paramInt1, paramInt2); }
  
  public final void flush() throws IOException { this.impl.flush(); }
  
  public final void close() throws IOException { this.impl.close(); }
  
  public final void start_block() throws IOException { this.impl.start_block(); }
  
  public final void end_block() throws IOException { this.impl.end_block(); }
  
  public final void putEndian() throws IOException { this.impl.putEndian(); }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException { this.impl.writeTo(paramOutputStream); }
  
  public final byte[] toByteArray() { return this.impl.toByteArray(); }
  
  public final void write_Abstract(Object paramObject) { this.impl.write_Abstract(paramObject); }
  
  public final void write_Value(Serializable paramSerializable) { this.impl.write_Value(paramSerializable); }
  
  public final void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2) { this.impl.write_any_array(paramArrayOfAny, paramInt1, paramInt2); }
  
  public void setMessageMediator(MessageMediator paramMessageMediator) { this.corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator; }
  
  public MessageMediator getMessageMediator() { return this.corbaMessageMediator; }
  
  public final String[] _truncatable_ids() { return this.impl._truncatable_ids(); }
  
  protected final int getSize() { return this.impl.getSize(); }
  
  protected final int getIndex() { return this.impl.getIndex(); }
  
  protected int getRealIndex(int paramInt) { return paramInt; }
  
  protected final void setIndex(int paramInt) { this.impl.setIndex(paramInt); }
  
  protected final ByteBuffer getByteBuffer() { return this.impl.getByteBuffer(); }
  
  protected final void setByteBuffer(ByteBuffer paramByteBuffer) { this.impl.setByteBuffer(paramByteBuffer); }
  
  protected final boolean isSharing(ByteBuffer paramByteBuffer) { return (getByteBuffer() == paramByteBuffer); }
  
  public final boolean isLittleEndian() { return this.impl.isLittleEndian(); }
  
  public ByteBufferWithInfo getByteBufferWithInfo() { return this.impl.getByteBufferWithInfo(); }
  
  protected void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) { this.impl.setByteBufferWithInfo(paramByteBufferWithInfo); }
  
  public final BufferManagerWrite getBufferManager() { return this.impl.getBufferManager(); }
  
  public final void write_fixed(BigDecimal paramBigDecimal, short paramShort1, short paramShort2) { this.impl.write_fixed(paramBigDecimal, paramShort1, paramShort2); }
  
  public final void writeOctetSequenceTo(OutputStream paramOutputStream) { this.impl.writeOctetSequenceTo(paramOutputStream); }
  
  public final GIOPVersion getGIOPVersion() { return this.impl.getGIOPVersion(); }
  
  public final void writeIndirection(int paramInt1, int paramInt2) { this.impl.writeIndirection(paramInt1, paramInt2); }
  
  protected CodeSetConversion.CTBConverter createCharCTBConverter() { return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1); }
  
  protected abstract CodeSetConversion.CTBConverter createWCharCTBConverter();
  
  protected final void freeInternalCaches() throws IOException { this.impl.freeInternalCaches(); }
  
  void printBuffer() throws IOException { this.impl.printBuffer(); }
  
  public void alignOnBoundary(int paramInt) { this.impl.alignOnBoundary(paramInt); }
  
  public void setHeaderPadding(boolean paramBoolean) { this.impl.setHeaderPadding(paramBoolean); }
  
  public void start_value(String paramString) { this.impl.start_value(paramString); }
  
  public void end_value() throws IOException { this.impl.end_value(); }
  
  private static class OutputStreamFactory {
    public static CDROutputStreamBase newOutputStream(ORB param1ORB, GIOPVersion param1GIOPVersion, byte param1Byte) {
      switch (param1GIOPVersion.intValue()) {
        case 256:
          return new CDROutputStream_1_0();
        case 257:
          return new CDROutputStream_1_1();
        case 258:
          return (param1Byte != 0) ? new IDLJavaSerializationOutputStream(param1Byte) : new CDROutputStream_1_2();
      } 
      ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(param1ORB, "rpc.encoding");
      throw oRBUtilSystemException.unsupportedGiopVersion(param1GIOPVersion);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */