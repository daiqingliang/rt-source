package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynStruct;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

public class DynStructImpl extends DynAnyComplexImpl implements DynStruct {
  private DynStructImpl() { this(null, (Any)null, false); }
  
  protected DynStructImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynStructImpl(ORB paramORB, TypeCode paramTypeCode) {
    super(paramORB, paramTypeCode);
    this.index = 0;
  }
  
  public NameValuePair[] get_members() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    checkInitComponents();
    return this.nameValuePairs;
  }
  
  public NameDynAnyPair[] get_members_as_dyn_any() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    checkInitComponents();
    return this.nameDynAnyPairs;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynStructImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */