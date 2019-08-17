package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class StringHolder implements Streamable {
  public String value;
  
  public StringHolder() {}
  
  public StringHolder(String paramString) { this.value = paramString; }
  
  public void _read(InputStream paramInputStream) { this.value = paramInputStream.read_string(); }
  
  public void _write(OutputStream paramOutputStream) { paramOutputStream.write_string(this.value); }
  
  public TypeCode _type() { return ORB.init().get_primitive_tc(TCKind.tk_string); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\StringHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */