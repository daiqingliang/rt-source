package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class PolicyListHolder implements Streamable {
  public Policy[] value = null;
  
  public PolicyListHolder() {}
  
  public PolicyListHolder(Policy[] paramArrayOfPolicy) { this.value = paramArrayOfPolicy; }
  
  public void _read(InputStream paramInputStream) { this.value = PolicyListHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { PolicyListHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return PolicyListHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PolicyListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */