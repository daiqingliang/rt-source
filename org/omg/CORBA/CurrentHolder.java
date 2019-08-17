package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CurrentHolder implements Streamable {
  public Current value = null;
  
  public CurrentHolder() {}
  
  public CurrentHolder(Current paramCurrent) { this.value = paramCurrent; }
  
  public void _read(InputStream paramInputStream) { this.value = CurrentHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { CurrentHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return CurrentHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\CurrentHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */