package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyRegisteredHolder implements Streamable {
  public ServerAlreadyRegistered value = null;
  
  public ServerAlreadyRegisteredHolder() {}
  
  public ServerAlreadyRegisteredHolder(ServerAlreadyRegistered paramServerAlreadyRegistered) { this.value = paramServerAlreadyRegistered; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerAlreadyRegisteredHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerAlreadyRegisteredHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerAlreadyRegisteredHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyRegisteredHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */