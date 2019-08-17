package sun.security.jca;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import sun.security.util.Debug;

public final class ProviderList {
  static final Debug debug = Debug.getInstance("jca", "ProviderList");
  
  private static final ProviderConfig[] PC0 = new ProviderConfig[0];
  
  private static final Provider[] P0 = new Provider[0];
  
  static final ProviderList EMPTY = new ProviderList(PC0, true);
  
  private static final Provider EMPTY_PROVIDER = new Provider("##Empty##", 1.0D, "initialization in progress") {
      private static final long serialVersionUID = 1151354171352296389L;
      
      public Service getService(String param1String1, String param1String2) { return null; }
    };
  
  private final ProviderConfig[] configs;
  
  private final List<Provider> userList = new AbstractList<Provider>() {
      public int size() { return ProviderList.this.configs.length; }
      
      public Provider get(int param1Int) { return ProviderList.this.getProvider(param1Int); }
    };
  
  static ProviderList fromSecurityProperties() { return (ProviderList)AccessController.doPrivileged(new PrivilegedAction<ProviderList>() {
          public ProviderList run() { return new ProviderList(null); }
        }); }
  
  public static ProviderList add(ProviderList paramProviderList, Provider paramProvider) { return insertAt(paramProviderList, paramProvider, -1); }
  
  public static ProviderList insertAt(ProviderList paramProviderList, Provider paramProvider, int paramInt) {
    if (paramProviderList.getProvider(paramProvider.getName()) != null)
      return paramProviderList; 
    ArrayList arrayList = new ArrayList(Arrays.asList(paramProviderList.configs));
    int i = arrayList.size();
    if (paramInt < 0 || paramInt > i)
      paramInt = i; 
    arrayList.add(paramInt, new ProviderConfig(paramProvider));
    return new ProviderList((ProviderConfig[])arrayList.toArray(PC0), true);
  }
  
  public static ProviderList remove(ProviderList paramProviderList, String paramString) {
    if (paramProviderList.getProvider(paramString) == null)
      return paramProviderList; 
    ProviderConfig[] arrayOfProviderConfig = new ProviderConfig[paramProviderList.size() - 1];
    byte b = 0;
    for (ProviderConfig providerConfig : paramProviderList.configs) {
      if (!providerConfig.getProvider().getName().equals(paramString))
        arrayOfProviderConfig[b++] = providerConfig; 
    } 
    return new ProviderList(arrayOfProviderConfig, true);
  }
  
  public static ProviderList newList(Provider... paramVarArgs) {
    ProviderConfig[] arrayOfProviderConfig = new ProviderConfig[paramVarArgs.length];
    for (byte b = 0; b < paramVarArgs.length; b++)
      arrayOfProviderConfig[b] = new ProviderConfig(paramVarArgs[b]); 
    return new ProviderList(arrayOfProviderConfig, true);
  }
  
  private ProviderList(ProviderConfig[] paramArrayOfProviderConfig, boolean paramBoolean) {
    this.configs = paramArrayOfProviderConfig;
    this.allLoaded = paramBoolean;
  }
  
  private ProviderList() {
    ArrayList arrayList = new ArrayList();
    for (byte b = 1;; b++) {
      ProviderConfig providerConfig;
      String str = Security.getProperty("security.provider." + b);
      if (str == null)
        break; 
      str = str.trim();
      if (str.length() == 0) {
        System.err.println("invalid entry for security.provider." + b);
        break;
      } 
      int i = str.indexOf(' ');
      if (i == -1) {
        providerConfig = new ProviderConfig(str);
      } else {
        String str1 = str.substring(0, i);
        String str2 = str.substring(i + 1).trim();
        providerConfig = new ProviderConfig(str1, str2);
      } 
      if (!arrayList.contains(providerConfig))
        arrayList.add(providerConfig); 
    } 
    this.configs = (ProviderConfig[])arrayList.toArray(PC0);
    if (debug != null)
      debug.println("provider configuration: " + arrayList); 
  }
  
  ProviderList getJarList(String[] paramArrayOfString) {
    ArrayList arrayList = new ArrayList();
    for (String str : paramArrayOfString) {
      ProviderConfig providerConfig = new ProviderConfig(str);
      for (ProviderConfig providerConfig1 : this.configs) {
        if (providerConfig1.equals(providerConfig)) {
          providerConfig = providerConfig1;
          break;
        } 
      } 
      arrayList.add(providerConfig);
    } 
    ProviderConfig[] arrayOfProviderConfig = (ProviderConfig[])arrayList.toArray(PC0);
    return new ProviderList(arrayOfProviderConfig, false);
  }
  
  public int size() { return this.configs.length; }
  
  Provider getProvider(int paramInt) {
    Provider provider = this.configs[paramInt].getProvider();
    return (provider != null) ? provider : EMPTY_PROVIDER;
  }
  
  public List<Provider> providers() { return this.userList; }
  
  private ProviderConfig getProviderConfig(String paramString) {
    int i = getIndex(paramString);
    return (i != -1) ? this.configs[i] : null;
  }
  
  public Provider getProvider(String paramString) {
    ProviderConfig providerConfig = getProviderConfig(paramString);
    return (providerConfig == null) ? null : providerConfig.getProvider();
  }
  
  public int getIndex(String paramString) {
    for (byte b = 0; b < this.configs.length; b++) {
      Provider provider = getProvider(b);
      if (provider.getName().equals(paramString))
        return b; 
    } 
    return -1;
  }
  
  private int loadAll() {
    if (this.allLoaded)
      return this.configs.length; 
    if (debug != null) {
      debug.println("Loading all providers");
      (new Exception("Call trace")).printStackTrace();
    } 
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.configs.length; b2++) {
      Provider provider = this.configs[b2].getProvider();
      if (provider != null)
        b1++; 
    } 
    if (b1 == this.configs.length)
      this.allLoaded = true; 
    return b1;
  }
  
  ProviderList removeInvalid() {
    int i = loadAll();
    if (i == this.configs.length)
      return this; 
    ProviderConfig[] arrayOfProviderConfig = new ProviderConfig[i];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < this.configs.length) {
      ProviderConfig providerConfig = this.configs[b1];
      if (providerConfig.isLoaded())
        arrayOfProviderConfig[b2++] = providerConfig; 
      b1++;
    } 
    return new ProviderList(arrayOfProviderConfig, true);
  }
  
  public Provider[] toArray() { return (Provider[])providers().toArray(P0); }
  
  public String toString() { return Arrays.asList(this.configs).toString(); }
  
  public Provider.Service getService(String paramString1, String paramString2) {
    for (byte b = 0; b < this.configs.length; b++) {
      Provider provider = getProvider(b);
      Provider.Service service = provider.getService(paramString1, paramString2);
      if (service != null)
        return service; 
    } 
    return null;
  }
  
  public List<Provider.Service> getServices(String paramString1, String paramString2) { return new ServiceList(paramString1, paramString2); }
  
  @Deprecated
  public List<Provider.Service> getServices(String paramString, List<String> paramList) {
    ArrayList arrayList = new ArrayList();
    for (String str : paramList)
      arrayList.add(new ServiceId(paramString, str)); 
    return getServices(arrayList);
  }
  
  public List<Provider.Service> getServices(List<ServiceId> paramList) { return new ServiceList(paramList); }
  
  private final class ServiceList extends AbstractList<Provider.Service> {
    private final String type;
    
    private final String algorithm;
    
    private final List<ServiceId> ids;
    
    private Provider.Service firstService;
    
    private List<Provider.Service> services;
    
    private int providerIndex;
    
    ServiceList(String param1String1, String param1String2) {
      this.type = param1String1;
      this.algorithm = param1String2;
      this.ids = null;
    }
    
    ServiceList(List<ServiceId> param1List) {
      this.type = null;
      this.algorithm = null;
      this.ids = param1List;
    }
    
    private void addService(Provider.Service param1Service) {
      if (this.firstService == null) {
        this.firstService = param1Service;
      } else {
        if (this.services == null) {
          this.services = new ArrayList(4);
          this.services.add(this.firstService);
        } 
        this.services.add(param1Service);
      } 
    }
    
    private Provider.Service tryGet(int param1Int) {
      while (true) {
        if (param1Int == 0 && this.firstService != null)
          return this.firstService; 
        if (this.services != null && this.services.size() > param1Int)
          return (Provider.Service)this.services.get(param1Int); 
        if (this.providerIndex >= ProviderList.this.configs.length)
          return null; 
        Provider provider = ProviderList.this.getProvider(this.providerIndex++);
        if (this.type != null) {
          Provider.Service service = provider.getService(this.type, this.algorithm);
          if (service != null)
            addService(service); 
          continue;
        } 
        for (ServiceId serviceId : this.ids) {
          Provider.Service service = provider.getService(serviceId.type, serviceId.algorithm);
          if (service != null)
            addService(service); 
        } 
      } 
    }
    
    public Provider.Service get(int param1Int) {
      Provider.Service service = tryGet(param1Int);
      if (service == null)
        throw new IndexOutOfBoundsException(); 
      return service;
    }
    
    public int size() {
      byte b;
      if (this.services != null) {
        b = this.services.size();
      } else {
        b = (this.firstService != null) ? 1 : 0;
      } 
      while (tryGet(b) != null)
        b++; 
      return b;
    }
    
    public boolean isEmpty() { return (tryGet(false) == null); }
    
    public Iterator<Provider.Service> iterator() { return new Iterator<Provider.Service>() {
          int index;
          
          public boolean hasNext() { return (ProviderList.ServiceList.this.tryGet(this.index) != null); }
          
          public Provider.Service next() {
            Provider.Service service = ProviderList.ServiceList.this.tryGet(this.index);
            if (service == null)
              throw new NoSuchElementException(); 
            this.index++;
            return service;
          }
          
          public void remove() { throw new UnsupportedOperationException(); }
        }; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jca\ProviderList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */