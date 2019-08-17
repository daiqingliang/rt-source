package com.sun.jmx.snmp.IPAcl;

import com.sun.jmx.defaults.JmxProperties;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

class NetMaskImpl extends PrincipalImpl implements Group, Serializable {
  private static final long serialVersionUID = -7332541893877932896L;
  
  protected byte[] subnet = null;
  
  protected int prefix = -1;
  
  public NetMaskImpl() throws UnknownHostException {}
  
  private byte[] extractSubNet(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    byte[] arrayOfByte = null;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "BINARY ARRAY :");
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b4 = 0; b4 < i; b4++)
        stringBuffer.append((paramArrayOfByte[b4] & 0xFF) + ":"); 
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", stringBuffer.toString());
    } 
    int j = this.prefix / 8;
    if (j == i) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "The mask is the complete address, strange..." + i); 
      return paramArrayOfByte;
    } 
    if (j > i) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "The number of covered byte is longer than the address. BUG"); 
      throw new IllegalArgumentException("The number of covered byte is longer than the address.");
    } 
    int k = j;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Partially covered index : " + k); 
    byte b = paramArrayOfByte[k];
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Partially covered byte : " + b); 
    int m = this.prefix % 8;
    int n = 0;
    if (m == 0) {
      n = k;
    } else {
      n = k + 1;
    } 
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Remains : " + m); 
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < m; b2++)
      b1 = (byte)(b1 | 1 << 7 - b2); 
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Mask value : " + (b1 & 0xFF)); 
    b2 = (byte)(b & b1);
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Masked byte : " + (b2 & 0xFF)); 
    arrayOfByte = new byte[n];
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
      JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Resulting subnet : "); 
    for (byte b3 = 0; b3 < k; b3++) {
      arrayOfByte[b3] = paramArrayOfByte[b3];
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", (arrayOfByte[b3] & 0xFF) + ":"); 
    } 
    if (m != 0) {
      arrayOfByte[k] = b2;
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "extractSubNet", "Last subnet byte : " + (arrayOfByte[k] & 0xFF)); 
    } 
    return arrayOfByte;
  }
  
  public NetMaskImpl(String paramString, int paramInt) throws UnknownHostException {
    super(paramString);
    this.prefix = paramInt;
    this.subnet = extractSubNet(getAddress().getAddress());
  }
  
  public boolean addMember(Principal paramPrincipal) { return true; }
  
  public int hashCode() { return super.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof PrincipalImpl || paramObject instanceof NetMaskImpl) {
      PrincipalImpl principalImpl = (PrincipalImpl)paramObject;
      InetAddress inetAddress = principalImpl.getAddress();
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "Received Address : " + inetAddress); 
      byte[] arrayOfByte = inetAddress.getAddress();
      for (byte b = 0; b < this.subnet.length; b++) {
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST)) {
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "(recAddr[i]) : " + (arrayOfByte[b] & 0xFF));
          JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "(recAddr[i] & subnet[i]) : " + (arrayOfByte[b] & this.subnet[b] & 0xFF) + " subnet[i] : " + (this.subnet[b] & 0xFF));
        } 
        if ((arrayOfByte[b] & this.subnet[b]) != this.subnet[b]) {
          if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
            JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "FALSE"); 
          return false;
        } 
      } 
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, NetMaskImpl.class.getName(), "equals", "TRUE"); 
      return true;
    } 
    return false;
  }
  
  public boolean isMember(Principal paramPrincipal) { return ((paramPrincipal.hashCode() & super.hashCode()) == paramPrincipal.hashCode()); }
  
  public Enumeration<? extends Principal> members() {
    Vector vector = new Vector(1);
    vector.addElement(this);
    return vector.elements();
  }
  
  public boolean removeMember(Principal paramPrincipal) { return true; }
  
  public String toString() { return "NetMaskImpl :" + getAddress().toString() + "/" + this.prefix; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\NetMaskImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */