package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LocatorHolder implements Streamable {
  public Locator value = null;
  
  public LocatorHolder() {}
  
  public LocatorHolder(Locator paramLocator) { this.value = paramLocator; }
  
  public void _read(InputStream paramInputStream) { this.value = LocatorHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { LocatorHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return LocatorHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\LocatorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */