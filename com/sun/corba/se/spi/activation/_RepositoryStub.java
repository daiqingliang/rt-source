package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
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

public class _RepositoryStub extends ObjectImpl implements Repository {
  private static String[] __ids = { "IDL:activation/Repository:1.0" };
  
  public int registerServer(ServerDef paramServerDef) throws ServerAlreadyRegistered, BadServerDefinition {
    inputStream = null;
    try {
      OutputStream outputStream = _request("registerServer", true);
      ServerDefHelper.write(outputStream, paramServerDef);
      inputStream = _invoke(outputStream);
      int i = ServerIdHelper.read(inputStream);
      return i;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerAlreadyRegistered:1.0"))
        throw ServerAlreadyRegisteredHelper.read(inputStream); 
      if (str.equals("IDL:activation/BadServerDefinition:1.0"))
        throw BadServerDefinitionHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return registerServer(paramServerDef);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void unregisterServer(int paramInt) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("unregisterServer", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      unregisterServer(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public ServerDef getServer(int paramInt) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("getServer", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      ServerDef serverDef = ServerDefHelper.read(inputStream);
      return serverDef;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return getServer(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public boolean isInstalled(int paramInt) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("isInstalled", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      boolean bool = inputStream.read_boolean();
      return bool;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return isInstalled(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void install(int paramInt) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("install", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerAlreadyInstalled:1.0"))
        throw ServerAlreadyInstalledHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      install(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void uninstall(int paramInt) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("uninstall", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerAlreadyUninstalled:1.0"))
        throw ServerAlreadyUninstalledHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      uninstall(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public int[] listRegisteredServers() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("listRegisteredServers", true);
      inputStream = _invoke(outputStream);
      int[] arrayOfInt = ServerIdsHelper.read(inputStream);
      return arrayOfInt;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return listRegisteredServers();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public String[] getApplicationNames() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("getApplicationNames", true);
      inputStream = _invoke(outputStream);
      String[] arrayOfString = StringSeqHelper.read(inputStream);
      return arrayOfString;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return getApplicationNames();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public int getServerID(String paramString) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("getServerID", true);
      outputStream.write_string(paramString);
      inputStream = _invoke(outputStream);
      int i = ServerIdHelper.read(inputStream);
      return i;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return getServerID(paramString);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_RepositoryStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */