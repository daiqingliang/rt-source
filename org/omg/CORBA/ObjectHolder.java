package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectHolder implements Streamable {
  public Object value;
  
  public ObjectHolder() {}
  
  public ObjectHolder(Object paramObject) { this.value = paramObject; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_Object(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_Object(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_objref); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ObjectHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */