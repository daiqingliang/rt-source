package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Operation;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.security.AccessController;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Connection;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.StreamRemoteCall;
import sun.security.action.GetBooleanAction;

public class UnicastRef implements RemoteRef {
  public static final Log clientRefLog;
  
  public static final Log clientCallLog = (clientRefLog = Log.getLog("sun.rmi.client.ref", "transport", Util.logLevel)).getLog("sun.rmi.client.call", "RMI", ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.client.logCalls"))).booleanValue());
  
  private static final long serialVersionUID = 8258372400816541186L;
  
  protected LiveRef ref;
  
  public UnicastRef() {}
  
  public UnicastRef(LiveRef paramLiveRef) { this.ref = paramLiveRef; }
  
  public LiveRef getLiveRef() { return this.ref; }
  
  public Object invoke(Remote paramRemote, Method paramMethod, Object[] paramArrayOfObject, long paramLong) throws Exception {
    if (clientRefLog.isLoggable(Log.VERBOSE))
      clientRefLog.log(Log.VERBOSE, "method: " + paramMethod); 
    if (clientCallLog.isLoggable(Log.VERBOSE))
      logClientCall(paramRemote, paramMethod); 
    connection = this.ref.getChannel().newConnection();
    streamRemoteCall = null;
    bool = true;
    bool1 = false;
    try {
      if (clientRefLog.isLoggable(Log.VERBOSE))
        clientRefLog.log(Log.VERBOSE, "opnum = " + paramLong); 
      streamRemoteCall = new StreamRemoteCall(connection, this.ref.getObjID(), -1, paramLong);
      try {
        ObjectOutput objectOutput = streamRemoteCall.getOutputStream();
        marshalCustomCallData(objectOutput);
        Class[] arrayOfClass = paramMethod.getParameterTypes();
        for (byte b = 0; b < arrayOfClass.length; b++)
          marshalValue(arrayOfClass[b], paramArrayOfObject[b], objectOutput); 
      } catch (IOException iOException) {
        clientRefLog.log(Log.BRIEF, "IOException marshalling arguments: ", iOException);
        throw new MarshalException("error marshalling arguments", iOException);
      } 
      streamRemoteCall.executeCall();
    } catch (RuntimeException runtimeException) {
      if (streamRemoteCall == null || ((StreamRemoteCall)streamRemoteCall).getServerException() != runtimeException)
        bool = false; 
      throw runtimeException;
    } catch (RemoteException remoteException) {
      bool = false;
      throw remoteException;
    } catch (Error error) {
      bool = false;
      throw error;
    } finally {
      if (!bool1) {
        if (clientRefLog.isLoggable(Log.BRIEF))
          clientRefLog.log(Log.BRIEF, "free connection (reuse = " + bool + ")"); 
        this.ref.getChannel().free(connection, bool);
      } 
    } 
  }
  
  protected void marshalCustomCallData(ObjectOutput paramObjectOutput) throws IOException {}
  
  protected static void marshalValue(Class<?> paramClass, Object paramObject, ObjectOutput paramObjectOutput) throws IOException {
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class) {
        paramObjectOutput.writeInt(((Integer)paramObject).intValue());
      } else if (paramClass == boolean.class) {
        paramObjectOutput.writeBoolean(((Boolean)paramObject).booleanValue());
      } else if (paramClass == byte.class) {
        paramObjectOutput.writeByte(((Byte)paramObject).byteValue());
      } else if (paramClass == char.class) {
        paramObjectOutput.writeChar(((Character)paramObject).charValue());
      } else if (paramClass == short.class) {
        paramObjectOutput.writeShort(((Short)paramObject).shortValue());
      } else if (paramClass == long.class) {
        paramObjectOutput.writeLong(((Long)paramObject).longValue());
      } else if (paramClass == float.class) {
        paramObjectOutput.writeFloat(((Float)paramObject).floatValue());
      } else if (paramClass == double.class) {
        paramObjectOutput.writeDouble(((Double)paramObject).doubleValue());
      } else {
        throw new Error("Unrecognized primitive type: " + paramClass);
      } 
    } else {
      paramObjectOutput.writeObject(paramObject);
    } 
  }
  
  protected static Object unmarshalValue(Class<?> paramClass, ObjectInput paramObjectInput) throws IOException, ClassNotFoundException {
    if (paramClass.isPrimitive()) {
      if (paramClass == int.class)
        return Integer.valueOf(paramObjectInput.readInt()); 
      if (paramClass == boolean.class)
        return Boolean.valueOf(paramObjectInput.readBoolean()); 
      if (paramClass == byte.class)
        return Byte.valueOf(paramObjectInput.readByte()); 
      if (paramClass == char.class)
        return Character.valueOf(paramObjectInput.readChar()); 
      if (paramClass == short.class)
        return Short.valueOf(paramObjectInput.readShort()); 
      if (paramClass == long.class)
        return Long.valueOf(paramObjectInput.readLong()); 
      if (paramClass == float.class)
        return Float.valueOf(paramObjectInput.readFloat()); 
      if (paramClass == double.class)
        return Double.valueOf(paramObjectInput.readDouble()); 
      throw new Error("Unrecognized primitive type: " + paramClass);
    } 
    return paramObjectInput.readObject();
  }
  
  public RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong) throws RemoteException {
    clientRefLog.log(Log.BRIEF, "get connection");
    Connection connection = this.ref.getChannel().newConnection();
    try {
      clientRefLog.log(Log.VERBOSE, "create call context");
      if (clientCallLog.isLoggable(Log.VERBOSE))
        logClientCall(paramRemoteObject, paramArrayOfOperation[paramInt]); 
      StreamRemoteCall streamRemoteCall = new StreamRemoteCall(connection, this.ref.getObjID(), paramInt, paramLong);
      try {
        marshalCustomCallData(streamRemoteCall.getOutputStream());
      } catch (IOException iOException) {
        throw new MarshalException("error marshaling custom call data");
      } 
      return streamRemoteCall;
    } catch (RemoteException remoteException) {
      this.ref.getChannel().free(connection, false);
      throw remoteException;
    } 
  }
  
  public void invoke(RemoteCall paramRemoteCall) throws Exception {
    try {
      clientRefLog.log(Log.VERBOSE, "execute call");
      paramRemoteCall.executeCall();
    } catch (RemoteException remoteException) {
      clientRefLog.log(Log.BRIEF, "exception: ", remoteException);
      free(paramRemoteCall, false);
      throw remoteException;
    } catch (Error error) {
      clientRefLog.log(Log.BRIEF, "error: ", error);
      free(paramRemoteCall, false);
      throw error;
    } catch (RuntimeException runtimeException) {
      clientRefLog.log(Log.BRIEF, "exception: ", runtimeException);
      free(paramRemoteCall, false);
      throw runtimeException;
    } catch (Exception exception) {
      clientRefLog.log(Log.BRIEF, "exception: ", exception);
      free(paramRemoteCall, true);
      throw exception;
    } 
  }
  
  private void free(RemoteCall paramRemoteCall, boolean paramBoolean) throws RemoteException {
    Connection connection = ((StreamRemoteCall)paramRemoteCall).getConnection();
    this.ref.getChannel().free(connection, paramBoolean);
  }
  
  public void done(RemoteCall paramRemoteCall) throws Exception {
    clientRefLog.log(Log.BRIEF, "free connection (reuse = true)");
    free(paramRemoteCall, true);
    try {
      paramRemoteCall.done();
    } catch (IOException iOException) {}
  }
  
  void logClientCall(Object paramObject1, Object paramObject2) { clientCallLog.log(Log.VERBOSE, "outbound call: " + this.ref + " : " + paramObject1.getClass().getName() + this.ref.getObjID().toString() + ": " + paramObject2); }
  
  public String getRefClass(ObjectOutput paramObjectOutput) { return "UnicastRef"; }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException { this.ref.write(paramObjectOutput, false); }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException { this.ref = LiveRef.read(paramObjectInput, false); }
  
  public String remoteToString() { return Util.getUnqualifiedName(getClass()) + " [liveRef: " + this.ref + "]"; }
  
  public int remoteHashCode() { return this.ref.hashCode(); }
  
  public boolean remoteEquals(RemoteRef paramRemoteRef) { return (paramRemoteRef instanceof UnicastRef) ? this.ref.remoteEquals(((UnicastRef)paramRemoteRef).ref) : 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\UnicastRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */