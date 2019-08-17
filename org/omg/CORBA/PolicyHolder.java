package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyHolder implements Streamable {
  public Policy value = null;
  
  public PolicyHolder() {}
  
  public PolicyHolder(Policy paramPolicy) { this.value = paramPolicy; }
  
  public void _read(InputStream paramInputStream) { this.value = PolicyHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { PolicyHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return PolicyHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PolicyHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */