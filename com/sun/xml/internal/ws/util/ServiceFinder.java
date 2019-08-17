package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceFinder<T> extends Object implements Iterable<T> {
  private static final String prefix = "META-INF/services/";
  
  private static WeakHashMap<ClassLoader, ConcurrentHashMap<String, ServiceName[]>> serviceNameCache = new WeakHashMap();
  
  private final Class<T> serviceClass;
  
  @Nullable
  private final ClassLoader classLoader;
  
  @Nullable
  private final ComponentEx component;
  
  public static <T> ServiceFinder<T> find(@NotNull Class<T> paramClass, @Nullable ClassLoader paramClassLoader, Component paramComponent) { return new ServiceFinder(paramClass, paramClassLoader, paramComponent); }
  
  public static <T> ServiceFinder<T> find(@NotNull Class<T> paramClass, Component paramComponent) { return find(paramClass, Thread.currentThread().getContextClassLoader(), paramComponent); }
  
  public static <T> ServiceFinder<T> find(@NotNull Class<T> paramClass, @Nullable ClassLoader paramClassLoader) { return find(paramClass, paramClassLoader, ContainerResolver.getInstance().getContainer()); }
  
  public static <T> ServiceFinder<T> find(Class<T> paramClass) { return find(paramClass, Thread.currentThread().getContextClassLoader()); }
  
  private ServiceFinder(Class<T> paramClass, ClassLoader paramClassLoader, Component paramComponent) {
    this.serviceClass = paramClass;
    this.classLoader = paramClassLoader;
    this.component = getComponentEx(paramComponent);
  }
  
  private static ServiceName[] serviceClassNames(Class paramClass, ClassLoader paramClassLoader) {
    ArrayList arrayList = new ArrayList();
    ServiceNameIterator serviceNameIterator = new ServiceNameIterator(paramClass, paramClassLoader, null);
    while (serviceNameIterator.hasNext())
      arrayList.add(serviceNameIterator.next()); 
    return (ServiceName[])arrayList.toArray(new ServiceName[arrayList.size()]);
  }
  
  public Iterator<T> iterator() {
    LazyIterator lazyIterator = new LazyIterator(this.serviceClass, this.classLoader, null);
    return (this.component != null) ? new CompositeIterator(new Iterator[] { this.component.getIterableSPI(this.serviceClass).iterator(), lazyIterator }) : lazyIterator;
  }
  
  public T[] toArray() {
    ArrayList arrayList = new ArrayList();
    for (Object object : this)
      arrayList.add(object); 
    return (T[])arrayList.toArray((Object[])Array.newInstance(this.serviceClass, arrayList.size()));
  }
  
  private static void fail(Class paramClass, String paramString, Throwable paramThrowable) throws ServiceConfigurationError {
    ServiceConfigurationError serviceConfigurationError = new ServiceConfigurationError(paramClass.getName() + ": " + paramString);
    serviceConfigurationError.initCause(paramThrowable);
    throw serviceConfigurationError;
  }
  
  private static void fail(Class paramClass, String paramString) throws ServiceConfigurationError { throw new ServiceConfigurationError(paramClass.getName() + ": " + paramString); }
  
  private static void fail(Class paramClass, URL paramURL, int paramInt, String paramString) throws ServiceConfigurationError { fail(paramClass, paramURL + ":" + paramInt + ": " + paramString); }
  
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
  
  private static ComponentEx getComponentEx(Component paramComponent) { return (paramComponent instanceof ComponentEx) ? (ComponentEx)paramComponent : ((paramComponent != null) ? new ComponentExWrapper(paramComponent) : null); }
  
  private static class ComponentExWrapper implements ComponentEx {
    private final Component component;
    
    public ComponentExWrapper(Component param1Component) { this.component = param1Component; }
    
    public <S> S getSPI(Class<S> param1Class) { return (S)this.component.getSPI(param1Class); }
    
    public <S> Iterable<S> getIterableSPI(Class<S> param1Class) {
      Object object = getSPI(param1Class);
      return (object != null) ? Collections.singletonList(object) : Collections.emptySet();
    }
  }
  
  private static class CompositeIterator<T> extends Object implements Iterator<T> {
    private final Iterator<Iterator<T>> it;
    
    private Iterator<T> current = null;
    
    public CompositeIterator(Iterator<T>... param1VarArgs) { this.it = Arrays.asList(param1VarArgs).iterator(); }
    
    public boolean hasNext() {
      if (this.current != null && this.current.hasNext())
        return true; 
      while (this.it.hasNext()) {
        this.current = (Iterator)this.it.next();
        if (this.current.hasNext())
          return true; 
      } 
      return false;
    }
    
    public T next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      return (T)this.current.next();
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  private static class LazyIterator<T> extends Object implements Iterator<T> {
    Class<T> service;
    
    @Nullable
    ClassLoader loader;
    
    ServiceFinder.ServiceName[] names;
    
    int index;
    
    private LazyIterator(Class<T> param1Class, ClassLoader param1ClassLoader) {
      this.service = param1Class;
      this.loader = param1ClassLoader;
      this.names = null;
      this.index = 0;
    }
    
    public boolean hasNext() {
      if (this.names == null) {
        ConcurrentHashMap concurrentHashMap = null;
        synchronized (serviceNameCache) {
          concurrentHashMap = (ConcurrentHashMap)serviceNameCache.get(this.loader);
        } 
        this.names = (concurrentHashMap != null) ? (ServiceName[])concurrentHashMap.get(this.service.getName()) : null;
        if (this.names == null) {
          this.names = ServiceFinder.serviceClassNames(this.service, this.loader);
          if (concurrentHashMap == null)
            concurrentHashMap = new ConcurrentHashMap(); 
          concurrentHashMap.put(this.service.getName(), this.names);
          synchronized (serviceNameCache) {
            serviceNameCache.put(this.loader, concurrentHashMap);
          } 
        } 
      } 
      return (this.index < this.names.length);
    }
    
    public T next() {
      if (!hasNext())
        throw new NoSuchElementException(); 
      ServiceFinder.ServiceName serviceName = this.names[this.index++];
      String str = serviceName.className;
      URL uRL = serviceName.config;
      try {
        return (T)this.service.cast(Class.forName(str, true, this.loader).newInstance());
      } catch (ClassNotFoundException classNotFoundException) {
        ServiceFinder.fail(this.service, "Provider " + str + " is specified in " + uRL + " but not found");
      } catch (Exception exception) {
        ServiceFinder.fail(this.service, "Provider " + str + " is specified in " + uRL + "but could not be instantiated: " + exception, exception);
      } 
      return null;
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  private static class ServiceName {
    final String className;
    
    final URL config;
    
    public ServiceName(String param1String, URL param1URL) {
      this.className = param1String;
      this.config = param1URL;
    }
  }
  
  private static class ServiceNameIterator extends Object implements Iterator<ServiceName> {
    Class service;
    
    @Nullable
    ClassLoader loader;
    
    Enumeration<URL> configs = null;
    
    Iterator<String> pending = null;
    
    Set<String> returned = new TreeSet();
    
    String nextName = null;
    
    URL currentConfig = null;
    
    private ServiceNameIterator(Class param1Class, ClassLoader param1ClassLoader) {
      this.service = param1Class;
      this.loader = param1ClassLoader;
    }
    
    public boolean hasNext() {
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
          ServiceFinder.fail(this.service, ": " + iOException);
        }  
      while (this.pending == null || !this.pending.hasNext()) {
        if (!this.configs.hasMoreElements())
          return false; 
        this.currentConfig = (URL)this.configs.nextElement();
        this.pending = ServiceFinder.parse(this.service, this.currentConfig, this.returned);
      } 
      this.nextName = (String)this.pending.next();
      return true;
    }
    
    public ServiceFinder.ServiceName next() throws ServiceConfigurationError {
      if (!hasNext())
        throw new NoSuchElementException(); 
      String str = this.nextName;
      this.nextName = null;
      return new ServiceFinder.ServiceName(str, this.currentConfig);
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\ServiceFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */