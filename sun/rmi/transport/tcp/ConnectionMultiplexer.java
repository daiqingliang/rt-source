package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.LogStream;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

final class ConnectionMultiplexer {
  static int logLevel = LogStream.parseLevel(getLogLevel());
  
  static final Log multiplexLog = Log.getLog("sun.rmi.transport.tcp.multiplex", "multiplex", logLevel);
  
  private static final int OPEN = 225;
  
  private static final int CLOSE = 226;
  
  private static final int CLOSEACK = 227;
  
  private static final int REQUEST = 228;
  
  private static final int TRANSMIT = 229;
  
  private TCPChannel channel;
  
  private InputStream in;
  
  private OutputStream out;
  
  private boolean orig;
  
  private DataInputStream dataIn;
  
  private DataOutputStream dataOut;
  
  private Hashtable<Integer, MultiplexConnectionInfo> connectionTable = new Hashtable(7);
  
  private int numConnections = 0;
  
  private static final int maxConnections = 256;
  
  private int lastID = 4097;
  
  private boolean alive = true;
  
  private static String getLogLevel() { return (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.transport.tcp.multiplex.logLevel")); }
  
  public ConnectionMultiplexer(TCPChannel paramTCPChannel, InputStream paramInputStream, OutputStream paramOutputStream, boolean paramBoolean) {
    this.channel = paramTCPChannel;
    this.in = paramInputStream;
    this.out = paramOutputStream;
    this.orig = paramBoolean;
    this.dataIn = new DataInputStream(paramInputStream);
    this.dataOut = new DataOutputStream(paramOutputStream);
  }
  
  public void run() throws IOException {
    try {
      int i;
      while (true) {
        TCPConnection tCPConnection;
        MultiplexConnectionInfo multiplexConnectionInfo;
        int k;
        int j;
        i = this.dataIn.readUnsignedByte();
        switch (i) {
          case 225:
            j = this.dataIn.readUnsignedShort();
            if (multiplexLog.isLoggable(Log.VERBOSE))
              multiplexLog.log(Log.VERBOSE, "operation  OPEN " + j); 
            multiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
            if (multiplexConnectionInfo != null)
              throw new IOException("OPEN: Connection ID already exists"); 
            multiplexConnectionInfo = new MultiplexConnectionInfo(j);
            multiplexConnectionInfo.in = new MultiplexInputStream(this, multiplexConnectionInfo, 2048);
            multiplexConnectionInfo.out = new MultiplexOutputStream(this, multiplexConnectionInfo, 2048);
            synchronized (this.connectionTable) {
              this.connectionTable.put(Integer.valueOf(j), multiplexConnectionInfo);
              this.numConnections++;
            } 
            tCPConnection = new TCPConnection(this.channel, multiplexConnectionInfo.in, multiplexConnectionInfo.out);
            this.channel.acceptMultiplexConnection(tCPConnection);
            continue;
          case 226:
            j = this.dataIn.readUnsignedShort();
            if (multiplexLog.isLoggable(Log.VERBOSE))
              multiplexLog.log(Log.VERBOSE, "operation  CLOSE " + j); 
            multiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
            if (multiplexConnectionInfo == null)
              throw new IOException("CLOSE: Invalid connection ID"); 
            multiplexConnectionInfo.in.disconnect();
            multiplexConnectionInfo.out.disconnect();
            if (!multiplexConnectionInfo.closed)
              sendCloseAck(multiplexConnectionInfo); 
            synchronized (this.connectionTable) {
              this.connectionTable.remove(Integer.valueOf(j));
              this.numConnections--;
              continue;
            } 
          case 227:
            j = this.dataIn.readUnsignedShort();
            if (multiplexLog.isLoggable(Log.VERBOSE))
              multiplexLog.log(Log.VERBOSE, "operation  CLOSEACK " + j); 
            multiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
            if (multiplexConnectionInfo == null)
              throw new IOException("CLOSEACK: Invalid connection ID"); 
            if (!multiplexConnectionInfo.closed)
              throw new IOException("CLOSEACK: Connection not closed"); 
            multiplexConnectionInfo.in.disconnect();
            multiplexConnectionInfo.out.disconnect();
            synchronized (this.connectionTable) {
              this.connectionTable.remove(Integer.valueOf(j));
              this.numConnections--;
              continue;
            } 
          case 228:
            j = this.dataIn.readUnsignedShort();
            multiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
            if (multiplexConnectionInfo == null)
              throw new IOException("REQUEST: Invalid connection ID"); 
            k = this.dataIn.readInt();
            if (multiplexLog.isLoggable(Log.VERBOSE))
              multiplexLog.log(Log.VERBOSE, "operation  REQUEST " + j + ": " + k); 
            multiplexConnectionInfo.out.request(k);
            continue;
          case 229:
            j = this.dataIn.readUnsignedShort();
            multiplexConnectionInfo = (MultiplexConnectionInfo)this.connectionTable.get(Integer.valueOf(j));
            if (multiplexConnectionInfo == null)
              throw new IOException("SEND: Invalid connection ID"); 
            k = this.dataIn.readInt();
            if (multiplexLog.isLoggable(Log.VERBOSE))
              multiplexLog.log(Log.VERBOSE, "operation  TRANSMIT " + j + ": " + k); 
            multiplexConnectionInfo.in.receive(k, this.dataIn);
            continue;
        } 
        break;
      } 
      throw new IOException("Invalid operation: " + Integer.toHexString(i));
    } finally {
      shutDown();
    } 
  }
  
  public TCPConnection openConnection() throws IOException {
    int i;
    do {
      this.lastID = ++this.lastID & 0x7FFF;
      i = this.lastID;
      if (!this.orig)
        continue; 
      i |= 0x8000;
    } while (this.connectionTable.get(Integer.valueOf(i)) != null);
    MultiplexConnectionInfo multiplexConnectionInfo = new MultiplexConnectionInfo(i);
    multiplexConnectionInfo.in = new MultiplexInputStream(this, multiplexConnectionInfo, 2048);
    multiplexConnectionInfo.out = new MultiplexOutputStream(this, multiplexConnectionInfo, 2048);
    synchronized (this.connectionTable) {
      if (!this.alive)
        throw new IOException("Multiplexer connection dead"); 
      if (this.numConnections >= 256)
        throw new IOException("Cannot exceed 256 simultaneous multiplexed connections"); 
      this.connectionTable.put(Integer.valueOf(i), multiplexConnectionInfo);
      this.numConnections++;
    } 
    synchronized (this.dataOut) {
      try {
        this.dataOut.writeByte(225);
        this.dataOut.writeShort(i);
        this.dataOut.flush();
      } catch (IOException iOException) {
        multiplexLog.log(Log.BRIEF, "exception: ", iOException);
        shutDown();
        throw iOException;
      } 
    } 
    return new TCPConnection(this.channel, multiplexConnectionInfo.in, multiplexConnectionInfo.out);
  }
  
  public void shutDown() throws IOException {
    synchronized (this.connectionTable) {
      if (!this.alive)
        return; 
      this.alive = false;
      Enumeration enumeration = this.connectionTable.elements();
      while (enumeration.hasMoreElements()) {
        MultiplexConnectionInfo multiplexConnectionInfo = (MultiplexConnectionInfo)enumeration.nextElement();
        multiplexConnectionInfo.in.disconnect();
        multiplexConnectionInfo.out.disconnect();
      } 
      this.connectionTable.clear();
      this.numConnections = 0;
    } 
    try {
      this.in.close();
    } catch (IOException iOException) {}
    try {
      this.out.close();
    } catch (IOException iOException) {}
  }
  
  void sendRequest(MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt) throws IOException {
    synchronized (this.dataOut) {
      if (this.alive && !paramMultiplexConnectionInfo.closed)
        try {
          this.dataOut.writeByte(228);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.writeInt(paramInt);
          this.dataOut.flush();
        } catch (IOException iOException) {
          multiplexLog.log(Log.BRIEF, "exception: ", iOException);
          shutDown();
          throw iOException;
        }  
    } 
  }
  
  void sendTransmit(MultiplexConnectionInfo paramMultiplexConnectionInfo, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.dataOut) {
      if (this.alive && !paramMultiplexConnectionInfo.closed)
        try {
          this.dataOut.writeByte(229);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.writeInt(paramInt2);
          this.dataOut.write(paramArrayOfByte, paramInt1, paramInt2);
          this.dataOut.flush();
        } catch (IOException iOException) {
          multiplexLog.log(Log.BRIEF, "exception: ", iOException);
          shutDown();
          throw iOException;
        }  
    } 
  }
  
  void sendClose(MultiplexConnectionInfo paramMultiplexConnectionInfo) throws IOException {
    paramMultiplexConnectionInfo.out.disconnect();
    synchronized (this.dataOut) {
      if (this.alive && !paramMultiplexConnectionInfo.closed)
        try {
          this.dataOut.writeByte(226);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.flush();
          paramMultiplexConnectionInfo.closed = true;
        } catch (IOException iOException) {
          multiplexLog.log(Log.BRIEF, "exception: ", iOException);
          shutDown();
          throw iOException;
        }  
    } 
  }
  
  void sendCloseAck(MultiplexConnectionInfo paramMultiplexConnectionInfo) throws IOException {
    synchronized (this.dataOut) {
      if (this.alive && !paramMultiplexConnectionInfo.closed)
        try {
          this.dataOut.writeByte(227);
          this.dataOut.writeShort(paramMultiplexConnectionInfo.id);
          this.dataOut.flush();
          paramMultiplexConnectionInfo.closed = true;
        } catch (IOException iOException) {
          multiplexLog.log(Log.BRIEF, "exception: ", iOException);
          shutDown();
          throw iOException;
        }  
    } 
  }
  
  protected void finalize() throws IOException {
    super.finalize();
    shutDown();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\ConnectionMultiplexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */