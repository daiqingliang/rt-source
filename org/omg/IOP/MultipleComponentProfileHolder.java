package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class MultipleComponentProfileHolder implements Streamable {
  public TaggedComponent[] value = null;
  
  public MultipleComponentProfileHolder() {}
  
  public MultipleComponentProfileHolder(TaggedComponent[] paramArrayOfTaggedComponent) { this.value = paramArrayOfTaggedComponent; }
  
  public void _read(InputStream paramInputStream) { this.value = MultipleComponentProfileHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { MultipleComponentProfileHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return MultipleComponentProfileHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\MultipleComponentProfileHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */