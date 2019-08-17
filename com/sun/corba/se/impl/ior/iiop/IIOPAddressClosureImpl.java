package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.orbutil.closure.Closure;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class IIOPAddressClosureImpl extends IIOPAddressBase {
  private Closure host;
  
  private Closure port;
  
  public IIOPAddressClosureImpl(Closure paramClosure1, Closure paramClosure2) {
    this.host = paramClosure1;
    this.port = paramClosure2;
  }
  
  public String getHost() { return (String)this.host.evaluate(); }
  
  public int getPort() {
    Integer integer = (Integer)this.port.evaluate();
    return integer.intValue();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPAddressClosureImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */