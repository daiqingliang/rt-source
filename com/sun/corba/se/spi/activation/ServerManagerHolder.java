package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerManagerHolder implements Streamable {
  public ServerManager value = null;
  
  public ServerManagerHolder() {}
  
  public ServerManagerHolder(ServerManager paramServerManager) { this.value = paramServerManager; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerManagerHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerManagerHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerManagerHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerManagerHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */