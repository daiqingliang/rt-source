package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPeer;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibHandler;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

public interface SnmpAdaptorServerMBean extends CommunicatorServerMBean {
  InetAddressAcl getInetAddressAcl();
  
  Integer getTrapPort();
  
  void setTrapPort(Integer paramInteger);
  
  int getInformPort();
  
  void setInformPort(int paramInt);
  
  int getServedClientCount();
  
  int getActiveClientCount();
  
  int getMaxActiveClientCount();
  
  void setMaxActiveClientCount(int paramInt);
  
  String getProtocol();
  
  Integer getBufferSize();
  
  void setBufferSize(Integer paramInteger);
  
  int getMaxTries();
  
  void setMaxTries(int paramInt);
  
  int getTimeout();
  
  void setTimeout(int paramInt);
  
  SnmpPduFactory getPduFactory();
  
  void setPduFactory(SnmpPduFactory paramSnmpPduFactory);
  
  void setUserDataFactory(SnmpUserDataFactory paramSnmpUserDataFactory);
  
  SnmpUserDataFactory getUserDataFactory();
  
  boolean getAuthTrapEnabled();
  
  void setAuthTrapEnabled(boolean paramBoolean);
  
  boolean getAuthRespEnabled();
  
  void setAuthRespEnabled(boolean paramBoolean);
  
  String getEnterpriseOid();
  
  void setEnterpriseOid(String paramString) throws IllegalArgumentException;
  
  String[] getMibs();
  
  Long getSnmpOutTraps();
  
  Long getSnmpOutGetResponses();
  
  Long getSnmpOutGenErrs();
  
  Long getSnmpOutBadValues();
  
  Long getSnmpOutNoSuchNames();
  
  Long getSnmpOutTooBigs();
  
  Long getSnmpInASNParseErrs();
  
  Long getSnmpInBadCommunityUses();
  
  Long getSnmpInBadCommunityNames();
  
  Long getSnmpInBadVersions();
  
  Long getSnmpOutPkts();
  
  Long getSnmpInPkts();
  
  Long getSnmpInGetRequests();
  
  Long getSnmpInGetNexts();
  
  Long getSnmpInSetRequests();
  
  Long getSnmpInTotalSetVars();
  
  Long getSnmpInTotalReqVars();
  
  Long getSnmpSilentDrops();
  
  Long getSnmpProxyDrops();
  
  SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent) throws IllegalArgumentException;
  
  SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid) throws IllegalArgumentException;
  
  boolean removeMib(SnmpMibAgent paramSnmpMibAgent);
  
  void snmpV1Trap(int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException;
  
  void snmpV1Trap(InetAddress paramInetAddress, String paramString, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException;
  
  void snmpV1Trap(SnmpPeer paramSnmpPeer, SnmpIpAddress paramSnmpIpAddress, SnmpOid paramSnmpOid, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException;
  
  void snmpV2Trap(SnmpPeer paramSnmpPeer, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException;
  
  void snmpV2Trap(SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException;
  
  void snmpV2Trap(InetAddress paramInetAddress, String paramString, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException;
  
  void snmpPduTrap(InetAddress paramInetAddress, SnmpPduPacket paramSnmpPduPacket) throws IOException, SnmpStatusException;
  
  void snmpPduTrap(SnmpPeer paramSnmpPeer, SnmpPduPacket paramSnmpPduPacket) throws IOException, SnmpStatusException;
  
  Vector<?> snmpInformRequest(SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IllegalStateException, IOException, SnmpStatusException;
  
  SnmpInformRequest snmpInformRequest(InetAddress paramInetAddress, String paramString, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IllegalStateException, IOException, SnmpStatusException;
  
  SnmpInformRequest snmpInformRequest(SnmpPeer paramSnmpPeer, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IllegalStateException, IOException, SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpAdaptorServerMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */