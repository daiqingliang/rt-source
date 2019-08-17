package com.sun.jndi.dns;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ProtocolFamily;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.Objects;
import java.util.Random;
import sun.net.PortConfig;

class DNSDatagramSocketFactory {
  static final int DEVIATION = 3;
  
  static final int THRESHOLD = 6;
  
  static final int BIT_DEVIATION = 2;
  
  static final int HISTORY = 32;
  
  static final int MAX_RANDOM_TRIES = 5;
  
  int lastport = 0;
  
  int suitablePortCount;
  
  int unsuitablePortCount;
  
  final ProtocolFamily family;
  
  final int thresholdCount;
  
  final int deviation;
  
  final Random random;
  
  final PortHistory history;
  
  DNSDatagramSocketFactory() { this(new Random()); }
  
  DNSDatagramSocketFactory(Random paramRandom) { this((Random)Objects.requireNonNull(paramRandom), null, 3, 6); }
  
  DNSDatagramSocketFactory(Random paramRandom, ProtocolFamily paramProtocolFamily, int paramInt1, int paramInt2) {
    this.random = (Random)Objects.requireNonNull(paramRandom);
    this.history = new PortHistory(32, paramRandom);
    this.family = paramProtocolFamily;
    this.deviation = Math.max(1, paramInt1);
    this.thresholdCount = Math.max(2, paramInt2);
  }
  
  public DatagramSocket open() throws SocketException {
    int i = this.lastport;
    boolean bool1 = (this.unsuitablePortCount > this.thresholdCount) ? 1 : 0;
    if (bool1) {
      DatagramSocket datagramSocket = openRandom();
      if (datagramSocket != null)
        return datagramSocket; 
      this.unsuitablePortCount = 0;
      this.suitablePortCount = 0;
      i = 0;
    } 
    DatagramSocket datagramSocket1 = openDefault();
    this.lastport = datagramSocket1.getLocalPort();
    if (i == 0) {
      this.history.offer(this.lastport);
      return datagramSocket1;
    } 
    bool1 = (this.suitablePortCount > this.thresholdCount) ? 1 : 0;
    boolean bool2 = (Integer.bitCount(i ^ this.lastport) > 2 && Math.abs(this.lastport - i) > this.deviation) ? 1 : 0;
    boolean bool = this.history.contains(this.lastport);
    boolean bool3 = (bool1 || (bool2 && !bool)) ? 1 : 0;
    if (bool3 && !bool)
      this.history.add(this.lastport); 
    if (bool3) {
      if (!bool1) {
        this.suitablePortCount++;
      } else if (!bool2 || bool) {
        this.unsuitablePortCount = 1;
        this.suitablePortCount = this.thresholdCount / 2;
      } 
      return datagramSocket1;
    } 
    assert !bool1;
    DatagramSocket datagramSocket2 = openRandom();
    if (datagramSocket2 == null)
      return datagramSocket1; 
    this.unsuitablePortCount++;
    datagramSocket1.close();
    return datagramSocket2;
  }
  
  private DatagramSocket openDefault() throws SocketException {
    if (this.family != null)
      try {
        DatagramChannel datagramChannel = DatagramChannel.open(this.family);
        try {
          DatagramSocket datagramSocket = datagramChannel.socket();
          datagramSocket.bind(null);
          return datagramSocket;
        } catch (Throwable throwable) {
          datagramChannel.close();
          throw throwable;
        } 
      } catch (SocketException socketException) {
        throw socketException;
      } catch (IOException iOException) {
        SocketException socketException = new SocketException(iOException.getMessage());
        socketException.initCause(iOException);
        throw socketException;
      }  
    return new DatagramSocket();
  }
  
  boolean isUsingNativePortRandomization() { return (this.unsuitablePortCount <= this.thresholdCount && this.suitablePortCount > this.thresholdCount); }
  
  boolean isUsingJavaPortRandomization() { return (this.unsuitablePortCount > this.thresholdCount); }
  
  boolean isUndecided() { return (!isUsingJavaPortRandomization() && !isUsingNativePortRandomization()); }
  
  private DatagramSocket openRandom() throws SocketException {
    byte b = 5;
    while (b-- > 0) {
      int i = EphemeralPortRange.LOWER + this.random.nextInt(EphemeralPortRange.RANGE);
      try {
        if (this.family != null) {
          DatagramChannel datagramChannel = DatagramChannel.open(this.family);
          try {
            DatagramSocket datagramSocket = datagramChannel.socket();
            datagramSocket.bind(new InetSocketAddress(i));
            return datagramSocket;
          } catch (Throwable throwable) {
            datagramChannel.close();
            throw throwable;
          } 
        } 
        return new DatagramSocket(i);
      } catch (IOException iOException) {}
    } 
    return null;
  }
  
  static final class EphemeralPortRange {
    static final int LOWER = PortConfig.getLower();
    
    static final int UPPER = PortConfig.getUpper();
    
    static final int RANGE = UPPER - LOWER + 1;
  }
  
  static final class PortHistory {
    final int capacity;
    
    final int[] ports;
    
    final Random random;
    
    int index;
    
    PortHistory(int param1Int, Random param1Random) {
      this.random = param1Random;
      this.capacity = param1Int;
      this.ports = new int[param1Int];
    }
    
    public boolean contains(int param1Int) {
      int i = 0;
      for (byte b = 0; b < this.capacity && (i = this.ports[b]) != 0 && i != param1Int; b++);
      return (i == param1Int);
    }
    
    public boolean add(int param1Int) {
      if (this.ports[this.index] != 0) {
        this.ports[this.random.nextInt(this.capacity)] = param1Int;
      } else {
        this.ports[this.index] = param1Int;
      } 
      if (++this.index == this.capacity)
        this.index = 0; 
      return true;
    }
    
    public boolean offer(int param1Int) { return contains(param1Int) ? false : add(param1Int); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\DNSDatagramSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */