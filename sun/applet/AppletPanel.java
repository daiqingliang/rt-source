package sun.applet;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.EmbeddedFrame;
import sun.awt.SunToolkit;
import sun.misc.MessageUtils;
import sun.misc.PerformanceLogger;
import sun.misc.Queue;
import sun.security.util.SecurityConstants;

public abstract class AppletPanel extends Panel implements AppletStub, Runnable {
  Applet applet;
  
  protected boolean doInit = true;
  
  protected AppletClassLoader loader;
  
  public static final int APPLET_DISPOSE = 0;
  
  public static final int APPLET_LOAD = 1;
  
  public static final int APPLET_INIT = 2;
  
  public static final int APPLET_START = 3;
  
  public static final int APPLET_STOP = 4;
  
  public static final int APPLET_DESTROY = 5;
  
  public static final int APPLET_QUIT = 6;
  
  public static final int APPLET_ERROR = 7;
  
  public static final int APPLET_RESIZE = 51234;
  
  public static final int APPLET_LOADING = 51235;
  
  public static final int APPLET_LOADING_COMPLETED = 51236;
  
  protected int status;
  
  protected Thread handler;
  
  Dimension defaultAppletSize = new Dimension(10, 10);
  
  Dimension currentAppletSize = new Dimension(10, 10);
  
  MessageUtils mu = new MessageUtils();
  
  Thread loaderThread = null;
  
  boolean loadAbortRequest = false;
  
  private static int threadGroupNumber = 0;
  
  private AppletListener listeners;
  
  private Queue queue = null;
  
  private EventQueue appEvtQ = null;
  
  private static HashMap classloaders = new HashMap();
  
  private boolean jdk11Applet = false;
  
  private boolean jdk12Applet = false;
  
  private static AppletMessageHandler amh = new AppletMessageHandler("appletpanel");
  
  protected abstract String getCode();
  
  protected abstract String getJarFiles();
  
  protected abstract String getSerializedObject();
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract boolean hasInitialFocus();
  
  protected void setupAppletAppContext() {}
  
  void createAppletThread() {
    String str1 = "applet-" + getCode();
    this.loader = getClassLoader(getCodeBase(), getClassLoaderCacheKey());
    this.loader.grab();
    String str2 = getParameter("codebase_lookup");
    if (str2 != null && str2.equals("false")) {
      this.loader.setCodebaseLookup(false);
    } else {
      this.loader.setCodebaseLookup(true);
    } 
    ThreadGroup threadGroup = this.loader.getThreadGroup();
    this.handler = new Thread(threadGroup, this, "thread " + str1);
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            AppletPanel.this.handler.setContextClassLoader(AppletPanel.this.loader);
            return null;
          }
        });
    this.handler.start();
  }
  
  void joinAppletThread() {
    if (this.handler != null) {
      this.handler.join();
      this.handler = null;
    } 
  }
  
  void release() {
    if (this.loader != null) {
      this.loader.release();
      this.loader = null;
    } 
  }
  
  public void init() {
    try {
      this.defaultAppletSize.width = getWidth();
      this.currentAppletSize.width = this.defaultAppletSize.width;
      this.defaultAppletSize.height = getHeight();
      this.currentAppletSize.height = this.defaultAppletSize.height;
    } catch (NumberFormatException numberFormatException) {
      this.status = 7;
      showAppletStatus("badattribute.exception");
      showAppletLog("badattribute.exception");
      showAppletException(numberFormatException);
    } 
    setLayout(new BorderLayout());
    createAppletThread();
  }
  
  public Dimension minimumSize() { return new Dimension(this.defaultAppletSize.width, this.defaultAppletSize.height); }
  
  public Dimension preferredSize() { return new Dimension(this.currentAppletSize.width, this.currentAppletSize.height); }
  
  public void addAppletListener(AppletListener paramAppletListener) { this.listeners = AppletEventMulticaster.add(this.listeners, paramAppletListener); }
  
  public void removeAppletListener(AppletListener paramAppletListener) { this.listeners = AppletEventMulticaster.remove(this.listeners, paramAppletListener); }
  
  public void dispatchAppletEvent(int paramInt, Object paramObject) {
    if (this.listeners != null) {
      AppletEvent appletEvent = new AppletEvent(this, paramInt, paramObject);
      this.listeners.appletStateChanged(appletEvent);
    } 
  }
  
  public void sendEvent(int paramInt) {
    synchronized (this) {
      if (this.queue == null)
        this.queue = new Queue(); 
      Integer integer = Integer.valueOf(paramInt);
      this.queue.enqueue(integer);
      notifyAll();
    } 
    if (paramInt == 6) {
      try {
        joinAppletThread();
      } catch (InterruptedException interruptedException) {}
      if (this.loader == null)
        this.loader = getClassLoader(getCodeBase(), getClassLoaderCacheKey()); 
      release();
    } 
  }
  
  AppletEvent getNextEvent() throws InterruptedException {
    while (this.queue == null || this.queue.isEmpty())
      wait(); 
    Integer integer = (Integer)this.queue.dequeue();
    return new AppletEvent(this, integer.intValue(), null);
  }
  
  boolean emptyEventQueue() { return (this.queue == null || this.queue.isEmpty()); }
  
  private void setExceptionStatus(AccessControlException paramAccessControlException) {
    Permission permission = paramAccessControlException.getPermission();
    if (permission instanceof RuntimePermission && permission.getName().startsWith("modifyThread")) {
      if (this.loader == null)
        this.loader = getClassLoader(getCodeBase(), getClassLoaderCacheKey()); 
      this.loader.setExceptionStatus();
    } 
  }
  
  public void run() {
    Thread thread = Thread.currentThread();
    if (thread == this.loaderThread) {
      runLoader();
      return;
    } 
    boolean bool = false;
    while (!bool && !thread.isInterrupted()) {
      AppletEvent appletEvent;
      try {
        appletEvent = getNextEvent();
      } catch (InterruptedException interruptedException) {
        showAppletStatus("bail");
        return;
      } 
      try {
        Font font;
        switch (appletEvent.getID()) {
          case 1:
            if (okToLoad() && this.loaderThread == null) {
              setLoaderThread(new Thread(this));
              this.loaderThread.start();
              this.loaderThread.join();
              setLoaderThread(null);
            } 
            break;
          case 2:
            if (this.status != 1 && this.status != 5) {
              showAppletStatus("notloaded");
              break;
            } 
            this.applet.resize(this.defaultAppletSize);
            if (this.doInit) {
              if (PerformanceLogger.loggingEnabled()) {
                PerformanceLogger.setTime("Applet Init");
                PerformanceLogger.outputLog();
              } 
              this.applet.init();
            } 
            font = getFont();
            if (font == null || ("dialog".equals(font.getFamily().toLowerCase(Locale.ENGLISH)) && font.getSize() == 12 && font.getStyle() == 0))
              setFont(new Font("Dialog", 0, 12)); 
            this.doInit = true;
            try {
              final AppletPanel p = this;
              Runnable runnable = new Runnable() {
                  public void run() { p.validate(); }
                };
              AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, runnable);
            } catch (InterruptedException interruptedException) {
            
            } catch (InvocationTargetException invocationTargetException) {}
            this.status = 2;
            showAppletStatus("inited");
            break;
          case 3:
            if (this.status != 2 && this.status != 4) {
              showAppletStatus("notinited");
              break;
            } 
            this.applet.resize(this.currentAppletSize);
            this.applet.start();
            try {
              final AppletPanel p = this;
              final Applet a = this.applet;
              Runnable runnable = new Runnable() {
                  public void run() {
                    p.validate();
                    a.setVisible(true);
                    if (AppletPanel.this.hasInitialFocus())
                      AppletPanel.this.setDefaultFocus(); 
                  }
                };
              AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, runnable);
            } catch (InterruptedException interruptedException) {
            
            } catch (InvocationTargetException invocationTargetException) {}
            this.status = 3;
            showAppletStatus("started");
            break;
          case 4:
            if (this.status != 3) {
              showAppletStatus("notstarted");
              break;
            } 
            this.status = 4;
            try {
              final Applet a = this.applet;
              Runnable runnable = new Runnable() {
                  public void run() { a.setVisible(false); }
                };
              AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, runnable);
            } catch (InterruptedException interruptedException) {
            
            } catch (InvocationTargetException invocationTargetException) {}
            try {
              this.applet.stop();
            } catch (AccessControlException accessControlException) {
              setExceptionStatus(accessControlException);
              throw accessControlException;
            } 
            showAppletStatus("stopped");
            break;
          case 5:
            if (this.status != 4 && this.status != 2) {
              showAppletStatus("notstopped");
              break;
            } 
            this.status = 5;
            try {
              this.applet.destroy();
            } catch (AccessControlException accessControlException) {
              setExceptionStatus(accessControlException);
              throw accessControlException;
            } 
            showAppletStatus("destroyed");
            break;
          case 0:
            if (this.status != 5 && this.status != 1) {
              showAppletStatus("notdestroyed");
              break;
            } 
            this.status = 0;
            try {
              final Applet a = this.applet;
              Runnable runnable = new Runnable() {
                  public void run() { AppletPanel.this.remove(a); }
                };
              AWTAccessor.getEventQueueAccessor().invokeAndWait(this.applet, runnable);
            } catch (InterruptedException interruptedException) {
            
            } catch (InvocationTargetException invocationTargetException) {}
            this.applet = null;
            showAppletStatus("disposed");
            bool = true;
            break;
          case 6:
            return;
        } 
      } catch (Exception exception) {
        this.status = 7;
        if (exception.getMessage() != null) {
          showAppletStatus("exception2", exception.getClass().getName(), exception.getMessage());
        } else {
          showAppletStatus("exception", exception.getClass().getName());
        } 
        showAppletException(exception);
      } catch (ThreadDeath threadDeath) {
        showAppletStatus("death");
        return;
      } catch (Error error) {
        this.status = 7;
        if (error.getMessage() != null) {
          showAppletStatus("error2", error.getClass().getName(), error.getMessage());
        } else {
          showAppletStatus("error", error.getClass().getName());
        } 
        showAppletException(error);
      } 
      clearLoadAbortRequest();
    } 
  }
  
  private Component getMostRecentFocusOwnerForWindow(Window paramWindow) {
    Method method = (Method)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Method method = null;
            try {
              method = java.awt.KeyboardFocusManager.class.getDeclaredMethod("getMostRecentFocusOwner", new Class[] { Window.class });
              method.setAccessible(true);
            } catch (Exception exception) {
              exception.printStackTrace();
            } 
            return method;
          }
        });
    if (method != null)
      try {
        return (Component)method.invoke(null, new Object[] { paramWindow });
      } catch (Exception exception) {
        exception.printStackTrace();
      }  
    return paramWindow.getMostRecentFocusOwner();
  }
  
  private void setDefaultFocus() {
    Component component = null;
    Container container = getParent();
    if (container != null)
      if (container instanceof Window) {
        component = getMostRecentFocusOwnerForWindow((Window)container);
        if (component == container || component == null)
          component = container.getFocusTraversalPolicy().getInitialComponent((Window)container); 
      } else if (container.isFocusCycleRoot()) {
        component = container.getFocusTraversalPolicy().getDefaultComponent(container);
      }  
    if (component != null) {
      if (container instanceof EmbeddedFrame)
        ((EmbeddedFrame)container).synthesizeWindowActivation(true); 
      component.requestFocusInWindow();
    } 
  }
  
  private void runLoader() {
    if (this.status != 0) {
      showAppletStatus("notdisposed");
      return;
    } 
    dispatchAppletEvent(51235, null);
    this.status = 1;
    this.loader = getClassLoader(getCodeBase(), getClassLoaderCacheKey());
    String str = getCode();
    setupAppletAppContext();
    try {
      loadJarFiles(this.loader);
      this.applet = createApplet(this.loader);
    } catch (ClassNotFoundException classNotFoundException) {
      this.status = 7;
      showAppletStatus("notfound", str);
      showAppletLog("notfound", str);
      showAppletException(classNotFoundException);
      return;
    } catch (InstantiationException instantiationException) {
      this.status = 7;
      showAppletStatus("nocreate", str);
      showAppletLog("nocreate", str);
      showAppletException(instantiationException);
      return;
    } catch (IllegalAccessException illegalAccessException) {
      this.status = 7;
      showAppletStatus("noconstruct", str);
      showAppletLog("noconstruct", str);
      showAppletException(illegalAccessException);
      return;
    } catch (Exception exception) {
      this.status = 7;
      showAppletStatus("exception", exception.getMessage());
      showAppletException(exception);
      return;
    } catch (ThreadDeath threadDeath) {
      this.status = 7;
      showAppletStatus("death");
      return;
    } catch (Error error) {
      this.status = 7;
      showAppletStatus("error", error.getMessage());
      showAppletException(error);
      return;
    } finally {
      dispatchAppletEvent(51236, null);
    } 
    if (this.applet != null) {
      this.applet.setStub(this);
      this.applet.hide();
      add("Center", this.applet);
      showAppletStatus("loaded");
      validate();
    } 
  }
  
  protected Applet createApplet(AppletClassLoader paramAppletClassLoader) throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException {
    String str1 = getSerializedObject();
    String str2 = getCode();
    if (str2 != null && str1 != null) {
      System.err.println(amh.getMessage("runloader.err"));
      throw new InstantiationException("Either \"code\" or \"object\" should be specified, but not both.");
    } 
    if (str2 == null && str1 == null) {
      String str = "nocode";
      this.status = 7;
      showAppletStatus(str);
      showAppletLog(str);
      repaint();
    } 
    if (str2 != null) {
      this.applet = (Applet)paramAppletClassLoader.loadCode(str2).newInstance();
      this.doInit = true;
    } else {
      try(InputStream null = (InputStream)AccessController.doPrivileged(() -> paramAppletClassLoader.getResourceAsStream(paramString)); AppletObjectInputStream null = new AppletObjectInputStream(inputStream, paramAppletClassLoader)) {
        this.applet = (Applet)appletObjectInputStream.readObject();
        this.doInit = false;
      } 
    } 
    findAppletJDKLevel(this.applet);
    if (Thread.interrupted()) {
      try {
        this.status = 0;
        this.applet = null;
        showAppletStatus("death");
      } finally {
        Thread.currentThread().interrupt();
      } 
      return null;
    } 
    return this.applet;
  }
  
  protected void loadJarFiles(AppletClassLoader paramAppletClassLoader) throws IOException, InterruptedException {
    String str = getJarFiles();
    if (str != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str, ",", false);
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken().trim();
        try {
          paramAppletClassLoader.addJar(str1);
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
    } 
  }
  
  protected void stopLoading() {
    if (this.loaderThread != null) {
      this.loaderThread.interrupt();
    } else {
      setLoadAbortRequest();
    } 
  }
  
  protected boolean okToLoad() { return !this.loadAbortRequest; }
  
  protected void clearLoadAbortRequest() { this.loadAbortRequest = false; }
  
  protected void setLoadAbortRequest() { this.loadAbortRequest = true; }
  
  private void setLoaderThread(Thread paramThread) { this.loaderThread = paramThread; }
  
  public boolean isActive() { return (this.status == 3); }
  
  public void appletResize(int paramInt1, int paramInt2) {
    this.currentAppletSize.width = paramInt1;
    this.currentAppletSize.height = paramInt2;
    Dimension dimension = new Dimension(this.currentAppletSize.width, this.currentAppletSize.height);
    if (this.loader != null) {
      AppContext appContext = this.loader.getAppContext();
      if (appContext != null)
        this.appEvtQ = (EventQueue)appContext.get(AppContext.EVENT_QUEUE_KEY); 
    } 
    AppletPanel appletPanel = this;
    if (this.appEvtQ != null)
      this.appEvtQ.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new Runnable(this, appletPanel, dimension) {
              public void run() {
                if (ap != null)
                  ap.dispatchAppletEvent(51234, currentSize); 
              }
            })); 
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    this.currentAppletSize.width = paramInt3;
    this.currentAppletSize.height = paramInt4;
  }
  
  public Applet getApplet() { return this.applet; }
  
  protected void showAppletStatus(String paramString) { getAppletContext().showStatus(amh.getMessage(paramString)); }
  
  protected void showAppletStatus(String paramString, Object paramObject) { getAppletContext().showStatus(amh.getMessage(paramString, paramObject)); }
  
  protected void showAppletStatus(String paramString, Object paramObject1, Object paramObject2) { getAppletContext().showStatus(amh.getMessage(paramString, paramObject1, paramObject2)); }
  
  protected void showAppletLog(String paramString) { System.out.println(amh.getMessage(paramString)); }
  
  protected void showAppletLog(String paramString, Object paramObject) { System.out.println(amh.getMessage(paramString, paramObject)); }
  
  protected void showAppletException(Throwable paramThrowable) {
    paramThrowable.printStackTrace();
    repaint();
  }
  
  public String getClassLoaderCacheKey() { return getCodeBase().toString(); }
  
  public static void flushClassLoader(String paramString) { classloaders.remove(paramString); }
  
  public static void flushClassLoaders() { classloaders = new HashMap(); }
  
  protected AppletClassLoader createClassLoader(URL paramURL) { return new AppletClassLoader(paramURL); }
  
  AppletClassLoader getClassLoader(final URL codebase, final String key) {
    AppletClassLoader appletClassLoader = (AppletClassLoader)classloaders.get(paramString);
    if (appletClassLoader == null) {
      AccessControlContext accessControlContext = getAccessControlContext(paramURL);
      appletClassLoader = (AppletClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              AppletClassLoader appletClassLoader = AppletPanel.this.createClassLoader(codebase);
              synchronized (getClass()) {
                AppletClassLoader appletClassLoader1 = (AppletClassLoader)classloaders.get(key);
                if (appletClassLoader1 == null) {
                  classloaders.put(key, appletClassLoader);
                  return appletClassLoader;
                } 
                return appletClassLoader1;
              } 
            }
          }accessControlContext);
    } 
    return appletClassLoader;
  }
  
  private AccessControlContext getAccessControlContext(URL paramURL) {
    Permission permission;
    PermissionCollection permissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Policy policy = Policy.getPolicy();
            return (policy != null) ? policy.getPermissions(new CodeSource(null, (Certificate[])null)) : null;
          }
        });
    if (permissionCollection == null)
      permissionCollection = new Permissions(); 
    permissionCollection.add(SecurityConstants.CREATE_CLASSLOADER_PERMISSION);
    URLConnection uRLConnection = null;
    try {
      uRLConnection = paramURL.openConnection();
      permission = uRLConnection.getPermission();
    } catch (IOException iOException) {
      permission = null;
    } 
    if (permission != null)
      permissionCollection.add(permission); 
    if (permission instanceof FilePermission) {
      String str = permission.getName();
      int i = str.lastIndexOf(File.separatorChar);
      if (i != -1) {
        str = str.substring(0, i + 1);
        if (str.endsWith(File.separator))
          str = str + "-"; 
        permissionCollection.add(new FilePermission(str, "read"));
      } 
    } else {
      URL uRL = paramURL;
      if (uRLConnection instanceof JarURLConnection)
        uRL = ((JarURLConnection)uRLConnection).getJarFileURL(); 
      String str = uRL.getHost();
      if (str != null && str.length() > 0)
        permissionCollection.add(new SocketPermission(str, "connect,accept")); 
    } 
    ProtectionDomain protectionDomain = new ProtectionDomain(new CodeSource(paramURL, (Certificate[])null), permissionCollection);
    return new AccessControlContext(new ProtectionDomain[] { protectionDomain });
  }
  
  public Thread getAppletHandlerThread() { return this.handler; }
  
  public int getAppletWidth() { return this.currentAppletSize.width; }
  
  public int getAppletHeight() { return this.currentAppletSize.height; }
  
  public static void changeFrameAppContext(Frame paramFrame, AppContext paramAppContext) {
    AppContext appContext = SunToolkit.targetToAppContext(paramFrame);
    if (appContext == paramAppContext)
      return; 
    synchronized (Window.class) {
      WeakReference weakReference = null;
      Vector vector = (Vector)appContext.get(Window.class);
      if (vector != null) {
        for (WeakReference weakReference1 : vector) {
          if (weakReference1.get() == paramFrame) {
            weakReference = weakReference1;
            break;
          } 
        } 
        if (weakReference != null)
          vector.remove(weakReference); 
      } 
      SunToolkit.insertTargetMapping(paramFrame, paramAppContext);
      vector = (Vector)paramAppContext.get(Window.class);
      if (vector == null) {
        vector = new Vector();
        paramAppContext.put(Window.class, vector);
      } 
      vector.add(weakReference);
    } 
  }
  
  private void findAppletJDKLevel(Applet paramApplet) {
    Class clazz = paramApplet.getClass();
    synchronized (clazz) {
      Boolean bool1 = this.loader.isJDK11Target(clazz);
      Boolean bool2 = this.loader.isJDK12Target(clazz);
      if (bool1 != null || bool2 != null) {
        this.jdk11Applet = (bool1 == null) ? false : bool1.booleanValue();
        this.jdk12Applet = (bool2 == null) ? false : bool2.booleanValue();
        return;
      } 
      String str1 = clazz.getName();
      str1 = str1.replace('.', '/');
      String str2 = str1 + ".class";
      byte[] arrayOfByte = new byte[8];
      try (InputStream null = (InputStream)AccessController.doPrivileged(() -> this.loader.getResourceAsStream(paramString))) {
        j = inputStream.read(arrayOfByte, 0, 8);
        if (j != 8)
          return; 
      } catch (IOException iOException) {
        return;
      } 
      int i = readShort(arrayOfByte, 6);
      if (i < 46) {
        this.jdk11Applet = true;
      } else if (i == 46) {
        this.jdk12Applet = true;
      } 
      this.loader.setJDK11Target(clazz, this.jdk11Applet);
      this.loader.setJDK12Target(clazz, this.jdk12Applet);
    } 
  }
  
  protected boolean isJDK11Applet() { return this.jdk11Applet; }
  
  protected boolean isJDK12Applet() { return this.jdk12Applet; }
  
  private int readShort(byte[] paramArrayOfByte, int paramInt) {
    int i = readByte(paramArrayOfByte[paramInt]);
    int j = readByte(paramArrayOfByte[paramInt + 1]);
    return i << 8 | j;
  }
  
  private int readByte(byte paramByte) { return paramByte & 0xFF; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */