package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class CharSeqHolder implements Streamable {
  public char[] value = null;
  
  public CharSeqHolder() {}
  
  public CharSeqHolder(char[] paramArrayOfChar) { this.value = paramArrayOfChar; }
  
  public void _read(InputStream paramInputStream) { this.value = CharSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { CharSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return CharSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\CharSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */