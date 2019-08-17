package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerDefHolder implements Streamable {
  public ServerDef value = null;
  
  public ServerDefHolder() {}
  
  public ServerDefHolder(ServerDef paramServerDef) { this.value = paramServerDef; }
  
  public void _read(InputStream paramInputStream) { this.value = ServerDefHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServerDefHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServerDefHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\RepositoryPackage\ServerDefHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */