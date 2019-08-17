package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.NotOwnerException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

abstract class Host extends SimpleNode implements Serializable {
  public Host(int paramInt) { super(paramInt); }
  
  public Host(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  protected abstract PrincipalImpl createAssociatedPrincipal() throws UnknownHostException;
  
  protected abstract String getHname();
  
  public void buildAclEntries(PrincipalImpl paramPrincipalImpl, AclImpl paramAclImpl) {
    PrincipalImpl principalImpl = null;
    try {
      principalImpl = createAssociatedPrincipal();
    } catch (UnknownHostException unknownHostException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", "Cannot create ACL entry; got exception", unknownHostException); 
      throw new IllegalArgumentException("Cannot create ACL entry for " + unknownHostException.getMessage());
    } 
    AclEntryImpl aclEntryImpl = null;
    try {
      aclEntryImpl = new AclEntryImpl(principalImpl);
      registerPermission(aclEntryImpl);
      paramAclImpl.addEntry(paramPrincipalImpl, aclEntryImpl);
    } catch (UnknownHostException unknownHostException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", "Cannot create ACL entry; got exception", unknownHostException); 
      return;
    } catch (NotOwnerException notOwnerException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildAclEntries", "Cannot create ACL entry; got exception", notOwnerException); 
      return;
    } 
  }
  
  private void registerPermission(AclEntryImpl paramAclEntryImpl) {
    JDMHost jDMHost = (JDMHost)jjtGetParent();
    JDMManagers jDMManagers = (JDMManagers)jDMHost.jjtGetParent();
    JDMAclItem jDMAclItem = (JDMAclItem)jDMManagers.jjtGetParent();
    JDMAccess jDMAccess = jDMAclItem.getAccess();
    jDMAccess.putPermission(paramAclEntryImpl);
    JDMCommunities jDMCommunities = jDMAclItem.getCommunities();
    jDMCommunities.buildCommunities(paramAclEntryImpl);
  }
  
  public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> paramHashtable) {
    JDMHostTrap jDMHostTrap = (JDMHostTrap)jjtGetParent();
    JDMTrapInterestedHost jDMTrapInterestedHost = (JDMTrapInterestedHost)jDMHostTrap.jjtGetParent();
    JDMTrapItem jDMTrapItem = (JDMTrapItem)jDMTrapInterestedHost.jjtGetParent();
    JDMTrapCommunity jDMTrapCommunity = jDMTrapItem.getCommunity();
    String str = jDMTrapCommunity.getCommunity();
    InetAddress inetAddress = null;
    try {
      inetAddress = InetAddress.getByName(getHname());
    } catch (UnknownHostException unknownHostException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildTrapEntries", "Cannot create TRAP entry; got exception", unknownHostException); 
      return;
    } 
    Vector vector = null;
    if (paramHashtable.containsKey(inetAddress)) {
      vector = (Vector)paramHashtable.get(inetAddress);
      if (!vector.contains(str))
        vector.addElement(str); 
    } else {
      vector = new Vector();
      vector.addElement(str);
      paramHashtable.put(inetAddress, vector);
    } 
  }
  
  public void buildInformEntries(Hashtable<InetAddress, Vector<String>> paramHashtable) {
    JDMHostInform jDMHostInform = (JDMHostInform)jjtGetParent();
    JDMInformInterestedHost jDMInformInterestedHost = (JDMInformInterestedHost)jDMHostInform.jjtGetParent();
    JDMInformItem jDMInformItem = (JDMInformItem)jDMInformInterestedHost.jjtGetParent();
    JDMInformCommunity jDMInformCommunity = jDMInformItem.getCommunity();
    String str = jDMInformCommunity.getCommunity();
    InetAddress inetAddress = null;
    try {
      inetAddress = InetAddress.getByName(getHname());
    } catch (UnknownHostException unknownHostException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, Host.class.getName(), "buildTrapEntries", "Cannot create INFORM entry; got exception", unknownHostException); 
      return;
    } 
    Vector vector = null;
    if (paramHashtable.containsKey(inetAddress)) {
      vector = (Vector)paramHashtable.get(inetAddress);
      if (!vector.contains(str))
        vector.addElement(str); 
    } else {
      vector = new Vector();
      vector.addElement(str);
      paramHashtable.put(inetAddress, vector);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\Host.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */