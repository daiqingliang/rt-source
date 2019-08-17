package org.omg.DynamicAny;

import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public interface DynAnyOperations {
  TypeCode type();
  
  void assign(DynAny paramDynAny) throws TypeMismatch;
  
  void from_any(Any paramAny) throws TypeMismatch, InvalidValue;
  
  Any to_any();
  
  boolean equal(DynAny paramDynAny);
  
  void destroy();
  
  DynAny copy();
  
  void insert_boolean(boolean paramBoolean) throws TypeMismatch, InvalidValue;
  
  void insert_octet(byte paramByte) throws TypeMismatch, InvalidValue;
  
  void insert_char(char paramChar) throws TypeMismatch, InvalidValue;
  
  void insert_short(short paramShort) throws TypeMismatch, InvalidValue;
  
  void insert_ushort(short paramShort) throws TypeMismatch, InvalidValue;
  
  void insert_long(int paramInt) throws TypeMismatch, InvalidValue;
  
  void insert_ulong(int paramInt) throws TypeMismatch, InvalidValue;
  
  void insert_float(float paramFloat) throws TypeMismatch, InvalidValue;
  
  void insert_double(double paramDouble) throws TypeMismatch, InvalidValue;
  
  void insert_string(String paramString) throws TypeMismatch, InvalidValue;
  
  void insert_reference(Object paramObject) throws TypeMismatch, InvalidValue;
  
  void insert_typecode(TypeCode paramTypeCode) throws TypeMismatch, InvalidValue;
  
  void insert_longlong(long paramLong) throws TypeMismatch, InvalidValue;
  
  void insert_ulonglong(long paramLong) throws TypeMismatch, InvalidValue;
  
  void insert_wchar(char paramChar) throws TypeMismatch, InvalidValue;
  
  void insert_wstring(String paramString) throws TypeMismatch, InvalidValue;
  
  void insert_any(Any paramAny) throws TypeMismatch, InvalidValue;
  
  void insert_dyn_any(DynAny paramDynAny) throws TypeMismatch;
  
  void insert_val(Serializable paramSerializable) throws TypeMismatch, InvalidValue;
  
  boolean get_boolean() throws TypeMismatch, InvalidValue;
  
  byte get_octet() throws TypeMismatch, InvalidValue;
  
  char get_char() throws TypeMismatch, InvalidValue;
  
  short get_short() throws TypeMismatch, InvalidValue;
  
  short get_ushort() throws TypeMismatch, InvalidValue;
  
  int get_long() throws TypeMismatch, InvalidValue;
  
  int get_ulong() throws TypeMismatch, InvalidValue;
  
  float get_float() throws TypeMismatch, InvalidValue;
  
  double get_double() throws TypeMismatch, InvalidValue;
  
  String get_string() throws TypeMismatch, InvalidValue;
  
  Object get_reference() throws TypeMismatch, InvalidValue;
  
  TypeCode get_typecode();
  
  long get_longlong() throws TypeMismatch, InvalidValue;
  
  long get_ulonglong() throws TypeMismatch, InvalidValue;
  
  char get_wchar() throws TypeMismatch, InvalidValue;
  
  String get_wstring() throws TypeMismatch, InvalidValue;
  
  Any get_any();
  
  DynAny get_dyn_any();
  
  Serializable get_val() throws TypeMismatch, InvalidValue;
  
  boolean seek(int paramInt);
  
  void rewind();
  
  boolean next() throws TypeMismatch, InvalidValue;
  
  int component_count() throws TypeMismatch, InvalidValue;
  
  DynAny current_component();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\DynAnyOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */