package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.Context;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.NO_IMPLEMENT;
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

abstract class CDRInputStreamBase extends InputStream {
  protected CDRInputStream parent;
  
  public void setParent(CDRInputStream paramCDRInputStream) { this.parent = paramCDRInputStream; }
  
  public abstract void init(ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, BufferManagerRead paramBufferManagerRead);
  
  public abstract boolean read_boolean();
  
  public abstract char read_char();
  
  public abstract char read_wchar();
  
  public abstract byte read_octet();
  
  public abstract short read_short();
  
  public abstract short read_ushort();
  
  public abstract int read_long();
  
  public abstract int read_ulong();
  
  public abstract long read_longlong();
  
  public abstract long read_ulonglong();
  
  public abstract float read_float();
  
  public abstract double read_double();
  
  public abstract String read_string();
  
  public abstract String read_wstring();
  
  public abstract void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2);
  
  public abstract void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public abstract void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public abstract void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  public abstract void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  public abstract void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  public abstract void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  public abstract void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  public abstract void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  public abstract void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2);
  
  public abstract void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2);
  
  public abstract Object read_Object();
  
  public abstract TypeCode read_TypeCode();
  
  public abstract Any read_any();
  
  public abstract Principal read_Principal();
  
  public int read() { throw new NO_IMPLEMENT(); }
  
  public abstract BigDecimal read_fixed();
  
  public Context read_Context() { throw new NO_IMPLEMENT(); }
  
  public abstract Object read_Object(Class paramClass);
  
  public abstract ORB orb();
  
  public abstract Serializable read_value();
  
  public abstract Serializable read_value(Class paramClass);
  
  public abstract Serializable read_value(BoxedValueHelper paramBoxedValueHelper);
  
  public abstract Serializable read_value(String paramString);
  
  public abstract Serializable read_value(Serializable paramSerializable);
  
  public abstract Object read_abstract_interface();
  
  public abstract Object read_abstract_interface(Class paramClass);
  
  public abstract void consumeEndian();
  
  public abstract int getPosition();
  
  public abstract Object read_Abstract();
  
  public abstract Serializable read_Value();
  
  public abstract void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2);
  
  public abstract void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2);
  
  public abstract String[] _truncatable_ids();
  
  public abstract void mark(int paramInt);
  
  public abstract void reset();
  
  public boolean markSupported() { return false; }
  
  public abstract CDRInputStreamBase dup();
  
  public abstract BigDecimal read_fixed(short paramShort1, short paramShort2);
  
  public abstract boolean isLittleEndian();
  
  abstract void setHeaderPadding(boolean paramBoolean);
  
  public abstract ByteBuffer getByteBuffer();
  
  public abstract void setByteBuffer(ByteBuffer paramByteBuffer);
  
  public abstract void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo);
  
  public abstract int getBufferLength();
  
  public abstract void setBufferLength(int paramInt);
  
  public abstract int getIndex();
  
  public abstract void setIndex(int paramInt);
  
  public abstract void orb(ORB paramORB);
  
  public abstract BufferManagerRead getBufferManager();
  
  public abstract GIOPVersion getGIOPVersion();
  
  abstract CodeBase getCodeBase();
  
  abstract void printBuffer();
  
  abstract void alignOnBoundary(int paramInt);
  
  abstract void performORBVersionSpecificInit();
  
  public abstract void resetCodeSetConverters();
  
  public abstract void start_value();
  
  public abstract void end_value();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStreamBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */