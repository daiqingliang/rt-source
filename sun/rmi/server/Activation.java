package sun.rmi.server;

import com.sun.rmi.rmid.ExecOptionPermission;
import com.sun.rmi.rmid.ExecPermission;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.Channel;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Files;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationInstantiator;
import java.rmi.activation.ActivationMonitor;
import java.rmi.activation.ActivationSystem;
import java.rmi.activation.Activator;
import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.UnknownObjectException;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import sun.rmi.log.LogHandler;
import sun.rmi.log.ReliableLog;
import sun.rmi.registry.RegistryImpl;
import sun.rmi.transport.LiveRef;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;

public class Activation implements Serializable {
  private static final long serialVersionUID = 2921265612698155191L;
  
  private static final byte MAJOR_VERSION = 1;
  
  private static final byte MINOR_VERSION = 0;
  
  private static Object execPolicy;
  
  private static Method execPolicyMethod;
  
  private static boolean debugExec;
  
  private Map<ActivationID, ActivationGroupID> idTable = new ConcurrentHashMap();
  
  private Map<ActivationGroupID, GroupEntry> groupTable = new ConcurrentHashMap();
  
  private byte majorVersion = 1;
  
  private byte minorVersion = 0;
  
  private int groupSemaphore;
  
  private int groupCounter;
  
  private ReliableLog log;
  
  private int numUpdates;
  
  private String[] command;
  
  private static final long groupTimeout = getInt("sun.rmi.activation.groupTimeout", 60000);
  
  private static final int snapshotInterval = getInt("sun.rmi.activation.snapshotInterval", 200);
  
  private static final long execTimeout = getInt("sun.rmi.activation.execTimeout", 30000);
  
  private static final Object initLock = new Object();
  
  private static boolean initDone = false;
  
  private Activator activator;
  
  private Activator activatorStub;
  
  private ActivationSystem system;
  
  private ActivationSystem systemStub;
  
  private ActivationMonitor monitor;
  
  private Registry registry;
  
  private Thread shutdownHook;
  
  private static ResourceBundle resources = null;
  
  private static int getInt(String paramString, int paramInt) { return ((Integer)AccessController.doPrivileged(new GetIntegerAction(paramString, paramInt))).intValue(); }
  
  private Activation() {}
  
  private static void startActivation(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, String paramString, String[] paramArrayOfString) throws Exception {
    ReliableLog reliableLog = new ReliableLog(paramString, new ActLogHandler());
    Activation activation = (Activation)reliableLog.recover();
    activation.init(paramInt, paramRMIServerSocketFactory, reliableLog, paramArrayOfString);
  }
  
  private void init(int paramInt, RMIServerSocketFactory paramRMIServerSocketFactory, ReliableLog paramReliableLog, String[] paramArrayOfString) throws Exception {
    this.log = paramReliableLog;
    this.numUpdates = 0;
    this.shutdownHook = new ShutdownHook();
    this.groupSemaphore = getInt("sun.rmi.activation.groupThrottle", 3);
    this.groupCounter = 0;
    Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    ActivationGroupID[] arrayOfActivationGroupID = (ActivationGroupID[])this.groupTable.keySet().toArray(new ActivationGroupID[0]);
    synchronized (this.startupLock = new Object()) {
      this.activator = new ActivatorImpl(paramInt, paramRMIServerSocketFactory);
      this.activatorStub = (Activator)RemoteObject.toStub(this.activator);
      this.system = new ActivationSystemImpl(paramInt, paramRMIServerSocketFactory);
      this.systemStub = (ActivationSystem)RemoteObject.toStub(this.system);
      this.monitor = new ActivationMonitorImpl(paramInt, paramRMIServerSocketFactory);
      initCommand(paramArrayOfString);
      this.registry = new SystemRegistryImpl(paramInt, null, paramRMIServerSocketFactory, this.systemStub);
      if (paramRMIServerSocketFactory != null)
        synchronized (initLock) {
          initDone = true;
          initLock.notifyAll();
        }  
    } 
    this.startupLock = null;
    int i = arrayOfActivationGroupID.length;
    while (--i >= 0) {
      try {
        getGroupEntry(arrayOfActivationGroupID[i]).restartServices();
      } catch (UnknownGroupException unknownGroupException) {
        System.err.println(getTextResource("rmid.restart.group.warning"));
        unknownGroupException.printStackTrace();
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (!(this.groupTable instanceof ConcurrentHashMap))
      this.groupTable = new ConcurrentHashMap(this.groupTable); 
    if (!(this.idTable instanceof ConcurrentHashMap))
      this.idTable = new ConcurrentHashMap(this.idTable); 
  }
  
  private void checkShutdown() {
    Object object = this.startupLock;
    if (object != null)
      synchronized (object) {
      
      }  
    if (this.shuttingDown == true)
      throw new ActivationException("activation system shutting down"); 
  }
  
  private static void unexport(Remote paramRemote) {
    while (true) {
      try {
        if (UnicastRemoteObject.unexportObject(paramRemote, false) == true)
          break; 
        Thread.sleep(100L);
      } catch (Exception exception) {}
    } 
  }
  
  private ActivationGroupID getGroupID(ActivationID paramActivationID) throws UnknownObjectException {
    ActivationGroupID activationGroupID = (ActivationGroupID)this.idTable.get(paramActivationID);
    if (activationGroupID != null)
      return activationGroupID; 
    throw new UnknownObjectException("unknown object: " + paramActivationID);
  }
  
  private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID, boolean paramBoolean) throws UnknownGroupException {
    if (paramActivationGroupID.getClass() == ActivationGroupID.class) {
      GroupEntry groupEntry;
      if (paramBoolean) {
        groupEntry = (GroupEntry)this.groupTable.remove(paramActivationGroupID);
      } else {
        groupEntry = (GroupEntry)this.groupTable.get(paramActivationGroupID);
      } 
      if (groupEntry != null && !groupEntry.removed)
        return groupEntry; 
    } 
    throw new UnknownGroupException("group unknown");
  }
  
  private GroupEntry getGroupEntry(ActivationGroupID paramActivationGroupID) throws UnknownGroupException { return getGroupEntry(paramActivationGroupID, false); }
  
  private GroupEntry removeGroupEntry(ActivationGroupID paramActivationGroupID) throws UnknownGroupException { return getGroupEntry(paramActivationGroupID, true); }
  
  private GroupEntry getGroupEntry(ActivationID paramActivationID) throws UnknownObjectException {
    ActivationGroupID activationGroupID = getGroupID(paramActivationID);
    GroupEntry groupEntry = (GroupEntry)this.groupTable.get(activationGroupID);
    if (groupEntry != null && !groupEntry.removed)
      return groupEntry; 
    throw new UnknownObjectException("object's group removed");
  }
  
  private String[] activationArgs(ActivationGroupDesc paramActivationGroupDesc) {
    ActivationGroupDesc.CommandEnvironment commandEnvironment = paramActivationGroupDesc.getCommandEnvironment();
    ArrayList arrayList = new ArrayList();
    arrayList.add((commandEnvironment != null && commandEnvironment.getCommandPath() != null) ? commandEnvironment.getCommandPath() : this.command[0]);
    if (commandEnvironment != null && commandEnvironment.getCommandOptions() != null)
      arrayList.addAll(Arrays.asList(commandEnvironment.getCommandOptions())); 
    Properties properties = paramActivationGroupDesc.getPropertyOverrides();
    if (properties != null) {
      Enumeration enumeration = properties.propertyNames();
      while (enumeration.hasMoreElements()) {
        String str = (String)enumeration.nextElement();
        arrayList.add("-D" + str + "=" + properties.getProperty(str));
      } 
    } 
    for (byte b = 1; b < this.command.length; b++)
      arrayList.add(this.command[b]); 
    String[] arrayOfString = new String[arrayList.size()];
    System.arraycopy(arrayList.toArray(), 0, arrayOfString, 0, arrayOfString.length);
    return arrayOfString;
  }
  
  private void checkArgs(ActivationGroupDesc paramActivationGroupDesc, String[] paramArrayOfString) throws SecurityException, ActivationException {
    if (execPolicyMethod != null) {
      if (paramArrayOfString == null)
        paramArrayOfString = activationArgs(paramActivationGroupDesc); 
      try {
        execPolicyMethod.invoke(execPolicy, new Object[] { paramActivationGroupDesc, paramArrayOfString });
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        if (throwable instanceof SecurityException)
          throw (SecurityException)throwable; 
        throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", invocationTargetException);
      } catch (Exception exception) {
        throw new ActivationException(execPolicyMethod.getName() + ": unexpected exception", exception);
      } 
    } 
  }
  
  private void addLogRecord(LogRecord paramLogRecord) throws ActivationException {
    synchronized (this.log) {
      checkShutdown();
      try {
        this.log.update(paramLogRecord, true);
      } catch (Exception exception) {
        this.numUpdates = snapshotInterval;
        System.err.println(getTextResource("rmid.log.update.warning"));
        exception.printStackTrace();
      } 
      if (++this.numUpdates < snapshotInterval)
        return; 
      try {
        this.log.snapshot(this);
        this.numUpdates = 0;
      } catch (Exception exception) {
        System.err.println(getTextResource("rmid.log.snapshot.warning"));
        exception.printStackTrace();
        try {
          this.system.shutdown();
        } catch (RemoteException remoteException) {}
        throw new ActivationException("log snapshot failed", exception);
      } 
    } 
  }
  
  private void initCommand(String[] paramArrayOfString) {
    this.command = new String[paramArrayOfString.length + 2];
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              Activation.this.command[0] = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            } catch (Exception exception) {
              System.err.println(Activation.getTextResource("rmid.unfound.java.home.property"));
              Activation.this.command[0] = "java";
            } 
            return null;
          }
        });
    System.arraycopy(paramArrayOfString, 0, this.command, 1, paramArrayOfString.length);
    this.command[this.command.length - 1] = "sun.rmi.server.ActivationGroupInit";
  }
  
  private static void bomb(String paramString) {
    System.err.println("rmid: " + paramString);
    System.err.println(MessageFormat.format(getTextResource("rmid.usage"), new Object[] { "rmid" }));
    System.exit(1);
  }
  
  public static void main(String[] paramArrayOfString) {
    boolean bool = false;
    if (System.getSecurityManager() == null)
      System.setSecurityManager(new SecurityManager()); 
    try {
      int i = 1098;
      ActivationServerSocketFactory activationServerSocketFactory = null;
      Channel channel = (Channel)AccessController.doPrivileged(new PrivilegedExceptionAction<Channel>() {
            public Channel run() throws IOException { return System.inheritedChannel(); }
          });
      if (channel != null && channel instanceof ServerSocketChannel) {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() {
                File file = Files.createTempFile("rmid-err", null, new java.nio.file.attribute.FileAttribute[0]).toFile();
                PrintStream printStream = new PrintStream(new FileOutputStream(file));
                System.setErr(printStream);
                return null;
              }
            });
        ServerSocket serverSocket = ((ServerSocketChannel)channel).socket();
        i = serverSocket.getLocalPort();
        activationServerSocketFactory = new ActivationServerSocketFactory(serverSocket);
        System.err.println(new Date());
        System.err.println(getTextResource("rmid.inherited.channel.info") + ": " + channel);
      } 
      String str1 = null;
      ArrayList arrayList = new ArrayList();
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        if (paramArrayOfString[b].equals("-port")) {
          if (activationServerSocketFactory != null)
            bomb(getTextResource("rmid.syntax.port.badarg")); 
          if (b + true < paramArrayOfString.length) {
            try {
              i = Integer.parseInt(paramArrayOfString[++b]);
            } catch (NumberFormatException numberFormatException) {
              bomb(getTextResource("rmid.syntax.port.badnumber"));
            } 
          } else {
            bomb(getTextResource("rmid.syntax.port.missing"));
          } 
        } else if (paramArrayOfString[b].equals("-log")) {
          if (b + 1 < paramArrayOfString.length) {
            str1 = paramArrayOfString[++b];
          } else {
            bomb(getTextResource("rmid.syntax.log.missing"));
          } 
        } else if (paramArrayOfString[b].equals("-stop")) {
          bool = true;
        } else if (paramArrayOfString[b].startsWith("-C")) {
          arrayList.add(paramArrayOfString[b].substring(2));
        } else {
          bomb(MessageFormat.format(getTextResource("rmid.syntax.illegal.option"), new Object[] { paramArrayOfString[b] }));
        } 
      } 
      if (str1 == null)
        if (activationServerSocketFactory != null) {
          bomb(getTextResource("rmid.syntax.log.required"));
        } else {
          str1 = "log";
        }  
      debugExec = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.activation.debugExec"))).booleanValue();
      String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.activation.execPolicy", null));
      if (str2 == null) {
        if (!bool)
          DefaultExecPolicy.checkConfiguration(); 
        str2 = "default";
      } 
      if (!str2.equals("none")) {
        if (str2.equals("") || str2.equals("default"))
          str2 = DefaultExecPolicy.class.getName(); 
        try {
          Class clazz = getRMIClass(str2);
          execPolicy = clazz.newInstance();
          execPolicyMethod = clazz.getMethod("checkExecCommand", new Class[] { ActivationGroupDesc.class, String[].class });
        } catch (Exception exception) {
          if (debugExec) {
            System.err.println(getTextResource("rmid.exec.policy.exception"));
            exception.printStackTrace();
          } 
          bomb(getTextResource("rmid.exec.policy.invalid"));
        } 
      } 
      if (bool == true) {
        final int finalPort = i;
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                System.setProperty("java.rmi.activation.port", Integer.toString(finalPort));
                return null;
              }
            });
        ActivationSystem activationSystem = ActivationGroup.getSystem();
        activationSystem.shutdown();
        System.exit(0);
      } 
      startActivation(i, activationServerSocketFactory, str1, (String[])arrayList.toArray(new String[arrayList.size()]));
      while (true) {
        try {
          while (true)
            Thread.sleep(Float.MAX_VALUE); 
          break;
        } catch (InterruptedException interruptedException) {}
      } 
    } catch (Exception exception) {
      System.err.println(MessageFormat.format(getTextResource("rmid.unexpected.exception"), new Object[] { exception }));
      exception.printStackTrace();
      System.exit(1);
      return;
    } 
  }
  
  private static String getTextResource(String paramString) {
    if (resources == null) {
      try {
        resources = ResourceBundle.getBundle("sun.rmi.server.resources.rmid");
      } catch (MissingResourceException missingResourceException) {}
      if (resources == null)
        return "[missing resource file: " + paramString + "]"; 
    } 
    String str = null;
    try {
      str = resources.getString(paramString);
    } catch (MissingResourceException missingResourceException) {}
    return (str == null) ? ("[missing resource: " + paramString + "]") : str;
  }
  
  private static Class<?> getRMIClass(String paramString) throws Exception { return RMIClassLoader.loadClass(paramString); }
  
  private String Pstartgroup() throws ActivationException {
    while (true) {
      checkShutdown();
      if (this.groupSemaphore > 0) {
        this.groupSemaphore--;
        return "Group-" + this.groupCounter++;
      } 
      try {
        wait();
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  private void Vstartgroup() {
    this.groupSemaphore++;
    notifyAll();
  }
  
  private static class ActLogHandler extends LogHandler {
    public Object initialSnapshot() { return new Activation(null); }
    
    public Object applyUpdate(Object param1Object1, Object param1Object2) throws Exception { return ((Activation.LogRecord)param1Object1).apply(param1Object2); }
  }
  
  class ActivationMonitorImpl extends UnicastRemoteObject implements ActivationMonitor {
    private static final long serialVersionUID = -6214940464757948867L;
    
    ActivationMonitorImpl(int param1Int, RMIServerSocketFactory param1RMIServerSocketFactory) throws RemoteException { super(param1Int, null, param1RMIServerSocketFactory); }
    
    public void inactiveObject(ActivationID param1ActivationID) throws UnknownObjectException, RemoteException {
      try {
        Activation.this.checkShutdown();
      } catch (ActivationException activationException) {
        return;
      } 
      RegistryImpl.checkAccess("Activator.inactiveObject");
      Activation.this.getGroupEntry(param1ActivationID).inactiveObject(param1ActivationID);
    }
    
    public void activeObject(ActivationID param1ActivationID, MarshalledObject<? extends Remote> param1MarshalledObject) throws UnknownObjectException, RemoteException {
      try {
        Activation.this.checkShutdown();
      } catch (ActivationException activationException) {
        return;
      } 
      RegistryImpl.checkAccess("ActivationSystem.activeObject");
      Activation.this.getGroupEntry(param1ActivationID).activeObject(param1ActivationID, param1MarshalledObject);
    }
    
    public void inactiveGroup(ActivationGroupID param1ActivationGroupID, long param1Long) throws UnknownGroupException, RemoteException {
      try {
        Activation.this.checkShutdown();
      } catch (ActivationException activationException) {
        return;
      } 
      RegistryImpl.checkAccess("ActivationMonitor.inactiveGroup");
      Activation.this.getGroupEntry(param1ActivationGroupID).inactiveGroup(param1Long, false);
    }
  }
  
  private static class ActivationServerSocketFactory implements RMIServerSocketFactory {
    private final ServerSocket serverSocket;
    
    ActivationServerSocketFactory(ServerSocket param1ServerSocket) { this.serverSocket = param1ServerSocket; }
    
    public ServerSocket createServerSocket(int param1Int) throws IOException { return new Activation.DelayedAcceptServerSocket(this.serverSocket); }
  }
  
  class ActivationSystemImpl extends RemoteServer implements ActivationSystem {
    private static final long serialVersionUID = 9100152600327688967L;
    
    ActivationSystemImpl(int param1Int, RMIServerSocketFactory param1RMIServerSocketFactory) throws RemoteException {
      LiveRef liveRef = new LiveRef(new ObjID(4), param1Int, null, param1RMIServerSocketFactory);
      Activation.SameHostOnlyServerRef sameHostOnlyServerRef = new Activation.SameHostOnlyServerRef(liveRef, "ActivationSystem.nonLocalAccess");
      this.ref = sameHostOnlyServerRef;
      sameHostOnlyServerRef.exportObject(this, null);
    }
    
    public ActivationID registerObject(ActivationDesc param1ActivationDesc) throws ActivationException, UnknownGroupException, RemoteException {
      Activation.this.checkShutdown();
      ActivationGroupID activationGroupID = param1ActivationDesc.getGroupID();
      ActivationID activationID = new ActivationID(Activation.this.activatorStub);
      Activation.this.getGroupEntry(activationGroupID).registerObject(activationID, param1ActivationDesc, true);
      return activationID;
    }
    
    public void unregisterObject(ActivationID param1ActivationID) throws UnknownObjectException, RemoteException {
      Activation.this.checkShutdown();
      Activation.this.getGroupEntry(param1ActivationID).unregisterObject(param1ActivationID, true);
    }
    
    public ActivationGroupID registerGroup(ActivationGroupDesc param1ActivationGroupDesc) throws ActivationException, RemoteException {
      Thread.dumpStack();
      Activation.this.checkShutdown();
      Activation.this.checkArgs(param1ActivationGroupDesc, null);
      ActivationGroupID activationGroupID = new ActivationGroupID(Activation.this.systemStub);
      Activation.GroupEntry groupEntry = new Activation.GroupEntry(Activation.this, activationGroupID, param1ActivationGroupDesc);
      Activation.this.groupTable.put(activationGroupID, groupEntry);
      Activation.this.addLogRecord(new Activation.LogRegisterGroup(activationGroupID, param1ActivationGroupDesc));
      return activationGroupID;
    }
    
    public ActivationMonitor activeGroup(ActivationGroupID param1ActivationGroupID, ActivationInstantiator param1ActivationInstantiator, long param1Long) throws ActivationException, UnknownGroupException, RemoteException {
      Activation.this.checkShutdown();
      Activation.this.getGroupEntry(param1ActivationGroupID).activeGroup(param1ActivationInstantiator, param1Long);
      return Activation.this.monitor;
    }
    
    public void unregisterGroup(ActivationGroupID param1ActivationGroupID) throws ActivationException, UnknownGroupException, RemoteException {
      Activation.this.checkShutdown();
      Activation.this.removeGroupEntry(param1ActivationGroupID).unregisterGroup(true);
    }
    
    public ActivationDesc setActivationDesc(ActivationID param1ActivationID, ActivationDesc param1ActivationDesc) throws ActivationException, UnknownObjectException, RemoteException {
      Activation.this.checkShutdown();
      if (!Activation.this.getGroupID(param1ActivationID).equals(param1ActivationDesc.getGroupID()))
        throw new ActivationException("ActivationDesc contains wrong group"); 
      return Activation.this.getGroupEntry(param1ActivationID).setActivationDesc(param1ActivationID, param1ActivationDesc, true);
    }
    
    public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID param1ActivationGroupID, ActivationGroupDesc param1ActivationGroupDesc) throws ActivationException, UnknownGroupException, RemoteException {
      Activation.this.checkShutdown();
      Activation.this.checkArgs(param1ActivationGroupDesc, null);
      return Activation.this.getGroupEntry(param1ActivationGroupID).setActivationGroupDesc(param1ActivationGroupID, param1ActivationGroupDesc, true);
    }
    
    public ActivationDesc getActivationDesc(ActivationID param1ActivationID) throws ActivationException, UnknownObjectException, RemoteException {
      Activation.this.checkShutdown();
      return Activation.this.getGroupEntry(param1ActivationID).getActivationDesc(param1ActivationID);
    }
    
    public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID param1ActivationGroupID) throws ActivationException, UnknownGroupException, RemoteException {
      Activation.this.checkShutdown();
      return (this.this$0.getGroupEntry(param1ActivationGroupID)).desc;
    }
    
    public void shutdown() {
      Object object = Activation.this.startupLock;
      if (object != null)
        synchronized (object) {
        
        }  
      synchronized (Activation.this) {
        if (!Activation.this.shuttingDown) {
          Activation.this.shuttingDown = true;
          (new Activation.Shutdown(Activation.this)).start();
        } 
      } 
    }
  }
  
  class ActivatorImpl extends RemoteServer implements Activator {
    private static final long serialVersionUID = -3654244726254566136L;
    
    ActivatorImpl(int param1Int, RMIServerSocketFactory param1RMIServerSocketFactory) throws RemoteException {
      LiveRef liveRef = new LiveRef(new ObjID(1), param1Int, null, param1RMIServerSocketFactory);
      UnicastServerRef unicastServerRef = new UnicastServerRef(liveRef);
      this.ref = unicastServerRef;
      unicastServerRef.exportObject(this, null, false);
    }
    
    public MarshalledObject<? extends Remote> activate(ActivationID param1ActivationID, boolean param1Boolean) throws ActivationException, UnknownObjectException, RemoteException {
      Activation.this.checkShutdown();
      return Activation.this.getGroupEntry(param1ActivationID).activate(param1ActivationID, param1Boolean);
    }
  }
  
  public static class DefaultExecPolicy {
    public void checkExecCommand(ActivationGroupDesc param1ActivationGroupDesc, String[] param1ArrayOfString) throws SecurityException, ActivationException {
      PermissionCollection permissionCollection = getExecPermissions();
      Properties properties = param1ActivationGroupDesc.getPropertyOverrides();
      if (properties != null) {
        Enumeration enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
          String str1 = (String)enumeration.nextElement();
          String str2 = properties.getProperty(str1);
          String str3 = "-D" + str1 + "=" + str2;
          try {
            checkPermission(permissionCollection, new ExecOptionPermission(str3));
          } catch (AccessControlException accessControlException) {
            if (str2.equals("")) {
              checkPermission(permissionCollection, new ExecOptionPermission("-D" + str1));
              continue;
            } 
            throw accessControlException;
          } 
        } 
      } 
      String str = param1ActivationGroupDesc.getClassName();
      if ((str != null && !str.equals(ActivationGroupImpl.class.getName())) || param1ActivationGroupDesc.getLocation() != null || param1ActivationGroupDesc.getData() != null)
        throw new AccessControlException("access denied (custom group implementation not allowed)"); 
      ActivationGroupDesc.CommandEnvironment commandEnvironment = param1ActivationGroupDesc.getCommandEnvironment();
      if (commandEnvironment != null) {
        String str1 = commandEnvironment.getCommandPath();
        if (str1 != null)
          checkPermission(permissionCollection, new ExecPermission(str1)); 
        String[] arrayOfString = commandEnvironment.getCommandOptions();
        if (arrayOfString != null)
          for (String str2 : arrayOfString)
            checkPermission(permissionCollection, new ExecOptionPermission(str2));  
      } 
    }
    
    static void checkConfiguration() {
      Policy policy = (Policy)AccessController.doPrivileged(new PrivilegedAction<Policy>() {
            public Policy run() { return Policy.getPolicy(); }
          });
      if (!(policy instanceof sun.security.provider.PolicyFile))
        return; 
      PermissionCollection permissionCollection = getExecPermissions();
      Enumeration enumeration = permissionCollection.elements();
      while (enumeration.hasMoreElements()) {
        Permission permission = (Permission)enumeration.nextElement();
        if (permission instanceof java.security.AllPermission || permission instanceof ExecPermission || permission instanceof ExecOptionPermission)
          return; 
      } 
      System.err.println(Activation.getTextResource("rmid.exec.perms.inadequate"));
    }
    
    private static PermissionCollection getExecPermissions() { return (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction<PermissionCollection>() {
            public PermissionCollection run() {
              CodeSource codeSource = new CodeSource(null, (Certificate[])null);
              Policy policy = Policy.getPolicy();
              return (policy != null) ? policy.getPermissions(codeSource) : new Permissions();
            }
          }); }
    
    private static void checkPermission(PermissionCollection param1PermissionCollection, Permission param1Permission) throws AccessControlException {
      if (!param1PermissionCollection.implies(param1Permission))
        throw new AccessControlException("access denied " + param1Permission.toString()); 
    }
  }
  
  private static class DelayedAcceptServerSocket extends ServerSocket {
    private final ServerSocket serverSocket;
    
    DelayedAcceptServerSocket(ServerSocket param1ServerSocket) { this.serverSocket = param1ServerSocket; }
    
    public void bind(SocketAddress param1SocketAddress) throws IOException { this.serverSocket.bind(param1SocketAddress); }
    
    public void bind(SocketAddress param1SocketAddress, int param1Int) throws IOException { this.serverSocket.bind(param1SocketAddress, param1Int); }
    
    public InetAddress getInetAddress() { return (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
            public InetAddress run() { return Activation.DelayedAcceptServerSocket.this.serverSocket.getInetAddress(); }
          }); }
    
    public int getLocalPort() { return this.serverSocket.getLocalPort(); }
    
    public SocketAddress getLocalSocketAddress() { return (SocketAddress)AccessController.doPrivileged(new PrivilegedAction<SocketAddress>() {
            public SocketAddress run() { return Activation.DelayedAcceptServerSocket.this.serverSocket.getLocalSocketAddress(); }
          }); }
    
    public Socket accept() throws IOException {
      synchronized (initLock) {
        while (true) {
          try {
            if (!initDone) {
              initLock.wait();
              continue;
            } 
            break;
          } catch (InterruptedException interruptedException) {
            throw new AssertionError(interruptedException);
          } 
        } 
      } 
      return this.serverSocket.accept();
    }
    
    public void close() { this.serverSocket.close(); }
    
    public ServerSocketChannel getChannel() { return this.serverSocket.getChannel(); }
    
    public boolean isBound() { return this.serverSocket.isBound(); }
    
    public boolean isClosed() { return this.serverSocket.isClosed(); }
    
    public void setSoTimeout(int param1Int) { this.serverSocket.setSoTimeout(param1Int); }
    
    public int getSoTimeout() { return this.serverSocket.getSoTimeout(); }
    
    public void setReuseAddress(boolean param1Boolean) throws SocketException { this.serverSocket.setReuseAddress(param1Boolean); }
    
    public boolean getReuseAddress() { return this.serverSocket.getReuseAddress(); }
    
    public String toString() throws ActivationException { return this.serverSocket.toString(); }
    
    public void setReceiveBufferSize(int param1Int) { this.serverSocket.setReceiveBufferSize(param1Int); }
    
    public int getReceiveBufferSize() { return this.serverSocket.getReceiveBufferSize(); }
  }
  
  private class GroupEntry implements Serializable {
    private static final long serialVersionUID = 7222464070032993304L;
    
    private static final int MAX_TRIES = 2;
    
    private static final int NORMAL = 0;
    
    private static final int CREATING = 1;
    
    private static final int TERMINATE = 2;
    
    private static final int TERMINATING = 3;
    
    ActivationGroupDesc desc = null;
    
    ActivationGroupID groupID = null;
    
    long incarnation = 0L;
    
    Map<ActivationID, Activation.ObjectEntry> objects = new HashMap();
    
    Set<ActivationID> restartSet = new HashSet();
    
    ActivationInstantiator group = null;
    
    int status = 0;
    
    long waitTime = 0L;
    
    String groupName = null;
    
    Process child = null;
    
    boolean removed = false;
    
    Watchdog watchdog = null;
    
    GroupEntry(ActivationGroupID param1ActivationGroupID, ActivationGroupDesc param1ActivationGroupDesc) {
      this.groupID = param1ActivationGroupID;
      this.desc = param1ActivationGroupDesc;
    }
    
    void restartServices() {
      Iterator iterator = null;
      synchronized (this) {
        if (this.restartSet.isEmpty())
          return; 
        iterator = (new HashSet(this.restartSet)).iterator();
      } 
      while (iterator.hasNext()) {
        ActivationID activationID = (ActivationID)iterator.next();
        try {
          activate(activationID, true);
        } catch (Exception exception) {
          if (Activation.this.shuttingDown)
            return; 
          System.err.println(Activation.getTextResource("rmid.restart.service.warning"));
          exception.printStackTrace();
        } 
      } 
    }
    
    void activeGroup(ActivationInstantiator param1ActivationInstantiator, long param1Long) throws ActivationException, UnknownGroupException {
      if (this.incarnation != param1Long)
        throw new ActivationException("invalid incarnation"); 
      if (this.group != null) {
        if (this.group.equals(param1ActivationInstantiator))
          return; 
        throw new ActivationException("group already active");
      } 
      if (this.child != null && this.status != 1)
        throw new ActivationException("group not being created"); 
      this.group = param1ActivationInstantiator;
      this.status = 0;
      notifyAll();
    }
    
    private void checkRemoved() {
      if (this.removed)
        throw new UnknownGroupException("group removed"); 
    }
    
    private Activation.ObjectEntry getObjectEntry(ActivationID param1ActivationID) throws UnknownObjectException {
      if (this.removed)
        throw new UnknownObjectException("object's group removed"); 
      Activation.ObjectEntry objectEntry = (Activation.ObjectEntry)this.objects.get(param1ActivationID);
      if (objectEntry == null)
        throw new UnknownObjectException("object unknown"); 
      return objectEntry;
    }
    
    void registerObject(ActivationID param1ActivationID, ActivationDesc param1ActivationDesc, boolean param1Boolean) throws UnknownGroupException, ActivationException {
      checkRemoved();
      this.objects.put(param1ActivationID, new Activation.ObjectEntry(param1ActivationDesc));
      if (param1ActivationDesc.getRestartMode() == true)
        this.restartSet.add(param1ActivationID); 
      Activation.this.idTable.put(param1ActivationID, this.groupID);
      if (param1Boolean)
        Activation.this.addLogRecord(new Activation.LogRegisterObject(param1ActivationID, param1ActivationDesc)); 
    }
    
    void unregisterObject(ActivationID param1ActivationID, boolean param1Boolean) throws UnknownGroupException, ActivationException {
      Activation.ObjectEntry objectEntry = getObjectEntry(param1ActivationID);
      objectEntry.removed = true;
      this.objects.remove(param1ActivationID);
      if (objectEntry.desc.getRestartMode() == true)
        this.restartSet.remove(param1ActivationID); 
      Activation.this.idTable.remove(param1ActivationID);
      if (param1Boolean)
        Activation.this.addLogRecord(new Activation.LogUnregisterObject(param1ActivationID)); 
    }
    
    void unregisterGroup(boolean param1Boolean) throws SocketException {
      checkRemoved();
      this.removed = true;
      for (Map.Entry entry : this.objects.entrySet()) {
        ActivationID activationID = (ActivationID)entry.getKey();
        Activation.this.idTable.remove(activationID);
        Activation.ObjectEntry objectEntry = (Activation.ObjectEntry)entry.getValue();
        objectEntry.removed = true;
      } 
      this.objects.clear();
      this.restartSet.clear();
      reset();
      childGone();
      if (param1Boolean)
        Activation.this.addLogRecord(new Activation.LogUnregisterGroup(this.groupID)); 
    }
    
    ActivationDesc setActivationDesc(ActivationID param1ActivationID, ActivationDesc param1ActivationDesc, boolean param1Boolean) throws UnknownObjectException, UnknownGroupException, ActivationException {
      Activation.ObjectEntry objectEntry = getObjectEntry(param1ActivationID);
      ActivationDesc activationDesc = objectEntry.desc;
      objectEntry.desc = param1ActivationDesc;
      if (param1ActivationDesc.getRestartMode() == true) {
        this.restartSet.add(param1ActivationID);
      } else {
        this.restartSet.remove(param1ActivationID);
      } 
      if (param1Boolean)
        Activation.this.addLogRecord(new Activation.LogUpdateDesc(param1ActivationID, param1ActivationDesc)); 
      return activationDesc;
    }
    
    ActivationDesc getActivationDesc(ActivationID param1ActivationID) throws ActivationException, UnknownObjectException, RemoteException { return (getObjectEntry(param1ActivationID)).desc; }
    
    ActivationGroupDesc setActivationGroupDesc(ActivationGroupID param1ActivationGroupID, ActivationGroupDesc param1ActivationGroupDesc, boolean param1Boolean) throws UnknownGroupException, ActivationException {
      checkRemoved();
      ActivationGroupDesc activationGroupDesc = this.desc;
      this.desc = param1ActivationGroupDesc;
      if (param1Boolean)
        Activation.this.addLogRecord(new Activation.LogUpdateGroupDesc(param1ActivationGroupID, param1ActivationGroupDesc)); 
      return activationGroupDesc;
    }
    
    void inactiveGroup(long param1Long, boolean param1Boolean) throws UnknownGroupException {
      checkRemoved();
      if (this.incarnation != param1Long)
        throw new UnknownGroupException("invalid incarnation"); 
      reset();
      if (param1Boolean) {
        terminate();
      } else if (this.child != null && this.status == 0) {
        this.status = 2;
        this.watchdog.noRestart();
      } 
    }
    
    void activeObject(ActivationID param1ActivationID, MarshalledObject<? extends Remote> param1MarshalledObject) throws UnknownObjectException, RemoteException { (getObjectEntry(param1ActivationID)).stub = param1MarshalledObject; }
    
    void inactiveObject(ActivationID param1ActivationID) throws UnknownObjectException, RemoteException { getObjectEntry(param1ActivationID).reset(); }
    
    private void reset() {
      this.group = null;
      for (Activation.ObjectEntry objectEntry : this.objects.values())
        objectEntry.reset(); 
    }
    
    private void childGone() {
      if (this.child != null) {
        this.child = null;
        this.watchdog.dispose();
        this.watchdog = null;
        this.status = 0;
        notifyAll();
      } 
    }
    
    private void terminate() {
      if (this.child != null && this.status != 3) {
        this.child.destroy();
        this.status = 3;
        this.waitTime = System.currentTimeMillis() + groupTimeout;
        notifyAll();
      } 
    }
    
    private void await() {
      while (true) {
        switch (this.status) {
          case 0:
            return;
          case 2:
            terminate();
          case 3:
            try {
              this.child.exitValue();
            } catch (IllegalThreadStateException illegalThreadStateException) {
              long l = System.currentTimeMillis();
              if (this.waitTime > l)
                try {
                  wait(this.waitTime - l);
                  continue;
                } catch (InterruptedException interruptedException) {
                  continue;
                }  
            } 
            childGone();
            return;
          case 1:
            try {
              wait();
            } catch (InterruptedException interruptedException) {}
        } 
      } 
    }
    
    void shutdownFast() {
      Process process = this.child;
      if (process != null)
        process.destroy(); 
    }
    
    void shutdown() {
      reset();
      terminate();
      await();
    }
    
    MarshalledObject<? extends Remote> activate(ActivationID param1ActivationID, boolean param1Boolean) throws ActivationException, UnknownObjectException, RemoteException {
      RemoteException remoteException = null;
      for (byte b = 2; b > 0; b--) {
        Activation.ObjectEntry objectEntry;
        long l;
        ActivationInstantiator activationInstantiator;
        synchronized (this) {
          objectEntry = getObjectEntry(param1ActivationID);
          if (!param1Boolean && objectEntry.stub != null)
            return objectEntry.stub; 
          activationInstantiator = getInstantiator(this.groupID);
          l = this.incarnation;
        } 
        boolean bool = false;
        boolean bool1 = false;
        try {
          return objectEntry.activate(param1ActivationID, param1Boolean, activationInstantiator);
        } catch (NoSuchObjectException noSuchObjectException) {
          bool = true;
          remoteException = noSuchObjectException;
        } catch (ConnectException connectException) {
          bool = true;
          bool1 = true;
          remoteException = connectException;
        } catch (ConnectIOException connectIOException) {
          bool = true;
          bool1 = true;
          remoteException = connectIOException;
        } catch (InactiveGroupException inactiveGroupException) {
          bool = true;
          remoteException = inactiveGroupException;
        } catch (RemoteException remoteException1) {
          if (remoteException == null)
            remoteException = remoteException1; 
        } 
        if (bool)
          try {
            System.err.println(MessageFormat.format(Activation.getTextResource("rmid.group.inactive"), new Object[] { remoteException.toString() }));
            remoteException.printStackTrace();
            Activation.this.getGroupEntry(this.groupID).inactiveGroup(l, bool1);
          } catch (UnknownGroupException unknownGroupException) {} 
      } 
      throw new ActivationException("object activation failed after 2 tries", remoteException);
    }
    
    private ActivationInstantiator getInstantiator(ActivationGroupID param1ActivationGroupID) throws ActivationException {
      assert Thread.holdsLock(this);
      await();
      if (this.group != null)
        return this.group; 
      checkRemoved();
      bool = false;
      try {
        this.groupName = Activation.this.Pstartgroup();
        bool = true;
        String[] arrayOfString = Activation.this.activationArgs(this.desc);
        Activation.this.checkArgs(this.desc, arrayOfString);
        if (debugExec) {
          StringBuffer stringBuffer = new StringBuffer(arrayOfString[0]);
          for (byte b = 1; b < arrayOfString.length; b++) {
            stringBuffer.append(' ');
            stringBuffer.append(arrayOfString[b]);
          } 
          System.err.println(MessageFormat.format(Activation.getTextResource("rmid.exec.command"), new Object[] { stringBuffer.toString() }));
        } 
        try {
          this.child = Runtime.getRuntime().exec(arrayOfString);
          this.status = 1;
          this.incarnation++;
          this.watchdog = new Watchdog();
          this.watchdog.start();
          Activation.this.addLogRecord(new Activation.LogGroupIncarnation(param1ActivationGroupID, this.incarnation));
          PipeWriter.plugTogetherPair(this.child.getInputStream(), System.out, this.child.getErrorStream(), System.err);
          try (MarshalOutputStream null = new MarshalOutputStream(this.child.getOutputStream())) {
            marshalOutputStream.writeObject(param1ActivationGroupID);
            marshalOutputStream.writeObject(this.desc);
            marshalOutputStream.writeLong(this.incarnation);
            marshalOutputStream.flush();
          } 
        } catch (IOException iOException) {
          terminate();
          throw new ActivationException("unable to create activation group", iOException);
        } 
        try {
          long l1 = System.currentTimeMillis();
          long l2 = l1 + execTimeout;
          do {
            wait(l2 - l1);
            if (this.group != null)
              return this.group; 
            l1 = System.currentTimeMillis();
          } while (this.status == 1 && l1 < l2);
        } catch (InterruptedException interruptedException) {}
        terminate();
        throw new ActivationException(this.removed ? "activation group unregistered" : "timeout creating child process");
      } finally {
        if (bool)
          Activation.this.Vstartgroup(); 
      } 
    }
    
    private class Watchdog extends Thread {
      private final Process groupProcess = Activation.GroupEntry.this.child;
      
      private final long groupIncarnation = Activation.GroupEntry.this.incarnation;
      
      private boolean canInterrupt = true;
      
      private boolean shouldQuit = false;
      
      private boolean shouldRestart = true;
      
      Watchdog() {
        super("WatchDog-" + this$0.groupName + "-" + this$0.incarnation);
        setDaemon(true);
      }
      
      public void run() {
        if (this.shouldQuit)
          return; 
        try {
          this.groupProcess.waitFor();
        } catch (InterruptedException interruptedException) {
          return;
        } 
        boolean bool = false;
        synchronized (Activation.GroupEntry.this) {
          if (this.shouldQuit)
            return; 
          this.canInterrupt = false;
          interrupted();
          if (this.groupIncarnation == Activation.GroupEntry.this.incarnation) {
            bool = (this.shouldRestart && !Activation.GroupEntry.this.this$0.shuttingDown) ? 1 : 0;
            Activation.GroupEntry.this.reset();
            Activation.GroupEntry.this.childGone();
          } 
        } 
        if (bool)
          Activation.GroupEntry.this.restartServices(); 
      }
      
      void dispose() {
        this.shouldQuit = true;
        if (this.canInterrupt)
          interrupt(); 
      }
      
      void noRestart() { this.shouldRestart = false; }
    }
  }
  
  private static class LogGroupIncarnation extends LogRecord {
    private static final long serialVersionUID = 4146872747377631897L;
    
    private ActivationGroupID id;
    
    private long inc;
    
    LogGroupIncarnation(ActivationGroupID param1ActivationGroupID, long param1Long) throws UnknownGroupException, RemoteException {
      super(null);
      this.id = param1ActivationGroupID;
      this.inc = param1Long;
    }
    
    Object apply(Object param1Object) {
      try {
        Activation.GroupEntry groupEntry = ((Activation)param1Object).getGroupEntry(this.id);
        groupEntry.incarnation = this.inc;
      } catch (Exception exception) {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogGroupIncarnation" }));
        exception.printStackTrace();
      } 
      return param1Object;
    }
  }
  
  private static abstract class LogRecord implements Serializable {
    private static final long serialVersionUID = 8395140512322687529L;
    
    private LogRecord() {}
    
    abstract Object apply(Object param1Object);
  }
  
  private static class LogRegisterGroup extends LogRecord {
    private static final long serialVersionUID = -1966827458515403625L;
    
    private ActivationGroupID id;
    
    private ActivationGroupDesc desc;
    
    LogRegisterGroup(ActivationGroupID param1ActivationGroupID, ActivationGroupDesc param1ActivationGroupDesc) {
      super(null);
      this.id = param1ActivationGroupID;
      this.desc = param1ActivationGroupDesc;
    }
    
    Object apply(Object param1Object) {
      ((Activation)param1Object).getClass();
      ((Activation)param1Object).groupTable.put(this.id, new Activation.GroupEntry((Activation)param1Object, this.id, this.desc));
      return param1Object;
    }
  }
  
  private static class LogRegisterObject extends LogRecord {
    private static final long serialVersionUID = -6280336276146085143L;
    
    private ActivationID id;
    
    private ActivationDesc desc;
    
    LogRegisterObject(ActivationID param1ActivationID, ActivationDesc param1ActivationDesc) {
      super(null);
      this.id = param1ActivationID;
      this.desc = param1ActivationDesc;
    }
    
    Object apply(Object param1Object) {
      try {
        ((Activation)param1Object).getGroupEntry(this.desc.getGroupID()).registerObject(this.id, this.desc, false);
      } catch (Exception exception) {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogRegisterObject" }));
        exception.printStackTrace();
      } 
      return param1Object;
    }
  }
  
  private static class LogUnregisterGroup extends LogRecord {
    private static final long serialVersionUID = -3356306586522147344L;
    
    private ActivationGroupID id;
    
    LogUnregisterGroup(ActivationGroupID param1ActivationGroupID) throws ActivationException, UnknownGroupException, RemoteException {
      super(null);
      this.id = param1ActivationGroupID;
    }
    
    Object apply(Object param1Object) {
      Activation.GroupEntry groupEntry = (Activation.GroupEntry)((Activation)param1Object).groupTable.remove(this.id);
      try {
        groupEntry.unregisterGroup(false);
      } catch (Exception exception) {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterGroup" }));
        exception.printStackTrace();
      } 
      return param1Object;
    }
  }
  
  private static class LogUnregisterObject extends LogRecord {
    private static final long serialVersionUID = 6269824097396935501L;
    
    private ActivationID id;
    
    LogUnregisterObject(ActivationID param1ActivationID) throws UnknownObjectException, RemoteException {
      super(null);
      this.id = param1ActivationID;
    }
    
    Object apply(Object param1Object) {
      try {
        ((Activation)param1Object).getGroupEntry(this.id).unregisterObject(this.id, false);
      } catch (Exception exception) {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUnregisterObject" }));
        exception.printStackTrace();
      } 
      return param1Object;
    }
  }
  
  private static class LogUpdateDesc extends LogRecord {
    private static final long serialVersionUID = 545511539051179885L;
    
    private ActivationID id;
    
    private ActivationDesc desc;
    
    LogUpdateDesc(ActivationID param1ActivationID, ActivationDesc param1ActivationDesc) {
      super(null);
      this.id = param1ActivationID;
      this.desc = param1ActivationDesc;
    }
    
    Object apply(Object param1Object) {
      try {
        ((Activation)param1Object).getGroupEntry(this.id).setActivationDesc(this.id, this.desc, false);
      } catch (Exception exception) {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateDesc" }));
        exception.printStackTrace();
      } 
      return param1Object;
    }
  }
  
  private static class LogUpdateGroupDesc extends LogRecord {
    private static final long serialVersionUID = -1271300989218424337L;
    
    private ActivationGroupID id;
    
    private ActivationGroupDesc desc;
    
    LogUpdateGroupDesc(ActivationGroupID param1ActivationGroupID, ActivationGroupDesc param1ActivationGroupDesc) {
      super(null);
      this.id = param1ActivationGroupID;
      this.desc = param1ActivationGroupDesc;
    }
    
    Object apply(Object param1Object) {
      try {
        ((Activation)param1Object).getGroupEntry(this.id).setActivationGroupDesc(this.id, this.desc, false);
      } catch (Exception exception) {
        System.err.println(MessageFormat.format(Activation.getTextResource("rmid.log.recover.warning"), new Object[] { "LogUpdateGroupDesc" }));
        exception.printStackTrace();
      } 
      return param1Object;
    }
  }
  
  private static class ObjectEntry implements Serializable {
    private static final long serialVersionUID = -5500114225321357856L;
    
    ActivationDesc desc;
    
    ObjectEntry(ActivationDesc param1ActivationDesc) { this.desc = param1ActivationDesc; }
    
    MarshalledObject<? extends Remote> activate(ActivationID param1ActivationID, boolean param1Boolean, ActivationInstantiator param1ActivationInstantiator) throws RemoteException, ActivationException {
      MarshalledObject marshalledObject = this.stub;
      if (this.removed)
        throw new UnknownObjectException("object removed"); 
      if (!param1Boolean && marshalledObject != null)
        return marshalledObject; 
      marshalledObject = param1ActivationInstantiator.newInstance(param1ActivationID, this.desc);
      this.stub = marshalledObject;
      return marshalledObject;
    }
    
    void reset() { this.stub = null; }
  }
  
  static class SameHostOnlyServerRef extends UnicastServerRef {
    private static final long serialVersionUID = 1234L;
    
    private String accessKind;
    
    SameHostOnlyServerRef(LiveRef param1LiveRef, String param1String) {
      super(param1LiveRef);
      this.accessKind = param1String;
    }
    
    protected void unmarshalCustomCallData(ObjectInput param1ObjectInput) throws IOException, ClassNotFoundException {
      RegistryImpl.checkAccess(this.accessKind);
      super.unmarshalCustomCallData(param1ObjectInput);
    }
  }
  
  private class Shutdown extends Thread {
    Shutdown() throws ActivationException { super("rmid Shutdown"); }
    
    public void run() {
      try {
        Activation.unexport(Activation.this.activator);
        Activation.unexport(Activation.this.system);
        for (Activation.GroupEntry groupEntry : Activation.this.groupTable.values())
          groupEntry.shutdown(); 
        Runtime.getRuntime().removeShutdownHook(Activation.this.shutdownHook);
        Activation.unexport(Activation.this.monitor);
        try {
          synchronized (Activation.this.log) {
            Activation.this.log.close();
          } 
        } catch (IOException iOException) {}
      } finally {
        System.err.println(Activation.getTextResource("rmid.daemon.shutdown"));
        System.exit(0);
      } 
    }
  }
  
  private class ShutdownHook extends Thread {
    ShutdownHook() throws ActivationException { super("rmid ShutdownHook"); }
    
    public void run() {
      synchronized (Activation.this) {
        Activation.this.shuttingDown = true;
      } 
      for (Activation.GroupEntry groupEntry : Activation.this.groupTable.values())
        groupEntry.shutdownFast(); 
    }
  }
  
  private static class SystemRegistryImpl extends RegistryImpl {
    private static final String NAME = ActivationSystem.class.getName();
    
    private static final long serialVersionUID = 4877330021609408794L;
    
    private final ActivationSystem systemStub;
    
    SystemRegistryImpl(int param1Int, RMIClientSocketFactory param1RMIClientSocketFactory, RMIServerSocketFactory param1RMIServerSocketFactory, ActivationSystem param1ActivationSystem) throws RemoteException {
      super(param1Int, param1RMIClientSocketFactory, param1RMIServerSocketFactory);
      this.systemStub = param1ActivationSystem;
    }
    
    public Remote lookup(String param1String) throws RemoteException, NotBoundException { return param1String.equals(NAME) ? this.systemStub : super.lookup(param1String); }
    
    public String[] list() throws RemoteException {
      String[] arrayOfString1 = super.list();
      int i = arrayOfString1.length;
      String[] arrayOfString2 = new String[i + 1];
      if (i > 0)
        System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i); 
      arrayOfString2[i] = NAME;
      return arrayOfString2;
    }
    
    public void bind(String param1String, Remote param1Remote) throws RemoteException, AlreadyBoundException, AccessException {
      if (param1String.equals(NAME))
        throw new AccessException("binding ActivationSystem is disallowed"); 
      RegistryImpl.checkAccess("ActivationSystem.bind");
      super.bind(param1String, param1Remote);
    }
    
    public void unbind(String param1String) {
      if (param1String.equals(NAME))
        throw new AccessException("unbinding ActivationSystem is disallowed"); 
      RegistryImpl.checkAccess("ActivationSystem.unbind");
      super.unbind(param1String);
    }
    
    public void rebind(String param1String, Remote param1Remote) throws RemoteException, AlreadyBoundException, AccessException {
      if (param1String.equals(NAME))
        throw new AccessException("binding ActivationSystem is disallowed"); 
      RegistryImpl.checkAccess("ActivationSystem.rebind");
      super.rebind(param1String, param1Remote);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\Activation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */