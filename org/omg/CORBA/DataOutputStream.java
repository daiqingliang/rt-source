package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.ValueBase;

public interface DataOutputStream extends ValueBase {
  void write_any(Any paramAny);
  
  void write_boolean(boolean paramBoolean);
  
  void write_char(char paramChar);
  
  void write_wchar(char paramChar);
  
  void write_octet(byte paramByte);
  
  void write_short(short paramShort);
  
  void write_ushort(short paramShort);
  
  void write_long(int paramInt);
  
  void write_ulong(int paramInt);
  
  void write_longlong(long paramLong);
  
  void write_ulonglong(long paramLong);
  
  void write_float(float paramFloat);
  
  void write_double(double paramDouble);
  
  void write_string(String paramString);
  
  void write_wstring(String paramString);
  
  void write_Object(Object paramObject);
  
  void write_Abstract(java.lang.Object paramObject);
  
  void write_Value(Serializable paramSerializable);
  
  void write_TypeCode(TypeCode paramTypeCode);
  
  void write_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2);
  
  void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2);
  
  void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2);
  
  void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DataOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */