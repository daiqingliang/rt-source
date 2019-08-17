package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundReasonHolder implements Streamable {
  public NotFoundReason value = null;
  
  public NotFoundReasonHolder() {}
  
  public NotFoundReasonHolder(NotFoundReason paramNotFoundReason) { this.value = paramNotFoundReason; }
  
  public void _read(InputStream paramInputStream) { this.value = NotFoundReasonHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NotFoundReasonHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NotFoundReasonHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFoundReasonHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */