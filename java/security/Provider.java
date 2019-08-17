package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.security.util.Debug;

public abstract class Provider extends Properties {
  static final long serialVersionUID = -4298000515446427739L;
  
  private static final Debug debug = Debug.getInstance("provider", "Provider");
  
  private String name;
  
  private String info;
  
  private double version;
  
  private Set<Map.Entry<Object, Object>> entrySet = null;
  
  private int entrySetCallCount = 0;
  
  private boolean initialized;
  
  private boolean legacyChanged;
  
  private boolean servicesChanged;
  
  private Map<String, String> legacyStrings;
  
  private Map<ServiceKey, Service> serviceMap;
  
  private Map<ServiceKey, Service> legacyMap;
  
  private Set<Service> serviceSet;
  
  private static final String ALIAS_PREFIX = "Alg.Alias.";
  
  private static final String ALIAS_PREFIX_LOWER = "alg.alias.";
  
  private static final int ALIAS_LENGTH = "Alg.Alias.".length();
  
  private static final Map<String, EngineDescription> knownEngines = new HashMap();
  
  protected Provider(String paramString1, double paramDouble, String paramString2) {
    this.name = paramString1;
    this.version = paramDouble;
    this.info = paramString2;
    putId();
    this.initialized = true;
  }
  
  public String getName() { return this.name; }
  
  public double getVersion() { return this.version; }
  
  public String getInfo() { return this.info; }
  
  public String toString() { return this.name + " version " + this.version; }
  
  public void clear() {
    check("clearProviderProperties." + this.name);
    if (debug != null)
      debug.println("Remove " + this.name + " provider properties"); 
    implClear();
  }
  
  public void load(InputStream paramInputStream) throws IOException {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println("Load " + this.name + " provider properties"); 
    Properties properties = new Properties();
    properties.load(paramInputStream);
    implPutAll(properties);
  }
  
  public void putAll(Map<?, ?> paramMap) {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println("Put all " + this.name + " provider properties"); 
    implPutAll(paramMap);
  }
  
  public Set<Map.Entry<Object, Object>> entrySet() {
    checkInitialized();
    if (this.entrySet == null)
      if (this.entrySetCallCount++ == 0) {
        this.entrySet = Collections.unmodifiableMap(this).entrySet();
      } else {
        return super.entrySet();
      }  
    if (this.entrySetCallCount != 2)
      throw new RuntimeException("Internal error."); 
    return this.entrySet;
  }
  
  public Set<Object> keySet() {
    checkInitialized();
    return Collections.unmodifiableSet(super.keySet());
  }
  
  public Collection<Object> values() {
    checkInitialized();
    return Collections.unmodifiableCollection(super.values());
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println("Set " + this.name + " provider property [" + paramObject1 + "/" + paramObject2 + "]"); 
    return implPut(paramObject1, paramObject2);
  }
  
  public Object putIfAbsent(Object paramObject1, Object paramObject2) {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println("Set " + this.name + " provider property [" + paramObject1 + "/" + paramObject2 + "]"); 
    return implPutIfAbsent(paramObject1, paramObject2);
  }
  
  public Object remove(Object paramObject) {
    check("removeProviderProperty." + this.name);
    if (debug != null)
      debug.println("Remove " + this.name + " provider property " + paramObject); 
    return implRemove(paramObject);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2) {
    check("removeProviderProperty." + this.name);
    if (debug != null)
      debug.println("Remove " + this.name + " provider property " + paramObject1); 
    return implRemove(paramObject1, paramObject2);
  }
  
  public boolean replace(Object paramObject1, Object paramObject2, Object paramObject3) {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println("Replace " + this.name + " provider property " + paramObject1); 
    return implReplace(paramObject1, paramObject2, paramObject3);
  }
  
  public Object replace(Object paramObject1, Object paramObject2) {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println("Replace " + this.name + " provider property " + paramObject1); 
    return implReplace(paramObject1, paramObject2);
  }
  
  public void replaceAll(BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println("ReplaceAll " + this.name + " provider property "); 
    implReplaceAll(paramBiFunction);
  }
  
  public Object compute(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    check("putProviderProperty." + this.name);
    check("removeProviderProperty" + this.name);
    if (debug != null)
      debug.println("Compute " + this.name + " provider property " + paramObject); 
    return implCompute(paramObject, paramBiFunction);
  }
  
  public Object computeIfAbsent(Object paramObject, Function<? super Object, ? extends Object> paramFunction) {
    check("putProviderProperty." + this.name);
    check("removeProviderProperty" + this.name);
    if (debug != null)
      debug.println("ComputeIfAbsent " + this.name + " provider property " + paramObject); 
    return implComputeIfAbsent(paramObject, paramFunction);
  }
  
  public Object computeIfPresent(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    check("putProviderProperty." + this.name);
    check("removeProviderProperty" + this.name);
    if (debug != null)
      debug.println("ComputeIfPresent " + this.name + " provider property " + paramObject); 
    return implComputeIfPresent(paramObject, paramBiFunction);
  }
  
  public Object merge(Object paramObject1, Object paramObject2, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    check("putProviderProperty." + this.name);
    check("removeProviderProperty" + this.name);
    if (debug != null)
      debug.println("Merge " + this.name + " provider property " + paramObject1); 
    return implMerge(paramObject1, paramObject2, paramBiFunction);
  }
  
  public Object get(Object paramObject) {
    checkInitialized();
    return super.get(paramObject);
  }
  
  public Object getOrDefault(Object paramObject1, Object paramObject2) {
    checkInitialized();
    return super.getOrDefault(paramObject1, paramObject2);
  }
  
  public void forEach(BiConsumer<? super Object, ? super Object> paramBiConsumer) {
    checkInitialized();
    super.forEach(paramBiConsumer);
  }
  
  public Enumeration<Object> keys() {
    checkInitialized();
    return super.keys();
  }
  
  public Enumeration<Object> elements() {
    checkInitialized();
    return super.elements();
  }
  
  public String getProperty(String paramString) {
    checkInitialized();
    return super.getProperty(paramString);
  }
  
  private void checkInitialized() {
    if (!this.initialized)
      throw new IllegalStateException(); 
  }
  
  private void check(String paramString) {
    checkInitialized();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSecurityAccess(paramString); 
  }
  
  private void putId() {
    super.put("Provider.id name", String.valueOf(this.name));
    super.put("Provider.id version", String.valueOf(this.version));
    super.put("Provider.id info", String.valueOf(this.info));
    super.put("Provider.id className", getClass().getName());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    HashMap hashMap = new HashMap();
    for (Map.Entry entry : super.entrySet())
      hashMap.put(entry.getKey(), entry.getValue()); 
    this.defaults = null;
    paramObjectInputStream.defaultReadObject();
    implClear();
    this.initialized = true;
    putAll(hashMap);
  }
  
  private boolean checkLegacy(Object paramObject) {
    String str = (String)paramObject;
    if (str.startsWith("Provider."))
      return false; 
    this.legacyChanged = true;
    if (this.legacyStrings == null)
      this.legacyStrings = new LinkedHashMap(); 
    return true;
  }
  
  private void implPutAll(Map<?, ?> paramMap) {
    for (Map.Entry entry : paramMap.entrySet())
      implPut(entry.getKey(), entry.getValue()); 
  }
  
  private Object implRemove(Object paramObject) {
    if (paramObject instanceof String) {
      if (!checkLegacy(paramObject))
        return null; 
      this.legacyStrings.remove((String)paramObject);
    } 
    return super.remove(paramObject);
  }
  
  private boolean implRemove(Object paramObject1, Object paramObject2) {
    if (paramObject1 instanceof String && paramObject2 instanceof String) {
      if (!checkLegacy(paramObject1))
        return false; 
      this.legacyStrings.remove((String)paramObject1, paramObject2);
    } 
    return super.remove(paramObject1, paramObject2);
  }
  
  private boolean implReplace(Object paramObject1, Object paramObject2, Object paramObject3) {
    if (paramObject1 instanceof String && paramObject2 instanceof String && paramObject3 instanceof String) {
      if (!checkLegacy(paramObject1))
        return false; 
      this.legacyStrings.replace((String)paramObject1, (String)paramObject2, (String)paramObject3);
    } 
    return super.replace(paramObject1, paramObject2, paramObject3);
  }
  
  private Object implReplace(Object paramObject1, Object paramObject2) {
    if (paramObject1 instanceof String && paramObject2 instanceof String) {
      if (!checkLegacy(paramObject1))
        return null; 
      this.legacyStrings.replace((String)paramObject1, (String)paramObject2);
    } 
    return super.replace(paramObject1, paramObject2);
  }
  
  private void implReplaceAll(BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    this.legacyChanged = true;
    if (this.legacyStrings == null) {
      this.legacyStrings = new LinkedHashMap();
    } else {
      this.legacyStrings.replaceAll(paramBiFunction);
    } 
    super.replaceAll(paramBiFunction);
  }
  
  private Object implMerge(Object paramObject1, Object paramObject2, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    if (paramObject1 instanceof String && paramObject2 instanceof String) {
      if (!checkLegacy(paramObject1))
        return null; 
      this.legacyStrings.merge((String)paramObject1, (String)paramObject2, paramBiFunction);
    } 
    return super.merge(paramObject1, paramObject2, paramBiFunction);
  }
  
  private Object implCompute(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    if (paramObject instanceof String) {
      if (!checkLegacy(paramObject))
        return null; 
      this.legacyStrings.computeIfAbsent((String)paramObject, (Function)paramBiFunction);
    } 
    return super.compute(paramObject, paramBiFunction);
  }
  
  private Object implComputeIfAbsent(Object paramObject, Function<? super Object, ? extends Object> paramFunction) {
    if (paramObject instanceof String) {
      if (!checkLegacy(paramObject))
        return null; 
      this.legacyStrings.computeIfAbsent((String)paramObject, paramFunction);
    } 
    return super.computeIfAbsent(paramObject, paramFunction);
  }
  
  private Object implComputeIfPresent(Object paramObject, BiFunction<? super Object, ? super Object, ? extends Object> paramBiFunction) {
    if (paramObject instanceof String) {
      if (!checkLegacy(paramObject))
        return null; 
      this.legacyStrings.computeIfPresent((String)paramObject, paramBiFunction);
    } 
    return super.computeIfPresent(paramObject, paramBiFunction);
  }
  
  private Object implPut(Object paramObject1, Object paramObject2) {
    if (paramObject1 instanceof String && paramObject2 instanceof String) {
      if (!checkLegacy(paramObject1))
        return null; 
      this.legacyStrings.put((String)paramObject1, (String)paramObject2);
    } 
    return super.put(paramObject1, paramObject2);
  }
  
  private Object implPutIfAbsent(Object paramObject1, Object paramObject2) {
    if (paramObject1 instanceof String && paramObject2 instanceof String) {
      if (!checkLegacy(paramObject1))
        return null; 
      this.legacyStrings.putIfAbsent((String)paramObject1, (String)paramObject2);
    } 
    return super.putIfAbsent(paramObject1, paramObject2);
  }
  
  private void implClear() {
    if (this.legacyStrings != null)
      this.legacyStrings.clear(); 
    if (this.legacyMap != null)
      this.legacyMap.clear(); 
    if (this.serviceMap != null)
      this.serviceMap.clear(); 
    this.legacyChanged = false;
    this.servicesChanged = false;
    this.serviceSet = null;
    super.clear();
    putId();
  }
  
  private void ensureLegacyParsed() {
    if (!this.legacyChanged || this.legacyStrings == null)
      return; 
    this.serviceSet = null;
    if (this.legacyMap == null) {
      this.legacyMap = new LinkedHashMap();
    } else {
      this.legacyMap.clear();
    } 
    for (Map.Entry entry : this.legacyStrings.entrySet())
      parseLegacyPut((String)entry.getKey(), (String)entry.getValue()); 
    removeInvalidServices(this.legacyMap);
    this.legacyChanged = false;
  }
  
  private void removeInvalidServices(Map<ServiceKey, Service> paramMap) {
    Iterator iterator = paramMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Service service;
      if (!service.isValid())
        iterator.remove(); 
    } 
  }
  
  private String[] getTypeAndAlgorithm(String paramString) {
    int i = paramString.indexOf(".");
    if (i < 1) {
      if (debug != null)
        debug.println("Ignoring invalid entry in provider " + this.name + ":" + paramString); 
      return null;
    } 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    return new String[] { str1, str2 };
  }
  
  private void parseLegacyPut(String paramString1, String paramString2) {
    if (paramString1.toLowerCase(Locale.ENGLISH).startsWith("alg.alias.")) {
      String str1 = paramString2;
      String str2 = paramString1.substring(ALIAS_LENGTH);
      String[] arrayOfString = getTypeAndAlgorithm(str2);
      if (arrayOfString == null)
        return; 
      String str3 = getEngineName(arrayOfString[0]);
      String str4 = arrayOfString[1].intern();
      ServiceKey serviceKey = new ServiceKey(str3, str1, true, null);
      Service service = (Service)this.legacyMap.get(serviceKey);
      if (service == null) {
        service.type = str3;
        service.algorithm = str1;
        this.legacyMap.put(serviceKey, service);
      } 
      this.legacyMap.put(new ServiceKey(str3, str4, true, null), service);
      service.addAlias(str4);
    } else {
      String[] arrayOfString = getTypeAndAlgorithm(paramString1);
      if (arrayOfString == null)
        return; 
      int i = arrayOfString[1].indexOf(' ');
      if (i == -1) {
        String str1 = getEngineName(arrayOfString[0]);
        String str2 = arrayOfString[1].intern();
        String str3 = paramString2;
        ServiceKey serviceKey = new ServiceKey(str1, str2, true, null);
        Service service = (Service)this.legacyMap.get(serviceKey);
        if (service == null) {
          service.type = str1;
          service.algorithm = str2;
          this.legacyMap.put(serviceKey, service);
        } 
        service.className = str3;
      } else {
        String str1 = paramString2;
        String str2 = getEngineName(arrayOfString[0]);
        String str3 = arrayOfString[1];
        String str4 = str3.substring(0, i).intern();
        String str5;
        for (str5 = str3.substring(i + 1); str5.startsWith(" "); str5 = str5.substring(1));
        str5 = str5.intern();
        ServiceKey serviceKey = new ServiceKey(str2, str4, true, null);
        Service service = (Service)this.legacyMap.get(serviceKey);
        if (service == null) {
          service.type = str2;
          service.algorithm = str4;
          this.legacyMap.put(serviceKey, service);
        } 
        service.addAttribute(str5, str1);
      } 
    } 
  }
  
  public Service getService(String paramString1, String paramString2) {
    checkInitialized();
    ServiceKey serviceKey = previousKey;
    if (!serviceKey.matches(paramString1, paramString2)) {
      serviceKey = new ServiceKey(paramString1, paramString2, false, null);
      previousKey = serviceKey;
    } 
    if (this.serviceMap != null) {
      Service service = (Service)this.serviceMap.get(serviceKey);
      if (service != null)
        return service; 
    } 
    ensureLegacyParsed();
    return (this.legacyMap != null) ? (Service)this.legacyMap.get(serviceKey) : null;
  }
  
  public Set<Service> getServices() {
    checkInitialized();
    if (this.legacyChanged || this.servicesChanged)
      this.serviceSet = null; 
    if (this.serviceSet == null) {
      ensureLegacyParsed();
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      if (this.serviceMap != null)
        linkedHashSet.addAll(this.serviceMap.values()); 
      if (this.legacyMap != null)
        linkedHashSet.addAll(this.legacyMap.values()); 
      this.serviceSet = Collections.unmodifiableSet(linkedHashSet);
      this.servicesChanged = false;
    } 
    return this.serviceSet;
  }
  
  protected void putService(Service paramService) {
    check("putProviderProperty." + this.name);
    if (debug != null)
      debug.println(this.name + ".putService(): " + paramService); 
    if (paramService == null)
      throw new NullPointerException(); 
    if (paramService.getProvider() != this)
      throw new IllegalArgumentException("service.getProvider() must match this Provider object"); 
    if (this.serviceMap == null)
      this.serviceMap = new LinkedHashMap(); 
    this.servicesChanged = true;
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    ServiceKey serviceKey = new ServiceKey(str1, str2, true, null);
    implRemoveService((Service)this.serviceMap.get(serviceKey));
    this.serviceMap.put(serviceKey, paramService);
    for (String str : paramService.getAliases())
      this.serviceMap.put(new ServiceKey(str1, str, true, null), paramService); 
    putPropertyStrings(paramService);
  }
  
  private void putPropertyStrings(Service paramService) {
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    super.put(str1 + "." + str2, paramService.getClassName());
    for (String str : paramService.getAliases())
      super.put("Alg.Alias." + str1 + "." + str, str2); 
    for (Map.Entry entry : paramService.attributes.entrySet()) {
      String str = str1 + "." + str2 + " " + entry.getKey();
      super.put(str, entry.getValue());
    } 
  }
  
  private void removePropertyStrings(Service paramService) {
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    super.remove(str1 + "." + str2);
    for (String str : paramService.getAliases())
      super.remove("Alg.Alias." + str1 + "." + str); 
    for (Map.Entry entry : paramService.attributes.entrySet()) {
      String str = str1 + "." + str2 + " " + entry.getKey();
      super.remove(str);
    } 
  }
  
  protected void removeService(Service paramService) {
    check("removeProviderProperty." + this.name);
    if (debug != null)
      debug.println(this.name + ".removeService(): " + paramService); 
    if (paramService == null)
      throw new NullPointerException(); 
    implRemoveService(paramService);
  }
  
  private void implRemoveService(Service paramService) {
    if (paramService == null || this.serviceMap == null)
      return; 
    String str1 = paramService.getType();
    String str2 = paramService.getAlgorithm();
    ServiceKey serviceKey = new ServiceKey(str1, str2, false, null);
    Service service = (Service)this.serviceMap.get(serviceKey);
    if (paramService != service)
      return; 
    this.servicesChanged = true;
    this.serviceMap.remove(serviceKey);
    for (String str : paramService.getAliases())
      this.serviceMap.remove(new ServiceKey(str1, str, false, null)); 
    removePropertyStrings(paramService);
  }
  
  private static void addEngine(String paramString1, boolean paramBoolean, String paramString2) {
    EngineDescription engineDescription = new EngineDescription(paramString1, paramBoolean, paramString2);
    knownEngines.put(paramString1.toLowerCase(Locale.ENGLISH), engineDescription);
    knownEngines.put(paramString1, engineDescription);
  }
  
  private static String getEngineName(String paramString) {
    EngineDescription engineDescription = (EngineDescription)knownEngines.get(paramString);
    if (engineDescription == null)
      engineDescription = (EngineDescription)knownEngines.get(paramString.toLowerCase(Locale.ENGLISH)); 
    return (engineDescription == null) ? paramString : engineDescription.name;
  }
  
  static  {
    addEngine("AlgorithmParameterGenerator", false, null);
    addEngine("AlgorithmParameters", false, null);
    addEngine("KeyFactory", false, null);
    addEngine("KeyPairGenerator", false, null);
    addEngine("KeyStore", false, null);
    addEngine("MessageDigest", false, null);
    addEngine("SecureRandom", false, null);
    addEngine("Signature", true, null);
    addEngine("CertificateFactory", false, null);
    addEngine("CertPathBuilder", false, null);
    addEngine("CertPathValidator", false, null);
    addEngine("CertStore", false, "java.security.cert.CertStoreParameters");
    addEngine("Cipher", true, null);
    addEngine("ExemptionMechanism", false, null);
    addEngine("Mac", true, null);
    addEngine("KeyAgreement", true, null);
    addEngine("KeyGenerator", false, null);
    addEngine("SecretKeyFactory", false, null);
    addEngine("KeyManagerFactory", false, null);
    addEngine("SSLContext", false, null);
    addEngine("TrustManagerFactory", false, null);
    addEngine("GssApiMechanism", false, null);
    addEngine("SaslClientFactory", false, null);
    addEngine("SaslServerFactory", false, null);
    addEngine("Policy", false, "java.security.Policy$Parameters");
    addEngine("Configuration", false, "javax.security.auth.login.Configuration$Parameters");
    addEngine("XMLSignatureFactory", false, null);
    addEngine("KeyInfoFactory", false, null);
    addEngine("TransformService", false, null);
    addEngine("TerminalFactory", false, "java.lang.Object");
  }
  
  private static class EngineDescription {
    final String name;
    
    final boolean supportsParameter;
    
    final String constructorParameterClassName;
    
    EngineDescription(String param1String1, boolean param1Boolean, String param1String2) {
      this.name = param1String1;
      this.supportsParameter = param1Boolean;
      this.constructorParameterClassName = param1String2;
    }
    
    Class<?> getConstructorParameterClass() throws ClassNotFoundException {
      Class clazz = this.constructorParameterClass;
      if (clazz == null) {
        clazz = Class.forName(this.constructorParameterClassName);
        this.constructorParameterClass = clazz;
      } 
      return clazz;
    }
  }
  
  public static class Service {
    private String type;
    
    private String algorithm;
    
    private String className;
    
    private final Provider provider;
    
    private List<String> aliases;
    
    private Map<Provider.UString, String> attributes;
    
    private String[] supportedFormats;
    
    private Class[] supportedClasses;
    
    private boolean registered;
    
    private static final Class<?>[] CLASS0 = new Class[0];
    
    private Service(Provider param1Provider) {
      this.provider = param1Provider;
      this.aliases = Collections.emptyList();
      this.attributes = Collections.emptyMap();
    }
    
    private boolean isValid() { return (this.type != null && this.algorithm != null && this.className != null); }
    
    private void addAlias(String param1String) {
      if (this.aliases.isEmpty())
        this.aliases = new ArrayList(2); 
      this.aliases.add(param1String);
    }
    
    void addAttribute(String param1String1, String param1String2) {
      if (this.attributes.isEmpty())
        this.attributes = new HashMap(8); 
      this.attributes.put(new Provider.UString(param1String1), param1String2);
    }
    
    public Service(Provider param1Provider, String param1String1, String param1String2, String param1String3, List<String> param1List, Map<String, String> param1Map) {
      if (param1Provider == null || param1String1 == null || param1String2 == null || param1String3 == null)
        throw new NullPointerException(); 
      this.type = Provider.getEngineName(param1String1);
      this.algorithm = param1String2;
      this.className = param1String3;
      if (param1List == null) {
        this.aliases = Collections.emptyList();
      } else {
        this.aliases = new ArrayList(param1List);
      } 
      if (param1Map == null) {
        this.attributes = Collections.emptyMap();
      } else {
        this.attributes = new HashMap();
        for (Map.Entry entry : param1Map.entrySet())
          this.attributes.put(new Provider.UString((String)entry.getKey()), entry.getValue()); 
      } 
    }
    
    public final String getType() { return this.type; }
    
    public final String getAlgorithm() { return this.algorithm; }
    
    public final Provider getProvider() { return this.provider; }
    
    public final String getClassName() { return this.className; }
    
    private final List<String> getAliases() { return this.aliases; }
    
    public final String getAttribute(String param1String) {
      if (param1String == null)
        throw new NullPointerException(); 
      return (String)this.attributes.get(new Provider.UString(param1String));
    }
    
    public Object newInstance(Object param1Object) {
      if (!this.registered) {
        if (this.provider.getService(this.type, this.algorithm) != this)
          throw new NoSuchAlgorithmException("Service not registered with Provider " + this.provider.getName() + ": " + this); 
        this.registered = true;
      } 
      try {
        Provider.EngineDescription engineDescription = (Provider.EngineDescription)knownEngines.get(this.type);
        if (engineDescription == null)
          return newInstanceGeneric(param1Object); 
        if (engineDescription.constructorParameterClassName == null) {
          if (param1Object != null)
            throw new InvalidParameterException("constructorParameter not used with " + this.type + " engines"); 
          Class clazz = getImplClass();
          Class[] arrayOfClass = new Class[0];
          Constructor constructor1 = clazz.getConstructor(arrayOfClass);
          return constructor1.newInstance(new Object[0]);
        } 
        Class clazz1 = engineDescription.getConstructorParameterClass();
        if (param1Object != null) {
          Class clazz = param1Object.getClass();
          if (!clazz1.isAssignableFrom(clazz))
            throw new InvalidParameterException("constructorParameter must be instanceof " + engineDescription.constructorParameterClassName.replace('$', '.') + " for engine type " + this.type); 
        } 
        Class clazz2 = getImplClass();
        Constructor constructor = clazz2.getConstructor(new Class[] { clazz1 });
        return constructor.newInstance(new Object[] { param1Object });
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw noSuchAlgorithmException;
      } catch (InvocationTargetException invocationTargetException) {
        throw new NoSuchAlgorithmException("Error constructing implementation (algorithm: " + this.algorithm + ", provider: " + this.provider.getName() + ", class: " + this.className + ")", invocationTargetException.getCause());
      } catch (Exception exception) {
        throw new NoSuchAlgorithmException("Error constructing implementation (algorithm: " + this.algorithm + ", provider: " + this.provider.getName() + ", class: " + this.className + ")", exception);
      } 
    }
    
    private Class<?> getImplClass() throws ClassNotFoundException {
      try {
        Reference reference = this.classRef;
        Class clazz = (reference == null) ? null : (Class)reference.get();
        if (clazz == null) {
          ClassLoader classLoader = this.provider.getClass().getClassLoader();
          if (classLoader == null) {
            clazz = Class.forName(this.className);
          } else {
            clazz = classLoader.loadClass(this.className);
          } 
          if (!Modifier.isPublic(clazz.getModifiers()))
            throw new NoSuchAlgorithmException("class configured for " + this.type + " (provider: " + this.provider.getName() + ") is not public."); 
          this.classRef = new WeakReference(clazz);
        } 
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {
        throw new NoSuchAlgorithmException("class configured for " + this.type + " (provider: " + this.provider.getName() + ") cannot be found.", classNotFoundException);
      } 
    }
    
    private Object newInstanceGeneric(Object param1Object) {
      Class clazz1 = getImplClass();
      if (param1Object == null)
        try {
          Class[] arrayOfClass = new Class[0];
          Constructor constructor = clazz1.getConstructor(arrayOfClass);
          return constructor.newInstance(new Object[0]);
        } catch (NoSuchMethodException noSuchMethodException) {
          throw new NoSuchAlgorithmException("No public no-arg constructor found in class " + this.className);
        }  
      Class clazz2 = param1Object.getClass();
      Constructor[] arrayOfConstructor1 = clazz1.getConstructors();
      Constructor[] arrayOfConstructor2 = arrayOfConstructor1;
      int i = arrayOfConstructor2.length;
      byte b = 0;
      while (b < i) {
        Constructor constructor = arrayOfConstructor2[b];
        Class[] arrayOfClass = constructor.getParameterTypes();
        if (arrayOfClass.length != 1 || !arrayOfClass[0].isAssignableFrom(clazz2)) {
          b++;
          continue;
        } 
        return constructor.newInstance(new Object[] { param1Object });
      } 
      throw new NoSuchAlgorithmException("No public constructor matching " + clazz2.getName() + " found in class " + this.className);
    }
    
    public boolean supportsParameter(Object param1Object) {
      Provider.EngineDescription engineDescription = (Provider.EngineDescription)knownEngines.get(this.type);
      if (engineDescription == null)
        return true; 
      if (!engineDescription.supportsParameter)
        throw new InvalidParameterException("supportsParameter() not used with " + this.type + " engines"); 
      if (param1Object != null && !(param1Object instanceof Key))
        throw new InvalidParameterException("Parameter must be instanceof Key for engine " + this.type); 
      if (!hasKeyAttributes())
        return true; 
      if (param1Object == null)
        return false; 
      Key key = (Key)param1Object;
      return supportsKeyFormat(key) ? true : (supportsKeyClass(key));
    }
    
    private boolean hasKeyAttributes() {
      Boolean bool = this.hasKeyAttributes;
      if (bool == null)
        synchronized (this) {
          String str = getAttribute("SupportedKeyFormats");
          if (str != null)
            this.supportedFormats = str.split("\\|"); 
          str = getAttribute("SupportedKeyClasses");
          if (str != null) {
            String[] arrayOfString = str.split("\\|");
            ArrayList arrayList = new ArrayList(arrayOfString.length);
            for (String str1 : arrayOfString) {
              Class clazz = getKeyClass(str1);
              if (clazz != null)
                arrayList.add(clazz); 
            } 
            this.supportedClasses = (Class[])arrayList.toArray(CLASS0);
          } 
          boolean bool1 = (this.supportedFormats != null || this.supportedClasses != null);
          bool = Boolean.valueOf(bool1);
          this.hasKeyAttributes = bool;
        }  
      return bool.booleanValue();
    }
    
    private Class<?> getKeyClass(String param1String) {
      try {
        return Class.forName(param1String);
      } catch (ClassNotFoundException classNotFoundException) {
        try {
          ClassLoader classLoader = this.provider.getClass().getClassLoader();
          if (classLoader != null)
            return classLoader.loadClass(param1String); 
        } catch (ClassNotFoundException classNotFoundException) {}
        return null;
      } 
    }
    
    private boolean supportsKeyFormat(Key param1Key) {
      if (this.supportedFormats == null)
        return false; 
      String str = param1Key.getFormat();
      if (str == null)
        return false; 
      for (String str1 : this.supportedFormats) {
        if (str1.equals(str))
          return true; 
      } 
      return false;
    }
    
    private boolean supportsKeyClass(Key param1Key) {
      if (this.supportedClasses == null)
        return false; 
      Class clazz = param1Key.getClass();
      for (Class clazz1 : this.supportedClasses) {
        if (clazz1.isAssignableFrom(clazz))
          return true; 
      } 
      return false;
    }
    
    public String toString() {
      String str1 = this.aliases.isEmpty() ? "" : ("\r\n  aliases: " + this.aliases.toString());
      String str2 = this.attributes.isEmpty() ? "" : ("\r\n  attributes: " + this.attributes.toString());
      return this.provider.getName() + ": " + this.type + "." + this.algorithm + " -> " + this.className + str1 + str2 + "\r\n";
    }
  }
  
  private static class ServiceKey {
    private final String type;
    
    private final String algorithm;
    
    private final String originalAlgorithm;
    
    private ServiceKey(String param1String1, String param1String2, boolean param1Boolean) {
      this.type = param1String1;
      this.originalAlgorithm = param1String2;
      param1String2 = param1String2.toUpperCase(Locale.ENGLISH);
      this.algorithm = param1Boolean ? param1String2.intern() : param1String2;
    }
    
    public int hashCode() { return this.type.hashCode() + this.algorithm.hashCode(); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof ServiceKey))
        return false; 
      ServiceKey serviceKey = (ServiceKey)param1Object;
      return (this.type.equals(serviceKey.type) && this.algorithm.equals(serviceKey.algorithm));
    }
    
    boolean matches(String param1String1, String param1String2) { return (this.type == param1String1 && this.originalAlgorithm == param1String2); }
  }
  
  private static class UString {
    final String string;
    
    final String lowerString;
    
    UString(String param1String) {
      this.string = param1String;
      this.lowerString = param1String.toLowerCase(Locale.ENGLISH);
    }
    
    public int hashCode() { return this.lowerString.hashCode(); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof UString))
        return false; 
      UString uString = (UString)param1Object;
      return this.lowerString.equals(uString.lowerString);
    }
    
    public String toString() { return this.string; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Provider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */