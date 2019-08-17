package com.sun.corba.se.spi.activation;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ServerImplBase extends ObjectImpl implements Server, InvokeHandler {
  private static Hashtable _methods = new Hashtable();
  
  private static String[] __ids;
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) {
    null = null;
    Integer integer = (Integer)_methods.get(paramString);
    if (integer == null)
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE); 
    switch (integer.intValue()) {
      case 0:
        shutdown();
        return paramResponseHandler.createReply();
      case 1:
        install();
        return paramResponseHandler.createReply();
      case 2:
        uninstall();
        return paramResponseHandler.createReply();
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  static  {
    _methods.put("shutdown", new Integer(0));
    _methods.put("install", new Integer(1));
    _methods.put("uninstall", new Integer(2));
    __ids = new String[] { "IDL:activation/Server:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_ServerImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */