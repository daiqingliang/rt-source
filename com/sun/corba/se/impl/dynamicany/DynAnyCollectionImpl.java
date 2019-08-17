package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyCollectionImpl extends DynAnyConstructedImpl {
  Any[] anys = null;
  
  private DynAnyCollectionImpl() { this(null, (Any)null, false); }
  
  protected DynAnyCollectionImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynAnyCollectionImpl(ORB paramORB, TypeCode paramTypeCode) { super(paramORB, paramTypeCode); }
  
  protected void createDefaultComponentAt(int paramInt, TypeCode paramTypeCode) {
    try {
      this.components[paramInt] = DynAnyUtil.createMostDerivedDynAny(paramTypeCode, this.orb);
    } catch (InconsistentTypeCode inconsistentTypeCode) {}
    this.anys[paramInt] = getAny(this.components[paramInt]);
  }
  
  protected TypeCode getContentType() {
    try {
      return this.any.type().content_type();
    } catch (BadKind badKind) {
      return null;
    } 
  }
  
  protected int getBound() {
    try {
      return this.any.type().length();
    } catch (BadKind badKind) {
      return 0;
    } 
  }
  
  public Any[] get_elements() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return checkInitComponents() ? this.anys : null;
  }
  
  protected abstract void checkValue(Object[] paramArrayOfObject) throws InvalidValue;
  
  public void set_elements(Any[] paramArrayOfAny) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    checkValue(paramArrayOfAny);
    this.components = new DynAny[paramArrayOfAny.length];
    this.anys = paramArrayOfAny;
    TypeCode typeCode = getContentType();
    for (byte b = 0; b < paramArrayOfAny.length; b++) {
      if (paramArrayOfAny[b] != null) {
        if (!paramArrayOfAny[b].type().equal(typeCode)) {
          clearData();
          throw new TypeMismatch();
        } 
        try {
          this.components[b] = DynAnyUtil.createMostDerivedDynAny(paramArrayOfAny[b], this.orb, false);
        } catch (InconsistentTypeCode inconsistentTypeCode) {
          throw new InvalidValue();
        } 
      } else {
        clearData();
        throw new InvalidValue();
      } 
    } 
    this.index = (paramArrayOfAny.length == 0) ? -1 : 0;
    this.representations = 4;
  }
  
  public DynAny[] get_elements_as_dyn_any() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return checkInitComponents() ? this.components : null;
  }
  
  public void set_elements_as_dyn_any(DynAny[] paramArrayOfDynAny) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    checkValue(paramArrayOfDynAny);
    this.components = (paramArrayOfDynAny == null) ? emptyComponents : paramArrayOfDynAny;
    this.anys = new Any[paramArrayOfDynAny.length];
    TypeCode typeCode = getContentType();
    for (byte b = 0; b < paramArrayOfDynAny.length; b++) {
      if (paramArrayOfDynAny[b] != null) {
        if (!paramArrayOfDynAny[b].type().equal(typeCode)) {
          clearData();
          throw new TypeMismatch();
        } 
        this.anys[b] = getAny(paramArrayOfDynAny[b]);
      } else {
        clearData();
        throw new InvalidValue();
      } 
    } 
    this.index = (paramArrayOfDynAny.length == 0) ? -1 : 0;
    this.representations = 4;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyCollectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */