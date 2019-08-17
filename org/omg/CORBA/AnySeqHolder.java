package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class AnySeqHolder implements Streamable {
  public Any[] value = null;
  
  public AnySeqHolder() {}
  
  public AnySeqHolder(Any[] paramArrayOfAny) { this.value = paramArrayOfAny; }
  
  public void _read(InputStream paramInputStream) { this.value = AnySeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { AnySeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return AnySeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\AnySeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */