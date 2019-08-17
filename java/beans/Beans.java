package java.beans;

import com.sun.beans.finder.ClassFinder;
import java.applet.Applet;
import java.beans.beancontext.BeanContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Modifier;
import java.net.URL;

public class Beans {
  public static Object instantiate(ClassLoader paramClassLoader, String paramString) throws IOException, ClassNotFoundException { return instantiate(paramClassLoader, paramString, null, null); }
  
  public static Object instantiate(ClassLoader paramClassLoader, String paramString, BeanContext paramBeanContext) throws IOException, ClassNotFoundException { return instantiate(paramClassLoader, paramString, paramBeanContext, null); }
  
  public static Object instantiate(ClassLoader paramClassLoader, String paramString, BeanContext paramBeanContext, AppletInitializer paramAppletInitializer) throws IOException, ClassNotFoundException {
    InputStream inputStream;
    ObjectInputStream objectInputStream = null;
    Object object = null;
    boolean bool = false;
    IOException iOException = null;
    if (paramClassLoader == null)
      try {
        paramClassLoader = ClassLoader.getSystemClassLoader();
      } catch (SecurityException securityException) {} 
    String str = paramString.replace('.', '/').concat(".ser");
    if (paramClassLoader == null) {
      inputStream = ClassLoader.getSystemResourceAsStream(str);
    } else {
      inputStream = paramClassLoader.getResourceAsStream(str);
    } 
    if (inputStream != null)
      try {
        if (paramClassLoader == null) {
          objectInputStream = new ObjectInputStream(inputStream);
        } else {
          objectInputStream = new ObjectInputStreamWithLoader(inputStream, paramClassLoader);
        } 
        object = objectInputStream.readObject();
        bool = true;
        objectInputStream.close();
      } catch (IOException iOException1) {
        inputStream.close();
        iOException = iOException1;
      } catch (ClassNotFoundException classNotFoundException) {
        inputStream.close();
        throw classNotFoundException;
      }  
    if (object == null) {
      Class clazz;
      try {
        clazz = ClassFinder.findClass(paramString, paramClassLoader);
      } catch (ClassNotFoundException classNotFoundException) {
        if (iOException != null)
          throw iOException; 
        throw classNotFoundException;
      } 
      if (!Modifier.isPublic(clazz.getModifiers()))
        throw new ClassNotFoundException("" + clazz + " : no public access"); 
      try {
        object = clazz.newInstance();
      } catch (Exception exception) {
        throw new ClassNotFoundException("" + clazz + " : " + exception, exception);
      } 
    } 
    if (object != null) {
      BeansAppletStub beansAppletStub = null;
      if (object instanceof Applet) {
        Applet applet = (Applet)object;
        boolean bool1 = (paramAppletInitializer == null) ? 1 : 0;
        if (bool1) {
          String str1;
          if (bool) {
            str1 = paramString.replace('.', '/').concat(".ser");
          } else {
            str1 = paramString.replace('.', '/').concat(".class");
          } 
          URL uRL1 = null;
          URL uRL2 = null;
          URL uRL3 = null;
          if (paramClassLoader == null) {
            uRL1 = ClassLoader.getSystemResource(str1);
          } else {
            uRL1 = paramClassLoader.getResource(str1);
          } 
          if (uRL1 != null) {
            String str2 = uRL1.toExternalForm();
            if (str2.endsWith(str1)) {
              int i = str2.length() - str1.length();
              uRL2 = new URL(str2.substring(0, i));
              uRL3 = uRL2;
              i = str2.lastIndexOf('/');
              if (i >= 0)
                uRL3 = new URL(str2.substring(0, i + 1)); 
            } 
          } 
          BeansAppletContext beansAppletContext = new BeansAppletContext(applet);
          beansAppletStub = new BeansAppletStub(applet, beansAppletContext, uRL2, uRL3);
          applet.setStub(beansAppletStub);
        } else {
          paramAppletInitializer.initialize(applet, paramBeanContext);
        } 
        if (paramBeanContext != null)
          unsafeBeanContextAdd(paramBeanContext, object); 
        if (!bool) {
          applet.setSize(100, 100);
          applet.init();
        } 
        if (bool1) {
          ((BeansAppletStub)beansAppletStub).active = true;
        } else {
          paramAppletInitializer.activate(applet);
        } 
      } else if (paramBeanContext != null) {
        unsafeBeanContextAdd(paramBeanContext, object);
      } 
    } 
    return object;
  }
  
  private static void unsafeBeanContextAdd(BeanContext paramBeanContext, Object paramObject) { paramBeanContext.add(paramObject); }
  
  public static Object getInstanceOf(Object paramObject, Class<?> paramClass) { return paramObject; }
  
  public static boolean isInstanceOf(Object paramObject, Class<?> paramClass) { return Introspector.isSubclass(paramObject.getClass(), paramClass); }
  
  public static boolean isDesignTime() { return ThreadGroupContext.getContext().isDesignTime(); }
  
  public static boolean isGuiAvailable() { return ThreadGroupContext.getContext().isGuiAvailable(); }
  
  public static void setDesignTime(boolean paramBoolean) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertiesAccess(); 
    ThreadGroupContext.getContext().setDesignTime(paramBoolean);
  }
  
  public static void setGuiAvailable(boolean paramBoolean) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertiesAccess(); 
    ThreadGroupContext.getContext().setGuiAvailable(paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\Beans.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */