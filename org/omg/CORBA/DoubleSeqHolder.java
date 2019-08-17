package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class DoubleSeqHolder implements Streamable {
  public double[] value = null;
  
  public DoubleSeqHolder() {}
  
  public DoubleSeqHolder(double[] paramArrayOfDouble) { this.value = paramArrayOfDouble; }
  
  public void _read(InputStream paramInputStream) { this.value = DoubleSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { DoubleSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return DoubleSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DoubleSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */