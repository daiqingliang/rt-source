package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedProfileHolder implements Streamable {
  public TaggedProfile value = null;
  
  public TaggedProfileHolder() {}
  
  public TaggedProfileHolder(TaggedProfile paramTaggedProfile) { this.value = paramTaggedProfile; }
  
  public void _read(InputStream paramInputStream) { this.value = TaggedProfileHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { TaggedProfileHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return TaggedProfileHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\TaggedProfileHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */