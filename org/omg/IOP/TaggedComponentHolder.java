package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedComponentHolder implements Streamable {
  public TaggedComponent value = null;
  
  public TaggedComponentHolder() {}
  
  public TaggedComponentHolder(TaggedComponent paramTaggedComponent) { this.value = paramTaggedComponent; }
  
  public void _read(InputStream paramInputStream) { this.value = TaggedComponentHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { TaggedComponentHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return TaggedComponentHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\TaggedComponentHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */