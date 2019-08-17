package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongSeqHolder implements Streamable {
  public int[] value = null;
  
  public LongSeqHolder() {}
  
  public LongSeqHolder(int[] paramArrayOfInt) { this.value = paramArrayOfInt; }
  
  public void _read(InputStream paramInputStream) { this.value = LongSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { LongSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return LongSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\LongSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */