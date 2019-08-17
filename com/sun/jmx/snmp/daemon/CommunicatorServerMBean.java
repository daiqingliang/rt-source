package com.sun.jmx.snmp.daemon;

public interface CommunicatorServerMBean {
  void start();
  
  void stop();
  
  boolean isActive();
  
  boolean waitState(int paramInt, long paramLong);
  
  int getState();
  
  String getStateString();
  
  String getHost();
  
  int getPort();
  
  void setPort(int paramInt) throws IllegalStateException;
  
  String getProtocol();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\CommunicatorServerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */