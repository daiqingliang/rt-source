package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class Encoding implements IDLEntity {
  public short format = 0;
  
  public byte major_version = 0;
  
  public byte minor_version = 0;
  
  public Encoding() {}
  
  public Encoding(short paramShort, byte paramByte1, byte paramByte2) {
    this.format = paramShort;
    this.major_version = paramByte1;
    this.minor_version = paramByte2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\Encoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */