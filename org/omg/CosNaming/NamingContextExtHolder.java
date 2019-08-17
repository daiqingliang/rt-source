package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextExtHolder implements Streamable {
  public NamingContextExt value = null;
  
  public NamingContextExtHolder() {}
  
  public NamingContextExtHolder(NamingContextExt paramNamingContextExt) { this.value = paramNamingContextExt; }
  
  public void _read(InputStream paramInputStream) { this.value = NamingContextExtHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { NamingContextExtHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return NamingContextExtHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextExtHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */