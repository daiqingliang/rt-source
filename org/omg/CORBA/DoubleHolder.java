package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class DoubleHolder implements Streamable {
  public double value;
  
  public DoubleHolder() {}
  
  public DoubleHolder(double paramDouble) { this.value = paramDouble; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_double(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_double(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_double); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DoubleHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */