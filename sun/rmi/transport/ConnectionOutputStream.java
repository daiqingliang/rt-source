package sun.rmi.transport;

import java.io.IOException;
import java.rmi.server.UID;
import sun.rmi.server.MarshalOutputStream;

class ConnectionOutputStream extends MarshalOutputStream {
  private final Connection conn;
  
  private final boolean resultStream;
  
  private final UID ackID;
  
  private DGCAckHandler dgcAckHandler = null;
  
  ConnectionOutputStream(Connection paramConnection, boolean paramBoolean) throws IOException {
    super(paramConnection.getOutputStream());
    this.conn = paramConnection;
    this.resultStream = paramBoolean;
    this.ackID = paramBoolean ? new UID() : null;
  }
  
  void writeID() throws IOException {
    assert this.resultStream;
    this.ackID.write(this);
  }
  
  boolean isResultStream() { return this.resultStream; }
  
  void saveObject(Object paramObject) {
    if (this.dgcAckHandler == null)
      this.dgcAckHandler = new DGCAckHandler(this.ackID); 
    this.dgcAckHandler.add(paramObject);
  }
  
  DGCAckHandler getDGCAckHandler() { return this.dgcAckHandler; }
  
  void done() throws IOException {
    if (this.dgcAckHandler != null)
      this.dgcAckHandler.startTimer(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\ConnectionOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */