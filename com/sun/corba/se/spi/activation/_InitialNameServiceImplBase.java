package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _InitialNameServiceImplBase extends ObjectImpl implements InitialNameService, InvokeHandler {
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
          String str = paramInputStream.read_string();
          Object object = ObjectHelper.read(paramInputStream);
          boolean bool = paramInputStream.read_boolean();
          bind(str, object, bool);
          outputStream = paramResponseHandler.createReply();
        } catch (NameAlreadyBound nameAlreadyBound) {
          outputStream = paramResponseHandler.createExceptionReply();
          NameAlreadyBoundHelper.write(outputStream, nameAlreadyBound);
        } 
        return outputStream;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  static  {
    _methods.put("bind", new Integer(0));
    __ids = new String[] { "IDL:activation/InitialNameService:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\_InitialNameServiceImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */