package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.io.ValueHandlerImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.IdentityHashtable;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.UnexpectedException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EmptyStackException;
import java.util.Enumeration;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.UtilDelegate;
import javax.rmi.CORBA.ValueHandler;
import javax.transaction.InvalidTransactionException;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionRolledbackException;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.SharedSecrets;

public class Util implements UtilDelegate {
  private static KeepAlive keepAlive = null;
  
  private static IdentityHashtable exportedServants = new IdentityHashtable();
  
  private static final ValueHandlerImpl valueHandlerSingleton = SharedSecrets.getJavaCorbaAccess().newValueHandlerImpl();
  
  private UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  
  private static Util instance = null;
  
  public Util() { setInstance(this); }
  
  private static void setInstance(Util paramUtil) {
    assert instance == null : "Instance already defined";
    instance = paramUtil;
  }
  
  public static Util getInstance() { return instance; }
  
  public static boolean isInstanceDefined() { return (instance != null); }
  
  public void unregisterTargetsForORB(ORB paramORB) {
    Enumeration enumeration = exportedServants.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      Remote remote = (Remote)((object instanceof Tie) ? ((Tie)object).getTarget() : object);
      try {
        if (paramORB == getTie(remote).orb())
          try {
            unexportObject(remote);
          } catch (NoSuchObjectException noSuchObjectException) {} 
      } catch (BAD_OPERATION bAD_OPERATION) {}
    } 
  }
  
  public RemoteException mapSystemException(SystemException paramSystemException) {
    String str3;
    if (paramSystemException instanceof UnknownException) {
      Throwable throwable = ((UnknownException)paramSystemException).originalEx;
      if (throwable instanceof Error)
        return new ServerError("Error occurred in server thread", (Error)throwable); 
      if (throwable instanceof RemoteException)
        return new ServerException("RemoteException occurred in server thread", (Exception)throwable); 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
    } 
    String str1 = paramSystemException.getClass().getName();
    String str2 = str1.substring(str1.lastIndexOf('.') + 1);
    switch (paramSystemException.completed.value()) {
      case 0:
        str3 = "Yes";
        break;
      case 1:
        str3 = "No";
        break;
      default:
        str3 = "Maybe";
        break;
    } 
    String str4 = "CORBA " + str2 + " " + paramSystemException.minor + " " + str3;
    if (paramSystemException instanceof org.omg.CORBA.COMM_FAILURE)
      return new MarshalException(str4, paramSystemException); 
    if (paramSystemException instanceof org.omg.CORBA.INV_OBJREF) {
      NoSuchObjectException noSuchObjectException = new NoSuchObjectException(str4);
      noSuchObjectException.detail = paramSystemException;
      return noSuchObjectException;
    } 
    if (paramSystemException instanceof org.omg.CORBA.NO_PERMISSION)
      return new AccessException(str4, paramSystemException); 
    if (paramSystemException instanceof org.omg.CORBA.MARSHAL)
      return new MarshalException(str4, paramSystemException); 
    if (paramSystemException instanceof org.omg.CORBA.OBJECT_NOT_EXIST) {
      NoSuchObjectException noSuchObjectException = new NoSuchObjectException(str4);
      noSuchObjectException.detail = paramSystemException;
      return noSuchObjectException;
    } 
    if (paramSystemException instanceof org.omg.CORBA.TRANSACTION_REQUIRED) {
      TransactionRequiredException transactionRequiredException = new TransactionRequiredException(str4);
      transactionRequiredException.detail = paramSystemException;
      return transactionRequiredException;
    } 
    if (paramSystemException instanceof org.omg.CORBA.TRANSACTION_ROLLEDBACK) {
      TransactionRolledbackException transactionRolledbackException = new TransactionRolledbackException(str4);
      transactionRolledbackException.detail = paramSystemException;
      return transactionRolledbackException;
    } 
    if (paramSystemException instanceof org.omg.CORBA.INVALID_TRANSACTION) {
      InvalidTransactionException invalidTransactionException = new InvalidTransactionException(str4);
      invalidTransactionException.detail = paramSystemException;
      return invalidTransactionException;
    } 
    if (paramSystemException instanceof org.omg.CORBA.BAD_PARAM) {
      NotSerializableException notSerializableException = paramSystemException;
      if (paramSystemException.minor == 1398079489 || paramSystemException.minor == 1330446342) {
        if (paramSystemException.getMessage() != null) {
          notSerializableException = new NotSerializableException(paramSystemException.getMessage());
        } else {
          notSerializableException = new NotSerializableException();
        } 
        notSerializableException.initCause(paramSystemException);
      } 
      return new MarshalException(str4, notSerializableException);
    } 
    if (paramSystemException instanceof org.omg.CORBA.ACTIVITY_REQUIRED) {
      try {
        Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityRequiredException");
        Class[] arrayOfClass = new Class[2];
        arrayOfClass[0] = String.class;
        arrayOfClass[1] = Throwable.class;
        Constructor constructor = clazz.getConstructor(arrayOfClass);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = str4;
        arrayOfObject[1] = paramSystemException;
        return (RemoteException)constructor.newInstance(arrayOfObject);
      } catch (Throwable throwable) {
        this.utilWrapper.classNotFound(throwable, "javax.activity.ActivityRequiredException");
      } 
    } else if (paramSystemException instanceof org.omg.CORBA.ACTIVITY_COMPLETED) {
      try {
        Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityCompletedException");
        Class[] arrayOfClass = new Class[2];
        arrayOfClass[0] = String.class;
        arrayOfClass[1] = Throwable.class;
        Constructor constructor = clazz.getConstructor(arrayOfClass);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = str4;
        arrayOfObject[1] = paramSystemException;
        return (RemoteException)constructor.newInstance(arrayOfObject);
      } catch (Throwable throwable) {
        this.utilWrapper.classNotFound(throwable, "javax.activity.ActivityCompletedException");
      } 
    } else if (paramSystemException instanceof org.omg.CORBA.INVALID_ACTIVITY) {
      try {
        Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.InvalidActivityException");
        Class[] arrayOfClass = new Class[2];
        arrayOfClass[0] = String.class;
        arrayOfClass[1] = Throwable.class;
        Constructor constructor = clazz.getConstructor(arrayOfClass);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = str4;
        arrayOfObject[1] = paramSystemException;
        return (RemoteException)constructor.newInstance(arrayOfObject);
      } catch (Throwable throwable) {
        this.utilWrapper.classNotFound(throwable, "javax.activity.InvalidActivityException");
      } 
    } 
    return new RemoteException(str4, paramSystemException);
  }
  
  public void writeAny(OutputStream paramOutputStream, Object paramObject) {
    ORB oRB = paramOutputStream.orb();
    Any any = oRB.create_any();
    Object object = Utility.autoConnect(paramObject, oRB, false);
    if (object instanceof Object) {
      any.insert_Object((Object)object);
    } else if (object == null) {
      any.insert_Value(null, createTypeCodeForNull(oRB));
    } else if (object instanceof Serializable) {
      TypeCode typeCode = createTypeCode((Serializable)object, any, oRB);
      if (typeCode == null) {
        any.insert_Value((Serializable)object);
      } else {
        any.insert_Value((Serializable)object, typeCode);
      } 
    } else if (object instanceof Remote) {
      ORBUtility.throwNotSerializableForCorba(object.getClass().getName());
    } else {
      ORBUtility.throwNotSerializableForCorba(object.getClass().getName());
    } 
    paramOutputStream.write_any(any);
  }
  
  private TypeCode createTypeCode(Serializable paramSerializable, Any paramAny, ORB paramORB) {
    if (paramAny instanceof AnyImpl && paramORB instanceof ORB) {
      AnyImpl anyImpl = (AnyImpl)paramAny;
      ORB oRB = (ORB)paramORB;
      return anyImpl.createTypeCodeForClass(paramSerializable.getClass(), oRB);
    } 
    return null;
  }
  
  private TypeCode createTypeCodeForNull(ORB paramORB) {
    if (paramORB instanceof ORB) {
      ORB oRB = (ORB)paramORB;
      if (!ORBVersionFactory.getFOREIGN().equals(oRB.getORBVersion()) && ORBVersionFactory.getNEWER().compareTo(oRB.getORBVersion()) > 0)
        return paramORB.get_primitive_tc(TCKind.tk_value); 
    } 
    String str = "IDL:omg.org/CORBA/AbstractBase:1.0";
    return paramORB.create_abstract_interface_tc(str, "");
  }
  
  public Object readAny(InputStream paramInputStream) {
    Any any = paramInputStream.read_any();
    return (any.type().kind().value() == 14) ? any.extract_Object() : any.extract_Value();
  }
  
  public void writeRemoteObject(OutputStream paramOutputStream, Object paramObject) {
    Object object = Utility.autoConnect(paramObject, paramOutputStream.orb(), false);
    paramOutputStream.write_Object((Object)object);
  }
  
  public void writeAbstractObject(OutputStream paramOutputStream, Object paramObject) {
    Object object = Utility.autoConnect(paramObject, paramOutputStream.orb(), false);
    ((OutputStream)paramOutputStream).write_abstract_interface(object);
  }
  
  public void registerTarget(Tie paramTie, Remote paramRemote) {
    synchronized (exportedServants) {
      if (lookupTie(paramRemote) == null) {
        exportedServants.put(paramRemote, paramTie);
        paramTie.setTarget(paramRemote);
        if (keepAlive == null) {
          keepAlive = (KeepAlive)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() { return new KeepAlive(); }
              });
          keepAlive.start();
        } 
      } 
    } 
  }
  
  public void unexportObject(Remote paramRemote) throws NoSuchObjectException {
    synchronized (exportedServants) {
      Tie tie = lookupTie(paramRemote);
      if (tie != null) {
        exportedServants.remove(paramRemote);
        Utility.purgeStubForTie(tie);
        Utility.purgeTieAndServant(tie);
        try {
          cleanUpTie(tie);
        } catch (BAD_OPERATION bAD_OPERATION) {
        
        } catch (OBJ_ADAPTER oBJ_ADAPTER) {}
        if (exportedServants.isEmpty()) {
          keepAlive.quit();
          keepAlive = null;
        } 
      } else {
        throw new NoSuchObjectException("Tie not found");
      } 
    } 
  }
  
  protected void cleanUpTie(Tie paramTie) throws NoSuchObjectException {
    paramTie.setTarget(null);
    paramTie.deactivate();
  }
  
  public Tie getTie(Remote paramRemote) {
    synchronized (exportedServants) {
      return lookupTie(paramRemote);
    } 
  }
  
  private static Tie lookupTie(Remote paramRemote) {
    Tie tie = (Tie)exportedServants.get(paramRemote);
    if (tie == null && paramRemote instanceof Tie && exportedServants.contains(paramRemote))
      tie = (Tie)paramRemote; 
    return tie;
  }
  
  public ValueHandler createValueHandler() { return valueHandlerSingleton; }
  
  public String getCodebase(Class paramClass) { return RMIClassLoader.getClassAnnotation(paramClass); }
  
  public Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader) throws ClassNotFoundException { return JDKBridge.loadClass(paramString1, paramString2, paramClassLoader); }
  
  public boolean isLocal(Stub paramStub) throws RemoteException {
    boolean bool = false;
    try {
      Delegate delegate = paramStub._get_delegate();
      if (delegate instanceof CorbaClientDelegate) {
        CorbaClientDelegate corbaClientDelegate = (CorbaClientDelegate)delegate;
        ContactInfoList contactInfoList = corbaClientDelegate.getContactInfoList();
        if (contactInfoList instanceof CorbaContactInfoList) {
          CorbaContactInfoList corbaContactInfoList = (CorbaContactInfoList)contactInfoList;
          LocalClientRequestDispatcher localClientRequestDispatcher = corbaContactInfoList.getLocalClientRequestDispatcher();
          bool = localClientRequestDispatcher.useLocalInvocation(null);
        } 
      } else {
        bool = delegate.is_local(paramStub);
      } 
    } catch (SystemException systemException) {
      throw Util.mapSystemException(systemException);
    } 
    return bool;
  }
  
  public RemoteException wrapException(Throwable paramThrowable) {
    if (paramThrowable instanceof SystemException)
      return mapSystemException((SystemException)paramThrowable); 
    if (paramThrowable instanceof Error)
      return new ServerError("Error occurred in server thread", (Error)paramThrowable); 
    if (paramThrowable instanceof RemoteException)
      return new ServerException("RemoteException occurred in server thread", (Exception)paramThrowable); 
    if (paramThrowable instanceof RuntimeException)
      throw (RuntimeException)paramThrowable; 
    return (paramThrowable instanceof Exception) ? new UnexpectedException(paramThrowable.toString(), (Exception)paramThrowable) : new UnexpectedException(paramThrowable.toString());
  }
  
  public Object[] copyObjects(Object[] paramArrayOfObject, ORB paramORB) throws RemoteException {
    if (paramArrayOfObject == null)
      throw new NullPointerException(); 
    Class clazz = paramArrayOfObject.getClass().getComponentType();
    if (Remote.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
      Remote[] arrayOfRemote = new Remote[paramArrayOfObject.length];
      System.arraycopy(paramArrayOfObject, 0, arrayOfRemote, 0, paramArrayOfObject.length);
      return (Object[])copyObject(arrayOfRemote, paramORB);
    } 
    return (Object[])copyObject(paramArrayOfObject, paramORB);
  }
  
  public Object copyObject(Object paramObject, ORB paramORB) throws RemoteException {
    if (paramORB instanceof ORB) {
      ORB oRB = (ORB)paramORB;
      try {
        return oRB.peekInvocationInfo().getCopierFactory().make().copy(paramObject);
      } catch (EmptyStackException emptyStackException) {
        CopierManager copierManager = oRB.getCopierManager();
        ObjectCopier objectCopier = copierManager.getDefaultObjectCopierFactory().make();
        return objectCopier.copy(paramObject);
      } catch (ReflectiveCopyException reflectiveCopyException) {
        RemoteException remoteException = new RemoteException();
        remoteException.initCause(reflectiveCopyException);
        throw remoteException;
      } 
    } 
    OutputStream outputStream = (OutputStream)paramORB.create_output_stream();
    outputStream.write_value((Serializable)paramObject);
    InputStream inputStream = (InputStream)outputStream.create_input_stream();
    return inputStream.read_value();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\javax\rmi\CORBA\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */