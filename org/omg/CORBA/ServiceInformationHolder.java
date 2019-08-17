package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceInformationHolder implements Streamable {
  public ServiceInformation value;
  
  public ServiceInformationHolder() { this(null); }
  
  public ServiceInformationHolder(ServiceInformation paramServiceInformation) { this.value = paramServiceInformation; }
  
  public void _write(OutputStream paramOutputStream) { ServiceInformationHelper.write(paramOutputStream, this.value); }
  
  public void _read(InputStream paramInputStream) { this.value = ServiceInformationHelper.read(paramInputStream); }
  
  public TypeCode _type() { return ServiceInformationHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ServiceInformationHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */