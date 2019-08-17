package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.ORBAlreadyRegistered;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.Server;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.omg.CORBA.SystemException;

public class ServerTableEntry {
  private static final int DE_ACTIVATED = 0;
  
  private static final int ACTIVATING = 1;
  
  private static final int ACTIVATED = 2;
  
  private static final int RUNNING = 3;
  
  private static final int HELD_DOWN = 4;
  
  private static final long waitTime = 2000L;
  
  private static final int ActivationRetryMax = 5;
  
  private int state;
  
  private int serverId;
  
  private HashMap orbAndPortInfo;
  
  private Server serverObj;
  
  private ServerDef serverDef;
  
  private Process process;
  
  private int activateRetryCount = 0;
  
  private String activationCmd;
  
  private ActivationSystemException wrapper;
  
  private static String javaHome = System.getProperty("java.home");
  
  private static String classPath = System.getProperty("java.class.path");
  
  private static String fileSep = System.getProperty("file.separator");
  
  private static String pathSep = System.getProperty("path.separator");
  
  private boolean debug = false;
  
  private String printState() {
    String str = "UNKNOWN";
    switch (this.state) {
      case 0:
        str = "DE_ACTIVATED";
        break;
      case 1:
        str = "ACTIVATING  ";
        break;
      case 2:
        str = "ACTIVATED   ";
        break;
      case 3:
        str = "RUNNING     ";
        break;
      case 4:
        str = "HELD_DOWN   ";
        break;
    } 
    return str;
  }
  
  public String toString() { return "ServerTableEntry[state=" + printState() + " serverId=" + this.serverId + " activateRetryCount=" + this.activateRetryCount + "]"; }
  
  ServerTableEntry(ActivationSystemException paramActivationSystemException, int paramInt1, ServerDef paramServerDef, int paramInt2, String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    this.wrapper = paramActivationSystemException;
    this.serverId = paramInt1;
    this.serverDef = paramServerDef;
    this.debug = paramBoolean2;
    this.orbAndPortInfo = new HashMap(255);
    this.activateRetryCount = 0;
    this.state = 1;
    this.activationCmd = javaHome + fileSep + "bin" + fileSep + "java " + paramServerDef.serverVmArgs + " -Dioser=" + System.getProperty("ioser") + " -D" + "org.omg.CORBA.ORBInitialPort" + "=" + paramInt2 + " -D" + "com.sun.CORBA.activation.DbDir" + "=" + paramString + " -D" + "com.sun.CORBA.POA.ORBActivated" + "=true -D" + "com.sun.CORBA.POA.ORBServerId" + "=" + paramInt1 + " -D" + "com.sun.CORBA.POA.ORBServerName" + "=" + paramServerDef.serverName + " " + (paramBoolean1 ? "-Dcom.sun.CORBA.activation.ORBServerVerify=true " : "") + "-classpath " + classPath + ((paramServerDef.serverClassPath.equals("") == true) ? "" : pathSep) + paramServerDef.serverClassPath + " com.sun.corba.se.impl.activation.ServerMain " + paramServerDef.serverArgs + (paramBoolean2 ? " -debug" : "");
    if (paramBoolean2)
      System.out.println("ServerTableEntry constructed with activation command " + this.activationCmd); 
  }
  
  public int verify() {
    try {
      if (this.debug)
        System.out.println("Server being verified w/" + this.activationCmd); 
      this.process = Runtime.getRuntime().exec(this.activationCmd);
      int i = this.process.waitFor();
      if (this.debug)
        printDebug("verify", "returns " + ServerMain.printResult(i)); 
      return i;
    } catch (Exception exception) {
      if (this.debug)
        printDebug("verify", "returns unknown error because of exception " + exception); 
      return 4;
    } 
  }
  
  private void printDebug(String paramString1, String paramString2) {
    System.out.println("ServerTableEntry: method  =" + paramString1);
    System.out.println("ServerTableEntry: server  =" + this.serverId);
    System.out.println("ServerTableEntry: state   =" + printState());
    System.out.println("ServerTableEntry: message =" + paramString2);
    System.out.println();
  }
  
  void activate() throws SystemException {
    this.state = 2;
    try {
      if (this.debug)
        printDebug("activate", "activating server"); 
      this.process = Runtime.getRuntime().exec(this.activationCmd);
    } catch (Exception exception) {
      deActivate();
      if (this.debug)
        printDebug("activate", "throwing premature process exit"); 
      throw this.wrapper.unableToStartProcess();
    } 
  }
  
  void register(Server paramServer) {
    if (this.state == 2) {
      this.serverObj = paramServer;
      if (this.debug)
        printDebug("register", "process registered back"); 
    } else {
      if (this.debug)
        printDebug("register", "throwing premature process exit"); 
      throw this.wrapper.serverNotExpectedToRegister();
    } 
  }
  
  void registerPorts(String paramString, EndPointInfo[] paramArrayOfEndPointInfo) throws ORBAlreadyRegistered {
    if (this.orbAndPortInfo.containsKey(paramString))
      throw new ORBAlreadyRegistered(paramString); 
    int i = paramArrayOfEndPointInfo.length;
    EndPointInfo[] arrayOfEndPointInfo = new EndPointInfo[i];
    for (byte b = 0; b < i; b++) {
      arrayOfEndPointInfo[b] = new EndPointInfo((paramArrayOfEndPointInfo[b]).endpointType, (paramArrayOfEndPointInfo[b]).port);
      if (this.debug)
        System.out.println("registering type: " + (arrayOfEndPointInfo[b]).endpointType + "  port  " + (arrayOfEndPointInfo[b]).port); 
    } 
    this.orbAndPortInfo.put(paramString, arrayOfEndPointInfo);
    if (this.state == 2) {
      this.state = 3;
      notifyAll();
    } 
    if (this.debug)
      printDebug("registerPorts", "process registered Ports"); 
  }
  
  void install() throws SystemException {
    Server server = null;
    synchronized (this) {
      if (this.state == 3) {
        server = this.serverObj;
      } else {
        throw this.wrapper.serverNotRunning();
      } 
    } 
    if (server != null)
      server.install(); 
  }
  
  void uninstall() throws SystemException {
    Server server = null;
    Process process1 = null;
    synchronized (this) {
      server = this.serverObj;
      process1 = this.process;
      if (this.state == 3) {
        deActivate();
      } else {
        throw this.wrapper.serverNotRunning();
      } 
    } 
    try {
      if (server != null) {
        server.shutdown();
        server.uninstall();
      } 
      if (process1 != null)
        process1.destroy(); 
    } catch (Exception exception) {}
  }
  
  void holdDown() throws SystemException {
    this.state = 4;
    if (this.debug)
      printDebug("holdDown", "server held down"); 
    notifyAll();
  }
  
  void deActivate() throws SystemException {
    this.state = 0;
    if (this.debug)
      printDebug("deActivate", "server deactivated"); 
    notifyAll();
  }
  
  void checkProcessHealth() throws SystemException {
    if (this.state == 3) {
      try {
        int i = this.process.exitValue();
      } catch (IllegalThreadStateException illegalThreadStateException) {
        return;
      } 
      synchronized (this) {
        this.orbAndPortInfo.clear();
        deActivate();
      } 
    } 
  }
  
  boolean isValid() {
    if (this.state == 1 || this.state == 4) {
      if (this.debug)
        printDebug("isValid", "returns true"); 
      return true;
    } 
    try {
      int i = this.process.exitValue();
    } catch (IllegalThreadStateException illegalThreadStateException) {
      return true;
    } 
    if (this.state == 2) {
      if (this.activateRetryCount < 5) {
        if (this.debug)
          printDebug("isValid", "reactivating server"); 
        this.activateRetryCount++;
        activate();
        return true;
      } 
      if (this.debug)
        printDebug("isValid", "holding server down"); 
      holdDown();
      return true;
    } 
    deActivate();
    return false;
  }
  
  ORBPortInfo[] lookup(String paramString) throws ServerHeldDown {
    while (this.state == 1 || this.state == 2) {
      try {
        wait(2000L);
        if (!isValid())
          break; 
      } catch (Exception exception) {}
    } 
    ORBPortInfo[] arrayOfORBPortInfo = null;
    if (this.state == 3) {
      arrayOfORBPortInfo = new ORBPortInfo[this.orbAndPortInfo.size()];
      Iterator iterator = this.orbAndPortInfo.keySet().iterator();
      try {
        for (byte b = 0; iterator.hasNext(); b++) {
          String str = (String)iterator.next();
          EndPointInfo[] arrayOfEndPointInfo = (EndPointInfo[])this.orbAndPortInfo.get(str);
          int i = -1;
          for (byte b1 = 0; b1 < arrayOfEndPointInfo.length; b1++) {
            if (this.debug)
              System.out.println("lookup num-ports " + arrayOfEndPointInfo.length + "   " + (arrayOfEndPointInfo[b1]).endpointType + "   " + (arrayOfEndPointInfo[b1]).port); 
            if ((arrayOfEndPointInfo[b1]).endpointType.equals(paramString)) {
              i = (arrayOfEndPointInfo[b1]).port;
              break;
            } 
          } 
          arrayOfORBPortInfo[b] = new ORBPortInfo(str, i);
        } 
      } catch (NoSuchElementException noSuchElementException) {}
      return arrayOfORBPortInfo;
    } 
    if (this.debug)
      printDebug("lookup", "throwing server held down error"); 
    throw new ServerHeldDown(this.serverId);
  }
  
  EndPointInfo[] lookupForORB(String paramString) throws ServerHeldDown, InvalidORBid {
    while (this.state == 1 || this.state == 2) {
      try {
        wait(2000L);
        if (!isValid())
          break; 
      } catch (Exception exception) {}
    } 
    EndPointInfo[] arrayOfEndPointInfo = null;
    if (this.state == 3) {
      try {
        EndPointInfo[] arrayOfEndPointInfo1 = (EndPointInfo[])this.orbAndPortInfo.get(paramString);
        arrayOfEndPointInfo = new EndPointInfo[arrayOfEndPointInfo1.length];
        for (byte b = 0; b < arrayOfEndPointInfo1.length; b++) {
          if (this.debug)
            System.out.println("lookup num-ports " + arrayOfEndPointInfo1.length + "   " + (arrayOfEndPointInfo1[b]).endpointType + "   " + (arrayOfEndPointInfo1[b]).port); 
          arrayOfEndPointInfo[b] = new EndPointInfo((arrayOfEndPointInfo1[b]).endpointType, (arrayOfEndPointInfo1[b]).port);
        } 
      } catch (NoSuchElementException noSuchElementException) {
        throw new InvalidORBid();
      } 
      return arrayOfEndPointInfo;
    } 
    if (this.debug)
      printDebug("lookup", "throwing server held down error"); 
    throw new ServerHeldDown(this.serverId);
  }
  
  String[] getORBList() {
    String[] arrayOfString = new String[this.orbAndPortInfo.size()];
    Iterator iterator = this.orbAndPortInfo.keySet().iterator();
    try {
      byte b = 0;
      while (iterator.hasNext()) {
        String str = (String)iterator.next();
        arrayOfString[b++] = str;
      } 
    } catch (NoSuchElementException noSuchElementException) {}
    return arrayOfString;
  }
  
  int getServerId() { return this.serverId; }
  
  boolean isActive() { return (this.state == 3 || this.state == 2); }
  
  void destroy() throws SystemException {
    Server server = null;
    Process process1 = null;
    synchronized (this) {
      server = this.serverObj;
      process1 = this.process;
      deActivate();
    } 
    try {
      if (server != null)
        server.shutdown(); 
      if (this.debug)
        printDebug("destroy", "server shutdown successfully"); 
    } catch (Exception exception) {
      if (this.debug)
        printDebug("destroy", "server shutdown threw exception" + exception); 
    } 
    try {
      if (process1 != null)
        process1.destroy(); 
      if (this.debug)
        printDebug("destroy", "process destroyed successfully"); 
    } catch (Exception exception) {
      if (this.debug)
        printDebug("destroy", "process destroy threw exception" + exception); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ServerTableEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */