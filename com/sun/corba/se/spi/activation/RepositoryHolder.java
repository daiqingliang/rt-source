package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class RepositoryHolder implements Streamable {
  public Repository value = null;
  
  public RepositoryHolder() {}
  
  public RepositoryHolder(Repository paramRepository) { this.value = paramRepository; }
  
  public void _read(InputStream paramInputStream) { this.value = RepositoryHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { RepositoryHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return RepositoryHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\RepositoryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */