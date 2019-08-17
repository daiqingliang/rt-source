package sun.rmi.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
import java.rmi.MarshalException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteCall;
import sun.rmi.runtime.Log;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.tcp.TCPEndpoint;

public class StreamRemoteCall implements RemoteCall {
  private ConnectionInputStream in = null;
  
  private ConnectionOutputStream out = null;
  
  private Connection conn;
  
  private boolean resultStarted = false;
  
  private Exception serverException = null;
  
  public StreamRemoteCall(Connection paramConnection) { this.conn = paramConnection; }
  
  public StreamRemoteCall(Connection paramConnection, ObjID paramObjID, int paramInt, long paramLong) throws RemoteException {
    try {
      this.conn = paramConnection;
      Transport.transportLog.log(Log.VERBOSE, "write remote call header...");
      this.conn.getOutputStream().write(80);
      getOutputStream();
      paramObjID.write(this.out);
      this.out.writeInt(paramInt);
      this.out.writeLong(paramLong);
    } catch (IOException iOException) {
      throw new MarshalException("Error marshaling call header", iOException);
    } 
  }
  
  public Connection getConnection() { return this.conn; }
  
  public ObjectOutput getOutputStream() throws IOException { return getOutputStream(false); }
  
  private ObjectOutput getOutputStream(boolean paramBoolean) throws IOException {
    if (this.out == null) {
      Transport.transportLog.log(Log.VERBOSE, "getting output stream");
      this.out = new ConnectionOutputStream(this.conn, paramBoolean);
    } 
    return this.out;
  }
  
  public void releaseOutputStream() throws IOException {
    try {
      if (this.out != null)
        try {
          this.out.flush();
        } finally {
          this.out.done();
        }  
      this.conn.releaseOutputStream();
    } finally {
      this.out = null;
    } 
  }
  
  public ObjectInput getInputStream() throws IOException {
    if (this.in == null) {
      Transport.transportLog.log(Log.VERBOSE, "getting input stream");
      this.in = new ConnectionInputStream(this.conn.getInputStream());
    } 
    return this.in;
  }
  
  public void releaseInputStream() throws IOException {
    try {
      if (this.in != null) {
        try {
          this.in.done();
        } catch (RuntimeException runtimeException) {}
        this.in.registerRefs();
        this.in.done(this.conn);
      } 
      this.conn.releaseInputStream();
    } finally {
      this.in = null;
    } 
  }
  
  public void discardPendingRefs() throws IOException { this.in.discardRefs(); }
  
  public ObjectOutput getResultStream(boolean paramBoolean) throws IOException {
    if (this.resultStarted)
      throw new StreamCorruptedException("result already in progress"); 
    this.resultStarted = true;
    DataOutputStream dataOutputStream = new DataOutputStream(this.conn.getOutputStream());
    dataOutputStream.writeByte(81);
    getOutputStream(true);
    if (paramBoolean) {
      this.out.writeByte(1);
    } else {
      this.out.writeByte(2);
    } 
    this.out.writeID();
    return this.out;
  }
  
  public void executeCall() throws IOException {
    Object object;
    byte b;
    dGCAckHandler = null;
    try {
      if (this.out != null)
        dGCAckHandler = this.out.getDGCAckHandler(); 
      releaseOutputStream();
      object = new DataInputStream(this.conn.getInputStream());
      byte b1 = object.readByte();
      if (b1 != 81) {
        if (Transport.transportLog.isLoggable(Log.BRIEF))
          Transport.transportLog.log(Log.BRIEF, "transport return code invalid: " + b1); 
        throw new UnmarshalException("Transport return code invalid");
      } 
      getInputStream();
      b = this.in.readByte();
      this.in.readID();
    } catch (UnmarshalException null) {
      throw object;
    } catch (IOException null) {
      throw new UnmarshalException("Error unmarshaling return header", object);
    } finally {
      if (dGCAckHandler != null)
        dGCAckHandler.release(); 
    } 
    switch (b) {
      case 1:
        return;
      case 2:
        try {
          object = this.in.readObject();
        } catch (Exception exception) {
          throw new UnmarshalException("Error unmarshaling return", exception);
        } 
        if (object instanceof Exception) {
          exceptionReceivedFromServer((Exception)object);
          break;
        } 
        throw new UnmarshalException("Return type not Exception");
    } 
    if (Transport.transportLog.isLoggable(Log.BRIEF))
      Transport.transportLog.log(Log.BRIEF, "return code invalid: " + b); 
    throw new UnmarshalException("Return code invalid");
  }
  
  protected void exceptionReceivedFromServer(Exception paramException) throws Exception {
    this.serverException = paramException;
    StackTraceElement[] arrayOfStackTraceElement1 = paramException.getStackTrace();
    StackTraceElement[] arrayOfStackTraceElement2 = (new Throwable()).getStackTrace();
    StackTraceElement[] arrayOfStackTraceElement3 = new StackTraceElement[arrayOfStackTraceElement1.length + arrayOfStackTraceElement2.length];
    System.arraycopy(arrayOfStackTraceElement1, 0, arrayOfStackTraceElement3, 0, arrayOfStackTraceElement1.length);
    System.arraycopy(arrayOfStackTraceElement2, 0, arrayOfStackTraceElement3, arrayOfStackTraceElement1.length, arrayOfStackTraceElement2.length);
    paramException.setStackTrace(arrayOfStackTraceElement3);
    if (UnicastRef.clientCallLog.isLoggable(Log.BRIEF)) {
      TCPEndpoint tCPEndpoint = (TCPEndpoint)this.conn.getChannel().getEndpoint();
      UnicastRef.clientCallLog.log(Log.BRIEF, "outbound call received exception: [" + tCPEndpoint.getHost() + ":" + tCPEndpoint.getPort() + "] exception: ", paramException);
    } 
    throw paramException;
  }
  
  public Exception getServerException() { return this.serverException; }
  
  public void done() throws IOException { releaseInputStream(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\StreamRemoteCall.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */