package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotActiveHolder implements Streamable {
  public ServerNotActive value = null;
  
  public ServerNotActiveHolder() {}
  
  public ServerNotActiveHolder(ServerNotActive paramServerNotActive) { this.value = paramServerNotActive; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerNotActiveHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerNotActiveHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerNotActiveHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ServerNotActiveHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */