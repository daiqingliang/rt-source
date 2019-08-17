package sun.security.jgss;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.action.GetPropertyAction;
import sun.security.jgss.spi.MechanismFactory;
import sun.security.jgss.wrapper.NativeGSSFactory;
import sun.security.jgss.wrapper.SunNativeProvider;

public final class ProviderList {
  private static final String PROV_PROP_PREFIX = "GssApiMechanism.";
  
  private static final int PROV_PROP_PREFIX_LEN = "GssApiMechanism.".length();
  
  private static final String SPI_MECH_FACTORY_TYPE = "sun.security.jgss.spi.MechanismFactory";
  
  private static final String DEFAULT_MECH_PROP = "sun.security.jgss.mechanism";
  
  public static final Oid DEFAULT_MECH_OID;
  
  private ArrayList<PreferencesEntry> preferences = new ArrayList(5);
  
  private HashMap<PreferencesEntry, MechanismFactory> factories = new HashMap(5);
  
  private HashSet<Oid> mechs = new HashSet(5);
  
  private final GSSCaller caller;
  
  public ProviderList(GSSCaller paramGSSCaller, boolean paramBoolean) {
    this.caller = paramGSSCaller;
    if (paramBoolean) {
      arrayOfProvider = new Provider[1];
      arrayOfProvider[0] = new SunNativeProvider();
    } else {
      arrayOfProvider = Security.getProviders();
    } 
    for (byte b = 0; b < arrayOfProvider.length; b++) {
      Provider provider = arrayOfProvider[b];
      try {
        addProviderAtEnd(provider, null);
      } catch (GSSException gSSException) {
        GSSUtil.debug("Error in adding provider " + provider.getName() + ": " + gSSException);
      } 
    } 
  }
  
  private boolean isMechFactoryProperty(String paramString) { return (paramString.startsWith("GssApiMechanism.") || paramString.regionMatches(true, 0, "GssApiMechanism.", 0, PROV_PROP_PREFIX_LEN)); }
  
  private Oid getOidFromMechFactoryProperty(String paramString) throws GSSException {
    String str = paramString.substring(PROV_PROP_PREFIX_LEN);
    return new Oid(str);
  }
  
  public MechanismFactory getMechFactory(Oid paramOid) throws GSSException {
    if (paramOid == null)
      paramOid = DEFAULT_MECH_OID; 
    return getMechFactory(paramOid, null);
  }
  
  public MechanismFactory getMechFactory(Oid paramOid, Provider paramProvider) throws GSSException {
    if (paramOid == null)
      paramOid = DEFAULT_MECH_OID; 
    if (paramProvider == null) {
      for (PreferencesEntry preferencesEntry1 : this.preferences) {
        if (preferencesEntry1.impliesMechanism(paramOid)) {
          MechanismFactory mechanismFactory = getMechFactory(preferencesEntry1, paramOid);
          if (mechanismFactory != null)
            return mechanismFactory; 
        } 
      } 
      throw new GSSExceptionImpl(2, paramOid);
    } 
    PreferencesEntry preferencesEntry = new PreferencesEntry(paramProvider, paramOid);
    return getMechFactory(preferencesEntry, paramOid);
  }
  
  private MechanismFactory getMechFactory(PreferencesEntry paramPreferencesEntry, Oid paramOid) throws GSSException {
    Provider provider = paramPreferencesEntry.getProvider();
    PreferencesEntry preferencesEntry = new PreferencesEntry(provider, paramOid);
    MechanismFactory mechanismFactory = (MechanismFactory)this.factories.get(preferencesEntry);
    if (mechanismFactory == null) {
      String str1 = "GssApiMechanism." + paramOid.toString();
      String str2 = provider.getProperty(str1);
      if (str2 != null) {
        mechanismFactory = getMechFactoryImpl(provider, str2, paramOid, this.caller);
        this.factories.put(preferencesEntry, mechanismFactory);
      } else if (paramPreferencesEntry.getOid() != null) {
        throw new GSSExceptionImpl(2, "Provider " + provider.getName() + " does not support mechanism " + paramOid);
      } 
    } 
    return mechanismFactory;
  }
  
  private static MechanismFactory getMechFactoryImpl(Provider paramProvider, String paramString, Oid paramOid, GSSCaller paramGSSCaller) throws GSSException {
    try {
      Class clazz2;
      Class clazz1 = Class.forName("sun.security.jgss.spi.MechanismFactory");
      ClassLoader classLoader = paramProvider.getClass().getClassLoader();
      if (classLoader != null) {
        clazz2 = classLoader.loadClass(paramString);
      } else {
        clazz2 = Class.forName(paramString);
      } 
      if (clazz1.isAssignableFrom(clazz2)) {
        Constructor constructor = clazz2.getConstructor(new Class[] { GSSCaller.class });
        MechanismFactory mechanismFactory = (MechanismFactory)constructor.newInstance(new Object[] { paramGSSCaller });
        if (mechanismFactory instanceof NativeGSSFactory)
          ((NativeGSSFactory)mechanismFactory).setMech(paramOid); 
        return mechanismFactory;
      } 
      throw createGSSException(paramProvider, paramString, "is not a sun.security.jgss.spi.MechanismFactory", null);
    } catch (ClassNotFoundException classNotFoundException) {
      throw createGSSException(paramProvider, paramString, "cannot be created", classNotFoundException);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw createGSSException(paramProvider, paramString, "cannot be created", noSuchMethodException);
    } catch (InvocationTargetException invocationTargetException) {
      throw createGSSException(paramProvider, paramString, "cannot be created", invocationTargetException);
    } catch (InstantiationException instantiationException) {
      throw createGSSException(paramProvider, paramString, "cannot be created", instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw createGSSException(paramProvider, paramString, "cannot be created", illegalAccessException);
    } catch (SecurityException securityException) {
      throw createGSSException(paramProvider, paramString, "cannot be created", securityException);
    } 
  }
  
  private static GSSException createGSSException(Provider paramProvider, String paramString1, String paramString2, Exception paramException) {
    String str = paramString1 + " configured by " + paramProvider.getName() + " for GSS-API Mechanism Factory ";
    return new GSSExceptionImpl(2, str + paramString2, paramException);
  }
  
  public Oid[] getMechs() { return (Oid[])this.mechs.toArray(new Oid[0]); }
  
  public void addProviderAtFront(Provider paramProvider, Oid paramOid) throws GSSException {
    boolean bool;
    PreferencesEntry preferencesEntry = new PreferencesEntry(paramProvider, paramOid);
    Iterator iterator = this.preferences.iterator();
    while (iterator.hasNext()) {
      PreferencesEntry preferencesEntry1 = (PreferencesEntry)iterator.next();
      if (preferencesEntry.implies(preferencesEntry1))
        iterator.remove(); 
    } 
    if (paramOid == null) {
      bool = addAllMechsFromProvider(paramProvider);
    } else {
      String str = paramOid.toString();
      if (paramProvider.getProperty("GssApiMechanism." + str) == null)
        throw new GSSExceptionImpl(2, "Provider " + paramProvider.getName() + " does not support " + str); 
      this.mechs.add(paramOid);
      bool = true;
    } 
    if (bool)
      this.preferences.add(0, preferencesEntry); 
  }
  
  public void addProviderAtEnd(Provider paramProvider, Oid paramOid) throws GSSException {
    boolean bool;
    PreferencesEntry preferencesEntry = new PreferencesEntry(paramProvider, paramOid);
    for (PreferencesEntry preferencesEntry1 : this.preferences) {
      if (preferencesEntry1.implies(preferencesEntry))
        return; 
    } 
    if (paramOid == null) {
      bool = addAllMechsFromProvider(paramProvider);
    } else {
      String str = paramOid.toString();
      if (paramProvider.getProperty("GssApiMechanism." + str) == null)
        throw new GSSExceptionImpl(2, "Provider " + paramProvider.getName() + " does not support " + str); 
      this.mechs.add(paramOid);
      bool = true;
    } 
    if (bool)
      this.preferences.add(preferencesEntry); 
  }
  
  private boolean addAllMechsFromProvider(Provider paramProvider) {
    boolean bool = false;
    Enumeration enumeration = paramProvider.keys();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      if (isMechFactoryProperty(str))
        try {
          Oid oid = getOidFromMechFactoryProperty(str);
          this.mechs.add(oid);
          bool = true;
        } catch (GSSException gSSException) {
          GSSUtil.debug("Ignore the invalid property " + str + " from provider " + paramProvider.getName());
        }  
    } 
    return bool;
  }
  
  static  {
    Oid oid = null;
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.jgss.mechanism"));
    if (str != null)
      oid = GSSUtil.createOid(str); 
    DEFAULT_MECH_OID = (oid == null) ? GSSUtil.GSS_KRB5_MECH_OID : oid;
  }
  
  private static final class PreferencesEntry {
    private Provider p;
    
    private Oid oid;
    
    PreferencesEntry(Provider param1Provider, Oid param1Oid) throws GSSException {
      this.p = param1Provider;
      this.oid = param1Oid;
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof PreferencesEntry))
        return false; 
      PreferencesEntry preferencesEntry = (PreferencesEntry)param1Object;
      return this.p.getName().equals(preferencesEntry.p.getName()) ? ((this.oid != null && preferencesEntry.oid != null) ? this.oid.equals(preferencesEntry.oid) : ((this.oid == null && preferencesEntry.oid == null) ? 1 : 0)) : 0;
    }
    
    public int hashCode() {
      int i = 17;
      i = 37 * i + this.p.getName().hashCode();
      if (this.oid != null)
        i = 37 * i + this.oid.hashCode(); 
      return i;
    }
    
    boolean implies(Object param1Object) {
      if (param1Object instanceof PreferencesEntry) {
        PreferencesEntry preferencesEntry = (PreferencesEntry)param1Object;
        return (equals(preferencesEntry) || (this.p.getName().equals(preferencesEntry.p.getName()) && this.oid == null));
      } 
      return false;
    }
    
    Provider getProvider() { return this.p; }
    
    Oid getOid() { return this.oid; }
    
    boolean impliesMechanism(Oid param1Oid) { return (this.oid == null || this.oid.equals(param1Oid)); }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer("<");
      stringBuffer.append(this.p.getName());
      stringBuffer.append(", ");
      stringBuffer.append(this.oid);
      stringBuffer.append(">");
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\ProviderList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */