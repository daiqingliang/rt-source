package com.sun.corba.se.impl.orb;

import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import com.sun.corba.se.spi.orb.DataCollector;
import com.sun.corba.se.spi.orb.PropertyParser;
import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public abstract class DataCollectorBase implements DataCollector {
  private PropertyParser parser;
  
  private Set propertyNames;
  
  private Set propertyPrefixes;
  
  private Set URLPropertyNames = new HashSet();
  
  protected String localHostName;
  
  protected String configurationHostName;
  
  private boolean setParserCalled;
  
  private Properties originalProps;
  
  private Properties resultProps;
  
  public DataCollectorBase(Properties paramProperties, String paramString1, String paramString2) {
    this.URLPropertyNames.add("org.omg.CORBA.ORBInitialServices");
    this.propertyNames = new HashSet();
    this.propertyNames.add("org.omg.CORBA.ORBInitRef");
    this.propertyPrefixes = new HashSet();
    this.originalProps = paramProperties;
    this.localHostName = paramString1;
    this.configurationHostName = paramString2;
    this.setParserCalled = false;
    this.resultProps = new Properties();
  }
  
  public boolean initialHostIsLocal() {
    checkSetParserCalled();
    return this.localHostName.equals(this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost"));
  }
  
  public void setParser(PropertyParser paramPropertyParser) {
    for (ParserAction parserAction : paramPropertyParser) {
      if (parserAction.isPrefix()) {
        this.propertyPrefixes.add(parserAction.getPropertyName());
        continue;
      } 
      this.propertyNames.add(parserAction.getPropertyName());
    } 
    collect();
    this.setParserCalled = true;
  }
  
  public Properties getProperties() {
    checkSetParserCalled();
    return this.resultProps;
  }
  
  public abstract boolean isApplet();
  
  protected abstract void collect();
  
  protected void checkPropertyDefaults() {
    String str1 = this.resultProps.getProperty("org.omg.CORBA.ORBInitialHost");
    if (str1 == null || str1.equals(""))
      setProperty("org.omg.CORBA.ORBInitialHost", this.configurationHostName); 
    String str2 = this.resultProps.getProperty("com.sun.CORBA.ORBServerHost");
    if (str2 == null || str2.equals("") || str2.equals("0.0.0.0") || str2.equals("::") || str2.toLowerCase().equals("::ffff:0.0.0.0")) {
      setProperty("com.sun.CORBA.ORBServerHost", this.localHostName);
      setProperty("com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces", "com.sun.CORBA.INTERNAL USE ONLY: listen on all interfaces");
    } 
  }
  
  protected void findPropertiesFromArgs(String[] paramArrayOfString) {
    if (paramArrayOfString == null)
      return; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str2 = null;
      String str1 = null;
      if (paramArrayOfString[b] != null && paramArrayOfString[b].startsWith("-ORB")) {
        String str = paramArrayOfString[b].substring(1);
        str1 = findMatchingPropertyName(this.propertyNames, str);
        if (str1 != null && b + true < paramArrayOfString.length && paramArrayOfString[b + true] != null)
          str2 = paramArrayOfString[++b]; 
      } 
      if (str2 != null)
        setProperty(str1, str2); 
    } 
  }
  
  protected void findPropertiesFromApplet(final Applet app) {
    if (paramApplet == null)
      return; 
    PropertyCallback propertyCallback1 = new PropertyCallback() {
        public String get(String param1String) { return app.getParameter(param1String); }
      };
    findPropertiesByName(this.propertyNames.iterator(), propertyCallback1);
    PropertyCallback propertyCallback2 = new PropertyCallback() {
        public String get(String param1String) {
          String str = DataCollectorBase.this.resultProps.getProperty(param1String);
          if (str == null)
            return null; 
          try {
            URL uRL = new URL(app.getDocumentBase(), str);
            return uRL.toExternalForm();
          } catch (MalformedURLException malformedURLException) {
            return str;
          } 
        }
      };
    findPropertiesByName(this.URLPropertyNames.iterator(), propertyCallback2);
  }
  
  private void doProperties(final Properties props) {
    PropertyCallback propertyCallback = new PropertyCallback() {
        public String get(String param1String) { return props.getProperty(param1String); }
      };
    findPropertiesByName(this.propertyNames.iterator(), propertyCallback);
    findPropertiesByPrefix(this.propertyPrefixes, makeIterator(paramProperties.propertyNames()), propertyCallback);
  }
  
  protected void findPropertiesFromFile() {
    Properties properties = getFileProperties();
    if (properties == null)
      return; 
    doProperties(properties);
  }
  
  protected void findPropertiesFromProperties() {
    if (this.originalProps == null)
      return; 
    doProperties(this.originalProps);
  }
  
  protected void findPropertiesFromSystem() {
    Set set1 = getCORBAPrefixes(this.propertyNames);
    Set set2 = getCORBAPrefixes(this.propertyPrefixes);
    PropertyCallback propertyCallback = new PropertyCallback() {
        public String get(String param1String) { return DataCollectorBase.getSystemProperty(param1String); }
      };
    findPropertiesByName(set1.iterator(), propertyCallback);
    findPropertiesByPrefix(set2, getSystemPropertyNames(), propertyCallback);
  }
  
  private void setProperty(String paramString1, String paramString2) {
    if (paramString1.equals("org.omg.CORBA.ORBInitRef")) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString2, "=");
      if (stringTokenizer.countTokens() != 2)
        throw new IllegalArgumentException(); 
      String str1 = stringTokenizer.nextToken();
      String str2 = stringTokenizer.nextToken();
      this.resultProps.setProperty(paramString1 + "." + str1, str2);
    } else {
      this.resultProps.setProperty(paramString1, paramString2);
    } 
  }
  
  private void checkSetParserCalled() {
    if (!this.setParserCalled)
      throw new IllegalStateException("setParser not called."); 
  }
  
  private void findPropertiesByPrefix(Set paramSet, Iterator paramIterator, PropertyCallback paramPropertyCallback) {
    while (paramIterator.hasNext()) {
      String str = (String)paramIterator.next();
      for (String str1 : paramSet) {
        if (str.startsWith(str1)) {
          String str2 = paramPropertyCallback.get(str);
          setProperty(str, str2);
        } 
      } 
    } 
  }
  
  private void findPropertiesByName(Iterator paramIterator, PropertyCallback paramPropertyCallback) {
    while (paramIterator.hasNext()) {
      String str1 = (String)paramIterator.next();
      String str2 = paramPropertyCallback.get(str1);
      if (str2 != null)
        setProperty(str1, str2); 
    } 
  }
  
  private static String getSystemProperty(String paramString) { return (String)AccessController.doPrivileged(new GetPropertyAction(paramString)); }
  
  private String findMatchingPropertyName(Set paramSet, String paramString) {
    for (String str : paramSet) {
      if (str.endsWith(paramString))
        return str; 
    } 
    return null;
  }
  
  private static Iterator makeIterator(final Enumeration enumeration) { return new Iterator() {
        public boolean hasNext() { return enumeration.hasMoreElements(); }
        
        public Object next() { return enumeration.nextElement(); }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
  
  private static Iterator getSystemPropertyNames() {
    Enumeration enumeration = (Enumeration)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperties().propertyNames(); }
        });
    return makeIterator(enumeration);
  }
  
  private void getPropertiesFromFile(Properties paramProperties, String paramString) {
    try {
      File file = new File(paramString);
      if (!file.exists())
        return; 
      fileInputStream = new FileInputStream(file);
      try {
        paramProperties.load(fileInputStream);
      } finally {
        fileInputStream.close();
      } 
    } catch (Exception exception) {}
  }
  
  private Properties getFileProperties() {
    Properties properties1 = new Properties();
    String str1 = getSystemProperty("java.home");
    String str2 = str1 + File.separator + "lib" + File.separator + "orb.properties";
    getPropertiesFromFile(properties1, str2);
    Properties properties2 = new Properties(properties1);
    String str3 = getSystemProperty("user.home");
    str2 = str3 + File.separator + "orb.properties";
    getPropertiesFromFile(properties2, str2);
    return properties2;
  }
  
  private boolean hasCORBAPrefix(String paramString) { return (paramString.startsWith("org.omg.") || paramString.startsWith("com.sun.CORBA.") || paramString.startsWith("com.sun.corba.") || paramString.startsWith("com.sun.corba.se.")); }
  
  private Set getCORBAPrefixes(Set paramSet) {
    HashSet hashSet = new HashSet();
    for (String str : paramSet) {
      if (hasCORBAPrefix(str))
        hashSet.add(str); 
    } 
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\DataCollectorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */