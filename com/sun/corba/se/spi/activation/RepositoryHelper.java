package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class RepositoryHelper {
  private static String _id = "IDL:activation/Repository:1.0";
  
  private static TypeCode __typeCode = null;
  
  public static void insert(Any paramAny, Repository paramRepository) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramRepository);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static Repository extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      __typeCode = ORB.init().create_interface_tc(id(), "Repository"); 
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static Repository read(InputStream paramInputStream) { return narrow(paramInputStream.read_Object(_RepositoryStub.class)); }
  
  public static void write(OutputStream paramOutputStream, Repository paramRepository) { paramOutputStream.write_Object(paramRepository); }
  
  public static Repository narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Repository)
      return (Repository)paramObject; 
    if (!paramObject._is_a(id()))
      throw new BAD_PARAM(); 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _RepositoryStub _RepositoryStub = new _RepositoryStub();
    _RepositoryStub._set_delegate(delegate);
    return _RepositoryStub;
  }
  
  public static Repository unchecked_narrow(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Repository)
      return (Repository)paramObject; 
    Delegate delegate = ((ObjectImpl)paramObject)._get_delegate();
    _RepositoryStub _RepositoryStub = new _RepositoryStub();
    _RepositoryStub._set_delegate(delegate);
    return _RepositoryStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\RepositoryHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */