package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.RepositoryHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _CodeBaseStub extends ObjectImpl implements CodeBase {
  private static String[] __ids = { "IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0" };
  
  public _CodeBaseStub() {}
  
  public _CodeBaseStub(Delegate paramDelegate) { _set_delegate(paramDelegate); }
  
  public Repository get_ir() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("get_ir", true);
      inputStream = _invoke(outputStream);
      Repository repository = RepositoryHelper.read(inputStream);
      return repository;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return get_ir();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public String implementation(String paramString) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("implementation", true);
      RepositoryIdHelper.write(outputStream, paramString);
      inputStream = _invoke(outputStream);
      String str = URLHelper.read(inputStream);
      return str;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return implementation(paramString);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public String[] implementations(String[] paramArrayOfString) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("implementations", true);
      RepositoryIdSeqHelper.write(outputStream, paramArrayOfString);
      inputStream = _invoke(outputStream);
      String[] arrayOfString = URLSeqHelper.read(inputStream);
      return arrayOfString;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return implementations(paramArrayOfString);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public FullValueDescription meta(String paramString) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("meta", true);
      RepositoryIdHelper.write(outputStream, paramString);
      inputStream = _invoke(outputStream);
      FullValueDescription fullValueDescription = FullValueDescriptionHelper.read(inputStream);
      return fullValueDescription;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return meta(paramString);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public FullValueDescription[] metas(String[] paramArrayOfString) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("metas", true);
      RepositoryIdSeqHelper.write(outputStream, paramArrayOfString);
      inputStream = _invoke(outputStream);
      FullValueDescription[] arrayOfFullValueDescription = ValueDescSeqHelper.read(inputStream);
      return arrayOfFullValueDescription;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return metas(paramArrayOfString);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public String[] bases(String paramString) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("bases", true);
      RepositoryIdHelper.write(outputStream, paramString);
      inputStream = _invoke(outputStream);
      String[] arrayOfString = RepositoryIdSeqHelper.read(inputStream);
      return arrayOfString;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return bases(paramString);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\SendingContext\_CodeBaseStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */