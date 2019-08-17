package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.IOP.TaggedProfile;

public final class TargetAddress implements IDLEntity {
  private byte[] ___object_key;
  
  private TaggedProfile ___profile;
  
  private IORAddressingInfo ___ior;
  
  private short __discriminator;
  
  private boolean __uninitialized = true;
  
  public short discriminator() {
    if (this.__uninitialized)
      throw new BAD_OPERATION(); 
    return this.__discriminator;
  }
  
  public byte[] object_key() {
    if (this.__uninitialized)
      throw new BAD_OPERATION(); 
    verifyobject_key(this.__discriminator);
    return this.___object_key;
  }
  
  public void object_key(byte[] paramArrayOfByte) {
    this.__discriminator = 0;
    this.___object_key = paramArrayOfByte;
    this.__uninitialized = false;
  }
  
  private void verifyobject_key(short paramShort) {
    if (paramShort != 0)
      throw new BAD_OPERATION(); 
  }
  
  public TaggedProfile profile() {
    if (this.__uninitialized)
      throw new BAD_OPERATION(); 
    verifyprofile(this.__discriminator);
    return this.___profile;
  }
  
  public void profile(TaggedProfile paramTaggedProfile) {
    this.__discriminator = 1;
    this.___profile = paramTaggedProfile;
    this.__uninitialized = false;
  }
  
  private void verifyprofile(short paramShort) {
    if (paramShort != 1)
      throw new BAD_OPERATION(); 
  }
  
  public IORAddressingInfo ior() {
    if (this.__uninitialized)
      throw new BAD_OPERATION(); 
    verifyior(this.__discriminator);
    return this.___ior;
  }
  
  public void ior(IORAddressingInfo paramIORAddressingInfo) {
    this.__discriminator = 2;
    this.___ior = paramIORAddressingInfo;
    this.__uninitialized = false;
  }
  
  private void verifyior(short paramShort) {
    if (paramShort != 2)
      throw new BAD_OPERATION(); 
  }
  
  public void _default() {
    this.__discriminator = Short.MIN_VALUE;
    this.__uninitialized = false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\TargetAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */