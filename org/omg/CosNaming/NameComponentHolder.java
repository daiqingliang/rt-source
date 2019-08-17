package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameComponentHolder implements Streamable {
  public NameComponent value = null;
  
  public NameComponentHolder() {}
  
  public NameComponentHolder(NameComponent paramNameComponent) { this.value = paramNameComponent; }
  
  public void _read(InputStream paramInputStream) { this.value = NameComponentHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NameComponentHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NameComponentHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NameComponentHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */