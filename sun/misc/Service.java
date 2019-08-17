package sun.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public final class Service<S> extends Object {
  private static final String prefix = "META-INF/services/";
  
  private static void fail(Class<?> paramClass, String paramString, Throwable paramThrowable) throws ServiceConfigurationError {
    ServiceConfigurationError serviceConfigurationError = new ServiceConfigurationError(paramClass.getName() + ": " + paramString);
    serviceConfigurationError.initCause(paramThrowable);
    throw serviceConfigurationError;
  }
  
  private static void fail(Class<?> paramClass, String paramString) throws ServiceConfigurationError { throw new ServiceConfigurationError(paramClass.getName() + ": " + paramString); }
  
  private static void fail(Class<?> paramClass, URL paramURL, int paramInt, String paramString) throws ServiceConfigurationError { fail(paramClass, paramURL + ":" + paramInt + ": " + paramString); }
  
  private static int parseLine(Class<?> paramClass, URL paramURL, BufferedReader paramBufferedReader, int paramInt, List<String> paramList, Set<String> paramSet) throws IOException, ServiceConfigurationError {
    String str = paramBufferedReader.readLine();
    if (str == null)
      return -1; 
    int i = str.indexOf('#');
    if (i >= 0)
      str = str.substring(0, i); 
    str = str.trim();
    int j = str.length();
    if (j != 0) {
      if (str.indexOf(' ') >= 0 || str.indexOf('\t') >= 0)
        fail(paramClass, paramURL, paramInt, "Illegal configuration-file syntax"); 
      int k = str.codePointAt(0);
      if (!Character.isJavaIdentifierStart(k))
        fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str); 
      int m;
      for (m = Character.charCount(k); m < j; m += Character.charCount(k)) {
        k = str.codePointAt(m);
        if (!Character.isJavaIdentifierPart(k) && k != 46)
          fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str); 
      } 
      if (!paramSet.contains(str)) {
        paramList.add(str);
        paramSet.add(str);
      } 
    } 
    return paramInt + 1;
  }
  
  private static Iterator<String> parse(Class<?> paramClass, URL paramURL, Set<String> paramSet) throws ServiceConfigurationError {
    inputStream = null;
    bufferedReader = null;
    ArrayList arrayList = new ArrayList();
    try {
      inputStream = paramURL.openStream();
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
      i = 1;
      while ((i = parseLine(paramClass, paramURL, bufferedReader, i, arrayList, paramSet)) >= 0);
    } catch (IOException iOException) {
      fail(paramClass, ": " + iOException);
    } finally {
      try {
        if (bufferedReader != null)
          bufferedReader.close(); 
        if (inputStream != null)
          inputStream.close(); 
      } catch (IOException iOException) {
        fail(paramClass, ": " + iOException);
      } 
    } 
    return arrayList.iterator();
  }
  
  public static <S> Iterator<S> providers(Class<S> paramClass, ClassLoader paramClassLoader) throws ServiceConfigurationError { return new LazyIterator(paramClass, paramClassLoader, null); }
  
  public static <S> Iterator<S> providers(Class<S> paramClass) throws ServiceConfigurationError {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    return providers(paramClass, classLoader);
  }
  
  public static <S> Iterator<S> installedProviders(Class<S> paramClass) throws ServiceConfigurationError {
    ClassLoader classLoader1 = ClassLoader.getSystemClassLoader();
    ClassLoader classLoader2 = null;
    while (classLoader1 != null) {
      classLoader2 = classLoader1;
      classLoader1 = classLoader1.getParent();
    } 
    return providers(paramClass, classLoader2);
  }
  
  private static class LazyIterator<S> extends Object implements Iterator<S> {
    Class<S> service;
    
    ClassLoader loader;
    
    Enumeration<URL> configs = null;
    
    Iterator<String> pending = null;
    
    Set<String> returned = new TreeSet();
    
    String nextName = null;
    
    private LazyIterator(Class<S> param1Class, ClassLoader param1ClassLoader) {
      this.service = param1Class;
      this.loader = param1ClassLoader;
    }
    
    public boolean hasNext() throws ServiceConfigurationError {
      if (this.nextName != null)
        return true; 
      if (this.configs == null)
        try {
          String str = "META-INF/services/" + this.service.getName();
          if (this.loader == null) {
            this.configs = ClassLoader.getSystemResources(str);
          } else {
            this.configs = this.loader.getResources(str);
          } 
        } catch (IOException iOException) {
          Service.fail(this.service, ": " + iOException);
        }  
      while (this.pending == null || !this.pending.hasNext()) {
        if (!this.configs.hasMoreElements())
          return false; 
        this.pending = Service.parse(this.service, (URL)this.configs.nextElement(), this.returned);
      } 
      this.nextName = (String)this.pending.next();
      return true;
    }
    
    public S next() throws ServiceConfigurationError {
      if (!hasNext())
        throw new NoSuchElementException(); 
      String str = this.nextName;
      this.nextName = null;
      Class clazz = null;
      try {
        clazz = Class.forName(str, false, this.loader);
      } catch (ClassNotFoundException classNotFoundException) {
        Service.fail(this.service, "Provider " + str + " not found");
      } 
      if (!this.service.isAssignableFrom(clazz))
        Service.fail(this.service, "Provider " + str + " not a subtype"); 
      try {
        return (S)this.service.cast(clazz.newInstance());
      } catch (Throwable throwable) {
        Service.fail(this.service, "Provider " + str + " could not be instantiated", throwable);
        return null;
      } 
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */