package javax.tools;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolProvider {
  private static final String propertyName = "sun.tools.ToolProvider";
  
  private static final String loggerName = "javax.tools";
  
  private static final String defaultJavaCompilerName = "com.sun.tools.javac.api.JavacTool";
  
  private static final String defaultDocumentationToolName = "com.sun.tools.javadoc.api.JavadocTool";
  
  private static ToolProvider instance;
  
  private Map<String, Reference<Class<?>>> toolClasses = new HashMap();
  
  private Reference<ClassLoader> refToolClassLoader = null;
  
  private static final String[] defaultToolsLocation = { "lib", "tools.jar" };
  
  static <T> T trace(Level paramLevel, Object paramObject) {
    try {
      if (System.getProperty("sun.tools.ToolProvider") != null) {
        StackTraceElement[] arrayOfStackTraceElement = Thread.currentThread().getStackTrace();
        String str1 = "???";
        String str2 = ToolProvider.class.getName();
        if (arrayOfStackTraceElement.length > 2) {
          StackTraceElement stackTraceElement = arrayOfStackTraceElement[2];
          str1 = String.format((Locale)null, "%s(%s:%s)", new Object[] { stackTraceElement.getMethodName(), stackTraceElement.getFileName(), Integer.valueOf(stackTraceElement.getLineNumber()) });
          str2 = stackTraceElement.getClassName();
        } 
        Logger logger = Logger.getLogger("javax.tools");
        if (paramObject instanceof Throwable) {
          logger.logp(paramLevel, str2, str1, paramObject.getClass().getName(), (Throwable)paramObject);
        } else {
          logger.logp(paramLevel, str2, str1, String.valueOf(paramObject));
        } 
      } 
    } catch (SecurityException securityException) {
      System.err.format((Locale)null, "%s: %s; %s%n", new Object[] { ToolProvider.class.getName(), paramObject, securityException.getLocalizedMessage() });
    } 
    return null;
  }
  
  public static JavaCompiler getSystemJavaCompiler() { return (JavaCompiler)instance().getSystemTool(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool"); }
  
  public static DocumentationTool getSystemDocumentationTool() { return (DocumentationTool)instance().getSystemTool(DocumentationTool.class, "com.sun.tools.javadoc.api.JavadocTool"); }
  
  public static ClassLoader getSystemToolClassLoader() {
    try {
      Class clazz = instance().getSystemToolClass(JavaCompiler.class, "com.sun.tools.javac.api.JavacTool");
      return clazz.getClassLoader();
    } catch (Throwable throwable) {
      return (ClassLoader)trace(Level.WARNING, throwable);
    } 
  }
  
  private static ToolProvider instance() {
    if (instance == null)
      instance = new ToolProvider(); 
    return instance;
  }
  
  private <T> T getSystemTool(Class<T> paramClass, String paramString) {
    Class clazz = getSystemToolClass(paramClass, paramString);
    try {
      return (T)clazz.asSubclass(paramClass).newInstance();
    } catch (Throwable throwable) {
      trace(Level.WARNING, throwable);
      return null;
    } 
  }
  
  private <T> Class<? extends T> getSystemToolClass(Class<T> paramClass, String paramString) {
    Reference reference = (Reference)this.toolClasses.get(paramString);
    Class clazz = (reference == null) ? null : (Class)reference.get();
    if (clazz == null) {
      try {
        clazz = findSystemToolClass(paramString);
      } catch (Throwable throwable) {
        return (Class)trace(Level.WARNING, throwable);
      } 
      this.toolClasses.put(paramString, new WeakReference(clazz));
    } 
    return clazz.asSubclass(paramClass);
  }
  
  private Class<?> findSystemToolClass(String paramString) throws MalformedURLException, ClassNotFoundException {
    try {
      return Class.forName(paramString, false, null);
    } catch (ClassNotFoundException classNotFoundException) {
      trace(Level.FINE, classNotFoundException);
      ClassLoader classLoader = (this.refToolClassLoader == null) ? null : (ClassLoader)this.refToolClassLoader.get();
      if (classLoader == null) {
        File file = new File(System.getProperty("java.home"));
        if (file.getName().equalsIgnoreCase("jre"))
          file = file.getParentFile(); 
        for (String str : defaultToolsLocation)
          file = new File(file, str); 
        if (!file.exists())
          throw classNotFoundException; 
        URL[] arrayOfURL = { file.toURI().toURL() };
        trace(Level.FINE, arrayOfURL[0].toString());
        classLoader = URLClassLoader.newInstance(arrayOfURL);
        this.refToolClassLoader = new WeakReference(classLoader);
      } 
      return Class.forName(paramString, false, classLoader);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\ToolProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */