package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.JarEntry;
import java.util.spi.ResourceBundleControlProvider;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.util.locale.BaseLocale;
import sun.util.locale.LocaleObjectCache;

public abstract class ResourceBundle {
  private static final int INITIAL_CACHE_SIZE = 32;
  
  private static final ResourceBundle NONEXISTENT_BUNDLE = new ResourceBundle() {
      public Enumeration<String> getKeys() { return null; }
      
      protected Object handleGetObject(String param1String) { return null; }
      
      public String toString() { return "NONEXISTENT_BUNDLE"; }
    };
  
  private static final ConcurrentMap<CacheKey, BundleReference> cacheList = new ConcurrentHashMap(32);
  
  private static final ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
  
  protected ResourceBundle parent = null;
  
  private Locale locale = null;
  
  private String name;
  
  private static final List<ResourceBundleControlProvider> providers;
  
  public String getBaseBundleName() { return this.name; }
  
  public final String getString(String paramString) { return (String)getObject(paramString); }
  
  public final String[] getStringArray(String paramString) { return (String[])getObject(paramString); }
  
  public final Object getObject(String paramString) {
    Object object = handleGetObject(paramString);
    if (object == null) {
      if (this.parent != null)
        object = this.parent.getObject(paramString); 
      if (object == null)
        throw new MissingResourceException("Can't find resource for bundle " + getClass().getName() + ", key " + paramString, getClass().getName(), paramString); 
    } 
    return object;
  }
  
  public Locale getLocale() { return this.locale; }
  
  private static ClassLoader getLoader(Class<?> paramClass) {
    ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
    if (classLoader == null)
      classLoader = INSTANCE; 
    return classLoader;
  }
  
  protected void setParent(ResourceBundle paramResourceBundle) {
    assert paramResourceBundle != NONEXISTENT_BUNDLE;
    this.parent = paramResourceBundle;
  }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString) { return getBundleImpl(paramString, Locale.getDefault(), getLoader(Reflection.getCallerClass()), getDefaultControl(paramString)); }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString, Control paramControl) { return getBundleImpl(paramString, Locale.getDefault(), getLoader(Reflection.getCallerClass()), paramControl); }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString, Locale paramLocale) { return getBundleImpl(paramString, paramLocale, getLoader(Reflection.getCallerClass()), getDefaultControl(paramString)); }
  
  @CallerSensitive
  public static final ResourceBundle getBundle(String paramString, Locale paramLocale, Control paramControl) { return getBundleImpl(paramString, paramLocale, getLoader(Reflection.getCallerClass()), paramControl); }
  
  public static ResourceBundle getBundle(String paramString, Locale paramLocale, ClassLoader paramClassLoader) {
    if (paramClassLoader == null)
      throw new NullPointerException(); 
    return getBundleImpl(paramString, paramLocale, paramClassLoader, getDefaultControl(paramString));
  }
  
  public static ResourceBundle getBundle(String paramString, Locale paramLocale, ClassLoader paramClassLoader, Control paramControl) {
    if (paramClassLoader == null || paramControl == null)
      throw new NullPointerException(); 
    return getBundleImpl(paramString, paramLocale, paramClassLoader, paramControl);
  }
  
  private static Control getDefaultControl(String paramString) {
    if (providers != null)
      for (ResourceBundleControlProvider resourceBundleControlProvider : providers) {
        Control control = resourceBundleControlProvider.getControl(paramString);
        if (control != null)
          return control; 
      }  
    return INSTANCE;
  }
  
  private static ResourceBundle getBundleImpl(String paramString, Locale paramLocale, ClassLoader paramClassLoader, Control paramControl) {
    if (paramLocale == null || paramControl == null)
      throw new NullPointerException(); 
    CacheKey cacheKey1 = new CacheKey(paramString, paramLocale, paramClassLoader);
    ResourceBundle resourceBundle1 = null;
    BundleReference bundleReference = (BundleReference)cacheList.get(cacheKey1);
    if (bundleReference != null) {
      resourceBundle1 = (ResourceBundle)bundleReference.get();
      bundleReference = null;
    } 
    if (isValidBundle(resourceBundle1) && hasValidParentChain(resourceBundle1))
      return resourceBundle1; 
    boolean bool = (paramControl == INSTANCE || paramControl instanceof SingleFormatControl) ? 1 : 0;
    List list = paramControl.getFormats(paramString);
    if (!bool && !checkList(list))
      throw new IllegalArgumentException("Invalid Control: getFormats"); 
    ResourceBundle resourceBundle2 = null;
    for (Locale locale1 = paramLocale; locale1 != null; locale1 = paramControl.getFallbackLocale(paramString, locale1)) {
      List list1 = paramControl.getCandidateLocales(paramString, locale1);
      if (!bool && !checkList(list1))
        throw new IllegalArgumentException("Invalid Control: getCandidateLocales"); 
      if ((resourceBundle1 = findBundle(cacheKey1, list1, list, 0, paramControl, resourceBundle2)).isValidBundle(resourceBundle1)) {
        boolean bool1 = Locale.ROOT.equals(resourceBundle1.locale);
        if (!bool1 || resourceBundle1.locale.equals(paramLocale) || (list1.size() == 1 && resourceBundle1.locale.equals(list1.get(0))))
          break; 
        if (bool1 && resourceBundle2 == null)
          resourceBundle2 = resourceBundle1; 
      } 
    } 
    if (resourceBundle1 == null) {
      if (resourceBundle2 == null)
        throwMissingResourceException(paramString, paramLocale, cacheKey1.getCause()); 
      resourceBundle1 = resourceBundle2;
    } 
    return resourceBundle1;
  }
  
  private static boolean checkList(List<?> paramList) {
    boolean bool = (paramList != null && !paramList.isEmpty());
    if (bool) {
      int i = paramList.size();
      for (byte b = 0; bool && b < i; b++)
        bool = (paramList.get(b) != null); 
    } 
    return bool;
  }
  
  private static ResourceBundle findBundle(CacheKey paramCacheKey, List<Locale> paramList1, List<String> paramList2, int paramInt, Control paramControl, ResourceBundle paramResourceBundle) {
    Locale locale1 = (Locale)paramList1.get(paramInt);
    ResourceBundle resourceBundle1 = null;
    if (paramInt != paramList1.size() - 1) {
      resourceBundle1 = findBundle(paramCacheKey, paramList1, paramList2, paramInt + 1, paramControl, paramResourceBundle);
    } else if (paramResourceBundle != null && Locale.ROOT.equals(locale1)) {
      return paramResourceBundle;
    } 
    Reference reference;
    while ((reference = referenceQueue.poll()) != null)
      cacheList.remove(((CacheKeyReference)reference).getCacheKey()); 
    boolean bool = false;
    paramCacheKey.setLocale(locale1);
    ResourceBundle resourceBundle2;
    if ((resourceBundle2 = findBundleInCache(paramCacheKey, paramControl)).isValidBundle(resourceBundle2)) {
      bool = resourceBundle2.expired;
      if (!bool) {
        if (resourceBundle2.parent == resourceBundle1)
          return resourceBundle2; 
        BundleReference bundleReference = (BundleReference)cacheList.get(paramCacheKey);
        if (bundleReference != null && bundleReference.get() == resourceBundle2)
          cacheList.remove(paramCacheKey, bundleReference); 
      } 
    } 
    if (resourceBundle2 != NONEXISTENT_BUNDLE) {
      cacheKey1 = (CacheKey)paramCacheKey.clone();
      try {
        resourceBundle2 = loadBundle(paramCacheKey, paramList2, paramControl, bool);
        if (resourceBundle2 != null) {
          if (resourceBundle2.parent == null)
            resourceBundle2.setParent(resourceBundle1); 
          resourceBundle2.locale = locale1;
          resourceBundle2 = putBundleInCache(paramCacheKey, resourceBundle2, paramControl);
          return resourceBundle2;
        } 
        putBundleInCache(paramCacheKey, NONEXISTENT_BUNDLE, paramControl);
      } finally {
        if (cacheKey1.getCause() instanceof InterruptedException)
          Thread.currentThread().interrupt(); 
      } 
    } 
    return resourceBundle1;
  }
  
  private static ResourceBundle loadBundle(CacheKey paramCacheKey, List<String> paramList, Control paramControl, boolean paramBoolean) {
    Locale locale1 = paramCacheKey.getLocale();
    ResourceBundle resourceBundle = null;
    int i = paramList.size();
    for (byte b = 0; b < i; b++) {
      String str = (String)paramList.get(b);
      try {
        resourceBundle = paramControl.newBundle(paramCacheKey.getName(), locale1, str, paramCacheKey.getLoader(), paramBoolean);
      } catch (LinkageError linkageError) {
        paramCacheKey.setCause(linkageError);
      } catch (Exception exception) {
        paramCacheKey.setCause(exception);
      } 
      if (resourceBundle != null) {
        paramCacheKey.setFormat(str);
        resourceBundle.name = paramCacheKey.getName();
        resourceBundle.locale = locale1;
        resourceBundle.expired = false;
        break;
      } 
    } 
    return resourceBundle;
  }
  
  private static boolean isValidBundle(ResourceBundle paramResourceBundle) { return (paramResourceBundle != null && paramResourceBundle != NONEXISTENT_BUNDLE); }
  
  private static boolean hasValidParentChain(ResourceBundle paramResourceBundle) {
    long l = System.currentTimeMillis();
    while (paramResourceBundle != null) {
      if (paramResourceBundle.expired)
        return false; 
      CacheKey cacheKey1 = paramResourceBundle.cacheKey;
      if (cacheKey1 != null) {
        long l1 = cacheKey1.expirationTime;
        if (l1 >= 0L && l1 <= l)
          return false; 
      } 
      paramResourceBundle = paramResourceBundle.parent;
    } 
    return true;
  }
  
  private static void throwMissingResourceException(String paramString, Locale paramLocale, Throwable paramThrowable) {
    if (paramThrowable instanceof MissingResourceException)
      paramThrowable = null; 
    throw new MissingResourceException("Can't find bundle for base name " + paramString + ", locale " + paramLocale, paramString + "_" + paramLocale, "", paramThrowable);
  }
  
  private static ResourceBundle findBundleInCache(CacheKey paramCacheKey, Control paramControl) {
    BundleReference bundleReference = (BundleReference)cacheList.get(paramCacheKey);
    if (bundleReference == null)
      return null; 
    ResourceBundle resourceBundle1 = (ResourceBundle)bundleReference.get();
    if (resourceBundle1 == null)
      return null; 
    ResourceBundle resourceBundle2 = resourceBundle1.parent;
    assert resourceBundle2 != NONEXISTENT_BUNDLE;
    if (resourceBundle2 != null && resourceBundle2.expired) {
      assert resourceBundle1 != NONEXISTENT_BUNDLE;
      resourceBundle1.expired = true;
      resourceBundle1.cacheKey = null;
      cacheList.remove(paramCacheKey, bundleReference);
      resourceBundle1 = null;
    } else {
      CacheKey cacheKey1;
      long l = cacheKey1.expirationTime;
      if (!resourceBundle1.expired && l >= 0L && l <= System.currentTimeMillis())
        if (resourceBundle1 != NONEXISTENT_BUNDLE) {
          synchronized (resourceBundle1) {
            l = cacheKey1.expirationTime;
            if (!resourceBundle1.expired && l >= 0L && l <= System.currentTimeMillis()) {
              try {
                resourceBundle1.expired = paramControl.needsReload(cacheKey1.getName(), cacheKey1.getLocale(), cacheKey1.getFormat(), cacheKey1.getLoader(), resourceBundle1, cacheKey1.loadTime);
              } catch (Exception exception) {
                paramCacheKey.setCause(exception);
              } 
              if (resourceBundle1.expired) {
                resourceBundle1.cacheKey = null;
                cacheList.remove(paramCacheKey, bundleReference);
              } else {
                setExpirationTime(cacheKey1, paramControl);
              } 
            } 
          } 
        } else {
          cacheList.remove(paramCacheKey, bundleReference);
          resourceBundle1 = null;
        }  
    } 
    return resourceBundle1;
  }
  
  private static ResourceBundle putBundleInCache(CacheKey paramCacheKey, ResourceBundle paramResourceBundle, Control paramControl) {
    setExpirationTime(paramCacheKey, paramControl);
    if (paramCacheKey.expirationTime != -1L) {
      CacheKey cacheKey1 = (CacheKey)paramCacheKey.clone();
      BundleReference bundleReference1 = new BundleReference(paramResourceBundle, referenceQueue, cacheKey1);
      paramResourceBundle.cacheKey = cacheKey1;
      BundleReference bundleReference2 = (BundleReference)cacheList.putIfAbsent(cacheKey1, bundleReference1);
      if (bundleReference2 != null) {
        ResourceBundle resourceBundle = (ResourceBundle)bundleReference2.get();
        if (resourceBundle != null && !resourceBundle.expired) {
          paramResourceBundle.cacheKey = null;
          paramResourceBundle = resourceBundle;
          bundleReference1.clear();
        } else {
          cacheList.put(cacheKey1, bundleReference1);
        } 
      } 
    } 
    return paramResourceBundle;
  }
  
  private static void setExpirationTime(CacheKey paramCacheKey, Control paramControl) {
    long l = paramControl.getTimeToLive(paramCacheKey.getName(), paramCacheKey.getLocale());
    if (l >= 0L) {
      long l1 = System.currentTimeMillis();
      paramCacheKey.loadTime = l1;
      paramCacheKey.expirationTime = l1 + l;
    } else if (l >= -2L) {
      paramCacheKey.expirationTime = l;
    } else {
      throw new IllegalArgumentException("Invalid Control: TTL=" + l);
    } 
  }
  
  @CallerSensitive
  public static final void clearCache() { clearCache(getLoader(Reflection.getCallerClass())); }
  
  public static final void clearCache(ClassLoader paramClassLoader) {
    if (paramClassLoader == null)
      throw new NullPointerException(); 
    Set set = cacheList.keySet();
    for (CacheKey cacheKey1 : set) {
      if (cacheKey1.getLoader() == paramClassLoader)
        set.remove(cacheKey1); 
    } 
  }
  
  protected abstract Object handleGetObject(String paramString);
  
  public abstract Enumeration<String> getKeys();
  
  public boolean containsKey(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    for (ResourceBundle resourceBundle = this; resourceBundle != null; resourceBundle = resourceBundle.parent) {
      if (resourceBundle.handleKeySet().contains(paramString))
        return true; 
    } 
    return false;
  }
  
  public Set<String> keySet() {
    HashSet hashSet = new HashSet();
    for (ResourceBundle resourceBundle = this; resourceBundle != null; resourceBundle = resourceBundle.parent)
      hashSet.addAll(resourceBundle.handleKeySet()); 
    return hashSet;
  }
  
  protected Set<String> handleKeySet() {
    if (this.keySet == null)
      synchronized (this) {
        if (this.keySet == null) {
          HashSet hashSet = new HashSet();
          Enumeration enumeration = getKeys();
          while (enumeration.hasMoreElements()) {
            String str = (String)enumeration.nextElement();
            if (handleGetObject(str) != null)
              hashSet.add(str); 
          } 
          this.keySet = hashSet;
        } 
      }  
    return this.keySet;
  }
  
  static  {
    ArrayList arrayList = null;
    ServiceLoader serviceLoader = ServiceLoader.loadInstalled(ResourceBundleControlProvider.class);
    for (ResourceBundleControlProvider resourceBundleControlProvider : serviceLoader) {
      if (arrayList == null)
        arrayList = new ArrayList(); 
      arrayList.add(resourceBundleControlProvider);
    } 
    providers = arrayList;
  }
  
  private static class BundleReference extends SoftReference<ResourceBundle> implements CacheKeyReference {
    private ResourceBundle.CacheKey cacheKey;
    
    BundleReference(ResourceBundle param1ResourceBundle, ReferenceQueue<Object> param1ReferenceQueue, ResourceBundle.CacheKey param1CacheKey) {
      super(param1ResourceBundle, param1ReferenceQueue);
      this.cacheKey = param1CacheKey;
    }
    
    public ResourceBundle.CacheKey getCacheKey() { return this.cacheKey; }
  }
  
  private static class CacheKey implements Cloneable {
    private String name;
    
    private Locale locale;
    
    private ResourceBundle.LoaderReference loaderRef;
    
    private String format;
    
    private Throwable cause;
    
    private int hashCodeCache;
    
    CacheKey(String param1String, Locale param1Locale, ClassLoader param1ClassLoader) {
      this.name = param1String;
      this.locale = param1Locale;
      if (param1ClassLoader == null) {
        this.loaderRef = null;
      } else {
        this.loaderRef = new ResourceBundle.LoaderReference(param1ClassLoader, referenceQueue, this);
      } 
      calculateHashCode();
    }
    
    String getName() { return this.name; }
    
    CacheKey setName(String param1String) {
      if (!this.name.equals(param1String)) {
        this.name = param1String;
        calculateHashCode();
      } 
      return this;
    }
    
    Locale getLocale() { return this.locale; }
    
    CacheKey setLocale(Locale param1Locale) {
      if (!this.locale.equals(param1Locale)) {
        this.locale = param1Locale;
        calculateHashCode();
      } 
      return this;
    }
    
    ClassLoader getLoader() { return (this.loaderRef != null) ? (ClassLoader)this.loaderRef.get() : null; }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      try {
        CacheKey cacheKey = (CacheKey)param1Object;
        if (this.hashCodeCache != cacheKey.hashCodeCache)
          return false; 
        if (!this.name.equals(cacheKey.name))
          return false; 
        if (!this.locale.equals(cacheKey.locale))
          return false; 
        if (this.loaderRef == null)
          return (cacheKey.loaderRef == null); 
        ClassLoader classLoader = (ClassLoader)this.loaderRef.get();
        return (cacheKey.loaderRef != null && classLoader != null && classLoader == cacheKey.loaderRef.get());
      } catch (NullPointerException|ClassCastException nullPointerException) {
        return false;
      } 
    }
    
    public int hashCode() { return this.hashCodeCache; }
    
    private void calculateHashCode() {
      this.hashCodeCache = this.name.hashCode() << 3;
      this.hashCodeCache ^= this.locale.hashCode();
      ClassLoader classLoader = getLoader();
      if (classLoader != null)
        this.hashCodeCache ^= classLoader.hashCode(); 
    }
    
    public Object clone() {
      try {
        CacheKey cacheKey = (CacheKey)super.clone();
        if (this.loaderRef != null)
          cacheKey.loaderRef = new ResourceBundle.LoaderReference((ClassLoader)this.loaderRef.get(), referenceQueue, cacheKey); 
        cacheKey.cause = null;
        return cacheKey;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        throw new InternalError(cloneNotSupportedException);
      } 
    }
    
    String getFormat() { return this.format; }
    
    void setFormat(String param1String) { this.format = param1String; }
    
    private void setCause(Throwable param1Throwable) {
      if (this.cause == null) {
        this.cause = param1Throwable;
      } else if (this.cause instanceof ClassNotFoundException) {
        this.cause = param1Throwable;
      } 
    }
    
    private Throwable getCause() { return this.cause; }
    
    public String toString() {
      String str = this.locale.toString();
      if (str.length() == 0)
        if (this.locale.getVariant().length() != 0) {
          str = "__" + this.locale.getVariant();
        } else {
          str = "\"\"";
        }  
      return "CacheKey[" + this.name + ", lc=" + str + ", ldr=" + getLoader() + "(format=" + this.format + ")]";
    }
  }
  
  private static interface CacheKeyReference {
    ResourceBundle.CacheKey getCacheKey();
  }
  
  public static class Control {
    public static final List<String> FORMAT_DEFAULT = Collections.unmodifiableList(Arrays.asList(new String[] { "java.class", "java.properties" }));
    
    public static final List<String> FORMAT_CLASS = Collections.unmodifiableList(Arrays.asList(new String[] { "java.class" }));
    
    public static final List<String> FORMAT_PROPERTIES = Collections.unmodifiableList(Arrays.asList(new String[] { "java.properties" }));
    
    public static final long TTL_DONT_CACHE = -1L;
    
    public static final long TTL_NO_EXPIRATION_CONTROL = -2L;
    
    private static final Control INSTANCE = new Control();
    
    private static final CandidateListCache CANDIDATES_CACHE = new CandidateListCache(null);
    
    public static final Control getControl(List<String> param1List) {
      if (param1List.equals(FORMAT_PROPERTIES))
        return PROPERTIES_ONLY; 
      if (param1List.equals(FORMAT_CLASS))
        return CLASS_ONLY; 
      if (param1List.equals(FORMAT_DEFAULT))
        return INSTANCE; 
      throw new IllegalArgumentException();
    }
    
    public static final Control getNoFallbackControl(List<String> param1List) {
      if (param1List.equals(FORMAT_DEFAULT))
        return NO_FALLBACK; 
      if (param1List.equals(FORMAT_PROPERTIES))
        return PROPERTIES_ONLY_NO_FALLBACK; 
      if (param1List.equals(FORMAT_CLASS))
        return CLASS_ONLY_NO_FALLBACK; 
      throw new IllegalArgumentException();
    }
    
    public List<String> getFormats(String param1String) {
      if (param1String == null)
        throw new NullPointerException(); 
      return FORMAT_DEFAULT;
    }
    
    public List<Locale> getCandidateLocales(String param1String, Locale param1Locale) {
      if (param1String == null)
        throw new NullPointerException(); 
      return new ArrayList((Collection)CANDIDATES_CACHE.get(param1Locale.getBaseLocale()));
    }
    
    public Locale getFallbackLocale(String param1String, Locale param1Locale) {
      if (param1String == null)
        throw new NullPointerException(); 
      Locale locale = Locale.getDefault();
      return param1Locale.equals(locale) ? null : locale;
    }
    
    public ResourceBundle newBundle(String param1String1, Locale param1Locale, String param1String2, ClassLoader param1ClassLoader, boolean param1Boolean) throws IllegalAccessException, InstantiationException, IOException {
      String str = toBundleName(param1String1, param1Locale);
      ResourceBundle resourceBundle = null;
      if (param1String2.equals("java.class")) {
        try {
          Class clazz = param1ClassLoader.loadClass(str);
          if (ResourceBundle.class.isAssignableFrom(clazz)) {
            resourceBundle = (ResourceBundle)clazz.newInstance();
          } else {
            throw new ClassCastException(clazz.getName() + " cannot be cast to ResourceBundle");
          } 
        } catch (ClassNotFoundException classNotFoundException) {}
      } else if (param1String2.equals("java.properties")) {
        final String resourceName = toResourceName0(str, "properties");
        if (str1 == null)
          return resourceBundle; 
        final ClassLoader classLoader = param1ClassLoader;
        final boolean reloadFlag = param1Boolean;
        inputStream = null;
        try {
          inputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                public InputStream run() throws IOException {
                  InputStream inputStream = null;
                  if (reloadFlag) {
                    URL uRL = classLoader.getResource(resourceName);
                    if (uRL != null) {
                      URLConnection uRLConnection = uRL.openConnection();
                      if (uRLConnection != null) {
                        uRLConnection.setUseCaches(false);
                        inputStream = uRLConnection.getInputStream();
                      } 
                    } 
                  } else {
                    inputStream = classLoader.getResourceAsStream(resourceName);
                  } 
                  return inputStream;
                }
              });
        } catch (PrivilegedActionException privilegedActionException) {
          throw (IOException)privilegedActionException.getException();
        } 
        if (inputStream != null)
          try {
            resourceBundle = new PropertyResourceBundle(inputStream);
          } finally {
            inputStream.close();
          }  
      } else {
        throw new IllegalArgumentException("unknown format: " + param1String2);
      } 
      return resourceBundle;
    }
    
    public long getTimeToLive(String param1String, Locale param1Locale) {
      if (param1String == null || param1Locale == null)
        throw new NullPointerException(); 
      return -2L;
    }
    
    public boolean needsReload(String param1String1, Locale param1Locale, String param1String2, ClassLoader param1ClassLoader, ResourceBundle param1ResourceBundle, long param1Long) {
      if (param1ResourceBundle == null)
        throw new NullPointerException(); 
      if (param1String2.equals("java.class") || param1String2.equals("java.properties"))
        param1String2 = param1String2.substring(5); 
      boolean bool = false;
      try {
        String str = toResourceName0(toBundleName(param1String1, param1Locale), param1String2);
        if (str == null)
          return bool; 
        URL uRL = param1ClassLoader.getResource(str);
        if (uRL != null) {
          long l = 0L;
          URLConnection uRLConnection = uRL.openConnection();
          if (uRLConnection != null) {
            uRLConnection.setUseCaches(false);
            if (uRLConnection instanceof JarURLConnection) {
              JarEntry jarEntry = ((JarURLConnection)uRLConnection).getJarEntry();
              if (jarEntry != null) {
                l = jarEntry.getTime();
                if (l == -1L)
                  l = 0L; 
              } 
            } else {
              l = uRLConnection.getLastModified();
            } 
          } 
          bool = (l >= param1Long);
        } 
      } catch (NullPointerException nullPointerException) {
        throw nullPointerException;
      } catch (Exception exception) {}
      return bool;
    }
    
    public String toBundleName(String param1String, Locale param1Locale) {
      if (param1Locale == Locale.ROOT)
        return param1String; 
      String str1 = param1Locale.getLanguage();
      String str2 = param1Locale.getScript();
      String str3 = param1Locale.getCountry();
      String str4 = param1Locale.getVariant();
      if (str1 == "" && str3 == "" && str4 == "")
        return param1String; 
      StringBuilder stringBuilder = new StringBuilder(param1String);
      stringBuilder.append('_');
      if (str2 != "") {
        if (str4 != "") {
          stringBuilder.append(str1).append('_').append(str2).append('_').append(str3).append('_').append(str4);
        } else if (str3 != "") {
          stringBuilder.append(str1).append('_').append(str2).append('_').append(str3);
        } else {
          stringBuilder.append(str1).append('_').append(str2);
        } 
      } else if (str4 != "") {
        stringBuilder.append(str1).append('_').append(str3).append('_').append(str4);
      } else if (str3 != "") {
        stringBuilder.append(str1).append('_').append(str3);
      } else {
        stringBuilder.append(str1);
      } 
      return stringBuilder.toString();
    }
    
    public final String toResourceName(String param1String1, String param1String2) {
      StringBuilder stringBuilder = new StringBuilder(param1String1.length() + 1 + param1String2.length());
      stringBuilder.append(param1String1.replace('.', '/')).append('.').append(param1String2);
      return stringBuilder.toString();
    }
    
    private String toResourceName0(String param1String1, String param1String2) { return param1String1.contains("://") ? null : toResourceName(param1String1, param1String2); }
    
    private static class CandidateListCache extends LocaleObjectCache<BaseLocale, List<Locale>> {
      private CandidateListCache() {}
      
      protected List<Locale> createObject(BaseLocale param2BaseLocale) {
        String str1 = param2BaseLocale.getLanguage();
        String str2 = param2BaseLocale.getScript();
        String str3 = param2BaseLocale.getRegion();
        String str4 = param2BaseLocale.getVariant();
        boolean bool1 = false;
        boolean bool2 = false;
        if (str1.equals("no"))
          if (str3.equals("NO") && str4.equals("NY")) {
            str4 = "";
            bool2 = true;
          } else {
            bool1 = true;
          }  
        if (str1.equals("nb") || bool1) {
          List list = getDefaultList("nb", str2, str3, str4);
          LinkedList linkedList = new LinkedList();
          for (Locale locale : list) {
            linkedList.add(locale);
            if (locale.getLanguage().length() == 0)
              break; 
            linkedList.add(Locale.getInstance("no", locale.getScript(), locale.getCountry(), locale.getVariant(), null));
          } 
          return linkedList;
        } 
        if (str1.equals("nn") || bool2) {
          List list = getDefaultList("nn", str2, str3, str4);
          int i = list.size() - 1;
          list.add(i++, Locale.getInstance("no", "NO", "NY"));
          list.add(i++, Locale.getInstance("no", "NO", ""));
          list.add(i++, Locale.getInstance("no", "", ""));
          return list;
        } 
        if (str1.equals("zh"))
          if (str2.length() == 0 && str3.length() > 0) {
            switch (str3) {
              case "TW":
              case "HK":
              case "MO":
                str2 = "Hant";
                break;
              case "CN":
              case "SG":
                str2 = "Hans";
                break;
            } 
          } else if (str2.length() > 0 && str3.length() == 0) {
            switch (str2) {
              case "Hans":
                str3 = "CN";
                break;
              case "Hant":
                str3 = "TW";
                break;
            } 
          }  
        return getDefaultList(str1, str2, str3, str4);
      }
      
      private static List<Locale> getDefaultList(String param2String1, String param2String2, String param2String3, String param2String4) {
        LinkedList linkedList1 = null;
        if (param2String4.length() > 0) {
          linkedList1 = new LinkedList();
          int i;
          for (i = param2String4.length(); i != -1; i = param2String4.lastIndexOf('_', --i))
            linkedList1.add(param2String4.substring(0, i)); 
        } 
        LinkedList linkedList2 = new LinkedList();
        if (linkedList1 != null)
          for (String str : linkedList1)
            linkedList2.add(Locale.getInstance(param2String1, param2String2, param2String3, str, null));  
        if (param2String3.length() > 0)
          linkedList2.add(Locale.getInstance(param2String1, param2String2, param2String3, "", null)); 
        if (param2String2.length() > 0) {
          linkedList2.add(Locale.getInstance(param2String1, param2String2, "", "", null));
          if (linkedList1 != null)
            for (String str : linkedList1)
              linkedList2.add(Locale.getInstance(param2String1, "", param2String3, str, null));  
          if (param2String3.length() > 0)
            linkedList2.add(Locale.getInstance(param2String1, "", param2String3, "", null)); 
        } 
        if (param2String1.length() > 0)
          linkedList2.add(Locale.getInstance(param2String1, "", "", "", null)); 
        linkedList2.add(Locale.ROOT);
        return linkedList2;
      }
    }
  }
  
  private static class LoaderReference extends WeakReference<ClassLoader> implements CacheKeyReference {
    private ResourceBundle.CacheKey cacheKey;
    
    LoaderReference(ClassLoader param1ClassLoader, ReferenceQueue<Object> param1ReferenceQueue, ResourceBundle.CacheKey param1CacheKey) {
      super(param1ClassLoader, param1ReferenceQueue);
      this.cacheKey = param1CacheKey;
    }
    
    public ResourceBundle.CacheKey getCacheKey() { return this.cacheKey; }
  }
  
  private static final class NoFallbackControl extends SingleFormatControl {
    private static final ResourceBundle.Control NO_FALLBACK = new NoFallbackControl(FORMAT_DEFAULT);
    
    private static final ResourceBundle.Control PROPERTIES_ONLY_NO_FALLBACK = new NoFallbackControl(FORMAT_PROPERTIES);
    
    private static final ResourceBundle.Control CLASS_ONLY_NO_FALLBACK = new NoFallbackControl(FORMAT_CLASS);
    
    protected NoFallbackControl(List<String> param1List) { super(param1List); }
    
    public Locale getFallbackLocale(String param1String, Locale param1Locale) {
      if (param1String == null || param1Locale == null)
        throw new NullPointerException(); 
      return null;
    }
  }
  
  private static class RBClassLoader extends ClassLoader {
    private static final RBClassLoader INSTANCE = (RBClassLoader)AccessController.doPrivileged(new PrivilegedAction<RBClassLoader>() {
          public ResourceBundle.RBClassLoader run() { return new ResourceBundle.RBClassLoader(null); }
        });
    
    private static final ClassLoader loader;
    
    private RBClassLoader() {}
    
    public Class<?> loadClass(String param1String) throws ClassNotFoundException { return (loader != null) ? loader.loadClass(param1String) : Class.forName(param1String); }
    
    public URL getResource(String param1String) { return (loader != null) ? loader.getResource(param1String) : ClassLoader.getSystemResource(param1String); }
    
    public InputStream getResourceAsStream(String param1String) { return (loader != null) ? loader.getResourceAsStream(param1String) : ClassLoader.getSystemResourceAsStream(param1String); }
    
    static  {
      ClassLoader classLoader1;
      ClassLoader classLoader2;
      for (classLoader1 = ClassLoader.getSystemClassLoader(); (classLoader2 = classLoader1.getParent()) != null; classLoader1 = classLoader2);
      loader = classLoader1;
    }
  }
  
  private static class SingleFormatControl extends Control {
    private static final ResourceBundle.Control PROPERTIES_ONLY = new SingleFormatControl(FORMAT_PROPERTIES);
    
    private static final ResourceBundle.Control CLASS_ONLY = new SingleFormatControl(FORMAT_CLASS);
    
    private final List<String> formats;
    
    protected SingleFormatControl(List<String> param1List) { this.formats = param1List; }
    
    public List<String> getFormats(String param1String) {
      if (param1String == null)
        throw new NullPointerException(); 
      return this.formats;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */