package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
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

public class _LocatorStub extends ObjectImpl implements Locator {
  private static String[] __ids = { "IDL:activation/Locator:1.0" };
  
  public ServerLocation locateServer(int paramInt, String paramString) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown {
    inputStream = null;
    try {
      OutputStream outputStream = _request("locateServer", true);
      ServerIdHelper.write(outputStream, paramInt);
      outputStream.write_string(paramString);
      inputStream = _invoke(outputStream);
      ServerLocation serverLocation = ServerLocationHelper.read(inputStream);
      return serverLocation;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/NoSuchEndPoint:1.0"))
        throw NoSuchEndPointHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerHeldDown:1.0"))
        throw ServerHeldDownHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return locateServer(paramInt, paramString);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public ServerLocationPerORB locateServerForORB(int paramInt, String paramString) throws InvalidORBid, ServerNotRegistered, ServerHeldDown {
    inputStream = null;
    try {
      OutputStream outputStream = _request("locateServerForORB", true);
      ServerIdHelper.write(outputStream, paramInt);
      ORBidHelper.write(outputStream, paramString);
      inputStream = _invoke(outputStream);
      ServerLocationPerORB serverLocationPerORB = ServerLocationPerORBHelper.read(inputStream);
      return serverLocationPerORB;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/InvalidORBid:1.0"))
        throw InvalidORBidHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerNotRegistered:1.0"))
        throw ServerNotRegisteredHelper.read(inputStream); 
      if (str.equals("IDL:activation/ServerHeldDown:1.0"))
        throw ServerHeldDownHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return locateServerForORB(paramInt, paramString);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public int getEndpoint(String paramString) throws NoSuchEndPoint {
    inputStream = null;
    try {
      OutputStream outputStream = _request("getEndpoint", true);
      outputStream.write_string(paramString);
      inputStream = _invoke(outputStream);
      int i = TCPPortHelper.read(inputStream);
      return i;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/NoSuchEndPoint:1.0"))
        throw NoSuchEndPointHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return getEndpoint(paramString);
    } finally {
      _releaseReply(inputStream);
    } 
  }
  
  public int getServerPortForType(ServerLocationPerORB paramServerLocationPerORB, String paramString) throws NoSuchEndPoint {
    inputStream = null;
    try {
      OutputStream outputStream = _request("getServerPortForType", true);
      ServerLocationPerORBHelper.write(outputStream, paramServerLocationPerORB);
      outputStream.write_string(paramString);
      inputStream = _invoke(outputStream);
      int i = TCPPortHelper.read(inputStream);
      return i;
    } catch (ApplicationException applicationException) {
      inputStream = applicationException.getInputStream();
      String str = applicationException.getId();
      if (str.equals("IDL:activation/NoSuchEndPoint:1.0"))
        throw NoSuchEndPointHelper.read(inputStream); 
      throw new MARSHAL(str);
    } catch (RemarshalException remarshalException) {
      return getServerPortForType(paramServerLocationPerORB, paramString);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_LocatorStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */