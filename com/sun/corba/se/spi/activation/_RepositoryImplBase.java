package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _RepositoryImplBase extends ObjectImpl implements Repository, InvokeHandler {
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
          ServerDef serverDef = ServerDefHelper.read(paramInputStream);
          int i = 0;
          i = registerServer(serverDef);
          outputStream = paramResponseHandler.createReply();
          outputStream.write_long(i);
        } catch (ServerAlreadyRegistered serverAlreadyRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerAlreadyRegisteredHelper.write(outputStream, serverAlreadyRegistered);
        } catch (BadServerDefinition badServerDefinition) {
          outputStream = paramResponseHandler.createExceptionReply();
          BadServerDefinitionHelper.write(outputStream, badServerDefinition);
        } 
        return outputStream;
      case 1:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          unregisterServer(i);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } 
        return outputStream;
      case 2:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          ServerDef serverDef = null;
          serverDef = getServer(i);
          outputStream = paramResponseHandler.createReply();
          ServerDefHelper.write(outputStream, serverDef);
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } 
        return outputStream;
      case 3:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          boolean bool = false;
          bool = isInstalled(i);
          outputStream = paramResponseHandler.createReply();
          outputStream.write_boolean(bool);
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } 
        return outputStream;
      case 4:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          install(i);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } catch (ServerAlreadyInstalled serverAlreadyInstalled) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerAlreadyInstalledHelper.write(outputStream, serverAlreadyInstalled);
        } 
        return outputStream;
      case 5:
        try {
          int i = ServerIdHelper.read(paramInputStream);
          uninstall(i);
          outputStream = paramResponseHandler.createReply();
        } catch (ServerNotRegistered serverNotRegistered) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, serverNotRegistered);
        } catch (ServerAlreadyUninstalled serverAlreadyUninstalled) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerAlreadyUninstalledHelper.write(outputStream, serverAlreadyUninstalled);
        } 
        return outputStream;
      case 6:
        arrayOfString = null;
        arrayOfString = listRegisteredServers();
        outputStream = paramResponseHandler.createReply();
        ServerIdsHelper.write(outputStream, arrayOfString);
        return outputStream;
      case 7:
        arrayOfString = null;
        arrayOfString = getApplicationNames();
        outputStream = paramResponseHandler.createReply();
        StringSeqHelper.write(outputStream, arrayOfString);
        return outputStream;
      case 8:
        try {
          arrayOfString = paramInputStream.read_string();
          int i = 0;
          i = getServerID(arrayOfString);
          outputStream = paramResponseHandler.createReply();
          outputStream.write_long(i);
        } catch (ServerNotRegistered arrayOfString) {
          outputStream = paramResponseHandler.createExceptionReply();
          ServerNotRegisteredHelper.write(outputStream, arrayOfString);
        } 
        return outputStream;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  static  {
    _methods.put("registerServer", new Integer(0));
    _methods.put("unregisterServer", new Integer(1));
    _methods.put("getServer", new Integer(2));
    _methods.put("isInstalled", new Integer(3));
    _methods.put("install", new Integer(4));
    _methods.put("uninstall", new Integer(5));
    _methods.put("listRegisteredServers", new Integer(6));
    _methods.put("getApplicationNames", new Integer(7));
    _methods.put("getServerID", new Integer(8));
    __ids = new String[] { "IDL:activation/Repository:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_RepositoryImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */