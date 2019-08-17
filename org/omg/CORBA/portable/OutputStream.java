package org.omg.CORBA.portable;

import java.io.OutputStream;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;

public abstract class OutputStream extends OutputStream {
  public abstract InputStream create_input_stream();
  
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
  
  @Deprecated
  public void write_Principal(Principal paramPrincipal) { throw new NO_IMPLEMENT(); }
  
  public void write(int paramInt) { throw new NO_IMPLEMENT(); }
  
  public void write_fixed(BigDecimal paramBigDecimal) { throw new NO_IMPLEMENT(); }
  
  public void write_Context(Context paramContext, ContextList paramContextList) { throw new NO_IMPLEMENT(); }
  
  public ORB orb() { throw new NO_IMPLEMENT(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\OutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */