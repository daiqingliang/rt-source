package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextHolder implements Streamable {
  public NamingContext value = null;
  
  public NamingContextHolder() {}
  
  public NamingContextHolder(NamingContext paramNamingContext) { this.value = paramNamingContext; }
  
  public void _read(InputStream paramInputStream) { this.value = NamingContextHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NamingContextHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NamingContextHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */