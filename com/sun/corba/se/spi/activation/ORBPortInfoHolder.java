package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBPortInfoHolder implements Streamable {
  public ORBPortInfo value = null;
  
  public ORBPortInfoHolder() {}
  
  public ORBPortInfoHolder(ORBPortInfo paramORBPortInfo) { this.value = paramORBPortInfo; }
  
  public void _read(InputStream paramInputStream) { this.value = ORBPortInfoHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ORBPortInfoHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ORBPortInfoHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfoHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */