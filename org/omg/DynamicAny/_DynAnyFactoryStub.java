package org.omg.DynamicAny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public class _DynAnyFactoryStub extends ObjectImpl implements DynAnyFactory {
  public static final Class _opsClass = DynAnyFactoryOperations.class;
  
  private static String[] __ids = { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };
  
  public DynAny create_dyn_any(Any paramAny) throws InconsistentTypeCode {
    servantObject = _servant_preinvoke("create_dyn_any", _opsClass);
    DynAnyFactoryOperations dynAnyFactoryOperations = (DynAnyFactoryOperations)servantObject.servant;
    try {
      return dynAnyFactoryOperations.create_dyn_any(paramAny);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public DynAny create_dyn_any_from_type_code(TypeCode paramTypeCode) throws InconsistentTypeCode {
    servantObject = _servant_preinvoke("create_dyn_any_from_type_code", _opsClass);
    DynAnyFactoryOperations dynAnyFactoryOperations = (DynAnyFactoryOperations)servantObject.servant;
    try {
      return dynAnyFactoryOperations.create_dyn_any_from_type_code(paramTypeCode);
    } finally {
      _servant_postinvoke(servantObject);
    } 
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
    String str = paramObjectInputStream.readUTF();
    String[] arrayOfString = null;
    Properties properties = null;
    oRB = ORB.init(arrayOfString, properties);
    try {
      Object object = oRB.string_to_object(str);
      Delegate delegate = ((ObjectImpl)object)._get_delegate();
      _set_delegate(delegate);
    } finally {
      oRB.destroy();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    String[] arrayOfString = null;
    Properties properties = null;
    oRB = ORB.init(arrayOfString, properties);
    try {
      String str = oRB.object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    } finally {
      oRB.destroy();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\DynamicAny\_DynAnyFactoryStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */