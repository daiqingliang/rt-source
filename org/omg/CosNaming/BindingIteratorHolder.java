package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingIteratorHolder implements Streamable {
  public BindingIterator value = null;
  
  public BindingIteratorHolder() {}
  
  public BindingIteratorHolder(BindingIterator paramBindingIterator) { this.value = paramBindingIterator; }
  
  public void _read(InputStream paramInputStream) { this.value = BindingIteratorHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { BindingIteratorHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return BindingIteratorHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\BindingIteratorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */