package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceContextHolder implements Streamable {
  public ServiceContext value = null;
  
  public ServiceContextHolder() {}
  
  public ServiceContextHolder(ServiceContext paramServiceContext) { this.value = paramServiceContext; }
  
  public void _read(InputStream paramInputStream) { this.value = ServiceContextHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServiceContextHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServiceContextHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\ServiceContextHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */