package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ValueBase;

public interface ObjectReferenceFactory extends ValueBase {
  Object make_object(String paramString, byte[] paramArrayOfByte);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */