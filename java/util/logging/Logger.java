package java.util.logging;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public class Logger {
  private static final Handler[] emptyHandlers = new Handler[0];
  
  private static final int offValue = Level.OFF.intValue();
  
  static final String SYSTEM_LOGGER_RB_NAME = "sun.util.logging.resources.logging";
  
  private static final LoggerBundle SYSTEM_BUNDLE = new LoggerBundle("sun.util.logging.resources.logging", null, null);
  
  private static final LoggerBundle NO_RESOURCE_BUNDLE = new LoggerBundle(null, null, null);
  
  private String name;
  
  private final CopyOnWriteArrayList<Handler> handlers = new CopyOnWriteArrayList();
  
  private boolean anonymous;
  
  private ResourceBundle catalog;
  
  private String catalogName;
  
  private Locale catalogLocale;
  
  private static final Object treeLock = new Object();
  
  private ArrayList<LogManager.LoggerWeakRef> kids;
  
  private WeakReference<ClassLoader> callersClassLoaderRef;
  
  private final boolean isSystemLogger;
  
  public static final String GLOBAL_LOGGER_NAME = "global";
  
  @Deprecated
  public static final Logger global = new Logger("global");
  
  public static final Logger getGlobal() {
    LogManager.getLogManager();
    return global;
  }
  
  protected Logger(String paramString1, String paramString2) { this(paramString1, paramString2, null, LogManager.getLogManager(), false); }
  
  Logger(String paramString1, String paramString2, Class<?> paramClass, LogManager paramLogManager, boolean paramBoolean) {
    this.manager = paramLogManager;
    this.isSystemLogger = paramBoolean;
    setupResourceInfo(paramString2, paramClass);
    this.name = paramString1;
    this.levelValue = Level.INFO.intValue();
  }
  
  private void setCallersClassLoaderRef(Class<?> paramClass) {
    ClassLoader classLoader = (paramClass != null) ? paramClass.getClassLoader() : null;
    if (classLoader != null)
      this.callersClassLoaderRef = new WeakReference(classLoader); 
  }
  
  private ClassLoader getCallersClassLoader() { return (this.callersClassLoaderRef != null) ? (ClassLoader)this.callersClassLoaderRef.get() : null; }
  
  private Logger(String paramString) {
    this.name = paramString;
    this.isSystemLogger = true;
    this.levelValue = Level.INFO.intValue();
  }
  
  void setLogManager(LogManager paramLogManager) { this.manager = paramLogManager; }
  
  private void checkPermission() throws SecurityException {
    if (!this.anonymous) {
      if (this.manager == null)
        this.manager = LogManager.getLogManager(); 
      this.manager.checkPermission();
    } 
  }
  
  private static Logger demandLogger(String paramString1, String paramString2, Class<?> paramClass) {
    LogManager logManager = LogManager.getLogManager();
    SecurityManager securityManager = System.getSecurityManager();
    return (securityManager != null && !SystemLoggerHelper.disableCallerCheck && paramClass.getClassLoader() == null) ? logManager.demandSystemLogger(paramString1, paramString2) : logManager.demandLogger(paramString1, paramString2, paramClass);
  }
  
  @CallerSensitive
  public static Logger getLogger(String paramString) { return demandLogger(paramString, null, Reflection.getCallerClass()); }
  
  @CallerSensitive
  public static Logger getLogger(String paramString1, String paramString2) {
    Class clazz = Reflection.getCallerClass();
    Logger logger = demandLogger(paramString1, paramString2, clazz);
    logger.setupResourceInfo(paramString2, clazz);
    return logger;
  }
  
  static Logger getPlatformLogger(String paramString) {
    LogManager logManager = LogManager.getLogManager();
    return logManager.demandSystemLogger(paramString, "sun.util.logging.resources.logging");
  }
  
  public static Logger getAnonymousLogger() { return getAnonymousLogger(null); }
  
  @CallerSensitive
  public static Logger getAnonymousLogger(String paramString) {
    LogManager logManager = LogManager.getLogManager();
    logManager.drainLoggerRefQueueBounded();
    Logger logger1 = new Logger(null, paramString, Reflection.getCallerClass(), logManager, false);
    logger1.anonymous = true;
    Logger logger2 = logManager.getLogger("");
    logger1.doSetParent(logger2);
    return logger1;
  }
  
  public ResourceBundle getResourceBundle() { return findResourceBundle(getResourceBundleName(), true); }
  
  public String getResourceBundleName() { return this.loggerBundle.resourceBundleName; }
  
  public void setFilter(Filter paramFilter) throws SecurityException {
    checkPermission();
    this.filter = paramFilter;
  }
  
  public Filter getFilter() { return this.filter; }
  
  public void log(LogRecord paramLogRecord) {
    if (!isLoggable(paramLogRecord.getLevel()))
      return; 
    Filter filter1 = this.filter;
    if (filter1 != null && !filter1.isLoggable(paramLogRecord))
      return; 
    for (Logger logger = this; logger != null; logger = this.isSystemLogger ? logger.parent : logger.getParent()) {
      Handler[] arrayOfHandler = this.isSystemLogger ? logger.accessCheckedHandlers() : logger.getHandlers();
      for (Handler handler : arrayOfHandler)
        handler.publish(paramLogRecord); 
      boolean bool = this.isSystemLogger ? logger.useParentHandlers : logger.getUseParentHandlers();
      if (!bool)
        break; 
    } 
  }
  
  private void doLog(LogRecord paramLogRecord) {
    paramLogRecord.setLoggerName(this.name);
    LoggerBundle loggerBundle1 = getEffectiveLoggerBundle();
    ResourceBundle resourceBundle = loggerBundle1.userBundle;
    String str = loggerBundle1.resourceBundleName;
    if (str != null && resourceBundle != null) {
      paramLogRecord.setResourceBundleName(str);
      paramLogRecord.setResourceBundle(resourceBundle);
    } 
    log(paramLogRecord);
  }
  
  public void log(Level paramLevel, String paramString) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString);
    doLog(logRecord);
  }
  
  public void log(Level paramLevel, Supplier<String> paramSupplier) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    doLog(logRecord);
  }
  
  public void log(Level paramLevel, String paramString, Object paramObject) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString);
    Object[] arrayOfObject = { paramObject };
    logRecord.setParameters(arrayOfObject);
    doLog(logRecord);
  }
  
  public void log(Level paramLevel, String paramString, Object[] paramArrayOfObject) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString);
    logRecord.setParameters(paramArrayOfObject);
    doLog(logRecord);
  }
  
  public void log(Level paramLevel, String paramString, Throwable paramThrowable) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString);
    logRecord.setThrown(paramThrowable);
    doLog(logRecord);
  }
  
  public void log(Level paramLevel, Throwable paramThrowable, Supplier<String> paramSupplier) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    logRecord.setThrown(paramThrowable);
    doLog(logRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString3);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    doLog(logRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, Supplier<String> paramSupplier) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    doLog(logRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Object paramObject) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString3);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    Object[] arrayOfObject = { paramObject };
    logRecord.setParameters(arrayOfObject);
    doLog(logRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString3);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    logRecord.setParameters(paramArrayOfObject);
    doLog(logRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, String paramString3, Throwable paramThrowable) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString3);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    logRecord.setThrown(paramThrowable);
    doLog(logRecord);
  }
  
  public void logp(Level paramLevel, String paramString1, String paramString2, Throwable paramThrowable, Supplier<String> paramSupplier) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, (String)paramSupplier.get());
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    logRecord.setThrown(paramThrowable);
    doLog(logRecord);
  }
  
  private void doLog(LogRecord paramLogRecord, String paramString) {
    paramLogRecord.setLoggerName(this.name);
    if (paramString != null) {
      paramLogRecord.setResourceBundleName(paramString);
      paramLogRecord.setResourceBundle(findResourceBundle(paramString, false));
    } 
    log(paramLogRecord);
  }
  
  private void doLog(LogRecord paramLogRecord, ResourceBundle paramResourceBundle) {
    paramLogRecord.setLoggerName(this.name);
    if (paramResourceBundle != null) {
      paramLogRecord.setResourceBundleName(paramResourceBundle.getBaseBundleName());
      paramLogRecord.setResourceBundle(paramResourceBundle);
    } 
    log(paramLogRecord);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString4);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    doLog(logRecord, paramString3);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString4);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    Object[] arrayOfObject = { paramObject };
    logRecord.setParameters(arrayOfObject);
    doLog(logRecord, paramString3);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Object[] paramArrayOfObject) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString4);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    logRecord.setParameters(paramArrayOfObject);
    doLog(logRecord, paramString3);
  }
  
  public void logrb(Level paramLevel, String paramString1, String paramString2, ResourceBundle paramResourceBundle, String paramString3, Object... paramVarArgs) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString3);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    if (paramVarArgs != null && paramVarArgs.length != 0)
      logRecord.setParameters(paramVarArgs); 
    doLog(logRecord, paramResourceBundle);
  }
  
  @Deprecated
  public void logrb(Level paramLevel, String paramString1, String paramString2, String paramString3, String paramString4, Throwable paramThrowable) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString4);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    logRecord.setThrown(paramThrowable);
    doLog(logRecord, paramString3);
  }
  
  public void logrb(Level paramLevel, String paramString1, String paramString2, ResourceBundle paramResourceBundle, String paramString3, Throwable paramThrowable) {
    if (!isLoggable(paramLevel))
      return; 
    LogRecord logRecord = new LogRecord(paramLevel, paramString3);
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    logRecord.setThrown(paramThrowable);
    doLog(logRecord, paramResourceBundle);
  }
  
  public void entering(String paramString1, String paramString2) { logp(Level.FINER, paramString1, paramString2, "ENTRY"); }
  
  public void entering(String paramString1, String paramString2, Object paramObject) { logp(Level.FINER, paramString1, paramString2, "ENTRY {0}", paramObject); }
  
  public void entering(String paramString1, String paramString2, Object[] paramArrayOfObject) {
    String str = "ENTRY";
    if (paramArrayOfObject == null) {
      logp(Level.FINER, paramString1, paramString2, str);
      return;
    } 
    if (!isLoggable(Level.FINER))
      return; 
    for (byte b = 0; b < paramArrayOfObject.length; b++)
      str = str + " {" + b + "}"; 
    logp(Level.FINER, paramString1, paramString2, str, paramArrayOfObject);
  }
  
  public void exiting(String paramString1, String paramString2) { logp(Level.FINER, paramString1, paramString2, "RETURN"); }
  
  public void exiting(String paramString1, String paramString2, Object paramObject) { logp(Level.FINER, paramString1, paramString2, "RETURN {0}", paramObject); }
  
  public void throwing(String paramString1, String paramString2, Throwable paramThrowable) {
    if (!isLoggable(Level.FINER))
      return; 
    LogRecord logRecord = new LogRecord(Level.FINER, "THROW");
    logRecord.setSourceClassName(paramString1);
    logRecord.setSourceMethodName(paramString2);
    logRecord.setThrown(paramThrowable);
    doLog(logRecord);
  }
  
  public void severe(String paramString) { log(Level.SEVERE, paramString); }
  
  public void warning(String paramString) { log(Level.WARNING, paramString); }
  
  public void info(String paramString) { log(Level.INFO, paramString); }
  
  public void config(String paramString) { log(Level.CONFIG, paramString); }
  
  public void fine(String paramString) { log(Level.FINE, paramString); }
  
  public void finer(String paramString) { log(Level.FINER, paramString); }
  
  public void finest(String paramString) { log(Level.FINEST, paramString); }
  
  public void severe(Supplier<String> paramSupplier) { log(Level.SEVERE, paramSupplier); }
  
  public void warning(Supplier<String> paramSupplier) { log(Level.WARNING, paramSupplier); }
  
  public void info(Supplier<String> paramSupplier) { log(Level.INFO, paramSupplier); }
  
  public void config(Supplier<String> paramSupplier) { log(Level.CONFIG, paramSupplier); }
  
  public void fine(Supplier<String> paramSupplier) { log(Level.FINE, paramSupplier); }
  
  public void finer(Supplier<String> paramSupplier) { log(Level.FINER, paramSupplier); }
  
  public void finest(Supplier<String> paramSupplier) { log(Level.FINEST, paramSupplier); }
  
  public void setLevel(Level paramLevel) throws SecurityException {
    checkPermission();
    synchronized (treeLock) {
      this.levelObject = paramLevel;
      updateEffectiveLevel();
    } 
  }
  
  final boolean isLevelInitialized() { return (this.levelObject != null); }
  
  public Level getLevel() { return this.levelObject; }
  
  public boolean isLoggable(Level paramLevel) { return !(paramLevel.intValue() < this.levelValue || this.levelValue == offValue); }
  
  public String getName() { return this.name; }
  
  public void addHandler(Handler paramHandler) throws SecurityException {
    paramHandler.getClass();
    checkPermission();
    this.handlers.add(paramHandler);
  }
  
  public void removeHandler(Handler paramHandler) throws SecurityException {
    checkPermission();
    if (paramHandler == null)
      return; 
    this.handlers.remove(paramHandler);
  }
  
  public Handler[] getHandlers() { return accessCheckedHandlers(); }
  
  Handler[] accessCheckedHandlers() { return (Handler[])this.handlers.toArray(emptyHandlers); }
  
  public void setUseParentHandlers(boolean paramBoolean) {
    checkPermission();
    this.useParentHandlers = paramBoolean;
  }
  
  public boolean getUseParentHandlers() { return this.useParentHandlers; }
  
  private static ResourceBundle findSystemResourceBundle(final Locale locale) { return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
          public ResourceBundle run() {
            try {
              return ResourceBundle.getBundle("sun.util.logging.resources.logging", locale);
            } catch (MissingResourceException missingResourceException) {
              throw new InternalError(missingResourceException.toString());
            } 
          }
        }); }
  
  private ResourceBundle findResourceBundle(String paramString, boolean paramBoolean) {
    if (paramString == null)
      return null; 
    Locale locale = Locale.getDefault();
    LoggerBundle loggerBundle1 = this.loggerBundle;
    if (loggerBundle1.userBundle != null && paramString.equals(loggerBundle1.resourceBundleName))
      return loggerBundle1.userBundle; 
    if (this.catalog != null && locale.equals(this.catalogLocale) && paramString.equals(this.catalogName))
      return this.catalog; 
    if (paramString.equals("sun.util.logging.resources.logging")) {
      this.catalog = findSystemResourceBundle(locale);
      this.catalogName = paramString;
      this.catalogLocale = locale;
      return this.catalog;
    } 
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null)
      classLoader = ClassLoader.getSystemClassLoader(); 
    try {
      this.catalog = ResourceBundle.getBundle(paramString, locale, classLoader);
      this.catalogName = paramString;
      this.catalogLocale = locale;
      return this.catalog;
    } catch (MissingResourceException missingResourceException) {
      if (paramBoolean) {
        ClassLoader classLoader1 = getCallersClassLoader();
        if (classLoader1 == null || classLoader1 == classLoader)
          return null; 
        try {
          this.catalog = ResourceBundle.getBundle(paramString, locale, classLoader1);
          this.catalogName = paramString;
          this.catalogLocale = locale;
          return this.catalog;
        } catch (MissingResourceException missingResourceException1) {
          return null;
        } 
      } 
      return null;
    } 
  }
  
  private void setupResourceInfo(String paramString, Class<?> paramClass) {
    LoggerBundle loggerBundle1 = this.loggerBundle;
    if (loggerBundle1.resourceBundleName != null) {
      if (loggerBundle1.resourceBundleName.equals(paramString))
        return; 
      throw new IllegalArgumentException(loggerBundle1.resourceBundleName + " != " + paramString);
    } 
    if (paramString == null)
      return; 
    setCallersClassLoaderRef(paramClass);
    if (this.isSystemLogger && getCallersClassLoader() != null)
      checkPermission(); 
    if (findResourceBundle(paramString, true) == null) {
      this.callersClassLoaderRef = null;
      throw new MissingResourceException("Can't find " + paramString + " bundle", paramString, "");
    } 
    assert loggerBundle1.userBundle == null;
    this.loggerBundle = LoggerBundle.get(paramString, null);
  }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle) {
    checkPermission();
    String str = paramResourceBundle.getBaseBundleName();
    if (str == null || str.isEmpty())
      throw new IllegalArgumentException("resource bundle must have a name"); 
    synchronized (this) {
      LoggerBundle loggerBundle1 = this.loggerBundle;
      boolean bool = (loggerBundle1.resourceBundleName == null || loggerBundle1.resourceBundleName.equals(str)) ? 1 : 0;
      if (!bool)
        throw new IllegalArgumentException("can't replace resource bundle"); 
      this.loggerBundle = LoggerBundle.get(str, paramResourceBundle);
    } 
  }
  
  public Logger getParent() { return this.parent; }
  
  public void setParent(Logger paramLogger) {
    if (paramLogger == null)
      throw new NullPointerException(); 
    if (this.manager == null)
      this.manager = LogManager.getLogManager(); 
    this.manager.checkPermission();
    doSetParent(paramLogger);
  }
  
  private void doSetParent(Logger paramLogger) {
    synchronized (treeLock) {
      LogManager.LoggerWeakRef loggerWeakRef = null;
      if (this.parent != null) {
        Iterator iterator = this.parent.kids.iterator();
        while (iterator.hasNext()) {
          loggerWeakRef = (LogManager.LoggerWeakRef)iterator.next();
          Logger logger = (Logger)loggerWeakRef.get();
          if (logger == this) {
            iterator.remove();
            break;
          } 
          loggerWeakRef = null;
        } 
      } 
      this.parent = paramLogger;
      if (this.parent.kids == null)
        this.parent.kids = new ArrayList(2); 
      if (loggerWeakRef == null) {
        this.manager.getClass();
        loggerWeakRef = new LogManager.LoggerWeakRef(this.manager, this);
      } 
      loggerWeakRef.setParentRef(new WeakReference(this.parent));
      this.parent.kids.add(loggerWeakRef);
      updateEffectiveLevel();
    } 
  }
  
  final void removeChildLogger(LogManager.LoggerWeakRef paramLoggerWeakRef) {
    synchronized (treeLock) {
      Iterator iterator = this.kids.iterator();
      while (iterator.hasNext()) {
        LogManager.LoggerWeakRef loggerWeakRef = (LogManager.LoggerWeakRef)iterator.next();
        if (loggerWeakRef == paramLoggerWeakRef) {
          iterator.remove();
          return;
        } 
      } 
    } 
  }
  
  private void updateEffectiveLevel() throws SecurityException {
    int i;
    if (this.levelObject != null) {
      i = this.levelObject.intValue();
    } else if (this.parent != null) {
      i = this.parent.levelValue;
    } else {
      i = Level.INFO.intValue();
    } 
    if (this.levelValue == i)
      return; 
    this.levelValue = i;
    if (this.kids != null)
      for (byte b = 0; b < this.kids.size(); b++) {
        LogManager.LoggerWeakRef loggerWeakRef = (LogManager.LoggerWeakRef)this.kids.get(b);
        Logger logger = (Logger)loggerWeakRef.get();
        if (logger != null)
          logger.updateEffectiveLevel(); 
      }  
  }
  
  private LoggerBundle getEffectiveLoggerBundle() {
    LoggerBundle loggerBundle1 = this.loggerBundle;
    if (loggerBundle1.isSystemBundle())
      return SYSTEM_BUNDLE; 
    ResourceBundle resourceBundle = getResourceBundle();
    if (resourceBundle != null && resourceBundle == loggerBundle1.userBundle)
      return loggerBundle1; 
    if (resourceBundle != null) {
      String str = getResourceBundleName();
      return LoggerBundle.get(str, resourceBundle);
    } 
    for (Logger logger = this.parent; logger != null; logger = this.isSystemLogger ? logger.parent : logger.getParent()) {
      LoggerBundle loggerBundle2 = logger.loggerBundle;
      if (loggerBundle2.isSystemBundle())
        return SYSTEM_BUNDLE; 
      if (loggerBundle2.userBundle != null)
        return loggerBundle2; 
      String str = this.isSystemLogger ? (logger.isSystemLogger ? loggerBundle2.resourceBundleName : null) : logger.getResourceBundleName();
      if (str != null)
        return LoggerBundle.get(str, findResourceBundle(str, true)); 
    } 
    return NO_RESOURCE_BUNDLE;
  }
  
  private static final class LoggerBundle {
    final String resourceBundleName;
    
    final ResourceBundle userBundle;
    
    private LoggerBundle(String param1String, ResourceBundle param1ResourceBundle) {
      this.resourceBundleName = param1String;
      this.userBundle = param1ResourceBundle;
    }
    
    boolean isSystemBundle() { return "sun.util.logging.resources.logging".equals(this.resourceBundleName); }
    
    static LoggerBundle get(String param1String, ResourceBundle param1ResourceBundle) { return (param1String == null && param1ResourceBundle == null) ? NO_RESOURCE_BUNDLE : (("sun.util.logging.resources.logging".equals(param1String) && param1ResourceBundle == null) ? SYSTEM_BUNDLE : new LoggerBundle(param1String, param1ResourceBundle)); }
  }
  
  private static class SystemLoggerHelper {
    static boolean disableCallerCheck = getBooleanProperty("sun.util.logging.disableCallerCheck");
    
    private static boolean getBooleanProperty(final String key) {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return System.getProperty(key); }
          });
      return Boolean.valueOf(str).booleanValue();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\Logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */