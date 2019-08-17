package com.sun.jmx.snmp;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SnmpPeer implements Serializable {
  private static final long serialVersionUID = -5554565062847175999L;
  
  public static final int defaultSnmpRequestPktSize = 2048;
  
  public static final int defaultSnmpResponsePktSize = 8192;
  
  private int maxVarBindLimit = 25;
  
  private int portNum = 161;
  
  private int maxTries = 3;
  
  private int timeout = 3000;
  
  private SnmpPduFactory pduFactory = new SnmpPduFactoryBER();
  
  private long _maxrtt;
  
  private long _minrtt;
  
  private long _avgrtt;
  
  private SnmpParams _snmpParameter = new SnmpParameters();
  
  private InetAddress _devAddr = null;
  
  private int maxSnmpPacketSize = 2048;
  
  InetAddress[] _devAddrList = null;
  
  int _addrIndex = 0;
  
  private boolean customPduFactory = false;
  
  public SnmpPeer(String paramString) throws UnknownHostException { this(paramString, 161); }
  
  public SnmpPeer(InetAddress paramInetAddress, int paramInt) {
    this._devAddr = paramInetAddress;
    this.portNum = paramInt;
  }
  
  public SnmpPeer(InetAddress paramInetAddress) { this._devAddr = paramInetAddress; }
  
  public SnmpPeer(String paramString, int paramInt) throws UnknownHostException {
    useIPAddress(paramString);
    this.portNum = paramInt;
  }
  
  public final void useIPAddress(String paramString) throws UnknownHostException { this._devAddr = InetAddress.getByName(paramString); }
  
  public final String ipAddressInUse() {
    byte[] arrayOfByte = this._devAddr.getAddress();
    return (arrayOfByte[0] & 0xFF) + "." + (arrayOfByte[1] & 0xFF) + "." + (arrayOfByte[2] & 0xFF) + "." + (arrayOfByte[3] & 0xFF);
  }
  
  public final void useAddressList(InetAddress[] paramArrayOfInetAddress) {
    this._devAddrList = (paramArrayOfInetAddress != null) ? (InetAddress[])paramArrayOfInetAddress.clone() : null;
    this._addrIndex = 0;
    useNextAddress();
  }
  
  public final void useNextAddress() {
    if (this._devAddrList == null)
      return; 
    if (this._addrIndex > this._devAddrList.length - 1)
      this._addrIndex = 0; 
    this._devAddr = this._devAddrList[this._addrIndex++];
  }
  
  public boolean allowSnmpSets() { return this._snmpParameter.allowSnmpSets(); }
  
  public final InetAddress[] getDestAddrList() { return (this._devAddrList == null) ? null : (InetAddress[])this._devAddrList.clone(); }
  
  public final InetAddress getDestAddr() { return this._devAddr; }
  
  public final int getDestPort() { return this.portNum; }
  
  public final void setDestPort(int paramInt) { this.portNum = paramInt; }
  
  public final int getTimeout() { return this.timeout; }
  
  public final void setTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.timeout = paramInt;
  }
  
  public final int getMaxTries() { return this.maxTries; }
  
  public final void setMaxTries(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.maxTries = paramInt;
  }
  
  public final String getDevName() { return getDestAddr().getHostName(); }
  
  public String toString() { return "Peer/Port : " + getDestAddr().getHostAddress() + "/" + getDestPort(); }
  
  public final int getVarBindLimit() { return this.maxVarBindLimit; }
  
  public final void setVarBindLimit(int paramInt) { this.maxVarBindLimit = paramInt; }
  
  public void setParams(SnmpParams paramSnmpParams) { this._snmpParameter = paramSnmpParams; }
  
  public SnmpParams getParams() { return this._snmpParameter; }
  
  public final int getMaxSnmpPktSize() { return this.maxSnmpPacketSize; }
  
  public final void setMaxSnmpPktSize(int paramInt) { this.maxSnmpPacketSize = paramInt; }
  
  boolean isCustomPduFactory() { return this.customPduFactory; }
  
  protected void finalize() {
    this._devAddr = null;
    this._devAddrList = null;
    this._snmpParameter = null;
  }
  
  public long getMinRtt() { return this._minrtt; }
  
  public long getMaxRtt() { return this._maxrtt; }
  
  public long getAvgRtt() { return this._avgrtt; }
  
  private void updateRttStats(long paramLong) {
    if (this._minrtt > paramLong) {
      this._minrtt = paramLong;
    } else if (this._maxrtt < paramLong) {
      this._maxrtt = paramLong;
    } else {
      this._avgrtt = paramLong;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */