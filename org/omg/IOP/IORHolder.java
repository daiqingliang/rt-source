package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class IORHolder implements Streamable {
  public IOR value = null;
  
  public IORHolder() {}
  
  public IORHolder(IOR paramIOR) { this.value = paramIOR; }
  
  public void _read(InputStream paramInputStream) { this.value = IORHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { IORHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return IORHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\IORHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */