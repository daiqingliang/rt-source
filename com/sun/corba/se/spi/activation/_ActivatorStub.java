package com.sun.corba.se.spi.activation;

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

public class _ActivatorStub extends ObjectImpl implements Activator {
  private static String[] __ids = { "IDL:activation/Activator:1.0" };
  
  public void active(int paramInt, Server paramServer) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("active", true);
      ServerIdHelper.write(outputStream, paramInt);
      ServerHelper.write(outputStream, paramServer);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      active(paramInt, paramServer);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void registerEndpoints(int paramInt, String paramString, EndPointInfo[] paramArrayOfEndPointInfo) throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("registerEndpoints", true);
      ServerIdHelper.write(outputStream, paramInt);
      ORBidHelper.write(outputStream, paramString);
      EndpointInfoListHelper.write(outputStream, paramArrayOfEndPointInfo);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      if (str.equals("IDL:activation/NoSuchEndPoint:1.0"))
        throw NoSuchEndPointHelper.read(inputStream); 
      if (str.equals("IDL:activation/ORBAlreadyRegistered:1.0"))
        throw ORBAlreadyRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      registerEndpoints(paramInt, paramString, paramArrayOfEndPointInfo);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public int[] getActiveServers() {
    inputStream = null;
    try {
      OutputStream outputStream = _request("getActiveServers", true);
      inputStream = _invoke(outputStream);
      int[] arrayOfInt = ServerIdsHelper.read(inputStream);
      return arrayOfInt;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return getActiveServers();
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void activate(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
    inputStream = null;
    try {
      OutputStream outputStream = _request("activate", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerAlreadyActive:1.0"))
        throw ServerAlreadyActiveHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerHeldDown:1.0"))
        throw ServerHeldDownHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      activate(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void shutdown(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
    inputStream = null;
    try {
      OutputStream outputStream = _request("shutdown", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      return;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotActive:1.0"))
        throw ServerNotActiveHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      shutdown(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void install(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
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
      if (str.equals("IDL:activation/ServerHeldDown:1.0"))
        throw ServerHeldDownHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerAlreadyInstalled:1.0"))
        throw ServerAlreadyInstalledHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      install(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public String[] getORBNames(int paramInt) throws ServerNotRegistered {
    inputStream = null;
    try {
      OutputStream outputStream = _request("getORBNames", true);
      ServerIdHelper.write(outputStream, paramInt);
      inputStream = _invoke(outputStream);
      String[] arrayOfString = ORBidListHelper.read(inputStream);
      return arrayOfString;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return getORBNames(paramInt);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public void uninstall(int paramInt) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown {
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
      if (str.equals("IDL:activation/ServerHeldDown:1.0"))
        throw ServerHeldDownHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerAlreadyUninstalled:1.0"))
        throw ServerAlreadyUninstalledHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      uninstall(paramInt);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_ActivatorStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */