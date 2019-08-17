package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CannotProceedHolder implements Streamable {
  public CannotProceed value = null;
  
  public CannotProceedHolder() {}
  
  public CannotProceedHolder(CannotProceed paramCannotProceed) { this.value = paramCannotProceed; }
  
  public void _read(InputStream paramInputStream) { this.value = CannotProceedHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { CannotProceedHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return CannotProceedHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\CannotProceedHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */