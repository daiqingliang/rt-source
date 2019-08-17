package sun.management.snmp;

import com.sun.jmx.snmp.IPAcl.SnmpAcl;
import com.sun.jmx.snmp.InetAddressAcl;
import com.sun.jmx.snmp.daemon.SnmpAdaptorServer;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import sun.management.Agent;
import sun.management.AgentConfigurationError;
import sun.management.FileSystem;
import sun.management.snmp.jvminstr.JVM_MANAGEMENT_MIB_IMPL;
import sun.management.snmp.jvminstr.NotificationTarget;
import sun.management.snmp.jvminstr.NotificationTargetImpl;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;

public final class AdaptorBootstrap {
  private static final MibLogger log = new MibLogger(AdaptorBootstrap.class);
  
  private SnmpAdaptorServer adaptor;
  
  private JVM_MANAGEMENT_MIB_IMPL jvmmib;
  
  private AdaptorBootstrap(SnmpAdaptorServer paramSnmpAdaptorServer, JVM_MANAGEMENT_MIB_IMPL paramJVM_MANAGEMENT_MIB_IMPL) {
    this.jvmmib = paramJVM_MANAGEMENT_MIB_IMPL;
    this.adaptor = paramSnmpAdaptorServer;
  }
  
  private static String getDefaultFileName(String paramString) {
    String str = File.separator;
    return System.getProperty("java.home") + str + "lib" + str + "management" + str + paramString;
  }
  
  private static List<NotificationTarget> getTargetList(InetAddressAcl paramInetAddressAcl, int paramInt) {
    ArrayList arrayList = new ArrayList();
    if (paramInetAddressAcl != null) {
      if (log.isDebugOn())
        log.debug("getTargetList", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.processing")); 
      Enumeration enumeration = paramInetAddressAcl.getTrapDestinations();
      while (enumeration.hasMoreElements()) {
        InetAddress inetAddress = (InetAddress)enumeration.nextElement();
        Enumeration enumeration1 = paramInetAddressAcl.getTrapCommunities(inetAddress);
        while (enumeration1.hasMoreElements()) {
          String str = (String)enumeration1.nextElement();
          NotificationTargetImpl notificationTargetImpl = new NotificationTargetImpl(inetAddress, paramInt, str);
          if (log.isDebugOn())
            log.debug("getTargetList", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.adding", new String[] { notificationTargetImpl.toString() })); 
          arrayList.add(notificationTargetImpl);
        } 
      } 
    } 
    return arrayList;
  }
  
  public static AdaptorBootstrap initialize() {
    Properties properties = Agent.loadManagementProperties();
    if (properties == null)
      return null; 
    String str = properties.getProperty("com.sun.management.snmp.port");
    return initialize(str, properties);
  }
  
  public static AdaptorBootstrap initialize(String paramString, Properties paramProperties) {
    int j;
    int i;
    if (paramString.length() == 0)
      paramString = "161"; 
    try {
      i = Integer.parseInt(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new AgentConfigurationError("agent.err.invalid.snmp.port", numberFormatException, new String[] { paramString });
    } 
    if (i < 0)
      throw new AgentConfigurationError("agent.err.invalid.snmp.port", new String[] { paramString }); 
    String str1 = paramProperties.getProperty("com.sun.management.snmp.trap", "162");
    try {
      j = Integer.parseInt(str1);
    } catch (NumberFormatException numberFormatException) {
      throw new AgentConfigurationError("agent.err.invalid.snmp.trap.port", numberFormatException, new String[] { str1 });
    } 
    if (j < 0)
      throw new AgentConfigurationError("agent.err.invalid.snmp.trap.port", new String[] { str1 }); 
    String str2 = paramProperties.getProperty("com.sun.management.snmp.interface", "localhost");
    String str3 = getDefaultFileName("snmp.acl");
    String str4 = paramProperties.getProperty("com.sun.management.snmp.acl.file", str3);
    String str5 = paramProperties.getProperty("com.sun.management.snmp.acl", "true");
    boolean bool = Boolean.valueOf(str5).booleanValue();
    if (bool)
      checkAclFile(str4); 
    AdaptorBootstrap adaptorBootstrap = null;
    try {
      adaptorBootstrap = getAdaptorBootstrap(i, j, str2, bool, str4);
    } catch (Exception exception) {
      throw new AgentConfigurationError("agent.err.exception", exception, new String[] { exception.getMessage() });
    } 
    return adaptorBootstrap;
  }
  
  private static AdaptorBootstrap getAdaptorBootstrap(int paramInt1, int paramInt2, String paramString1, boolean paramBoolean, String paramString2) {
    SnmpAcl snmpAcl;
    InetAddress inetAddress;
    try {
      inetAddress = InetAddress.getByName(paramString1);
    } catch (UnknownHostException null) {
      throw new AgentConfigurationError("agent.err.unknown.snmp.interface", snmpAcl, new String[] { paramString1 });
    } 
    if (log.isDebugOn())
      log.debug("initialize", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.starting\n\tcom.sun.management.snmp.port=" + paramInt1 + "\n\t" + "com.sun.management.snmp.trap" + "=" + paramInt2 + "\n\t" + "com.sun.management.snmp.interface" + "=" + inetAddress + (paramBoolean ? ("\n\tcom.sun.management.snmp.acl.file=" + paramString2) : "\n\tNo ACL") + "")); 
    try {
      snmpAcl = paramBoolean ? new SnmpAcl(System.getProperty("user.name"), paramString2) : null;
    } catch (UnknownHostException unknownHostException) {
      throw new AgentConfigurationError("agent.err.unknown.snmp.interface", unknownHostException, new String[] { unknownHostException.getMessage() });
    } 
    SnmpAdaptorServer snmpAdaptorServer = new SnmpAdaptorServer(snmpAcl, paramInt1, inetAddress);
    snmpAdaptorServer.setUserDataFactory(new JvmContextFactory());
    snmpAdaptorServer.setTrapPort(paramInt2);
    JVM_MANAGEMENT_MIB_IMPL jVM_MANAGEMENT_MIB_IMPL = new JVM_MANAGEMENT_MIB_IMPL();
    try {
      jVM_MANAGEMENT_MIB_IMPL.init();
    } catch (IllegalAccessException illegalAccessException) {
      throw new AgentConfigurationError("agent.err.snmp.mib.init.failed", illegalAccessException, new String[] { illegalAccessException.getMessage() });
    } 
    jVM_MANAGEMENT_MIB_IMPL.addTargets(getTargetList(snmpAcl, paramInt2));
    try {
      snmpAdaptorServer.start(Float.MAX_VALUE);
    } catch (Exception exception) {
      Throwable throwable = exception;
      if (exception instanceof com.sun.jmx.snmp.daemon.CommunicationException) {
        Throwable throwable1 = throwable.getCause();
        if (throwable1 != null)
          throwable = throwable1; 
      } 
      throw new AgentConfigurationError("agent.err.snmp.adaptor.start.failed", throwable, new String[] { inetAddress + ":" + paramInt1, "(" + throwable.getMessage() + ")" });
    } 
    if (!snmpAdaptorServer.isActive())
      throw new AgentConfigurationError("agent.err.snmp.adaptor.start.failed", new String[] { inetAddress + ":" + paramInt1 }); 
    try {
      snmpAdaptorServer.addMib(jVM_MANAGEMENT_MIB_IMPL);
      jVM_MANAGEMENT_MIB_IMPL.setSnmpAdaptor(snmpAdaptorServer);
    } catch (RuntimeException runtimeException) {
      (new AdaptorBootstrap(snmpAdaptorServer, jVM_MANAGEMENT_MIB_IMPL)).terminate();
      throw runtimeException;
    } 
    log.debug("initialize", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.initialize1"));
    log.config("initialize", Agent.getText("jmxremote.AdaptorBootstrap.getTargetList.initialize2", new String[] { inetAddress.toString(), Integer.toString(snmpAdaptorServer.getPort()) }));
    return new AdaptorBootstrap(snmpAdaptorServer, jVM_MANAGEMENT_MIB_IMPL);
  }
  
  private static void checkAclFile(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new AgentConfigurationError("agent.err.acl.file.notset"); 
    File file = new File(paramString);
    if (!file.exists())
      throw new AgentConfigurationError("agent.err.acl.file.notfound", new String[] { paramString }); 
    if (!file.canRead())
      throw new AgentConfigurationError("agent.err.acl.file.not.readable", new String[] { paramString }); 
    FileSystem fileSystem = FileSystem.open();
    try {
      if (fileSystem.supportsFileSecurity(file) && !fileSystem.isAccessUserOnly(file))
        throw new AgentConfigurationError("agent.err.acl.file.access.notrestricted", new String[] { paramString }); 
    } catch (IOException iOException) {
      throw new AgentConfigurationError("agent.err.acl.file.read.failed", new String[] { paramString });
    } 
  }
  
  public int getPort() { return (this.adaptor != null) ? this.adaptor.getPort() : 0; }
  
  public void terminate() {
    if (this.adaptor == null)
      return; 
    try {
      this.jvmmib.terminate();
    } catch (Exception exception) {
      log.debug("jmxremote.AdaptorBootstrap.getTargetList.terminate", exception.toString());
    } finally {
      this.jvmmib = null;
    } 
    try {
      this.adaptor.stop();
    } finally {
      this.adaptor = null;
    } 
  }
  
  public static interface DefaultValues {
    public static final String PORT = "161";
    
    public static final String CONFIG_FILE_NAME = "management.properties";
    
    public static final String TRAP_PORT = "162";
    
    public static final String USE_ACL = "true";
    
    public static final String ACL_FILE_NAME = "snmp.acl";
    
    public static final String BIND_ADDRESS = "localhost";
  }
  
  public static interface PropertyNames {
    public static final String PORT = "com.sun.management.snmp.port";
    
    public static final String CONFIG_FILE_NAME = "com.sun.management.config.file";
    
    public static final String TRAP_PORT = "com.sun.management.snmp.trap";
    
    public static final String USE_ACL = "com.sun.management.snmp.acl";
    
    public static final String ACL_FILE_NAME = "com.sun.management.snmp.acl.file";
    
    public static final String BIND_ADDRESS = "com.sun.management.snmp.interface";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\AdaptorBootstrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */