package sun.security.jca;

import java.security.Provider;

public class Providers {
  private static final ThreadLocal<ProviderList> threadLists = new InheritableThreadLocal();
  
  private static final String BACKUP_PROVIDER_CLASSNAME = "sun.security.provider.VerificationProvider";
  
  private static final String[] jarVerificationProviders = { "sun.security.provider.Sun", "sun.security.rsa.SunRsaSign", "sun.security.ec.SunEC", "sun.security.provider.VerificationProvider" };
  
  public static Provider getSunProvider() {
    try {
      Class clazz = Class.forName(jarVerificationProviders[0]);
      return (Provider)clazz.newInstance();
    } catch (Exception exception) {
      try {
        Class clazz = Class.forName("sun.security.provider.VerificationProvider");
        return (Provider)clazz.newInstance();
      } catch (Exception exception1) {
        throw new RuntimeException("Sun provider not found", exception);
      } 
    } 
  }
  
  public static Object startJarVerification() {
    ProviderList providerList1 = getProviderList();
    ProviderList providerList2 = providerList1.getJarList(jarVerificationProviders);
    return beginThreadProviderList(providerList2);
  }
  
  public static void stopJarVerification(Object paramObject) { endThreadProviderList((ProviderList)paramObject); }
  
  public static ProviderList getProviderList() {
    ProviderList providerList1 = getThreadProviderList();
    if (providerList1 == null)
      providerList1 = getSystemProviderList(); 
    return providerList1;
  }
  
  public static void setProviderList(ProviderList paramProviderList) {
    if (getThreadProviderList() == null) {
      setSystemProviderList(paramProviderList);
    } else {
      changeThreadProviderList(paramProviderList);
    } 
  }
  
  public static ProviderList getFullProviderList() {
    synchronized (Providers.class) {
      ProviderList providerList3 = getThreadProviderList();
      if (providerList3 != null) {
        ProviderList providerList4 = providerList3.removeInvalid();
        if (providerList4 != providerList3) {
          changeThreadProviderList(providerList4);
          providerList3 = providerList4;
        } 
        return providerList3;
      } 
    } 
    ProviderList providerList1 = getSystemProviderList();
    ProviderList providerList2 = providerList1.removeInvalid();
    if (providerList2 != providerList1) {
      setSystemProviderList(providerList2);
      providerList1 = providerList2;
    } 
    return providerList1;
  }
  
  private static ProviderList getSystemProviderList() { return providerList; }
  
  private static void setSystemProviderList(ProviderList paramProviderList) { providerList = paramProviderList; }
  
  public static ProviderList getThreadProviderList() { return (threadListsUsed == 0) ? null : (ProviderList)threadLists.get(); }
  
  private static void changeThreadProviderList(ProviderList paramProviderList) { threadLists.set(paramProviderList); }
  
  public static ProviderList beginThreadProviderList(ProviderList paramProviderList) {
    if (ProviderList.debug != null)
      ProviderList.debug.println("ThreadLocal providers: " + paramProviderList); 
    ProviderList providerList1 = (ProviderList)threadLists.get();
    threadListsUsed++;
    threadLists.set(paramProviderList);
    return providerList1;
  }
  
  public static void endThreadProviderList(ProviderList paramProviderList) {
    if (paramProviderList == null) {
      if (ProviderList.debug != null)
        ProviderList.debug.println("Disabling ThreadLocal providers"); 
      threadLists.remove();
    } else {
      if (ProviderList.debug != null)
        ProviderList.debug.println("Restoring previous ThreadLocal providers: " + paramProviderList); 
      threadLists.set(paramProviderList);
    } 
    threadListsUsed--;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jca\Providers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */