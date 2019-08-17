package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.omg.CORBA.DynAnyPackage.InvalidValue;
import org.omg.CORBA.DynAnyPackage.TypeMismatch;

@Deprecated
public interface DynAny extends Object {
  TypeCode type();
  
  void assign(DynAny paramDynAny) throws Invalid;
  
  void from_any(Any paramAny) throws Invalid;
  
  Any to_any() throws Invalid;
  
  void destroy();
  
  DynAny copy();
  
  void insert_boolean(boolean paramBoolean) throws InvalidValue;
  
  void insert_octet(byte paramByte) throws InvalidValue;
  
  void insert_char(char paramChar) throws InvalidValue;
  
  void insert_short(short paramShort) throws InvalidValue;
  
  void insert_ushort(short paramShort) throws InvalidValue;
  
  void insert_long(int paramInt) throws InvalidValue;
  
  void insert_ulong(int paramInt) throws InvalidValue;
  
  void insert_float(float paramFloat) throws InvalidValue;
  
  void insert_double(double paramDouble) throws InvalidValue;
  
  void insert_string(String paramString) throws InvalidValue;
  
  void insert_reference(Object paramObject) throws InvalidValue;
  
  void insert_typecode(TypeCode paramTypeCode) throws InvalidValue;
  
  void insert_longlong(long paramLong) throws InvalidValue;
  
  void insert_ulonglong(long paramLong) throws InvalidValue;
  
  void insert_wchar(char paramChar) throws InvalidValue;
  
  void insert_wstring(String paramString) throws InvalidValue;
  
  void insert_any(Any paramAny) throws Invalid;
  
  void insert_val(Serializable paramSerializable) throws InvalidValue;
  
  Serializable get_val() throws TypeMismatch;
  
  boolean get_boolean() throws TypeMismatch;
  
  byte get_octet() throws TypeMismatch;
  
  char get_char() throws TypeMismatch;
  
  short get_short() throws TypeMismatch;
  
  short get_ushort() throws TypeMismatch;
  
  int get_long() throws TypeMismatch;
  
  int get_ulong() throws TypeMismatch;
  
  float get_float() throws TypeMismatch;
  
  double get_double() throws TypeMismatch;
  
  String get_string() throws TypeMismatch;
  
  Object get_reference() throws TypeMismatch;
  
  TypeCode get_typecode();
  
  long get_longlong() throws TypeMismatch;
  
  long get_ulonglong() throws TypeMismatch;
  
  char get_wchar() throws TypeMismatch;
  
  String get_wstring() throws TypeMismatch;
  
  Any get_any() throws Invalid;
  
  DynAny current_component();
  
  boolean next() throws TypeMismatch;
  
  boolean seek(int paramInt);
  
  void rewind();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DynAny.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */