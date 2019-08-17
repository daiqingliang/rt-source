package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynSequence;

public class DynSequenceImpl extends DynAnyCollectionImpl implements DynSequence {
  private DynSequenceImpl() { this(null, (Any)null, false); }
  
  protected DynSequenceImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynSequenceImpl(ORB paramORB, TypeCode paramTypeCode) { super(paramORB, paramTypeCode); }
  
  protected boolean initializeComponentsFromAny() {
    InputStream inputStream;
    TypeCode typeCode1 = this.any.type();
    TypeCode typeCode2 = getContentType();
    try {
      inputStream = this.any.create_input_stream();
    } catch (BAD_OPERATION bAD_OPERATION) {
      return false;
    } 
    int i = inputStream.read_long();
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
    this.components = new DynAny[0];
    this.anys = new Any[0];
    return true;
  }
  
  protected boolean initializeAnyFromComponents() {
    OutputStream outputStream = this.any.create_output_stream();
    outputStream.write_long(this.components.length);
    for (byte b = 0; b < this.components.length; b++) {
      if (this.components[b] instanceof DynAnyImpl) {
        ((DynAnyImpl)this.components[b]).writeAny(outputStream);
      } else {
        this.components[b].to_any().write_value(outputStream);
      } 
    } 
    this.any.read_value(outputStream.create_input_stream(), this.any.type());
    return true;
  }
  
  public int get_length() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return checkInitComponents() ? this.components.length : 0;
  }
  
  public void set_length(int paramInt) throws InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    int i = getBound();
    if (i > 0 && paramInt > i)
      throw new InvalidValue(); 
    checkInitComponents();
    int j = this.components.length;
    if (paramInt > j) {
      DynAny[] arrayOfDynAny = new DynAny[paramInt];
      Any[] arrayOfAny = new Any[paramInt];
      System.arraycopy(this.components, 0, arrayOfDynAny, 0, j);
      System.arraycopy(this.anys, 0, arrayOfAny, 0, j);
      this.components = arrayOfDynAny;
      this.anys = arrayOfAny;
      TypeCode typeCode = getContentType();
      for (int k = j; k < paramInt; k++)
        createDefaultComponentAt(k, typeCode); 
      if (this.index == -1)
        this.index = j; 
    } else if (paramInt < j) {
      DynAny[] arrayOfDynAny = new DynAny[paramInt];
      Any[] arrayOfAny = new Any[paramInt];
      System.arraycopy(this.components, 0, arrayOfDynAny, 0, paramInt);
      System.arraycopy(this.anys, 0, arrayOfAny, 0, paramInt);
      this.components = arrayOfDynAny;
      this.anys = arrayOfAny;
      if (paramInt == 0 || this.index >= paramInt)
        this.index = -1; 
    } else if (this.index == -1 && paramInt > 0) {
      this.index = 0;
    } 
  }
  
  protected void checkValue(Object[] paramArrayOfObject) throws InvalidValue {
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0) {
      clearData();
      this.index = -1;
      return;
    } 
    this.index = 0;
    int i = getBound();
    if (i > 0 && paramArrayOfObject.length > i)
      throw new InvalidValue(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynSequenceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */