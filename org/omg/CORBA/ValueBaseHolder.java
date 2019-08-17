package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class ValueBaseHolder implements Streamable {
  public Serializable value;
  
  public ValueBaseHolder() {}
  
  public ValueBaseHolder(Serializable paramSerializable) { this.value = paramSerializable; }
  
  public void _read(InputStream paramInputStream) { this.value = ((InputStream)paramInputStream).read_value(); }
  
  public void _write(OutputStream paramOutputStream) { ((OutputStream)paramOutputStream).write_value(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_value); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ValueBaseHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */