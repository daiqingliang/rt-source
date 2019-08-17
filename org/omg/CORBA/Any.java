package org.omg.CORBA;

import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public abstract class Any implements IDLEntity {
  public abstract boolean equal(Any paramAny);
  
  public abstract TypeCode type();
  
  public abstract void type(TypeCode paramTypeCode);
  
  public abstract void read_value(InputStream paramInputStream, TypeCode paramTypeCode) throws MARSHAL;
  
  public abstract void write_value(OutputStream paramOutputStream);
  
  public abstract OutputStream create_output_stream();
  
  public abstract InputStream create_input_stream();
  
  public abstract short extract_short() throws BAD_OPERATION;
  
  public abstract void insert_short(short paramShort);
  
  public abstract int extract_long() throws BAD_OPERATION;
  
  public abstract void insert_long(int paramInt);
  
  public abstract long extract_longlong() throws BAD_OPERATION;
  
  public abstract void insert_longlong(long paramLong);
  
  public abstract short extract_ushort() throws BAD_OPERATION;
  
  public abstract void insert_ushort(short paramShort);
  
  public abstract int extract_ulong() throws BAD_OPERATION;
  
  public abstract void insert_ulong(int paramInt);
  
  public abstract long extract_ulonglong() throws BAD_OPERATION;
  
  public abstract void insert_ulonglong(long paramLong);
  
  public abstract float extract_float() throws BAD_OPERATION;
  
  public abstract void insert_float(float paramFloat);
  
  public abstract double extract_double() throws BAD_OPERATION;
  
  public abstract void insert_double(double paramDouble);
  
  public abstract boolean extract_boolean() throws BAD_OPERATION;
  
  public abstract void insert_boolean(boolean paramBoolean);
  
  public abstract char extract_char() throws BAD_OPERATION;
  
  public abstract void insert_char(char paramChar) throws DATA_CONVERSION;
  
  public abstract char extract_wchar() throws BAD_OPERATION;
  
  public abstract void insert_wchar(char paramChar) throws DATA_CONVERSION;
  
  public abstract byte extract_octet() throws BAD_OPERATION;
  
  public abstract void insert_octet(byte paramByte);
  
  public abstract Any extract_any() throws BAD_OPERATION;
  
  public abstract void insert_any(Any paramAny);
  
  public abstract Object extract_Object() throws BAD_OPERATION;
  
  public abstract void insert_Object(Object paramObject);
  
  public abstract Serializable extract_Value() throws BAD_OPERATION;
  
  public abstract void insert_Value(Serializable paramSerializable);
  
  public abstract void insert_Value(Serializable paramSerializable, TypeCode paramTypeCode) throws MARSHAL;
  
  public abstract void insert_Object(Object paramObject, TypeCode paramTypeCode) throws BAD_PARAM;
  
  public abstract String extract_string() throws BAD_OPERATION;
  
  public abstract void insert_string(String paramString) throws DATA_CONVERSION, MARSHAL;
  
  public abstract String extract_wstring() throws BAD_OPERATION;
  
  public abstract void insert_wstring(String paramString) throws DATA_CONVERSION, MARSHAL;
  
  public abstract TypeCode extract_TypeCode();
  
  public abstract void insert_TypeCode(TypeCode paramTypeCode);
  
  @Deprecated
  public Principal extract_Principal() throws BAD_OPERATION { throw new NO_IMPLEMENT(); }
  
  @Deprecated
  public void insert_Principal(Principal paramPrincipal) { throw new NO_IMPLEMENT(); }
  
  public Streamable extract_Streamable() throws BAD_INV_ORDER { throw new NO_IMPLEMENT(); }
  
  public void insert_Streamable(Streamable paramStreamable) { throw new NO_IMPLEMENT(); }
  
  public BigDecimal extract_fixed() { throw new NO_IMPLEMENT(); }
  
  public void insert_fixed(BigDecimal paramBigDecimal) { throw new NO_IMPLEMENT(); }
  
  public void insert_fixed(BigDecimal paramBigDecimal, TypeCode paramTypeCode) throws BAD_INV_ORDER { throw new NO_IMPLEMENT(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\Any.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */