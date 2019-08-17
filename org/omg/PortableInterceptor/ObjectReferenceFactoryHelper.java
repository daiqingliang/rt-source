package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class ObjectReferenceFactoryHelper {
  private static String _id = "IDL:omg.org/PortableInterceptor/ObjectReferenceFactory:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, ObjectReferenceFactory paramObjectReferenceFactory) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramObjectReferenceFactory);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static ObjectReferenceFactory extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          ValueMember[] arrayOfValueMember = new ValueMember[0];
          Object object = null;
          __typeCode = ORB.init().create_value_tc(_id, "ObjectReferenceFactory", (short)2, null, arrayOfValueMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static ObjectReferenceFactory read(InputStream paramInputStream) { return (ObjectReferenceFactory)((InputStream)paramInputStream).read_value(id()); }
  
  public static void write(OutputStream paramOutputStream, ObjectReferenceFactory paramObjectReferenceFactory) { ((OutputStream)paramOutputStream).write_value(paramObjectReferenceFactory, id()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ObjectReferenceFactoryHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */