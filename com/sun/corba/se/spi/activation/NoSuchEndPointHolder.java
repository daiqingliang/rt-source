package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NoSuchEndPointHolder implements Streamable {
  public NoSuchEndPoint value = null;
  
  public NoSuchEndPointHolder() {}
  
  public NoSuchEndPointHolder(NoSuchEndPoint paramNoSuchEndPoint) { this.value = paramNoSuchEndPoint; }
  
  public void _read(InputStream paramInputStream) { this.value = NoSuchEndPointHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NoSuchEndPointHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NoSuchEndPointHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\NoSuchEndPointHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */