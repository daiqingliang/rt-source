package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ActivatorHolder implements Streamable {
  public Activator value = null;
  
  public ActivatorHolder() {}
  
  public ActivatorHolder(Activator paramActivator) { this.value = paramActivator; }
  
  public void _read(InputStream paramInputStream) { this.value = ActivatorHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ActivatorHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ActivatorHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\ActivatorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */