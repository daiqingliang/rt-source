package com.sun.org.omg.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.DefinitionKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _IDLTypeStub extends ObjectImpl implements IDLType {
  private static String[] __ids = { "IDL:omg.org/CORBA/IDLType:1.0", "IDL:omg.org/CORBA/IRObject:1.0" };
  
  public _IDLTypeStub() {}
  
  public _IDLTypeStub(Delegate paramDelegate) { _set_delegate(paramDelegate); }
  
  public TypeCode type() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("_get_type", true);
      inputStream = _invoke(outputStream);
      TypeCode typeCode = inputStream.read_TypeCode();
      return typeCode;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return type();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public DefinitionKind def_kind() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("_get_def_kind", true);
      inputStream = _invoke(outputStream);
      DefinitionKind definitionKind = DefinitionKindHelper.read(inputStream);
      return definitionKind;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return def_kind();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void destroy() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("destroy", true);
      inputStream = _invoke(outputStream);
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      destroy();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) {
    try {
      String str = paramObjectInputStream.readUTF();
      Object object = ORB.init().string_to_object(str);
      Delegate delegate = ((ObjectImpl)object)._get_delegate();
      _set_delegate(delegate);
    } catch (IOException iOException) {}
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) {
    try {
      String str = ORB.init().object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    } catch (IOException iOException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\_IDLTypeStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */