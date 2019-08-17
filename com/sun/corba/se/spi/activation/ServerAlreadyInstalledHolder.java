package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyInstalledHolder implements Streamable {
  public ServerAlreadyInstalled value = null;
  
  public ServerAlreadyInstalledHolder() {}
  
  public ServerAlreadyInstalledHolder(ServerAlreadyInstalled paramServerAlreadyInstalled) { this.value = paramServerAlreadyInstalled; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerAlreadyInstalledHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerAlreadyInstalledHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerAlreadyInstalledHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyInstalledHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */