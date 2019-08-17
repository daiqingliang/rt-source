package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ServerManagerImplBase extends ObjectImpl implements ServerManager, InvokeHandler {
  private static Hashtable _methods = new Hashtable();
  
  private static String[] __ids;
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) {
    OutputStream outputStream = null;
    Integer integer = (Integer)_methods.get(paramString);
    if (integer == null)
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE); 
    switch (integer.intValue()) {
      case 0:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          Server server = ServerHelper.read(paramInputStream);
          active(i, server);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } 
        return outputStream;
      case 1:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          String str = ORBidHelper.read(paramInputStream);
          EndPointInfo[] arrayOfEndPointInfo = EndpointInfoListHelper.read(paramInputStream);
          registerEndpoints(i, str, arrayOfEndPointInfo);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } catch (NoSuchEndPoint noSuchEndPoint) {
          outputStream = paramResponseHandler.createExceptionReply();
          NoSuchEndPointHelper.write(outputStream, noSuchEndPoint);
        } catch (ORBAlreadyRegistered oRBAlreadyRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ORBAlreadyRegisteredHelper.write(outputStream, oRBAlreadyRegistered);
        } 
        return outputStream;
      case 2:
        arrayOfInt = null;
        arrayOfInt = getActiveServers();
        outputStream = paramResponseHandler.createReply();
        ServerIdsHelper.write(outputStream, arrayOfInt);
        return outputStream;
      case 3:
        try {
          arrayOfInt = ServerIdHelper.read(paramInputStream);
          activate(arrayOfInt);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerAlreadyActive arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerAlreadyActiveHelper.write(outputStream, arrayOfInt);
        } catch (ServerNotRegistered arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfInt);
        } catch (ServerHeldDown arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerHeldDownHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 4:
        try {
          arrayOfInt = ServerIdHelper.read(paramInputStream);
          shutdown(arrayOfInt);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotActive arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotActiveHelper.write(outputStream, arrayOfInt);
        } catch (ServerNotRegistered arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 5:
        try {
          arrayOfInt = ServerIdHelper.read(paramInputStream);
          install(arrayOfInt);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotRegistered arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfInt);
        } catch (ServerHeldDown arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerHeldDownHelper.write(outputStream, arrayOfInt);
        } catch (ServerAlreadyInstalled arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerAlreadyInstalledHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 6:
        try {
          arrayOfInt = ServerIdHelper.read(paramInputStream);
          String[] arrayOfString = null;
          arrayOfString = getORBNames(arrayOfInt);
          outputStream = paramResponseHandler.createReply();
          ORBidListHelper.write(outputStream, arrayOfString);
        } catch (ServerNotRegistered arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 7:
        try {
          arrayOfInt = ServerIdHelper.read(paramInputStream);
          uninstall(arrayOfInt);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotRegistered arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfInt);
        } catch (ServerHeldDown arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerHeldDownHelper.write(outputStream, arrayOfInt);
        } catch (ServerAlreadyUninstalled arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerAlreadyUninstalledHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 8:
        try {
          arrayOfInt = ServerIdHelper.read(paramInputStream);
          String str = paramInputStream.read_string();
          ServerLocation serverLocation = null;
          serverLocation = locateServer(arrayOfInt, str);
          outputStream = paramResponseHandler.createReply();
          ServerLocationHelper.write(outputStream, serverLocation);
        } catch (NoSuchEndPoint arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          NoSuchEndPointHelper.write(outputStream, arrayOfInt);
        } catch (ServerNotRegistered arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfInt);
        } catch (ServerHeldDown arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerHeldDownHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 9:
        try {
          arrayOfInt = ServerIdHelper.read(paramInputStream);
          String str = ORBidHelper.read(paramInputStream);
          ServerLocationPerORB serverLocationPerORB = null;
          serverLocationPerORB = locateServerForORB(arrayOfInt, str);
          outputStream = paramResponseHandler.createReply();
          ServerLocationPerORBHelper.write(outputStream, serverLocationPerORB);
        } catch (InvalidORBid arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidORBidHelper.write(outputStream, arrayOfInt);
        } catch (ServerNotRegistered arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfInt);
        } catch (ServerHeldDown arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerHeldDownHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 10:
        try {
          arrayOfInt = paramInputStream.read_string();
          int i = 0;
          i = getEndpoint(arrayOfInt);
          outputStream = paramResponseHandler.createReply();
          outputStream.write_long(i);
        } catch (NoSuchEndPoint arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          NoSuchEndPointHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
      case 11:
        try {
          ServerLocationPerORB serverLocationPerORB = ServerLocationPerORBHelper.read(paramInputStream);
          String str = paramInputStream.read_string();
          int i = 0;
          i = getServerPortForType(serverLocationPerORB, str);
          outputStream = paramResponseHandler.createReply();
          outputStream.write_long(i);
        } catch (NoSuchEndPoint arrayOfInt) {
          outputStream = paramResponseHandler.createExceptionReply();
          NoSuchEndPointHelper.write(outputStream, arrayOfInt);
        } 
        return outputStream;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  static  {
    _methods.put("active", new Integer(0));
    _methods.put("registerEndpoints", new Integer(1));
    _methods.put("getActiveServers", new Integer(2));
    _methods.put("activate", new Integer(3));
    _methods.put("shutdown", new Integer(4));
    _methods.put("install", new Integer(5));
    _methods.put("getORBNames", new Integer(6));
    _methods.put("uninstall", new Integer(7));
    _methods.put("locateServer", new Integer(8));
    _methods.put("locateServerForORB", new Integer(9));
    _methods.put("getEndpoint", new Integer(10));
    _methods.put("getServerPortForType", new Integer(11));
    __ids = new String[] { "IDL:activation/ServerManager:1.0", "IDL:activation/Activator:1.0", "IDL:activation/Locator:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_ServerManagerImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */