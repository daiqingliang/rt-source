package com.sun.jmx.snmp.daemon;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.IPAcl.SnmpAcl;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.SnmpDefinitions;
import com.sun.jmx.snmp.SnmpIpAddress;
import com.sun.jmx.snmp.SnmpMessage;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpParameters;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpPduFactoryBER;
import com.sun.jmx.snmp.SnmpPduPacket;
import com.sun.jmx.snmp.SnmpPduRequest;
import com.sun.jmx.snmp.SnmpPduTrap;
import com.sun.jmx.snmp.SnmpPeer;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTimeticks;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpErrorHandlerAgent;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import com.sun.jmx.snmp.agent.SnmpMibHandler;
import com.sun.jmx.snmp.agent.SnmpUserDataFactory;
import com.sun.jmx.snmp.tasks.ThreadService;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SnmpAdaptorServer extends CommunicatorServer implements SnmpAdaptorServerMBean, MBeanRegistration, SnmpDefinitions, SnmpMibHandler {
  private int trapPort = 162;
  
  private int informPort = 162;
  
  InetAddress address = null;
  
  private InetAddressAcl ipacl = null;
  
  private SnmpPduFactory pduFactory = null;
  
  private SnmpUserDataFactory userDataFactory = null;
  
  private boolean authRespEnabled = true;
  
  private boolean authTrapEnabled = true;
  
  private SnmpOid enterpriseOid = new SnmpOid("1.3.6.1.4.1.42");
  
  int bufferSize = 1024;
  
  private long startUpTime = 0L;
  
  private DatagramSocket socket = null;
  
  DatagramSocket trapSocket = null;
  
  private SnmpSession informSession = null;
  
  private DatagramPacket packet = null;
  
  Vector<SnmpMibAgent> mibs = new Vector();
  
  private SnmpMibTree root;
  
  private boolean useAcl = true;
  
  private int maxTries = 3;
  
  private int timeout = 3000;
  
  int snmpOutTraps = 0;
  
  private int snmpOutGetResponses = 0;
  
  private int snmpOutGenErrs = 0;
  
  private int snmpOutBadValues = 0;
  
  private int snmpOutNoSuchNames = 0;
  
  private int snmpOutTooBigs = 0;
  
  int snmpOutPkts = 0;
  
  private int snmpInASNParseErrs = 0;
  
  private int snmpInBadCommunityUses = 0;
  
  private int snmpInBadCommunityNames = 0;
  
  private int snmpInBadVersions = 0;
  
  private int snmpInGetRequests = 0;
  
  private int snmpInGetNexts = 0;
  
  private int snmpInSetRequests = 0;
  
  private int snmpInPkts = 0;
  
  private int snmpInTotalReqVars = 0;
  
  private int snmpInTotalSetVars = 0;
  
  private int snmpSilentDrops = 0;
  
  private static final String InterruptSysCallMsg = "Interrupted system call";
  
  static final SnmpOid sysUpTimeOid = new SnmpOid("1.3.6.1.2.1.1.3.0");
  
  static final SnmpOid snmpTrapOidOid = new SnmpOid("1.3.6.1.6.3.1.1.4.1.0");
  
  private ThreadService threadService;
  
  private static int threadNumber = 6;
  
  public SnmpAdaptorServer() { this(true, null, 161, null); }
  
  public SnmpAdaptorServer(int paramInt) { this(true, null, paramInt, null); }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl) { this(false, paramInetAddressAcl, 161, null); }
  
  public SnmpAdaptorServer(InetAddress paramInetAddress) { this(true, null, 161, paramInetAddress); }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl, int paramInt) { this(false, paramInetAddressAcl, paramInt, null); }
  
  public SnmpAdaptorServer(int paramInt, InetAddress paramInetAddress) { this(true, null, paramInt, paramInetAddress); }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl, InetAddress paramInetAddress) { this(false, paramInetAddressAcl, 161, paramInetAddress); }
  
  public SnmpAdaptorServer(InetAddressAcl paramInetAddressAcl, int paramInt, InetAddress paramInetAddress) { this(false, paramInetAddressAcl, paramInt, paramInetAddress); }
  
  public SnmpAdaptorServer(boolean paramBoolean, int paramInt, InetAddress paramInetAddress) { this(paramBoolean, null, paramInt, paramInetAddress); }
  
  private SnmpAdaptorServer(boolean paramBoolean, InetAddressAcl paramInetAddressAcl, int paramInt, InetAddress paramInetAddress) {
    super(4);
    if (paramInetAddressAcl == null && paramBoolean) {
      try {
        paramInetAddressAcl = new SnmpAcl("SNMP protocol adaptor IP ACL");
      } catch (UnknownHostException unknownHostException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "constructor", "UnknowHostException when creating ACL", unknownHostException); 
      } 
    } else {
      this.useAcl = (paramInetAddressAcl != null || paramBoolean);
    } 
    init(paramInetAddressAcl, paramInt, paramInetAddress);
  }
  
  public int getServedClientCount() { return super.getServedClientCount(); }
  
  public int getActiveClientCount() { return super.getActiveClientCount(); }
  
  public int getMaxActiveClientCount() { return super.getMaxActiveClientCount(); }
  
  public void setMaxActiveClientCount(int paramInt) { super.setMaxActiveClientCount(paramInt); }
  
  public InetAddressAcl getInetAddressAcl() { return this.ipacl; }
  
  public Integer getTrapPort() { return new Integer(this.trapPort); }
  
  public void setTrapPort(Integer paramInteger) { setTrapPort(paramInteger.intValue()); }
  
  public void setTrapPort(int paramInt) {
    int i = paramInt;
    if (i < 0)
      throw new IllegalArgumentException("Trap port cannot be a negative value"); 
    this.trapPort = i;
  }
  
  public int getInformPort() { return this.informPort; }
  
  public void setInformPort(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Inform request port cannot be a negative value"); 
    this.informPort = paramInt;
  }
  
  public String getProtocol() { return "snmp"; }
  
  public Integer getBufferSize() { return new Integer(this.bufferSize); }
  
  public void setBufferSize(Integer paramInteger) {
    if (this.state == 0 || this.state == 3)
      throw new IllegalStateException("Stop server before carrying out this operation"); 
    this.bufferSize = paramInteger.intValue();
  }
  
  public final int getMaxTries() { return this.maxTries; }
  
  public final void setMaxTries(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.maxTries = paramInt;
  }
  
  public final int getTimeout() { return this.timeout; }
  
  public final void setTimeout(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    this.timeout = paramInt;
  }
  
  public SnmpPduFactory getPduFactory() { return this.pduFactory; }
  
  public void setPduFactory(SnmpPduFactory paramSnmpPduFactory) {
    if (paramSnmpPduFactory == null) {
      this.pduFactory = new SnmpPduFactoryBER();
    } else {
      this.pduFactory = paramSnmpPduFactory;
    } 
  }
  
  public void setUserDataFactory(SnmpUserDataFactory paramSnmpUserDataFactory) { this.userDataFactory = paramSnmpUserDataFactory; }
  
  public SnmpUserDataFactory getUserDataFactory() { return this.userDataFactory; }
  
  public boolean getAuthTrapEnabled() { return this.authTrapEnabled; }
  
  public void setAuthTrapEnabled(boolean paramBoolean) { this.authTrapEnabled = paramBoolean; }
  
  public boolean getAuthRespEnabled() { return this.authRespEnabled; }
  
  public void setAuthRespEnabled(boolean paramBoolean) { this.authRespEnabled = paramBoolean; }
  
  public String getEnterpriseOid() { return this.enterpriseOid.toString(); }
  
  public void setEnterpriseOid(String paramString) throws IllegalArgumentException { this.enterpriseOid = new SnmpOid(paramString); }
  
  public String[] getMibs() {
    String[] arrayOfString = new String[this.mibs.size()];
    byte b = 0;
    Enumeration enumeration = this.mibs.elements();
    while (enumeration.hasMoreElements()) {
      SnmpMibAgent snmpMibAgent = (SnmpMibAgent)enumeration.nextElement();
      arrayOfString[b++] = snmpMibAgent.getMibName();
    } 
    return arrayOfString;
  }
  
  public Long getSnmpOutTraps() { return new Long(this.snmpOutTraps); }
  
  public Long getSnmpOutGetResponses() { return new Long(this.snmpOutGetResponses); }
  
  public Long getSnmpOutGenErrs() { return new Long(this.snmpOutGenErrs); }
  
  public Long getSnmpOutBadValues() { return new Long(this.snmpOutBadValues); }
  
  public Long getSnmpOutNoSuchNames() { return new Long(this.snmpOutNoSuchNames); }
  
  public Long getSnmpOutTooBigs() { return new Long(this.snmpOutTooBigs); }
  
  public Long getSnmpInASNParseErrs() { return new Long(this.snmpInASNParseErrs); }
  
  public Long getSnmpInBadCommunityUses() { return new Long(this.snmpInBadCommunityUses); }
  
  public Long getSnmpInBadCommunityNames() { return new Long(this.snmpInBadCommunityNames); }
  
  public Long getSnmpInBadVersions() { return new Long(this.snmpInBadVersions); }
  
  public Long getSnmpOutPkts() { return new Long(this.snmpOutPkts); }
  
  public Long getSnmpInPkts() { return new Long(this.snmpInPkts); }
  
  public Long getSnmpInGetRequests() { return new Long(this.snmpInGetRequests); }
  
  public Long getSnmpInGetNexts() { return new Long(this.snmpInGetNexts); }
  
  public Long getSnmpInSetRequests() { return new Long(this.snmpInSetRequests); }
  
  public Long getSnmpInTotalSetVars() { return new Long(this.snmpInTotalSetVars); }
  
  public Long getSnmpInTotalReqVars() { return new Long(this.snmpInTotalReqVars); }
  
  public Long getSnmpSilentDrops() { return new Long(this.snmpSilentDrops); }
  
  public Long getSnmpProxyDrops() { return new Long(0L); }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    if (paramObjectName == null)
      paramObjectName = new ObjectName(paramMBeanServer.getDefaultDomain() + ":" + "name=SnmpAdaptorServer"); 
    return super.preRegister(paramMBeanServer, paramObjectName);
  }
  
  public void postRegister(Boolean paramBoolean) { super.postRegister(paramBoolean); }
  
  public void preDeregister() { super.preDeregister(); }
  
  public void postDeregister() { super.postDeregister(); }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent) throws IllegalArgumentException {
    if (paramSnmpMibAgent == null)
      throw new IllegalArgumentException(); 
    if (!this.mibs.contains(paramSnmpMibAgent))
      this.mibs.addElement(paramSnmpMibAgent); 
    this.root.register(paramSnmpMibAgent);
    return this;
  }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid) throws IllegalArgumentException {
    if (paramSnmpMibAgent == null)
      throw new IllegalArgumentException(); 
    if (paramArrayOfSnmpOid == null)
      return addMib(paramSnmpMibAgent); 
    if (!this.mibs.contains(paramSnmpMibAgent))
      this.mibs.addElement(paramSnmpMibAgent); 
    for (byte b = 0; b < paramArrayOfSnmpOid.length; b++)
      this.root.register(paramSnmpMibAgent, paramArrayOfSnmpOid[b].longValue()); 
    return this;
  }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, String paramString) throws IllegalArgumentException { return addMib(paramSnmpMibAgent); }
  
  public SnmpMibHandler addMib(SnmpMibAgent paramSnmpMibAgent, String paramString, SnmpOid[] paramArrayOfSnmpOid) throws IllegalArgumentException { return addMib(paramSnmpMibAgent, paramArrayOfSnmpOid); }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent, String paramString) { return removeMib(paramSnmpMibAgent); }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent) {
    this.root.unregister(paramSnmpMibAgent);
    return this.mibs.removeElement(paramSnmpMibAgent);
  }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid) {
    this.root.unregister(paramSnmpMibAgent, paramArrayOfSnmpOid);
    return this.mibs.removeElement(paramSnmpMibAgent);
  }
  
  public boolean removeMib(SnmpMibAgent paramSnmpMibAgent, String paramString, SnmpOid[] paramArrayOfSnmpOid) { return removeMib(paramSnmpMibAgent, paramArrayOfSnmpOid); }
  
  protected void doBind() {
    try {
      synchronized (this) {
        this.socket = new DatagramSocket(this.port, this.address);
      } 
      this.dbgTag = makeDebugTag();
    } catch (SocketException socketException) {
      if (socketException.getMessage().equals("Interrupted system call"))
        throw new InterruptedException(socketException.toString()); 
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "doBind", "cannot bind on port " + this.port); 
      throw new CommunicationException(socketException);
    } 
  }
  
  public int getPort() {
    synchronized (this) {
      if (this.socket != null)
        return this.socket.getLocalPort(); 
    } 
    return super.getPort();
  }
  
  protected void doUnbind() {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doUnbind", "Finally close the socket"); 
    synchronized (this) {
      if (this.socket != null) {
        this.socket.close();
        this.socket = null;
      } 
    } 
    closeTrapSocketIfNeeded();
    closeInformSocketIfNeeded();
  }
  
  private void createSnmpRequestHandler(SnmpAdaptorServer paramSnmpAdaptorServer, int paramInt, DatagramSocket paramDatagramSocket, DatagramPacket paramDatagramPacket, SnmpMibTree paramSnmpMibTree, Vector<SnmpMibAgent> paramVector, InetAddressAcl paramInetAddressAcl, SnmpPduFactory paramSnmpPduFactory, SnmpUserDataFactory paramSnmpUserDataFactory, MBeanServer paramMBeanServer, ObjectName paramObjectName) {
    SnmpRequestHandler snmpRequestHandler = new SnmpRequestHandler(this, paramInt, paramDatagramSocket, paramDatagramPacket, paramSnmpMibTree, paramVector, paramInetAddressAcl, paramSnmpPduFactory, paramSnmpUserDataFactory, paramMBeanServer, paramObjectName);
    this.threadService.submitTask(snmpRequestHandler);
  }
  
  protected void doReceive() {
    try {
      this.packet = new DatagramPacket(new byte[this.bufferSize], this.bufferSize);
      this.socket.receive(this.packet);
      int i = getState();
      if (i != 0) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doReceive", "received a message but state not online, returning."); 
        return;
      } 
      createSnmpRequestHandler(this, this.servedClientCount, this.socket, this.packet, this.root, this.mibs, this.ipacl, this.pduFactory, this.userDataFactory, this.topMBS, this.objectName);
    } catch (SocketException socketException) {
      if (socketException.getMessage().equals("Interrupted system call"))
        throw new InterruptedException(socketException.toString()); 
      throw new CommunicationException(socketException);
    } catch (InterruptedIOException interruptedIOException) {
      throw new InterruptedException(interruptedIOException.toString());
    } catch (CommunicationException communicationException) {
      throw communicationException;
    } catch (Exception exception) {
      throw new CommunicationException(exception);
    } 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "doReceive", "received a message"); 
  }
  
  protected void doError(Exception paramException) throws CommunicationException {}
  
  protected void doProcess() {}
  
  protected int getBindTries() { return 1; }
  
  public void stop() {
    int i = getPort();
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Stopping: using port " + i); 
    if (this.state == 0 || this.state == 3) {
      super.stop();
      try {
        datagramSocket = new DatagramSocket(0);
        try {
          DatagramPacket datagramPacket;
          byte[] arrayOfByte = new byte[1];
          if (this.address != null) {
            datagramPacket = new DatagramPacket(arrayOfByte, 1, this.address, i);
          } else {
            datagramPacket = new DatagramPacket(arrayOfByte, 1, InetAddress.getLocalHost(), i);
          } 
          if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "stop", "Sending: using port " + i); 
          datagramSocket.send(datagramPacket);
        } finally {
          datagramSocket.close();
        } 
      } catch (Throwable throwable) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "stop", "Got unexpected Throwable", throwable); 
      } 
    } 
  }
  
  public void snmpV1Trap(int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV1Trap", "generic=" + paramInt1 + ", specific=" + paramInt2); 
    SnmpPduTrap snmpPduTrap = new SnmpPduTrap();
    snmpPduTrap.address = null;
    snmpPduTrap.port = this.trapPort;
    snmpPduTrap.type = 164;
    snmpPduTrap.version = 0;
    snmpPduTrap.community = null;
    snmpPduTrap.enterprise = this.enterpriseOid;
    snmpPduTrap.genericTrap = paramInt1;
    snmpPduTrap.specificTrap = paramInt2;
    snmpPduTrap.timeStamp = getSysUpTime();
    if (paramSnmpVarBindList != null) {
      snmpPduTrap.varBindList = new SnmpVarBind[paramSnmpVarBindList.size()];
      paramSnmpVarBindList.copyInto(snmpPduTrap.varBindList);
    } else {
      snmpPduTrap.varBindList = null;
    } 
    try {
      if (this.address != null) {
        snmpPduTrap.agentAddr = handleMultipleIpVersion(this.address.getAddress());
      } else {
        snmpPduTrap.agentAddr = handleMultipleIpVersion(InetAddress.getLocalHost().getAddress());
      } 
    } catch (UnknownHostException unknownHostException) {
      byte[] arrayOfByte = new byte[4];
      snmpPduTrap.agentAddr = handleMultipleIpVersion(arrayOfByte);
    } 
    sendTrapPdu(snmpPduTrap);
  }
  
  private SnmpIpAddress handleMultipleIpVersion(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length == 4)
      return new SnmpIpAddress(paramArrayOfByte); 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "handleMultipleIPVersion", "Not an IPv4 address, return null"); 
    return null;
  }
  
  public void snmpV1Trap(InetAddress paramInetAddress, String paramString, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV1Trap", "generic=" + paramInt1 + ", specific=" + paramInt2); 
    SnmpPduTrap snmpPduTrap = new SnmpPduTrap();
    snmpPduTrap.address = null;
    snmpPduTrap.port = this.trapPort;
    snmpPduTrap.type = 164;
    snmpPduTrap.version = 0;
    if (paramString != null) {
      snmpPduTrap.community = paramString.getBytes();
    } else {
      snmpPduTrap.community = null;
    } 
    snmpPduTrap.enterprise = this.enterpriseOid;
    snmpPduTrap.genericTrap = paramInt1;
    snmpPduTrap.specificTrap = paramInt2;
    snmpPduTrap.timeStamp = getSysUpTime();
    if (paramSnmpVarBindList != null) {
      snmpPduTrap.varBindList = new SnmpVarBind[paramSnmpVarBindList.size()];
      paramSnmpVarBindList.copyInto(snmpPduTrap.varBindList);
    } else {
      snmpPduTrap.varBindList = null;
    } 
    try {
      if (this.address != null) {
        snmpPduTrap.agentAddr = handleMultipleIpVersion(this.address.getAddress());
      } else {
        snmpPduTrap.agentAddr = handleMultipleIpVersion(InetAddress.getLocalHost().getAddress());
      } 
    } catch (UnknownHostException unknownHostException) {
      byte[] arrayOfByte = new byte[4];
      snmpPduTrap.agentAddr = handleMultipleIpVersion(arrayOfByte);
    } 
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, snmpPduTrap);
    } else {
      sendTrapPdu(snmpPduTrap);
    } 
  }
  
  public void snmpV1Trap(InetAddress paramInetAddress, SnmpIpAddress paramSnmpIpAddress, String paramString, SnmpOid paramSnmpOid, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException { snmpV1Trap(paramInetAddress, this.trapPort, paramSnmpIpAddress, paramString, paramSnmpOid, paramInt1, paramInt2, paramSnmpVarBindList, paramSnmpTimeticks); }
  
  public void snmpV1Trap(SnmpPeer paramSnmpPeer, SnmpIpAddress paramSnmpIpAddress, SnmpOid paramSnmpOid, int paramInt1, int paramInt2, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException {
    SnmpParameters snmpParameters = (SnmpParameters)paramSnmpPeer.getParams();
    snmpV1Trap(paramSnmpPeer.getDestAddr(), paramSnmpPeer.getDestPort(), paramSnmpIpAddress, snmpParameters.getRdCommunity(), paramSnmpOid, paramInt1, paramInt2, paramSnmpVarBindList, paramSnmpTimeticks);
  }
  
  private void snmpV1Trap(InetAddress paramInetAddress, int paramInt1, SnmpIpAddress paramSnmpIpAddress, String paramString, SnmpOid paramSnmpOid, int paramInt2, int paramInt3, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException {
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV1Trap", "generic=" + paramInt2 + ", specific=" + paramInt3); 
    SnmpPduTrap snmpPduTrap = new SnmpPduTrap();
    snmpPduTrap.address = null;
    snmpPduTrap.port = paramInt1;
    snmpPduTrap.type = 164;
    snmpPduTrap.version = 0;
    if (paramString != null) {
      snmpPduTrap.community = paramString.getBytes();
    } else {
      snmpPduTrap.community = null;
    } 
    if (paramSnmpOid != null) {
      snmpPduTrap.enterprise = paramSnmpOid;
    } else {
      snmpPduTrap.enterprise = this.enterpriseOid;
    } 
    snmpPduTrap.genericTrap = paramInt2;
    snmpPduTrap.specificTrap = paramInt3;
    if (paramSnmpTimeticks != null) {
      snmpPduTrap.timeStamp = paramSnmpTimeticks.longValue();
    } else {
      snmpPduTrap.timeStamp = getSysUpTime();
    } 
    if (paramSnmpVarBindList != null) {
      snmpPduTrap.varBindList = new SnmpVarBind[paramSnmpVarBindList.size()];
      paramSnmpVarBindList.copyInto(snmpPduTrap.varBindList);
    } else {
      snmpPduTrap.varBindList = null;
    } 
    if (paramSnmpIpAddress == null)
      try {
        InetAddress inetAddress = (this.address != null) ? this.address : InetAddress.getLocalHost();
        paramSnmpIpAddress = handleMultipleIpVersion(inetAddress.getAddress());
      } catch (UnknownHostException unknownHostException) {
        byte[] arrayOfByte = new byte[4];
        paramSnmpIpAddress = handleMultipleIpVersion(arrayOfByte);
      }  
    snmpPduTrap.agentAddr = paramSnmpIpAddress;
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, snmpPduTrap);
    } else {
      sendTrapPdu(snmpPduTrap);
    } 
  }
  
  public void snmpV2Trap(SnmpPeer paramSnmpPeer, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException {
    SnmpParameters snmpParameters = (SnmpParameters)paramSnmpPeer.getParams();
    snmpV2Trap(paramSnmpPeer.getDestAddr(), paramSnmpPeer.getDestPort(), snmpParameters.getRdCommunity(), paramSnmpOid, paramSnmpVarBindList, paramSnmpTimeticks);
  }
  
  public void snmpV2Trap(SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException {
    SnmpVarBindList snmpVarBindList;
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV2Trap", "trapOid=" + paramSnmpOid); 
    SnmpPduRequest snmpPduRequest = new SnmpPduRequest();
    snmpPduRequest.address = null;
    snmpPduRequest.port = this.trapPort;
    snmpPduRequest.type = 167;
    snmpPduRequest.version = 1;
    snmpPduRequest.community = null;
    if (paramSnmpVarBindList != null) {
      snmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      snmpVarBindList = new SnmpVarBindList(2);
    } 
    SnmpTimeticks snmpTimeticks = new SnmpTimeticks(getSysUpTime());
    snmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    snmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, snmpTimeticks), 0);
    snmpPduRequest.varBindList = new SnmpVarBind[snmpVarBindList.size()];
    snmpVarBindList.copyInto(snmpPduRequest.varBindList);
    sendTrapPdu(snmpPduRequest);
  }
  
  public void snmpV2Trap(InetAddress paramInetAddress, String paramString, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IOException, SnmpStatusException {
    SnmpVarBindList snmpVarBindList;
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV2Trap", "trapOid=" + paramSnmpOid); 
    SnmpPduRequest snmpPduRequest = new SnmpPduRequest();
    snmpPduRequest.address = null;
    snmpPduRequest.port = this.trapPort;
    snmpPduRequest.type = 167;
    snmpPduRequest.version = 1;
    if (paramString != null) {
      snmpPduRequest.community = paramString.getBytes();
    } else {
      snmpPduRequest.community = null;
    } 
    if (paramSnmpVarBindList != null) {
      snmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      snmpVarBindList = new SnmpVarBindList(2);
    } 
    SnmpTimeticks snmpTimeticks = new SnmpTimeticks(getSysUpTime());
    snmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    snmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, snmpTimeticks), 0);
    snmpPduRequest.varBindList = new SnmpVarBind[snmpVarBindList.size()];
    snmpVarBindList.copyInto(snmpPduRequest.varBindList);
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, snmpPduRequest);
    } else {
      sendTrapPdu(snmpPduRequest);
    } 
  }
  
  public void snmpV2Trap(InetAddress paramInetAddress, String paramString, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException { snmpV2Trap(paramInetAddress, this.trapPort, paramString, paramSnmpOid, paramSnmpVarBindList, paramSnmpTimeticks); }
  
  private void snmpV2Trap(InetAddress paramInetAddress, int paramInt, String paramString, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList, SnmpTimeticks paramSnmpTimeticks) throws IOException, SnmpStatusException {
    SnmpTimeticks snmpTimeticks;
    SnmpVarBindList snmpVarBindList;
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("trapOid=").append(paramSnmpOid).append("\ncommunity=").append(paramString).append("\naddr=").append(paramInetAddress).append("\nvarBindList=").append(paramSnmpVarBindList).append("\ntime=").append(paramSnmpTimeticks).append("\ntrapPort=").append(paramInt);
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpV2Trap", stringBuilder.toString());
    } 
    SnmpPduRequest snmpPduRequest = new SnmpPduRequest();
    snmpPduRequest.address = null;
    snmpPduRequest.port = paramInt;
    snmpPduRequest.type = 167;
    snmpPduRequest.version = 1;
    if (paramString != null) {
      snmpPduRequest.community = paramString.getBytes();
    } else {
      snmpPduRequest.community = null;
    } 
    if (paramSnmpVarBindList != null) {
      snmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      snmpVarBindList = new SnmpVarBindList(2);
    } 
    if (paramSnmpTimeticks != null) {
      snmpTimeticks = paramSnmpTimeticks;
    } else {
      snmpTimeticks = new SnmpTimeticks(getSysUpTime());
    } 
    snmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    snmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, snmpTimeticks), 0);
    snmpPduRequest.varBindList = new SnmpVarBind[snmpVarBindList.size()];
    snmpVarBindList.copyInto(snmpPduRequest.varBindList);
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, snmpPduRequest);
    } else {
      sendTrapPdu(snmpPduRequest);
    } 
  }
  
  public void snmpPduTrap(InetAddress paramInetAddress, SnmpPduPacket paramSnmpPduPacket) throws IOException, SnmpStatusException {
    if (paramInetAddress != null) {
      sendTrapPdu(paramInetAddress, paramSnmpPduPacket);
    } else {
      sendTrapPdu(paramSnmpPduPacket);
    } 
  }
  
  public void snmpPduTrap(SnmpPeer paramSnmpPeer, SnmpPduPacket paramSnmpPduPacket) throws IOException, SnmpStatusException {
    if (paramSnmpPeer != null) {
      paramSnmpPduPacket.port = paramSnmpPeer.getDestPort();
      sendTrapPdu(paramSnmpPeer.getDestAddr(), paramSnmpPduPacket);
    } else {
      paramSnmpPduPacket.port = getTrapPort().intValue();
      sendTrapPdu(paramSnmpPduPacket);
    } 
  }
  
  private void sendTrapPdu(SnmpPduPacket paramSnmpPduPacket) throws SnmpStatusException, IOException {
    SnmpMessage snmpMessage = null;
    try {
      snmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(paramSnmpPduPacket, this.bufferSize);
      if (snmpMessage == null)
        throw new SnmpStatusException(16); 
    } catch (SnmpTooBigException snmpTooBigException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to anyone"); 
      throw new SnmpStatusException(1);
    } 
    byte b = 0;
    openTrapSocketIfNeeded();
    if (this.ipacl != null) {
      Enumeration enumeration = this.ipacl.getTrapDestinations();
      while (enumeration.hasMoreElements()) {
        snmpMessage.address = (InetAddress)enumeration.nextElement();
        Enumeration enumeration1 = this.ipacl.getTrapCommunities(snmpMessage.address);
        while (enumeration1.hasMoreElements()) {
          snmpMessage.community = ((String)enumeration1.nextElement()).getBytes();
          try {
            sendTrapMessage(snmpMessage);
            b++;
          } catch (SnmpTooBigException snmpTooBigException) {
            if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
              JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to " + snmpMessage.address); 
          } 
        } 
      } 
    } 
    if (b == 0)
      try {
        snmpMessage.address = InetAddress.getLocalHost();
        sendTrapMessage(snmpMessage);
      } catch (SnmpTooBigException snmpTooBigException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent."); 
      } catch (UnknownHostException unknownHostException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent."); 
      }  
    closeTrapSocketIfNeeded();
  }
  
  private void sendTrapPdu(InetAddress paramInetAddress, SnmpPduPacket paramSnmpPduPacket) throws IOException, SnmpStatusException {
    SnmpMessage snmpMessage = null;
    try {
      snmpMessage = (SnmpMessage)this.pduFactory.encodeSnmpPdu(paramSnmpPduPacket, this.bufferSize);
      if (snmpMessage == null)
        throw new SnmpStatusException(16); 
    } catch (SnmpTooBigException snmpTooBigException) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to the specified host."); 
      throw new SnmpStatusException(1);
    } 
    openTrapSocketIfNeeded();
    if (paramInetAddress != null) {
      snmpMessage.address = paramInetAddress;
      try {
        sendTrapMessage(snmpMessage);
      } catch (SnmpTooBigException snmpTooBigException) {
        if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, this.dbgTag, "sendTrapPdu", "Trap pdu is too big. Trap hasn't been sent to " + snmpMessage.address); 
      } 
    } 
    closeTrapSocketIfNeeded();
  }
  
  private void sendTrapMessage(SnmpMessage paramSnmpMessage) throws IOException, SnmpTooBigException {
    byte[] arrayOfByte = new byte[this.bufferSize];
    DatagramPacket datagramPacket = new DatagramPacket(arrayOfByte, arrayOfByte.length);
    int i = paramSnmpMessage.encodeMessage(arrayOfByte);
    datagramPacket.setLength(i);
    datagramPacket.setAddress(paramSnmpMessage.address);
    datagramPacket.setPort(paramSnmpMessage.port);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "sendTrapMessage", "sending trap to " + paramSnmpMessage.address + ":" + paramSnmpMessage.port); 
    this.trapSocket.send(datagramPacket);
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "sendTrapMessage", "sent to " + paramSnmpMessage.address + ":" + paramSnmpMessage.port); 
    this.snmpOutTraps++;
    this.snmpOutPkts++;
  }
  
  void openTrapSocketIfNeeded() {
    if (this.trapSocket == null) {
      this.trapSocket = new DatagramSocket(0, this.address);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "openTrapSocketIfNeeded", "using port " + this.trapSocket.getLocalPort() + " to send traps"); 
    } 
  }
  
  void closeTrapSocketIfNeeded() {
    if (this.trapSocket != null && this.state != 0) {
      this.trapSocket.close();
      this.trapSocket = null;
    } 
  }
  
  public Vector<SnmpInformRequest> snmpInformRequest(SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IllegalStateException, IOException, SnmpStatusException {
    SnmpVarBindList snmpVarBindList;
    if (!isActive())
      throw new IllegalStateException("Start SNMP adaptor server before carrying out this operation"); 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpInformRequest", "trapOid=" + paramSnmpOid); 
    if (paramSnmpVarBindList != null) {
      snmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      snmpVarBindList = new SnmpVarBindList(2);
    } 
    SnmpTimeticks snmpTimeticks = new SnmpTimeticks(getSysUpTime());
    snmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    snmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, snmpTimeticks), 0);
    openInformSocketIfNeeded();
    Vector vector = new Vector();
    if (this.ipacl != null) {
      Enumeration enumeration = this.ipacl.getInformDestinations();
      while (enumeration.hasMoreElements()) {
        InetAddress inetAddress = (InetAddress)enumeration.nextElement();
        Enumeration enumeration1 = this.ipacl.getInformCommunities(inetAddress);
        while (enumeration1.hasMoreElements()) {
          String str = (String)enumeration1.nextElement();
          vector.addElement(this.informSession.makeAsyncRequest(inetAddress, str, paramSnmpInformHandler, snmpVarBindList, getInformPort()));
        } 
      } 
    } 
    return vector;
  }
  
  public SnmpInformRequest snmpInformRequest(InetAddress paramInetAddress, String paramString, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IllegalStateException, IOException, SnmpStatusException { return snmpInformRequest(paramInetAddress, getInformPort(), paramString, paramSnmpInformHandler, paramSnmpOid, paramSnmpVarBindList); }
  
  public SnmpInformRequest snmpInformRequest(SnmpPeer paramSnmpPeer, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IllegalStateException, IOException, SnmpStatusException {
    SnmpParameters snmpParameters = (SnmpParameters)paramSnmpPeer.getParams();
    return snmpInformRequest(paramSnmpPeer.getDestAddr(), paramSnmpPeer.getDestPort(), snmpParameters.getInformCommunity(), paramSnmpInformHandler, paramSnmpOid, paramSnmpVarBindList);
  }
  
  public static int mapErrorStatus(int paramInt1, int paramInt2, int paramInt3) { return SnmpSubRequestHandler.mapErrorStatus(paramInt1, paramInt2, paramInt3); }
  
  private SnmpInformRequest snmpInformRequest(InetAddress paramInetAddress, int paramInt, String paramString, SnmpInformHandler paramSnmpInformHandler, SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) throws IllegalStateException, IOException, SnmpStatusException {
    SnmpVarBindList snmpVarBindList;
    if (!isActive())
      throw new IllegalStateException("Start SNMP adaptor server before carrying out this operation"); 
    if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "snmpInformRequest", "trapOid=" + paramSnmpOid); 
    if (paramSnmpVarBindList != null) {
      snmpVarBindList = paramSnmpVarBindList.clone();
    } else {
      snmpVarBindList = new SnmpVarBindList(2);
    } 
    SnmpTimeticks snmpTimeticks = new SnmpTimeticks(getSysUpTime());
    snmpVarBindList.insertElementAt(new SnmpVarBind(snmpTrapOidOid, paramSnmpOid), 0);
    snmpVarBindList.insertElementAt(new SnmpVarBind(sysUpTimeOid, snmpTimeticks), 0);
    openInformSocketIfNeeded();
    return this.informSession.makeAsyncRequest(paramInetAddress, paramString, paramSnmpInformHandler, snmpVarBindList, paramInt);
  }
  
  void openInformSocketIfNeeded() {
    if (this.informSession == null) {
      this.informSession = new SnmpSession(this);
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "openInformSocketIfNeeded", "to send inform requests and receive inform responses"); 
    } 
  }
  
  void closeInformSocketIfNeeded() {
    if (this.informSession != null && this.state != 0) {
      this.informSession.destroySession();
      this.informSession = null;
    } 
  }
  
  InetAddress getAddress() { return this.address; }
  
  protected void finalize() {
    try {
      if (this.socket != null) {
        this.socket.close();
        this.socket = null;
      } 
      this.threadService.terminate();
    } catch (Exception exception) {
      if (JmxProperties.SNMP_ADAPTOR_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, this.dbgTag, "finalize", "Exception in finalizer", exception); 
    } 
  }
  
  String makeDebugTag() { return "SnmpAdaptorServer[" + getProtocol() + ":" + getPort() + "]"; }
  
  void updateRequestCounters(int paramInt) {
    switch (paramInt) {
      case 160:
        this.snmpInGetRequests++;
        break;
      case 161:
        this.snmpInGetNexts++;
        break;
      case 163:
        this.snmpInSetRequests++;
        break;
    } 
    this.snmpInPkts++;
  }
  
  void updateErrorCounters(int paramInt) {
    switch (paramInt) {
      case 0:
        this.snmpOutGetResponses++;
        break;
      case 5:
        this.snmpOutGenErrs++;
        break;
      case 3:
        this.snmpOutBadValues++;
        break;
      case 2:
        this.snmpOutNoSuchNames++;
        break;
      case 1:
        this.snmpOutTooBigs++;
        break;
    } 
    this.snmpOutPkts++;
  }
  
  void updateVarCounters(int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case 160:
      case 161:
      case 165:
        this.snmpInTotalReqVars += paramInt2;
        break;
      case 163:
        this.snmpInTotalSetVars += paramInt2;
        break;
    } 
  }
  
  void incSnmpInASNParseErrs(int paramInt) { this.snmpInASNParseErrs += paramInt; }
  
  void incSnmpInBadVersions(int paramInt) { this.snmpInBadVersions += paramInt; }
  
  void incSnmpInBadCommunityUses(int paramInt) { this.snmpInBadCommunityUses += paramInt; }
  
  void incSnmpInBadCommunityNames(int paramInt) { this.snmpInBadCommunityNames += paramInt; }
  
  void incSnmpSilentDrops(int paramInt) { this.snmpSilentDrops += paramInt; }
  
  long getSysUpTime() { return (System.currentTimeMillis() - this.startUpTime) / 10L; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.mibs = new Vector();
  }
  
  private void init(InetAddressAcl paramInetAddressAcl, int paramInt, InetAddress paramInetAddress) {
    this.root = new SnmpMibTree();
    this.root.setDefaultAgent(new SnmpErrorHandlerAgent());
    this.startUpTime = System.currentTimeMillis();
    this.maxActiveClientCount = 10;
    this.pduFactory = new SnmpPduFactoryBER();
    this.port = paramInt;
    this.ipacl = paramInetAddressAcl;
    this.address = paramInetAddress;
    if (this.ipacl == null && this.useAcl == true)
      throw new IllegalArgumentException("ACL object cannot be null"); 
    this.threadService = new ThreadService(threadNumber);
  }
  
  SnmpMibAgent getAgentMib(SnmpOid paramSnmpOid) { return this.root.getAgentMib(paramSnmpOid); }
  
  protected Thread createMainThread() {
    Thread thread = super.createMainThread();
    thread.setDaemon(true);
    return thread;
  }
  
  static  {
    String str = System.getProperty("com.sun.jmx.snmp.threadnumber");
    if (str != null)
      try {
        threadNumber = Integer.parseInt(System.getProperty(str));
      } catch (Exception exception) {
        JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINER, SnmpAdaptorServer.class.getName(), "<static init>", "Got wrong value for com.sun.jmx.snmp.threadnumber: " + str + ". Use the default value: " + threadNumber);
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpAdaptorServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */