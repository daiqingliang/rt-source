package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynAnyBasicImpl extends DynAnyImpl {
  private DynAnyBasicImpl() { this(null, (Any)null, false); }
  
  protected DynAnyBasicImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynAnyBasicImpl(ORB paramORB, TypeCode paramTypeCode) { super(paramORB, paramTypeCode); }
  
  public void assign(DynAny paramDynAny) throws TypeMismatch {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    super.assign(paramDynAny);
    this.index = -1;
  }
  
  public void from_any(Any paramAny) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    super.from_any(paramAny);
    this.index = -1;
  }
  
  public Any to_any() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return DynAnyUtil.copy(this.any, this.orb);
  }
  
  public boolean equal(DynAny paramDynAny) {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return (paramDynAny == this) ? true : (!this.any.type().equal(paramDynAny.type()) ? false : this.any.equal(getAny(paramDynAny)));
  }
  
  public void destroy() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.status == 0)
      this.status = 2; 
  }
  
  public DynAny copy() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    try {
      return DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, true);
    } catch (InconsistentTypeCode inconsistentTypeCode) {
      return null;
    } 
  }
  
  public DynAny current_component() { return null; }
  
  public int component_count() { return 0; }
  
  public boolean next() { return false; }
  
  public boolean seek(int paramInt) { return false; }
  
  public void rewind() {}
  
  public void insert_boolean(boolean paramBoolean) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 8)
      throw new TypeMismatch(); 
    this.any.insert_boolean(paramBoolean);
  }
  
  public void insert_octet(byte paramByte) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 10)
      throw new TypeMismatch(); 
    this.any.insert_octet(paramByte);
  }
  
  public void insert_char(char paramChar) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 9)
      throw new TypeMismatch(); 
    this.any.insert_char(paramChar);
  }
  
  public void insert_short(short paramShort) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 2)
      throw new TypeMismatch(); 
    this.any.insert_short(paramShort);
  }
  
  public void insert_ushort(short paramShort) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 4)
      throw new TypeMismatch(); 
    this.any.insert_ushort(paramShort);
  }
  
  public void insert_long(int paramInt) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 3)
      throw new TypeMismatch(); 
    this.any.insert_long(paramInt);
  }
  
  public void insert_ulong(int paramInt) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 5)
      throw new TypeMismatch(); 
    this.any.insert_ulong(paramInt);
  }
  
  public void insert_float(float paramFloat) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 6)
      throw new TypeMismatch(); 
    this.any.insert_float(paramFloat);
  }
  
  public void insert_double(double paramDouble) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 7)
      throw new TypeMismatch(); 
    this.any.insert_double(paramDouble);
  }
  
  public void insert_string(String paramString) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 18)
      throw new TypeMismatch(); 
    if (paramString == null)
      throw new InvalidValue(); 
    try {
      if (this.any.type().length() > 0 && this.any.type().length() < paramString.length())
        throw new InvalidValue(); 
    } catch (BadKind badKind) {}
    this.any.insert_string(paramString);
  }
  
  public void insert_reference(Object paramObject) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 14)
      throw new TypeMismatch(); 
    this.any.insert_Object(paramObject);
  }
  
  public void insert_typecode(TypeCode paramTypeCode) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 12)
      throw new TypeMismatch(); 
    this.any.insert_TypeCode(paramTypeCode);
  }
  
  public void insert_longlong(long paramLong) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 23)
      throw new TypeMismatch(); 
    this.any.insert_longlong(paramLong);
  }
  
  public void insert_ulonglong(long paramLong) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 24)
      throw new TypeMismatch(); 
    this.any.insert_ulonglong(paramLong);
  }
  
  public void insert_wchar(char paramChar) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 26)
      throw new TypeMismatch(); 
    this.any.insert_wchar(paramChar);
  }
  
  public void insert_wstring(String paramString) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 27)
      throw new TypeMismatch(); 
    if (paramString == null)
      throw new InvalidValue(); 
    try {
      if (this.any.type().length() > 0 && this.any.type().length() < paramString.length())
        throw new InvalidValue(); 
    } catch (BadKind badKind) {}
    this.any.insert_wstring(paramString);
  }
  
  public void insert_any(Any paramAny) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 11)
      throw new TypeMismatch(); 
    this.any.insert_any(paramAny);
  }
  
  public void insert_dyn_any(DynAny paramDynAny) throws TypeMismatch {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 11)
      throw new TypeMismatch(); 
    this.any.insert_any(paramDynAny.to_any());
  }
  
  public void insert_val(Serializable paramSerializable) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    int i = this.any.type().kind().value();
    if (i != 29 && i != 30)
      throw new TypeMismatch(); 
    this.any.insert_Value(paramSerializable);
  }
  
  public Serializable get_val() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    int i = this.any.type().kind().value();
    if (i != 29 && i != 30)
      throw new TypeMismatch(); 
    return this.any.extract_Value();
  }
  
  public boolean get_boolean() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 8)
      throw new TypeMismatch(); 
    return this.any.extract_boolean();
  }
  
  public byte get_octet() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 10)
      throw new TypeMismatch(); 
    return this.any.extract_octet();
  }
  
  public char get_char() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 9)
      throw new TypeMismatch(); 
    return this.any.extract_char();
  }
  
  public short get_short() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 2)
      throw new TypeMismatch(); 
    return this.any.extract_short();
  }
  
  public short get_ushort() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 4)
      throw new TypeMismatch(); 
    return this.any.extract_ushort();
  }
  
  public int get_long() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 3)
      throw new TypeMismatch(); 
    return this.any.extract_long();
  }
  
  public int get_ulong() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 5)
      throw new TypeMismatch(); 
    return this.any.extract_ulong();
  }
  
  public float get_float() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 6)
      throw new TypeMismatch(); 
    return this.any.extract_float();
  }
  
  public double get_double() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 7)
      throw new TypeMismatch(); 
    return this.any.extract_double();
  }
  
  public String get_string() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 18)
      throw new TypeMismatch(); 
    return this.any.extract_string();
  }
  
  public Object get_reference() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 14)
      throw new TypeMismatch(); 
    return this.any.extract_Object();
  }
  
  public TypeCode get_typecode() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 12)
      throw new TypeMismatch(); 
    return this.any.extract_TypeCode();
  }
  
  public long get_longlong() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 23)
      throw new TypeMismatch(); 
    return this.any.extract_longlong();
  }
  
  public long get_ulonglong() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 24)
      throw new TypeMismatch(); 
    return this.any.extract_ulonglong();
  }
  
  public char get_wchar() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 26)
      throw new TypeMismatch(); 
    return this.any.extract_wchar();
  }
  
  public String get_wstring() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 27)
      throw new TypeMismatch(); 
    return this.any.extract_wstring();
  }
  
  public Any get_any() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 11)
      throw new TypeMismatch(); 
    return this.any.extract_any();
  }
  
  public DynAny get_dyn_any() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any.type().kind().value() != 11)
      throw new TypeMismatch(); 
    try {
      return DynAnyUtil.createMostDerivedDynAny(this.any.extract_any(), this.orb, true);
    } catch (InconsistentTypeCode inconsistentTypeCode) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyBasicImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */