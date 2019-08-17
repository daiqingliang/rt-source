package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.OutputStream;

abstract class CDROutputStreamBase extends OutputStream {
  protected CDROutputStream parent;
  
  public void setParent(CDROutputStream paramCDROutputStream) { this.parent = paramCDROutputStream; }
  
  public void init(ORB paramORB, BufferManagerWrite paramBufferManagerWrite, byte paramByte) { init(paramORB, false, paramBufferManagerWrite, paramByte, true); }
  
  protected abstract void init(ORB paramORB, boolean paramBoolean1, BufferManagerWrite paramBufferManagerWrite, byte paramByte, boolean paramBoolean2);
  
  public abstract void write_boolean(boolean paramBoolean);
  
  public abstract void write_char(char paramChar);
  
  public abstract void write_wchar(char paramChar);
  
  public abstract void write_octet(byte paramByte);
  
  public abstract void write_short(short paramShort);
  
  public abstract void write_ushort(short paramShort);
  
  public abstract void write_long(int paramInt);
  
  public abstract void write_ulong(int paramInt);
  
  public abstract void write_longlong(long paramLong);
  
  public abstract void write_ulonglong(long paramLong);
  
  public abstract void write_float(float paramFloat);
  
  public abstract void write_double(double paramDouble);
  
  public abstract void write_string(String paramString);
  
  public abstract void write_wstring(String paramString);
  
  public abstract void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2);
  
  public abstract void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public abstract void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public abstract void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  public abstract void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  public abstract void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  public abstract void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  public abstract void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  public abstract void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  public abstract void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2);
  
  public abstract void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2);
  
  public abstract void write_Object(Object paramObject);
  
  public abstract void write_TypeCode(TypeCode paramTypeCode);
  
  public abstract void write_any(Any paramAny);
  
  public abstract void write_Principal(Principal paramPrincipal);
  
  public void write(int paramInt) { throw new NO_IMPLEMENT(); }
  
  public abstract void write_fixed(BigDecimal paramBigDecimal);
  
  public void write_Context(Context paramContext, ContextList paramContextList) { throw new NO_IMPLEMENT(); }
  
  public abstract ORB orb();
  
  public abstract void write_value(Serializable paramSerializable);
  
  public abstract void write_value(Serializable paramSerializable, Class paramClass);
  
  public abstract void write_value(Serializable paramSerializable, String paramString);
  
  public abstract void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper);
  
  public abstract void write_abstract_interface(Object paramObject);
  
  public abstract void start_block();
  
  public abstract void end_block();
  
  public abstract void putEndian();
  
  public abstract void writeTo(OutputStream paramOutputStream) throws IOException;
  
  public abstract byte[] toByteArray();
  
  public abstract void write_Abstract(Object paramObject);
  
  public abstract void write_Value(Serializable paramSerializable);
  
  public abstract void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2);
  
  public abstract String[] _truncatable_ids();
  
  abstract void setHeaderPadding(boolean paramBoolean);
  
  public abstract int getSize();
  
  public abstract int getIndex();
  
  public abstract void setIndex(int paramInt);
  
  public abstract ByteBuffer getByteBuffer();
  
  public abstract void setByteBuffer(ByteBuffer paramByteBuffer);
  
  public abstract boolean isLittleEndian();
  
  public abstract ByteBufferWithInfo getByteBufferWithInfo();
  
  public abstract void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo);
  
  public abstract BufferManagerWrite getBufferManager();
  
  public abstract void write_fixed(BigDecimal paramBigDecimal, short paramShort1, short paramShort2);
  
  public abstract void writeOctetSequenceTo(OutputStream paramOutputStream);
  
  public abstract GIOPVersion getGIOPVersion();
  
  public abstract void writeIndirection(int paramInt1, int paramInt2);
  
  abstract void freeInternalCaches();
  
  abstract void printBuffer();
  
  abstract void alignOnBoundary(int paramInt);
  
  public abstract void start_value(String paramString);
  
  public abstract void end_value();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStreamBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */