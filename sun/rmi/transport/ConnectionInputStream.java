package sun.rmi.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.rmi.runtime.Log;
import sun.rmi.server.MarshalInputStream;

class ConnectionInputStream extends MarshalInputStream {
  private boolean dgcAckNeeded = false;
  
  private Map<Endpoint, List<LiveRef>> incomingRefTable = new HashMap(5);
  
  private UID ackID;
  
  ConnectionInputStream(InputStream paramInputStream) throws IOException { super(paramInputStream); }
  
  void readID() throws IOException { this.ackID = UID.read(this); }
  
  void saveRef(LiveRef paramLiveRef) {
    Endpoint endpoint = paramLiveRef.getEndpoint();
    List list = (List)this.incomingRefTable.get(endpoint);
    if (list == null) {
      list = new ArrayList();
      this.incomingRefTable.put(endpoint, list);
    } 
    list.add(paramLiveRef);
  }
  
  void discardRefs() throws IOException { this.incomingRefTable.clear(); }
  
  void registerRefs() throws IOException {
    if (!this.incomingRefTable.isEmpty())
      for (Map.Entry entry : this.incomingRefTable.entrySet())
        DGCClient.registerRefs((Endpoint)entry.getKey(), (List)entry.getValue());  
  }
  
  void setAckNeeded() throws IOException { this.dgcAckNeeded = true; }
  
  void done(Connection paramConnection) {
    if (this.dgcAckNeeded) {
      Connection connection = null;
      Channel channel = null;
      boolean bool = true;
      DGCImpl.dgcLog.log(Log.VERBOSE, "send ack");
      try {
        channel = paramConnection.getChannel();
        connection = channel.newConnection();
        DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
        dataOutputStream.writeByte(84);
        if (this.ackID == null)
          this.ackID = new UID(); 
        this.ackID.write(dataOutputStream);
        connection.releaseOutputStream();
        connection.getInputStream().available();
        connection.releaseInputStream();
      } catch (RemoteException remoteException) {
        bool = false;
      } catch (IOException iOException) {
        bool = false;
      } 
      try {
        if (connection != null)
          channel.free(connection, bool); 
      } catch (RemoteException remoteException) {}
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\ConnectionInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */