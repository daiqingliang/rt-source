package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateSeqHolder implements Streamable {
  public ObjectReferenceTemplate[] value = null;
  
  public ObjectReferenceTemplateSeqHolder() {}
  
  public ObjectReferenceTemplateSeqHolder(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate) { this.value = paramArrayOfObjectReferenceTemplate; }
  
  public void _read(InputStream paramInputStream) { this.value = ObjectReferenceTemplateSeqHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ObjectReferenceTemplateSeqHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ObjectReferenceTemplateSeqHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceTemplateSeqHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */