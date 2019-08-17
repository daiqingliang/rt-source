package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHeldDownHolder implements Streamable {
  public ServerHeldDown value = null;
  
  public ServerHeldDownHolder() {}
  
  public ServerHeldDownHolder(ServerHeldDown paramServerHeldDown) { this.value = paramServerHeldDown; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerHeldDownHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerHeldDownHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerHeldDownHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerHeldDownHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */