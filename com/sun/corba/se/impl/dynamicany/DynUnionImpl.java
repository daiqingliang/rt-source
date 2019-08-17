package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynUnion;

public class DynUnionImpl extends DynAnyConstructedImpl implements DynUnion {
  DynAny discriminator = null;
  
  DynAny currentMember = null;
  
  int currentMemberIndex = -1;
  
  private DynUnionImpl() { this(null, (Any)null, false); }
  
  protected DynUnionImpl(ORB paramORB, Any paramAny, boolean paramBoolean) { super(paramORB, paramAny, paramBoolean); }
  
  protected DynUnionImpl(ORB paramORB, TypeCode paramTypeCode) { super(paramORB, paramTypeCode); }
  
  protected boolean initializeComponentsFromAny() {
    try {
      InputStream inputStream = this.any.create_input_stream();
      Any any1 = DynAnyUtil.extractAnyFromStream(discriminatorType(), inputStream, this.orb);
      this.discriminator = DynAnyUtil.createMostDerivedDynAny(any1, this.orb, false);
      this.currentMemberIndex = currentUnionMemberIndex(any1);
      Any any2 = DynAnyUtil.extractAnyFromStream(memberType(this.currentMemberIndex), inputStream, this.orb);
      this.currentMember = DynAnyUtil.createMostDerivedDynAny(any2, this.orb, false);
      this.components = new DynAny[] { this.discriminator, this.currentMember };
    } catch (InconsistentTypeCode inconsistentTypeCode) {}
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode() {
    try {
      this.discriminator = DynAnyUtil.createMostDerivedDynAny(memberLabel(0), this.orb, false);
      this.index = 0;
      this.currentMemberIndex = 0;
      this.currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(0), this.orb);
      this.components = new DynAny[] { this.discriminator, this.currentMember };
    } catch (InconsistentTypeCode inconsistentTypeCode) {}
    return true;
  }
  
  private TypeCode discriminatorType() {
    TypeCode typeCode = null;
    try {
      typeCode = this.any.type().discriminator_type();
    } catch (BadKind badKind) {}
    return typeCode;
  }
  
  private int memberCount() {
    int i = 0;
    try {
      i = this.any.type().member_count();
    } catch (BadKind badKind) {}
    return i;
  }
  
  private Any memberLabel(int paramInt) {
    Any any = null;
    try {
      any = this.any.type().member_label(paramInt);
    } catch (BadKind badKind) {
    
    } catch (Bounds bounds) {}
    return any;
  }
  
  private TypeCode memberType(int paramInt) {
    TypeCode typeCode = null;
    try {
      typeCode = this.any.type().member_type(paramInt);
    } catch (BadKind badKind) {
    
    } catch (Bounds bounds) {}
    return typeCode;
  }
  
  private String memberName(int paramInt) {
    String str = null;
    try {
      str = this.any.type().member_name(paramInt);
    } catch (BadKind badKind) {
    
    } catch (Bounds bounds) {}
    return str;
  }
  
  private int defaultIndex() {
    int i = -1;
    try {
      i = this.any.type().default_index();
    } catch (BadKind badKind) {}
    return i;
  }
  
  private int currentUnionMemberIndex(Any paramAny) {
    int i = memberCount();
    for (byte b = 0; b < i; b++) {
      Any any = memberLabel(b);
      if (any.equal(paramAny))
        return b; 
    } 
    return (defaultIndex() != -1) ? defaultIndex() : -1;
  }
  
  protected void clearData() {
    super.clearData();
    this.discriminator = null;
    this.currentMember.destroy();
    this.currentMember = null;
    this.currentMemberIndex = -1;
  }
  
  public DynAny get_discriminator() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return checkInitComponents() ? this.discriminator : null;
  }
  
  public void set_discriminator(DynAny paramDynAny) throws TypeMismatch {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (!paramDynAny.type().equal(discriminatorType()))
      throw new TypeMismatch(); 
    paramDynAny = DynAnyUtil.convertToNative(paramDynAny, this.orb);
    Any any = getAny(paramDynAny);
    int i = currentUnionMemberIndex(any);
    if (i == -1) {
      clearData();
      this.index = 0;
    } else {
      checkInitComponents();
      if (this.currentMemberIndex == -1 || i != this.currentMemberIndex) {
        clearData();
        this.index = 1;
        this.currentMemberIndex = i;
        try {
          this.currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(this.currentMemberIndex), this.orb);
        } catch (InconsistentTypeCode inconsistentTypeCode) {}
        this.discriminator = paramDynAny;
        this.components = new DynAny[] { this.discriminator, this.currentMember };
        this.representations = 4;
      } 
    } 
  }
  
  public void set_to_default_member() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    int i = defaultIndex();
    if (i == -1)
      throw new TypeMismatch(); 
    try {
      clearData();
      this.index = 1;
      this.currentMemberIndex = i;
      this.currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(i), this.orb);
      this.components = new DynAny[] { this.discriminator, this.currentMember };
      Any any = this.orb.create_any();
      any.insert_octet((byte)0);
      this.discriminator = DynAnyUtil.createMostDerivedDynAny(any, this.orb, false);
      this.representations = 4;
    } catch (InconsistentTypeCode inconsistentTypeCode) {}
  }
  
  public void set_to_no_active_member() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (defaultIndex() != -1)
      throw new TypeMismatch(); 
    checkInitComponents();
    Any any = getAny(this.discriminator);
    any.type(any.type());
    this.index = 0;
    this.currentMemberIndex = -1;
    this.currentMember.destroy();
    this.currentMember = null;
    this.components[0] = this.discriminator;
    this.representations = 4;
  }
  
  public boolean has_no_active_member() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (defaultIndex() != -1)
      return false; 
    checkInitComponents();
    return checkInitComponents() ? ((this.currentMemberIndex == -1)) : false;
  }
  
  public TCKind discriminator_kind() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    return discriminatorType().kind();
  }
  
  public DynAny member() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (!checkInitComponents() || this.currentMemberIndex == -1)
      throw new InvalidValue(); 
    return this.currentMember;
  }
  
  public String member_name() throws InvalidValue {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (!checkInitComponents() || this.currentMemberIndex == -1)
      throw new InvalidValue(); 
    String str = memberName(this.currentMemberIndex);
    return (str == null) ? "" : str;
  }
  
  public TCKind member_kind() {
    if (this.status == 2)
      throw this.wrapper.dynAnyDestroyed(); 
    if (!checkInitComponents() || this.currentMemberIndex == -1)
      throw new InvalidValue(); 
    return memberType(this.currentMemberIndex).kind();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynUnionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */