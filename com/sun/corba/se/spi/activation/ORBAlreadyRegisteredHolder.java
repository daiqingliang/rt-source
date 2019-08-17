package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBAlreadyRegisteredHolder implements Streamable {
  public ORBAlreadyRegistered value = null;
  
  public ORBAlreadyRegisteredHolder() {}
  
  public ORBAlreadyRegisteredHolder(ORBAlreadyRegistered paramORBAlreadyRegistered) { this.value = paramORBAlreadyRegistered; }
  
  public void _read(InputStream paramInputStream) { this.value = ORBAlreadyRegisteredHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ORBAlreadyRegisteredHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ORBAlreadyRegisteredHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBAlreadyRegisteredHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */