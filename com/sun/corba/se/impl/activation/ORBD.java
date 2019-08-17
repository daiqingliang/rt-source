package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.legacy.connection.SocketFactoryAcceptorImpl;
import com.sun.corba.se.impl.naming.cosnaming.TransientNameService;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.util.Properties;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INTERNAL;

public class ORBD {
  private int initSvcPort;
  
  protected File dbDir;
  
  private String dbDirName;
  
  protected Locator locator;
  
  protected Activator activator;
  
  protected RepositoryImpl repository;
  
  private static String[][] orbServers = { { "" } };
  
  protected void initializeBootNaming(ORB paramORB) {
    SocketFactoryAcceptorImpl socketFactoryAcceptorImpl;
    this.initSvcPort = paramORB.getORBData().getORBInitialPort();
    if (paramORB.getORBData().getLegacySocketFactory() == null) {
      socketFactoryAcceptorImpl = new SocketOrChannelAcceptorImpl(paramORB, this.initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
    } else {
      socketFactoryAcceptorImpl = new SocketFactoryAcceptorImpl(paramORB, this.initSvcPort, "BOOT_NAMING", "IIOP_CLEAR_TEXT");
    } 
    paramORB.getCorbaTransportManager().registerAcceptor(socketFactoryAcceptorImpl);
  }
  
  protected ORB createORB(String[] paramArrayOfString) {
    Properties properties = System.getProperties();
    properties.put("com.sun.CORBA.POA.ORBServerId", "1000");
    properties.put("com.sun.CORBA.POA.ORBPersistentServerPort", properties.getProperty("com.sun.CORBA.activation.Port", Integer.toString(1049)));
    properties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
    return (ORB)ORB.init(paramArrayOfString, properties);
  }
  
  private void run(String[] paramArrayOfString) {
    try {
      processArgs(paramArrayOfString);
      ORB oRB = createORB(paramArrayOfString);
      if (oRB.orbdDebugFlag)
        System.out.println("ORBD begins initialization."); 
      boolean bool = createSystemDirs("orb.db");
      startActivationObjects(oRB);
      if (bool)
        installOrbServers(getRepository(), getActivator()); 
      if (oRB.orbdDebugFlag) {
        System.out.println("ORBD is ready.");
        System.out.println("ORBD serverid: " + System.getProperty("com.sun.CORBA.POA.ORBServerId"));
        System.out.println("activation dbdir: " + System.getProperty("com.sun.CORBA.activation.DbDir"));
        System.out.println("activation port: " + System.getProperty("com.sun.CORBA.activation.Port"));
        String str1 = System.getProperty("com.sun.CORBA.activation.ServerPollingTime");
        if (str1 == null)
          str1 = Integer.toString(1000); 
        System.out.println("activation Server Polling Time: " + str1 + " milli-seconds ");
        String str2 = System.getProperty("com.sun.CORBA.activation.ServerStartupDelay");
        if (str2 == null)
          str2 = Integer.toString(1000); 
        System.out.println("activation Server Startup Delay: " + str2 + " milli-seconds ");
      } 
      NameServiceStartThread nameServiceStartThread = new NameServiceStartThread(oRB, this.dbDir);
      nameServiceStartThread.start();
      oRB.run();
    } catch (COMM_FAILURE cOMM_FAILURE) {
      System.out.println(CorbaResourceUtil.getText("orbd.commfailure"));
      System.out.println(cOMM_FAILURE);
      cOMM_FAILURE.printStackTrace();
    } catch (INTERNAL iNTERNAL) {
      System.out.println(CorbaResourceUtil.getText("orbd.internalexception"));
      System.out.println(iNTERNAL);
      iNTERNAL.printStackTrace();
    } catch (Exception exception) {
      System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
      System.out.println(exception);
      exception.printStackTrace();
    } 
  }
  
  private void processArgs(String[] paramArrayOfString) {
    Properties properties = System.getProperties();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].equals("-port")) {
        if (b + true < paramArrayOfString.length) {
          properties.put("com.sun.CORBA.activation.Port", paramArrayOfString[++b]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        } 
      } else if (paramArrayOfString[b].equals("-defaultdb")) {
        if (b + 1 < paramArrayOfString.length) {
          properties.put("com.sun.CORBA.activation.DbDir", paramArrayOfString[++b]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        } 
      } else if (paramArrayOfString[b].equals("-serverid")) {
        if (b + 1 < paramArrayOfString.length) {
          properties.put("com.sun.CORBA.POA.ORBServerId", paramArrayOfString[++b]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        } 
      } else if (paramArrayOfString[b].equals("-serverPollingTime")) {
        if (b + 1 < paramArrayOfString.length) {
          properties.put("com.sun.CORBA.activation.ServerPollingTime", paramArrayOfString[++b]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        } 
      } else if (paramArrayOfString[b].equals("-serverStartupDelay")) {
        if (b + 1 < paramArrayOfString.length) {
          properties.put("com.sun.CORBA.activation.ServerStartupDelay", paramArrayOfString[++b]);
        } else {
          System.out.println(CorbaResourceUtil.getText("orbd.usage", "orbd"));
        } 
      } 
    } 
  }
  
  protected boolean createSystemDirs(String paramString) {
    boolean bool = false;
    Properties properties = System.getProperties();
    String str = properties.getProperty("file.separator");
    this.dbDir = new File(properties.getProperty("com.sun.CORBA.activation.DbDir", properties.getProperty("user.dir") + str + paramString));
    this.dbDirName = this.dbDir.getAbsolutePath();
    properties.put("com.sun.CORBA.activation.DbDir", this.dbDirName);
    if (!this.dbDir.exists()) {
      this.dbDir.mkdir();
      bool = true;
    } 
    File file = new File(this.dbDir, "logs");
    if (!file.exists())
      file.mkdir(); 
    return bool;
  }
  
  protected File getDbDir() { return this.dbDir; }
  
  protected String getDbDirName() { return this.dbDirName; }
  
  protected void startActivationObjects(ORB paramORB) {
    initializeBootNaming(paramORB);
    this.repository = new RepositoryImpl(paramORB, this.dbDir, paramORB.orbdDebugFlag);
    paramORB.register_initial_reference("ServerRepository", this.repository);
    ServerManagerImpl serverManagerImpl = new ServerManagerImpl(paramORB, paramORB.getCorbaTransportManager(), this.repository, getDbDirName(), paramORB.orbdDebugFlag);
    this.locator = LocatorHelper.narrow(serverManagerImpl);
    paramORB.register_initial_reference("ServerLocator", this.locator);
    this.activator = ActivatorHelper.narrow(serverManagerImpl);
    paramORB.register_initial_reference("ServerActivator", this.activator);
    TransientNameService transientNameService = new TransientNameService(paramORB, "TNameService");
  }
  
  protected Locator getLocator() { return this.locator; }
  
  protected Activator getActivator() { return this.activator; }
  
  protected RepositoryImpl getRepository() { return this.repository; }
  
  protected void installOrbServers(RepositoryImpl paramRepositoryImpl, Activator paramActivator) {
    for (byte b = 0; b < orbServers.length; b++) {
      try {
        String[] arrayOfString = orbServers[b];
        ServerDef serverDef = new ServerDef(arrayOfString[1], arrayOfString[2], arrayOfString[3], arrayOfString[4], arrayOfString[5]);
        int i = Integer.valueOf(orbServers[b][0]).intValue();
        paramRepositoryImpl.registerServer(serverDef, i);
        paramActivator.activate(i);
      } catch (Exception exception) {}
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    ORBD oRBD = new ORBD();
    oRBD.run(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ORBD.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */