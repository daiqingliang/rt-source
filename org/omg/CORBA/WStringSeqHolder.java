package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WStringSeqHolder implements Streamable {
  public String[] value = null;
  
  public WStringSeqHolder() {}
  
  public WStringSeqHolder(String[] paramArrayOfString) { this.value = paramArrayOfString; }
  
  public void _read(InputStream paramInputStream) { this.value = WStringSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { WStringSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return WStringSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\WStringSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */