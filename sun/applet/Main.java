package sun.applet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import sun.net.www.ParseUtil;

public class Main {
  static File theUserPropertiesFile;
  
  static final String[][] avDefaultUserProps = { { "http.proxyHost", "" }, { "http.proxyPort", "80" }, { "package.restrict.access.sun", "true" } };
  
  private static AppletMessageHandler amh;
  
  private boolean debugFlag = false;
  
  private boolean helpFlag = false;
  
  private String encoding = null;
  
  private boolean noSecurityFlag = false;
  
  private static boolean cmdLineTestFlag;
  
  private static Vector urlList;
  
  public static final String theVersion;
  
  public static void main(String[] paramArrayOfString) {
    Main main = new Main();
    int i = main.run(paramArrayOfString);
    if (i != 0 || cmdLineTestFlag)
      System.exit(i); 
  }
  
  private int run(String[] paramArrayOfString) {
    try {
      if (paramArrayOfString.length == 0) {
        usage();
        return 0;
      } 
      for (int i = 0; i < paramArrayOfString.length; i += j) {
        int j = decodeArg(paramArrayOfString, i);
        if (j == 0)
          throw new ParseException(lookup("main.err.unrecognizedarg", paramArrayOfString[i])); 
      } 
    } catch (ParseException parseException) {
      System.err.println(parseException.getMessage());
      return 1;
    } 
    if (this.helpFlag) {
      usage();
      return 0;
    } 
    if (urlList.size() == 0) {
      System.err.println(lookup("main.err.inputfile"));
      return 1;
    } 
    if (this.debugFlag)
      return invokeDebugger(paramArrayOfString); 
    if (!this.noSecurityFlag && System.getSecurityManager() == null)
      init(); 
    for (byte b = 0; b < urlList.size(); b++) {
      try {
        AppletViewer.parse((URL)urlList.elementAt(b), this.encoding);
      } catch (IOException iOException) {
        System.err.println(lookup("main.err.io", iOException.getMessage()));
        return 1;
      } 
    } 
    return 0;
  }
  
  private static void usage() { System.out.println(lookup("usage")); }
  
  private int decodeArg(String[] paramArrayOfString, int paramInt) throws ParseException {
    String str = paramArrayOfString[paramInt];
    int i = paramArrayOfString.length;
    if ("-help".equalsIgnoreCase(str) || "-?".equals(str)) {
      this.helpFlag = true;
      return 1;
    } 
    if ("-encoding".equals(str) && paramInt < i - 1) {
      if (this.encoding != null)
        throw new ParseException(lookup("main.err.dupoption", str)); 
      this.encoding = paramArrayOfString[++paramInt];
      return 2;
    } 
    if ("-debug".equals(str)) {
      this.debugFlag = true;
      return 1;
    } 
    if ("-Xnosecurity".equals(str)) {
      System.err.println();
      System.err.println(lookup("main.warn.nosecmgr"));
      System.err.println();
      this.noSecurityFlag = true;
      return 1;
    } 
    if ("-XcmdLineTest".equals(str)) {
      cmdLineTestFlag = true;
      return 1;
    } 
    if (str.startsWith("-"))
      throw new ParseException(lookup("main.err.unsupportedopt", str)); 
    URL uRL = parseURL(str);
    if (uRL != null) {
      urlList.addElement(uRL);
      return 1;
    } 
    return 0;
  }
  
  private URL parseURL(String paramString) throws ParseException {
    URL uRL = null;
    String str = "file:";
    try {
      if (paramString.indexOf(':') <= 1) {
        uRL = ParseUtil.fileToEncodedURL(new File(paramString));
      } else if (paramString.startsWith(str) && paramString.length() != str.length() && !(new File(paramString.substring(str.length()))).isAbsolute()) {
        String str1 = ParseUtil.fileToEncodedURL(new File(System.getProperty("user.dir"))).getPath() + paramString.substring(str.length());
        uRL = new URL("file", "", str1);
      } else {
        uRL = new URL(paramString);
      } 
    } catch (MalformedURLException malformedURLException) {
      throw new ParseException(lookup("main.err.badurl", paramString, malformedURLException.getMessage()));
    } 
    return uRL;
  }
  
  private int invokeDebugger(String[] paramArrayOfString) {
    String[] arrayOfString = new String[paramArrayOfString.length + 1];
    byte b1 = 0;
    String str = System.getProperty("java.home") + File.separator + "phony";
    arrayOfString[b1++] = "-Djava.class.path=" + str;
    arrayOfString[b1++] = "sun.applet.Main";
    for (b2 = 0; b2 < paramArrayOfString.length; b2++) {
      if (!"-debug".equals(paramArrayOfString[b2]))
        arrayOfString[b1++] = paramArrayOfString[b2]; 
    } 
    try {
      Class clazz = Class.forName("com.sun.tools.example.debug.tty.TTY", true, ClassLoader.getSystemClassLoader());
      Method method = clazz.getDeclaredMethod("main", new Class[] { String[].class });
      method.invoke(null, new Object[] { arrayOfString });
    } catch (ClassNotFoundException b2) {
      ClassNotFoundException classNotFoundException;
      System.err.println(lookup("main.debug.cantfinddebug"));
      return 1;
    } catch (NoSuchMethodException b2) {
      NoSuchMethodException noSuchMethodException;
      System.err.println(lookup("main.debug.cantfindmain"));
      return 1;
    } catch (InvocationTargetException b2) {
      InvocationTargetException invocationTargetException;
      System.err.println(lookup("main.debug.exceptionindebug"));
      return 1;
    } catch (IllegalAccessException b2) {
      IllegalAccessException illegalAccessException;
      System.err.println(lookup("main.debug.cantaccess"));
      return 1;
    } 
    return 0;
  }
  
  private void init() {
    Properties properties1 = getAVProps();
    properties1.put("browser", "sun.applet.AppletViewer");
    properties1.put("browser.version", "1.06");
    properties1.put("browser.vendor", "Oracle Corporation");
    properties1.put("http.agent", "Java(tm) 2 SDK, Standard Edition v" + theVersion);
    properties1.put("package.restrict.definition.java", "true");
    properties1.put("package.restrict.definition.sun", "true");
    properties1.put("java.version.applet", "true");
    properties1.put("java.vendor.applet", "true");
    properties1.put("java.vendor.url.applet", "true");
    properties1.put("java.class.version.applet", "true");
    properties1.put("os.name.applet", "true");
    properties1.put("os.version.applet", "true");
    properties1.put("os.arch.applet", "true");
    properties1.put("file.separator.applet", "true");
    properties1.put("path.separator.applet", "true");
    properties1.put("line.separator.applet", "true");
    Properties properties2 = System.getProperties();
    Enumeration enumeration = properties2.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = properties2.getProperty(str1);
      String str3;
      if ((str3 = (String)properties1.setProperty(str1, str2)) != null)
        System.err.println(lookup("main.warn.prop.overwrite", str1, str3, str2)); 
    } 
    System.setProperties(properties1);
    if (!this.noSecurityFlag) {
      System.setSecurityManager(new AppletSecurity());
    } else {
      System.err.println(lookup("main.nosecmgr"));
    } 
  }
  
  private Properties getAVProps() {
    Properties properties = new Properties();
    File file = theUserPropertiesFile;
    if (file.exists()) {
      if (file.canRead()) {
        properties = getAVProps(file);
      } else {
        System.err.println(lookup("main.warn.cantreadprops", file.toString()));
        properties = setDefaultAVProps();
      } 
    } else {
      File file1 = new File(System.getProperty("user.home"));
      File file2 = new File(file1, ".hotjava");
      file2 = new File(file2, "properties");
      if (file2.exists()) {
        properties = getAVProps(file2);
      } else {
        System.err.println(lookup("main.warn.cantreadprops", file2.toString()));
        properties = setDefaultAVProps();
      } 
      try (FileOutputStream null = new FileOutputStream(file)) {
        properties.store(fileOutputStream, lookup("main.prop.store"));
      } catch (IOException iOException) {
        System.err.println(lookup("main.err.prop.cantsave", file.toString()));
      } 
    } 
    return properties;
  }
  
  private Properties setDefaultAVProps() {
    Properties properties = new Properties();
    for (byte b = 0; b < avDefaultUserProps.length; b++)
      properties.setProperty(avDefaultUserProps[b][0], avDefaultUserProps[b][1]); 
    return properties;
  }
  
  private Properties getAVProps(File paramFile) {
    Properties properties1 = new Properties();
    Properties properties2 = new Properties();
    try (FileInputStream null = new FileInputStream(paramFile)) {
      properties2.load(new BufferedInputStream(fileInputStream));
    } catch (IOException iOException) {
      System.err.println(lookup("main.err.prop.cantread", paramFile.toString()));
    } 
    for (byte b = 0; b < avDefaultUserProps.length; b++) {
      String str = properties2.getProperty(avDefaultUserProps[b][0]);
      if (str != null) {
        properties1.setProperty(avDefaultUserProps[b][0], str);
      } else {
        properties1.setProperty(avDefaultUserProps[b][0], avDefaultUserProps[b][1]);
      } 
    } 
    return properties1;
  }
  
  private static String lookup(String paramString) { return amh.getMessage(paramString); }
  
  private static String lookup(String paramString1, String paramString2) { return amh.getMessage(paramString1, paramString2); }
  
  private static String lookup(String paramString1, String paramString2, String paramString3) { return amh.getMessage(paramString1, paramString2, paramString3); }
  
  private static String lookup(String paramString1, String paramString2, String paramString3, String paramString4) { return amh.getMessage(paramString1, paramString2, paramString3, paramString4); }
  
  static  {
    File file = new File(System.getProperty("user.home"));
    file.canWrite();
    theUserPropertiesFile = new File(file, ".appletviewer");
    amh = new AppletMessageHandler("appletviewer");
    cmdLineTestFlag = false;
    urlList = new Vector(1);
    theVersion = System.getProperty("java.version");
  }
  
  class ParseException extends RuntimeException {
    Throwable t = null;
    
    public ParseException(String param1String) { super(param1String); }
    
    public ParseException(Throwable param1Throwable) {
      super(param1Throwable.getMessage());
      this.t = param1Throwable;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */