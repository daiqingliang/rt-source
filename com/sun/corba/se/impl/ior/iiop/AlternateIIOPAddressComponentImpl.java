package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import org.omg.CORBA_2_3.portable.OutputStream;

public class AlternateIIOPAddressComponentImpl extends TaggedComponentBase implements AlternateIIOPAddressComponent {
  private IIOPAddress addr;
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof AlternateIIOPAddressComponentImpl))
      return false; 
    AlternateIIOPAddressComponentImpl alternateIIOPAddressComponentImpl = (AlternateIIOPAddressComponentImpl)paramObject;
    return this.addr.equals(alternateIIOPAddressComponentImpl.addr);
  }
  
  public int hashCode() { return this.addr.hashCode(); }
  
  public String toString() { return "AlternateIIOPAddressComponentImpl[addr=" + this.addr + "]"; }
  
  public AlternateIIOPAddressComponentImpl(IIOPAddress paramIIOPAddress) { this.addr = paramIIOPAddress; }
  
  public IIOPAddress getAddress() { return this.addr; }
  
  public void writeContents(OutputStream paramOutputStream) { this.addr.write(paramOutputStream); }
  
  public int getId() { return 3; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\AlternateIIOPAddressComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */