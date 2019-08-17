package org.omg.CosNaming;

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
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;

public class _NamingContextStub extends ObjectImpl implements NamingContext {
  private static String[] __ids = { "IDL:omg.org/CosNaming/NamingContext:1.0" };
  
  public void bind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    inputStream = null;
    try {
      OutputStream outputStream = _request("bind", true);
      NameHelper.write(outputStream, paramArrayOfNameComponent);
      ObjectHelper.write(outputStream, paramObject);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0"))
        throw NotFoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0"))
        throw CannotProceedHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0"))
        throw InvalidNameHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0"))
        throw AlreadyBoundHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      bind(paramArrayOfNameComponent, paramObject);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    inputStream = null;
    try {
      OutputStream outputStream = _request("bind_context", true);
      NameHelper.write(outputStream, paramArrayOfNameComponent);
      NamingContextHelper.write(outputStream, paramNamingContext);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0"))
        throw NotFoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0"))
        throw CannotProceedHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0"))
        throw InvalidNameHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0"))
        throw AlreadyBoundHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      bind_context(paramArrayOfNameComponent, paramNamingContext);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void rebind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    inputStream = null;
    try {
      OutputStream outputStream = _request("rebind", true);
      NameHelper.write(outputStream, paramArrayOfNameComponent);
      ObjectHelper.write(outputStream, paramObject);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0"))
        throw NotFoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0"))
        throw CannotProceedHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0"))
        throw InvalidNameHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      rebind(paramArrayOfNameComponent, paramObject);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    inputStream = null;
    try {
      OutputStream outputStream = _request("rebind_context", true);
      NameHelper.write(outputStream, paramArrayOfNameComponent);
      NamingContextHelper.write(outputStream, paramNamingContext);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0"))
        throw NotFoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0"))
        throw CannotProceedHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0"))
        throw InvalidNameHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      rebind_context(paramArrayOfNameComponent, paramNamingContext);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public Object resolve(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    inputStream = null;
    try {
      OutputStream outputStream = _request("resolve", true);
      NameHelper.write(outputStream, paramArrayOfNameComponent);
      inputStream = _invoke(outputStream);
      Object object = ObjectHelper.read(inputStream);
      return object;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0"))
        throw NotFoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0"))
        throw CannotProceedHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0"))
        throw InvalidNameHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return resolve(paramArrayOfNameComponent);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void unbind(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName {
    inputStream = null;
    try {
      OutputStream outputStream = _request("unbind", true);
      NameHelper.write(outputStream, paramArrayOfNameComponent);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0"))
        throw NotFoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0"))
        throw CannotProceedHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0"))
        throw InvalidNameHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      unbind(paramArrayOfNameComponent);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder) {
    inputStream = null;
    try {
      OutputStream outputStream = _request("list", true);
      outputStream.write_ulong(paramInt);
      inputStream = _invoke(outputStream);
      paramBindingListHolder.value = BindingListHelper.read(inputStream);
      paramBindingIteratorHolder.value = BindingIteratorHelper.read(inputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      list(paramInt, paramBindingListHolder, paramBindingIteratorHolder);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public NamingContext new_context() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("new_context", true);
      inputStream = _invoke(outputStream);
      NamingContext namingContext = NamingContextHelper.read(inputStream);
      return namingContext;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return new_context();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
    inputStream = null;
    try {
      OutputStream outputStream = _request("bind_new_context", true);
      NameHelper.write(outputStream, paramArrayOfNameComponent);
      inputStream = _invoke(outputStream);
      NamingContext namingContext = NamingContextHelper.read(inputStream);
      return namingContext;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotFound:1.0"))
        throw NotFoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0"))
        throw AlreadyBoundHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0"))
        throw CannotProceedHelper.read(inputStream); 
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0"))
        throw InvalidNameHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return bind_new_context(paramArrayOfNameComponent);
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
      if (str.equals("IDL:omg.org/CosNaming/NamingContext/NotEmpty:1.0"))
        throw NotEmptyHelper.read(inputStream); 
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\_NamingContextStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */