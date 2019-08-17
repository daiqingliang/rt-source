package sun.management.jmxremote;

import com.sun.jmx.remote.internal.RMIExporter;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import com.sun.jmx.remote.util.ClassLogger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyStore;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.management.MBeanServer;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.security.auth.Subject;
import sun.management.Agent;
import sun.management.AgentConfigurationError;
import sun.management.ConnectorAddressLink;
import sun.management.FileSystem;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;

public final class ConnectorBootstrap {
  private static Registry registry = null;
  
  private static final ClassLogger log = new ClassLogger(ConnectorBootstrap.class.getPackage().getName(), "ConnectorBootstrap");
  
  public static void unexportRegistry() {
    try {
      if (registry != null) {
        UnicastRemoteObject.unexportObject(registry, true);
        registry = null;
      } 
    } catch (NoSuchObjectException noSuchObjectException) {}
  }
  
  public static JMXConnectorServer initialize() {
    Properties properties = Agent.loadManagementProperties();
    if (properties == null)
      return null; 
    String str = properties.getProperty("com.sun.management.jmxremote.port");
    return startRemoteConnectorServer(str, properties);
  }
  
  public static JMXConnectorServer initialize(String paramString, Properties paramProperties) { return startRemoteConnectorServer(paramString, paramProperties); }
  
  public static JMXConnectorServer startRemoteConnectorServer(String paramString, Properties paramProperties) {
    int i;
    try {
      i = Integer.parseInt(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", numberFormatException, new String[] { paramString });
    } 
    if (i < 0)
      throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[] { paramString }); 
    int j = 0;
    String str1 = paramProperties.getProperty("com.sun.management.jmxremote.rmi.port");
    try {
      if (str1 != null)
        j = Integer.parseInt(str1); 
    } catch (NumberFormatException numberFormatException) {
      throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", numberFormatException, new String[] { str1 });
    } 
    if (j < 0)
      throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", new String[] { str1 }); 
    String str2 = paramProperties.getProperty("com.sun.management.jmxremote.authenticate", "true");
    boolean bool1 = Boolean.valueOf(str2).booleanValue();
    String str3 = paramProperties.getProperty("com.sun.management.jmxremote.ssl", "true");
    boolean bool2 = Boolean.valueOf(str3).booleanValue();
    String str4 = paramProperties.getProperty("com.sun.management.jmxremote.registry.ssl", "false");
    boolean bool3 = Boolean.valueOf(str4).booleanValue();
    String str5 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.enabled.cipher.suites");
    String[] arrayOfString1 = null;
    if (str5 != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str5, ",");
      int k = stringTokenizer.countTokens();
      arrayOfString1 = new String[k];
      for (byte b = 0; b < k; b++)
        arrayOfString1[b] = stringTokenizer.nextToken(); 
    } 
    String str6 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.enabled.protocols");
    String[] arrayOfString2 = null;
    if (str6 != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str6, ",");
      int k = stringTokenizer.countTokens();
      arrayOfString2 = new String[k];
      for (byte b = 0; b < k; b++)
        arrayOfString2[b] = stringTokenizer.nextToken(); 
    } 
    String str7 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.need.client.auth", "false");
    boolean bool4 = Boolean.valueOf(str7).booleanValue();
    String str8 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.config.file");
    String str9 = null;
    String str10 = null;
    String str11 = null;
    if (bool1) {
      str9 = paramProperties.getProperty("com.sun.management.jmxremote.login.config");
      if (str9 == null) {
        str10 = paramProperties.getProperty("com.sun.management.jmxremote.password.file", getDefaultFileName("jmxremote.password"));
        checkPasswordFile(str10);
      } 
      str11 = paramProperties.getProperty("com.sun.management.jmxremote.access.file", getDefaultFileName("jmxremote.access"));
      checkAccessFile(str11);
    } 
    String str12 = paramProperties.getProperty("com.sun.management.jmxremote.host");
    if (log.debugOn())
      log.debug("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.starting") + "\n\t" + "com.sun.management.jmxremote.port" + "=" + i + ((str12 == null) ? "" : ("\n\tcom.sun.management.jmxremote.host=" + str12)) + "\n\t" + "com.sun.management.jmxremote.rmi.port" + "=" + j + "\n\t" + "com.sun.management.jmxremote.ssl" + "=" + bool2 + "\n\t" + "com.sun.management.jmxremote.registry.ssl" + "=" + bool3 + "\n\t" + "com.sun.management.jmxremote.ssl.config.file" + "=" + str8 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.cipher.suites" + "=" + str5 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.protocols" + "=" + str6 + "\n\t" + "com.sun.management.jmxremote.ssl.need.client.auth" + "=" + bool4 + "\n\t" + "com.sun.management.jmxremote.authenticate" + "=" + bool1 + (bool1 ? ((str9 == null) ? ("\n\tcom.sun.management.jmxremote.password.file=" + str10) : ("\n\tcom.sun.management.jmxremote.login.config=" + str9)) : ("\n\t" + Agent.getText("jmxremote.ConnectorBootstrap.noAuthentication"))) + (bool1 ? ("\n\tcom.sun.management.jmxremote.access.file=" + str11) : "") + ""); 
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    JMXConnectorServer jMXConnectorServer = null;
    JMXServiceURL jMXServiceURL = null;
    try {
      JMXConnectorServerData jMXConnectorServerData = exportMBeanServer(mBeanServer, i, j, bool2, bool3, str8, arrayOfString1, arrayOfString2, bool4, bool1, str9, str10, str11, str12);
      jMXConnectorServer = jMXConnectorServerData.jmxConnectorServer;
      jMXServiceURL = jMXConnectorServerData.jmxRemoteURL;
      log.config("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.ready", new String[] { jMXServiceURL.toString() }));
    } catch (Exception exception) {
      throw new AgentConfigurationError("agent.err.exception", exception, new String[] { exception.toString() });
    } 
    try {
      HashMap hashMap = new HashMap();
      hashMap.put("remoteAddress", jMXServiceURL.toString());
      hashMap.put("authenticate", str2);
      hashMap.put("ssl", str3);
      hashMap.put("sslRegistry", str4);
      hashMap.put("sslNeedClientAuth", str7);
      ConnectorAddressLink.exportRemote(hashMap);
    } catch (Exception exception) {
      log.debug("startRemoteConnectorServer", exception);
    } 
    return jMXConnectorServer;
  }
  
  public static JMXConnectorServer startLocalConnectorServer() {
    System.setProperty("java.rmi.server.randomIDs", "true");
    HashMap hashMap = new HashMap();
    hashMap.put("com.sun.jmx.remote.rmi.exporter", new PermanentExporter(null));
    hashMap.put("jmx.remote.rmi.server.credential.types", new String[] { String[].class.getName(), String.class.getName() });
    String str = "localhost";
    InetAddress inetAddress = null;
    try {
      inetAddress = InetAddress.getByName(str);
      str = inetAddress.getHostAddress();
    } catch (UnknownHostException unknownHostException) {}
    if (inetAddress == null || !inetAddress.isLoopbackAddress())
      str = "127.0.0.1"; 
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    try {
      JMXServiceURL jMXServiceURL = new JMXServiceURL("rmi", str, 0);
      Properties properties = Agent.getManagementProperties();
      if (properties == null)
        properties = new Properties(); 
      String str1 = properties.getProperty("com.sun.management.jmxremote.local.only", "true");
      boolean bool = Boolean.valueOf(str1).booleanValue();
      if (bool)
        hashMap.put("jmx.remote.rmi.server.socket.factory", new LocalRMIServerSocketFactory()); 
      JMXConnectorServer jMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jMXServiceURL, hashMap, mBeanServer);
      jMXConnectorServer.start();
      return jMXConnectorServer;
    } catch (Exception exception) {
      throw new AgentConfigurationError("agent.err.exception", exception, new String[] { exception.toString() });
    } 
  }
  
  private static void checkPasswordFile(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new AgentConfigurationError("agent.err.password.file.notset"); 
    File file = new File(paramString);
    if (!file.exists())
      throw new AgentConfigurationError("agent.err.password.file.notfound", new String[] { paramString }); 
    if (!file.canRead())
      throw new AgentConfigurationError("agent.err.password.file.not.readable", new String[] { paramString }); 
    FileSystem fileSystem = FileSystem.open();
    try {
      if (fileSystem.supportsFileSecurity(file) && !fileSystem.isAccessUserOnly(file)) {
        String str = Agent.getText("jmxremote.ConnectorBootstrap.password.readonly", new String[] { paramString });
        log.config("startRemoteConnectorServer", str);
        throw new AgentConfigurationError("agent.err.password.file.access.notrestricted", new String[] { paramString });
      } 
    } catch (IOException iOException) {
      throw new AgentConfigurationError("agent.err.password.file.read.failed", iOException, new String[] { paramString });
    } 
  }
  
  private static void checkAccessFile(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new AgentConfigurationError("agent.err.access.file.notset"); 
    File file = new File(paramString);
    if (!file.exists())
      throw new AgentConfigurationError("agent.err.access.file.notfound", new String[] { paramString }); 
    if (!file.canRead())
      throw new AgentConfigurationError("agent.err.access.file.not.readable", new String[] { paramString }); 
  }
  
  private static void checkRestrictedFile(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new AgentConfigurationError("agent.err.file.not.set"); 
    File file = new File(paramString);
    if (!file.exists())
      throw new AgentConfigurationError("agent.err.file.not.found", new String[] { paramString }); 
    if (!file.canRead())
      throw new AgentConfigurationError("agent.err.file.not.readable", new String[] { paramString }); 
    FileSystem fileSystem = FileSystem.open();
    try {
      if (fileSystem.supportsFileSecurity(file) && !fileSystem.isAccessUserOnly(file)) {
        String str = Agent.getText("jmxremote.ConnectorBootstrap.file.readonly", new String[] { paramString });
        log.config("startRemoteConnectorServer", str);
        throw new AgentConfigurationError("agent.err.file.access.not.restricted", new String[] { paramString });
      } 
    } catch (IOException iOException) {
      throw new AgentConfigurationError("agent.err.file.read.failed", iOException, new String[] { paramString });
    } 
  }
  
  private static String getDefaultFileName(String paramString) {
    String str = File.separator;
    return System.getProperty("java.home") + str + "lib" + str + "management" + str + paramString;
  }
  
  private static SslRMIServerSocketFactory createSslRMIServerSocketFactory(String paramString1, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean, String paramString2) {
    if (paramString1 == null)
      return new HostAwareSslSocketFactory(paramArrayOfString1, paramArrayOfString2, paramBoolean, paramString2, null); 
    checkRestrictedFile(paramString1);
    try {
      Properties properties = new Properties();
      try (FileInputStream null = new FileInputStream(paramString1)) {
        bufferedInputStream = new BufferedInputStream(fileInputStream);
        properties.load(bufferedInputStream);
      } 
      String str1 = properties.getProperty("javax.net.ssl.keyStore");
      String str2 = properties.getProperty("javax.net.ssl.keyStorePassword", "");
      String str3 = properties.getProperty("javax.net.ssl.trustStore");
      String str4 = properties.getProperty("javax.net.ssl.trustStorePassword", "");
      char[] arrayOfChar1 = null;
      if (str2.length() != 0)
        arrayOfChar1 = str2.toCharArray(); 
      char[] arrayOfChar2 = null;
      if (str4.length() != 0)
        arrayOfChar2 = str4.toCharArray(); 
      KeyStore keyStore1 = null;
      if (str1 != null) {
        keyStore1 = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream null = new FileInputStream(str1)) {
          keyStore1.load(fileInputStream1, arrayOfChar1);
        } 
      } 
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore1, arrayOfChar1);
      KeyStore keyStore2 = null;
      if (str3 != null) {
        keyStore2 = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream null = new FileInputStream(str3)) {
          keyStore2.load(fileInputStream1, arrayOfChar2);
        } 
      } 
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore2);
      SSLContext sSLContext = SSLContext.getInstance("SSL");
      sSLContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
      return new HostAwareSslSocketFactory(sSLContext, paramArrayOfString1, paramArrayOfString2, paramBoolean, paramString2, null);
    } catch (Exception exception) {
      throw new AgentConfigurationError("agent.err.exception", exception, new String[] { exception.toString() });
    } 
  }
  
  private static JMXConnectorServerData exportMBeanServer(MBeanServer paramMBeanServer, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, boolean paramBoolean4, String paramString2, String paramString3, String paramString4, String paramString5) throws IOException, MalformedURLException {
    System.setProperty("java.rmi.server.randomIDs", "true");
    JMXServiceURL jMXServiceURL1 = new JMXServiceURL("rmi", paramString5, paramInt2);
    HashMap hashMap = new HashMap();
    PermanentExporter permanentExporter = new PermanentExporter(null);
    hashMap.put("com.sun.jmx.remote.rmi.exporter", permanentExporter);
    hashMap.put("jmx.remote.rmi.server.credential.types", new String[] { String[].class.getName(), String.class.getName() });
    boolean bool = (paramString5 != null && !paramBoolean1) ? 1 : 0;
    if (paramBoolean4) {
      if (paramString2 != null)
        hashMap.put("jmx.remote.x.login.config", paramString2); 
      if (paramString3 != null)
        hashMap.put("jmx.remote.x.password.file", paramString3); 
      hashMap.put("jmx.remote.x.access.file", paramString4);
      if (hashMap.get("jmx.remote.x.password.file") != null || hashMap.get("jmx.remote.x.login.config") != null)
        hashMap.put("jmx.remote.authenticator", new AccessFileCheckerAuthenticator(hashMap)); 
    } 
    SslRMIClientSocketFactory sslRMIClientSocketFactory = null;
    HostAwareSocketFactory hostAwareSocketFactory = null;
    if (paramBoolean1 || paramBoolean2) {
      sslRMIClientSocketFactory = new SslRMIClientSocketFactory();
      hostAwareSocketFactory = createSslRMIServerSocketFactory(paramString1, paramArrayOfString1, paramArrayOfString2, paramBoolean3, paramString5);
    } 
    if (paramBoolean1) {
      hashMap.put("jmx.remote.rmi.client.socket.factory", sslRMIClientSocketFactory);
      hashMap.put("jmx.remote.rmi.server.socket.factory", hostAwareSocketFactory);
    } 
    if (bool) {
      hostAwareSocketFactory = new HostAwareSocketFactory(paramString5, null);
      hashMap.put("jmx.remote.rmi.server.socket.factory", hostAwareSocketFactory);
    } 
    JMXConnectorServer jMXConnectorServer = null;
    try {
      jMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jMXServiceURL1, hashMap, paramMBeanServer);
      jMXConnectorServer.start();
    } catch (IOException iOException) {
      if (jMXConnectorServer == null || jMXConnectorServer.getAddress() == null)
        throw new AgentConfigurationError("agent.err.connector.server.io.error", iOException, new String[] { jMXServiceURL1.toString() }); 
      throw new AgentConfigurationError("agent.err.connector.server.io.error", iOException, new String[] { jMXConnectorServer.getAddress().toString() });
    } 
    if (paramBoolean2) {
      registry = new SingleEntryRegistry(paramInt1, sslRMIClientSocketFactory, hostAwareSocketFactory, "jmxrmi", permanentExporter.firstExported);
    } else if (bool) {
      registry = new SingleEntryRegistry(paramInt1, sslRMIClientSocketFactory, hostAwareSocketFactory, "jmxrmi", permanentExporter.firstExported);
    } else {
      registry = new SingleEntryRegistry(paramInt1, "jmxrmi", permanentExporter.firstExported);
    } 
    int i = ((UnicastRef)((RemoteObject)registry).getRef()).getLiveRef().getPort();
    String str = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", new Object[] { jMXServiceURL1.getHost(), Integer.valueOf(i) });
    JMXServiceURL jMXServiceURL2 = new JMXServiceURL(str);
    return new JMXConnectorServerData(jMXConnectorServer, jMXServiceURL2);
  }
  
  private static class AccessFileCheckerAuthenticator implements JMXAuthenticator {
    private final Map<String, Object> environment;
    
    private final Properties properties;
    
    private final String accessFile;
    
    public AccessFileCheckerAuthenticator(Map<String, Object> param1Map) throws IOException {
      this.environment = param1Map;
      this.accessFile = (String)param1Map.get("jmx.remote.x.access.file");
      this.properties = propertiesFromFile(this.accessFile);
    }
    
    public Subject authenticate(Object param1Object) {
      JMXPluggableAuthenticator jMXPluggableAuthenticator = new JMXPluggableAuthenticator(this.environment);
      Subject subject = jMXPluggableAuthenticator.authenticate(param1Object);
      checkAccessFileEntries(subject);
      return subject;
    }
    
    private void checkAccessFileEntries(Subject param1Subject) {
      if (param1Subject == null)
        throw new SecurityException("Access denied! No matching entries found in the access file [" + this.accessFile + "] as the authenticated Subject is null"); 
      Set set = param1Subject.getPrincipals();
      for (Principal principal : set) {
        if (this.properties.containsKey(principal.getName()))
          return; 
      } 
      HashSet hashSet = new HashSet();
      for (Principal principal : set)
        hashSet.add(principal.getName()); 
      throw new SecurityException("Access denied! No entries found in the access file [" + this.accessFile + "] for any of the authenticated identities " + hashSet);
    }
    
    private static Properties propertiesFromFile(String param1String) throws IOException {
      Properties properties1 = new Properties();
      if (param1String == null)
        return properties1; 
      try (FileInputStream null = new FileInputStream(param1String)) {
        properties1.load(fileInputStream);
      } 
      return properties1;
    }
  }
  
  public static interface DefaultValues {
    public static final String PORT = "0";
    
    public static final String CONFIG_FILE_NAME = "management.properties";
    
    public static final String USE_SSL = "true";
    
    public static final String USE_LOCAL_ONLY = "true";
    
    public static final String USE_REGISTRY_SSL = "false";
    
    public static final String USE_AUTHENTICATION = "true";
    
    public static final String PASSWORD_FILE_NAME = "jmxremote.password";
    
    public static final String ACCESS_FILE_NAME = "jmxremote.access";
    
    public static final String SSL_NEED_CLIENT_AUTH = "false";
  }
  
  private static class HostAwareSocketFactory implements RMIServerSocketFactory {
    private final String bindAddress;
    
    private HostAwareSocketFactory(String param1String) { this.bindAddress = param1String; }
    
    public ServerSocket createServerSocket(int param1Int) throws IOException {
      if (this.bindAddress == null)
        return new ServerSocket(param1Int); 
      try {
        InetAddress inetAddress = InetAddress.getByName(this.bindAddress);
        return new ServerSocket(param1Int, 0, inetAddress);
      } catch (UnknownHostException unknownHostException) {
        return new ServerSocket(param1Int);
      } 
    }
  }
  
  private static class HostAwareSslSocketFactory extends SslRMIServerSocketFactory {
    private final String bindAddress;
    
    private final String[] enabledCipherSuites;
    
    private final String[] enabledProtocols;
    
    private final boolean needClientAuth;
    
    private final SSLContext context;
    
    private HostAwareSslSocketFactory(String[] param1ArrayOfString1, String[] param1ArrayOfString2, boolean param1Boolean, String param1String) throws IllegalArgumentException { this(null, param1ArrayOfString1, param1ArrayOfString2, param1Boolean, param1String); }
    
    private HostAwareSslSocketFactory(SSLContext param1SSLContext, String[] param1ArrayOfString1, String[] param1ArrayOfString2, boolean param1Boolean, String param1String) throws IllegalArgumentException {
      this.context = param1SSLContext;
      this.bindAddress = param1String;
      this.enabledProtocols = param1ArrayOfString2;
      this.enabledCipherSuites = param1ArrayOfString1;
      this.needClientAuth = param1Boolean;
      checkValues(param1SSLContext, param1ArrayOfString1, param1ArrayOfString2);
    }
    
    public ServerSocket createServerSocket(int param1Int) throws IOException {
      if (this.bindAddress != null)
        try {
          InetAddress inetAddress = InetAddress.getByName(this.bindAddress);
          return new ConnectorBootstrap.SslServerSocket(param1Int, 0, inetAddress, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth, null);
        } catch (UnknownHostException unknownHostException) {
          return new ConnectorBootstrap.SslServerSocket(param1Int, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth, null);
        }  
      return new ConnectorBootstrap.SslServerSocket(param1Int, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth, null);
    }
    
    private static void checkValues(SSLContext param1SSLContext, String[] param1ArrayOfString1, String[] param1ArrayOfString2) throws IllegalArgumentException {
      SSLSocketFactory sSLSocketFactory = (param1SSLContext == null) ? (SSLSocketFactory)SSLSocketFactory.getDefault() : param1SSLContext.getSocketFactory();
      SSLSocket sSLSocket = null;
      if (param1ArrayOfString1 != null || param1ArrayOfString2 != null)
        try {
          sSLSocket = (SSLSocket)sSLSocketFactory.createSocket();
        } catch (Exception exception) {
          throw (IllegalArgumentException)(new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported")).initCause(exception);
        }  
      if (param1ArrayOfString1 != null)
        sSLSocket.setEnabledCipherSuites(param1ArrayOfString1); 
      if (param1ArrayOfString2 != null)
        sSLSocket.setEnabledProtocols(param1ArrayOfString2); 
    }
  }
  
  private static class JMXConnectorServerData {
    JMXConnectorServer jmxConnectorServer;
    
    JMXServiceURL jmxRemoteURL;
    
    public JMXConnectorServerData(JMXConnectorServer param1JMXConnectorServer, JMXServiceURL param1JMXServiceURL) {
      this.jmxConnectorServer = param1JMXConnectorServer;
      this.jmxRemoteURL = param1JMXServiceURL;
    }
  }
  
  private static class PermanentExporter implements RMIExporter {
    Remote firstExported;
    
    private PermanentExporter() {}
    
    public Remote exportObject(Remote param1Remote, int param1Int, RMIClientSocketFactory param1RMIClientSocketFactory, RMIServerSocketFactory param1RMIServerSocketFactory) throws RemoteException {
      UnicastServerRef2 unicastServerRef2;
      synchronized (this) {
        if (this.firstExported == null)
          this.firstExported = param1Remote; 
      } 
      if (param1RMIClientSocketFactory == null && param1RMIServerSocketFactory == null) {
        unicastServerRef2 = new UnicastServerRef(param1Int);
      } else {
        unicastServerRef2 = new UnicastServerRef2(param1Int, param1RMIClientSocketFactory, param1RMIServerSocketFactory);
      } 
      return unicastServerRef2.exportObject(param1Remote, null, true);
    }
    
    public boolean unexportObject(Remote param1Remote, boolean param1Boolean) throws NoSuchObjectException { return UnicastRemoteObject.unexportObject(param1Remote, param1Boolean); }
  }
  
  public static interface PropertyNames {
    public static final String PORT = "com.sun.management.jmxremote.port";
    
    public static final String HOST = "com.sun.management.jmxremote.host";
    
    public static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
    
    public static final String CONFIG_FILE_NAME = "com.sun.management.config.file";
    
    public static final String USE_LOCAL_ONLY = "com.sun.management.jmxremote.local.only";
    
    public static final String USE_SSL = "com.sun.management.jmxremote.ssl";
    
    public static final String USE_REGISTRY_SSL = "com.sun.management.jmxremote.registry.ssl";
    
    public static final String USE_AUTHENTICATION = "com.sun.management.jmxremote.authenticate";
    
    public static final String PASSWORD_FILE_NAME = "com.sun.management.jmxremote.password.file";
    
    public static final String ACCESS_FILE_NAME = "com.sun.management.jmxremote.access.file";
    
    public static final String LOGIN_CONFIG_NAME = "com.sun.management.jmxremote.login.config";
    
    public static final String SSL_ENABLED_CIPHER_SUITES = "com.sun.management.jmxremote.ssl.enabled.cipher.suites";
    
    public static final String SSL_ENABLED_PROTOCOLS = "com.sun.management.jmxremote.ssl.enabled.protocols";
    
    public static final String SSL_NEED_CLIENT_AUTH = "com.sun.management.jmxremote.ssl.need.client.auth";
    
    public static final String SSL_CONFIG_FILE_NAME = "com.sun.management.jmxremote.ssl.config.file";
  }
  
  private static class SslServerSocket extends ServerSocket {
    private static SSLSocketFactory defaultSSLSocketFactory;
    
    private final String[] enabledCipherSuites;
    
    private final String[] enabledProtocols;
    
    private final boolean needClientAuth;
    
    private final SSLContext context;
    
    private SslServerSocket(int param1Int, SSLContext param1SSLContext, String[] param1ArrayOfString1, String[] param1ArrayOfString2, boolean param1Boolean) throws IOException {
      super(param1Int);
      this.enabledProtocols = param1ArrayOfString2;
      this.enabledCipherSuites = param1ArrayOfString1;
      this.needClientAuth = param1Boolean;
      this.context = param1SSLContext;
    }
    
    private SslServerSocket(int param1Int1, int param1Int2, InetAddress param1InetAddress, SSLContext param1SSLContext, String[] param1ArrayOfString1, String[] param1ArrayOfString2, boolean param1Boolean) throws IOException {
      super(param1Int1, param1Int2, param1InetAddress);
      this.enabledProtocols = param1ArrayOfString2;
      this.enabledCipherSuites = param1ArrayOfString1;
      this.needClientAuth = param1Boolean;
      this.context = param1SSLContext;
    }
    
    public Socket accept() throws IOException {
      SSLSocketFactory sSLSocketFactory = (this.context == null) ? getDefaultSSLSocketFactory() : this.context.getSocketFactory();
      Socket socket = super.accept();
      SSLSocket sSLSocket = (SSLSocket)sSLSocketFactory.createSocket(socket, socket.getInetAddress().getHostName(), socket.getPort(), true);
      sSLSocket.setUseClientMode(false);
      if (this.enabledCipherSuites != null)
        sSLSocket.setEnabledCipherSuites(this.enabledCipherSuites); 
      if (this.enabledProtocols != null)
        sSLSocket.setEnabledProtocols(this.enabledProtocols); 
      sSLSocket.setNeedClientAuth(this.needClientAuth);
      return sSLSocket;
    }
    
    private static SSLSocketFactory getDefaultSSLSocketFactory() {
      if (defaultSSLSocketFactory == null) {
        defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        return defaultSSLSocketFactory;
      } 
      return defaultSSLSocketFactory;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\jmxremote\ConnectorBootstrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */