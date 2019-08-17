package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

abstract class DynAnyImpl extends LocalObject implements DynAny {
  protected static final int NO_INDEX = -1;
  
  protected static final byte STATUS_DESTROYABLE = 0;
  
  protected static final byte STATUS_UNDESTROYABLE = 1;
  
  protected static final byte STATUS_DESTROYED = 2;
  
  protected ORB orb = null;
  
  protected ORBUtilSystemException wrapper;
  
  protected Any any = null;
  
  protected byte status = 0;
  
  protected int index = -1;
  
  private String[] __ids = { "IDL:omg.org/DynamicAny/DynAny:1.0" };
  
  protected DynAnyImpl() { this.wrapper = ORBUtilSystemException.get("rpc.presentation"); }
  
  protected DynAnyImpl(ORB paramORB, Any paramAny, boolean paramBoolean) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
    if (paramBoolean) {
      this.any = DynAnyUtil.copy(paramAny, paramORB);
    } else {
      this.any = paramAny;
    } 
    this.index = -1;
  }
  
  protected DynAnyImpl(ORB paramORB, TypeCode paramTypeCode) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
    this.any = DynAnyUtil.createDefaultAnyOfType(paramTypeCode, paramORB);
  }
  
  protected DynAnyFactory factory() {
    try {
      return (DynAnyFactory)this.orb.resolve_initial_references("DynAnyFactory");
    } catch (InvalidName invalidName) {
      throw new RuntimeException("Unable to find DynAnyFactory");
    } 
  }
  
  protected Any getAny() { return this.any; }
  
  protected Any getAny(DynAny paramDynAny) { return (paramDynAny instanceof DynAnyImpl) ? ((DynAnyImpl)paramDynAny).getAny() : paramDynAny.to_any(); }
  
  protected void writeAny(OutputStream paramOutputStream) { this.any.write_value(paramOutputStream); }
  
  protected void setStatus(byte paramByte) { this.status = paramByte; }
  
  protected void clearData() { this.any.type(this.any.type()); }
  
  public TypeCode type() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return this.any.type();
  }
  
  public void assign(DynAny paramDynAny) throws TypeMismatch {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any != null && !this.any.type().equal(paramDynAny.type()))
      throw new TypeMismatch(); 
    this.any = paramDynAny.to_any();
  }
  
  public void from_any(Any paramAny) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (this.any != null && !this.any.type().equal(paramAny.type()))
      throw new TypeMismatch(); 
    Any any1 = null;
    try {
      any1 = DynAnyUtil.copy(paramAny, this.orb);
    } catch (Exception exception) {
      throw new InvalidValue();
    } 
    if (!DynAnyUtil.isInitialized(any1))
      throw new InvalidValue(); 
    this.any = any1;
  }
  
  public abstract Any to_any();
  
  public abstract boolean equal(DynAny paramDynAny);
  
  public abstract void destroy();
  
  public abstract DynAny copy();
  
  public String[] _ids() { return (String[])this.__ids.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */