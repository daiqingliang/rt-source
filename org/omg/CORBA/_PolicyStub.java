package org.omg.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _PolicyStub extends ObjectImpl implements Policy {
  private static String[] __ids = { "IDL:omg.org/CORBA/Policy:1.0" };
  
  public _PolicyStub() {}
  
  public _PolicyStub(Delegate paramDelegate) { _set_delegate(paramDelegate); }
  
  public int policy_type() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("_get_policy_type", true);
      inputStream = _invoke(outputStream);
      int i = PolicyTypeHelper.read(inputStream);
      return i;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return policy_type();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public Policy copy() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("copy", true);
      inputStream = _invoke(outputStream);
      Policy policy = PolicyHelper.read(inputStream);
      return policy;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return copy();
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\_PolicyStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */