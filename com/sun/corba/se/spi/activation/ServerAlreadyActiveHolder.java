package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyActiveHolder implements Streamable {
  public ServerAlreadyActive value = null;
  
  public ServerAlreadyActiveHolder() {}
  
  public ServerAlreadyActiveHolder(ServerAlreadyActive paramServerAlreadyActive) { this.value = paramServerAlreadyActive; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerAlreadyActiveHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerAlreadyActiveHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerAlreadyActiveHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyActiveHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */