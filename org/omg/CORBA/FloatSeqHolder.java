package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class FloatSeqHolder implements Streamable {
  public float[] value = null;
  
  public FloatSeqHolder() {}
  
  public FloatSeqHolder(float[] paramArrayOfFloat) { this.value = paramArrayOfFloat; }
  
  public void _read(InputStream paramInputStream) { this.value = FloatSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { FloatSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return FloatSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\FloatSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */