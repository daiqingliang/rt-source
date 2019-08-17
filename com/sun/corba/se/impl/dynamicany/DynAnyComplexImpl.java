package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;

abstract class DynAnyComplexImpl extends DynAnyConstructedImpl {
  String[] names = null;
  
  NameValuePair[] nameValuePairs = null;
  
  NameDynAnyPair[] nameDynAnyPairs = null;
  
  private DynAnyComplexImpl() { this(null, (Any)null, false); }
  
  protected DynAnyComplexImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynAnyComplexImpl(ORB paramORB, TypeCode paramTypeCode) {
    super(paramORB, paramTypeCode);
    this.index = 0;
  }
  
  public String current_member_name() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (!checkInitComponents() || this.index < 0 || this.index >= this.names.length)
      throw new InvalidValue(); 
    return this.names[this.index];
  }
  
  public TCKind current_member_kind() throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (!checkInitComponents() || this.index < 0 || this.index >= this.components.length)
      throw new InvalidValue(); 
    return this.components[this.index].type().kind();
  }
  
  public void set_members(NameValuePair[] paramArrayOfNameValuePair) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (paramArrayOfNameValuePair == null || paramArrayOfNameValuePair.length == 0) {
      clearData();
      return;
    } 
    DynAny dynAny = null;
    TypeCode typeCode = this.any.type();
    int i = 0;
    try {
      i = typeCode.member_count();
    } catch (BadKind badKind) {}
    if (i != paramArrayOfNameValuePair.length) {
      clearData();
      throw new InvalidValue();
    } 
    allocComponents(paramArrayOfNameValuePair);
    for (byte b = 0; b < paramArrayOfNameValuePair.length; b++) {
      if (paramArrayOfNameValuePair[b] != null) {
        String str1 = (paramArrayOfNameValuePair[b]).id;
        String str2 = null;
        try {
          str2 = typeCode.member_name(b);
        } catch (BadKind badKind) {
        
        } catch (Bounds bounds) {}
        if (!str2.equals(str1) && !str1.equals("")) {
          clearData();
          throw new TypeMismatch();
        } 
        Any any = (paramArrayOfNameValuePair[b]).value;
        TypeCode typeCode1 = null;
        try {
          typeCode1 = typeCode.member_type(b);
        } catch (BadKind badKind) {
        
        } catch (Bounds bounds) {}
        if (!typeCode1.equal(any.type())) {
          clearData();
          throw new TypeMismatch();
        } 
        try {
          dynAny = DynAnyUtil.createMostDerivedDynAny(any, this.orb, false);
        } catch (InconsistentTypeCode inconsistentTypeCode) {
          throw new InvalidValue();
        } 
        addComponent(b, str1, any, dynAny);
      } else {
        clearData();
        throw new InvalidValue();
      } 
    } 
    this.index = (paramArrayOfNameValuePair.length == 0) ? -1 : 0;
    this.representations = 4;
  }
  
  public void set_members_as_dyn_any(NameDynAnyPair[] paramArrayOfNameDynAnyPair) throws TypeMismatch, InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (paramArrayOfNameDynAnyPair == null || paramArrayOfNameDynAnyPair.length == 0) {
      clearData();
      return;
    } 
    TypeCode typeCode = this.any.type();
    int i = 0;
    try {
      i = typeCode.member_count();
    } catch (BadKind badKind) {}
    if (i != paramArrayOfNameDynAnyPair.length) {
      clearData();
      throw new InvalidValue();
    } 
    allocComponents(paramArrayOfNameDynAnyPair);
    for (byte b = 0; b < paramArrayOfNameDynAnyPair.length; b++) {
      if (paramArrayOfNameDynAnyPair[b] != null) {
        String str1 = (paramArrayOfNameDynAnyPair[b]).id;
        String str2 = null;
        try {
          str2 = typeCode.member_name(b);
        } catch (BadKind badKind) {
        
        } catch (Bounds bounds) {}
        if (!str2.equals(str1) && !str1.equals("")) {
          clearData();
          throw new TypeMismatch();
        } 
        DynAny dynAny = (paramArrayOfNameDynAnyPair[b]).value;
        Any any = getAny(dynAny);
        TypeCode typeCode1 = null;
        try {
          typeCode1 = typeCode.member_type(b);
        } catch (BadKind badKind) {
        
        } catch (Bounds bounds) {}
        if (!typeCode1.equal(any.type())) {
          clearData();
          throw new TypeMismatch();
        } 
        addComponent(b, str1, any, dynAny);
      } else {
        clearData();
        throw new InvalidValue();
      } 
    } 
    this.index = (paramArrayOfNameDynAnyPair.length == 0) ? -1 : 0;
    this.representations = 4;
  }
  
  private void allocComponents(int paramInt) {
    this.components = new DynAny[paramInt];
    this.names = new String[paramInt];
    this.nameValuePairs = new NameValuePair[paramInt];
    this.nameDynAnyPairs = new NameDynAnyPair[paramInt];
    for (byte b = 0; b < paramInt; b++) {
      this.nameValuePairs[b] = new NameValuePair();
      this.nameDynAnyPairs[b] = new NameDynAnyPair();
    } 
  }
  
  private void allocComponents(NameValuePair[] paramArrayOfNameValuePair) throws TypeMismatch, InvalidValue {
    this.components = new DynAny[paramArrayOfNameValuePair.length];
    this.names = new String[paramArrayOfNameValuePair.length];
    this.nameValuePairs = paramArrayOfNameValuePair;
    this.nameDynAnyPairs = new NameDynAnyPair[paramArrayOfNameValuePair.length];
    for (byte b = 0; b < paramArrayOfNameValuePair.length; b++)
      this.nameDynAnyPairs[b] = new NameDynAnyPair(); 
  }
  
  private void allocComponents(NameDynAnyPair[] paramArrayOfNameDynAnyPair) throws TypeMismatch, InvalidValue {
    this.components = new DynAny[paramArrayOfNameDynAnyPair.length];
    this.names = new String[paramArrayOfNameDynAnyPair.length];
    this.nameValuePairs = new NameValuePair[paramArrayOfNameDynAnyPair.length];
    for (byte b = 0; b < paramArrayOfNameDynAnyPair.length; b++)
      this.nameValuePairs[b] = new NameValuePair(); 
    this.nameDynAnyPairs = paramArrayOfNameDynAnyPair;
  }
  
  private void addComponent(int paramInt, String paramString, Any paramAny, DynAny paramDynAny) {
    this.components[paramInt] = paramDynAny;
    this.names[paramInt] = (paramString != null) ? paramString : "";
    (this.nameValuePairs[paramInt]).id = paramString;
    (this.nameValuePairs[paramInt]).value = paramAny;
    (this.nameDynAnyPairs[paramInt]).id = paramString;
    (this.nameDynAnyPairs[paramInt]).value = paramDynAny;
    if (paramDynAny instanceof DynAnyImpl)
      ((DynAnyImpl)paramDynAny).setStatus((byte)1); 
  }
  
  protected boolean initializeComponentsFromAny() {
    TypeCode typeCode1 = this.any.type();
    TypeCode typeCode2 = null;
    DynAny dynAny = null;
    String str = null;
    int i = 0;
    try {
      i = typeCode1.member_count();
    } catch (BadKind badKind) {}
    InputStream inputStream = this.any.create_input_stream();
    allocComponents(i);
    for (byte b = 0; b < i; b++) {
      try {
        str = typeCode1.member_name(b);
        typeCode2 = typeCode1.member_type(b);
      } catch (BadKind badKind) {
      
      } catch (Bounds bounds) {}
      Any any = DynAnyUtil.extractAnyFromStream(typeCode2, inputStream, this.orb);
      try {
        dynAny = DynAnyUtil.createMostDerivedDynAny(any, this.orb, false);
      } catch (InconsistentTypeCode inconsistentTypeCode) {}
      addComponent(b, str, any, dynAny);
    } 
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode() {
    TypeCode typeCode1 = this.any.type();
    TypeCode typeCode2 = null;
    DynAny dynAny = null;
    int i = 0;
    try {
      i = typeCode1.member_count();
    } catch (BadKind badKind) {}
    allocComponents(i);
    for (byte b = 0; b < i; b++) {
      String str = null;
      try {
        str = typeCode1.member_name(b);
        typeCode2 = typeCode1.member_type(b);
      } catch (BadKind badKind) {
      
      } catch (Bounds bounds) {}
      try {
        dynAny = DynAnyUtil.createMostDerivedDynAny(typeCode2, this.orb);
      } catch (InconsistentTypeCode inconsistentTypeCode) {}
      Any any = getAny(dynAny);
      addComponent(b, str, any, dynAny);
    } 
    return true;
  }
  
  protected void clearData() {
    super.clearData();
    this.names = null;
    this.nameValuePairs = null;
    this.nameDynAnyPairs = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyComplexImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */