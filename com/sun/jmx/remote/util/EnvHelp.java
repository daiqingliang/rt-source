package com.sun.jmx.remote.util;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.remote.security.NotificationAccessController;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class EnvHelp {
  public static final String CREDENTIAL_TYPES = "jmx.remote.rmi.server.credential.types";
  
  private static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
  
  private static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
  
  public static final String BUFFER_SIZE_PROPERTY = "jmx.remote.x.notification.buffer.size";
  
  public static final String MAX_FETCH_NOTIFS = "jmx.remote.x.notification.fetch.max";
  
  public static final String FETCH_TIMEOUT = "jmx.remote.x.notification.fetch.timeout";
  
  public static final String NOTIF_ACCESS_CONTROLLER = "com.sun.jmx.remote.notification.access.controller";
  
  public static final String DEFAULT_ORB = "java.naming.corba.orb";
  
  public static final String HIDDEN_ATTRIBUTES = "jmx.remote.x.hidden.attributes";
  
  public static final String DEFAULT_HIDDEN_ATTRIBUTES = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
  
  private static final SortedSet<String> defaultHiddenStrings = new TreeSet();
  
  private static final SortedSet<String> defaultHiddenPrefixes = new TreeSet();
  
  public static final String SERVER_CONNECTION_TIMEOUT = "jmx.remote.x.server.connection.timeout";
  
  public static final String CLIENT_CONNECTION_CHECK_PERIOD = "jmx.remote.x.client.connection.check.period";
  
  public static final String JMX_SERVER_DAEMON = "jmx.remote.x.daemon";
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "EnvHelp");
  
  public static ClassLoader resolveServerClassLoader(Map<String, ?> paramMap, MBeanServer paramMBeanServer) throws InstanceNotFoundException {
    ObjectName objectName;
    if (paramMap == null)
      return Thread.currentThread().getContextClassLoader(); 
    Object object1 = paramMap.get("jmx.remote.default.class.loader");
    Object object2 = paramMap.get("jmx.remote.default.class.loader.name");
    if (object1 != null && object2 != null)
      throw new IllegalArgumentException("Only one of jmx.remote.default.class.loader or jmx.remote.default.class.loader.name should be specified."); 
    if (object1 == null && object2 == null)
      return Thread.currentThread().getContextClassLoader(); 
    if (object1 != null) {
      if (object1 instanceof ClassLoader)
        return (ClassLoader)object1; 
      objectName = "ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + object1.getClass().getName();
      throw new IllegalArgumentException(objectName);
    } 
    if (object2 instanceof ObjectName) {
      objectName = (ObjectName)object2;
    } else {
      String str = "ClassLoader name is not an instance of " + ObjectName.class.getName() + " : " + object2.getClass().getName();
      throw new IllegalArgumentException(str);
    } 
    if (paramMBeanServer == null)
      throw new IllegalArgumentException("Null MBeanServer object"); 
    return paramMBeanServer.getClassLoader(objectName);
  }
  
  public static ClassLoader resolveClientClassLoader(Map<String, ?> paramMap) {
    if (paramMap == null)
      return Thread.currentThread().getContextClassLoader(); 
    Object object = paramMap.get("jmx.remote.default.class.loader");
    if (object == null)
      return Thread.currentThread().getContextClassLoader(); 
    if (object instanceof ClassLoader)
      return (ClassLoader)object; 
    String str = "ClassLoader object is not an instance of " + ClassLoader.class.getName() + " : " + object.getClass().getName();
    throw new IllegalArgumentException(str);
  }
  
  public static <T extends Throwable> T initCause(T paramT, Throwable paramThrowable) {
    paramT.initCause(paramThrowable);
    return paramT;
  }
  
  public static Throwable getCause(Throwable paramThrowable) {
    Throwable throwable = paramThrowable;
    try {
      Method method = paramThrowable.getClass().getMethod("getCause", (Class[])null);
      throwable = (Throwable)method.invoke(paramThrowable, (Object[])null);
    } catch (Exception exception) {}
    return (throwable != null) ? throwable : paramThrowable;
  }
  
  public static int getNotifBufferSize(Map<String, ?> paramMap) {
    int i = 1000;
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.remote.x.notification.buffer.size");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      if (str != null) {
        i = Integer.parseInt(str);
      } else {
        getPropertyAction = new GetPropertyAction("jmx.remote.x.buffer.size");
        str = (String)AccessController.doPrivileged(getPropertyAction);
        if (str != null)
          i = Integer.parseInt(str); 
      } 
    } catch (RuntimeException runtimeException) {
      logger.warning("getNotifBufferSize", "Can't use System property jmx.remote.x.notification.buffer.size: " + runtimeException);
      logger.debug("getNotifBufferSize", runtimeException);
    } 
    int j = i;
    try {
      if (paramMap.containsKey("jmx.remote.x.notification.buffer.size")) {
        j = (int)getIntegerAttribute(paramMap, "jmx.remote.x.notification.buffer.size", i, 0L, 2147483647L);
      } else {
        j = (int)getIntegerAttribute(paramMap, "jmx.remote.x.buffer.size", i, 0L, 2147483647L);
      } 
    } catch (RuntimeException runtimeException) {
      logger.warning("getNotifBufferSize", "Can't determine queuesize (using default): " + runtimeException);
      logger.debug("getNotifBufferSize", runtimeException);
    } 
    return j;
  }
  
  public static int getMaxFetchNotifNumber(Map<String, ?> paramMap) { return (int)getIntegerAttribute(paramMap, "jmx.remote.x.notification.fetch.max", 1000L, 1L, 2147483647L); }
  
  public static long getFetchTimeout(Map<String, ?> paramMap) { return getIntegerAttribute(paramMap, "jmx.remote.x.notification.fetch.timeout", 60000L, 0L, Float.MAX_VALUE); }
  
  public static NotificationAccessController getNotificationAccessController(Map<String, ?> paramMap) { return (paramMap == null) ? null : (NotificationAccessController)paramMap.get("com.sun.jmx.remote.notification.access.controller"); }
  
  public static long getIntegerAttribute(Map<String, ?> paramMap, String paramString, long paramLong1, long paramLong2, long paramLong3) {
    long l;
    Object object;
    if (paramMap == null || (object = paramMap.get(paramString)) == null)
      return paramLong1; 
    if (object instanceof Number) {
      l = ((Number)object).longValue();
    } else if (object instanceof String) {
      l = Long.parseLong((String)object);
    } else {
      String str = "Attribute " + paramString + " value must be Integer or String: " + object;
      throw new IllegalArgumentException(str);
    } 
    if (l < paramLong2) {
      String str = "Attribute " + paramString + " value must be at least " + paramLong2 + ": " + l;
      throw new IllegalArgumentException(str);
    } 
    if (l > paramLong3) {
      String str = "Attribute " + paramString + " value must be at most " + paramLong3 + ": " + l;
      throw new IllegalArgumentException(str);
    } 
    return l;
  }
  
  public static void checkAttributes(Map<?, ?> paramMap) {
    for (Object object : paramMap.keySet()) {
      if (!(object instanceof String)) {
        String str = "Attributes contain key that is not a string: " + object;
        throw new IllegalArgumentException(str);
      } 
    } 
  }
  
  public static <V> Map<String, V> filterAttributes(Map<String, V> paramMap) {
    if (logger.traceOn())
      logger.trace("filterAttributes", "starts"); 
    TreeMap treeMap = new TreeMap(paramMap);
    purgeUnserializable(treeMap.values());
    hideAttributes(treeMap);
    return treeMap;
  }
  
  private static void purgeUnserializable(Collection<?> paramCollection) {
    logger.trace("purgeUnserializable", "starts");
    ObjectOutputStream objectOutputStream = null;
    byte b = 0;
    Iterator iterator = paramCollection.iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      if (object == null || object instanceof String) {
        if (logger.traceOn())
          logger.trace("purgeUnserializable", "Value trivially serializable: " + object); 
      } else {
        try {
          if (objectOutputStream == null)
            objectOutputStream = new ObjectOutputStream(new SinkOutputStream(null)); 
          objectOutputStream.writeObject(object);
          if (logger.traceOn())
            logger.trace("purgeUnserializable", "Value serializable: " + object); 
        } catch (IOException iOException) {
          if (logger.traceOn())
            logger.trace("purgeUnserializable", "Value not serializable: " + object + ": " + iOException); 
          iterator.remove();
          objectOutputStream = null;
        } 
      } 
      b++;
    } 
  }
  
  private static void hideAttributes(SortedMap<String, ?> paramSortedMap) {
    String str4;
    String str3;
    SortedSet sortedSet2;
    SortedSet sortedSet1;
    if (paramSortedMap.isEmpty())
      return; 
    String str1 = (String)paramSortedMap.get("jmx.remote.x.hidden.attributes");
    if (str1 != null) {
      if (str1.startsWith("=")) {
        str1 = str1.substring(1);
      } else {
        str1 = str1 + " java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
      } 
      sortedSet1 = new TreeSet();
      sortedSet2 = new TreeSet();
      parseHiddenAttributes(str1, sortedSet1, sortedSet2);
    } else {
      str1 = "java.naming.security.* jmx.remote.authenticator jmx.remote.context jmx.remote.default.class.loader jmx.remote.message.connection.server jmx.remote.object.wrapping jmx.remote.rmi.client.socket.factory jmx.remote.rmi.server.socket.factory jmx.remote.sasl.callback.handler jmx.remote.tls.socket.factory jmx.remote.x.access.file jmx.remote.x.password.file ";
      synchronized (defaultHiddenStrings) {
        if (defaultHiddenStrings.isEmpty())
          parseHiddenAttributes(str1, defaultHiddenStrings, defaultHiddenPrefixes); 
        sortedSet1 = defaultHiddenStrings;
        sortedSet2 = defaultHiddenPrefixes;
      } 
    } 
    String str2 = (String)paramSortedMap.lastKey() + "X";
    Iterator iterator1 = paramSortedMap.keySet().iterator();
    Iterator iterator2 = sortedSet1.iterator();
    Iterator iterator3 = sortedSet2.iterator();
    if (iterator2.hasNext()) {
      str3 = (String)iterator2.next();
    } else {
      str3 = str2;
    } 
    if (iterator3.hasNext()) {
      str4 = (String)iterator3.next();
    } else {
      str4 = str2;
    } 
    while (iterator1.hasNext()) {
      String str = (String)iterator1.next();
      int i = 1;
      while ((i = str3.compareTo(str)) < 0) {
        if (iterator2.hasNext()) {
          str3 = (String)iterator2.next();
          continue;
        } 
        str3 = str2;
      } 
      if (i == 0) {
        iterator1.remove();
        continue;
      } 
      while (str4.compareTo(str) <= 0) {
        if (str.startsWith(str4)) {
          iterator1.remove();
          break;
        } 
        if (iterator3.hasNext()) {
          str4 = (String)iterator3.next();
          continue;
        } 
        str4 = str2;
      } 
    } 
  }
  
  private static void parseHiddenAttributes(String paramString, SortedSet<String> paramSortedSet1, SortedSet<String> paramSortedSet2) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      if (str.endsWith("*")) {
        paramSortedSet2.add(str.substring(0, str.length() - 1));
        continue;
      } 
      paramSortedSet1.add(str);
    } 
  }
  
  public static long getServerConnectionTimeout(Map<String, ?> paramMap) { return getIntegerAttribute(paramMap, "jmx.remote.x.server.connection.timeout", 120000L, 0L, Float.MAX_VALUE); }
  
  public static long getConnectionCheckPeriod(Map<String, ?> paramMap) { return getIntegerAttribute(paramMap, "jmx.remote.x.client.connection.check.period", 60000L, 0L, Float.MAX_VALUE); }
  
  public static boolean computeBooleanFromString(String paramString) { return computeBooleanFromString(paramString, false); }
  
  public static boolean computeBooleanFromString(String paramString, boolean paramBoolean) {
    if (paramString == null)
      return paramBoolean; 
    if (paramString.equalsIgnoreCase("true"))
      return true; 
    if (paramString.equalsIgnoreCase("false"))
      return false; 
    throw new IllegalArgumentException("Property value must be \"true\" or \"false\" instead of \"" + paramString + "\"");
  }
  
  public static <K, V> Hashtable<K, V> mapToHashtable(Map<K, V> paramMap) {
    HashMap hashMap = new HashMap(paramMap);
    if (hashMap.containsKey(null))
      hashMap.remove(null); 
    Iterator iterator = hashMap.values().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == null)
        iterator.remove(); 
    } 
    return new Hashtable(hashMap);
  }
  
  public static boolean isServerDaemon(Map<String, ?> paramMap) { return (paramMap != null && "true".equalsIgnoreCase((String)paramMap.get("jmx.remote.x.daemon"))); }
  
  private static final class SinkOutputStream extends OutputStream {
    private SinkOutputStream() {}
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {}
    
    public void write(int param1Int) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remot\\util\EnvHelp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */