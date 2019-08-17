package org.omg.CosNaming;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class NamingContextPOA extends Servant implements NamingContextOperations, InvokeHandler {
  private static Hashtable _methods = new Hashtable();
  
  private static String[] __ids;
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) {
    BindingIteratorHolder bindingIteratorHolder;
    BindingListHolder bindingListHolder;
    int i;
    OutputStream outputStream = null;
    Integer integer = (Integer)_methods.get(paramString);
    if (integer == null)
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE); 
    switch (integer.intValue()) {
      case 0:
        try {
          NameComponent[] arrayOfNameComponent = NameHelper.read(paramInputStream);
          Object object = ObjectHelper.read(paramInputStream);
          bind(arrayOfNameComponent, object);
          outputStream = paramResponseHandler.createReply();
        } catch (NotFound notFound) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotFoundHelper.write(outputStream, notFound);
        } catch (CannotProceed cannotProceed) {
          outputStream = paramResponseHandler.createExceptionReply();
          CannotProceedHelper.write(outputStream, cannotProceed);
        } catch (InvalidName invalidName) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidNameHelper.write(outputStream, invalidName);
        } catch (AlreadyBound alreadyBound) {
          outputStream = paramResponseHandler.createExceptionReply();
          AlreadyBoundHelper.write(outputStream, alreadyBound);
        } 
        return outputStream;
      case 1:
        try {
          NameComponent[] arrayOfNameComponent = NameHelper.read(paramInputStream);
          NamingContext namingContext1 = NamingContextHelper.read(paramInputStream);
          bind_context(arrayOfNameComponent, namingContext1);
          outputStream = paramResponseHandler.createReply();
        } catch (NotFound notFound) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotFoundHelper.write(outputStream, notFound);
        } catch (CannotProceed cannotProceed) {
          outputStream = paramResponseHandler.createExceptionReply();
          CannotProceedHelper.write(outputStream, cannotProceed);
        } catch (InvalidName invalidName) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidNameHelper.write(outputStream, invalidName);
        } catch (AlreadyBound alreadyBound) {
          outputStream = paramResponseHandler.createExceptionReply();
          AlreadyBoundHelper.write(outputStream, alreadyBound);
        } 
        return outputStream;
      case 2:
        try {
          NameComponent[] arrayOfNameComponent = NameHelper.read(paramInputStream);
          Object object = ObjectHelper.read(paramInputStream);
          rebind(arrayOfNameComponent, object);
          outputStream = paramResponseHandler.createReply();
        } catch (NotFound notFound) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotFoundHelper.write(outputStream, notFound);
        } catch (CannotProceed cannotProceed) {
          outputStream = paramResponseHandler.createExceptionReply();
          CannotProceedHelper.write(outputStream, cannotProceed);
        } catch (InvalidName invalidName) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidNameHelper.write(outputStream, invalidName);
        } 
        return outputStream;
      case 3:
        try {
          NameComponent[] arrayOfNameComponent = NameHelper.read(paramInputStream);
          NamingContext namingContext1 = NamingContextHelper.read(paramInputStream);
          rebind_context(arrayOfNameComponent, namingContext1);
          outputStream = paramResponseHandler.createReply();
        } catch (NotFound notFound) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotFoundHelper.write(outputStream, notFound);
        } catch (CannotProceed cannotProceed) {
          outputStream = paramResponseHandler.createExceptionReply();
          CannotProceedHelper.write(outputStream, cannotProceed);
        } catch (InvalidName invalidName) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidNameHelper.write(outputStream, invalidName);
        } 
        return outputStream;
      case 4:
        try {
          NameComponent[] arrayOfNameComponent = NameHelper.read(paramInputStream);
          Object object = null;
          object = resolve(arrayOfNameComponent);
          outputStream = paramResponseHandler.createReply();
          ObjectHelper.write(outputStream, object);
        } catch (NotFound notFound) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotFoundHelper.write(outputStream, notFound);
        } catch (CannotProceed cannotProceed) {
          outputStream = paramResponseHandler.createExceptionReply();
          CannotProceedHelper.write(outputStream, cannotProceed);
        } catch (InvalidName invalidName) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidNameHelper.write(outputStream, invalidName);
        } 
        return outputStream;
      case 5:
        try {
          NameComponent[] arrayOfNameComponent = NameHelper.read(paramInputStream);
          unbind(arrayOfNameComponent);
          outputStream = paramResponseHandler.createReply();
        } catch (NotFound notFound) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotFoundHelper.write(outputStream, notFound);
        } catch (CannotProceed cannotProceed) {
          outputStream = paramResponseHandler.createExceptionReply();
          CannotProceedHelper.write(outputStream, cannotProceed);
        } catch (InvalidName invalidName) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidNameHelper.write(outputStream, invalidName);
        } 
        return outputStream;
      case 6:
        i = paramInputStream.read_ulong();
        bindingListHolder = new BindingListHolder();
        bindingIteratorHolder = new BindingIteratorHolder();
        list(i, bindingListHolder, bindingIteratorHolder);
        outputStream = paramResponseHandler.createReply();
        BindingListHelper.write(outputStream, bindingListHolder.value);
        BindingIteratorHelper.write(outputStream, bindingIteratorHolder.value);
        return outputStream;
      case 7:
        namingContext = null;
        namingContext = new_context();
        outputStream = paramResponseHandler.createReply();
        NamingContextHelper.write(outputStream, namingContext);
        return outputStream;
      case 8:
        try {
          NameComponent[] arrayOfNameComponent = NameHelper.read(paramInputStream);
          bindingListHolder = null;
          NamingContext namingContext1 = bind_new_context(arrayOfNameComponent);
          outputStream = paramResponseHandler.createReply();
          NamingContextHelper.write(outputStream, namingContext1);
        } catch (NotFound namingContext) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotFoundHelper.write(outputStream, namingContext);
        } catch (AlreadyBound namingContext) {
          outputStream = paramResponseHandler.createExceptionReply();
          AlreadyBoundHelper.write(outputStream, namingContext);
        } catch (CannotProceed namingContext) {
          outputStream = paramResponseHandler.createExceptionReply();
          CannotProceedHelper.write(outputStream, namingContext);
        } catch (InvalidName namingContext) {
          outputStream = paramResponseHandler.createExceptionReply();
          InvalidNameHelper.write(outputStream, namingContext);
        } 
        return outputStream;
      case 9:
        try {
          destroy();
          outputStream = paramResponseHandler.createReply();
        } catch (NotEmpty namingContext) {
          outputStream = paramResponseHandler.createExceptionReply();
          NotEmptyHelper.write(outputStream, namingContext);
        } 
        return outputStream;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte) { return (String[])__ids.clone(); }
  
  public NamingContext _this() { return NamingContextHelper.narrow(_this_object()); }
  
  public NamingContext _this(ORB paramORB) { return NamingContextHelper.narrow(_this_object(paramORB)); }
  
  static  {
    _methods.put("bind", new Integer(0));
    _methods.put("bind_context", new Integer(1));
    _methods.put("rebind", new Integer(2));
    _methods.put("rebind_context", new Integer(3));
    _methods.put("resolve", new Integer(4));
    _methods.put("unbind", new Integer(5));
    _methods.put("list", new Integer(6));
    _methods.put("new_context", new Integer(7));
    _methods.put("bind_new_context", new Integer(8));
    _methods.put("destroy", new Integer(9));
    __ids = new String[] { "IDL:omg.org/CosNaming/NamingContext:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPOA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */