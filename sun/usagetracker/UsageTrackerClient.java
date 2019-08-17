package sun.usagetracker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import jdk.internal.util.EnvUtils;

public final class UsageTrackerClient {
  private static final Object LOCK = new Object();
  
  private static final String ORCL_UT_CONFIG_FILE_NAME = "usagetracker.properties";
  
  private static final String ORCL_UT_USAGE_DIR = ".oracle_jre_usage";
  
  private static final String ORCL_UT_PROPERTY_NAME = "com.oracle.usagetracker.";
  
  private static final String ORCL_UT_PROPERTY_RUN_SYNCHRONOUSLY = "com.oracle.usagetracker.run.synchronous";
  
  private static final String ORCL_UT_PROPERTY_CONFIG_FILE_NAME = "com.oracle.usagetracker.config.file";
  
  private static final String ORCL_UT_LOGTOFILE = "com.oracle.usagetracker.logToFile";
  
  private static final String ORCL_UT_LOGFILEMAXSIZE = "com.oracle.usagetracker.logFileMaxSize";
  
  private static final String ORCL_UT_LOGTOUDP = "com.oracle.usagetracker.logToUDP";
  
  private static final String ORCL_UT_RECORD_MAXSIZE = "com.oracle.usagetracker.maxSize";
  
  private static final String ORCL_UT_RECORD_MAXFIELDSIZE = "com.oracle.usagetracker.maxFieldSize";
  
  private static final String ORCL_UT_SEND_TRUNCATED = "com.oracle.usagetracker.sendTruncatedRecords";
  
  private static final String ORCL_UT_TRACK_LAST_USAGE = "com.oracle.usagetracker.track.last.usage";
  
  private static final String ORCL_UT_VERBOSE = "com.oracle.usagetracker.verbose";
  
  private static final String ORCL_UT_DEBUG = "com.oracle.usagetracker.debug";
  
  private static final String ORCL_UT_ADDITIONALPROPERTIES = "com.oracle.usagetracker.additionalProperties";
  
  private static final String ORCL_UT_SEPARATOR = "com.oracle.usagetracker.separator";
  
  private static final String ORCL_UT_QUOTE = "com.oracle.usagetracker.quote";
  
  private static final String ORCL_UT_QUOTE_INNER = "com.oracle.usagetracker.innerQuote";
  
  private static final String DISABLE_LAST_USAGE_PROP_NAME = "jdk.disableLastUsageTracking";
  
  private static final String DEFAULT_SEP = ",";
  
  private static final String DEFAULT_QUOTE = "\"";
  
  private static final String DEFAULT_QUOTE_INNER = "'";
  
  private static final AtomicBoolean isFirstRun = new AtomicBoolean(true);
  
  private static final String javaHome = getPropertyPrivileged("java.home");
  
  private static final String userHomeKeyword = "${user.home}";
  
  private static String separator;
  
  private static String quote;
  
  private static String innerQuote;
  
  private static boolean enabled;
  
  private static boolean verbose;
  
  private static boolean debug;
  
  private static boolean trackTime = initTrackTime();
  
  private static String[] additionalProperties;
  
  private static String fullLogFilename;
  
  private static long logFileMaxSize;
  
  private static int maxSize;
  
  private static int maxFieldSize;
  
  private static boolean sendTruncated;
  
  private static String datagramHost;
  
  private static int datagramPort;
  
  private static String staticMessage;
  
  private static boolean staticMessageIsTruncated;
  
  private static String getPropertyPrivileged(String paramString) { return getPropertyPrivileged(paramString, null); }
  
  private static String getPropertyPrivileged(final String property, final String defaultValue) { return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return System.getProperty(property, defaultValue); }
        }); }
  
  private static String getEnvPrivileged(final String envName) { return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return EnvUtils.getEnvVar(envName); }
        }); }
  
  private static boolean initTrackTime() {
    String str = getPropertyPrivileged("jdk.disableLastUsageTracking");
    return (str == null) ? true : ((!str.isEmpty() && !str.equalsIgnoreCase("true")));
  }
  
  private static File getConfigFilePrivileged() {
    File file = null;
    String[] arrayOfString = new String[3];
    arrayOfString[0] = getPropertyPrivileged("com.oracle.usagetracker.config.file");
    arrayOfString[1] = getOSSpecificConfigFilePath();
    arrayOfString[2] = javaHome + File.separator + "lib" + File.separator + "management" + File.separator + "usagetracker.properties";
    for (String str : arrayOfString) {
      if (str != null) {
        file = (File)AccessController.doPrivileged(new PrivilegedAction<File>() {
              public File run() {
                File file = new File(path);
                return file.exists() ? file : null;
              }
            });
        if (file != null)
          break; 
      } 
    } 
    return file;
  }
  
  private static String getOSSpecificConfigFilePath() {
    String str = getPropertyPrivileged("os.name");
    if (str != null) {
      if (str.toLowerCase().startsWith("sunos"))
        return "/etc/oracle/java/usagetracker.properties"; 
      if (str.toLowerCase().startsWith("mac"))
        return "/Library/Application Support/Oracle/Java/usagetracker.properties"; 
      if (str.toLowerCase().startsWith("win")) {
        String str1 = getEnvPrivileged("ProgramFiles");
        return (str1 == null) ? null : (str1 + "\\Java\\conf\\" + "usagetracker.properties");
      } 
      if (str.toLowerCase().startsWith("linux"))
        return "/etc/oracle/java/usagetracker.properties"; 
    } 
    return null;
  }
  
  private String getFullLogFilename(Properties paramProperties) {
    String str = paramProperties.getProperty("com.oracle.usagetracker.logToFile", "");
    if (str.isEmpty())
      return null; 
    if (str.startsWith("${user.home}")) {
      if (str.length() > "${user.home}".length()) {
        str = getPropertyPrivileged("user.home") + str.substring("${user.home}".length());
      } else {
        printVerbose("UsageTracker: blank filename after user.home.");
        return null;
      } 
    } else if (!(new File(str)).isAbsolute()) {
      printVerbose("UsageTracker: relative path disallowed.");
      return null;
    } 
    return str;
  }
  
  private long getPropertyValueLong(Properties paramProperties, String paramString) {
    String str = paramProperties.getProperty(paramString, "");
    if (!str.isEmpty())
      try {
        return Long.parseLong(str);
      } catch (NumberFormatException numberFormatException) {
        printVerbose("UsageTracker: bad value: " + paramString);
      }  
    return -1L;
  }
  
  private boolean getPropertyValueBoolean(Properties paramProperties, String paramString, boolean paramBoolean) {
    String str = paramProperties.getProperty(paramString, "");
    return !str.isEmpty() ? Boolean.parseBoolean(str) : paramBoolean;
  }
  
  private String[] getAdditionalProperties(Properties paramProperties) {
    String str = paramProperties.getProperty("com.oracle.usagetracker.additionalProperties", "");
    return str.isEmpty() ? new String[0] : str.split(",");
  }
  
  private String parseDatagramHost(String paramString) {
    if (paramString != null) {
      int i = paramString.indexOf(':');
      if (i > 0 && i < paramString.length() - 1)
        return paramString.substring(0, i); 
      printVerbose("UsageTracker: bad UDP details.");
    } 
    return null;
  }
  
  private int parseDatagramPort(String paramString) {
    if (paramString != null) {
      int i = paramString.indexOf(':');
      try {
        return Integer.parseInt(paramString.substring(i + 1));
      } catch (Exception exception) {
        printVerbose("UsageTracker: bad UDP port.");
      } 
    } 
    return 0;
  }
  
  private void printVerbose(String paramString) {
    if (verbose)
      System.err.println(paramString); 
  }
  
  private void printDebug(String paramString) {
    if (debug)
      System.err.println(paramString); 
  }
  
  private void printDebugStackTrace(Throwable paramThrowable) {
    if (debug)
      paramThrowable.printStackTrace(); 
  }
  
  private void setupAndTimestamp(long paramLong) {
    if (isFirstRun.compareAndSet(true, false)) {
      File file = getConfigFilePrivileged();
      if (file != null)
        setup(file); 
      if (trackTime)
        registerUsage(paramLong); 
    } 
  }
  
  public void run(final String callerName, final String javaCommand) {
    printDebug("UsageTracker.run: " + paramString1 + ", javaCommand: " + paramString2);
    try {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              long l = System.currentTimeMillis();
              boolean bool = Boolean.parseBoolean(System.getProperty("com.oracle.usagetracker.run.synchronous", "true"));
              if (bool) {
                UsageTrackerClient.this.setupAndTimestamp(l);
                UsageTrackerClient.this.printVerbose("UsageTracker: running synchronous.");
              } 
              if (enabled || !bool) {
                UsageTrackerClient.UsageTrackerRunnable usageTrackerRunnable = new UsageTrackerClient.UsageTrackerRunnable(UsageTrackerClient.this, callerName, javaCommand, l, !bool);
                ThreadGroup threadGroup;
                for (threadGroup = Thread.currentThread().getThreadGroup(); threadGroup.getParent() != null; threadGroup = threadGroup.getParent());
                Thread thread = new Thread(threadGroup, usageTrackerRunnable, "UsageTracker");
                thread.setDaemon(true);
                thread.start();
              } 
              return null;
            }
          });
    } catch (Throwable throwable) {
      printVerbose("UsageTracker: error in starting thread.");
      printDebugStackTrace(throwable);
    } 
  }
  
  private void setup(File paramFile) {
    Properties properties = new Properties();
    if (paramFile != null)
      try(FileInputStream null = new FileInputStream(paramFile); BufferedInputStream null = new BufferedInputStream(fileInputStream)) {
        properties.load(bufferedInputStream);
      } catch (Exception exception) {
        properties.clear();
      }  
    verbose = getPropertyValueBoolean(properties, "com.oracle.usagetracker.verbose", false);
    debug = getPropertyValueBoolean(properties, "com.oracle.usagetracker.debug", false);
    separator = properties.getProperty("com.oracle.usagetracker.separator", ",");
    quote = properties.getProperty("com.oracle.usagetracker.quote", "\"");
    innerQuote = properties.getProperty("com.oracle.usagetracker.innerQuote", "'");
    fullLogFilename = getFullLogFilename(properties);
    logFileMaxSize = getPropertyValueLong(properties, "com.oracle.usagetracker.logFileMaxSize");
    maxSize = (int)getPropertyValueLong(properties, "com.oracle.usagetracker.maxSize");
    maxFieldSize = (int)getPropertyValueLong(properties, "com.oracle.usagetracker.maxFieldSize");
    sendTruncated = getPropertyValueBoolean(properties, "com.oracle.usagetracker.sendTruncatedRecords", true);
    additionalProperties = getAdditionalProperties(properties);
    String str = properties.getProperty("com.oracle.usagetracker.logToUDP");
    datagramHost = parseDatagramHost(str);
    datagramPort = parseDatagramPort(str);
    enabled = (((fullLogFilename != null || (datagramHost != null && datagramPort > 0)) ? 1 : 0) == true);
    if (trackTime)
      trackTime = getPropertyValueBoolean(properties, "com.oracle.usagetracker.track.last.usage", true); 
  }
  
  private void registerUsage(long paramLong) {
    try {
      String str1 = (new File(System.getProperty("java.home"))).getCanonicalPath();
      String str2 = getPropertyPrivileged("os.name");
      File file = null;
      if (str2.toLowerCase().startsWith("win")) {
        String str = getEnvPrivileged("ProgramData");
        if (str != null) {
          file = new File(str + File.separator + "Oracle" + File.separator + "Java" + File.separator + ".oracle_jre_usage", getPathHash(str1) + ".timestamp");
          if (!file.exists()) {
            if (!file.getParentFile().exists())
              if (file.getParentFile().mkdirs()) {
                String str3 = getEnvPrivileged("SYSTEMROOT");
                File file1 = new File(str3 + File.separator + "system32" + File.separator + "icacls.exe");
                if (file1.exists())
                  Runtime.getRuntime().exec(file1 + " " + file.getParentFile() + " /grant \"everyone\":(OI)(CI)M"); 
              } else {
                file = null;
              }  
            if (file != null)
              file.createNewFile(); 
          } 
        } 
      } else {
        String str = System.getProperty("user.home");
        if (str != null) {
          file = new File(str + File.separator + ".oracle_jre_usage", getPathHash(str1) + ".timestamp");
          if (!file.exists()) {
            if (!file.getParentFile().exists())
              file.getParentFile().mkdirs(); 
            file.createNewFile();
          } 
        } 
      } 
      if (file != null)
        try (FileOutputStream null = new FileOutputStream(file)) {
          str = str1 + System.lineSeparator() + paramLong + System.lineSeparator();
          fileOutputStream.write(str.getBytes("UTF-8"));
        } catch (IOException iOException) {
          printDebugStackTrace(iOException);
        }  
    } catch (IOException iOException) {
      printDebugStackTrace(iOException);
    } 
  }
  
  private String getPathHash(String paramString) {
    long l = 0L;
    for (byte b = 0; b < paramString.length(); b++)
      l = 31L * l + paramString.charAt(b); 
    return Long.toHexString(l);
  }
  
  class UsageTrackerRunnable implements Runnable {
    private String callerName;
    
    private String javaCommand;
    
    private long timestamp;
    
    private boolean runAsync;
    
    private boolean truncated;
    
    UsageTrackerRunnable(String param1String1, String param1String2, long param1Long, boolean param1Boolean) {
      this.callerName = param1String1;
      this.javaCommand = (param1String2 != null) ? param1String2 : "";
      this.timestamp = param1Long;
      this.runAsync = param1Boolean;
    }
    
    private String limitString(String param1String, int param1Int) {
      if (param1Int > 0 && param1String.length() >= param1Int) {
        UsageTrackerClient.this.printDebug("UsgeTracker: limitString truncating: max=" + param1Int + " length=" + param1String.length() + " String: " + param1String);
        this.truncated = true;
        param1String = param1String.substring(0, param1Int);
      } 
      return param1String;
    }
    
    private String buildMessage(String param1String1, String param1String2, long param1Long) {
      param1String2 = limitString(param1String2, maxFieldSize);
      if (this.truncated && !sendTruncated)
        return null; 
      StringBuilder stringBuilder = new StringBuilder();
      appendWithQuotes(stringBuilder, param1String1);
      stringBuilder.append(separator);
      Date date = new Date(param1Long);
      appendWithQuotes(stringBuilder, date.toString());
      stringBuilder.append(separator);
      String str1 = "0";
      try {
        InetAddress inetAddress = InetAddress.getLocalHost();
        str1 = inetAddress.toString();
      } catch (Throwable throwable) {}
      appendWithQuotes(stringBuilder, str1);
      stringBuilder.append(separator);
      appendWithQuotes(stringBuilder, param1String2);
      stringBuilder.append(separator);
      stringBuilder.append(getRuntimeDetails());
      stringBuilder.append("\n");
      String str2 = limitString(stringBuilder.toString(), maxSize);
      if (this.truncated && !sendTruncated) {
        UsageTrackerClient.this.printVerbose("UsageTracker: length limit exceeded.");
        return null;
      } 
      return str2;
    }
    
    private String getRuntimeDetails() {
      synchronized (LOCK) {
        if (staticMessage == null) {
          StringBuilder stringBuilder1 = new StringBuilder();
          boolean bool = this.truncated;
          this.truncated = false;
          appendWithQuotes(stringBuilder1, javaHome);
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.version"));
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vm.version"));
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vendor"));
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.vm.vendor"));
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.name"));
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.arch"));
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("os.version"));
          stringBuilder1.append(separator);
          List list = getInputArguments();
          StringBuilder stringBuilder2 = new StringBuilder();
          for (String str : list) {
            stringBuilder2.append(addQuotesFor(str, " ", innerQuote));
            stringBuilder2.append(' ');
          } 
          appendWithQuotes(stringBuilder1, stringBuilder2.toString());
          stringBuilder1.append(separator);
          appendWithQuotes(stringBuilder1, UsageTrackerClient.getPropertyPrivileged("java.class.path"));
          stringBuilder1.append(separator);
          StringBuilder stringBuilder3 = new StringBuilder();
          for (String str : additionalProperties) {
            stringBuilder3.append(str.trim());
            stringBuilder3.append("=");
            stringBuilder3.append(addQuotesFor(UsageTrackerClient.getPropertyPrivileged(str.trim()), " ", innerQuote));
            stringBuilder3.append(" ");
          } 
          appendWithQuotes(stringBuilder1, stringBuilder3.toString());
          staticMessage = stringBuilder1.toString();
          staticMessageIsTruncated = this.truncated;
          this.truncated = bool | staticMessageIsTruncated;
        } else {
          this.truncated |= staticMessageIsTruncated;
        } 
        return staticMessage;
      } 
    }
    
    private void appendWithQuotes(StringBuilder param1StringBuilder, String param1String) {
      param1StringBuilder.append(quote);
      param1String = limitString(param1String, maxFieldSize);
      param1String = param1String.replace(quote, quote + quote);
      if (!param1String.isEmpty()) {
        param1StringBuilder.append(param1String);
      } else {
        param1StringBuilder.append(" ");
      } 
      param1StringBuilder.append(quote);
    }
    
    private String addQuotesFor(String param1String1, String param1String2, String param1String3) {
      if (param1String1 == null)
        return param1String1; 
      param1String1 = param1String1.replace(param1String3, param1String3 + param1String3);
      if (param1String1.indexOf(param1String2) >= 0)
        param1String1 = param1String3 + param1String1 + param1String3; 
      return param1String1;
    }
    
    private List<String> getInputArguments() { return (List)AccessController.doPrivileged(new PrivilegedAction<List<String>>() {
            public List<String> run() {
              try {
                Class clazz = Class.forName("java.lang.management.ManagementFactory", true, null);
                Method method = clazz.getMethod("getRuntimeMXBean", (Class[])null);
                Object object = method.invoke(null, (Object[])null);
                clazz = Class.forName("java.lang.management.RuntimeMXBean", true, null);
                method = clazz.getMethod("getInputArguments", (Class[])null);
                return (List)method.invoke(object, (Object[])null);
              } catch (ClassNotFoundException classNotFoundException) {
                return Collections.singletonList("n/a");
              } catch (NoSuchMethodException noSuchMethodException) {
                throw new AssertionError(noSuchMethodException);
              } catch (IllegalAccessException illegalAccessException) {
                throw new AssertionError(illegalAccessException);
              } catch (InvocationTargetException invocationTargetException) {
                throw new AssertionError(invocationTargetException.getCause());
              } 
            }
          }); }
    
    private void sendDatagram(String param1String) {
      UsageTrackerClient.this.printDebug("UsageTracker: sendDatagram");
      try (DatagramSocket null = new DatagramSocket()) {
        arrayOfByte = param1String.getBytes("UTF-8");
        if (arrayOfByte.length > datagramSocket.getSendBufferSize())
          UsageTrackerClient.this.printVerbose("UsageTracker: message truncated for Datagram."); 
        UsageTrackerClient.this.printDebug("UsageTracker: host=" + datagramHost + ", port=" + datagramPort);
        UsageTrackerClient.this.printDebug("UsageTracker: SendBufferSize = " + datagramSocket.getSendBufferSize());
        UsageTrackerClient.this.printDebug("UsageTracker: packet length  = " + arrayOfByte.length);
        InetAddress inetAddress = InetAddress.getByName(datagramHost);
        DatagramPacket datagramPacket = new DatagramPacket(arrayOfByte, (arrayOfByte.length > datagramSocket.getSendBufferSize()) ? datagramSocket.getSendBufferSize() : arrayOfByte.length, inetAddress, datagramPort);
        datagramSocket.send(datagramPacket);
        UsageTrackerClient.this.printVerbose("UsageTracker: done sending to UDP.");
        UsageTrackerClient.this.printDebug("UsageTracker: sent size = " + datagramPacket.getLength());
      } catch (Throwable throwable) {
        UsageTrackerClient.this.printVerbose("UsageTracker: error in sendDatagram: " + throwable);
        UsageTrackerClient.this.printDebugStackTrace(throwable);
      } 
    }
    
    private void sendToFile(String param1String) {
      UsageTrackerClient.this.printDebug("UsageTracker: sendToFile");
      File file = new File(fullLogFilename);
      if (logFileMaxSize >= 0L && file.length() >= logFileMaxSize) {
        UsageTrackerClient.this.printVerbose("UsageTracker: log file size exceeds maximum.");
        return;
      } 
      synchronized (LOCK) {
        try(FileOutputStream null = new FileOutputStream(file, true); OutputStreamWriter null = new OutputStreamWriter(fileOutputStream, "UTF-8")) {
          outputStreamWriter.write(param1String, 0, param1String.length());
          UsageTrackerClient.this.printVerbose("UsageTracker: done sending to file.");
          UsageTrackerClient.this.printDebug("UsageTracker: " + fullLogFilename);
        } catch (Throwable throwable) {
          UsageTrackerClient.this.printVerbose("UsageTracker: error in sending to file.");
          UsageTrackerClient.this.printDebugStackTrace(throwable);
        } 
      } 
    }
    
    public void run() {
      if (this.runAsync) {
        UsageTrackerClient.this.setupAndTimestamp(this.timestamp);
        UsageTrackerClient.this.printVerbose("UsageTracker: running asynchronous.");
      } 
      if (enabled) {
        UsageTrackerClient.this.printDebug("UsageTrackerRunnable.run: " + this.callerName + ", javaCommand: " + this.javaCommand);
        String str = buildMessage(this.callerName, this.javaCommand, this.timestamp);
        if (str != null) {
          if (datagramHost != null && datagramPort > 0)
            sendDatagram(str); 
          if (fullLogFilename != null)
            sendToFile(str); 
        } else {
          UsageTrackerClient.this.printVerbose("UsageTracker: length limit exceeded.");
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\usagetracker\UsageTrackerClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */