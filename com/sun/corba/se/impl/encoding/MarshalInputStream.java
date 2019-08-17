package com.sun.corba.se.impl.encoding;

import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;

public interface MarshalInputStream {
  boolean read_boolean();
  
  char read_char();
  
  char read_wchar();
  
  byte read_octet();
  
  short read_short();
  
  short read_ushort();
  
  int read_long();
  
  int read_ulong();
  
  long read_longlong();
  
  long read_ulonglong();
  
  float read_float();
  
  double read_double();
  
  String read_string();
  
  String read_wstring();
  
  void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2);
  
  void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2);
  
  void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2);
  
  void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2);
  
  void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2);
  
  Object read_Object();
  
  TypeCode read_TypeCode();
  
  Any read_any();
  
  Principal read_Principal();
  
  Object read_Object(Class paramClass);
  
  Serializable read_value() throws Exception;
  
  void consumeEndian();
  
  int getPosition();
  
  void mark(int paramInt);
  
  void reset();
  
  void performORBVersionSpecificInit();
  
  void resetCodeSetConverters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\MarshalInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */