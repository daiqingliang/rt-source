package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongLongSeqHolder implements Streamable {
  public long[] value = null;
  
  public LongLongSeqHolder() {}
  
  public LongLongSeqHolder(long[] paramArrayOfLong) { this.value = paramArrayOfLong; }
  
  public void _read(InputStream paramInputStream) { this.value = LongLongSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { LongLongSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return LongLongSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\LongLongSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */