package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBPortInfoListHolder implements Streamable {
  public ORBPortInfo[] value = null;
  
  public ORBPortInfoListHolder() {}
  
  public ORBPortInfoListHolder(ORBPortInfo[] paramArrayOfORBPortInfo) { this.value = paramArrayOfORBPortInfo; }
  
  public void _read(InputStream paramInputStream) { this.value = ORBPortInfoListHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ORBPortInfoListHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ORBPortInfoListHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfoListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */