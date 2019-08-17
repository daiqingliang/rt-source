package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.ValueBase;

public interface DataInputStream extends ValueBase {
  Any read_any();
  
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
  
  Object read_Object();
  
  java.lang.Object read_Abstract();
  
  Serializable read_Value();
  
  TypeCode read_TypeCode();
  
  void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2);
  
  void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2);
  
  void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2);
  
  void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2);
  
  void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2);
  
  void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2);
  
  void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2);
  
  void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2);
  
  void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2);
  
  void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2);
  
  void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2);
  
  void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2);
  
  void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DataInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */