package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;

final class SnmpSocket implements Runnable {
  private DatagramSocket _socket = null;
  
  private SnmpResponseHandler _dgramHdlr = null;
  
  private Thread _sockThread = null;
  
  private byte[] _buffer = null;
  
  private boolean isClosing = false;
  
  int _socketPort = 0;
  
  int responseBufSize = 1024;
  
  public SnmpSocket(SnmpResponseHandler paramSnmpResponseHandler, InetAddress paramInetAddress, int paramInt) throws SocketException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "constructor", "Creating new SNMP datagram socket"); 
    this._socket = new DatagramSocket(0, paramInetAddress);
    this._socketPort = this._socket.getLocalPort();
    this.responseBufSize = paramInt;
    this._buffer = new byte[this.responseBufSize];
    this._dgramHdlr = paramSnmpResponseHandler;
    this._sockThread = new Thread(this, "SnmpSocket");
    this._sockThread.start();
  }
  
  public void sendPacket(byte[] paramArrayOfByte, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException {
    DatagramPacket datagramPacket = new DatagramPacket(paramArrayOfByte, paramInt1, paramInetAddress, paramInt2);
    sendPacket(datagramPacket);
  }
  
  public void sendPacket(DatagramPacket paramDatagramPacket) throws IOException {
    try {
      if (isValid()) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "sendPacket", "Sending DatagramPacket. Length = " + paramDatagramPacket.getLength() + " through socket = " + this._socket.toString()); 
        this._socket.send(paramDatagramPacket);
      } else {
        throw new IOException("Invalid state of SNMP datagram socket.");
      } 
    } catch (IOException iOException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "sendPacket", "I/O error while sending", iOException); 
      throw iOException;
    } 
  }
  
  public boolean isValid() { return (this._socket != null && this._sockThread != null && this._sockThread.isAlive()); }
  
  public void close() {
    this.isClosing = true;
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "close", "Closing and destroying the SNMP datagram socket -> " + toString()); 
    try {
      DatagramSocket datagramSocket = new DatagramSocket(0);
      byte[] arrayOfByte = new byte[1];
      DatagramPacket datagramPacket = new DatagramPacket(arrayOfByte, 1, InetAddress.getLocalHost(), this._socketPort);
      datagramSocket.send(datagramPacket);
      datagramSocket.close();
    } catch (Exception exception) {}
    if (this._socket != null) {
      this._socket.close();
      this._socket = null;
    } 
    if (this._sockThread != null && this._sockThread.isAlive()) {
      this._sockThread.interrupt();
      try {
        this._sockThread.join();
      } catch (InterruptedException interruptedException) {}
      this._sockThread = null;
    } 
  }
  
  public void run() {
    Thread.currentThread().setPriority(8);
    while (true) {
      try {
        DatagramPacket datagramPacket = new DatagramPacket(this._buffer, this._buffer.length);
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Blocking for receiving packet"); 
        this._socket.receive(datagramPacket);
        if (this.isClosing)
          break; 
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Received a packet"); 
        if (datagramPacket.getLength() <= 0)
          continue; 
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpSocket.class.getName(), "run", "[" + Thread.currentThread().toString() + "]:Received a packet from : " + datagramPacket.getAddress().toString() + ", Length = " + datagramPacket.getLength()); 
        handleDatagram(datagramPacket);
        if (this.isClosing)
          break; 
      } catch (IOException iOException) {
        if (this.isClosing)
          break; 
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "IOEXception while receiving datagram", iOException); 
      } catch (Exception exception) {
        if (this.isClosing)
          break; 
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "Exception in socket thread...", exception); 
      } catch (ThreadDeath threadDeath) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "Socket Thread DEAD..." + toString(), threadDeath); 
        close();
        throw threadDeath;
      } catch (Error error) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "run", "Got unexpected error", error); 
        handleJavaError(error);
      } 
    } 
  }
  
  protected void finalize() { close(); }
  
  private void handleJavaError(Throwable paramThrowable) {
    if (paramThrowable instanceof OutOfMemoryError) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "handleJavaError", "OutOfMemory error", paramThrowable); 
      Thread.yield();
      return;
    } 
    if (this._socket != null) {
      this._socket.close();
      this._socket = null;
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpSocket.class.getName(), "handleJavaError", "Global Internal error"); 
    Thread.yield();
  }
  
  private void handleDatagram(DatagramPacket paramDatagramPacket) throws IOException { this._dgramHdlr.processDatagram(paramDatagramPacket); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */