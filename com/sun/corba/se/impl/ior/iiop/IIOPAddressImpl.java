package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class IIOPAddressImpl extends IIOPAddressBase {
  private ORB orb;
  
  private IORSystemException wrapper;
  
  private String host;
  
  private int port;
  
  public IIOPAddressImpl(ORB paramORB, String paramString, int paramInt) {
    this.orb = paramORB;
    this.wrapper = IORSystemException.get(paramORB, "oa.ior");
    if (paramInt < 0 || paramInt > 65535)
      throw this.wrapper.badIiopAddressPort(new Integer(paramInt)); 
    this.host = paramString;
    this.port = paramInt;
  }
  
  public IIOPAddressImpl(InputStream paramInputStream) {
    this.host = paramInputStream.read_string();
    short s = paramInputStream.read_short();
    this.port = shortToInt(s);
  }
  
  public String getHost() { return this.host; }
  
  public int getPort() { return this.port; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPAddressImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */