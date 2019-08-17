package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WCharSeqHolder implements Streamable {
  public char[] value = null;
  
  public WCharSeqHolder() {}
  
  public WCharSeqHolder(char[] paramArrayOfChar) { this.value = paramArrayOfChar; }
  
  public void _read(InputStream paramInputStream) { this.value = WCharSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { WCharSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return WCharSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\WCharSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */