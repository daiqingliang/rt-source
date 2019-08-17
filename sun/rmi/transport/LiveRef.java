package sun.rmi.transport;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.Arrays;
import sun.rmi.transport.tcp.TCPEndpoint;

public class LiveRef implements Cloneable {
  private final Endpoint ep;
  
  private final ObjID id;
  
  private Channel ch;
  
  private final boolean isLocal;
  
  public LiveRef(ObjID paramObjID, Endpoint paramEndpoint, boolean paramBoolean) {
    this.ep = paramEndpoint;
    this.id = paramObjID;
    this.isLocal = paramBoolean;
  }
  
  public LiveRef(int paramInt) { this(new ObjID(), paramInt); }
  
  public LiveRef(int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) { this(new ObjID(), paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory); }
  
  public LiveRef(ObjID paramObjID, int paramInt) { this(paramObjID, TCPEndpoint.getLocalEndpoint(paramInt), true); }
  
  public LiveRef(ObjID paramObjID, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory) { this(paramObjID, TCPEndpoint.getLocalEndpoint(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory), true); }
  
  public Object clone() {
    try {
      return (LiveRef)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
  
  public int getPort() { return ((TCPEndpoint)this.ep).getPort(); }
  
  public RMIClientSocketFactory getClientSocketFactory() { return ((TCPEndpoint)this.ep).getClientSocketFactory(); }
  
  public RMIServerSocketFactory getServerSocketFactory() { return ((TCPEndpoint)this.ep).getServerSocketFactory(); }
  
  public void exportObject(Target paramTarget) throws RemoteException { this.ep.exportObject(paramTarget); }
  
  public Channel getChannel() throws RemoteException {
    if (this.ch == null)
      this.ch = this.ep.getChannel(); 
    return this.ch;
  }
  
  public ObjID getObjID() { return this.id; }
  
  Endpoint getEndpoint() { return this.ep; }
  
  public String toString() {
    String str;
    if (this.isLocal) {
      str = "local";
    } else {
      str = "remote";
    } 
    return "[endpoint:" + this.ep + "(" + str + "),objID:" + this.id + "]";
  }
  
  public int hashCode() { return this.id.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof LiveRef) {
      LiveRef liveRef = (LiveRef)paramObject;
      return (this.ep.equals(liveRef.ep) && this.id.equals(liveRef.id) && this.isLocal == liveRef.isLocal);
    } 
    return false;
  }
  
  public boolean remoteEquals(Object paramObject) {
    if (paramObject != null && paramObject instanceof LiveRef) {
      LiveRef liveRef = (LiveRef)paramObject;
      TCPEndpoint tCPEndpoint1 = (TCPEndpoint)this.ep;
      TCPEndpoint tCPEndpoint2 = (TCPEndpoint)liveRef.ep;
      RMIClientSocketFactory rMIClientSocketFactory1 = tCPEndpoint1.getClientSocketFactory();
      RMIClientSocketFactory rMIClientSocketFactory2 = tCPEndpoint2.getClientSocketFactory();
      return (tCPEndpoint1.getPort() != tCPEndpoint2.getPort() || !tCPEndpoint1.getHost().equals(tCPEndpoint2.getHost())) ? false : ((((rMIClientSocketFactory1 == null) ? 1 : 0) ^ ((rMIClientSocketFactory2 == null) ? 1 : 0)) ? false : ((rMIClientSocketFactory1 != null && (rMIClientSocketFactory1.getClass() != rMIClientSocketFactory2.getClass() || !rMIClientSocketFactory1.equals(rMIClientSocketFactory2))) ? false : this.id.equals(liveRef.id)));
    } 
    return false;
  }
  
  public void write(ObjectOutput paramObjectOutput, boolean paramBoolean) throws IOException {
    boolean bool = false;
    if (paramObjectOutput instanceof ConnectionOutputStream) {
      ConnectionOutputStream connectionOutputStream = (ConnectionOutputStream)paramObjectOutput;
      bool = connectionOutputStream.isResultStream();
      if (this.isLocal) {
        ObjectEndpoint objectEndpoint = new ObjectEndpoint(this.id, this.ep.getInboundTransport());
        Target target = ObjectTable.getTarget(objectEndpoint);
        if (target != null) {
          Remote remote = target.getImpl();
          if (remote != null)
            connectionOutputStream.saveObject(remote); 
        } 
      } else {
        connectionOutputStream.saveObject(this);
      } 
    } 
    if (paramBoolean) {
      ((TCPEndpoint)this.ep).write(paramObjectOutput);
    } else {
      ((TCPEndpoint)this.ep).writeHostPortFormat(paramObjectOutput);
    } 
    this.id.write(paramObjectOutput);
    paramObjectOutput.writeBoolean(bool);
  }
  
  public static LiveRef read(ObjectInput paramObjectInput, boolean paramBoolean) throws IOException, ClassNotFoundException {
    TCPEndpoint tCPEndpoint;
    if (paramBoolean) {
      tCPEndpoint = TCPEndpoint.read(paramObjectInput);
    } else {
      tCPEndpoint = TCPEndpoint.readHostPortFormat(paramObjectInput);
    } 
    ObjID objID = ObjID.read(paramObjectInput);
    boolean bool = paramObjectInput.readBoolean();
    LiveRef liveRef = new LiveRef(objID, tCPEndpoint, false);
    if (paramObjectInput instanceof ConnectionInputStream) {
      ConnectionInputStream connectionInputStream = (ConnectionInputStream)paramObjectInput;
      connectionInputStream.saveRef(liveRef);
      if (bool)
        connectionInputStream.setAckNeeded(); 
    } else {
      DGCClient.registerRefs(tCPEndpoint, Arrays.asList(new LiveRef[] { liveRef }));
    } 
    return liveRef;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\LiveRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */