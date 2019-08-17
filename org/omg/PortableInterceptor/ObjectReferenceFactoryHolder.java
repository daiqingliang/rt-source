package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceFactoryHolder implements Streamable {
  public ObjectReferenceFactory value = null;
  
  public ObjectReferenceFactoryHolder() {}
  
  public ObjectReferenceFactoryHolder(ObjectReferenceFactory paramObjectReferenceFactory) { this.value = paramObjectReferenceFactory; }
  
  public void _read(InputStream paramInputStream) { this.value = ObjectReferenceFactoryHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { ObjectReferenceFactoryHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return ObjectReferenceFactoryHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceFactoryHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */