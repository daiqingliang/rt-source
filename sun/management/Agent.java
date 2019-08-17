package sun.management;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import sun.management.jdp.JdpController;
import sun.management.jdp.JdpException;
import sun.management.jmxremote.ConnectorBootstrap;
import sun.misc.VMSupport;

public class Agent {
  private static Properties mgmtProps;
  
  private static ResourceBundle messageRB;
  
  private static final String CONFIG_FILE = "com.sun.management.config.file";
  
  private static final String SNMP_PORT = "com.sun.management.snmp.port";
  
  private static final String JMXREMOTE = "com.sun.management.jmxremote";
  
  private static final String JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
  
  private static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
  
  private static final String ENABLE_THREAD_CONTENTION_MONITORING = "com.sun.management.enableThreadContentionMonitoring";
  
  private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";
  
  private static final String SNMP_ADAPTOR_BOOTSTRAP_CLASS_NAME = "sun.management.snmp.AdaptorBootstrap";
  
  private static final String JDP_DEFAULT_ADDRESS = "224.0.23.178";
  
  private static final int JDP_DEFAULT_PORT = 7095;
  
  private static JMXConnectorServer jmxServer = null;
  
  private static Properties parseString(String paramString) {
    Properties properties = new Properties();
    if (paramString != null && !paramString.trim().equals(""))
      for (String str1 : paramString.split(",")) {
        String[] arrayOfString = str1.split("=", 2);
        String str2 = arrayOfString[0].trim();
        String str3 = (arrayOfString.length > 1) ? arrayOfString[1].trim() : "";
        if (!str2.startsWith("com.sun.management."))
          error("agent.err.invalid.option", str2); 
        properties.setProperty(str2, str3);
      }  
    return properties;
  }
  
  public static void premain(String paramString) throws Exception { agentmain(paramString); }
  
  public static void agentmain(String paramString) throws Exception {
    if (paramString == null || paramString.length() == 0)
      paramString = "com.sun.management.jmxremote"; 
    Properties properties1 = parseString(paramString);
    Properties properties2 = new Properties();
    String str = properties1.getProperty("com.sun.management.config.file");
    readConfiguration(str, properties2);
    properties2.putAll(properties1);
    startAgent(properties2);
  }
  
  private static void startLocalManagementAgent() {
    Properties properties = VMSupport.getAgentProperties();
    if (properties.get("com.sun.management.jmxremote.localConnectorAddress") == null) {
      JMXConnectorServer jMXConnectorServer = ConnectorBootstrap.startLocalConnectorServer();
      String str = jMXConnectorServer.getAddress().toString();
      properties.put("com.sun.management.jmxremote.localConnectorAddress", str);
      try {
        ConnectorAddressLink.export(str);
      } catch (Exception exception) {
        warning("agent.err.exportaddress.failed", exception.getMessage());
      } 
    } 
  }
  
  private static void startRemoteManagementAgent(String paramString) throws Exception {
    if (jmxServer != null)
      throw new RuntimeException(getText("agent.err.invalid.state", new String[] { "Agent already started" })); 
    try {
      Properties properties1 = parseString(paramString);
      Properties properties2 = new Properties();
      String str1 = System.getProperty("com.sun.management.config.file");
      readConfiguration(str1, properties2);
      Properties properties3 = System.getProperties();
      synchronized (properties3) {
        properties2.putAll(properties3);
      } 
      String str2 = properties1.getProperty("com.sun.management.config.file");
      if (str2 != null)
        readConfiguration(str2, properties2); 
      properties2.putAll(properties1);
      String str3 = properties2.getProperty("com.sun.management.enableThreadContentionMonitoring");
      if (str3 != null)
        ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true); 
      String str4 = properties2.getProperty("com.sun.management.jmxremote.port");
      if (str4 != null) {
        jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str4, properties2);
        startDiscoveryService(properties2);
      } else {
        throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[] { "No port specified" });
      } 
    } catch (AgentConfigurationError agentConfigurationError) {
      error(agentConfigurationError);
    } 
  }
  
  private static void stopRemoteManagementAgent() {
    JdpController.stopDiscoveryService();
    if (jmxServer != null) {
      ConnectorBootstrap.unexportRegistry();
      jmxServer.stop();
      jmxServer = null;
    } 
  }
  
  private static void startAgent(Properties paramProperties) throws Exception {
    String str1 = paramProperties.getProperty("com.sun.management.snmp.port");
    String str2 = paramProperties.getProperty("com.sun.management.jmxremote");
    String str3 = paramProperties.getProperty("com.sun.management.jmxremote.port");
    String str4 = paramProperties.getProperty("com.sun.management.enableThreadContentionMonitoring");
    if (str4 != null)
      ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true); 
    try {
      if (str1 != null)
        loadSnmpAgent(str1, paramProperties); 
      if (str2 != null || str3 != null) {
        if (str3 != null) {
          jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str3, paramProperties);
          startDiscoveryService(paramProperties);
        } 
        startLocalManagementAgent();
      } 
    } catch (AgentConfigurationError agentConfigurationError) {
      error(agentConfigurationError);
    } catch (Exception exception) {
      error(exception);
    } 
  }
  
  private static void startDiscoveryService(Properties paramProperties) throws Exception {
    String str1 = paramProperties.getProperty("com.sun.management.jdp.port");
    String str2 = paramProperties.getProperty("com.sun.management.jdp.address");
    String str3 = paramProperties.getProperty("com.sun.management.jmxremote.autodiscovery");
    boolean bool = false;
    if (str3 == null) {
      bool = (str1 != null);
    } else {
      try {
        bool = Boolean.parseBoolean(str3);
      } catch (NumberFormatException numberFormatException) {
        throw new AgentConfigurationError("Couldn't parse autodiscovery argument");
      } 
    } 
    if (bool) {
      InetAddress inetAddress;
      try {
        inetAddress = (str2 == null) ? InetAddress.getByName("224.0.23.178") : InetAddress.getByName(str2);
      } catch (UnknownHostException unknownHostException) {
        throw new AgentConfigurationError("Unable to broadcast to requested address", unknownHostException);
      } 
      int i = 7095;
      if (str1 != null)
        try {
          i = Integer.parseInt(str1);
        } catch (NumberFormatException numberFormatException) {
          throw new AgentConfigurationError("Couldn't parse JDP port argument");
        }  
      String str4 = paramProperties.getProperty("com.sun.management.jmxremote.port");
      String str5 = paramProperties.getProperty("com.sun.management.jmxremote.rmi.port");
      JMXServiceURL jMXServiceURL = jmxServer.getAddress();
      String str6;
      String str7 = (str5 != null) ? (str6 = jMXServiceURL.getHost()).format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", new Object[] { str6, str5, str6, str4 }) : String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", new Object[] { str6, str4 });
      String str8 = paramProperties.getProperty("com.sun.management.jdp.name");
      try {
        JdpController.startDiscoveryService(inetAddress, i, str8, str7);
      } catch (JdpException jdpException) {
        throw new AgentConfigurationError("Couldn't start JDP service", jdpException);
      } 
    } 
  }
  
  public static Properties loadManagementProperties() {
    Properties properties1 = new Properties();
    String str = System.getProperty("com.sun.management.config.file");
    readConfiguration(str, properties1);
    Properties properties2 = System.getProperties();
    synchronized (properties2) {
      properties1.putAll(properties2);
    } 
    return properties1;
  }
  
  public static Properties getManagementProperties() {
    if (mgmtProps == null) {
      String str1 = System.getProperty("com.sun.management.config.file");
      String str2 = System.getProperty("com.sun.management.snmp.port");
      String str3 = System.getProperty("com.sun.management.jmxremote");
      String str4 = System.getProperty("com.sun.management.jmxremote.port");
      if (str1 == null && str2 == null && str3 == null && str4 == null)
        return null; 
      mgmtProps = loadManagementProperties();
    } 
    return mgmtProps;
  }
  
  private static void loadSnmpAgent(String paramString, Properties paramProperties) {
    try {
      Class clazz = Class.forName("sun.management.snmp.AdaptorBootstrap", true, null);
      Method method = clazz.getMethod("initialize", new Class[] { String.class, Properties.class });
      method.invoke(null, new Object[] { paramString, paramProperties });
    } catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException classNotFoundException) {
      throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", classNotFoundException);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", throwable);
    } 
  }
  
  private static void readConfiguration(String paramString, Properties paramProperties) {
    if (paramString == null) {
      String str = System.getProperty("java.home");
      if (str == null)
        throw new Error("Can't find java.home ??"); 
      StringBuffer stringBuffer = new StringBuffer(str);
      stringBuffer.append(File.separator).append("lib");
      stringBuffer.append(File.separator).append("management");
      stringBuffer.append(File.separator).append("management.properties");
      paramString = stringBuffer.toString();
    } 
    File file = new File(paramString);
    if (!file.exists())
      error("agent.err.configfile.notfound", paramString); 
    fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(file);
      bufferedInputStream = new BufferedInputStream(fileInputStream);
      paramProperties.load(bufferedInputStream);
    } catch (FileNotFoundException fileNotFoundException) {
      error("agent.err.configfile.failed", fileNotFoundException.getMessage());
    } catch (IOException iOException) {
      error("agent.err.configfile.failed", iOException.getMessage());
    } catch (SecurityException securityException) {
      error("agent.err.configfile.access.denied", paramString);
    } finally {
      if (fileInputStream != null)
        try {
          fileInputStream.close();
        } catch (IOException iOException) {
          error("agent.err.configfile.closed.failed", paramString);
        }  
    } 
  }
  
  public static void startAgent() {
    String str1 = System.getProperty("com.sun.management.agent.class");
    if (str1 == null) {
      Properties properties = getManagementProperties();
      if (properties != null)
        startAgent(properties); 
      return;
    } 
    String[] arrayOfString = str1.split(":");
    if (arrayOfString.length < 1 || arrayOfString.length > 2)
      error("agent.err.invalid.agentclass", "\"" + str1 + "\""); 
    String str2 = arrayOfString[0];
    String str3 = (arrayOfString.length == 2) ? arrayOfString[1] : null;
    if (str2 == null || str2.length() == 0)
      error("agent.err.invalid.agentclass", "\"" + str1 + "\""); 
    if (str2 != null)
      try {
        Class clazz = ClassLoader.getSystemClassLoader().loadClass(str2);
        Method method = clazz.getMethod("premain", new Class[] { String.class });
        method.invoke(null, new Object[] { str3 });
      } catch (ClassNotFoundException classNotFoundException) {
        error("agent.err.agentclass.notfound", "\"" + str2 + "\"");
      } catch (NoSuchMethodException noSuchMethodException) {
        error("agent.err.premain.notfound", "\"" + str2 + "\"");
      } catch (SecurityException securityException) {
        error("agent.err.agentclass.access.denied");
      } catch (Exception exception) {
        String str = (exception.getCause() == null) ? exception.getMessage() : exception.getCause().getMessage();
        error("agent.err.agentclass.failed", str);
      }  
  }
  
  public static void error(String paramString) throws Exception {
    String str = getText(paramString);
    System.err.print(getText("agent.err.error") + ": " + str);
    throw new RuntimeException(str);
  }
  
  public static void error(String paramString1, String paramString2) {
    String str = getText(paramString1);
    System.err.print(getText("agent.err.error") + ": " + str);
    System.err.println(": " + paramString2);
    throw new RuntimeException(str + ": " + paramString2);
  }
  
  public static void error(Exception paramException) {
    paramException.printStackTrace();
    System.err.println(getText("agent.err.exception") + ": " + paramException.toString());
    throw new RuntimeException(paramException);
  }
  
  public static void error(AgentConfigurationError paramAgentConfigurationError) {
    String str = getText(paramAgentConfigurationError.getError());
    String[] arrayOfString = paramAgentConfigurationError.getParams();
    System.err.print(getText("agent.err.error") + ": " + str);
    if (arrayOfString != null && arrayOfString.length != 0) {
      StringBuffer stringBuffer = new StringBuffer(arrayOfString[0]);
      for (byte b = 1; b < arrayOfString.length; b++)
        stringBuffer.append(" " + arrayOfString[b]); 
      System.err.println(": " + stringBuffer);
    } 
    paramAgentConfigurationError.printStackTrace();
    throw new RuntimeException(paramAgentConfigurationError);
  }
  
  public static void warning(String paramString1, String paramString2) {
    System.err.print(getText("agent.err.warning") + ": " + getText(paramString1));
    System.err.println(": " + paramString2);
  }
  
  private static void initResource() {
    try {
      messageRB = ResourceBundle.getBundle("sun.management.resources.agent");
    } catch (MissingResourceException missingResourceException) {
      throw new Error("Fatal: Resource for management agent is missing");
    } 
  }
  
  public static String getText(String paramString) {
    if (messageRB == null)
      initResource(); 
    try {
      return messageRB.getString(paramString);
    } catch (MissingResourceException missingResourceException) {
      return "Missing management agent resource bundle: key = \"" + paramString + "\"";
    } 
  }
  
  public static String getText(String paramString, String... paramVarArgs) {
    if (messageRB == null)
      initResource(); 
    String str = messageRB.getString(paramString);
    if (str == null)
      str = "missing resource key: key = \"" + paramString + "\", arguments = \"{0}\", \"{1}\", \"{2}\""; 
    return MessageFormat.format(str, (Object[])paramVarArgs);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\Agent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */