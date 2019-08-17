package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynArray;

public class DynArrayImpl extends DynAnyCollectionImpl implements DynArray {
  private DynArrayImpl() { this(null, (Any)null, false); }
  
  protected DynArrayImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynArrayImpl(ORB paramORB, TypeCode paramTypeCode) { super(paramORB, paramTypeCode); }
  
  protected boolean initializeComponentsFromAny() {
    InputStream inputStream;
    TypeCode typeCode1 = this.any.type();
    int i = getBound();
    TypeCode typeCode2 = getContentType();
    try {
      inputStream = this.any.create_input_stream();
    } catch (BAD_OPERATION bAD_OPERATION) {
      return false;
    } 
    this.components = new DynAny[i];
    this.anys = new Any[i];
    for (byte b = 0; b < i; b++) {
      this.anys[b] = DynAnyUtil.extractAnyFromStream(typeCode2, inputStream, this.orb);
      try {
        this.components[b] = DynAnyUtil.createMostDerivedDynAny(this.anys[b], this.orb, false);
      } catch (InconsistentTypeCode inconsistentTypeCode) {}
    } 
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode() {
    TypeCode typeCode1 = this.any.type();
    int i = getBound();
    TypeCode typeCode2 = getContentType();
    this.components = new DynAny[i];
    this.anys = new Any[i];
    for (byte b = 0; b < i; b++)
      createDefaultComponentAt(b, typeCode2); 
    return true;
  }
  
  protected void checkValue(Object[] paramArrayOfObject) throws InvalidValue {
    if (paramArrayOfObject == null || paramArrayOfObject.length != getBound())
      throw new InvalidValue(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynArrayImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */