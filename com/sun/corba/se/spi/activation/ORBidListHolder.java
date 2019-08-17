package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBidListHolder implements Streamable {
  public String[] value = null;
  
  public ORBidListHolder() {}
  
  public ORBidListHolder(String[] paramArrayOfString) { this.value = paramArrayOfString; }
  
  public void _read(InputStream paramInputStream) { this.value = ORBidListHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ORBidListHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ORBidListHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ORBidListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */