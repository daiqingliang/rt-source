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

public abstract class _LocatorImplBase extends ObjectImpl implements Locator, InvokeHandler {
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
          String str = paramInputStream.read_string();
          ServerLocation serverLocation = null;
          serverLocation = locateServer(i, str);
          outputStream = paramResponseHandler.createReply();
          ServerLocationHelper.write(outputStream, serverLocation);
        } catch (NoSuchEndPoint noSuchEndPoint) {
          outputStream = paramResponseHandler.createExceptionReply();
          NoSuchEndPointHelper.write(outputStream, noSuchEndPoint);
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } catch (ServerHeldDown serverHeldDown) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerHeldDownHelper.write(outputStream, serverHeldDown);
        } 
        return outputStream;
      case 1:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          String str = ORBidHelper.read(paramInputStream);
          ServerLocationPerORB serverLocationPerORB = null;
          serverLocationPerORB = locateServerForORB(i, str);
          outputStream = paramResponseHandler.createReply();
          ServerLocationPerORBHelper.write(outputStream, serverLocationPerORB);
        } catch (InvalidORBid invalidORBid) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidORBidHelper.write(outputStream, invalidORBid);
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } catch (ServerHeldDown serverHeldDown) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerHeldDownHelper.write(outputStream, serverHeldDown);
        } 
        return outputStream;
      case 2:
        try {
          String str = paramInputStream.read_string();
          int i = 0;
          i = getEndpoint(str);
          outputStream = paramResponseHandler.createReply();
          outputStream.write_long(i);
        } catch (NoSuchEndPoint noSuchEndPoint) {
          outputStream = paramResponseHandler.createExceptionReply();
          NoSuchEndPointHelper.write(outputStream, noSuchEndPoint);
        } 
        return outputStream;
      case 3:
        try {
          ServerLocationPerORB serverLocationPerORB = ServerLocationPerORBHelper.read(paramInputStream);
          String str = paramInputStream.read_string();
          int i = 0;
          i = getServerPortForType(serverLocationPerORB, str);
          outputStream = paramResponseHandler.createReply();
          outputStream.write_long(i);
        } catch (NoSuchEndPoint noSuchEndPoint) {
          outputStream = paramResponseHandler.createExceptionReply();
          NoSuchEndPointHelper.write(outputStream, noSuchEndPoint);
        } 
        return outputStream;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  static  {
    _methods.put("locateServer", new Integer(0));
    _methods.put("locateServerForORB", new Integer(1));
    _methods.put("getEndpoint", new Integer(2));
    _methods.put("getServerPortForType", new Integer(3));
    __ids = new String[] { "IDL:activation/Locator:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_LocatorImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */