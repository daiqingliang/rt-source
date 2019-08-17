package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceContextListHolder implements Streamable {
  public ServiceContext[] value = null;
  
  public ServiceContextListHolder() {}
  
  public ServiceContextListHolder(ServiceContext[] paramArrayOfServiceContext) { this.value = paramArrayOfServiceContext; }
  
  public void _read(InputStream paramInputStream) { this.value = ServiceContextListHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ServiceContextListHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ServiceContextListHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\ServiceContextListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */