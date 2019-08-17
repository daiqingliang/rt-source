package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerIdsHolder implements Streamable {
  public int[] value = null;
  
  public ServerIdsHolder() {}
  
  public ServerIdsHolder(int[] paramArrayOfInt) { this.value = paramArrayOfInt; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerIdsHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerIdsHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerIdsHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerIdsHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */