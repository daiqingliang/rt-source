package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.InetAddressAcl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

public class SnmpAcl implements InetAddressAcl, Serializable {
  private static final long serialVersionUID = -6702287103824397063L;
  
  static final PermissionImpl READ = new PermissionImpl("READ");
  
  static final PermissionImpl WRITE = new PermissionImpl("WRITE");
  
  private AclImpl acl = null;
  
  private boolean alwaysAuthorized = false;
  
  private String authorizedListFile = null;
  
  private Hashtable<InetAddress, Vector<String>> trapDestList = null;
  
  private Hashtable<InetAddress, Vector<String>> informDestList = null;
  
  private PrincipalImpl owner = null;
  
  public SnmpAcl(String paramString) throws UnknownHostException, IllegalArgumentException { this(paramString, null); }
  
  public SnmpAcl(String paramString1, String paramString2) throws UnknownHostException, IllegalArgumentException {
    this.trapDestList = new Hashtable();
    this.informDestList = new Hashtable();
    this.owner = new PrincipalImpl();
    try {
      this.acl = new AclImpl(this.owner, paramString1);
      AclEntryImpl aclEntryImpl = new AclEntryImpl(this.owner);
      aclEntryImpl.addPermission(READ);
      aclEntryImpl.addPermission(WRITE);
      this.acl.addEntry(this.owner, aclEntryImpl);
    } catch (NotOwnerException notOwnerException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "SnmpAcl(String,String)", "Should never get NotOwnerException as the owner is built in this constructor"); 
    } 
    if (paramString2 == null) {
      setDefaultFileName();
    } else {
      setAuthorizedListFile(paramString2);
    } 
    readAuthorizedListFile();
  }
  
  public Enumeration<AclEntry> entries() { return this.acl.entries(); }
  
  public Enumeration<String> communities() {
    HashSet hashSet = new HashSet();
    Vector vector = new Vector();
    Enumeration enumeration = this.acl.entries();
    while (enumeration.hasMoreElements()) {
      AclEntryImpl aclEntryImpl = (AclEntryImpl)enumeration.nextElement();
      Enumeration enumeration1 = aclEntryImpl.communities();
      while (enumeration1.hasMoreElements())
        hashSet.add(enumeration1.nextElement()); 
    } 
    String[] arrayOfString = (String[])hashSet.toArray(new String[0]);
    for (byte b = 0; b < arrayOfString.length; b++)
      vector.addElement(arrayOfString[b]); 
    return vector.elements();
  }
  
  public String getName() { return this.acl.getName(); }
  
  public static PermissionImpl getREAD() { return READ; }
  
  public static PermissionImpl getWRITE() { return WRITE; }
  
  public static String getDefaultAclFileName() {
    String str = System.getProperty("file.separator");
    StringBuffer stringBuffer = (new StringBuffer(System.getProperty("java.home"))).append(str).append("lib").append(str).append("snmp.acl");
    return stringBuffer.toString();
  }
  
  public void setAuthorizedListFile(String paramString) throws UnknownHostException, IllegalArgumentException {
    File file = new File(paramString);
    if (!file.isFile()) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "setAuthorizedListFile", "ACL file not found: " + paramString); 
      throw new IllegalArgumentException("The specified file [" + file + "] doesn't exist or is not a file, no configuration loaded");
    } 
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "setAuthorizedListFile", "Default file set to " + paramString); 
    this.authorizedListFile = paramString;
  }
  
  public void rereadTheFile() throws NotOwnerException, UnknownHostException {
    this.alwaysAuthorized = false;
    this.acl.removeAll(this.owner);
    this.trapDestList.clear();
    this.informDestList.clear();
    AclEntryImpl aclEntryImpl = new AclEntryImpl(this.owner);
    aclEntryImpl.addPermission(READ);
    aclEntryImpl.addPermission(WRITE);
    this.acl.addEntry(this.owner, aclEntryImpl);
    readAuthorizedListFile();
  }
  
  public String getAuthorizedListFile() { return this.authorizedListFile; }
  
  public boolean checkReadPermission(InetAddress paramInetAddress) {
    if (this.alwaysAuthorized)
      return true; 
    PrincipalImpl principalImpl = new PrincipalImpl(paramInetAddress);
    return this.acl.checkPermission(principalImpl, READ);
  }
  
  public boolean checkReadPermission(InetAddress paramInetAddress, String paramString) {
    if (this.alwaysAuthorized)
      return true; 
    PrincipalImpl principalImpl = new PrincipalImpl(paramInetAddress);
    return this.acl.checkPermission(principalImpl, paramString, READ);
  }
  
  public boolean checkCommunity(String paramString) { return this.acl.checkCommunity(paramString); }
  
  public boolean checkWritePermission(InetAddress paramInetAddress) {
    if (this.alwaysAuthorized)
      return true; 
    PrincipalImpl principalImpl = new PrincipalImpl(paramInetAddress);
    return this.acl.checkPermission(principalImpl, WRITE);
  }
  
  public boolean checkWritePermission(InetAddress paramInetAddress, String paramString) {
    if (this.alwaysAuthorized)
      return true; 
    PrincipalImpl principalImpl = new PrincipalImpl(paramInetAddress);
    return this.acl.checkPermission(principalImpl, paramString, WRITE);
  }
  
  public Enumeration<InetAddress> getTrapDestinations() { return this.trapDestList.keys(); }
  
  public Enumeration<String> getTrapCommunities(InetAddress paramInetAddress) {
    Vector vector = null;
    if ((vector = (Vector)this.trapDestList.get(paramInetAddress)) != null) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getTrapCommunities", "[" + paramInetAddress.toString() + "] is in list"); 
      return vector.elements();
    } 
    vector = new Vector();
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getTrapCommunities", "[" + paramInetAddress.toString() + "] is not in list"); 
    return vector.elements();
  }
  
  public Enumeration<InetAddress> getInformDestinations() { return this.informDestList.keys(); }
  
  public Enumeration<String> getInformCommunities(InetAddress paramInetAddress) {
    Vector vector = null;
    if ((vector = (Vector)this.informDestList.get(paramInetAddress)) != null) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getInformCommunities", "[" + paramInetAddress.toString() + "] is in list"); 
      return vector.elements();
    } 
    vector = new Vector();
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "getInformCommunities", "[" + paramInetAddress.toString() + "] is not in list"); 
    return vector.elements();
  }
  
  private void readAuthorizedListFile() throws NotOwnerException, UnknownHostException {
    this.alwaysAuthorized = false;
    if (this.authorizedListFile == null) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
        JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "alwaysAuthorized set to true"); 
      this.alwaysAuthorized = true;
    } else {
      Parser parser = null;
      try {
        parser = new Parser(new FileInputStream(getAuthorizedListFile()));
      } catch (FileNotFoundException fileNotFoundException) {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", "The specified file was not found, authorize everybody"); 
        this.alwaysAuthorized = true;
        return;
      } 
      try {
        JDMSecurityDefs jDMSecurityDefs = parser.SecurityDefs();
        jDMSecurityDefs.buildAclEntries(this.owner, this.acl);
        jDMSecurityDefs.buildTrapEntries(this.trapDestList);
        jDMSecurityDefs.buildInformEntries(this.informDestList);
      } catch (ParseException parseException) {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", "Got parsing exception", parseException); 
        throw new IllegalArgumentException(parseException.getMessage());
      } catch (Error error) {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpAcl.class.getName(), "readAuthorizedListFile", "Got unexpected error", error); 
        throw new IllegalArgumentException(error.getMessage());
      } 
      Enumeration enumeration = this.acl.entries();
      while (enumeration.hasMoreElements()) {
        AclEntryImpl aclEntryImpl = (AclEntryImpl)enumeration.nextElement();
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "===> " + aclEntryImpl.getPrincipal().toString()); 
        Enumeration enumeration1 = aclEntryImpl.permissions();
        while (enumeration1.hasMoreElements()) {
          Permission permission = (Permission)enumeration1.nextElement();
          if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
            JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpAcl.class.getName(), "readAuthorizedListFile", "perm = " + permission); 
        } 
      } 
    } 
  }
  
  private void setDefaultFileName() throws NotOwnerException, UnknownHostException {
    try {
      setAuthorizedListFile(getDefaultAclFileName());
    } catch (IllegalArgumentException illegalArgumentException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\SnmpAcl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */