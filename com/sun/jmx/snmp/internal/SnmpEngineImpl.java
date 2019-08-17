package com.sun.jmx.snmp.internal;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpEngine;
import com.sun.jmx.snmp.SnmpEngineFactory;
import com.sun.jmx.snmp.SnmpEngineId;
import com.sun.jmx.snmp.SnmpUsmKeyHandler;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class SnmpEngineImpl implements SnmpEngine, Serializable {
  private static final long serialVersionUID = -2564301391365614725L;
  
  public static final int noAuthNoPriv = 0;
  
  public static final int authNoPriv = 1;
  
  public static final int authPriv = 3;
  
  public static final int reportableFlag = 4;
  
  public static final int authMask = 1;
  
  public static final int privMask = 2;
  
  public static final int authPrivMask = 3;
  
  private SnmpEngineId engineid = null;
  
  private SnmpEngineFactory factory = null;
  
  private long startTime = 0L;
  
  private int boot = 0;
  
  private boolean checkOid = false;
  
  private SnmpUsmKeyHandler usmKeyHandler = null;
  
  private SnmpLcd lcd = null;
  
  private SnmpSecuritySubSystem securitySub = null;
  
  private SnmpMsgProcessingSubSystem messageSub = null;
  
  private SnmpAccessControlSubSystem accessSub = null;
  
  public int getEngineTime() {
    long l = System.currentTimeMillis() / 1000L - this.startTime;
    if (l > 2147483647L) {
      this.startTime = System.currentTimeMillis() / 1000L;
      if (this.boot != Integer.MAX_VALUE)
        this.boot++; 
      storeNBBoots(this.boot);
    } 
    return (int)(System.currentTimeMillis() / 1000L - this.startTime);
  }
  
  public SnmpEngineId getEngineId() { return this.engineid; }
  
  public SnmpUsmKeyHandler getUsmKeyHandler() { return this.usmKeyHandler; }
  
  public SnmpLcd getLcd() { return this.lcd; }
  
  public int getEngineBoots() { return this.boot; }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd, SnmpEngineId paramSnmpEngineId) throws UnknownHostException {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (this.engineid == null)
      if (paramSnmpEngineId != null) {
        this.engineid = paramSnmpEngineId;
      } else {
        this.engineid = SnmpEngineId.createEngineId();
      }  
    paramSnmpLcd.storeEngineId(this.engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,SnmpEngineId)", "LOCAL ENGINE ID: " + this.engineid); 
  }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd, InetAddress paramInetAddress, int paramInt) throws UnknownHostException {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (this.engineid == null)
      this.engineid = SnmpEngineId.createEngineId(paramInetAddress, paramInt); 
    paramSnmpLcd.storeEngineId(this.engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,InetAddress,int)", "LOCAL ENGINE ID: " + this.engineid + " / LOCAL ENGINE NB BOOTS: " + this.boot + " / LOCAL ENGINE START TIME: " + getEngineTime()); 
  }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd, int paramInt) throws UnknownHostException {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (this.engineid == null)
      this.engineid = SnmpEngineId.createEngineId(paramInt); 
    paramSnmpLcd.storeEngineId(this.engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd,int)", "LOCAL ENGINE ID: " + this.engineid + " / LOCAL ENGINE NB BOOTS: " + this.boot + " / LOCAL ENGINE START TIME: " + getEngineTime()); 
  }
  
  public SnmpEngineImpl(SnmpEngineFactory paramSnmpEngineFactory, SnmpLcd paramSnmpLcd) throws UnknownHostException {
    init(paramSnmpLcd, paramSnmpEngineFactory);
    initEngineID();
    if (this.engineid == null)
      this.engineid = SnmpEngineId.createEngineId(); 
    paramSnmpLcd.storeEngineId(this.engineid);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpEngineImpl.class.getName(), "SnmpEngineImpl(SnmpEngineFactory,SnmpLcd)", "LOCAL ENGINE ID: " + this.engineid + " / LOCAL ENGINE NB BOOTS: " + this.boot + " / LOCAL ENGINE START TIME: " + getEngineTime()); 
  }
  
  public void activateCheckOid() { this.checkOid = true; }
  
  public void deactivateCheckOid() { this.checkOid = false; }
  
  public boolean isCheckOidActivated() { return this.checkOid; }
  
  private void storeNBBoots(int paramInt) {
    if (paramInt < 0 || paramInt == Integer.MAX_VALUE) {
      paramInt = Integer.MAX_VALUE;
      this.lcd.storeEngineBoots(paramInt);
    } else {
      this.lcd.storeEngineBoots(paramInt + 1);
    } 
  }
  
  private void init(SnmpLcd paramSnmpLcd, SnmpEngineFactory paramSnmpEngineFactory) {
    this.factory = paramSnmpEngineFactory;
    this.lcd = paramSnmpLcd;
    this.boot = paramSnmpLcd.getEngineBoots();
    if (this.boot == -1 || this.boot == 0)
      this.boot = 1; 
    storeNBBoots(this.boot);
    this.startTime = System.currentTimeMillis() / 1000L;
  }
  
  void setUsmKeyHandler(SnmpUsmKeyHandler paramSnmpUsmKeyHandler) { this.usmKeyHandler = paramSnmpUsmKeyHandler; }
  
  private void initEngineID() {
    String str = this.lcd.getEngineId();
    if (str != null)
      this.engineid = SnmpEngineId.createEngineId(str); 
  }
  
  public SnmpMsgProcessingSubSystem getMsgProcessingSubSystem() { return this.messageSub; }
  
  public void setMsgProcessingSubSystem(SnmpMsgProcessingSubSystem paramSnmpMsgProcessingSubSystem) { this.messageSub = paramSnmpMsgProcessingSubSystem; }
  
  public SnmpSecuritySubSystem getSecuritySubSystem() { return this.securitySub; }
  
  public void setSecuritySubSystem(SnmpSecuritySubSystem paramSnmpSecuritySubSystem) { this.securitySub = paramSnmpSecuritySubSystem; }
  
  public void setAccessControlSubSystem(SnmpAccessControlSubSystem paramSnmpAccessControlSubSystem) { this.accessSub = paramSnmpAccessControlSubSystem; }
  
  public SnmpAccessControlSubSystem getAccessControlSubSystem() { return this.accessSub; }
  
  public static void checkSecurityLevel(byte paramByte) throws SnmpBadSecurityLevelException {
    byte b = paramByte & 0x3;
    if ((b & 0x2) != 0 && (b & true) == 0)
      throw new SnmpBadSecurityLevelException("Security level: noAuthPriv!!!"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\internal\SnmpEngineImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */