package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class WrongTransactionHolder implements Streamable {
  public WrongTransaction value = null;
  
  public WrongTransactionHolder() {}
  
  public WrongTransactionHolder(WrongTransaction paramWrongTransaction) { this.value = paramWrongTransaction; }
  
  public void _read(InputStream paramInputStream) { this.value = WrongTransactionHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { WrongTransactionHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return WrongTransactionHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\WrongTransactionHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */