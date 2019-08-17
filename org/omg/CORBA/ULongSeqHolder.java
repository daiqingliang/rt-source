package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ULongSeqHolder implements Streamable {
  public int[] value = null;
  
  public ULongSeqHolder() {}
  
  public ULongSeqHolder(int[] paramArrayOfInt) { this.value = paramArrayOfInt; }
  
  public void _read(InputStream paramInputStream) { this.value = ULongSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ULongSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ULongSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ULongSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */