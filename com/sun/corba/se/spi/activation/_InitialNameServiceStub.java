package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _InitialNameServiceStub extends ObjectImpl implements InitialNameService {
  private static String[] __ids = { "IDL:activation/InitialNameService:1.0" };
  
  public void bind(String paramString, Object paramObject, boolean paramBoolean) throws NameAlreadyBound {
    inputStream = null;
    try {
      OutputStream outputStream = _request("bind", true);
      outputStream.write_string(paramString);
      ObjectHelper.write(outputStream, paramObject);
      outputStream.write_boolean(paramBoolean);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/InitialNameService/NameAlreadyBound:1.0"))
        throw NameAlreadyBoundHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      bind(paramString, paramObject, paramBoolean);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_InitialNameServiceStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */