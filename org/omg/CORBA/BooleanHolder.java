package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BooleanHolder implements Streamable {
  public boolean value;
  
  public BooleanHolder() {}
  
  public BooleanHolder(boolean paramBoolean) { this.value = paramBoolean; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_boolean(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_boolean(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_boolean); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\BooleanHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */