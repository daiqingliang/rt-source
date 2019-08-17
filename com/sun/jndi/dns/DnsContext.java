package com.sun.jndi.dns;

import com.sun.jndi.toolkit.ctx.ComponentDirContext;
import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public class DnsContext extends ComponentDirContext {
  DnsName domain;
  
  Hashtable<Object, Object> environment;
  
  private boolean envShared;
  
  private boolean parentIsDns;
  
  private String[] servers;
  
  private Resolver resolver;
  
  private boolean authoritative;
  
  private boolean recursion;
  
  private int timeout;
  
  private int retries;
  
  static final NameParser nameParser = new DnsNameParser();
  
  private static final int DEFAULT_INIT_TIMEOUT = 1000;
  
  private static final int DEFAULT_RETRIES = 4;
  
  private static final String INIT_TIMEOUT = "com.sun.jndi.dns.timeout.initial";
  
  private static final String RETRIES = "com.sun.jndi.dns.timeout.retries";
  
  private CT lookupCT;
  
  private static final String LOOKUP_ATTR = "com.sun.jndi.dns.lookup.attr";
  
  private static final String RECURSION = "com.sun.jndi.dns.recursion";
  
  private static final int ANY = 255;
  
  private static final ZoneNode zoneTree = new ZoneNode(null);
  
  private static final boolean debug = false;
  
  public DnsContext(String paramString, String[] paramArrayOfString, Hashtable<?, ?> paramHashtable) throws NamingException {
    this.domain = new DnsName(paramString.endsWith(".") ? paramString : (paramString + "."));
    this.servers = (paramArrayOfString == null) ? null : (String[])paramArrayOfString.clone();
    this.environment = (Hashtable)paramHashtable.clone();
    this.envShared = false;
    this.parentIsDns = false;
    this.resolver = null;
    initFromEnvironment();
  }
  
  DnsContext(DnsContext paramDnsContext, DnsName paramDnsName) {
    this(paramDnsContext);
    this.domain = paramDnsName;
    this.parentIsDns = true;
  }
  
  private DnsContext(DnsContext paramDnsContext) {
    this.environment = paramDnsContext.environment;
    paramDnsContext.envShared = true;
    this.parentIsDns = paramDnsContext.parentIsDns;
    this.domain = paramDnsContext.domain;
    this.servers = paramDnsContext.servers;
    this.resolver = paramDnsContext.resolver;
    this.authoritative = paramDnsContext.authoritative;
    this.recursion = paramDnsContext.recursion;
    this.timeout = paramDnsContext.timeout;
    this.retries = paramDnsContext.retries;
    this.lookupCT = paramDnsContext.lookupCT;
  }
  
  public void close() {
    if (this.resolver != null) {
      this.resolver.close();
      this.resolver = null;
    } 
  }
  
  protected Hashtable<?, ?> p_getEnvironment() { return this.environment; }
  
  public Hashtable<?, ?> getEnvironment() { return (Hashtable)this.environment.clone(); }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    if (paramString.equals("com.sun.jndi.dns.lookup.attr")) {
      this.lookupCT = getLookupCT((String)paramObject);
    } else if (paramString.equals("java.naming.authoritative")) {
      this.authoritative = "true".equalsIgnoreCase((String)paramObject);
    } else if (paramString.equals("com.sun.jndi.dns.recursion")) {
      this.recursion = "true".equalsIgnoreCase((String)paramObject);
    } else if (paramString.equals("com.sun.jndi.dns.timeout.initial")) {
      int i = Integer.parseInt((String)paramObject);
      if (this.timeout != i) {
        this.timeout = i;
        this.resolver = null;
      } 
    } else if (paramString.equals("com.sun.jndi.dns.timeout.retries")) {
      int i = Integer.parseInt((String)paramObject);
      if (this.retries != i) {
        this.retries = i;
        this.resolver = null;
      } 
    } 
    if (!this.envShared)
      return this.environment.put(paramString, paramObject); 
    if (this.environment.get(paramString) != paramObject) {
      this.environment = (Hashtable)this.environment.clone();
      this.envShared = false;
      return this.environment.put(paramString, paramObject);
    } 
    return paramObject;
  }
  
  public Object removeFromEnvironment(String paramString) throws NamingException {
    if (paramString.equals("com.sun.jndi.dns.lookup.attr")) {
      this.lookupCT = getLookupCT(null);
    } else if (paramString.equals("java.naming.authoritative")) {
      this.authoritative = false;
    } else if (paramString.equals("com.sun.jndi.dns.recursion")) {
      this.recursion = true;
    } else if (paramString.equals("com.sun.jndi.dns.timeout.initial")) {
      if (this.timeout != 1000) {
        this.timeout = 1000;
        this.resolver = null;
      } 
    } else if (paramString.equals("com.sun.jndi.dns.timeout.retries") && this.retries != 4) {
      this.retries = 4;
      this.resolver = null;
    } 
    if (!this.envShared)
      return this.environment.remove(paramString); 
    if (this.environment.get(paramString) != null) {
      this.environment = (Hashtable)this.environment.clone();
      this.envShared = false;
      return this.environment.remove(paramString);
    } 
    return null;
  }
  
  void setProviderUrl(String paramString) { this.environment.put("java.naming.provider.url", paramString); }
  
  private void initFromEnvironment() {
    this.lookupCT = getLookupCT((String)this.environment.get("com.sun.jndi.dns.lookup.attr"));
    this.authoritative = "true".equalsIgnoreCase((String)this.environment.get("java.naming.authoritative"));
    String str = (String)this.environment.get("com.sun.jndi.dns.recursion");
    this.recursion = (str == null || "true".equalsIgnoreCase(str));
    str = (String)this.environment.get("com.sun.jndi.dns.timeout.initial");
    this.timeout = (str == null) ? 1000 : Integer.parseInt(str);
    str = (String)this.environment.get("com.sun.jndi.dns.timeout.retries");
    this.retries = (str == null) ? 4 : Integer.parseInt(str);
  }
  
  private CT getLookupCT(String paramString) throws InvalidAttributeIdentifierException { return (paramString == null) ? new CT(1, 16) : fromAttrId(paramString); }
  
  public Object c_lookup(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setSuccess();
    if (paramName.isEmpty()) {
      DnsContext dnsContext = new DnsContext(this);
      dnsContext.resolver = new Resolver(this.servers, this.timeout, this.retries);
      return dnsContext;
    } 
    try {
      DnsName dnsName = fullyQualify(paramName);
      ResourceRecords resourceRecords = getResolver().query(dnsName, this.lookupCT.rrclass, this.lookupCT.rrtype, this.recursion, this.authoritative);
      Attributes attributes = rrsToAttrs(resourceRecords, null);
      DnsContext dnsContext = new DnsContext(this, dnsName);
      return DirectoryManager.getObjectInstance(dnsContext, paramName, this, this.environment, attributes);
    } catch (NamingException namingException) {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(namingException);
    } catch (Exception exception) {
      paramContinuation.setError(this, paramName);
      NamingException namingException = new NamingException("Problem generating object using object factory");
      namingException.setRootCause(exception);
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  public Object c_lookupLink(Name paramName, Continuation paramContinuation) throws NamingException { return c_lookup(paramName, paramContinuation); }
  
  public NamingEnumeration<NameClassPair> c_list(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setSuccess();
    try {
      DnsName dnsName = fullyQualify(paramName);
      NameNode nameNode = getNameNode(dnsName);
      DnsContext dnsContext = new DnsContext(this, dnsName);
      return new NameClassPairEnumeration(dnsContext, nameNode.getChildren());
    } catch (NamingException namingException) {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  public NamingEnumeration<Binding> c_listBindings(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setSuccess();
    try {
      DnsName dnsName = fullyQualify(paramName);
      NameNode nameNode = getNameNode(dnsName);
      DnsContext dnsContext = new DnsContext(this, dnsName);
      return new BindingEnumeration(dnsContext, nameNode.getChildren());
    } catch (NamingException namingException) {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  public void c_bind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_rebind(Name paramName, Object paramObject, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_unbind(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_rename(Name paramName1, Name paramName2, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName1);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public Context c_createSubcontext(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_destroySubcontext(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public NameParser c_getNameParser(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setSuccess();
    return nameParser;
  }
  
  public void c_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public DirContext c_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public Attributes c_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException {
    paramContinuation.setSuccess();
    try {
      DnsName dnsName = fullyQualify(paramName);
      CT[] arrayOfCT = attrIdsToClassesAndTypes(paramArrayOfString);
      CT cT = getClassAndTypeToQuery(arrayOfCT);
      ResourceRecords resourceRecords = getResolver().query(dnsName, cT.rrclass, cT.rrtype, this.recursion, this.authoritative);
      return rrsToAttrs(resourceRecords, arrayOfCT);
    } catch (NamingException namingException) {
      paramContinuation.setError(this, paramName);
      throw paramContinuation.fillInException(namingException);
    } 
  }
  
  public void c_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public void c_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation) throws NamingException { throw new OperationNotSupportedException(); }
  
  public NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException { throw new OperationNotSupportedException(); }
  
  public NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation) throws NamingException { throw new OperationNotSupportedException(); }
  
  public DirContext c_getSchema(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public DirContext c_getSchemaClassDefinition(Name paramName, Continuation paramContinuation) throws NamingException {
    paramContinuation.setError(this, paramName);
    throw paramContinuation.fillInException(new OperationNotSupportedException());
  }
  
  public String getNameInNamespace() { return this.domain.toString(); }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    if (!(paramName2 instanceof DnsName) && !(paramName2 instanceof CompositeName))
      paramName2 = (new DnsName()).addAll(paramName2); 
    if (!(paramName1 instanceof DnsName) && !(paramName1 instanceof CompositeName))
      paramName1 = (new DnsName()).addAll(paramName1); 
    if (paramName2 instanceof DnsName && paramName1 instanceof DnsName) {
      DnsName dnsName = (DnsName)paramName2.clone();
      dnsName.addAll(paramName1);
      return (new CompositeName()).add(dnsName.toString());
    } 
    Name name1 = (paramName2 instanceof CompositeName) ? paramName2 : (new CompositeName()).add(paramName2.toString());
    Name name2 = (paramName1 instanceof CompositeName) ? paramName1 : (new CompositeName()).add(paramName1.toString());
    int i = name1.size() - 1;
    if (name2.isEmpty() || name2.get(0).equals("") || name1.isEmpty() || name1.get(i).equals(""))
      return super.composeName(name2, name1); 
    CompositeName compositeName = (paramName2 == name1) ? (CompositeName)name1.clone() : name1;
    compositeName.addAll(name2);
    if (this.parentIsDns) {
      DnsName dnsName = (paramName2 instanceof DnsName) ? (DnsName)paramName2.clone() : new DnsName(name1.get(i));
      dnsName.addAll((paramName1 instanceof DnsName) ? paramName1 : new DnsName(name2.get(0)));
      compositeName.remove(i + 1);
      compositeName.remove(i);
      compositeName.add(i, dnsName.toString());
    } 
    return compositeName;
  }
  
  private Resolver getResolver() throws NamingException {
    if (this.resolver == null)
      this.resolver = new Resolver(this.servers, this.timeout, this.retries); 
    return this.resolver;
  }
  
  DnsName fullyQualify(Name paramName) throws NamingException {
    if (paramName.isEmpty())
      return this.domain; 
    DnsName dnsName = (paramName instanceof CompositeName) ? new DnsName(paramName.get(0)) : (DnsName)(new DnsName()).addAll(paramName);
    if (dnsName.hasRootLabel()) {
      if (this.domain.size() == 1)
        return dnsName; 
      throw new InvalidNameException("DNS name " + dnsName + " not relative to " + this.domain);
    } 
    return (DnsName)dnsName.addAll(0, this.domain);
  }
  
  private static Attributes rrsToAttrs(ResourceRecords paramResourceRecords, CT[] paramArrayOfCT) {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    for (byte b = 0; b < paramResourceRecords.answer.size(); b++) {
      ResourceRecord resourceRecord = (ResourceRecord)paramResourceRecords.answer.elementAt(b);
      int i = resourceRecord.getType();
      int j = resourceRecord.getRrclass();
      if (classAndTypeMatch(j, i, paramArrayOfCT)) {
        String str = toAttrId(j, i);
        Attribute attribute = basicAttributes.get(str);
        if (attribute == null) {
          attribute = new BasicAttribute(str);
          basicAttributes.put(attribute);
        } 
        attribute.add(resourceRecord.getRdata());
      } 
    } 
    return basicAttributes;
  }
  
  private static boolean classAndTypeMatch(int paramInt1, int paramInt2, CT[] paramArrayOfCT) {
    if (paramArrayOfCT == null)
      return true; 
    for (byte b = 0; b < paramArrayOfCT.length; b++) {
      CT cT = paramArrayOfCT[b];
      boolean bool1 = (cT.rrclass == 255 || cT.rrclass == paramInt1) ? 1 : 0;
      boolean bool2 = (cT.rrtype == 255 || cT.rrtype == paramInt2) ? 1 : 0;
      if (bool1 && bool2)
        return true; 
    } 
    return false;
  }
  
  private static String toAttrId(int paramInt1, int paramInt2) {
    String str = ResourceRecord.getTypeName(paramInt2);
    if (paramInt1 != 1)
      str = ResourceRecord.getRrclassName(paramInt1) + " " + str; 
    return str;
  }
  
  private static CT fromAttrId(String paramString) throws InvalidAttributeIdentifierException {
    int i;
    if (paramString.equals(""))
      throw new InvalidAttributeIdentifierException("Attribute ID cannot be empty"); 
    int k = paramString.indexOf(' ');
    if (k < 0) {
      i = 1;
    } else {
      String str1 = paramString.substring(0, k);
      i = ResourceRecord.getRrclass(str1);
      if (i < 0)
        throw new InvalidAttributeIdentifierException("Unknown resource record class '" + str1 + '\''); 
    } 
    String str = paramString.substring(k + 1);
    int j = ResourceRecord.getType(str);
    if (j < 0)
      throw new InvalidAttributeIdentifierException("Unknown resource record type '" + str + '\''); 
    return new CT(i, j);
  }
  
  private static CT[] attrIdsToClassesAndTypes(String[] paramArrayOfString) throws InvalidAttributeIdentifierException {
    if (paramArrayOfString == null)
      return null; 
    CT[] arrayOfCT = new CT[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++)
      arrayOfCT[b] = fromAttrId(paramArrayOfString[b]); 
    return arrayOfCT;
  }
  
  private static CT getClassAndTypeToQuery(CT[] paramArrayOfCT) {
    int j;
    int i;
    if (paramArrayOfCT == null) {
      i = 255;
      j = 255;
    } else if (paramArrayOfCT.length == 0) {
      i = 1;
      j = 255;
    } else {
      i = (paramArrayOfCT[0]).rrclass;
      j = (paramArrayOfCT[0]).rrtype;
      for (byte b = 1; b < paramArrayOfCT.length; b++) {
        if (i != (paramArrayOfCT[b]).rrclass)
          i = 255; 
        if (j != (paramArrayOfCT[b]).rrtype)
          j = 255; 
      } 
    } 
    return new CT(i, j);
  }
  
  private NameNode getNameNode(DnsName paramDnsName) throws NamingException {
    NameNode nameNode1;
    ZoneNode zoneNode;
    dprint("getNameNode(" + paramDnsName + ")");
    synchronized (zoneTree) {
      zoneNode = zoneTree.getDeepestPopulated(paramDnsName);
    } 
    dprint("Deepest related zone in zone tree: " + ((zoneNode != null) ? zoneNode.getLabel() : "[none]"));
    if (zoneNode != null) {
      synchronized (zoneNode) {
        nameNode1 = zoneNode.getContents();
      } 
      if (nameNode1 != null) {
        NameNode nameNode = nameNode1.get(paramDnsName, zoneNode.depth() + 1);
        if (nameNode != null && !nameNode.isZoneCut()) {
          dprint("Found node " + paramDnsName + " in zone tree");
          DnsName dnsName1 = (DnsName)paramDnsName.getPrefix(zoneNode.depth() + 1);
          boolean bool = isZoneCurrent(zoneNode, dnsName1);
          boolean bool1 = false;
          synchronized (zoneNode) {
            if (nameNode1 != zoneNode.getContents()) {
              bool1 = true;
            } else if (!bool) {
              zoneNode.depopulate();
            } else {
              return nameNode;
            } 
          } 
          dprint("Zone not current; discarding node");
          if (bool1)
            return getNameNode(paramDnsName); 
        } 
      } 
    } 
    dprint("Adding node " + paramDnsName + " to zone tree");
    DnsName dnsName = getResolver().findZoneName(paramDnsName, 1, this.recursion);
    dprint("Node's zone is " + dnsName);
    synchronized (zoneTree) {
      zoneNode = (ZoneNode)zoneTree.add(dnsName, 1);
    } 
    synchronized (zoneNode) {
      nameNode1 = zoneNode.isPopulated() ? zoneNode.getContents() : populateZone(zoneNode, dnsName);
    } 
    NameNode nameNode2 = nameNode1.get(paramDnsName, dnsName.size());
    if (nameNode2 == null)
      throw new ConfigurationException("DNS error: node not found in its own zone"); 
    dprint("Found node in newly-populated zone");
    return nameNode2;
  }
  
  private NameNode populateZone(ZoneNode paramZoneNode, DnsName paramDnsName) throws NamingException {
    dprint("Populating zone " + paramDnsName);
    ResourceRecords resourceRecords = getResolver().queryZone(paramDnsName, 1, this.recursion);
    dprint("zone xfer complete: " + resourceRecords.answer.size() + " records");
    return paramZoneNode.populate(paramDnsName, resourceRecords);
  }
  
  private boolean isZoneCurrent(ZoneNode paramZoneNode, DnsName paramDnsName) throws NamingException {
    if (!paramZoneNode.isPopulated())
      return false; 
    ResourceRecord resourceRecord = getResolver().findSoa(paramDnsName, 1, this.recursion);
    synchronized (paramZoneNode) {
      if (resourceRecord == null)
        paramZoneNode.depopulate(); 
      return (paramZoneNode.isPopulated() && paramZoneNode.compareSerialNumberTo(resourceRecord) >= 0);
    } 
  }
  
  private static final void dprint(String paramString) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\DnsContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */