package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CurrentHelper {
  private static String _id = "IDL:omg.org/CORBA/Current:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Current paramCurrent) { throw new MARSHAL(); }
  
  public static Current extract(Any paramAny) { throw new MARSHAL(); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "Current"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Current read(InputStream paramInputStream) { throw new MARSHAL(); }
  
  public static void write(OutputStream paramOutputStream, Current paramCurrent) { throw new MARSHAL(); }
  
  public static Current narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Current)
      return (Current)paramObject; 
    throw new BAD_PARAM();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\CurrentHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */