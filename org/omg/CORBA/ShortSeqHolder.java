package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ShortSeqHolder implements Streamable {
  public short[] value = null;
  
  public ShortSeqHolder() {}
  
  public ShortSeqHolder(short[] paramArrayOfShort) { this.value = paramArrayOfShort; }
  
  public void _read(InputStream paramInputStream) { this.value = ShortSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ShortSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ShortSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ShortSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */