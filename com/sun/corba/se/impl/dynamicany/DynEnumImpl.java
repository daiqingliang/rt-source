package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynEnum;

public class DynEnumImpl extends DynAnyBasicImpl implements DynEnum {
  int currentEnumeratorIndex = -1;
  
  private DynEnumImpl() { this(null, (Any)null, false); }
  
  protected DynEnumImpl(ORB paramORB, Any paramAny, boolean paramBoolean) {
    super(paramORB, paramAny, paramBoolean);
    try {
      this.currentEnumeratorIndex = this.any.extract_long();
    } catch (BAD_OPERATION bAD_OPERATION) {
      this.currentEnumeratorIndex = 0;
      this.any.type(this.any.type());
      this.any.insert_long(0);
    } 
  }
  
  protected DynEnumImpl(ORB paramORB, TypeCode paramTypeCode) {
    super(paramORB, paramTypeCode);
    this.currentEnumeratorIndex = 0;
    this.any.insert_long(0);
  }
  
  private int memberCount() {
    int i = 0;
    try {
      i = this.any.type().member_count();
    } catch (BadKind badKind) {}
    return i;
  }
  
  private String memberName(int paramInt) {
    String str = null;
    try {
      str = this.any.type().member_name(paramInt);
    } catch (BadKind badKind) {
    
    } catch (Bounds bounds) {}
    return str;
  }
  
  private int computeCurrentEnumeratorIndex(String paramString) {
    int i = memberCount();
    for (byte b = 0; b < i; b++) {
      if (memberName(b).equals(paramString))
        return b; 
    } 
    return -1;
  }
  
  public int component_count() { return 0; }
  
  public DynAny current_component() throws TypeMismatch {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    throw new TypeMismatch();
  }
  
  public String get_as_string() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return memberName(this.currentEnumeratorIndex);
  }
  
  public void set_as_string(String paramString) throws InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    int i = computeCurrentEnumeratorIndex(paramString);
    if (i == -1)
      throw new InvalidValue(); 
    this.currentEnumeratorIndex = i;
    this.any.insert_long(i);
  }
  
  public int get_as_ulong() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return this.currentEnumeratorIndex;
  }
  
  public void set_as_ulong(int paramInt) throws InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (paramInt < 0 || paramInt >= memberCount())
      throw new InvalidValue(); 
    this.currentEnumeratorIndex = paramInt;
    this.any.insert_long(paramInt);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynEnumImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */