package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

final class PropMap extends Object implements SortedMap<String, String> {
  private final TreeMap<String, String> theMap = new TreeMap();
  
  private final List<Object> listenerList = new ArrayList(1);
  
  private static Map<String, String> defaultProps;
  
  void addListener(Object paramObject) {
    assert Beans.isPropertyChangeListener(paramObject);
    this.listenerList.add(paramObject);
  }
  
  void removeListener(Object paramObject) {
    assert Beans.isPropertyChangeListener(paramObject);
    this.listenerList.remove(paramObject);
  }
  
  public String put(String paramString1, String paramString2) {
    String str = (String)this.theMap.put(paramString1, paramString2);
    if (paramString2 != str && !this.listenerList.isEmpty()) {
      assert Beans.isBeansPresent();
      Object object = Beans.newPropertyChangeEvent(this, paramString1, str, paramString2);
      for (Object object1 : this.listenerList)
        Beans.invokePropertyChange(object1, object); 
    } 
    return str;
  }
  
  PropMap() { this.theMap.putAll(defaultProps); }
  
  SortedMap<String, String> prefixMap(String paramString) {
    int i = paramString.length();
    if (i == 0)
      return this; 
    char c = (char)(paramString.charAt(i - 1) + '\001');
    String str = paramString.substring(0, i - 1) + c;
    return subMap(paramString, str);
  }
  
  String getProperty(String paramString) { return get(paramString); }
  
  String getProperty(String paramString1, String paramString2) {
    String str = getProperty(paramString1);
    return (str == null) ? paramString2 : str;
  }
  
  String setProperty(String paramString1, String paramString2) { return put(paramString1, paramString2); }
  
  List<String> getProperties(String paramString) {
    Collection collection = prefixMap(paramString).values();
    ArrayList arrayList = new ArrayList(collection.size());
    arrayList.addAll(collection);
    while (arrayList.remove(null));
    return arrayList;
  }
  
  private boolean toBoolean(String paramString) { return Boolean.valueOf(paramString).booleanValue(); }
  
  boolean getBoolean(String paramString) { return toBoolean(getProperty(paramString)); }
  
  boolean setBoolean(String paramString, boolean paramBoolean) { return toBoolean(setProperty(paramString, String.valueOf(paramBoolean))); }
  
  int toInteger(String paramString) { return toInteger(paramString, 0); }
  
  int toInteger(String paramString, int paramInt) { return (paramString == null) ? paramInt : ("true".equals(paramString) ? 1 : ("false".equals(paramString) ? 0 : Integer.parseInt(paramString))); }
  
  int getInteger(String paramString, int paramInt) { return toInteger(getProperty(paramString), paramInt); }
  
  int getInteger(String paramString) { return toInteger(getProperty(paramString)); }
  
  int setInteger(String paramString, int paramInt) { return toInteger(setProperty(paramString, String.valueOf(paramInt))); }
  
  long toLong(String paramString) {
    try {
      return (paramString == null) ? 0L : Long.parseLong(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new IllegalArgumentException("Invalid value");
    } 
  }
  
  long getLong(String paramString) { return toLong(getProperty(paramString)); }
  
  long setLong(String paramString, long paramLong) { return toLong(setProperty(paramString, String.valueOf(paramLong))); }
  
  int getTime(String paramString) {
    String str = getProperty(paramString, "0");
    if ("now".equals(str))
      return (int)((System.currentTimeMillis() + 500L) / 1000L); 
    long l = toLong(str);
    if (l < 10000000000L && !"0".equals(str))
      Utils.log.warning("Supplied modtime appears to be seconds rather than milliseconds: " + str); 
    return (int)((l + 500L) / 1000L);
  }
  
  void list(PrintStream paramPrintStream) {
    PrintWriter printWriter = new PrintWriter(paramPrintStream);
    list(printWriter);
    printWriter.flush();
  }
  
  void list(PrintWriter paramPrintWriter) {
    paramPrintWriter.println("#PACK200[");
    Set set = defaultProps.entrySet();
    for (Map.Entry entry : this.theMap.entrySet()) {
      if (set.contains(entry))
        continue; 
      paramPrintWriter.println("  " + (String)entry.getKey() + " = " + (String)entry.getValue());
    } 
    paramPrintWriter.println("#]");
  }
  
  public int size() { return this.theMap.size(); }
  
  public boolean isEmpty() { return this.theMap.isEmpty(); }
  
  public boolean containsKey(Object paramObject) { return this.theMap.containsKey(paramObject); }
  
  public boolean containsValue(Object paramObject) { return this.theMap.containsValue(paramObject); }
  
  public String get(Object paramObject) { return (String)this.theMap.get(paramObject); }
  
  public String remove(Object paramObject) { return (String)this.theMap.remove(paramObject); }
  
  public void putAll(Map<? extends String, ? extends String> paramMap) { this.theMap.putAll(paramMap); }
  
  public void clear() { this.theMap.clear(); }
  
  public Set<String> keySet() { return this.theMap.keySet(); }
  
  public Collection<String> values() { return this.theMap.values(); }
  
  public Set<Map.Entry<String, String>> entrySet() { return this.theMap.entrySet(); }
  
  public Comparator<? super String> comparator() { return this.theMap.comparator(); }
  
  public SortedMap<String, String> subMap(String paramString1, String paramString2) { return this.theMap.subMap(paramString1, paramString2); }
  
  public SortedMap<String, String> headMap(String paramString) { return this.theMap.headMap(paramString); }
  
  public SortedMap<String, String> tailMap(String paramString) { return this.theMap.tailMap(paramString); }
  
  public String firstKey() { return (String)this.theMap.firstKey(); }
  
  public String lastKey() { return (String)this.theMap.lastKey(); }
  
  static  {
    Properties properties = new Properties();
    properties.put("com.sun.java.util.jar.pack.disable.native", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.disable.native")));
    properties.put("com.sun.java.util.jar.pack.verbose", String.valueOf(Integer.getInteger("com.sun.java.util.jar.pack.verbose", 0)));
    properties.put("com.sun.java.util.jar.pack.default.timezone", String.valueOf(Boolean.getBoolean("com.sun.java.util.jar.pack.default.timezone")));
    properties.put("pack.segment.limit", "-1");
    properties.put("pack.keep.file.order", "true");
    properties.put("pack.modification.time", "keep");
    properties.put("pack.deflate.hint", "keep");
    properties.put("pack.unknown.attribute", "pass");
    properties.put("com.sun.java.util.jar.pack.class.format.error", System.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass"));
    properties.put("pack.effort", "5");
    String str = "intrinsic.properties";
    try (InputStream null = PackerImpl.class.getResourceAsStream(str)) {
      if (inputStream == null)
        throw new RuntimeException(str + " cannot be loaded"); 
      properties.load(inputStream);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    for (Map.Entry entry : properties.entrySet()) {
      String str1 = (String)entry.getKey();
      String str2 = (String)entry.getValue();
      if (str1.startsWith("attribute."))
        entry.setValue(Attribute.normalizeLayoutString(str2)); 
    } 
    HashMap hashMap = new HashMap(properties);
    defaultProps = hashMap;
  }
  
  private static class Beans {
    private static final Class<?> propertyChangeListenerClass = getClass("java.beans.PropertyChangeListener");
    
    private static final Class<?> propertyChangeEventClass = getClass("java.beans.PropertyChangeEvent");
    
    private static final Method propertyChangeMethod = getMethod(propertyChangeListenerClass, "propertyChange", new Class[] { propertyChangeEventClass });
    
    private static final Constructor<?> propertyEventCtor = getConstructor(propertyChangeEventClass, new Class[] { Object.class, String.class, Object.class, Object.class });
    
    private static Class<?> getClass(String param1String) {
      try {
        return Class.forName(param1String, true, Beans.class.getClassLoader());
      } catch (ClassNotFoundException classNotFoundException) {
        return null;
      } 
    }
    
    private static Constructor<?> getConstructor(Class<?> param1Class, Class<?>... param1VarArgs) {
      try {
        return (param1Class == null) ? null : param1Class.getDeclaredConstructor(param1VarArgs);
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new AssertionError(noSuchMethodException);
      } 
    }
    
    private static Method getMethod(Class<?> param1Class, String param1String, Class<?>... param1VarArgs) {
      try {
        return (param1Class == null) ? null : param1Class.getMethod(param1String, param1VarArgs);
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new AssertionError(noSuchMethodException);
      } 
    }
    
    static boolean isBeansPresent() { return (propertyChangeListenerClass != null && propertyChangeEventClass != null); }
    
    static boolean isPropertyChangeListener(Object param1Object) { return (propertyChangeListenerClass == null) ? false : propertyChangeListenerClass.isInstance(param1Object); }
    
    static Object newPropertyChangeEvent(Object param1Object1, String param1String, Object param1Object2, Object param1Object3) {
      try {
        return propertyEventCtor.newInstance(new Object[] { param1Object1, param1String, param1Object2, param1Object3 });
      } catch (InstantiationException|IllegalAccessException instantiationException) {
        throw new AssertionError(instantiationException);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof Error)
          throw (Error)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        throw new AssertionError(invocationTargetException);
      } 
    }
    
    static void invokePropertyChange(Object param1Object1, Object param1Object2) {
      try {
        propertyChangeMethod.invoke(param1Object1, new Object[] { param1Object2 });
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof Error)
          throw (Error)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        throw new AssertionError(invocationTargetException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\PropMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */