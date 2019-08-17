package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.RuntimeOperationsException;

public class Repository {
  private final Map<String, Map<String, NamedObject>> domainTb;
  
  private final String domain;
  
  private final ReentrantReadWriteLock lock;
  
  private void addAllMatching(Map<String, NamedObject> paramMap, Set<NamedObject> paramSet, ObjectNamePattern paramObjectNamePattern) {
    synchronized (paramMap) {
      for (NamedObject namedObject : paramMap.values()) {
        ObjectName objectName = namedObject.getName();
        if (paramObjectNamePattern.matchKeys(objectName))
          paramSet.add(namedObject); 
      } 
    } 
  }
  
  private void addNewDomMoi(DynamicMBean paramDynamicMBean, String paramString, ObjectName paramObjectName, RegistrationContext paramRegistrationContext) {
    HashMap hashMap = new HashMap();
    String str = paramObjectName.getCanonicalKeyPropertyListString();
    addMoiToTb(paramDynamicMBean, paramObjectName, str, hashMap, paramRegistrationContext);
    this.domainTb.put(paramString, hashMap);
    this.nbElements++;
  }
  
  private void registering(RegistrationContext paramRegistrationContext) {
    if (paramRegistrationContext == null)
      return; 
    try {
      paramRegistrationContext.registering();
    } catch (RuntimeOperationsException runtimeOperationsException) {
      throw runtimeOperationsException;
    } catch (RuntimeException runtimeException) {
      throw new RuntimeOperationsException(runtimeException);
    } 
  }
  
  private void unregistering(RegistrationContext paramRegistrationContext, ObjectName paramObjectName) {
    if (paramRegistrationContext == null)
      return; 
    try {
      paramRegistrationContext.unregistered();
    } catch (Exception exception) {
      JmxProperties.MBEANSERVER_LOGGER.log(Level.FINE, "Unexpected exception while unregistering " + paramObjectName, exception);
    } 
  }
  
  private void addMoiToTb(DynamicMBean paramDynamicMBean, ObjectName paramObjectName, String paramString, Map<String, NamedObject> paramMap, RegistrationContext paramRegistrationContext) {
    registering(paramRegistrationContext);
    paramMap.put(paramString, new NamedObject(paramObjectName, paramDynamicMBean));
  }
  
  private NamedObject retrieveNamedObject(ObjectName paramObjectName) {
    if (paramObjectName.isPattern())
      return null; 
    String str = paramObjectName.getDomain().intern();
    if (str.length() == 0)
      str = this.domain; 
    Map map = (Map)this.domainTb.get(str);
    return (map == null) ? null : (NamedObject)map.get(paramObjectName.getCanonicalKeyPropertyListString());
  }
  
  public Repository(String paramString) { this(paramString, true); }
  
  public Repository(String paramString, boolean paramBoolean) {
    this.lock = new ReentrantReadWriteLock(paramBoolean);
    this.domainTb = new HashMap(5);
    if (paramString != null && paramString.length() != 0) {
      this.domain = paramString.intern();
    } else {
      this.domain = "DefaultDomain";
    } 
    this.domainTb.put(this.domain, new HashMap());
  }
  
  public String[] getDomains() {
    ArrayList arrayList;
    this.lock.readLock().lock();
    try {
      arrayList = new ArrayList(this.domainTb.size());
      for (Map.Entry entry : this.domainTb.entrySet()) {
        Map map = (Map)entry.getValue();
        if (map != null && map.size() != 0)
          arrayList.add(entry.getKey()); 
      } 
    } finally {
      this.lock.readLock().unlock();
    } 
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  
  public void addMBean(DynamicMBean paramDynamicMBean, ObjectName paramObjectName, RegistrationContext paramRegistrationContext) throws InstanceAlreadyExistsException {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "addMBean", "name = " + paramObjectName); 
    String str = paramObjectName.getDomain().intern();
    boolean bool = false;
    if (str.length() == 0)
      paramObjectName = Util.newObjectName(this.domain + paramObjectName.toString()); 
    if (str == this.domain) {
      bool = true;
      str = this.domain;
    } else {
      bool = false;
    } 
    if (paramObjectName.isPattern())
      throw new RuntimeOperationsException(new IllegalArgumentException("Repository: cannot add mbean for pattern name " + paramObjectName.toString())); 
    this.lock.writeLock().lock();
    try {
      if (!bool && str.equals("JMImplementation") && this.domainTb.containsKey("JMImplementation"))
        throw new RuntimeOperationsException(new IllegalArgumentException("Repository: domain name cannot be JMImplementation")); 
      Map map = (Map)this.domainTb.get(str);
      if (map == null) {
        addNewDomMoi(paramDynamicMBean, str, paramObjectName, paramRegistrationContext);
        return;
      } 
      String str1 = paramObjectName.getCanonicalKeyPropertyListString();
      NamedObject namedObject = (NamedObject)map.get(str1);
      if (namedObject != null)
        throw new InstanceAlreadyExistsException(paramObjectName.toString()); 
      this.nbElements++;
      addMoiToTb(paramDynamicMBean, paramObjectName, str1, map, paramRegistrationContext);
    } finally {
      this.lock.writeLock().unlock();
    } 
  }
  
  public boolean contains(ObjectName paramObjectName) {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "contains", " name = " + paramObjectName); 
    this.lock.readLock().lock();
    try {
      return (retrieveNamedObject(paramObjectName) != null);
    } finally {
      this.lock.readLock().unlock();
    } 
  }
  
  public DynamicMBean retrieve(ObjectName paramObjectName) {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "retrieve", "name = " + paramObjectName); 
    this.lock.readLock().lock();
    try {
      NamedObject namedObject = retrieveNamedObject(paramObjectName);
      if (namedObject == null)
        return null; 
      return namedObject.getObject();
    } finally {
      this.lock.readLock().unlock();
    } 
  }
  
  public Set<NamedObject> query(ObjectName paramObjectName, QueryExp paramQueryExp) {
    ObjectName objectName;
    HashSet hashSet = new HashSet();
    if (paramObjectName == null || paramObjectName.getCanonicalName().length() == 0 || paramObjectName.equals(ObjectName.WILDCARD)) {
      objectName = ObjectName.WILDCARD;
    } else {
      objectName = paramObjectName;
    } 
    this.lock.readLock().lock();
    try {
      if (!objectName.isPattern()) {
        NamedObject namedObject = retrieveNamedObject(objectName);
        if (namedObject != null)
          hashSet.add(namedObject); 
        return hashSet;
      } 
      if (objectName == ObjectName.WILDCARD) {
        for (Map map : this.domainTb.values())
          hashSet.addAll(map.values()); 
        return hashSet;
      } 
      String str1 = objectName.getCanonicalKeyPropertyListString();
      boolean bool = (str1.length() == 0) ? 1 : 0;
      ObjectNamePattern objectNamePattern = bool ? null : new ObjectNamePattern(objectName);
      if (objectName.getDomain().length() == 0) {
        Map map = (Map)this.domainTb.get(this.domain);
        if (bool) {
          hashSet.addAll(map.values());
        } else {
          addAllMatching(map, hashSet, objectNamePattern);
        } 
        return hashSet;
      } 
      if (!objectName.isDomainPattern()) {
        Map map = (Map)this.domainTb.get(objectName.getDomain());
        if (map == null)
          return Collections.emptySet(); 
        if (bool) {
          hashSet.addAll(map.values());
        } else {
          addAllMatching(map, hashSet, objectNamePattern);
        } 
        return hashSet;
      } 
      String str2 = objectName.getDomain();
      for (String str : this.domainTb.keySet()) {
        if (Util.wildmatch(str, str2)) {
          Map map = (Map)this.domainTb.get(str);
          if (bool) {
            hashSet.addAll(map.values());
            continue;
          } 
          addAllMatching(map, hashSet, objectNamePattern);
        } 
      } 
      return hashSet;
    } finally {
      this.lock.readLock().unlock();
    } 
  }
  
  public void remove(ObjectName paramObjectName, RegistrationContext paramRegistrationContext) throws InstanceNotFoundException {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, Repository.class.getName(), "remove", "name = " + paramObjectName); 
    String str = paramObjectName.getDomain().intern();
    if (str.length() == 0)
      str = this.domain; 
    this.lock.writeLock().lock();
    try {
      Map map = (Map)this.domainTb.get(str);
      if (map == null)
        throw new InstanceNotFoundException(paramObjectName.toString()); 
      if (map.remove(paramObjectName.getCanonicalKeyPropertyListString()) == null)
        throw new InstanceNotFoundException(paramObjectName.toString()); 
      this.nbElements--;
      if (map.isEmpty()) {
        this.domainTb.remove(str);
        if (str == this.domain)
          this.domainTb.put(this.domain, new HashMap()); 
      } 
      unregistering(paramRegistrationContext, paramObjectName);
    } finally {
      this.lock.writeLock().unlock();
    } 
  }
  
  public Integer getCount() { return Integer.valueOf(this.nbElements); }
  
  public String getDefaultDomain() { return this.domain; }
  
  private static final class ObjectNamePattern {
    private final String[] keys;
    
    private final String[] values;
    
    private final String properties;
    
    private final boolean isPropertyListPattern;
    
    private final boolean isPropertyValuePattern;
    
    public final ObjectName pattern;
    
    public ObjectNamePattern(ObjectName param1ObjectName) { this(param1ObjectName.isPropertyListPattern(), param1ObjectName.isPropertyValuePattern(), param1ObjectName.getCanonicalKeyPropertyListString(), param1ObjectName.getKeyPropertyList(), param1ObjectName); }
    
    ObjectNamePattern(boolean param1Boolean1, boolean param1Boolean2, String param1String, Map<String, String> param1Map, ObjectName param1ObjectName) {
      this.isPropertyListPattern = param1Boolean1;
      this.isPropertyValuePattern = param1Boolean2;
      this.properties = param1String;
      int i = param1Map.size();
      this.keys = new String[i];
      this.values = new String[i];
      byte b = 0;
      for (Map.Entry entry : param1Map.entrySet()) {
        this.keys[b] = (String)entry.getKey();
        this.values[b] = (String)entry.getValue();
        b++;
      } 
      this.pattern = param1ObjectName;
    }
    
    public boolean matchKeys(ObjectName param1ObjectName) {
      if (this.isPropertyValuePattern && !this.isPropertyListPattern && param1ObjectName.getKeyPropertyList().size() != this.keys.length)
        return false; 
      if (this.isPropertyValuePattern || this.isPropertyListPattern) {
        for (int i = this.keys.length - 1; i >= 0; i--) {
          String str = param1ObjectName.getKeyProperty(this.keys[i]);
          if (str == null)
            return false; 
          if (this.isPropertyValuePattern && this.pattern.isPropertyValuePattern(this.keys[i])) {
            if (!Util.wildmatch(str, this.values[i]))
              return false; 
          } else if (!str.equals(this.values[i])) {
            return false;
          } 
        } 
        return true;
      } 
      String str1 = param1ObjectName.getCanonicalKeyPropertyListString();
      String str2 = this.properties;
      return str1.equals(str2);
    }
  }
  
  public static interface RegistrationContext {
    void registering();
    
    void unregistered();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\Repository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */