package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.LogStream;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import sun.rmi.runtime.Log;
import sun.rmi.server.Dispatcher;
import sun.rmi.server.UnicastServerRef;
import sun.security.action.GetPropertyAction;

public abstract class Transport {
  static final int logLevel = LogStream.parseLevel(getLogLevel());
  
  static final Log transportLog = Log.getLog("sun.rmi.transport.misc", "transport", logLevel);
  
  private static final ThreadLocal<Transport> currentTransport = new ThreadLocal();
  
  private static final ObjID dgcID = new ObjID(2);
  
  private static final AccessControlContext SETCCL_ACC;
  
  private static String getLogLevel() { return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.logLevel")); }
  
  public abstract Channel getChannel(Endpoint paramEndpoint);
  
  public abstract void free(Endpoint paramEndpoint);
  
  public void exportObject(Target paramTarget) throws RemoteException {
    paramTarget.setExportedTransport(this);
    ObjectTable.putTarget(paramTarget);
  }
  
  protected void targetUnexported() {}
  
  static Transport currentTransport() { return (Transport)currentTransport.get(); }
  
  protected abstract void checkAcceptPermission(AccessControlContext paramAccessControlContext);
  
  private static void setContextClassLoader(ClassLoader paramClassLoader) { AccessController.doPrivileged(() -> {
          Thread.currentThread().setContextClassLoader(paramClassLoader);
          return null;
        }SETCCL_ACC); }
  
  public boolean serviceCall(final RemoteCall call) {
    try {
      ObjID objID;
      try {
        objID = ObjID.read(paramRemoteCall.getInputStream());
      } catch (IOException iOException) {
        throw new MarshalException("unable to read objID", iOException);
      } 
      Transport transport = objID.equals(dgcID) ? null : this;
      target = ObjectTable.getTarget(new ObjectEndpoint(objID, transport));
      final Remote impl;
      if (target == null || (remote = target.getImpl()) == null)
        throw new NoSuchObjectException("no such object in table"); 
      final Dispatcher disp = target.getDispatcher();
      target.incrementCallCount();
      try {
        transportLog.log(Log.VERBOSE, "call dispatcher");
        final AccessControlContext acc = target.getAccessControlContext();
        ClassLoader classLoader1 = target.getContextClassLoader();
        classLoader2 = Thread.currentThread().getContextClassLoader();
        try {
          setContextClassLoader(classLoader1);
          currentTransport.set(this);
          try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                  public Void run() throws IOException {
                    Transport.this.checkAcceptPermission(acc);
                    disp.dispatch(impl, call);
                    return null;
                  }
                }accessControlContext);
          } catch (PrivilegedActionException privilegedActionException) {
            throw (IOException)privilegedActionException.getException();
          } 
        } finally {
          setContextClassLoader(classLoader2);
          currentTransport.set(null);
        } 
      } catch (IOException iOException) {
        transportLog.log(Log.BRIEF, "exception thrown by dispatcher: ", iOException);
        return false;
      } finally {
        target.decrementCallCount();
      } 
    } catch (RemoteException remoteException) {
      if (UnicastServerRef.callLog.isLoggable(Log.BRIEF)) {
        String str1 = "";
        try {
          str1 = "[" + RemoteServer.getClientHost() + "] ";
        } catch (ServerNotActiveException serverNotActiveException) {}
        String str2 = str1 + "exception: ";
        UnicastServerRef.callLog.log(Log.BRIEF, str2, remoteException);
      } 
      try {
        ObjectOutput objectOutput = paramRemoteCall.getResultStream(false);
        UnicastServerRef.clearStackTraces(remoteException);
        objectOutput.writeObject(remoteException);
        paramRemoteCall.releaseOutputStream();
      } catch (IOException iOException) {
        transportLog.log(Log.BRIEF, "exception thrown marshalling exception: ", iOException);
        return false;
      } 
    } 
    return true;
  }
  
  static  {
    Permissions permissions = new Permissions();
    permissions.add(new RuntimePermission("setContextClassLoader"));
    ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, permissions) };
    SETCCL_ACC = new AccessControlContext(arrayOfProtectionDomain);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\Transport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */