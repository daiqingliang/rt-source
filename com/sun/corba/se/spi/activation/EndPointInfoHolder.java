package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class EndPointInfoHolder implements Streamable {
  public EndPointInfo value = null;
  
  public EndPointInfoHolder() {}
  
  public EndPointInfoHolder(EndPointInfo paramEndPointInfo) { this.value = paramEndPointInfo; }
  
  public void _read(InputStream paramInputStream) { this.value = EndPointInfoHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { EndPointInfoHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return EndPointInfoHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\EndPointInfoHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */