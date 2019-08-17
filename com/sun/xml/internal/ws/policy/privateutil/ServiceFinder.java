package com.sun.xml.internal.ws.policy.privateutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

final class ServiceFinder<T> extends Object implements Iterable<T> {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(ServiceFinder.class);
  
  private static final String prefix = "META-INF/services/";
  
  private final Class<T> serviceClass;
  
  private final ClassLoader classLoader;
  
  static <T> ServiceFinder<T> find(Class<T> paramClass, ClassLoader paramClassLoader) {
    if (null == paramClass)
      throw (NullPointerException)LOGGER.logSevereException(new NullPointerException(LocalizationMessages.WSP_0032_SERVICE_CAN_NOT_BE_NULL())); 
    return new ServiceFinder(paramClass, paramClassLoader);
  }
  
  public static <T> ServiceFinder<T> find(Class<T> paramClass) { return find(paramClass, Thread.currentThread().getContextClassLoader()); }
  
  private ServiceFinder(Class<T> paramClass, ClassLoader paramClassLoader) {
    this.serviceClass = paramClass;
    this.classLoader = paramClassLoader;
  }
  
  public Iterator<T> iterator() { return new LazyIterator(this.serviceClass, this.classLoader, null); }
  
  public T[] toArray() {
    ArrayList arrayList = new ArrayList();
    for (Object object : this)
      arrayList.add(object); 
    return (T[])arrayList.toArray((Object[])Array.newInstance(this.serviceClass, arrayList.size()));
  }
  
  private static void fail(Class paramClass, String paramString, Throwable paramThrowable) throws ServiceConfigurationError {
    ServiceConfigurationError serviceConfigurationError = new ServiceConfigurationError(LocalizationMessages.WSP_0025_SPI_FAIL_SERVICE_MSG(paramClass.getName(), paramString));
    if (null != paramThrowable)
      serviceConfigurationError.initCause(paramThrowable); 
    throw (ServiceConfigurationError)LOGGER.logSevereException(serviceConfigurationError);
  }
  
  private static void fail(Class paramClass, URL paramURL, int paramInt, String paramString, Throwable paramThrowable) throws ServiceConfigurationError { fail(paramClass, LocalizationMessages.WSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG(paramURL, Integer.valueOf(paramInt), paramString), paramThrowable); }
  
  private static int parseLine(Class paramClass, URL paramURL, BufferedReader paramBufferedReader, int paramInt, List<String> paramList, Set<String> paramSet) throws IOException, ServiceConfigurationError {
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
        fail(paramClass, paramURL, paramInt, LocalizationMessages.WSP_0067_ILLEGAL_CFG_FILE_SYNTAX(), null); 
      int k = str.codePointAt(0);
      if (!Character.isJavaIdentifierStart(k))
        fail(paramClass, paramURL, paramInt, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(str), null); 
      int m;
      for (m = Character.charCount(k); m < j; m += Character.charCount(k)) {
        k = str.codePointAt(m);
        if (!Character.isJavaIdentifierPart(k) && k != 46)
          fail(paramClass, paramURL, paramInt, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(str), null); 
      } 
      if (!paramSet.contains(str)) {
        paramList.add(str);
        paramSet.add(str);
      } 
    } 
    return paramInt + 1;
  }
  
  private static Iterator<String> parse(Class paramClass, URL paramURL, Set<String> paramSet) throws ServiceConfigurationError {
    inputStream = null;
    bufferedReader = null;
    ArrayList arrayList = new ArrayList();
    try {
      inputStream = paramURL.openStream();
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
      i = 1;
      while ((i = parseLine(paramClass, paramURL, bufferedReader, i, arrayList, paramSet)) >= 0);
    } catch (IOException iOException) {
      fail(paramClass, ": " + iOException, iOException);
    } finally {
      try {
        if (bufferedReader != null)
          bufferedReader.close(); 
        if (inputStream != null)
          inputStream.close(); 
      } catch (IOException iOException) {
        fail(paramClass, ": " + iOException, iOException);
      } 
    } 
    return arrayList.iterator();
  }
  
  private static class LazyIterator<T> extends Object implements Iterator<T> {
    Class<T> service;
    
    ClassLoader loader;
    
    Enumeration<URL> configs = null;
    
    Iterator<String> pending = null;
    
    Set<String> returned = new TreeSet();
    
    String nextName = null;
    
    private LazyIterator(Class<T> param1Class, ClassLoader param1ClassLoader) {
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
          ServiceFinder.fail(this.service, ": " + iOException, iOException);
        }  
      while (this.pending == null || !this.pending.hasNext()) {
        if (!this.configs.hasMoreElements())
          return false; 
        this.pending = ServiceFinder.parse(this.service, (URL)this.configs.nextElement(), this.returned);
      } 
      this.nextName = (String)this.pending.next();
      return true;
    }
    
    public T next() throws ServiceConfigurationError {
      if (!hasNext())
        throw new NoSuchElementException(); 
      String str = this.nextName;
      this.nextName = null;
      try {
        return (T)this.service.cast(Class.forName(str, true, this.loader).newInstance());
      } catch (ClassNotFoundException classNotFoundException) {
        ServiceFinder.fail(this.service, LocalizationMessages.WSP_0027_SERVICE_PROVIDER_NOT_FOUND(str), classNotFoundException);
      } catch (Exception exception) {
        ServiceFinder.fail(this.service, LocalizationMessages.WSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED(str), exception);
      } 
      return null;
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\privateutil\ServiceFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */