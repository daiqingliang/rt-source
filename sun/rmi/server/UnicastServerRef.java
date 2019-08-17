package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectStreamClass;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.UnmarshalException;
import java.rmi.server.ExportException;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.ServerRef;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonNotFoundException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import sun.misc.ObjectInputFilter;
import sun.rmi.runtime.Log;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.StreamRemoteCall;
import sun.rmi.transport.Target;
import sun.rmi.transport.tcp.TCPTransport;
import sun.security.action.GetBooleanAction;

public class UnicastServerRef extends UnicastRef implements ServerRef, Dispatcher {
  public static final boolean logCalls = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.rmi.server.logCalls"))).booleanValue();
  
  public static final Log callLog = Log.getLog("sun.rmi.server.call", "RMI", logCalls);
  
  private static final long serialVersionUID = -7384275867073752268L;
  
  private static final boolean wantExceptionLog = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.exceptionTrace"))).booleanValue();
  
  private boolean forceStubUse = false;
  
  private static final boolean suppressStackTraces = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.suppressStackTraces"))).booleanValue();
  
  private Skeleton skel;
  
  private final ObjectInputFilter filter = null;
  
  private Map<Long, Method> hashToMethod_Map = null;
  
  private static final WeakClassHashMap<Map<Long, Method>> hashToMethod_Maps = new HashToMethod_Maps();
  
  private static final Map<Class<?>, ?> withoutSkeletons = Collections.synchronizedMap(new WeakHashMap());
  
  private final AtomicInteger methodCallIDCount = new AtomicInteger(0);
  
  public UnicastServerRef() {}
  
  public UnicastServerRef(LiveRef paramLiveRef) { super(paramLiveRef); }
  
  public UnicastServerRef(LiveRef paramLiveRef, ObjectInputFilter paramObjectInputFilter) { super(paramLiveRef); }
  
  public UnicastServerRef(int paramInt) { super(new LiveRef(paramInt)); }
  
  public UnicastServerRef(boolean paramBoolean) {
    this(0);
    this.forceStubUse = paramBoolean;
  }
  
  public RemoteStub exportObject(Remote paramRemote, Object paramObject) throws RemoteException {
    this.forceStubUse = true;
    return (RemoteStub)exportObject(paramRemote, paramObject, false);
  }
  
  public Remote exportObject(Remote paramRemote, Object paramObject, boolean paramBoolean) throws RemoteException {
    Remote remote;
    Class clazz = paramRemote.getClass();
    try {
      remote = Util.createProxy(clazz, getClientRef(), this.forceStubUse);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ExportException("remote object implements illegal remote interface", illegalArgumentException);
    } 
    if (remote instanceof RemoteStub)
      setSkeleton(paramRemote); 
    Target target = new Target(paramRemote, this, remote, this.ref.getObjID(), paramBoolean);
    this.ref.exportObject(target);
    this.hashToMethod_Map = (Map)hashToMethod_Maps.get(clazz);
    return remote;
  }
  
  public String getClientHost() throws ServerNotActiveException { return TCPTransport.getClientHost(); }
  
  public void setSkeleton(Remote paramRemote) throws RemoteException {
    if (!withoutSkeletons.containsKey(paramRemote.getClass()))
      try {
        this.skel = Util.createSkeleton(paramRemote);
      } catch (SkeletonNotFoundException skeletonNotFoundException) {
        withoutSkeletons.put(paramRemote.getClass(), null);
      }  
  }
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall) throws IOException {
    try {
      Object object;
      ObjectInput objectInput;
      long l;
      int i;
      try {
        objectInput = paramRemoteCall.getInputStream();
        i = objectInput.readInt();
      } catch (Exception exception) {
        throw new UnmarshalException("error unmarshalling call header", exception);
      } 
      if (i >= 0) {
        if (this.skel != null) {
          oldDispatch(paramRemote, paramRemoteCall, i);
          return;
        } 
        throw new UnmarshalException("skeleton class not found but required for client version");
      } 
      try {
        l = objectInput.readLong();
      } catch (Exception exception) {
        throw new UnmarshalException("error unmarshalling call header", exception);
      } 
      MarshalInputStream marshalInputStream = (MarshalInputStream)objectInput;
      marshalInputStream.skipDefaultResolveClass();
      Method method = (Method)this.hashToMethod_Map.get(Long.valueOf(l));
      if (method == null)
        throw new UnmarshalException("unrecognized method hash: method not supported by remote object"); 
      logCall(paramRemote, method);
      Object[] arrayOfObject = null;
      try {
        unmarshalCustomCallData(objectInput);
        arrayOfObject = unmarshalParameters(paramRemote, method, marshalInputStream);
      } catch (AccessException null) {
        ((StreamRemoteCall)paramRemoteCall).discardPendingRefs();
        throw object;
      } catch (IOException|ClassNotFoundException null) {
        ((StreamRemoteCall)paramRemoteCall).discardPendingRefs();
        throw new UnmarshalException("error unmarshalling arguments", object);
      } finally {
        paramRemoteCall.releaseInputStream();
      } 
      try {
        object = method.invoke(paramRemote, arrayOfObject);
      } catch (InvocationTargetException invocationTargetException) {
        throw invocationTargetException.getTargetException();
      } 
      try {
        ObjectOutput objectOutput = paramRemoteCall.getResultStream(true);
        Class clazz = method.getReturnType();
        if (clazz != void.class)
          marshalValue(clazz, object, objectOutput); 
      } catch (IOException iOException) {
        throw new MarshalException("error marshalling return", iOException);
      } 
    } catch (Throwable throwable1) {
      Throwable throwable2 = throwable1;
      logCallException(throwable1);
      ObjectOutput objectOutput = paramRemoteCall.getResultStream(false);
      if (throwable1 instanceof Error) {
        throwable1 = new ServerError("Error occurred in server thread", (Error)throwable1);
      } else if (throwable1 instanceof RemoteException) {
        throwable1 = new ServerException("RemoteException occurred in server thread", (Exception)throwable1);
      } 
      if (suppressStackTraces)
        clearStackTraces(throwable1); 
      objectOutput.writeObject(throwable1);
      if (throwable2 instanceof AccessException)
        throw new IOException("Connection is not reusable", throwable2); 
    } finally {
      paramRemoteCall.releaseInputStream();
      paramRemoteCall.releaseOutputStream();
    } 
  }
  
  protected void unmarshalCustomCallData(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    if (this.filter != null && paramObjectInput instanceof ObjectInputStream) {
      final ObjectInputStream ois = (ObjectInputStream)paramObjectInput;
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              ObjectInputFilter.Config.setObjectInputFilter(ois, UnicastServerRef.this.filter);
              return null;
            }
          });
    } 
  }
  
  private void oldDispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt) throws Exception {
    long l;
    ObjectInput objectInput = paramRemoteCall.getInputStream();
    try {
      Class clazz = Class.forName("sun.rmi.transport.DGCImpl_Skel");
      if (clazz.isAssignableFrom(this.skel.getClass()))
        ((MarshalInputStream)objectInput).useCodebaseOnly(); 
    } catch (ClassNotFoundException classNotFoundException) {}
    try {
      l = objectInput.readLong();
    } catch (Exception exception) {
      throw new UnmarshalException("error unmarshalling call header", exception);
    } 
    logCall(paramRemote, this.skel.getOperations()[paramInt]);
    unmarshalCustomCallData(objectInput);
    this.skel.dispatch(paramRemote, paramRemoteCall, paramInt, l);
  }
  
  public static void clearStackTraces(Throwable paramThrowable) {
    StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[0];
    while (paramThrowable != null) {
      paramThrowable.setStackTrace(arrayOfStackTraceElement);
      paramThrowable = paramThrowable.getCause();
    } 
  }
  
  private void logCall(Remote paramRemote, Object paramObject) {
    if (callLog.isLoggable(Log.VERBOSE)) {
      String str;
      try {
        str = getClientHost();
      } catch (ServerNotActiveException serverNotActiveException) {
        str = "(local)";
      } 
      callLog.log(Log.VERBOSE, "[" + str + ": " + paramRemote.getClass().getName() + this.ref.getObjID().toString() + ": " + paramObject + "]");
    } 
  }
  
  private void logCallException(Throwable paramThrowable) {
    if (callLog.isLoggable(Log.BRIEF)) {
      String str = "";
      try {
        str = "[" + getClientHost() + "] ";
      } catch (ServerNotActiveException serverNotActiveException) {}
      callLog.log(Log.BRIEF, str + "exception: ", paramThrowable);
    } 
    if (wantExceptionLog) {
      PrintStream printStream = System.err;
      synchronized (printStream) {
        printStream.println();
        printStream.println("Exception dispatching call to " + this.ref.getObjID() + " in thread \"" + Thread.currentThread().getName() + "\" at " + new Date() + ":");
        paramThrowable.printStackTrace(printStream);
      } 
    } 
  }
  
  public String getRefClass(ObjectOutput paramObjectOutput) { return "UnicastServerRef"; }
  
  protected RemoteRef getClientRef() { return new UnicastRef(this.ref); }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException {}
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    this.ref = null;
    this.skel = null;
  }
  
  private Object[] unmarshalParameters(Object paramObject, Method paramMethod, MarshalInputStream paramMarshalInputStream) throws IOException, ClassNotFoundException { return (paramObject instanceof DeserializationChecker) ? unmarshalParametersChecked((DeserializationChecker)paramObject, paramMethod, paramMarshalInputStream) : unmarshalParametersUnchecked(paramMethod, paramMarshalInputStream); }
  
  private Object[] unmarshalParametersUnchecked(Method paramMethod, ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Object[] arrayOfObject = new Object[arrayOfClass.length];
    for (byte b = 0; b < arrayOfClass.length; b++)
      arrayOfObject[b] = unmarshalValue(arrayOfClass[b], paramObjectInput); 
    return arrayOfObject;
  }
  
  private Object[] unmarshalParametersChecked(DeserializationChecker paramDeserializationChecker, Method paramMethod, MarshalInputStream paramMarshalInputStream) throws IOException, ClassNotFoundException {
    int i = this.methodCallIDCount.getAndIncrement();
    MyChecker myChecker = new MyChecker(paramDeserializationChecker, paramMethod, i);
    paramMarshalInputStream.setStreamChecker(myChecker);
    try {
      Class[] arrayOfClass = paramMethod.getParameterTypes();
      Object[] arrayOfObject = new Object[arrayOfClass.length];
      for (byte b = 0; b < arrayOfClass.length; b++) {
        myChecker.setIndex(b);
        arrayOfObject[b] = unmarshalValue(arrayOfClass[b], paramMarshalInputStream);
      } 
      myChecker.end(i);
      return arrayOfObject;
    } finally {
      paramMarshalInputStream.setStreamChecker(null);
    } 
  }
  
  private static class HashToMethod_Maps extends WeakClassHashMap<Map<Long, Method>> {
    protected Map<Long, Method> computeValue(Class<?> param1Class) {
      HashMap hashMap = new HashMap();
      for (Class<?> clazz = param1Class; clazz != null; clazz = clazz.getSuperclass()) {
        for (Class clazz1 : clazz.getInterfaces()) {
          if (Remote.class.isAssignableFrom(clazz1))
            for (Method method1 : clazz1.getMethods()) {
              final Method m = method1;
              AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                      m.setAccessible(true);
                      return null;
                    }
                  });
              hashMap.put(Long.valueOf(Util.computeMethodHash(method2)), method2);
            }  
        } 
      } 
      return hashMap;
    }
  }
  
  private static class MyChecker implements MarshalInputStream.StreamChecker {
    private final DeserializationChecker descriptorCheck;
    
    private final Method method;
    
    private final int callID;
    
    private int parameterIndex;
    
    MyChecker(DeserializationChecker param1DeserializationChecker, Method param1Method, int param1Int) {
      this.descriptorCheck = param1DeserializationChecker;
      this.method = param1Method;
      this.callID = param1Int;
    }
    
    public void validateDescriptor(ObjectStreamClass param1ObjectStreamClass) { this.descriptorCheck.check(this.method, param1ObjectStreamClass, this.parameterIndex, this.callID); }
    
    public void checkProxyInterfaceNames(String[] param1ArrayOfString) { this.descriptorCheck.checkProxyClass(this.method, param1ArrayOfString, this.parameterIndex, this.callID); }
    
    void setIndex(int param1Int) { this.parameterIndex = param1Int; }
    
    void end(int param1Int) { this.descriptorCheck.end(param1Int); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\UnicastServerRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */