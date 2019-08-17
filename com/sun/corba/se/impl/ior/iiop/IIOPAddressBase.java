package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import org.omg.CORBA_2_3.portable.OutputStream;

abstract class IIOPAddressBase implements IIOPAddress {
  protected short intToShort(int paramInt) { return (paramInt > 32767) ? (short)(paramInt - 65536) : (short)paramInt; }
  
  protected int shortToInt(short paramShort) { return (paramShort < 0) ? (paramShort + 65536) : paramShort; }
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_string(getHost());
    int i = getPort();
    paramOutputStream.write_short(intToShort(i));
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof IIOPAddress))
      return false; 
    IIOPAddress iIOPAddress = (IIOPAddress)paramObject;
    return (getHost().equals(iIOPAddress.getHost()) && getPort() == iIOPAddress.getPort());
  }
  
  public int hashCode() { return getHost().hashCode() ^ getPort(); }
  
  public String toString() { return "IIOPAddress[" + getHost() + "," + getPort() + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPAddressBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */