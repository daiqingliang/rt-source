package org.omg.CosNaming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _BindingIteratorStub extends ObjectImpl implements BindingIterator {
  private static String[] __ids = { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
  
  public boolean next_one(BindingHolder paramBindingHolder) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("next_one", true);
      inputStream = _invoke(outputStream);
      boolean bool = inputStream.read_boolean();
      paramBindingHolder.value = BindingHelper.read(inputStream);
      return bool;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return next_one(paramBindingHolder);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public boolean next_n(int paramInt, BindingListHolder paramBindingListHolder) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("next_n", true);
      outputStream.write_ulong(paramInt);
      inputStream = _invoke(outputStream);
      boolean bool = inputStream.read_boolean();
      paramBindingListHolder.value = BindingListHelper.read(inputStream);
      return bool;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return next_n(paramInt, paramBindingListHolder);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void destroy() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("destroy", true);
      inputStream = _invoke(outputStream);
      return;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\_BindingIteratorStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */