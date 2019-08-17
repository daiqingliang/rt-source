package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationHolder implements Streamable {
  public ServerLocation value = null;
  
  public ServerLocationHolder() {}
  
  public ServerLocationHolder(ServerLocation paramServerLocation) { this.value = paramServerLocation; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerLocationHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerLocationHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerLocationHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */