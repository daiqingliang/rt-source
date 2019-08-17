package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BooleanSeqHolder implements Streamable {
  public boolean[] value = null;
  
  public BooleanSeqHolder() {}
  
  public BooleanSeqHolder(boolean[] paramArrayOfBoolean) { this.value = paramArrayOfBoolean; }
  
  public void _read(InputStream paramInputStream) { this.value = BooleanSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { BooleanSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return BooleanSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\BooleanSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */