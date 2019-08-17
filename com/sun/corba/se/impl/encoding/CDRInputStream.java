package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.Context;
import org.omg.CORBA.DataInputStream;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
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
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class CDRInputStream extends InputStream implements MarshalInputStream, DataInputStream, ValueInputStream {
  protected CorbaMessageMediator messageMediator;
  
  private CDRInputStreamBase impl;
  
  public CDRInputStream() {}
  
  public CDRInputStream(CDRInputStream paramCDRInputStream) {
    this.impl = paramCDRInputStream.impl.dup();
    this.impl.setParent(this);
  }
  
  public CDRInputStream(ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion, byte paramByte, BufferManagerRead paramBufferManagerRead) {
    this.impl = InputStreamFactory.newInputStream((ORB)paramORB, paramGIOPVersion, paramByte);
    this.impl.init(paramORB, paramByteBuffer, paramInt, paramBoolean, paramBufferManagerRead);
    this.impl.setParent(this);
  }
  
  public final boolean read_boolean() { return this.impl.read_boolean(); }
  
  public final char read_char() { return this.impl.read_char(); }
  
  public final char read_wchar() { return this.impl.read_wchar(); }
  
  public final byte read_octet() { return this.impl.read_octet(); }
  
  public final short read_short() { return this.impl.read_short(); }
  
  public final short read_ushort() { return this.impl.read_ushort(); }
  
  public final int read_long() { return this.impl.read_long(); }
  
  public final int read_ulong() { return this.impl.read_ulong(); }
  
  public final long read_longlong() { return this.impl.read_longlong(); }
  
  public final long read_ulonglong() { return this.impl.read_ulonglong(); }
  
  public final float read_float() { return this.impl.read_float(); }
  
  public final double read_double() { return this.impl.read_double(); }
  
  public final String read_string() { return this.impl.read_string(); }
  
  public final String read_wstring() { return this.impl.read_wstring(); }
  
  public final void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) { this.impl.read_boolean_array(paramArrayOfBoolean, paramInt1, paramInt2); }
  
  public final void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this.impl.read_char_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public final void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this.impl.read_wchar_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public final void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.impl.read_octet_array(paramArrayOfByte, paramInt1, paramInt2); }
  
  public final void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { this.impl.read_short_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public final void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { this.impl.read_ushort_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public final void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { this.impl.read_long_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public final void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { this.impl.read_ulong_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public final void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { this.impl.read_longlong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public final void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { this.impl.read_ulonglong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public final void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) { this.impl.read_float_array(paramArrayOfFloat, paramInt1, paramInt2); }
  
  public final void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) { this.impl.read_double_array(paramArrayOfDouble, paramInt1, paramInt2); }
  
  public final Object read_Object() { return this.impl.read_Object(); }
  
  public final TypeCode read_TypeCode() { return this.impl.read_TypeCode(); }
  
  public final Any read_any() { return this.impl.read_any(); }
  
  public final Principal read_Principal() { return this.impl.read_Principal(); }
  
  public final int read() { return this.impl.read(); }
  
  public final BigDecimal read_fixed() { return this.impl.read_fixed(); }
  
  public final Context read_Context() { return this.impl.read_Context(); }
  
  public final Object read_Object(Class paramClass) { return this.impl.read_Object(paramClass); }
  
  public final ORB orb() { return this.impl.orb(); }
  
  public final Serializable read_value() { return this.impl.read_value(); }
  
  public final Serializable read_value(Class paramClass) { return this.impl.read_value(paramClass); }
  
  public final Serializable read_value(BoxedValueHelper paramBoxedValueHelper) { return this.impl.read_value(paramBoxedValueHelper); }
  
  public final Serializable read_value(String paramString) { return this.impl.read_value(paramString); }
  
  public final Serializable read_value(Serializable paramSerializable) { return this.impl.read_value(paramSerializable); }
  
  public final Object read_abstract_interface() { return this.impl.read_abstract_interface(); }
  
  public final Object read_abstract_interface(Class paramClass) { return this.impl.read_abstract_interface(paramClass); }
  
  public final void consumeEndian() { this.impl.consumeEndian(); }
  
  public final int getPosition() { return this.impl.getPosition(); }
  
  public final Object read_Abstract() { return this.impl.read_Abstract(); }
  
  public final Serializable read_Value() { return this.impl.read_Value(); }
  
  public final void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2) { this.impl.read_any_array(paramAnySeqHolder, paramInt1, paramInt2); }
  
  public final void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2) { this.impl.read_boolean_array(paramBooleanSeqHolder, paramInt1, paramInt2); }
  
  public final void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2) { this.impl.read_char_array(paramCharSeqHolder, paramInt1, paramInt2); }
  
  public final void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2) { this.impl.read_wchar_array(paramWCharSeqHolder, paramInt1, paramInt2); }
  
  public final void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2) { this.impl.read_octet_array(paramOctetSeqHolder, paramInt1, paramInt2); }
  
  public final void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2) { this.impl.read_short_array(paramShortSeqHolder, paramInt1, paramInt2); }
  
  public final void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2) { this.impl.read_ushort_array(paramUShortSeqHolder, paramInt1, paramInt2); }
  
  public final void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2) { this.impl.read_long_array(paramLongSeqHolder, paramInt1, paramInt2); }
  
  public final void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2) { this.impl.read_ulong_array(paramULongSeqHolder, paramInt1, paramInt2); }
  
  public final void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2) { this.impl.read_ulonglong_array(paramULongLongSeqHolder, paramInt1, paramInt2); }
  
  public final void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2) { this.impl.read_longlong_array(paramLongLongSeqHolder, paramInt1, paramInt2); }
  
  public final void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2) { this.impl.read_float_array(paramFloatSeqHolder, paramInt1, paramInt2); }
  
  public final void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2) { this.impl.read_double_array(paramDoubleSeqHolder, paramInt1, paramInt2); }
  
  public final String[] _truncatable_ids() { return this.impl._truncatable_ids(); }
  
  public final int read(byte[] paramArrayOfByte) throws IOException { return this.impl.read(paramArrayOfByte); }
  
  public final int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { return this.impl.read(paramArrayOfByte, paramInt1, paramInt2); }
  
  public final long skip(long paramLong) throws IOException { return this.impl.skip(paramLong); }
  
  public final int available() { return this.impl.available(); }
  
  public final void close() { this.impl.close(); }
  
  public final void mark(int paramInt) { this.impl.mark(paramInt); }
  
  public final void reset() { this.impl.reset(); }
  
  public final boolean markSupported() { return this.impl.markSupported(); }
  
  public abstract CDRInputStream dup();
  
  public final BigDecimal read_fixed(short paramShort1, short paramShort2) { return this.impl.read_fixed(paramShort1, paramShort2); }
  
  public final boolean isLittleEndian() { return this.impl.isLittleEndian(); }
  
  protected final ByteBuffer getByteBuffer() { return this.impl.getByteBuffer(); }
  
  protected final void setByteBuffer(ByteBuffer paramByteBuffer) { this.impl.setByteBuffer(paramByteBuffer); }
  
  protected final void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) { this.impl.setByteBufferWithInfo(paramByteBufferWithInfo); }
  
  protected final boolean isSharing(ByteBuffer paramByteBuffer) { return (getByteBuffer() == paramByteBuffer); }
  
  public final int getBufferLength() { return this.impl.getBufferLength(); }
  
  protected final void setBufferLength(int paramInt) { this.impl.setBufferLength(paramInt); }
  
  protected final int getIndex() { return this.impl.getIndex(); }
  
  protected final void setIndex(int paramInt) { this.impl.setIndex(paramInt); }
  
  public final void orb(ORB paramORB) { this.impl.orb(paramORB); }
  
  public final GIOPVersion getGIOPVersion() { return this.impl.getGIOPVersion(); }
  
  public final BufferManagerRead getBufferManager() { return this.impl.getBufferManager(); }
  
  public CodeBase getCodeBase() { return null; }
  
  protected CodeSetConversion.BTCConverter createCharBTCConverter() { return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1, this.impl.isLittleEndian()); }
  
  protected abstract CodeSetConversion.BTCConverter createWCharBTCConverter();
  
  void printBuffer() { this.impl.printBuffer(); }
  
  public void alignOnBoundary(int paramInt) { this.impl.alignOnBoundary(paramInt); }
  
  public void setHeaderPadding(boolean paramBoolean) { this.impl.setHeaderPadding(paramBoolean); }
  
  public void performORBVersionSpecificInit() {
    if (this.impl != null)
      this.impl.performORBVersionSpecificInit(); 
  }
  
  public void resetCodeSetConverters() { this.impl.resetCodeSetConverters(); }
  
  public void setMessageMediator(MessageMediator paramMessageMediator) { this.messageMediator = (CorbaMessageMediator)paramMessageMediator; }
  
  public MessageMediator getMessageMediator() { return this.messageMediator; }
  
  public void start_value() { this.impl.start_value(); }
  
  public void end_value() { this.impl.end_value(); }
  
  private static class InputStreamFactory {
    public static CDRInputStreamBase newInputStream(ORB param1ORB, GIOPVersion param1GIOPVersion, byte param1Byte) {
      switch (param1GIOPVersion.intValue()) {
        case 256:
          return new CDRInputStream_1_0();
        case 257:
          return new CDRInputStream_1_1();
        case 258:
          return (param1Byte != 0) ? new IDLJavaSerializationInputStream(param1Byte) : new CDRInputStream_1_2();
      } 
      ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(param1ORB, "rpc.encoding");
      throw oRBUtilSystemException.unsupportedGiopVersion(param1GIOPVersion);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */