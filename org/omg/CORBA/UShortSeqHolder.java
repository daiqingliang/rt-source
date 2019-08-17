package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class UShortSeqHolder implements Streamable {
  public short[] value = null;
  
  public UShortSeqHolder() {}
  
  public UShortSeqHolder(short[] paramArrayOfShort) { this.value = paramArrayOfShort; }
  
  public void _read(InputStream paramInputStream) { this.value = UShortSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { UShortSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return UShortSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UShortSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */