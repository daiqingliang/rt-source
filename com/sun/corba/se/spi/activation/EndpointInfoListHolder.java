package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class EndpointInfoListHolder implements Streamable {
  public EndPointInfo[] value = null;
  
  public EndpointInfoListHolder() {}
  
  public EndpointInfoListHolder(EndPointInfo[] paramArrayOfEndPointInfo) { this.value = paramArrayOfEndPointInfo; }
  
  public void _read(InputStream paramInputStream) { this.value = EndpointInfoListHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { EndpointInfoListHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return EndpointInfoListHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\EndpointInfoListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */